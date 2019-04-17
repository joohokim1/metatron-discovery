package app.metatron.discovery.domain.ipm.service.lpm;

import app.metatron.discovery.domain.ipm.common.util.excel.ExcelGenHelper;
import app.metatron.discovery.domain.ipm.domain.lpm.CommDto;
import app.metatron.discovery.domain.ipm.domain.lpm.LpmChartDto.Population;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class LpmDruidService {

    /**
     * epm chart eNB 장비목록 dimension 명
     */
    @Value("${polaris.lpm.ds-lpm-grid-dim-nm}")
    private String[] dsLpmGridDimNm;	
	
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

    /**
     * 차트 데이터
     * @param jsonObject
     * @return
     */
    public Object getChartPopulData(JSONObject jsonObject) {
       	return lpmChartService.getChartPerAge(jsonObject);
    }    
    
    /**
     * 그리드 데이터
     * @param jsonObject
     * @return
     */
    public Object getGridPopulData(JSONObject jsonObject) {
       	return lpmChartService.getGridPerLoc(jsonObject);
    }     
    
    /**
     * 그리드 엑셀 데이터
     * @param jsonObject
     * @return
     */
    public List<Population> getGridExcelData(JSONObject jsonObject) {
       	//return lpmChartService.getXdrRowData(jsonObject);
    	return lpmChartService.getXdrGroupByData(jsonObject);
    }      
    
    /**
     * 중심점 데이터
     * @param jsonObject
     * @return
     */
    public Object getAddrCenterPoint(JSONObject jsonObject) {
       	return lpmChartService.getAddrCenterPoint(jsonObject);
    }     
    
    /**
     * xDR Row Data
     * @param jsonObject
     * @return
     */
    public Object getXdrRowData(JSONObject jsonObject) {
        Map<String, Object> data = new HashMap<>();
        data.put("dataList", lpmChartService.getXdrRowData(jsonObject));
        
        return data;
    }

    /**
     * Grid 목록 EXCEL 다운로드
     * @param
     * @return
     */    
	public boolean getGridListExcel(JSONObject param, String limit, HttpServletResponse response) {
		
        boolean excelDownload = true;
        List<Population> data = getGridExcelData(param);
        String startEndTimestamp = (String) param.get("startEndTimestamp");
        
        if (data != null) {
        	List<Population> body = data;

            try {

                // 워크북 생성
                SXSSFWorkbook wb = new SXSSFWorkbook();
                Sheet sheet = wb.createSheet("GridData_List");
                org.apache.poi.ss.usermodel.Row row = null;
                Cell cell = null;
                int rowNo = 0;

                // cell 설정
                CellStyle cellStyle = wb.createCellStyle();
                cellStyle.setBorderTop(BorderStyle.THIN);
                cellStyle.setBorderBottom(BorderStyle.THIN);
                cellStyle.setBorderLeft(BorderStyle.THIN);
                cellStyle.setBorderRight(BorderStyle.THIN);
                Font font = wb.createFont();
                font.setFontHeightInPoints((short) 10);
                font.setFontName("맑은 고딕");
                cellStyle.setFont(font);

                //시트 생성
                row = sheet.createRow(rowNo++);
                String msg = "최대 " + limit + "건까지 출력됩니다.";
                cell = row.createCell(0);
                cell.setCellValue(msg);

                row = sheet.createRow(rowNo++);
                String time = "기간 : " + startEndTimestamp;
                cell = row.createCell(0);
                cell.setCellValue(time);                
                
                // 시트 설정
                sheet.setColumnWidth(2, (short)3000);
                sheet.setColumnWidth(3, (short)3000);
                sheet.setColumnWidth(4, (short)4000);
                sheet.setColumnWidth(5, (short)3000);

                row = sheet.createRow(rowNo++);
                
                
                // 헤더 생성
                List<String> header = Arrays.asList(dsLpmGridDimNm);

                // 번호
                cell = row.createCell(0);
                cell.setCellStyle(cellStyle);
                cell.setCellValue("번호");

                int headerSize = header.size();
                for (int i = 0; i < headerSize; i++) {
                    cell = row.createCell(i + 1);
                    cell.setCellStyle(cellStyle);
                    cell.setCellValue(header.get(i));
                }
				
                
                // 데이터 생성
                int bodySize = body.size();
                for (int i = 0; i < bodySize; i++) {

                    row = sheet.createRow(rowNo++);

                    // 번호
                    cell = row.createCell(0);
                    cell.setCellStyle(cellStyle);
                    cell.setCellValue(body.size() - i);
                    
                    List<String> tdLIst = new ArrayList<String>();

                    tdLIst.add(body.get(i).getAddress().getSidoNm().toString());
                    tdLIst.add(body.get(i).getAddress().getSggNm().toString());
                    tdLIst.add(body.get(i).getAddress().getDongNm().toString());
                    tdLIst.add(body.get(i).getLdongCd().toString());
                    tdLIst.add(body.get(i).getEnbId().toString());
                    tdLIst.add(body.get(i).getCellId().toString());
                    
                    switch (body.get(i).getSex().toString()) {
					case "1":
						tdLIst.add("남");
						break;
					case "2":
						tdLIst.add("여");
						break;
					case "3":
						tdLIst.add("법인");
						break;
					case "4":
						tdLIst.add("시험폰");
						break;						
					}
                    
                    switch (body.get(i).getAge().toString()) {
					case "01":
						tdLIst.add("10대");
						break;
					case "02":
						tdLIst.add("20대");
						break;
					case "03":
						tdLIst.add("30대");
						break;
					case "04":
						tdLIst.add("40대");
						break;	
					case "05":
						tdLIst.add("50대");
						break;	
					case "06":
						tdLIst.add("60대");
						break;	
					case "07":
						tdLIst.add("70대");
						break;	
					case "08":
						tdLIst.add("80대");
						break;	
					default :
						tdLIst.add("90대 이상");
						break;	
					}                    
                    tdLIst.add(body.get(i).getTotCeiVal().toString());
                    //tdLIst.add(body.get(i).getGeo().toString());
                    tdLIst.add(String.valueOf(body.get(i).getUserCnt()));
                    
                    int tdListSize = tdLIst.size();

                    for (int j = 0; j < tdListSize; j++) {

                        // cell 생성
                        cell = row.createCell(j + 1);
                        cell.setCellStyle(cellStyle);
                        cell.setCellValue(tdLIst.get(j));

                    }

                }
				
                // 엑셀 응답 정보 및 파일 이름 지정
                response.setContentType("ms-vnd/excel");
                response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("파일이름", "UTF-8") + "." + ExcelGenHelper.XLSX);

                // 엑셀 출력
                wb.write(response.getOutputStream());
                wb.close();
				
            } catch (Exception e) {
                log.error(e.getMessage());
                excelDownload = false;
            }
        } else {
            excelDownload = false;
        }
        return excelDownload;		
	}      
}
