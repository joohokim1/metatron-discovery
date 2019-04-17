package app.metatron.discovery.domain.ipm.service.lpm;

import app.metatron.discovery.domain.ipm.domain.lpm.LpmDto;
import app.metatron.discovery.domain.ipm.repository.lpm.LpmFltrBasRepository;
import java.util.ArrayList;
import java.util.List;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class LpmService {
    @Autowired
    private LpmFltrBasRepository lpmFltrBasRepository;

    public List<LpmDto> getLpmFltrBasList(JSONObject params) {
    	
    	String layrGrpId = params.get("layrGrpId").toString();
    	boolean isSubUseYn = (boolean) params.get("subUseYn");
    	
    	if(layrGrpId == null || layrGrpId.isEmpty() || layrGrpId.length() < 1) {
    		return null;
    	}
    	
    	List<LpmDto> result = new ArrayList<>();
    	LpmDto grpFltr = null;
    	
    	List<LpmDto.LpmFltrBasList> list = lpmFltrBasRepository.findByFltrGrpUidIsNullOrderByFltrTurn();
    	
    	for (LpmDto.LpmFltrBasList item : list) {
    		//그룹 필터목록 비교
    		if(item.getLayrGrpId().indexOf(layrGrpId) > -1) {
    			grpFltr = new LpmDto();
    			
    			grpFltr.setFltrListCnt(item.getFltrListCnt());
    			grpFltr.setFltrNm(item.getFltrNm());
    			grpFltr.setLayrGrpId(item.getLayrGrpId());
    			grpFltr.setLeafYn(item.getLeafYn());
    			grpFltr.setScrnClNm(item.getScrnClNm());
    			grpFltr.setScrnNm(item.getScrnNm());
    			grpFltr.setUseYn(item.getUseYn());
    			grpFltr.setSubUseYn(item.getSubUseYn());
    			
    			//하위 필터목록 비교
    			List<LpmDto> resultFltrList = new ArrayList<>();
    			LpmDto resultFltr = null;
    			if(item.getFltrListCnt() > 0) {
    				List<LpmDto.LpmFltrBasList> fltrArr = item.getFltrList();
    				for(LpmDto.LpmFltrBasList fltr : fltrArr) {
    					if(fltr.getLayrGrpId().indexOf(layrGrpId) > -1) {
    						resultFltr = new LpmDto();
    						
    						if ( isSubUseYn ) {
    							if ( fltr.getSubUseYn().equalsIgnoreCase("Y") ) {
    								resultFltr.setFltrListCnt(fltr.getFltrListCnt());
            						resultFltr.setFltrNm(fltr.getFltrNm());
            						resultFltr.setLayrGrpId(fltr.getLayrGrpId());
            						resultFltr.setLeafYn(fltr.getLeafYn());
            						resultFltr.setScrnClNm(fltr.getScrnClNm());
            						resultFltr.setScrnNm(fltr.getScrnNm());
            						resultFltr.setUseYn(fltr.getUseYn());  
            						resultFltr.setFltrDelYn(fltr.getFltrDelYn());
            						resultFltr.setSubUseYn(fltr.getSubUseYn());
            						resultFltrList.add(resultFltr);
    							}
    						} else {
    							resultFltr.setFltrListCnt(fltr.getFltrListCnt());
        						resultFltr.setFltrNm(fltr.getFltrNm());
        						resultFltr.setLayrGrpId(fltr.getLayrGrpId());
        						resultFltr.setLeafYn(fltr.getLeafYn());
        						resultFltr.setScrnClNm(fltr.getScrnClNm());
        						resultFltr.setScrnNm(fltr.getScrnNm());
        						resultFltr.setUseYn(fltr.getUseYn());  
        						resultFltr.setFltrDelYn(fltr.getFltrDelYn());
        						resultFltr.setSubUseYn(fltr.getSubUseYn());
        						resultFltrList.add(resultFltr);
    						}
    					}
    				}
    				grpFltr.setFltrListCnt(resultFltrList.size());
    				grpFltr.setFltrList(resultFltrList);
    			}
    			result.add(grpFltr);
    		}
    		
    	}
    	
    	return result;
    }

    public LpmDto.LpmFltrBas getLpmFltrBas(String fltrNm) {
        return lpmFltrBasRepository.findByFltrNm(fltrNm);
    }
}
