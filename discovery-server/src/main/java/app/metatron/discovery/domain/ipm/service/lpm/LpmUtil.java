package app.metatron.discovery.domain.ipm.service.lpm;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Nullable;
import lombok.extern.slf4j.Slf4j;
import org.apache.parquet.Strings;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

@Slf4j
public class LpmUtil {

    public static List<String> parseValue(String value) {
        List<String> ret = null;

        if(Strings.isNullOrEmpty(value)) {
            return ret;
        }

        String[] out;
        if (value.matches("^[0-9]*~[0-9]*$")) {
            out = value.split("~");
        } else {
            out = value.split("\\|");
        }

        ret = Arrays.asList(out);

        return ret;
    }

    //Integer만 허용함. dimension에서 사용하는 것이므로 integer가 많으리라 예상됨.
    public static List<String> getRangeData(String start, String end, @Nullable String deciFormat) {
        List<String> ret = null;
        if(Strings.isNullOrEmpty(start) ||
            Strings.isNullOrEmpty(end)) {
            return ret;
        }

        Integer s, e;
        try {
            s = Integer.parseInt(start);
            e = Integer.parseInt(end);
            NumberFormat nf = new DecimalFormat(deciFormat);
            ret = new ArrayList<>();
            if(s < e) {
                for(int i = s; i <= e; i++) {
                    ret.add(nf.format(i));
                }

            } else if(s == e) {
            	ret.add(nf.format(s));
            	
            } else {
                for(int i = s; i <= e; i--) {
                    ret.add(nf.format(i));
                }
            }

        }catch(Exception ex) {
            log.error(ex.getMessage());
        }
        return ret;

    }

    public static String[] safeArray(String[] split, int haveTosize) {

        String[] ret = new String[haveTosize];
        System.arraycopy(split, 0, ret, 0, split.length);
        return ret;

    }

    public static String makeLikeWord(List<String> filter) {
        String likeStr = "";
        for(String item : filter) {
            likeStr += item;
        }
        return likeStr;
    }

    public static JSONArray getJsonArray(JSONObject jsonObject) {

        JSONParser jsonParser = new JSONParser();
        JSONArray jsonArray = null;
        if (jsonObject != null) {
            try {
                jsonObject = (JSONObject) jsonParser.parse(jsonObject.toJSONString());
                jsonArray = (JSONArray) jsonObject.get("fltrDatVal");

            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        return jsonArray;
    }
}
