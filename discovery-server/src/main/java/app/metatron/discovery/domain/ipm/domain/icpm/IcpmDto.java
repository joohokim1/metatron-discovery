package app.metatron.discovery.domain.ipm.domain.icpm;

import com.fasterxml.jackson.annotation.JsonIgnore;
import app.metatron.discovery.domain.ipm.domain.common.ComDto;
import app.metatron.discovery.domain.ipm.domain.common.UserBmrkDto;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

public class IcpmDto {

    /**
     * filter rmk : QRY
     */
    private static final String QRY = "QRY";

    /**
     * icpm 필터 기본 목록
     */
    public interface IcpmFltrBasList {

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

        // 하위 필터 목록
        List<IcpmFltrBasList> getFltrList();
    }

    /**
     * icpm 필터 상세 목록
     */
    public interface IcpmFltrDtlList {

        // 필터 단위
        String getFltrUnit();

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
     * icpm 필터 기본
     */
    public interface IcpmFltrBas {

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

        // 필터 상세 목록
        List<IcpmFltrDtlList> getFltrDtlList();
    }

    /**
     * icpm chart
     */
    @Data
    public static class Chart {

        // 차트 번호
        private String chartNum;

        // 필터 데이터 값
        private List<UserBmrkDto.FltrDatVal> fltrDatVal;

        // 주소 코드
        private List<String> addrCd;

        // 날짜
        private String occrDt;
    }
}
