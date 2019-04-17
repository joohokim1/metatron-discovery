package app.metatron.discovery.domain.ipm.service.common;

import app.metatron.discovery.domain.ipm.domain.common.UserEstDtlEntity;
import app.metatron.discovery.domain.ipm.repository.common.UserEstDtlRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserEstService {

    @Autowired
    private UserEstDtlRepository userEstDtlRepository;

    /**
     * 사용자 설정 상세 조회
     * @param param
     * @return
     */
    public UserEstDtlEntity getUserEstDtl(UserEstDtlEntity param) {
        try {
            UserEstDtlEntity data = userEstDtlRepository.findByUserIdAndMenuLinkNm(param.getUserId(), param.getMenuLinkNm());

            // 사용자 설정 값이 DB에 존재하지 않을 경우(최초 설정 시)
            if (data == null) {
                data = new UserEstDtlEntity();
                data.setUserId(param.getUserId());
                data.setMenuLinkNm(param.getMenuLinkNm());
                data.setEstVal("");
            }

            return data;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 사용자 설정 상세 수정
     * @param param
     * @return
     */
    public Boolean editUserEstDtl(UserEstDtlEntity param) {
        try {
            // 사용자 설정 상세 정보를 조회
            UserEstDtlEntity userEstDtlEntity = userEstDtlRepository.findByUserIdAndMenuLinkNm(param.getUserId(), param.getMenuLinkNm());
            if (userEstDtlEntity != null) {
                //변경할 정보로 set하여 저장
                userEstDtlEntity.setEstVal(param.getEstVal());
                if (userEstDtlRepository.save(userEstDtlEntity) != null) {
                    return true;
                }
            } else {
                if (userEstDtlRepository.save(param) != null) {
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
