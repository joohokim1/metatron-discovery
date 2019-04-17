package app.metatron.discovery.domain.ipm.domain.epm;

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
@Table(name = "ipm_epm_fltr_dtl")
public class EpmFltrDtlEntity {

    /**
     * 필터 상세 UID
     */
    @Id
    @Column(name = "fltr_dtl_uid")
    private String fltrDtlUid;

    /**
     * 최초 등록 일자
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "frst_reg_date")
    private Date frstRegDate;

    /**
     * 최초 등록 사용자 ID
     */
    @Column(name = "frst_reg_user_id")
    private String frstRegUserId;

    /**
     * 최종 변경 일자
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "last_chg_date")
    private Date lastChgDate;

    /**
     * 최종 변경 사용자 ID
     */
    @Column(name = "last_chg_user_id")
    private String lastChgUserId;

    /**
     * 필터 UID
     */
    @Column(name = "fltr_uid")
    private String fltrUid;

    /**
     * 필터 값
     */
    @Column(name = "fltr_val")
    private String fltrVal;

    /**
     * 필터 단위
     */
    @Column(name = "fltr_unit")
    private String fltrUnit;

    /**
     * 필터 형식 값
     */
    @Column(name = "fltr_fmt_val")
    private String fltrFmtVal;

    /**
     * 필터 비고
     */
    @Column(name = "fltr_rmk")
    private String fltrRmk;

    /**
     * 필터 분류 명
     */
    @Column(name = "fltr_cl_nm")
    private String fltrClNm;
}
