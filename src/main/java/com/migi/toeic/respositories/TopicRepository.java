package com.migi.toeic.respositories;

import com.migi.toeic.base.ResultDataDTO;
import com.migi.toeic.dto.ApparamForGetPartOrTopicDTO;
import com.migi.toeic.dto.TopicDTO;
import com.migi.toeic.model.Topic;
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

@Repository
@Transactional
public class TopicRepository extends HibernateRepository<Topic, Long> {

	@Transactional
	public ResultDataDTO getListTopic(TopicDTO obj) {

		StringBuilder sql = new StringBuilder(
				"select TOPICS.ID as topicId," +
						" TOPICS.NAME as topicName ," +
						" TOPICS.STATUS as status ," +
						" TOPICS.LEVEL_CODE as levelCode ," +
						" TOPICS.CREATED_TIME as createdTime, " +
						" TOPICS.LAST_UPDATE as lastUpdate, " +
						" TOPICS.CODE as code ," +
						" TOPICS.PART_TOPIC_CODE as partTopicCode ," +
						" TOPICS.TYPE_TOPIC_CODE as typeTopicCode ," +
						" AP_PARAM.NAME as paramName " +
						" from TOPICS left join AP_PARAM " +
						" on TOPICS.PART_TOPIC_CODE = AP_PARAM.CODE " +
						" where 1=1");

		if (StringUtils.isNotEmpty(obj.getTopicName())) {
			sql.append(" and upper(TOPICS.NAME) LIKE upper(:nameSearch) escape '&'  ");
		}
		if (StringUtils.isNotEmpty(obj.getTypeTopicCode())) {
			sql.append(" and upper(TOPICS.TYPE_TOPIC_CODE) LIKE upper(:typeTopicCodeSearch) escape '&'  ");
		}
		if (StringUtils.isNotEmpty(obj.getPartTopicCode())) {
			sql.append(" and upper(TOPICS.PART_TOPIC_CODE) LIKE upper(:partTopicCode) escape '&'  ");
		}

		sql.append(" order by TOPICS.TYPE_TOPIC_CODE,TOPICS.PART_TOPIC_CODE,TOPICS.NAME ");

		StringBuilder sqlCount = new StringBuilder("SELECT COUNT(*) FROM (");
		sqlCount.append(sql.toString());
		sqlCount.append(")");

		NativeQuery query = currentSession().createNativeQuery(sql.toString());
		NativeQuery queryCount = currentSession().createNativeQuery(sqlCount.toString());

		query.addScalar("topicId", new LongType());
		query.addScalar("topicName", new StringType());
		query.addScalar("levelCode", new LongType());
		query.addScalar("status", new LongType());
		query.addScalar("createdTime", new TimestampType());
		query.addScalar("lastUpdate", new TimestampType());
		query.addScalar("code", new StringType());
		query.addScalar("partTopicCode", new StringType());
		query.addScalar("typeTopicCode", new StringType());
		query.addScalar("paramName", new StringType());

		query.setResultTransformer(Transformers.aliasToBean(TopicDTO.class));

		if (obj.getPage() != null && obj.getPageSize() != null) {
			query.setFirstResult((obj.getPage().intValue() - 1) * obj.getPageSize().intValue());
			query.setMaxResults(obj.getPageSize().intValue());
		}

		if (StringUtils.isNotEmpty(obj.getTopicName())) {
			query.setParameter("nameSearch", "%" + ValidateUtils.validateKeySearch(obj.getTopicName()) + "%");
			queryCount.setParameter("nameSearch", "%" + ValidateUtils.validateKeySearch(obj.getTopicName()) + "%");
		}

		if (StringUtils.isNotEmpty(obj.getTypeTopicCode())) {
			query.setParameter("typeTopicCodeSearch", "%" + ValidateUtils.validateKeySearch(obj.getTypeTopicCode()) + "%");
			queryCount.setParameter("typeTopicCodeSearch", "%" + ValidateUtils.validateKeySearch(obj.getTypeTopicCode()) + "%");
		}

		if (StringUtils.isNotEmpty(obj.getPartTopicCode())) {
			query.setParameter("partTopicCode", obj.getPartTopicCode());
			queryCount.setParameter("partTopicCode", obj.getPartTopicCode());
		}

		ResultDataDTO resultDataDTO = new ResultDataDTO();
		List<TopicDTO> lst = query.list();
		resultDataDTO.setData(lst);
		obj.setTotalRecord(((BigDecimal) queryCount.uniqueResult()).intValue());
		resultDataDTO.setTotal(obj.getTotalRecord());
		return resultDataDTO;
	}

