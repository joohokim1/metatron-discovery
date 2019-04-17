package app.metatron.discovery.domain.ipm.common.code.repository;

import app.metatron.discovery.domain.ipm.common.code.domain.GroupCodeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupCodeRepository extends JpaRepository<GroupCodeEntity, String> ,GroupCodeRepositoryCustom{

	
}
