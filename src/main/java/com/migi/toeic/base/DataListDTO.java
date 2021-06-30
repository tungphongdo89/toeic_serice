package com.migi.toeic.base;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public  class DataListDTO implements Serializable {
	/**
    *
    */
   private static final long serialVersionUID = 1L;
   private List data;
   private Integer total;
   private Integer size;
   private Integer start;

}
