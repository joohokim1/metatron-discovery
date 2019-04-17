package app.metatron.discovery.domain.ipm.domain.epm;

import com.fasterxml.jackson.annotation.JsonBackReference;
import java.util.Date;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import lombok.Data;

@Data
@Entity
@Table(name = "ipm_epm_cl_bas")
public class EpmClBasEntity {

    /**
     * 분류 UID
     */
    @Id
    @Column(name = "cl_uid")
    private String clUid;

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
     * 분류 그룹 UID
     */
    @Column(name = "cl_grp_uid")
    private String clGrpUid;

    /**
     * 분류 명
     */
    @Column(name = "cl_nm")
    private String clNm;

    /**
     * 분류 순서
     */
    @Column(name = "cl_turn")
    private int clTurn;

    /**
     * 하위 분류 목록
     */
    @OneToMany
    @JoinColumn(name = "cl_grp_uid")
    @OrderBy("cl_turn")
    @JsonBackReference
    private List<EpmClBasEntity> clList;

    /**
     * 분류에 해당하는 필터 목록
     */
    @ManyToMany
    @JoinTable(
            name = "ipm_epm_cl_fltr_rel",
            joinColumns = @JoinColumn(name = "cl_uid"),
            inverseJoinColumns = @JoinColumn(name = "fltr_uid")
    )
    @OrderBy("fltr_turn")
    private List<EpmFltrBasEntity> fltrList;
}
