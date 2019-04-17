package app.metatron.discovery.domain.ipm.domain.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;
import lombok.Data;

public class UserBmrkDto {

    /**
     * 사용자 즐겨찾기 상세 목록 조회
     */
    public interface UserBmrkDtlList {

        // 즐겨찾기 UID
        String getBmrkUid();

        // 즐겨찾기 명
        String getBmrkNm();

        // 필터 데이터 값
        List<Object> getFltrDatVal();
    }

    /**
     * 필터 데이터 값
     */
    @Data
    public static class FltrDatVal {

        // 필터 UID
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private String fltrUid;

        // 필터 명
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private String fltrNm;

        // druid 명
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private String druidNm;

        // 화면 명
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private String scrnNm;

        // 화면 분류 명
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private String scrnClNm;

        // 필터 삭제 여부
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private String fltrDelYn;

        // 필터 제외 여부
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private String fltrWoYn;

        // type : NOT 필터
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private String type;

        // dt 명 : 화면용 필터 요약
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private String dtNm;

        // 필터 값
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private List<ComDto.Code> fltrVal;
    }
}
