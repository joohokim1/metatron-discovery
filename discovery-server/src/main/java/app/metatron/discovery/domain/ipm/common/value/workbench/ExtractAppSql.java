package app.metatron.discovery.domain.ipm.common.value.workbench;

import java.io.Serializable;
import java.util.List;

/**
 * 추출앱 쿼리 객체
 */
public class ExtractAppSql implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 원본 쿼리(변수 포함)
     */
    private String original;

    /**
     * 변환 쿼리
     */
    private String processed;

    /**
     * 표현식 모듈들
     */
    private List<ExtractAppModule> modules;

    public String getOriginal() {
        return original;
    }

    public void setOriginal(String original) {
        this.original = original;
    }

    public String getProcessed() {
        return processed;
    }

    public void setProcessed(String processed) {
        this.processed = processed;
    }

    public List<ExtractAppModule> getModules() {
        return modules;
    }

    public void setModules(List<ExtractAppModule> modules) {
        this.modules = modules;
    }
}
