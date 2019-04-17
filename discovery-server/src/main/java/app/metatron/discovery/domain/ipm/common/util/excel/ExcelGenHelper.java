package app.metatron.discovery.domain.ipm.common.util.excel;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * 엑셀 변환 유틸
 */
public class ExcelGenHelper {

    private Workbook workbook;

    /** Excel data */
    private Map<String, Object> model;

    private HttpServletResponse response;

    /** 파일 이름 */
    public static final String FILE_NAME = "FILE_NAME";

    /** Data 목록 */
    public static final String DATA = "DATA";

    /** Head Data */
    public static final String HEAD = "HEAD_DATA";

    /** 확장자 */
    public static final String XLSX = "xlsx";

    /** 날짜 포멧 */
    private CellStyle dateCellStyle;

    public ExcelGenHelper(Workbook workbook, Map<String, Object> model, HttpServletResponse response) {
        this.workbook = workbook;
        this.model = model;
        this.response = response;

        // 날짜 포맷을 설정한다.
        dateCellStyle = workbook.createCellStyle();
        DataFormat foramt = workbook.createDataFormat();
        dateCellStyle.setDataFormat(foramt.getFormat("m/d/yy h:mm"));

    }

    /**
     * Excel을 생성한다.
     */
    public void createExcel() {
        setFileName(response, getFileName());

        Sheet sheet = workbook.createSheet();
//        sheet.addMergedRegion(new CellRangeAddress(0, 1, 0, 0));
        List<List<Object>> dataList = getDataList();

        if( dataList == null) {
            // null 이면
            dataList =  new ArrayList<List<Object>>();
        }

        if( dataList.size() < 1) {
            // Data가 없어면 NO DATA 출력
            List<Object> list = new ArrayList<Object>();
            list.add("NO Data");

            dataList.add(list);
        }

        createRows(sheet, dataList);
    }

    /**
     * 모델에서 파일 이름을 가져온다. 없으면 날짜
     * @return
     */
    private String getFileName() {
        String fileName = (String) model.get(FILE_NAME);
        if( fileName != null && fileName.trim().length() > 0) {
            return fileName;
        }

        return new Date().getTime()+"";
    }

    /**
     * Excel에 출력할 Data 목록
     * @return
     */
    @SuppressWarnings("unchecked")
    private List<List<Object>> getDataList() {
        return (List<List<Object>>) model.get(DATA);
    }

    /**
     * 브라우저에 File 이름을 설정한다.
     * @param response
     * @param fileName
     */
    private void setFileName(HttpServletResponse response, String fileName) {
        response.setHeader("Content-Disposition",
                "attachment; filename=\"" + setFileExtension(fileName) + "\"");
    }

    /**
     * 파일 이름에 확장자를 설정한다.
     * @param fileName
     * @return
     */
    private String setFileExtension(String fileName) {
        if (workbook instanceof XSSFWorkbook) {
            fileName += ".xlsx";
        }
        if (workbook instanceof SXSSFWorkbook) {
            fileName += ".xlsx";
        }
        if (workbook instanceof HSSFWorkbook) {
            fileName += ".xls";
        }

        return fileName;
    }

    /**
     * Excel의 Row를 설정한다.
     * @param sheet
     * @param bodyList
     */
    private void createRows(Sheet sheet, List<List<Object>> bodyList) {
        int rowSize = bodyList.size();
        for (int i = 0; i < rowSize; i++) {
            createRow(sheet, bodyList.get(i), i);
        }
    }

    /**
     * Head 스타일을 설정한다.
     * @param cell
     */
    private void setBackGroundColor(Cell cell) {

        CellStyle backStyle = workbook.createCellStyle();

        backStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());

        backStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        cell.setCellStyle(backStyle);
    }

    /**
     * Row를 생성한다.
     * @param sheet
     * @param cellList
     * @param rowNum
     */
    private void createRow(Sheet sheet, List<Object> cellList, int rowNum) {
        int size = cellList.size();
        Row row = sheet.createRow(rowNum);

        boolean isHead = false;
        if( size > 0 ) {
            Object obj = cellList.get(0);
            if( HEAD.equals(((String)obj).trim())  ) {
                // 첫 Row가 HEAD 일 경우
                isHead = true;
                cellList.remove(0);

                size--;
            }
        }

        for (int i = 0; i < size; i++) {
            Cell cell = row.createCell(i);
            Object obj = cellList.get(i);
            if (obj == null) {
                cell.setCellValue("");
            } else if (obj instanceof Integer) {
                cell.setCellValue(((Integer) obj).doubleValue());
            } else if (obj instanceof Long) {
                cell.setCellValue(((Long) obj).doubleValue());
            } else if (obj instanceof Float) {
                cell.setCellValue(((Float) obj).doubleValue());
            } else if (obj instanceof Double) {
                cell.setCellValue((Double) obj);
            } else if (obj instanceof String) {
                cell.setCellValue((String) obj);
            } else if (obj instanceof Date) {
                // 날짜일 경우
                cell.setCellValue((Date) obj);
                cell.setCellStyle(dateCellStyle);
            } else {
                // 아니면 문자열 출력
                cell.setCellValue(obj.toString());
            }

            if (isHead) {
                // Head일 경우 스타일 설정
                setBackGroundColor(cell);
            }

        }
    }
}
