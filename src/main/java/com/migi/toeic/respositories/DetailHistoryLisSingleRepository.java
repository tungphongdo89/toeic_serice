package com.migi.toeic.respositories;

import com.migi.toeic.dto.DetailHistoryLisSingleDTO;
import com.migi.toeic.dto.DetailHistoryListeningDTO;
import com.migi.toeic.model.DetailHistoryLisSingle;
import com.migi.toeic.model.DetailHistoryListening;
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
public class DetailHistoryLisSingleRepository extends HibernateRepository<DetailHistoryLisSingle, Long> {

    public List<DetailHistoryLisSingleDTO> getListDetailHistoryLisSingleByParentId(Long parentId){
        StringBuilder sql = new StringBuilder(
                "select  DETAIL_HISTORY_LIS_SINGLE.ID id, \n" +
                        "        DETAIL_HISTORY_LIS_SINGLE.PARENT_ID parentId, \n" +
                        "        DETAIL_HISTORY_LIS_SINGLE.USER_CHOOSE userChoose, \n" +
                        "        DETAIL_HISTORY_LIS_SINGLE.CORRECT correct, \n" +
                        "        DETAIL_HISTORY_LIS_SINGLE.QUESTION_ID questionId,\n" +
                        "        DETAIL_HISTORY_LIS_SINGLE.TOPIC_NAME topicName\n" +
                        "from DETAIL_HISTORY_LIS_SINGLE where DETAIL_HISTORY_LIS_SINGLE.PARENT_ID =:parentId "
        );
        NativeQuery query = currentSession().createNativeQuery(sql.toString());
        query.addScalar("id",new LongType());
        query.addScalar("parentId",new LongType());
        query.addScalar("userChoose",new StringType());
        query.addScalar("correct",new StringType());
        query.addScalar("questionId",new LongType());
        query.addScalar("topicName",new StringType());
        query.setResultTransformer(Transformers.aliasToBean(DetailHistoryLisSingleDTO.class));
        query.setParameter( "parentId", parentId );

        List<DetailHistoryLisSingleDTO> lstQuest = query.list();
        return lstQuest;
    }

    // for deleting cateogry
    public List<DetailHistoryLisSingleDTO> checkForDeletingCategory(Long parentId){
        StringBuilder sql = new StringBuilder(
                "select id from detail_history_lis_single where question_id in ( \n" +
                        "   select id from question_answer_listening where parent_id =:parentId \n" +
                        " ) "
        );

        NativeQuery query = currentSession().createNativeQuery(sql.toString());
        query.addScalar("id",new LongType());
        query.setResultTransformer(Transformers.aliasToBean(DetailHistoryLisSingleDTO.class));
        query.setParameter("parentId",parentId);
        return query.list();
    }


}
