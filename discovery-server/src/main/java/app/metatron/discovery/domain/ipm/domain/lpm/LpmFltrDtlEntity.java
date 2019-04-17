package app.metatron.discovery.domain.ipm.domain.lpm;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import lombok.Data;

@Data
@Entity
@Table(name="ipm_lpm_fltr_dtl")
public class LpmFltrDtlEntity {

    @Id
    @Column(name = "fltr_dtl_uid")
    private String fltrDtlUid;

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

    @Column(name = "fltr_uid")
    private String fltrUid;

    @Column(name = "fltr_val")
    private String fltrVal;

    @Column(name = "fltr_unit")
    private String fltrUnit;

    @Column(name = "fltr_fmt_val")
    private String fltrFmtVal;

    @Column(name = "fltr_rmk")
    private String fltrRmk;
}
