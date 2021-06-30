package com.migi.toeic.respositories;

import com.migi.toeic.dto.DetailHistoryListeningDTO;
import com.migi.toeic.dto.HistoryPracticesDTO;
import com.migi.toeic.model.DetailHistoryListening;
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
public class DetailHistoryListeningRepository extends HibernateRepository<DetailHistoryListening, Long> {

    public List<DetailHistoryListeningDTO> getListDetailHistoryListeningByParentId(Long parentId){
        StringBuilder sql = new StringBuilder(
                "select  DETAIL_HISTORY_LISTENING.ID id, \n" +
                        "        DETAIL_HISTORY_LISTENING.PARENT_ID parentId, \n" +
                        "        DETAIL_HISTORY_LISTENING.CORRECT_INDEX correctIndex, \n" +
                        "        DETAIL_HISTORY_LISTENING.INCORRECT_INDEX incorrectIndex, \n" +
                        "        DETAIL_HISTORY_LISTENING.QUESTION_ID questionId, \n" +
                        "        DETAIL_HISTORY_LISTENING.CATEGORY_ID categoryId\n" +
                        "from DETAIL_HISTORY_LISTENING where DETAIL_HISTORY_LISTENING.PARENT_ID =:parentId "
        );
        NativeQuery query = currentSession().createNativeQuery(sql.toString());
        query.addScalar("id",new LongType());
        query.addScalar("parentId",new LongType());
        query.addScalar("correctIndex",new LongType());
        query.addScalar("incorrectIndex",new LongType());
        query.addScalar("questionId",new LongType());
        query.addScalar("categoryId",new LongType());
        query.setResultTransformer(Transformers.aliasToBean(DetailHistoryListeningDTO.class));
        query.setParameter( "parentId", parentId );

        List<DetailHistoryListeningDTO> lstQuest = query.list();
        return lstQuest;
    }

    public List<DetailHistoryListeningDTO> getDistinctListDetailHistoryListeningByParentId(Long parentId){
        StringBuilder sql = new StringBuilder(
                " SELECT DISTINCT CATEGORY_ID categoryId\n" +
                        " FROM DETAIL_HISTORY_LISTENING where DETAIL_HISTORY_LISTENING.PARENT_ID =:parentId "
        );
        NativeQuery query = currentSession().createNativeQuery(sql.toString());
        query.addScalar("categoryId",new LongType());
        query.setResultTransformer(Transformers.aliasToBean(DetailHistoryListeningDTO.class));
        query.setParameter( "parentId", parentId );

        List<DetailHistoryListeningDTO> lstQuest = query.list();
        return lstQuest;
    }

    // for deleting cateogry
    public List<DetailHistoryListeningDTO> checkForDeletingCategory(Long parentId){
        StringBuilder sql = new StringBuilder(
                "select id from detail_history_listening where category_id =:parentId  "
        );

        NativeQuery query = currentSession().createNativeQuery(sql.toString());
        query.addScalar("id",new LongType());
        query.setResultTransformer(Transformers.aliasToBean(DetailHistoryListeningDTO.class));
        query.setParameter("parentId",parentId);
        return query.list();
    }


}
