package com.migi.toeic.respositories;

import com.migi.toeic.base.ResultDataDTO;
import com.migi.toeic.dto.*;
import com.migi.toeic.model.Category;
import com.migi.toeic.respositories.common.HibernateRepository;
import com.migi.toeic.utils.ValidateUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.query.NativeQuery;
import org.hibernate.transform.Transformers;
import org.hibernate.type.FloatType;
import org.hibernate.type.LongType;
import org.hibernate.type.StringType;
import org.hibernate.type.TimestampType;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;

@Repository
@Transactional
public class CategoryRepository extends HibernateRepository<Category, Long> {

	@Transactional
	public List<CategoryDTO> getLevelCode() {
		StringBuilder sql = new StringBuilder("select distinct c.LEVEL_CODE as levelCode " +
				"from category c");
		NativeQuery query = currentSession().createNativeQuery(sql.toString());
		query.addScalar("levelCode", new StringType());
		query.setResultTransformer(Transformers.aliasToBean(CategoryDTO.class));
		return query.list();
	}

	@Transactional
	public ResultDataDTO getListCategory(CategoryDTO obj) {

		StringBuilder sql = new StringBuilder(
				"SELECT c.id categoryId,\n" +
						"       tmp.nameType nameType,\n" +
						"       tmp.CODE code,\n" +
						"       tmp.nameTopic nameTopic,\n" +
						"       tmp.part part,\n" +
						"       tmp.codeTopic codeTopic,\n" +
						"       ap3.nameType namePart,\n" +
						"       c.LEVEL_CODE levelCode,\n" +
						"       c.NAME nameCategory,\n" +
						"       c.created_date createdDate,\n" +
						"       c.updated_date updatedDate,\n" +
						"       c.STATUS status, c.parent_id parentId , c.type_code typeCode  \n" +
						"FROM\n" +
						"  (SELECT t.id ID,\n" +
						"          ap.nameType nameType,\n" +
						"          t.NAME nameTopic,\n" +
						"          t.code codeTopic,\n" +
						"          t.part_topic_code part,\n" +
						"          ap.CODE code, ap.VALUE value\n" +
						"   FROM\n" +
						"     (SELECT a.NAME nameType,\n" +
						"             a.CODE code, a.VALUE value \n" +
						"      FROM ap_param a\n" +
						"      WHERE a.STATUS =1\n" +
						"        AND upper(a.TYPE) = upper('topic_type')) ap\n" +
						"   LEFT JOIN topics t ON ap.CODE = t.type_topic_code) tmp\n" +
						"LEFT JOIN\n" +
						"  (SELECT a.NAME nameType,\n" +
						"          a.CODE code\n" +
						"   FROM ap_param a\n" +
						"   WHERE status =1\n" +
						"     AND a.type= upper('PARt_topic')) ap3 ON tmp.part = ap3.code\n" +
						"LEFT JOIN category c ON tmp.id = c.parent_id and tmp.VALUE = c.type_code \n" +
						"WHERE c.NAME IS NOT NULL ");

		if (StringUtils.isNotEmpty(obj.getKeySearch())) {
			sql.append(" and upper(c.NAME) LIKE upper(:nameSearch) escape '&'  ");
		}
		if (obj.getStatus() != null) {
			if (obj.getStatus() != 2L) {
				sql.append(" and c.STATUS =:status ");
			}
		}
		if (StringUtils.isNotBlank(obj.getLevelCode())) {
			sql.append(" and upper(c.LEVEL_CODE) =upper(:levelCode) ");
		}
		if (StringUtils.isNotBlank(obj.getPart())) {
			sql.append(" and upper(tmp.PART) = upper(:part) ");
		}
		if (StringUtils.isNotBlank(obj.getCode())) {
			sql.append(" and upper(tmp.CODE) = upper(:code)");
		}
		if (StringUtils.isNotBlank(obj.getCodeTopic())) {
			sql.append(" and upper(tmp.codeTopic) = upper(:codeTopic)");
		}

		//------------------------
        /*
        if(obj.getCreatedDate() != null && obj.getUpdatedDate() !=null)
        {
            sql.append(" and ( c.CREATED_DATE between :createDateTo and :createDateFrom ) ");
        }
        if(obj.getCreatedDate() != null && obj.getUpdatedDate() ==null)
        {
            sql.append(" and c.CREATED_DATE  <=:createDateTo ");
        }
        if(obj.getCreatedDate() == null && obj.getUpdatedDate() !=null)
        {
            sql.append(" and c.CREATED_DATE  >=:createDateFrom ");
        }
        */

		if (obj.getCreatedDateFrom() != null && obj.getCreatedDateTo() != null) {
			sql.append(" and ( TO_CHAR(c.CREATED_DATE, 'YYYYMMDD') between :createDateFrom and :createDateTo ) ");
		}
		if (obj.getCreatedDateTo() != null && obj.getCreatedDateFrom() == null) {
			sql.append(" and TO_CHAR(c.CREATED_DATE, 'YYYYMMDD')  <=:createDateTo ");
		}
		if (obj.getCreatedDateTo() == null && obj.getCreatedDateFrom() != null) {
			sql.append(" and TO_CHAR(c.CREATED_DATE, 'YYYYMMDD')  >=:createDateFrom ");
		}
		//--------------------------

		if (obj.getTypeCode() != null) {
			sql.append(" and c.type_code = :typeCode ");
		}
		sql.append(" order by c.id");

		StringBuilder sqlCount = new StringBuilder("SELECT COUNT(*) FROM (");
		sqlCount.append(sql.toString());
		sqlCount.append(")");

		NativeQuery query = currentSession().createNativeQuery(sql.toString());
		NativeQuery queryCount = currentSession().createNativeQuery(sqlCount.toString());

		query.addScalar("categoryId", new LongType());
		query.addScalar("nameType", new StringType());
		query.addScalar("code", new StringType());
		query.addScalar("nameTopic", new StringType());
		query.addScalar("part", new StringType());
		query.addScalar("codeTopic", new StringType());
		query.addScalar("namePart", new StringType());
		query.addScalar("levelCode", new StringType());
		query.addScalar("nameCategory", new StringType());
		query.addScalar("createdDate", new TimestampType());
		query.addScalar("updatedDate", new TimestampType());
		query.addScalar("status", new LongType());
		query.addScalar("parentId", new LongType());
		query.addScalar("typeCode", new LongType());

		query.setResultTransformer(Transformers.aliasToBean(CategoryDTO.class));

		if (obj.getPage() != null && obj.getPageSize() != null) {
			query.setFirstResult((obj.getPage().intValue() - 1) * obj.getPageSize().intValue());
			query.setMaxResults(obj.getPageSize().intValue());
		}
		if (StringUtils.isNotEmpty(obj.getKeySearch())) {
			query.setParameter("nameSearch", "%" + ValidateUtils.validateKeySearch(obj.getKeySearch()) + "%");
			queryCount.setParameter("nameSearch", "%" + ValidateUtils.validateKeySearch(obj.getKeySearch()) + "%");
		}
		if (obj.getStatus() != null) {
			if (obj.getStatus() != 2) {
				query.setParameter("status", obj.getStatus());
				queryCount.setParameter("status", obj.getStatus());
			}
		}
		if (StringUtils.isNotBlank(obj.getLevelCode())) {
			query.setParameter("levelCode", obj.getLevelCode());
			queryCount.setParameter("levelCode", obj.getLevelCode());
		}
		if (StringUtils.isNotBlank(obj.getPart())) {
			query.setParameter("part", obj.getPart());
			queryCount.setParameter("part", obj.getPart());
		}
		if (StringUtils.isNotBlank(obj.getCode())) {
			query.setParameter("code", obj.getCode());
			queryCount.setParameter("code", obj.getCode());
		}
		if (StringUtils.isNotBlank(obj.getCodeTopic())) {
			query.setParameter("codeTopic", obj.getCodeTopic());
			queryCount.setParameter("codeTopic", obj.getCodeTopic());
		}
		//-----------------------------
		if (obj.getCreatedDateFrom() != null && obj.getCreatedDateTo() != null) {
			query.setParameter("createDateFrom", obj.getCreatedDateFromString());
			query.setParameter("createDateTo", obj.getCreatedDateToString());
			queryCount.setParameter("createDateFrom", obj.getCreatedDateFromString());
			queryCount.setParameter("createDateTo", obj.getCreatedDateToString());
		}
		if (obj.getCreatedDateTo() != null && obj.getCreatedDateFrom() == null) {
			query.setParameter("createDateTo", obj.getCreatedDateToString());
			queryCount.setParameter("createDateTo", obj.getCreatedDateToString());
		}
		if (obj.getCreatedDateTo() == null && obj.getCreatedDateFrom() != null) {
			query.setParameter("createDateFrom", obj.getCreatedDateFromString());
			queryCount.setParameter("createDateFrom", obj.getCreatedDateFromString());
		}
		//----------------------------
		if (obj.getTypeCode() != null) {
			query.setParameter("typeCode", obj.getTypeCode().toString());
			queryCount.setParameter("typeCode", obj.getTypeCode().toString());
		}
		ResultDataDTO resultDataDTO = new ResultDataDTO();
		List<CategoryDTO> lst = query.list();
		resultDataDTO.setData(lst);
		obj.setTotalRecord(((BigDecimal) queryCount.uniqueResult()).intValue());
		resultDataDTO.setTotal(obj.getTotalRecord());
		return resultDataDTO;
	}

