package app.metatron.discovery.domain.ipm.common.constant;

/**
 * 공통 상수
 */
public class Const {

    // 공통
    public static class Common {

        // 공통 Y,N 벨류
        public static final String VALUE_Y = "Y";
        public static final String VALUE_N = "N";
        public static final String VALUE_ALL = "ALL";

        // 기본 페이지 사이즈
        public static final int VALUE_DEFUALT_PAGE_SIZE = 10;

        // 통신 결과 코드
        public static class HTTP_RESPONSE_CODE {
            public static final String SUCCESS = "200";
            public static final String INVALID_REQUEST = "400";
            public static final String UNAUTORIZED = "401";
            public static final String REQUEST_TIMEOUT = "408";
            public static final String SERVER_ERROR = "500";
            public static final String FAIL = "-1";
        }
        // RESEULT CODE
        public static class RESULT_CODE {
            public static final String SUCCESS = "C0000";
            public static final String FAIL = "C9999";
            public static final String NOT_LOGIN = "NOT_LOGIN";
            public static final String UNAUTORIZED = "UNAUTORIZED";
        }


        // 콤마
        public static final String COMMA = ",";

        // 언어
        public static final String KOREAN = "kr";
        public static final String ENGLSH = "en";

        // 엑셀뷰
        public static final String VIEW_TYPE_EXCEL = "excelXlsxView";
    }

    // 공통
    public static class SSO {
        public static final String SYSTEM_ID = "172";
        public static final String TOKEN_METTATRON_ID = "TOKEN_METTATRON_ID";
        public static final String TOKEN_TANGO_ID = "TOKEN_TANGO_ID";

    }

    // IA 관련
    public static class IA {

        // 1 depth IA codes
        public static final String MAIN = "IA000001";
        public static final String COMMUNICATION = "IA000002";
        public static final String MY_SPACE = "IA000003";
        public static final String APP_PLACE = "IA000004";
        public static final String METADATA = "IA000005";
        public static final String PORTAL_MANAGE = "IA000006";
        public static final String SITEMAP = "IA000007";
        public static final String METATRON = "IA000008";
        public static final String HELP = "IA000009";


        // 2 depth Analysis, Report
        public static final String MY_REPORT = "IA000021";
        public static final String MY_ANALYSIS = "IA000022";
        public static final String REPORT = "IA000031";
        public static final String ANALYSIS = "IA000032";

    }


    // 앱 관련
    public static class App {
        public static final String CATEGORY_REPORT = "GC0000003";
        public static final String CATEGORY_ANALYSIS = "GC0000004";

        public static final int TOP_2 = 2;
        public static final int TOP_3 = 3;
        public static final int TOP_10 = 10;
        public static final int TOP_12 = 12;
    }

    // 권한 그룹 관련
    public static class RoleGroup {
        public static final String SYSTEM_ADMIN = "SYSTEM_ADMIN";
        public static final String DEFAULT_USER = "DEFAULT_USER";

        public static final String ORG_SKT = "A000000000";

        public static final String EMAIL_RECIPIENT_GROUP = "EMAIL_RECIPIENT";
    }

    // 이메일 관련
    public static class Email {
        public static final String ADDRESS_SYSTEM = "idcube@sk.com";

        public static final String TEMPLATE_REQUEST_REG = "REQUEST_REG";
        public static final String TEMPLATE_REQUEST_MOD = "REQUEST_MOD";
        public static final String TEMPLATE_REQUEST_RLY = "REQUEST_RLY";
    }

    // 사용자 관련
    public static class USER {
        public static final String SUBFIX_PASSWORD = "@2018";
        public static final String ADMIN_ID = "admin";
    }

    // 엘라스틱서치 관련
    public static class ElasticSearch {
        public static final String INDEX = "dt-portal";

        public static final String FIELD_AUTOCOMPLETE = "autocomplete";

        public static final String TYPE_AUTO_COMPLETE = "autocomplete";

        public static final String TYPE_COMMUNICATION = "communication";
        public static final String TYPE_DT_PROJECT = "project";
        public static final String TYPE_REPORT_APP = "report-app";
        public static final String TYPE_ANALYSIS_APP = "analysis-app";
        public static final String TYPE_META_TABLE = "meta-table";
        public static final String TYPE_META_COLUMN = "meta-column";

        // 품사 태크
        public static class Tag {
            // 일반명사
            public static final String NNG = "NNG";
            // 고유 명사
            public static final String NNP = "NNP";
            // 의존명사
            public static final String NNB = "NNB";
            // 단위를 나타내는 명사
            public static final String NNBC = "NNBC";
            // 복함 COMPOUND
            public static final String COMPOUND = "COMPOUND";
            // 수사
            public static final String NR = "NR";
            // 대명사
            public static final String NP = "NP";
            // 외국어
            public static final String SL = "SL";
            // 한자
            public static final String SH = "SH";
        }
        // 품사 태크
        public static String[] AUTOCOMPLETE_TAG = {Tag.NNG, Tag.NNP, Tag.NNB, Tag.NNBC, Tag.COMPOUND,
            Tag.NR, Tag.NP, Tag.SL, Tag.SH};
    }

    /**
     * URL
     */
    public static class Url {
        public static final String PREFIX_HTTP = "http://";
        public static final String PREFIX_HTTPS = "https://";
        public static final String BLANK = "about:blank";
    }

}