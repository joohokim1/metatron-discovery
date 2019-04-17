package app.metatron.discovery.domain.ipm.web.lpm;

import app.metatron.discovery.domain.ipm.common.constant.Const;
import app.metatron.discovery.domain.ipm.common.constant.Path;
import app.metatron.discovery.domain.ipm.common.controller.AbstractController;
import app.metatron.discovery.domain.ipm.common.value.ResultVO;
import app.metatron.discovery.domain.ipm.domain.lpm.LpmDto;
import app.metatron.discovery.domain.ipm.domain.lpm.LpmFltrBasEntity;
import app.metatron.discovery.domain.ipm.domain.lpm.LpmLayerMstEntity;
import app.metatron.discovery.domain.ipm.domain.lpm.LpmLayrGrpMstEntity;
import app.metatron.discovery.domain.ipm.domain.lpm.LpmStyleMstEntity;
import app.metatron.discovery.domain.ipm.service.lpm.LpmDruidService;
import app.metatron.discovery.domain.ipm.service.lpm.LpmLayerMstService;
import app.metatron.discovery.domain.ipm.service.lpm.LpmLayrGrpMstService;
import app.metatron.discovery.domain.ipm.service.lpm.LpmService;
import app.metatron.discovery.domain.ipm.service.lpm.LpmStyleMstService;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class LpmController extends AbstractController {

    @Autowired
    private LpmLayerMstService lpmLayerMstService;
    
    @Autowired
    private LpmLayrGrpMstService lpmGrpLayrMstService;
    
    @Autowired
    private LpmStyleMstService lpmStyleMstService;    
    
    @Autowired
    private LpmDruidService lpmDruidService;    
    
    @Autowired
    private LpmService lpmService;
    
    @ApiOperation(
            value = "getLpmFltrBasList",
            notes = "lpm 필터 기본 목록 조회"
    )
    @RequestMapping(value = Path.API + "/ipm/lpm/getLpmFltrBasList", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseEntity getLpmFltrBasList(@RequestBody JSONObject params) { //@RequestBody String layrGrpId
    	
//    	List<LpmDto> data = lpmService.getLpmFltrBasList(layrGrpId.get("layrGrpId").toString());
    	List<LpmDto> data = lpmService.getLpmFltrBasList(params);
    	if (data != null && !data.isEmpty()) {
    		return ResponseEntity.ok(new ResultVO(Const.Common.RESULT_CODE.SUCCESS, "", data));
    	} else {
    		return ResponseEntity.ok(new ResultVO(Const.Common.RESULT_CODE.FAIL, ""));
    	}
    }
    
    @ApiOperation(
    		value = "getLpmLayrGrpList",
    		notes = "lpm 그룹 레이어 목록 조회"
    		
	)
    
    @RequestMapping(value = Path.API + "/ipm/lpm/getLpmLayrGrpList", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseEntity getLpmLayrGrpList() 
    {	
    	List<LpmLayrGrpMstEntity> lpmLayerGrpMstList = lpmGrpLayrMstService.getLpmLayrGrpList();
    	
    	if (lpmLayerGrpMstList != null && !lpmLayerGrpMstList.isEmpty()) 
    	{
    		return ResponseEntity.ok(new ResultVO(Const.Common.RESULT_CODE.SUCCESS, "", lpmLayerGrpMstList));
    	} 
    	else 
    	{
    		return ResponseEntity.ok(new ResultVO(Const.Common.RESULT_CODE.FAIL, ""));
    	}
    }
    

    @ApiOperation(
            value = "getLpmFltrBas",
            notes = "lpm 필터 기본 조회"
    )
    @RequestMapping(value = Path.API + "/ipm/lpm/getLpmFltrBas", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseEntity getLpmFltrBas(@RequestBody LpmFltrBasEntity param) {
    	if (param.getFltrNm() != null && !param.getFltrNm().isEmpty()) {

            LpmDto.LpmFltrBas data = lpmService.getLpmFltrBas(param.getFltrNm());
            if (data != null) {
                return ResponseEntity.ok(new ResultVO(Const.Common.RESULT_CODE.SUCCESS, "", data));
            } else {
                return ResponseEntity.ok(new ResultVO(Const.Common.RESULT_CODE.FAIL, ""));
            }
        }

        return ResponseEntity.ok(new ResultVO(Const.Common.RESULT_CODE.FAIL, ""));
    }    
    
    @ApiOperation(
            value = "Get Layer Info",
            notes = "레이어 정보 조회"
    )
    @RequestMapping(value = Path.API + "/ipm/lpm/getLayerInfoList", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseEntity getLayerInfo() {

        List<LpmLayerMstEntity> data = lpmLayerMstService.getLayerInfo();
        ResultVO resultVO;

        if (!data.isEmpty()) {
            resultVO = new ResultVO(Const.Common.RESULT_CODE.SUCCESS, "", data);
        } else {
            resultVO = new ResultVO(Const.Common.RESULT_CODE.FAIL, "");
        }

        return ResponseEntity.ok(resultVO);
    }
    
    @ApiOperation(
            value = "getLayerInfoByLayrGrpId",
            notes = "레이어 그룹ID를 통한 분석레이어 목록 조회"
    )
    @RequestMapping(value = Path.API + "/ipm/lpm/getLayerInfoByLayrGrpId", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseEntity getLayerInfoByLayrGrpId(@RequestBody String layrGrpId ) {

        List<LpmLayerMstEntity> data = lpmLayerMstService.getLayerInfoByLayrGrpId(layrGrpId);
        ResultVO resultVO;

        if (!data.isEmpty()) {
            resultVO = new ResultVO(Const.Common.RESULT_CODE.SUCCESS, "", data);
        } else {
            resultVO = new ResultVO(Const.Common.RESULT_CODE.FAIL, "");
        }

        return ResponseEntity.ok(resultVO);
    } 
    
    @ApiOperation(
            value = "Get Layer Style Info",
            notes = "레이어 스타일 정보 조회"
    )    
    @RequestMapping(value = Path.API + "/ipm/lpm/getLayerStyleList", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseEntity getLayerStyleList() {
    	
    	List<LpmStyleMstEntity> sytleData = lpmStyleMstService.getStyleInfo();
    	
        List<LpmLayerMstEntity> layerData = lpmLayerMstService.getLayerInfo();

        int layerId = 0;
        int subLayerId = 0;
        String layerName = null;
        String geometryType = null;
        
        JSONObject typeObject = null;
        JSONArray typeArray = null;
        JSONObject layerInfo = null;
        JSONArray layerArray = new JSONArray();
        
        JSONArray styleArray = null;
        JSONParser parser = new JSONParser();
        
        for(int i=0; i<layerData.size(); i++) {
        	layerId = Integer.parseInt(layerData.get(i).getLayrId());
        	layerName = layerData.get(i).getName();
        	geometryType = layerData.get(i).getGeometryType();
        	
        	styleArray = new JSONArray();
        	for(int j=0; j<sytleData.size(); j++) {
        		subLayerId = Integer.parseInt(sytleData.get(j).getLayrId());
        		
        		if(layerId == subLayerId) {
        			try {
						JSONObject json = (JSONObject) parser.parse(sytleData.get(j).getStyDs());
						json.put("id", sytleData.get(j).getStyId());
						styleArray.add(json);
					} catch (ParseException e) {
						e.printStackTrace();
					}
        			
        		}
        	}
        	
        	typeObject = new JSONObject();
        	typeArray = new JSONArray();
        	
        	if(geometryType.equals("point")) {
        		typeObject.put("POINT", styleArray);
        	} else if(geometryType.equals("line")) {
        		typeObject.put("LINE", styleArray);
        	} else if(geometryType.equals("polygon")) {
        		typeObject.put("POLYGON", styleArray);
        	} else {
        		typeObject.put("TEXT", styleArray);       	
        	}
        	typeArray.add(typeObject);
        	
        	layerInfo = new JSONObject();
        	layerInfo.put("layerName", layerName);
        	layerInfo.put("rules", typeArray);
        	layerArray.add(layerInfo);
        }
        
        return ResponseEntity.ok(layerArray);
    }         
    
    @ApiOperation(
            value = "Get Chart Population List",
            notes = "차트 유동인구 조회"
    )
    @RequestMapping(value = Path.API + "/ipm/lpm/getChartPopulation", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseEntity getChartPopulation(@RequestBody JSONObject param) {
    	
        Object result = null;
        ResultVO resultVO;
        
        result = lpmDruidService.getChartPopulData(param);
        
        if (result != null) {
            resultVO = new ResultVO(Const.Common.RESULT_CODE.SUCCESS, "", result);
        } else {
            resultVO = new ResultVO(Const.Common.RESULT_CODE.FAIL, "");
        }

        return ResponseEntity.ok(resultVO);
    }     
    
    @ApiOperation(
            value = "Get Grid Population List",
            notes = "그리드 유동인구 조회"
    )
    @RequestMapping(value = Path.API + "/ipm/lpm/getGridPopulation", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseEntity getGridPopulation(@RequestBody JSONObject param) {
    	
        Object result = null;
        ResultVO resultVO;
        
        result = lpmDruidService.getGridPopulData(param);
        
        if (result != null) {
            resultVO = new ResultVO(Const.Common.RESULT_CODE.SUCCESS, "", result);
        } else {
            resultVO = new ResultVO(Const.Common.RESULT_CODE.FAIL, "");
        }

        return ResponseEntity.ok(resultVO);
    }     
    
    @ApiOperation(
            value = "Get Grid Population List",
            notes = "그리드 유동인구 조회"
    )
    @RequestMapping(value = Path.API + "/ipm/lpm/getAddrCenterPoint", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseEntity getAddrCenterPoint(@RequestBody JSONObject param) {
    	
        Object result = null;
        ResultVO resultVO;
        
        result = lpmDruidService.getAddrCenterPoint(param);
        
        if (result != null) {
            resultVO = new ResultVO(Const.Common.RESULT_CODE.SUCCESS, "", result);
        } else {
            resultVO = new ResultVO(Const.Common.RESULT_CODE.FAIL, "");
        }

        return ResponseEntity.ok(resultVO);
    }    
    
    @ApiOperation(
            value = "Get Grid Population List",
            notes = "그리드 유동인구 조회"
    )
    @RequestMapping(value = Path.API + "/ipm/lpm/getXdrRowData", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseEntity getXdrRowData(@RequestBody JSONObject param) {
    	
        Object result = null;
        ResultVO resultVO;
        
        result = lpmDruidService.getXdrRowData(param);
        
        if (result != null) {
            resultVO = new ResultVO(Const.Common.RESULT_CODE.SUCCESS, "", result);
        } else {
            resultVO = new ResultVO(Const.Common.RESULT_CODE.FAIL, "");
        }

        return ResponseEntity.ok(resultVO);
    }     
    
    /**
     * lpm EXCEL 다운로드
     * @return
     */
    @ApiOperation(
            value = "getGridExcel",
            notes = "EXCEL 다운로드"
    )
    @RequestMapping(value = Path.API + "/ipm/lpm/getLpmGridExcel", method = RequestMethod.POST)
    public void getEpmChartsExcel(@RequestHeader(name = "Authorization") String authorization,
                          @RequestBody JSONObject param, HttpServletResponse response) {

        if (param != null && !param.isEmpty()) {
        	lpmDruidService.getGridListExcel(param, "10000", response);
        }
    }    
}