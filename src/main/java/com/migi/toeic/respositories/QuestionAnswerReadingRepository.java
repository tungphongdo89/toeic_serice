package com.migi.toeic.respositories;

import com.migi.toeic.authen.model.RequestPractice;
import com.migi.toeic.base.ResultDataDTO;
import com.migi.toeic.dto.*;
import com.migi.toeic.model.QuestionAnswerReading;
import com.migi.toeic.respositories.common.HibernateRepository;
import com.migi.toeic.utils.ValidateUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.query.NativeQuery;
import org.hibernate.transform.Transformers;
import org.hibernate.type.FloatType;
import org.hibernate.type.LongType;
import org.hibernate.type.StringType;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;

@Transactional
@Repository
public class QuestionAnswerReadingRepository extends HibernateRepository<QuestionAnswerReading, Long> {

    @Transactional
    public ResultDataDTO getListQuestionReading(QuestionAnswersReadingDTO obj) {

        StringBuilder sql = new StringBuilder(
                "select ID as id," +
                        " NAME as name ," +
                        " ANSWER as answer," +
                        " PARENT_ID as parentId," +
                        " NUM_OF_ANSWER as numOfAnswer," +
                        " ANSWERS_TO_CHOOSE as answersToChoose ," +
                        " DESCRIPTION as description," +
                        " STATUS as status ," +
                        " SCORE as score " +
                        " from QUESTION_ANSWER_READING where 1=1 ");

        if (StringUtils.isNotEmpty(obj.getName())) {
            sql.append(" and upper(NAME) LIKE upper(:nameSearch) escape '&'  ");
        }

        if (obj.getParentId() != null) {
            sql.append(" and PARENT_ID = :parentIdSearch ");
        }

        if (obj.getStatus() != null) {
            sql.append(" and STATUS = :statusSearch ");
        }

        sql.append(" order by ID ");

        StringBuilder sqlCount = new StringBuilder("SELECT COUNT(*) FROM (");
        sqlCount.append(sql.toString());
        sqlCount.append(")");

        NativeQuery query = currentSession().createNativeQuery(sql.toString());
        NativeQuery queryCount = currentSession().createNativeQuery(sqlCount.toString());

        query.addScalar("id", new LongType());
        query.addScalar("name", new StringType());
        query.addScalar("answer", new StringType());
        query.addScalar("parentId", new LongType());
        query.addScalar("description", new StringType());
        query.addScalar("numOfAnswer", new LongType());
        query.addScalar("answersToChoose", new StringType());
        query.addScalar("status", new LongType());
        query.addScalar("score", new FloatType());

        query.setResultTransformer(Transformers.aliasToBean(QuestionAnswersReadingDTO.class));

        if (obj.getPage() != null && obj.getPageSize() != null) {
            query.setFirstResult((obj.getPage().intValue() - 1) * obj.getPageSize().intValue());
            query.setMaxResults(obj.getPageSize().intValue());
        }

        if (StringUtils.isNotEmpty(obj.getName())) {
            query.setParameter("nameSearch", "%" + ValidateUtils.validateKeySearch(obj.getName()) + "%");
            queryCount.setParameter("nameSearch", "%" + ValidateUtils.validateKeySearch(obj.getName()) + "%");
        }

        if (obj.getStatus() != null) {
            query.setParameter("statusSearch", obj.getStatus());
            queryCount.setParameter("statusSearch", obj.getStatus());
        }

        if (obj.getParentId() != null) {
            query.setParameter("categoryIdSearch", obj.getParentId());
            queryCount.setParameter("categoryIdSearch", obj.getParentId());
        }

        ResultDataDTO resultDataDTO = new ResultDataDTO();
        List<QuestionAnswersReadingDTO> lst = query.list();
        resultDataDTO.setData(lst);
        obj.setTotalRecord(((BigDecimal) queryCount.uniqueResult()).intValue());
        resultDataDTO.setTotal(obj.getTotalRecord());
        return resultDataDTO;
    }

