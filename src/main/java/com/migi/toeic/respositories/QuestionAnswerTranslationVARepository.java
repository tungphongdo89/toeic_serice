package com.migi.toeic.respositories;

import com.migi.toeic.base.ResultDataDTO;
import com.migi.toeic.dto.QuestionAnswersTranslationVADTO;
import com.migi.toeic.model.QuestionAnswerTranslationVA;
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
public class QuestionAnswerTranslationVARepository extends HibernateRepository<QuestionAnswerTranslationVA, Long> {

    @Transactional
    public ResultDataDTO getListQuestionTransVA(QuestionAnswersTranslationVADTO obj) {

        StringBuilder sql = new StringBuilder(
                "select ID as id," +
                        " NAME as name ," +
                        " ANSWER as answer," +
                        " PARENT_ID as parentId," +
                        " ANSWERS_TO_CHOOSE as answersToChoose ," +
                        " DESCRIPTION as description," +
                        " STATUS as status ," +
                        " SCORE as score " +
                        " from Q_A_TRANSLATION_V_A where 1=1 ");

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
        query.addScalar("answersToChoose", new StringType());
        query.addScalar("status", new LongType());
        query.addScalar("score", new FloatType());

        query.setResultTransformer(Transformers.aliasToBean(QuestionAnswersTranslationVADTO.class));

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
        List<QuestionAnswersTranslationVADTO> lst = query.list();
        resultDataDTO.setData(lst);
        obj.setTotalRecord(((BigDecimal) queryCount.uniqueResult()).intValue());
        resultDataDTO.setTotal(obj.getTotalRecord());
        return resultDataDTO;
    }


    public QuestionAnswersTranslationVADTO getDetail(Long id){
        StringBuilder sql = new StringBuilder(
                "select ID as id," +
                        " NAME as name ," +
                        " ANSWER as answer," +
                        " PARENT_ID as parentId," +
                        " ANSWERS_TO_CHOOSE as answerToChoose ," +
                        " DESCRIPTION as description," +
                        " STATUS as status ," +
                        " SCORE as score " +
                        " from Q_A_TRANSLATION_V_A where 1=1 and ID = :idSearch ");

        NativeQuery query = currentSession().createNativeQuery(sql.toString());

        query.addScalar("questionId", new LongType());
        query.addScalar("questionName", new StringType());
        query.addScalar("answer", new StringType());
        query.addScalar("parentId", new LongType());
        query.addScalar("description", new StringType());
        query.addScalar("answersToChoose", new StringType());
        query.addScalar("status", new LongType());
        query.addScalar("score", new FloatType());

        query.setResultTransformer(Transformers.aliasToBean(QuestionAnswersTranslationVADTO.class));

        query.setParameter( "idSearch", id );

        return (QuestionAnswersTranslationVADTO) query.uniqueResult();
    }

    public void deleteQuestionByCategory(Long categoryId){
        StringBuilder sql = new StringBuilder(
                "DELETE FROM q_a_translation_v_a WHERE parent_id = :categoryId ");

        NativeQuery query = currentSession().createNativeQuery(sql.toString());

        query.setResultTransformer(Transformers.aliasToBean(QuestionAnswersTranslationVADTO.class));

        query.setParameter( "categoryId", categoryId );
    }

