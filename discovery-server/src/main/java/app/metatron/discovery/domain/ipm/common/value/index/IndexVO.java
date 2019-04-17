package app.metatron.discovery.domain.ipm.common.value.index;

import java.io.Serializable;
import java.util.List;

/**
 * 검색 색인용 기본 VO
 */
public class IndexVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 아이디
     */
    private String id;

    /**
     * 생성일시
     */
    private String createdDate;

    /**
     * 자동완성용 색인정보
     */
    private List<String> autocompletes;

    /**
     * 인덱스일시
     */
    private String indexDate;

    /**
     * 색인 타입
     */
    private String type;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public List<String> getAutocompletes() {
        return autocompletes;
    }

    public void setAutocompletes(List<String> autocompletes) {
        this.autocompletes = autocompletes;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getIndexDate() {
        return indexDate;
    }

    public void setIndexDate(String indexDate) {
        this.indexDate = indexDate;
    }
}
