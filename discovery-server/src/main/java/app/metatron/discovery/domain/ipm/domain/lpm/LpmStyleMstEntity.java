package app.metatron.discovery.domain.ipm.domain.lpm;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "ipm_lpm_layr_sty")
public class LpmStyleMstEntity {
    @Id
    @Column(name = "sty_id")
    private String styId;

    @Column(name = "layr_id")
    private String layrId;    
    
    @Column(name = "sty_ds")
    private String styDs;        
 
    /*
    @OneToMany
    @JoinColumn(name = "field_group_id")
    @JsonBackReference
    private List<LpmStyleMstEntity> styleInfo;
    */
    
}
