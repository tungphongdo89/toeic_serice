package com.migi.toeic.respositories;

import com.migi.toeic.dto.DetailHistoryMinitestDTO;
import com.migi.toeic.dto.HistoryDTO;
import com.migi.toeic.dto.HistoryMinitestDTO;
import com.migi.toeic.model.DetailHistoryMinitest;
import com.migi.toeic.model.HistoryMinitest;
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
public class HistoryMinitestRepository extends HibernateRepository<HistoryMinitest, Long> {

    public HistoryMinitestDTO getMaxIdOfHistoryMinitest(){
        StringBuilder sql = new StringBuilder(
                "select HISTORY_MINITEST.ID id from  HISTORY_MINITEST where rownum <=1 order by id desc ");

        NativeQuery query = currentSession().createNativeQuery(sql.toString());

        query.addScalar("id", new LongType());

        query.setResultTransformer(Transformers.aliasToBean(HistoryMinitestDTO.class));

        return (HistoryMinitestDTO) query.uniqueResult();
    }

    public HistoryMinitestDTO getMaxTestIdOfHistoryMinitest(){
        StringBuilder sql = new StringBuilder(
                "select  TEST_ID as testId \n" +
                        "        from ( \n" +
                        "                select  HISTORY_MINITEST.TEST_ID \n" +
                        "                        from  HISTORY_MINITEST \n" +
                        "                        order by test_id desc \n" +
                        "            ) \n" +
                        "        where rownum <=1  ");

        NativeQuery query = currentSession().createNativeQuery(sql.toString());

        query.addScalar("testId", new LongType());

        query.setResultTransformer(Transformers.aliasToBean(HistoryMinitestDTO.class));

        return (HistoryMinitestDTO) query.uniqueResult();
    }

    public List<HistoryMinitestDTO> getListHistoryMinitestByUserId(Long userId){
        StringBuilder sql = new StringBuilder(
                "select  HISTORY_MINITEST.ID id,\n" +
                        "            HISTORY_MINITEST.DO_TEST_DATE doTestDate, \n" +
                        "            HISTORY_MINITEST.TOTAL_CORRECT_ANSWER_LISTENING totalCorrectAnswerListening,\n" +
                        "            HISTORY_MINITEST.TOTAL_CORRECT_ANSWER_READING totalCorrectAnswerReading,\n" +
                        "            HISTORY_MINITEST.PART1 part1,\n" +
                        "            HISTORY_MINITEST.PART2 part2,\n" +
                        "            HISTORY_MINITEST.PART3 part3,\n" +
                        "            HISTORY_MINITEST.PART4 part4,\n" +
                        "            HISTORY_MINITEST.PART5 part5,\n" +
                        "            HISTORY_MINITEST.PART6 part6,\n" +
                        "            HISTORY_MINITEST.PART7 part7,\n" +
                        "            HISTORY_MINITEST.PART8 part8,\n" +
                        "            HISTORY_MINITEST.TOTAL_TIME totalTime \n" +
                        "            from HISTORY_MINITEST\n" +
                        "            where HISTORY_MINITEST.USER_ID =:userId order by doTestDate desc ");

        NativeQuery query = currentSession().createNativeQuery(sql.toString());
        query.addScalar("id",new LongType());
        query.addScalar("doTestDate",new TimestampType());
        query.addScalar("totalCorrectAnswerListening",new StringType());
        query.addScalar("totalCorrectAnswerReading",new StringType());
        query.addScalar("part1",new StringType());
        query.addScalar("part2",new StringType());
        query.addScalar("part3",new StringType());
        query.addScalar("part4",new StringType());
        query.addScalar("part5",new StringType());
        query.addScalar("part6",new StringType());
        query.addScalar("part7",new StringType());
        query.addScalar("part8",new StringType());
        query.addScalar("totalTime",new StringType());
        query.setResultTransformer(Transformers.aliasToBean(HistoryMinitestDTO.class));
        query.setParameter("userId",userId);
        return query.list();
    }

