package com.migi.toeic.respositories;

import com.migi.toeic.base.ResultDataDTO;
import com.migi.toeic.dto.DictionaryDTO;
import com.migi.toeic.model.Dictionary;
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

@Repository
@Transactional
public class DictionaryRepository extends HibernateRepository<Dictionary, Long> {

    @Transactional
    public ResultDataDTO doSearch(DictionaryDTO dictionaryDTO) {
        StringBuilder sql = new StringBuilder(" select" +
                " ID id, NAME_ENG nameEng, NAME_VN nameVn,WORD_TYPE wordType,DESCRIPTION description," +
                "CREATE_DATE createDate, UPDATED_DATE updatedDate,STATUS status ,MP3 mp3,  TRANSCRIBE transcribe, SYNONYMOUS synonymous from dictionary_management where 1=1 ");
        if (StringUtils.isNotBlank(dictionaryDTO.getKeySearch())) {
            sql.append(" and upper(NAME_ENG) like upper(:nameEng)  escape '&' " +
                    " or upper(DESCRIPTION) like upper(:description) escape '&' " +
                    " ORDER BY NAME_ENG ASC ");
        }
        else {
            sql.append("  ORDER BY NAME_ENG ASC");
        }

        NativeQuery query = currentSession().createNativeQuery(sql.toString());

        StringBuilder sqlCount = new StringBuilder("SELECT COUNT(*) FROM (");
        sqlCount.append(sql.toString());
        sqlCount.append(")");

        NativeQuery queryCount = currentSession().createNativeQuery(sqlCount.toString());
        query.addScalar("id", new LongType());
        query.addScalar("nameEng", new StringType());
        query.addScalar("nameVn", new StringType());
        query.addScalar("wordType", new StringType());
        query.addScalar("description", new StringType());
        query.addScalar("createDate", new TimestampType());
        query.addScalar("updatedDate", new TimestampType());
        query.addScalar("status", new LongType());
        query.addScalar("mp3", new StringType());
        query.addScalar("transcribe", new StringType());
        query.addScalar("synonymous", new StringType());
        if (StringUtils.isNotEmpty(dictionaryDTO.getKeySearch())) {
            query.setParameter("nameEng", "%" + ValidateUtils.validateKeySearch(dictionaryDTO.getKeySearch()) + "%");
            query.setParameter("description", "%" + ValidateUtils.validateKeySearch(dictionaryDTO.getKeySearch()) + "%");
            queryCount.setParameter("nameEng", "%" + ValidateUtils.validateKeySearch(dictionaryDTO.getKeySearch()) + "%");
            queryCount.setParameter("description", "%" + ValidateUtils.validateKeySearch(dictionaryDTO.getKeySearch()) + "%");
        }

        if (dictionaryDTO.getPage() != null && dictionaryDTO.getPageSize() != null) {
            query.setFirstResult((dictionaryDTO.getPage() - 1) * dictionaryDTO.getPageSize().intValue());
            query.setMaxResults(dictionaryDTO.getPageSize().intValue());
        }


        query.setResultTransformer(Transformers.aliasToBean(DictionaryDTO.class));
        dictionaryDTO.setTotalRecord(((BigDecimal) queryCount.uniqueResult()).intValue());
        ResultDataDTO resultDataDTO = new ResultDataDTO();
        List<DictionaryDTO> lst = query.list();
        resultDataDTO.setData(lst);
        resultDataDTO.setTotal(dictionaryDTO.getTotalRecord());
        return resultDataDTO;
    }


    @Transactional
    public DictionaryDTO getDictionaryById(Long id) {
        StringBuilder sql = new StringBuilder("select " +
                "ID id, NAME_ENG nameEng, NAME_VN nameVn,WORD_TYPE wordType,DESCRIPTION description," +
                "CREATE_DATE createDate, UPDATED_DATE updatedDate,STATUS status, MP3 mp3,  TRANSCRIBE transcribe , SYNONYMOUS synonymous from dictionary_management where 1=1");
        if (id != null) {
            sql.append(" and id=:id");
        }
        NativeQuery query = currentSession().createNativeQuery(sql.toString());
        query.addScalar("id", new LongType());
        query.addScalar("nameEng", new StringType());
        query.addScalar("nameVn", new StringType());
        query.addScalar("wordType", new StringType());
        query.addScalar("description", new StringType());
        query.addScalar("createDate", new DateType());
        query.addScalar("updatedDate", new DateType());
        query.addScalar("status", new LongType());
        query.addScalar("mp3", new StringType());
        query.addScalar("transcribe", new StringType());
        query.addScalar("synonymous", new StringType());
        if (id != null) {
            query.setParameter("id", id);
        }
        query.setResultTransformer(Transformers.aliasToBean(DictionaryDTO.class));
        return (DictionaryDTO) query.uniqueResult();
    }



    @Transactional
    public DictionaryDTO getDictionaryByNameEng(String nameEng) {
        StringBuilder sql = new StringBuilder("select " +
                "ID id, NAME_ENG nameEng, NAME_VN nameVn,WORD_TYPE wordType,DESCRIPTION description," +
                "CREATE_DATE createDate, UPDATED_DATE updatedDate,STATUS status, MP3 mp3,  TRANSCRIBE transcribe , SYNONYMOUS synonymous" +
                " from dictionary_management where 1=1");
        if (StringUtils.isNotBlank(nameEng)) {
            sql.append(" and upper(NAME_ENG) = upper(:nameEng) ");
        }
        NativeQuery query = currentSession().createNativeQuery(sql.toString());
        query.addScalar("id", new LongType());
        query.addScalar("nameEng", new StringType());
        query.addScalar("nameVn", new StringType());
        query.addScalar("wordType", new StringType());
        query.addScalar("description", new StringType());
        query.addScalar("createDate", new DateType());
        query.addScalar("updatedDate", new DateType());
        query.addScalar("status", new LongType());
        query.addScalar("mp3", new StringType());
        query.addScalar("transcribe", new StringType());
        query.addScalar("synonymous", new StringType());
        if (StringUtils.isNotEmpty(nameEng)) {
            query.setParameter("nameEng", ValidateUtils.validateKeySearch(nameEng)  );
        }
        query.setResultTransformer(Transformers.aliasToBean(DictionaryDTO.class));
        return (DictionaryDTO) query.uniqueResult();
    }
}
