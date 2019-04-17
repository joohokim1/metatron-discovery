package app.metatron.discovery.domain.ipm.common.domain;

/**
 * 경로 등과 같은 path에 대한 인터페이스
 */
public interface INavigable {

    /**
     * 경로를 얻다
     * @return
     */
    String getNavigation();
}
