package com.migi.toeic.respositories;

import com.migi.toeic.base.ResultDataDTO;
import com.migi.toeic.dto.HistoryDTO;
import com.migi.toeic.dto.SysUserDTO;
import com.migi.toeic.dto.TestDTO;
import com.migi.toeic.model.History;
import com.migi.toeic.model.SysUser;
import com.migi.toeic.respositories.common.HibernateRepository;
import com.migi.toeic.utils.ValidateUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.query.NativeQuery;
import org.hibernate.transform.Transformers;
import org.hibernate.type.DateType;
import org.hibernate.type.LongType;
import org.hibernate.type.StringType;
import org.hibernate.type.TimestampType;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;

@Transactional
@Repository
public class HistoryRepository extends HibernateRepository<History,Long> {

    @Transactional
    public ResultDataDTO listStudentNotPass(HistoryDTO historyDTO, Long status){
        StringBuilder sql = new StringBuilder(
                "SELECT\n" +
                        "\ta.userName,\n" +
                        "\ta.userShowName,\n" +
                        "\ta.totalScore,\n" +
                        "\ta.testName,\n" +
                        "\ta.part1,\n" +
                        "\ta.part2,\n" +
                        "\ta.part3,\n" +
                        "\ta.part4,\n" +
                        "\ta.part5,\n" +
                        "\ta.part6,\n" +
                        "\ta.part7\n" +
                        "\tFROM \n" +
                        "(SELECT \n" +
                        "\tDISTINCT \n" +
                        "\tu.USER_ID userID, \n" +
                        "\tu.USER_NAME userName,\n" +
                        "    u.USER_SHOW_NAME userShowName,\n" +
                        "\th.PART1 part1,\n" +
                        "\th.PART2 part2,\n" +
                        "\th.PART3 part3,\n" +
                        "\th.PART4 part4,\n" +
                        "\th.PART5 part5,\n" +
                        "\th.PART6 part6,\n" +
                        "\th.PART7 part7,\n" +
                        "\th.TOTAL_SCORE totalScore,\n" +
                        "\tt.NAME testName,\n" +
                        "\th.CREATED_DATE createdDate\n" +
                        "FROM HISTORY h INNER JOIN USERS u \n" +
                        "ON h.USER_ID = u.USER_ID\n" +
                        "INNER JOIN TEST t\n" +
                        "ON h.TEST_ID = t.ID\n" +
                        "WHERE h.TEST_NAME = :testName AND h.TOTAL_SCORE < 450 AND h.STATUS = :status) a\n" +
                        "INNER JOIN \n" +
                        "(SELECT \n" +
                        "\tDISTINCT u.USER_ID userID, max(h.CREATED_DATE) latestHomeworkTime\n" +
                        "FROM HISTORY h INNER JOIN USERS u \n" +
                        "ON h.USER_ID = u.USER_ID\n" +
                        "INNER JOIN TEST t\n" +
                        "ON h.TEST_ID = t.ID\n" +
                        "WHERE h.TEST_NAME = :testName AND h.TOTAL_SCORE < 450 AND h.STATUS = :status GROUP BY u.USER_ID) b \n" +
                        "ON a.userID = b.userID AND a.createdDate = b.latestHomeworkTime  "
        );
//        if(status!=null){
//            sql.append(" AND h.STATUS = :status");
//        }

        StringBuilder sqlCount = new StringBuilder("SELECT COUNT(*) FROM (");
        sqlCount.append(sql.toString());
        sqlCount.append(")");

        NativeQuery query = currentSession().createNativeQuery(sql.toString());
        NativeQuery queryCount =currentSession().createNativeQuery(sqlCount.toString());

        query.addScalar("userName",new StringType());
        query.addScalar("userShowName", new StringType());
        query.addScalar("totalScore",new LongType());
        query.addScalar("testName", new StringType());
        query.addScalar("part1",new StringType());
        query.addScalar("part2",new StringType());
        query.addScalar("part3",new StringType());
        query.addScalar("part4",new StringType());
        query.addScalar("part5",new StringType());
        query.addScalar("part6",new StringType());
        query.addScalar("part7",new StringType());
        query.setResultTransformer(Transformers.aliasToBean(SysUserDTO.class));

        if (historyDTO.getPage() != null && historyDTO.getPageSize() != null) {
            query.setFirstResult((historyDTO.getPage().intValue() - 1) * historyDTO.getPageSize().intValue());
            query.setMaxResults(historyDTO.getPageSize().intValue());
        }
        if(status != null){
            query.setParameter("status",status);
            queryCount.setParameter("status",status);
        }
        if(StringUtils.isNotBlank(historyDTO.getTestName())){
            query.setParameter("testName",historyDTO.getTestName());
            queryCount.setParameter("testName",historyDTO.getTestName());
        }
        ResultDataDTO resultDataDTO = new ResultDataDTO();
        List<SysUserDTO> lst= query.list();
        resultDataDTO.setData(lst);
        resultDataDTO.setTotal(((BigDecimal) queryCount.uniqueResult()).intValue());
        return resultDataDTO;
    }

