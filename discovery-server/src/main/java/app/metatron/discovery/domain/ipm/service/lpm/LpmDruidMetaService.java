package app.metatron.discovery.domain.ipm.service.lpm;


import com.simplaex.clients.druid.DruidClient;
import com.simplaex.clients.druid.DruidResult;
import io.druid.data.input.Row;
import io.druid.query.Druids;
import io.druid.query.Query;
import io.druid.query.Result;
import io.druid.query.select.SelectResultValue;
import io.druid.query.timeboundary.TimeBoundaryQuery;
import io.druid.query.timeboundary.TimeBoundaryResultValue;
import java.util.HashMap;
import java.util.List;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class LpmDruidMetaService {


    private static String host;
    private static Integer port;

    private static DruidClient client = null;
    private static DruidClient getClient() {

        if (LpmDruidMetaService.client == null) {
            LpmDruidMetaService.client = DruidClient.create(host, port);
        }

        return LpmDruidMetaService.client;
    }


    @Value("${polaris.ipm.druid-host}")
    public void setHost(String host) {
    	LpmDruidMetaService.host = host;
    }
    @Value("${polaris.ipm.druid-port}")
    public void setPort(Integer port) {
    	LpmDruidMetaService.port = port;
    }

    private static HashMap<String, String> intervalForDs = new HashMap<>();
    
    public static String getLocDefaultIntervar(String datasource) {
        String ret = "";
        if(intervalForDs.containsKey(datasource)) {
            ret = intervalForDs.get(datasource);
        } else {
            ret = (String) getTimeMeta(datasource, host, port );
            intervalForDs.put(datasource, ret);
        }
        return ret;
    }
    
    public static String getDefaultIntervar(String datasource) {
        String ret;
        if(intervalForDs.containsKey(datasource)) {
            ret = intervalForDs.get(datasource);
        } else {
            ret = (String) getTimeMeta(datasource);
            intervalForDs.put(datasource, ret);
        }
        return ret;
    }    
    
    public static Object getTimeMeta(String datasource, String host, Integer port) {

        String ret = null;
        Druids.TimeBoundaryQueryBuilder builder = new Druids.TimeBoundaryQueryBuilder();
        builder.dataSource(datasource);
        TimeBoundaryQuery query = builder.build();


        DruidClient client = DruidClient.create(host, port);
        DruidResult<Result<TimeBoundaryResultValue>> o = client.run(query);
        List<Result<TimeBoundaryResultValue>> lst = o.toList();
        if(lst.size() == 1) {
            Result<TimeBoundaryResultValue> obj = lst.get(0);
            ret = obj.getValue().getMinTime().minusDays(1).toString("yyyy-MM-dd'TZ'")
                    +"/"+
                    obj.getValue().getMaxTime().plusDays(1).toString("yyyy-MM-dd'TZ'");

        } else {
            ret = "";
            DateTime sdate = new DateTime(), edate = new DateTime();
            ret = sdate.minusMonths(1).toString("yyyy-MM-dd'T'HH:mm:ss'Z'")
                    +"/"+edate.plusMonths(1).toString("yyyy-MM-dd'T'HH:mm:ss'Z'");

        }
        return ret;
    }
    
    private static Object getTimeMeta(String datasource) {

        String ret;
        Druids.TimeBoundaryQueryBuilder builder = new Druids.TimeBoundaryQueryBuilder();
        builder.dataSource(datasource);
        TimeBoundaryQuery query = builder.build();

        DruidResult<Result<TimeBoundaryResultValue>> o = getClient().run(query);
        List<Result<TimeBoundaryResultValue>> lst = o.toList();
        if(lst.size() == 1) {
            Result<TimeBoundaryResultValue> obj = lst.get(0);
            ret = obj.getValue().getMinTime().minusDays(1).toString("yyyy-MM-dd'TZ'")
                    +"/"+
                    obj.getValue().getMaxTime().plusDays(1).toString("yyyy-MM-dd'TZ'");

        } else {
            DateTime sdate = new DateTime(), edate = new DateTime();
            ret = sdate.minusMonths(1).toString("yyyy-MM-dd'T'HH:mm:ss'Z'")
                    +"/"+edate.plusMonths(1).toString("yyyy-MM-dd'T'HH:mm:ss'Z'");

        }

        return ret;
    }
    
    public static List<Row> druidRun(Query query) {
        DruidResult<Row> result = getClient().run(query);
        List<Row> results = result.toList();
        return results;
    }
    public static DruidResult<Result<SelectResultValue>> druidRunSelect(Query query) {
        return getClient().run(query);
    }    
    public static DruidResult<Row> druidRunForTopN(Query query) {
        DruidResult<Row> result = getClient().run(query);
        return result;
    }

    public static List<Row> druidTestRun(Query query) {
        DruidResult<Row> result = getClient().run(query);
        List<Row> results = result.toList();
        return results;
    }
}