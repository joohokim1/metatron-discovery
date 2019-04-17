package app.metatron.discovery.domain.ipm.domain.common;

import java.io.Serializable;
import lombok.Data;

/**
 * 사용자 설정 상세 복합키
 */
@Data
public class UserEstDtlId implements Serializable {
    private String userId;
    private String menuLinkNm;
}
