package app.metatron.discovery.domain.ipm.repository.lpm;

import app.metatron.discovery.domain.ipm.domain.lpm.LpmDto;
import app.metatron.discovery.domain.ipm.domain.lpm.LpmFltrBasEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LpmFltrBasRepository extends JpaRepository<LpmFltrBasEntity, String> {
    List<LpmDto.LpmFltrBasList> findByFltrGrpUidIsNullOrderByFltrTurn();
    LpmDto.LpmFltrBas findByFltrNm(String fltrNm);
}
