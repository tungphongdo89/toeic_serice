package com.migi.toeic.respositories;

import com.migi.toeic.base.ResultDataDTO;
import com.migi.toeic.dto.*;
import com.migi.toeic.model.Test;
import com.migi.toeic.respositories.common.HibernateRepository;
import com.migi.toeic.utils.ValidateUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.query.NativeQuery;
import org.hibernate.transform.Transformers;
import org.hibernate.type.*;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;

@Transactional
@Repository
public class TestRepository extends HibernateRepository<Test,Long> {

    @Transactional
    public ResultDataDTO doSearch(TestDTO testDTO)
    {
        StringBuilder sql = new StringBuilder("  SELECT DISTINCT CONCAT(CONCAT(c.NAME,' - '),t.NAME) name ,\n" +
                "t.id id, \n" +
                "c.NAME categoryName, " +
                "t.type type, \n" +
                "t.rank_code rankCode, \n" +
                "t.timing timing, \n" +
                "t.status status, \n" +
                "t.target_score targetScore, \n" +
                "t.create_time create_time, \n" +
                "t.updated_time updated_time, \n" +
                "t.path_file_1 pathFile1, \n" +
                "(SELECT count(*) FROM\n" +
                "\t(SELECT \n" +
                "\t\tDISTINCT u.USER_ID userID, max(h.CREATED_DATE) latestHomeworkTime\n" +
                "\tFROM HISTORY h INNER JOIN USERS u \n" +
                "\tON h.USER_ID = u.USER_ID\n" +
                "\tWHERE h.TEST_NAME =  CONCAT(CONCAT(c.NAME,' - '),t.NAME) AND h.TOTAL_SCORE < 450 AND h.STATUS = 1 GROUP BY u.USER_ID)) totalFaild,\n" +
                "(SELECT COUNT(h2.USER_ID) FROM HISTORY h2 WHERE h2.TEST_NAME = CONCAT(CONCAT(c.NAME,' - '),t.NAME) ) countCategory,\n" +
                "(SELECT max(h2.CREATED_DATE ) FROM HISTORY h2 WHERE h2.TEST_NAME = CONCAT(CONCAT(c.NAME,' - '),t.NAME)  ) latestHomeworkTime\n" +
                "\tFROM TEST t \n" +
                "\tJOIN CATEGORY_TEST ct ON ct.TEST_ID =t.ID \n" +
                "\tJOIN CATEGORY c ON c.ID =ct.CATEGORY_ID \n" +
                " where 1=1 AND  c.NAME NOT LIKE 'LC%old' ");

        if (StringUtils.isNotBlank(testDTO.getKeySearch())) {
            sql.append(" and (upper(CONCAT(CONCAT(c.NAME,' - '),t.NAME))  like upper(:keySearch)  escape '&' )");
        }
        if (testDTO.getStatus() != null) {
            sql.append(" and t.STATUS =:status ");
        }
        if ( testDTO.getCreateFrom() == null && testDTO.getCreateTo() != null){
            sql.append(" and  TO_CHAR(t.create_time, 'YYYYMMDD') = :createTo ");
        }
        if ( testDTO.getCreateTo() == null && testDTO.getCreateFrom() != null){
            sql.append(" and  TO_CHAR(t.create_time, 'YYYYMMDD') = :createFrom ");
        }
        if ( testDTO.getCreateTo() != null && testDTO.getCreateFrom() != null){
            sql.append(" and  TO_CHAR(t.create_time, 'YYYYMMDD') BETWEEN  :createFrom and  :createTo ");
        }
//        sql.append(" group by test.id, test.name, test.type, test.rank_code, test.timing, test.status, test.target_score, test.create_time, test.updated_time,test.path_file_1,tempCount.totalFaild ");
        sql.append(" order by name ASC ");

        StringBuilder sqlCount = new StringBuilder("SELECT COUNT(*) FROM ( ");
        sqlCount.append(sql.toString());
        sqlCount.append(" )");

        NativeQuery query = currentSession().createNativeQuery(sql.toString());
        NativeQuery queryCount = currentSession().createNativeQuery(sqlCount.toString());

        query.addScalar("id", new LongType());
        query.addScalar("name", new StringType());
        query.addScalar("type", new LongType());
        query.addScalar("rankCode", new LongType());
        query.addScalar("targetScore", new LongType());
        query.addScalar("timing", new LongType());
        query.addScalar("status",new LongType());
        query.addScalar("create_time", new TimestampType());
        query.addScalar("updated_time", new TimestampType());
        query.addScalar("pathFile1", new StringType());
        query.addScalar("countCategory", new LongType());
        query.addScalar("latestHomeworkTime", new TimestampType());
        query.addScalar("totalFaild", new LongType());
        query.addScalar("categoryName", new StringType());

        query.setResultTransformer(Transformers.aliasToBean(TestDTO.class));

        if (testDTO.getPage() != null && testDTO.getPageSize() != null) {
            query.setFirstResult((testDTO.getPage().intValue() - 1) * testDTO.getPageSize().intValue());
            query.setMaxResults(testDTO.getPageSize().intValue());
        }

        if (StringUtils.isNotBlank(testDTO.getKeySearch())) {
            query.setParameter("keySearch", "%" + ValidateUtils.validateKeySearch(testDTO.getKeySearch()) + "%");
            queryCount.setParameter("keySearch", "%" + ValidateUtils.validateKeySearch(testDTO.getKeySearch()) + "%");
        }

        if (testDTO.getStatus() != null) {
            query.setParameter("status", testDTO.getStatus());
            queryCount.setParameter("status", testDTO.getStatus());
        }
        if ( testDTO.getCreateFrom() == null && testDTO.getCreateTo() != null){
            query.setParameter("createTo", testDTO.getCreateToString());
            queryCount.setParameter("createTo", testDTO.getCreateToString());
        }
        if ( testDTO.getCreateTo() == null && testDTO.getCreateFrom() != null){
            query.setParameter("createFrom", testDTO.getCreateFromString());
            queryCount.setParameter("createFrom", testDTO.getCreateFromString());
        }
        if ( testDTO.getCreateTo() != null && testDTO.getCreateFrom() != null){
            query.setParameter("createFrom", testDTO.getCreateFromString());
            queryCount.setParameter("createFrom", testDTO.getCreateFromString());
            query.setParameter("createTo", testDTO.getCreateToString());
            queryCount.setParameter("createTo", testDTO.getCreateToString());
        }
        ResultDataDTO resultDataDTO = new ResultDataDTO();
        List<TestDTO> lst = query.list();
        resultDataDTO.setData(lst);
        testDTO.setTotalRecord(((BigDecimal) queryCount.uniqueResult()).intValue());
        resultDataDTO.setTotal(testDTO.getTotalRecord());
        return resultDataDTO;

    }

