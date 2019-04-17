package app.metatron.discovery.domain.ipm.domain.lpm;

import com.fasterxml.jackson.annotation.JsonBackReference;
import java.util.Date;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import lombok.Data;

@Data
@Entity
@Table(name="ipm_lpm_fltr_bas")
public class LpmFltrBasEntity {

    @Id
    @Column(name = "fltr_uid")
    private String fltrUid;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "frst_reg_date")
    private Date frstRegDate;

    @Column(name = "frst_reg_user_id")
    private String frstRegUserId;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "last_chg_date")
    private Date lastChgDate;

    @Column(name = "last_chg_user_id")
    private String lastChgUserId;

    @Column(name = "fltr_grp_uid")
    private String fltrGrpUid;

    @Column(name = "fltr_nm")
    private String fltrNm;

    @Column(name = "druid_nm")
    private String druidNm;
    
    @Column(name = "mttr_nm")
    private String metatronNm;

    @Column(name = "scrn_nm")
    private String scrnNm;

    @Column(name = "scrn_cl_nm")
    private String scrnClNm;

    @Column(name = "fltr_desc")
    private String fltrDesc;

    @Column(name = "fltr_turn")
    private int fltrTurn;

    @Column(name = "leaf_yn")
    private String leafYn;

    @Column(name = "use_yn")
    private String useYn;
    
    @Column(name = "layr_grp_id")
    private String layrGrpId;
    
    @Column(name = "fltr_del_yn")
    private String FltrDelYn;
    
    @Column(name = "sub_use_yn")
    private String subUseYn;

    
    @OneToMany
    @JoinColumn(name = "fltr_grp_uid")
    @JsonBackReference
    private List<LpmFltrBasEntity> fltrList;

    @OneToMany
    @JoinColumn(name = "fltr_uid")
    private List<LpmFltrDtlEntity> fltrDtlList;
}
