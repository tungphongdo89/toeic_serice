package com.migi.toeic.respositories;

import com.migi.toeic.dto.DetailHistoryDTO;
import com.migi.toeic.dto.DetailHistoryListenFillDTO;
import com.migi.toeic.dto.HistoryPracticesDTO;
import com.migi.toeic.model.DetailHistoryListenFill;
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
public class DetailHistoryListenFillRepository extends HibernateRepository<DetailHistoryListenFill,Long> {

    @Transactional
    public List<DetailHistoryListenFillDTO> getListDetailHistoryListenFill(HistoryPracticesDTO historyPracticesDTO){
        StringBuilder sql = new StringBuilder("SELECT detail_history_listen_fill.id id,  " +
                "                detail_history_listen_fill.parent_id parentId,  " +
                "                detail_history_listen_fill.user_fill userFill,  " +
                "                detail_history_listen_fill.index_correct indexCorrect,  " +
                "                detail_history_listen_fill.index_incorrect indexInCorrect,  " +
                "                detail_history_listen_fill.question_id questionId,  " +
                "                detail_history_listen_fill.category_id categoryId  " +
                "                FROM detail_history_listen_fill INNER JOIN history_practices ON detail_history_listen_fill.PARENT_ID = history_practices.ID  " +
                "                AND history_practices.ID = :id" +
                "                AND TO_CHAR(history_practices.created_date, 'yyyy-MM-dd HH24:MI:ss') =:created_date " +
                "                ORDER BY id ");
        NativeQuery query=currentSession().createNativeQuery(sql.toString());
        query.addScalar("id",new LongType());
        query.addScalar("parentId",new LongType());
        query.addScalar("userFill",new StringType());
        query.addScalar("indexCorrect",new StringType());
        query.addScalar("indexInCorrect",new StringType());
        query.addScalar("questionId",new LongType());
        query.addScalar("categoryId",new LongType());
        query.setResultTransformer(Transformers.aliasToBean(DetailHistoryListenFillDTO.class));
        query.setParameter( "id", historyPracticesDTO.getId() );
        query.setParameter( "created_date", historyPracticesDTO.getCreateDateString() );
        return query.list();
    }

    // for deleting cateogry
    public List<DetailHistoryListenFillDTO> checkForDeletingCategory(Long parentId){
        StringBuilder sql = new StringBuilder(
                "select id from detail_history_listen_fill where category_id =:parentId  "
        );

        NativeQuery query = currentSession().createNativeQuery(sql.toString());
        query.addScalar("id",new LongType());
        query.setResultTransformer(Transformers.aliasToBean(DetailHistoryListenFillDTO.class));
        query.setParameter("parentId",parentId);
        return query.list();
    }

}
