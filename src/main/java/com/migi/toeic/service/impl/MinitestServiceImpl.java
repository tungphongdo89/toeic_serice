package com.migi.toeic.service.impl;

import com.migi.toeic.base.DataListDTO;
import com.migi.toeic.dto.*;
import com.migi.toeic.exception.BusinessException;
import com.migi.toeic.model.HistoryMinitest;
import com.migi.toeic.model.Object;
import com.migi.toeic.model.QuestionAnswerListening;
import com.migi.toeic.respositories.*;
import com.migi.toeic.service.MinitestService;
import com.migi.toeic.utils.DateUtil;
import com.migi.toeic.utils.MessageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Service
public class MinitestServiceImpl implements MinitestService {

    private static final String PART1 = "PART1";
    private static final String PART2 = "PART2";
    private static final String PART3 = "PART3";
    private static final String PART4 = "PART4";
    private static final String PART5 = "PART5";
    private static final String PART6 = "PART6";
    private static final String PART7 = "PART7";
    private static final String PART8 = "PART8";


    @Autowired
    QuestionAnswerListeningRepository questionAnswerListeningRepository;

    @Autowired
    QuestionAnswerReadingRepository questionAnswerReadingRepository;

    @Autowired
    TopicRepository topicRepository;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    DetailHistoryMinitestRepository detailHistoryMinitestRepository;

    @Autowired
    HistoryMinitestRepository historyMinitestRepository;

    // lay cac cau hoi minitest theo part1, part2, part5
    public MinitestDTO getQuestionMinitestByPart(String partTopicCode, long typeCode, int amountOfQuestion, long sttQuestion){
        MinitestDTO minitestDTO = new MinitestDTO();

        List<CategoryMinitestDTO> lisCategoryMinitestDTOs = new ArrayList<>();

        List<QuestionMinitestDTO> listQuestionMinitestDTOs = new ArrayList<>();

        List<TopicDTO>  listTopicDTOs = new ArrayList<>();

        while(listQuestionMinitestDTOs.size() < amountOfQuestion){
            TopicDTO topic = topicRepository.getRandomTopicByPartTopicCode(partTopicCode);
            if(!listTopicDTOs.contains(topic)){
                listTopicDTOs.add(topic);
                List<CategoryMinitestDTO> listCategories = categoryRepository.getListCategoriesByParentId(topic.getTopicId(), typeCode);
                Collections.shuffle(listCategories);

                for (CategoryMinitestDTO categoryDTO: listCategories) {
                    List<QuestionMinitestDTO> listQuestionAnswerListeningDTOS2 ;
                    if(typeCode == 2){
                        listQuestionAnswerListeningDTOS2 = questionAnswerListeningRepository.getListQuestionByCategoryIdForMinitest(categoryDTO.getCategoryId());
                    }
                    else{
                        listQuestionAnswerListeningDTOS2 = questionAnswerReadingRepository.getListQuestionByCategoryIdForMinitest(categoryDTO.getCategoryId());
                    }

                    for (QuestionMinitestDTO questionMinitestDTO:listQuestionAnswerListeningDTOS2) {
                        if(listQuestionMinitestDTOs.size() < amountOfQuestion){
                            sttQuestion = sttQuestion + 1;
                            questionMinitestDTO.setStt(sttQuestion);
                            listQuestionMinitestDTOs.add(questionMinitestDTO);
                        }
                    }
                }
            }
        }

        CategoryMinitestDTO categoryMinitestDTO = new CategoryMinitestDTO();
        categoryMinitestDTO.setListQuestionMinitestDTOS(listQuestionMinitestDTOs);
        lisCategoryMinitestDTOs.add(categoryMinitestDTO);


        minitestDTO.setPartName(partTopicCode);
        minitestDTO.setListCategoryMinitestDTOS(lisCategoryMinitestDTOs);

        return minitestDTO;
    }
    // end lay cac cau hoi minitest theo part1, part2, part5


    /* lay cac cau hoi minitest theo part3, part4, part6, part7, part8 */
    public MinitestDTO getQuestionMinitestByPartOther(String partTopicCode, long typeCode, int amountOfQuestion, long sttQuestion){
        MinitestDTO  minitestDTO = new MinitestDTO();
        int totalQuestionOfPart = 0;

        List<CategoryMinitestDTO> lisCategoryMinitestDTOs = new ArrayList<>();
        while (lisCategoryMinitestDTOs.size() < amountOfQuestion){
            TopicDTO topic = topicRepository.getRandomTopicByPartTopicCode(partTopicCode);
            List<CategoryMinitestDTO> lisCategoryMinitestDTOs2 = categoryRepository.getListCategoriesByParentId2(topic.getTopicId(), typeCode);
            for(CategoryMinitestDTO category: lisCategoryMinitestDTOs2){
                if(!lisCategoryMinitestDTOs.contains(category)){
                    lisCategoryMinitestDTOs.add(category);
                }
            }
        }

        Collections.shuffle(lisCategoryMinitestDTOs);
        lisCategoryMinitestDTOs.subList(amountOfQuestion - 1, lisCategoryMinitestDTOs.size()-1).clear();

        for (CategoryMinitestDTO categoryMinitestDTO : lisCategoryMinitestDTOs) {
            List<QuestionMinitestDTO> listQuestionMinitestDTO;
            if(typeCode == 2){
                listQuestionMinitestDTO =
                        questionAnswerListeningRepository.getListQuestionByCategoryIdForMinitest(categoryMinitestDTO.getCategoryId());
            }
            else {
                listQuestionMinitestDTO =
                        questionAnswerReadingRepository.getListQuestionByCategoryIdForMinitest(categoryMinitestDTO.getCategoryId());
            }
            for (QuestionMinitestDTO questionMinitestDTO: listQuestionMinitestDTO) {
                sttQuestion = sttQuestion + 1;
                totalQuestionOfPart = totalQuestionOfPart + 1;
                questionMinitestDTO.setStt(sttQuestion);
            }
            categoryMinitestDTO.setListQuestionMinitestDTOS(listQuestionMinitestDTO);
        }

        minitestDTO.setPartName(partTopicCode);
        minitestDTO.setTotalQuestion(totalQuestionOfPart);
        minitestDTO.setListCategoryMinitestDTOS(lisCategoryMinitestDTOs);

        return minitestDTO;
    }
    /* end lay cac cau hoi minitest theo part3, part4, part6, part7, part8 */


    // lay list cau hoi minitest
    @Override
    public List<MinitestDTO> getListQuestionMinitest() {
        List<MinitestDTO> listMinitestDTOS = new ArrayList<>();
        long sttQuestion = 0;
        //part1
        MinitestDTO minitestDTO = getQuestionMinitestByPart(PART1, 2, 6, sttQuestion);
        listMinitestDTOS.add(minitestDTO);
        sttQuestion += 6;

        //part2
        minitestDTO = getQuestionMinitestByPart(PART2, 2, 10, sttQuestion);
        listMinitestDTOS.add(minitestDTO);
        sttQuestion += 10;

        //part3
        minitestDTO = getQuestionMinitestByPartOther(PART3, 2, 5, sttQuestion);
        listMinitestDTOS.add(minitestDTO);
        sttQuestion = sttQuestion + minitestDTO.getTotalQuestion();

        //part4
        minitestDTO = getQuestionMinitestByPartOther(PART4, 2, 5, sttQuestion);
        listMinitestDTOS.add(minitestDTO);
        sttQuestion = sttQuestion + minitestDTO.getTotalQuestion();

        //part5
        minitestDTO = getQuestionMinitestByPart(PART5, 1, 10, sttQuestion);
        listMinitestDTOS.add(minitestDTO);
        sttQuestion += 10;

        //part6
        minitestDTO = getQuestionMinitestByPartOther(PART6, 1, 3, sttQuestion);
        listMinitestDTOS.add(minitestDTO);
        sttQuestion = sttQuestion + minitestDTO.getTotalQuestion();

        //part7
        minitestDTO = getQuestionMinitestByPartOther(PART7, 1, 4, sttQuestion);
        listMinitestDTOS.add(minitestDTO);
        sttQuestion = sttQuestion + minitestDTO.getTotalQuestion();

        //part8
        minitestDTO = getQuestionMinitestByPartOther(PART8, 1, 3, sttQuestion);
        listMinitestDTOS.add(minitestDTO);

        return listMinitestDTOS;
    }

    public String cutString(String s){
        s = s.substring(0, s.length()-1);
        return s.trim();
    }

    public String findABCDofAnswerChoosen(String answerToChoose, String answerChoosen){
        String[] arrAnswer= answerToChoose.split("\\|");
        String answerABCD = "";
        int indexCorrect = 0;

        if(answerChoosen.trim().equals("A") || answerChoosen.trim().equals("B") ||
                answerChoosen.trim().equals("C") || answerChoosen.trim().equals("D")){
            answerABCD = answerChoosen;
        }
        else {
            for(int i=0;i<arrAnswer.length;i++){
                if(answerChoosen.trim().equals(arrAnswer[i].trim())){
                    indexCorrect = i;
                }
            }
            if(indexCorrect==0){
                answerABCD = "A";
            }
            if(indexCorrect==1){
                answerABCD = "B";
            }
            if(indexCorrect==2){
                answerABCD = "C";
            }
            if(indexCorrect==3){
                answerABCD = "D";
            }

        }
        return answerABCD;
    }

    public long compareAnswer(String answerToChoose, String answer){
        long index = 0;
        String[] arrAnswer=answerToChoose.split("\\|");
        for(int i=0;i<arrAnswer.length;i++){
            if((arrAnswer[i].trim()).equals(this.cutString(answer).trim())){
                index = i;
            }
        }
        return index;

    }



    public long compareAnswer2(String answerToChoose, String answer){

        String correctAnswer = this.findABCDofAnswerChoosen(answerToChoose, answer);
        long index = 0;

        if(correctAnswer.equals("A")){
            index = 0;
        }
        if(correctAnswer.equals("B")){
            index = 1;
        }
        if(correctAnswer.equals("C")){
            index = 2;
        }
        if(correctAnswer.equals("D")){
            index = 3;
        }
        return index;

    }


    /*---------------------------------------Submit Answer------------------------------------------*/


    /*lưu từng câu hỏi vào bảng detail history minitest*/
    public void saveQuesiontoDetailHistoryMinitest(QuestionMinitestDTO questionMinitestDTO, String partName, long check){
        if(check == 1){

        }
        else {
            DetailHistoryMinitestDTO detailHistoryMinitestDTO = new DetailHistoryMinitestDTO();

//            HistoryMinitestDTO historyMinitestDTO = historyMinitestRepository.getMaxIdOfHistoryMinitest();
            HistoryMinitestDTO historyMinitestDTO = historyMinitestRepository.getMaxTestIdOfHistoryMinitest();
            long testId = 1;
            if(null == historyMinitestDTO){
                testId = 1;
            }
            else {
                testId = testId + historyMinitestDTO.getTestId();
            }

            detailHistoryMinitestDTO.setParentId(testId);
            detailHistoryMinitestDTO.setPartName(partName);
            detailHistoryMinitestDTO.setIndexCorrectAnswer(questionMinitestDTO.getIndexCorrectAnswer());
            detailHistoryMinitestDTO.setIndexIncorrectAnswer(questionMinitestDTO.getIndexIncorrectAnswer());
            detailHistoryMinitestDTO.setQuestionId(questionMinitestDTO.getId());
            detailHistoryMinitestDTO.setCategoryId(questionMinitestDTO.getParentId());
            detailHistoryMinitestDTO.setStt(questionMinitestDTO.getStt());
            detailHistoryMinitestRepository.insert(detailHistoryMinitestDTO.toModel());
        }

    }
    /*End lưu từng câu hỏi vào bảng detail history minitest*/

