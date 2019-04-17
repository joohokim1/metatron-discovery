package app.metatron.discovery.domain.ipm.domain.lpm;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.Data;
import org.apache.parquet.Strings;

@Data
public class CommDto<K,V> {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private K name;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private K code;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private V value;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private K leaf;

    public CommDto(K name, K code, V value, K leaf) {
        this.name = name; this.code = code; this.value = value; this.leaf = leaf;
    }

    public CommDto(K name, K code, V value) {
        this.name = name; this.code = code; this.value = value;
    }

    public CommDto(K name, K code) {
        this.name = name; this.code = code;
    }

    public CommDto() {

    }

    public static CommDto set(Object name, Object value) {
        CommDto ret = new CommDto();
        ret.setName(name);
        ret.setValue(value);
        return ret;
    }
    
    public static CommDto set(Object name, Object code, Object value) {
        CommDto ret = new CommDto();
        ret.setName(name);
        ret.setCode(code);
        ret.setValue(value);
        return ret;
    }    

    @Data
    public static class CHART<T> {
        List<T> target;
        List<T> all;
    }

    @Data
    public static class FAVOR {
        private String dtNm;
        private String type;

//        private String fieldNm;
        private String fltrNm;

//        private String dispDesc;
        private String scrnNm;

        private String scrnClNm;

//        private List<CommDto> dtValue;
        private List<CommDto> fltrVal;

        private boolean usedHour;
        
//        private String fieldId;
        private String fltrUid;

        private String fltrDelYn;
        private String fltrWoYn;

        private String druidNm;
        private List<String> multiDruidNm;

        public List<String> getMultiDruidNm() {
            String OR = "\\|";
            List<String> ret = new ArrayList<>();
            if(Strings.isNullOrEmpty(druidNm)) {
                return ret;
            }

            if(druidNm.contains("|")) {
                ret = Arrays.asList(druidNm.split(OR));
            } else {
                ret = Arrays.asList(druidNm);
            }
            return ret;
        }
    }

    @Data
    public static class APPR {
        private String sidoCd;
        private String sidoNm;
        private String sggCd;
        private String sggNm;
        private String dongCd;
        private String dongNm;
    }

    @Data
    public static class COMM {
        private String comGrpCd;
    }

    @Data
    public static class BASIC {
        private String domain;
        private String division;
        private String vendor;
        private List<CommDto> interval;
        private List<String> orgCd;
        private List<String> addrCd;
        private List<String> emsCd;
        private String mtsTypCd;
        private String mtsoTypCd;
    }
}