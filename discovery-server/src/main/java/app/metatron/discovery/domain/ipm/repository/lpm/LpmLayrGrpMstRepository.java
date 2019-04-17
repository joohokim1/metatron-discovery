package app.metatron.discovery.domain.ipm.repository.lpm;

import app.metatron.discovery.domain.ipm.domain.lpm.LpmLayrGrpMstEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LpmLayrGrpMstRepository extends JpaRepository<LpmLayrGrpMstEntity, String> {
	
    List<LpmLayrGrpMstEntity> findAll();
}
