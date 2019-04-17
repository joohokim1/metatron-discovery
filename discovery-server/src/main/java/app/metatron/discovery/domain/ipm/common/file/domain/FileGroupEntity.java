package app.metatron.discovery.domain.ipm.common.file.domain;

import java.util.List;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import org.hibernate.annotations.GenericGenerator;

/**
 * 파일 그룹 Entity
 *
 * @author nogah
 */
@Entity
@Table(name = "dt_cm_file_group")
public class FileGroupEntity {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "org.hibernate.id.UUIDGenerator")
    private String id;

    /**
     * 파일 리스트
     */
    @OneToMany(mappedBy = "fileGroup", fetch = FetchType.EAGER)
    @OrderBy(value = "dispOrder ASC")
    private List<FileEntity> files;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<FileEntity> getFiles() {
        return files;
    }

    public void setFiles(List<FileEntity> files) {
        this.files = files;
    }
}
