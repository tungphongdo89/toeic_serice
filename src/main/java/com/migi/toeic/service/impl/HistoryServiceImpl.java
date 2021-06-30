package com.migi.toeic.service.impl;

import com.migi.toeic.base.DataListDTO;
import com.migi.toeic.base.ResultDataDTO;
import com.migi.toeic.dto.*;
import com.migi.toeic.exception.BusinessException;
import com.migi.toeic.respositories.*;
import com.migi.toeic.service.HistoryService;
import com.migi.toeic.utils.DateUtil;
import com.migi.toeic.utils.MessageUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@Service
public class HistoryServiceImpl implements HistoryService {

    @Autowired
    private HistoryRepository historyRepository;
    @Autowired
    private Environment environment;
    @Autowired
    private DetailHistoryRepository detailHistoryRepository;
    @Autowired
    private QuestionAnswerListeningRepository questionAnswerListeningRepository;
    @Autowired
    private QuestionAnswerReadingRepository questionAnswerReadingRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private DetailHistoryListenFillRepository detailHistoryListenFillRepository;
    @Autowired
    private HistoryPracticesRepository historyListenFillRepository;
    @Autowired
    private DetailHistoryReadingRepository detailHistoryReadingRepository;
    @Autowired
    private CategoryTestRepository categoryTestRepository;

    @Override
    public DataListDTO doSearch(HistoryDTO historyDTO) {
        if (historyDTO.getCreateFrom() != null) {
            historyDTO.setCreateFrom(historyDTO.getCreateFrom());
        }
        if (historyDTO.getCreateTo() != null) {
            historyDTO.setCreateTo(historyDTO.getCreateTo());
        }
        if (historyDTO.getCreateFrom() != null && historyDTO.getCreateTo() != null) {
            if (DateUtil.compareDate(historyDTO.getCreateFrom(), historyDTO.getCreateTo()) == 0) {
                throw new BusinessException("Thời điểm từ phải nhỏ hơn thời điểm đến");
            }
//            historyDTO.setCreateFromString(historyDTO.getCreateFrom());
//            historyDTO.setCreateToString(historyDTO.getCreateTo());
        }

        List<HistoryDTO> resultDataDTO = historyRepository.doSearch(historyDTO);

        if (resultDataDTO.size() > 0) {
            for (int i = 0; i < resultDataDTO.get(0).getNumberTest(); i++) {
                if (i < resultDataDTO.size()) {
                    List<Long> listRank = historyRepository.getListRankTest(resultDataDTO.get(i));
                    int index = listRank.indexOf(historyDTO.getUserId()) + 1;
                    int sizeUserId = listRank.size();
                    String rankOfUser = String.valueOf(index) + "/" + String.valueOf(sizeUserId);
                    resultDataDTO.get(i).setRankOfUser(rankOfUser);
                }
            }
        }
        DataListDTO dataListDTO = new DataListDTO();
        dataListDTO.setData(resultDataDTO);
        dataListDTO.setStart(1);
        dataListDTO.setSize(resultDataDTO.size());
        dataListDTO.setTotal(resultDataDTO.size());
        return dataListDTO;
    }

    @Override
    public DataListDTO listStudentFaild(HistoryDTO history) {
        if (history.getTestId() != null) {
            ResultDataDTO resultDto = historyRepository.listStudentNotPass(history, 1l);
            DataListDTO data = new DataListDTO();
            data.setData(resultDto.getData());
            data.setTotal(resultDto.getTotal());
            data.setSize(resultDto.getTotal());
            data.setStart(1);
            return data;
        } else {
            throw new BusinessException(environment.getProperty("error_test_id"));
        }
    }

    @Override
    public DataListDTO getDetailHistoryTest(HistoryDTO historyDTO) {
        List<HistoryDTO> resultDataDTO = historyRepository.getDetailHistoryTest(historyDTO);
        Long numberDoTest = historyRepository.numberDoTest(historyDTO.getTestId(), historyDTO.getUserId());
        DataListDTO dataListDTO = new DataListDTO();
        dataListDTO.setData(resultDataDTO);
        dataListDTO.setStart(1);
        dataListDTO.setSize(resultDataDTO.size());
        dataListDTO.setTotal(numberDoTest.intValue());
        return dataListDTO;
    }

