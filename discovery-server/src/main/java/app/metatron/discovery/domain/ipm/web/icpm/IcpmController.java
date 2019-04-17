package app.metatron.discovery.domain.ipm.web.icpm;

import app.metatron.discovery.domain.ipm.common.constant.Const;
import app.metatron.discovery.domain.ipm.common.constant.Path;
import app.metatron.discovery.domain.ipm.common.controller.AbstractController;
import app.metatron.discovery.domain.ipm.common.value.ResultVO;
import app.metatron.discovery.domain.ipm.domain.common.UserBmrkDto;
import app.metatron.discovery.domain.ipm.domain.icpm.IcpmDto;
import app.metatron.discovery.domain.ipm.domain.icpm.IcpmFltrBasEntity;
import app.metatron.discovery.domain.ipm.service.icpm.*;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class IcpmController extends AbstractController {

    /**
     * chart : 카운트
     */
    private final String C0 = "c0";

    /**
     * chart : 지역별
     */
    private final String C1 = "c1";

    /**
     * chart : 연령별
     */
    private final String C2 = "c2";

    /**
     * chart : 제조사별
     */
    private final String C3 = "c3";

    /**
     * chart : 모델/애칭별
     */
    private final String C4 = "c4";

    /**
     * chart : 조직별
     */
    private final String C5 = "c5";

    /**
     * chart : traffic별
     */
    private final String C6 = "c6";

    /**
     * chart : cei별
     */
    private final String C7 = "c7";

    @Autowired
    private IcpmDruidService icpmDruidService;

    @Autowired
    private IcpmService icpmService;

    /**
     * icpm 필터 기본 목록 조회
     *
     * @return
     */
    @ApiOperation(
            value = "getIcpmFltrBasList",
            notes = "icpm 필터 기본 목록 조회"
    )
    @RequestMapping(value = Path.API + "/ipm/icpm/getIcpmFltrBasList", method = RequestMethod.POST)
    public ResponseEntity getIcpmFltrBasList(@RequestHeader(name = "Authorization") String authorization) {

        List<IcpmDto.IcpmFltrBasList> data = icpmService.getIcpmFltrBasList();
        if (data != null && !data.isEmpty()) {
            return ResponseEntity.ok(new ResultVO(Const.Common.RESULT_CODE.SUCCESS, "", data));
        }

        return ResponseEntity.ok(new ResultVO(Const.Common.RESULT_CODE.FAIL, ""));
    }

    /**
     * icpm 필터 기본 조회
     * @param param
     * @return
     */
    @ApiOperation(
            value = "getIcpmFltrBas",
            notes = "icpm 필터 기본 조회"
    )
    @RequestMapping(value = Path.API + "/ipm/icpm/getIcpmFltrBas", method = RequestMethod.POST)
    public ResponseEntity getIcpmFltrBas(@RequestHeader(name = "Authorization") String authorization,
                                         @RequestBody IcpmFltrBasEntity param) {

        if (StringUtils.isNotBlank(param.getFltrUid())) {

            IcpmDto.IcpmFltrBas data = icpmService.getIcpmFltrBas(param.getFltrUid());
            if (data != null) {
                return ResponseEntity.ok(new ResultVO(Const.Common.RESULT_CODE.SUCCESS, "", data));
            }
        }

        return ResponseEntity.ok(new ResultVO(Const.Common.RESULT_CODE.FAIL, ""));
    }

    /**
     * icpm 차트 조회
     * @param param
     * @return
     */
    @ApiOperation(
            value = "getIcpmCharts",
            notes = "Icpm 차트 조회"
    )
    @RequestMapping(value = Path.API + "/ipm/icpm/getIcpmCharts", method = RequestMethod.POST)
    public ResponseEntity getIcpmCharts(@RequestHeader(name = "Authorization") String authorization,
                                        @RequestBody IcpmDto.Chart param) {

        String chartNum = String.valueOf(param.getChartNum());

        if (StringUtils.isNotBlank(chartNum)) {
            List<UserBmrkDto.FltrDatVal> fltrDatVal = param.getFltrDatVal();
            String occrDt = param.getOccrDt();
            Object data = null;

            if ((C0).equals(chartNum)) { // 카운트
                data = icpmDruidService.getChartCount(fltrDatVal, occrDt);
            } else {

                // occrDt 필수 차트
                if (StringUtils.isNotBlank(occrDt)) {
                    if ((C1).equals(chartNum)) { // 지역별
                        List<String> addrCd = param.getAddrCd();
                        if (addrCd != null) {
                            data = icpmDruidService.getRegionChart(fltrDatVal, occrDt, addrCd);
                        }

                    } else if ((C2).equals(chartNum)) { // 연령별
                        data = icpmDruidService.getAgeChart(fltrDatVal, occrDt);

                    } else if ((C3).equals(chartNum)) { // 제조사별
                        data = icpmDruidService.getVendorChart(fltrDatVal, occrDt);

                    } else if ((C4).equals(chartNum)) { // 단말애칭별
                        data = icpmDruidService.getModelChart(fltrDatVal, occrDt);

                    } else if ((C5).equals(chartNum)) { // 본부/팀별
                        data = icpmDruidService.getOrgChart(fltrDatVal, occrDt);

                    } else if ((C6).equals(chartNum)) { // Traffic별
                        data = icpmDruidService.getTrafficChart(fltrDatVal, occrDt);

                    } else if ((C7).equals(chartNum)) { // cei별
                        data = icpmDruidService.getCeiChart(fltrDatVal, occrDt);
                    }
                }
            }

            if (data != null) {
                return ResponseEntity.ok(new ResultVO(Const.Common.RESULT_CODE.SUCCESS, "", data));
            }
        }

        return ResponseEntity.ok(new ResultVO(Const.Common.RESULT_CODE.FAIL, ""));
    }

    /**
     * icpm EXCEL 다운로드
     * @return
     */
    @ApiOperation(
            value = "getIcpmExcel",
            notes = "icpm EXCEL 다운로드"
    )
    @RequestMapping(value = Path.API + "/ipm/icpm/getIcpmExcel", method = RequestMethod.POST)
    public HttpEntity getIcpmExcel(@RequestHeader(name = "Authorization") String authorization,
                                   @RequestBody IcpmDto.Chart param, HttpServletResponse response) {

        if (param != null) {
            List<UserBmrkDto.FltrDatVal> fltrDatVal = param.getFltrDatVal();
            String occrDt = param.getOccrDt();
            if (StringUtils.isNotBlank(occrDt)) {
                icpmDruidService.getIcpmExcel(fltrDatVal, occrDt, response);
            }
        }

        return new ResponseEntity(HttpStatus.OK);
    }
}