    /* lay cac cau tra loi minitest theo part1, part2, part5 */
    public MinitestDTO getQuestionAndAnswerMinitestByPart(List<QuestionListeningChoosenDTO> listQuestionListeningChoosenDTOS,
                                                          List<QuestionReadingChoosenDTO> listQuestionReadingChoosenDTOS,
                                                          String partName, long check){

        MinitestDTO minitestDTO = new MinitestDTO();
        int totalQuestion = 0;
        int totalCorectAnswer = 0;

        List<CategoryMinitestDTO> lisCategoryMinitestDTOs = new ArrayList<>();

        CategoryMinitestDTO categoryMinitestDTO = new CategoryMinitestDTO();

        List<QuestionMinitestDTO> listQuestionMinitestDTOs = new ArrayList<>();

        if(partName.equals(PART1) || partName.equals(PART2)){
            for (QuestionListeningChoosenDTO questionListeningChoosenDTO : listQuestionListeningChoosenDTOS) {

                if(questionListeningChoosenDTO.getPartName().equalsIgnoreCase(partName)){

                    List<QuestionMinitestDTO> questionMinitestDTO =
                            questionAnswerListeningRepository.getListAnswerListeningById(questionListeningChoosenDTO.getId());
                    if(this.findABCDofAnswerChoosen(questionMinitestDTO.get(0).getAnswersToChoose().trim(),(questionListeningChoosenDTO.getAnswerChoosen().trim()))
                            .equals(questionMinitestDTO.get(0).getAnswer()) && null != questionListeningChoosenDTO.getIndexSubAnswer() ){
                        //Trường hợp đúng
                        questionMinitestDTO.get(0).setIndexCorrectAnswer(questionListeningChoosenDTO.getIndexSubAnswer()); // set vị trí câu đúng
                        questionMinitestDTO.get(0).setIndexIncorrectAnswer((long) -1); // set vị trí câu sai
                        questionMinitestDTO.get(0).setStt(questionListeningChoosenDTO.getStt());
                        totalQuestion++;
                        totalCorectAnswer++;

                    }
                    else {
                        //Trường hợp sai
                        long indexCorrectAnswer = this.compareAnswer2(questionMinitestDTO.get(0).getAnswersToChoose().trim(), // tìm vị trí câu đúng
                                questionMinitestDTO.get(0).getAnswer().trim());
                        questionMinitestDTO.get(0).setIndexCorrectAnswer(indexCorrectAnswer); // set vị trí câu đúng
                        questionMinitestDTO.get(0).setIndexIncorrectAnswer(questionListeningChoosenDTO.getIndexSubAnswer()); // set vị trí câu sai
                        questionMinitestDTO.get(0).setStt(questionListeningChoosenDTO.getStt());
                        totalQuestion++;
                    }


                    listQuestionMinitestDTOs.add(questionMinitestDTO.get(0));

                    //Gọi hàm lưu quesion vào bảng detail history minitest
                    saveQuesiontoDetailHistoryMinitest(questionMinitestDTO.get(0), partName, check);

                    categoryMinitestDTO.setListQuestionMinitestDTOS(listQuestionMinitestDTOs);

                }
            }
        }

        if(partName.equals(PART5)){
            for (QuestionReadingChoosenDTO questionReadingChoosenDTO : listQuestionReadingChoosenDTOS) {

                if(questionReadingChoosenDTO.getPartName().equalsIgnoreCase(partName)){

                    QuestionMinitestDTO questionMinitestDTO =
                            questionAnswerReadingRepository.getAnswerReadingById(questionReadingChoosenDTO.getId());
                    if(this.findABCDofAnswerChoosen(questionMinitestDTO.getAnswersToChoose().trim(),(questionReadingChoosenDTO.getAnswerChoosen().trim()))
                            .equals(questionMinitestDTO.getAnswer()) && null != questionReadingChoosenDTO.getIndexSubAnswer()){
                        //Trường hợp đúng
                        questionMinitestDTO.setIndexCorrectAnswer(questionReadingChoosenDTO.getIndexSubAnswer()); // set vị trí câu đúng
                        questionMinitestDTO.setIndexIncorrectAnswer((long) -1); // set vị trí câu sai
                        questionMinitestDTO.setStt(questionReadingChoosenDTO.getStt());
                        totalQuestion++;
                        totalCorectAnswer++;
                    }
                    else {
                        //Trường hợp sai
                        long indexCorrectAnswer = this.compareAnswer2(questionMinitestDTO.getAnswersToChoose().trim(), // tìm vị trí câu đúng
                                questionMinitestDTO.getAnswer().trim());
                        questionMinitestDTO.setIndexCorrectAnswer(indexCorrectAnswer); // set vị trí câu đúng
                        questionMinitestDTO.setIndexIncorrectAnswer(questionReadingChoosenDTO.getIndexSubAnswer()); // set vị trí câu sai
                        questionMinitestDTO.setStt(questionReadingChoosenDTO.getStt());
                        totalQuestion++;
                    }
                    listQuestionMinitestDTOs.add(questionMinitestDTO);

                    //Gọi hàm lưu quesion vào bảng detail history minitest
                    saveQuesiontoDetailHistoryMinitest(questionMinitestDTO, partName, check);

                    categoryMinitestDTO.setListQuestionMinitestDTOS(listQuestionMinitestDTOs);

                }
            }
        }

        lisCategoryMinitestDTOs.add(categoryMinitestDTO);

        minitestDTO.setPartName(partName);
        minitestDTO.setTotalQuestion(totalQuestion);
        minitestDTO.setTotalCorectAnswer(totalCorectAnswer);
        minitestDTO.setListCategoryMinitestDTOS(lisCategoryMinitestDTOs);

        return minitestDTO;
    }
    /* end lay cac cau tra loi minitest theo part1, part2, part5 */