    @Override
    public List<HistoryDTO> getListRankOfTest(HistoryDTO historyDTO) {
        List<HistoryDTO> listRank = historyRepository.getListRankOfTest(historyDTO);
        Long numberStudent = historyRepository.numberStudentDoTest(historyDTO.getTestId());
        if (listRank.size() != 0) {
            listRank.get(0).setNumberUser(numberStudent);
        }
        return listRank;
    }

    public MinitestDTO convertFormatTestListening(List<DetailHistoryDTO> listQuestionListeningChoosenDTOSChildRootPart, long stt) {
        MinitestDTO minitestDTOPart = new MinitestDTO();
        List<CategoryMinitestDTO> lisCategoryMinitestDTOsPart = new ArrayList<>();
        CategoryMinitestDTO categoryMinitestDTOPart = new CategoryMinitestDTO();
        List<CategoryListeningChoosenDTO> lstCategoryListeningChooseDTO = new ArrayList<>();
        Long idHistory = listQuestionListeningChoosenDTOSChildRootPart.get(0).getParentId();

        long sttQuestion = stt;

        for (int i = 0; i < listQuestionListeningChoosenDTOSChildRootPart.size(); i++) {
            if (lstCategoryListeningChooseDTO.size() == 0 ||
                    !listQuestionListeningChoosenDTOSChildRootPart.get(i).getCategoryId().equals(lstCategoryListeningChooseDTO.get(lstCategoryListeningChooseDTO.size() - 1).getCategoryId())) {
                CategoryListeningChoosenDTO categoryListeningChoosenDTOChild = new CategoryListeningChoosenDTO();
                List<QuestionListeningChoosenDTO> listQuestionListeningChoosenDTOSChild = new ArrayList<>();
                QuestionListeningChoosenDTO questionListeningChoosenDTO = new QuestionListeningChoosenDTO();

                questionListeningChoosenDTO.setCategoryId(listQuestionListeningChoosenDTOSChildRootPart.get(i).getCategoryId());
                questionListeningChoosenDTO.setId(listQuestionListeningChoosenDTOSChildRootPart.get(i).getQuestionId());
                questionListeningChoosenDTO.setPartName(listQuestionListeningChoosenDTOSChildRootPart.get(i).getPart());
                questionListeningChoosenDTO.setStt(Long.valueOf(i));

                listQuestionListeningChoosenDTOSChild.add(questionListeningChoosenDTO);
                categoryListeningChoosenDTOChild.setListQuestionListeningChooseDTO(listQuestionListeningChoosenDTOSChild);
                categoryListeningChoosenDTOChild.setCategoryId(questionListeningChoosenDTO.getCategoryId());
                lstCategoryListeningChooseDTO.add(categoryListeningChoosenDTOChild);
            } else if (listQuestionListeningChoosenDTOSChildRootPart.get(i).getCategoryId().equals(lstCategoryListeningChooseDTO.get(lstCategoryListeningChooseDTO.size() - 1).getCategoryId())) {

                QuestionListeningChoosenDTO questionListeningChoosenDTO = new QuestionListeningChoosenDTO();
                questionListeningChoosenDTO.setCategoryId(listQuestionListeningChoosenDTOSChildRootPart.get(i).getCategoryId());
                questionListeningChoosenDTO.setId(listQuestionListeningChoosenDTOSChildRootPart.get(i).getQuestionId());
                questionListeningChoosenDTO.setPartName(listQuestionListeningChoosenDTOSChildRootPart.get(i).getPart());
                questionListeningChoosenDTO.setStt(Long.valueOf(i));

                lstCategoryListeningChooseDTO.get(lstCategoryListeningChooseDTO.size() - 1).getListQuestionListeningChooseDTO().add(questionListeningChoosenDTO);
            }
        }

        for (CategoryListeningChoosenDTO categoryListeningChoosenDTO : lstCategoryListeningChooseDTO) {
            List<QuestionMinitestDTO> listQuestionMinitestDTOsPart = new ArrayList<>();
            for (QuestionListeningChoosenDTO questionListeningChoosenDTO : categoryListeningChoosenDTO.getListQuestionListeningChooseDTO()) {
                sttQuestion = sttQuestion + 1;
                categoryMinitestDTOPart = categoryRepository.getCategoryById(questionListeningChoosenDTO.getCategoryId());

                QuestionMinitestDTO ques = questionAnswerListeningRepository.getQuestionListeningHistory(questionListeningChoosenDTO.getId(), questionListeningChoosenDTO.getPartName(), idHistory);
                ques.setStt(ques.getSentenceNo());
                listQuestionMinitestDTOsPart.add(ques);
            }
            categoryMinitestDTOPart.setListQuestionMinitestDTOS(listQuestionMinitestDTOsPart);
            lisCategoryMinitestDTOsPart.add(categoryMinitestDTOPart);
        }

        minitestDTOPart.setListCategoryMinitestDTOS(lisCategoryMinitestDTOsPart);

        return minitestDTOPart;
    }

