package app.metatron.discovery.domain.ipm.domain.epm;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "evt_eqp_inf")
public class EpmEqpEntity {

    /**
     * 장비ID
     */
    @Id
    @Column(name = "eqp_id")
    private String eqpId;

    /**
     * eNB ID
     */
    @Column(name = "enb_id")
    private String enbId;

    /**
     * 장비 명
     */
    @Column(name = "eqp_nm")
    private String eqpNm;

    /**
     * vendor 코드
     */
    @Column(name = "vend_cd")
    private String vendCd;

    /**
     * vendor 명
     */
    @Column(name = "vend_nm")
    private String vendNm;

    /**
     * skt 본부 ID
     */
    @Column(name = "skt_oper_hdofc_org_id")
    private String sktOperHdofcOrgId;

    /**
     * skt 본부 명
     */

    @Column(name = "skt_oper_hdofc_org_nm")
    private String sktOperHdofcOrgNm;

    /**
     * skt 팀 ID
     */
    @Column(name = "skt_oper_team_org_id")
    private String sktOperTeamOrgId;

    /**
     * skt 팀 명
     */
    @Column(name = "skt_oper_team_org_nm")
    private String sktOperTeamOrgNm;

    /**
     * ons 본부 코드
     */
    @Column(name = "nwons_hdofc_cd")
    private String nwonsHdofcCd;

    /**
     * ons 본부 명
     */
    @Column(name = "nwons_hdofc_nm")
    private String nwonsHdofcNm;

    /**
     * ons 팀 코드
     */
    @Column(name = "nwons_team_cd")
    private String nwonsTeamCd;

    /**
     * ons 팀 명
     */
    @Column(name = "nwons_team_nm")
    private String nwonsTeamNm;

    /**
     * 시도 코드
     */
    @Column(name = "sido_cd")
    private String sidoCd;

    /**
     * 시도 명
     */
    @Column(name = "sido_nm")
    private String sidoNm;

    /**
     * 시군구 코드
     */
    @Column(name = "sgg_cd")
    private String sggCd;

    /**
     * 시군구 명
     */
    @Column(name = "sgg_nm")
    private String sggNm;

    /**
     * 읍면동 코드
     */
    @Column(name = "emd_cd")
    private String emdCd;

    /**
     * 읍면동 명
     */
    @Column(name = "emd_nm")
    private String emdNm;

    /**
     * 국사 ID
     */
    @Column(name = "mtso_id")
    private String mtsoId;

    /**
     * 국사 명
     */
    @Column(name = "mtso_nm")
    private String mtsoNm;

    /**
     * 국사 타입 코드
     */
    @Column(name = "mtso_typ_cd")
    private String mtsoTypCd;

    /**
     * EMS 장비 ID
     */
    @Column(name = "ems_eqp_id")
    private String emsEqpId;

    /**
     * EMS 장비 명
     */
    @Column(name = "ems_eqp_nm")
    private String emsEqpNm;
}
