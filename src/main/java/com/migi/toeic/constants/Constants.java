package com.migi.toeic.constants;

public class Constants {
	public static final String SIGN_UP_URL = "/users/record";
	public static final String KEY = "q3t6w9z$C&F)J@NcQfTjWnZr4u7x!A%D*G-KaPdSgUkXp2s5v8y/B?E(H+MbQeTh";
	public static final String HEADER_NAME = "Authorization";
	public static final Long EXPIRATION_TIME = 1000L * 60 * 30;
	public static final String INSERT_ERROR="Error when insert";
    public static final String UPDATE_ERROR="Error when update";
    public static final class LEVEL_CODE {
		public static final String EASY = "EASY";
		public static final String MEDIUM = "MEDIUM";
		public static final String DIFFICULT = "DIFFICULT";
	}
	public static final class LEVEL_CODE_VN {
		public static final String EASY = "DỄ";
		public static final String MEDIUM = "TRUNG BÌNH";
		public static final String DIFFICULT = "KHÓ";
	}
	public static final class PART_TOPIC {
		public static final String PART7 = "PART7";
	}
	public static final class ROLE {
		public static final String ADMIN = "ADMIN";
		public static final String STUDENT = "STUDENT";
		public static final String PREVIEW = "PREVIEW";
		public static final String TEACHER = "TEACHER";
	}
}
