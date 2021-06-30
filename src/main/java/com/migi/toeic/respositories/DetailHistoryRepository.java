package com.migi.toeic.respositories;

import com.migi.toeic.dto.DetailHistoryDTO;
import com.migi.toeic.dto.HistoryDTO;
import com.migi.toeic.model.DetailsHistory;
import com.migi.toeic.model.History;
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
public class DetailHistoryRepository extends HibernateRepository<DetailsHistory,Long> {

    @Transactional
    public List<DetailHistoryDTO> getListDetailHistory(HistoryDTO historyDTO){
        StringBuilder sql = new StringBuilder("SELECT details_history.id id, " +
                "details_history.parent_id parentId, " +
                "details_history.part part, " +
                "details_history.index_correct indexCorrect, " +
                "details_history.index_incorrect indexIncorrect, " +
                "details_history.question_id questionId, " +
                "details_history.category_id categoryId " +
                "FROM DETAILS_HISTORY INNER JOIN history ON DETAILS_HISTORY.PARENT_ID = HISTORY.ID " +
                "AND HISTORY.TEST_ID =:testId " +
                "AND HISTORY.USER_ID =:userId " +
                "AND TO_CHAR(history.created_date, 'yyyy-MM-dd HH24:MI:ss') =:created_date " +
                "order by details_history.part,categoryId ");
        NativeQuery query=currentSession().createNativeQuery(sql.toString());
        query.addScalar("id",new LongType());
        query.addScalar("parentId",new LongType());
        query.addScalar("part",new StringType());
        query.addScalar("indexCorrect",new LongType());
        query.addScalar("indexIncorrect",new LongType());
        query.addScalar("questionId",new LongType());
        query.addScalar("categoryId",new LongType());
        query.setResultTransformer(Transformers.aliasToBean(DetailHistoryDTO.class));
        query.setParameter( "userId", historyDTO.getUserId() );
        query.setParameter( "testId", historyDTO.getTestId() );
        query.setParameter( "created_date", historyDTO.getCreateDateString() );
        return query.list();
    }

    // for deleting cateogry
    public List<DetailHistoryDTO> checkForDeletingCategory(Long parentId){
        StringBuilder sql = new StringBuilder(
                "select id from details_history where category_id =:parentId  "
        );

        NativeQuery query = currentSession().createNativeQuery(sql.toString());
        query.addScalar("id",new LongType());
        query.setResultTransformer(Transformers.aliasToBean(DetailHistoryDTO.class));
        query.setParameter("parentId",parentId);
        return query.list();
    }

}