	public List<QuestionAnswersDTO> getListQuestionByCategory(Long id) {
		StringBuilder sqlGetListQuestionByCategory = new StringBuilder(
				"select id as id , name as name , parent_id as parentId , status as status , description as description , Answers_To_Choose as answersToChoose , answer as answer , num_of_answer as numOfAnswer , null as startTime , score as score , null as typeFile1 , null as typeFile2 , null as pathFile1 , null as pathFile2, null as transcript " +
						"from question_answer_reading where parent_id = :parentId " +
						"union all " +
						"select id as id , name as name , parent_id as parentId , status as status , description as description , Answers_To_Choose as answersToChoose , answer as answer , num_of_answer as numOfAnswer , start_time as startTime , score as score , type_file_1 as typeFile1 , type_file_2 as typeFile2 , question_answer_listening.part_file_1 as pathFile1 , question_answer_listening.part_file_2 as pathFile2, transcript as transcript " +
						"from question_answer_listening where parent_id = :parentId " +
						"union all " +
						"select id as id , name as name , parent_id as parentId , status as status , description as description , null as answersToChoose , answer as answer , null as numOfAnswer , null as startTime , score as score , null as typeFile1 , null as typeFile2 , null as pathFile1 , null as pathFile2, null as transcript " +
						"from q_a_translation_a_v where parent_id = :parentId " +
						"union all " +
						"select id as id , name as name , parent_id as parentId , status as status , description as description , null as answersToChoose , answer as answer , null as numOfAnswer , null as startTime , score as score , null as typeFile1 , null as typeFile2 , null as pathFile1 , null as pathFile2, null as transcript " +
						"from q_a_translation_v_a where parent_id = :parentId");

		NativeQuery queryGetListQuestionByCategory = currentSession().createNativeQuery(sqlGetListQuestionByCategory.toString());

		queryGetListQuestionByCategory.addScalar("id", new LongType());
		queryGetListQuestionByCategory.addScalar("name", new StringType());
		queryGetListQuestionByCategory.addScalar("parentId", new LongType());
		queryGetListQuestionByCategory.addScalar("status", new LongType());
		queryGetListQuestionByCategory.addScalar("description", new StringType());
		queryGetListQuestionByCategory.addScalar("numOfAnswer", new LongType());
		queryGetListQuestionByCategory.addScalar("answersToChoose", new StringType());
		queryGetListQuestionByCategory.addScalar("answer", new StringType());
		queryGetListQuestionByCategory.addScalar("numOfAnswer", new LongType());
		queryGetListQuestionByCategory.addScalar("startTime", new StringType());
		queryGetListQuestionByCategory.addScalar("score", new FloatType());
		queryGetListQuestionByCategory.addScalar("typeFile1", new StringType());
		queryGetListQuestionByCategory.addScalar("typeFile2", new StringType());
		queryGetListQuestionByCategory.addScalar("pathFile1", new StringType());
		queryGetListQuestionByCategory.addScalar("pathFile2", new StringType());
		queryGetListQuestionByCategory.addScalar("transcript", new StringType());

		queryGetListQuestionByCategory.setResultTransformer(Transformers.aliasToBean(QuestionAnswersDTO.class));

		queryGetListQuestionByCategory.setParameter("parentId", id);

		List<QuestionAnswersDTO> lstQuestion = queryGetListQuestionByCategory.list();

		return lstQuestion;
	}