    /* lay cac cau tra loi minitest theo part3, part4, part6, part7, part8 */
    public MinitestDTO getQuestionAndAnswerMinitestByPartOther(List<QuestionListeningChoosenDTO> listQuestionListeningChoosenDTOS,
                                                               List<QuestionReadingChoosenDTO> listQuestionReadingChoosenDTOS,
                                                               String partName, long check){
        //part3, part4
        if(partName.equals(PART3) || partName.equals(PART4)){
            //--xử lý chia question theo categoryId
            List<QuestionListeningChoosenDTO> listQuestionListeningChoosenDTOSChildRoot = new ArrayList<>();

            List<QuestionListeningChoosenDTO> listQuestionListeningChoosenDTOSChild1 = new ArrayList<>();
            List<QuestionListeningChoosenDTO> listQuestionListeningChoosenDTOSChild2 = new ArrayList<>();
            List<QuestionListeningChoosenDTO> listQuestionListeningChoosenDTOSChild3 = new ArrayList<>();
            List<QuestionListeningChoosenDTO> listQuestionListeningChoosenDTOSChild4 = new ArrayList<>();
            List<QuestionListeningChoosenDTO> listQuestionListeningChoosenDTOSChild5 = new ArrayList<>();

            for (QuestionListeningChoosenDTO questionListeningChoosenDTO :listQuestionListeningChoosenDTOS) {
                if(questionListeningChoosenDTO.getPartName().equalsIgnoreCase(partName)){
                    listQuestionListeningChoosenDTOSChildRoot.add(questionListeningChoosenDTO);
                }
            }

            if(listQuestionListeningChoosenDTOSChildRoot.size() > 0){
                for (QuestionListeningChoosenDTO questionListeningChoosenDTO :listQuestionListeningChoosenDTOSChildRoot) {
                    if(questionListeningChoosenDTO.getCategoryId().equals(listQuestionListeningChoosenDTOSChildRoot.get(0).getCategoryId())){
                        listQuestionListeningChoosenDTOSChild1.add(questionListeningChoosenDTO);
                    }
                }
                listQuestionListeningChoosenDTOSChildRoot.subList(0, listQuestionListeningChoosenDTOSChild1.size()-1).clear();
                listQuestionListeningChoosenDTOSChildRoot.remove(0);
            }

            if(listQuestionListeningChoosenDTOSChildRoot.size() > 0){
                for (QuestionListeningChoosenDTO questionListeningChoosenDTO :listQuestionListeningChoosenDTOSChildRoot) {
                    if(questionListeningChoosenDTO.getCategoryId().equals(listQuestionListeningChoosenDTOSChildRoot.get(0).getCategoryId())){
                        listQuestionListeningChoosenDTOSChild2.add(questionListeningChoosenDTO);
                    }
                }
                listQuestionListeningChoosenDTOSChildRoot.subList(0, listQuestionListeningChoosenDTOSChild2.size()-1).clear();
                listQuestionListeningChoosenDTOSChildRoot.remove(0);
            }

            if(listQuestionListeningChoosenDTOSChildRoot.size() > 0){
                for (QuestionListeningChoosenDTO questionListeningChoosenDTO :listQuestionListeningChoosenDTOSChildRoot) {
                    if(questionListeningChoosenDTO.getCategoryId().equals(listQuestionListeningChoosenDTOSChildRoot.get(0).getCategoryId())){
                        listQuestionListeningChoosenDTOSChild3.add(questionListeningChoosenDTO);
                    }
                }
                listQuestionListeningChoosenDTOSChildRoot.subList(0, listQuestionListeningChoosenDTOSChild3.size()-1).clear();
                listQuestionListeningChoosenDTOSChildRoot.remove(0);
            }

            if(listQuestionListeningChoosenDTOSChildRoot.size() > 0){
                for (QuestionListeningChoosenDTO questionListeningChoosenDTO :listQuestionListeningChoosenDTOSChildRoot) {
                    if(questionListeningChoosenDTO.getCategoryId().equals(listQuestionListeningChoosenDTOSChildRoot.get(0).getCategoryId())){
                        listQuestionListeningChoosenDTOSChild4.add(questionListeningChoosenDTO);
                    }
                }
                listQuestionListeningChoosenDTOSChildRoot.subList(0, listQuestionListeningChoosenDTOSChild4.size()-1).clear();
                listQuestionListeningChoosenDTOSChildRoot.remove(0);
            }

            if(listQuestionListeningChoosenDTOSChildRoot.size() > 0){
                for (QuestionListeningChoosenDTO questionListeningChoosenDTO :listQuestionListeningChoosenDTOSChildRoot) {
                    if(questionListeningChoosenDTO.getCategoryId().equals(listQuestionListeningChoosenDTOSChildRoot.get(0).getCategoryId())){
                        listQuestionListeningChoosenDTOSChild5.add(questionListeningChoosenDTO);
                    }
                }
            }

            //add vào list trong api
            MinitestDTO minitestDTO = new MinitestDTO();
            int totalQuestion = 0;
            int totalCorectAnswer = 0;
            List<CategoryMinitestDTO> lisCategoryMinitestDTOs = new ArrayList<>();
            CategoryMinitestDTO categoryMinitestDTOChild1 = new CategoryMinitestDTO();

            //for đoạn 1
            List<QuestionMinitestDTO> listQuestionMinitestDTOsChild1 = new ArrayList<>();
            for (QuestionListeningChoosenDTO questionListeningChoosenDTO :listQuestionListeningChoosenDTOSChild1) {
                List<QuestionMinitestDTO> questionMinitestDTO =
                        questionAnswerListeningRepository.getListAnswerListeningById(questionListeningChoosenDTO.getId());

                categoryMinitestDTOChild1 = categoryRepository.getCategoryById(questionListeningChoosenDTO.getCategoryId());

                if(this.findABCDofAnswerChoosen(questionMinitestDTO.get(0).getAnswersToChoose().trim(),(questionListeningChoosenDTO.getAnswerChoosen().trim()))
                        .equals(questionMinitestDTO.get(0).getAnswer()) && null != questionListeningChoosenDTO.getIndexSubAnswer() ){
                    //Trường hợp đúng
                    questionMinitestDTO.get(0).setIndexCorrectAnswer(questionListeningChoosenDTO.getIndexSubAnswer()); // set vị trí câu đúng
                    questionMinitestDTO.get(0).setIndexIncorrectAnswer((long) -1); // set vị trí câu sai
                    questionMinitestDTO.get(0).setStt(questionListeningChoosenDTO.getStt());
                    totalQuestion++;
                    totalCorectAnswer++;
                }
                else {
                    //Trường hợp sai
                    long indexCorrectAnswer = this.compareAnswer2(questionMinitestDTO.get(0).getAnswersToChoose().trim(), // tìm vị trí câu đúng
                            questionMinitestDTO.get(0).getAnswer().trim());
                    questionMinitestDTO.get(0).setIndexCorrectAnswer(indexCorrectAnswer); // set vị trí câu đúng
                    questionMinitestDTO.get(0).setIndexIncorrectAnswer(questionListeningChoosenDTO.getIndexSubAnswer()); // set vị trí câu sai
                    questionMinitestDTO.get(0).setStt(questionListeningChoosenDTO.getStt());
                    totalQuestion++;
                }
                listQuestionMinitestDTOsChild1.add(questionMinitestDTO.get(0));

                //Gọi hàm lưu quesion vào bảng detail history minitest
                saveQuesiontoDetailHistoryMinitest(questionMinitestDTO.get(0), partName, check);
            }
            categoryMinitestDTOChild1.setListQuestionMinitestDTOS(listQuestionMinitestDTOsChild1);
            lisCategoryMinitestDTOs.add(categoryMinitestDTOChild1);

            //for đoạn 2
            CategoryMinitestDTO categoryMinitestDTOChild2 = new CategoryMinitestDTO();
            List<QuestionMinitestDTO> listQuestionMinitestDTOsChild2 = new ArrayList<>();
            for (QuestionListeningChoosenDTO questionListeningChoosenDTO :listQuestionListeningChoosenDTOSChild2) {
                List<QuestionMinitestDTO> questionMinitestDTO =
                        questionAnswerListeningRepository.getListAnswerListeningById(questionListeningChoosenDTO.getId());

                categoryMinitestDTOChild2 = categoryRepository.getCategoryById(questionListeningChoosenDTO.getCategoryId());

                if(this.findABCDofAnswerChoosen(questionMinitestDTO.get(0).getAnswersToChoose().trim(),(questionListeningChoosenDTO.getAnswerChoosen().trim()))
                        .equals(questionMinitestDTO.get(0).getAnswer()) && null != questionListeningChoosenDTO.getIndexSubAnswer() ){
                    //Trường hợp đúng
                    questionMinitestDTO.get(0).setIndexCorrectAnswer(questionListeningChoosenDTO.getIndexSubAnswer()); // set vị trí câu đúng
                    questionMinitestDTO.get(0).setIndexIncorrectAnswer((long) -1); // set vị trí câu sai
                    questionMinitestDTO.get(0).setStt(questionListeningChoosenDTO.getStt());
                    totalQuestion++;
                    totalCorectAnswer++;
                }
                else {
                    //Trường hợp sai
                    long indexCorrectAnswer = this.compareAnswer2(questionMinitestDTO.get(0).getAnswersToChoose().trim(), // tìm vị trí câu đúng
                            questionMinitestDTO.get(0).getAnswer().trim());
                    questionMinitestDTO.get(0).setIndexCorrectAnswer(indexCorrectAnswer); // set vị trí câu đúng
                    questionMinitestDTO.get(0).setIndexIncorrectAnswer(questionListeningChoosenDTO.getIndexSubAnswer()); // set vị trí câu sai
                    questionMinitestDTO.get(0).setStt(questionListeningChoosenDTO.getStt());
                    totalQuestion++;
                }
                listQuestionMinitestDTOsChild2.add(questionMinitestDTO.get(0));

                //Gọi hàm lưu quesion vào bảng detail history minitest
                saveQuesiontoDetailHistoryMinitest(questionMinitestDTO.get(0), partName, check);
            }
            categoryMinitestDTOChild2.setListQuestionMinitestDTOS(listQuestionMinitestDTOsChild2);
            lisCategoryMinitestDTOs.add(categoryMinitestDTOChild2);

            //for đoạn 3
            CategoryMinitestDTO categoryMinitestDTOChild3 = new CategoryMinitestDTO();
            List<QuestionMinitestDTO> listQuestionMinitestDTOsChild3 = new ArrayList<>();
            for (QuestionListeningChoosenDTO questionListeningChoosenDTO :listQuestionListeningChoosenDTOSChild3) {
                List<QuestionMinitestDTO> questionMinitestDTO =
                        questionAnswerListeningRepository.getListAnswerListeningById(questionListeningChoosenDTO.getId());

                categoryMinitestDTOChild3 = categoryRepository.getCategoryById(questionListeningChoosenDTO.getCategoryId());

                if(this.findABCDofAnswerChoosen(questionMinitestDTO.get(0).getAnswersToChoose().trim(),(questionListeningChoosenDTO.getAnswerChoosen().trim()))
                        .equals(questionMinitestDTO.get(0).getAnswer()) && null != questionListeningChoosenDTO.getIndexSubAnswer() ){
                    //Trường hợp đúng
                    questionMinitestDTO.get(0).setIndexCorrectAnswer(questionListeningChoosenDTO.getIndexSubAnswer()); // set vị trí câu đúng
                    questionMinitestDTO.get(0).setIndexIncorrectAnswer((long) -1); // set vị trí câu sai
                    questionMinitestDTO.get(0).setStt(questionListeningChoosenDTO.getStt());
                    totalQuestion++;
                    totalCorectAnswer++;
                }
                else {
                    //Trường hợp sai
                    long indexCorrectAnswer = this.compareAnswer2(questionMinitestDTO.get(0).getAnswersToChoose().trim(), // tìm vị trí câu đúng
                            questionMinitestDTO.get(0).getAnswer().trim());
                    questionMinitestDTO.get(0).setIndexCorrectAnswer(indexCorrectAnswer); // set vị trí câu đúng
                    questionMinitestDTO.get(0).setIndexIncorrectAnswer(questionListeningChoosenDTO.getIndexSubAnswer()); // set vị trí câu sai
                    questionMinitestDTO.get(0).setStt(questionListeningChoosenDTO.getStt());
                    totalQuestion++;
                }
                listQuestionMinitestDTOsChild3.add(questionMinitestDTO.get(0));

                //Gọi hàm lưu quesion vào bảng detail history minitest
                saveQuesiontoDetailHistoryMinitest(questionMinitestDTO.get(0), partName, check);
            }
            categoryMinitestDTOChild3.setListQuestionMinitestDTOS(listQuestionMinitestDTOsChild3);
            lisCategoryMinitestDTOs.add(categoryMinitestDTOChild3);

            //for đoạn 4
            CategoryMinitestDTO categoryMinitestDTOChild4 = new CategoryMinitestDTO();
            List<QuestionMinitestDTO> listQuestionMinitestDTOsChild4 = new ArrayList<>();
            for (QuestionListeningChoosenDTO questionListeningChoosenDTO :listQuestionListeningChoosenDTOSChild4) {
                List<QuestionMinitestDTO> questionMinitestDTO =
                        questionAnswerListeningRepository.getListAnswerListeningById(questionListeningChoosenDTO.getId());

                categoryMinitestDTOChild4 = categoryRepository.getCategoryById(questionListeningChoosenDTO.getCategoryId());

                if(this.findABCDofAnswerChoosen(questionMinitestDTO.get(0).getAnswersToChoose().trim(),(questionListeningChoosenDTO.getAnswerChoosen().trim()))
                        .equals(questionMinitestDTO.get(0).getAnswer()) && null != questionListeningChoosenDTO.getIndexSubAnswer() ){
                    //Trường hợp đúng
                    questionMinitestDTO.get(0).setIndexCorrectAnswer(questionListeningChoosenDTO.getIndexSubAnswer()); // set vị trí câu đúng
                    questionMinitestDTO.get(0).setIndexIncorrectAnswer((long) -1); // set vị trí câu sai
                    questionMinitestDTO.get(0).setStt(questionListeningChoosenDTO.getStt());
                    totalQuestion++;
                    totalCorectAnswer++;
                }
                else {
                    //Trường hợp sai
                    long indexCorrectAnswer = this.compareAnswer2(questionMinitestDTO.get(0).getAnswersToChoose().trim(), // tìm vị trí câu đúng
                            questionMinitestDTO.get(0).getAnswer().trim());
                    questionMinitestDTO.get(0).setIndexCorrectAnswer(indexCorrectAnswer); // set vị trí câu đúng
                    questionMinitestDTO.get(0).setIndexIncorrectAnswer(questionListeningChoosenDTO.getIndexSubAnswer()); // set vị trí câu sai
                    questionMinitestDTO.get(0).setStt(questionListeningChoosenDTO.getStt());
                    totalQuestion++;
                }
                listQuestionMinitestDTOsChild4.add(questionMinitestDTO.get(0));
                //Gọi hàm lưu quesion vào bảng detail history minitest
                saveQuesiontoDetailHistoryMinitest(questionMinitestDTO.get(0), partName, check);
            }
            categoryMinitestDTOChild4.setListQuestionMinitestDTOS(listQuestionMinitestDTOsChild4);
            lisCategoryMinitestDTOs.add(categoryMinitestDTOChild4);

            //for đoạn 5
            CategoryMinitestDTO categoryMinitestDTOPart3Child5 = new CategoryMinitestDTO();
            List<QuestionMinitestDTO> listQuestionMinitestDTOsPart3Child5 = new ArrayList<>();
            for (QuestionListeningChoosenDTO questionListeningChoosenDTO :listQuestionListeningChoosenDTOSChild5) {
                List<QuestionMinitestDTO> questionMinitestDTO =
                        questionAnswerListeningRepository.getListAnswerListeningById(questionListeningChoosenDTO.getId());

                categoryMinitestDTOPart3Child5 = categoryRepository.getCategoryById(questionListeningChoosenDTO.getCategoryId());

                if(this.findABCDofAnswerChoosen(questionMinitestDTO.get(0).getAnswersToChoose().trim(),(questionListeningChoosenDTO.getAnswerChoosen().trim()))
                        .equals(questionMinitestDTO.get(0).getAnswer()) && null != questionListeningChoosenDTO.getIndexSubAnswer() ){
                    //Trường hợp đúng
                    questionMinitestDTO.get(0).setIndexCorrectAnswer(questionListeningChoosenDTO.getIndexSubAnswer()); // set vị trí câu đúng
                    questionMinitestDTO.get(0).setIndexIncorrectAnswer((long) -1); // set vị trí câu sai
                    questionMinitestDTO.get(0).setStt(questionListeningChoosenDTO.getStt());
                    totalQuestion++;
                    totalCorectAnswer++;
                }
                else {
                    //Trường hợp sai
                    long indexCorrectAnswer = this.compareAnswer2(questionMinitestDTO.get(0).getAnswersToChoose().trim(), // tìm vị trí câu đúng
                            questionMinitestDTO.get(0).getAnswer().trim());
                    questionMinitestDTO.get(0).setIndexCorrectAnswer(indexCorrectAnswer); // set vị trí câu đúng
                    questionMinitestDTO.get(0).setIndexIncorrectAnswer(questionListeningChoosenDTO.getIndexSubAnswer()); // set vị trí câu sai
                    questionMinitestDTO.get(0).setStt(questionListeningChoosenDTO.getStt());
                    totalQuestion++;
                }
                listQuestionMinitestDTOsPart3Child5.add(questionMinitestDTO.get(0));
                //Gọi hàm lưu quesion vào bảng detail history minitest
                saveQuesiontoDetailHistoryMinitest(questionMinitestDTO.get(0), partName, check);
            }
            categoryMinitestDTOPart3Child5.setListQuestionMinitestDTOS(listQuestionMinitestDTOsPart3Child5);
            lisCategoryMinitestDTOs.add(categoryMinitestDTOPart3Child5);

            minitestDTO.setPartName(partName);
            minitestDTO.setTotalQuestion(totalQuestion);
            minitestDTO.setTotalCorectAnswer(totalCorectAnswer);
            minitestDTO.setListCategoryMinitestDTOS(lisCategoryMinitestDTOs);
            return minitestDTO;
        }

        if(partName.equals(PART6) || partName.equals(PART7) || partName.equals(PART8)){

            //--xử lý chia question theo categoryId
            List<QuestionReadingChoosenDTO> listQuestionReadingChoosenDTOSChildRoot = new ArrayList<>();

            List<QuestionReadingChoosenDTO> listQuestionReadingChoosenDTOSChild1 = new ArrayList<>();
            List<QuestionReadingChoosenDTO> listQuestionReadingChoosenDTOSChild2 = new ArrayList<>();
            List<QuestionReadingChoosenDTO> listQuestionReadingChoosenDTOSChild3 = new ArrayList<>();
            List<QuestionReadingChoosenDTO> listQuestionReadingChoosenDTOSChild4 = new ArrayList<>();

            for (QuestionReadingChoosenDTO questionReadingChoosenDTO :listQuestionReadingChoosenDTOS) {
                if(questionReadingChoosenDTO.getPartName().equalsIgnoreCase(partName)){
                    listQuestionReadingChoosenDTOSChildRoot.add(questionReadingChoosenDTO);
                }
            }

            if(listQuestionReadingChoosenDTOSChildRoot.size() > 0){
                for (QuestionReadingChoosenDTO questionReadingChoosenDTO :listQuestionReadingChoosenDTOS) {
                    if(questionReadingChoosenDTO.getCategoryId().equals(listQuestionReadingChoosenDTOSChildRoot.get(0).getCategoryId())){
                        listQuestionReadingChoosenDTOSChild1.add(questionReadingChoosenDTO);
                    }
                }
                listQuestionReadingChoosenDTOSChildRoot.subList(0, listQuestionReadingChoosenDTOSChild1.size()-1).clear();
                listQuestionReadingChoosenDTOSChildRoot.remove(0);
            }



            if(listQuestionReadingChoosenDTOSChildRoot.size() > 0){
                for (QuestionReadingChoosenDTO questionReadingChoosenDTO :listQuestionReadingChoosenDTOS) {
                    if(questionReadingChoosenDTO.getCategoryId().equals(listQuestionReadingChoosenDTOSChildRoot.get(0).getCategoryId())){
                        listQuestionReadingChoosenDTOSChild2.add(questionReadingChoosenDTO);
                    }
                }
                listQuestionReadingChoosenDTOSChildRoot.subList(0, listQuestionReadingChoosenDTOSChild2.size()-1).clear();
                listQuestionReadingChoosenDTOSChildRoot.remove(0);
            }



            if(listQuestionReadingChoosenDTOSChildRoot.size() > 0){
                for (QuestionReadingChoosenDTO questionReadingChoosenDTO :listQuestionReadingChoosenDTOS) {
                    if(questionReadingChoosenDTO.getCategoryId().equals(listQuestionReadingChoosenDTOSChildRoot.get(0).getCategoryId())){
                        listQuestionReadingChoosenDTOSChild3.add(questionReadingChoosenDTO);
                    }
                }
                listQuestionReadingChoosenDTOSChildRoot.subList(0, listQuestionReadingChoosenDTOSChild3.size()-1).clear();
                listQuestionReadingChoosenDTOSChildRoot.remove(0);
            }

            if(partName.equals(PART7)){
                if(listQuestionReadingChoosenDTOSChildRoot.size() > 0){
                    for (QuestionReadingChoosenDTO questionReadingChoosenDTO :listQuestionReadingChoosenDTOS) {
                        if(questionReadingChoosenDTO.getCategoryId().equals(listQuestionReadingChoosenDTOSChildRoot.get(0).getCategoryId())){
                            listQuestionReadingChoosenDTOSChild4.add(questionReadingChoosenDTO);
                        }
                    }
                }
            }


            //add vào list trong api
            MinitestDTO minitestDTO = new MinitestDTO();
            int totalQuestion = 0;
            int totalCorectAnswer = 0;
            List<CategoryMinitestDTO> lisCategoryMinitestDTOs = new ArrayList<>();
            CategoryMinitestDTO categoryMinitestDTOChild1 = new CategoryMinitestDTO();

            //for đoạn 1
            List<QuestionMinitestDTO> listQuestionMinitestDTOsChild1 = new ArrayList<>();
            for (QuestionReadingChoosenDTO questionReadingChoosenDTO :listQuestionReadingChoosenDTOSChild1) {
                QuestionMinitestDTO questionMinitestDTO =
                        questionAnswerReadingRepository.getAnswerReadingById(questionReadingChoosenDTO.getId());

                categoryMinitestDTOChild1 = categoryRepository.getCategoryById(questionReadingChoosenDTO.getCategoryId());

                if(this.findABCDofAnswerChoosen(questionMinitestDTO.getAnswersToChoose().trim(),(questionReadingChoosenDTO.getAnswerChoosen().trim()))
                        .equals(questionMinitestDTO.getAnswer()) && null != questionReadingChoosenDTO.getIndexSubAnswer()){
                    //Trường hợp đúng
                    questionMinitestDTO.setIndexCorrectAnswer(questionReadingChoosenDTO.getIndexSubAnswer()); // set vị trí câu đúng
                    questionMinitestDTO.setIndexIncorrectAnswer((long) -1); // set vị trí câu sai
                    questionMinitestDTO.setStt(questionReadingChoosenDTO.getStt());
                    totalQuestion++;
                    totalCorectAnswer++;
                }
                else {
                    //Trường hợp sai
                    long indexCorrectAnswer = this.compareAnswer2(questionMinitestDTO.getAnswersToChoose().trim(), // tìm vị trí câu đúng
                            questionMinitestDTO.getAnswer().trim());
                    questionMinitestDTO.setIndexCorrectAnswer(indexCorrectAnswer); // set vị trí câu đúng
                    questionMinitestDTO.setIndexIncorrectAnswer(questionReadingChoosenDTO.getIndexSubAnswer()); // set vị trí câu sai
                    questionMinitestDTO.setStt(questionReadingChoosenDTO.getStt());
                    totalQuestion++;
                }
                listQuestionMinitestDTOsChild1.add(questionMinitestDTO);
                //Gọi hàm lưu quesion vào bảng detail history minitest
                saveQuesiontoDetailHistoryMinitest(questionMinitestDTO, partName, check);
            }
            categoryMinitestDTOChild1.setListQuestionMinitestDTOS(listQuestionMinitestDTOsChild1);
            lisCategoryMinitestDTOs.add(categoryMinitestDTOChild1);

            //for đoạn 2
            CategoryMinitestDTO categoryMinitestDTOChild2 = new CategoryMinitestDTO();
            List<QuestionMinitestDTO> listQuestionMinitestDTOsChild2 = new ArrayList<>();
            for (QuestionReadingChoosenDTO questionReadingChoosenDTO :listQuestionReadingChoosenDTOSChild2) {
                QuestionMinitestDTO questionMinitestDTO =
                        questionAnswerReadingRepository.getAnswerReadingById(questionReadingChoosenDTO.getId());

                categoryMinitestDTOChild2 = categoryRepository.getCategoryById(questionReadingChoosenDTO.getCategoryId());

                if(this.findABCDofAnswerChoosen(questionMinitestDTO.getAnswersToChoose().trim(),(questionReadingChoosenDTO.getAnswerChoosen().trim()))
                        .equals(questionMinitestDTO.getAnswer()) && null != questionReadingChoosenDTO.getIndexSubAnswer()){
                    //Trường hợp đúng
                    questionMinitestDTO.setIndexCorrectAnswer(questionReadingChoosenDTO.getIndexSubAnswer()); // set vị trí câu đúng
                    questionMinitestDTO.setIndexIncorrectAnswer((long) -1); // set vị trí câu sai
                    questionMinitestDTO.setStt(questionReadingChoosenDTO.getStt());
                    totalQuestion++;
                    totalCorectAnswer++;
                }
                else {
                    //Trường hợp sai
                    long indexCorrectAnswer = this.compareAnswer2(questionMinitestDTO.getAnswersToChoose().trim(), // tìm vị trí câu đúng
                            questionMinitestDTO.getAnswer().trim());
                    questionMinitestDTO.setIndexCorrectAnswer(indexCorrectAnswer); // set vị trí câu đúng
                    questionMinitestDTO.setIndexIncorrectAnswer(questionReadingChoosenDTO.getIndexSubAnswer()); // set vị trí câu sai
                    questionMinitestDTO.setStt(questionReadingChoosenDTO.getStt());
                    totalQuestion++;
                }
                listQuestionMinitestDTOsChild2.add(questionMinitestDTO);
                //Gọi hàm lưu quesion vào bảng detail history minitest
                saveQuesiontoDetailHistoryMinitest(questionMinitestDTO, partName, check);
            }
            categoryMinitestDTOChild2.setListQuestionMinitestDTOS(listQuestionMinitestDTOsChild2);
            lisCategoryMinitestDTOs.add(categoryMinitestDTOChild2);

            //for đoạn 3
            CategoryMinitestDTO categoryMinitestDTOChild3 = new CategoryMinitestDTO();
            List<QuestionMinitestDTO> listQuestionMinitestDTOsChild3 = new ArrayList<>();
            for (QuestionReadingChoosenDTO questionReadingChoosenDTO :listQuestionReadingChoosenDTOSChild3) {
                QuestionMinitestDTO questionMinitestDTO =
                        questionAnswerReadingRepository.getAnswerReadingById(questionReadingChoosenDTO.getId());

                categoryMinitestDTOChild3 = categoryRepository.getCategoryById(questionReadingChoosenDTO.getCategoryId());

                if(this.findABCDofAnswerChoosen(questionMinitestDTO.getAnswersToChoose().trim(),(questionReadingChoosenDTO.getAnswerChoosen().trim()))
                        .equals(questionMinitestDTO.getAnswer()) && null != questionReadingChoosenDTO.getIndexSubAnswer()){
                    //Trường hợp đúng
                    questionMinitestDTO.setIndexCorrectAnswer(questionReadingChoosenDTO.getIndexSubAnswer()); // set vị trí câu đúng
                    questionMinitestDTO.setIndexIncorrectAnswer((long) -1); // set vị trí câu sai
                    questionMinitestDTO.setStt(questionReadingChoosenDTO.getStt());
                    totalQuestion++;
                    totalCorectAnswer++;
                }
                else {
                    //Trường hợp sai
                    long indexCorrectAnswer = this.compareAnswer2(questionMinitestDTO.getAnswersToChoose().trim(), // tìm vị trí câu đúng
                            questionMinitestDTO.getAnswer().trim());
                    questionMinitestDTO.setIndexCorrectAnswer(indexCorrectAnswer); // set vị trí câu đúng
                    questionMinitestDTO.setIndexIncorrectAnswer(questionReadingChoosenDTO.getIndexSubAnswer()); // set vị trí câu sai
                    questionMinitestDTO.setStt(questionReadingChoosenDTO.getStt());
                    totalQuestion++;
                }
                listQuestionMinitestDTOsChild3.add(questionMinitestDTO);
                //Gọi hàm lưu quesion vào bảng detail history minitest
                saveQuesiontoDetailHistoryMinitest(questionMinitestDTO, partName, check);
            }
            categoryMinitestDTOChild3.setListQuestionMinitestDTOS(listQuestionMinitestDTOsChild3);
            lisCategoryMinitestDTOs.add(categoryMinitestDTOChild3);

            //for đoạn 4
            if(partName.equals(PART7)){
                CategoryMinitestDTO categoryMinitestDTOChild4 = new CategoryMinitestDTO();
                List<QuestionMinitestDTO> listQuestionMinitestDTOsChild4 = new ArrayList<>();
                for (QuestionReadingChoosenDTO questionReadingChoosenDTO :listQuestionReadingChoosenDTOSChild4) {
                    QuestionMinitestDTO questionMinitestDTO =
                            questionAnswerReadingRepository.getAnswerReadingById(questionReadingChoosenDTO.getId());

                    categoryMinitestDTOChild4 = categoryRepository.getCategoryById(questionReadingChoosenDTO.getCategoryId());

                    if(this.findABCDofAnswerChoosen(questionMinitestDTO.getAnswersToChoose().trim(),(questionReadingChoosenDTO.getAnswerChoosen().trim()))
                            .equals(questionMinitestDTO.getAnswer()) && null != questionReadingChoosenDTO.getIndexSubAnswer()){
                        //Trường hợp đúng
                        questionMinitestDTO.setIndexCorrectAnswer(questionReadingChoosenDTO.getIndexSubAnswer()); // set vị trí câu đúng
                        questionMinitestDTO.setIndexIncorrectAnswer((long) -1); // set vị trí câu sai
                        questionMinitestDTO.setStt(questionReadingChoosenDTO.getStt());
                        totalQuestion++;
                        totalCorectAnswer++;
                    }
                    else {
                        //Trường hợp sai
                        long indexCorrectAnswer = this.compareAnswer2(questionMinitestDTO.getAnswersToChoose().trim(), // tìm vị trí câu đúng
                                questionMinitestDTO.getAnswer().trim());
                        questionMinitestDTO.setIndexCorrectAnswer(indexCorrectAnswer); // set vị trí câu đúng
                        questionMinitestDTO.setIndexIncorrectAnswer(questionReadingChoosenDTO.getIndexSubAnswer()); // set vị trí câu sai
                        questionMinitestDTO.setStt(questionReadingChoosenDTO.getStt());
                        totalQuestion++;
                    }
                    listQuestionMinitestDTOsChild4.add(questionMinitestDTO);
                    //Gọi hàm lưu quesion vào bảng detail history minitest
                    saveQuesiontoDetailHistoryMinitest(questionMinitestDTO, partName, check);
                }
                categoryMinitestDTOChild4.setListQuestionMinitestDTOS(listQuestionMinitestDTOsChild4);
                lisCategoryMinitestDTOs.add(categoryMinitestDTOChild4);
            }

            minitestDTO.setPartName(partName);
            minitestDTO.setTotalQuestion(totalQuestion);
            minitestDTO.setTotalCorectAnswer(totalCorectAnswer);
            minitestDTO.setListCategoryMinitestDTOS(lisCategoryMinitestDTOs);
            return minitestDTO;
        }

        return null;

    }
    /* end lay cac cau tra loi minitest theo part3, part4, part6, part7, part8 */

