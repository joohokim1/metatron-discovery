package app.metatron.discovery.domain.ipm.common.code.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import org.hibernate.annotations.GenericGenerator;

/**
 * 공통 코드
 */
@Entity
@Table(name = "dt_cm_code")
public class CodeEntity {


	// 코드 아이디
	@Id
	@GeneratedValue(generator = "uuid")
	@GenericGenerator(name = "uuid", strategy = "org.hibernate.id.UUIDGenerator")
	private String id;

	@Column(length = 50)
	private String cd;
	
	// 코드명 - 한글
	@Column(length = 50)
	private String nmKr;
	
	// 코드명 - 영어
	@Column(length = 50)
	private String nmEn;
	
	// 코드 설명
	@Column
	private String cdDesc;
	
	// 화면 노출 순서
	@Column
	private Integer cdOrder;

	@Transient
	private Object extra;

	// 그룹코드
	@ManyToOne(fetch = FetchType.EAGER, optional = false)
	@JoinColumn(name="group_id")
	@JsonIgnore
	private GroupCodeEntity groupCd;

	@JsonProperty
	public Object getExtra() {
		return extra;
	}

	public void setExtra(Object extra) {
		this.extra = extra;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCd() {
		return cd;
	}

	public void setCd(String cd) {
		this.cd = cd;
	}

	public String getNmKr() {
		return nmKr;
	}

	public void setNmKr(String nmKr) {
		this.nmKr = nmKr;
	}

	public String getNmEn() {
		return nmEn;
	}

	public void setNmEn(String nmEn) {
		this.nmEn = nmEn;
	}

	public String getCdDesc() {
		return cdDesc;
	}

	public void setCdDesc(String cdDesc) {
		this.cdDesc = cdDesc;
	}

	public Integer getCdOrder() {
		return cdOrder;
	}

	public void setCdOrder(Integer cdOrder) {
		this.cdOrder = cdOrder;
	}

	public GroupCodeEntity getGroupCd() {
		return groupCd;
	}

	public void setGroupCd(GroupCodeEntity groupCd) {
		this.groupCd = groupCd;
	}
}
