package app.metatron.discovery.domain.ipm.service.lpm;

import app.metatron.discovery.domain.ipm.common.service.AbstractGenericService;
import app.metatron.discovery.domain.ipm.domain.lpm.LpmStyleMstEntity;
import app.metatron.discovery.domain.ipm.repository.lpm.LpmStyleMstRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class LpmStyleMstService extends AbstractGenericService<LpmStyleMstEntity, String> {

    @Autowired
    private LpmStyleMstRepository lpmStyleMstRepository;

    @Override
    protected JpaRepository<LpmStyleMstEntity, String> getRepository() {
        return lpmStyleMstRepository;
    }

    public List<LpmStyleMstEntity> getStyleInfo() {
        return lpmStyleMstRepository.findAll();
    }
    
    public List<LpmStyleMstEntity> getStyleFromLayrId(String layrId) {
        return lpmStyleMstRepository.findByLayrId(layrId);
    }
    
    /*
	public List<LpmStyleMstDto.Style> getStyleByLayers() {
		return lpmStyleMstRepository.getStyleByLayers();
	}
   
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