    public HistoryMinitestDTO getHistoryMinitestById(Long testId){
        StringBuilder sql = new StringBuilder(
                "select  HISTORY_MINITEST.ID id,\n" +
                        "            HISTORY_MINITEST.DO_TEST_DATE doTestDate, \n" +
                        "            HISTORY_MINITEST.TOTAL_CORRECT_ANSWER_LISTENING totalCorrectAnswerListening,\n" +
                        "            HISTORY_MINITEST.TOTAL_CORRECT_ANSWER_READING totalCorrectAnswerReading,\n" +
                        "            HISTORY_MINITEST.PART1 part1,\n" +
                        "            HISTORY_MINITEST.PART2 part2,\n" +
                        "            HISTORY_MINITEST.PART3 part3,\n" +
                        "            HISTORY_MINITEST.PART4 part4,\n" +
                        "            HISTORY_MINITEST.PART5 part5,\n" +
                        "            HISTORY_MINITEST.PART6 part6,\n" +
                        "            HISTORY_MINITEST.PART7 part7,\n" +
                        "            HISTORY_MINITEST.PART8 part8,\n" +
                        "            HISTORY_MINITEST.TOTAL_TIME totalTime \n" +
                        "            from HISTORY_MINITEST\n" +
                        "            where HISTORY_MINITEST.TEST_ID =:testId");

        NativeQuery query = currentSession().createNativeQuery(sql.toString());
        query.addScalar("id",new LongType());
        query.addScalar("doTestDate",new TimestampType());
        query.addScalar("totalCorrectAnswerListening",new StringType());
        query.addScalar("totalCorrectAnswerReading",new StringType());
        query.addScalar("part1",new StringType());
        query.addScalar("part2",new StringType());
        query.addScalar("part3",new StringType());
        query.addScalar("part4",new StringType());
        query.addScalar("part5",new StringType());
        query.addScalar("part6",new StringType());
        query.addScalar("part7",new StringType());
        query.addScalar("part8",new StringType());
        query.addScalar("totalTime",new StringType());
        query.setResultTransformer(Transformers.aliasToBean(HistoryMinitestDTO.class));
        query.setParameter("testId",testId);
        return (HistoryMinitestDTO) query.uniqueResult();
    }


