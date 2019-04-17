package app.metatron.discovery.domain.ipm.service.lpm;

import app.metatron.discovery.domain.ipm.common.service.AbstractGenericService;
import app.metatron.discovery.domain.ipm.domain.lpm.LpmLayerMstEntity;
import app.metatron.discovery.domain.ipm.repository.lpm.LpmLayerMstRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class LpmLayerMstService extends AbstractGenericService<LpmLayerMstEntity, String> {

    @Autowired
    private LpmLayerMstRepository lpmLayerMstRepository;

    @Override
    protected JpaRepository<LpmLayerMstEntity, String> getRepository() {
        return lpmLayerMstRepository;
    }

    public List<LpmLayerMstEntity> getLayerInfo() {
        return lpmLayerMstRepository.findAll();
    }

	public List<LpmLayerMstEntity> getLayerInfoByLayrGrpId(String layrGrpId) {
		return lpmLayerMstRepository.findByLayrGrpId(layrGrpId);
	}

    /*
    public String getLayerId(String layerNm) {
    	LpmLayerMstEntity e = lpmLayerMstRepository.findFirstByLayerNmAndUseYn(layerNm, "Y");
        return e.getLayerId();
    }
    public LpmLayerMstEntity getFieldObjById(String layerNm) {
    	LpmLayerMstEntity e = lpmLayerMstRepository.findFirstByLayerNmAndUseYn(layerNm, "Y");
        return e;
    }
    */
}