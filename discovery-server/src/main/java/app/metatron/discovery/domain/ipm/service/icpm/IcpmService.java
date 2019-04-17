package app.metatron.discovery.domain.ipm.service.icpm;

import app.metatron.discovery.domain.ipm.domain.icpm.IcpmDto;
import app.metatron.discovery.domain.ipm.repository.icpm.IcpmFltrBasRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class IcpmService {

    @Autowired
    private IcpmFltrBasRepository icpmFltrBasRepository;

    /**
     * icpm 필터 기본 목록 조회
     * @return
     */
    public List<IcpmDto.IcpmFltrBasList> getIcpmFltrBasList() {
        try {
            return icpmFltrBasRepository.findByFltrGrpUidIsNullOrderByFltrTurn();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * icpm 필터 기본 조회
     * @param fltrUid
     * @return
     */
    public IcpmDto.IcpmFltrBas getIcpmFltrBas(String fltrUid) {
        try {
            return icpmFltrBasRepository.findByFltrUid(fltrUid);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
