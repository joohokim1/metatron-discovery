package app.metatron.discovery.domain.ipm.repository.epm;

import app.metatron.discovery.domain.ipm.domain.epm.EpmClBasEntity;
import app.metatron.discovery.domain.ipm.domain.epm.EpmDto;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EpmClBasRepository extends JpaRepository<EpmClBasEntity, String> {
    /**
     * epm 분류 기본 목록 조회
     * @return
     */
    List<EpmDto.EpmClBasList> findByClGrpUidIsNullOrderByClTurn();

    /**
     * epm 필터 기본 목록 조회
     * @param clUid
     * @return
     */
    EpmDto.EpmClBas findByClUid(String clUid);

    /**
     * epm 분류 명 조회
     * @param clUid
     * @return
     */
    EpmClBasEntity findClNmByClUid(String clUid);
}
