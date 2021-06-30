package com.migi.toeic.respositories;

import com.migi.toeic.dto.HistoryMinitestDTO;
import com.migi.toeic.dto.HistoryPracticesDTO;
import com.migi.toeic.model.HistoryPractices;
import com.migi.toeic.respositories.common.HibernateRepository;
import com.migi.toeic.utils.ValidateUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.query.NativeQuery;
import org.hibernate.transform.Transformers;
import org.hibernate.type.LongType;
import org.hibernate.type.StringType;
import org.hibernate.type.TimestampType;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;


@Transactional
@Repository
public class HistoryPracticesRepository extends HibernateRepository<HistoryPractices,Long> {

    public List<HistoryPracticesDTO> doSearch(HistoryPracticesDTO historyPracticesDTO){
        StringBuilder sql = new StringBuilder(
                "select t.id, \n" +
                        "            t.createDate, \n" +
                        "            t.typeCode, \n" +
                        "            t.typeName, \n" +
                        "            t.part, \n" +
                        "            a2.NAME partName, \n" +
                        "            t.levelCode, \n" +
                        "            t.topicId, \n" +
                        "            t.topicName,\n" +
                        "            t.numberCorrect, \n" +
                        "            t.userId \n" +
                        "    from(\n" +
                        "        select  h.ID id,\n" +
                        "                h.CREATED_DATE createDate,\n" +
                        "                h.TYPE_CODE typeCode,\n" +
                        "                a.NAME typeName,\n" +
                        "                h.PART part,\n" +
                        "                h.LEVEL_CODE levelCode,\n" +
                        "                h.TOPIC_ID topicId,\n" +
                        "                t.NAME topicName,\n" +
                        "                h.NUMBER_CORRECT numberCorrect,\n" +
                        "                h.USER_ID userId\n" +
                        "                from HISTORY_PRACTICES h inner join AP_PARAM a on h.TYPE_CODE = a.VALUE \n" +
                        "                    inner join TOPICS t on h.TOPIC_ID = t.ID\n" +
                        "    ) t inner join AP_PARAM a2 on part = a2.CODE where t.userId =:userId " );

        if (StringUtils.isNotEmpty( historyPracticesDTO.getTypeCode() )) {
            sql.append( " and t.typeCode =:typeCode " );
        }
        if (StringUtils.isNotEmpty( historyPracticesDTO.getPart() )) {
            sql.append( " and t.part = :part " );
        }
        if (null != historyPracticesDTO.getTopicId() && historyPracticesDTO.getTopicId() != 0) {
            sql.append( " and t.topicId =:topicId  " );
        }
        if (StringUtils.isNotEmpty( historyPracticesDTO.getLevelCode() )) {
            sql.append( " and upper(t.levelCode) LIKE upper(:levelCode) escape '&'  " );
        }

        if ( historyPracticesDTO.getCreateFrom() == null && historyPracticesDTO.getCreateTo() != null){
            sql.append(" and  TO_CHAR(t.createDate, 'YYYYMMDD') <= :createTo ");
        }
        if ( historyPracticesDTO.getCreateTo() == null && historyPracticesDTO.getCreateFrom() != null){
            sql.append(" and  TO_CHAR(t.createDate, 'YYYYMMDD') >= :createFrom ");
        }
        if ( historyPracticesDTO.getCreateTo() != null && historyPracticesDTO.getCreateFrom() != null){
            sql.append(" and  TO_CHAR(t.createDate, 'YYYYMMDD') BETWEEN  :createFrom and  :createTo ");
        }
        sql.append(" order by t.createDate desc ");
        StringBuilder sqlCount = new StringBuilder("SELECT COUNT(*) FROM ( ");
        sqlCount.append(sql.toString());
        sqlCount.append(" )");

        NativeQuery query = currentSession().createNativeQuery(sql.toString());
        NativeQuery queryCount = currentSession().createNativeQuery(sqlCount.toString());

        query.addScalar("id",new LongType());
        query.addScalar("createDate",new TimestampType());
        query.addScalar("typeCode",new StringType());
        query.addScalar("typeName",new StringType());
        query.addScalar("part",new StringType());
        query.addScalar("partName",new StringType());
        query.addScalar("levelCode",new StringType());
        query.addScalar("topicId",new LongType());
        query.addScalar("topicName",new StringType());
        query.addScalar("numberCorrect",new StringType());
        query.addScalar("userId",new LongType());


        query.setResultTransformer(Transformers.aliasToBean(HistoryPracticesDTO.class));

        if(historyPracticesDTO.getUserId() != null ){
            query.setParameter("userId", historyPracticesDTO.getUserId());
            queryCount.setParameter("userId", historyPracticesDTO.getUserId());
        }
        if (StringUtils.isNotEmpty( historyPracticesDTO.getTypeCode() )) {
            query.setParameter( "typeCode", historyPracticesDTO.getTypeCode() );
            queryCount.setParameter( "typeCode", historyPracticesDTO.getTypeCode() );
        }
        if (StringUtils.isNotEmpty( historyPracticesDTO.getPart() )) {
            query.setParameter( "part", historyPracticesDTO.getPart() );
            queryCount.setParameter( "part", historyPracticesDTO.getPart() );
        }
        if ( null != historyPracticesDTO.getTopicId() && historyPracticesDTO.getTopicId() != 0 ) {
            query.setParameter( "topicId", historyPracticesDTO.getTopicId() );
            queryCount.setParameter( "topicId", historyPracticesDTO.getTopicId() );
        }
        if (StringUtils.isNotEmpty( historyPracticesDTO.getLevelCode() )) {
            query.setParameter( "levelCode", "%" + ValidateUtils.validateKeySearch(historyPracticesDTO.getLevelCode()) + "%" );
            queryCount.setParameter( "levelCode", "%" + ValidateUtils.validateKeySearch(historyPracticesDTO.getLevelCode()) + "%" );
        }
        if (historyPracticesDTO.getPage() != null && historyPracticesDTO.getPageSize() != null) {
            query.setFirstResult((historyPracticesDTO.getPage().intValue() - 1) * historyPracticesDTO.getPageSize().intValue());
            query.setMaxResults(historyPracticesDTO.getPageSize().intValue());
        }

        if (StringUtils.isNotBlank(historyPracticesDTO.getKeySearch())) {
            query.setParameter("keySearch", "%" + ValidateUtils.validateKeySearch(historyPracticesDTO.getKeySearch()) + "%");
            queryCount.setParameter("keySearch", "%" + ValidateUtils.validateKeySearch(historyPracticesDTO.getKeySearch()) + "%");
        }

        if ( historyPracticesDTO.getCreateFrom() == null && historyPracticesDTO.getCreateTo() != null){
            query.setParameter("createTo", historyPracticesDTO.getCreateToString());
            queryCount.setParameter("createTo", historyPracticesDTO.getCreateToString());
        }
        if ( historyPracticesDTO.getCreateTo() == null && historyPracticesDTO.getCreateFrom() != null){
            query.setParameter("createFrom", historyPracticesDTO.getCreateFromString());
            queryCount.setParameter("createFrom", historyPracticesDTO.getCreateFromString());
        }
        if ( historyPracticesDTO.getCreateTo() != null && historyPracticesDTO.getCreateFrom() != null){
            query.setParameter("createFrom", historyPracticesDTO.getCreateFromString());
            queryCount.setParameter("createFrom", historyPracticesDTO.getCreateFromString());
            query.setParameter("createTo", historyPracticesDTO.getCreateToString());
            queryCount.setParameter("createTo", historyPracticesDTO.getCreateToString());
        }

        List<HistoryPracticesDTO> lst = query.list();
        if(lst.size() > 0){
            lst.get(0).setNumberTest(((BigDecimal) queryCount.uniqueResult()).intValue());
        }
        return lst;

    }

