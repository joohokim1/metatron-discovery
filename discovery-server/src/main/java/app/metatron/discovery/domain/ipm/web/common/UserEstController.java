package app.metatron.discovery.domain.ipm.web.common;

import app.metatron.discovery.domain.ipm.common.constant.Const;
import app.metatron.discovery.domain.ipm.common.constant.Path;
import app.metatron.discovery.domain.ipm.common.controller.AbstractController;
import app.metatron.discovery.domain.ipm.common.value.ResultVO;
import app.metatron.discovery.domain.ipm.domain.common.UserEstDtlEntity;
import app.metatron.discovery.domain.ipm.domain.common.UserEstDto;
import app.metatron.discovery.domain.ipm.service.common.UserEstService;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserEstController extends AbstractController {

    @Autowired
    private UserEstService userEstService;

    /**
     * 사용자 설정 상세 조회
     * @param authorization
     * @param param
     * @return
     */
    @ApiOperation(
            value = "getUserEstDtl",
            notes = "사용자 설정 상세 조회"
    )
    @RequestMapping(value = Path.API + "/ipm/getUserEstDtl", method = RequestMethod.POST)
    public ResponseEntity getUserEstDtl(@RequestHeader(name = "Authorization") String authorization,
                                        @RequestBody UserEstDtlEntity param) {

        String userId = this.getCurrentUserId();
        param.setUserId(userId);

        if (StringUtils.isNotBlank(param.getUserId()) && StringUtils.isNotBlank(param.getMenuLinkNm())) {

            // userId와 menuLinkNm 값으로 차트 설정값을 가져온다.
            UserEstDtlEntity data = userEstService.getUserEstDtl(param);
            if (data != null) {
                UserEstDto.UserEstDtl dto = new UserEstDto.UserEstDtl() {
                    @Override
                    public String getEstVal() {
                        return data.getEstVal();
                    }
                };

                return ResponseEntity.ok(new ResultVO(Const.Common.RESULT_CODE.SUCCESS, "", dto));
            }
        }

        return ResponseEntity.ok(new ResultVO(Const.Common.RESULT_CODE.FAIL, ""));
    }

    /**
     * 사용자 설정 상세 수정
     * @param authorization
     * @param param
     * @return
     */
    @ApiOperation(
            value = "editUserEstDtl",
            notes = "사용자 설정 상세 수정"
    )
    @RequestMapping(value = Path.API + "/ipm/editUserEstDtl", method = RequestMethod.POST)
    public ResponseEntity editUserEstDtl(@RequestHeader(name = "Authorization") String authorization,
                                         @RequestBody UserEstDtlEntity param) {

        String userId = this.getCurrentUserId();
        param.setUserId(userId);

        if (StringUtils.isNotBlank(param.getMenuLinkNm())) {

            boolean result = userEstService.editUserEstDtl(param);
            if (result) {
                return ResponseEntity.ok(new ResultVO(Const.Common.RESULT_CODE.SUCCESS, ""));
            }
        }

        return ResponseEntity.ok(new ResultVO(Const.Common.RESULT_CODE.FAIL, ""));
    }
}
