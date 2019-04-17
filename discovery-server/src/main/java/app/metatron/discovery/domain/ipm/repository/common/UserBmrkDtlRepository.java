package app.metatron.discovery.domain.ipm.repository.common;

import app.metatron.discovery.domain.ipm.domain.common.UserBmrkDtlEntity;
import app.metatron.discovery.domain.ipm.domain.common.UserBmrkDto;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserBmrkDtlRepository extends JpaRepository<UserBmrkDtlEntity, String> {

    /**
     * 사용자 즐겨찾기 상세 목록 조회
     * @param userId
     * @param menuLinkNm
     * @param delYn
     * @return
     */
    List<UserBmrkDto.UserBmrkDtlList> findByUserIdAndMenuLinkNmAndDelYnOrderByBmrkTurnDesc(
        String userId, String menuLinkNm, String delYn);

    /**
     * 즐겨찾기 순번 최대값 조회
     * @param userId
     * @return
     */
    @Query("select COALESCE(max(t.bmrkTurn), '0') as max from UserBmrkDtlEntity t where t.userId = :userId")
    Integer getMaxBmrkTurn(@Param("userId") String userId);

    /**
     * 사용자 즐겨찾기 상세 조회
     * @param bmrkUid
     * @param userId
     * @return
     */
    UserBmrkDtlEntity findByBmrkUidAndUserId(String bmrkUid, String userId);
}