	public List<CategoryDTO> getListTypeDataInput() {
		StringBuilder sql = new StringBuilder(
				"SELECT name as paramName , " +
						" type as paramType , " +
						" code as paramCode " +
						" FROM ap_param where upper(type) LIKE 'DATA_INPUT'");
		NativeQuery query = currentSession().createNativeQuery(sql.toString());

		query.addScalar("paramName", new StringType());
		query.addScalar("paramType", new StringType());
		query.addScalar("paramCode", new StringType());

		query.setResultTransformer(Transformers.aliasToBean(CategoryDTO.class));

		return query.list();
	}

	public List<CategoryDTO> getListTypeLevel() {
		StringBuilder sql = new StringBuilder(
				"SELECT name as paramName , " +
						" type as paramType , " +
						" code as paramCode " +
						" FROM ap_param where upper(type) LIKE 'LEVEL_CODE' escape '&' ");
		NativeQuery query = currentSession().createNativeQuery(sql.toString());

		query.addScalar("paramName", new StringType());
		query.addScalar("paramType", new StringType());
		query.addScalar("paramCode", new StringType());

		query.setResultTransformer(Transformers.aliasToBean(CategoryDTO.class));

		return query.list();
	}

	public Boolean checkCategoryInHistoryFullTest(CategoryDTO categoryDTO) {
		StringBuilder sql = new StringBuilder("Select c.ID id " +
				"from details_history h inner join category c on h.category_id = c.id " +
				"where c.id =:id");
		NativeQuery query = currentSession().createNativeQuery(sql.toString());
		query.addScalar("id", new LongType());

		query.setResultTransformer(Transformers.aliasToBean(CategoryDTO.class));

		query.setParameter("id", categoryDTO.getTypeCode());

		return query.list().isEmpty();
	}