    public HistoryPracticesDTO getMaxHistoryPracticesById(){
        StringBuilder sql = new StringBuilder(
                "select id,\n" +
                        "            createDate,\n" +
                        "            typeCode,\n" +
                        "            part,\n" +
                        "            levelCode,\n" +
                        "            topicId,\n" +
                        "            numberCorrect,\n" +
                        "            userId\n" +
                        "            from ( select HISTORY_PRACTICES.ID id,\n" +
                        "                            HISTORY_PRACTICES.CREATED_DATE createDate,\n" +
                        "                            HISTORY_PRACTICES.TYPE_CODE typeCode,\n" +
                        "                            HISTORY_PRACTICES.PART part,\n" +
                        "                            HISTORY_PRACTICES.LEVEL_CODE levelCode,\n" +
                        "                            HISTORY_PRACTICES.TOPIC_ID topicId,\n" +
                        "                            HISTORY_PRACTICES.NUMBER_CORRECT numberCorrect,\n" +
                        "                            HISTORY_PRACTICES.USER_ID userId\n" +
                        "                     from HISTORY_PRACTICES order by ID desc ) where rownum <= 1 ");

        NativeQuery query = currentSession().createNativeQuery(sql.toString());
        query.addScalar("id",new LongType());
        query.addScalar("topicId",new LongType());
        query.addScalar("createDate",new TimestampType());
        query.addScalar("typeCode",new StringType());
        query.addScalar("part",new StringType());
        query.addScalar("levelCode",new StringType());
        query.addScalar("numberCorrect",new StringType());
        query.addScalar("userId",new LongType());
        query.setResultTransformer(Transformers.aliasToBean(HistoryPracticesDTO.class));
        return (HistoryPracticesDTO) query.uniqueResult();
    }




}
