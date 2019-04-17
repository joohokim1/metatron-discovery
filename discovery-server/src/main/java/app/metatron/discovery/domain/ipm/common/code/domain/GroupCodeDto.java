package app.metatron.discovery.domain.ipm.common.code.domain;

import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

/**
 * 코드 그룹 DTO
 */
public class GroupCodeDto {

	@Getter
	@Setter
	@ApiModel("GroupCodeDto.CREATE")
	public static class CREATE{
		// 그룹코드 키
		@NonNull
		private String groupCd;

		// 그룹코드명 = 한글
		@NonNull
		private String groupNmKr;

		// 그룹코드명 - 영어
		@NonNull
		private String groupNmEn;

		// 그룹코드 설명
		private String groupDesc;

	}

	@Getter
	@Setter
	@ApiModel("GroupCodeDto.UPDATE")
	public static class UPDATE{
		// 그룹코드 키
		private String id;
		// 그룹코드 키
		private String groupCd;

		// 그룹코드명 = 한글
		private String groupNmKr;

		// 그룹코드명 - 영어
		private String groupNmEn;

		// 그룹코드 설명
		private String groupDesc;

	}

}
