/**
 * 쿠키 Key 상수
 */
export class CookieKey {

	// return url
	public RETURN_URL = 'D_RETURN_URL';

	// user id
	public USER_ID = 'D_USER_ID';

	// 로그인 아이디 저장
	public REM_USER_ID = 'D_REM_USER_ID';

	// token
	public TOKEN = 'D_TOKEN';

	// TangoSSO Token
	public TANGO_TOKEN = 'D_T_TOKEN';

	// Metatron Token
	public METATRON_TOKEN = 'D_M_TOKEN';

	// Metatron Token Type
	public METATRON_TOKEN_TYPE = 'D_M_TOKEN_TYPE';

	// Metatron Refresh Token
	public METATRON_REFRESH_TOKEN = 'D_M_REFRESH_TOKEN';

	// Language
	public LANGUAGE = 'D_LANG';

	// 검색어 자동 완성 사용여부 플래그
	public SEARCH_WORD_AUTO_COMPLETE_USE_FLAG = 'D_SEARCH_WORD_AUTO_COMPLETE_USE_FLAG';

	// 검색어 쿠키 저장여부 플래그
	public SEARCH_WORD_SAVE_FLAG = 'D_SEARCH_WORD_SAVE_FLAG';

	// 검색어 리스트
	public SEARCH_WORD_LIST = 'D_SEARCH_WORD_LIST';

	// 공지알림
	public NOTICE = 'D_NOTICE';

	// DW 에서 사용할 페이지 사이즈
	public DW_PAGE_SIZE = 'D_DW_PAGE_SIZE';
}

/**
 * 쿠키 관련 상수
 */
export class CookieConstant {

	// 쿠키 key
	public static KEY: CookieKey = new CookieKey();
}