    public List<HistoryDTO> doSearch(HistoryDTO historyDTO){
        StringBuilder sql = new StringBuilder(
                "select  " +
                "a.countCategory countCategory, " +
                "a.latestHomeworkTime latestHomeworkTime, " +
                "b.totalScore totalScore," +
                "b.id id," +
                "b.part1 part1," +
                "b.part2 part2, " +
                "b.part3 part3, " +
                "b.part4 part4, " +
                "b.part5 part5, " +
                "b.part6 part6," +
                 " b.part7 part7, " +
                 "b.listeningScore listeningScore, " +
                 "b.readingScore readingScore, " +
                 "b.totalTime totalTime," +
                "b.testName testName," +
                "a.pathFile1 pathFile1," +
                "a.testId testId from " +
                "(select  " +
                        "count(history.test_id) countCategory, " +
                        "max(history.created_date) latestHomeworkTime, " +
                        "test.path_file_1 pathFile1," +
                        "history.test_id testId from history " +
                        "inner join test on history.test_id = test.id where user_id = :userId " +
                        "group by history.test_id, test.path_file_1) a " +
                "inner join " +
                "(select test_id , " +
                        " history.total_score totalScore, " +
                        " history.id id," +
                        "history.part1 part1," +
                        "history.part2 part2, " +
                        "history.part3 part3, " +
                        "history.part4 part4, " +
                        "history.part5 part5, " +
                        "history.part6 part6," +
                        "history.part7 part7, " +
                        "history.listening_score listeningScore, " +
                        "history.reading_score readingScore, " +
                        "history.total_time totalTime," +
                        "history.test_name testName," +
                        " history.created_date from history " +
                        " inner join test on history.test_id = test.id where user_id = :userId " +
                        " group by test_id, history.total_score, history.created_date, history.id, history.part1, history.part2, history.part3, history.part4," +
                        " history.part5, history.part6, history.part7, history.listening_score, history.reading_score,history.total_time, history.test_name) b " +
                "on a.testId = b.test_id and a.latestHomeworkTime = b.created_date " +
                " where 1=1 " );

        if (StringUtils.isNotBlank(historyDTO.getKeySearch())) {
            sql.append(" and (upper(b.testName)  like upper(:keySearch)  escape '&' )");
        }
        if ( historyDTO.getCreateFrom() == null && historyDTO.getCreateTo() != null){
            sql.append(" and  a.latestHomeworkTime <= :createTo +1 ");
        }
        if ( historyDTO.getCreateTo() == null && historyDTO.getCreateFrom() != null){
            sql.append(" and  a.latestHomeworkTime >= :createFrom ");
        }
        if ( historyDTO.getCreateTo() != null && historyDTO.getCreateFrom() != null){
            sql.append(" and  a.latestHomeworkTime BETWEEN  :createFrom and  :createTo +1");
        }
        sql.append("group by  a.countCategory, a.latestHomeworkTime, b.totalScore, a.testId, b.id," +
                "b.part1,b.part2, b.part3, b.part4, b.part5, b.part6, b.part7, b.listeningScore, b.readingScore, b.totalTime,b.testName ,a.pathFile1 " +
                " ORDER BY  a.latestHomeworkTime DESC");
        StringBuilder sqlCount = new StringBuilder("SELECT COUNT(*) FROM ( ");
        sqlCount.append(sql.toString());
        sqlCount.append(" )");

        NativeQuery query = currentSession().createNativeQuery(sql.toString());
        NativeQuery queryCount = currentSession().createNativeQuery(sqlCount.toString());

        query.addScalar("testId", new LongType());
        query.addScalar("countCategory", new LongType());
        query.addScalar("latestHomeworkTime", new TimestampType());
        query.addScalar("totalScore", new LongType());
        query.addScalar("id", new LongType());
        query.addScalar("part1", new StringType());
        query.addScalar("part2", new StringType());
        query.addScalar("part3", new StringType());
        query.addScalar("part4", new StringType());
        query.addScalar("part5", new StringType());
        query.addScalar("part6", new StringType());
        query.addScalar("part7", new StringType());
        query.addScalar("listeningScore", new LongType());
        query.addScalar("readingScore", new LongType());
        query.addScalar("totalTime", new StringType());
        query.addScalar("testName", new StringType());
        query.addScalar("pathFile1", new StringType());


        query.setResultTransformer(Transformers.aliasToBean(HistoryDTO.class));

        if(historyDTO.getUserId() != null ){
            query.setParameter("userId", historyDTO.getUserId());
            queryCount.setParameter("userId", historyDTO.getUserId());
        }
        if (historyDTO.getPage() != null && historyDTO.getPageSize() != null) {
            query.setFirstResult((historyDTO.getPage().intValue() - 1) * historyDTO.getPageSize().intValue());
            query.setMaxResults(historyDTO.getPageSize().intValue());
        }

        if (StringUtils.isNotBlank(historyDTO.getKeySearch())) {
            query.setParameter("keySearch", "%" + ValidateUtils.validateKeySearch(historyDTO.getKeySearch()) + "%");
            queryCount.setParameter("keySearch", "%" + ValidateUtils.validateKeySearch(historyDTO.getKeySearch()) + "%");
        }

        if ( historyDTO.getCreateFrom() == null && historyDTO.getCreateTo() != null){
            query.setParameter("createTo", historyDTO.getCreateTo());
            queryCount.setParameter("createTo", historyDTO.getCreateTo());
        }
        if ( historyDTO.getCreateTo() == null && historyDTO.getCreateFrom() != null){
            query.setParameter("createFrom", historyDTO.getCreateFrom());
            queryCount.setParameter("createFrom", historyDTO.getCreateFrom());
        }
        if ( historyDTO.getCreateTo() != null && historyDTO.getCreateFrom() != null){
            query.setParameter("createFrom", historyDTO.getCreateFrom());
            queryCount.setParameter("createFrom", historyDTO.getCreateFrom());
            query.setParameter("createTo", historyDTO.getCreateTo());
            queryCount.setParameter("createTo", historyDTO.getCreateTo());
        }

        List<HistoryDTO> lst = query.list();
        if(lst.size() > 0){
            lst.get(0).setNumberTest(((BigDecimal) queryCount.uniqueResult()).intValue());
        }
        return lst;
    }

