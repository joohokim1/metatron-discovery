package app.metatron.discovery.domain.ipm.domain.lpm;

import io.swagger.annotations.ApiModel;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

public class LpmStyleMstDto {
	
    public interface Style {
        //String getMetatronNm();
        List<Style> getStyleInfo();
        default int getStyleInfoCnt(){
            return getStyleInfo().size();
        };
    }
	
    @Setter
    @Getter
    @ApiModel("LpmStyleMstDto.PARAM")
    public static class PARAM {
    	private String metatronNm;
        private String layerId;
        private String stylId;
        private String stylDs;
        private List<LpmStyleMstEntity> styleInfo;
    }

}