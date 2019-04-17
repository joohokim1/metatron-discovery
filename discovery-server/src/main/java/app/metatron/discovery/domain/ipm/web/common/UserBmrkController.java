package app.metatron.discovery.domain.ipm.web.common;

import app.metatron.discovery.domain.ipm.common.constant.Const;
import app.metatron.discovery.domain.ipm.common.constant.Path;
import app.metatron.discovery.domain.ipm.common.controller.AbstractController;
import app.metatron.discovery.domain.ipm.common.value.ResultVO;
import app.metatron.discovery.domain.ipm.domain.common.UserBmrkDtlEntity;
import app.metatron.discovery.domain.ipm.domain.common.UserBmrkDto;
import app.metatron.discovery.domain.ipm.service.common.UserBmrkService;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class UserBmrkController extends AbstractController {

    @Autowired
    private UserBmrkService userBmrkService;

    /**
     * 사용자 즐겨찾기 상세 목록 조회
     * @param authorization
     * @param param
     * @return
     */
    @ApiOperation(
            value = "getUserBmrkDtlList",
            notes = "사용자 즐겨찾기 상세 목록 조회"
    )
    @RequestMapping(value = Path.API + "/ipm/getUserBmrkDtlList", method = RequestMethod.POST)
    public ResponseEntity getUserBmrkDtlList(@RequestHeader(name = "Authorization") String authorization,
                                             @RequestBody UserBmrkDtlEntity param) {

        param.setUserId(this.getCurrentUserId());
        if (StringUtils.isNotBlank(param.getUserId()) && StringUtils.isNotBlank(param.getMenuLinkNm())) {

            List<UserBmrkDto.UserBmrkDtlList> data = userBmrkService.getUserBmrkDtlList(param);
            if (data != null) {
                return ResponseEntity.ok(new ResultVO(Const.Common.RESULT_CODE.SUCCESS, "", data));
            }
        }

        return ResponseEntity.ok(new ResultVO(Const.Common.RESULT_CODE.FAIL, ""));
    }

    /**
     * 사용자 즐겨찾기 상세 등록
     * @param authorization
     * @param param
     * @return
     */
    @ApiOperation(
            value = "addUserBmrkDtl",
            notes = "사용자 즐겨찾기 상세 등록"
    )
    @RequestMapping(value = Path.API + "/ipm/addUserBmrkDtl", method = RequestMethod.POST)
    public ResponseEntity addUserBmrkDtl(@RequestHeader(name = "Authorization") String authorization,
                                         @RequestBody UserBmrkDtlEntity param) {

        param.setUserId(this.getCurrentUserId());
        if (StringUtils.isNotBlank(param.getUserId()) && StringUtils.isNotBlank(param.getMenuLinkNm()) &&
                param.getFltrDatVal() != null && !param.getFltrDatVal().isEmpty()) {

            boolean result = userBmrkService.addUserBmrkDtl(param);
            if (result) {
                return ResponseEntity.ok(new ResultVO(Const.Common.RESULT_CODE.SUCCESS, ""));
            }
        }

        return ResponseEntity.ok(new ResultVO(Const.Common.RESULT_CODE.FAIL, ""));
    }

    /**
     * 사용자 즐겨찾기 상세 수정
     * @param authorization
     * @param param
     * @return
     */
    @ApiOperation(
            value = "editUserBmrkDtl",
            notes = "사용자 즐겨찾기 상세 수정"
    )
    @RequestMapping(value = Path.API + "/ipm/editUserBmrkDtl", method = RequestMethod.POST)
    public ResponseEntity editUserBmrkDtl(@RequestHeader(name = "Authorization") String authorization,
                                          @RequestBody UserBmrkDtlEntity param) {

        param.setUserId(this.getCurrentUserId());
        if (StringUtils.isNotBlank(param.getUserId()) && StringUtils.isNotBlank(param.getBmrkUid()) &&
                param.getFltrDatVal() != null && !param.getFltrDatVal().isEmpty()) {

            boolean result = userBmrkService.editUserBmrkDtl(param);
            if (result) {
                return ResponseEntity.ok(new ResultVO(Const.Common.RESULT_CODE.SUCCESS, ""));
            }
        }

        return ResponseEntity.ok(new ResultVO(Const.Common.RESULT_CODE.FAIL, ""));
    }

    /**
     * 사용자 즐겨찾기 상세 삭제
     * @param authorization
     * @param param
     * @return
     */
    @ApiOperation(
            value = "deleteUserBmrkDtl",
            notes = "사용자 즐겨찾기 상세 삭제"
    )
    @RequestMapping(value = Path.API + "/ipm/deleteUserBmrkDtl", method = RequestMethod.POST)
    public ResponseEntity deleteUserBmrkDtl(@RequestHeader(name = "Authorization") String authorization,
                                            @RequestBody UserBmrkDtlEntity param) {

        param.setUserId(this.getCurrentUserId());
        if (StringUtils.isNotBlank(param.getUserId()) && StringUtils.isNotBlank(param.getBmrkUid())) {

            boolean result = userBmrkService.deleteUserBmrkDtl(param);
            if (result) {
                return ResponseEntity.ok(new ResultVO(Const.Common.RESULT_CODE.SUCCESS, ""));
            }
        }

        return ResponseEntity.ok(new ResultVO(Const.Common.RESULT_CODE.FAIL, ""));
    }
}
