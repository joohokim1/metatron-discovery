package app.metatron.discovery.domain.ipm.service.epm;

import app.metatron.discovery.domain.ipm.domain.common.ComDto;
import app.metatron.discovery.domain.ipm.domain.epm.EpmDto;
import app.metatron.discovery.domain.ipm.domain.epm.EpmFltrBasEntity;
import app.metatron.discovery.domain.ipm.repository.epm.EpmClBasRepository;
import app.metatron.discovery.domain.ipm.repository.epm.EpmEqpRepository;
import app.metatron.discovery.domain.ipm.repository.epm.EpmFltrBasRepository;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class EpmService {

    /**
     * org : SKT
     */
    private final String SKT = "SKT";

    /**
     * org : ONS
     */
    private final String ONS = "ONS";

    /**
     * vendor DB 코드 : 공통
     */
    private final String COM = "COM";

    /**
     * vendor : 삼성
     */
    @Value("${polaris.epm.vendor-ss}")
    private String[] vendorSs;

    /**
     * vendor : NSN
     */
    @Value("${polaris.epm.vendor-nsn}")
    private String[] vendorNsn;

    /**
     * vendor : ELG
     */
    @Value("${polaris.epm.vendor-elg}")
    private String[] vendorElg;

    /**
     * vendor DB 코드 : 삼성
     */
    @Value("${polaris.epm.vendor-ss-cd}")
    private String vendorSsCd;

    /**
     * vendor DB 코드 : NSN
     */
    @Value("${polaris.epm.vendor-nsn-cd}")
    private String vendorNsnCd;

    /**
     * vendor DB 코드 : ELG
     */
    @Value("${polaris.epm.vendor-elg-cd}")
    private String vendorElgCd;

    @Autowired
    private EpmClBasRepository epmClBasRepository;

    @Autowired
    private EpmFltrBasRepository epmFltrBasRepository;

    @Autowired
    private EpmEqpRepository epmEqpRepository;

    /**
     * epm 분류 기본 목록 조회
     * @return
     */
    public List<EpmDto.EpmClBasList> getEpmClBasList() {
        try {
            return epmClBasRepository.findByClGrpUidIsNullOrderByClTurn();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * epm 필터 기본 목록 조회
     * @param clUid
     * @return
     */
    public List<EpmDto.EpmFltrBasList> getEpmFltrBasList(String clUid) {
        try {
            EpmDto.EpmClBas epmClBas = epmClBasRepository.findByClUid(clUid);
            return epmClBas.getFltrList();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * epm 필터 기본 조회
     * @param fltrUid
     * @return
     */
    public EpmDto.EpmFltrBas getEpmFltrBas(String fltrUid) {
        try {
            return epmFltrBasRepository.findByFltrUid(fltrUid);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * epm 조직 목록 조회
     * @param info
     * @return
     */
    public List<ComDto.Code> getEpmOrgList(EpmDto.Info info) {
        try {
            List<Map<String, Object>> data = null;
            String leaf = "N";
            String vendorCd = getVendorCd(info.getVendor());
            List<String> orgList = info.getOrgCd();
            int orgSize = orgList.size();
            String org = orgList.get(0);
            if (SKT.equals(org)) {
                switch (orgSize) {
                    case 1: // 본부
                        data = epmEqpRepository.getSktHdofcList(vendorCd);
                        break;
                    case 2: // 팀
                        data = epmEqpRepository.getSktTeamList(vendorCd, orgList.get(1));
                        break;
                    case 3: // 세부 팀
                        data = epmEqpRepository.getSktDetailTeamList(vendorCd, orgList.get(1), orgList.get(2));
                        leaf = "Y";
                        break;
                }
            } else if (ONS.equals(org)) {
                switch (orgSize) {
                    case 1: // 본부
                        data = epmEqpRepository.getOnsHdofcList(vendorCd);
                        break;
                    case 2: // 팀
                        data = epmEqpRepository.getOnsTeamList(vendorCd, orgList.get(1));
                        leaf = "Y";
                        break;
                }
            }

            List<ComDto.Code> result = new ArrayList<>();
            if (data != null && !data.isEmpty()) {
                for (Map map : data) {
                    result.add(new ComDto.Code(map.get("code"), map.get("name"), null, leaf));
                }
            }

            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * epm 주소 목록 조회
     * @param info
     * @return
     */
    public List<ComDto.Code> getEpmAddrList(EpmDto.Info info) {
        try {
            List<Map<String, Object>> data = null;
            String leaf = "N";
            String vendorCd = getVendorCd(info.getVendor());
            List<String> addrList = info.getAddrCd();
            int addrSize = addrList.size();
            switch(addrSize) {
                case 0: // 시도
                    data = epmEqpRepository.getSidoList(vendorCd);
                    break;
                case 1: // 시군구
                    data = epmEqpRepository.getSggList(vendorCd, addrList.get(0));
                    break;
                case 2: // 읍면동
                    data = epmEqpRepository.getEmdList(vendorCd, addrList.get(1));
                    leaf = "Y";
                    break;
            }

            List<ComDto.Code> result = new ArrayList<>();
            if (data != null && !data.isEmpty()) {
                for (Map map : data) {
                    result.add(new ComDto.Code(map.get("code"), map.get("name"), null, leaf));
                }
            }

            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * epm EMS 목록 조회
     * @param info
     * @return
     */
    public List<ComDto.Code> getEpmEmsList(EpmDto.Info info) {
        try {
            List<Map<String, Object>> data = null;
            String leaf = "Y";
            String vendorCd = getVendorCd(info.getVendor());
            data = epmEqpRepository.getEmsList(vendorCd);

            List<ComDto.Code> result = new ArrayList<>();
            if (data != null && !data.isEmpty()) {
                for (Map map : data) {
                    result.add(new ComDto.Code(map.get("code"), map.get("name"), null, leaf));
                }
            }

            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * epm ENB 목록 조회
     * @param info
     * @return
     */
    public List<ComDto.Code> getEpmEnbList(EpmDto.Info info) {
        try {
            List<Map<String, Object>> data = null;
            String vendorCd = getVendorCd(info.getVendor());

            // 조직으로 필터
            List<String> orgList = info.getOrgCd();
            if (orgList != null && !orgList.isEmpty()) {
                int orgSize = orgList.size();
                String org = orgList.get(0);
                if (SKT.equals(org)) {
                    switch (orgSize) {
                        case 2: // SKT 본부 선택
                            data = epmEqpRepository.getSktEnbList(vendorCd, orgList.get(1), null, null);
                            break;
                        case 3: // SKT 본부, 팀 선택
                            data = epmEqpRepository.getSktEnbList(vendorCd, orgList.get(1), orgList.get(2), null);
                            break;
                        case 4: // SKT 본부, 팀, 세부팀 선택
                            data = epmEqpRepository.getSktEnbList(vendorCd, orgList.get(1), orgList.get(2), orgList.get(3));
                            break;
                    }
                } else if (ONS.equals(org)) {
                    switch (orgSize) {
                        case 2: // ONS 본부 선택
                            data = epmEqpRepository.getOnsEnbList(vendorCd, orgList.get(1), null);
                            break;
                        case 3: // ONS 본부, 팀 선택
                            data = epmEqpRepository.getOnsEnbList(vendorCd, orgList.get(1), orgList.get(2));
                            break;
                    }
                }
            }

            // 주소로 필터
            List<String> addrList = info.getAddrCd();
            if (addrList != null && !addrList.isEmpty()) {
                int addrSize = addrList.size();
                switch (addrSize) {
                    case 1: // 시도
                        data = epmEqpRepository.getSidoEnbList(vendorCd, addrList.get(0));
                        break;
                    case 2: // 시군구
                        data = epmEqpRepository.getSggEnbList(vendorCd, addrList.get(1));
                        break;
                    case 3: // 읍면동
                        data = epmEqpRepository.getEmdEnbList(vendorCd, addrList.get(2));
                        break;
                }
            }

            // EMS로 필터
            List<String> emsList = info.getEmsCd();
            if (emsList != null && !emsList.isEmpty()) {
                data = epmEqpRepository.getEmsEnbList(vendorCd, emsList.get(0));
            }

            List<ComDto.Code> result = new ArrayList<>();
            if (data != null && !data.isEmpty()) {
                for (Map map : data) {
                    result.add(new ComDto.Code(map.get("code"), map.get("name")));
                }
            }

            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * epm 국사 목록 조회
     * @param info
     * @return
     */
    public List<ComDto.Code> getEpmMtsoList(EpmDto.Info info) {
        try {
            List<Map<String, Object>> data = null;
            String vendorCd = getVendorCd(info.getVendor());

            // 조직으로 필터
            List<String> orgList = info.getOrgCd();
            if (orgList != null && !orgList.isEmpty()) {
                int orgSize = orgList.size();
                String org = orgList.get(0);
                if (SKT.equals(org)) {
                    switch (orgSize) {
                        case 2: // SKT 본부 선택
                            data = epmEqpRepository.getSktMtsoList(vendorCd, orgList.get(1), null, null);
                            break;
                        case 3: // SKT 본부, 팀 선택
                            data = epmEqpRepository.getSktMtsoList(vendorCd, orgList.get(1), orgList.get(2), null);
                            break;
                        case 4: // SKT 본부, 팀, 세부팀 선택
                            data = epmEqpRepository.getSktMtsoList(vendorCd, orgList.get(1), orgList.get(2), orgList.get(3));
                            break;
                    }
                } else if (ONS.equals(org)) {
                    switch (orgSize) {
                        case 2: // ONS 본부 선택
                            data = epmEqpRepository.getOnsMtsoList(vendorCd, orgList.get(1), null);
                            break;
                        case 3: // ONS 본부, 팀 선택
                            data = epmEqpRepository.getOnsMtsoList(vendorCd, orgList.get(1), orgList.get(2));
                            break;
                    }
                }
            }

            // 주소로 필터
            List<String> addrList = info.getAddrCd();
            if (addrList != null && !addrList.isEmpty()) {
                int addrSize = addrList.size();
                switch (addrSize) {
                    case 1: // 시도
                        data = epmEqpRepository.getSidoMtsoList(vendorCd, addrList.get(0));
                        break;
                    case 2: // 시군구
                        data = epmEqpRepository.getSggMtsoList(vendorCd, addrList.get(1));
                        break;
                    case 3: // 읍면동
                        data = epmEqpRepository.getEmdMtsoList(vendorCd, addrList.get(2));
                        break;
                }
            }

            // 국사유형으로 필터
            String mtsoTypCd = info.getMtsoTypCd();
            if (mtsoTypCd != null) {
                if (mtsoTypCd.isEmpty()) {
                    data = epmEqpRepository.getTypMtsoList(vendorCd, null);

                } else {
                    data = epmEqpRepository.getTypMtsoList(vendorCd, mtsoTypCd);
                }
            }

            List<ComDto.Code> result = new ArrayList<>();
            if (data != null && !data.isEmpty()) {
                for (Map map : data) {
                    result.add(new ComDto.Code(map.get("code"), map.get("name")));
                }
            }

            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * vendor 코드 조회
     * @param vendor
     * @return
     */
    private String getVendorCd(String vendor) {

        String vendorCd = null;
        if (Arrays.asList(vendorSs).contains(vendor)) {
            vendorCd = vendorSsCd; // 삼성

        } else if (Arrays.asList(vendorNsn).contains(vendor)) {
            vendorCd = vendorNsnCd; // NSN

        } else if (Arrays.asList(vendorElg).contains(vendor)) {
            vendorCd = vendorElgCd; // ELG
        }

        return vendorCd;
    }

    /**
     * grid 목록 조회
     * @param vendor
     * @return
     */
    public List<EpmFltrBasEntity> getGridList(String vendor) {
        String vendorCd = getVendorCd(vendor);
        if (vendorCd == null) {
            vendorCd = COM;
        }
        return epmFltrBasRepository.getGridList(vendorCd);
    }
}
