package app.metatron.discovery.domain.ipm.service.lpm;

import app.metatron.discovery.domain.ipm.domain.lpm.CommDto;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class LpmApiService {

    @Autowired
    private LpmChartService lpmChartService;

    private JSONArray getJsonArray(JSONObject jsonObject) {

        JSONParser jsonParser = new JSONParser();
        JSONArray jsonArray = null;
        try {
            jsonObject = (JSONObject) jsonParser.parse(jsonObject.toJSONString());
            jsonArray = (JSONArray) jsonObject.get("fltrDatVal");

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return jsonArray;
    }

    private List<CommDto> setMissingRangeData(List<CommDto> result, int min, int max, int interval, String division) {
        Map oldData = new HashMap(); // druid Data

        for (int i = 0; i < result.size(); i++) {
            oldData.put(result.get(i).getName(), result.get(i).getValue());
        }

        List<CommDto> list = new ArrayList<>();
        if ( "age".equals(division) ) {
            for (int i = min; i <= max; i = i + interval) {
                if ( null != oldData.get(String.valueOf(i)) ) {
                    if ( 80 == i ) {
                        list.add(CommDto.set(String.valueOf(i) + "+", oldData.get(String.valueOf(i))));
                    } else {
                        list.add(CommDto.set(String.valueOf(i), oldData.get(String.valueOf(i))));
                    }

                } else {
                    list.add(CommDto.set(String.valueOf(i), null));
                }
            }

        } else {
            for (int i = min; i <= max; i = i + interval) {
                if ( null != oldData.get(String.valueOf(i)) ) {
                    list.add(CommDto.set(String.valueOf(i), oldData.get(String.valueOf(i))));

                } else {
                    list.add(CommDto.set(String.valueOf(i), null));
                }
            }
        }

        return list;
    }
}
