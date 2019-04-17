package app.metatron.discovery.domain.ipm.common.code.service;


import app.metatron.discovery.domain.ipm.common.code.domain.CodeDto;
import app.metatron.discovery.domain.ipm.common.code.domain.CodeEntity;
import app.metatron.discovery.domain.ipm.common.code.domain.GroupCodeEntity;
import app.metatron.discovery.domain.ipm.common.code.repository.CodeRepository;
import app.metatron.discovery.domain.ipm.common.code.repository.GroupCodeRepository;
import app.metatron.discovery.domain.ipm.common.service.AbstractGenericService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

/**
 * 공통 코드 서비스
 */
@Service
@Validated
@Transactional
public class CodeService extends AbstractGenericService<CodeEntity, String> {

    @Autowired
    private CodeRepository codeRepository;
	@Autowired
	private GroupCodeRepository groupCodeRepository;

    
    @Autowired
	protected ModelMapper modelMapper;

	@Override
	protected JpaRepository<CodeEntity, String> getRepository() {
		return codeRepository;
	}

	/**
	 * 그룹 아이디로 코드 조회
	 * @param grpCdKey
	 * @return
	 */
	public CodeEntity getByGrpCdKey(String grpCdKey) {
		GroupCodeEntity groupCodeEntity = new GroupCodeEntity();
		groupCodeEntity.setGroupCd(grpCdKey);
		return codeRepository.findByGroupCd(groupCodeEntity);
	}

	/**
	 * 그룹 아이디로 코드 조회
	 * @param grpCdKey
	 * @return
	 */
	public List<CodeEntity> listByGrpCdKey(String grpCdKey){
		GroupCodeEntity groupCodeEntity = groupCodeRepository.findOne(grpCdKey);
		return codeRepository.findByGroupCdOrderByCdOrderAsc(groupCodeEntity);
	};

	/**
	 * 제한된 코드 정보 조회
	 * @param grpCdKey
	 * @return
	 */
	public List<CodeDto.Code> getCodeListWithDto(String grpCdKey) {
		
		if( grpCdKey == null ) {
			return null;
		}
		
//		List<CodeEntity> codeList = listByGrpCdKey(grpCdKey);
		// id 로 조회하는 것으로 변경
		GroupCodeEntity groupCode = groupCodeRepository.findOne(grpCdKey);
		if( groupCode == null ) {
			return null;
		}
		List<CodeEntity> codeList = groupCode.getCode();

		if( codeList == null || codeList.size() < 1) {
			return null;
		}
		
		int count = codeList.size();
		
		List<CodeDto.Code> codeDtoList = new ArrayList<CodeDto.Code>(count);
		for( int i=0 ; i<count ; i++ ) {

			
			CodeEntity code = codeList.get(i);
			codeDtoList.add(modelMapper.map(code, CodeDto.Code.class));
		}
		
		return codeDtoList;
	}

	/**
	 * 제한된 코드 정보 조회
	 * @return
	 */
	public Map<String,List<CodeDto.Code>> getCodeGroupMapWithDto(String... grpCdKeys) {
		if( grpCdKeys == null || grpCdKeys.length < 1) {
			return null;
		}
		
		Map<String, List<CodeDto.Code> > map = new HashMap<String,List<CodeDto.Code>>();
		for (String grpCd : grpCdKeys) {
			map.put(grpCd, getCodeListWithDto( grpCd));
		}
		
		return map;
	}

	/**
	 * 코드 수정
	 * @param codes
	 */
	public void editCodes(List<CodeDto.Code> codes) {
		List<CodeEntity> editCodes = new ArrayList<>();
		if( codes != null && codes.size() > 0 ) {
			GroupCodeEntity groupCode = groupCodeRepository.findOne(codes.get(0).getGroupCd());
			codes.forEach(code -> {
				CodeEntity codeEntity = modelMapper.map(code, CodeEntity.class);
				codeEntity.setGroupCd(groupCode);
				editCodes.add(codeEntity);
			});
			codeRepository.save(editCodes);
		}
	}

}