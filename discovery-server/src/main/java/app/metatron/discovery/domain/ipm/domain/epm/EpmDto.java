package app.metatron.discovery.domain.ipm.domain.epm;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import app.metatron.discovery.domain.ipm.domain.common.ComDto;
import app.metatron.discovery.domain.ipm.domain.common.UserBmrkDto;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

public class EpmDto {

    /**
     * filter rmk : QRY
     */
    private static final String QRY = "QRY";

    /**
     * epm 분류 기본 목록
     */
    public interface EpmClBasList {

        // 분류 UID
        String getClUid();

        // 분류 명
        String getClNm();

        // 하위 분류 목록
        List<EpmClBasList> getClList();
    }

    /**
     * epm 분류 기본
     */
    public interface EpmClBas {

        // 필터 목록
        List<EpmFltrBasList> getFltrList();
    }

    /**
     * epm 필터 기본 목록
     */
    public interface EpmFltrBasList {

        // 필터 UID
        String getFltrUid();

        // 필터 명
        String getFltrNm();

        // druid 명
        String getDruidNm();

        // 화면 명
        String getScrnNm();

        // 화면 분류 명
        String getScrnClNm();

        // leaf 여부
        String getLeafYn();

        // 사용 여부
        String getUseYn();

        // 필터 삭제 여부
        String getFltrDelYn();

        // 필터 제외 여부
        String getFltrWoYn();

        // 하위 필터 목록
        List<EpmFltrBasList> getFltrList();
    }

    /**
     * epm 필터 기본
     */
    public interface EpmFltrBas {

        // 필터 UID
        String getFltrUid();

        // 필터 명
        String getFltrNm();

        // druid 명
        String getDruidNm();

        // 화면 명
        String getScrnNm();

        // 화면 분류 명
        String getScrnClNm();

        // 필터 설명
        String getFltrDesc();

        // 사용 여부
        String getUseYn();

        // 필터 삭제 여부
        String getFltrDelYn();

        // 필터 제외 여부
        String getFltrWoYn();

        // 필터 상세 목록
        List<EpmFltrDtlList> getFltrDtlList();
    }

    /**
     * epm 필터 상세 목록
     */
    public interface EpmFltrDtlList {

        // 필터 단위
        String getFltrUnit();

        // 필터 분류 명
        String getFltrClNm();

        // 필터 비고
        @JsonIgnore
        String getFltrRmk();

        // 필터 값
        @JsonIgnore
        String getFltrVal();

        // values
        default List<Object> getValues() {
            String fltrVal = getFltrVal();
            String rmk = getFltrRmk();
            List<Object> list = new ArrayList<>();

            if (fltrVal != null) {
                // range, calendar, checkrange
                if (fltrVal.contains("~")) {
                    String[] arr = fltrVal.split("~");
                    int arrSize = arr.length;
                    if (arrSize > 0) {
                        for (String item : arr) {
                            list.add(item);
                        }
                    }

                    // mutiple, list
                } else if (fltrVal.contains("|")) {
                    String[] arr = fltrVal.split("\\|");
                    int arrSize = arr.length;
                    if (arrSize > 0) {
                        for (String item : arr) {
                            String[] items = Arrays.copyOf(item.split(":"), 2);
                            if (StringUtils.isBlank(items[1])) {
                                items[1] = items[0];
                            }
                            list.add(new ComDto.Code(items[0], items[1]));
                        }
                    }

                    // range, calendar, checkrange, mutiple, list이 아닌 경우 : fltrVal add
                } else {
                    list.add(fltrVal);
                }

                // fltrVal이 null인 경우 : 공백 add
            } else {
                list.add("");
            }

            return list;
        }

        // rmk : 초과/미만
        default String getRmk() {
            String rmk = getFltrRmk();
            if (StringUtils.isNotBlank(rmk)) {
                if (!QRY.equals(rmk)) {
                    String[] arr = rmk.split("\\|");
                    int arrSize = arr.length;
                    if (arrSize > 1) {
                        // + (초과 가능) / - (미만 가능)
                        rmk = arr[1];

                        // arrSize가 1인 경우 : 초과/미만 null 처리
                    } else {
                        rmk = null;
                    }
                }
            }

            return rmk;
        }

