package app.metatron.discovery.domain.ipm.common.util;

import app.metatron.discovery.domain.ipm.common.util.excel.ExcelGenHelper;
import app.metatron.discovery.domain.ipm.common.value.workbench.QueryResult;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 엑셀 유틸
 */
public class ExcelUtil {

    /**
     * QueryResult 를 엑셀 데이터로 변환
     * @param name
     * @param queryResult
     * @return
     */
    public static Map<String, Object> convertQueryResultToExcelData(String name, QueryResult queryResult) {
        Map<String, Object> excelModel = new HashMap<>();
        List<List<Object>> excelData = new ArrayList<>();

        // excel file name

        excelModel.put(ExcelGenHelper.FILE_NAME, name);

        // excel head
        List<Object> heads = new ArrayList<>();
        heads.add(ExcelGenHelper.HEAD);
        queryResult.getHeadList().forEach(head -> {
            heads.add(head);
        });

        // excel list data
        List<List<Object>> datas = new ArrayList<>();
        queryResult.getResultList().forEach(record -> {
            List<Object> row = new ArrayList<>();
            record.keySet().forEach(key -> {
                row.add(record.get(key));
            });
            datas.add(row);
        });

        // merge
        excelData.add(heads);
        excelData.addAll(datas);
        excelModel.put(ExcelGenHelper.DATA, excelData);

        return excelModel;
    }

}