    public Long numberStudentDoTest(Long testId){
        StringBuilder sql = new StringBuilder("select count(user_id) numberUser " +
                "from (select DISTINCT user_id, test_id from history where test_id =:testId)");
        NativeQuery query = currentSession().createNativeQuery(sql.toString());
        query.addScalar("numberUser", new LongType());
        query.setParameter("testId", testId);
        return (Long) query.uniqueResult();
    }

    public Long numberDoTest(Long testId, Long userId){
        StringBuilder sql = new StringBuilder(" select count(history.test_id) countCategory " +
                "                         from history where user_id =:userId and test_id =:testId ");
        NativeQuery query = currentSession().createNativeQuery(sql.toString());
        query.addScalar("countCategory", new LongType());
        query.setParameter("testId", testId);
        query.setParameter("userId", userId);
        return (Long) query.uniqueResult();
    }

    public List<HistoryDTO> getDetailHistoryTest(HistoryDTO historyDTO){
        StringBuilder sql = new StringBuilder(
                "select created_date createDate, " +
                        "total_score totalScore, " +
                        "listening_score listeningScore, " +
                        "reading_score readingScore, " +
                        "part1 part1, " +
                        "part2 part2, " +
                        "part3 part3, " +
                        "part4 part4, " +
                        "part5 part5, " +
                        "part6 part6, " +
                        "part7 part7 " +
                        "from history where user_id =:userId and test_id =:testId " +
                        "order by created_date desc " +
                        "FETCH NEXT 3 ROWS ONLY " );
        NativeQuery query = currentSession().createNativeQuery(sql.toString());


        query.addScalar("createDate",new TimestampType());
        query.addScalar("totalScore",new LongType());
        query.addScalar("listeningScore",new LongType());
        query.addScalar("readingScore",new LongType());
        query.addScalar("part1",new StringType());
        query.addScalar("part2",new StringType());
        query.addScalar("part3",new StringType());
        query.addScalar("part4",new StringType());
        query.addScalar("part5",new StringType());
        query.addScalar("part6",new StringType());
        query.addScalar("part7",new StringType());
        query.setResultTransformer(Transformers.aliasToBean(HistoryDTO.class));
        if (historyDTO.getPage() != null && historyDTO.getPageSize() != null) {
            query.setFirstResult((historyDTO.getPage().intValue() - 1) * historyDTO.getPageSize().intValue());
            query.setMaxResults(historyDTO.getPageSize().intValue());
        }
        query.setParameter( "userId", historyDTO.getUserId() );
        query.setParameter( "testId", historyDTO.getTestId() );
        return query.list();
    }

