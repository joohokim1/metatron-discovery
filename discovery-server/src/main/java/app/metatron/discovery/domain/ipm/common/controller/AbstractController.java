package app.metatron.discovery.domain.ipm.common.controller;

/**
 * 기본 상위 컨트롤러
 */
public abstract class AbstractController {
    /**
     * 현재 접속한 사용자 아이디
     * @return
     */
    protected String getCurrentUserId() {
        // 세션에서 로그인정보 가져옴
//        Object principal    = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//        User user           = principal instanceof User ? (User) principal : null;
//        String anonymous    = principal instanceof String ? (String) principal : null;
//        String username     = user != null ? user.getUsername() : anonymous;

        return "polaris";
    }

}