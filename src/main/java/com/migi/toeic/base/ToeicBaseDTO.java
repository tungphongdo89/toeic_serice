package com.migi.toeic.base;

public abstract class ToeicBaseDTO {
	/**
    *
    */
   private static final long serialVersionUID = 1L;
   private Integer page;
   private Long id;
   private String pathFile;

   public String getPathFile() {
       return pathFile;
   }

   public void setPathFile(String pathFile) {
       this.pathFile = pathFile;
   }

   public Long getId() {
       return id;
   }

   public void setId(Long id) {
       this.id = id;
   }

   public String getText() {
       return text;
   }

   public void setText(String text) {
       this.text = text;
   }

   private String text;
   private boolean isSize;

   public boolean getIsSize() {
       return isSize;
   }

   public void setIsSize(boolean isSize) {
       this.isSize = isSize;
   }

   public Integer getPage() {
       return page;
   }

   public void setPage(Integer page) {
       this.page = page;
   }

   public Integer getPageSize() {
       return pageSize;
   }

   public void setPageSize(Integer pageSize) {
       this.pageSize = pageSize;
   }

   private Integer pageSize;
   private String keySearch;
   private String keySearchAction;
   // thêm keysearch2
   private String keySearch2;

   public String getKeySearchAction() {
       return keySearchAction;
   }

   public void setKeySearchAction(String keySearchAction) {
       this.keySearchAction = keySearchAction;
   }

   public String getKeySearch() {
       return keySearch;
   }

   public void setKeySearch(String keySearch) {
       this.keySearch = keySearch;
   }

   public String getKeySearch2() {
       return keySearch2;
   }

   public void setKeySearch2(String keySearch2) {
       this.keySearch2 = keySearch2;
   }

   private Integer totalRecord;

   public Integer getTotalRecord() {
       return totalRecord;
   }

   public void setTotalRecord(Integer totalRecord) {
       this.totalRecord = totalRecord;
   }

}