    public List<HistoryDTO> getListRankOfTest(HistoryDTO historyDTO){
        StringBuilder sql = new StringBuilder(
                "select a.user_id userId, " +
                        "b.user_show_name userShowName, " +
                        "b.total_score totalScore, " +
                        "b.listening_score listeningScore, " +
                        "b.reading_score readingScore, " +
                        "a.latestHomeworkTime latestHomeworkTime from " +
                        "(select " +
                                "user_id, " +
                                "max(created_date) latestHomeworkTime from history " +
                                "where history.test_id =:testId " +
                                "group by user_id) a " +
                        "inner join " +
                        "(select " +
                                "user_id , " +
                                "user_show_name, " +
                                "total_score , " +
                                "listening_score , " +
                                "reading_score , " +
                                "created_date from history " +
                                "where history.test_id =:testId " +
                                "group by user_id, user_show_name, total_score, listening_score, reading_score, created_date) b " +
                        "on a.user_id = b.user_id and a.latestHomeworkTime = b.created_date " +
                        "order by b.total_score desc " +
                        "FETCH NEXT 10 ROWS ONLY " );
        NativeQuery query = currentSession().createNativeQuery(sql.toString());

        query.addScalar("userId",new LongType());
        query.addScalar("userShowName",new StringType());
        query.addScalar("totalScore",new LongType());
        query.addScalar("listeningScore",new LongType());
        query.addScalar("readingScore",new LongType());
        query.addScalar("latestHomeworkTime",new TimestampType());
        query.setResultTransformer(Transformers.aliasToBean(HistoryDTO.class));
        query.setParameter( "testId", historyDTO.getTestId() );
        return query.list();
    }


    public List<Long> getListRankTest(HistoryDTO historyDTO){
        StringBuilder sql = new StringBuilder(
                "select a.user_id userId " +
                        " from " +
                        "(select " +
                        "user_id, " +
                        "max(created_date) latestHomeworkTime from history " +
                        "where history.test_id =:testId " +
                        "group by user_id) a " +
                        "inner join " +
                        "(select " +
                        "user_id , " +
                        "user_show_name, " +
                        "total_score , " +
                        "listening_score , " +
                        "reading_score , " +
                        "created_date from history " +
                        "where history.test_id =:testId " +
                        "group by user_id, user_show_name, total_score, listening_score, reading_score, created_date) b " +
                        "on a.user_id = b.user_id and a.latestHomeworkTime = b.created_date " +
                        "order by b.total_score desc " );
        NativeQuery query = currentSession().createNativeQuery(sql.toString());
        query.addScalar("userId",new LongType());
        query.setParameter( "testId", historyDTO.getTestId() );
        return query.list();
    }

    public HistoryDTO getHistoryById(Long id){
        StringBuilder sql = new StringBuilder("select id id," +
                "created_date createDate, " +
                "total_score totalScore, " +
                "listening_score listeningScore," +
                "reading_score readingScore," +
                "part1 part1," +
                "part2 part2," +
                "part3 part3," +
                "part4 part4," +
                "part5 part5," +
                "part6 part6," +
                "part7 part7," +
                "total_time totalTime," +
                "test_name testName  " +
                "from history where id =:id");
        NativeQuery query = currentSession().createNativeQuery(sql.toString());
        query.addScalar("id", new LongType());
        query.addScalar("createDate", new DateType());
        query.addScalar("totalScore", new LongType());
        query.addScalar("listeningScore", new LongType());
        query.addScalar("readingScore", new LongType());
        query.addScalar("part1", new StringType());
        query.addScalar("part2", new StringType());
        query.addScalar("part3", new StringType());
        query.addScalar("part4", new StringType());
        query.addScalar("part5", new StringType());
        query.addScalar("part6", new StringType());
        query.addScalar("part7", new StringType());
        query.addScalar("totalTime", new StringType());
        query.addScalar("testName", new StringType());
        query.setResultTransformer(Transformers.aliasToBean(HistoryDTO.class));

        query.setParameter("id",id);
        return (HistoryDTO) query.uniqueResult();
    }

}