	public Boolean checkCategoryInHistoryReading(CategoryDTO categoryDTO) {
		StringBuilder sql = new StringBuilder("Select c.ID id " +
				"from detail_history_reading h inner join category c on h.category_id = c.id " +
				"where c.id =:id");
		NativeQuery query = currentSession().createNativeQuery(sql.toString());
		query.addScalar("id", new LongType());

		query.setResultTransformer(Transformers.aliasToBean(CategoryDTO.class));

		query.setParameter("id", categoryDTO.getTypeCode());

		return query.list().isEmpty();
	}

	public Boolean checkCategoryInHistoryMiniTest(CategoryDTO categoryDTO) {
		StringBuilder sql = new StringBuilder("Select c.ID id " +
				"from detail_history_minitest h inner join category c on h.category_id = c.id " +
				"where c.id =:id");
		NativeQuery query = currentSession().createNativeQuery(sql.toString());
		query.addScalar("id", new LongType());

		query.setResultTransformer(Transformers.aliasToBean(CategoryDTO.class));

		query.setParameter("id", categoryDTO.getTypeCode());

		return query.list().isEmpty();
	}

	public Boolean checkCategoryInHistoryListenFill(CategoryDTO categoryDTO) {
		StringBuilder sql = new StringBuilder("Select c.ID id " +
				"from detail_history_listen_fill h inner join category c on h.category_id = c.id " +
				"where c.id =:id");
		NativeQuery query = currentSession().createNativeQuery(sql.toString());
		query.addScalar("id", new LongType());

		query.setResultTransformer(Transformers.aliasToBean(CategoryDTO.class));

		query.setParameter("id", categoryDTO.getTypeCode());

		return query.list().isEmpty();
	}

