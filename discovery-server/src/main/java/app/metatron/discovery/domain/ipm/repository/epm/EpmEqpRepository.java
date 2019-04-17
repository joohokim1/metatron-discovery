package app.metatron.discovery.domain.ipm.repository.epm;

import app.metatron.discovery.domain.ipm.domain.epm.EpmEqpEntity;
import java.util.List;
import java.util.Map;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EpmEqpRepository extends JpaRepository<EpmEqpEntity, String> {

    /**
     * SKT 본부 목록 조회
     * @param vendorCd
     * @return
     */
    @Query("select t.sktOperHdofcOrgId as code," +
            "      t.sktOperHdofcOrgNm as name" +
            " from EpmEqpEntity t " +
            "where t.sktOperHdofcOrgId is not null " +
            "  and t.sktOperHdofcOrgId <> 'null'" +
            "  and t.sktOperHdofcOrgNm is not null" +
            "  and t.sktOperHdofcOrgNm <> 'null'" +
            "  and (:vendorCd is null or t.vendCd = :vendorCd) " +
            "group by t.sktOperHdofcOrgId")
    List<Map<String, Object>> getSktHdofcList(@Param("vendorCd") String vendorCd);

    /**
     * SKT 팀 목록 조회
     * @param vendorCd
     * @param sktOperHdofcOrgId
     * @return
     */
    @Query("select t.sktOperTeamOrgId as code," +
            "      t.sktOperTeamOrgNm as name" +
            " from EpmEqpEntity t " +
            "where t.sktOperTeamOrgId is not null" +
            "  and t.sktOperTeamOrgId <> 'null'" +
            "  and t.sktOperTeamOrgNm is not null" +
            "  and t.sktOperTeamOrgNm <> 'null'" +
            "  and (:vendorCd is null or t.vendCd = :vendorCd) " +
            "  and t.sktOperHdofcOrgId = :sktOperHdofcOrgId " +
            "group by t.sktOperTeamOrgId")
    List<Map<String, Object>> getSktTeamList(@Param("vendorCd") String vendorCd,
        @Param("sktOperHdofcOrgId") String sktOperHdofcOrgId);

    /**
     * SKT 세부 팀 목록 조회
     * @param vendorCd
     * @param sktOperHdofcOrgId
     * @param sktOperTeamOrgId
     * @return
     */
    @Query("select t.nwonsTeamCd as code," +
            "      t.nwonsTeamNm as name" +
            " from EpmEqpEntity t " +
            "where t.nwonsTeamCd is not null" +
            "  and t.nwonsTeamCd <> 'null'" +
            "  and t.nwonsTeamNm is not null" +
            "  and t.nwonsTeamNm <> 'null'" +
            "  and (:vendorCd is null or t.vendCd = :vendorCd) " +
            "  and t.sktOperHdofcOrgId = :sktOperHdofcOrgId" +
            "  and t.sktOperTeamOrgId = :sktOperTeamOrgId " +
            "group by t.nwonsTeamCd")
    List<Map<String, Object>> getSktDetailTeamList(@Param("vendorCd") String vendorCd,
        @Param("sktOperHdofcOrgId") String sktOperHdofcOrgId,
        @Param("sktOperTeamOrgId") String sktOperTeamOrgId);

    /**
     * ONS 본부 목록 조회
     * @param vendorCd
     * @return
     */
    @Query("select t.nwonsHdofcCd as code," +
            "      t.nwonsHdofcNm as name" +
            " from EpmEqpEntity t " +
            "where t.nwonsHdofcCd is not null " +
            "  and t.nwonsHdofcCd <> 'null'" +
            "  and t.nwonsHdofcNm is not null" +
            "  and t.nwonsHdofcNm <> 'null'" +
            "  and (:vendorCd is null or t.vendCd = :vendorCd) " +
            "group by t.nwonsHdofcCd")
    List<Map<String, Object>> getOnsHdofcList(@Param("vendorCd") String vendorCd);

    /**
     * ONS 팀 목록 조회
     * @param vendorCd
     * @param nwonsHdofcCd
     * @return
     */
    @Query("select t.nwonsTeamCd as code," +
            "      t.nwonsTeamNm as name" +
            " from EpmEqpEntity t " +
            "where t.nwonsTeamCd is not null " +
            "  and t.nwonsTeamCd <> 'null'" +
            "  and t.nwonsTeamNm is not null" +
            "  and t.nwonsTeamNm <> 'null'" +
            "  and (:vendorCd is null or t.vendCd = :vendorCd) " +
            "  and t.nwonsHdofcCd = :nwonsHdofcCd " +
            "group by t.nwonsTeamCd")
    List<Map<String, Object>> getOnsTeamList(@Param("vendorCd") String vendorCd,
        @Param("nwonsHdofcCd") String nwonsHdofcCd);

    /**
     * 시도 목록 조회
     * @param vendorCd
     * @return
     */
    @Query("select t.sidoCd as code," +
            "      t.sidoNm as name" +
            " from EpmEqpEntity t " +
            "where t.sidoCd is not null " +
            "  and t.sidoCd <> 'null'" +
            "  and t.sidoNm is not null" +
            "  and t.sidoNm <> 'null'" +
            "  and (:vendorCd is null or t.vendCd = :vendorCd) " +
            "group by t.sidoCd")
    List<Map<String, Object>> getSidoList(@Param("vendorCd") String vendorCd);

    /**
     * 시군구 목록 조회
     * @param vendorCd
     * @param sidoCd
     * @return
     */
    @Query("select t.sggCd as code," +
            "      t.sggNm as name" +
            " from EpmEqpEntity t " +
            "where t.sggCd is not null " +
            "  and t.sggCd <> 'null'" +
            "  and t.sggNm is not null" +
            "  and t.sggNm <> 'null'" +
            "  and (:vendorCd is null or t.vendCd = :vendorCd) " +
            "  and t.sidoCd = :sidoCd " +
            "group by t.sggCd")
    List<Map<String, Object>> getSggList(@Param("vendorCd") String vendorCd,
        @Param("sidoCd") String sidoCd);

    /**
     * 읍면동 목록 조회
     * @param vendorCd
     * @param sggCd
     * @return
     */
    @Query("select t.emdCd as code," +
            "      t.emdNm as name" +
            " from EpmEqpEntity t " +
            "where t.emdCd is not null " +
            "  and t.emdCd <> 'null'" +
            "  and t.emdNm is not null" +
            "  and t.emdNm <> 'null'" +
            "  and (:vendorCd is null or t.vendCd = :vendorCd) " +
            "  and t.sggCd = :sggCd " +
            "group by t.emdCd")
    List<Map<String, Object>> getEmdList(@Param("vendorCd") String vendorCd,
        @Param("sggCd") String sggCd);

    /**
     * EMS 목록 조회
     * @param vendorCd
     * @return
     */
    @Query("select t.emsEqpId as code," +
            "      t.emsEqpNm as name" +
            " from EpmEqpEntity t " +
            "where t.emsEqpId is not null " +
            "  and t.emsEqpId <> 'null'" +
            "  and t.emsEqpNm is not null" +
            "  and t.emsEqpNm <> 'null'" +
            "  and (:vendorCd is null or t.vendCd = :vendorCd) " +
            "group by t.emsEqpId")
    List<Map<String, Object>> getEmsList(@Param("vendorCd") String vendorCd);

    /**
     * SKT eNB 목록 조회
     * @param vendorCd
     * @param sktOperHdofcOrgId
     * @param sktOperTeamOrgId
     * @param nwonsTeamCd
     * @return
     */
    @Query("select t.enbId as code," +
            "      t.eqpNm as name" +
            " from EpmEqpEntity t " +
            "where t.enbId is not null" +
            "  and t.enbId <> 'null'" +
            "  and t.eqpNm is not null" +
            "  and t.eqpNm <> 'null'" +
            "  and (:vendorCd is null or t.vendCd = :vendorCd) " +
            "  and t.sktOperHdofcOrgId = :sktOperHdofcOrgId" +
            "  and (:sktOperTeamOrgId is null or t.sktOperTeamOrgId = :sktOperTeamOrgId) " +
            "  and (:nwonsTeamCd is null or t.nwonsTeamCd = :nwonsTeamCd) " +
            "order by t.enbId")
    List<Map<String, Object>> getSktEnbList(@Param("vendorCd") String vendorCd,
        @Param("sktOperHdofcOrgId") String sktOperHdofcOrgId,
        @Param("sktOperTeamOrgId") String sktOperTeamOrgId,
        @Param("nwonsTeamCd") String nwonsTeamCd);

    /**
     * ONS eNB 목록 조회
     * @param vendorCd
     * @param nwonsHdofcCd
     * @param nwonsTeamCd
     * @return
     */
    @Query("select t.enbId as code," +
            "      t.eqpNm as name" +
            " from EpmEqpEntity t " +
            "where t.enbId is not null" +
            "  and t.enbId <> 'null'" +
            "  and t.eqpNm is not null" +
            "  and t.eqpNm <> 'null'" +
            "  and (:vendorCd is null or t.vendCd = :vendorCd) " +
            "  and t.nwonsHdofcCd = :nwonsHdofcCd" +
            "  and (:nwonsTeamCd is null or t.nwonsTeamCd = :nwonsTeamCd) " +
            "order by t.enbId")
    List<Map<String, Object>> getOnsEnbList(@Param("vendorCd") String vendorCd,
        @Param("nwonsHdofcCd") String nwonsHdofcCd,
        @Param("nwonsTeamCd") String nwonsTeamCd);

    /**
     * 시도별 eNB 목록 조회
     * @param vendorCd
     * @param sidoCd
     * @return
     */
    @Query("select t.enbId as code," +
            "      t.eqpNm as name" +
            " from EpmEqpEntity t " +
            "where t.enbId is not null" +
            "  and t.enbId <> 'null'" +
            "  and t.eqpNm is not null" +
            "  and t.eqpNm <> 'null'" +
            "  and (:vendorCd is null or t.vendCd = :vendorCd) " +
            "  and t.sidoCd = :sidoCd " +
            "order by t.enbId")
    List<Map<String, Object>> getSidoEnbList(@Param("vendorCd") String vendorCd,
        @Param("sidoCd") String sidoCd);

    /**
     * 시군구별 eNB 목록 조회
     * @param vendorCd
     * @param sggCd
     * @return
     */
    @Query("select t.enbId as code," +
            "      t.eqpNm as name" +
            " from EpmEqpEntity t " +
            "where t.enbId is not null" +
            "  and t.enbId <> 'null'" +
            "  and t.eqpNm is not null" +
            "  and t.eqpNm <> 'null'" +
            "  and (:vendorCd is null or t.vendCd = :vendorCd) " +
            "  and t.sggCd = :sggCd " +
            "order by t.enbId")
    List<Map<String, Object>> getSggEnbList(@Param("vendorCd") String vendorCd,
        @Param("sggCd") String sggCd);

    /**
     * 읍면동별 eNB 목록 조회
     * @param vendorCd
     * @param emdCd
     * @return
     */
    @Query("select t.enbId as code," +
            "      t.eqpNm as name" +
            " from EpmEqpEntity t " +
            "where t.enbId is not null" +
            "  and t.enbId <> 'null'" +
            "  and t.eqpNm is not null" +
            "  and t.eqpNm <> 'null'" +
            "  and (:vendorCd is null or t.vendCd = :vendorCd) " +
            "  and t.emdCd = :emdCd " +
            "order by t.enbId")
    List<Map<String, Object>> getEmdEnbList(@Param("vendorCd") String vendorCd,
        @Param("emdCd") String emdCd);

    /**
     * EMS별 eNB 목록 조회
     * @param vendorCd
     * @param emsEqpId
     * @return
     */
    @Query("select t.enbId as code," +
            "      t.eqpNm as name" +
            " from EpmEqpEntity t " +
            "where t.enbId is not null" +
            "  and t.enbId <> 'null'" +
            "  and t.eqpNm is not null" +
            "  and t.eqpNm <> 'null'" +
            "  and (:vendorCd is null or t.vendCd = :vendorCd) " +
            "  and emsEqpId = :emsEqpId " +
            "order by t.enbId")
    List<Map<String, Object>> getEmsEnbList(@Param("vendorCd") String vendorCd,
        @Param("emsEqpId") String emsEqpId);

    /**
     * SKT 국사 목록 조회
     * @param vendorCd
     * @param sktOperHdofcOrgId
     * @param sktOperTeamOrgId
     * @param nwonsTeamCd
     * @return
     */
    @Query("select t.mtsoId as code," +
            "      t.mtsoNm as name" +
            " from EpmEqpEntity t " +
            "where t.mtsoId is not null" +
            "  and t.mtsoId <> 'null'" +
            "  and t.mtsoNm is not null" +
            "  and t.mtsoNm <> 'null'" +
            "  and (:vendorCd is null or t.vendCd = :vendorCd) " +
            "  and t.sktOperHdofcOrgId = :sktOperHdofcOrgId" +
            "  and (:sktOperTeamOrgId is null or t.sktOperTeamOrgId = :sktOperTeamOrgId) " +
            "  and (:nwonsTeamCd is null or t.nwonsTeamCd = :nwonsTeamCd) " +
            "group by t.mtsoId")
    List<Map<String, Object>> getSktMtsoList(@Param("vendorCd") String vendorCd,
        @Param("sktOperHdofcOrgId") String sktOperHdofcOrgId,
        @Param("sktOperTeamOrgId") String sktOperTeamOrgId,
        @Param("nwonsTeamCd") String nwonsTeamCd);

    /**
     * ONS 국사 목록 조회
     * @param vendorCd
     * @param nwonsHdofcCd
     * @param nwonsTeamCd
     * @return
     */
    @Query("select t.mtsoId as code," +
            "      t.mtsoNm as name" +
            " from EpmEqpEntity t " +
            "where t.mtsoId is not null" +
            "  and t.mtsoId <> 'null'" +
            "  and t.mtsoNm is not null" +
            "  and t.mtsoNm <> 'null'" +
            "  and (:vendorCd is null or t.vendCd = :vendorCd) " +
            "  and t.nwonsHdofcCd = :nwonsHdofcCd" +
            "  and (:nwonsTeamCd is null or t.nwonsTeamCd = :nwonsTeamCd) " +
            "group by t.mtsoId")
    List<Map<String, Object>> getOnsMtsoList(@Param("vendorCd") String vendorCd,
        @Param("nwonsHdofcCd") String nwonsHdofcCd,
        @Param("nwonsTeamCd") String nwonsTeamCd);

    /**
     * 시도별 국사 목록 조회
     * @param vendorCd
     * @param sidoCd
     * @return
     */
    @Query("select t.mtsoId as code," +
            "      t.mtsoNm as name" +
            " from EpmEqpEntity t " +
            "where t.mtsoId is not null" +
            "  and t.mtsoId <> 'null'" +
            "  and t.mtsoNm is not null" +
            "  and t.mtsoNm <> 'null'" +
            "  and (:vendorCd is null or t.vendCd = :vendorCd) " +
            "  and t.sidoCd = :sidoCd " +
            "group by t.mtsoId")
    List<Map<String, Object>> getSidoMtsoList(@Param("vendorCd") String vendorCd,
        @Param("sidoCd") String sidoCd);

    /**
     * 시군구별 국사 목록 조회
     * @param vendorCd
     * @param sggCd
     * @return
     */
    @Query("select t.mtsoId as code," +
            "      t.mtsoNm as name" +
            " from EpmEqpEntity t " +
            "where t.mtsoId is not null" +
            "  and t.mtsoId <> 'null'" +
            "  and t.mtsoNm is not null" +
            "  and t.mtsoNm <> 'null'" +
            "  and (:vendorCd is null or t.vendCd = :vendorCd) " +
            "  and  t.sggCd = :sggCd " +
            "group by t.mtsoId")
    List<Map<String, Object>> getSggMtsoList(@Param("vendorCd") String vendorCd,
        @Param("sggCd") String sggCd);

    /**
     * 읍면동별 국사 목록 조회
     * @param vendorCd
     * @param emdCd
     * @return
     */
    @Query("select t.mtsoId as code," +
            "      t.mtsoNm as name" +
            " from EpmEqpEntity t " +
            "where t.mtsoId is not null" +
            "  and t.mtsoId <> 'null'" +
            "  and t.mtsoNm is not null" +
            "  and t.mtsoNm <> 'null'" +
            "  and (:vendorCd is null or t.vendCd = :vendorCd) " +
            "  and t.emdCd = :emdCd " +
            "group by t.mtsoId")
    List<Map<String, Object>> getEmdMtsoList(@Param("vendorCd") String vendorCd,
        @Param("emdCd") String emdCd);

    /**
     * 유형별 국사 목록 조회
     * @param vendorCd
     * @param mtsoTypCd
     * @return
     */
    @Query("select t.mtsoId as code," +
            "      t.mtsoNm as name" +
            " from EpmEqpEntity t " +
            "where t.mtsoId is not null" +
            "  and t.mtsoId <> 'null'" +
            "  and t.mtsoNm is not null" +
            "  and t.mtsoNm <> 'null'" +
            "  and (:vendorCd is null or t.vendCd = :vendorCd) " +
            "  and (:mtsoTypCd is null or t.mtsoTypCd = :mtsoTypCd) " +
            "group by t.mtsoId")
    List<Map<String, Object>> getTypMtsoList(@Param("vendorCd") String vendorCd,
        @Param("mtsoTypCd") String mtsoTypCd);
}