    /*Luu ket qua lam bai*/
    public String saveResultMinitest(MinitestResultHistoryDTO minitestResultHistoryDTO){

        try{
            List<QuestionListeningChoosenDTO> listQuestionListeningChoosenDTOS =
                    minitestResultHistoryDTO.getMinitestSubmitAnswerDTO().getListQuestionListeningChoosenDTOS();
            List<QuestionReadingChoosenDTO> listQuestionReadingChoosenDTOS =
                    minitestResultHistoryDTO.getMinitestSubmitAnswerDTO().getListQuestionReadingChoosenDTOS();

            List<MinitestDTO> listMinitestDTOS = new ArrayList<>();
            //get answer part1
            MinitestDTO minitestDTO = getQuestionAndAnswerMinitestByPart(listQuestionListeningChoosenDTOS,
                    listQuestionReadingChoosenDTOS, PART1, minitestResultHistoryDTO.getCheck());
            listMinitestDTOS.add(minitestDTO);

            //get answer part2
            minitestDTO = getQuestionAndAnswerMinitestByPart(listQuestionListeningChoosenDTOS,
                    listQuestionReadingChoosenDTOS, PART2, minitestResultHistoryDTO.getCheck());
            listMinitestDTOS.add(minitestDTO);

            //get answer part3
            minitestDTO = getQuestionAndAnswerMinitestByPartOther(listQuestionListeningChoosenDTOS,
                    listQuestionReadingChoosenDTOS, PART3, minitestResultHistoryDTO.getCheck());
            listMinitestDTOS.add(minitestDTO);

            //get answer part4
            minitestDTO = getQuestionAndAnswerMinitestByPartOther(listQuestionListeningChoosenDTOS,
                    listQuestionReadingChoosenDTOS, PART4, minitestResultHistoryDTO.getCheck());
            listMinitestDTOS.add(minitestDTO);

            //get answer part5
            minitestDTO = getQuestionAndAnswerMinitestByPart(listQuestionListeningChoosenDTOS,
                    listQuestionReadingChoosenDTOS, PART5, minitestResultHistoryDTO.getCheck());
            listMinitestDTOS.add(minitestDTO);

            //get answer part6
            minitestDTO = getQuestionAndAnswerMinitestByPartOther(listQuestionListeningChoosenDTOS,
                    listQuestionReadingChoosenDTOS, PART6, minitestResultHistoryDTO.getCheck());
            listMinitestDTOS.add(minitestDTO);

            //get answer part7
            minitestDTO = getQuestionAndAnswerMinitestByPartOther(listQuestionListeningChoosenDTOS,
                    listQuestionReadingChoosenDTOS, PART7, minitestResultHistoryDTO.getCheck());
            listMinitestDTOS.add(minitestDTO);

            //get answer part8
            minitestDTO = getQuestionAndAnswerMinitestByPartOther(listQuestionListeningChoosenDTOS,
                    listQuestionReadingChoosenDTOS, PART8, minitestResultHistoryDTO.getCheck());
            listMinitestDTOS.add(minitestDTO);

            long totalCorrectAnswerListening = listMinitestDTOS.get(0).getTotalCorectAnswer() + listMinitestDTOS.get(1).getTotalCorectAnswer()
                    + listMinitestDTOS.get(2).getTotalCorectAnswer() + listMinitestDTOS.get(3).getTotalCorectAnswer();
            long totalQuestionListening = listMinitestDTOS.get(0).getTotalQuestion() + listMinitestDTOS.get(1).getTotalQuestion() +
                    listMinitestDTOS.get(2).getTotalQuestion() + listMinitestDTOS.get(3).getTotalQuestion() ;

            long totalCorrectAnswerReading = listMinitestDTOS.get(4).getTotalCorectAnswer() + listMinitestDTOS.get(5).getTotalCorectAnswer()
                    + listMinitestDTOS.get(6).getTotalCorectAnswer() + listMinitestDTOS.get(7).getTotalCorectAnswer();
            long totalQuestionReading = listMinitestDTOS.get(4).getTotalQuestion() + listMinitestDTOS.get(5).getTotalQuestion() +
                    listMinitestDTOS.get(6).getTotalQuestion() + listMinitestDTOS.get(7).getTotalQuestion() ;

            HistoryMinitestDTO historyMinitestDTO = new HistoryMinitestDTO();

            HistoryMinitestDTO historyMinitestDTO2 = historyMinitestRepository.getMaxTestIdOfHistoryMinitest();

            long testId = 1;
            if(null == historyMinitestDTO2){
                testId = 1;
            }
            else {
                testId = testId + historyMinitestDTO2.getTestId();
            }


            historyMinitestDTO.setTestId(testId);
            historyMinitestDTO.setUserId(minitestResultHistoryDTO.getUserId());

//            LocalDateTime localDateTime = LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh"));
//            Date createDate = Date.from(localDateTime.atZone(ZoneId.of("Asia/Ho_Chi_Minh")).toInstant());
            historyMinitestDTO.setDoTestDate(new Date());

            historyMinitestDTO.setStatus((long) 1);
            historyMinitestDTO.setTotalCorrectAnswerListening(totalCorrectAnswerListening +"/"+ totalQuestionListening);
            historyMinitestDTO.setTotalCorrectAnswerReading(totalCorrectAnswerReading +"/"+ totalQuestionReading);

            historyMinitestDTO.setPart1(listMinitestDTOS.get(0).getTotalCorectAnswer() +"/"+ listMinitestDTOS.get(0).getTotalQuestion());
            historyMinitestDTO.setPart2(listMinitestDTOS.get(1).getTotalCorectAnswer() +"/"+ listMinitestDTOS.get(1).getTotalQuestion());
            historyMinitestDTO.setPart3(listMinitestDTOS.get(2).getTotalCorectAnswer() +"/"+ listMinitestDTOS.get(2).getTotalQuestion());
            historyMinitestDTO.setPart4(listMinitestDTOS.get(3).getTotalCorectAnswer() +"/"+ listMinitestDTOS.get(3).getTotalQuestion());
            historyMinitestDTO.setPart5(listMinitestDTOS.get(4).getTotalCorectAnswer() +"/"+ listMinitestDTOS.get(4).getTotalQuestion());
            historyMinitestDTO.setPart6(listMinitestDTOS.get(5).getTotalCorectAnswer() +"/"+ listMinitestDTOS.get(5).getTotalQuestion());
            historyMinitestDTO.setPart7(listMinitestDTOS.get(6).getTotalCorectAnswer() +"/"+ listMinitestDTOS.get(6).getTotalQuestion());
            historyMinitestDTO.setPart8(listMinitestDTOS.get(7).getTotalCorectAnswer() +"/"+ listMinitestDTOS.get(7).getTotalQuestion());
            historyMinitestDTO.setTotalTime(minitestResultHistoryDTO.getTotalTime());

            historyMinitestRepository.insert(historyMinitestDTO.toModel());

//            return "Lưu kết quả bài thi Minitest thành công";
            return MessageUtils.getMessage("save_minitest_result_successfully");
        }
        catch (Exception e){
//            return "Đã xảy ra lỗi trong lúc lưu";
            return MessageUtils.getMessage("there_was_an_error_while_saving_result");
        }

    }
    /*End luu ket qua lam bai*/

