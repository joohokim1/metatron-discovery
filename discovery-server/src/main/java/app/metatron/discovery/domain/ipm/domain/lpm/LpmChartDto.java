package app.metatron.discovery.domain.ipm.domain.lpm;

import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;

public class LpmChartDto {
    
	@Setter
    @Getter
    @ApiModel("LpmChartDto.Population")
    public static class Population {
		// enb id
		private String EnbId;
		// cell id
		private String cellId;
		//CEI합
		private String totCeiVal;				
        // age
        private String age;
        // geo
        private String geo;
        // ldong_cd
        private String ldongCd;
        // hh
        private String hh;
        // sex
        private String sex;
        // userCnt
        private double userCnt;
        
        private Address address;
        
	}
	
	@Setter
    @Getter
    @ApiModel("LpmChartDto.Address")
    public static class Address {
        // 시도 이름
        private String sidoNm;
        // 시군구 이름
        private String sggNm;
        // 동 이름
        private String dongNm;
        // 시도 코드
        private String sidoCd;
        // 시군구 코드
        private String sggCd;
        // 동 코드
        private String dongCd;        
        // 주소 코드
        private String ldongCd;
	}	
	
}
