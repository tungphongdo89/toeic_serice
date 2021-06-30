/*
 * Copyright (C) 2010 Viettel Telecom. All rights reserved.
 * VIETTEL PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.migi.toeic.utils;

import com.migi.toeic.exception.BusinessException;
import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidateUtils {

	/**
	 * private contructor
	 */
	private ValidateUtils() {
	}

	/**
	 * <P>Check is Integer or not</P>
	 *
	 * @param str String to check
	 * @param str
	 * @return @boolean true if valid, false if not valid
	 */
	public static boolean isInteger(String str) {
		if (str == null || !str.matches("[0-9]+$")) {
			return false;
		}
		return true;
	}

	public static String validateKeySearch(String str) {
		return str.replaceAll("&", "&&").replaceAll("%", "&%").replaceAll("_", "&_");
	}

	public static boolean getSpecialCharacterCount(String s) {
		if (s == null || s.trim().isEmpty()) {
			return false;
		}
		Pattern p = Pattern.compile("[^A-Za-z0-9_]");
		Matcher m = p.matcher(s.replaceAll(" ", ""));
		// boolean b = m.matches();
		boolean b = m.find();
		if (b == true)
			return false;
		else
			return true;
	}

	public static boolean getSpecialCharacterCountName(String s) {
		if (s == null || s.trim().isEmpty()) {
			return false;
		}
		Pattern p = Pattern.compile("[^A-Za-z0-9Ạ-ỹáàạảãâấầậẩẫăắằặẳẵÁÀẠẢÃÂẤẦẬẨẪĂẮẰẶẲẴéèẹẻẽêếềệểễÉÈẸẺẼÊẾỀỆỂỄóòọỏõôốồộổỗơớờợởỡÓÒỌỎÕÔỐỒỘỔỖƠỚỜỢỞỠúùụủũưứừựửữÚÙỤỦŨƯỨỪỰỬỮíìịỉĩÍÌỊỈĨđĐýỳỵỷỹÝỲỴỶỸ]");
		Matcher m = p.matcher(s.replaceAll(" ", ""));
		// boolean b = m.matches();
		boolean b = m.find();
		if (b == true)
			return false;
		else
			return true;
	}

	public static boolean isStringInt(String s) {
		try {
			int a = Integer.parseInt(s);
			if (a > 0) {
				return true;
			} else {
				return false;
			}
		} catch (NumberFormatException ex) {
			return false;
		}
	}

	public static boolean isStringFloat(String s) {

		try {
			Float.parseFloat(s);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	public static boolean isStringDouble(String s) {

		try {
			Double.parseDouble(s);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	public static boolean isStringLong(String s) {

		try {
			Long.parseLong(s);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

//	public static void checkRequire(String param, String messKey, String messParam) {
//		if (StringUtils.isEmpty(param)) {
//			throw new BusinessException(MessageUtils.getMessage(messKey, messParam));
//		}
//	}
//
//	public static void checkMaxlength(String param, String messKey, String messParam, int maxLength) {
//		if (param.length() > maxLength) {
//			throw new BusinessException(MessageUtils.getMessage(messKey, messParam, maxLength));
//		}
//	}
}
