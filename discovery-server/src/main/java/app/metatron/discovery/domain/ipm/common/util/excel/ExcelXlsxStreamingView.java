package app.metatron.discovery.domain.ipm.common.util.excel;

import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.document.AbstractXlsxStreamingView;

/**
 * 엑셀 뷰 (스트림)
 */
@Component
public class ExcelXlsxStreamingView extends AbstractXlsxStreamingView {

    @Override
    protected void buildExcelDocument(Map<String, Object> model,
                                      Workbook workbook,
                                      HttpServletRequest request,
                                      HttpServletResponse response) throws Exception {
        new ExcelGenHelper(workbook, model, response).createExcel();
    }
}
