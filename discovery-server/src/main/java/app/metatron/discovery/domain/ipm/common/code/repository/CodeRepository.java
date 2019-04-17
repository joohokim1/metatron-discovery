package app.metatron.discovery.domain.ipm.common.code.repository;

import app.metatron.discovery.domain.ipm.common.code.domain.CodeEntity;
import app.metatron.discovery.domain.ipm.common.code.domain.GroupCodeEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CodeRepository extends JpaRepository<CodeEntity, String> {
	
	/**
	 * 그룹코드를 기준으로 코드 단건 조회
	 * @return
	 */
	CodeEntity findByGroupCd(GroupCodeEntity groupCode);
	
	/**
	 * 그룹코드를 기준으로 코드 리스트 조회
	 * @return
	 */
	List<CodeEntity> findByGroupCdOrderByCdOrderAsc(GroupCodeEntity groupCode);
	
}