	public TopicDTO getDetail(String code) {
		StringBuilder sql = new StringBuilder(
				"select ID as topicId," +
						" NAME as topicName ," +
						" STATUS as status ," +
						" LEVEL_CODE as levelCode ," +
						" CREATED_TIME as createdTime ," +
						" LAST_UPDATE as lastUpdate ," +
						" CODE as code ," +
						" PART_TOPIC_CODE as partTopicCode ," +
						" TYPE_TOPIC_CODE as typeTopicCode " +
						" from TOPICS where upper(CODE) =  upper(:code)");
		NativeQuery query = currentSession().createNativeQuery(sql.toString());

		query.addScalar("topicId", new LongType());
		query.addScalar("topicName", new StringType());
		query.addScalar("levelCode", new LongType());
		query.addScalar("status", new LongType());
		query.addScalar("createdTime", new TimestampType());
		query.addScalar("lastUpdate", new TimestampType());
		query.addScalar("code", new StringType());
		query.addScalar("partTopicCode", new StringType());
		query.addScalar("typeTopicCode", new StringType());

		query.setResultTransformer(Transformers.aliasToBean(TopicDTO.class));

		query.setParameter("code", code);

		return (TopicDTO) query.uniqueResult();
	}

	public TopicDTO checkTopicExist(TopicDTO topicDTO) {
		StringBuilder sql = new StringBuilder(
				"select ID as topicId," +
						" NAME as topicName ," +
						" STATUS as status ," +
						" LEVEL_CODE as levelCode ," +
						" CREATED_TIME as createdTime ," +
						" LAST_UPDATE as lastUpdate, " +
						" CODE as code ," +
						" PART_TOPIC_CODE as partTopicCode ," +
						" TYPE_TOPIC_CODE as typeTopicCode " +
						" from TOPICS " +
						"where upper(NAME) = upper(:topicName) " +
						" and upper(TYPE_TOPIC_CODE) = upper(:typeTopicCode) "
		);
		if(StringUtils.isNotBlank(topicDTO.getPartTopicCode())){
			sql.append(" and upper(PART_TOPIC_CODE) = upper(:partTopicCode)");
		}
		NativeQuery query = currentSession().createNativeQuery(sql.toString());

		query.addScalar("topicId", new LongType());
		query.addScalar("topicName", new StringType());
		query.addScalar("levelCode", new LongType());
		query.addScalar("status", new LongType());
		query.addScalar("createdTime", new DateType());
		query.addScalar("lastUpdate", new DateType());
		query.addScalar("code", new StringType());
		query.addScalar("partTopicCode", new StringType());
		query.addScalar("typeTopicCode", new StringType());

		query.setResultTransformer(Transformers.aliasToBean(TopicDTO.class));

		query.setParameter("topicName", topicDTO.getTopicName());
		query.setParameter("typeTopicCode", topicDTO.getTypeTopicCode());
		if(StringUtils.isNotBlank(topicDTO.getPartTopicCode())){
			query.setParameter("partTopicCode", topicDTO.getPartTopicCode());
		}


		return (TopicDTO) query.uniqueResult();
	}

	public boolean isCheckTopicName(TopicDTO dto) {
		StringBuilder sql = new StringBuilder(
				"select ID FROM TOPICS WHERE upper(NAME)=upper(:topicName) AND upper(TYPE_TOPIC_CODE) =upper(:topicTypeCode) AND id != :topicId");
		NativeQuery query = currentSession().createNativeQuery(sql.toString());

		query.setParameter("topicName", dto.getTopicName());
		query.setParameter("topicId", dto.getTopicId());
		query.setParameter("topicTypeCode", dto.getTypeTopicCode());
		List<Long> lst = query.list();

		if (null != lst && lst.size() > 0) {
			return false;
		}


		return true;
	}


	public List<TopicDTO> getListTopicEasyByPart(String part, String value) {
		StringBuilder sqlTopicLevelEasy = new StringBuilder(
				"select distinct TOPICS.ID topicId , " +
						"  TOPICS.NAME topicName " +
						"  from TOPICS left join CATEGORY on TOPICS.ID = CATEGORY.PARENT_ID  " +
						"  inner join ap_param on CAST(category.type_code AS VARCHAR2(2000)) =  ap_param.value " +
						"  where upper(TOPICS.PART_TOPIC_CODE) LIKE upper(:part) escape '&' and CATEGORY.LEVEL_CODE = 'EASY' " +
						"  AND ap_param.value =:value ");
		NativeQuery queryTopicLevelEasy = currentSession().createNativeQuery(sqlTopicLevelEasy.toString());

		queryTopicLevelEasy.addScalar("topicId", new LongType());
		queryTopicLevelEasy.addScalar("topicName", new StringType());

		queryTopicLevelEasy.setResultTransformer(Transformers.aliasToBean(TopicDTO.class));

		queryTopicLevelEasy.setParameter("part", part);
		queryTopicLevelEasy.setParameter("value", value);

		return queryTopicLevelEasy.list();
	}