    public QuestionAnswersReadingDTO getDetail(Long id) {
        StringBuilder sql = new StringBuilder(
                "select ID as id," +
                        " NAME as name ," +
                        " ANSWER as answer," +
                        " PARENT_ID as parentId," +
                        " NUM_OF_ANSWER as numOfAnswer," +
                        " ANSWERS_TO_CHOOSE as answersToChoose ," +
                        " DESCRIPTION as description," +
                        " STATUS as status ," +
                        " SCORE as score " +
                        " from QUESTION_ANSWER_READING where 1=1 and ID = :idSearch ");

        NativeQuery query = currentSession().createNativeQuery(sql.toString());

        query.addScalar("id", new LongType());
        query.addScalar("name", new StringType());
        query.addScalar("answer", new StringType());
        query.addScalar("parentId", new LongType());
        query.addScalar("description", new StringType());
        query.addScalar("numOfAnswer", new LongType());
        query.addScalar("answersToChoose", new StringType());
        query.addScalar("status", new LongType());
        query.addScalar("score", new FloatType());

        query.setResultTransformer(Transformers.aliasToBean(QuestionAnswersReadingDTO.class));

        query.setParameter("idSearch", id);

        return (QuestionAnswersReadingDTO) query.uniqueResult();
    }

    @Transactional
    public ResultDataDTO getListQuestionsOfRead(Long categoryId) {

        StringBuilder sql = new StringBuilder(
                "select ID as id," +
                        " NAME as name ," +
                        " PARENT_ID as parentId," +
                        " NUM_OF_ANSWER as numOfAnswer," +
                        " ANSWERS_TO_CHOOSE as answersToChoose ," +
//                        " DESCRIPTION as description," +
                        " STATUS as status ," +
                        " SCORE as score " +
                        " from QUESTION_ANSWER_READING where 1=1 ");

        if (categoryId != null) {
            sql.append(" AND PARENT_ID = :categoryId");
        }

        sql.append(" order by ID ");

        StringBuilder sqlCount = new StringBuilder("SELECT COUNT(*) FROM (");
        sqlCount.append(sql.toString());
        sqlCount.append(")");

        NativeQuery query = currentSession().createNativeQuery(sql.toString());
        NativeQuery queryCount = currentSession().createNativeQuery(sqlCount.toString());

        query.addScalar("id", new LongType());
        query.addScalar("name", new StringType());
        query.addScalar("parentId", new LongType());
//        query.addScalar("description", new StringType());
        query.addScalar("numOfAnswer", new LongType());
        query.addScalar("answersToChoose", new StringType());
        query.addScalar("status", new LongType());
        query.addScalar("score", new FloatType());

        query.setResultTransformer(Transformers.aliasToBean(QuestionAnswersReadingDTO.class));

        if (categoryId != null) {
            query.setParameter("categoryId", categoryId);
            queryCount.setParameter("categoryId", categoryId);
        }

        ResultDataDTO resultDataDTO = new ResultDataDTO();
        List<QuestionAnswersReadingDTO> lst = query.list();
        resultDataDTO.setData(lst);
        resultDataDTO.setTotal(((BigDecimal) queryCount.uniqueResult()).intValue());
        return resultDataDTO;
    }

    public void deleteQuestionByCategory(Long categoryId) {
        StringBuilder sql = new StringBuilder(
                "DELETE FROM question_answer_reading WHERE parent_id = :categoryId ");

        NativeQuery query = currentSession().createNativeQuery(sql.toString());

        query.setResultTransformer(Transformers.aliasToBean(QuestionAnswersReadingDTO.class));

        query.setParameter("categoryId", categoryId);
        query.executeUpdate();
    }

    public List<QuestionAnswersReadingDTO> getListQuestionsOfReadWordFill(String topicName, String levelCode, int number) {
        StringBuilder sql = new StringBuilder("select * from ( " +
                "                   select qa.ID id,qa.NAME name, " +
                "                   qa.ANSWERS_TO_CHOOSE answersToChoose " +
                "                   from TOPICS t left join CATEGORY c on t.ID = c.PARENT_ID " +
                "                   inner join QUESTION_ANSWER_READING qa on qa.PARENT_ID = c.ID " +
                "                   where t.STATUS = 1 and qa.STATUS = 1 " +
                "                   and c.TYPE_CODE =1 and upper(t.NAME) =upper(:topicName) and c.level_code =:levelCode " +
                "                   order by DBMS_RANDOM.RANDOM ) where rownum <= :number  ");
        NativeQuery query = currentSession().createNativeQuery(sql.toString());
        query.addScalar("id", new LongType());
        query.addScalar("name", new StringType());
        query.addScalar("answersToChoose", new StringType());
        query.setResultTransformer(Transformers.aliasToBean(QuestionAnswersReadingDTO.class));
        query.setParameter("topicName", topicName);
        query.setParameter("levelCode", levelCode);
        query.setParameter("number", number);

        List<QuestionAnswersReadingDTO> lstQuest = query.list();
        return lstQuest;
    }