    public MinitestDTO convertFormatTestReading(List<DetailHistoryDTO> listQuestionReadingChoosenDTOSChildRootPart, long stt) {
        MinitestDTO minitestDTOPart = new MinitestDTO();
        List<CategoryMinitestDTO> lisCategoryMinitestDTOsPart = new ArrayList<>();
        CategoryMinitestDTO categoryMinitestDTOPart = new CategoryMinitestDTO();
        List<CategoryReadingChoosenDTO> lstCategoryReadingChooseDTO = new ArrayList<>();
        Long idHistory = listQuestionReadingChoosenDTOSChildRootPart.get(0).getParentId();
        long sttQuestion = stt;

        for (int i = 0; i < listQuestionReadingChoosenDTOSChildRootPart.size(); i++) {
            if (lstCategoryReadingChooseDTO.size() == 0 ||
                    !listQuestionReadingChoosenDTOSChildRootPart.get(i).getCategoryId().equals(lstCategoryReadingChooseDTO.get(lstCategoryReadingChooseDTO.size() - 1).getCategoryId())) {
                CategoryReadingChoosenDTO categoryReadingChoosenDTOChild = new CategoryReadingChoosenDTO();
                List<QuestionReadingChoosenDTO> listQuestionReadingChoosenDTOSChild = new ArrayList<>();
                QuestionReadingChoosenDTO questionReadingChoosenDTO = new QuestionReadingChoosenDTO();

                questionReadingChoosenDTO.setCategoryId(listQuestionReadingChoosenDTOSChildRootPart.get(i).getCategoryId());
                questionReadingChoosenDTO.setId(listQuestionReadingChoosenDTOSChildRootPart.get(i).getQuestionId());
                questionReadingChoosenDTO.setPartName(listQuestionReadingChoosenDTOSChildRootPart.get(i).getPart());
                questionReadingChoosenDTO.setStt(Long.valueOf(i));

                listQuestionReadingChoosenDTOSChild.add(questionReadingChoosenDTO);
                categoryReadingChoosenDTOChild.setListQuestionReadingChooseDTO(listQuestionReadingChoosenDTOSChild);
                categoryReadingChoosenDTOChild.setCategoryId(questionReadingChoosenDTO.getCategoryId());
                lstCategoryReadingChooseDTO.add(categoryReadingChoosenDTOChild);
            } else if (listQuestionReadingChoosenDTOSChildRootPart.get(i).getCategoryId().equals(lstCategoryReadingChooseDTO.get(lstCategoryReadingChooseDTO.size() - 1).getCategoryId())) {
                QuestionReadingChoosenDTO questionReadingChoosenDTO = new QuestionReadingChoosenDTO();
                questionReadingChoosenDTO.setCategoryId(listQuestionReadingChoosenDTOSChildRootPart.get(i).getCategoryId());
                questionReadingChoosenDTO.setId(listQuestionReadingChoosenDTOSChildRootPart.get(i).getQuestionId());
                questionReadingChoosenDTO.setPartName(listQuestionReadingChoosenDTOSChildRootPart.get(i).getPart());
                questionReadingChoosenDTO.setStt(Long.valueOf(i));
                lstCategoryReadingChooseDTO.get(lstCategoryReadingChooseDTO.size() - 1).getListQuestionReadingChooseDTO().add(questionReadingChoosenDTO);
            }
        }

        for (CategoryReadingChoosenDTO categoryReadingChoosenDTO : lstCategoryReadingChooseDTO) {
            List<QuestionMinitestDTO> listQuestionMinitestDTOsPart = new ArrayList<>();
            for (QuestionReadingChoosenDTO questionReadingChoosenDTO : categoryReadingChoosenDTO.getListQuestionReadingChooseDTO()) {
                sttQuestion = sttQuestion + 1;
                categoryMinitestDTOPart = categoryRepository.getCategoryById(questionReadingChoosenDTO.getCategoryId());

                QuestionMinitestDTO ques = questionAnswerReadingRepository.getQuestionReadingHistory(questionReadingChoosenDTO.getId(), questionReadingChoosenDTO.getPartName(), idHistory);
                ques.setStt(ques.getSentenceNo());
                listQuestionMinitestDTOsPart.add(ques);
            }
            categoryMinitestDTOPart.setListQuestionMinitestDTOS(listQuestionMinitestDTOsPart);
            lisCategoryMinitestDTOsPart.add(categoryMinitestDTOPart);
        }
        minitestDTOPart.setListCategoryMinitestDTOS(lisCategoryMinitestDTOsPart);
        return minitestDTOPart;
    }