	public List<TopicDTO> getListTopicMediumByPart(String part, String value) {
		StringBuilder sqlTopicLevelMedium = new StringBuilder(
				"select distinct TOPICS.ID topicId ," +
						" TOPICS.NAME topicName " +
						" from TOPICS left join CATEGORY on TOPICS.ID = CATEGORY.PARENT_ID  " +
						" inner join ap_param on CAST(category.type_code AS VARCHAR2(2000)) =  ap_param.value " +
						" where upper(TOPICS.PART_TOPIC_CODE) LIKE upper(:part) escape '&' and CATEGORY.LEVEL_CODE = 'MEDIUM' " +
						" AND ap_param.value =:value");
		NativeQuery queryTopicLevelMedium = currentSession().createNativeQuery(sqlTopicLevelMedium.toString());

		queryTopicLevelMedium.addScalar("topicId", new LongType());
		queryTopicLevelMedium.addScalar("topicName", new StringType());

		queryTopicLevelMedium.setResultTransformer(Transformers.aliasToBean(TopicDTO.class));

		queryTopicLevelMedium.setParameter("part", part);
		queryTopicLevelMedium.setParameter("value", value);

		return queryTopicLevelMedium.list();
	}


	public List<TopicDTO> getListTopicDifficultByPart(String part, String value) {
		StringBuilder sqlTopicLevelDifficult = new StringBuilder(
				"select distinct TOPICS.ID topicId ," +
						" TOPICS.NAME topicName " +
						" from TOPICS left join CATEGORY on TOPICS.ID = CATEGORY.PARENT_ID  " +
						" inner join ap_param on CAST(category.type_code AS VARCHAR2(2000)) =  ap_param.value " +
						" where upper(TOPICS.PART_TOPIC_CODE) LIKE upper(:part) escape '&' and CATEGORY.LEVEL_CODE = 'DIFFICULT' " +
						" AND ap_param.value =:value");
		NativeQuery queryTopicLevelDifficult = currentSession().createNativeQuery(sqlTopicLevelDifficult.toString());

		queryTopicLevelDifficult.addScalar("topicId", new LongType());
		queryTopicLevelDifficult.addScalar("topicName", new StringType());

		queryTopicLevelDifficult.setResultTransformer(Transformers.aliasToBean(TopicDTO.class));

		queryTopicLevelDifficult.setParameter("part", part);
		queryTopicLevelDifficult.setParameter("value", value);

		return queryTopicLevelDifficult.list();
	}

	public TopicDTO checkTopicCode(String code) {
		StringBuilder sql = new StringBuilder(
				"select ID as topicId," +
						" NAME as topicName ," +
						" STATUS as status ," +
						" LEVEL_CODE as levelCode ," +
						" CREATED_TIME as createdTime ," +
						" LAST_UPDATE as lastUpdate, " +
						" CODE as code ," +
						" PART_TOPIC_CODE as partTopicCode ," +
						" TYPE_TOPIC_CODE as typeTopicCode " +
						" from TOPICS where upper(CODE) LIKE upper (:code)");
		NativeQuery query = currentSession().createNativeQuery(sql.toString());

		query.addScalar("topicId", new LongType());
		query.addScalar("topicName", new StringType());
		query.addScalar("levelCode", new LongType());
		query.addScalar("status", new LongType());
		query.addScalar("createdTime", new DateType());
		query.addScalar("lastUpdate", new DateType());
		query.addScalar("code", new StringType());
		query.addScalar("partTopicCode", new StringType());
		query.addScalar("typeTopicCode", new StringType());

		query.setResultTransformer(Transformers.aliasToBean(TopicDTO.class));

		query.setParameter("code", code);

		return (TopicDTO) query.uniqueResult();
	}

