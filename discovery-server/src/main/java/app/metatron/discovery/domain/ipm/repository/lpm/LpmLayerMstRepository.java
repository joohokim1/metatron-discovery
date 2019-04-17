package app.metatron.discovery.domain.ipm.repository.lpm;

import app.metatron.discovery.domain.ipm.domain.lpm.LpmLayerMstEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LpmLayerMstRepository extends JpaRepository<LpmLayerMstEntity, String> {
    List<LpmLayerMstEntity> findAll();
	List<LpmLayerMstEntity> findByLayrGrpId(String layrGrpId);
}
