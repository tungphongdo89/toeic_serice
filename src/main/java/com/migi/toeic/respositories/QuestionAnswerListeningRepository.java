package com.migi.toeic.respositories;

import com.migi.toeic.authen.model.RequestPractice;
import com.migi.toeic.base.ResultDataDTO;
import com.migi.toeic.dto.*;
import com.migi.toeic.model.QuestionAnswerListening;
import com.migi.toeic.respositories.common.HibernateRepository;
import com.migi.toeic.utils.ValidateUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.query.NativeQuery;
import org.hibernate.transform.Transformers;
import org.hibernate.type.*;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Transactional
@Repository
public class QuestionAnswerListeningRepository extends HibernateRepository<QuestionAnswerListening, Long> {

    @Transactional
    public ResultDataDTO getListQuestionReading(QuestionAnswerListeningDTO obj) {

        StringBuilder sql = new StringBuilder(
                "select ID as id," +
                        " NAME as name ," +
                        " ANSWER as answer," +
                        " PARENT_ID as parentId," +
                        " NUM_OF_ANSWER as numOfAnswer," +
                        " ANSWERS_TO_CHOOSE as answersToChoose ," +
                        " DESCRIPTION as description," +
                        " STATUS as status ," +
                        " START_TIME as startTime , " +
                        " END_TIME as endTime , " +
                        " SCORE as score " +
                        " from QUESTION_ANSWER_LISTENING where 1=1 ");

        if (StringUtils.isNotEmpty( obj.getName() )) {
            sql.append( " and upper(NAME) LIKE upper(:nameSearch) escape '&'  " );
        }

        if (obj.getParentId() != null) {
            sql.append( " and PARENT_ID = :parentIdSearch " );
        }

        if (obj.getStatus() != null) {
            sql.append( " and STATUS = :statusSearch " );
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
        query.addScalar("startTime", new StringType());
        query.addScalar("endTime", new TimestampType());
        query.addScalar("score", new FloatType());

        query.setResultTransformer(Transformers.aliasToBean(QuestionAnswerListeningDTO.class));

        if (obj.getPage() != null && obj.getPageSize() != null) {
            query.setFirstResult((obj.getPage().intValue() - 1) * obj.getPageSize().intValue());
            query.setMaxResults(obj.getPageSize().intValue());
        }

        if (StringUtils.isNotEmpty( obj.getName() )) {
            query.setParameter( "nameSearch", "%" + ValidateUtils.validateKeySearch(obj.getName()) + "%" );
            queryCount.setParameter( "nameSearch", "%" + ValidateUtils.validateKeySearch(obj.getName()) + "%" );
        }

        if (obj.getStatus() != null) {
            query.setParameter( "statusSearch", obj.getStatus() );
            queryCount.setParameter( "statusSearch", obj.getStatus() );
        }

        if (obj.getParentId() != null) {
            query.setParameter( "categoryIdSearch", obj.getParentId() );
            queryCount.setParameter( "categoryIdSearch", obj.getParentId() );
        }

        ResultDataDTO resultDataDTO = new ResultDataDTO();
        List<QuestionAnswerListeningDTO> lst = query.list();
        resultDataDTO.setData(lst);
        obj.setTotalRecord(((BigDecimal) queryCount.uniqueResult()).intValue());
        resultDataDTO.setTotal(obj.getTotalRecord());
        return resultDataDTO;
    }


    public QuestionAnswerListeningDTO getDetail(Long id){
        StringBuilder sql = new StringBuilder(
                "select ID as id," +
                        " NAME as name ," +
                        " ANSWER as answer," +
                        " PARENT_ID as parentId," +
                        " NUM_OF_ANSWER as numOfAnswer," +
                        " ANSWERS_TO_CHOOSE as answersToChoose ," +
                        " DESCRIPTION as description," +
                        " STATUS as status ," +
                        " START_TIME as startTime , " +
                        " END_TIME as endTime , " +
                        " SCORE as score " +
                        " from QUESTION_ANSWER_LISTENING where 1=1 and ID = :idSearch ");

        NativeQuery query = currentSession().createNativeQuery(sql.toString());

        query.addScalar("id", new LongType());
        query.addScalar("name", new StringType());
        query.addScalar("answer", new StringType());
        query.addScalar("parentId", new LongType());
        query.addScalar("description", new StringType());
        query.addScalar("numOfAnswer", new LongType());
        query.addScalar("answersToChoose", new StringType());
        query.addScalar("status", new LongType());
        query.addScalar("startTime", new StringType());
        query.addScalar("endTime", new TimestampType());
        query.addScalar("score", new FloatType());

        query.setResultTransformer(Transformers.aliasToBean(QuestionAnswerListeningDTO.class));

        query.setParameter( "idSearch", id );

        return (QuestionAnswerListeningDTO) query.uniqueResult();
    }

    public void deleteQuestionByCategory(Long categoryId){
        StringBuilder sql = new StringBuilder(
                "DELETE FROM question_answer_listening WHERE parent_id = :categoryId ");

        NativeQuery query = currentSession().createNativeQuery(sql.toString());

        query.setResultTransformer(Transformers.aliasToBean(QuestionAnswerListeningDTO.class));

        query.setParameter( "categoryId", categoryId );
        query.executeUpdate();
    }

    public List<QuestionAnswerListeningDTO> listMultiQuestionListenBytotal(Long topicId , int totalRecord , String levelCode,Long Qid){
        StringBuilder sql = new StringBuilder(
                "SELECT * FROM (SELECT \n" +
                        "ql.ID as id,\n" +
                        "ql.NAME as name,\n" +
                        "ql.TYPE_FILE_1 typeFile1,\n" +
                        "ql.START_TIME startTime,\n" +
                        "ql.TYPE_FILE_2 typeFile2,\n" +
                        "ql.PART_FILE_1 pathFile1,\n" +
                        "ql.PART_FILE_2 pathFile2,\n" +
                        " ql.PATH_FILE_QUES pathFileQues, \n" +
                        " ql.PATH_FILE_QUES_A pathFileQuesA, \n" +
                        " ql.PATH_FILE_QUES_B pathFileQuesB, \n" +
                        " ql.PATH_FILE_QUES_C pathFileQuesC, \n" +
                        " ql.PATH_FILE_QUES_D pathFileQuesD, \n" +
                        " t.PART_TOPIC_CODE part, \n"

        );
        if(Qid != null){
            sql.append(
                    "ql.ANSWER answer,\n" +
                    "ql.DESCRIPTION description,\n" +
                    "ql.TRANSCRIPT transcript,\n"
            );
        }
        sql.append("ql.ANSWERS_TO_CHOOSE answersToChoose, " +
                "ql.SCORE score " +
                "FROM QUESTION_ANSWER_LISTENING ql INNER JOIN  CATEGORY c\n" +
                "\t\t\ton ql.PARENT_ID = c.ID\n" +
                "\t\t\tINNER JOIN TOPICS t\n" +
                "\t\t\ton t.ID = c.PARENT_ID\n" +
                "WHERE t.ID = :topicId and c.TYPE_CODE = 2 \n");
        if(Qid != null){
            sql.append("and ql.ID = :Qid \n");
        }else{
            sql.append("and c.LEVEL_CODE = :levelCode \n");
        }
        sql.append("ORDER BY DBMS_RANDOM.RANDOM\n" +
                ") where ROWNUM <= :totalRecord");
        NativeQuery query = currentSession().createNativeQuery(sql.toString());
        query.addScalar("id",new LongType());
        query.addScalar("name",new StringType());
        query.addScalar("typeFile1",new StringType());
        query.addScalar("startTime",new StringType());
        query.addScalar("typeFile2",new StringType());
        query.addScalar("pathFile1",new StringType());
        query.addScalar("pathFile2",new StringType());
        query.addScalar("answersToChoose",new StringType());

        query.addScalar("pathFileQues",new StringType());
        query.addScalar("pathFileQuesA",new StringType());
        query.addScalar("pathFileQuesB",new StringType());
        query.addScalar("pathFileQuesC",new StringType());
        query.addScalar("pathFileQuesD",new StringType());
        query.addScalar("part",new StringType());
        query.addScalar("score", new FloatType());
        if(Qid != null){
            query.addScalar("answer", new StringType());
            query.addScalar("description",new StringType());
            query.addScalar("transcript",new StringType());
        }
        query.setResultTransformer(Transformers.aliasToBean(QuestionAnswerListeningDTO.class));
        query.setParameter( "topicId", topicId );
        query.setParameter( "totalRecord", totalRecord );
        if(Qid != null){
            query.setParameter("Qid",Qid);
        }else{
            query.setParameter( "levelCode", levelCode );
        }

        List<QuestionAnswerListeningDTO> lstQuest = query.list();
        return lstQuest;
    }

    public List<QuestionAnswerListeningDTO> getListQuestionOfListenAndFill(String topicName, String levelCode, int number){
        StringBuilder sql = new StringBuilder(
                "SELECT * FROM (SELECT " +
                        "ql.ID id, " +
                        "ql.NAME name, " +
                        "ql.TYPE_FILE_1 typeFile1, " +
                        "ql.TYPE_FILE_2 typeFile2, " +
                        "ql.PART_FILE_1 pathFile1, " +
                        "ql.PART_FILE_2 pathFile2, " +
                        "ql.ANSWERS_TO_CHOOSE answersToChoose," +
                        "ql.START_TIME startTime," +
                        "c.path_file_1 pathFileCategory1," +
                        "c.path_file_2 pathFileCategory2 " +
                        "FROM QUESTION_ANSWER_LISTENING ql INNER JOIN  CATEGORY c " +
                        "on ql.PARENT_ID = c.ID " +
                        "INNER JOIN TOPICS t " +
                        "on t.ID = c.PARENT_ID " +
                        "WHERE upper(t.NAME) =upper(:topicName) and c.LEVEL_CODE = :levelCode and c.type_code = 5 " +
                        "ORDER BY DBMS_RANDOM.RANDOM " +
                        ") where ROWNUM <= :number"
        );
        NativeQuery query = currentSession().createNativeQuery(sql.toString());
        query.addScalar("id",new LongType());
        query.addScalar("name",new StringType());
        query.addScalar("typeFile1",new StringType());
        query.addScalar("typeFile2",new StringType());
        query.addScalar("pathFile1",new StringType());
        query.addScalar("pathFile2",new StringType());
        query.addScalar("answersToChoose",new StringType());
        query.addScalar("startTime", new StringType());
        query.addScalar("pathFileCategory1", new StringType());
        query.addScalar("pathFileCategory2", new StringType());
        query.setResultTransformer(Transformers.aliasToBean(QuestionAnswerListeningDTO.class));
        query.setParameter( "topicName", topicName );
        query.setParameter( "levelCode", levelCode );
        query.setParameter( "number", number );

        List<QuestionAnswerListeningDTO> lstQuest = query.list();
        return lstQuest;
    }

    public QuestionAnswersDTO getResultQuestionOfListenWordFill(QuestionAnswerListeningDTO questionAnswerListeningDTO){
        StringBuilder sql = new StringBuilder("select qa.id id, " +
                "qa.name name, " +
                "qa.answer answer, " +
                "qa.answers_to_choose answersToChoose, " +
                "qa.start_time startTime, " +
                "qa.end_time endTime, " +
                "qa.description description, " +
                "qa.score score, " +
                "qa.type_file_1 typeFile1, " +
                "qa.type_file_2 typeFile2, " +
                "qa.part_file_1 pathFile1, " +
                "qa.part_file_2 pathFile2, " +
                "qa.parent_id parentId," +
                "qa.translating_question translatingQuestion," +
                "qa.translating_ques_a translatingQuesA," +
                "qa.translating_ques_b translatingQuesB," +
                "qa.translating_ques_c translatingQuesC," +
                "qa.translating_ques_d translatingQuesD," +
                "qa.PATH_FILE_QUES_A pathFileQuesA, " +
                "qa.PATH_FILE_QUES_B pathFileQuesB, " +
                "qa.PATH_FILE_QUES_C pathFileQuesC, " +
                "qa.PATH_FILE_QUES_D pathFileQuesD, " +
                "qa.PATH_FILE_QUES pathFileQues " +
                "from QUESTION_ANSWER_LISTENING qa where id =:id");
        NativeQuery query = currentSession().createNativeQuery(sql.toString());
        query.addScalar("id", new LongType());
        query.addScalar("name", new StringType());
        query.addScalar("answer", new StringType());
        query.addScalar("answersToChoose", new StringType());
        query.addScalar("startTime", new StringType());
        query.addScalar("endTime", new DateType());
        query.addScalar("description", new StringType());
        query.addScalar("score", new FloatType());
        query.addScalar("typeFile1", new StringType());
        query.addScalar("typeFile2", new StringType());
        query.addScalar("pathFile1", new StringType());
        query.addScalar("pathFile2", new StringType());
        query.addScalar("parentId", new LongType());
        query.addScalar("translatingQuestion", new StringType());
        query.addScalar("translatingQuesA", new StringType());
        query.addScalar("translatingQuesB", new StringType());
        query.addScalar("translatingQuesC", new StringType());
        query.addScalar("translatingQuesD", new StringType());
        query.addScalar("pathFileQuesA", new StringType());
        query.addScalar("pathFileQuesB", new StringType());
        query.addScalar("pathFileQuesC", new StringType());
        query.addScalar("pathFileQuesD", new StringType());
        query.addScalar("pathFileQues", new StringType());

        query.setResultTransformer(Transformers.aliasToBean(QuestionAnswersDTO.class));
        query.setParameter("id", questionAnswerListeningDTO.getId());

        return (QuestionAnswersDTO) query.uniqueResult();
    }

    public List<RequestPractice> getRandomListenCateWithLimitLenghtQuest (Long topicId, String part, String levelCode, int amountOfCategory){
        StringBuilder sql = new StringBuilder(
                "SELECT\n" +
                        "                    ID as categoryId,\n" +
                        "                    PATH_FILE_1 as pathFile1,\n" +
                        "                    PATH_FILE_2 as pathFile2, TRANSCRIPT as transcript, LEVEL_CODE as levelCode, topicName, topicId \n" +
                        "                FROM\n" +
                        "                (\n" +
                        "                    SELECT\n" +
                        "                        CATEGORY.ID ,\n" +
                        "                        CATEGORY.PATH_FILE_1 ,\n" +
                        "                        CATEGORY.PATH_FILE_2 , CATEGORY.TRANSCRIPT, CATEGORY.LEVEL_CODE, TOPICS.NAME as topicName, TOPICS.ID as topicId \n" +
                        "                    FROM CATEGORY \n" +
                        "                    INNER JOIN TOPICS \n" +
                        "                    ON CATEGORY.PARENT_ID = TOPICS.ID\n" +
                        "                    WHERE TOPICS.PART_TOPIC_CODE = :part AND CATEGORY.LEVEL_CODE = :levelCode AND CATEGORY.ID IN \n" +
                        "                    (\n" +
                        "                    SELECT * FROM ( SELECT \n" +
                        "                    CATEGORY.ID\n" +
                        "                    FROM QUESTION_ANSWER_LISTENING INNER JOIN CATEGORY\n" +
                        "                    ON QUESTION_ANSWER_LISTENING.PARENT_ID = CATEGORY.ID\n" +
                        "                    INNER JOIN TOPICS\n" +
                        "                    ON TOPICS.ID = CATEGORY.PARENT_ID\n" +
                        "                    WHERE TOPICS.ID = :topicId))\n" +
                        "                    order by DBMS_RANDOM.RANDOM\n" +
                        "                )\n" +
                        "                WHERE rownum <= :amountOfCategory"
        );
        NativeQuery query = currentSession().createNativeQuery(sql.toString());
        query.addScalar("categoryId",new LongType());
        query.addScalar("pathFile1",new StringType());
        query.addScalar("pathFile2",new StringType());
        query.addScalar("transcript",new StringType());
        query.addScalar("levelCode",new StringType());
        query.addScalar("topicName",new StringType());
        query.addScalar("topicId",new LongType());
        query.setResultTransformer(Transformers.aliasToBean(RequestPractice.class));
        query.setParameter("part",part);
        query.setParameter("topicId",topicId);
        query.setParameter("levelCode",levelCode);
        query.setParameter("amountOfCategory",amountOfCategory);

        List<RequestPractice> listRequestPractices = query.list();

        return listRequestPractices;
    }

    public List<QuestionAnswersDTO> getListQuestionByCategoryId(Long categoryId){
        StringBuilder sql = new StringBuilder(
                "SELECT \n" +
                "\tQUESTION_ANSWER_LISTENING.ID id,\n" +
                "\tQUESTION_ANSWER_LISTENING.NAME name,\n" +
                "\tQUESTION_ANSWER_LISTENING.ANSWERS_TO_CHOOSE answersToChoose,\n" +
                "\tQUESTION_ANSWER_LISTENING.DESCRIPTION description,\n" +
                "\tQUESTION_ANSWER_LISTENING.ANSWER answer,\n" +
                        "\tQUESTION_ANSWER_LISTENING.TRANSLATING_QUESTION translatingQuestion, \n" +
                        "\tQUESTION_ANSWER_LISTENING.TRANSLATING_QUES_A translatingQuesA, \n" +
                        "\tQUESTION_ANSWER_LISTENING.TRANSLATING_QUES_B translatingQuesB, \n" +
                        "\tQUESTION_ANSWER_LISTENING.TRANSLATING_QUES_C translatingQuesC, \n" +
                        "\tQUESTION_ANSWER_LISTENING.TRANSLATING_QUES_D translatingQuesD, \n" +

                        "\tQUESTION_ANSWER_LISTENING.PATH_FILE_QUES pathFileQues, \n" +
                        "\tQUESTION_ANSWER_LISTENING.PATH_FILE_QUES_A pathFileQuesA, \n" +
                        "\tQUESTION_ANSWER_LISTENING.PATH_FILE_QUES_B pathFileQuesB, \n" +
                        "\tQUESTION_ANSWER_LISTENING.PATH_FILE_QUES_C pathFileQuesC, \n" +
                        "\tQUESTION_ANSWER_LISTENING.PATH_FILE_QUES_D pathFileQuesD, \n" +

                "\tQUESTION_ANSWER_LISTENING.START_TIME startTime, QUESTION_ANSWER_LISTENING.SCORE score \n" +
                "FROM QUESTION_ANSWER_LISTENING INNER JOIN CATEGORY\n" +
                "ON CATEGORY.ID = QUESTION_ANSWER_LISTENING.PARENT_ID\n" +
                "WHERE CATEGORY.ID = :categoryId");
        NativeQuery query = currentSession().createNativeQuery(sql.toString());
        query.addScalar("id",new LongType());
        query.addScalar("name",new StringType());
        query.addScalar("answersToChoose",new StringType());
        query.addScalar("answer",new StringType());
        query.addScalar("description",new StringType());
        query.addScalar("startTime",new StringType());
        query.addScalar("score", new FloatType());
        query.addScalar("translatingQuestion",new StringType());
        query.addScalar("translatingQuesA",new StringType());
        query.addScalar("translatingQuesB",new StringType());
        query.addScalar("translatingQuesC",new StringType());
        query.addScalar("translatingQuesD",new StringType());

        query.addScalar("pathFileQues",new StringType());
        query.addScalar("pathFileQuesA",new StringType());
        query.addScalar("pathFileQuesB",new StringType());
        query.addScalar("pathFileQuesC",new StringType());
        query.addScalar("pathFileQuesD",new StringType());
        query.setResultTransformer(Transformers.aliasToBean(QuestionAnswersDTO.class));
        query.setParameter("categoryId",categoryId);
        return query.list();
    }

    //for minitest
    public List<QuestionMinitestDTO> getListQuestionByCategoryIdForMinitest(Long categoryId){
        StringBuilder sql = new StringBuilder(
                "SELECT \n" +
                        "\tQUESTION_ANSWER_LISTENING.ID id,\n" +
                        "\tQUESTION_ANSWER_LISTENING.NAME name,\n" +
                        "\tQUESTION_ANSWER_LISTENING.ANSWERS_TO_CHOOSE answersToChoose,\n"+
                        "\tQUESTION_ANSWER_LISTENING.ANSWER answer,\n" +
                        "\tQUESTION_ANSWER_LISTENING.START_TIME startTime, QUESTION_ANSWER_LISTENING.SCORE score, \n" +
                        "\tQUESTION_ANSWER_LISTENING.PART_FILE_1 pathFile1,\n" +
                        "\tQUESTION_ANSWER_LISTENING.PART_FILE_2 pathFile2, \n" +
                        "\tQUESTION_ANSWER_LISTENING.DESCRIPTION description, \n" +
                        "\tQUESTION_ANSWER_LISTENING.PARENT_ID parentId \n" +
                        " FROM QUESTION_ANSWER_LISTENING \n" +
                        " WHERE QUESTION_ANSWER_LISTENING.PARENT_ID = :categoryId");

        NativeQuery query = currentSession().createNativeQuery(sql.toString());
        query.addScalar("id",new LongType());
        query.addScalar("name",new StringType());
        query.addScalar("answersToChoose",new StringType());
        query.addScalar("answer",new StringType());
        query.addScalar("startTime",new StringType());
        query.addScalar("score", new FloatType());
        query.addScalar("pathFile1",new StringType());
        query.addScalar("pathFile2",new StringType());
        query.addScalar("description",new StringType());
        query.addScalar("parentId",new LongType());
        query.setResultTransformer(Transformers.aliasToBean(QuestionMinitestDTO.class));
        query.setParameter("categoryId",categoryId);
        return query.list();
    }

    public List<QuestionMinitestDTO> getListAnswerListeningById(Long quesId){
        StringBuilder sql = new StringBuilder(
                "SELECT \n" +
                        "\tQUESTION_ANSWER_LISTENING.ID id,\n" +
                        "\tQUESTION_ANSWER_LISTENING.NAME name,\n" +
                        "\tQUESTION_ANSWER_LISTENING.ANSWERS_TO_CHOOSE answersToChoose,\n"+
                        "\tQUESTION_ANSWER_LISTENING.ANSWER answer,\n" +
                        "\tQUESTION_ANSWER_LISTENING.START_TIME startTime, QUESTION_ANSWER_LISTENING.SCORE score, \n" +
                        "\tQUESTION_ANSWER_LISTENING.PART_FILE_1 pathFile1,\n" +
                        "\tQUESTION_ANSWER_LISTENING.PART_FILE_2 pathFile2, \n" +
                        "\tQUESTION_ANSWER_LISTENING.DESCRIPTION description, \n" +
                        "\tQUESTION_ANSWER_LISTENING.TRANSLATING_QUESTION translatingQuestion, \n" +
                        "\tQUESTION_ANSWER_LISTENING.TRANSLATING_QUES_A translatingQuesA, \n" +
                        "\tQUESTION_ANSWER_LISTENING.TRANSLATING_QUES_B translatingQuesB, \n" +
                        "\tQUESTION_ANSWER_LISTENING.TRANSLATING_QUES_C translatingQuesC, \n" +
                        "\tQUESTION_ANSWER_LISTENING.TRANSLATING_QUES_D translatingQuesD, \n" +

                        "\tQUESTION_ANSWER_LISTENING.PATH_FILE_QUES pathFileQues, \n" +
                        "\tQUESTION_ANSWER_LISTENING.PATH_FILE_QUES_A pathFileQuesA, \n" +
                        "\tQUESTION_ANSWER_LISTENING.PATH_FILE_QUES_B pathFileQuesB, \n" +
                        "\tQUESTION_ANSWER_LISTENING.PATH_FILE_QUES_C pathFileQuesC, \n" +
                        "\tQUESTION_ANSWER_LISTENING.PATH_FILE_QUES_D pathFileQuesD, \n" +

                        "\tQUESTION_ANSWER_LISTENING.PARENT_ID parentId \n" +
                        " FROM QUESTION_ANSWER_LISTENING \n" +
                        " WHERE QUESTION_ANSWER_LISTENING.ID = :quesId");

        NativeQuery query = currentSession().createNativeQuery(sql.toString());
        query.addScalar("id",new LongType());
        query.addScalar("name",new StringType());
        query.addScalar("answersToChoose",new StringType());
        query.addScalar("answer",new StringType());
        query.addScalar("startTime",new StringType());
        query.addScalar("score", new FloatType());
        query.addScalar("pathFile1",new StringType());
        query.addScalar("pathFile2",new StringType());
        query.addScalar("description",new StringType());
        query.addScalar("translatingQuestion",new StringType());
        query.addScalar("translatingQuesA",new StringType());
        query.addScalar("translatingQuesB",new StringType());
        query.addScalar("translatingQuesC",new StringType());
        query.addScalar("translatingQuesD",new StringType());

        query.addScalar("pathFileQues",new StringType());
        query.addScalar("pathFileQuesA",new StringType());
        query.addScalar("pathFileQuesB",new StringType());
        query.addScalar("pathFileQuesC",new StringType());
        query.addScalar("pathFileQuesD",new StringType());

        query.addScalar("parentId",new LongType());
        query.setResultTransformer(Transformers.aliasToBean(QuestionMinitestDTO.class));
        query.setParameter("quesId",quesId);
//        System.out.println(query.uniqueResult());
        return  query.list();
    }

    public QuestionMinitestDTO getAnswerListeningById(Long quesId){
        StringBuilder sql = new StringBuilder(
                "SELECT \n" +
                        "\tQUESTION_ANSWER_LISTENING.ID id,\n" +
                        "\tQUESTION_ANSWER_LISTENING.NAME name,\n" +
                        "\tQUESTION_ANSWER_LISTENING.ANSWERS_TO_CHOOSE answersToChoose,\n"+
                        "\tQUESTION_ANSWER_LISTENING.ANSWER answer,\n" +
                        "\tQUESTION_ANSWER_LISTENING.START_TIME startTime, QUESTION_ANSWER_LISTENING.SCORE score, \n" +
                        "\tQUESTION_ANSWER_LISTENING.PART_FILE_1 pathFile1,\n" +
                        "\tQUESTION_ANSWER_LISTENING.PART_FILE_2 pathFile2, \n" +
                        "\tQUESTION_ANSWER_LISTENING.DESCRIPTION description, \n" +
                        "\tQUESTION_ANSWER_LISTENING.TRANSLATING_QUESTION translatingQuestion, \n" +
                        "\tQUESTION_ANSWER_LISTENING.TRANSLATING_QUES_A translatingQuesA, \n" +
                        "\tQUESTION_ANSWER_LISTENING.TRANSLATING_QUES_B translatingQuesB, \n" +
                        "\tQUESTION_ANSWER_LISTENING.TRANSLATING_QUES_C translatingQuesC, \n" +
                        "\tQUESTION_ANSWER_LISTENING.TRANSLATING_QUES_D translatingQuesD, \n" +
                        "\tQUESTION_ANSWER_LISTENING.PARENT_ID parentId \n" +
                        " FROM QUESTION_ANSWER_LISTENING \n" +
                        " WHERE QUESTION_ANSWER_LISTENING.ID =:quesId");

        NativeQuery query = currentSession().createNativeQuery(sql.toString());
        query.addScalar("id",new LongType());
        query.addScalar("name",new StringType());
        query.addScalar("answersToChoose",new StringType());
        query.addScalar("answer",new StringType());
        query.addScalar("startTime",new StringType());
        query.addScalar("score", new FloatType());
        query.addScalar("pathFile1",new StringType());
        query.addScalar("pathFile2",new StringType());
        query.addScalar("description",new StringType());
        query.addScalar("translatingQuestion",new StringType());
        query.addScalar("translatingQuesA",new StringType());
        query.addScalar("translatingQuesB",new StringType());
        query.addScalar("translatingQuesC",new StringType());
        query.addScalar("translatingQuesD",new StringType());
        query.addScalar("parentId",new LongType());
        query.setResultTransformer(Transformers.aliasToBean(QuestionMinitestDTO.class));
        query.setParameter("quesId",quesId);
//        System.out.println(query.uniqueResult());
        return (QuestionMinitestDTO)  query.uniqueResult();
    }

    //end for minitest


    public List<QuestionMinitestDTO> getQuestionListeningByCategoryId(Long categoryId, Long testId){
        StringBuilder sql = new StringBuilder(
                "select qal.id id, " +
                        "qal.name name," +
                        "qal.parent_id parentId, " +
                        "qal.num_of_answer numberOfAnswer, " +
                        "qal.status status, " +
                        "qal.answers_to_choose answersToChoose," +
                        "qal.type_file_1 typeFile1, " +
                        "qal.type_file_2 typeFile2," +
                        "qal.part_file_1 pathFile1, " +
                        "qal.part_file_2 pathFile2," +
                        "qal.sentence_no sentenceNo, " +
                        "qal.test_id testId " +
                        "from question_answer_listening qal " +
                        "where qal.parent_id =:categoryId and qal.test_id =:testId " +
                        "order by qal.sentence_no");

        NativeQuery query = currentSession().createNativeQuery(sql.toString());
        query.addScalar("id",new LongType());
        query.addScalar("name",new StringType());
        query.addScalar("parentId",new LongType());
        query.addScalar("numberOfAnswer",new LongType());
        query.addScalar("status",new LongType());
        query.addScalar("answersToChoose",new StringType());
        query.addScalar("typeFile1",new StringType());
        query.addScalar("typeFile2",new StringType());
        query.addScalar("pathFile1",new StringType());
        query.addScalar("pathFile2",new StringType());
        query.addScalar("sentenceNo",new LongType());
        query.addScalar("testId",new LongType());

        query.setResultTransformer(Transformers.aliasToBean(QuestionMinitestDTO.class));
        query.setParameter("categoryId",categoryId);
        query.setParameter("testId",testId);
//        System.out.println(query.uniqueResult());
        return query.list();
    }

    public List<QuestionMinitestDTO> getResultQuestionListeningByCategoryId(Long categoryId){
        StringBuilder sql = new StringBuilder(
                "SELECT " +
                        "QUESTION_ANSWER_LISTENING.ID id, " +
                        "QUESTION_ANSWER_LISTENING.NAME name, " +
                        "QUESTION_ANSWER_LISTENING.ANSWERS_TO_CHOOSE answersToChoose, "+
                        "QUESTION_ANSWER_LISTENING.PART_FILE_1 pathFile1," +
                        "QUESTION_ANSWER_LISTENING.PART_FILE_2 pathFile2, "+
                        "QUESTION_ANSWER_LISTENING.ANSWER answer, " +
                        "QUESTION_ANSWER_LISTENING.DESCRIPTION description " +
                        "FROM QUESTION_ANSWER_LISTENING " +
                        "inner join CATEGORY on QUESTION_ANSWER_LISTENING.PARENT_ID = CATEGORY.ID " +
                        "WHERE QUESTION_ANSWER_LISTENING.PARENT_ID =:categoryId");

        NativeQuery query = currentSession().createNativeQuery(sql.toString());
        query.addScalar("id",new LongType());
        query.addScalar("name",new StringType());
        query.addScalar("answersToChoose",new StringType());
        query.addScalar("pathFile1",new StringType());
        query.addScalar("pathFile2",new StringType());
        query.addScalar("answer",new StringType());
        query.addScalar("description",new StringType());
        query.setResultTransformer(Transformers.aliasToBean(QuestionMinitestDTO.class));
        query.setParameter("categoryId",categoryId);
        return query.list();
    }

    public QuestionMinitestDTO getResultQuestionListeningById(Long id){
        StringBuilder sql = new StringBuilder(
                "SELECT " +
                        "QUESTION_ANSWER_LISTENING.ID id, " +
                        "QUESTION_ANSWER_LISTENING.NAME name, " +
                        "QUESTION_ANSWER_LISTENING.ANSWERS_TO_CHOOSE answersToChoose, "+
                        "QUESTION_ANSWER_LISTENING.PART_FILE_1 pathFile1, " +
                        "QUESTION_ANSWER_LISTENING.PART_FILE_2 pathFile2, "+
                        "QUESTION_ANSWER_LISTENING.ANSWER answer, " +
                        "QUESTION_ANSWER_LISTENING.DESCRIPTION description, " +
                        "QUESTION_ANSWER_LISTENING.TRANSLATING_QUESTION translatingQuestion, " +
                        "QUESTION_ANSWER_LISTENING.TRANSLATING_QUES_A translatingQuesA, " +
                        "QUESTION_ANSWER_LISTENING.TRANSLATING_QUES_B translatingQuesB, " +
                        "QUESTION_ANSWER_LISTENING.TRANSLATING_QUES_C translatingQuesC, " +
                        "QUESTION_ANSWER_LISTENING.TRANSLATING_QUES_D translatingQuesD, " +
                        "QUESTION_ANSWER_LISTENING.sentence_no sentenceNo, " +
                        "QUESTION_ANSWER_LISTENING.PATH_FILE_QUES_A pathFileQuesA, " +
                        "QUESTION_ANSWER_LISTENING.PATH_FILE_QUES_B pathFileQuesB, " +
                        "QUESTION_ANSWER_LISTENING.PATH_FILE_QUES_C pathFileQuesC, " +
                        "QUESTION_ANSWER_LISTENING.PATH_FILE_QUES_D pathFileQuesD, " +
                        "QUESTION_ANSWER_LISTENING.PATH_FILE_QUES pathFileQues " +
                        "FROM QUESTION_ANSWER_LISTENING " +
                        "WHERE QUESTION_ANSWER_LISTENING.ID =:id");

        NativeQuery query = currentSession().createNativeQuery(sql.toString());
        query.addScalar("id",new LongType());
        query.addScalar("name",new StringType());
        query.addScalar("answersToChoose",new StringType());
        query.addScalar("pathFile1",new StringType());
        query.addScalar("pathFile2",new StringType());
        query.addScalar("answer",new StringType());
        query.addScalar("description",new StringType());
        query.addScalar("translatingQuestion",new StringType());
        query.addScalar("translatingQuesA",new StringType());
        query.addScalar("translatingQuesB",new StringType());
        query.addScalar("translatingQuesC",new StringType());
        query.addScalar("translatingQuesD",new StringType());
        query.addScalar("sentenceNo",new LongType());
        query.addScalar("pathFileQuesA",new StringType());
        query.addScalar("pathFileQuesB",new StringType());
        query.addScalar("pathFileQuesC",new StringType());
        query.addScalar("pathFileQuesD",new StringType());
        query.addScalar("pathFileQues",new StringType());
        query.setResultTransformer(Transformers.aliasToBean(QuestionMinitestDTO.class));
        query.setParameter("id",id);
        return (QuestionMinitestDTO) query.uniqueResult();
    }

    public QuestionMinitestDTO getQuestionListeningHistory(Long id,String partName, Long idHistory){
        StringBuilder sql = new StringBuilder(
                "SELECT " +
                        "QUESTION_ANSWER_LISTENING.ID id, " +
                        "QUESTION_ANSWER_LISTENING.NAME name, " +
                        "QUESTION_ANSWER_LISTENING.ANSWERS_TO_CHOOSE answersToChoose, "+
                        "QUESTION_ANSWER_LISTENING.PART_FILE_1 pathFile1, " +
                        "QUESTION_ANSWER_LISTENING.PART_FILE_2 pathFile2, "+
                        "QUESTION_ANSWER_LISTENING.ANSWER answer, " +
                        "QUESTION_ANSWER_LISTENING.DESCRIPTION description, " +
                        "QUESTION_ANSWER_LISTENING.TRANSLATING_QUESTION translatingQuestion, " +
                        "QUESTION_ANSWER_LISTENING.TRANSLATING_QUES_A translatingQuesA, " +
                        "QUESTION_ANSWER_LISTENING.TRANSLATING_QUES_B translatingQuesB, " +
                        "QUESTION_ANSWER_LISTENING.TRANSLATING_QUES_C translatingQuesC, " +
                        "QUESTION_ANSWER_LISTENING.TRANSLATING_QUES_D translatingQuesD," +
                        "QUESTION_ANSWER_LISTENING.sentence_no sentenceNo, " +
                        "QUESTION_ANSWER_LISTENING.PATH_FILE_QUES_A pathFileQuesA, " +
                        "QUESTION_ANSWER_LISTENING.PATH_FILE_QUES_B pathFileQuesB, " +
                        "QUESTION_ANSWER_LISTENING.PATH_FILE_QUES_C pathFileQuesC, " +
                        "QUESTION_ANSWER_LISTENING.PATH_FILE_QUES_D pathFileQuesD, " +
                        "QUESTION_ANSWER_LISTENING.PATH_FILE_QUES pathFileQues, " +
                        "DETAILS_HISTORY.INDEX_CORRECT indexCorrectAnswer," +
                        "DETAILS_HISTORY.INDEX_INCORRECT indexIncorrectAnswer " +
                        "FROM QUESTION_ANSWER_LISTENING inner join DETAILS_HISTORY " +
                        "ON QUESTION_ANSWER_LISTENING.ID = DETAILS_HISTORY.QUESTION_ID " +
                        "AND DETAILS_HISTORY.QUESTION_ID =:id AND DETAILS_HISTORY.PART =:partName AND DETAILS_HISTORY.PARENT_ID =:idHistory ");

        NativeQuery query = currentSession().createNativeQuery(sql.toString());
        query.addScalar("id",new LongType());
        query.addScalar("name",new StringType());
        query.addScalar("answersToChoose",new StringType());
        query.addScalar("pathFile1",new StringType());
        query.addScalar("pathFile2",new StringType());
        query.addScalar("answer",new StringType());
        query.addScalar("description",new StringType());
        query.addScalar("translatingQuestion",new StringType());
        query.addScalar("translatingQuesA",new StringType());
        query.addScalar("translatingQuesB",new StringType());
        query.addScalar("translatingQuesC",new StringType());
        query.addScalar("translatingQuesD",new StringType());
        query.addScalar("sentenceNo",new LongType());
        query.addScalar("pathFileQuesA",new StringType());
        query.addScalar("pathFileQuesB",new StringType());
        query.addScalar("pathFileQuesC",new StringType());
        query.addScalar("pathFileQuesD",new StringType());
        query.addScalar("pathFileQues",new StringType());
        query.addScalar("indexCorrectAnswer",new LongType());
        query.addScalar("indexIncorrectAnswer",new LongType());
        query.setResultTransformer(Transformers.aliasToBean(QuestionMinitestDTO.class));
        query.setParameter("id",id);
        query.setParameter("partName",partName);
        query.setParameter("idHistory",idHistory);
        return (QuestionMinitestDTO) query.uniqueResult();
    }

    public QuestionAnswersDTO getQuestionFillById(Long id){
        StringBuilder sql = new StringBuilder("select qa.id id, " +
                "qa.name name, " +
                "qa.answer answer, " +
                "qa.answers_to_choose answersToChoose, " +
                "qa.start_time startTime, " +
                "qa.end_time endTime, " +
                "qa.description description, " +
                "qa.score score, " +
                "qa.type_file_1 typeFile1, " +
                "qa.type_file_2 typeFile2, " +
                "qa.part_file_1 pathFile1, " +
                "qa.part_file_2 pathFile2, " +
                "qa.parent_id parentId," +
                "qa.TRANSLATING_QUESTION translatingQuestion ," +
                "qa.TRANSLATING_QUES_A translatingQuesA," +
                "qa.TRANSLATING_QUES_B translatingQuesB," +
                "qa.TRANSLATING_QUES_C translatingQuesC," +
                "qa.TRANSLATING_QUES_D translatingQuesD," +
                "qa.PATH_FILE_QUES_A pathFileQuesA," +
                "qa.PATH_FILE_QUES_B pathFileQuesB," +
                "qa.PATH_FILE_QUES_C pathFileQuesC," +
                "qa.PATH_FILE_QUES_D pathFileQuesD," +
                "qa.PATH_FILE_QUES pathFileQues," +
                "c.path_file_1 pathFileCategory1," +
                "c.path_file_2 pathFileCategory2  " +
                "from QUESTION_ANSWER_LISTENING qa inner join category c on qa.parent_id = c.id " +
                "where qa.id =:id");
        NativeQuery query = currentSession().createNativeQuery(sql.toString());
        query.addScalar("id", new LongType());
        query.addScalar("name", new StringType());
        query.addScalar("answer", new StringType());
        query.addScalar("answersToChoose", new StringType());
        query.addScalar("startTime", new StringType());
        query.addScalar("endTime", new DateType());
        query.addScalar("description", new StringType());
        query.addScalar("score", new FloatType());
        query.addScalar("typeFile1", new StringType());
        query.addScalar("typeFile2", new StringType());
        query.addScalar("pathFile1", new StringType());
        query.addScalar("pathFile2", new StringType());
        query.addScalar("parentId", new LongType());
        query.addScalar("translatingQuestion", new StringType());
        query.addScalar("translatingQuesA", new StringType());
        query.addScalar("translatingQuesB", new StringType());
        query.addScalar("translatingQuesC", new StringType());
        query.addScalar("translatingQuesD", new StringType());
        query.addScalar("pathFileQuesA", new StringType());
        query.addScalar("pathFileQuesB", new StringType());
        query.addScalar("pathFileQuesC", new StringType());
        query.addScalar("pathFileQuesD", new StringType());
        query.addScalar("pathFileQues", new StringType());
        query.addScalar("pathFileCategory1", new StringType());
        query.addScalar("pathFileCategory2", new StringType());
        query.setResultTransformer(Transformers.aliasToBean(QuestionAnswersDTO.class));
        query.setParameter("id", id);

        return (QuestionAnswersDTO) query.uniqueResult();
    }

    public QuestionAnswersDTO getListQuestionAnswerListeningById(Long id){
        StringBuilder sql = new StringBuilder(
                "select QUESTION_ANSWER_LISTENING.ID id,\n" +
                        "        QUESTION_ANSWER_LISTENING.NAME name,\n" +
                        "        QUESTION_ANSWER_LISTENING.answer answer,\n" +
                        "        QUESTION_ANSWER_LISTENING.ANSWERS_TO_CHOOSE answersToChoose,\n" +
                        "        QUESTION_ANSWER_LISTENING.DESCRIPTION description,\n" +
                        "        QUESTION_ANSWER_LISTENING.START_TIME startTime,\n" +
                        "        QUESTION_ANSWER_LISTENING.PARENT_ID parentId,\n" +
                        "        QUESTION_ANSWER_LISTENING.TRANSLATING_QUESTION translatingQuestion,\n" +
                        "        QUESTION_ANSWER_LISTENING.TRANSLATING_QUES_A translatingQuesA,\n" +
                        "        QUESTION_ANSWER_LISTENING.TRANSLATING_QUES_B translatingQuesB,\n" +
                        "        QUESTION_ANSWER_LISTENING.TRANSLATING_QUES_C translatingQuesC,\n" +
                        "        QUESTION_ANSWER_LISTENING.TRANSLATING_QUES_D translatingQuesD,\n" +

                        "        QUESTION_ANSWER_LISTENING.PATH_FILE_QUES pathFileQues,\n" +
                        "        QUESTION_ANSWER_LISTENING.PATH_FILE_QUES_A pathFileQuesA,\n" +
                        "        QUESTION_ANSWER_LISTENING.PATH_FILE_QUES_B pathFileQuesB,\n" +
                        "        QUESTION_ANSWER_LISTENING.PATH_FILE_QUES_C pathFileQuesC,\n" +
                        "        QUESTION_ANSWER_LISTENING.PATH_FILE_QUES_D pathFileQuesD \n" +

                        "from QUESTION_ANSWER_LISTENING where QUESTION_ANSWER_LISTENING.ID =:id "
        );
        NativeQuery query = currentSession().createNativeQuery(sql.toString());
        query.addScalar("id",new LongType());
        query.addScalar("name",new StringType());
        query.addScalar("answer",new StringType());
        query.addScalar("answersToChoose",new StringType());
        query.addScalar("description",new StringType());
        query.addScalar("startTime",new StringType());
        query.addScalar("parentId",new LongType());
        query.addScalar("translatingQuestion",new StringType());
        query.addScalar("translatingQuesA",new StringType());
        query.addScalar("translatingQuesB",new StringType());
        query.addScalar("translatingQuesC",new StringType());
        query.addScalar("translatingQuesD",new StringType());

        query.addScalar("pathFileQues",new StringType());
        query.addScalar("pathFileQuesA",new StringType());
        query.addScalar("pathFileQuesB",new StringType());
        query.addScalar("pathFileQuesC",new StringType());
        query.addScalar("pathFileQuesD",new StringType());
        query.setResultTransformer(Transformers.aliasToBean(QuestionAnswersDTO.class));
        query.setParameter( "id", id );

        return (QuestionAnswersDTO) query.uniqueResult();
    }

    public QuestionAnswerListeningDTO getQuestionAnswerListeningById(Long id){
        StringBuilder sql = new StringBuilder(
                "select ID as id,\n" +
                        "                        NAME as name ,\n" +
                        "                        ANSWER as answer,\n" +
                        "                        PARENT_ID as parentId,\n" +
                        "                        NUM_OF_ANSWER as numOfAnswer,\n" +
                        "                        ANSWERS_TO_CHOOSE as answersToChoose ,\n" +
                        "                        DESCRIPTION as description,\n" +
                        "                        TRANSCRIPT as transcript,\n" +
                        "                        STATUS as status ,\n" +
                        "                        START_TIME as startTime , \n" +
                        "                        END_TIME as endTime , \n" +
                        "                        PART_FILE_1 as pathFile1,\n" +
                        "                        PART_FILE_2 as pathFile2,\n" +
                        "                        TYPE_FILE_1 as typeFile1,\n" +
                        "                        TYPE_FILE_2 as typeFile2,\n" +

                        "                        PATH_FILE_QUES as pathFileQues,\n" +
                        "                        PATH_FILE_QUES_A as pathFileQuesA,\n" +
                        "                        PATH_FILE_QUES_B as pathFileQuesB,\n" +
                        "                        PATH_FILE_QUES_C as pathFileQuesC,\n" +
                        "                        PATH_FILE_QUES_D as pathFileQuesD,\n" +

                        "                        SCORE as score \n" +
                        "                        from QUESTION_ANSWER_LISTENING where 1=1 and ID =:idSearch ");

        NativeQuery query = currentSession().createNativeQuery(sql.toString());

        query.addScalar("id", new LongType());
        query.addScalar("name", new StringType());
        query.addScalar("answer", new StringType());
        query.addScalar("parentId", new LongType());
        query.addScalar("description", new StringType());
        query.addScalar("transcript", new StringType());
        query.addScalar("numOfAnswer", new LongType());
        query.addScalar("answersToChoose", new StringType());
        query.addScalar("status", new LongType());
        query.addScalar("startTime", new StringType());
        query.addScalar("pathFile1", new StringType());
        query.addScalar("pathFile2", new StringType());
        query.addScalar("typeFile1", new StringType());
        query.addScalar("typeFile2", new StringType());

        query.addScalar("pathFileQues", new StringType());
        query.addScalar("pathFileQuesA", new StringType());
        query.addScalar("pathFileQuesB", new StringType());
        query.addScalar("pathFileQuesC", new StringType());
        query.addScalar("pathFileQuesD", new StringType());

        query.addScalar("endTime", new TimestampType());
        query.addScalar("score", new FloatType());

        query.setResultTransformer(Transformers.aliasToBean(QuestionAnswerListeningDTO.class));

        query.setParameter( "idSearch", id );

        return (QuestionAnswerListeningDTO) query.uniqueResult();
    }

    public QuestionAnswerListeningDTO getListQuestionByCategoryIdListenFill(Long parentId){
        StringBuilder sql = new StringBuilder(
                "SELECT * FROM (SELECT " +
                        "ql.ID id, " +
                        "ql.NAME name, " +
                        "ql.TYPE_FILE_1 typeFile1, " +
                        "ql.TYPE_FILE_2 typeFile2, " +
                        "ql.PART_FILE_1 pathFile1, " +
                        "ql.PART_FILE_2 pathFile2, " +
                        "ql.ANSWERS_TO_CHOOSE answersToChoose," +
                        "ql.START_TIME startTime," +
                        "c.path_file_1 pathFileCategory1," +
                        "c.path_file_2 pathFileCategory2 " +
                        "FROM QUESTION_ANSWER_LISTENING ql INNER JOIN  CATEGORY c " +
                        "on ql.PARENT_ID = c.ID where ql.parent_id =:parentId " +
                        "ORDER BY DBMS_RANDOM.RANDOM " +
                        ") where ROWNUM = 1"
        );
        NativeQuery query = currentSession().createNativeQuery(sql.toString());
        query.addScalar("id",new LongType());
        query.addScalar("name",new StringType());
        query.addScalar("typeFile1",new StringType());
        query.addScalar("typeFile2",new StringType());
        query.addScalar("pathFile1",new StringType());
        query.addScalar("pathFile2",new StringType());
        query.addScalar("answersToChoose",new StringType());
        query.addScalar("startTime", new StringType());
        query.addScalar("pathFileCategory1", new StringType());
        query.addScalar("pathFileCategory2", new StringType());
        query.setResultTransformer(Transformers.aliasToBean(QuestionAnswerListeningDTO.class));
        query.setParameter( "parentId", parentId );

        return (QuestionAnswerListeningDTO) query.uniqueResult();
    }


    // for deleting cateogry
    public List<QuestionAnswerListeningDTO> getListQuetionByCategoryId(Long parentId){
        StringBuilder sql = new StringBuilder(
                "select id from question_answer_listening where parent_id =:parentId "
        );

        NativeQuery query = currentSession().createNativeQuery(sql.toString());
        query.addScalar("id",new LongType());
        query.setResultTransformer(Transformers.aliasToBean(QuestionAnswerListeningDTO.class));
        query.setParameter("parentId",parentId);
        return query.list();
    }
}
