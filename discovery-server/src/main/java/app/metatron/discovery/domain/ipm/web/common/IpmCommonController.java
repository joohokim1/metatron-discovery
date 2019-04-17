package app.metatron.discovery.domain.ipm.web.common;

import app.metatron.discovery.domain.ipm.common.constant.Const;
import app.metatron.discovery.domain.ipm.common.constant.Path;
import app.metatron.discovery.domain.ipm.common.controller.AbstractController;
import app.metatron.discovery.domain.ipm.common.value.ResultVO;
import app.metatron.discovery.domain.ipm.domain.common.ComDto;
import app.metatron.discovery.domain.ipm.service.common.IpmCommonService;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class IpmCommonController extends AbstractController {

    @Autowired
    private IpmCommonService ipmCommonService;

    /**
     * 공통 코드 조회
     * @param param
     * @return
     */
    @ApiOperation(
            value = "getCommCode",
            notes = "공통 코드 조회"
    )
    @RequestMapping(value = Path.API+"/ipm/getCommCode", method=RequestMethod.POST)
    public ResponseEntity getCommCode(@RequestHeader(name = "Authorization") String authorization,
                                      @RequestBody Map<String, String> param) {

        if(param != null && StringUtils.isNotBlank(param.get("comGrpCd")) && StringUtils.isNotBlank(param.get("occrDt"))) {
            List<ComDto.Code> data = ipmCommonService.getCommList(param);

            if (data != null) {
                return ResponseEntity.ok(new ResultVO(Const.Common.RESULT_CODE.SUCCESS, "", data));
            }
        }

        return ResponseEntity.ok(new ResultVO(Const.Common.RESULT_CODE.FAIL, ""));
    }

    /**
     * 주소 조회
     * @param param
     * @return
     */
    @ApiOperation(
            value = "getAddress",
            notes = "주소 조회"
    )
    @RequestMapping(value = Path.API + "/ipm/getAddress", method = RequestMethod.POST)
    public ResponseEntity getAddress(@RequestHeader(name = "Authorization") String authorization,
                                     @RequestBody List<String> param) {

        if (param != null) {
            List<ComDto.Code> addrList = ipmCommonService.getAddress(param);

            if (addrList != null && !addrList.isEmpty()) {
                return ResponseEntity.ok(new ResultVO(Const.Common.RESULT_CODE.SUCCESS, "", addrList));
            }
        }

        return ResponseEntity.ok(new ResultVO(Const.Common.RESULT_CODE.FAIL, ""));
    }
}
