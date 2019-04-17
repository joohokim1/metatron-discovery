package app.metatron.discovery.domain.ipm.service.common;

import com.simplaex.clients.druid.DruidClient;
import app.metatron.discovery.domain.ipm.domain.common.ComDto;
import io.druid.query.Query;
import io.druid.query.filter.*;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class DruidService {

    /**
     * druid host
     */
    @Value("${polaris.ipm.druid-host}")
    private String host;

    /**
     * druid port
     */
    @Value("${polaris.ipm.druid-port}")
    private Integer port;

    private int index = 0;
    private List<DruidClient> clientList = null;

    protected String setHost() {
        return host;
    }

    protected Integer setPort() {
        return port;
    }

    protected Integer setPool() {
        return 1;
    }

    private void initClient(String host, int port, int pool) {

        if (clientList == null) {
            clientList = new ArrayList<>();
            for (int i = 0; i < pool; i++) {
                clientList.add(DruidClient.create(host, port));
            }
        }
    }

    protected void initClient() {
        initClient(setHost(), setPort(), setPool());
    }

    protected DruidClient getClient() {

        if (clientList == null) {
            initClient();
        }

        index = index % setPool();
        return clientList.get(index++);
    }

    /**
     * RegexDimFilter 생성
     * @param druidNm
     * @param fltrVal
     * @return
     */
    protected DimFilter orRegexDimFilter(String druidNm, List<ComDto.Code> fltrVal) {

        List<DimFilter> ret = new ArrayList<>();
        for (ComDto.Code cd : fltrVal) {
            List<String> code = (List<String>)cd.getCode();
            String pattern = "^";
            for (String key : code) {
                // 시도, 시군구, 읍면동 코드를 합쳐준다(존재하는 코드만).
                pattern += key;
            }
            ret.add(new RegexDimFilter(druidNm, pattern + "[0-9]*", null));
        }

        if (ret.size() > 1) {
            return new OrDimFilter(ret);
        } else {
            return ret.get(0);
        }
    }

    /**
     * BoundDimFilter 생성
     * @param druidNm
     * @param fltrVal
     * @return
     */
    protected DimFilter orBoundDimFilter(String druidNm, List<ComDto.Code> fltrVal) {

        List<DimFilter> ret = new ArrayList<>();
        for (ComDto.Code cd : fltrVal) {
            String[] bound = ((String)cd.getCode()).split("~");
            String lower = bound[0];
            String upper = null;
            if (bound.length > 1) {
                upper = bound[1];
            }
            ret.add(new BoundDimFilter(druidNm, lower, upper, false, false, null, null, null));
        }

        if (ret.size() > 1) {
            return new OrDimFilter(ret);
        } else {
            return ret.get(0);
        }
    }

    /**
     * InDimFilter 생성
     * @param druidNm
     * @param fltrVal
     * @return
     */
    protected DimFilter inDimFilter(String druidNm, List<ComDto.Code> fltrVal) {

        List<String> ret = new ArrayList<>();
        for (ComDto.Code cd : fltrVal) {
            ret.add((String)cd.getCode());
        }

        if (ret.size() > 1) {
            return new InDimFilter(druidNm, ret, null);
        } else {
            return new SelectorDimFilter(druidNm, ret.get(0), null);
        }
    }

    /**
     * druid 실행
     * @param query
     * @param <T>
     * @return
     */
    protected <T> List<T> druidRun(Query<T> query) {
        return getClient().run(query).toList();
    }
}