    public QuestionAnswersDTO getResultQuestionOfReadWordFill(QuestionAnswersReadingDTO questionAnswersReadingDTO) {
        StringBuilder sql = new StringBuilder("select qa.id id, " +
                "qa.name name, qa.answer answer, qa.answers_to_choose answersToChoose, qa.description description, qa.score score " +
                "from QUESTION_ANSWER_READING qa where id =:id ");
        NativeQuery query = currentSession().createNativeQuery(sql.toString());
        query.addScalar("id", new LongType());
        query.addScalar("name", new StringType());
        query.addScalar("answer", new StringType());
        query.addScalar("answersToChoose", new StringType());
        query.addScalar("description", new StringType());
        query.addScalar("score", new FloatType());
        query.setResultTransformer(Transformers.aliasToBean(QuestionAnswersDTO.class));
        query.setParameter("id", questionAnswersReadingDTO.getId());

        return (QuestionAnswersDTO) query.uniqueResult();
    }


    public List<CategoryDTO> getRandomReadingCateWithLimitLengthQuest(Long topicId, String part, String levelCode, Integer amount) {
        StringBuilder sql = new StringBuilder(
                "SELECT " +
                        " ID AS categoryId, " +
                        " PATH_FILE_1 AS pathFile1, " +
                        " PATH_FILE_2 AS pathFile2, " +
                        " PATH_FILE_3 AS pathFile3, " +
                        " TYPE_FILE_3 as typeFile3, " +
                        " TYPE_FILE_1 as typeFile1, " +
                        " TYPE_FILE_2 as typeFile2, " +
                        " TRANSCRIPT AS transcript  " +
                        "FROM " +
                        " ( " +
                        " SELECT " +
                        "  CATEGORY.ID, " +
                        "  CATEGORY.PATH_FILE_1, " +
                        "  CATEGORY.PATH_FILE_2, " +
                        "  CATEGORY.PATH_FILE_3, " +
                        " CATEGORY.TYPE_FILE_3, " +
                        " CATEGORY.TYPE_FILE_1, " +
                        " CATEGORY.TYPE_FILE_2, " +
                        "  CATEGORY.TRANSCRIPT  " +
                        " FROM " +
                        "  CATEGORY " +
                        "  INNER JOIN TOPICS ON CATEGORY.PARENT_ID = TOPICS.ID  " +
                        " WHERE " +
                        "  TOPICS.PART_TOPIC_CODE = :part  " +
                        "  AND CATEGORY.LEVEL_CODE = :levelCode  " +
                        "  AND CATEGORY.ID IN ( " +
                        "  SELECT " +
                        "   *  " +
                        "  FROM " +
                        "   ( " +
                        "   SELECT " +
                        "    CATEGORY.ID  " +
                        "   FROM " +
                        "    QUESTION_ANSWER_READING " +
                        "    INNER JOIN CATEGORY ON QUESTION_ANSWER_READING.PARENT_ID = CATEGORY.ID " +
                        "    INNER JOIN TOPICS ON TOPICS.ID = CATEGORY.PARENT_ID  " +
                        "   WHERE " +
                        "    TOPICS.ID = :topicId AND CATEGORY.STATUS = 1 " +
                        "   )  " +
                        "  )  " +
                        " ORDER BY " +
                        "  DBMS_RANDOM.RANDOM  " +
                        " )  " +
                        "WHERE " +
                        " ROWNUM <= :amount"
        );
        NativeQuery query = currentSession().createNativeQuery(sql.toString());
        query.addScalar("categoryId", new LongType());
        query.addScalar("pathFile1", new StringType());
        query.addScalar("pathFile2", new StringType());
        query.addScalar("pathFile3", new StringType());
        query.addScalar("typeFile1", new StringType());
        query.addScalar("typeFile2", new StringType());
        query.addScalar("typeFile3", new StringType());
        query.addScalar("transcript", new StringType());
        query.setResultTransformer(Transformers.aliasToBean(CategoryDTO.class));
        query.setParameter("part", part);
        query.setParameter("topicId", topicId);
        query.setParameter("levelCode", levelCode);
        query.setParameter("amount", amount);

        List<CategoryDTO> dtoList = query.list();

        return dtoList;
    }

