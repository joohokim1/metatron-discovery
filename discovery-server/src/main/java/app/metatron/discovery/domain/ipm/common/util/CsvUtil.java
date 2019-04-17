package app.metatron.discovery.domain.ipm.common.util;

import app.metatron.discovery.domain.ipm.common.value.workbench.QueryResult;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * csv 유틸
 */
@Slf4j
@Component
public class CsvUtil {

    /**
     * 샘플 데이터 경로 명
     */
    private static final String SAMPLE_DATA_DIR = "sample_data";
    /**
     * 확장자
     */
    private static final String SAMPLE_DATA_EXT = "csv";

    /**
     * 첨부 파일 기본 경로
     */
    @Value("${polaris.ipm.upload.path}")
    private String basePath;

    /**
     * 샘플 데이터 경로
     * 첨부파일 기본 경로 + 샘플 데이터 경로 + 스키마명 + 확장자
     * @param databaseNm
     * @param tableNm
     * @return
     */
    public String getSampleDataPath(String databaseNm, String tableNm) {
        StringBuilder fileName = new StringBuilder();
        fileName.append(basePath);
        fileName.append("/");
        fileName.append(SAMPLE_DATA_DIR);
        fileName.append("/");
        fileName.append(databaseNm);
        fileName.append(".");
        fileName.append(tableNm);
        fileName.append(".");
        fileName.append(SAMPLE_DATA_EXT);
        return fileName.toString();
    }

    /**
     * 샘플 데이터 읽기
     * @param databaseNm
     * @param tableNm
     * @return
     */
    public QueryResult read(String databaseNm, String tableNm) {
        QueryResult queryResult = new QueryResult();

        Set<String> headList = new HashSet<>();
        List<Map<String, Object>> resultList = new ArrayList<>();

        List<List<String>> rawList = new ArrayList<>();
        BufferedReader br = null;
        Path path = Paths.get(this.getSampleDataPath(databaseNm, tableNm));
        try {
            br = Files.newBufferedReader(path, Charset.forName("UTF-8"));
            String line;

            while( (line = br.readLine()) != null ) {
                // except comma in quotes
                String[] cols = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
                if( cols != null && cols.length > 0 ) {
                    List<String> row = new ArrayList<>();
                    // remove quotes
                    for(String col : cols) {
                        row.add(col.replace("\"", ""));
                    }
                    rawList.add(row);
                }
            }

            if( rawList.size() > 0 ) {
                String[] tmpHeadList = new String[rawList.get(0).size()];
                for( int i=0; i< rawList.size(); i++ ) {
                    List<String> row = rawList.get(i);
                    if( i == 0 ) {
                        // 헤더 만들기
                        for( int k=0; k<row.size(); k++ ) {
                            tmpHeadList[k] = row.get(k);
                            headList.add(row.get(k));
                        }
                    } else {
                        // 리스트 데이터
                        Map<String, Object> data = new HashMap<>();
                        for( int j=0; j<tmpHeadList.length; j++ ) {
                            String head = tmpHeadList[j];
                            String colData = null;
                            try {
                                colData = row.get(j);
                            } catch(Exception e) {
                                // ignore
                            }
                            data.put(head, colData);
                        }
                        resultList.add(data);
                    }
                }
                queryResult.setHeadList(headList);
                queryResult.setResultList(resultList);
            }
        } catch( Exception e ) {
            log.error("Failed read CSV file.");
        } finally {
            if( br != null ) {
                try {
                    br.close();
                } catch (IOException e) {
                    log.error("Failed read CSV file.");
                }
            }
        }
        return queryResult;
    }

    /**
     * 예정 없음
     * @param data
     */
    public void write(QueryResult data) {
        // not yet
    }

}
