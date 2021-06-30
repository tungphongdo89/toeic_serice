package com.migi.toeic.respositories;

import com.migi.toeic.dto.ApParamDTO;
import com.migi.toeic.dto.ApParamTestDTO;
import com.migi.toeic.dto.ApparamForGetPartOrTopicDTO;
import com.migi.toeic.model.ApParam;
import com.migi.toeic.respositories.common.HibernateRepository;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.query.NativeQuery;
import org.hibernate.transform.Transformers;
import org.hibernate.type.LongType;
import org.hibernate.type.StringType;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public class ApParamRepository extends HibernateRepository<ApParam,Long> {

    @Transactional
    public List<ApParamDTO> getLevelCode()
    {
        StringBuilder sql = new StringBuilder(" select a.NAME name, a.CODE code " +
            "from AP_PARAM a where upper(a.TYPE) = upper(:type) ");
        NativeQuery query = currentSession().createNativeQuery(sql.toString());
        query.addScalar("name",new StringType());
        query.addScalar("code",new StringType());
        query.setResultTransformer(Transformers.aliasToBean(ApParamDTO.class));
        query.setParameter("type","LEVEL_CODE");
        return query.list();
    }
    @Transactional
    public List<ApParamDTO> getType()
    {
        StringBuilder sql = new StringBuilder(" select a.NAME name, a.CODE code, a.VALUE value " +
            "from AP_PARAM a where upper(a.TYPE) = upper(:type) ");
        NativeQuery query = currentSession().createNativeQuery(sql.toString());
        query.addScalar("name",new StringType());
        query.addScalar("code",new StringType());
        query.addScalar("value",new StringType());
        query.setResultTransformer(Transformers.aliasToBean(ApParamDTO.class));
        query.setParameter("type","TOPIC_TYPE");
        return query.list();
    }
    @Transactional
    public List<ApParamDTO> getPartByParentCode(String parentCode)
    {
        StringBuilder sql = new StringBuilder(" select a.NAME name, a.CODE code, a.PARENT_CODE parentCode from AP_PARAM a" +
            " where upper(a.TYPE) = upper(:partType) " );
        if(StringUtils.isNotBlank(parentCode))
        {
            sql.append(" and  upper(a.PARENT_CODE) = upper(:parentCode)");
        }
        sql.append(" order by code");
        NativeQuery query = currentSession().createNativeQuery(sql.toString());
        query.addScalar("name",new StringType());
        query.addScalar("code",new StringType());
        query.addScalar("parentCode",new StringType());
        query.setResultTransformer(Transformers.aliasToBean(ApParamDTO.class));
        query.setParameter("partType","PART_TOPIC");
        if(StringUtils.isNotBlank(parentCode))
        {
            query.setParameter("parentCode",parentCode);
        }
        return query.list();
    }

    //get menu list test

    @Transactional
    public List<ApParamTestDTO> getMenuListTest(){
        StringBuilder sql = new StringBuilder(" select name as typeTestName, code as typeTestCode, " +
                " value as typeTestValue from ap_param where ap_param.type='TOPIC_TYPE_TEST' " );

        NativeQuery query = currentSession().createNativeQuery(sql.toString());
        query.addScalar("typeTestName",new StringType());
        query.addScalar("typeTestCode",new StringType());
        query.addScalar("typeTestValue",new StringType());
        query.setResultTransformer(Transformers.aliasToBean(ApParamTestDTO.class));

        return query.list();
    }

    @Transactional
    public List<ApParamTestDTO> getMenuListTestByParentCode(String parentCode){
        StringBuilder sql = new StringBuilder(" select name as partName, code as partCode, value as partValue, " +
                " parent_code as parentCode from ap_param where ap_param.parent_code = :parentCode " );

        NativeQuery query = currentSession().createNativeQuery(sql.toString());
        query.addScalar("partName",new StringType());
        query.addScalar("partCode",new StringType());
        query.addScalar("partValue",new StringType());
        query.addScalar("parentCode",new StringType());
        query.setResultTransformer(Transformers.aliasToBean(ApParamTestDTO.class));
        query.setParameter("parentCode",parentCode);

        return query.list();
    }

    @Transactional
    public ApParamDTO getScoreListening(){
        StringBuilder sql = new StringBuilder("select value as value from ap_param where type='SCORE_LISTENING' ");

        NativeQuery query = currentSession().createNativeQuery(sql.toString());
        query.addScalar("value", new StringType());
        query.setResultTransformer(Transformers.aliasToBean(ApParamDTO.class));

        return (ApParamDTO) query.uniqueResult();
    }

    @Transactional
    public ApParamDTO getScoreReading(){
        StringBuilder sql = new StringBuilder("select value as value from ap_param where type='SCORE_READING' ");

        NativeQuery query = currentSession().createNativeQuery(sql.toString());
        query.addScalar("value", new StringType());
        query.setResultTransformer(Transformers.aliasToBean(ApParamDTO.class));

        return (ApParamDTO) query.uniqueResult();
    }

    @Transactional
    public List<ApParamDTO> getTypeForHistoryPractices()
    {
        StringBuilder sql = new StringBuilder(
                " select  a.ID id,\n" +
                        "            a.NAME name,\n" +
                        "            a.CODE code,\n" +
                        "            a.VALUE value \n" +
                        "    from AP_PARAM a where a.TYPE = 'TOPIC_TYPE' " +
                        " and a.CODE != 'TRANS_ENG_TO_VIET' " +
                        " and a.CODE != 'TRANS_VIET_TO_ENG' " +
                        " order by name ");
        NativeQuery query = currentSession().createNativeQuery(sql.toString());
        query.addScalar("id",new LongType());
        query.addScalar("name",new StringType());
        query.addScalar("code",new StringType());
        query.addScalar("value",new StringType());
        query.setResultTransformer(Transformers.aliasToBean(ApParamDTO.class));
        return query.list();
    }

    @Transactional
    public List<ApParamDTO> getPartForHistoryPractices(ApparamForGetPartOrTopicDTO apparamForGetPartOrTopicDTO)
    {
        StringBuilder sql = new StringBuilder(
                " select  a.ID id,\n" +
                        "            a.NAME name,\n" +
                        "            a.CODE code,\n" +
                        "            a.PARENT_CODE parentCode\n" +
                        "    from AP_PARAM a where a.TYPE = 'PART_TOPIC' ");

        if (StringUtils.isNotEmpty( apparamForGetPartOrTopicDTO.getValue() )) {
            sql.append( " and a.PARENT_CODE =:parentCode " );
        }

        sql.append( " order by ID " );

        NativeQuery query = currentSession().createNativeQuery(sql.toString());
        query.addScalar("id",new LongType());
        query.addScalar("name",new StringType());
        query.addScalar("code",new StringType());
        query.addScalar("parentCode",new StringType());

        query.setResultTransformer(Transformers.aliasToBean(ApParamDTO.class));
        if(StringUtils.isNotEmpty( apparamForGetPartOrTopicDTO.getValue() )){
            query.setParameter("parentCode", apparamForGetPartOrTopicDTO.getParentCode());
        }
        return query.list();
    }

}
