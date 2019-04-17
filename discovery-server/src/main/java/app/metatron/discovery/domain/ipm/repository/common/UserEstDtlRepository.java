package app.metatron.discovery.domain.ipm.repository.common;

import app.metatron.discovery.domain.ipm.domain.common.UserEstDtlEntity;
import app.metatron.discovery.domain.ipm.domain.common.UserEstDtlId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserEstDtlRepository extends JpaRepository<UserEstDtlEntity, UserEstDtlId> {

    /**
     * 사용자 설정 상세 조회
     * @param userId
     * @param menuLinkNm
     * @return
     */
    UserEstDtlEntity findByUserIdAndMenuLinkNm(String userId, String menuLinkNm);
}