    public Integer cutTotal(String totalA, int partIndex){
        String[] parts = totalA.split("/");
        return Integer.parseInt(parts[partIndex]);
    }

    /*get detail history minitest*/
    public MinitestDTO getDetailHistoryMinitestByPart(Long minitestId, String partName){
        MinitestDTO minitestDTO = new MinitestDTO();
        List<DetailHistoryMinitestDTO> listDetailHistoryMinitestDTOs =
                detailHistoryMinitestRepository.getDetailHistoryMinitestByParentId(minitestId, partName);

        HistoryMinitestDTO historyMinitestDTO = historyMinitestRepository.getHistoryMinitestById(minitestId);

        //------------
        List<CategoryMinitestDTO> listCategoryMinitestDTOs = new ArrayList<>();

        CategoryMinitestDTO categoryMinitestDTO = new CategoryMinitestDTO();

        List<QuestionMinitestDTO> listQuestionMinitestDTOs = new ArrayList<>();



        if(partName.equals(PART1) || partName.equals(PART2)){

            for (DetailHistoryMinitestDTO detailHistoryMinitestDTO: listDetailHistoryMinitestDTOs) {
                List<QuestionMinitestDTO> questionMinitestDTO =
                        questionAnswerListeningRepository.getListAnswerListeningById(detailHistoryMinitestDTO.getQuestionId());
                questionMinitestDTO.get(0).setIndexCorrectAnswer(detailHistoryMinitestDTO.getIndexCorrectAnswer());
                questionMinitestDTO.get(0).setIndexIncorrectAnswer(detailHistoryMinitestDTO.getIndexIncorrectAnswer());
                questionMinitestDTO.get(0).setStt(detailHistoryMinitestDTO.getStt());
                listQuestionMinitestDTOs.add(questionMinitestDTO.get(0));
            }

            categoryMinitestDTO.setListQuestionMinitestDTOS(listQuestionMinitestDTOs);
        }

        if(partName.equals(PART5)){
            for (DetailHistoryMinitestDTO detailHistoryMinitestDTO: listDetailHistoryMinitestDTOs) {
                QuestionMinitestDTO questionMinitestDTO =
                        questionAnswerReadingRepository.getAnswerReadingById(detailHistoryMinitestDTO.getQuestionId());
                questionMinitestDTO.setIndexCorrectAnswer(detailHistoryMinitestDTO.getIndexCorrectAnswer());
                questionMinitestDTO.setIndexIncorrectAnswer(detailHistoryMinitestDTO.getIndexIncorrectAnswer());
                questionMinitestDTO.setStt(detailHistoryMinitestDTO.getStt());
                listQuestionMinitestDTOs.add(questionMinitestDTO);
            }

            categoryMinitestDTO.setListQuestionMinitestDTOS(listQuestionMinitestDTOs);
        }

        listCategoryMinitestDTOs.add(categoryMinitestDTO);

        minitestDTO.setPartName(partName);
//        minitestDTO.setTotalQuestion(cutTotal(historyMinitestDTO.getTotalCorrectAnswerListening(), 1) +
//                cutTotal(historyMinitestDTO.getTotalCorrectAnswerReading(), 1));
//        minitestDTO.setTotalCorectAnswer(cutTotal(historyMinitestDTO.getTotalCorrectAnswerListening(), 0) +
//                cutTotal(historyMinitestDTO.getTotalCorrectAnswerReading(), 0));

        if(partName.equals(PART1)){
            minitestDTO.setTotalQuestion(cutTotal(historyMinitestDTO.getPart1(), 1));
            minitestDTO.setTotalCorectAnswer(cutTotal(historyMinitestDTO.getPart1(), 0));
        }
        if(partName.equals(PART2)){
            minitestDTO.setTotalQuestion(cutTotal(historyMinitestDTO.getPart2(), 1));
            minitestDTO.setTotalCorectAnswer(cutTotal(historyMinitestDTO.getPart2(), 0));
        }
        if(partName.equals(PART5)){
            minitestDTO.setTotalQuestion(cutTotal(historyMinitestDTO.getPart5(), 1));
            minitestDTO.setTotalCorectAnswer(cutTotal(historyMinitestDTO.getPart5(), 0));
        }


        minitestDTO.setListCategoryMinitestDTOS(listCategoryMinitestDTOs);
        minitestDTO.setTotalTime(historyMinitestDTO.getTotalTime());

        return minitestDTO;
    }

