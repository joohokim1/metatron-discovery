package app.metatron.discovery.domain.ipm.domain.lpm;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "ipm_lpm_layr_grp")
public class LpmLayrGrpMstEntity {
    @Id
    @Column(name = "layr_grp_id")
    private String layrGrpId;

    @Column(name = "layr_grp_nm")
    private String layrGrpNm;    
    
    @Column(name = "sort_no")
    private int sortNo;    
    
    @Column(name = "use_yn")
    private String useYn;    
    
    @Column(name = "druid_nm")
    private String DruidNm;    

    @Column(name = "frst_reg_date")    
    private String frstRegDate;
   
    @Column(name = "last_chg_date")
    private String lastChgDate;    
    
    @Column(name = "frst_reg_user_id")
    private String frstRegUserId;
    
    @Column(name = "last_chg_user_id")
    private String lastChgUserId;

}

