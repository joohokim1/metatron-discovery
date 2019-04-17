package app.metatron.discovery.domain.ipm.domain.common;

import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import lombok.Data;
import org.hibernate.annotations.Type;
import org.joda.time.LocalDateTime;

@Data
@Entity
@IdClass(UserEstDtlId.class)
@Table(name = "ipm_user_est_dtl")
public class UserEstDtlEntity {

    /**
     * 사용자 ID
     */
    @Id
    @Column(name = "user_id")
    private String userId;

    /**
     * 메뉴 링크 명
     */
    @Id
    @Column(name = "menu_link_nm")
    private String menuLinkNm;

    /**
     * 최초 등록 일자
     */
    @JsonIgnore
    @Column(name = "frst_reg_date")
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDateTime")
    private LocalDateTime frstRegDate;

    /**
     * 최초 등록 사용자 ID
     */
    @JsonIgnore
    @JoinColumn(name = "frst_reg_user_id")
    private String frstRegUserId;

    /**
     * 최종 변경 일자
     */
    @JsonIgnore
    @Column(name = "last_chg_date")
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDateTime")
    private LocalDateTime lastChgDate;

    /**
     * 최종 변경 사용자 ID
     */
    @JsonIgnore
    @JoinColumn(name = "last_chg_user_id")
    private String lastChgUserId;

    /**
     * 설정 값
     */
    @Column(name = "est_val")
    private String estVal;

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