    public MinitestDTO getDetailHistoryMinitesByParttOther(Long minitestId, String partName){

        List<DetailHistoryMinitestDTO> listDetailHistoryMinitestDTOs =
                detailHistoryMinitestRepository.getDetailHistoryMinitestByParentId(minitestId, partName);

        HistoryMinitestDTO historyMinitestDTO = historyMinitestRepository.getHistoryMinitestById(minitestId);

        //------------

        if(partName.equals(PART3) || partName.equals(PART4)) {

            MinitestDTO minitestDTO = new MinitestDTO();
            List<DetailHistoryMinitestDTO> listDetailHistoryMinitestDTOSChild1 = new ArrayList<>();
            List<DetailHistoryMinitestDTO> listDetailHistoryMinitestDTOSChild2 = new ArrayList<>();
            List<DetailHistoryMinitestDTO> listDetailHistoryMinitestDTOSChild3 = new ArrayList<>();
            List<DetailHistoryMinitestDTO> listDetailHistoryMinitestDTOSChild4 = new ArrayList<>();
            List<DetailHistoryMinitestDTO> listDetailHistoryMinitestDTOSChild5 = new ArrayList<>();

            if (listDetailHistoryMinitestDTOs.size() > 0) {
                for (DetailHistoryMinitestDTO detailHistoryMinitestDTO : listDetailHistoryMinitestDTOs) {
                    if (detailHistoryMinitestDTO.getCategoryId().equals(listDetailHistoryMinitestDTOs.get(0).getCategoryId())) {
                        listDetailHistoryMinitestDTOSChild1.add(detailHistoryMinitestDTO);
                    }
                }
                listDetailHistoryMinitestDTOs.subList(0, listDetailHistoryMinitestDTOSChild1.size() - 1).clear();
                listDetailHistoryMinitestDTOs.remove(0);
            }

            if (listDetailHistoryMinitestDTOs.size() > 0) {
                for (DetailHistoryMinitestDTO detailHistoryMinitestDTO : listDetailHistoryMinitestDTOs) {
                    if (detailHistoryMinitestDTO.getCategoryId().equals(listDetailHistoryMinitestDTOs.get(0).getCategoryId())) {
                        listDetailHistoryMinitestDTOSChild2.add(detailHistoryMinitestDTO);
                    }
                }
                listDetailHistoryMinitestDTOs.subList(0, listDetailHistoryMinitestDTOSChild2.size() - 1).clear();
                listDetailHistoryMinitestDTOs.remove(0);
            }

            if (listDetailHistoryMinitestDTOs.size() > 0) {
                for (DetailHistoryMinitestDTO detailHistoryMinitestDTO : listDetailHistoryMinitestDTOs) {
                    if (detailHistoryMinitestDTO.getCategoryId().equals(listDetailHistoryMinitestDTOs.get(0).getCategoryId())) {
                        listDetailHistoryMinitestDTOSChild3.add(detailHistoryMinitestDTO);
                    }
                }
                listDetailHistoryMinitestDTOs.subList(0, listDetailHistoryMinitestDTOSChild3.size() - 1).clear();
                listDetailHistoryMinitestDTOs.remove(0);
            }

            if (listDetailHistoryMinitestDTOs.size() > 0) {
                for (DetailHistoryMinitestDTO detailHistoryMinitestDTO : listDetailHistoryMinitestDTOs) {
                    if (detailHistoryMinitestDTO.getCategoryId().equals(listDetailHistoryMinitestDTOs.get(0).getCategoryId())) {
                        listDetailHistoryMinitestDTOSChild4.add(detailHistoryMinitestDTO);
                    }
                }
                listDetailHistoryMinitestDTOs.subList(0, listDetailHistoryMinitestDTOSChild4.size() - 1).clear();
                listDetailHistoryMinitestDTOs.remove(0);
            }

            if (listDetailHistoryMinitestDTOs.size() > 0) {
                for (DetailHistoryMinitestDTO detailHistoryMinitestDTO : listDetailHistoryMinitestDTOs) {
                    if (detailHistoryMinitestDTO.getCategoryId().equals(listDetailHistoryMinitestDTOs.get(0).getCategoryId())) {
                        listDetailHistoryMinitestDTOSChild5.add(detailHistoryMinitestDTO);
                    }
                }
            }

            List<CategoryMinitestDTO> listCategoryMinitestDTOs = new ArrayList<>();

            //for đoạn 1
            CategoryMinitestDTO categoryMinitestDTO = new CategoryMinitestDTO();
            categoryMinitestDTO = categoryRepository.getCategoryById(listDetailHistoryMinitestDTOSChild1.get(0).getCategoryId());
            List<QuestionMinitestDTO> listQuestionMinitestDTOs = new ArrayList<>();
            for (DetailHistoryMinitestDTO detailHistoryMinitestDTO: listDetailHistoryMinitestDTOSChild1) {
                List<QuestionMinitestDTO> questionMinitestDTO =
                        questionAnswerListeningRepository.getListAnswerListeningById(detailHistoryMinitestDTO.getQuestionId());
                questionMinitestDTO.get(0).setIndexCorrectAnswer(detailHistoryMinitestDTO.getIndexCorrectAnswer());
                questionMinitestDTO.get(0).setIndexIncorrectAnswer(detailHistoryMinitestDTO.getIndexIncorrectAnswer());
                questionMinitestDTO.get(0).setStt(detailHistoryMinitestDTO.getStt());
                listQuestionMinitestDTOs.add(questionMinitestDTO.get(0));
            }

            categoryMinitestDTO.setListQuestionMinitestDTOS(listQuestionMinitestDTOs);
            listCategoryMinitestDTOs.add(categoryMinitestDTO);

            //for đoạn 2
            categoryMinitestDTO = new CategoryMinitestDTO();
            categoryMinitestDTO = categoryRepository.getCategoryById(listDetailHistoryMinitestDTOSChild2.get(0).getCategoryId());
            listQuestionMinitestDTOs = new ArrayList<>();
            for (DetailHistoryMinitestDTO detailHistoryMinitestDTO: listDetailHistoryMinitestDTOSChild2) {
                List<QuestionMinitestDTO> questionMinitestDTO =
                        questionAnswerListeningRepository.getListAnswerListeningById(detailHistoryMinitestDTO.getQuestionId());
                questionMinitestDTO.get(0).setIndexCorrectAnswer(detailHistoryMinitestDTO.getIndexCorrectAnswer());
                questionMinitestDTO.get(0).setIndexIncorrectAnswer(detailHistoryMinitestDTO.getIndexIncorrectAnswer());
                questionMinitestDTO.get(0).setStt(detailHistoryMinitestDTO.getStt());
                listQuestionMinitestDTOs.add(questionMinitestDTO.get(0));
            }

            categoryMinitestDTO.setListQuestionMinitestDTOS(listQuestionMinitestDTOs);
            listCategoryMinitestDTOs.add(categoryMinitestDTO);

            //for đoạn 3
            categoryMinitestDTO = new CategoryMinitestDTO();
            categoryMinitestDTO = categoryRepository.getCategoryById(listDetailHistoryMinitestDTOSChild3.get(0).getCategoryId());
            listQuestionMinitestDTOs = new ArrayList<>();
            for (DetailHistoryMinitestDTO detailHistoryMinitestDTO: listDetailHistoryMinitestDTOSChild3) {
                List<QuestionMinitestDTO> questionMinitestDTO =
                        questionAnswerListeningRepository.getListAnswerListeningById(detailHistoryMinitestDTO.getQuestionId());
                questionMinitestDTO.get(0).setIndexCorrectAnswer(detailHistoryMinitestDTO.getIndexCorrectAnswer());
                questionMinitestDTO.get(0).setIndexIncorrectAnswer(detailHistoryMinitestDTO.getIndexIncorrectAnswer());
                questionMinitestDTO.get(0).setStt(detailHistoryMinitestDTO.getStt());
                listQuestionMinitestDTOs.add(questionMinitestDTO.get(0));
            }

            categoryMinitestDTO.setListQuestionMinitestDTOS(listQuestionMinitestDTOs);
            listCategoryMinitestDTOs.add(categoryMinitestDTO);


            //for đoạn 4
            categoryMinitestDTO = new CategoryMinitestDTO();
            categoryMinitestDTO = categoryRepository.getCategoryById(listDetailHistoryMinitestDTOSChild4.get(0).getCategoryId());
            listQuestionMinitestDTOs = new ArrayList<>();
            for (DetailHistoryMinitestDTO detailHistoryMinitestDTO: listDetailHistoryMinitestDTOSChild4) {
                List<QuestionMinitestDTO> questionMinitestDTO =
                        questionAnswerListeningRepository.getListAnswerListeningById(detailHistoryMinitestDTO.getQuestionId());
                questionMinitestDTO.get(0).setIndexCorrectAnswer(detailHistoryMinitestDTO.getIndexCorrectAnswer());
                questionMinitestDTO.get(0).setIndexIncorrectAnswer(detailHistoryMinitestDTO.getIndexIncorrectAnswer());
                questionMinitestDTO.get(0).setStt(detailHistoryMinitestDTO.getStt());
                listQuestionMinitestDTOs.add(questionMinitestDTO.get(0));
            }

            categoryMinitestDTO.setListQuestionMinitestDTOS(listQuestionMinitestDTOs);
            listCategoryMinitestDTOs.add(categoryMinitestDTO);


            //for đoạn 5
            categoryMinitestDTO = new CategoryMinitestDTO();
            categoryMinitestDTO = categoryRepository.getCategoryById(listDetailHistoryMinitestDTOSChild5.get(0).getCategoryId());
            listQuestionMinitestDTOs = new ArrayList<>();
            for (DetailHistoryMinitestDTO detailHistoryMinitestDTO: listDetailHistoryMinitestDTOSChild5) {
                List<QuestionMinitestDTO> questionMinitestDTO =
                        questionAnswerListeningRepository.getListAnswerListeningById(detailHistoryMinitestDTO.getQuestionId());
                questionMinitestDTO.get(0).setIndexCorrectAnswer(detailHistoryMinitestDTO.getIndexCorrectAnswer());
                questionMinitestDTO.get(0).setIndexIncorrectAnswer(detailHistoryMinitestDTO.getIndexIncorrectAnswer());
                questionMinitestDTO.get(0).setStt(detailHistoryMinitestDTO.getStt());
                listQuestionMinitestDTOs.add(questionMinitestDTO.get(0));
            }

            categoryMinitestDTO.setListQuestionMinitestDTOS(listQuestionMinitestDTOs);
            listCategoryMinitestDTOs.add(categoryMinitestDTO);

            minitestDTO.setListCategoryMinitestDTOS(listCategoryMinitestDTOs);
            minitestDTO.setPartName(partName);

            if(partName.equals(PART3)){
                minitestDTO.setTotalQuestion(cutTotal(historyMinitestDTO.getPart3(), 1));
                minitestDTO.setTotalCorectAnswer(cutTotal(historyMinitestDTO.getPart3(), 0));
            }
            if(partName.equals(PART4)){
                minitestDTO.setTotalQuestion(cutTotal(historyMinitestDTO.getPart4(), 1));
                minitestDTO.setTotalCorectAnswer(cutTotal(historyMinitestDTO.getPart4(), 0));
            }

            minitestDTO.setListCategoryMinitestDTOS(listCategoryMinitestDTOs);
            minitestDTO.setTotalTime(historyMinitestDTO.getTotalTime());

            return minitestDTO;
        }

        if(partName.equals(PART6) || partName.equals(PART7) || partName.equals(PART8)) {

            MinitestDTO minitestDTO = new MinitestDTO();
            List<DetailHistoryMinitestDTO> listDetailHistoryMinitestDTOSChild1 = new ArrayList<>();
            List<DetailHistoryMinitestDTO> listDetailHistoryMinitestDTOSChild2 = new ArrayList<>();
            List<DetailHistoryMinitestDTO> listDetailHistoryMinitestDTOSChild3 = new ArrayList<>();
            List<DetailHistoryMinitestDTO> listDetailHistoryMinitestDTOSChild4 = new ArrayList<>();
            List<DetailHistoryMinitestDTO> listDetailHistoryMinitestDTOSChild5 = new ArrayList<>();

            if (listDetailHistoryMinitestDTOs.size() > 0) {
                for (DetailHistoryMinitestDTO detailHistoryMinitestDTO : listDetailHistoryMinitestDTOs) {
                    if (detailHistoryMinitestDTO.getCategoryId().equals(listDetailHistoryMinitestDTOs.get(0).getCategoryId())) {
                        listDetailHistoryMinitestDTOSChild1.add(detailHistoryMinitestDTO);
                    }
                }
                listDetailHistoryMinitestDTOs.subList(0, listDetailHistoryMinitestDTOSChild1.size() - 1).clear();
                listDetailHistoryMinitestDTOs.remove(0);
            }

            if (listDetailHistoryMinitestDTOs.size() > 0) {
                for (DetailHistoryMinitestDTO detailHistoryMinitestDTO : listDetailHistoryMinitestDTOs) {
                    if (detailHistoryMinitestDTO.getCategoryId().equals(listDetailHistoryMinitestDTOs.get(0).getCategoryId())) {
                        listDetailHistoryMinitestDTOSChild2.add(detailHistoryMinitestDTO);
                    }
                }
                listDetailHistoryMinitestDTOs.subList(0, listDetailHistoryMinitestDTOSChild2.size() - 1).clear();
                listDetailHistoryMinitestDTOs.remove(0);
            }

            if (listDetailHistoryMinitestDTOs.size() > 0) {
                for (DetailHistoryMinitestDTO detailHistoryMinitestDTO : listDetailHistoryMinitestDTOs) {
                    if (detailHistoryMinitestDTO.getCategoryId().equals(listDetailHistoryMinitestDTOs.get(0).getCategoryId())) {
                        listDetailHistoryMinitestDTOSChild3.add(detailHistoryMinitestDTO);
                    }
                }
                listDetailHistoryMinitestDTOs.subList(0, listDetailHistoryMinitestDTOSChild3.size() - 1).clear();
                listDetailHistoryMinitestDTOs.remove(0);
            }

            if (listDetailHistoryMinitestDTOs.size() > 0) {
                for (DetailHistoryMinitestDTO detailHistoryMinitestDTO : listDetailHistoryMinitestDTOs) {
                    if (detailHistoryMinitestDTO.getCategoryId().equals(listDetailHistoryMinitestDTOs.get(0).getCategoryId())) {
                        listDetailHistoryMinitestDTOSChild4.add(detailHistoryMinitestDTO);
                    }
                }
                listDetailHistoryMinitestDTOs.subList(0, listDetailHistoryMinitestDTOSChild4.size() - 1).clear();
                listDetailHistoryMinitestDTOs.remove(0);
            }

            if (listDetailHistoryMinitestDTOs.size() > 0) {
                for (DetailHistoryMinitestDTO detailHistoryMinitestDTO : listDetailHistoryMinitestDTOs) {
                    if (detailHistoryMinitestDTO.getCategoryId().equals(listDetailHistoryMinitestDTOs.get(0).getCategoryId())) {
                        listDetailHistoryMinitestDTOSChild5.add(detailHistoryMinitestDTO);
                    }
                }
            }

            List<CategoryMinitestDTO> listCategoryMinitestDTOs = new ArrayList<>();

            //for đoạn 1
            CategoryMinitestDTO categoryMinitestDTO = new CategoryMinitestDTO();
            categoryMinitestDTO = categoryRepository.getCategoryById(listDetailHistoryMinitestDTOSChild1.get(0).getCategoryId());
            List<QuestionMinitestDTO> listQuestionMinitestDTOs = new ArrayList<>();
            for (DetailHistoryMinitestDTO detailHistoryMinitestDTO: listDetailHistoryMinitestDTOSChild1) {
                QuestionMinitestDTO questionMinitestDTO =
                        questionAnswerReadingRepository.getAnswerReadingById(detailHistoryMinitestDTO.getQuestionId());
                questionMinitestDTO.setIndexCorrectAnswer(detailHistoryMinitestDTO.getIndexCorrectAnswer());
                questionMinitestDTO.setIndexIncorrectAnswer(detailHistoryMinitestDTO.getIndexIncorrectAnswer());
                questionMinitestDTO.setStt(detailHistoryMinitestDTO.getStt());
                listQuestionMinitestDTOs.add(questionMinitestDTO);
            }

            categoryMinitestDTO.setListQuestionMinitestDTOS(listQuestionMinitestDTOs);
            listCategoryMinitestDTOs.add(categoryMinitestDTO);

            //for đoạn 2
            categoryMinitestDTO = new CategoryMinitestDTO();
            categoryMinitestDTO = categoryRepository.getCategoryById(listDetailHistoryMinitestDTOSChild2.get(0).getCategoryId());
            listQuestionMinitestDTOs = new ArrayList<>();
            for (DetailHistoryMinitestDTO detailHistoryMinitestDTO: listDetailHistoryMinitestDTOSChild2) {
                QuestionMinitestDTO questionMinitestDTO =
                        questionAnswerReadingRepository.getAnswerReadingById(detailHistoryMinitestDTO.getQuestionId());
                questionMinitestDTO.setIndexCorrectAnswer(detailHistoryMinitestDTO.getIndexCorrectAnswer());
                questionMinitestDTO.setIndexIncorrectAnswer(detailHistoryMinitestDTO.getIndexIncorrectAnswer());
                questionMinitestDTO.setStt(detailHistoryMinitestDTO.getStt());
                listQuestionMinitestDTOs.add(questionMinitestDTO);
            }

            categoryMinitestDTO.setListQuestionMinitestDTOS(listQuestionMinitestDTOs);
            listCategoryMinitestDTOs.add(categoryMinitestDTO);

            //for đoạn 3
            categoryMinitestDTO = new CategoryMinitestDTO();
            categoryMinitestDTO = categoryRepository.getCategoryById(listDetailHistoryMinitestDTOSChild3.get(0).getCategoryId());
            listQuestionMinitestDTOs = new ArrayList<>();
            for (DetailHistoryMinitestDTO detailHistoryMinitestDTO: listDetailHistoryMinitestDTOSChild3) {
                QuestionMinitestDTO questionMinitestDTO =
                        questionAnswerReadingRepository.getAnswerReadingById(detailHistoryMinitestDTO.getQuestionId());
                questionMinitestDTO.setIndexCorrectAnswer(detailHistoryMinitestDTO.getIndexCorrectAnswer());
                questionMinitestDTO.setIndexIncorrectAnswer(detailHistoryMinitestDTO.getIndexIncorrectAnswer());
                questionMinitestDTO.setStt(detailHistoryMinitestDTO.getStt());
                listQuestionMinitestDTOs.add(questionMinitestDTO);
            }

            categoryMinitestDTO.setListQuestionMinitestDTOS(listQuestionMinitestDTOs);
            listCategoryMinitestDTOs.add(categoryMinitestDTO);


            //for đoạn 4
            if(partName.equals(PART7)){
                if(listDetailHistoryMinitestDTOSChild4.size() > 0){
                    categoryMinitestDTO = new CategoryMinitestDTO();
                    categoryMinitestDTO = categoryRepository.getCategoryById(listDetailHistoryMinitestDTOSChild4.get(0).getCategoryId());
                    listQuestionMinitestDTOs = new ArrayList<>();
                    for (DetailHistoryMinitestDTO detailHistoryMinitestDTO: listDetailHistoryMinitestDTOSChild4) {
                        QuestionMinitestDTO questionMinitestDTO =
                                questionAnswerReadingRepository.getAnswerReadingById(detailHistoryMinitestDTO.getQuestionId());
                        questionMinitestDTO.setIndexCorrectAnswer(detailHistoryMinitestDTO.getIndexCorrectAnswer());
                        questionMinitestDTO.setIndexIncorrectAnswer(detailHistoryMinitestDTO.getIndexIncorrectAnswer());
                        questionMinitestDTO.setStt(detailHistoryMinitestDTO.getStt());
                        listQuestionMinitestDTOs.add(questionMinitestDTO);
                    }

                    categoryMinitestDTO.setListQuestionMinitestDTOS(listQuestionMinitestDTOs);
                    listCategoryMinitestDTOs.add(categoryMinitestDTO);


                    //for đoạn 5
//                    categoryMinitestDTO = new CategoryMinitestDTO();
//                    categoryMinitestDTO = categoryRepository.getCategoryById(listDetailHistoryMinitestDTOSChild5.get(0).getCategoryId());
//                    listQuestionMinitestDTOs = new ArrayList<>();
//                    for (DetailHistoryMinitestDTO detailHistoryMinitestDTO: listDetailHistoryMinitestDTOSChild5) {
//                        QuestionMinitestDTO questionMinitestDTO =
//                                questionAnswerReadingRepository.getAnswerReadingById(detailHistoryMinitestDTO.getQuestionId());
//                        questionMinitestDTO.setIndexCorrectAnswer(detailHistoryMinitestDTO.getIndexCorrectAnswer());
//                        questionMinitestDTO.setIndexIncorrectAnswer(detailHistoryMinitestDTO.getIndexIncorrectAnswer());
//                        questionMinitestDTO.setStt(detailHistoryMinitestDTO.getStt());
//                        listQuestionMinitestDTOs.add(questionMinitestDTO);
//                    }
//
//                    categoryMinitestDTO.setListQuestionMinitestDTOS(listQuestionMinitestDTOs);
//                    listCategoryMinitestDTOs.add(categoryMinitestDTO);
                }
            }


            minitestDTO.setListCategoryMinitestDTOS(listCategoryMinitestDTOs);
            minitestDTO.setPartName(partName);

            if(partName.equals(PART6)){
                minitestDTO.setTotalQuestion(cutTotal(historyMinitestDTO.getPart6(), 1));
                minitestDTO.setTotalCorectAnswer(cutTotal(historyMinitestDTO.getPart6(), 0));
            }
            if(partName.equals(PART7)){
                minitestDTO.setTotalQuestion(cutTotal(historyMinitestDTO.getPart7(), 1));
                minitestDTO.setTotalCorectAnswer(cutTotal(historyMinitestDTO.getPart7(), 0));
            }
            if(partName.equals(PART8)){
                minitestDTO.setTotalQuestion(cutTotal(historyMinitestDTO.getPart8(), 1));
                minitestDTO.setTotalCorectAnswer(cutTotal(historyMinitestDTO.getPart8(), 0));
            }

            minitestDTO.setListCategoryMinitestDTOS(listCategoryMinitestDTOs);
            minitestDTO.setTotalTime(historyMinitestDTO.getTotalTime());

            return minitestDTO;

        }

        return null;


    }
    /*end get detail history minitest*/

