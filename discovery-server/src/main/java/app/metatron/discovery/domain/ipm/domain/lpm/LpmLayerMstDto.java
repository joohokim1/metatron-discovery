package app.metatron.discovery.domain.ipm.domain.lpm;

import io.swagger.annotations.ApiModel;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

public class LpmLayerMstDto {

    public interface Layer {
    	String getLayerId();
        String getLayerNm();
        String getLayerGroupId();
        String getOverlap();
        List<Layer> getLayerTest();
        default int getLayerTestCnt(){
            return getLayerTest().size();
        };
    }
    
    @Setter
    @Getter
    @ApiModel("LpmLayerMstDto.PARAM")
    public static class PARAM {
        private String layerId;
        private String layerNm;
        private String overlap;
        private String layerGroupId;        
        private String selectYn;
        private List<LpmLayerMstEntity> layerTest;
    }
    
    /*
    public interface Test {
    	String getMetatronNm();
    	String getLayerId();
    	String getStylId();
    	String getStylDs();
    } 
    */    
}
