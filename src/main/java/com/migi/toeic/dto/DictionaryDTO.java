package com.migi.toeic.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.migi.toeic.base.ToeicBaseDTO;
import com.migi.toeic.model.Dictionary;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;


import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown =true)
public class DictionaryDTO extends ToeicBaseDTO {

    private Long id;
    private String nameEng;
    private String nameVn;
    private String wordType;
    private String description;
    private Date createDate;
    private Date updatedDate;
    private Long status;
    private String mp3;
    private String transcribe;
    private String synonymous;

    private MultipartFile fileUpload;


    public Dictionary toModel()
    {
        Dictionary dictionary=new Dictionary();
        dictionary.setId(this.id);
        dictionary.setNameEng(this.nameEng);
        dictionary.setNameVn(this.nameVn);
        dictionary.setWordType(this.wordType);
        dictionary.setDescription(this.description);
        dictionary.setCreateDate(this.createDate);
        dictionary.setUpdatedDate(this.updatedDate);
        dictionary.setStatus(this.status);
        dictionary.setMp3(this.mp3);
        dictionary.setTranscribe(this.transcribe);
        dictionary.setSynonymous(this.synonymous);
        return  dictionary;
    }

}