        // rmk : step
        default String getStep() {
            String rmk = getFltrRmk();
            if (StringUtils.isNotBlank(rmk)) {
                if (!QRY.equals(rmk)) {
                    String[] arr = rmk.split("\\|");
                    int arrSize = arr.length;
                    if (arrSize > 0) {
                        rmk = arr[0];
                    }

                    // RMK가 QRY인 경우 : step null 처리
                } else {
                    rmk = null;
                }
            }

            return rmk;
        }
    }

    /**
     * 차트
     */
    @Data
    public static class Chart {

        // 차트 번호
        private String chartNum;

        // 필터 데이터 값
        private List<UserBmrkDto.FltrDatVal> fltrDatVal;

        // 조직 코드
        private List<String> orgCd;

        // 주소 코드
        private List<String> addrCd;

        // 페이징
        private Map<String, Integer> paging;

        // 정보
        private List<Info> info;

        // limit
        private String limit;
    }

    /**
     * 정보
     */
    @Data
    public static class Info {

        // network
        private String network;

        // 장비
        private String equipment;

        // 제조사
        private String vendor;

        // 발생일시
        private List<ComDto.Code> occrDth;

        // 조직코드
        private List<String> orgCd;

        // 주소 코드
        private List<String> addrCd;

        // ems 코드
        private List<String> emsCd;

        // 국사 타입 코드
        private String mtsoTypCd;

        // druid 명
        private String druidNm;

        // eNB ID
        private String enbId;

        // 장비 명
        private String eqpNm;

        // Cell 번호
        private String cellNum;

        // 일시
        private String dth;

        // step
        private String step;

        public Info() {}

        public Info(String network, String equipment, String vendor) {
            this.network = network;
            this.equipment = equipment;
            this.vendor = vendor;
        }

        public Info(String network, String equipment, String vendor, List<ComDto.Code> occrDth, List<String> orgCd, List<String> addrCd,
                    List<String> emsCd, String mtsoTypCd, String druidNm, String enbId, String eqpNm, String cellNum, String dth, String step) {
            this.network = network;
            this.equipment = equipment;
            this.vendor = vendor;
            this.occrDth = occrDth;
            this.orgCd = orgCd;
            this.addrCd = addrCd;
            this.emsCd = emsCd;
            this.mtsoTypCd = mtsoTypCd;
            this.druidNm = druidNm;
            this.enbId = enbId;
            this.eqpNm = eqpNm;
            this.cellNum = cellNum;
            this.dth = dth;
            this.step = step;
        }
    }

    /**
     * 장비
     */
    @Data
    public static class Equipment {

        @JsonInclude(JsonInclude.Include.NON_NULL)
        private String equipment;

        @JsonInclude(JsonInclude.Include.NON_NULL)
        private List<List<String>> list;

        @JsonInclude(JsonInclude.Include.NON_NULL)
        private List<String> header;

        @JsonInclude(JsonInclude.Include.NON_NULL)
        private String cnt;

        @JsonInclude(JsonInclude.Include.NON_NULL)
        private List<List<String>> smryList;

        @JsonInclude(JsonInclude.Include.NON_NULL)
        private List<ComDto.Code> smryHeader;

        @JsonInclude(JsonInclude.Include.NON_NULL)
        private Object paging;

        public Equipment(String equipment, List<List<String>> list, List<String> header) {
            this.equipment = equipment;
            this.list = list;
            this.header = header;
        }

        public Equipment(String equipment, List<List<String>> list, List<String> header, String cnt, List<List<String>> smryList, List<ComDto.Code> smryHeader, Map<String, Integer> paging) {
            this.equipment = equipment;
            this.list = list;
            this.header = header;
            this.cnt = cnt;
            this.smryList = smryList;
            this.smryHeader = smryHeader;
            this.paging = paging;
        }
    }
}
