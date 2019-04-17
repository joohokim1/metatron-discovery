package app.metatron.discovery.domain.ipm.repository.icpm;

import app.metatron.discovery.domain.ipm.domain.icpm.IcpmDto;
import app.metatron.discovery.domain.ipm.domain.icpm.IcpmFltrBasEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface IcpmFltrBasRepository extends JpaRepository<IcpmFltrBasEntity, String> {
    /**
     * icpm 필터 기본 목록 조회
     * @return
     */
    List<IcpmDto.IcpmFltrBasList> findByFltrGrpUidIsNullOrderByFltrTurn();

    /**
     * icpm 필터 기본 조회
     * @param fltrUid
     * @return
     */
    IcpmDto.IcpmFltrBas findByFltrUid(String fltrUid);

    /**
     * grid 목록 조회
     * @return
     */
    @Query("select t" +
            " from IcpmFltrBasEntity t " +
            "where leafYn = 'Y' and useYn = 'Y' " +
            "  and druid_nm not in :gridWoList " +
            "order by fltrTurn")
    List<IcpmFltrBasEntity> getGridList(@Param("gridWoList") List<String> gridWoList);
}