    public List<QuestionAnswersDTO> getListQuestionByCategoryId(Long categoryId) {
        StringBuilder sql = new StringBuilder(
                "SELECT " +
                        " QUESTION_ANSWER_READING.ID id, " +
                        " QUESTION_ANSWER_READING.NAME name, " +
                        " QUESTION_ANSWER_READING.NUM_OF_ANSWER numOfAnswer, " +
                        " QUESTION_ANSWER_READING.STATUS status, " +
                        " QUESTION_ANSWER_READING.ANSWERS_TO_CHOOSE answersToChoose, " +
                        " QUESTION_ANSWER_READING.SCORE score " +
                        "FROM " +
                        " QUESTION_ANSWER_READING " +
                        " INNER JOIN CATEGORY ON CATEGORY.ID = QUESTION_ANSWER_READING.PARENT_ID  " +
                        "WHERE " +
                        " CATEGORY.ID = :categoryId");
        NativeQuery query = currentSession().createNativeQuery(sql.toString());
        query.addScalar("id",new LongType());
        query.addScalar("name",new StringType());
        query.addScalar("numOfAnswer", new LongType());
        query.addScalar("status", new LongType());
        query.addScalar("answersToChoose",new StringType());
        query.addScalar("score",new FloatType());
        query.setResultTransformer(Transformers.aliasToBean(QuestionAnswersDTO.class));
        query.setParameter("categoryId",categoryId);
        return query.list();
    }

    public List<QuestionAnswersDTO> submitListQuestionByCategoryId(Long categoryId) {
        StringBuilder sql = new StringBuilder(
                "SELECT " +
                        " QUESTION_ANSWER_READING.ID id, " +
                        " QUESTION_ANSWER_READING.NAME name, " +
                        " QUESTION_ANSWER_READING.ANSWER answer, " +
                        " QUESTION_ANSWER_READING.NUM_OF_ANSWER numOfAnswer, " +
                        " QUESTION_ANSWER_READING.STATUS status, " +
                        " QUESTION_ANSWER_READING.ANSWERS_TO_CHOOSE answersToChoose, " +
                        " QUESTION_ANSWER_READING.DESCRIPTION description, " +
						"QUESTION_ANSWER_READING.SCORE score, " +
                        " CATEGORY.ID categoryId, " +
                        " QUESTION_ANSWER_READING.TRANSLATING_QUESTION translatingQuestion, " +
                        " QUESTION_ANSWER_READING.TRANSLATING_QUES_A translatingQuesA, " +
                        " QUESTION_ANSWER_READING.TRANSLATING_QUES_B translatingQuesB, " +
                        " QUESTION_ANSWER_READING.TRANSLATING_QUES_C translatingQuesC, " +
                        " QUESTION_ANSWER_READING.TRANSLATING_QUES_D translatingQuesD " +                        "FROM " +
                        " QUESTION_ANSWER_READING " +
                        " INNER JOIN CATEGORY ON CATEGORY.ID = QUESTION_ANSWER_READING.PARENT_ID  " +
                        "WHERE " +
                        " CATEGORY.ID = :categoryId");
        NativeQuery query = currentSession().createNativeQuery(sql.toString());
        query.addScalar("id",new LongType());
        query.addScalar("name",new StringType());
        query.addScalar("answer",new StringType());
        query.addScalar("numOfAnswer", new LongType());
        query.addScalar("status", new LongType());
        query.addScalar("answersToChoose",new StringType());
        query.addScalar("description",new StringType());
        query.addScalar("score",new FloatType());
		query.addScalar("categoryId",new LongType());
        query.addScalar("translatingQuestion",new StringType());
        query.addScalar("translatingQuesA",new StringType());
        query.addScalar("translatingQuesB",new StringType());
        query.addScalar("translatingQuesC",new StringType());
        query.addScalar("translatingQuesD",new StringType());        query.setResultTransformer(Transformers.aliasToBean(QuestionAnswersDTO.class));
        query.setParameter("categoryId",categoryId);
        return query.list();
    }

