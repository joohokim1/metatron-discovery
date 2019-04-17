package app.metatron.discovery.domain.ipm.domain.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.List;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Converter
public class FltrDatValConvert implements AttributeConverter<List<Object>, String> {
    private ObjectMapper objectMapper = new ObjectMapper();

    /**
     * DB : List<Object> -> String
     * @param list
     * @return
     */
    @Override
    public String convertToDatabaseColumn(List<Object> list) {
        try {
            return objectMapper.writeValueAsString(list);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Entity : String -> List<Object>
     * @param s
     * @return
     */
    @Override
    public List<Object> convertToEntityAttribute(String s) {
        try {
            return objectMapper.readValue(s, List.class);

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
