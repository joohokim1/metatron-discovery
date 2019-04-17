package app.metatron.discovery.domain.ipm.common.file.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import org.hibernate.annotations.GenericGenerator;

/**
 * 파일 첨부 Entity
 *
 * @author nogah
 */
@Entity
@Table(name = "dt_cm_file")
public class FileEntity {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "org.hibernate.id.UUIDGenerator")
    private String id;

    /**
     * 파일 오리지날 이름
     */
    @Column
    private String originalNm;

    /**
     * 파일 확장자
     */
    @Column
    private String originalExt;

    /**
     * 파일 저장 이름
     */
    @JsonIgnore
    @Column
    private String savedNm;

    /**
     * 파일 저장경로
     */
    @JsonIgnore
    @Column
    private String savedPath;

    /**
     * 파일 사이즈
     */
    @Column
    private Long size;

    /**
     * 마임 타입
     */
    @JsonIgnore
    @Column
    private String contentType;

    /**
     * 첨부 파일 순서
     */
    @Column
    private int dispOrder;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "file_group_id")
    private FileGroupEntity fileGroup;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOriginalNm() {
        return originalNm;
    }

    public void setOriginalNm(String originalNm) {
        this.originalNm = originalNm;
    }

    public String getOriginalExt() {
        return originalExt;
    }

    public void setOriginalExt(String originalExt) {
        this.originalExt = originalExt;
    }

    public String getSavedNm() {
        return savedNm;
    }

    public void setSavedNm(String savedNm) {
        this.savedNm = savedNm;
    }

    public String getSavedPath() {
        return savedPath;
    }

    public void setSavedPath(String savedPath) {
        this.savedPath = savedPath;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public int getDispOrder() {
        return dispOrder;
    }

    public void setDispOrder(int dispOrder) {
        this.dispOrder = dispOrder;
    }

    public FileGroupEntity getFileGroup() {
        return fileGroup;
    }

    public void setFileGroup(FileGroupEntity fileGroup) {
        this.fileGroup = fileGroup;
    }
}
