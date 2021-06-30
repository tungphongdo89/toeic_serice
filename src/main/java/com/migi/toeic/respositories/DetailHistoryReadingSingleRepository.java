package com.migi.toeic.respositories;

import com.migi.toeic.dto.DetailHistoryReadingDTO;
import com.migi.toeic.dto.DetailHistoryReadingSingleDTO;
import com.migi.toeic.dto.HistoryPracticesDTO;
import com.migi.toeic.model.DetailHistoryReading;
import com.migi.toeic.model.DetailHistoryReadingSingle;
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
public class DetailHistoryReadingSingleRepository extends HibernateRepository<DetailHistoryReadingSingle,Long> {

    @Transactional
    public List<DetailHistoryReadingSingleDTO> getListDetailHistoryReadSingle(Long history_practicesId){
        StringBuilder sql = new StringBuilder("SELECT a.id id," +
                "                                a.parent_id parentId," +
                "                                a.user_choose userChoose," +
                "                                a.index_correct indexCorrect," +
                "                                a.index_incorrect indexInCorrect," +
                "                                a.question_id questionId," +
                "                                a.category_id categoryId," +
                "                                a.number_selected numberSelected " +
                "                                FROM detail_history_read_single a INNER JOIN history_practices ON a.PARENT_ID = history_practices.ID  " +
                "                                AND history_practices.ID =:parentId " +
                "                                ORDER BY id ");
        NativeQuery query=currentSession().createNativeQuery(sql.toString());
        query.addScalar("id",new LongType());
        query.addScalar("parentId",new LongType());
        query.addScalar("userChoose",new StringType());
        query.addScalar("indexCorrect",new StringType());
        query.addScalar("indexInCorrect",new StringType());
        query.addScalar("questionId",new LongType());
        query.addScalar("categoryId",new LongType());
        query.addScalar("numberSelected", new LongType());
        query.setResultTransformer(Transformers.aliasToBean(DetailHistoryReadingSingleDTO.class));
        query.setParameter( "parentId", history_practicesId);
        return query.list();
    }
    public List<DetailHistoryReadingSingleDTO> getDistinctListDetailHistoryReadingByParentId(Long parentId){
        StringBuilder sql = new StringBuilder(
                " SELECT DISTINCT CATEGORY_ID categoryId\n" +
                        " FROM detail_history_read_single where detail_history_read_single.PARENT_ID = :parentId "
        );
        NativeQuery query = currentSession().createNativeQuery(sql.toString());
        query.addScalar("categoryId",new LongType());
        query.setResultTransformer(Transformers.aliasToBean(DetailHistoryReadingSingleDTO.class));
        query.setParameter( "parentId", parentId );
        List<DetailHistoryReadingSingleDTO> lstQuest = query.list();
        return lstQuest;
    }

    // for deleting cateogry
    public List<DetailHistoryReadingSingleDTO> checkForDeletingCategory(Long parentId){
        StringBuilder sql = new StringBuilder(
                "select id from detail_history_read_single where category_id =:parentId  "
        );

        NativeQuery query = currentSession().createNativeQuery(sql.toString());
        query.addScalar("id",new LongType());
        query.setResultTransformer(Transformers.aliasToBean(DetailHistoryReadingSingleDTO.class));
        query.setParameter("parentId",parentId);
        return query.list();
    }

}