    @Override
    public List<MinitestDTO> getDetailHistoryFullTest(HistoryDTO historyDTO) {
        List<MinitestDTO> lst = new ArrayList<>();
        if (historyDTO.getCreateDate() != null) {
            String x = DateUtil.getSqlDateTime(historyDTO.getCreateDate());
            historyDTO.setCreateDateString(x);
        }
        List<DetailHistoryDTO> listDetail = detailHistoryRepository.getListDetailHistory(historyDTO);
        if (listDetail.size() == 0) {
            throw new BusinessException(MessageUtils.getMessage("detail_fulltest_null"));
        }
        HistoryDTO history = historyRepository.getHistoryById(historyDTO.getId());
        List<DetailHistoryDTO> listQuestionListeningChoosenDTOSChildRootPart1 = new ArrayList<>();
        List<DetailHistoryDTO> listQuestionListeningChoosenDTOSChildRootPart2 = new ArrayList<>();
        List<DetailHistoryDTO> listQuestionListeningChoosenDTOSChildRootPart3 = new ArrayList<>();
        List<DetailHistoryDTO> listQuestionListeningChoosenDTOSChildRootPart4 = new ArrayList<>();
        List<DetailHistoryDTO> listQuestionReadingChoosenDTOSChildRootPart5 = new ArrayList<>();
        List<DetailHistoryDTO> listQuestionReadingChoosenDTOSChildRootPart6 = new ArrayList<>();
        List<DetailHistoryDTO> listQuestionReadingChoosenDTOSChildRootPart7 = new ArrayList<>();

        String nameCategory ="";
        if(history.getTestName() != null){
            String[] cutName = history.getTestName().split(" - ");
            nameCategory = cutName[0];
        }

        for (DetailHistoryDTO detailHistoryDTO : listDetail) {
            if (detailHistoryDTO.getPart().equalsIgnoreCase("PART1")) {
                listQuestionListeningChoosenDTOSChildRootPart1.add(detailHistoryDTO);
            }
            if (detailHistoryDTO.getPart().equalsIgnoreCase("PART2")) {
                listQuestionListeningChoosenDTOSChildRootPart2.add(detailHistoryDTO);
            }
            if (detailHistoryDTO.getPart().equalsIgnoreCase("PART3")) {
                listQuestionListeningChoosenDTOSChildRootPart3.add(detailHistoryDTO);
            }
            if (detailHistoryDTO.getPart().equalsIgnoreCase("PART4")) {
                listQuestionListeningChoosenDTOSChildRootPart4.add(detailHistoryDTO);
            }
            if (detailHistoryDTO.getPart().equalsIgnoreCase("PART5")) {
                listQuestionReadingChoosenDTOSChildRootPart5.add(detailHistoryDTO);
            }
            if (detailHistoryDTO.getPart().equalsIgnoreCase("PART6")) {
                listQuestionReadingChoosenDTOSChildRootPart6.add(detailHistoryDTO);
            }
            if (detailHistoryDTO.getPart().equalsIgnoreCase("PART7")) {
                listQuestionReadingChoosenDTOSChildRootPart7.add(detailHistoryDTO);
            }
        }
        //part1
        MinitestDTO minitestDTOPart1 = convertFormatTestListening(listQuestionListeningChoosenDTOSChildRootPart1, 0);
        List<CategoryTestDTO> lstCategoryTestPart1 = categoryTestRepository.getListCategoryTestByTestIdAndCategoryName(historyDTO.getTestId(),nameCategory,"PART1");
        if(lstCategoryTestPart1.size() != 0){
            minitestDTOPart1.setPathFile(lstCategoryTestPart1.get(0).getPathFile());
        }
        minitestDTOPart1.setPartName("PART1");
        String[] part1 = history.getPart1().trim().split("/");
        int totalCorrectPart1 = Integer.parseInt(part1[0]);
        int totalQuestionPart1 = Integer.parseInt(part1[1]);
        minitestDTOPart1.setTotalCorectAnswer(totalCorrectPart1);
        minitestDTOPart1.setTotalQuestion(totalQuestionPart1);
        //part2
        MinitestDTO minitestDTOPart2 = convertFormatTestListening(listQuestionListeningChoosenDTOSChildRootPart2, listQuestionListeningChoosenDTOSChildRootPart1.size());
        List<CategoryTestDTO> lstCategoryTestPart2 = categoryTestRepository.getListCategoryTestByTestIdAndCategoryName(historyDTO.getTestId(),nameCategory,"PART2");
        if(lstCategoryTestPart2.size() != 0){
            minitestDTOPart2.setPathFile(lstCategoryTestPart2.get(0).getPathFile());
        }
        minitestDTOPart2.setPartName("PART2");
        String[] part2 = history.getPart2().trim().split("/");
        int totalCorrectPart2 = Integer.parseInt(part2[0]);
        int totalQuestionPart2 = Integer.parseInt(part2[1]);
        minitestDTOPart2.setTotalCorectAnswer(totalCorrectPart2);
        minitestDTOPart2.setTotalQuestion(totalQuestionPart2);
        //part3
        MinitestDTO minitestDTOPart3 = convertFormatTestListening(listQuestionListeningChoosenDTOSChildRootPart3, listQuestionListeningChoosenDTOSChildRootPart1.size()
                + listQuestionListeningChoosenDTOSChildRootPart2.size());
        List<CategoryTestDTO> lstCategoryTestPart3 = categoryTestRepository.getListCategoryTestByTestIdAndCategoryName(historyDTO.getTestId(),nameCategory,"PART3");
        if(lstCategoryTestPart3.size() != 0){
            minitestDTOPart3.setPathFile(lstCategoryTestPart3.get(0).getPathFile());
        }
        minitestDTOPart3.setPartName("PART3");
        String[] part3 = history.getPart3().trim().split("/");
        int totalCorrectPart3 = Integer.parseInt(part3[0]);
        int totalQuestionPart3 = Integer.parseInt(part3[1]);
        minitestDTOPart3.setTotalCorectAnswer(totalCorrectPart3);
        minitestDTOPart3.setTotalQuestion(totalQuestionPart3);
        //part4
        MinitestDTO minitestDTOPart4 = convertFormatTestListening(listQuestionListeningChoosenDTOSChildRootPart4, listQuestionListeningChoosenDTOSChildRootPart1.size()
                + listQuestionListeningChoosenDTOSChildRootPart2.size() + listQuestionListeningChoosenDTOSChildRootPart3.size());
        List<CategoryTestDTO> lstCategoryTestPart4 = categoryTestRepository.getListCategoryTestByTestIdAndCategoryName(historyDTO.getTestId(),nameCategory,"PART4");
        if(lstCategoryTestPart4.size() != 0){
            minitestDTOPart4.setPathFile(lstCategoryTestPart4.get(0).getPathFile());
        }
        minitestDTOPart4.setPartName("PART4");
        String[] part4 = history.getPart4().trim().split("/");
        int totalCorrectPart4 = Integer.parseInt(part4[0]);
        int totalQuestionPart4 = Integer.parseInt(part4[1]);
        minitestDTOPart4.setTotalCorectAnswer(totalCorrectPart4);
        minitestDTOPart4.setTotalQuestion(totalQuestionPart4);
        //part5
        MinitestDTO minitestDTOPart5 = convertFormatTestReading(listQuestionReadingChoosenDTOSChildRootPart5, listQuestionListeningChoosenDTOSChildRootPart1.size()
                + listQuestionListeningChoosenDTOSChildRootPart2.size() + listQuestionListeningChoosenDTOSChildRootPart3.size() +
                listQuestionListeningChoosenDTOSChildRootPart4.size());
        minitestDTOPart5.setPartName("PART5");
        String[] part5 = history.getPart5().trim().split("/");
        int totalCorrectPart5 = Integer.parseInt(part5[0]);
        int totalQuestionPart5 = Integer.parseInt(part5[1]);
        minitestDTOPart5.setTotalCorectAnswer(totalCorrectPart5);
        minitestDTOPart5.setTotalQuestion(totalQuestionPart5);
        //part6
        MinitestDTO minitestDTOPart6 = convertFormatTestReading(listQuestionReadingChoosenDTOSChildRootPart6, listQuestionListeningChoosenDTOSChildRootPart1.size()
                + listQuestionListeningChoosenDTOSChildRootPart2.size() + listQuestionListeningChoosenDTOSChildRootPart3.size() +
                listQuestionListeningChoosenDTOSChildRootPart4.size() + listQuestionReadingChoosenDTOSChildRootPart5.size());
        minitestDTOPart6.setPartName("PART6");
        String[] part6 = history.getPart6().trim().split("/");
        int totalCorrectPart6 = Integer.parseInt(part6[0]);
        int totalQuestionPart6 = Integer.parseInt(part6[1]);
        minitestDTOPart6.setTotalCorectAnswer(totalCorrectPart6);
        minitestDTOPart6.setTotalQuestion(totalQuestionPart6);
        //part7
        MinitestDTO minitestDTOPart7 = convertFormatTestReading(listQuestionReadingChoosenDTOSChildRootPart7, listQuestionListeningChoosenDTOSChildRootPart1.size()
                + listQuestionListeningChoosenDTOSChildRootPart2.size() + listQuestionListeningChoosenDTOSChildRootPart3.size() +
                listQuestionListeningChoosenDTOSChildRootPart4.size() + listQuestionReadingChoosenDTOSChildRootPart5.size() + listQuestionReadingChoosenDTOSChildRootPart6.size());
        minitestDTOPart7.setPartName("PART7");
        String[] part7 = history.getPart7().trim().split("/");
        int totalCorrectPart7 = Integer.parseInt(part7[0]);
        int totalQuestionPart7 = Integer.parseInt(part7[1]);
        minitestDTOPart7.setTotalCorectAnswer(totalCorrectPart7);
        minitestDTOPart7.setTotalQuestion(totalQuestionPart7);

        minitestDTOPart1.setSumScore(history.getListeningScore().intValue());
        minitestDTOPart5.setSumScore(history.getReadingScore().intValue());
        if (minitestDTOPart1 != null && minitestDTOPart2 != null && minitestDTOPart3 != null && minitestDTOPart4 != null && minitestDTOPart5 != null && minitestDTOPart6 != null && minitestDTOPart7 != null) {
            lst.add(minitestDTOPart1);
            lst.add(minitestDTOPart2);
            lst.add(minitestDTOPart3);
            lst.add(minitestDTOPart4);
            lst.add(minitestDTOPart5);
            lst.add(minitestDTOPart6);
            lst.add(minitestDTOPart7);
        }

        return lst;
    }