    //for minitest
    public List<QuestionMinitestDTO> getListQuestionByCategoryIdForMinitest(Long categoryId){
        StringBuilder sql = new StringBuilder(
                "SELECT \n" +
                        " QUESTION_ANSWER_READING.ID id,\n" +
                        " QUESTION_ANSWER_READING.NAME name,\n" +
                        " QUESTION_ANSWER_READING.ANSWERS_TO_CHOOSE answersToChoose,\n" +
                        " QUESTION_ANSWER_READING.ANSWER answer,\n" +
                        " QUESTION_ANSWER_READING.SCORE score, \n" +
                        " QUESTION_ANSWER_READING.PARENT_ID parentId\n" +
                        " FROM QUESTION_ANSWER_READING \n" +
                        " WHERE QUESTION_ANSWER_READING.PARENT_ID = :categoryId");

        NativeQuery query = currentSession().createNativeQuery(sql.toString());
        query.addScalar("id",new LongType());
        query.addScalar("name",new StringType());
        query.addScalar("answersToChoose",new StringType());
        query.addScalar("answer",new StringType());
        query.addScalar("score", new FloatType());
        query.addScalar("parentId",new LongType());
        query.setResultTransformer(Transformers.aliasToBean(QuestionMinitestDTO.class));
        query.setParameter("categoryId",categoryId);
        return query.list();
    }

    public QuestionMinitestDTO getAnswerReadingById(Long id){
        StringBuilder sql = new StringBuilder(
                " SELECT \n" +
                        " QUESTION_ANSWER_READING.ID id,\n" +
                        " QUESTION_ANSWER_READING.NAME name,\n" +
                        " QUESTION_ANSWER_READING.ANSWER answer,\n" +
                        " QUESTION_ANSWER_READING.ANSWERS_TO_CHOOSE answersToChoose,\n" +
                        " QUESTION_ANSWER_READING.SCORE score,\n" +
                        " QUESTION_ANSWER_READING.DESCRIPTION description,\n" +
                        " QUESTION_ANSWER_READING.TRANSLATING_QUESTION translatingQuestion,\n" +
                        " QUESTION_ANSWER_READING.TRANSLATING_QUES_A translatingQuesA,\n" +
                        " QUESTION_ANSWER_READING.TRANSLATING_QUES_B translatingQuesB,\n" +
                        " QUESTION_ANSWER_READING.TRANSLATING_QUES_C translatingQuesC,\n" +
                        " QUESTION_ANSWER_READING.TRANSLATING_QUES_D translatingQuesD,\n" +
                        " QUESTION_ANSWER_READING.PARENT_ID parentId\n" +
                        " FROM QUESTION_ANSWER_READING \n" +
                        " WHERE QUESTION_ANSWER_READING.ID = :id");

        NativeQuery query = currentSession().createNativeQuery(sql.toString());
        query.addScalar("id",new LongType());
        query.addScalar("name",new StringType());
        query.addScalar("answer",new StringType());
        query.addScalar("answersToChoose",new StringType());
        query.addScalar("score",new FloatType());
        query.addScalar("description",new StringType());
        query.addScalar("translatingQuestion",new StringType());
        query.addScalar("translatingQuesA",new StringType());
        query.addScalar("translatingQuesB",new StringType());
        query.addScalar("translatingQuesC",new StringType());
        query.addScalar("translatingQuesD",new StringType());
        query.addScalar("parentId",new LongType());
        query.setResultTransformer(Transformers.aliasToBean(QuestionMinitestDTO.class));
        query.setParameter("id",id);
        return (QuestionMinitestDTO) query.uniqueResult();
    }
    //end for minitest