    public TestDTO getTestById(TestDTO obj) {
        StringBuilder sql = new StringBuilder(" select " +
                " test.id id, " +
                " test.name name, " +
                " test.status status, " +
                " test.target_score targetScore, " +
                " test.timing timing " +
                " from test " +
                " where 1=1 "  );
        if(obj.getId() !=null)
        {
            sql.append(" and test.ID = :id ");
        }
        NativeQuery query = currentSession().createNativeQuery(sql.toString());
        query.addScalar("id", new LongType());
        query.addScalar("name", new StringType());
        query.addScalar("targetScore", new LongType());
        query.addScalar("timing", new LongType());
        query.addScalar("status",new LongType());

        if(obj.getId() !=null)
        {
            query.setParameter("id",obj.getId());
        }
        query.setResultTransformer(Transformers.aliasToBean(TestDTO.class));
        return (TestDTO) query.uniqueResult();

    }

    public ResultDataDTO getStudentByTestId(TestDTO testDTO){
        StringBuilder sql = new StringBuilder(" select users.user_id userId," +
                " users.user_name userName, " +
                " users.status status " +
                " from management_student_tests " +
                " inner join users on management_student_tests.user_id = users.user_id " +
                " inner join test on test.id = management_student_tests.test_id " +
                " where 1=1 and test.ID = :testId" );

        StringBuilder sqlCount = new StringBuilder("SELECT COUNT(*) FROM ( ");
        sqlCount.append(sql.toString());
        sqlCount.append(" ) ");

        NativeQuery query = currentSession().createNativeQuery(sql.toString());
        NativeQuery queryCount = currentSession().createNativeQuery(sqlCount.toString());

        query.addScalar("userId", new LongType());
        query.addScalar("userName", new StringType());
        query.addScalar("status", new LongType());


        if (testDTO.getId() != null) {
            query.setParameter( "testId", testDTO.getId() );
            queryCount.setParameter( "testId", testDTO.getId() );
        }
        query.setResultTransformer(Transformers.aliasToBean(SysUserDTO.class));
        ResultDataDTO resultDataDTO = new ResultDataDTO();
        List<SysUserDTO> lst = query.list();
        resultDataDTO.setData(lst);
        testDTO.setTotalRecord(((BigDecimal) queryCount.uniqueResult()).intValue());
        resultDataDTO.setTotal(testDTO.getTotalRecord());
        return resultDataDTO;
    }