    @Override
    public List<QuestionAnswersDTO> getDetailHistoryListenFill(HistoryPracticesDTO historyPracticesDTO){
        List<QuestionAnswersDTO> lstHistory = new ArrayList<>();
        if (historyPracticesDTO.getCreateDate() != null) {
            String x = DateUtil.getSqlDateTime(historyPracticesDTO.getCreateDate());
            historyPracticesDTO.setCreateDateString(x);
        }
        List<DetailHistoryListenFillDTO> lstDetail = detailHistoryListenFillRepository.getListDetailHistoryListenFill(historyPracticesDTO);
        for (DetailHistoryListenFillDTO detailHistoryListenFillDTO : lstDetail){
            QuestionAnswersDTO result = questionAnswerListeningRepository.getQuestionFillById(detailHistoryListenFillDTO.getQuestionId());
            String[] a = result.getAnswer().toLowerCase().split("[\\|]");
            String[] userFill = detailHistoryListenFillDTO.getUserFill().toLowerCase().split("[\\|]");
            List<Integer> lstCount = new ArrayList<>();
            int indexStringAnswer = 0;
            List<String[]> lstFill = new ArrayList<>();
            List<Integer> lstIndexCorrect = new ArrayList<>();
            List<Integer> lstIndexInCorrect = new ArrayList<>();

            String[] name = result.getName().split("\n");
            List<String[]> lstAnswer = splitAnswer(name);
            if(historyPracticesDTO.getPart().equalsIgnoreCase("PART3.1")){
                lstAnswer.removeIf(s -> s.length == 0);
            }

//            for (String retval: result.getName().split("\n")) {

//                int counter = retval.split("<", -1).length - 1;
//                lstCount.add(counter);
//            }

            if(lstAnswer.size() != 0){
                for(String[] answer : lstAnswer){
                    if(answer.length != 0){
                        String[] rowFill = new String[answer.length];
                        for(int i =0; i<answer.length ;i++){
                            rowFill[i] = userFill[indexStringAnswer];
                            indexStringAnswer++;
                        }
                        lstFill.add(rowFill);
                    } else {
                        lstFill.add(null);
                    }
                }
            }
            if(detailHistoryListenFillDTO.getIndexCorrect() != null){
                String[] indexCorrect = detailHistoryListenFillDTO.getIndexCorrect().split("[\\|]");
                for(int i =0; i< indexCorrect.length; i++){
                    if(indexCorrect[i] != null){
                        lstIndexCorrect.add(Integer.parseInt(indexCorrect[i].trim()));
                    }else {
                        lstIndexCorrect.add(null);
                    }
                }
            }
            if(detailHistoryListenFillDTO.getIndexInCorrect() != null){
                String[] indexInCorrect = detailHistoryListenFillDTO.getIndexInCorrect().split("[\\|]");
                for(int i =0; i< indexInCorrect.length; i++){
                    if(indexInCorrect[i] != null){
                        String c = indexInCorrect[i].trim();
                        lstIndexInCorrect.add(Integer.parseInt(indexInCorrect[i].trim()));
                    } else {
                        lstIndexInCorrect.add(null);
                    }
                }
            }
            long lstSize = lstFill.size();
            result.setLstSize(lstSize);
            result.setLstIndexCorrect(lstIndexCorrect);
            result.setLstIndexInCorrect(lstIndexInCorrect);
            result.setLstAnswerCut(lstAnswer);
            result.setLstUserFill(lstFill);
            lstHistory.add(result);
        }
        return lstHistory;
    }