    /*tra list dap an ve cho web*/
    @Override
    public List<MinitestDTO> getListQuestionMinitestChoosenAnswer(MinitestSubmitAnswerDTO minitestSubmitAnswerDTO) {
        List<MinitestDTO> listMinitestDTOS = new ArrayList<>();
        //Bằng 0 thì thực hiện lấy chi tiết bài thi
        if(minitestSubmitAnswerDTO.getCheckDetail() == 0){
            listMinitestDTOS.add(getDetailHistoryMinitestByPart(minitestSubmitAnswerDTO.getMinitestId(), PART1));
            listMinitestDTOS.add(getDetailHistoryMinitestByPart(minitestSubmitAnswerDTO.getMinitestId(), PART2));
            listMinitestDTOS.add(getDetailHistoryMinitesByParttOther(minitestSubmitAnswerDTO.getMinitestId(), PART3));
            listMinitestDTOS.add(getDetailHistoryMinitesByParttOther(minitestSubmitAnswerDTO.getMinitestId(), PART4));
            listMinitestDTOS.add(getDetailHistoryMinitestByPart(minitestSubmitAnswerDTO.getMinitestId(), PART5));
            listMinitestDTOS.add(getDetailHistoryMinitesByParttOther(minitestSubmitAnswerDTO.getMinitestId(), PART6));
            listMinitestDTOS.add(getDetailHistoryMinitesByParttOther(minitestSubmitAnswerDTO.getMinitestId(), PART7));
            listMinitestDTOS.add(getDetailHistoryMinitesByParttOther(minitestSubmitAnswerDTO.getMinitestId(), PART8));
        }
        //bằng 1 thì thực hiện submit đáp án
        else{
            List<QuestionListeningChoosenDTO> listQuestionListeningChoosenDTOS = minitestSubmitAnswerDTO.getListQuestionListeningChoosenDTOS();
            List<QuestionReadingChoosenDTO> listQuestionReadingChoosenDTOS = minitestSubmitAnswerDTO.getListQuestionReadingChoosenDTOS();

            //get answer part1
            MinitestDTO minitestDTO = getQuestionAndAnswerMinitestByPart(listQuestionListeningChoosenDTOS,
                    listQuestionReadingChoosenDTOS, PART1, 1);
            listMinitestDTOS.add(minitestDTO);

            //get answer part2
            minitestDTO = getQuestionAndAnswerMinitestByPart(listQuestionListeningChoosenDTOS, listQuestionReadingChoosenDTOS,
                    PART2, 1);
            listMinitestDTOS.add(minitestDTO);

            //get answer part3
            minitestDTO = getQuestionAndAnswerMinitestByPartOther(listQuestionListeningChoosenDTOS,
                    listQuestionReadingChoosenDTOS, PART3, 1);
            listMinitestDTOS.add(minitestDTO);

            //get answer part4
            minitestDTO = getQuestionAndAnswerMinitestByPartOther(listQuestionListeningChoosenDTOS,
                    listQuestionReadingChoosenDTOS, PART4, 1);
            listMinitestDTOS.add(minitestDTO);

            //get answer part5
            minitestDTO = getQuestionAndAnswerMinitestByPart(listQuestionListeningChoosenDTOS,
                    listQuestionReadingChoosenDTOS, PART5, 1);
            listMinitestDTOS.add(minitestDTO);

            //get answer part6
            minitestDTO = getQuestionAndAnswerMinitestByPartOther(listQuestionListeningChoosenDTOS,
                    listQuestionReadingChoosenDTOS, PART6, 1);
            listMinitestDTOS.add(minitestDTO);

            //get answer part7
            minitestDTO = getQuestionAndAnswerMinitestByPartOther(listQuestionListeningChoosenDTOS,
                    listQuestionReadingChoosenDTOS, PART7, 1);
            listMinitestDTOS.add(minitestDTO);

            //get answer part8
            minitestDTO = getQuestionAndAnswerMinitestByPartOther(listQuestionListeningChoosenDTOS,
                    listQuestionReadingChoosenDTOS, PART8, 1);
            listMinitestDTOS.add(minitestDTO);
        }

        return listMinitestDTOS;
    }

