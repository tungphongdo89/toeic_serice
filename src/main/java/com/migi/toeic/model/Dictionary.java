package com.migi.toeic.model;

import com.migi.toeic.dto.DictionaryDTO;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Data
@Entity
@Table(name = "DICTIONARY_MANAGEMENT")
public class Dictionary implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "dictionary")
    @SequenceGenerator(name = "dictionary",sequenceName = "dictionary_seq",allocationSize = 1)
    private Long id;
    @Column(name = "NAME_ENG")
    private String nameEng;
    @Column(name = "NAME_VN")
    private String nameVn;
    @Column(name = "WORD_TYPE")
    private String wordType;
    @Column(name = "DESCRIPTION")
    private String description;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "CREATE_DATE")
    private Date createDate;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "UPDATED_DATE")
    private Date updatedDate;
    @Column(name = "STATUS")
    private Long status;
    @Column(name="MP3")
    private  String mp3;
    @Column(name="TRANSCRIBE")
    private  String transcribe;
    @Column(name = "SYNONYMOUS")
    private String synonymous;

    public DictionaryDTO toModel()
    {
        DictionaryDTO dictionaryDTO=new DictionaryDTO();
        dictionaryDTO.setId(this.id);
        dictionaryDTO.setNameEng(this.nameEng);
        dictionaryDTO.setNameVn(this.nameVn);
        dictionaryDTO.setWordType(this.wordType);
        dictionaryDTO.setDescription(this.description);
        dictionaryDTO.setCreateDate(this.createDate);
        dictionaryDTO.setUpdatedDate(this.updatedDate);
        dictionaryDTO.setStatus(this.status);
        dictionaryDTO.setMp3(this.mp3);
        dictionaryDTO.setTranscribe(this.transcribe);
        dictionaryDTO.setSynonymous(this.synonymous);
        return dictionaryDTO;
    }

}
