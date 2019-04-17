package app.metatron.discovery.domain.ipm.common.code.service;

import app.metatron.discovery.domain.ipm.common.code.domain.GroupCodeEntity;
import app.metatron.discovery.domain.ipm.common.code.repository.GroupCodeRepository;
import app.metatron.discovery.domain.ipm.common.service.AbstractGenericService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class GroupCodeService extends AbstractGenericService<GroupCodeEntity, String>{

    @Autowired
    private GroupCodeRepository groupCodeRepository;
    
	@Override
	protected JpaRepository<GroupCodeEntity, String> getRepository() {
		return groupCodeRepository;
	}
}