    @Override
    public List<QuestionAnswersDTO> getDetailHistoryReadWordFill(HistoryPracticesDTO historyPracticesDTO){
        List<QuestionAnswersDTO> lstHistory = new ArrayList<>();
        if (historyPracticesDTO.getCreateDate() != null) {
            String x = DateUtil.getSqlDateTime(historyPracticesDTO.getCreateDate());
            historyPracticesDTO.setCreateDateString(x);
        }
        List<DetailHistoryReadingDTO> lstDetail = detailHistoryReadingRepository.getListDetailHistoryReadWordFill(historyPracticesDTO);
        for (DetailHistoryReadingDTO detailHistoryReadingDTO : lstDetail){
            QuestionAnswersDTO result = questionAnswerReadingRepository.getDetailById(detailHistoryReadingDTO.getQuestionId());
//            String[] a = result.getAnswer().toLowerCase().split("[\\|]");
//            String[] userChoose = result.getUserChoose().toLowerCase().split("[\\|]");
            result.setNumberSelected(detailHistoryReadingDTO.getNumberSelected());
            if(detailHistoryReadingDTO.getIndexCorrect() != null){
                result.setIndexCorrect(Long.parseLong(detailHistoryReadingDTO.getIndexCorrect()));
            }
            if(detailHistoryReadingDTO.getIndexInCorrect() != null){
                result.setIndexInCorrect(Long.parseLong(detailHistoryReadingDTO.getIndexInCorrect()));
            }
            result.setUserChoose(detailHistoryReadingDTO.getUserChoose());
            lstHistory.add(result);
        }
        return lstHistory;
    }