    @Transactional
    public List<QuestionAnswersTranslationVADTO> getListQaTransVAByTopic(QuestionAnswersTranslationVADTO obj) {

        StringBuilder sql = new StringBuilder(
                "select ID as id," +
                        " NAME as name ," +
                        " ANSWER as answer," +
                        " PARENT_ID as parentId," +
                        " ANSWERS_TO_CHOOSE as answersToChoose ," +
                        " DESCRIPTION as description," +
                        " STATUS as status ," +
                        " SCORE as score ," +
                        " TOPICNAME as topicName " +
                        " from ( select Q_A_TRANSLATION_V_A.ID, " +
                                " Q_A_TRANSLATION_V_A.NAME, " +
                                " Q_A_TRANSLATION_V_A.ANSWER, " +
                                " Q_A_TRANSLATION_V_A.PARENT_ID ," +
                                " Q_A_TRANSLATION_V_A.ANSWERS_TO_CHOOSE ," +
                                " Q_A_TRANSLATION_V_A.DESCRIPTION ," +
                                " Q_A_TRANSLATION_V_A.STATUS ," +
                                " Q_A_TRANSLATION_V_A.SCORE ," +
                                " TOPICS.NAME as topicName " +
                                " from Q_A_TRANSLATION_V_A " +
                                " left join TOPICS on Q_A_TRANSLATION_V_A.PARENT_ID = TOPICS.ID order by DBMS_RANDOM.RANDOM ) " +
                        " where rownum <= 3 ");


        if (obj.getParentId() != null) {
            sql.append( " and PARENT_ID = :parentIdSearch " );
        }

        NativeQuery query = currentSession().createNativeQuery(sql.toString());

        query.addScalar("id", new LongType());
        query.addScalar("name", new StringType());
        query.addScalar("answer", new StringType());
        query.addScalar("parentId", new LongType());
        query.addScalar("description", new StringType());
        query.addScalar("answersToChoose", new StringType());
        query.addScalar("status", new LongType());
        query.addScalar("score", new FloatType());
        query.addScalar("topicName", new StringType());

        query.setResultTransformer(Transformers.aliasToBean(QuestionAnswersTranslationVADTO.class));

        if (obj.getParentId() != null) {
            query.setParameter( "parentIdSearch", obj.getParentId() );
        }

        return query.list();
    }

    @Transactional
    public List<QuestionAnswersTranslationVADTO> getListQaTransVAByLevel(QuestionAnswersTranslationVADTO obj, int amountOfQues) {

        StringBuilder sql = new StringBuilder(
                "select ID as id," +
                        " NAME as name ," +
                        " ANSWER as answer," +
                        " PARENT_ID as parentId," +
                        " ANSWERS_TO_CHOOSE as answersToChoose ," +
                        " DESCRIPTION as description," +
                        " STATUS as status ," +
                        " SCORE as score ," +
                        " AP_PARAMNAME as ap_paramName " +
                        " from ( select Q_A_TRANSLATION_V_A.ID, " +
                                " Q_A_TRANSLATION_V_A.NAME, " +
                                " Q_A_TRANSLATION_V_A.ANSWER, " +
                                " Q_A_TRANSLATION_V_A.PARENT_ID ," +
                                " Q_A_TRANSLATION_V_A.ANSWERS_TO_CHOOSE ," +
                                " Q_A_TRANSLATION_V_A.DESCRIPTION ," +
                                " Q_A_TRANSLATION_V_A.STATUS ," +
                                " Q_A_TRANSLATION_V_A.SCORE ," +
                                " AP_PARAM.NAME as ap_paramName " +
                                " from Q_A_TRANSLATION_V_A " +
                                " left join AP_PARAM on Q_A_TRANSLATION_V_A.PARENT_ID = AP_PARAM.ID order by DBMS_RANDOM.RANDOM ) " +
                                " where rownum <=:amountOfQues ");


        if (obj.getParentId() != null) {
            sql.append( " and PARENT_ID = :parentIdSearch " );
        }

        NativeQuery query = currentSession().createNativeQuery(sql.toString());

        query.addScalar("id", new LongType());
        query.addScalar("name", new StringType());
        query.addScalar("answer", new StringType());
        query.addScalar("parentId", new LongType());
        query.addScalar("description", new StringType());
        query.addScalar("answersToChoose", new StringType());
        query.addScalar("status", new LongType());
        query.addScalar("score", new FloatType());
        query.addScalar("ap_paramName", new StringType());

        query.setResultTransformer(Transformers.aliasToBean(QuestionAnswersTranslationVADTO.class));

        if (obj.getParentId() != null) {
            query.setParameter( "parentIdSearch", obj.getParentId() );
            query.setParameter( "amountOfQues", amountOfQues );
        }

        return query.list();
    }

}
