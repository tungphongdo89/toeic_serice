package com.migi.toeic.respositories;

import com.migi.toeic.authen.model.RequestPractice;
import com.migi.toeic.base.ResultDataDTO;
import com.migi.toeic.dto.QuestionAnswersDTO;
import com.migi.toeic.model.QuestionAnswerReading;
import com.migi.toeic.respositories.common.HibernateRepository;
import org.hibernate.query.NativeQuery;
import org.hibernate.transform.Transformers;
import org.hibernate.type.DateType;
import org.hibernate.type.LongType;
import org.hibernate.type.StringType;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Transactional
@Repository
public class QuestionAnswerRepository extends HibernateRepository<QuestionAnswerReading, Long> {

    @Transactional
    public ResultDataDTO doPractices(RequestPractice requestPractice) {
        List<QuestionAnswersDTO> lst = null;
        // 1 - reading
        // 2 -listening
        // 3 - translation a-v
        // 4 - translation v-a
        StringBuilder sqlRandom = new StringBuilder();
        StringBuilder sql = new StringBuilder(" select qa.ID id,qa.NAME name, t.LEVEL_CODE levelCode, " +
                "c.TIME_TO_ANSWER timeToAnswer, qa.ANSWERS_TO_CHOOSE answersToChoose ");
        String sqlJoin = " from TOPICS t left join CATEGORY c on t.ID = c.PARENT_ID ";
        String sqlWhere = " where t.LEVEL_CODE =:levelCode and t.STATUS = 1 and qa.STATUS = 1 and c.TYPE_CODE =:typeCode and upper(t.NAME) =upper(:topicName)";
        if (requestPractice.getTypeCode() == 1L) {
            sql.append(" , qa.ANSWER answer ");
            sql.append(sqlJoin);
            sql.append(" inner join QUESTION_ANSWER_READING qa on qa.PARENT_ID = c.ID");
            sql.append(sqlWhere);
        } else if (requestPractice.getTypeCode() == 2L) {
            sql.append(" , qa.ANSWER answer,qa.START_TIME startTime,qa.END_TIME endTime ");
            sql.append(sqlJoin);
            sql.append(" inner join QUESTION_ANSWER_LISTENING qa on qa.PARENT_ID = c.ID");
            sql.append(sqlWhere);
        } else if (requestPractice.getTypeCode() == 3L) {
            sql.append(sqlJoin);
            sql.append(" inner join Q_A_TRANSLATION_A_V qa on qa.PARENT_ID = c.ID");
            sql.append(sqlWhere);
        } else {
            sql.append(sqlJoin);
            sql.append(" inner join Q_A_TRANSLATION_V_A qa on qa.PARENT_ID = c.ID");
            sql.append(sqlWhere);
        }
        sqlRandom.append(" select * from ( ");
        sqlRandom.append(sql);
        sqlRandom.append("  order by DBMS_RANDOM.RANDOM ) where rownum <=:numberQuestion");

        NativeQuery query = currentSession().createNativeQuery(sqlRandom.toString());
        query.addScalar("id", new LongType());
        query.addScalar("name", new StringType());
        query.addScalar("levelCode", new StringType());
        query.addScalar("timeToAnswer", new LongType());
        query.addScalar("answersToChoose", new StringType());
        if (requestPractice.getTypeCode() == 1L) {
            query.addScalar("answer", new StringType());
        } else if (requestPractice.getTypeCode() == 2L) {
            query.addScalar("answer", new StringType());
            query.addScalar("startTime", new StringType());
            query.addScalar("endTime", new DateType());
        }
        query.setParameter("levelCode", requestPractice.getLevelCode());
        query.setParameter("typeCode", requestPractice.getTypeCode());
        query.setParameter("topicName", requestPractice.getTopicName());
        query.setParameter("numberQuestion", requestPractice.getNumberQuestion());

        query.setResultTransformer(Transformers.aliasToBean(QuestionAnswersDTO.class));
        lst = query.list();
        ResultDataDTO resultDataDTO = new ResultDataDTO();
        if (lst != null) {
            resultDataDTO.setData(lst);
            resultDataDTO.setTotal(lst.size());
        }
        return resultDataDTO;
    }

    @Transactional
    public ResultDataDTO getViewResultPractice(List<Long> list, Long tag) {
        List<QuestionAnswersDTO> lstResultView = null;
        // 1 - reading
        // 2 -listening
        // 3 - translation a-v
        // 4 - translation v-a
        StringBuilder sql = new StringBuilder(" select qa.ID id,qa.NAME name,qa.ANSWER answer,qa.ANSWERS_TO_CHOOSE answersToChoose," +
                " qa.DESCRIPTION description ");
        String sqlWhere = " where qa.ID IN (:listId)";
        if (tag == 1L) {
           sql.append(" from QUESTION_ANSWER_READING qa ");
            sql.append(sqlWhere);
        } else if (tag == 2L) {
            sql.append(" ,qa.START_TIME startTime,qa.END_TIME endTime from QUESTION_ANSWER_LISTENING qa ");
            sql.append(sqlWhere);
        } else if (tag == 3L) {
            sql.append(" from Q_A_TRANSLATION_A_V qa ");
            sql.append(sqlWhere);
        } else {
            sql.append(" from Q_A_TRANSLATION_V_A qa ");
            sql.append(sqlWhere);
        }
        NativeQuery query = currentSession().createNativeQuery(sql.toString());
        query.addScalar("id",new LongType());
        query.addScalar("name", new StringType());
        query.addScalar("answer",new StringType());
        query.addScalar("description",new StringType());
        query.addScalar("answersToChoose", new StringType());
        if (tag == 2L) {
            query.addScalar("startTime", new StringType());
            query.addScalar("endTime", new DateType());
        }
        query.setParameter("listId",list);
        query.setResultTransformer(Transformers.aliasToBean(QuestionAnswersDTO.class));
        lstResultView = query.getResultList();
        ResultDataDTO resultDataDTO = new ResultDataDTO();
        if (lstResultView != null) {
            resultDataDTO.setData(lstResultView);
            resultDataDTO.setTotal(lstResultView.size());
        }
        return resultDataDTO;
    }

}
