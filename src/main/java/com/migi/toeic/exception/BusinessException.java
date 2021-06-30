package com.migi.toeic.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.ArrayList;
import java.util.List;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BusinessException extends RuntimeException {

	/**
	 *
	 */
	private static final long serialVersionUID = -609099771903107222L;

	private List<Object> lstParam;

	public List<Object> getLstParam() {
		return lstParam;
	}

	public void setLstParam(List<Object> lstParam) {
		this.lstParam = lstParam;
	}

	public BusinessException(String arg0) {
		super(arg0);
	}

	public BusinessException(String arg0, Object... params) {
		super(arg0);
		lstParam = new ArrayList<>();
		for (Object object : params) {
			lstParam.add(object);
		}
	}
}
