package com.migi.toeic.service;

import com.migi.toeic.base.DataListDTO;
import com.migi.toeic.dto.DictionaryDTO;

public interface DictionaryService {
    public String insertDictionary(DictionaryDTO dictionaryDTO) throws Exception;
    public DataListDTO doSearch(DictionaryDTO dictionaryDTO) throws Exception;
    public String updateDictionary(DictionaryDTO dictionaryDTO) throws Exception;
    public String deleteDictionary(DictionaryDTO dictionaryDTO) throws Exception;
    public DictionaryDTO getDictionaryById(Long id) throws Exception;
    public DictionaryDTO getDictionaryByNameEng(DictionaryDTO dictionaryDTO) throws Exception;

}
