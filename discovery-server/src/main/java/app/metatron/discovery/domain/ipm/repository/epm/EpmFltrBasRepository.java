package app.metatron.discovery.domain.ipm.repository.epm;

import app.metatron.discovery.domain.ipm.domain.epm.EpmDto;
import app.metatron.discovery.domain.ipm.domain.epm.EpmFltrBasEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EpmFltrBasRepository extends JpaRepository<EpmFltrBasEntity, String> {

    /**
     * 필터 상세 조회
     * @param fltrUid
     * @return
     */
    EpmDto.EpmFltrBas findByFltrUid(String fltrUid);

    /**
     * grid 목록 조회
     * @param vendor
     * @return
     */
    @Query("select t" +
            " from EpmFltrBasEntity t " +
            "where fltrNm like concat('%', :vendor, '%') " +
            "order by fltrNm, fltrTurn")
    List<EpmFltrBasEntity> getGridList(@Param("vendor") String vendor);
}