    public List<HistoryMinitestDTO> doSearch(HistoryMinitestDTO historyMinitestDTO){
        StringBuilder sql = new StringBuilder(
                "select  HISTORY_MINITEST.ID id,\n" +
                        "            HISTORY_MINITEST.TEST_ID testId, \n" +
                        "            HISTORY_MINITEST.DO_TEST_DATE doTestDate, \n" +
                        "            HISTORY_MINITEST.TOTAL_CORRECT_ANSWER_LISTENING totalCorrectAnswerListening,\n" +
                        "            HISTORY_MINITEST.TOTAL_CORRECT_ANSWER_READING totalCorrectAnswerReading,\n" +
                        "            HISTORY_MINITEST.PART1 part1,\n" +
                        "            HISTORY_MINITEST.PART2 part2,\n" +
                        "            HISTORY_MINITEST.PART3 part3,\n" +
                        "            HISTORY_MINITEST.PART4 part4,\n" +
                        "            HISTORY_MINITEST.PART5 part5,\n" +
                        "            HISTORY_MINITEST.PART6 part6,\n" +
                        "            HISTORY_MINITEST.PART7 part7,\n" +
                        "            HISTORY_MINITEST.PART8 part8,\n" +
                        "            HISTORY_MINITEST.TOTAL_TIME totalTime \n" +
                        "            from HISTORY_MINITEST \n" +
                        "            where HISTORY_MINITEST.USER_ID =:userId " );

        if ( historyMinitestDTO.getCreateFrom() == null && historyMinitestDTO.getCreateTo() != null){
            sql.append(" and  TO_CHAR(HISTORY_MINITEST.DO_TEST_DATE, 'YYYYMMDD') <= :createTo ");
        }
        if ( historyMinitestDTO.getCreateTo() == null && historyMinitestDTO.getCreateFrom() != null){
            sql.append(" and  TO_CHAR(HISTORY_MINITEST.DO_TEST_DATE, 'YYYYMMDD') >= :createFrom ");
        }
        if ( historyMinitestDTO.getCreateTo() != null && historyMinitestDTO.getCreateFrom() != null){
            sql.append(" and  TO_CHAR(HISTORY_MINITEST.DO_TEST_DATE, 'YYYYMMDD') BETWEEN  :createFrom and  :createTo ");
        }
        sql.append(" order by HISTORY_MINITEST.DO_TEST_DATE desc ");
        StringBuilder sqlCount = new StringBuilder("SELECT COUNT(*) FROM ( ");
        sqlCount.append(sql.toString());
        sqlCount.append(" )");

        NativeQuery query = currentSession().createNativeQuery(sql.toString());
        NativeQuery queryCount = currentSession().createNativeQuery(sqlCount.toString());

        query.addScalar("id",new LongType());
        query.addScalar("testId",new LongType());
        query.addScalar("doTestDate",new TimestampType());
        query.addScalar("totalCorrectAnswerListening",new StringType());
        query.addScalar("totalCorrectAnswerReading",new StringType());
        query.addScalar("part1",new StringType());
        query.addScalar("part2",new StringType());
        query.addScalar("part3",new StringType());
        query.addScalar("part4",new StringType());
        query.addScalar("part5",new StringType());
        query.addScalar("part6",new StringType());
        query.addScalar("part7",new StringType());
        query.addScalar("part8",new StringType());
        query.addScalar("totalTime",new StringType());


        query.setResultTransformer(Transformers.aliasToBean(HistoryMinitestDTO.class));

        if(historyMinitestDTO.getUserId() != null ){
            query.setParameter("userId", historyMinitestDTO.getUserId());
            queryCount.setParameter("userId", historyMinitestDTO.getUserId());
        }
        if (historyMinitestDTO.getPage() != null && historyMinitestDTO.getPageSize() != null) {
            query.setFirstResult((historyMinitestDTO.getPage().intValue() - 1) * historyMinitestDTO.getPageSize().intValue());
            query.setMaxResults(historyMinitestDTO.getPageSize().intValue());
        }

        if (StringUtils.isNotBlank(historyMinitestDTO.getKeySearch())) {
            query.setParameter("keySearch", "%" + ValidateUtils.validateKeySearch(historyMinitestDTO.getKeySearch()) + "%");
            queryCount.setParameter("keySearch", "%" + ValidateUtils.validateKeySearch(historyMinitestDTO.getKeySearch()) + "%");
        }

        if ( historyMinitestDTO.getCreateFrom() == null && historyMinitestDTO.getCreateTo() != null){
            query.setParameter("createTo", historyMinitestDTO.getCreateToString());
            queryCount.setParameter("createTo", historyMinitestDTO.getCreateToString());
        }
        if ( historyMinitestDTO.getCreateTo() == null && historyMinitestDTO.getCreateFrom() != null){
            query.setParameter("createFrom", historyMinitestDTO.getCreateFromString());
            queryCount.setParameter("createFrom", historyMinitestDTO.getCreateFromString());
        }
        if ( historyMinitestDTO.getCreateTo() != null && historyMinitestDTO.getCreateFrom() != null){
            query.setParameter("createFrom", historyMinitestDTO.getCreateFromString());
            queryCount.setParameter("createFrom", historyMinitestDTO.getCreateFromString());
            query.setParameter("createTo", historyMinitestDTO.getCreateToString());
            queryCount.setParameter("createTo", historyMinitestDTO.getCreateToString());
        }

        List<HistoryMinitestDTO> lst = query.list();
        if(lst.size() > 0){
            lst.get(0).setNumberTest(((BigDecimal) queryCount.uniqueResult()).intValue());
        }
        return lst;

    }

}
