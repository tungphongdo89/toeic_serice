//package com.migi.toeic.respositories;
//
//import com.migi.toeic.dto.CategoryMinitestDTO;
//import com.migi.toeic.dto.TopicCategoryDTO;
//import com.migi.toeic.model.TopicCategory;
//import com.migi.toeic.respositories.common.HibernateRepository;
//import org.hibernate.query.NativeQuery;
//import org.hibernate.transform.Transformers;
//import org.hibernate.type.LongType;
//import org.springframework.stereotype.Repository;
//
//import javax.transaction.Transactional;
//
//@Repository
//@Transactional
//public class TopicCategoryRepository extends HibernateRepository<TopicCategory, Long> {
//
//	public TopicCategoryDTO findByCategoryIdAndTopicID(TopicCategoryDTO topicCategoryDTO) {
//		StringBuilder sql = new StringBuilder("select id id,category_id categoryId,topic_id topicId where topic_id=:topicId and category_id =: categoryId and status = 1");
//
//		NativeQuery query = currentSession().createNativeQuery(sql.toString());
//		query.addScalar("id", new LongType());
//		query.addScalar("categoryId", new LongType());
//		query.addScalar("topicId", new LongType());
//
//		query.setParameter("categoryId", topicCategoryDTO.getCategoryId());
//		query.setParameter("topicId", topicCategoryDTO.getTopicId());
//		query.setResultTransformer(Transformers.aliasToBean(CategoryMinitestDTO.class));
//		return (TopicCategoryDTO) query.uniqueResult();
//	}
//}
