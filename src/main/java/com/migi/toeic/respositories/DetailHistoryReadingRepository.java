package com.migi.toeic.respositories;

import com.migi.toeic.dto.DetailHistoryListenFillDTO;
import com.migi.toeic.dto.DetailHistoryReadingDTO;
import com.migi.toeic.dto.HistoryPracticesDTO;
import com.migi.toeic.model.DetailHistoryListenFill;
import com.migi.toeic.model.DetailHistoryReading;
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
public class DetailHistoryReadingRepository extends HibernateRepository<DetailHistoryReading,Long> {

    @Transactional
    public List<DetailHistoryReadingDTO> getListDetailHistoryReadWordFill(HistoryPracticesDTO historyPracticesDTO){
        StringBuilder sql = new StringBuilder("SELECT detail_history_reading.id id," +
                "                                detail_history_reading.parent_id parentId," +
                "                                detail_history_reading.user_choose userChoose," +
                "                                detail_history_reading.index_correct indexCorrect," +
                "                                detail_history_reading.index_incorrect indexInCorrect," +
                "                                detail_history_reading.question_id questionId," +
                "                                detail_history_reading.category_id categoryId," +
                "                                detail_history_reading.number_selected numberSelected " +
                "                                FROM detail_history_reading INNER JOIN history_practices ON detail_history_reading.PARENT_ID = history_practices.ID  " +
                "                                AND history_practices.ID =:id " +
                "                                AND TO_CHAR(history_practices.created_date, 'yyyy-MM-dd HH24:MI:ss') =:created_date " +
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
        query.setResultTransformer(Transformers.aliasToBean(DetailHistoryReadingDTO.class));
        query.setParameter( "id", historyPracticesDTO.getId() );
        query.setParameter( "created_date", historyPracticesDTO.getCreateDateString() );
        return query.list();
    }

    // for deleting cateogry
    public List<DetailHistoryReadingDTO> checkForDeletingCategory(Long parentId){
        StringBuilder sql = new StringBuilder(
                "select id from detail_history_reading where category_id =:parentId  "
        );

        NativeQuery query = currentSession().createNativeQuery(sql.toString());
        query.addScalar("id",new LongType());
        query.setResultTransformer(Transformers.aliasToBean(DetailHistoryReadingDTO.class));
        query.setParameter("parentId",parentId);
        return query.list();
    }

}