	public List<QuestionOfReadingAndComplitingDTO> getListQuestionOfReadingAndCompliting(String topicName, String levelCode, int number) {
		StringBuilder sql = new StringBuilder("select DISTINCT * from ( \n" +
				"SELECT  c.ID idCategory,\n" +
				" c.NAME nameCategory,\n" +
				" c.TYPE_FILE_1 typeFile1,\n" +
				" c.TYPE_FILE_2 typeFile2,\n" +
				" c.LEVEL_CODE levelCode,\n" +
				" c.TRANSCRIPT transcript,\n" +
				" c.PATH_FILE_1 pathFile1,\n" +
				" c.PATH_FILE_2 pathFile2 \n" +
				" FROM CATEGORY c\n" +
				" inner join QUESTION_ANSWER_READING qa on qa.PARENT_ID = c.ID \n" +
				" WHERE c.PARENT_ID  in (SELECT id FROM TOPICS t WHERE upper(t.NAME) =upper(:topicName) AND t.STATUS = 1) and c.level_code = :levelCode \n" +
				" AND   qa.STATUS = 1 AND c.STATUS = 1\n" +
				" order by DBMS_RANDOM.RANDOM) WHERE ROWNUM <= :number ");
		NativeQuery query = currentSession().createNativeQuery(sql.toString());
		query.addScalar("idCategory", new LongType());
		query.addScalar("nameCategory", new StringType());
		query.addScalar("typeFile1", new StringType());
		query.addScalar("typeFile2", new StringType());
		query.addScalar("levelCode", new StringType());
		query.addScalar("transcript", new StringType());
		query.addScalar("pathFile1", new StringType());
		query.addScalar("pathFile2", new StringType());
//        query.addScalar("idQuestionAnswerReading",new LongType());
//        query.addScalar("nameQuestionAnswerReading",new StringType());
//        query.addScalar("description",new StringType());
//        query.addScalar("answersToChoose",new StringType());
		query.setResultTransformer(Transformers.aliasToBean(QuestionOfReadingAndComplitingDTO.class));
		query.setParameter("topicName", topicName);
		query.setParameter("levelCode", levelCode);
		query.setParameter("number", number);

		List<QuestionOfReadingAndComplitingDTO> lstQuest = query.list();
		return lstQuest;
	}

