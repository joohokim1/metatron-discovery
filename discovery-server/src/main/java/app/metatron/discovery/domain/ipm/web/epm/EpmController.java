package app.metatron.discovery.domain.ipm.web.epm;

import app.metatron.discovery.domain.ipm.common.constant.Const;
import app.metatron.discovery.domain.ipm.common.constant.Path;
import app.metatron.discovery.domain.ipm.common.controller.AbstractController;
import app.metatron.discovery.domain.ipm.common.value.ResultVO;
import app.metatron.discovery.domain.ipm.domain.common.ComDto;
import app.metatron.discovery.domain.ipm.domain.common.UserBmrkDto;
import app.metatron.discovery.domain.ipm.domain.epm.EpmClBasEntity;
import app.metatron.discovery.domain.ipm.domain.epm.EpmDto;
import app.metatron.discovery.domain.ipm.domain.epm.EpmFltrBasEntity;
import app.metatron.discovery.domain.ipm.service.epm.EpmDruidService;
import app.metatron.discovery.domain.ipm.service.epm.EpmService;
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
public class EpmController extends AbstractController {

    @Autowired
    private EpmService epmService;

    @Autowired
    private EpmDruidService epmDruidService;

    /**
     * epm 분류 기본 목록 조회
     * @return
     */
    @ApiOperation(
            value = "getEpmClBasList",
            notes = "epm 분류 기본 목록 조회"
    )
    @RequestMapping(value = Path.API + "/ipm/epm/getEpmClBasList", method = RequestMethod.POST)
    public ResponseEntity getEpmClBasList(@RequestHeader(name = "Authorization") String authorization) {

        List<EpmDto.EpmClBasList> data = epmService.getEpmClBasList();

        if (data != null && !data.isEmpty()) {
            return ResponseEntity.ok(new ResultVO(Const.Common.RESULT_CODE.SUCCESS, "", data));
        }

        return ResponseEntity.ok(new ResultVO(Const.Common.RESULT_CODE.FAIL, ""));
    }

    @ApiOperation(
            value = "getEpmFltrBasList",
            notes = "epm 필터 기본 목록 조회"
    )
    @RequestMapping(value = Path.API + "/ipm/epm/getEpmFltrBasList", method = RequestMethod.POST)
    public ResponseEntity getEpmFltrBasList(@RequestHeader(name = "Authorization") String authorization,
                                            @RequestBody EpmClBasEntity param) {

        if (StringUtils.isNotBlank(param.getClUid())) {
            List<EpmDto.EpmFltrBasList> data = epmService.getEpmFltrBasList(param.getClUid());

            if (data != null && !data.isEmpty()) {
                return ResponseEntity.ok(new ResultVO(Const.Common.RESULT_CODE.SUCCESS, "", data));
            }
        }

        return ResponseEntity.ok(new ResultVO(Const.Common.RESULT_CODE.FAIL, ""));
    }

    /**
     * epm 필터 기본 조회
     * @param param
     * @return
     */
    @ApiOperation(
            value = "getEpmFltrBas",
            notes = "epm 필터 기본 조회"
    )
    @RequestMapping(value = Path.API + "/ipm/epm/getEpmFltrBas", method = RequestMethod.POST)
    public ResponseEntity getEpmFltrBas(@RequestHeader(name = "Authorization") String authorization,
                                        @RequestBody EpmFltrBasEntity param) {

        if (StringUtils.isNotBlank(param.getFltrUid())) {
            EpmDto.EpmFltrBas data = epmService.getEpmFltrBas(param.getFltrUid());

            if (data != null) {
                return ResponseEntity.ok(new ResultVO(Const.Common.RESULT_CODE.SUCCESS, "", data));
            }
        }

        return ResponseEntity.ok(new ResultVO(Const.Common.RESULT_CODE.FAIL, ""));
    }

