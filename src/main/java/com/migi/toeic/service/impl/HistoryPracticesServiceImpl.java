package com.migi.toeic.service.impl;

import com.migi.toeic.authen.model.RequestPractice;
import com.migi.toeic.base.DataListDTO;
import com.migi.toeic.dto.*;
import com.migi.toeic.exception.BusinessException;
import com.migi.toeic.respositories.*;
import com.migi.toeic.service.HistoryPracticesService;
import com.migi.toeic.utils.DateUtil;
import com.migi.toeic.utils.MessageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class HistoryPracticesServiceImpl implements HistoryPracticesService {

    @Autowired
    private HistoryPracticesRepository historyPracticesRepository;

    @Autowired
    private ApParamRepository apParamRepository;

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private DetailHistoryListeningRepository detailHistoryListeningRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private QuestionAnswerListeningRepository questionAnswerListeningRepository;

    @Autowired
    private DetailHistoryLisSingleRepository detailHistoryLisSingleRepository;

    @Autowired
    private QuestionAnswerReadingRepository questionAnswerReadingRepository;

    @Autowired
    private DetailHistoryReadingSingleRepository detailHistoryReadingSingleRepository;

    @Override
    public DataListDTO doSearch(HistoryPracticesDTO historyPracticesDTO) {
        if (historyPracticesDTO.getCreateFrom() != null) {
            String x = DateUtil.convertTimeDisplay(historyPracticesDTO.getCreateFrom());
            historyPracticesDTO.setCreateFromString(x);
        }
        if (historyPracticesDTO.getCreateTo() != null) {
            String x = DateUtil.convertTimeDisplay(historyPracticesDTO.getCreateTo());
            historyPracticesDTO.setCreateToString(x);
        }
        if (historyPracticesDTO.getCreateFrom() != null && historyPracticesDTO.getCreateTo() != null) {
            if (DateUtil.compareDate(historyPracticesDTO.getCreateFrom(), historyPracticesDTO.getCreateTo()) == 0) {
                throw new BusinessException("Thời điểm từ phải nhỏ hơn thời điểm đến");
            }
            historyPracticesDTO.setCreateFromString(DateUtil.convertTimeDisplay(historyPracticesDTO.getCreateFrom()));
            historyPracticesDTO.setCreateToString(DateUtil.convertTimeDisplay(historyPracticesDTO.getCreateTo()));
        }

        List<HistoryPracticesDTO> resultDataDTO = historyPracticesRepository.doSearch(historyPracticesDTO);

        DataListDTO dataListDTO = new DataListDTO();
        dataListDTO.setData(resultDataDTO);
        dataListDTO.setStart(1);
        dataListDTO.setSize(resultDataDTO.size());
        dataListDTO.setTotal(resultDataDTO.size());
        return dataListDTO;
    }

    @Override
    public List<ApParamDTO> getTypeForHistoryPractices() {
        List<ApParamDTO> listTypes = apParamRepository.getTypeForHistoryPractices();
        return listTypes;
    }

    @Override
    public List<ApParamDTO> getPartForHistoryPractices(ApparamForGetPartOrTopicDTO apparamForGetPartOrTopicDTO) {
        if(null != apparamForGetPartOrTopicDTO.getValue()){
            if(apparamForGetPartOrTopicDTO.getValue().equals("1")){
                apparamForGetPartOrTopicDTO.setParentCode("READING_UNIT");
            }
            if(apparamForGetPartOrTopicDTO.getValue().equals("2")){
                apparamForGetPartOrTopicDTO.setParentCode("LISTENING_UNIT");
            }
            if(apparamForGetPartOrTopicDTO.getValue().equals("3")){
                apparamForGetPartOrTopicDTO.setParentCode("TRANS_ENG_TO_VIET");
            }
            if(apparamForGetPartOrTopicDTO.getValue().equals("4")){
                apparamForGetPartOrTopicDTO.setParentCode("TRANS_VIET_TO_ENG");
            }
            if(apparamForGetPartOrTopicDTO.getValue().equals("5")){
                apparamForGetPartOrTopicDTO.setParentCode("LISTENING_FILL_UNIT");
            }
        }
        List<ApParamDTO> listParts = apParamRepository.getPartForHistoryPractices(apparamForGetPartOrTopicDTO);
        return listParts;
    }

    @Override
    public List<TopicDTO> getListTopicsForHistoryPractices(ApparamForGetPartOrTopicDTO apparamForGetPartOrTopicDTO) {
        if(null != apparamForGetPartOrTopicDTO.getType()){
            if(apparamForGetPartOrTopicDTO.getType().equals("1")){
                apparamForGetPartOrTopicDTO.setType("READING_UNIT");
            }
            if(apparamForGetPartOrTopicDTO.getType().equals("2")){
                apparamForGetPartOrTopicDTO.setType("LISTENING_UNIT");
            }
            if(apparamForGetPartOrTopicDTO.getType().equals("3")){
                apparamForGetPartOrTopicDTO.setType("TRANS_ENG_TO_VIET");
            }
            if(apparamForGetPartOrTopicDTO.getType().equals("4")){
                apparamForGetPartOrTopicDTO.setType("TRANS_VIET_TO_ENG");
            }
            if(apparamForGetPartOrTopicDTO.getType().equals("5")){
                apparamForGetPartOrTopicDTO.setType("LISTENING_FILL_UNIT");
            }
        }
        List<TopicDTO> listTopics = topicRepository.getListTopicsForHistoryPractices(apparamForGetPartOrTopicDTO);
        return listTopics;
    }

    public String saveResultHisotryListening(HistoryPracticesDTO historyPracticesDTO){

        HistoryPracticesDTO historyPracticesDTO1 = new HistoryPracticesDTO();
        historyPracticesDTO1.setTypeCode(historyPracticesDTO.getTypeCode());
        historyPracticesDTO1.setPart(historyPracticesDTO.getPart());
        historyPracticesDTO1.setLevelCode(historyPracticesDTO.getLevelCode());
        historyPracticesDTO1.setTopicId(historyPracticesDTO.getTopicId());
        historyPracticesDTO1.setUserId(historyPracticesDTO.getUserId());
        historyPracticesDTO1.setNumberCorrect(historyPracticesDTO.getNumberCorrect());

//        LocalDateTime localDateTime = LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh"));
//        Date createDate = Date.from(localDateTime.atZone(ZoneId.of("Asia/Ho_Chi_Minh")).toInstant());
        historyPracticesDTO1.setCreateDate(new Date());

        historyPracticesRepository.insert(historyPracticesDTO1.toModel());

        HistoryPracticesDTO historyPracticesDTO2 = historyPracticesRepository.getMaxHistoryPracticesById();

        for (DetailHistoryListeningDTO detailHistoryListeningDTO: historyPracticesDTO.getListDetailHistoryListening()) {
            detailHistoryListeningDTO.setParentId(historyPracticesDTO2.getId());
            detailHistoryListeningRepository.insert(detailHistoryListeningDTO.toModel());
        }

//        return MessageUtils.getMessage("save_result_history_listening");
        if(historyPracticesDTO.getPart().equals("PART3")){
            return MessageUtils.getMessage("save_result_history_listening_PART3");
        }
        else {
            return MessageUtils.getMessage("save_result_history_listening_PART4");
        }
    }


    public List<RequestPractice> getDetailHistoryPracticesListening(HistoryPracticesDTO historyPracticesDTO){
        List<RequestPractice> listCategories = new ArrayList<>();

        List<DetailHistoryListeningDTO> listDetailHistoryListeningDTOs =
                detailHistoryListeningRepository.getListDetailHistoryListeningByParentId(historyPracticesDTO.getId());

        List<DetailHistoryListeningDTO> listDistinctDetailHistoryListeningDTOs =
                detailHistoryListeningRepository.getDistinctListDetailHistoryListeningByParentId(historyPracticesDTO.getId());

        for (DetailHistoryListeningDTO DetailHistoryListeningDTO: listDistinctDetailHistoryListeningDTOs) {
            CategoryDTO categoryDTO = categoryRepository.getCategoryByCategoryId(DetailHistoryListeningDTO.getCategoryId());
            RequestPractice requestPractice = new RequestPractice();
            requestPractice.setPathFile2(categoryDTO.getPathFile2());
            requestPractice.setPathFile1(categoryDTO.getPathFile1());
            requestPractice.setLevelCode(categoryDTO.getLevelCode());
            requestPractice.setTopicId(categoryDTO.getParentId());
            requestPractice.setCategoryId(categoryDTO.getCategoryId());
            requestPractice.setTranscript(categoryDTO.getTranscript());
            requestPractice.setTopicName(categoryDTO.getNameTopic());
            listCategories.add(requestPractice);
        }

        List<QuestionAnswersDTO> listQuestionAnswersDTOS = new ArrayList<>();
        for(DetailHistoryListeningDTO detailHistoryListeningDTO: listDetailHistoryListeningDTOs){
            QuestionAnswersDTO questionAnswerDTO =
                    questionAnswerListeningRepository.getListQuestionAnswerListeningById(detailHistoryListeningDTO.getQuestionId());
            questionAnswerDTO.setCorrectIndex(detailHistoryListeningDTO.getCorrectIndex());
            questionAnswerDTO.setIncorrectIndex(detailHistoryListeningDTO.getIncorrectIndex());
            listQuestionAnswersDTOS.add(questionAnswerDTO);

        }

        for(int i=0;i<listCategories.size();i++){
            List<QuestionAnswersDTO> listQuestionAnswersDTOSChild = new ArrayList<>();
            long correctQuesNumber = 0;
            for(int j=0;j<listQuestionAnswersDTOS.size();j++){
                if(listQuestionAnswersDTOS.get(j).getParentId().equals(listCategories.get(i).getCategoryId()) ){
                    listQuestionAnswersDTOSChild.add(listQuestionAnswersDTOS.get(j));
                    if(null == listQuestionAnswersDTOS.get(j).getIncorrectIndex()){
                        correctQuesNumber += 1;
                    }
                }

            }
            listCategories.get(i).setListQuestion(listQuestionAnswersDTOSChild);
            listCategories.get(i).setCorrectQuesNumber(correctQuesNumber);
        }


        return listCategories;
    }

    public String saveResultHistoryLisSingle(HistoryPracticesDTO historyPracticesDTO){
        HistoryPracticesDTO historyPracticesDTO1 = new HistoryPracticesDTO();
        historyPracticesDTO1.setTypeCode(historyPracticesDTO.getTypeCode());
        historyPracticesDTO1.setPart(historyPracticesDTO.getPart());
        historyPracticesDTO1.setLevelCode(historyPracticesDTO.getLevelCode());
        historyPracticesDTO1.setTopicId(historyPracticesDTO.getTopicId());
        historyPracticesDTO1.setUserId(historyPracticesDTO.getUserId());
        historyPracticesDTO1.setNumberCorrect(historyPracticesDTO.getNumberCorrect());

//        LocalDateTime localDateTime = LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh"));
//        Date createDate = Date.from(localDateTime.atZone(ZoneId.of("Asia/Ho_Chi_Minh")).toInstant());
        historyPracticesDTO1.setCreateDate(new Date());

        try{
            historyPracticesRepository.insert(historyPracticesDTO1.toModel());
        }
        catch (Exception e){
            return MessageUtils.getMessage("error_save_result");
        }

        HistoryPracticesDTO historyPracticesDTO2 = historyPracticesRepository.getMaxHistoryPracticesById();

        for (DetailHistoryLisSingleDTO detailHistoryLisSingleDTO: historyPracticesDTO.getListDetailHistoryLisSingle()) {
            detailHistoryLisSingleDTO.setParentId(historyPracticesDTO2.getId());
            try{
                detailHistoryLisSingleRepository.insert(detailHistoryLisSingleDTO.toModel());
            }
            catch (Exception e){
                return MessageUtils.getMessage("error_save_result");
            }
        }

        if(historyPracticesDTO.getPart().equals("PART1")){
            return MessageUtils.getMessage("save_result_history_listening_PART1");
        }
        else {
            return MessageUtils.getMessage("save_result_history_listening_PART2");
        }

    }

    public List<QuestionAnswerListeningDTO> getDetailHistoryLisSingle(HistoryPracticesDTO historyPracticesDTO){

        List<DetailHistoryLisSingleDTO> listDetailHistoryLisSingles =
                detailHistoryLisSingleRepository.getListDetailHistoryLisSingleByParentId(historyPracticesDTO.getId());

        QuestionAnswersDTO questionAnswersDTO1 = new QuestionAnswersDTO();
        questionAnswersDTO1.setAnswer("A");
        QuestionAnswersDTO questionAnswersDTO2 = new QuestionAnswersDTO();
        questionAnswersDTO2.setAnswer("B");
        QuestionAnswersDTO questionAnswersDTO3 = new QuestionAnswersDTO();
        questionAnswersDTO3.setAnswer("C");

        QuestionAnswersDTO questionAnswersDTO4 = new QuestionAnswersDTO();
        questionAnswersDTO4.setAnswer("D");

        List<QuestionAnswersDTO> listAnswerToChoose = new ArrayList<>();
        listAnswerToChoose.add(questionAnswersDTO1);
        listAnswerToChoose.add(questionAnswersDTO2);
        listAnswerToChoose.add(questionAnswersDTO3);

        if(historyPracticesDTO.getPart().equals("PART1")){
            listAnswerToChoose.add(questionAnswersDTO4);
        }

        List<QuestionAnswerListeningDTO> listQuestionAnswer = new ArrayList<>();

        for (DetailHistoryLisSingleDTO detailHistoryLisSingleDTO: listDetailHistoryLisSingles) {
            QuestionAnswerListeningDTO questionAnswerListeningDTO =
                    questionAnswerListeningRepository.getQuestionAnswerListeningById(detailHistoryLisSingleDTO.getQuestionId());
            questionAnswerListeningDTO.setCorrect(Boolean.parseBoolean(detailHistoryLisSingleDTO.getCorrect()));
            questionAnswerListeningDTO.setNameTopic(detailHistoryLisSingleDTO.getTopicName());
            questionAnswerListeningDTO.setUserChoose(detailHistoryLisSingleDTO.getUserChoose());
            questionAnswerListeningDTO.setListAnswerToChoose(listAnswerToChoose);

            listQuestionAnswer.add(questionAnswerListeningDTO);
        }

        return listQuestionAnswer;
    }
    public List<CategoryDTO> getDetailHistoryReadingSingle(HistoryPracticesDTO historyPracticesDTO){
        List<CategoryDTO> listCategories = new ArrayList<>();
        List<DetailHistoryReadingSingleDTO> listDetailHistoryReadingDTOs =
                detailHistoryReadingSingleRepository.getListDetailHistoryReadSingle(historyPracticesDTO.getId());
        List<DetailHistoryReadingSingleDTO> listDistinctDetailHistoryReadingDTOs =
                detailHistoryReadingSingleRepository.getDistinctListDetailHistoryReadingByParentId(historyPracticesDTO.getId());

        for (DetailHistoryReadingSingleDTO DetailHistoryListeningDTO: listDistinctDetailHistoryReadingDTOs) {
            CategoryDTO categoryDTO = categoryRepository.getCategoryByCategoryId(DetailHistoryListeningDTO.getCategoryId());
            listCategories.add(categoryDTO);
        }

        List<QuestionAnswersDTO> listQuestionAnswersDTOS = new ArrayList<>();
        for(DetailHistoryReadingSingleDTO detailHistoryReadingDTO: listDetailHistoryReadingDTOs){
            QuestionAnswersDTO questionAnswerDTO =
                    questionAnswerReadingRepository.getDetailById(detailHistoryReadingDTO.getQuestionId());
            String ansLst[] = questionAnswerDTO.getAnswersToChoose().split("\\|");
            //questionAnswerDTO.setCorrectIndex(detailHistoryReadingDTO.getIndexCorrect());
            questionAnswerDTO.setCorrectIndex( Long.parseLong(detailHistoryReadingDTO.getIndexCorrect() ));
            if(detailHistoryReadingDTO.getIndexInCorrect()!=null) {
                questionAnswerDTO.setCorrect(false);
                questionAnswerDTO.setUserChoose(ansLst[ Integer.parseInt(detailHistoryReadingDTO.getIndexInCorrect()) ]);
                questionAnswerDTO.setIncorrectIndex( Long.parseLong(detailHistoryReadingDTO.getIndexInCorrect() ));
            }
            else {
                questionAnswerDTO.setUserChoose(ansLst[ questionAnswerDTO.getCorrectIndex().intValue() ]);
                questionAnswerDTO.setCorrect(true);
            }
            listQuestionAnswersDTOS.add(questionAnswerDTO);
        }

        for(int i=0;i<listCategories.size();i++){
            List<QuestionAnswersDTO> answerToChoose = new ArrayList<>();
            long correctQuesNumber = 0;
            for(int j=0;j<listQuestionAnswersDTOS.size();j++){
                if(listQuestionAnswersDTOS.get(j).getParentId().equals(listCategories.get(i).getCategoryId()) ){
                    String ans[] = listQuestionAnswersDTOS.get(j).getAnswersToChoose().split("\\|");
                    List<QuestionAnswersDTO> result = Arrays.stream(ans).map(s -> {
                        QuestionAnswersDTO questionAnswersDTO = new QuestionAnswersDTO();
                        questionAnswersDTO.setAnswer(s);
                        return questionAnswersDTO;
                    }).collect(Collectors.toList());
                    listQuestionAnswersDTOS.get(j).setListAnswerToChoose(result);
                    answerToChoose.add(listQuestionAnswersDTOS.get(j));
                    if(null == listQuestionAnswersDTOS.get(j).getIncorrectIndex()){
                        correctQuesNumber += 1;
                    }
                }
            }
            listCategories.get(i).setListQuestion(answerToChoose);
            listCategories.get(i).setCorrectQuesNumber(correctQuesNumber);
        }
        return listCategories;
    }
}
