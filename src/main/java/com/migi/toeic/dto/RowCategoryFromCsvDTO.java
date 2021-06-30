package com.migi.toeic.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.github.millij.poi.ss.model.annotations.Sheet;
import io.github.millij.poi.ss.model.annotations.SheetColumn;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@JsonIgnoreProperties(ignoreUnknown =true)
@AllArgsConstructor
@NoArgsConstructor
@Sheet
public class RowCategoryFromCsvDTO implements Comparable< RowCategoryFromCsvDTO > {
    //các trường của đề bài
    @SheetColumn("Tên đề bài")
    private String nameCategory;
    @SheetColumn("Áp dụng?")
    private String status;
    @SheetColumn("Loại đề bài")
    private String typeCode;
    @SheetColumn("Trình độ")
    private String levelCode;
    @SheetColumn("Loại dữ liệu 1")
    private String typeFile1;
    @SheetColumn("Loại dữ liệu 2")
    private String typeFile2;
    @SheetColumn("Nội dung dữ liệu 1")
    private String pathFile1;
    @SheetColumn("Nội dung dữ liệu 2")
    private String pathFile2;
    @SheetColumn("Phần")
    private String part;
    @SheetColumn("Chủ đề")
    private String nameTopic;
    @SheetColumn("Transcript")
    private String transcript;
    @SheetColumn("Điểm mục tiêu")
    private Long targetScore;

    //các trường của câu hỏi
    @SheetColumn("Câu hỏi")
    private Long numQuestion;
    @SheetColumn("Nội dung câu hỏi")
    private String name;
    @SheetColumn("Trắc nghiệm?")
    private String typeQuestion;
    @SheetColumn("Điểm câu hỏi")
    private Float score;
    @SheetColumn("Dữ liệu câu hỏi 1")
    private String typeQuestionFile1;
    @SheetColumn("Dữ liệu câu hỏi 2")
    private String typeQuestionFile2;
    @SheetColumn("Nội dung dữ liệu câu hỏi 1")
    private String pathQuestionFile1;
    @SheetColumn("Nội dung dữ liệu câu hỏi 2")
    private String pathQuestionFile2;
    @SheetColumn("Transcript cho dữ liệu câu hỏi")
    private String transcriptQuestion;
    @SheetColumn("Phương án 1")
    private String answerToChoose1;
    @SheetColumn("Phương án 2")
    private String answerToChoose2;
    @SheetColumn("Phương án 3")
    private String answerToChoose3;
    @SheetColumn("Phương án 4")
    private String answerToChoose4;
    @SheetColumn("Lúc phát phương án 1")
    private String startTime1;
    @SheetColumn("Lúc phát phương án 2")
    private String startTime2;
    @SheetColumn("Lúc phát phương án 3")
    private String startTime3;
    @SheetColumn("Lúc phát phương án 4")
    private String startTime4;
    @SheetColumn("Đáp án 1")
    private String answer1;
    @SheetColumn("Đáp án 2")
    private String answer2;
    @SheetColumn("Đáp án 3")
    private String answer3;
    @SheetColumn("Giải thích")
    private String description;




    public boolean compareTwoRow(RowCategoryFromCsvDTO row){
        if(row.getNameCategory().equals(this.getNameCategory())){
            return true;
        }
        return false;
    }


    @Override
    public int compareTo(RowCategoryFromCsvDTO o) {
        if(this.getNameCategory().compareTo(o.getNameCategory())==0){
            return this.getNumQuestion().compareTo(o.getNumQuestion());
        }
        return this.getNameCategory().compareTo(o.getNameCategory());
    }
}