    /**
     * epm 조직 목록 조회
     * @param info - request parameter
     * @return
     */
    @ApiOperation(
            value = "getEpmOrgList",
            notes = "epm 조직 목록 조회"
    )
    @RequestMapping(value = Path.API + "/ipm/epm/getEpmOrgList", method = RequestMethod.POST)
    public ResponseEntity getEpmOrgList(@RequestHeader(name = "Authorization") String authorization,
                                        @RequestBody EpmDto.Info info) {

        if (info != null && StringUtils.isNotBlank(info.getDruidNm())) {
            List<ComDto.Code> data = epmService.getEpmOrgList(info);

            if (data != null && !data.isEmpty()) {
                return ResponseEntity.ok(new ResultVO(Const.Common.RESULT_CODE.SUCCESS, "", data));
            }
        }

        return ResponseEntity.ok(new ResultVO(Const.Common.RESULT_CODE.FAIL, ""));
    }

    /**
     * epm 주소 목록 조회
     * @param info - request parameter
     * @return
     */
    @ApiOperation(
            value = "getEpmAddrList",
            notes = "epm 주소 목록 조회"
    )
    @RequestMapping(value = Path.API + "/ipm/epm/getEpmAddrList", method = RequestMethod.POST)
    public ResponseEntity getEpmAddrList(@RequestHeader(name = "Authorization") String authorization,
                                         @RequestBody EpmDto.Info info) {

        if (info != null && StringUtils.isNotBlank(info.getDruidNm())) {
            List<ComDto.Code> data = epmService.getEpmAddrList(info);

            if (data != null && !data.isEmpty()) {
                return ResponseEntity.ok(new ResultVO(Const.Common.RESULT_CODE.SUCCESS, "", data));
            }
        }

        return ResponseEntity.ok(new ResultVO(Const.Common.RESULT_CODE.FAIL, ""));
    }

    /**
     * epm EMS 목록 조회
     * @param info - request parameter
     * @return
     */
    @ApiOperation(
            value = "getEpmEmsList",
            notes = "epm EMS 목록 조회"
    )
    @RequestMapping(value = Path.API + "/ipm/epm/getEpmEmsList", method = RequestMethod.POST)
    public ResponseEntity getEpmEmsList(@RequestHeader(name = "Authorization") String authorization,
                                        @RequestBody EpmDto.Info info) {

        if (info != null && StringUtils.isNotBlank(info.getDruidNm())) {
            List<ComDto.Code> data = epmService.getEpmEmsList(info);

            if (data != null && !data.isEmpty()) {
                return ResponseEntity.ok(new ResultVO(Const.Common.RESULT_CODE.SUCCESS, "", data));
            }
        }

        return ResponseEntity.ok(new ResultVO(Const.Common.RESULT_CODE.FAIL, ""));
    }

    /**
     * epm ENB 목록 조회
     * @param info - request parameter
     * @return
     */
    @ApiOperation(
            value = "getEpmEnbList",
            notes = "epm ENB 목록 조회"
    )
    @RequestMapping(value = Path.API + "/ipm/epm/getEpmEnbList", method = RequestMethod.POST)
    public ResponseEntity getEpmEnbList(@RequestHeader(name = "Authorization") String authorization,
                                        @RequestBody EpmDto.Info info) {

        if (info != null && StringUtils.isNotBlank(info.getDruidNm())) {
            List<ComDto.Code> data = epmService.getEpmEnbList(info);

            if (data != null) {
                return ResponseEntity.ok(new ResultVO(Const.Common.RESULT_CODE.SUCCESS, "", data));
            }
        }

        return ResponseEntity.ok(new ResultVO(Const.Common.RESULT_CODE.FAIL, ""));
    }

    /**
     * epm 국사 목록 조회
     * @param info - request parameter
     * @return
     */
    @ApiOperation(
            value = "getEpmMtsoList",
            notes = "epm 국사 목록 조회"
    )
    @RequestMapping(value = Path.API + "/ipm/epm/getEpmMtsoList", method = RequestMethod.POST)
    public ResponseEntity getEpmMtsoList(@RequestHeader(name = "Authorization") String authorization,
                                         @RequestBody EpmDto.Info info) {

        if (info != null && StringUtils.isNotBlank(info.getDruidNm())) {
            List<ComDto.Code> data = epmService.getEpmMtsoList(info);

            if (data != null) {
                return ResponseEntity.ok(new ResultVO(Const.Common.RESULT_CODE.SUCCESS, "", data));
            }
        }

        return ResponseEntity.ok(new ResultVO(Const.Common.RESULT_CODE.FAIL, ""));
    }

