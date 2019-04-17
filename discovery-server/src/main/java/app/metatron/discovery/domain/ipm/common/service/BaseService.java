package app.metatron.discovery.domain.ipm.common.service;

/**
 * 최상위 기본 서비스
 * 사용자 정보 조회
 */
public class BaseService {
    /**
     * 현재 접속한 사용자의 아이디
     * @return
     */
    protected String getCurrentUserId() {
        // 세션에서 로그인정보 가져옴
//        Object principal    = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//        User user           = principal instanceof User ? (User) principal : null;
//        String anonymous    = principal instanceof String ? (String) principal : null;
//        String username     = user != null ? user.getUsername() : anonymous;
//
//        return username;
        return "polaris";
    }
}
