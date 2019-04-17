package app.metatron.discovery.domain.ipm.service.common;

import app.metatron.discovery.domain.ipm.domain.common.UserBmrkDtlEntity;
import app.metatron.discovery.domain.ipm.domain.common.UserBmrkDto;
import app.metatron.discovery.domain.ipm.repository.common.UserBmrkDtlRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserBmrkService {

    @Autowired
    private UserBmrkDtlRepository userBmrkDtlRepository;

    /**
     * 사용자 즐겨찾기 상세 목록 조회
     * @param param
     * @return
     */
    public List<UserBmrkDto.UserBmrkDtlList> getUserBmrkDtlList(UserBmrkDtlEntity param) {
        try {
            return userBmrkDtlRepository.findByUserIdAndMenuLinkNmAndDelYnOrderByBmrkTurnDesc(param.getUserId(), param.getMenuLinkNm(), "N");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 사용자 즐겨찾기 상세 등록
     * @param param
     * @return
     */
    public boolean addUserBmrkDtl(UserBmrkDtlEntity param) {
        try {
            param.setBmrkTurn(userBmrkDtlRepository.getMaxBmrkTurn(param.getUserId()) + 1);
            param.setDelYn("N");

            if (userBmrkDtlRepository.save(param) != null) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 사용자 즐겨찾기 상세 수정
     * @param param
     * @return
     */
    public boolean editUserBmrkDtl(UserBmrkDtlEntity param) {
        try {
            UserBmrkDtlEntity userBmrkDtlEntity = userBmrkDtlRepository.findByBmrkUidAndUserId(param.getBmrkUid(), param.getUserId());
            if (userBmrkDtlEntity != null) {
                userBmrkDtlEntity.setBmrkNm(param.getBmrkNm());
                userBmrkDtlEntity.setFltrDatVal(param.getFltrDatVal());

                if (userBmrkDtlRepository.save(userBmrkDtlEntity) != null) {
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 사용자 즐겨찾기 상세 삭제
     * @param param
     * @return
     */
    public boolean deleteUserBmrkDtl(UserBmrkDtlEntity param) {
        try {
            UserBmrkDtlEntity userBmrkDtlEntity = userBmrkDtlRepository.findByBmrkUidAndUserId(param.getBmrkUid(), param.getUserId());
            if (userBmrkDtlEntity != null) {
                userBmrkDtlEntity.setDelYn("Y");

                if (userBmrkDtlRepository.save(userBmrkDtlEntity) != null) {
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