    @Override
    public List<QuestionOfReadingAndComplitingDTO> getDetailHistoryReadingCompletedPassage(HistoryPracticesDTO historyPracticesDTO){
        List<QuestionOfReadingAndComplitingDTO> lst = new ArrayList<>();
        if (historyPracticesDTO.getCreateDate() != null) {
            String x = DateUtil.getSqlDateTime(historyPracticesDTO.getCreateDate());
            historyPracticesDTO.setCreateDateString(x);
        }
        List<DetailHistoryReadingDTO> lstDetail = detailHistoryReadingRepository.getListDetailHistoryReadWordFill(historyPracticesDTO);

        for(int i=0; i < lstDetail.size(); i++){
            QuestionAnswersDTO result = questionAnswerReadingRepository.getDetailById(lstDetail.get(i).getQuestionId());
            int numberCorrect = 0;
            if(result != null){
                result.setUserChoose(lstDetail.get(i).getUserChoose());
                if(lstDetail.get(i).getIndexCorrect() != null){
                    result.setIndexCorrect(Long.parseLong(lstDetail.get(i).getIndexCorrect()));
                }
                if(lstDetail.get(i).getIndexInCorrect() != null){
                    result.setIndexInCorrect(Long.parseLong(lstDetail.get(i).getIndexInCorrect()));
                }
            }
            if(lst.size() == 0 || !lstDetail.get(i).getCategoryId().equals(lst.get(lst.size() - 1)
                    .getListAnswerToChoose().get(lst.get(lst.size() - 1).getListAnswerToChoose().size() - 1).getParentId())){
                QuestionOfReadingAndComplitingDTO questionOfReadingAndComplitingDTO = categoryRepository.getCategoryReadingAndCompliting(lstDetail.get(i).getCategoryId());
                List<QuestionAnswersDTO> listQuestionAnswer = new ArrayList<>();
                listQuestionAnswer.add(result);
                questionOfReadingAndComplitingDTO.setListAnswerToChoose(listQuestionAnswer);
                lst.add(questionOfReadingAndComplitingDTO);
            } else if(lstDetail.get(i).getCategoryId().equals(lst.get(lst.size() - 1)
                    .getListAnswerToChoose().get(lst.get(lst.size() - 1).getListAnswerToChoose().size() - 1).getParentId())){
                lst.get(lst.size() - 1).getListAnswerToChoose().add(result);
            }

        }
        return lst;
    }

    public List<String[]> splitAnswer(String[] name){
        List<String[]> lstAnswer = new ArrayList<>();
        for(String data : name){
            String result = "";
            String getIndexValue;
            int counter = data.split("<", -1).length - 1;
            List<String> temp = new ArrayList<>();
//            String[] temp = new String[counter];
            for(int i=0;i<counter;i++){
                if (data.contains("<")) {
                    getIndexValue = data.substring(data.indexOf("<") + 1, data.indexOf(">"));
                    if(getIndexValue.indexOf(" ") != -1){
                        String start = getIndexValue.trim().split(" ")[0];
                        String end = getIndexValue.trim().split(" ")[1];
                        temp.add(start);
                        temp.add(end);
                    } else {
                        temp.add(getIndexValue);
                    }
                    result = data.replace("<" + getIndexValue + ">", "");
                    data = result;
                } else {
                    temp.add(null);
                }
            }
            String[] answer;
            if(temp.size() != 0){
                answer = temp.toArray(new String[temp.size()]);
            } else {
                answer = temp.toArray(new String[0]);
            }
            lstAnswer.add(answer);
        }
        return lstAnswer;
    }
}
