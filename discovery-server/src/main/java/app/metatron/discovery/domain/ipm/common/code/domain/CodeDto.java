package app.metatron.discovery.domain.ipm.common.code.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 코드 DTO
 */
public class CodeDto {
	
	@Getter
	@Setter
	@ApiModel("CodeDto.Param")
    public static class Param{

		// 코드 아이디
		private String cd;

		// 그룹코드
		private String groupCd;

		// 코드명 - 한글
		private String nmKr;

		// 코드명 - 영어
		private String nmEn;

		// 코드 설명
		private String cdDesc;

		// 화면 노출 순서
		private Integer cdOrder;
    }

	@Getter
	@Setter
	@ApiModel("CodeDto.Code")
    public static class Code{

		private String id;

		// 코드 아이디
		private String cd;

		// 그룹코드
		private String groupCd;

		// 코드명 - 한글
		private String nmKr;

		// 코드명 - 영어
		private String nmEn;

		// 코드 설명
		private String cdDesc;

		// 화면 노출 순서
		private Integer cdOrder;

	}

	@Getter
	@Setter
	@ApiModel("CodeDto.ID")
	public static class ID{

		// 코드 아이디
		@ApiModelProperty(required = true)
		private String id;

	}
	
}
