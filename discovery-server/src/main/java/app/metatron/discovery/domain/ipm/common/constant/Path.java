package app.metatron.discovery.domain.ipm.common.constant;

/**
 * 모든 Request Path 에 대해 정의
 */
public class Path {

    /**
     * API Prefix
     */
    public static final String API = "/api";

    /*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
	| LOG & UI Tagging
	|-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/


    public static final String LOG = API + "/log";
    public static final String LOG_SYS = LOG + "/sys";
    public static final String LOG_APP = LOG + "/app";

    public static final String TAGGING = API + "/tag";

    /*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
	| METATRON
	|-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

    public static final String METATRON ="/metatron";

    /*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    | Login
	|-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

    public static final String LOGIN_VIEW = "http://idcube.sktelecom.com/view/user/login";

    public static final String LOGIN = API + "/login";

    public static final String LOGIN_CHECK = API + "/login/check";

    public static final String LOGOUT = API + "/logout";

    /*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
	| AUTH
	|-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

    public static final String AUTH = API + "/auth";

    /*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
	| SSO
	|-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

    public static final String SSO_SITE = API + "/sso/site/{type}";

    public static final String SSO_FROM_TNET = "/sso/tnet";

    public static final String SSO_TNET_VIEW = "http://idcube.sktelecom.com/view/sso/loading/tnet/pre";

    public static final String SSO_METATRON = API + "/sso/metatron/token";

    /*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
	| COMMON
	|-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

    public static final String CODE = API + "/codes";

    public static final String CODE_DETAIL = CODE + "/{id}";

    public static final String GROUP_CODE = API + "/group-codes";

    public static final String GROUP_CODE_DETAIL = GROUP_CODE + "/{id}";

    public static final String CODE_BY_GROUP_CODE = GROUP_CODE_DETAIL + "/codes";

    public static final String CODE_BY_GROUP_CODES = GROUP_CODE + "/codes";

    /*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
	| ZipCode
	|-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

    public static final String ZIPCODE = API + "/zipcodes";

    public static final String ZIPCODE_POLYGON = ZIPCODE + "/wkt";

    public static final String ZIPCODE_SIDO = ZIPCODE + "/sido";

    public static final String ZIPCODE_SIGUNGU = ZIPCODE + "/sido/{sido-nm}/sigungu";

    public static final String ZIPCODE_DONG = ZIPCODE + "/sido/{sido-nm}/sigungu/{sigungu-nm}/dong";

    public static final String ZIPCODE_CODE = ZIPCODE + "/sido/{sido-nm}/sigungu/{sigungu-nm}/dong/{dong-nm}/zipcode";

    /*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
	| User
	|-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

    public static final String USER = API + "/users";

    public static final String USER_DETAIL = USER + "/{id}";

    public static final String USER_START_PAGE = USER + "/start-page";

    public static final String USER_ME = USER + "/me";

    public static final String USER_PROFILE = USER + "/profile";

    public static final String USER_SEARCH_EMAIL = USER + "/email";

    /*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
	| User's ROLE GROUP
	|-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

    public static final String GROUP = API + "/groups";

    public static final String GROUP_DETAIL = GROUP + "/{id}";

    public static final String GROUP_ADD_MEMBER = GROUP_DETAIL + "/add-members";

    public static final String GROUP_MEMBERS = GROUP_DETAIL + "/members";

    public static final String GROUP_ROOT_LIST = GROUP + "/root";

    public static final String GROUP_CHILDREN = GROUP_DETAIL + "/children";

    public static final String GROUP_PARENT = GROUP_DETAIL + "/parent";

    public static final String GROUP_ANALYSIS_APP = GROUP + "/analysis-app/{id}";

    public static final String GROUP_REPORT_APP = GROUP + "/report-app/{id}";

    public static final String GROUP_REQUEST_LIST = GROUP + "/request";

    public static final String GROUP_REQUEST_COUNT = GROUP + "/request/count";

    public static final String GROUP_REQUEST = GROUP + "/request";

    public static final String GROUP_DENY = GROUP + "/deny/{id}";

    public static final String GROUP_ACCEPT = GROUP + "/accept/{id}";

    public static final String GROUP_IA_PERMISSION_SETUP = GROUP_DETAIL + "/setup";

    public static final String GROUP_SYSTEM_ADMIN = GROUP + "/sa/{userId}";

    /*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
	| IA & MENU
	|-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

    public static final String IA = API + "/ia";

    public static final String MENU = IA + "/menu";

    public static final String MENU_1DEPTH = MENU + "/1depth";

    public static final String MENU_HAS_ROLE = MENU + "/{id}/role";

    public static final String IA_ROOT_LIST = IA + "/root";

    public static final String IA_DETAIL = IA + "/{id}";

    public static final String IA_CHILDREN_LIST = IA_DETAIL + "/children";


    /*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
	| MEDIA & FILE
	|-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

    public static final String MEDIA = API + "/media";

    public static final String MEDIA_VIEW = MEDIA + "/{id}";

    public static final String MEDIA_VIEW_TYPE = MEDIA + "/{id}/{type}";

    public static final String FILE = API + "/file";

    public static final String FILE_LIST = FILE + "/{id}";

    public static final String FILE_UPLOAD = FILE + "/upload";

    public static final String FILE_DOWNLOAD = FILE + "/download/{id}";

    /*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    | Extract
	|-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

    public static final String EXTRACT_APP = API + "/extract";

    public static final String EXTRACT_CUSTOM_VAR = EXTRACT_APP + "/vars";

    public static final String EXTRACT_CUSTOM_VAR_DETAIL = EXTRACT_CUSTOM_VAR + "/{id}";

    public static final String EXTRACT_CUSTOM_VAR_EXEC = EXTRACT_CUSTOM_VAR + "/{name}/exec";

    public static final String EXTRACT_APP_PARSE = EXTRACT_APP + "/parse";

    public static final String EXTRACT_APP_PROCESS = EXTRACT_APP + "/process";

    public static final String EXTRACT_CUSTOM_VAR_CHECK = EXTRACT_APP + "/check";

    /*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    | DataSource
	|-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

    public static final String DATASOURCE = API + "/datasource";

    public static final String DATASOURCE_DETAIL = DATASOURCE + "/{id}";

    public static final String DATASOURCE_TEST = DATASOURCE_DETAIL + "/test";

    public static final String DATASOURCE_SELECT = DATASOURCE_DETAIL + "/select";

    public static final String DATASOURCE_EXEC = DATASOURCE_DETAIL + "/execute";

    public static final String DATASOURCE_EXPORT = DATASOURCE_SELECT + "/export";

    public static final String DATASOURCE_VALID = DATASOURCE_DETAIL + "/valid";

    /*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    | Communication
	|-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

    public static final String COMMUNICATION = API + "/communication";

    public static final String COMMUNICATION_ALL_POSTS = COMMUNICATION + "/all";

    public static final String COMMUNICATION_BY_TYPE = COMMUNICATION + "/type";

    public static final String COMMUNICATION_MY_POSTS = COMMUNICATION + "/my/posts";

    public static final String COMMUNICATION_REPLIES_TO_MY_POSTS = COMMUNICATION_MY_POSTS + "/replies";

    public static final String COMMUNICATION_POSTS_MY_REPLIES = COMMUNICATION + "/my/replies";

    public static final String COMMUNICATION_MEDIA_UPLOAD = COMMUNICATION + "/media/upload";

    public static final String COMMUNICATION_MEDIA_UPLOAD_CHANGE = COMMUNICATION_MEDIA_UPLOAD + "/{id}";

    public static final String COMMUNICATION_POSTS = COMMUNICATION + "/{slug}";

    public static final String COMMUNICATION_MASTER = COMMUNICATION_POSTS + "/master";

    public static final String COMMUNICATION_POSTS_DRAFT = COMMUNICATION_POSTS + "/draft";

    public static final String COMMUNICATION_POSTS_DETAIL = COMMUNICATION_POSTS + "/{id}";

    public static final String COMMUNICATION_POSTS_WORKER = COMMUNICATION_POSTS_DETAIL + "/worker";

    public static final String COMMUNICATION_POSTS_REPLIES = COMMUNICATION_POSTS_DETAIL + "/replies";

    public static final String COMMUNICATION_POSTS_REPLIES_DETAIL = COMMUNICATION_POSTS_REPLIES + "/{replyId}";

    public static final String COMMUNICATION_INDICES = COMMUNICATION + "/indices";

    public static final String COMMUNICATION_SUMMARY = COMMUNICATION + "/summary";

    /*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    | Main
	|-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

    public static final String MAIN = API + "/main";

    public static final String MAIN_COMMUNICATIONS = MAIN + "/communications";

    public static final String MAIN_ANALYSIS_APPS = MAIN + "/analysis-apps";

    public static final String MAIN_REPORT_APPS = MAIN + "/report-apps";

    public static final String MAIN_PROJECTS = MAIN + "/projects";

    public static final String MAIN_NOTICES = MAIN + "/notices";

    public static final String MAIN_NOTICES_CHECK = MAIN_NOTICES + "/check";

    public static final String MAIN_ALARM_BADGE = MAIN + "/badge";

    public static final String MAIN_REQUESTED_NOT_PROCESS = MAIN + "/requested-not-process";

    public static final String MAIN_REQUESTED_PROCESS = MAIN + "/requested-process";

    public static final String MAIN_REQUESTED_COMPLETED = MAIN + "/requested-completed";

    /*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    | Metadata
	|-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

    public static final String METADATA = API + "/meta";

    public static final String METADATA_SUBJECT = METADATA + "/subjects";

    public static final String METADATA_SUBJECT_ROOT = METADATA_SUBJECT + "/root";

    public static final String METADATA_SUBJECT_DETAIL = METADATA_SUBJECT + "/{id}";

    public static final String METADATA_SUBJECT_TABLE = METADATA_SUBJECT_DETAIL + "/table";

    public static final String METADATA_TABLE_DETAIL = METADATA + "/table/{id}";

    public static final String METADATA_COLUMN_DETAIL = METADATA + "/column/{id}";

    public static final String METADATA_TABLE_SAMPLE = METADATA_TABLE_DETAIL + "/sample-data";

    public static final String METADATA_TABLE_SAMPLE_LIST = METADATA_TABLE_SAMPLE + "/list";


    public static final String METADATA_INDICES = METADATA + "/indices";

    public static final String METADATA_TABLE_INDICES = METADATA_INDICES + "/table";

    public static final String METADATA_COLUMN_INDICES = METADATA_INDICES + "/column";


    public static final String METADATA_SYSTEM = METADATA + "/system";

    public static final String METADATA_SYSTEM_ROOT = METADATA_SYSTEM + "/root";

    public static final String METADATA_INSTANCE = METADATA + "/instance";

    public static final String METADATA_INSTANCE_DETAIL = METADATA_INSTANCE + "/{id}";

    public static final String METADATA_INSTANCE_DATABASE = METADATA_INSTANCE_DETAIL + "/database";

    public static final String METADATA_SYSTEM_DETAIL = METADATA_SYSTEM + "/{id}";

    public static final String METADATA_SYSTEM_CHILDREN = METADATA_SYSTEM_DETAIL + "/children";

    public static final String METADATA_DATABASE_DETAIL = METADATA + "/database/{id}";

    public static final String METADATA_DATABASE_TABLE = METADATA_DATABASE_DETAIL + "/table";

    public static final String METADATA_INSTANCE_TABLE = METADATA_INSTANCE_DETAIL + "/table";

    public static final String METADATA_DICTIONARY = METADATA + "/dic";

    public static final String METADATA_DICTIONARY_DETAIL = METADATA_DICTIONARY + "/{id}";

    /*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    | Project
	|-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

    public static final String PROJECTS = API + "/projects";

    public static final String PROJECTS_DETAIL = PROJECTS + "/{id}";

    public static final String PROJECTS_INDICES = PROJECTS + "/indices";

    /*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    |  Analysis App
    |-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/
    // ANALYSIS_APP 리스트 조회
    public static final String ANALYSIS_APPS = API + "/analysis-apps";

    // 분석앱 관리용 카테고리 리스트
    public static final String ANALYSIS_APPS_CATEGORIES = ANALYSIS_APPS + "/categories";

    // 앱 플레이스 메인 정보(통합)
    public static final String ANALYSIS_APPS_PAGE_MAIN  = ANALYSIS_APPS + "/main";

    // ANALYSIS_APP 상세 정보(통합)
    public static final String ANALYSIS_APPS_PAGE_DETAIL  = ANALYSIS_APPS + "/{id}/detail";

    // ANALYSIS_APP 조회
    public static final String ANALYSIS_APPS_DETAIL = ANALYSIS_APPS + "/{id}";

    // ANALYSIS_APP Category 조회
    public static final String ANALYSIS_APPS_CODE = ANALYSIS_APPS + "/codes";

    // ANALYSIS_APP 자동 추천
    public static final String ANALYSIS_APPS_RECOMMAND = ANALYSIS_APPS + "/recommand";

    // ANALYSIS_APP 인기 순위
    public static final String ANALYSIS_APPS_RANK = ANALYSIS_APPS + "/rank";


    // ANALYSIS_APP MY APP 조회
    public static final String ANALYSIS_MY_APP = ANALYSIS_APPS + "/my-app";

    public static final String ANALYSIS_MY_APP_MAIN = ANALYSIS_APPS + "/my-app/main";

    // ANALYSIS_APP MY APP 추가(POST)/삭제(DELETE)/실행(GET)
    public static final String ANALYSIS_MY_APP_DETAIL = ANALYSIS_APPS + "/my-app/{id}";

    // ANALYSIS_APP 리뷰 분석앱별 리스트
    public static final String ANALYSIS_APPS_ID_REVIEWS = ANALYSIS_APPS + "/{id}/reviews";

    // ANALYSIS_APP 리뷰 리스트
    public static final String ANALYSIS_APPS_REVIEWS = ANALYSIS_APPS + "/reviews";

    // ANALYSIS_APP 리뷰 상세 (POST)/삭제(DELETE)/실행(GET)
    public static final String ANALYSIS_APPS_REVIEWS_DETAIL = ANALYSIS_APPS + "/reviews/{id}";

    public static final String ANALYSIS_APPS_INDICES = ANALYSIS_APPS + "/indices";

    /*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    |  Report App
    |-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/
    // REPORT_APP 리스트 조회
    public static final String REPORT_APPS = API + "/report-apps";

    // Report 관리용 카테고리 리스트
    public static final String REPORT_APPS_CATEGORIES = REPORT_APPS + "/categories";

    // 앱 플레이스 메인 정보(통합)
    public static final String REPORT_APPS_PAGE_MAIN  = REPORT_APPS + "/main";

    // REPORT_APP 상세 정보(통합)
    public static final String REPORT_APPS_PAGE_DETAIL  = REPORT_APPS + "/{id}/detail";

    // REPORT_APP 조회
    public static final String REPORT_APPS_DETAIL = REPORT_APPS + "/{id}";

    // REPORT_APP Category 조회
    public static final String REPORT_APPS_CODE = REPORT_APPS + "/codes";

    // REPORT_APP 자동 추천
    public static final String REPORT_APPS_RECOMMAND = REPORT_APPS + "/recommand";

    // REPORT_APP 인기 순위
    public static final String REPORT_APPS_RANK = REPORT_APPS + "/rank";


    // REPORT_APP MY APP 조회
    public static final String REPORT_MY_APP = REPORT_APPS + "/my-app";

    public static final String REPORT_MY_APP_MAIN = REPORT_APPS + "/my-app/main";

    // REPORT_APP MY APP 추가(POST)/삭제(DELETE)/실행(GET)
    public static final String REPORT_MY_APP_DETAIL = REPORT_APPS + "/my-app/{id}";

    // REPORT_APP 리뷰 분석앱별 리스트
    public static final String REPORT_APPS_ID_REVIEWS = REPORT_APPS + "/{id}/reviews";

    // REPORT_APP 리뷰 리스트
    public static final String REPORT_APPS_REVIEWS = REPORT_APPS + "/reviews";

    // REPORT_APP 리뷰 상세 (POST)/삭제(DELETE)/실행(GET)
    public static final String REPORT_APPS_REVIEWS_DETAIL = REPORT_APPS + "/reviews/{id}";

    public static final String REPORT_APPS_INDICES = REPORT_APPS + "/indices";

    /*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
	| Custom Approach
	|-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

    public static final String CUSTOM = API + "/custom";

    public static final String CUSTOM_APPROACH_ZIPCODES = CUSTOM + "/approach/zipcodes";
    public static final String CUSTOM_APPROACH_ZIPCODES_BULIDINGS = CUSTOM + "/approach/zipcodes/buildings";
    public static final String CUSTOM_APPROACH_ZIPCODES_DATA = CUSTOM + "/approach/zipcodes/data";
    public static final String CUSTOM_APPROACH_ZIPCODES_POPULATION = CUSTOM + "/approach/zipcodes/population";
    public static final String CUSTOM_APPROACH_ZIPCODES_HOUSEHOLDS = CUSTOM + "/approach/zipcodes/households";
    public static final String CUSTOM_APPROACH_INFILTRATIONRATE = CUSTOM + "/approach/zipcodes/infiltrationrate";

    /*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
	| Search
	|-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

    public static final String SEARCH = API + "/search";
    public static final String SEARCH_AUTO_COMPLETE= SEARCH + "/auto-complete";
    public static final String SEARCH_ALL= SEARCH + "/all";
    public static final String SEARCH_TYPE= SEARCH + "/type";




}
