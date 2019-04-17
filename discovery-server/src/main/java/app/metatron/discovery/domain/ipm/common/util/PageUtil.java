package app.metatron.discovery.domain.ipm.common.util;

import java.util.List;
import org.springframework.data.domain.Sort;

/**
 * 페이젱 유틸
 */
public class PageUtil {

    /**
     * Sort 정보를 가져온다.
     *
     * @param sort
     * @return
     */
    public static Sort getPageSort(List<String> sort) {
        String sortStr = sort.get(0);
        String sortName = sortStr.split(",")[0];
        String sortDirection = sortStr.split(",")[1].toUpperCase();

        return new Sort(Sort.Direction.valueOf(sortDirection), sortName);
    }
}
