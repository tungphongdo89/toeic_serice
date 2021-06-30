package com.migi.toeic.respositories;

import com.migi.toeic.dto.CategoryMinitestDTO;
import com.migi.toeic.dto.CategoryTestDTO;
import com.migi.toeic.model.CategoryTest;
import com.migi.toeic.respositories.common.HibernateRepository;
import org.hibernate.query.NativeQuery;
import org.hibernate.transform.Transformers;
import org.hibernate.type.LongType;
import org.hibernate.type.StringType;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Transactional
@Repository
public class CategoryTestRepository extends HibernateRepository<CategoryTest,Long> {
    public List<CategoryTestDTO> getListCategoryTestByTestIdAndCategoryName(Long testId, String categoryName, String part){
        StringBuilder sql = new StringBuilder("select ct.id id," +
                "ct.test_id testId," +
                "ct.category_id categoryId," +
                "ct.path_file pathFile," +
                "ct.part part  " +
                "from category_test ct inner join category c on ct.category_id = c.id " +
                "where ct.test_id =:testId and c.name =:categoryName and ct.part =:part");

        NativeQuery query = currentSession().createNativeQuery(sql.toString());
        query.addScalar("id", new LongType());
        query.addScalar("testId", new LongType());
        query.addScalar("categoryId", new LongType());
        query.addScalar("pathFile", new StringType());
        query.addScalar("part", new StringType());

        query.setParameter("testId", testId);
        query.setParameter("part", part);
        query.setParameter("categoryName", categoryName);
        query.setResultTransformer(Transformers.aliasToBean(CategoryTestDTO.class));
        return query.list();
    }
    public List<CategoryTestDTO> getCategoryTestByTestIdAndCategoryName(CategoryTestDTO dto){
        StringBuilder sql = new StringBuilder("select ct.id id," +
                "ct.test_id testId," +
                "ct.category_id categoryId," +
                "ct.path_file pathFile," +
                "ct.part part  " +
                "from category_test ct inner join category c on ct.category_id = c.id " +
                "where ct.test_id =:testId and c.name =:categoryName");

        NativeQuery query = currentSession().createNativeQuery(sql.toString());
        query.addScalar("id", new LongType());
        query.addScalar("testId", new LongType());
        query.addScalar("categoryId", new LongType());
        query.addScalar("pathFile", new StringType());
        query.addScalar("part", new StringType());

        query.setParameter("testId", dto.getTestId());
        query.setParameter("categoryName", dto.getCategoryName());
        query.setResultTransformer(Transformers.aliasToBean(CategoryTestDTO.class));
        return query.list();
    }
}
