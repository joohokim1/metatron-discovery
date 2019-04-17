package app.metatron.discovery.domain.ipm.domain.lpm;

import com.fasterxml.jackson.annotation.JsonIgnore;
import app.metatron.discovery.domain.ipm.service.lpm.LpmUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class LpmDto {
	
	private String fltrNm;
	private String scrnNm;
	private String scrnClNm;
	private String leafYn;
	private String useYn;
	private String layrGrpId;
	private String fltrDelYn;
	private List<LpmDto> fltrList;
	private int fltrListCnt;
	private String subUseYn;
	
	public interface LpmFltrBasList {
		String getFltrNm();
        String getScrnNm();
        String getScrnClNm();
        String getLeafYn();
        String getUseYn();
        String getLayrGrpId();
        String getFltrDelYn();
        String getSubUseYn();
        List<LpmFltrBasList> getFltrList();
        default int getFltrListCnt() {
            return getFltrList().size();
        }
    }

    public interface LpmFltrDtlList {
        String getFltrUnit();
        @JsonIgnore
        String getFltrRmk();
        @JsonIgnore
        String getFltrVal();
        default List<Object> getValues() {
            String fltrVal = getFltrVal();
            String rmk = getFltrRmk();
            if ("QRY".equals(rmk)) {
                ArrayList list = new ArrayList<Object>();
                try {
                    JSONObject obj = (JSONObject) (new JSONParser()).parse(fltrVal);
                    list.add(obj);
                } catch(ParseException e) {
                }
                return list;
            } else {
                if (fltrVal != null) {
                    if (fltrVal.contains("~")) {
                        return Arrays.asList(fltrVal.split("~"));

                    } else if (fltrVal.contains("|")) {
                        String[] arr = fltrVal.split("\\|");
                        List<Object> list = null;
                        if (arr.length != 0) {
                            list = new ArrayList<>();
                            for (String item : arr) {
                                String[] items = LpmUtil.safeArray(item.split(":"), 2);
                                CommDto dto = new CommDto(items[1], items[0]);
                                list.add(dto);
                            }
                        }
                        return list;
                    }
                }
            }
            return null;
        }
        default String getRmk() {
            String rmk = getFltrRmk();
            if (StringUtils.isNotBlank(rmk)) {
                if (!"QRY".equals(rmk)) {
                    String[] arr = rmk.split("\\|");
                    if (arr.length > 1) {
                        rmk = rmk.split("\\|")[1];
                    } else {
                        rmk = null;
                    }
                }
            } else {
                rmk = null;
            }

            return rmk;
        }
        default String getStep() {
            String rmk = getFltrRmk();
            if (StringUtils.isNotBlank(rmk)) {
                if (!"QRY".equals(rmk)) {
                    String[] arr = rmk.split("\\|");
                    int arrSize = arr.length;
                    if (arrSize > 0 ) {
                        rmk = rmk.split("\\|")[0];
                    } else {
                        rmk = null;
                    }
                } else {
                    rmk = null;
                }
            } else {
                rmk = null;
            }

            return rmk;
        }
    }

    public interface LpmFltrBas {
        String getFltrUid();
        String getFltrNm();
        String getDruidNm();
        String getScrnNm();
        String getScrnClNm();
        String getFltrDesc();
        String getFltrDelYn();
        String getSubUseYn();
        List<LpmFltrDtlList> getFltrDtlList();
    }
    
	public String getFltrNm() {
		return fltrNm;
	}

	public void setFltrNm(String fltrNm) {
		this.fltrNm = fltrNm;
	}

	public String getScrnNm() {
		return scrnNm;
	}

	public void setScrnNm(String scrnNm) {
		this.scrnNm = scrnNm;
	}

	public String getScrnClNm() {
		return scrnClNm;
	}

	public void setScrnClNm(String scrnClNm) {
		this.scrnClNm = scrnClNm;
	}

	public String getLeafYn() {
		return leafYn;
	}

	public void setLeafYn(String leafYn) {
		this.leafYn = leafYn;
	}

	public String getUseYn() {
		return useYn;
	}

	public void setUseYn(String useYn) {
		this.useYn = useYn;
	}

	public String getLayrGrpId() {
		return layrGrpId;
	}

	public void setLayrGrpId(String layrGrpId) {
		this.layrGrpId = layrGrpId;
	}

	public List<LpmDto> getFltrList() {
		return fltrList;
	}

	public void setFltrList(List<LpmDto> fltrList) {
		this.fltrList = fltrList;
	}    
	
    public int getFltrListCnt() {
		return fltrListCnt;
	}

	public void setFltrListCnt(int fltrListCnt) {
		this.fltrListCnt = fltrListCnt;
	}
	
	public String getFltrDelYn() {
		return this.fltrDelYn;
	}
	
	public void setFltrDelYn(String fltrDelYn) {
		this.fltrDelYn = fltrDelYn;
	}
	
	public String getSubUseYn() {
		return this.subUseYn;
	}
	
	public void setSubUseYn(String subUseYn) {
		this.subUseYn = subUseYn;
	}
}
