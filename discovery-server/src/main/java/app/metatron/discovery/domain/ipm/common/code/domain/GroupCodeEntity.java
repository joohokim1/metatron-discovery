package app.metatron.discovery.domain.ipm.common.code.domain;

import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

/**
 * 코드 그룹
 */
@Getter
@Setter
@Entity
@Table(name = "dt_cm_group_code")
public class GroupCodeEntity {

	@Id
	@GeneratedValue(generator = "uuid")
	@GenericGenerator(name = "uuid", strategy = "org.hibernate.id.UUIDGenerator")
	private String id;

	@Column(length = 50)
	private String groupCd;

	// 그룹코드명 = 한글
	@Column(length = 50)
	private String groupNmKr;
	
	// 그룹코드명 - 영어
	@Column(length = 50)
	private String groupNmEn;
	
	// 그룹코드 설명
	@Column
	private String groupDesc;

	@OneToMany(mappedBy = "groupCd", fetch = FetchType.LAZY)
	@OrderBy(value = "cdOrder ASC")
	private List<CodeEntity> code;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getGroupCd() {
		return groupCd;
	}

	public void setGroupCd(String groupCd) {
		this.groupCd = groupCd;
	}

	public String getGroupNmKr() {
		return groupNmKr;
	}

	public void setGroupNmKr(String groupNmKr) {
		this.groupNmKr = groupNmKr;
	}

	public String getGroupNmEn() {
		return groupNmEn;
	}

	public void setGroupNmEn(String groupNmEn) {
		this.groupNmEn = groupNmEn;
	}

	public String getGroupDesc() {
		return groupDesc;
	}

	public void setGroupDesc(String groupDesc) {
		this.groupDesc = groupDesc;
	}

	public List<CodeEntity> getCode() {
		return code;
	}

	public void setCode(List<CodeEntity> code) {
		this.code = code;
	}
}