	@Transactional
	public List<TopicDTO> getTopicName(String part_topic_code, String type_topic_code) {
		StringBuilder sql = new StringBuilder(" select t.NAME topicName, t.CODE code from TOPICS t where t.STATUS =1  ");
		if (StringUtils.isNotBlank(part_topic_code)) {
			sql.append(" and upper(t.PART_TOPIC_CODE) =upper(:partTopicCode)");
		}
		if (StringUtils.isNotBlank(type_topic_code)) {
			sql.append(" and upper(t.TYPE_TOPIC_CODE) =upper(:typeTopicCode)");
		}
		NativeQuery query = currentSession().createSQLQuery(sql.toString());
		query.addScalar("topicName", new StringType());
		query.addScalar("code", new StringType());
		query.setResultTransformer(Transformers.aliasToBean(TopicDTO.class));
		if (StringUtils.isNotBlank(part_topic_code)) {
			query.setParameter("partTopicCode", part_topic_code);
		}
		if (StringUtils.isNotBlank(type_topic_code)) {
			query.setParameter("typeTopicCode", type_topic_code);
		}
		return query.list();

	}

	public TopicDTO getTopicInfor(Long topicId) {
		StringBuilder sql = new StringBuilder(
				"select ID as topicId," +
						" NAME as topicName ," +
						" STATUS as status ," +
						" LEVEL_CODE as levelCode ," +
						" CREATED_TIME as createdTime ," +
						" LAST_UPDATE as lastUpdate ," +
						" CODE as code ," +
						" PART_TOPIC_CODE as partTopicCode ," +
						" TYPE_TOPIC_CODE as typeTopicCode " +
						" from TOPICS where 1=1 and ID = :topicId ");
		NativeQuery query = currentSession().createNativeQuery(sql.toString());

		query.addScalar("topicId", new LongType());
		query.addScalar("topicName", new StringType());
		query.addScalar("levelCode", new LongType());
		query.addScalar("status", new LongType());
		query.addScalar("createdTime", new DateType());
		query.addScalar("lastUpdate", new DateType());
		query.addScalar("code", new StringType());
		query.addScalar("partTopicCode", new StringType());
		query.addScalar("typeTopicCode", new StringType());

		query.setResultTransformer(Transformers.aliasToBean(TopicDTO.class));

		query.setParameter("topicId", topicId);

		return (TopicDTO) query.uniqueResult();
	}

	public List<TopicDTO> getListTypeExercise() {
		StringBuilder sql = new StringBuilder(
				"select ap_param.name as paramName , " +
						"ap_param.type as paramType , " +
						"ap_param.value as paramValue , " +
						"ap_param.code as paramCode " +
						"from AP_PARAM where type='TOPIC_TYPE'");
		NativeQuery query = currentSession().createNativeQuery(sql.toString());

		query.addScalar("paramName", new StringType());
		query.addScalar("paramCode", new StringType());
		query.addScalar("paramType", new StringType());
		query.addScalar("paramValue", new StringType());

		query.setResultTransformer(Transformers.aliasToBean(TopicDTO.class));

		return query.list();
	}

	public List<TopicDTO> getListPartExercise(String typeExercise) {
		StringBuilder sql = new StringBuilder(
				"select ap1.id as paramId , ap1.name as paramName , ap1.code as paramCode , ap1.value as paramValue , ap1.parent_code as paramParentCode " +
						"from ap_param ap1 left join ap_param ap2 on ap1.parent_code = ap2.code " +
						"where ap2.value = :typeExercise " +
						"UNION all " +
						"select topics.id as paramId , topics.name as paramName, topics.code as paramCode , topics.part_topic_code as paramValue , topics.type_topic_code as paramParentCode " +
						"from topics left join ap_param on topics.type_topic_code = ap_param.code " +
						"where topics.part_topic_code is null and ap_param.value = :typeExercise  order by paramCode ");
		NativeQuery query = currentSession().createNativeQuery(sql.toString());

		query.addScalar("paramId", new LongType());
		query.addScalar("paramName", new StringType());
		query.addScalar("paramCode", new StringType());
		query.addScalar("paramValue", new StringType());
		query.addScalar("paramParentCode", new StringType());

		query.setResultTransformer(Transformers.aliasToBean(TopicDTO.class));

		query.setParameter("typeExercise", typeExercise);

		return query.list();
	}