    public ResultDataDTO getQuestionReadingByTestId(TestDTO testDTO){
        StringBuilder sql = new StringBuilder("select " +
                " question_answer_reading.id id," +
                " question_answer_reading.name name," +
                " question_answer_reading.parent_id parentId," +
                " question_answer_reading.num_of_answer numOfAnswer," +
                " question_answer_reading.answer answer," +
                " question_answer_reading.status status," +
                " question_answer_reading.description description," +
                " question_answer_reading.answers_to_choose answersToChoose" +
                " from question_answer_reading " +
                " inner join category on question_answer_reading.parent_id = category.id " +
                " inner join category_test on category.id = category_test.category_id " +
                " inner join test on test.id = category_test.test_id " +
                " where 1=1 " +
                " and test.id = :testId ");

        StringBuilder sqlCount = new StringBuilder("SELECT COUNT(*) FROM ( ");
        sqlCount.append(sql.toString());
        sqlCount.append(" ) ");

        NativeQuery query = currentSession().createNativeQuery(sql.toString());
        NativeQuery queryCount = currentSession().createNativeQuery(sqlCount.toString());

        query.addScalar("id", new LongType());
        query.addScalar("name", new StringType());
        query.addScalar("parentId", new LongType());
        query.addScalar("numOfAnswer", new LongType());
        query.addScalar("answer", new StringType());
        query.addScalar("status", new LongType());
        query.addScalar("description", new StringType());
        query.addScalar("answersToChoose", new StringType());

        if (testDTO.getId() != null) {
            query.setParameter( "testId", testDTO.getId() );
            queryCount.setParameter( "testId", testDTO.getId() );
        }
        query.setResultTransformer(Transformers.aliasToBean(QuestionAnswersDTO.class));
        ResultDataDTO resultDataDTO = new ResultDataDTO();
        List<QuestionAnswersDTO> lst = query.list();
        resultDataDTO.setData(lst);
        testDTO.setTotalRecord(((BigDecimal) queryCount.uniqueResult()).intValue());
        resultDataDTO.setTotal(testDTO.getTotalRecord());
        return resultDataDTO;
    }

    public ResultDataDTO getQuestionListeningByTestId(TestDTO testDTO){
        StringBuilder sql = new StringBuilder("select " +
                " question_answer_listening.id id," +
                " question_answer_listening.name name," +
                " question_answer_listening.parent_id parentId," +
                " question_answer_listening.num_of_answer numOfAnswer," +
                " question_answer_listening.answer answer, " +
                " question_answer_listening.status status," +
                " question_answer_listening.description description," +
                " question_answer_listening.start_time startTime," +
                " question_answer_listening.end_time endTime," +
                " question_answer_listening.answers_to_choose answersToChoose" +
                " from question_answer_listening" +
                " inner join category on question_answer_listening.parent_id = category.id " +
                " inner join category_test on category.id = category_test.category_id " +
                " inner join test on test.id = category_test.test_id " +
                " where 1=1 " +
                " and test.id = :testId ");


        StringBuilder sqlCount = new StringBuilder("SELECT COUNT(*) FROM ( ");
        sqlCount.append(sql.toString());
        sqlCount.append(" ) ");

        NativeQuery query = currentSession().createNativeQuery(sql.toString());
        NativeQuery queryCount = currentSession().createNativeQuery(sqlCount.toString());


        query.addScalar("id", new LongType());
        query.addScalar("name", new StringType());
        query.addScalar("parentId", new LongType());
        query.addScalar("numOfAnswer", new LongType());
        query.addScalar("answer", new StringType());
        query.addScalar("status", new LongType());
        query.addScalar("description", new StringType());
        query.addScalar("startTime", new DateType());
        query.addScalar("endTime", new DateType());
        query.addScalar("answersToChoose", new StringType());

        if (testDTO.getId() != null) {
            query.setParameter( "testId", testDTO.getId() );
            queryCount.setParameter( "testId", testDTO.getId() );
        }

        query.setResultTransformer(Transformers.aliasToBean(QuestionAnswersDTO.class));
        ResultDataDTO resultDataDTO = new ResultDataDTO();
        List<QuestionAnswersDTO> lst = query.list();
        resultDataDTO.setData(lst);
        testDTO.setTotalRecord(((BigDecimal) queryCount.uniqueResult()).intValue());
        resultDataDTO.setTotal(testDTO.getTotalRecord());
        return resultDataDTO;
    }

