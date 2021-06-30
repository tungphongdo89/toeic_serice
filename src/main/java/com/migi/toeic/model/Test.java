package com.migi.toeic.model;

import com.migi.toeic.dto.TestDTO;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Data
@Entity
@Table(name = "TEST")
public class Test implements Serializable {

    @Id
    @SequenceGenerator(sequenceName = "Test_SEQ",allocationSize = 1,name = "test")
    @GeneratedValue(generator = "test",strategy = GenerationType.SEQUENCE)
    private Long id;
    @Column(name = "NAME")
    private String name;
    @Column(name = "TYPE")
    private Long type;
    @Column(name = "RANK_CODE")
    private Long rankCode;
    @Column(name = "TARGET_SCORE")
    private Long targetScore;
    @Column(name = "TIMING")
    private Long timing;
    @Column(name = "STATUS")
    private Long status;
    @Column(name = "CREATE_TIME")
    private Date createTime;
    @Column(name = "UPDATED_TIME")
    private Date updatedTime;
    @Column(name = "PATH_FILE_1")
    private String pathFile1;
    @Column(name = "PATH_FILE_2")
    private String pathFile2;
    @Column(name = "TYPE_FILE_2")
    private String typeFile2;
    @Column(name = "TYPE_FILE_1")
    private String typeFile1;

    public TestDTO toModel()
    {
        TestDTO testDTO =new TestDTO();

        testDTO.setId(this.id);
        testDTO.setName(this.name);
        testDTO.setType(this.type);
        testDTO.setRankCode(this.rankCode);
        testDTO.setTargetScore(this.targetScore);
        testDTO.setTiming(this.timing);
        testDTO.setStatus(this.status);
        testDTO.setCreate_time(this.createTime);
        testDTO.setUpdated_time(this.updatedTime);
        testDTO.setPathFile1(this.pathFile1);
        testDTO.setPathFile2(this.pathFile2);
        testDTO.setTypeFile1(this.typeFile1);
        testDTO.setTypeFile2(this.typeFile2);
        return testDTO;
    }

}
