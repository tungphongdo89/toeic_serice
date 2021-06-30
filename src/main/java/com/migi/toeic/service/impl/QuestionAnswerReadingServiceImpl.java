package com.migi.toeic.service.impl;

import com.migi.toeic.authen.model.RequestPractice;
import com.migi.toeic.base.DataListDTO;
import com.migi.toeic.base.ResultDataDTO;
import com.migi.toeic.constants.Constants;
import com.migi.toeic.dto.*;
import com.migi.toeic.exception.BusinessException;
import com.migi.toeic.model.DetailHistoryReading;
import com.migi.toeic.respositories.QuestionAnswerReadingRepository;
import com.migi.toeic.respositories.HistoryPracticesRepository;
import com.migi.toeic.respositories.DetailHistoryReadingRepository;
import com.migi.toeic.respositories.DetailHistoryReadingSingleRepository;
import com.migi.toeic.service.QuestionAnswerReadingService;
import com.migi.toeic.utils.MessageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class QuestionAnswerReadingServiceImpl implements QuestionAnswerReadingService {
    @Autowired
    QuestionAnswerReadingRepository questionAnswerReadingRepository;
    @Autowired
    HistoryPracticesRepository historyPracticesRepository;
    @Autowired
    DetailHistoryReadingRepository detailHistoryReadingRepository;
    @Autowired
    DetailHistoryReadingSingleRepository detailHistoryReadingSingleRepository;

    @Override
    public DataListDTO getListQuestionReading(QuestionAnswersReadingDTO obj) {
        ResultDataDTO resultDto = questionAnswerReadingRepository.getListQuestionReading(obj);
        DataListDTO data = new DataListDTO();
        data.setData(resultDto.getData());
        data.setTotal(resultDto.getTotal());
        data.setSize(resultDto.getTotal());
        data.setStart(1);
        return data;
    }

    @Override
    public QuestionAnswersReadingDTO getDetail(Long id) {
        if (id == null) {
            throw new BusinessException(MessageUtils.getMessage("error_miss_info"));
        }
        return questionAnswerReadingRepository.getDetail(id);
    }

    @Override
    public Long createQAReading(QuestionAnswersReadingDTO obj) {
        if (obj.getName() == null || obj.getAnswer() == null || obj.getParentId() == null || obj.getStatus() == null || obj.getAnswersToChoose() == null) {
            throw new BusinessException(MessageUtils.getMessage("error_miss_info"));
        }
        return questionAnswerReadingRepository.insert(obj.toModel());
    }

    @Override
    public Long updateQAReading(QuestionAnswersReadingDTO obj) {
        if (obj.getName() == null || obj.getAnswer() == null || obj.getParentId() == null || obj.getStatus() == null || obj.getAnswersToChoose() == null) {
            throw new BusinessException(MessageUtils.getMessage("error_miss_info"));
        }
        questionAnswerReadingRepository.update(obj.toModel());
        return 1L;
    }

    @Override
    public Long deleteQAReading(QuestionAnswersReadingDTO obj) {
        questionAnswerReadingRepository.delete(obj.toModel());
        return 1L;
    }

    @Override
    public ResultDataDTO getListQuestionsOfRead(Long categoryId) {
        if (categoryId == null) {
            throw new BusinessException(MessageUtils.getMessage("error_list_read_questions"));
        }
        ResultDataDTO data = questionAnswerReadingRepository.getListQuestionsOfRead(categoryId);
        if (data.getTotal() < 1) {
            throw new BusinessException(MessageUtils.getMessage("error_list_read_questions"));
        }
        return data;
    }

    @Override
    public List<QuestionAnswersReadingDTO> getListQuestionsOfReadWordFill(RequestPractice requestPractice) {
        if (requestPractice.getLevelCode().equals("EASY")) {
            List<QuestionAnswersReadingDTO> lstEasy = questionAnswerReadingRepository.getListQuestionsOfReadWordFill(requestPractice.getTopicName(), "EASY", 10);
            return lstEasy;
        }
        if (requestPractice.getLevelCode().equals("MEDIUM")) {
            List<QuestionAnswersReadingDTO> lstEasy =
                    questionAnswerReadingRepository.getListQuestionsOfReadWordFill(requestPractice.getTopicName(), "EASY", 6);
            List<QuestionAnswersReadingDTO> lstMedium =
                    questionAnswerReadingRepository.getListQuestionsOfReadWordFill(requestPractice.getTopicName(), "MEDIUM", 3);
            List<QuestionAnswersReadingDTO> lstDifficult =
                    questionAnswerReadingRepository
                            .getListQuestionsOfReadWordFill(requestPractice.getTopicName(), "DIFFICULT", 1);
            lstEasy.addAll(lstMedium);
            lstEasy.addAll(lstDifficult);
            return lstEasy;
        }
        if (requestPractice.getLevelCode().equals("DIFFICULT")) {
            List<QuestionAnswersReadingDTO> lstEasy =
                    questionAnswerReadingRepository.getListQuestionsOfReadWordFill(requestPractice.getTopicName(), "EASY", 4);
            List<QuestionAnswersReadingDTO> lstMedium =
                    questionAnswerReadingRepository.getListQuestionsOfReadWordFill(requestPractice.getTopicName(), "MEDIUM", 4);
            List<QuestionAnswersReadingDTO> lstDifficult =
                    questionAnswerReadingRepository
                            .getListQuestionsOfReadWordFill(requestPractice.getTopicName(), "DIFFICULT", 2);
            lstEasy.addAll(lstMedium);
            lstEasy.addAll(lstDifficult);
            return lstEasy;
        }
        return null;
    }

    @Override
    public QuestionAnswersDTO getResultQuestionOfReadWordFill(QuestionAnswersReadingDTO obj) {
        QuestionAnswersDTO questionAnswersDTO = questionAnswerReadingRepository.getDetailById(obj.getId());
        if(questionAnswersDTO == null){
            throw new BusinessException(MessageUtils.getMessage("error_miss_info"));
        } else {
            String[] a = questionAnswersDTO.getAnswersToChoose().toLowerCase().split("[\\|]");
            int indexChoose;
            if(obj.getUserChoose().equalsIgnoreCase("A")){
                indexChoose = 0;
            }else if(obj.getUserChoose().equalsIgnoreCase("B")){
                indexChoose = 1;
            } else if(obj.getUserChoose().equalsIgnoreCase("C")){
                indexChoose = 2;
            } else {
                indexChoose = 3;
            }
            int indexAnswer;
            if(questionAnswersDTO.getAnswer().equalsIgnoreCase("A")){
                indexAnswer = 0;
            }else if(questionAnswersDTO.getAnswer().equalsIgnoreCase("B")){
                indexAnswer = 1;
            } else if(questionAnswersDTO.getAnswer().equalsIgnoreCase("C")){
                indexAnswer = 2;
            } else {
                indexAnswer = 3;
            }
            if(obj.getNumberSelected() < 3){
                for(int i=0; i< a.length; i++){
                    if((i == indexChoose)  && indexChoose == indexAnswer ){
                        questionAnswersDTO.setIndexCorrect(Long.valueOf(i));
                    }
                    else if((i ==  indexChoose) && indexChoose != indexAnswer ){
                        questionAnswersDTO.setIndexInCorrect(Long.valueOf(i));
                    }
                }
            } else if(obj.getNumberSelected() == 3) {
                for(int i =0 ; i< a.length;i++){
                    if(i == indexChoose  &&  indexChoose == indexAnswer ){
                        questionAnswersDTO.setIndexCorrect(Long.valueOf(i));
                    }else if(i == indexChoose && indexChoose != indexAnswer ){
                        questionAnswersDTO.setIndexInCorrect(Long.valueOf(i));
                    }else if(i == indexAnswer){
                        questionAnswersDTO.setIndexCorrect(Long.valueOf(i));
                    }
                }
            }
        }
        questionAnswersDTO.setNumberSelected(obj.getNumberSelected());
        return questionAnswersDTO;
    }

    @Override
    public List<CategoryDTO> getListQuestionSingleOrDual(RequestPractice rp) {
        Float totalScore = 0F;
        List<CategoryDTO> lst = new ArrayList<>();

        List<CategoryDTO> random = new ArrayList<>();

        if (rp.getPart().toUpperCase().equals(Constants.PART_TOPIC.PART7)) {
            if (rp.getLevelCode().toUpperCase().equals(Constants.LEVEL_CODE.EASY)) {
                random.addAll(questionAnswerReadingRepository.getRandomReadingCateWithLimitLengthQuest(rp.getTopicId(), rp.getPart(), rp.getLevelCode(), 2));
            } else if (rp.getLevelCode().toUpperCase().equals(Constants.LEVEL_CODE.MEDIUM)) {
                random.addAll(questionAnswerReadingRepository.getRandomReadingCateWithLimitLengthQuest(rp.getTopicId(), rp.getPart(), Constants.LEVEL_CODE.EASY, 1));
                random.addAll(questionAnswerReadingRepository.getRandomReadingCateWithLimitLengthQuest(rp.getTopicId(), rp.getPart(), Constants.LEVEL_CODE.MEDIUM, 1));
            } else {
                random.addAll(questionAnswerReadingRepository.getRandomReadingCateWithLimitLengthQuest(rp.getTopicId(), rp.getPart(), Constants.LEVEL_CODE.MEDIUM, 1));
                random.addAll(questionAnswerReadingRepository.getRandomReadingCateWithLimitLengthQuest(rp.getTopicId(), rp.getPart(), Constants.LEVEL_CODE.DIFFICULT, 1));
            }
        } else {
            random.addAll(questionAnswerReadingRepository.getRandomReadingCateWithLimitLengthQuest(rp.getTopicId(), rp.getPart(), rp.getLevelCode(), 1));
        }

        for (CategoryDTO req : random) {
            List<QuestionAnswersDTO> listQuestionAnswersDTOS =
                    questionAnswerReadingRepository.getListQuestionByCategoryId(req.getCategoryId());
            CategoryDTO category = new CategoryDTO();
            category.setCategoryId(req.getCategoryId());
            category.setPathFile1(req.getPathFile1());
            category.setPathFile2(req.getPathFile2());
            category.setPathFile3(req.getPathFile3());
            category.setTypeFile1(req.getTypeFile1());
            category.setTypeFile2(req.getTypeFile2());
            category.setTypeFile3(req.getTypeFile3());
            category.setTranscript(req.getTranscript());

            List<QuestionAnswersDTO> answerToChoose = new ArrayList<>();
            for (QuestionAnswersDTO ques : listQuestionAnswersDTOS) {
                //totalScore += ques.getScore();
                String ans[] = ques.getAnswersToChoose().split("\\|");
                List<QuestionAnswersDTO> result = Arrays.stream(ans).map(s -> {
                    QuestionAnswersDTO questionAnswersDTO = new QuestionAnswersDTO();
                    questionAnswersDTO.setAnswer(s);
                    return questionAnswersDTO;
                }).collect(Collectors.toList());
                ques.setListAnswerToChoose(result);
                answerToChoose.add(ques);
            }
            category.setListQuestion(answerToChoose);
            lst.add(category);
        }
//        for (int i = 0; i < lst.size(); i++) {
//            lst.get(i).setTotalScore(totalScore);
//        }
        return lst;
    }

    @Override
    public CategoryDTO submitListQuestionSingleOrDual(CategoryDTO categoryDTO) {
        Float totalSubCorrect = 0F;
        List<QuestionAnswersDTO> submitted = categoryDTO.getListQuestion();
        List<QuestionAnswersDTO> lstQuestion = questionAnswerReadingRepository.submitListQuestionByCategoryId(categoryDTO.getCategoryId());
        List<QuestionAnswersDTO> result = new ArrayList<>();
        Iterator<QuestionAnswersDTO> iteratorQuestion = lstQuestion.listIterator();
        while (iteratorQuestion.hasNext()) {
            QuestionAnswersDTO nextQuestion = iteratorQuestion.next();
            Iterator<QuestionAnswersDTO> iteratorSubmitted = submitted.listIterator();
            boolean isCorrect = false;


            while (iteratorSubmitted.hasNext()) {
                QuestionAnswersDTO nextSubmitted = iteratorSubmitted.next();
                nextQuestion.setAnswer(nextQuestion.getAnswer().replaceAll("\\|", ""));
                String[] answerList = nextQuestion.getAnswersToChoose().split("[\\|]");
                int indexAns=0;
                if(nextQuestion.getAnswer().equalsIgnoreCase("A")) indexAns=0;
                else if(nextQuestion.getAnswer().equalsIgnoreCase("B")) indexAns=1;
                else if(nextQuestion.getAnswer().equalsIgnoreCase("C")) indexAns=2;
                else indexAns=3;

                if (nextQuestion.getId().equals(nextSubmitted.getId())) {
                    nextQuestion.setIndexCorrect(Long.valueOf(indexAns));
                    nextQuestion.setUserChoose(nextSubmitted.getUserChoose());
                    for(int index=0;index<answerList.length;index++){
                        if (answerList[index].trim().equals(nextSubmitted.getUserChoose().trim()) && answerList[indexAns].trim().equals(nextSubmitted.getUserChoose().trim())) {
                            isCorrect = true;
//                          totalSubCorrect += nextQuestion.getScore();
                            iteratorQuestion.remove();
                            iteratorSubmitted.remove();
                            break;
                        }
                        else if (answerList[index].trim().equals(nextSubmitted.getUserChoose().trim()) && !answerList[indexAns].trim().equals(nextSubmitted.getUserChoose().trim())) {
                            isCorrect = false;
                            nextQuestion.setIndexInCorrect(Long.valueOf(index));
                            iteratorQuestion.remove();
                            iteratorSubmitted.remove();
                            break;
                        }
                    }
                }
            }
            nextQuestion.setCorrect(isCorrect);
            String ans[] = nextQuestion.getAnswersToChoose().split("\\|");
            List<QuestionAnswersDTO> answersDTOS = Arrays.stream(ans).map(s -> {
                QuestionAnswersDTO questionAnswersDTO = new QuestionAnswersDTO();
                questionAnswersDTO.setAnswer(s);
                return questionAnswersDTO;
            }).collect(Collectors.toList());
            nextQuestion.setListAnswerToChoose(answersDTOS);
            result.add(nextQuestion);
        }
        categoryDTO.setListQuestion(result);
        //categoryDTO.setTotalSubCorrect(totalSubCorrect);
        return categoryDTO;
    }

    @Override
    public String createHistoryReadWordFill(HistoryPracticesDTO historyPracticesDTO){
        try{
//            LocalDateTime localDateTime = LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh"));
//            Date createDate = Date.from(localDateTime.atZone(ZoneId.of("Asia/Ho_Chi_Minh")).toInstant());
            historyPracticesDTO.setCreateDate(new Date());
            Long idHistory = historyPracticesRepository.insert(historyPracticesDTO.toModel());
            try{
                createDetailHistoryReading(historyPracticesDTO.getLstQuestionAnswer(), idHistory);
            } catch (Exception e){
                throw new BusinessException("create_history_reading_fill_fail");
            }
            return MessageUtils.getMessage("create_history_reading_fill_success");
        } catch (Exception e){
            throw new BusinessException("create_history_reading_fill_fail");
        }
    }

    public void createDetailHistoryReading(List<QuestionAnswersDTO> listQuestionAnswer, Long idHistory){
        for(int i = 0; i<listQuestionAnswer.size(); i++){
            DetailHistoryReadingDTO obj = new DetailHistoryReadingDTO();
            if(listQuestionAnswer.get(i).getIndexCorrect() != null){
                obj.setIndexCorrect(listQuestionAnswer.get(i).getIndexCorrect().toString());
            } else {
                obj.setIndexCorrect("");
            }
            if(listQuestionAnswer.get(i).getIndexInCorrect() != null){
                obj.setIndexInCorrect(listQuestionAnswer.get(i).getIndexInCorrect().toString());
            } else {
                obj.setIndexInCorrect("");
            }
            obj.setUserChoose(listQuestionAnswer.get(i).getUserChoose());
            obj.setParentId(idHistory);
            obj.setQuestionId(listQuestionAnswer.get(i).getId());
            obj.setCategoryId(listQuestionAnswer.get(i).getParentId());
            obj.setNumberSelected(listQuestionAnswer.get(i).getNumberSelected());
            Long idDetail = detailHistoryReadingRepository.insert(obj.toModel());
        }
    }

    @Override
    public Long insertData(QuestionAnswersReadingDTO dto) {
        return questionAnswerReadingRepository.insert(dto.toModel());
    }

}

