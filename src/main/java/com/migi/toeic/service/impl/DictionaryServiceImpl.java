package com.migi.toeic.service.impl;

import com.migi.toeic.base.DataListDTO;
import com.migi.toeic.base.ResultDataDTO;
import com.migi.toeic.base.common.amazons3.UpFileAmazon;
import com.migi.toeic.dto.DictionaryDTO;
import com.migi.toeic.exception.BusinessException;
import com.migi.toeic.respositories.DictionaryRepository;
import com.migi.toeic.service.DictionaryService;
import com.migi.toeic.utils.MessageUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Date;


@Service
public class DictionaryServiceImpl implements DictionaryService {

    @Autowired
    private DictionaryRepository dictionaryRepository;
    @Autowired
    private Environment environment;

    public boolean checkId(Long id, DictionaryDTO dictionaryDTO) {

        if (id == null) {
            return false;
        } else {
            DictionaryDTO dictionaryDTO1 = dictionaryRepository.getDictionaryById(dictionaryDTO.getId().longValue());
            if (dictionaryDTO1 != null) {
                return true;
            } else
                return false;
        }
    }

    @Override
    public DataListDTO doSearch(DictionaryDTO dictionaryDTO) {
        ResultDataDTO resultDto = dictionaryRepository.doSearch(dictionaryDTO);

        DataListDTO data = new DataListDTO();
        data.setData(resultDto.getData());
        data.setTotal(resultDto.getTotal());
        data.setSize(resultDto.getTotal());
        data.setStart(1);
        return data;
    }

    @Override
    public String insertDictionary(DictionaryDTO dictionaryDTO) {
        dictionaryDTO.setNameEng(dictionaryDTO.getNameEng().trim());
        dictionaryDTO.setTranscribe(dictionaryDTO.getTranscribe().trim());
        dictionaryDTO.setSynonymous(dictionaryDTO.getSynonymous().trim());
     if( dictionaryDTO.getDescription().equals("null") || StringUtils.isBlank(dictionaryDTO.getNameEng()) ||  StringUtils.isBlank(dictionaryDTO.getTranscribe()) || dictionaryDTO.getFileUpload()==null ){
         throw new BusinessException(MessageUtils.getMessage("error_miss_info"));

     }
     if(null != dictionaryRepository.getDictionaryByNameEng(dictionaryDTO.getNameEng())){
         throw new BusinessException(MessageUtils.getMessage("dictionary_exit"));

     }
        if (StringUtils.isNotBlank(dictionaryDTO.getNameEng()) )  {
            try {
                UpFileAmazon upFileAmazon =new UpFileAmazon();
//                LocalDateTime localDateTime = LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh"));
//                Date createDate = Date.from(localDateTime.atZone(ZoneId.of("Asia/Ho_Chi_Minh")).toInstant());
            dictionaryDTO.setCreateDate(new Date());
            dictionaryDTO.setUpdatedDate(new Date());
                try{
                    dictionaryDTO.setMp3(upFileAmazon.UpLoadFile(dictionaryDTO.getFileUpload() , null , null));
                }catch (Exception e) {
                    throw new BusinessException(MessageUtils.getMessage("dictionary_upload_mp3"));
                }
                if (dictionaryRepository.insert(dictionaryDTO.toModel())!= null) {
                    return MessageUtils.getMessage("create_dictionary_success");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            throw new BusinessException(MessageUtils.getMessage("INSERT_ERROR"));
        }
        return null;
    }

    @Override
    public String updateDictionary(DictionaryDTO dictionaryDTO) {
        dictionaryDTO.setNameEng(dictionaryDTO.getNameEng().trim());
        dictionaryDTO.setTranscribe(dictionaryDTO.getTranscribe().trim());
        dictionaryDTO.setSynonymous(dictionaryDTO.getSynonymous().trim());
        if( dictionaryDTO.getDescription().equals("null") || StringUtils.isBlank(dictionaryDTO.getNameEng()) ||  StringUtils.isBlank(dictionaryDTO.getTranscribe()) ){
            throw new BusinessException(MessageUtils.getMessage("error_miss_info"));

        }
        if(null != dictionaryRepository.getDictionaryByNameEng(dictionaryDTO.getNameEng())
                && !dictionaryRepository.getDictionaryByNameEng(dictionaryDTO.getNameEng()).getId().equals(dictionaryDTO.getId())){
            throw new BusinessException(MessageUtils.getMessage("dictionary_exit"));

        }
        if (checkId(dictionaryDTO.getId(), dictionaryDTO)) {
          if(dictionaryDTO.getFileUpload()!=null){
              try {
                  UpFileAmazon upFileAmazon =new UpFileAmazon();
                  try{
                      dictionaryDTO.setMp3(upFileAmazon.UpLoadFile(dictionaryDTO.getFileUpload() , null , null));
                  }catch (Exception e) {
                      throw new BusinessException(MessageUtils.getMessage("dictionary_upload_mp3"));
                  }

              } catch (Exception e) {
                  e.printStackTrace();
              }
          }
            try {
//                LocalDateTime localDateTime = LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh"));
//                Date updateDate = Date.from(localDateTime.atZone(ZoneId.of("Asia/Ho_Chi_Minh")).toInstant());
                dictionaryDTO.setUpdatedDate(new Date());
                dictionaryRepository.update(dictionaryDTO.toModel());
                return MessageUtils.getMessage("update_dictionary_success");

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
              throw new BusinessException(environment.getProperty("dictionary_not_exit"));
        }
        return null;
    }

    @Override
    public String deleteDictionary(DictionaryDTO dictionaryDTO) {
        if (dictionaryDTO != null) {
            dictionaryRepository.delete(dictionaryDTO.toModel());
            return  MessageUtils.getMessage("delete_dictionary_success");
        }else {
            throw new BusinessException(environment.getProperty("delete_dictionary_faile"));
        }
    }

    @Override
    public DictionaryDTO getDictionaryById(Long id)  {
        if(id !=null){
            try {
            DictionaryDTO dictionaryDTO= dictionaryRepository.getDictionaryById(id);
            return dictionaryDTO;
            } catch (Exception e) {
                throw new BusinessException(environment.getProperty("dictionary_not_exit"));
            }
        }
        else {
            throw new BusinessException(environment.getProperty("dictionary_not_exit"));

        }

    }

    @Override
    public DictionaryDTO getDictionaryByNameEng(DictionaryDTO dictionaryDTO){
        DictionaryDTO dictionaryDTO1 = dictionaryRepository.getDictionaryByNameEng(dictionaryDTO.getNameEng());
        return dictionaryDTO1;
    }
}
