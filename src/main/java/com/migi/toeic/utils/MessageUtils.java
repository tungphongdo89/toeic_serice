package com.migi.toeic.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.i18n.LocaleContextHolder;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

@Slf4j
public class MessageUtils {


	private static Locale getLocale() {
		return LocaleContextHolder.getLocale();
	}

	public static String getMessage(String code, Locale locale) {
		return getMessage(code, locale, null);
	}

	public static String getMessage(String code, Locale locale, Object... args) {
		ResourceBundle resourceBundle = ResourceBundle.getBundle("i18n/messages", locale);
		String message;
		try {
			message = resourceBundle.getString(code);
			message = MessageFormat.format(message, args);
		} catch (Exception ex) {
			log.info(">>> Can not get message with code {}", code);
			log.info(ex.getMessage(), ex);
			message = code;
		}

		return message;
	}

	public static String getMessage(String code) {

		return getMessage(code, getLocale(), null);
	}

	public static String getMessage(String code, Object... args) {
		return getMessage(code, LocaleContextHolder.getLocale(), args);
	}
}