	public List<TopicDTO> getListPractices() {
		StringBuilder sql = new StringBuilder(
				"select id as paramId, name as paramName , code as paramCode , value as paramValue , parent_code as paramParentCode,ord AS ord\n" +
						"from ( \n" +
						"\tselect ap_param.id, ap_param.name , ap_param.code , ap_param.value , ap_param.parent_code,ap_param.ORD from ap_param \n" +
						"\twhere type like 'TOPIC_TYPE' OR type like 'PART_TOPIC') t1  \n" +
						"UNION all select a1.id as paramId, a1.name as paramName, a1.code as paramCode , a2.value as paramValue, a1.parent_code as paramParentCode,a1.ORD \n" +
						"\tfrom ap_param a1 \n" +
						"\tleft join ap_param a2 on a1.parent_code = a2.code  \n" +
						"where a1.value is null  order by ord,paramCode");

		NativeQuery query = currentSession().createNativeQuery(sql.toString());

		query.addScalar("paramId", new LongType());
		query.addScalar("paramName", new StringType());
		query.addScalar("paramCode", new StringType());
		query.addScalar("paramValue", new StringType());
		query.addScalar("paramParentCode", new StringType());
		query.addScalar("ord", new LongType());

		query.setResultTransformer(Transformers.aliasToBean(TopicDTO.class));

		return query.list();
	}

	public List<TopicDTO> getListTopicByPart(String part) {
		StringBuilder sql = new StringBuilder(
				"select ID as topicId , Name as topicName " +
						" from topics where upper(topics.part_topic_code) = :partExercise");

		NativeQuery query = currentSession().createNativeQuery(sql.toString());

		query.addScalar("topicId", new LongType());
		query.addScalar("topicName", new StringType());

		query.setResultTransformer(Transformers.aliasToBean(TopicDTO.class));

		query.setParameter("partExercise", part);

		return query.list();
	}

	//for minitest
	public TopicDTO getRandomTopicByPartTopicCode(String partTopicCode) {
		StringBuilder sql = new StringBuilder(
				"select \n" +
						"                    ID as topicId,\n" +
						"                    NAME as topicName ,\n" +
						"                    PART_TOPIC_CODE as partTopicCode \n" +
						"                    from(\n" +
						"                        select\n" +
						"                            ID ,\n" +
						"                            NAME  ,\n" +
						"                            PART_TOPIC_CODE   \n" +
						"                            from TOPICS where PART_TOPIC_CODE = :partTopicCode \n" +
						"                            order by DBMS_RANDOM.RANDOM\n" +
						"                    )\n" +
						"                    where rownum < = 1 "
		);
		NativeQuery query = currentSession().createNativeQuery(sql.toString());

		query.addScalar("topicId", new LongType());
		query.addScalar("topicName", new StringType());
		query.addScalar("partTopicCode", new StringType());
		query.setResultTransformer(Transformers.aliasToBean(TopicDTO.class));
		query.setParameter("partTopicCode", partTopicCode);

		return (TopicDTO) query.uniqueResult();
	}
	//end for minitest

	//Get topic for history practices
	public List<TopicDTO> getListTopicsForHistoryPractices(ApparamForGetPartOrTopicDTO apparamForGetPartOrTopicDTO) {
		StringBuilder sql = new StringBuilder(
				"select  t.ID topicId,\n" +
						"            t.NAME topicName\n" +
						"    from TOPICS t where t.STATUS = 1  ");

		if (StringUtils.isNotEmpty(apparamForGetPartOrTopicDTO.getType())) {
			sql.append(" and t.TYPE_TOPIC_CODE =:type ");
		}
		if (StringUtils.isNotEmpty(apparamForGetPartOrTopicDTO.getPart())) {
			sql.append(" and t.PART_TOPIC_CODE =:part ");
		}

		sql.append(" order by topicName ");

		NativeQuery query = currentSession().createNativeQuery(sql.toString());

		query.addScalar("topicId", new LongType());
		query.addScalar("topicName", new StringType());

		query.setResultTransformer(Transformers.aliasToBean(TopicDTO.class));
		if (StringUtils.isNotEmpty(apparamForGetPartOrTopicDTO.getType())) {
			query.setParameter("type", apparamForGetPartOrTopicDTO.getType());
		}
		if (StringUtils.isNotEmpty(apparamForGetPartOrTopicDTO.getPart())) {
			query.setParameter("part", apparamForGetPartOrTopicDTO.getPart());
		}

		return query.list();
	}
	//End get topic for history practices

	public List<TopicDTO> getTopicByNameAndPart(TopicDTO topicDTO) {
		StringBuilder sql = new StringBuilder(" select t.ID topicId from TOPICS t " +
				"where t.name =:topicName and t.PART_TOPIC_CODE =:partTopic ");
		NativeQuery query = currentSession().createSQLQuery(sql.toString());
		query.addScalar("topicId", new LongType());
		query.setResultTransformer(Transformers.aliasToBean(TopicDTO.class));
		query.setParameter("topicName", topicDTO.getTopicName());
		query.setParameter("partTopic", topicDTO.getLevelCode());
		return query.list();
	}
}