    @Override
    public List<HistoryMinitestDTO> getListHistoryMinitest(HistoryMinitestDTO historyMinitestDTO){
        List<HistoryMinitestDTO> listHistoryMinitestDTOs =
                historyMinitestRepository.getListHistoryMinitestByUserId(historyMinitestDTO.getUserId());

        return listHistoryMinitestDTOs;
    }

    @Override
    public DataListDTO doSearch(HistoryMinitestDTO historyMinitestDTO) {

        if (historyMinitestDTO.getCreateFrom() != null) {
            String x = DateUtil.convertTimeDisplay(historyMinitestDTO.getCreateFrom());
            historyMinitestDTO.setCreateFromString(x);
        }
        if (historyMinitestDTO.getCreateTo() != null) {
            String x = DateUtil.convertTimeDisplay(historyMinitestDTO.getCreateTo());
            historyMinitestDTO.setCreateToString(x);
        }
        if (historyMinitestDTO.getCreateFrom() != null && historyMinitestDTO.getCreateTo() != null) {
            if (DateUtil.compareDate(historyMinitestDTO.getCreateFrom(), historyMinitestDTO.getCreateTo()) == 0) {
                throw new BusinessException("Thời điểm từ phải nhỏ hơn thời điểm đến");
            }
            historyMinitestDTO.setCreateFromString(DateUtil.convertTimeDisplay(historyMinitestDTO.getCreateFrom()));
            historyMinitestDTO.setCreateToString(DateUtil.convertTimeDisplay(historyMinitestDTO.getCreateTo()));
        }

        List<HistoryMinitestDTO> resultDataDTO = historyMinitestRepository.doSearch(historyMinitestDTO);

        DataListDTO dataListDTO = new DataListDTO();
        dataListDTO.setData(resultDataDTO);
        dataListDTO.setStart(1);
        dataListDTO.setSize(resultDataDTO.size());
        dataListDTO.setTotal(resultDataDTO.size());
        return dataListDTO;

    }

}