	public ResultDataDTO getListQuestionsOfRead(Long categoryId) {

		StringBuilder sql = new StringBuilder(
				"select ID as id," +
						" NAME as name ," +
						" PARENT_ID as parentId," +
						" NUM_OF_ANSWER as numOfAnswer," +
						" ANSWERS_TO_CHOOSE as answersToChoose ," +
						" DESCRIPTION as description," +
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
		query.addScalar("description", new StringType());
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
		return resultDataDTO;
	}

	public List<QuestionOfReadingAndComplitingDTO> getResultQuestionOfReadingAndCompliting(QuestionOfReadingAndComplitingDTO questionAnswersReadingDTO) {
		StringBuilder sql = new StringBuilder("select qa.id id, " +
				"qa.name nameQuestionAnswerReading, qa.answer answer, qa.answers_to_choose answersToChoose," +
				" qa.description description, qa.score score " +
				"from QUESTION_ANSWER_READING qa where qa.PARENT_ID =:idCategory " +
				" order by qa.name");
		NativeQuery query = currentSession().createNativeQuery(sql.toString());
		query.addScalar("id", new LongType());
		query.addScalar("nameQuestionAnswerReading", new StringType());
		query.addScalar("answer", new StringType());
		query.addScalar("answersToChoose", new StringType());
		query.addScalar("description", new StringType());
		query.addScalar("score", new FloatType());
		query.setResultTransformer(Transformers.aliasToBean(QuestionOfReadingAndComplitingDTO.class));
		query.setParameter("idCategory", questionAnswersReadingDTO.getParentId());
		return query.list();
	}

	public List<CategoryDTO> getListCategoryWithTotalScoreAndTotalQuestion(Long typeCode) {
		StringBuilder sqlListening = new StringBuilder(
				"SELECT\n" +
						"\tCATEGORY.ID categoryId,\n" +
						"\tCATEGORY.NAME nameCategory,\n" +
						"\tCATEGORY.LEVEL_CODE levelCode,\n" +
						"\tCATEGORY.TYPE_CODE typeCode,\n" +
						"\tTOPICS.NAME nameTopic,\n" +
						"\tAP_PARAM.NAME typeName,\n" +
						"\tcount(*) countQuestion,\n" +
						"\tSUM(QUESTION_ANSWER_LISTENING.SCORE) countScore\n" +
						"FROM QUESTION_ANSWER_LISTENING INNER JOIN CATEGORY\n" +
						"\tON QUESTION_ANSWER_LISTENING.PARENT_ID = CATEGORY.ID\n" +
						"INNER JOIN TOPICS \n" +
						"\tON TOPICS.ID = CATEGORY.PARENT_ID\n" +
						"INNER JOIN AP_PARAM\n" +
						"\tON AP_PARAM.VALUE = TO_NCHAR(CATEGORY.TYPE_CODE)\n" +
						"GROUP BY (CATEGORY.ID,CATEGORY.NAME,CATEGORY.LEVEL_CODE,CATEGORY.TYPE_CODE,TOPICS.NAME,AP_PARAM.NAME)\n" +
						"HAVING CATEGORY.TYPE_CODE = 2"
		);
		StringBuilder sqlReading = new StringBuilder("SELECT\n" +
				"\tCATEGORY.ID categoryId,\n" +
				"\tCATEGORY.NAME nameCategory,\n" +
				"\tCATEGORY.LEVEL_CODE levelCode,\n" +
				"\tCATEGORY.TYPE_CODE typeCode,\n" +
				"\tTOPICS.NAME nameTopic,\n" +
				"\tAP_PARAM.NAME typeName,\n" +
				"\tcount(*) countQuestion,\n" +
				"\tSUM(QUESTION_ANSWER_READING.SCORE) countScore\n" +
				"FROM QUESTION_ANSWER_READING INNER JOIN CATEGORY\n" +
				"\tON QUESTION_ANSWER_READING.PARENT_ID = CATEGORY.ID\n" +
				"INNER JOIN TOPICS \n" +
				"\tON TOPICS.ID = CATEGORY.PARENT_ID\n" +
				"INNER JOIN AP_PARAM\n" +
				"\tON AP_PARAM.VALUE = TO_NCHAR(CATEGORY.TYPE_CODE)\n" +
				"GROUP BY (CATEGORY.ID,CATEGORY.NAME,CATEGORY.LEVEL_CODE,CATEGORY.TYPE_CODE,TOPICS.NAME,AP_PARAM.NAME)\n" +
				"HAVING CATEGORY.TYPE_CODE = 1");
		NativeQuery query;
		if (typeCode == 1) {
			query = currentSession().createNativeQuery(sqlReading.toString());
		} else {
			query = currentSession().createNativeQuery(sqlListening.toString());
		}
		query.addScalar("categoryId", new LongType());
		query.addScalar("nameCategory", new StringType());
		query.addScalar("levelCode", new StringType());
		query.addScalar("typeCode", new LongType());
		query.addScalar("typeName", new StringType());
		query.addScalar("nameTopic", new StringType());
		query.addScalar("countQuestion", new LongType());
		query.addScalar("countScore", new LongType());
		query.setResultTransformer(Transformers.aliasToBean(CategoryDTO.class));
		return query.list();
	}

	//for minitest
	public List<CategoryMinitestDTO> getListCategoriesByParentId(Long parentId, Long typeCode) {
		StringBuilder sql = new StringBuilder(
				"SELECT " +
						" CATEGORY.ID categoryId, " +
						" CATEGORY.PARENT_ID parentId " +
						" from CATEGORY where CATEGORY.PARENT_ID = :parentId and CATEGORY.TYPE_CODE = :typeCode "
		);
		NativeQuery query = currentSession().createNativeQuery(sql.toString());

		query.addScalar("categoryId", new LongType());
		query.addScalar("parentId", new LongType());
		query.setResultTransformer(Transformers.aliasToBean(CategoryMinitestDTO.class));
		query.setParameter("parentId", parentId);
		query.setParameter("typeCode", typeCode);
		return query.list();
	}

	public List<CategoryMinitestDTO> getListCategoriesByParentId2(Long parentId, Long typeCode) {
		StringBuilder sql = new StringBuilder(
				"SELECT " +
						" CATEGORY.ID categoryId, " +
						" CATEGORY.NAME nameCategory, " +
						" CATEGORY.PATH_FILE_1 pathFile1, " +
						" CATEGORY.PATH_FILE_2 pathFile2, " +
						" CATEGORY.PARENT_ID parentId " +
						" from CATEGORY where CATEGORY.PARENT_ID = :parentId and CATEGORY.TYPE_CODE = :typeCode "
		);
		NativeQuery query = currentSession().createNativeQuery(sql.toString());

		query.addScalar("categoryId", new LongType());
		query.addScalar("nameCategory", new StringType());
		query.addScalar("pathFile1", new StringType());
		query.addScalar("pathFile2", new StringType());
		query.addScalar("parentId", new LongType());
		query.setResultTransformer(Transformers.aliasToBean(CategoryMinitestDTO.class));
		query.setParameter("parentId", parentId);
		query.setParameter("typeCode", typeCode);
		return query.list();
	}

	public CategoryMinitestDTO getCategoryById(Long categoryId) {
		StringBuilder sql = new StringBuilder(
				"SELECT \n" +
						" CATEGORY.ID categoryId, \n" +
						" CATEGORY.PATH_FILE_1 pathFile1, \n" +
						" CATEGORY.PATH_FILE_2 pathFile2\n" +
						" from CATEGORY where CATEGORY.ID = :categoryId "
		);
		NativeQuery query = currentSession().createNativeQuery(sql.toString());

		query.addScalar("categoryId", new LongType());
		query.addScalar("pathFile1", new StringType());
		query.addScalar("pathFile2", new StringType());
		query.setResultTransformer(Transformers.aliasToBean(CategoryMinitestDTO.class));
		query.setParameter("categoryId", categoryId);
		return (CategoryMinitestDTO) query.uniqueResult();
	}

	//end for minitest
	public List<CategoryDTO> getCategoryByNameAndLevel(CategoryDTO categoryDTO) {
		StringBuilder sql = new StringBuilder(" select c.ID categoryId from category c " +
				"where c.name =:name and c.LEVEL_CODE =:level and c.PART_CODE =:partCode ");
		if (StringUtils.isNotBlank(categoryDTO.getArraySentenceNo())) {
			sql.append(" and c.ARRAY_SENTENCE_NO =:number ");
		}
		if (null != categoryDTO.getParentId()) {
			sql.append(" and c.PARENT_ID =:parentId ");
		}

		if (null != categoryDTO.getTypeCode()) {
			sql.append(" and c.TYPE_CODE =:typeCode ");
		}
		NativeQuery query = currentSession().createSQLQuery(sql.toString());
		query.addScalar("categoryId", new LongType());
		query.setResultTransformer(Transformers.aliasToBean(CategoryDTO.class));
		query.setParameter("name", categoryDTO.getNameCategory());
		query.setParameter("level", categoryDTO.getLevelCode());
		query.setParameter("partCode", categoryDTO.getPartCode());
		if (StringUtils.isNotBlank(categoryDTO.getArraySentenceNo())) {
			query.setParameter("number", categoryDTO.getArraySentenceNo());
		}
		if (null != categoryDTO.getParentId()) {
			query.setParameter("parentId", categoryDTO.getParentId());
		}
		if (null != categoryDTO.getTypeCode()) {
			query.setParameter("typeCode", categoryDTO.getTypeCode());
		}
		return query.list();
	}


	//for history practices listening
	public CategoryDTO getCategoryByCategoryId(Long categoryId) {
		StringBuilder sql = new StringBuilder("select CATEGORY.ID categoryId,\n" +
				"        CATEGORY.PATH_FILE_1 pathFile1,\n" +
				"        CATEGORY.PATH_FILE_2 pathFile2,\n" +
				"        CATEGORY.PATH_FILE_3 pathFile3,\n" +
				"        CATEGORY.LEVEL_CODE levelCode,\n" +
				"        CATEGORY.PARENT_ID parentId,\n" +
				"        CATEGORY.TRANSCRIPT transcript,\n" +
				"        TOPICS.NAME nameTopic\n" +
				"from CATEGORY  inner join TOPICS on CATEGORY.PARENT_ID = TOPICS.ID\n" +
				"where CATEGORY.ID =:categoryId");
		NativeQuery query = currentSession().createNativeQuery(sql.toString());
		query.addScalar("categoryId", new LongType());
		query.addScalar("pathFile1", new StringType());
		query.addScalar("pathFile2", new StringType());
		query.addScalar("pathFile3", new StringType());
		query.addScalar("levelCode", new StringType());
		query.addScalar("parentId", new LongType());
		query.addScalar("transcript", new StringType());
		query.addScalar("nameTopic", new StringType());

		query.setResultTransformer(Transformers.aliasToBean(CategoryDTO.class));
		query.setParameter("categoryId", categoryId);

		return (CategoryDTO) query.uniqueResult();
	}

	public List<CategoryDTO> getListCategoryListenFill(String topicName, String levelCode,int number){
		StringBuilder sql = new StringBuilder(
				"SELECT * FROM (SELECT DISTINCT c.id categoryId," +
						"                        c.path_file_1 pathFile1, " +
						"                        c.path_file_2 pathFile2 " +
						"                        FROM QUESTION_ANSWER_LISTENING ql INNER JOIN  CATEGORY c  " +
						"                        on ql.PARENT_ID = c.ID  " +
						"                        INNER JOIN TOPICS t  " +
						"                        on t.ID = c.PARENT_ID " +
						"                        WHERE upper(t.NAME) =upper(:topicName) and c.LEVEL_CODE = :levelCode and c.type_code = 5 " +
						"                        ORDER BY DBMS_RANDOM.RANDOM  " +
						"                        ) where ROWNUM <=:number"
		);
		NativeQuery query = currentSession().createNativeQuery(sql.toString());
		query.addScalar("categoryId",new LongType());
		query.addScalar("pathFile1",new StringType());
		query.addScalar("pathFile2",new StringType());
		query.setResultTransformer(Transformers.aliasToBean(CategoryDTO.class));
		query.setParameter( "topicName", topicName );
		query.setParameter( "levelCode", levelCode );
		query.setParameter( "number", number );

		List<CategoryDTO> lstQuest = query.list();
		return lstQuest;
	}

	public QuestionOfReadingAndComplitingDTO getCategoryReadingAndCompliting(Long id) {
		StringBuilder sql = new StringBuilder(
				"SELECT  c.ID idCategory," +
				" c.NAME nameCategory, " +
				" c.TYPE_FILE_1 typeFile1," +
				" c.TYPE_FILE_2 typeFile2," +
				" c.LEVEL_CODE levelCode," +
				" c.TRANSCRIPT transcript," +
				" c.PATH_FILE_1 pathFile1," +
				" c.PATH_FILE_2 pathFile2" +
				" FROM CATEGORY c where c.ID = :id");
		NativeQuery query = currentSession().createNativeQuery(sql.toString());
		query.addScalar("idCategory", new LongType());
		query.addScalar("nameCategory", new StringType());
		query.addScalar("typeFile1", new StringType());
		query.addScalar("typeFile2", new StringType());
		query.addScalar("levelCode", new StringType());
		query.addScalar("transcript", new StringType());
		query.addScalar("pathFile1", new StringType());
		query.addScalar("pathFile2", new StringType());
		query.setResultTransformer(Transformers.aliasToBean(QuestionOfReadingAndComplitingDTO.class));
		query.setParameter("id", id);

		return (QuestionOfReadingAndComplitingDTO) query.uniqueResult();
	}


	public  boolean isCheckTopic (Long parentId){
		StringBuilder sql = new StringBuilder(" SELECT ID FROM CATEGORY WHERE PARENT_ID =:parentId ");
		NativeQuery query = currentSession().createNativeQuery(sql.toString());
		query.setParameter("parentId", parentId);
		List<Long> lst = query.list();
	if(null !=  lst && lst.size()>0){
	return  false;
	}
	return  true;

	}

	public void deleteCategoryById(Long categoryId) {
		StringBuilder sql = new StringBuilder(
				"DELETE FROM category WHERE ID = :categoryId ");

		NativeQuery query = currentSession().createNativeQuery(sql.toString());

		query.setResultTransformer(Transformers.aliasToBean(CategoryDTO.class));

		query.setParameter("categoryId", categoryId);
		query.executeUpdate();
	}
}
