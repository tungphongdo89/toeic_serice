package com.migi.toeic.respositories;

import com.migi.toeic.authen.model.RequestPractice;
import com.migi.toeic.base.ResultDataDTO;
import com.migi.toeic.dto.DetailHistoryMinitestDTO;
import com.migi.toeic.dto.QuestionAnswerListeningDTO;
import com.migi.toeic.dto.QuestionAnswersDTO;
import com.migi.toeic.dto.QuestionMinitestDTO;
import com.migi.toeic.model.DetailHistoryMinitest;
import com.migi.toeic.model.QuestionAnswerListening;
import com.migi.toeic.respositories.common.HibernateRepository;
import com.migi.toeic.utils.ValidateUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.query.NativeQuery;
import org.hibernate.transform.Transformers;
import org.hibernate.type.*;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;

@Transactional
@Repository
public class DetailHistoryMinitestRepository extends HibernateRepository<DetailHistoryMinitest, Long> {

    public List<DetailHistoryMinitestDTO> getDetailHistoryMinitestByParentId(Long parentId, String partName){
        StringBuilder sql = new StringBuilder(
                "select  DETAIL_HISTORY_MINITEST.ID id, \n" +
                        "        DETAIL_HISTORY_MINITEST.PARENT_ID parentId, \n" +
                        "        DETAIL_HISTORY_MINITEST.PART_NAME partName, \n" +
                        "        DETAIL_HISTORY_MINITEST.INDEX_CORRECT_ANSWER indexCorrectAnswer, \n" +
                        "        DETAIL_HISTORY_MINITEST.INDEX_INCORRECT_ANSWER indexIncorrectAnswer, \n" +
                        "        DETAIL_HISTORY_MINITEST.QUESTION_ID questionId, \n" +
                        "        DETAIL_HISTORY_MINITEST.CATEGORY_ID categoryId, " +
                        "        DETAIL_HISTORY_MINITEST.STT stt \n" +
                        "        from DETAIL_HISTORY_MINITEST\n" +
                        "        where DETAIL_HISTORY_MINITEST.PARENT_ID =:parentId " +
                        "       and DETAIL_HISTORY_MINITEST.PART_NAME =:partName order by DETAIL_HISTORY_MINITEST.STT ");

        NativeQuery query = currentSession().createNativeQuery(sql.toString());

        query.addScalar("id", new LongType());
        query.addScalar("parentId", new LongType());
        query.addScalar("partName", new StringType());
        query.addScalar("indexCorrectAnswer", new LongType());
        query.addScalar("indexIncorrectAnswer", new LongType());
        query.addScalar("questionId", new LongType());
        query.addScalar("categoryId", new LongType());
        query.addScalar("stt", new LongType());

        query.setResultTransformer(Transformers.aliasToBean(DetailHistoryMinitestDTO.class));

        query.setParameter( "parentId", parentId );
        query.setParameter( "partName", partName );

        return query.list();
    }

    // for deleting cateogry
    public List<DetailHistoryMinitestDTO> checkForDeletingCategory(Long parentId){
        StringBuilder sql = new StringBuilder(
                "select id from detail_history_minitest where category_id =:parentId  "
        );

        NativeQuery query = currentSession().createNativeQuery(sql.toString());
        query.addScalar("id",new LongType());
        query.setResultTransformer(Transformers.aliasToBean(DetailHistoryMinitestDTO.class));
        query.setParameter("parentId",parentId);
        return query.list();
    }

}
