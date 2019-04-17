package app.metatron.discovery.domain.ipm.domain.icpm;

import com.fasterxml.jackson.annotation.JsonBackReference;
import java.util.Date;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import lombok.Data;

@Data
@Entity
@Table(name = "ipm_icpm_fltr_bas")
public class IcpmFltrBasEntity {

    /**
     * 필터 UID
     */
    @Id
    @Column(name = "fltr_uid")
    private String fltrUid;

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
     * 필터 그룹 UID
     */
    @Column(name = "fltr_grp_uid")
    private String fltrGrpUid;

    /**
     * 필터 명
     */
    @Column(name = "fltr_nm")
    private String fltrNm;

    /**
     * 드루이드 명
     */
    @Column(name = "druid_nm")
    private String druidNm;

    /**
     * 화면 명
     */
    @Column(name = "scrn_nm")
    private String scrnNm;

    /**
     * 화면 분류 명
     */
    @Column(name = "scrn_cl_nm")
    private String scrnClNm;

    /**
     * 필터 설명
     */
    @Column(name = "fltr_desc")
    private String fltrDesc;

    /**
     * 필터 순서
     */
    @Column(name = "fltr_turn")
    private int fltrTurn;

    /**
     * leaf 여부
     */
    @Column(name = "leaf_yn")
    private String leafYn;

    /**
     * 사용 여부
     */
    @Column(name = "use_yn")
    private String useYn;

    /**
     * 하위 필터 목록
     */
    @OneToMany
    @JoinColumn(name = "fltr_grp_uid")
    @JsonBackReference
    @OrderBy("fltr_turn")
    private List<IcpmFltrBasEntity> fltrList;

    /**
     * 필터 상세 목록
     */
    @OneToMany
    @JoinColumn(name = "fltr_uid")
    @OrderBy("fltr_dtl_uid")
    private List<IcpmFltrDtlEntity> fltrDtlList;
}
