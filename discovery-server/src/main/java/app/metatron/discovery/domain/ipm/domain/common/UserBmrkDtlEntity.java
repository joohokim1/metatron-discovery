package app.metatron.discovery.domain.ipm.domain.common;

import java.util.List;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.joda.time.LocalDateTime;

@Data
@Entity
@Table(name = "ipm_user_bmrk_dtl")
public class UserBmrkDtlEntity {

    /**
     * 즐겨찾기 UID
     */
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "bmrk_uid")
    private String bmrkUid;

    /**
     * 최초 등록 일자
     */
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDateTime")
    @Column(name = "frst_reg_date")
    private LocalDateTime frstRegDate;

    /**
     * 최초 등록 사용자 ID
     */
    @Column(name = "frst_reg_user_id")
    private String frstRegUserId;

    /**
     * 최종 변경 일자
     */
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDateTime")
    @Column(name = "last_chg_date")
    private LocalDateTime lastChgDate;

    /**
     * 최종 변경 사용자 ID
     */
    @Column(name = "last_chg_user_id")
    private String lastChgUserId;

    /**
     * 사용자 ID
     */
    @Column(name = "user_id")
    private String userId;

    /**
     * 메뉴 링크 명
     */
    @Column(name = "menu_link_nm")
    private String menuLinkNm;

    /**
     * 즐겨찾기 명
     */
    @Column(name = "bmrk_nm")
    private String bmrkNm;

    /**
     * 필터 데이터 값
     */
    @Column(name = "fltr_dat_val")
    @Convert(converter = FltrDatValConvert.class)
    private List<Object> fltrDatVal;

    /**
     * 즐겨찾기 순서
     */
    @Column(name = "bmrk_turn")
    private int bmrkTurn;

    /**
     * 삭제 여부
     */
    @Column(name = "del_yn")
    private String delYn;

    /**
     * persist 선행 method
     */
    @PrePersist
    public void prePersist() {
        this.frstRegDate = LocalDateTime.now();
        this.lastChgDate = this.frstRegDate;
        if (this.frstRegUserId == null) {
            this.frstRegUserId = this.userId;
        }
        if (this.lastChgUserId == null) {
            this.lastChgUserId = this.userId;
        }
    }

    /**
     * update 선행 method
     */
    @PreUpdate
    public void preUpdate() {
        lastChgDate = LocalDateTime.now();
        if (this.lastChgUserId == null) {
            this.lastChgUserId = this.userId;
        }
    }
}
