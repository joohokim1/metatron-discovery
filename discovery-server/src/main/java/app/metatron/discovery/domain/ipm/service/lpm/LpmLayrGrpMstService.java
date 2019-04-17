package app.metatron.discovery.domain.ipm.service.lpm;

import app.metatron.discovery.domain.ipm.common.service.AbstractGenericService;
import app.metatron.discovery.domain.ipm.domain.lpm.LpmLayrGrpMstEntity;
import app.metatron.discovery.domain.ipm.repository.lpm.LpmLayrGrpMstRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class LpmLayrGrpMstService extends AbstractGenericService<LpmLayrGrpMstEntity, String> {
 
	@Autowired
    private LpmLayrGrpMstRepository lpmLayrGrpMstRepository;
	
	@Override
	protected JpaRepository<LpmLayrGrpMstEntity, String> getRepository() {
		return lpmLayrGrpMstRepository;
	}

	public List<LpmLayrGrpMstEntity> getLpmLayrGrpList() {		
		return lpmLayrGrpMstRepository.findAll();
	}
	
}