    public List<CategoryDTO> getCategoryByTestId(Long testId) {
        StringBuilder sql = new StringBuilder(
                "select \n" +
                        "\t category.id categoryId,  \n" +
                        "\t category.name nameCategory,  \n" +
                        "\t category.parent_id parentId,  \n" +
                        "\t category.status status, \n" +
                        "\t category.type_code typeCode,  \n" +
                        "\t category.level_code levelCode,\n" +
                        "\t TOPICS.NAME nameTopic,\n" +
                        "\t TOPICS.PART_TOPIC_CODE part,\n" +
                        "\t temp.NAME typeName,\n" +
                        "\t category.time_to_answer timeToAnswer\n" +
                        "from category_test  inner join category \n" +
                        "\ton category_test.category_id = category.id   \n" +
                        "inner join test \n" +
                        "\ton test.id = category_test.test_id\n" +
                        "LEFT JOIN TOPICS\n" +
                        "\ton TOPICS.ID = CATEGORY.PARENT_ID\n" +
                        "left JOIN \n" +
                        "(\n" +
                        "SELECT \n" +
                        "\tNAME,\n" +
                        "\tVALUE \n" +
                        "From AP_PARAM \n" +
                        "WHERE AP_PARAM.TYPE = 'TOPIC_TYPE'\n" +
                        ")\ttemp\n" +
                        "ON temp.VALUE = CATEGORY.TYPE_CODE\n" +
                        "where 1=1  and test.id = :testId");
        NativeQuery query = currentSession().createNativeQuery(sql.toString());

        query.addScalar("categoryId", new LongType());
        query.addScalar("nameCategory", new StringType());
        query.addScalar("parentId", new LongType());
        query.addScalar("status", new LongType());
        query.addScalar("typeCode", new LongType());
        query.addScalar("timeToAnswer", new FloatType());
        query.addScalar("part", new StringType());
        query.addScalar("typeName",new StringType());
        query.addScalar("nameTopic",new StringType());
        query.addScalar("levelCode",new StringType());

        query.setParameter("testId", testId);
        query.setResultTransformer(Transformers.aliasToBean(CategoryDTO.class));
        return query.list();
    }

    public List<CategoryMinitestDTO> getListCategoryByTestId(TestDTO testDTO,String nameCategory,String part){
        StringBuilder sql = new StringBuilder("select c.id categoryId, " +
                "                 c.name nameCategory, " +
                "                 c.path_file_1 pathFile1, " +
                "                 c.path_file_2 pathFile2, " +
                "                 t.id testId, " +
                "                 t.name testName " +
                "                from test t inner join category_test ct on t.id = ct.test_id " +
                "                inner join category c on c.id = ct.category_id " +
                "                where c.name =:nameCategory and t.id=:testId and ct.part=:part" +
                "                 ORDER BY c.array_sentence_no");

        NativeQuery query = currentSession().createNativeQuery(sql.toString());
        query.addScalar("categoryId", new LongType());
        query.addScalar("nameCategory", new StringType());
        query.addScalar("pathFile1", new StringType());
        query.addScalar("pathFile2", new StringType());
        query.addScalar("testId", new LongType());
        query.addScalar("testName", new StringType());

        query.setParameter("testId", testDTO.getId());
        query.setParameter("part", part);
        query.setParameter("nameCategory", nameCategory);
        query.setResultTransformer(Transformers.aliasToBean(CategoryMinitestDTO.class));
        return query.list();
    }

    public List<CategoryDTO> getListNameCategoryOfTest(){
        StringBuilder sql = new StringBuilder("select DISTINCT (c.name) nameCategory " +
                "from test t join category_test ct on t.id = ct.test_id " +
                "join category c on ct.category_id = c.id " +
                "where c.name like '%ETS%' order by c.name");

        NativeQuery query = currentSession().createNativeQuery(sql.toString());
        query.addScalar("nameCategory", new StringType());

        query.setResultTransformer(Transformers.aliasToBean(CategoryDTO.class));
        return query.list();
    }

    public List<TestDTO> getListNameTestByNameCategory(CategoryDTO categoryDTO){
        StringBuilder sql = new StringBuilder("select DISTINCT CONCAT(c.name,CONCAT(' - ',t.name)) name, t.id id " +
                "from test t join category_test ct on t.id = ct.test_id " +
                "join category c on ct.category_id = c.id " +
                "where upper(c.name)  like upper(:keySearch)  escape '&' " +
                "order by name ");

        NativeQuery query = currentSession().createNativeQuery(sql.toString());
        query.addScalar("name", new StringType());
        query.addScalar("id", new LongType());

        if (StringUtils.isNotBlank(categoryDTO.getNameCategory())) {
            query.setParameter("keySearch", "%" + ValidateUtils.validateKeySearch(categoryDTO.getNameCategory()) + "%");
        }

        query.setResultTransformer(Transformers.aliasToBean(TestDTO.class));
        return query.list();
    }

}
