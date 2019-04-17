package app.metatron.discovery.domain.ipm.common.value.workbench;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 쿼리 결과 객체
 */
public class QueryResult implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 데이터 헤더 (컬럼 헤더)
     */
    private Set<String> headList;

    /**
     * 조회 데이터
     */
    private List<Map<String, Object>> resultList;

    public Set<String> getHeadList() {
        if( this.headList == null ) {
            return new HashSet<>();
        }
        return this.headList;
    }

    public void setHeadList(Set<String> headList) {
        this.headList = headList;
    }

    public List<Map<String, Object>> getResultList() {
        if( this.resultList == null ) {
            return new ArrayList<>();
        }
        return this.resultList;
    }

    public void setResultList(List<Map<String, Object>> resultList) {
        this.resultList = resultList;
    }
}