    public List<QuestionMinitestDTO> getQuestionReadingByCategoryId(Long categoryId, Long testId){
        StringBuilder sql = new StringBuilder(
                "SELECT " +
                        "QUESTION_ANSWER_READING.ID id, " +
                        "QUESTION_ANSWER_READING.NAME name, " +
                        "QUESTION_ANSWER_READING.ANSWERS_TO_CHOOSE answersToChoose," +
                        "QUESTION_ANSWER_READING.num_of_answer numberOfAnswer," +
                        "QUESTION_ANSWER_READING.sentence_no sentenceNo " +
                        " FROM QUESTION_ANSWER_READING " +
                        " WHERE QUESTION_ANSWER_READING.PARENT_ID = :categoryId" +
                        " order by sentenceNo");

        NativeQuery query = currentSession().createNativeQuery(sql.toString());
        query.addScalar("id",new LongType());
        query.addScalar("name",new StringType());
        query.addScalar("answersToChoose",new StringType());
        query.addScalar("numberOfAnswer",new LongType());
        query.addScalar("sentenceNo",new LongType());
        query.setResultTransformer(Transformers.aliasToBean(QuestionMinitestDTO.class));
        query.setParameter("categoryId",categoryId);

        return  query.list();
    }

    public QuestionMinitestDTO getResultQuestionReadingById(Long id){
        StringBuilder sql = new StringBuilder(
                "SELECT " +
                        "QUESTION_ANSWER_READING.ID id, " +
                        "QUESTION_ANSWER_READING.NAME name, " +
                        "QUESTION_ANSWER_READING.ANSWERS_TO_CHOOSE answersToChoose, "+
                        "QUESTION_ANSWER_READING.ANSWER answer, " +
                        "QUESTION_ANSWER_READING.DESCRIPTION description," +
                        "QUESTION_ANSWER_READING.TRANSLATING_QUESTION translatingQuestion," +
                        "QUESTION_ANSWER_READING.TRANSLATING_QUES_A translatingQuesA," +
                        "QUESTION_ANSWER_READING.TRANSLATING_QUES_B translatingQuesB," +
                        "QUESTION_ANSWER_READING.TRANSLATING_QUES_C translatingQuesC," +
                        "QUESTION_ANSWER_READING.TRANSLATING_QUES_D translatingQuesD,  " +
                        "QUESTION_ANSWER_READING.sentence_no sentenceNo  " +
                        " FROM QUESTION_ANSWER_READING " +
                        " WHERE QUESTION_ANSWER_READING.ID = :id");

        NativeQuery query = currentSession().createNativeQuery(sql.toString());
        query.addScalar("id",new LongType());
        query.addScalar("name",new StringType());
        query.addScalar("answersToChoose",new StringType());
        query.addScalar("answer",new StringType());
        query.addScalar("description",new StringType());
        query.addScalar("translatingQuestion",new StringType());
        query.addScalar("translatingQuesA",new StringType());
        query.addScalar("translatingQuesB",new StringType());
        query.addScalar("translatingQuesC",new StringType());
        query.addScalar("translatingQuesD",new StringType());
        query.addScalar("sentenceNo",new LongType());
        query.setResultTransformer(Transformers.aliasToBean(QuestionMinitestDTO.class));
        query.setParameter("id",id);

        return (QuestionMinitestDTO) query.uniqueResult();
    }