    /**
     * epm 차트 조회
     * @param param
     * @return
     */
    @ApiOperation(
            value = "getEpmCharts",
            notes = "epm 차트 조회"
    )
    @RequestMapping(value = Path.API + "/ipm/epm/getEpmCharts", method = RequestMethod.POST)
    public ResponseEntity getEpmCharts(@RequestHeader(name = "Authorization") String authorization,
                                       @RequestBody EpmDto.Chart param) {
        if (param != null) {

            String chartNum = param.getChartNum();
            List<UserBmrkDto.FltrDatVal> fltrDatVal = param.getFltrDatVal();
            if (StringUtils.isNotBlank(chartNum) && fltrDatVal != null && !fltrDatVal.isEmpty()) {

                Object data = null;
                switch (chartNum) {
                    case "e0":
                        data = epmDruidService.getChartCount(fltrDatVal);
                        break;
                    case "e1":
                        List<String> orgList = param.getOrgCd();
                        if (orgList != null && !orgList.isEmpty()) {
                            data = epmDruidService.getOrgChart(fltrDatVal, orgList);
                        }
                        break;
                    case "e2":
                        List<String> addrList = param.getAddrCd();
                        if (addrList != null) {
                            data = epmDruidService.getRegionChart(fltrDatVal, addrList);
                        }
                        break;
                    case "e3":
                        data = epmDruidService.getEqpList(fltrDatVal, param.getLimit());
                        break;
                    case "e4":
                        if (param.getInfo() != null) {
                            data = epmDruidService.getEqpPerdList(fltrDatVal, param.getInfo(), param.getPaging(), false);
                        }
                        break;
                    default:
                        break;
                }

                if (data != null) {
                    return ResponseEntity.ok(new ResultVO(Const.Common.RESULT_CODE.SUCCESS, "", data));
                }
            }
        }

        return ResponseEntity.ok(new ResultVO(Const.Common.RESULT_CODE.FAIL, ""));
    }

    /**
     * epm Range Min, Max 조회
     * @param info - request parameter
     * @return
     */
    @ApiOperation(
            value = "getEpmDruidrange",
            notes = "epm Range Min, Max 조회"
    )
    @RequestMapping(value = Path.API + "/ipm/epm/getEpmDruidrange", method = RequestMethod.POST)
    public ResponseEntity getEpmDruidrange(@RequestHeader(name = "Authorization") String authorization,
                                         @RequestBody EpmDto.Info info) {

        if (info != null && StringUtils.isNotBlank(info.getDruidNm())) {
            // Range min, max 조회
            List<ComDto.Code> data = epmDruidService.getEpmDruidrange(info);

            if (data != null && !data.isEmpty()) {
                return ResponseEntity.ok(new ResultVO(Const.Common.RESULT_CODE.SUCCESS, "", data));
            }
        }

        return ResponseEntity.ok(new ResultVO(Const.Common.RESULT_CODE.FAIL, ""));
    }

    /**
     * epm EXCEL 다운로드
     * @return
     */
    @ApiOperation(
            value = "getEpmChartsExcel",
            notes = "epm EXCEL 다운로드"
    )
    @RequestMapping(value = Path.API + "/ipm/epm/getEpmChartsExcel", method = RequestMethod.POST)
    public HttpEntity getEpmChartsExcel(@RequestHeader(name = "Authorization") String authorization,
                                        @RequestBody EpmDto.Chart param, HttpServletResponse response) {

        if (param != null) {
            String chartNum = param.getChartNum();
            List<UserBmrkDto.FltrDatVal> fltrDatVal = param.getFltrDatVal();
            if (StringUtils.isNotBlank(chartNum) && fltrDatVal != null && !fltrDatVal.isEmpty()) {

                switch (chartNum) {
                    case "e3":
                        epmDruidService.getEqpListExcel(fltrDatVal, response);
                        break;
                    case "e4":
                        if (param.getInfo() != null) {
                            epmDruidService.getEqpPerdListExcel(fltrDatVal, param.getInfo(), response);
                        }
                        break;
                }
            }
        }

        return new ResponseEntity(HttpStatus.OK);
    }

}