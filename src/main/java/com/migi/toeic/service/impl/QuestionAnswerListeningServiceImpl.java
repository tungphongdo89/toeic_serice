package com.migi.toeic.service.impl;

import com.migi.toeic.authen.model.RequestPractice;
import com.migi.toeic.base.DataListDTO;
import com.migi.toeic.base.ResultDataDTO;
import com.migi.toeic.dto.*;
import com.migi.toeic.exception.BusinessException;
import com.migi.toeic.respositories.CategoryRepository;
import com.migi.toeic.respositories.DetailHistoryListenFillRepository;
import com.migi.toeic.respositories.HistoryPracticesRepository;
import com.migi.toeic.respositories.QuestionAnswerListeningRepository;
import com.migi.toeic.service.QuestionAnswerListeningService;
import com.migi.toeic.utils.MessageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class QuestionAnswerListeningServiceImpl implements QuestionAnswerListeningService {
    @Autowired
    QuestionAnswerListeningRepository questionAnswerListeningRepository;
    @Autowired
    HistoryPracticesRepository historyPracticesRepository;
    @Autowired
    DetailHistoryListenFillRepository detailHistoryListenFillRepository;
    @Autowired
    CategoryRepository categoryRepository;

    @Override
    public DataListDTO getListQuestionListening(QuestionAnswerListeningDTO obj) {
        ResultDataDTO resultDto = questionAnswerListeningRepository.getListQuestionReading(obj);
        DataListDTO data = new DataListDTO();
        data.setData(resultDto.getData());
        data.setTotal(resultDto.getTotal());
        data.setSize(resultDto.getTotal());
        data.setStart(1);
        return data;
    }

    @Override
    public QuestionAnswerListeningDTO getDetail(Long id) {
        if (id == null) {
            throw new BusinessException(MessageUtils.getMessage("error_miss_info"));
        }
        return questionAnswerListeningRepository.getDetail(id);
    }

    @Override
    public Long createQAListening(QuestionAnswerListeningDTO obj) {
        if (obj.getName() == null || obj.getAnswer() == null || obj.getParentId() == null || obj.getStatus() == null || obj.getAnswersToChoose() == null || obj.getStartTime() == null || obj.getEndTime() == null) {
            throw new BusinessException(MessageUtils.getMessage("error_miss_info"));
        }
        return questionAnswerListeningRepository.insert(obj.toModel());
    }

    @Override
    public Long updateQAListening(QuestionAnswerListeningDTO obj) {
        if (obj.getName() == null || obj.getAnswer() == null || obj.getParentId() == null || obj.getStatus() == null || obj.getAnswersToChoose() == null || obj.getStartTime() == null || obj.getEndTime() == null) {
            throw new BusinessException(MessageUtils.getMessage("error_miss_info"));
        }
        questionAnswerListeningRepository.update(obj.toModel());
        return 1L;
    }

    @Override
    public Long deleteQAListening(QuestionAnswerListeningDTO obj) {
        questionAnswerListeningRepository.delete(obj.toModel());
        return 1L;
    }

    public List<QuestionAnswersDTO> toListAnswersToChoose(String answersToChoose) {
        List<QuestionAnswersDTO> lstResult = new ArrayList<>();
        String[] lst = answersToChoose.split("[|]");
        for (String item : lst) {
            QuestionAnswersDTO q = new QuestionAnswersDTO();
            q.setAnswer(item);
            lstResult.add(q);
        }
        return lstResult;
    }

    public void toListStartTime(QuestionAnswerListeningDTO quest) {
        if (quest.getStartTime() != null) {
            String[] lst = quest.getStartTime().split("[|]");
            if (lst.length == quest.getListAnswerToChoose().size()) {
                for (int i = 0; i < lst.length; i++) {
                    quest.getListAnswerToChoose().get(i).setStartTime(lst[i]);
                }
            }
        }
    }


    @Override
    public List<QuestionAnswerListeningDTO> listQuestionByTopicIdAndLevelCode(RequestPractice requestPractice) {
        if (requestPractice.getLevelCode().equals("EASY")) {
            List<QuestionAnswerListeningDTO> lstEasy =
                    questionAnswerListeningRepository
                            .listMultiQuestionListenBytotal(requestPractice.getTopicId(), 10, "EASY",requestPractice.getQid());
            for (QuestionAnswerListeningDTO quest : lstEasy) {
                quest.setListAnswerToChoose(toListAnswersToChoose(quest.getAnswersToChoose()));
                toListStartTime(quest);
            }
            List<QuestionAnswerListeningDTO> lstEasy2 = new ArrayList<>();
            if(lstEasy.size() > 5){
                for(int i=0;i<5;i++){
                    lstEasy2.add(lstEasy.get(i));
                }
            }
            else {
                for(int i=0;i<lstEasy.size();i++){
                    lstEasy2.add(lstEasy.get(i));
                }
            }

            return lstEasy2;
        }
        if (requestPractice.getLevelCode().equals("MEDIUM")) {
            List<QuestionAnswerListeningDTO> lstEasy =
                    questionAnswerListeningRepository
                            .listMultiQuestionListenBytotal(requestPractice.getTopicId(), 3, "EASY",requestPractice.getQid());
            List<QuestionAnswerListeningDTO> lstMedium =
                    questionAnswerListeningRepository
                            .listMultiQuestionListenBytotal(requestPractice.getTopicId(), 1, "MEDIUM",requestPractice.getQid());
            List<QuestionAnswerListeningDTO> lstHard =
                    questionAnswerListeningRepository
                            .listMultiQuestionListenBytotal(requestPractice.getTopicId(), 1, "DIFFICULT",requestPractice.getQid());
            lstEasy.addAll(lstMedium);
            lstEasy.addAll(lstHard);
            for (QuestionAnswerListeningDTO quest : lstEasy) {
                quest.setListAnswerToChoose(toListAnswersToChoose(quest.getAnswersToChoose()));
                toListStartTime(quest);
            }
            return lstEasy;
        }
        if (requestPractice.getLevelCode().equals("DIFFICULT")) {
            List<QuestionAnswerListeningDTO> lstEasy =
                    questionAnswerListeningRepository
                            .listMultiQuestionListenBytotal(requestPractice.getTopicId(), 2, "EASY",requestPractice.getQid());
            List<QuestionAnswerListeningDTO> lstMedium =
                    questionAnswerListeningRepository
                            .listMultiQuestionListenBytotal(requestPractice.getTopicId(), 2, "MEDIUM",requestPractice.getQid());
            List<QuestionAnswerListeningDTO> lstHard =
                    questionAnswerListeningRepository
                            .listMultiQuestionListenBytotal(requestPractice.getTopicId(), 1, "DIFFICULT",requestPractice.getQid());
            lstEasy.addAll(lstMedium);
            lstEasy.addAll(lstHard);
            for (QuestionAnswerListeningDTO quest : lstEasy) {
                quest.setListAnswerToChoose(toListAnswersToChoose(quest.getAnswersToChoose()));
                toListStartTime(quest);
            }
            return lstEasy;
        }
        return null;
    }

    @Override
    public QuestionAnswerListeningDTO getAnswerListeningQuestion(RequestPractice rp) {
        List<QuestionAnswerListeningDTO> lstEasy =
                questionAnswerListeningRepository
                        .listMultiQuestionListenBytotal(rp.getTopicId(), 10, "EASY",rp.getQid());
        for (QuestionAnswerListeningDTO quest : lstEasy) {
            quest.setListAnswerToChoose(toListAnswersToChoose(quest.getAnswersToChoose()));
            toListStartTime(quest);
        }
        if (rp.getUserChoose().equals(lstEasy.get(0).getAnswer().split("[|]")[0])) {
            lstEasy.get(0).setCorrect(true);
        }
        lstEasy.get(0).setAnswer(lstEasy.get(0).getAnswer().split("[|]")[0]);
        lstEasy.get(0).setUserChoose(rp.getUserChoose());
        return lstEasy.get(0);
    }

    @Override
    public List<QuestionAnswerListeningDTO> getListQuestionOfListenAndFill(RequestPractice requestPractice){
        if (requestPractice.getLevelCode().equals("EASY")) {
            if(requestPractice.getPart().equalsIgnoreCase("PART3.1") || requestPractice.getPart().equalsIgnoreCase("PART4.1") ){
                List<QuestionAnswerListeningDTO> lstEasy = new ArrayList<>();
                List<CategoryDTO> lstCategory = categoryRepository.getListCategoryListenFill(requestPractice.getTopicName(), "EASY", 3);
                for(int i=0;i< lstCategory.size();i++){
                    QuestionAnswerListeningDTO question = questionAnswerListeningRepository.getListQuestionByCategoryIdListenFill(lstCategory.get(i).getCategoryId());
                    lstEasy.add(question);
                }
                return lstEasy;
            } else {
                List<QuestionAnswerListeningDTO> lstEasy =
                        questionAnswerListeningRepository
                                .getListQuestionOfListenAndFill(requestPractice.getTopicName(),"EASY",5);
                return lstEasy;
            }

        }
        if (requestPractice.getLevelCode().equals("MEDIUM")) {
            if(requestPractice.getPart().equalsIgnoreCase("PART3.1") || requestPractice.getPart().equalsIgnoreCase("PART4.1") ){
                List<QuestionAnswerListeningDTO> lstEasy = new ArrayList<>();
                List<CategoryDTO> lstCategoryEasy = categoryRepository.getListCategoryListenFill(requestPractice.getTopicName(), "EASY", 2);
                for(int i=0; i< lstCategoryEasy.size(); i++){
                    QuestionAnswerListeningDTO question = questionAnswerListeningRepository.getListQuestionByCategoryIdListenFill(lstCategoryEasy.get(i).getCategoryId());
                    lstEasy.add(question);
                }
                List<CategoryDTO> lstCategoryMedium = categoryRepository.getListCategoryListenFill(requestPractice.getTopicName(), "MEDIUM", 1);
                for(int i=0; i< lstCategoryEasy.size(); i++){
                    QuestionAnswerListeningDTO question = questionAnswerListeningRepository.getListQuestionByCategoryIdListenFill(lstCategoryMedium.get(i).getCategoryId());
                    lstEasy.add(question);
                }

                return lstEasy;
            } else{
                List<QuestionAnswerListeningDTO> lstEasy =
                        questionAnswerListeningRepository
                                .getListQuestionOfListenAndFill(requestPractice.getTopicName(),"EASY",3);
                List<QuestionAnswerListeningDTO> lstMedium =
                        questionAnswerListeningRepository
                                .getListQuestionOfListenAndFill(requestPractice.getTopicName(),"MEDIUM",1);
                List<QuestionAnswerListeningDTO> lstHard =
                        questionAnswerListeningRepository
                                .getListQuestionOfListenAndFill(requestPractice.getTopicName(),"DIFFICULT",1);
                lstEasy.addAll(lstMedium);
                lstEasy.addAll(lstHard);
                return lstEasy;
            }

        }
        if(requestPractice.getLevelCode().equals("DIFFICULT")){
            if(requestPractice.getPart().equalsIgnoreCase("PART3.1") || requestPractice.getPart().equalsIgnoreCase("PART4.1") ){
                List<QuestionAnswerListeningDTO> lstEasy = new ArrayList<>();
                List<CategoryDTO> lstCategoryEasy = categoryRepository.getListCategoryListenFill(requestPractice.getTopicName(), "EASY", 1);
                for(int i=0; i< lstCategoryEasy.size(); i++){
                    QuestionAnswerListeningDTO question = questionAnswerListeningRepository.getListQuestionByCategoryIdListenFill(lstCategoryEasy.get(i).getCategoryId());
                    lstEasy.add(question);
                }
                List<CategoryDTO> lstCategoryMedium = categoryRepository.getListCategoryListenFill(requestPractice.getTopicName(), "MEDIUM", 1);
                for(int i=0; i< lstCategoryEasy.size(); i++){
                    QuestionAnswerListeningDTO question = questionAnswerListeningRepository.getListQuestionByCategoryIdListenFill(lstCategoryMedium.get(i).getCategoryId());
                    lstEasy.add(question);
                }
                List<CategoryDTO> lstCategoryHard = categoryRepository.getListCategoryListenFill(requestPractice.getTopicName(), "DIFFICULT", 1);
                for(int i=0; i< lstCategoryEasy.size(); i++){
                    QuestionAnswerListeningDTO question = questionAnswerListeningRepository.getListQuestionByCategoryIdListenFill(lstCategoryHard.get(i).getCategoryId());
                    lstEasy.add(question);
                }
                return lstEasy;

            } else {
                List<QuestionAnswerListeningDTO> lstEasy =
                        questionAnswerListeningRepository
                                .getListQuestionOfListenAndFill(requestPractice.getTopicName(),"EASY",2);
                List<QuestionAnswerListeningDTO> lstMedium =
                        questionAnswerListeningRepository
                                .getListQuestionOfListenAndFill(requestPractice.getTopicName(),"MEDIUM",2);
                List<QuestionAnswerListeningDTO> lstHard =
                        questionAnswerListeningRepository
                                .getListQuestionOfListenAndFill(requestPractice.getTopicName(),"DIFFICULT",1);
                lstEasy.addAll(lstMedium);
                lstEasy.addAll(lstHard);
                return lstEasy;
            }

        }
        return null;
    }

    @Override
    public QuestionAnswersDTO getResultQuestionOfListenWordFill(QuestionAnswerListeningDTO obj){
        if (obj.getId() == null) {
            throw new BusinessException(MessageUtils.getMessage("error_miss_info"));
        }
        QuestionAnswersDTO result = questionAnswerListeningRepository.getResultQuestionOfListenWordFill(obj);
        String[] a = result.getAnswer().toLowerCase().split("[\\|]");
        List<String[]> lst = new ArrayList<>();
        int indexStringAnswer = 0;
        long numberCorrect = 0;
        long number=0;
        long size =0;
        List<Integer> lstIndexCorrect = new ArrayList<>();
        List<Integer> lstIndexInCorrect = new ArrayList<>();
        for(int i=0; i< obj.getUserFill().size(); i++) {
//            int x = obj.getUserFill().get(i).length;
            if (obj.getUserFill().get(i) != null) {
                size++;
                String[] row = new String[obj.getUserFill().get(i).length];
                String temp = "";
                for (int j = 0; j < row.length; j++) {
                    if (a[indexStringAnswer].indexOf(" ") != -1 && temp == "") {
                        String[] cutString = a[indexStringAnswer].trim().split(" ");
                        row[j] = cutString[0].trim();
                        temp = cutString[1].trim();
                    } else if (a[indexStringAnswer].indexOf(" ") != -1 && temp != "") {
                        row[j] = temp;
                        temp = "";
                        indexStringAnswer++;
                    } else if (a[indexStringAnswer].indexOf(" ") == -1 && temp == "") {
                        row[j] = a[indexStringAnswer].trim();
                        indexStringAnswer++;
                    }
                }
                lst.add(row);
            } else {
                lst.add(null);
            }
        }

        for(int i=0 ; i< obj.getUserFill().size(); i++){
            if(obj.getUserFill().get(i) != null){
                for(int j=0; j < obj.getUserFill().get(i).length; j++){
                    String temp = obj.getUserFill().get(i)[j].toUpperCase();
                    String tempp = lst.get(i)[j].toUpperCase();
//                    if(temp.indexOf("'") != -1){
//                        temp = temp.replace("'", "_");
//                    }
//                    if(tempp.indexOf("'") != -1){
//                        tempp = tempp.replace("'","_");
//                    }
                    if(tempp.equalsIgnoreCase(temp)) {
                        numberCorrect++;

                    }
                }
                if(numberCorrect == obj.getUserFill().get(i).length){
                    number++;
                    numberCorrect = 0;
                    lstIndexCorrect.add(i);
                } else {
                    numberCorrect = 0;
                    lstIndexInCorrect.add(i);
                }
            }
        }
        long lstSize = size;
        result.setLstSize(lstSize);
        result.setNumberCorrect(number);
        result.setLstIndexCorrect(lstIndexCorrect);
        result.setLstIndexInCorrect(lstIndexInCorrect);
        result.setLstAnswerCut(lst);
        return result;
    }

    
    public List<RequestPractice> getPracticeConversation(RequestPractice rp) {

        List<RequestPractice> listRequestPractices2 = new ArrayList<>();
        int amountOfCategory;

        if(rp.getLevelCode().equals("EASY")){
            amountOfCategory = 3;
            List<RequestPractice> listRequestPractices =
                    questionAnswerListeningRepository.getRandomListenCateWithLimitLenghtQuest(
                            rp.getTopicId(), rp.getPart(), rp.getLevelCode(),  amountOfCategory);

            for (RequestPractice requestPractice : listRequestPractices) {
                List<QuestionAnswersDTO> listQuestionAnswersDTOS =
                        questionAnswerListeningRepository.getListQuestionByCategoryId(requestPractice.getCategoryId());

                RequestPractice requestPractice2 = new RequestPractice();
                requestPractice2.setCategoryId(requestPractice.getCategoryId());
                requestPractice2.setPathFile1(requestPractice.getPathFile1());
                requestPractice2.setPathFile2(requestPractice.getPathFile2());
                requestPractice2.setTranscript(requestPractice.getTranscript());
                requestPractice2.setLevelCode(requestPractice.getLevelCode());
                requestPractice2.setTopicName(requestPractice.getTopicName());
                requestPractice2.setTopicId(requestPractice.getTopicId());
                requestPractice2.setListQuestion(listQuestionAnswersDTOS);
                requestPractice2.setCurrentLevelCode(rp.getLevelCode());
                requestPractice2.setTranslatingQuestion(rp.getTranslatingQuestion());
                requestPractice2.setTranslatingQuesA(rp.getTranslatingQuesA());
                requestPractice2.setTranslatingQuesB(rp.getTranslatingQuesB());
                requestPractice2.setTranslatingQuesC(rp.getTranslatingQuesC());
                requestPractice2.setTranslatingQuesD(rp.getTranslatingQuesD());

                requestPractice2.setPathFileQues(rp.getPathFileQues());
                requestPractice2.setPathFileQuesA(rp.getPathFileQuesA());
                requestPractice2.setPathFileQuesB(rp.getPathFileQuesB());
                requestPractice2.setPathFileQuesC(rp.getPathFileQuesC());
                requestPractice2.setPathFileQuesD(rp.getPathFileQuesD());


                listRequestPractices2.add(requestPractice2);
            }
            return listRequestPractices2;
        }
        if(rp.getLevelCode().equals("MEDIUM")){
            amountOfCategory = 2;
            String newLevelCode = "EASY";
            List<RequestPractice> listRequestPractices =
                    questionAnswerListeningRepository.getRandomListenCateWithLimitLenghtQuest(
                            rp.getTopicId(), rp.getPart(), newLevelCode, amountOfCategory);

            for (RequestPractice requestPractice : listRequestPractices) {
                List<QuestionAnswersDTO> listQuestionAnswersDTOS =
                        questionAnswerListeningRepository.getListQuestionByCategoryId(requestPractice.getCategoryId());

                RequestPractice requestPractice2 = new RequestPractice();
                requestPractice2.setCategoryId(requestPractice.getCategoryId());
                requestPractice2.setPathFile1(requestPractice.getPathFile1());
                requestPractice2.setPathFile2(requestPractice.getPathFile2());
                requestPractice2.setTranscript(requestPractice.getTranscript());
                requestPractice2.setLevelCode(requestPractice.getLevelCode());
                requestPractice2.setTopicName(requestPractice.getTopicName());
                requestPractice2.setTopicId(requestPractice.getTopicId());
                requestPractice2.setListQuestion(listQuestionAnswersDTOS);
                requestPractice2.setCurrentLevelCode(rp.getLevelCode());

                requestPractice2.setTranslatingQuestion(rp.getTranslatingQuestion());
                requestPractice2.setTranslatingQuesA(rp.getTranslatingQuesA());
                requestPractice2.setTranslatingQuesB(rp.getTranslatingQuesB());
                requestPractice2.setTranslatingQuesC(rp.getTranslatingQuesC());
                requestPractice2.setTranslatingQuesD(rp.getTranslatingQuesD());

                requestPractice2.setPathFileQues(rp.getPathFileQues());
                requestPractice2.setPathFileQuesA(rp.getPathFileQuesA());
                requestPractice2.setPathFileQuesB(rp.getPathFileQuesB());
                requestPractice2.setPathFileQuesC(rp.getPathFileQuesC());
                requestPractice2.setPathFileQuesD(rp.getPathFileQuesD());

                listRequestPractices2.add(requestPractice2);
            }

            amountOfCategory = 1;
            newLevelCode = "MEDIUM";
            List<RequestPractice> listRequestPracticesNew =
                    questionAnswerListeningRepository.getRandomListenCateWithLimitLenghtQuest(
                            rp.getTopicId(), rp.getPart(), newLevelCode, amountOfCategory);

            for (RequestPractice requestPractice : listRequestPracticesNew) {
                List<QuestionAnswersDTO> listQuestionAnswersDTOS =
                        questionAnswerListeningRepository.getListQuestionByCategoryId(requestPractice.getCategoryId());

                RequestPractice requestPractice2 = new RequestPractice();
                requestPractice2.setCategoryId(requestPractice.getCategoryId());
                requestPractice2.setPathFile1(requestPractice.getPathFile1());
                requestPractice2.setPathFile2(requestPractice.getPathFile2());
                requestPractice2.setTranscript(requestPractice.getTranscript());
                requestPractice2.setLevelCode(requestPractice.getLevelCode());
                requestPractice2.setTopicName(requestPractice.getTopicName());
                requestPractice2.setTopicId(requestPractice.getTopicId());
                requestPractice2.setListQuestion(listQuestionAnswersDTOS);
                requestPractice2.setCurrentLevelCode(rp.getLevelCode());

                requestPractice2.setTranslatingQuestion(rp.getTranslatingQuestion());
                requestPractice2.setTranslatingQuesA(rp.getTranslatingQuesA());
                requestPractice2.setTranslatingQuesB(rp.getTranslatingQuesB());
                requestPractice2.setTranslatingQuesC(rp.getTranslatingQuesC());
                requestPractice2.setTranslatingQuesD(rp.getTranslatingQuesD());

                requestPractice2.setPathFileQues(rp.getPathFileQues());
                requestPractice2.setPathFileQuesA(rp.getPathFileQuesA());
                requestPractice2.setPathFileQuesB(rp.getPathFileQuesB());
                requestPractice2.setPathFileQuesC(rp.getPathFileQuesC());
                requestPractice2.setPathFileQuesD(rp.getPathFileQuesD());

                listRequestPractices2.add(requestPractice2);
            }


            return listRequestPractices2;

        }
        if(rp.getLevelCode().equals("DIFFICULT")){
            amountOfCategory = 1;
            String newLevelCode = "EASY";
            List<RequestPractice> listRequestPractices =
                    questionAnswerListeningRepository.getRandomListenCateWithLimitLenghtQuest(
                            rp.getTopicId(), rp.getPart(), newLevelCode, amountOfCategory);

            for (RequestPractice requestPractice : listRequestPractices) {
                List<QuestionAnswersDTO> listQuestionAnswersDTOS =
                        questionAnswerListeningRepository.getListQuestionByCategoryId(requestPractice.getCategoryId());

                RequestPractice requestPractice2 = new RequestPractice();
                requestPractice2.setCategoryId(requestPractice.getCategoryId());
                requestPractice2.setPathFile1(requestPractice.getPathFile1());
                requestPractice2.setPathFile2(requestPractice.getPathFile2());
                requestPractice2.setTranscript(requestPractice.getTranscript());
                requestPractice2.setLevelCode(requestPractice.getLevelCode());
                requestPractice2.setTopicName(requestPractice.getTopicName());
                requestPractice2.setTopicId(requestPractice.getTopicId());
                requestPractice2.setListQuestion(listQuestionAnswersDTOS);
                requestPractice2.setCurrentLevelCode(rp.getLevelCode());

                requestPractice2.setTranslatingQuestion(rp.getTranslatingQuestion());
                requestPractice2.setTranslatingQuesA(rp.getTranslatingQuesA());
                requestPractice2.setTranslatingQuesB(rp.getTranslatingQuesB());
                requestPractice2.setTranslatingQuesC(rp.getTranslatingQuesC());
                requestPractice2.setTranslatingQuesD(rp.getTranslatingQuesD());

                requestPractice2.setPathFileQues(rp.getPathFileQues());
                requestPractice2.setPathFileQuesA(rp.getPathFileQuesA());
                requestPractice2.setPathFileQuesB(rp.getPathFileQuesB());
                requestPractice2.setPathFileQuesC(rp.getPathFileQuesC());
                requestPractice2.setPathFileQuesD(rp.getPathFileQuesD());

                listRequestPractices2.add(requestPractice2);
            }

            amountOfCategory = 1;
            newLevelCode = "MEDIUM";
            List<RequestPractice> listRequestPracticesNew =
                    questionAnswerListeningRepository.getRandomListenCateWithLimitLenghtQuest(
                            rp.getTopicId(), rp.getPart(), newLevelCode, amountOfCategory);

            for (RequestPractice requestPractice : listRequestPracticesNew) {
                List<QuestionAnswersDTO> listQuestionAnswersDTOS =
                        questionAnswerListeningRepository.getListQuestionByCategoryId(requestPractice.getCategoryId());

                RequestPractice requestPractice2 = new RequestPractice();
                requestPractice2.setCategoryId(requestPractice.getCategoryId());
                requestPractice2.setPathFile1(requestPractice.getPathFile1());
                requestPractice2.setPathFile2(requestPractice.getPathFile2());
                requestPractice2.setTranscript(requestPractice.getTranscript());
                requestPractice2.setLevelCode(requestPractice.getLevelCode());
                requestPractice2.setTopicName(requestPractice.getTopicName());
                requestPractice2.setTopicId(requestPractice.getTopicId());
                requestPractice2.setListQuestion(listQuestionAnswersDTOS);
                requestPractice2.setCurrentLevelCode(rp.getLevelCode());

                requestPractice2.setTranslatingQuestion(rp.getTranslatingQuestion());
                requestPractice2.setTranslatingQuesA(rp.getTranslatingQuesA());
                requestPractice2.setTranslatingQuesB(rp.getTranslatingQuesB());
                requestPractice2.setTranslatingQuesC(rp.getTranslatingQuesC());
                requestPractice2.setTranslatingQuesD(rp.getTranslatingQuesD());

                requestPractice2.setPathFileQues(rp.getPathFileQues());
                requestPractice2.setPathFileQuesA(rp.getPathFileQuesA());
                requestPractice2.setPathFileQuesB(rp.getPathFileQuesB());
                requestPractice2.setPathFileQuesC(rp.getPathFileQuesC());
                requestPractice2.setPathFileQuesD(rp.getPathFileQuesD());

                listRequestPractices2.add(requestPractice2);
            }

            amountOfCategory = 1;
            newLevelCode = "DIFFICULT";
            List<RequestPractice> listRequestPracticesNew2 =
                    questionAnswerListeningRepository.getRandomListenCateWithLimitLenghtQuest(
                            rp.getTopicId(), rp.getPart(), newLevelCode, amountOfCategory);

            for (RequestPractice requestPractice : listRequestPracticesNew2) {
                List<QuestionAnswersDTO> listQuestionAnswersDTOS =
                        questionAnswerListeningRepository.getListQuestionByCategoryId(requestPractice.getCategoryId());

                RequestPractice requestPractice2 = new RequestPractice();
                requestPractice2.setCategoryId(requestPractice.getCategoryId());
                requestPractice2.setPathFile1(requestPractice.getPathFile1());
                requestPractice2.setPathFile2(requestPractice.getPathFile2());
                requestPractice2.setTranscript(requestPractice.getTranscript());
                requestPractice2.setLevelCode(requestPractice.getLevelCode());
                requestPractice2.setTopicName(requestPractice.getTopicName());
                requestPractice2.setTopicId(requestPractice.getTopicId());
                requestPractice2.setListQuestion(listQuestionAnswersDTOS);
                requestPractice2.setCurrentLevelCode(rp.getLevelCode());

                requestPractice2.setTranslatingQuestion(rp.getTranslatingQuestion());
                requestPractice2.setTranslatingQuesA(rp.getTranslatingQuesA());
                requestPractice2.setTranslatingQuesB(rp.getTranslatingQuesB());
                requestPractice2.setTranslatingQuesC(rp.getTranslatingQuesC());
                requestPractice2.setTranslatingQuesD(rp.getTranslatingQuesD());

                requestPractice2.setPathFileQues(rp.getPathFileQues());
                requestPractice2.setPathFileQuesA(rp.getPathFileQuesA());
                requestPractice2.setPathFileQuesB(rp.getPathFileQuesB());
                requestPractice2.setPathFileQuesC(rp.getPathFileQuesC());
                requestPractice2.setPathFileQuesD(rp.getPathFileQuesD());

                listRequestPractices2.add(requestPractice2);
            }


            return listRequestPractices2;

        }
        return null;

    }

    @Override
    public String createHistoryListenFill(HistoryPracticesDTO historyPracticesDTO){
        try{
//            LocalDateTime localDateTime = LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh"));
//            Date createDate = Date.from(localDateTime.atZone(ZoneId.of("Asia/Ho_Chi_Minh")).toInstant());
            historyPracticesDTO.setCreateDate(new Date());
            Long idHistory = historyPracticesRepository.insert(historyPracticesDTO.toModel());
            try{
                createDetailHistoryLF(historyPracticesDTO.getLstQuestionAnswer(), idHistory, historyPracticesDTO.getUserFill());
            } catch (Exception e){
                throw new BusinessException("create_detail_history_fail");
            }
            String message ="";
            if(historyPracticesDTO.getPart().equalsIgnoreCase("PART2.1")){
               message = "create_history_questions_responses_success";
            } else if(historyPracticesDTO.getPart().equalsIgnoreCase("PART1.1")){
                message ="create_history_photographs_LF_success";
            } else if(historyPracticesDTO.getPart().equalsIgnoreCase("PART3.1")){
                message = "create_history_short_conversation_success";
            } else if(historyPracticesDTO.getPart().equalsIgnoreCase("PART4.1")){
                message = "create_history_talk_success";
            }

            return MessageUtils.getMessage(message);
        } catch (Exception e){
            throw new BusinessException("create_detail_history_fail");
        }
    }

    public void createDetailHistoryLF(List<QuestionAnswersDTO> listQuestionAnswer, Long idHistory,List<UserFillDTO> userFillDTO){
        for(int i = 0; i<listQuestionAnswer.size(); i++){
            DetailHistoryListenFillDTO obj = new DetailHistoryListenFillDTO();
            if(listQuestionAnswer.get(i).getLstIndexCorrect().size() != 0){
                String indexCorrect = "";
                for(Integer index : listQuestionAnswer.get(i).getLstIndexCorrect()){
                    indexCorrect += index + "|";
                }
                obj.setIndexCorrect(indexCorrect);
            } else {
                obj.setIndexCorrect("");
            }
            if(listQuestionAnswer.get(i).getLstIndexInCorrect().size() != 0){
                String indexInCorrect = "";
                for(Integer index : listQuestionAnswer.get(i).getLstIndexInCorrect()){
                    indexInCorrect += index + "|";
                }
                obj.setIndexInCorrect(indexInCorrect);
            } else {
                obj.setIndexInCorrect("");
            }
            if(userFillDTO.get(i).getFill() != null){
                String fill = "";
                for(String[] s : userFillDTO.get(i).getFill()){
                    if(s != null){
                        String convert = String.join("|", s);
                        fill += convert + "|";
                    }
                }
                obj.setUserFill(fill);
            }
            obj.setParentId(idHistory);
            obj.setQuestionId(listQuestionAnswer.get(i).getId());
            obj.setCategoryId(listQuestionAnswer.get(i).getParentId());
            Long idDetail = detailHistoryListenFillRepository.insert(obj.toModel());
        }
    }

	@Override
	public Long insertData(QuestionAnswerListeningDTO listeningDTO) {
		return questionAnswerListeningRepository.insert(listeningDTO.toModel());
	}
}

