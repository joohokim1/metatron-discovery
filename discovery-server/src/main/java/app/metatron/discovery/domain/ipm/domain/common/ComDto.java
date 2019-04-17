package app.metatron.discovery.domain.ipm.domain.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;
import lombok.Data;

public class ComDto {

    /**
     * 공통 DTO : 차트
     * @param <T>
     */
    @Data
    public static class Chart<T> {
        List<T> target;
        List<T> all;
        List<T> sub;
        List<String> occrDt;

        public Chart() {}
        public Chart(List target, List all) {
            this.target = target;
            this.all = all;
        }
        public Chart(List target, List all, List sub) {
            this.target = target;
            this.all = all;
            this.sub = sub;
        }
        public Chart(List target, List all, List sub, List<String> occrDt) {
            this.target = target;
            this.all = all;
            this.sub = sub;
            this.occrDt = occrDt;
        }
    }

    /**
     * 공통 DTO : 코드
     */
    @Data
    public static class Code {
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private Object code;

        @JsonInclude(JsonInclude.Include.NON_NULL)
        private Object name;

        @JsonInclude(JsonInclude.Include.NON_NULL)
        private Object value;

        @JsonInclude(JsonInclude.Include.NON_NULL)
        private String leaf;


        public Code() {
        }

        public Code(Object code, Object name) {
            this.code = code;
            this.name = name;
        }

        public Code(Object code, Object name, Object value) {
            this.code = code;
            this.name = name;
            this.value = value;
        }

        public Code(Object code, Object name, Object value, String leaf) {
            this.code = code;
            this.name = name;
            this.value = value;
            this.leaf = leaf;
        }
    }
}