    public QuestionMinitestDTO getQuestionReadingHistory(Long id, String partName, Long idHistory){
        StringBuilder sql = new StringBuilder(
                "SELECT " +
                        "QUESTION_ANSWER_READING.ID id, " +
                        "QUESTION_ANSWER_READING.NAME name, " +
                        "QUESTION_ANSWER_READING.ANSWERS_TO_CHOOSE answersToChoose, "+
                        "QUESTION_ANSWER_READING.ANSWER answer, " +
                        "QUESTION_ANSWER_READING.DESCRIPTION description, " +
                        "QUESTION_ANSWER_READING.TRANSLATING_QUESTION translatingQuestion, " +
                        "QUESTION_ANSWER_READING.TRANSLATING_QUES_A translatingQuesA, " +
                        "QUESTION_ANSWER_READING.TRANSLATING_QUES_B translatingQuesB, " +
                        "QUESTION_ANSWER_READING.TRANSLATING_QUES_C translatingQuesC, " +
                        "QUESTION_ANSWER_READING.TRANSLATING_QUES_D translatingQuesD," +
                        "QUESTION_ANSWER_READING.sentence_no sentenceNo,  " +
                        "DETAILS_HISTORY.INDEX_CORRECT indexCorrectAnswer," +
                        "DETAILS_HISTORY.INDEX_INCORRECT indexIncorrectAnswer " +
                        "FROM QUESTION_ANSWER_READING inner join DETAILS_HISTORY " +
                        "ON QUESTION_ANSWER_READING.ID = DETAILS_HISTORY.QUESTION_ID " +
                        "AND DETAILS_HISTORY.QUESTION_ID =:id AND DETAILS_HISTORY.PART = :partName AND DETAILS_HISTORY.PARENT_ID =:idHistory ");

        NativeQuery query = currentSession().createNativeQuery(sql.toString());
        query.addScalar("id",new LongType());
        query.addScalar("name",new StringType());
        query.addScalar("answersToChoose",new StringType());
        query.addScalar("answer",new StringType());
        query.addScalar("description",new StringType());
        query.addScalar("translatingQuestion",new StringType());
        query.addScalar("translatingQuesA",new StringType());
        query.addScalar("translatingQuesB",new StringType());
        query.addScalar("translatingQuesC",new StringType());
        query.addScalar("translatingQuesD",new StringType());
        query.addScalar("sentenceNo",new LongType());
        query.addScalar("indexCorrectAnswer",new LongType());
        query.addScalar("indexIncorrectAnswer",new LongType());
        query.setResultTransformer(Transformers.aliasToBean(QuestionMinitestDTO.class));
        query.setParameter("id",id);
        query.setParameter("partName",partName);
        query.setParameter("idHistory",idHistory);
        return (QuestionMinitestDTO) query.uniqueResult();
    }

    public QuestionAnswersDTO getDetailById(Long id) {
        StringBuilder sql = new StringBuilder(
                "select ID as id," +
                        " NAME as name ," +
                        " ANSWER as answer," +
                        " PARENT_ID as parentId," +
                        " NUM_OF_ANSWER as numOfAnswer," +
                        " ANSWERS_TO_CHOOSE as answersToChoose ," +
                        " DESCRIPTION as description," +
                        " STATUS as status ," +
                        " SCORE as score," +
                        " TRANSLATING_QUESTION as translatingQuestion," +
                        " TRANSLATING_QUES_A as translatingQuesA," +
                        " TRANSLATING_QUES_B as translatingQuesB," +
                        " TRANSLATING_QUES_C as translatingQuesC," +
                        " TRANSLATING_QUES_D as translatingQuesD  " +
                        " from QUESTION_ANSWER_READING where 1=1 and ID = :id ");

        NativeQuery query = currentSession().createNativeQuery(sql.toString());

        query.addScalar("id", new LongType());
        query.addScalar("name", new StringType());
        query.addScalar("answer", new StringType());
        query.addScalar("parentId", new LongType());
        query.addScalar("description", new StringType());
        query.addScalar("numOfAnswer", new LongType());
        query.addScalar("answersToChoose", new StringType());
        query.addScalar("status", new LongType());
        query.addScalar("score", new FloatType());
        query.addScalar("translatingQuestion", new StringType());
        query.addScalar("translatingQuesA", new StringType());
        query.addScalar("translatingQuesB", new StringType());
        query.addScalar("translatingQuesC", new StringType());
        query.addScalar("translatingQuesD", new StringType());

        query.setResultTransformer(Transformers.aliasToBean(QuestionAnswersDTO.class));

        query.setParameter("id", id);

        return (QuestionAnswersDTO) query.uniqueResult();
    }

    // for deleting cateogry
    public List<QuestionAnswersReadingDTO> getListQuetionByCategoryId(Long parentId){
        StringBuilder sql = new StringBuilder(
                "select id from question_answer_reading where parent_id =:parentId "
        );

        NativeQuery query = currentSession().createNativeQuery(sql.toString());
        query.addScalar("id",new LongType());
        query.setResultTransformer(Transformers.aliasToBean(QuestionAnswersReadingDTO.class));
        query.setParameter("parentId",parentId);
        return query.list();
    }
}
