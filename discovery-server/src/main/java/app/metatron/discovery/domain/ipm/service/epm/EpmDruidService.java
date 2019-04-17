package app.metatron.discovery.domain.ipm.service.epm;

import app.metatron.discovery.domain.ipm.common.util.excel.ExcelGenHelper;
import app.metatron.discovery.domain.ipm.domain.common.ComDto;
import app.metatron.discovery.domain.ipm.domain.common.UserBmrkDto;
import app.metatron.discovery.domain.ipm.domain.epm.EpmDto;
import app.metatron.discovery.domain.ipm.domain.epm.EpmFltrBasEntity;
import app.metatron.discovery.domain.ipm.service.common.DruidService;
import app.metatron.discovery.domain.ipm.service.common.IpmCommonService;
import io.druid.data.input.MapBasedRow;
import io.druid.data.input.Row;
import io.druid.java.util.common.granularity.Granularities;
import io.druid.js.JavaScriptConfig;
import io.druid.query.Druids;
import io.druid.query.Result;
import io.druid.query.aggregation.*;
import io.druid.query.aggregation.cardinality.CardinalityAggregatorFactory;
import io.druid.query.aggregation.post.JavaScriptPostAggregator;
import io.druid.query.dimension.DefaultDimensionSpec;
import io.druid.query.dimension.DimensionSpec;
import io.druid.query.dimension.ExtractionDimensionSpec;
import io.druid.query.extraction.JavaScriptExtractionFn;
import io.druid.query.filter.*;
import io.druid.query.groupby.GroupByQuery;
import io.druid.query.select.EventHolder;
import io.druid.query.select.PagingSpec;
import io.druid.query.select.SelectResultValue;
import io.druid.segment.column.ValueType;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EpmDruidService extends DruidService {

    @Value("${polaris.ipm.druid-epm-pool}")
    private Integer pool;
    protected Integer setPool() {
        return pool;
    }

    @PostConstruct
    private void init() {
        initClient();
    }

    /**
     * equipment : eNB
     */
    @Value("${polaris.epm.equipment-enb}")
    private String[] equipmentEnb;

    /**
     * equipment : Cell
     */
    @Value("${polaris.epm.equipment-cell}")
    private String[] equipmentCell;

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
     * vendor : LG
     */
    @Value("${polaris.epm.vendor-elg}")
    private String[] vendorElg;

    /**
     * datasource : 공통 eNB 1h
     */
    @Value("${polaris.epm.ds-epm-com-enb-h}")
    private String dsEpmComEnbH;

    /**
     * datasource : 공통 Cell 1h
     */
    @Value("${polaris.epm.ds-epm-com-cell-h}")
    private String dsEpmComCellH;

    /**
     * datasource : 삼성 eNB 1h
     */
    @Value("${polaris.epm.ds-epm-ss-enb-h}")
    private String dsEpmSsEnbH;

    /**
     * datasource : 삼성 Cell 1h
     */
    @Value("${polaris.epm.ds-epm-ss-cell-h}")
    private String dsEpmSsCellH;

    /**
     * datasource : NSN eNB 1h
     */
    @Value("${polaris.epm.ds-epm-nsn-enb-h}")
    private String dsEpmNsnEnbH;

    /**
     * datasource : NSN Cell 1h
     */
    @Value("${polaris.epm.ds-epm-nsn-cell-h}")
    private String dsEpmNsnCellH;

    /**
     * datasource : ELG eNB 1h
     */
    @Value("${polaris.epm.ds-epm-elg-enb-h}")
    private String dsEpmElgEnbH;

    /**
     * datasource : ELG Cell 1h
     */
    @Value("${polaris.epm.ds-epm-elg-cell-h}")
    private String dsEpmElgCellH;

    /**
     * datasource : 공통 eNB 1d
     */
    @Value("${polaris.epm.ds-epm-com-enb-d}")
    private String dsEpmComEnbD;

    /**
     * datasource : 공통 Cell 1d
     */
    @Value("${polaris.epm.ds-epm-com-cell-d}")
    private String dsEpmComCellD;

    /**
     * datasource : 삼성 eNB 1d
     */
    @Value("${polaris.epm.ds-epm-ss-enb-d}")
    private String dsEpmSsEnbD;

    /**
     * datasource : 삼성 Cell 1d
     */
    @Value("${polaris.epm.ds-epm-ss-cell-d}")
    private String dsEpmSsCellD;

    /**
     * datasource : NSN eNB 1d
     */
    @Value("${polaris.epm.ds-epm-nsn-enb-d}")
    private String dsEpmNsnEnbD;

    /**
     * datasource : NSN Cell 1d
     */
    @Value("${polaris.epm.ds-epm-nsn-cell-d}")
    private String dsEpmNsnCellD;

    /**
     * datasource : ELG eNB 1d
     */
    @Value("${polaris.epm.ds-epm-elg-enb-d}")
    private String dsEpmElgEnbD;

    /**
     * datasource : ELG Cell 1d
     */
    @Value("${polaris.epm.ds-epm-elg-cell-d}")
    private String dsEpmElgCellD;

    /**
     * SKT 조직 dimension : 본부별
     */
    @Value("${polaris.epm.ds-epm-org-dim-skt1}")
    private String[] dsEpmOrgDimSkt1;

    /**
     * SKT 조직 dimension : 팀별
     */
    @Value("${polaris.epm.ds-epm-org-dim-skt2}")
    private String[] dsEpmOrgDimSkt2;

    /**
     * SKT 조직 filter : 팀별
     */
    @Value("${polaris.epm.ds-epm-org-fltr-skt2}")
    private String[] dsEpmOrgFltrSkt2;

    /**
     * SKT 조직 dimension : 세부 팀별
     */
    @Value("${polaris.epm.ds-epm-org-dim-skt3}")
    private String[] dsEpmOrgDimSkt3;

    /**
     * SKT 조직 filter : 세부 팀별
     */
    @Value("${polaris.epm.ds-epm-org-fltr-skt3}")
    private String[] dsEpmOrgFltrSkt3;

    /**
     * ONS 조직 dimension : 본부별
     */
    @Value("${polaris.epm.ds-epm-org-dim-ons1}")
    private String[] dsEpmOrgDimOns1;

    /**
     * ONS 조직 dimension : 팀별
     */
    @Value("${polaris.epm.ds-epm-org-dim-ons2}")
    private String[] dsEpmOrgDimOns2;

    /**
     * ONS 조직 filter : 팀별
     */
    @Value("${polaris.epm.ds-epm-org-fltr-ons2}")
    private String[] dsEpmOrgFltrOns2;

    /**
     * 시도 dimension
     */
    @Value("${polaris.epm.ds-epm-addr-dim-sido}")
    private String[] dsEpmAddrDimSido;

    /**
     * 시군구 dimension
     */
    @Value("${polaris.epm.ds-epm-addr-dim-sgg}")
    private String[] dsEpmAddrDimSgg;

    /**
     * 시군구 filter
     */
    @Value("${polaris.epm.ds-epm-addr-fltr-sgg}")
    private String[] dsEpmAddrFltrSgg;

    /**
     * 읍면동 dimension
     */
    @Value("${polaris.epm.ds-epm-addr-dim-ldong}")
    private String[] dsEpmAddrDimLdong;

    /**
     * 읍면동 filter
     */
    @Value("${polaris.epm.ds-epm-addr-fltr-ldong}")
    private String[] dsEpmAddrFltrLdong;

    /**
     * EMS dimension
     */
    @Value("${polaris.epm.ds-epm-ems-dim}")
    private String[] dsEpmEmsDim;

    /**
     * eNB dimension
     */
    @Value("${polaris.epm.ds-epm-enb-dim}")
    private String[] dsEpmEnbDim;

    /**
     * 국사 dimension
     */
    @Value("${polaris.epm.ds-epm-mtso-dim}")
    private String[] dsEpmMtsoDim;

    /**
     * SKT 조직 filter list
     */
    @Value("${polaris.epm.ds-epm-fltr-skt}")
    private String[] dsEpmFltrSkt;

    /**
     * ONS 조직 filter list
     */
    @Value("${polaris.epm.ds-epm-fltr-ons}")
    private String[] dsEpmFltrOns;

    /**
     * 주소 filter list
     */
    @Value("${polaris.epm.ds-epm-fltr-addr}")
    private String[] dsEpmFltrAddr;

    /**
     * eNB filter EMS
     */
    @Value("${polaris.epm.ds-epm-enb-fltr-ems}")
    private String[] dsEpmEnbFltrEms;

    /**
     * 국사 filter 타입
     */
    @Value("${polaris.epm.ds-epm-mtso-fltr-typ}")
    private String[] dsEpmMtsoFltrTyp;

    /**
     * epm chart eNB 장비목록 dimension
     */
    @Value("${polaris.epm.ds-epm-chart-eqp-dim-enb}")
    private String[] dsEpmChartEqpDimEnb;

    /**
     * epm chart eNB 장비목록 dimension 명
     */
    @Value("${polaris.epm.ds-epm-chart-eqp-dim-enb-nm}")
    private String[] dsEpmChartEqpDimEnbNm;

    /**
     * epm chart Cell 장비목록 dimension
     */
    @Value("${polaris.epm.ds-epm-chart-eqp-dim-cell}")
    private String[] dsEpmChartEqpDimCell;

    /**
     * epm chart Cell 장비목록 dimension 명
     */
    @Value("${polaris.epm.ds-epm-chart-eqp-dim-cell-nm}")
    private String[] dsEpmChartEqpDimCellNm;

    /**
     * epm chart limit
     */
    @Value("${polaris.epm.ds-epm-chart-limit}")
    private int dsEpmChartLimit;

    /**
     * epm chart threshold
     */
    @Value("${polaris.epm.ds-epm-chart-thr}")
    private int dsEpmChartThr;

    /**
     * epm chart excel limit
     */
    @Value("${polaris.epm.ds-epm-chart-excel-thr}")
    private int dsEpmChartExcelThr;

    /**
     * epm chart excel sheet name
     */
    @Value("${polaris.epm.ds-epm-chart-excel-sheet-nm}")
    private String[] dsEpmChartExcelSheetNm;

    /**
     * scrnClNm : druidrange
     */
    private final String DRUIDRANGE = "druidrange";

    /**
     * scrnClNm : range
     */
    private final String RANGE = "range";

    /**
     * scrnClNm : calendar
     */
    private final String CALENDAR = "calendar";

    /**
     * scrnClNm : ymdh
     */
    private final String YMDH = "ymdh";

    /**
     * scrnClNm : address
     */
    private final String ADDRESS = "address";

    /**
     * scrnClNm : region-cell
     */
    private final String REGION_CELL = "region-cell";

    /**
     * scrnClNm : org-cell
     */
    private final String ORG_CELL = "org-cell";

    /**
     * scrnClNm : ems-cell
     */
    private final String EMS_CELL = "ems-cell";

    /**
     * scrnClNm : hierarchy
     */
    private final String HIERARCHY = "hierarchy";

    /**
     * scrnClNm : group
     */
    private final String GROUP = "group";

    /**
     * DruidNm : enb_id
     */
    private final String ENB_ID = "enb_id";

    /**
     * DruidNm : eqp_nm
     */
    private final String EQP_NM = "eqp_nm";

    /**
     * DruidNm : cell_num
     */
    private final String CELL_NUM = "cell_num";

    /**
     * 장비 기간 목록 조회 dimension : dt
     */
    private final String DT = "dt";

    /**
     * 장비 기간 목록 조회 dimension : hh
     */
    private final String HH = "hh";

    /**
     * 장비기간 목록 조회 period data : PERD
     */
    private final String PERD = "perd";

    /**
     * 장비기간 목록 조회 summary data : SMRY
     */
    private final String SMRY = "smry";

    /**
     * Druidrange 등분 Default : STEP
     */
    private final int STEP = 20;

    /**
     * Druidrange : LOWER_VALUE
     */
    private final String LOWER_VALUE = "-9999999";

    /**
     * filter : OCCR_DTH
     */
    private final String OCCR_DTH = "occr_dth";

    /**
     * filter : type : not
     */
    private final String NOT = "not";

    /**
     * SKT 본부
     */
    private final String SKT = "SKT";

    /**
     * O&S 본부
     */
    private final String ONS = "ONS";

    /**
     * 기간 변수 : 1일
     */
    private final String DAY = "day";

    /**
     * 기간 변수 : 1시간
     */
    private final String HOUR = "hour";

    /**
     * EQUIPMENT 구분 : ENB
     */
    private final String ENB = "enb";

    /**
     * EQUIPMENT 구분 : CELL
     */
    private final String CELL = "cell";

    /**
     * Druid 조회결과 null : 정보없음
     */
    private final String UNCLASSIFED = "정보없음";

    @Autowired
    private EpmService epmService;

    @Autowired
    private IpmCommonService ipmCommonService;

    /**
     * step 값 int로 변환
     * @param step
     * @return
     */
    private int parseStep(String step) {
        int ret = 0;
        try {
            if (StringUtils.isNotBlank(step)) {
                ret = Integer.parseInt(step);
            }
        } catch (NumberFormatException e) {
            ret = 0;
        }
        return ret;
    }

    /**
     * FltrList 조회
     * @param fltrDatVal
     * @return
     */
    private List<DimFilter> getFltrList(List<UserBmrkDto.FltrDatVal> fltrDatVal) {

        // fltrDatVal setting
        if (fltrDatVal != null && !fltrDatVal.isEmpty()) {
            List<DimFilter> ret = new ArrayList<>();
            for (UserBmrkDto.FltrDatVal data : fltrDatVal) {

                String druidNm = data.getDruidNm();
                List<ComDto.Code> fltrVal = data.getFltrVal();

                if (StringUtils.isNotBlank(druidNm) && fltrVal != null && !fltrVal.isEmpty()) {

                    DimFilter dimFilter = null;
                    if (DT.equals(druidNm)) {

                        if ("Y".equals((String)fltrVal.get(0).getCode())) {

                            List<String> hdayList = ipmCommonService.getHdayList(fltrDatVal.get(fltrDatVal.size() - 1).getFltrVal());
                            if (hdayList != null && !hdayList.isEmpty()) {

                                if (hdayList.size() > 1) {
                                    dimFilter = new NotDimFilter(new InDimFilter(druidNm, hdayList, null));
                                } else {
                                    dimFilter = new NotDimFilter(new SelectorDimFilter(druidNm, hdayList.get(0), null));
                                }
                            }
                        }
                    } else {
                        String scrnClNm = data.getScrnClNm();
                        switch (scrnClNm) {
                            case DRUIDRANGE:
                            case RANGE:
                            case CALENDAR:
                            case YMDH:
                                dimFilter = orBoundDimFilter(druidNm, fltrVal);
                                break;
                            case ADDRESS:
                                dimFilter = orRegexDimFilter(druidNm, fltrVal);
                                break;
                            case REGION_CELL:
                            case ORG_CELL:
                            case EMS_CELL:
                                dimFilter = enbCellDimFilter(druidNm, fltrVal);
                                break;
                            case HIERARCHY:
                                dimFilter = hierDimFilter(fltrVal);
                                break;
                            case GROUP:
                                dimFilter = groupDimFilter(fltrVal);
                                break;
                            default:
                                dimFilter = inDimFilter(druidNm, fltrVal);
                                break;
                        }
                    }

                    if (dimFilter != null) {
                        if (NOT.equals(data.getType())) {
                            dimFilter = new NotDimFilter(dimFilter);
                        }
                        ret.add(dimFilter);
                    }
                }
            }

            return ret;
        }

        return null;
    }

    /**
     * Druid Datasouce 가져오기
     * @param info
     * @return
     */
    private String getDataSource(EpmDto.Info info) {

        String equipment = info.getEquipment();
        String vendor = info.getVendor();
        if (Arrays.asList(equipmentEnb).contains(equipment)) {
            if (StringUtils.isBlank(info.getDruidNm())) {
                info.setDruidNm(ENB_ID);
            }
            // TODO 1h / 1D 구분 추가
            if (Arrays.asList(vendorSs).contains(vendor)) {
                return dsEpmSsEnbH;
            } else if (Arrays.asList(vendorNsn).contains(vendor)) {
                return dsEpmNsnEnbH;
            } else if (Arrays.asList(vendorElg).contains(vendor)) {
                return dsEpmElgEnbH;
            } else {
                return dsEpmComEnbH;
            }
        } else if (Arrays.asList(equipmentCell).contains(equipment)) {
            if (StringUtils.isBlank(info.getDruidNm())) {
                info.setDruidNm(CELL_NUM);
            }
            // TODO 1h / 1D 구분 추가
            if (Arrays.asList(vendorSs).contains(vendor)) {
                return dsEpmSsCellH;
            } else if (Arrays.asList(vendorNsn).contains(vendor)) {
                return dsEpmNsnCellH;
            } else if (Arrays.asList(vendorElg).contains(vendor)) {
                return dsEpmElgCellH;
            } else {
                return dsEpmComCellH;
            }
        }

        if (StringUtils.isBlank(info.getDruidNm())) {
            info.setDruidNm(CELL_NUM);
        }
        return dsEpmComEnbH;
    }

    /**
     * EPM 분류 정보 가져오기 (망, 장비타입, 벤더사)
     * @param fltrDatVal
     * @return
     */
    private EpmDto.Info getInfo(List<UserBmrkDto.FltrDatVal> fltrDatVal) {

        EpmDto.Info ret = null;
        if (fltrDatVal != null && fltrDatVal.size() > 0) {
            UserBmrkDto.FltrDatVal info = fltrDatVal.get(fltrDatVal.size() - 1);
            List<String> code = (List<String>)info.getFltrVal().get(0).getCode();
            ret = new EpmDto.Info(code.get(0), code.get(1), code.get(2));
            fltrDatVal.remove(info);
        }

        return ret;
    }

    /**
     * Interval 생성
     * @param occrDth
     * @return
     */
    private String getInterval(List<ComDto.Code> occrDth) {

        String postfix = "T00:00:00.000Z";
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        // 어제
        cal.add(Calendar.DATE, -1);
        String startDt = sdf.format(cal.getTime()) + postfix;

        // 내일
        cal.add(Calendar.DATE, 2);
        String endDt = sdf.format(cal.getTime()) + postfix;

        String interval = startDt + "/" + endDt;

        // occrDth가 있을 경우
        if (occrDth != null && !occrDth.isEmpty()) {
            try {
                postfix = ":00:00.000Z";

                List<String> dthList = new ArrayList<>();
                for (ComDto.Code cdList : occrDth) {
                    String cd = (String)cdList.getCode();
                    dthList.add(cd.split("~")[0]);
                    dthList.add(cd.split("~")[1]);
                }

                // 시작일
                String min = Collections.min(dthList);
                // 종료일
                String max = Collections.max(dthList);

                sdf = new SimpleDateFormat("yyyyMMddHH");
                Date minDate = sdf.parse(min);
                Date maxDate = sdf.parse(max);

                // 종료일 + 1
                cal.setTime(maxDate);
                cal.add(cal.DATE, 1);

                sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH");
                startDt = sdf.format(minDate) + postfix;
                endDt = sdf.format(cal.getTime()) + postfix;

                interval =  startDt + "/" + endDt;

            } catch (Exception e) {
                log.error(e.getMessage());
            }

        }

        return interval;
    }

    /**
     * 날짜 필터 (어제 ~ 오늘)
     * @return
     */
    private DimFilter getDtFilter() {

        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String endDt = sdf.format(cal.getTime());
        cal.add(Calendar.DATE, -1);
        String startDt = sdf.format(cal.getTime());

        return new BoundDimFilter(DT, startDt, endDt, false, false, null, null, null);
    }

    /**
     * InCellDimFilter 생성
     * @param druidNm
     * @param fltrVal
     * @return
     */
    private DimFilter enbCellDimFilter(String druidNm, List<ComDto.Code> fltrVal) {

        List<DimFilter> ret = new ArrayList<>();
        for (ComDto.Code cd : fltrVal) {
            List<String> code = (List<String>)cd.getCode();

            if (code.size() == 1) {
                ret.add(new SelectorDimFilter(ENB_ID, code.get(0), null));
            } else {
                ret.add(new AndDimFilter(Arrays.asList(new SelectorDimFilter(ENB_ID, code.get(0), null), new SelectorDimFilter(druidNm, code.get(1), null))));
            }
        }

        if (ret.size() > 1) {
            return new OrDimFilter(ret);
        } else {
            return ret.get(0);
        }
    }

    /**
     * HierDimFilter 생성
     * @param fltrVal
     * @return
     */
    private DimFilter hierDimFilter(List<ComDto.Code> fltrVal) {

        List<DimFilter> ret = new ArrayList<>();

        for (ComDto.Code cd : fltrVal) {
            List<String> code = (List<String>)cd.getCode();
            if (code != null && !code.isEmpty()) {
                ret.add(new SelectorDimFilter(dsEpmFltrAddr[code.size() - 1], code.get(code.size() - 1), null));
            }
        }

        if (ret.size() > 1) {
            return new OrDimFilter(ret);
        } else {
            return ret.get(0);
        }
    }

    /**
     * GroupDimFilter 생성
     * @param fltrVal
     * @return
     */
    private DimFilter groupDimFilter(List<ComDto.Code> fltrVal) {

        List<DimFilter> ret = new ArrayList<>();

        for (ComDto.Code cd : fltrVal) {
            List<String> code = (List<String>)cd.getCode();
            if (code != null && !code.isEmpty()) {
                String head = code.get(0);
                if (SKT.equals(head)) {
                    ret.add(new SelectorDimFilter(dsEpmFltrSkt[code.size() - 2], code.get(code.size() - 1), null));
                } else if (ONS.equals(head)) {
                    ret.add(new SelectorDimFilter(dsEpmFltrOns[code.size() - 2], code.get(code.size() - 1), null));
                }
            }
        }

        if (ret.size() > 1) {
            return new OrDimFilter(ret);
        } else {
            return ret.get(0);
        }
    }

    /**
     * 최대 도달 범위
     * @param fltrDatVal
     * @return
     */
    public ComDto.Chart getChartCount(List<UserBmrkDto.FltrDatVal> fltrDatVal) {
        try {
            List<DimFilter> fltrList = new ArrayList<>();
            EpmDto.Info info = getInfo(fltrDatVal);

            // occr_dth로 interval 생성
            String interval = getInterval(fltrDatVal.get(fltrDatVal.size() - 1).getFltrVal());
            String dataSource = getDataSource(info);

            CardinalityAggregatorFactory cardinalityAggregator = null;
            String druidNm = info.getDruidNm();
            if (CELL_NUM.equals(druidNm)) {
                cardinalityAggregator = new CardinalityAggregatorFactory("value", DefaultDimensionSpec.toSpec(ENB_ID, druidNm), true);
            } else {
                cardinalityAggregator = new CardinalityAggregatorFactory("value", DefaultDimensionSpec.toSpec(druidNm), true);
            }

            GroupByQuery.Builder builder = new GroupByQuery.Builder()
                    .setGranularity(Granularities.ALL)
                    .setDataSource(dataSource)
                    .setInterval(interval)
                    .addAggregator(cardinalityAggregator)
                    ;

            // 필터 List 추가
            List<DimFilter> addFltrList = getFltrList(fltrDatVal);
            if (addFltrList != null && !addFltrList.isEmpty()) {
                fltrList.add(addFltrList.remove(addFltrList.size() - 1));
            }

            if (fltrList != null && !fltrList.isEmpty()) {
                builder.setDimFilter(DimFilters.and(fltrList));
            }

            // all
            List<Row> results = druidRun(builder.build());
            List<ComDto.Code> allRet = new ArrayList<>();
            if (results != null && !results.isEmpty()) {
                MapBasedRow mapBasedRow = (MapBasedRow)results.get(0);
                Map event = mapBasedRow.getEvent();
                allRet.add(new ComDto.Code(null, null, Math.round((double)event.get("value"))));
            } else {
                allRet.add(new ComDto.Code(null, null, 0));
            }

            // target이 필요한 경우
            if (addFltrList != null && !addFltrList.isEmpty()) {
                fltrList.addAll(addFltrList);
                builder.setDimFilter(DimFilters.and(fltrList));

                // target
                results = druidRun(builder.build());
                List<ComDto.Code> targetRet = new ArrayList<>();
                if (results != null && !results.isEmpty()) {
                    MapBasedRow mapBasedRow = (MapBasedRow)results.get(0);
                    Map event = mapBasedRow.getEvent();
                        targetRet.add(new ComDto.Code(null, null, Math.round((double)event.get("value"))));
                } else {
                    targetRet.add(new ComDto.Code(null, null, 0));
                }

                return new ComDto.Chart<ComDto.Code>(targetRet, allRet);
            }

            return new ComDto.Chart<ComDto.Code>(allRet, allRet);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return null;
    }

    /**
     * 본부/팀별 카운트
     * @param fltrDatVal
     * @return
     */
    public ComDto.Chart getOrgChart(List<UserBmrkDto.FltrDatVal> fltrDatVal, List<String> orgList) {
        try {
            List<DimensionSpec> dimList = new ArrayList<>();
            List<DimFilter> fltrList = new ArrayList<>();
            EpmDto.Info info = getInfo(fltrDatVal);

            // occr_dth로 interval 생성
            String interval = getInterval(fltrDatVal.get(fltrDatVal.size() - 1).getFltrVal());
            String dataSource = getDataSource(info);

            int orgSize = orgList.size();
            String org = orgList.get(0);
            if (SKT.equals(org)) {
                switch (orgSize) {
                    case 1:
                        dimList.add(new DefaultDimensionSpec(dsEpmOrgDimSkt1[0], "code"));
                        dimList.add(new DefaultDimensionSpec(dsEpmOrgDimSkt1[1], "name"));
                        break;
                    case 2:
                        dimList.add(new DefaultDimensionSpec(dsEpmOrgDimSkt2[0], "code"));
                        dimList.add(new DefaultDimensionSpec(dsEpmOrgDimSkt2[1], "name"));
                        for (int i = 0; i < dsEpmOrgFltrSkt2.length; i++) {
                            fltrList.add(new SelectorDimFilter(dsEpmOrgFltrSkt2[i], orgList.get(i + 1), null));
                        }
                        break;
                }
            } else if (ONS.equals(org)) {
                switch (orgSize) {
                    case 1:
                        dimList.add(new DefaultDimensionSpec(dsEpmOrgDimOns1[0], "code"));
                        dimList.add(new DefaultDimensionSpec(dsEpmOrgDimOns1[1], "name"));
                        break;
                    case 2:
                        dimList.add(new DefaultDimensionSpec(dsEpmOrgDimOns2[0], "code"));
                        dimList.add(new DefaultDimensionSpec(dsEpmOrgDimOns2[1], "name"));
                        for (int i = 0; i < dsEpmOrgFltrOns2.length; i++) {
                            fltrList.add(new SelectorDimFilter(dsEpmOrgFltrOns2[i], orgList.get(i + 1), null));
                        }
                        break;
                }
            }

            CardinalityAggregatorFactory cardinalityAggregator = null;
            String druidNm = info.getDruidNm();
            if (CELL_NUM.equals(druidNm)) {
                cardinalityAggregator = new CardinalityAggregatorFactory("value", DefaultDimensionSpec.toSpec(ENB_ID, druidNm), true);
            } else {
                cardinalityAggregator = new CardinalityAggregatorFactory("value", DefaultDimensionSpec.toSpec(druidNm), true);
            }

            GroupByQuery.Builder builder = new GroupByQuery.Builder()
                    .setGranularity(Granularities.ALL)
                    .setDataSource(dataSource)
                    .setInterval(interval)
                    .setDimensions(dimList)
                    .addAggregator(cardinalityAggregator)
                    ;

            // 필터 List 추가
            List<DimFilter> addFltrList = getFltrList(fltrDatVal);
            if (addFltrList != null && !addFltrList.isEmpty()) {
                fltrList.add(addFltrList.remove(addFltrList.size() - 1));
            }

            if (fltrList != null && !fltrList.isEmpty()) {
                builder.setDimFilter(DimFilters.and(fltrList));
            }

            // all
            List<Row> results = druidRun(builder.build());
            List<ComDto.Code> allRet = new ArrayList<>();
            ComDto.Code unclassfied = null;
            for (Row row : results) {
                MapBasedRow mapBasedRow = (MapBasedRow)row;
                Map<String, Object> event = mapBasedRow.getEvent();
                String evtCd = (String)event.get("code");
                String evtNm = (String)event.get("name");
                double evtVal = Math.round((double)event.get("value"));

                if (StringUtils.isNotBlank(evtNm)) {
                    allRet.add(new ComDto.Code(evtCd, evtNm, evtVal));
                } else {
                    unclassfied = new ComDto.Code(evtCd, UNCLASSIFED, evtVal);
                }
            }
            // 정보없음 추가
            if (unclassfied != null) {
                allRet.add(unclassfied);
            }

            // target이 필요한 경우
            if (addFltrList != null && !addFltrList.isEmpty()) {
                fltrList.addAll(addFltrList);
                builder.setDimFilter(DimFilters.and(fltrList));

                // target
                results = druidRun(builder.build());
                Map<String, ComDto.Code> targetMap = new HashMap();
                for (Row row : results) {
                    MapBasedRow mapBasedRow = (MapBasedRow)row;
                    Map<String, Object> event = mapBasedRow.getEvent();
                    String evtCd = (String)event.get("code");
                    String evtNm = (String)event.get("name");
                    double evtVal = Math.round((double)event.get("value"));

                    if (StringUtils.isNotBlank(evtNm)) {
                        targetMap.put(evtNm, new ComDto.Code(evtCd, evtNm, evtVal));
                    } else {
                        targetMap.put(UNCLASSIFED, new ComDto.Code(evtCd, UNCLASSIFED, evtVal));
                    }
                }

                // target 빈 값 채우기
                List<ComDto.Code> targetRet = new ArrayList<>();
                for (int i = 0, len = allRet.size(); i < len; i++) {
                    if (targetMap.containsKey(allRet.get(i).getName())) {
                        targetRet.add(targetMap.get(allRet.get(i).getName()));
                    } else {
                        targetRet.add(new ComDto.Code(allRet.get(i).getCode(), allRet.get(i).getName(), 0));
                    }
                }

                return new ComDto.Chart<ComDto.Code>(targetRet, allRet);
            }

            return new ComDto.Chart<ComDto.Code>(allRet, allRet);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return null;
    }

    /**
     * 지역별 카운트
     * @param fltrDatVal
     * @return
     */
    public ComDto.Chart getRegionChart(List<UserBmrkDto.FltrDatVal> fltrDatVal, List<String> addrList) {
        try {
            List<DimensionSpec> dimList = new ArrayList<>();
            List<DimFilter> fltrList = new ArrayList<>();
            EpmDto.Info info = getInfo(fltrDatVal);

            // occr_dth로 interval 생성
            String interval = getInterval(fltrDatVal.get(fltrDatVal.size() - 1).getFltrVal());
            String dataSource = getDataSource(info);

            int addrSize = addrList.size();
            switch(addrSize) {
                case 0:
                    dimList.add(new DefaultDimensionSpec(dsEpmAddrDimSido[0], "code"));
                    dimList.add(new DefaultDimensionSpec(dsEpmAddrDimSido[1], "name"));
                    break;
                case 1:
                    dimList.add(new DefaultDimensionSpec(dsEpmAddrDimSgg[0], "code"));
                    dimList.add(new DefaultDimensionSpec(dsEpmAddrDimSgg[1], "name"));
                    for (int i = 0; i < dsEpmAddrFltrSgg.length; i++) {
                        fltrList.add(new SelectorDimFilter(dsEpmAddrFltrSgg[i], addrList.get(i), null));
                    }
                    break;
                case 2:
                    dimList.add(new DefaultDimensionSpec(dsEpmAddrDimLdong[0], "code"));
                    dimList.add(new DefaultDimensionSpec(dsEpmAddrDimLdong[1], "name"));
                    for (int i = 0; i < dsEpmAddrFltrLdong.length; i++) {
                        fltrList.add(new SelectorDimFilter(dsEpmAddrFltrLdong[i], addrList.get(i), null));
                    }
                    break;
            }

            CardinalityAggregatorFactory cardinalityAggregator = null;
            String druidNm = info.getDruidNm();
            if (CELL_NUM.equals(druidNm)) {
                cardinalityAggregator = new CardinalityAggregatorFactory("value", DefaultDimensionSpec.toSpec(ENB_ID, druidNm), true);
            } else {
                cardinalityAggregator = new CardinalityAggregatorFactory("value", DefaultDimensionSpec.toSpec(druidNm), true);
            }

            GroupByQuery.Builder builder = new GroupByQuery.Builder()
                    .setGranularity(Granularities.ALL)
                    .setDataSource(dataSource)
                    .setInterval(interval)
                    .setDimensions(dimList)
                    .addAggregator(cardinalityAggregator)
                    ;

            // 필터 List 추가
            List<DimFilter> addFltrList = getFltrList(fltrDatVal);
            if (addFltrList != null && !addFltrList.isEmpty()) {
                fltrList.add(addFltrList.remove(addFltrList.size() - 1));
            }

            if (fltrList != null && !fltrList.isEmpty()) {
                builder.setDimFilter(DimFilters.and(fltrList));
            }

            // all
            List<Row> results = druidRun(builder.build());
            List<ComDto.Code> allRet = new ArrayList<>();
            ComDto.Code unclassfied = null;
            for (Row row : results) {
                MapBasedRow mapBasedRow = (MapBasedRow)row;
                Map<String, Object> event = mapBasedRow.getEvent();
                String evtCd = (String)event.get("code");
                String evtNm = (String)event.get("name");
                double evtVal = Math.round((double)event.get("value"));

                if (StringUtils.isNotBlank(evtNm)) {
                    allRet.add(new ComDto.Code(evtCd, evtNm, evtVal));
                } else {
                    unclassfied = new ComDto.Code(evtCd, UNCLASSIFED, evtVal);
                }
            }
            // 정보없음 추가
            if (unclassfied != null) {
                allRet.add(unclassfied);
            }

            // target이 필요한 경우
            if (addFltrList != null && !addFltrList.isEmpty()) {
                fltrList.addAll(addFltrList);
                builder.setDimFilter(DimFilters.and(fltrList));

                // target
                results = druidRun(builder.build());
                Map<String, ComDto.Code> targetMap = new HashMap();
                for (Row row : results) {
                    MapBasedRow mapBasedRow = (MapBasedRow)row;
                    Map<String, Object> event = mapBasedRow.getEvent();
                    String evtCd = (String)event.get("code");
                    String evtNm = (String)event.get("name");
                    double evtVal = Math.round((double)event.get("value"));

                    if (StringUtils.isNotBlank(evtNm)) {
                        targetMap.put(evtNm, new ComDto.Code(evtCd, (String)event.get("name"), evtVal));
                    } else {
                        targetMap.put(UNCLASSIFED, new ComDto.Code(evtCd, UNCLASSIFED, evtVal));
                    }
                }

                // target 빈 값 채우기
                List<ComDto.Code> targetRet = new ArrayList<>();
                for (int i = 0, len = allRet.size(); i < len; i++) {
                    if (targetMap.containsKey(allRet.get(i).getName())) {
                        targetRet.add(targetMap.get(allRet.get(i).getName()));
                    } else {
                        targetRet.add(new ComDto.Code(allRet.get(i).getCode(), allRet.get(i).getName(), 0));
                    }
                }

                return new ComDto.Chart<ComDto.Code>(targetRet, allRet);
            }

            return new ComDto.Chart<ComDto.Code>(allRet, allRet);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return null;
    }

    /**
     * 장비 목록 조회
     * @param fltrDatVal
     * @return
     */
    public EpmDto.Equipment getEqpList(List<UserBmrkDto.FltrDatVal> fltrDatVal, String limit) {
        try {
            EpmDto.Info info = getInfo(fltrDatVal);

            // 장비가 enb인지 cell인지 구분
            String equipment = ENB;
            if (Arrays.asList(equipmentCell).contains(info.getEquipment())) {
                equipment = CELL;
            }

            // limit 설정
            int rowLimit = dsEpmChartLimit;
            if (StringUtils.isNotBlank(limit)) {
                rowLimit = Integer.parseInt(limit);
            }

            // occr_dth로 interval 생성
            String interval = getInterval(fltrDatVal.get(fltrDatVal.size() - 1).getFltrVal());
            String dataSource = getDataSource(info);
            List<DimFilter> fltrList = getFltrList(fltrDatVal);

            // 테이블 tr, td 목록
            List<List<String>> list = new ArrayList<>();
            // header 정보
            List<String> header = new ArrayList<>();

            // header의 처음에 count명 추가
            header.add("횟수");

            // Dimension 정보
            List<String> dim;
            if (CELL.equals(equipment)) {
                header.addAll(Arrays.asList(dsEpmChartEqpDimCellNm));
                dim = Arrays.asList(dsEpmChartEqpDimCell);
            } else {
                header.addAll(Arrays.asList(dsEpmChartEqpDimEnbNm));
                dim = Arrays.asList(dsEpmChartEqpDimEnb);
            }

            GroupByQuery.Builder builder = new GroupByQuery.Builder()
                    .setGranularity(Granularities.ALL)
                    .setDataSource(dataSource)
                    .setInterval(interval)
                    .setDimensions(DefaultDimensionSpec.toSpec(dim))
                    .addAggregator(new CountAggregatorFactory("count"))
                    .setLimit(rowLimit)
                    ;

            if (fltrList != null && !fltrList.isEmpty()) {
                builder.setDimFilter(DimFilters.and(fltrList));
            }

            List<Row> results = druidRun(builder.build());

            // 테이블 list 생성
            for (Row row : results) {
                MapBasedRow mapBasedRow = (MapBasedRow)row;
                Map<String, Object> event = mapBasedRow.getEvent();

                List<String> tdList = new ArrayList<>();
                tdList.add(String.valueOf(event.get("count")));

                for (String col : dim) {
                    String td = "";
                    if (event.get(col) != null) {
                        if ("null".equals(event.get(col))) {
                            td = UNCLASSIFED;
                        } else {
                            td = String.valueOf(event.get(col));
                        }
                    }
                    tdList.add(td);
                }
                list.add(tdList);
            }

            return new EpmDto.Equipment(equipment, list, header);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return null;
    }

    /**
     * 장비 기간 목록 조회
     * @param fltrDatVal, eqpInfo, paging
     * @return
     */
    public EpmDto.Equipment getEqpPerdList(List<UserBmrkDto.FltrDatVal> fltrDatVal, List<EpmDto.Info> eqpInfo, Map<String, Integer> paging, boolean excelDownload) {
        try {
            // 테이블 생성 LIST
            List<List<String>> list = new ArrayList<>();
            Map<String, Integer> newPaging = null;

            EpmDto.Info info = getInfo(fltrDatVal);

            // 장비가 enb인지 cell인지 구분
            String equipment = ENB;
            if (Arrays.asList(equipmentCell).contains(info.getEquipment())) {
                equipment = CELL;
            }

            String interval = getInterval(fltrDatVal.get(fltrDatVal.size() - 1).getFltrVal());
            String dataSource = getDataSource(info);
            List<DimFilter> fltrList = getFltrList(fltrDatVal);

            List<DimFilter> eqpList = new ArrayList<>();
            for (EpmDto.Info eqp : eqpInfo) {
                if (eqp.getEnbId() != null && eqp.getEqpNm() != null) {

                    List<DimFilter> eqpFltr = new ArrayList<>();
                    eqpFltr.add(new SelectorDimFilter(ENB_ID, eqp.getEnbId(), null));
                    eqpFltr.add(new SelectorDimFilter(EQP_NM, eqp.getEqpNm(), null));

                    if (eqp.getCellNum() != null) {
                        eqpFltr.add(new SelectorDimFilter(CELL_NUM, eqp.getCellNum(), null));
                    }
                    eqpList.add(new AndDimFilter(eqpFltr));
                }
            }

            fltrList.add(new OrDimFilter(eqpList));

            // Aggregator 설정
            Map<String, Object> aggrMap = null;
            List<AggregatorFactory> aggregatorList = null;
            List<PostAggregator> postAggregatorList = null;
            if (Arrays.asList(vendorSs).contains(info.getVendor())) {
                aggrMap = getSsAggregator();
            } else if (Arrays.asList(vendorNsn).contains(info.getVendor())) {
                aggrMap = getNsnAggregator();
            } else if (Arrays.asList(vendorElg).contains(info.getVendor())) {
                aggrMap = getElgAggregator();
            } else {
                aggrMap = getComAggregator();
            }
            aggregatorList = (List<AggregatorFactory>)aggrMap.get("aggregator");
            postAggregatorList = (List<PostAggregator>)aggrMap.get("postAggregator");

            // vendor grid list
            List<EpmFltrBasEntity> vendorGrid = epmService.getGridList(info.getVendor());
            List<String> gridDruidNm = new ArrayList<>();
            List<String> gridScrnNm = new ArrayList<>();

            // 장비 기간 목록 grid
            if (vendorGrid != null && !vendorGrid.isEmpty()) {
                for (EpmFltrBasEntity entity : vendorGrid) {
                    if (entity.getFltrNm().contains(PERD)) {
                        gridDruidNm.add(entity.getDruidNm());
                        gridScrnNm.add(entity.getScrnNm());
                    }
                }
            }

            List<String> eqpDim = Arrays.asList(dsEpmChartEqpDimEnb[0], dsEpmChartEqpDimEnb[1]);
            List<String> eqpDimNm = Arrays.asList(dsEpmChartEqpDimEnbNm[0], dsEpmChartEqpDimEnbNm[1]);
            if (CELL.equals(equipment)) {
                eqpDim = Arrays.asList(dsEpmChartEqpDimCell[0], dsEpmChartEqpDimCell[1], dsEpmChartEqpDimCell[2]);
                eqpDimNm = Arrays.asList(dsEpmChartEqpDimCellNm[0], dsEpmChartEqpDimCellNm[1], dsEpmChartEqpDimCellNm[2]);
            }

            List<String> header = null;
            int totCnt = 0;
            List<List<String>> smryList = null;
            List<ComDto.Code> smryHeader = null;

            // 처음 조회일 경우
            if (paging == null || paging.isEmpty()) {

                header = new ArrayList<>(eqpDimNm);
                header.addAll(gridScrnNm);

                smryHeader = new ArrayList<>();
                List<ComDto.Code> smryHeader1 = new ArrayList<>();
                List<ComDto.Code> smryHeader2 = new ArrayList<>();
                for (int i = 0, len = eqpDim.size(); i < len; i++) {
                    smryHeader1.add(new ComDto.Code(eqpDim.get(i), eqpDimNm.get(i)));
                }

                // 장비 기간 목록 summary grid
                if (vendorGrid != null && !vendorGrid.isEmpty()) {
                    for (EpmFltrBasEntity entity : vendorGrid) {
                        if (entity.getFltrNm().contains(SMRY)) {
                            smryHeader2.add(new ComDto.Code(entity.getDruidNm(), entity.getScrnNm()));
                        }
                    }
                }
                smryHeader.addAll(smryHeader1);
                smryHeader.addAll(smryHeader2);

                // 장비 기간 목록 Count 조회
                GroupByQuery.Builder builder = new GroupByQuery.Builder()
                        .setGranularity(Granularities.ALL)
                        .setDataSource(dataSource)
                        .setInterval(interval)
                        .setDimensions(DefaultDimensionSpec.toSpec(eqpDim))
                        .setDimFilter(DimFilters.and(fltrList))
                        .setAggregatorSpecs(aggregatorList)
                        .setPostAggregatorSpecs(postAggregatorList)
                        ;

                smryList = new ArrayList<>();
                List<Row> results = druidRun(builder.build());
                for (Row row : results) {
                    MapBasedRow mapBasedRow = (MapBasedRow)row;
                    Map<String, Object> event = mapBasedRow.getEvent();

                    List<String> tdList = new ArrayList<>();

                    // summary 세팅
                    for (int i = 0, len = smryHeader1.size(); i < len; i++) {

                        String val = "";
                        String code = (String)smryHeader1.get(i).getCode();
                        if (event.get(code) != null) {
                            val = (String)event.get(code);
                        }
                        tdList.add(val);
                    }

                    for (int i = 0, len = smryHeader2.size(); i < len; i++) {

                        String val = "";
                        String code = (String)smryHeader2.get(i).getCode();
                        if (event.get(code) != null) {
                            val = String.valueOf(Math.ceil((double)event.get(code) * 1000) / 1000.0);
                        }
                        tdList.add(val);
                    }
                    smryList.add(tdList);

                    totCnt += (Integer)event.get("value");
                }
            }

            // 장비 기간 목록 조회
            List<String> dim = new ArrayList<>(Arrays.asList(DT, HH));
            if (DAY.equals(eqpInfo.get(0).getDth())) {
                dim.remove(HH);
            }
            dim.addAll(eqpDim);

            // 조회 row 수 세팅
            int thr = dsEpmChartExcelThr;
            if (!excelDownload) {
                thr = dsEpmChartThr;
            }

            Druids.SelectQueryBuilder select = new Druids.SelectQueryBuilder()
                    .granularity(Granularities.ALL)
                    .dataSource(dataSource)
                    .intervals(interval)
                    .dimensionSpecs(DefaultDimensionSpec.toSpec(dim))
                    .filters(DimFilters.and(fltrList))
                    .pagingSpec(new PagingSpec(paging, thr, true))
                    .descending(true)
                    ;

            List<Result<SelectResultValue>> results = druidRun(select.build());

            if (results != null && !results.isEmpty()) {

                Result<SelectResultValue> result = results.get(0);
                List<EventHolder> events = result.getValue().getEvents();

                if (!excelDownload) {
                    newPaging = result.getValue().getPagingIdentifiers();

                    String key = Collections.min(newPaging.keySet());
                    int value = newPaging.get(key);
                    newPaging.clear();
                    newPaging.put(key, value);
                }

                for (EventHolder holder : events) {

                    List<String> tdList = new ArrayList<>();
                    Map event = holder.getEvent();
                    for (String col : dim) {
                        String val = "";
                        if (event.get(col) instanceof String) {
                            val = String.valueOf(event.get(col));
                        }
                        tdList.add(val);
                    }

                    for (String druidNm : gridDruidNm) {
                        String  val = "";
                        if (event.get(druidNm) != null && Double.parseDouble(LOWER_VALUE) != (double)event.get(druidNm)) {
                            val = String.valueOf(Math.ceil((double)event.get(druidNm) * 1000) / 1000.0);
                        }
                        tdList.add(val);
                    }
                    list.add(tdList);
                }
            }

            return new EpmDto.Equipment(equipment, list, header, String.valueOf(totCnt), smryList, smryHeader, newPaging);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return null;
    }

    /**
     * Range Min, Max 조회
     * @param info
     * @return
     */
    public List<ComDto.Code> getEpmDruidrange(EpmDto.Info info) {
        try {
            int step = STEP;
            // step 값이 정상적일 경우
            if (parseStep(info.getStep()) != 0) {
                step = parseStep(info.getStep());
            }

            String druidNm = info.getDruidNm();
            String dataSource = getDataSource(info);

            List<DimFilter> fltrList = new ArrayList<>();
            fltrList.add(new BoundDimFilter(druidNm, LOWER_VALUE, null, true, false, null, null, null));
            fltrList.add(orBoundDimFilter(OCCR_DTH, info.getOccrDth()));

            // min max 값 조회 쿼리
            GroupByQuery.Builder mnBuilder = new GroupByQuery.Builder()
                    .setGranularity(Granularities.ALL)
                    .setDataSource(dataSource)
                    .setInterval(getInterval(info.getOccrDth()))
                    .setDimFilter(DimFilters.and(fltrList))
                    .setAggregatorSpecs(Arrays.asList(new CountAggregatorFactory("value"), new DoubleMinAggregatorFactory("min", druidNm), new DoubleMaxAggregatorFactory("max", druidNm)))
                    ;
            List<Row> mnResults = druidRun(mnBuilder.build());

            List<ComDto.Code> ret = new ArrayList<>();
            if (mnResults != null && !mnResults.isEmpty()) {
                MapBasedRow mnRow = (MapBasedRow)mnResults.get(0);
                Map<String, Object> event = mnRow.getEvent();
                double max = (Double)event.get("max");
                double min = (Double)event.get("min");

                // 데이터가 1개일 경우
                if (min == max) {
                    ret.add(new ComDto.Code(null, Math.floor(min), event.get("value")));
                    if (Math.floor(min) != Math.ceil(max)) {
                        ret.add(new ComDto.Code(null, Math.ceil(max), 0));
                    }

                // 각기 다른 데이터가 여러개 존재할 경우
                } else {

                    // 분할 수로 step값 구해서 세팅
                    double val = Math.ceil(max) - Math.floor(min);
                    if (val > 0) {
                        step = (int)Math.ceil(val / step);
                    }

                    GroupByQuery.Builder dBuilder = new GroupByQuery.Builder()
                            .setDimensions(Arrays.asList(new ExtractionDimensionSpec(druidNm, "name", ValueType.LONG,
                                    new JavaScriptExtractionFn("function(name) {return Math.floor(Math.floor(name) / " + step + ") * " + step + ";}", false, new JavaScriptConfig(true)))))
                            .setGranularity(Granularities.ALL)
                            .setDataSource(dataSource)
                            .setInterval(getInterval(info.getOccrDth()))
                            .setDimFilter(DimFilters.and(fltrList))
                            .setAggregatorSpecs(Arrays.asList(new CountAggregatorFactory("value")))
                            ;

                    List<Row> dResults = druidRun(dBuilder.build());

                    if (dResults != null && !dResults.isEmpty()) {

                        Map<Integer, ComDto.Code> retMap = new HashMap<>();
                        for (Row dRow : dResults) {
                            MapBasedRow mapBasedRow = (MapBasedRow)dRow;
                            event = mapBasedRow.getEvent();
                            String name = (String)event.get("name");
                            if (StringUtils.isNotBlank(name)) {
                                retMap.put(Integer.parseInt(name), new ComDto.Code(null, null, event.get("value")));
                            }
                        }

                        // name 오름차순 정렬
                        List<Integer> nameList = new ArrayList<>();
                        for (Integer name : retMap.keySet()) {
                            nameList.add(name);
                        }
                        Collections.sort(nameList);

                        if (!nameList.isEmpty()) {

                            // 최소값
                            int name = nameList.get(0);
                            while (true) {

                                // 드루이드 조회 결과에 해당 값이 있으면 해당 값, 없으면 0
                                double value = 0;
                                if (nameList.contains(name)) {
                                    value = Double.parseDouble(String.valueOf(retMap.get(name).getValue()));
                                }
                                ret.add(new ComDto.Code(null, name, value));

                                // max 값 초과시 break;
                                if ((name + step) > nameList.get(nameList.size() - 1)) {
                                    if (Math.ceil(max) > name) {
                                        ret.add(new ComDto.Code(null, Math.ceil(max), 0));
                                    }
                                    break;
                                }

                                // 증감치
                                name = name + step;
                            }
                        }
                    }
                }
            }

            return ret;
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return null;
    }

    /**
     * 장비 목록 EXCEL 다운로드
     * @param
     * @return
     */
    public boolean getEqpListExcel(List<UserBmrkDto.FltrDatVal> fltrDatVal, HttpServletResponse response) {

        EpmDto.Equipment data = getEqpList(fltrDatVal, String.valueOf(dsEpmChartExcelThr));
        if (data != null) {

            List<List<String>> list = data.getList();
            List<String> header = data.getHeader();

            if (list != null && header != null) {
                try {
                    // 워크북 생성
                    SXSSFWorkbook wb = new SXSSFWorkbook();
                    Sheet sheet = wb.createSheet(dsEpmChartExcelSheetNm[0]);
                    org.apache.poi.ss.usermodel.Row row = null;
                    Cell cell = null;
                    int rowNo = 0;

                    // cell 설정
                    CellStyle cellStyle = wb.createCellStyle();
                    cellStyle.setBorderTop(BorderStyle.THIN);
                    cellStyle.setBorderBottom(BorderStyle.THIN);
                    cellStyle.setBorderLeft(BorderStyle.THIN);
                    cellStyle.setBorderRight(BorderStyle.THIN);
                    Font font = wb.createFont();
                    font.setFontHeightInPoints((short) 10);
                    font.setFontName("맑은 고딕");
                    cellStyle.setFont(font);

                    //시트 생성
                    row = sheet.createRow(rowNo++);
                    String msg = "최대 " + dsEpmChartExcelThr + "건까지 출력됩니다.";
                    cell = row.createCell(0);
                    cell.setCellValue(msg);

                    // 시트 설정
                    sheet.setColumnWidth(2, (short) 10000);
                    int columnIndex = 0;
                    if (CELL.equals(data.getEquipment())) {
                        columnIndex = 1;
                    }
                    // cell 병합
                    sheet.setColumnWidth(4 + columnIndex, (short) 5000);
                    sheet.setColumnWidth(5 + columnIndex, (short) 6000);
                    sheet.setColumnWidth(9 + columnIndex, (short) 4000);
                    sheet.setColumnWidth(10 + columnIndex, (short) 5000);
                    sheet.setColumnWidth(11 + columnIndex, (short) 3000);
                    sheet.setColumnWidth(12 + columnIndex, (short) 5000);

                    row = sheet.createRow(rowNo++);

                    // 번호
                    cell = row.createCell(0);
                    cell.setCellStyle(cellStyle);
                    cell.setCellValue("번호");

                    for (int i = 0, len = header.size(); i < len; i++) {
                        cell = row.createCell(i + 1);
                        cell.setCellStyle(cellStyle);
                        cell.setCellValue(header.get(i));
                    }

                    // 데이터 생성
                    for (int i = 0, listSize = list.size(); i < listSize; i++) {

                        row = sheet.createRow(rowNo++);

                        // 번호
                        cell = row.createCell(0);
                        cell.setCellStyle(cellStyle);
                        cell.setCellValue(listSize - i);

                        List<String> tdLIst = list.get(i);
                        for (int j = 0, tdListSize = tdLIst.size(); j < tdListSize; j++) {

                            // cell 생성
                            cell = row.createCell(j + 1);
                            cell.setCellStyle(cellStyle);
                            cell.setCellValue(tdLIst.get(j));
                        }
                    }

                    // 엑셀 응답 정보 및 파일 이름 지정
                    response.setContentType("ms-vnd/excel");
                    response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(data.getEquipment(), "UTF-8") + "." + ExcelGenHelper.XLSX);

                    // 엑셀 출력
                    wb.write(response.getOutputStream());
                    wb.close();

                    return true;
                } catch (Exception e) {
                    log.error(e.getMessage());
                }
            }
        }
        return false;
    }

    /**
     * 장비 기간 목록 EXCEL 다운로드
     * @param
     * @return
     */
    public boolean getEqpPerdListExcel(List<UserBmrkDto.FltrDatVal> fltrDatVal, List<EpmDto.Info> eqpInfo, HttpServletResponse response) {

        EpmDto.Equipment data = getEqpPerdList(fltrDatVal, eqpInfo, null, true);
        if (data != null) {

            List<List<String>> list = data.getList();
            List<String> header = data.getHeader();
            List<List<String>> smryList = data.getSmryList();
            List<ComDto.Code> smryHeader = data.getSmryHeader();

            if (list != null && header != null && smryList != null && smryHeader != null) {
                try {
                    // 워크북 생성
                    SXSSFWorkbook wb = new SXSSFWorkbook();

                    // 공통 cell 스타일
                    CellStyle cellStyle = wb.createCellStyle();
                    cellStyle.setBorderTop(BorderStyle.THIN);
                    cellStyle.setBorderBottom(BorderStyle.THIN);
                    cellStyle.setBorderLeft(BorderStyle.THIN);
                    cellStyle.setBorderRight(BorderStyle.THIN);
                    Font font = wb.createFont();
                    font.setFontHeightInPoints((short) 10);
                    font.setFontName("맑은 고딕");
                    cellStyle.setFont(font);

                    Sheet[] sheets = new Sheet[2];
                    // SHEET1 - 1시간 단위 성능 or 1일단위 성능
                    sheets[0] = wb.createSheet(dsEpmChartExcelSheetNm[1]);

                    org.apache.poi.ss.usermodel.Row row = null;
                    Cell cell = null;
                    int rowNo = 0;

                    // column width 설정
                    sheets[0].setColumnWidth(0, (short) 1500);
                    sheets[0].setColumnWidth(1, (short) 4000);
                    sheets[0].setColumnWidth(2, (short) 1500);
                    sheets[0].setColumnWidth(3, (short) 10000);

                    row = sheets[0].createRow(rowNo++);
                    String msg = "최대 " + dsEpmChartExcelThr + "건까지 출력됩니다.";
                    cell = row.createCell(0);
                    cell.setCellValue(msg);

                    row = sheets[0].createRow(rowNo++);

                    // 헤더 생성
                    cell = row.createCell(0);
                    cell.setCellStyle(cellStyle);
                    cell.setCellValue("번호");

                    cell = row.createCell(1);
                    cell.setCellStyle(cellStyle);
                    cell.setCellValue("날짜");

                    cell = row.createCell(2);
                    cell.setCellStyle(cellStyle);
                    cell.setCellValue("시간");

                    cell = row.createCell(3);
                    cell.setCellStyle(cellStyle);
                    cell.setCellValue(header.get(0));

                    cell = row.createCell(4);
                    cell.setCellStyle(cellStyle);
                    cell.setCellValue(header.get(1));
                    int start = 2;
                    if (CELL.equals(data.getEquipment())) {
                        start = 3;
                        cell = row.createCell(5);
                        cell.setCellStyle(cellStyle);
                        cell.setCellValue(header.get(2));
                    }

                    for (int i = start, len = header.size(); i < len; i++) {

                        // column width 설정
                        sheets[0].setColumnWidth(i + 3, (short) 6000);

                        cell = row.createCell(i + 3);
                        cell.setCellStyle(cellStyle);
                        cell.setCellValue(header.get(i));
                    }

                    // 데이터 생성
                    for (int i = 0, listSize = list.size(); i < listSize; i++) {

                        // row 생성
                        row = sheets[0].createRow(rowNo++);

                        // 첫번째 cell - 번호
                        cell = row.createCell(0);
                        cell.setCellStyle(cellStyle);
                        cell.setCellValue(listSize - i);

                        List<String> tdLIst = list.get(i);
                        for (int j = 0, tdListSize = tdLIst.size(); j < tdListSize; j++) {
                            // cell 생성
                            cell = row.createCell(j + 1);
                            cell.setCellStyle(cellStyle);

                            // 날짜 Column
                            if (j == 0) {
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
                                Date date = sdf.parse(tdLIst.get(j));
                                sdf = new SimpleDateFormat("yyyy-MM-dd");
                                cell.setCellValue(sdf.format(date));
                            } else {
                                cell.setCellStyle(cellStyle);
                                cell.setCellValue(tdLIst.get(j));
                            }
                        }
                    }

                    // SHEET2 - 성능 요약
                    sheets[1] = wb.createSheet(dsEpmChartExcelSheetNm[3]);
                    // column width 설정
                    sheets[1].setColumnWidth(0, (short) 10000);

                    // 헤더 및 데이터 생성
                    org.apache.poi.ss.usermodel.Row nmRow = sheets[1].createRow(0);

                    cell = nmRow.createCell(0);
                    cell.setCellStyle(cellStyle);
                    cell.setCellValue((String) smryHeader.get(0).getName());

                    cell = nmRow.createCell(1);
                    cell.setCellStyle(cellStyle);
                    cell.setCellValue((String) smryHeader.get(1).getName());

                    if (CELL.equals(data.getEquipment())) {
                        cell = nmRow.createCell(2);
                        cell.setCellStyle(cellStyle);
                        cell.setCellValue((String) smryHeader.get(2).getName());
                    }

                    for (int i = start, len = smryHeader.size(); i < len; i++) {
                        sheets[1].setColumnWidth(i, (short) 6000);
                        ComDto.Code cd = smryHeader.get(i);

                        cell = nmRow.createCell(i);
                        cell.setCellStyle(cellStyle);
                        cell.setCellValue((String) cd.getName());
                    }

                    for (int i = 0, len = smryList.size(); i < len; i++) {

                        List<String> tdList = smryList.get(i);
                        org.apache.poi.ss.usermodel.Row valRow = sheets[1].createRow(i + 1);
                        for (int j = 0, tdLen = tdList.size(); j < tdLen; j++) {
                            cell = valRow.createCell(j);
                            cell.setCellStyle(cellStyle);
                            cell.setCellValue(tdList.get(j));
                        }
                    }

                    // 엑셀 응답 정보 및 파일 이름 지정
                    response.setContentType("ms-vnd/excel");
                    response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(eqpInfo.get(0).getEqpNm(), "UTF-8") + "." + ExcelGenHelper.XLSX);

                    // 엑셀 출력
                    wb.write(response.getOutputStream());
                    wb.close();

                    return true;
                } catch (Exception e) {
                    log.error(e.getMessage());
                }
            }
        }

        return false;
    }

    /**
     * 공통 Aggregator 목록 조회
     * @return
     */
    private Map<String, Object> getComAggregator() {

        Map<String, Object> ret = new HashMap<>();
        List<AggregatorFactory> aggregatorList = new ArrayList<>();
        List<PostAggregator> postAggregatorList = new ArrayList<>();

        // count
        aggregatorList.add(new CountAggregatorFactory("value"));

        //rrc_attc_cnt
        aggregatorList.add(new DoubleSumAggregatorFactory("rrc_attc_cnt", "rrc_attc_cnt"));

        //rrc_susc_rate
        aggregatorList.add(new DoubleSumAggregatorFactory("rrc_susc_cnt_s", "rrc_susc_cnt"));
        postAggregatorList.add(new JavaScriptPostAggregator("rrc_suss_rate", Arrays.asList("rrc_susc_cnt_s", "rrc_attc_cnt"),
                "function(rrc_susc_cnt_s, rrc_attc_cnt) { if(rrc_attc_cnt){ return rrc_susc_cnt_s / rrc_attc_cnt * 100; } else {return 0;}}",
                new JavaScriptConfig(true)));

        //erab_susc_rate
        aggregatorList.add(new DoubleSumAggregatorFactory("erab_susc_cnt_s", "erab_susc_cnt"));
        aggregatorList.add(new DoubleSumAggregatorFactory("erab_attc_cnt_s", "erab_attc_cnt"));
        postAggregatorList.add(new JavaScriptPostAggregator("erab_suss_rate", Arrays.asList("erab_susc_cnt_s", "erab_attc_cnt_s"),
                "function(erab_susc_cnt_s, erab_attc_cnt_s) { if(erab_attc_cnt_s){ return erab_susc_cnt_s / erab_attc_cnt_s * 100; } else {return 0;}}",
                new JavaScriptConfig(true)));

        ret.put("aggregator", aggregatorList);
        ret.put("postAggregator", postAggregatorList);

        return ret;
    }

    /**
     * 삼성 Aggregator 목록 조회
     * @return
     */
    private Map<String, Object> getSsAggregator() {

        Map<String, Object> ret = new HashMap<>();
        List<AggregatorFactory> aggregatorList = new ArrayList<>();
        List<PostAggregator> postAggregatorList = new ArrayList<>();

        // count
        aggregatorList.add(new CountAggregatorFactory("value"));

        //rrc_attc_cnt
        aggregatorList.add(new DoubleSumAggregatorFactory("rrc_attc_cnt", "rrc_attc_cnt"));

        //rrc_susc_rate
        aggregatorList.add(new DoubleSumAggregatorFactory("rrc_susc_cnt_s", "rrc_susc_cnt"));
        postAggregatorList.add(new JavaScriptPostAggregator("rrc_suss_rate", Arrays.asList("rrc_susc_cnt_s", "rrc_attc_cnt"),
                "function(rrc_susc_cnt_s, rrc_attc_cnt) { if(rrc_attc_cnt){ return rrc_susc_cnt_s / rrc_attc_cnt * 100; } else {return 0;}}",
                new JavaScriptConfig(true)));

        //erab_susc_rate
        aggregatorList.add(new DoubleSumAggregatorFactory("erab_susc_cnt_s", "erab_susc_cnt"));
        aggregatorList.add(new DoubleSumAggregatorFactory("erab_attc_cnt_s", "erab_attc_cnt"));
        postAggregatorList.add(new JavaScriptPostAggregator("erab_suss_rate", Arrays.asList("erab_susc_cnt_s", "erab_attc_cnt_s"),
                "function(erab_susc_cnt_s, erab_attc_cnt_s) { if(erab_attc_cnt_s){ return erab_susc_cnt_s / erab_attc_cnt_s * 100; } else {return 0;}}",
                new JavaScriptConfig(true)));

        //cd_rate
        aggregatorList.add(new DoubleSumAggregatorFactory("cd_cnt_s", "cd_cnt"));
        aggregatorList.add(new DoubleSumAggregatorFactory("estabinitsuccnbr_s", "estabinitsuccnbr"));
        aggregatorList.add(new DoubleSumAggregatorFactory("estabaddsuccnbr_s", "estabaddsuccnbr"));
        postAggregatorList.add(new JavaScriptPostAggregator("cd_rate", Arrays.asList("cd_cnt_s", "estabinitsuccnbr_s", "estabaddsuccnbr_s"),
                "function(cd_cnt_s, estabinitsuccnbr_s, estabaddsuccnbr_s) { if(estabinitsuccnbr_s + estabaddsuccnbr_s){ return cd_cnt_s / (estabinitsuccnbr_s + estabaddsuccnbr_s) * 100; } else {return 0;}}",
                new JavaScriptConfig(true)));

        //connreestabattrate
        aggregatorList.add(new DoubleSumAggregatorFactory("connreestabatt_s", "connreestabatt"));
        aggregatorList.add(new DoubleSumAggregatorFactory("connestabatt_s", "connestabatt"));
        postAggregatorList.add(new JavaScriptPostAggregator("connreestabattrate", Arrays.asList("connreestabatt_s", "connestabatt_s"),
                "function(connreestabatt_s, connestabatt_s) { if(connestabatt_s){ return connreestabatt_s / connestabatt_s * 100; } else {return 0;}}",
                new JavaScriptConfig(true)));

        //connreconfigsuccrate
        aggregatorList.add(new DoubleSumAggregatorFactory("connreconfigsucc_s", "connreconfigsucc"));
        aggregatorList.add(new DoubleSumAggregatorFactory("connreconfigatt_s", "connreconfigatt"));
        postAggregatorList.add(new JavaScriptPostAggregator("connreconfigsuccrate", Arrays.asList("connreconfigsucc_s", "connreconfigatt_s"),
                "function(connreconfigsucc_s, connreconfigatt_s) { if(connreconfigatt_s){ return connreconfigsucc_s / connreconfigatt_s * 100; } else {return 0;}}",
                new JavaScriptConfig(true)));

        //connnoavg
        aggregatorList.add(new DoubleSumAggregatorFactory("connnotot_s", "connnotot"));
        aggregatorList.add(new DoubleSumAggregatorFactory("connnocnt_s", "connnocnt"));
        postAggregatorList.add(new JavaScriptPostAggregator("connnoavg", Arrays.asList("connnotot_s", "connnocnt_s"),
                "function(connnotot_s, connnocnt_s) { if(connnocnt_s){ return connnotot_s / connnocnt_s;} else {return 0;}}",
                new JavaScriptConfig(true)));

        //connnomax
        aggregatorList.add(new DoubleMaxAggregatorFactory("connnomax", "connnomax"));

        //intraenbsuccrate
        aggregatorList.add(new DoubleSumAggregatorFactory("intraenbsucc_s", "intraenbsucc"));
        aggregatorList.add(new DoubleSumAggregatorFactory("intraenbatt_s", "intraenbatt"));
        postAggregatorList.add(new JavaScriptPostAggregator("intraenbsuccrate", Arrays.asList("intraenbsucc_s", "intraenbatt_s"),
                "function(intraenbsucc_s, intraenbatt_s) { if(intraenbatt_s){ return intraenbsucc_s / intraenbatt_s * 100;} else {return 0;}}",
                new JavaScriptConfig(true)));

        //interx2insuccrate
        aggregatorList.add(new DoubleSumAggregatorFactory("interx2insucc_s", "interx2insucc"));
        aggregatorList.add(new DoubleSumAggregatorFactory("interx2inatt_s", "interx2inatt"));
        postAggregatorList.add(new JavaScriptPostAggregator("interx2insuccrate", Arrays.asList("interx2insucc_s", "interx2inatt_s"),
                "function(interx2insucc_s, interx2inatt_s) { if(interx2inatt_s){ return interx2insucc_s / interx2inatt_s * 100;} else {return 0;}}",
                new JavaScriptConfig(true)));

        //interx2outsuccrate
        aggregatorList.add(new DoubleSumAggregatorFactory("interx2outsucc_s", "interx2outsucc"));
        aggregatorList.add(new DoubleSumAggregatorFactory("interx2outatt_s", "interx2outatt"));
        postAggregatorList.add(new JavaScriptPostAggregator("interx2outsuccrate", Arrays.asList("interx2outsucc_s", "interx2outatt_s"),
                "function(interx2outsucc_s, interx2outatt_s) { if(interx2outatt_s){ return interx2outsucc_s / interx2outatt_s * 100;} else {return 0;}}",
                new JavaScriptConfig(true)));

        //inters1insuccrate
        aggregatorList.add(new DoubleSumAggregatorFactory("inters1insucc_s", "inters1insucc"));
        aggregatorList.add(new DoubleSumAggregatorFactory("inters1inatt_s", "inters1inatt"));
        postAggregatorList.add(new JavaScriptPostAggregator("inters1insuccrate", Arrays.asList("inters1insucc_s", "inters1inatt_s"),
                "function(inters1insucc_s, inters1inatt_s) { if(inters1inatt_s){ return inters1insucc_s / inters1inatt_s * 100;} else {return 0;}}",
                new JavaScriptConfig(true)));

        //inters1outsuccrate
        aggregatorList.add(new DoubleSumAggregatorFactory("inters1outsucc_s", "inters1outsucc"));
        aggregatorList.add(new DoubleSumAggregatorFactory("inters1outatt_s", "inters1outatt"));
        postAggregatorList.add(new JavaScriptPostAggregator("inters1outsuccrate", Arrays.asList("inters1outsucc_s", "inters1outatt_s"),
                "function(inters1outsucc_s, inters1outatt_s) { if(inters1outatt_s){ return inters1outsucc_s / inters1outatt_s * 100;} else {return 0;}}",
                new JavaScriptConfig(true)));

        //intrafreqoutsuccrate
        aggregatorList.add(new DoubleSumAggregatorFactory("intrasfreqoutsucc_s", "intrafreqoutsucc"));
        aggregatorList.add(new DoubleSumAggregatorFactory("intrafreqoutatt_s", "intrafreqoutatt"));
        postAggregatorList.add(new JavaScriptPostAggregator("intrafreqoutsuccrate", Arrays.asList("intrasfreqoutsucc_s", "intrafreqoutatt_s"),
                "function(intrasfreqoutsucc_s, intrafreqoutatt_s) { if(intrafreqoutatt_s){ return intrasfreqoutsucc_s / intrafreqoutatt_s * 100;} else {return 0;}}",
                new JavaScriptConfig(true)));

        //alarmcount
        aggregatorList.add(new DoubleSumAggregatorFactory("alarmcount", "alarmcount"));

        //under_ta5
        aggregatorList.add(new DoubleSumAggregatorFactory("sum_under_ta5_s", "sum_under_ta5"));
        aggregatorList.add(new DoubleSumAggregatorFactory("sum_between_ta6_ta11_s", "sum_between_ta6_ta11"));
        aggregatorList.add(new DoubleSumAggregatorFactory("sum_upper_ta12_s", "sum_upper_ta12"));
        postAggregatorList.add(new JavaScriptPostAggregator("under_ta5", Arrays.asList("sum_under_ta5_s", "sum_between_ta6_ta11_s", "sum_upper_ta12_s"),
                "function(sum_under_ta5_s, sum_between_ta6_ta11_s, sum_upper_ta12_s){ if(sum_under_ta5_s + sum_between_ta6_ta11_s + sum_upper_ta12_s){ return sum_under_ta5_s / (sum_under_ta5_s + sum_between_ta6_ta11_s + sum_upper_ta12_s) * 100;} else {return 0;}}",
                new JavaScriptConfig(true)));

        //upper_ta12
        postAggregatorList.add(new JavaScriptPostAggregator("upper_ta12", Arrays.asList("sum_under_ta5_s", "sum_between_ta6_ta11_s", "sum_upper_ta12_s"),
                "function(sum_under_ta5_s, sum_between_ta6_ta11_s, sum_upper_ta12_s){ if(sum_under_ta5_s + sum_between_ta6_ta11_s + sum_upper_ta12_s){ return sum_upper_ta12_s / (sum_under_ta5_s + sum_between_ta6_ta11_s + sum_upper_ta12_s) * 100;} else {return 0;}}",
                new JavaScriptConfig(true)));

        //pdcpsdulossrateul
        aggregatorList.add(new DoubleSumAggregatorFactory("pdcpsdulossulnum_s", "pdcpsdulossulnum"));
        aggregatorList.add(new DoubleSumAggregatorFactory("pdcpsdutotalulnum_s", "pdcpsdutotalulnum"));
        postAggregatorList.add(new JavaScriptPostAggregator("pdcpsdulossrateul", Arrays.asList("pdcpsdulossulnum_s", "pdcpsdutotalulnum_s"),
                "function(pdcpsdulossulnum_s, pdcpsdutotalulnum_s) { if(pdcpsdutotalulnum_s){ return pdcpsdulossulnum_s / pdcpsdutotalulnum_s * 1000000;} else {return 0;}}",
                new JavaScriptConfig(true)));

        //totprbdlavg
        aggregatorList.add(new DoubleSumAggregatorFactory("totprbdlcnt_s", "totprbdlcnt"));
        aggregatorList.add(new DoubleSumAggregatorFactory("totprbdltot_s", "totprbdltot"));
        postAggregatorList.add(new JavaScriptPostAggregator("totprbdlavg", Arrays.asList("totprbdlcnt_s", "totprbdltot_s"),
                "function(totprbdlcnt_s, totprbdltot_s) { if(totprbdlcnt_s){ return totprbdltot_s / totprbdlcnt_s * 100;} else {return 0;}}",
                new JavaScriptConfig(true)));

        //totprbulavg
        aggregatorList.add(new DoubleSumAggregatorFactory("totprbulcnt_s", "totprbulcnt"));
        aggregatorList.add(new DoubleSumAggregatorFactory("totprbultot_s", "totprbultot"));
        postAggregatorList.add(new JavaScriptPostAggregator("totprbulavg", Arrays.asList("totprbulcnt_s", "totprbultot_s"),
                "function(totprbulcnt_s, totprbultot_s) { if(totprbulcnt_s){ return totprbultot_s / totprbulcnt_s * 100;} else {return 0;}}",
                new JavaScriptConfig(true)));

        //puschblerpermcs
        aggregatorList.add(new DoubleSumAggregatorFactory("puschblermcscnt_s", "puschblermcscnt"));
        aggregatorList.add(new DoubleSumAggregatorFactory("puschblermcstot_s", "puschblermcstot"));
        postAggregatorList.add(new JavaScriptPostAggregator("puschblerpermcs", Arrays.asList("puschblermcscnt_s", "puschblermcstot_s"),
                "function(puschblermcscnt_s, puschblermcstot_s) { if(puschblermcscnt_s){ return puschblermcstot_s / puschblermcscnt_s * 100;} else {return 0;}}",
                new JavaScriptConfig(true)));

        //pdschblerperlayer
        aggregatorList.add(new DoubleSumAggregatorFactory("pdschblerperlayercnt_s", "pdschblerperlayercnt"));
        aggregatorList.add(new DoubleSumAggregatorFactory("pdschblerperlayertot_s", "pdschblerperlayertot"));
        postAggregatorList.add(new JavaScriptPostAggregator("pdschblerperlayer", Arrays.asList("pdschblerperlayercnt_s", "pdschblerperlayertot_s"),
                "function(pdschblerperlayercnt_s, pdschblerperlayertot_s) { if(pdschblerperlayercnt_s){ return pdschblerperlayertot_s / pdschblerperlayercnt_s * 100;} else {return 0;}}",
                new JavaScriptConfig(true)));

        //puschblerperlayer
        aggregatorList.add(new DoubleSumAggregatorFactory("puschblerperlayercnt_s", "puschblerperlayercnt"));
        aggregatorList.add(new DoubleSumAggregatorFactory("puschblerperlayertot_s", "puschblerperlayertot"));
        postAggregatorList.add(new JavaScriptPostAggregator("puschblerperlayer", Arrays.asList("puschblerperlayercnt_s", "puschblerperlayertot_s"),
                "function(puschblerperlayercnt_s, puschblerperlayertot_s) { if(puschblerperlayercnt_s){ return puschblerperlayertot_s / puschblerperlayercnt_s * 100;} else {return 0;}}",
                new JavaScriptConfig(true)));

        //gtpsnenblossrate
        aggregatorList.add(new DoubleSumAggregatorFactory("gtpsnenbpeakloss_s", "gtpsnenbpeakloss"));
        aggregatorList.add(new DoubleSumAggregatorFactory("gtpsnenbdlcnt_s", "gtpsnenbdlcnt"));
        postAggregatorList.add(new JavaScriptPostAggregator("gtpsnenblossrate", Arrays.asList("gtpsnenbpeakloss_s", "gtpsnenbdlcnt_s"),
                "function(gtpsnenbpeakloss_s, gtpsnenbdlcnt_s) { if(gtpsnenbdlcnt_s){ return gtpsnenbpeakloss_s / gtpsnenbdlcnt_s * 100;} else {return 0;}}",
                new JavaScriptConfig(true)));

        //gtpsnenboosrate
        aggregatorList.add(new DoubleSumAggregatorFactory("gtpsnenboos_s", "gtpsnenboos"));
        postAggregatorList.add(new JavaScriptPostAggregator("gtpsnenboosrate", Arrays.asList("gtpsnenboos_s", "gtpsnenbdlcnt_s"),
                "function(gtpsnenboos_s, gtpsnenbdlcnt_s) { if(gtpsnenbdlcnt_s){ return gtpsnenboos_s / gtpsnenbdlcnt_s * 100;} else {return 0;}}",
                new JavaScriptConfig(true)));

        //nonhorachsuccessrate
        aggregatorList.add(new DoubleSumAggregatorFactory("nonhorachsuccess_s", "nonhorachsuccess"));
        aggregatorList.add(new DoubleSumAggregatorFactory("nonhorachatt_s", "nonhorachatt"));
        postAggregatorList.add(new JavaScriptPostAggregator("nonhorachsuccessrate", Arrays.asList("nonhorachsuccess_s", "nonhorachatt_s"),
                "function(nonhorachsuccess_s, nonhorachatt_s) { if(nonhorachatt_s){ return nonhorachsuccess_s / nonhorachatt_s * 100;} else {return 0;}}",
                new JavaScriptConfig(true)));

        //clp_lof
        aggregatorList.add(new DoubleSumAggregatorFactory("loslofcurr_s", "loslofcurr"));
        aggregatorList.add(new DoubleSumAggregatorFactory("hczpcurr_s", "hczpcurr"));
        aggregatorList.add(new DoubleSumAggregatorFactory("hcavgcurr_s", "hcavgcurr"));
        postAggregatorList.add(new JavaScriptPostAggregator("clp_lof", Arrays.asList("loslofcurr_s", "hczpcurr_s", "hcavgcurr_s"),
                "function(loslofcurr_s, hczpcurr_s, hcavgcurr_s) {return loslofcurr_s + hczpcurr_s + hcavgcurr_s;}",
                new JavaScriptConfig(true)));

        //clp_dpd
        aggregatorList.add(new DoubleSumAggregatorFactory("clp_dpd", "hcdpdcurr"));

        //rssipathavg
        aggregatorList.add(new DoubleSumAggregatorFactory("rssipathtot_s", "rssipathtot"));
        aggregatorList.add(new DoubleSumAggregatorFactory("rssipathcnt_s", "rssipathcnt"));
        postAggregatorList.add(new JavaScriptPostAggregator("rssipathavg", Arrays.asList("rssipathtot_s", "rssipathcnt_s"),
                "function(rssipathtot_s, rssipathcnt_s) { if(rssipathcnt_s){ return rssipathtot_s / rssipathcnt_s;} else {return 0;}}",
                new JavaScriptConfig(true)));

        //rssipathmax
        aggregatorList.add(new DoubleMaxAggregatorFactory("rssipathmax", "rssipathmax"));

        //puschtxpoweravg
        aggregatorList.add(new DoubleSumAggregatorFactory("sumtxpower_s", "sumtxpower"));
        aggregatorList.add(new DoubleSumAggregatorFactory("phrrxcnt_s", "phrrxcnt"));
        postAggregatorList.add(new JavaScriptPostAggregator("puschtxpoweravg", Arrays.asList("sumtxpower_s", "phrrxcnt_s"),
                "function(sumtxpower_s, phrrxcnt_s) { if(phrrxcnt_s){ return sumtxpower_s / phrrxcnt_s;} else {return 0;}}",
                new JavaScriptConfig(true)));

        //powershortageratio
        aggregatorList.add(new DoubleSumAggregatorFactory("powerlimitcnt_s", "powerlimitcnt"));
        postAggregatorList.add(new JavaScriptPostAggregator("powershortageratio", Arrays.asList("powerlimitcnt_s", "phrrxcnt_s"),
                "function(powerlimitcnt_s, phrrxcnt_s) { if(phrrxcnt_s){ return powerlimitcnt_s / phrrxcnt_s * 100;} else {return 0;}}",
                new JavaScriptConfig(true)));

        //interferencepoweravg
        aggregatorList.add(new DoubleSumAggregatorFactory("interferencepowertot_s", "interferencepowertot"));
        aggregatorList.add(new DoubleSumAggregatorFactory("interferencepowercnt_s", "interferencepowercnt"));
        postAggregatorList.add(new JavaScriptPostAggregator("interferencepoweravg", Arrays.asList("interferencepowertot_s", "interferencepowercnt_s"),
                "function(interferencepowertot_s, interferencepowercnt_s) { if(interferencepowercnt_s){ return interferencepowertot_s / interferencepowercnt_s;} else {return 0;}}",
                new JavaScriptConfig(true)));

        //thermalnoisepoweravg
        aggregatorList.add(new DoubleSumAggregatorFactory("thermalnoisepowertot_s", "thermalnoisepowertot"));
        aggregatorList.add(new DoubleSumAggregatorFactory("thermalnoisepowercnt_s", "thermalnoisepowercnt"));
        postAggregatorList.add(new JavaScriptPostAggregator("thermalnoisepoweravg", Arrays.asList("thermalnoisepowertot_s", "thermalnoisepowercnt_s"),
                "function(thermalnoisepowertot_s, thermalnoisepowercnt_s) { if(thermalnoisepowercnt_s){ return thermalnoisepowertot_s / thermalnoisepowercnt_s;} else {return 0;}}",
                new JavaScriptConfig(true)));

        //rssipathgap
        aggregatorList.add(new DoubleSumAggregatorFactory("rssipath0tot_s", "rssipath0tot"));
        aggregatorList.add(new DoubleSumAggregatorFactory("rssipath0cnt_s", "rssipath0cnt"));
        aggregatorList.add(new DoubleSumAggregatorFactory("rssipath1tot_s", "rssipath1tot"));
        aggregatorList.add(new DoubleSumAggregatorFactory("rssipath1cnt_s", "rssipath1cnt"));
        postAggregatorList.add(new JavaScriptPostAggregator("rssipathgap", Arrays.asList("rssipath0tot_s", "rssipath0cnt_s", "rssipath1tot_s", "rssipath1cnt_s"),
                "function(rssipath0tot_s, rssipath0cnt_s, rssipath1tot_s, rssipath1cnt_s) { if(rssipath0cnt_s && rssipath1cnt_s){ return (rssipath0tot_s / rssipath0cnt_s) - (rssipath1tot_s / rssipath1cnt_s);} else if(rssipath0cnt_s){ return rssipath0tot_s / rssipath0cnt_s} else if(rssipath1cnt_s){ return rssipath1tot_s / rssipath1cnt_s * -1;} else {return 0;}}",
                new JavaScriptConfig(true)));

        //volteintrahosuccessrate
        aggregatorList.add(new DoubleSumAggregatorFactory("sumvolteintraenbsucc_count_s", "sumvolteintraenbsucc_count"));
        aggregatorList.add(new DoubleSumAggregatorFactory("sumvolteintraenbatt_count_s", "sumvolteintraenbatt_count"));
        postAggregatorList.add(new JavaScriptPostAggregator("volteintrahosuccessrate", Arrays.asList("sumvolteintraenbsucc_count_s", "sumvolteintraenbatt_count_s"),
                "function(sumvolteintraenbsucc_count_s, sumvolteintraenbatt_count_s) { if(sumvolteintraenbatt_count_s){ return sumvolteintraenbsucc_count_s / sumvolteintraenbatt_count_s * 100;} else {return 0;}}",
                new JavaScriptConfig(true)));

        //voltex2hosuccessrate
        aggregatorList.add(new DoubleSumAggregatorFactory("sumvolteinterx2outsucc_count_s", "sumvolteinterx2outsucc_count"));
        aggregatorList.add(new DoubleSumAggregatorFactory("sumvolteinterx2outatt_count_s", "sumvolteinterx2outatt_count"));
        postAggregatorList.add(new JavaScriptPostAggregator("voltex2hosuccessrate", Arrays.asList("sumvolteinterx2outsucc_count_s", "sumvolteinterx2outatt_count_s"),
                "function(sumvolteinterx2outsucc_count_s, sumvolteinterx2outatt_count_s) { if(sumvolteinterx2outatt_count_s){ return sumvolteinterx2outsucc_count_s / sumvolteinterx2outatt_count_s * 100;} else {return 0;}}",
                new JavaScriptConfig(true)));

        //voltes1hosuccessrate
        aggregatorList.add(new DoubleSumAggregatorFactory("sumvolteinters1outsucc_count_s", "sumvolteinters1outsucc_count"));
        aggregatorList.add(new DoubleSumAggregatorFactory("sumvolteinters1outatt_count_s", "sumvolteinters1outatt_count"));
        postAggregatorList.add(new JavaScriptPostAggregator("voltes1hosuccessrate", Arrays.asList("sumvolteinters1outsucc_count_s", "sumvolteinters1outatt_count_s"),
                "function(sumvolteinters1outsucc_count_s, sumvolteinters1outatt_count_s) { if(sumvolteinters1outatt_count_s){ return sumvolteinters1outsucc_count_s / sumvolteinters1outatt_count_s * 100;} else {return 0;}}",
                new JavaScriptConfig(true)));

        //gtp_pathfail
        aggregatorList.add(new DoubleSumAggregatorFactory("gtp_pathfail", "gtp_pathfail"));

        //ecc_csfbconfigfailure
        aggregatorList.add(new DoubleSumAggregatorFactory("ecc_csfbconfigfailure", "ecc_csfbconfigfailure"));

        ret.put("aggregator", aggregatorList);
        ret.put("postAggregator", postAggregatorList);

        return ret;
    }

    /**
     * Nsn Aggregator 목록 조회
     * @return
     */
    private Map<String, Object> getNsnAggregator() {

        Map<String, Object> ret = new HashMap<>();
        List<AggregatorFactory> aggregatorList = new ArrayList<>();
        List<PostAggregator> postAggregatorList = new ArrayList<>();

        // count
        aggregatorList.add(new CountAggregatorFactory("value"));

        // rrc_attc_cnt
        aggregatorList.add(new DoubleSumAggregatorFactory("rrc_attc_cnt", "rrc_attc_cnt"));

        // rrc_suss_rate
        aggregatorList.add(new DoubleSumAggregatorFactory("sign_conn_estab_comp_s", "sign_conn_estab_comp"));
        postAggregatorList.add(new JavaScriptPostAggregator("rrc_suss_rate", Arrays.asList("sign_conn_estab_comp_s", "rrc_attc_cnt"),
                "function(sign_conn_estab_comp_s, rrc_attc_cnt) { if(rrc_attc_cnt){ return (sign_conn_estab_comp_s / rrc_attc_cnt) * 100; } else {return 0;}}",
                new JavaScriptConfig(true)));

        // erab_attc_cnt
        aggregatorList.add(new DoubleSumAggregatorFactory("erab_attc_cnt", "erab_attc_cnt"));

        // erab_succ_rate
        aggregatorList.add(new DoubleSumAggregatorFactory("eps_bearer_setup_completions_s", "eps_bearer_setup_completions"));
        postAggregatorList.add(new JavaScriptPostAggregator("erab_succ_rate", Arrays.asList("eps_bearer_setup_completions_s", "erab_attc_cnt"),
                "function(eps_bearer_setup_completions_s, erab_attc_cnt) { if(erab_attc_cnt){ return (eps_bearer_setup_completions_s / erab_attc_cnt) * 100; } else {return 0;}}",
                new JavaScriptConfig(true)));

        // erab_cdcl
        aggregatorList.add(new DoubleSumAggregatorFactory("erab_cdcl", "erab_cdcl"));

        // erab_tot_cdcl
        aggregatorList.add(new DoubleSumAggregatorFactory("erab_tot_cdcl", "erab_tot_cdcl"));

        // erab_cdrt
        postAggregatorList.add(new JavaScriptPostAggregator("erab_cdrt", Arrays.asList("erab_cdcl", "erab_tot_cdcl"),
                "function(erab_cdcl, erab_tot_cdcl) { if(erab_tot_cdcl){ return (erab_cdcl / erab_tot_cdcl) * 100; } else {return 0;}}",
                new JavaScriptConfig(true)));

        // rb_attc_cnt
        aggregatorList.add(new DoubleSumAggregatorFactory("rb_attc_cnt", "rb_attc_cnt"));

        // rb_suss_rate
        aggregatorList.add(new DoubleSumAggregatorFactory("data_rb_stp_comp_s", "data_rb_stp_comp"));
        postAggregatorList.add(new JavaScriptPostAggregator("rb_suss_rate", Arrays.asList("data_rb_stp_comp_s", "rb_attc_cnt"),
                "function(data_rb_stp_comp_s, rb_attc_cnt) { if(rb_attc_cnt){ return (data_rb_stp_comp_s / rb_attc_cnt) * 100; } else {return 0;}}",
                new JavaScriptConfig(true)));

        // volte_erab_att
        aggregatorList.add(new DoubleSumAggregatorFactory("volte_erab_att", "volte_erab_att"));

        // volte_erab_sr
        aggregatorList.add(new DoubleSumAggregatorFactory("erab_ini_setup_succ_qci1_s", "erab_ini_setup_succ_qci1"));
        aggregatorList.add(new DoubleSumAggregatorFactory("erab_add_setup_succ_qci1_s", "erab_add_setup_succ_qci1"));
        postAggregatorList.add(new JavaScriptPostAggregator("volte_erab_sr", Arrays.asList("erab_ini_setup_succ_qci1_s", "erab_add_setup_succ_qci1_s", "volte_erab_att"),
                "function(erab_ini_setup_succ_qci1_s, erab_add_setup_succ_qci1_s, volte_erab_att) { if(volte_erab_att){ return (erab_ini_setup_succ_qci1_s + erab_add_setup_succ_qci1_s) / volte_erab_att * 100; } else {return 0;}}",
                new JavaScriptConfig(true)));

        // volte_erab_dr
        aggregatorList.add(new DoubleSumAggregatorFactory("ereb_rel_enb_calc_s", "ereb_rel_enb_calc"));
        aggregatorList.add(new DoubleSumAggregatorFactory("erab_rel_enb_sum_s", "erab_rel_enb_sum"));
        postAggregatorList.add(new JavaScriptPostAggregator("volte_erab_dr", Arrays.asList("ereb_rel_enb_calc_s", "erab_rel_enb_sum_s"),
                "function(ereb_rel_enb_calc_s, erab_rel_enb_sum_s) { if(erab_rel_enb_sum_s){ return (100 * ereb_rel_enb_calc_s) / erab_rel_enb_sum_s; } else {return 0;}}",
                new JavaScriptConfig(true)));

        // volte_qci1_cd
        aggregatorList.add(new DoubleSumAggregatorFactory("volte_qci1_cd", "volte_qci1_cd"));

        // pdcp_dsu_vol_dl
        aggregatorList.add(new DoubleSumAggregatorFactory("pdcp_dsu_vol_dl", "pdcp_dsu_vol_dl"));

        // pdcp_dsu_vol_ul
        aggregatorList.add(new DoubleSumAggregatorFactory("pdcp_dsu_vol_ul", "pdcp_dsu_vol_ul"));

        // pdcp_max_thp_dl
        aggregatorList.add(new DoubleMaxAggregatorFactory("pdcp_max_thp_dl", "pdcp_max_thp_dl"));

        // pdcp_max_thp_ul
        aggregatorList.add(new DoubleMaxAggregatorFactory("pdcp_max_thp_ul", "pdcp_max_thp_ul"));

        // intra_enb_ho_attc_cnt
        aggregatorList.add(new DoubleSumAggregatorFactory("intra_enb_ho_attc_cnt", "intra_enb_ho_attc_cnt"));

        // intra_enb_ho_suss_rate
        aggregatorList.add(new DoubleSumAggregatorFactory("succ_intra_enb_ho_s", "succ_intra_enb_ho"));
        postAggregatorList.add(new JavaScriptPostAggregator("intra_enb_ho_suss_rate", Arrays.asList("succ_intra_enb_ho_s", "intra_enb_ho_attc_cnt"),
                "function(succ_intra_enb_ho_s, intra_enb_ho_attc_cnt) { if(intra_enb_ho_attc_cnt){ return (succ_intra_enb_ho_s / intra_enb_ho_attc_cnt) * 100; } else {return 0;}}",
                new JavaScriptConfig(true)));

        // inter_enb_ho_attc_cnt
        aggregatorList.add(new DoubleSumAggregatorFactory("inter_enb_ho_attc_cnt", "inter_enb_ho_attc_cnt"));

        // inter_enb_ho_suss_rate
        aggregatorList.add(new DoubleSumAggregatorFactory("succ_inter_enb_ho_s", "succ_inter_enb_ho"));
        postAggregatorList.add(new JavaScriptPostAggregator("inter_enb_ho_suss_rate", Arrays.asList("succ_inter_enb_ho_s", "inter_enb_ho_attc_cnt"),
                "function(succ_inter_enb_ho_s, inter_enb_ho_attc_cnt) { if(inter_enb_ho_attc_cnt){ return (succ_inter_enb_ho_s / inter_enb_ho_attc_cnt) * 100; } else {return 0;}}",
                new JavaScriptConfig(true)));

        // if_ho_attc_cnt
        aggregatorList.add(new DoubleSumAggregatorFactory("if_ho_attc_cnt", "if_ho_attc_cnt"));

        // if_ho_suss_rate
        aggregatorList.add(new DoubleSumAggregatorFactory("ho_intfreq_succ_s", "ho_intfreq_succ"));
        postAggregatorList.add(new JavaScriptPostAggregator("if_ho_suss_rate", Arrays.asList("ho_intfreq_succ_s", "if_ho_attc_cnt"),
                "function(succ_inter_enb_ho_s, inter_enb_ho_attc_cnt) { if(inter_enb_ho_attc_cnt){ return (succ_inter_enb_ho_s / inter_enb_ho_attc_cnt) * 100; } else {return 0;}}",
                new JavaScriptConfig(true)));

        // csfb_att_ue_idle
        aggregatorList.add(new DoubleSumAggregatorFactory("csfb_att_ue_idle", "csfb_att_ue_idle"));

        // csfb_att_ue_conn
        aggregatorList.add(new DoubleSumAggregatorFactory("csfb_att_ue_conn", "csfb_att_ue_conn"));

        // csfb_att_utran_ps
        aggregatorList.add(new DoubleSumAggregatorFactory("csfb_att_utran_ps", "csfb_att_utran_ps"));

        // srvcc_inter_sys_att_to_utran
        aggregatorList.add(new DoubleSumAggregatorFactory("srvcc_inter_sys_att_to_utran", "srvcc_inter_sys_att_to_utran"));

        // srvcc_suss_to_utran
        aggregatorList.add(new DoubleSumAggregatorFactory("isys_ho_utran_srvcc_succ_s", "isys_ho_utran_srvcc_succ"));
        postAggregatorList.add(new JavaScriptPostAggregator("srvcc_suss_to_utran", Arrays.asList("isys_ho_utran_srvcc_succ_s", "srvcc_inter_sys_att_to_utran"),
                "function(isys_ho_utran_srvcc_succ_s, srvcc_inter_sys_att_to_utran) { if(srvcc_inter_sys_att_to_utran){ return (100 * isys_ho_utran_srvcc_succ_s) / srvcc_inter_sys_att_to_utran; } else {return 0;}}",
                new JavaScriptConfig(true)));

        // lb_ho_attc_cnt
        aggregatorList.add(new DoubleSumAggregatorFactory("lb_ho_attc_cnt", "lb_ho_attc_cnt"));

        // lb_ho_suss_rate
        aggregatorList.add(new DoubleSumAggregatorFactory("ho_lb_succ_s", "ho_lb_succ"));
        postAggregatorList.add(new JavaScriptPostAggregator("lb_ho_suss_rate", Arrays.asList("ho_lb_succ_s", "lb_ho_attc_cnt"),
                "function(ho_lb_succ_s, lb_ho_attc_cnt) { if(lb_ho_attc_cnt){ return (100 * ho_lb_succ_s) / lb_ho_attc_cnt; } else {return 0;}}",
                new JavaScriptConfig(true)));

        // rfl_ho_prep_att
        aggregatorList.add(new DoubleSumAggregatorFactory("rfl_ho_prep_att", "rfl_ho_prep_att"));

        // rlf_ho_prep_sr
        aggregatorList.add(new DoubleSumAggregatorFactory("ho_rlf_prep_succ_s", "ho_rlf_prep_succ"));
        postAggregatorList.add(new JavaScriptPostAggregator("rlf_ho_prep_sr", Arrays.asList("ho_rlf_prep_succ_s", "rfl_ho_prep_att"),
                "function(ho_rlf_prep_succ_s, rfl_ho_prep_att) { if(rfl_ho_prep_att){ return (100 * ho_rlf_prep_succ_s) / rfl_ho_prep_att; } else {return 0;}}",
                new JavaScriptConfig(true)));

        // rlf_ho_total_sr
        aggregatorList.add(new DoubleSumAggregatorFactory("ho_rlf_succ_s", "ho_rlf_succ"));
        postAggregatorList.add(new JavaScriptPostAggregator("rlf_ho_total_sr", Arrays.asList("ho_rlf_succ_s", "rfl_ho_prep_att"),
                "function(ho_rlf_succ_s, rfl_ho_prep_att) { if(rfl_ho_prep_att){ return (100 * ho_rlf_succ_s) / rfl_ho_prep_att; } else {return 0;}}",
                new JavaScriptConfig(true)));

        // sb_ho_att
        aggregatorList.add(new DoubleSumAggregatorFactory("sb_ho_att", "sb_ho_att"));

        // sb_ho_sr
        aggregatorList.add(new DoubleSumAggregatorFactory("ho_sb_succ_s", "ho_sb_succ"));
        postAggregatorList.add(new JavaScriptPostAggregator("sb_ho_sr", Arrays.asList("ho_sb_succ_s", "sb_ho_att"),
                "function(ho_sb_succ_s, sb_ho_att) { if(sb_ho_att){ return (100 * ho_sb_succ_s) / sb_ho_att; } else {return 0;}}",
                new JavaScriptConfig(true)));

        // harq_dtx
        aggregatorList.add(new DoubleSumAggregatorFactory("harq_dtx", "harq_dtx"));

        // qlty_avg_cqi
        aggregatorList.add(new DoubleSumAggregatorFactory("ue_rep_cqi_level_sum0to15by_s", "ue_rep_cqi_level_sum0to15by"));
        aggregatorList.add(new DoubleSumAggregatorFactory("ue_rep_cqi_level_sum0to15_s", "ue_rep_cqi_level_sum0to15"));
        postAggregatorList.add(new JavaScriptPostAggregator("qlty_avg_cqi", Arrays.asList("ue_rep_cqi_level_sum0to15by_s", "ue_rep_cqi_level_sum0to15_s"),
                "function(ue_rep_cqi_level_sum0to15by_s, ue_rep_cqi_level_sum0to15_s) { if(ue_rep_cqi_level_sum0to15_s){ return ue_rep_cqi_level_sum0to15by_s / ue_rep_cqi_level_sum0to15_s; } else {return 0;}}",
                new JavaScriptConfig(true)));

        // qualty_avg_cqi_offset
        aggregatorList.add(new DoubleSumAggregatorFactory("qualty_avg_cqi_offset_s", "qualty_avg_cqi_offset"));
        postAggregatorList.add(new JavaScriptPostAggregator("qualty_avg_cqi_offset", Arrays.asList("qualty_avg_cqi_offset_s", "value"),
                "function(qualty_avg_cqi_offset_s, value) { if(value){ return qualty_avg_cqi_offset_s / value; } else {return 0;}}",
                new JavaScriptConfig(true)));

        // quality_avg_rssi_pucch
        aggregatorList.add(new DoubleSumAggregatorFactory("rssi_pucch_level_sum1to22by_s", "rssi_pucch_level_sum1to22by"));
        aggregatorList.add(new DoubleSumAggregatorFactory("rssi_pucch_level_sum1to22_s", "rssi_pucch_level_sum1to22"));
        postAggregatorList.add(new JavaScriptPostAggregator("quality_avg_rssi_pucch", Arrays.asList("rssi_pucch_level_sum1to22by_s", "rssi_pucch_level_sum1to22_s"),
                "function(rssi_pucch_level_sum1to22by_s, rssi_pucch_level_sum1to22_s) { if(rssi_pucch_level_sum1to22_s){ return rssi_pucch_level_sum1to22by_s / rssi_pucch_level_sum1to22_s; } else {return 0;}}",
                new JavaScriptConfig(true)));

        // qualty_avg_rssi_pusch
        aggregatorList.add(new DoubleSumAggregatorFactory("rssi_pusch_level_sum1to22by_s", "rssi_pusch_level_sum1to22by"));
        aggregatorList.add(new DoubleSumAggregatorFactory("rssi_pusch_level_sum1to22_s", "rssi_pusch_level_sum1to22"));
        postAggregatorList.add(new JavaScriptPostAggregator("qualty_avg_rssi_pusch", Arrays.asList("rssi_pusch_level_sum1to22by_s", "rssi_pusch_level_sum1to22_s"),
                "function(rssi_pusch_level_sum1to22by_s, rssi_pusch_level_sum1to22_s) { if(rssi_pusch_level_sum1to22_s){ return rssi_pusch_level_sum1to22by_s / rssi_pusch_level_sum1to22_s; } else {return 0;}}",
                new JavaScriptConfig(true)));

        // qualty_avg_sinr_pucch
        aggregatorList.add(new DoubleSumAggregatorFactory("sinr_pucch_level_sum1to22by_s", "sinr_pucch_level_sum1to22by"));
        aggregatorList.add(new DoubleSumAggregatorFactory("sinr_pucch_level_sum1to22_s", "sinr_pucch_level_sum1to22"));
        postAggregatorList.add(new JavaScriptPostAggregator("qualty_avg_sinr_pucch", Arrays.asList("sinr_pucch_level_sum1to22by_s", "sinr_pucch_level_sum1to22_s"),
                "function(sinr_pucch_level_sum1to22by_s, sinr_pucch_level_sum1to22_s) { if(sinr_pucch_level_sum1to22_s){ return sinr_pucch_level_sum1to22by_s / sinr_pucch_level_sum1to22_s; } else {return 0;}}",
                new JavaScriptConfig(true)));

        // qualty_avg_sinr_pusch
        aggregatorList.add(new DoubleSumAggregatorFactory("sinr_pusch_level_sum1to22by_s", "sinr_pusch_level_sum1to22by"));
        aggregatorList.add(new DoubleSumAggregatorFactory("sinr_pusch_level_sum1to22_s", "sinr_pusch_level_sum1to22"));
        postAggregatorList.add(new JavaScriptPostAggregator("qualty_avg_sinr_pusch", Arrays.asList("sinr_pusch_level_sum1to22by_s", "sinr_pusch_level_sum1to22_s"),
                "function(sinr_pusch_level_sum1to22by_s, sinr_pusch_level_sum1to22_s) { if(sinr_pusch_level_sum1to22_s){ return sinr_pusch_level_sum1to22by_s / sinr_pusch_level_sum1to22_s; } else {return 0;}}",
                new JavaScriptConfig(true)));

        // qualty_avg_pwr_hr_pusch
        aggregatorList.add(new DoubleSumAggregatorFactory("ue_pwr_headroom_pusch_level_sum1to32by_s", "ue_pwr_headroom_pusch_level_sum1to32by"));
        aggregatorList.add(new DoubleSumAggregatorFactory("ue_pwr_headroom_pusch_level_sum1to32_s", "ue_pwr_headroom_pusch_level_sum1to32"));
        postAggregatorList.add(new JavaScriptPostAggregator("qualty_avg_pwr_hr_pusch", Arrays.asList("ue_pwr_headroom_pusch_level_sum1to32by_s", "ue_pwr_headroom_pusch_level_sum1to32_s"),
                "function(ue_pwr_headroom_pusch_level_sum1to32by_s, ue_pwr_headroom_pusch_level_sum1to32_s) { if(ue_pwr_headroom_pusch_level_sum1to32_s){ return ue_pwr_headroom_pusch_level_sum1to32by_s / ue_pwr_headroom_pusch_level_sum1to32_s; } else {return 0;}}",
                new JavaScriptConfig(true)));

        // qualty_avg_rtwp_rx_ant_1
        aggregatorList.add(new DoubleSumAggregatorFactory("qualty_avg_rtwp_rx_ant_1_s", "qualty_avg_rtwp_rx_ant_1"));
        postAggregatorList.add(new JavaScriptPostAggregator("qualty_avg_rtwp_rx_ant_1", Arrays.asList("qualty_avg_rtwp_rx_ant_1_s", "value"),
                "function(qualty_avg_rtwp_rx_ant_1_s, value) { if(value){ return qualty_avg_rtwp_rx_ant_1_s / value; } else {return 0;}}",
                new JavaScriptConfig(true)));

        // qualty_avg_rtwp_rx_ant_2
        aggregatorList.add(new DoubleSumAggregatorFactory("qualty_avg_rtwp_rx_ant_2_s", "qualty_avg_rtwp_rx_ant_2"));
        postAggregatorList.add(new JavaScriptPostAggregator("qualty_avg_rtwp_rx_ant_2", Arrays.asList("qualty_avg_rtwp_rx_ant_2_s", "value"),
                "function(qualty_avg_rtwp_rx_ant_2_s, value) { if(value){ return qualty_avg_rtwp_rx_ant_2_s / value; } else {return 0;}}",
                new JavaScriptConfig(true)));

        // qualty_avg_rtwp_rx_ant_3
        aggregatorList.add(new DoubleSumAggregatorFactory("qualty_avg_rtwp_rx_ant_3_s", "qualty_avg_rtwp_rx_ant_3"));
        postAggregatorList.add(new JavaScriptPostAggregator("qualty_avg_rtwp_rx_ant_3", Arrays.asList("qualty_avg_rtwp_rx_ant_3_s", "value"),
                "function(qualty_avg_rtwp_rx_ant_3_s, value) { if(value){ return qualty_avg_rtwp_rx_ant_3_s / value; } else {return 0;}}",
                new JavaScriptConfig(true)));

        // qualty_avg_rtwp_rx_ant_4
        aggregatorList.add(new DoubleSumAggregatorFactory("qualty_avg_rtwp_rx_ant_4_s", "qualty_avg_rtwp_rx_ant_4"));
        postAggregatorList.add(new JavaScriptPostAggregator("qualty_avg_rtwp_rx_ant_4", Arrays.asList("qualty_avg_rtwp_rx_ant_4_s", "value"),
                "function(qualty_avg_rtwp_rx_ant_4_s, value) { if(value){ return qualty_avg_rtwp_rx_ant_4_s / value; } else {return 0;}}",
                new JavaScriptConfig(true)));

        // qualty_avg_rtwp_rx_ant_5
        aggregatorList.add(new DoubleSumAggregatorFactory("qualty_avg_rtwp_rx_ant_5_s", "qualty_avg_rtwp_rx_ant_5"));
        postAggregatorList.add(new JavaScriptPostAggregator("qualty_avg_rtwp_rx_ant_5", Arrays.asList("qualty_avg_rtwp_rx_ant_5_s", "value"),
                "function(qualty_avg_rtwp_rx_ant_5_s, value) { if(value){ return qualty_avg_rtwp_rx_ant_5_s / value; } else {return 0;}}",
                new JavaScriptConfig(true)));

        // qualty_avg_rtwp_rx_ant_6
        aggregatorList.add(new DoubleSumAggregatorFactory("qualty_avg_rtwp_rx_ant_6_s", "qualty_avg_rtwp_rx_ant_6"));
        postAggregatorList.add(new JavaScriptPostAggregator("qualty_avg_rtwp_rx_ant_6", Arrays.asList("qualty_avg_rtwp_rx_ant_6_s", "value"),
                "function(qualty_avg_rtwp_rx_ant_6_s, value) { if(value){ return qualty_avg_rtwp_rx_ant_6_s / value; } else {return 0;}}",
                new JavaScriptConfig(true)));

        // qualty_avg_rtwp_rx_ant_7
        aggregatorList.add(new DoubleSumAggregatorFactory("qualty_avg_rtwp_rx_ant_7_s", "qualty_avg_rtwp_rx_ant_7"));
        postAggregatorList.add(new JavaScriptPostAggregator("qualty_avg_rtwp_rx_ant_7", Arrays.asList("qualty_avg_rtwp_rx_ant_7_s", "value"),
                "function(qualty_avg_rtwp_rx_ant_7_s, value) { if(value){ return qualty_avg_rtwp_rx_ant_7_s / value; } else {return 0;}}",
                new JavaScriptConfig(true)));

        // qualty_avg_rtwp_rx_ant_8
        aggregatorList.add(new DoubleSumAggregatorFactory("qualty_avg_rtwp_rx_ant_8_s", "qualty_avg_rtwp_rx_ant_8"));
        postAggregatorList.add(new JavaScriptPostAggregator("qualty_avg_rtwp_rx_ant_8", Arrays.asList("qualty_avg_rtwp_rx_ant_8_s", "value"),
                "function(qualty_avg_rtwp_rx_ant_8_s, value) { if(value){ return qualty_avg_rtwp_rx_ant_8_s / value; } else {return 0;}}",
                new JavaScriptConfig(true)));

        // mimo_ol_div
        aggregatorList.add(new DoubleSumAggregatorFactory("mimo_ol_div_sub_s", "mimo_ol_div_sub"));
        aggregatorList.add(new DoubleSumAggregatorFactory("mimo_ol_sm_sub_s", "mimo_ol_sm_sub"));
        postAggregatorList.add(new JavaScriptPostAggregator("mimo_ol_div", Arrays.asList("mimo_ol_div_sub_s", "mimo_ol_sm_sub_s"),
                "function(mimo_ol_div_sub_s, mimo_ol_sm_sub_s) { if(mimo_ol_div_sub_s + mimo_ol_sm_sub_s){ return (mimo_ol_div_sub_s / (mimo_ol_div_sub_s + mimo_ol_sm_sub_s)) * 100; } else {return 0;}}",
                new JavaScriptConfig(true)));

        // mimo_ol_mux
        postAggregatorList.add(new JavaScriptPostAggregator("mimo_ol_mux", Arrays.asList("mimo_ol_div_sub_s", "mimo_ol_sm_sub_s"),
                "function(mimo_ol_div_sub_s, mimo_ol_sm_sub_s) { if(mimo_ol_div_sub_s + mimo_ol_sm_sub_s){ return (mimo_ol_sm_sub_s / (mimo_ol_div_sub_s + mimo_ol_sm_sub_s)) * 100; } else {return 0;}}",
                new JavaScriptConfig(true)));

        // active_ue_max_enb
        aggregatorList.add(new DoubleMaxAggregatorFactory("active_ue_max_enb", "active_ue_max_enb"));

        // under_tabin1_rate
        aggregatorList.add(new DoubleSumAggregatorFactory("timing_adv_bin_1_s", "timing_adv_bin_1"));
        aggregatorList.add(new DoubleSumAggregatorFactory("sum_timing_adv_bin_1to30_s", "sum_timing_adv_bin_1to30"));
        postAggregatorList.add(new JavaScriptPostAggregator("under_tabin1_rate", Arrays.asList("timing_adv_bin_1_s", "sum_timing_adv_bin_1to30_s"),
                "function(timing_adv_bin_1_s, sum_timing_adv_bin_1to30_s) { if(sum_timing_adv_bin_1to30_s){ return (timing_adv_bin_1_s * 100) / sum_timing_adv_bin_1to30_s; } else {return 0;}}",
                new JavaScriptConfig(true)));

        // upper_tabin3_rate
        aggregatorList.add(new DoubleSumAggregatorFactory("timing_adv_bin_2_s", "timing_adv_bin_2"));
        postAggregatorList.add(new JavaScriptPostAggregator("upper_tabin3_rate", Arrays.asList("timing_adv_bin_1_s", "timing_adv_bin_2_s", "sum_timing_adv_bin_1to30_s"),
                "function(timing_adv_bin_1_s, timing_adv_bin_2_s, sum_timing_adv_bin_1to30_s) { if(sum_timing_adv_bin_1to30_s){ return ((sum_timing_adv_bin_1to30_s - timing_adv_bin_1_s - timing_adv_bin_2_s) * 100) / sum_timing_adv_bin_1to30_s; } else {return 0;}}",
                new JavaScriptConfig(true)));

        // cell_load_rrc_conn_ue_max
        aggregatorList.add(new DoubleMaxAggregatorFactory("cell_load_rrc_conn_ue_max", "cell_load_rrc_conn_ue_max"));

        // cell_load_rrc_conn_ue_avg
        aggregatorList.add(new DoubleSumAggregatorFactory("cell_load_rrc_conn_ue_avg_s", "cell_load_rrc_conn_ue_avg"));
        postAggregatorList.add(new JavaScriptPostAggregator("cell_load_rrc_conn_ue_avg", Arrays.asList("cell_load_rrc_conn_ue_avg_s", "value"),
                "function(cell_load_rrc_conn_ue_avg_s, value) { if(value){ return cell_load_rrc_conn_ue_avg_s / value; } else {return 0;}}",
                new JavaScriptConfig(true)));

        // cell_avail
        aggregatorList.add(new DoubleSumAggregatorFactory("samples_cell_avail_s", "samples_cell_avail"));
        aggregatorList.add(new DoubleSumAggregatorFactory("denom_cell_avail_s", "denom_cell_avail"));
        postAggregatorList.add(new JavaScriptPostAggregator("cell_avail", Arrays.asList("samples_cell_avail_s", "denom_cell_avail_s"),
                "function(samples_cell_avail_s, denom_cell_avail_s) { if(denom_cell_avail_s){ return (samples_cell_avail_s / denom_cell_avail_s) * 100; } else {return 0;}}",
                new JavaScriptConfig(true)));

        // rach_attc_cnt
        aggregatorList.add(new DoubleSumAggregatorFactory("rach_attc_cnt", "rach_attc_cnt"));

        // rach_suss_rate
        aggregatorList.add(new DoubleSumAggregatorFactory("rach_msg3_contention_s", "rach_msg3_contention"));
        aggregatorList.add(new DoubleSumAggregatorFactory("rach_stp_att_small_msg_s", "rach_stp_att_small_msg"));
        aggregatorList.add(new DoubleSumAggregatorFactory("rach_stp_att_large_msg_s", "rach_stp_att_large_msg"));
        postAggregatorList.add(new JavaScriptPostAggregator("rach_suss_rate", Arrays.asList("rach_msg3_contention_s", "rach_stp_att_small_msg_s", "rach_stp_att_large_msg_s"),
                "function(rach_msg3_contention_s, rach_stp_att_small_msg_s, rach_stp_att_large_msg_s) { if(rach_stp_att_small_msg_s + rach_stp_att_large_msg_s){ return (rach_msg3_contention_s / (rach_stp_att_small_msg_s + rach_stp_att_large_msg_s)) * 100; } else {return 0;}}",
                new JavaScriptConfig(true)));

        // rrc_att_rate
        postAggregatorList.add(new JavaScriptPostAggregator("rrc_att_rate", Arrays.asList("rrc_attc_cnt", "rach_attc_cnt"),
                "function(rrc_attc_cnt, rach_attc_cnt) { if(rach_attc_cnt){ return (rrc_attc_cnt / rach_attc_cnt) * 100; } else {return 0;}}",
                new JavaScriptConfig(true)));

        // ca_scell_attc_cnt
        aggregatorList.add(new DoubleSumAggregatorFactory("ca_scell_attc_cnt", "ca_scell_attc_cnt"));

        // ca_scell_suss_rate
        aggregatorList.add(new DoubleSumAggregatorFactory("ca_scell_config_succ_s", "ca_scell_config_succ"));
        postAggregatorList.add(new JavaScriptPostAggregator("ca_scell_suss_rate", Arrays.asList("ca_scell_config_succ_s", "ca_scell_attc_cnt"),
                "function(ca_scell_config_succ_s, ca_scell_attc_cnt) { if(ca_scell_attc_cnt){ return (ca_scell_config_succ_s / ca_scell_attc_cnt) * 100; } else {return 0;}}",
                new JavaScriptConfig(true)));

        // ca_avg_ca_cap_ue_for_2cc
        aggregatorList.add(new DoubleSumAggregatorFactory("ca_avg_ca_cap_ue_for_2cc_s", "ca_avg_ca_cap_ue_for_2cc"));
        postAggregatorList.add(new JavaScriptPostAggregator("ca_avg_ca_cap_ue_for_2cc", Arrays.asList("ca_avg_ca_cap_ue_for_2cc_s", "value"),
                "function(ca_avg_ca_cap_ue_for_2cc_s, value) { if(value){ return ca_avg_ca_cap_ue_for_2cc_s / value; } else {return 0;}}",
                new JavaScriptConfig(true)));

        // ca_avg_ca_cap_ue_for_3cc
        aggregatorList.add(new DoubleSumAggregatorFactory("ca_avg_ca_cap_ue_for_3cc_s", "ca_avg_ca_cap_ue_for_3cc"));
        postAggregatorList.add(new JavaScriptPostAggregator("ca_avg_ca_cap_ue_for_3cc", Arrays.asList("ca_avg_ca_cap_ue_for_3cc_s", "value"),
                "function(ca_avg_ca_cap_ue_for_3cc_s, value) { if(value){ return ca_avg_ca_cap_ue_for_3cc_s / value; } else {return 0;}}",
                new JavaScriptConfig(true)));

        // ca_avg_num_conf_ue_scell_1
        aggregatorList.add(new DoubleSumAggregatorFactory("ca_avg_num_conf_ue_scell_1_s", "ca_avg_num_conf_ue_scell_1"));
        postAggregatorList.add(new JavaScriptPostAggregator("ca_avg_num_conf_ue_scell_1", Arrays.asList("ca_avg_num_conf_ue_scell_1_s", "value"),
                "function(ca_avg_num_conf_ue_scell_1_s, value) { if(value){ return ca_avg_num_conf_ue_scell_1_s / value; } else {return 0;}}",
                new JavaScriptConfig(true)));

        // ca_avg_num_conf_ue_scell_2
        aggregatorList.add(new DoubleSumAggregatorFactory("ca_avg_num_conf_ue_scell_2_s", "ca_avg_num_conf_ue_scell_2"));
        postAggregatorList.add(new JavaScriptPostAggregator("ca_avg_num_conf_ue_scell_2", Arrays.asList("ca_avg_num_conf_ue_scell_2_s", "value"),
                "function(ca_avg_num_conf_ue_scell_2_s, value) { if(value){ return ca_avg_num_conf_ue_scell_2_s / value; } else {return 0;}}",
                new JavaScriptConfig(true)));

        // ca_avg_num_act_ue_scell_1
        aggregatorList.add(new DoubleSumAggregatorFactory("ca_avg_num_act_ue_scell_1_s", "ca_avg_num_act_ue_scell_1"));
        postAggregatorList.add(new JavaScriptPostAggregator("ca_avg_num_act_ue_scell_1", Arrays.asList("ca_avg_num_act_ue_scell_1_s", "value"),
                "function(ca_avg_num_act_ue_scell_1_s, value) { if(value){ return ca_avg_num_act_ue_scell_1_s / value; } else {return 0;}}",
                new JavaScriptConfig(true)));

        // ca_avg_num_act_ue_scell_2
        aggregatorList.add(new DoubleSumAggregatorFactory("ca_avg_num_act_ue_scell_2_s", "ca_avg_num_act_ue_scell_2"));
        postAggregatorList.add(new JavaScriptPostAggregator("ca_avg_num_act_ue_scell_2", Arrays.asList("ca_avg_num_act_ue_scell_2_s", "value"),
                "function(ca_avg_num_act_ue_scell_2_s, value) { if(value){ return ca_avg_num_act_ue_scell_2_s / value; } else {return 0;}}",
                new JavaScriptConfig(true)));

        // ca_ues_activ_2ndry_cell
        aggregatorList.add(new DoubleSumAggregatorFactory("ca_scell_active_ue_avg_s", "ca_scell_active_ue_avg"));
        aggregatorList.add(new DoubleSumAggregatorFactory("ca_dl_cap_ue_avg_s", "ca_dl_cap_ue_avg"));
        postAggregatorList.add(new JavaScriptPostAggregator("ca_ues_activ_2ndry_cell", Arrays.asList("ca_scell_active_ue_avg_s", "ca_dl_cap_ue_avg_s"),
                "function(ca_scell_active_ue_avg_s, ca_dl_cap_ue_avg_s) { if(ca_dl_cap_ue_avg_s){ return (ca_scell_active_ue_avg_s / ca_dl_cap_ue_avg_s) * 100; } else {return 0;}}",
                new JavaScriptConfig(true)));

        // ca_penetr_ca_cap_ues_2ccs
        aggregatorList.add(new DoubleSumAggregatorFactory("ca_penetr_ca_cap_ues_2ccs_s", "ca_penetr_ca_cap_ues_2ccs"));
        postAggregatorList.add(new JavaScriptPostAggregator("ca_penetr_ca_cap_ues_2ccs", Arrays.asList("ca_penetr_ca_cap_ues_2ccs_s", "value"),
                "function(ca_penetr_ca_cap_ues_2ccs_s, value) { if(value){ return ca_penetr_ca_cap_ues_2ccs_s / value; } else {return 0;}}",
                new JavaScriptConfig(true)));

        // ca_penetr_ca_cap_ues_3ccs
        aggregatorList.add(new DoubleSumAggregatorFactory("ca_penetr_ca_cap_ues_3ccs_s", "ca_penetr_ca_cap_ues_3ccs"));
        postAggregatorList.add(new JavaScriptPostAggregator("ca_penetr_ca_cap_ues_3ccs", Arrays.asList("ca_penetr_ca_cap_ues_3ccs_s", "value"),
                "function(ca_penetr_ca_cap_ues_3ccs_s, value) { if(value){ return ca_penetr_ca_cap_ues_3ccs_s / value; } else {return 0;}}",
                new JavaScriptConfig(true)));

        // ca_use_ca_by_ues_data_in_buff
        aggregatorList.add(new DoubleSumAggregatorFactory("sum_active_ue_data_dl_s", "sum_active_ue_data_dl"));
        aggregatorList.add(new DoubleSumAggregatorFactory("denom_active_ue_data_dl_s", "denom_active_ue_data_dl"));
        postAggregatorList.add(new JavaScriptPostAggregator("ca_use_ca_by_ues_data_in_buff", Arrays.asList("ca_dl_cap_ue_avg_s", "sum_active_ue_data_dl_s", "denom_active_ue_data_dl_s"),
                "function(ca_dl_cap_ue_avg_s, sum_active_ue_data_dl_s, denom_active_ue_data_dl_s) { if(sum_active_ue_data_dl_s && denom_active_ue_data_dl_s){ return (100 * (ca_dl_cap_ue_avg_s / 100)) / (sum_active_ue_data_dl_s / ca_dl_cap_ue_avg_s); } else {return 0;}}",
                new JavaScriptConfig(true)));


        // ca_scel_offload_r
        aggregatorList.add(new DoubleSumAggregatorFactory("rlc_pdu_dl_vol_ca_scell_s", "rlc_pdu_dl_vol_ca_scell"));
        aggregatorList.add(new DoubleSumAggregatorFactory("rlc_pdu_vol_transmitted_s", "rlc_pdu_vol_transmitted"));
        postAggregatorList.add(new JavaScriptPostAggregator("ca_scel_offload_r", Arrays.asList("rlc_pdu_dl_vol_ca_scell_s", "rlc_pdu_vol_transmitted_s"),
                "function(rlc_pdu_dl_vol_ca_scell_s, rlc_pdu_vol_transmitted_s) { if(rlc_pdu_vol_transmitted_s){ return (100 * rlc_pdu_dl_vol_ca_scell_s) / rlc_pdu_vol_transmitted_s; } else {return 0;}}",
                new JavaScriptConfig(true)));

        // ca_data_vol_scell_dl
        postAggregatorList.add(new JavaScriptPostAggregator("ca_data_vol_scell_dl", Arrays.asList("rlc_pdu_dl_vol_ca_scell_s"),
                "function(rlc_pdu_dl_vol_ca_scell_s) { if(rlc_pdu_dl_vol_ca_scell_s){ return rlc_pdu_dl_vol_ca_scell_s / 1000; } else {return 0;}}",
                new JavaScriptConfig(true)));

        // ca_thp_scell_dl
        postAggregatorList.add(new JavaScriptPostAggregator("ca_thp_scell_dl", Arrays.asList("rlc_pdu_dl_vol_ca_scell_s"),
                "function(rlc_pdu_dl_vol_ca_scell_s) { if(rlc_pdu_dl_vol_ca_scell_s){ return (rlc_pdu_dl_vol_ca_scell_s / 1000) / (60 * 60); } else {return 0;}}",
                new JavaScriptConfig(true)));

        // lb_high_cell_load
        aggregatorList.add(new DoubleSumAggregatorFactory("high_cell_load_lb_sub_s", "high_cell_load_lb_sub"));
        postAggregatorList.add(new JavaScriptPostAggregator("lb_high_cell_load", Arrays.asList("high_cell_load_lb_sub_s"),
                "function(high_cell_load_lb_sub_s) { if(high_cell_load_lb_sub_s){ return (high_cell_load_lb_sub_s / (60 * 60)) * 100; } else {return 0;}}",
                new JavaScriptConfig(true)));

        // prb_usg_dl
        aggregatorList.add(new DoubleSumAggregatorFactory("prb_usg_dl_s", "prb_usg_dl"));
        postAggregatorList.add(new JavaScriptPostAggregator("prb_usg_dl", Arrays.asList("prb_usg_dl_s", "value"),
                "function(prb_usg_dl_s, value) { if(value){ return prb_usg_dl_s / value; } else {return 0;}}",
                new JavaScriptConfig(true)));

        // prb_usg_ul
        aggregatorList.add(new DoubleSumAggregatorFactory("prb_usg_ul_s", "prb_usg_ul"));
        postAggregatorList.add(new JavaScriptPostAggregator("prb_usg_ul", Arrays.asList("prb_usg_ul_s", "value"),
                "function(prb_usg_ul_s, value) { if(value){ return prb_usg_ul_s / value; } else {return 0;}}",
                new JavaScriptConfig(true)));

        // cell_rsce_agg1_used_pdcch
        aggregatorList.add(new DoubleSumAggregatorFactory("cell_rsce_agg1_used_pdcch", "cell_rsce_agg1_used_pdcch"));
        // cell_rsce_agg2_used_pdcch
        aggregatorList.add(new DoubleSumAggregatorFactory("cell_rsce_agg2_used_pdcch", "cell_rsce_agg2_used_pdcch"));
        // cell_rsce_agg3_used_pdcch
        aggregatorList.add(new DoubleSumAggregatorFactory("cell_rsce_agg3_used_pdcch", "cell_rsce_agg3_used_pdcch"));
        // cell_rsce_agg4_used_pdcch
        aggregatorList.add(new DoubleSumAggregatorFactory("cell_rsce_agg4_used_pdcch", "cell_rsce_agg4_used_pdcch"));
        // cell_rsce_agg1_blocked_pdcch
        aggregatorList.add(new DoubleSumAggregatorFactory("cell_rsce_agg1_blocked_pdcch", "cell_rsce_agg1_blocked_pdcch"));
        // cell_rsce_agg2_blocked_pdcch
        aggregatorList.add(new DoubleSumAggregatorFactory("cell_rsce_agg2_blocked_pdcch", "cell_rsce_agg2_blocked_pdcch"));
        // cell_rsce_agg3_blocked_pdcch
        aggregatorList.add(new DoubleSumAggregatorFactory("cell_rsce_agg3_blocked_pdcch", "cell_rsce_agg3_blocked_pdcch"));
        // cell_rsce_agg4_blocked_pdcch
        aggregatorList.add(new DoubleSumAggregatorFactory("cell_rsce_agg4_blocked_pdcch", "cell_rsce_agg4_blocked_pdcch"));

        // agg1_block_rate
        postAggregatorList.add(new JavaScriptPostAggregator("agg1_block_rate", Arrays.asList("cell_rsce_agg1_blocked_pdcch", "cell_rsce_agg1_used_pdcch"),
                "function(cell_rsce_agg1_blocked_pdcch, cell_rsce_agg1_used_pdcch) { if(cell_rsce_agg1_used_pdcch){ return (cell_rsce_agg1_blocked_pdcch * 100) / cell_rsce_agg1_used_pdcch; } else {return 0;}}",
                new JavaScriptConfig(true)));
        // agg2_block_rate
        postAggregatorList.add(new JavaScriptPostAggregator("agg2_block_rate", Arrays.asList("cell_rsce_agg2_blocked_pdcch", "cell_rsce_agg2_used_pdcch"),
                "function(cell_rsce_agg2_blocked_pdcch, cell_rsce_agg2_used_pdcch) { if(cell_rsce_agg2_used_pdcch){ return (cell_rsce_agg2_blocked_pdcch * 100) / cell_rsce_agg2_used_pdcch; } else {return 0;}}",
                new JavaScriptConfig(true)));
        // agg4_block_rate
        postAggregatorList.add(new JavaScriptPostAggregator("agg4_block_rate", Arrays.asList("cell_rsce_agg3_blocked_pdcch", "cell_rsce_agg3_used_pdcch"),
                "function(cell_rsce_agg3_blocked_pdcch, cell_rsce_agg3_used_pdcch) { if(cell_rsce_agg3_used_pdcch){ return (cell_rsce_agg3_blocked_pdcch * 100) / cell_rsce_agg3_used_pdcch; } else {return 0;}}",
                new JavaScriptConfig(true)));
        // agg8_block_rate
        postAggregatorList.add(new JavaScriptPostAggregator("agg8_block_rate", Arrays.asList("cell_rsce_agg4_blocked_pdcch", "cell_rsce_agg4_used_pdcch"),
                "function(cell_rsce_agg4_blocked_pdcch, cell_rsce_agg4_used_pdcch) { if(cell_rsce_agg4_used_pdcch){ return (cell_rsce_agg4_blocked_pdcch * 100) / cell_rsce_agg4_used_pdcch; } else {return 0;}}",
                new JavaScriptConfig(true)));

        // harq_retrans_dl
        aggregatorList.add(new DoubleSumAggregatorFactory("pdsch_trans_nack_mcs_sum0to31_s", "pdsch_trans_nack_mcs_sum0to31"));
        aggregatorList.add(new DoubleSumAggregatorFactory("pdsch_trans_using_mcs_sum0to31_s", "pdsch_trans_using_mcs_sum0to31"));
        postAggregatorList.add(new JavaScriptPostAggregator("harq_retrans_dl", Arrays.asList("pdsch_trans_nack_mcs_sum0to31_s", "pdsch_trans_using_mcs_sum0to31_s"),
                "function(pdsch_trans_nack_mcs_sum0to31_s, pdsch_trans_using_mcs_sum0to31_s) { if(pdsch_trans_using_mcs_sum0to31_s){ return (pdsch_trans_nack_mcs_sum0to31_s / pdsch_trans_using_mcs_sum0to31_s) * 100; } else {return 0;}}",
                new JavaScriptConfig(true)));

        // harq_retrans_ul
        aggregatorList.add(new DoubleSumAggregatorFactory("pusch_trans_nack_mcs_sum0to10_s", "pusch_trans_nack_mcs_sum0to10"));
        aggregatorList.add(new DoubleSumAggregatorFactory("pusch_trans_nack_mcs_sum11to20_s", "pusch_trans_nack_mcs_sum11to20"));
        aggregatorList.add(new DoubleSumAggregatorFactory("pusch_trans_nack_mcs_sum21to28_s", "pusch_trans_nack_mcs_sum21to28"));
        aggregatorList.add(new DoubleSumAggregatorFactory("pusch_trans_using_mcs_sum0to10_s", "pusch_trans_using_mcs_sum0to10"));
        aggregatorList.add(new DoubleSumAggregatorFactory("pusch_trans_using_mcs_sum11to20_s", "pusch_trans_using_mcs_sum11to20"));
        aggregatorList.add(new DoubleSumAggregatorFactory("pusch_trans_using_mcs_sum21to28_s", "pusch_trans_using_mcs_sum21to28"));
        postAggregatorList.add(new JavaScriptPostAggregator("harq_retrans_ul",
                Arrays.asList("pusch_trans_nack_mcs_sum0to10_s", "pusch_trans_nack_mcs_sum11to20_s", "pusch_trans_nack_mcs_sum21to28_s", "pusch_trans_using_mcs_sum0to10_s", "pusch_trans_using_mcs_sum11to20_s", "pusch_trans_using_mcs_sum21to28_s"),
                "function(pusch_trans_nack_mcs_sum0to10_s, pusch_trans_nack_mcs_sum11to20_s, pusch_trans_nack_mcs_sum21to28_s, pusch_trans_using_mcs_sum0to10_s, pusch_trans_using_mcs_sum11to20_s, pusch_trans_using_mcs_sum21to28_s)" +
                        " { if(pusch_trans_using_mcs_sum0to10_s + pusch_trans_using_mcs_sum11to20_s + pusch_trans_using_mcs_sum21to28_s)" +
                        " { return ((pusch_trans_nack_mcs_sum0to10_s + pusch_trans_nack_mcs_sum11to20_s + pusch_trans_nack_mcs_sum21to28_s) / (pusch_trans_using_mcs_sum0to10_s + pusch_trans_using_mcs_sum11to20_s + pusch_trans_using_mcs_sum21to28_s)) * 100; } else {return 0;}}",
                new JavaScriptConfig(true)));

        // rlc_retrans_dl
        aggregatorList.add(new DoubleSumAggregatorFactory("rlc_pdu_re_trans_s", "rlc_pdu_re_trans"));
        aggregatorList.add(new DoubleSumAggregatorFactory("rlc_pdu_first_trans_s", "rlc_pdu_first_trans"));
        postAggregatorList.add(new JavaScriptPostAggregator("rlc_retrans_dl", Arrays.asList("rlc_pdu_re_trans_s", "rlc_pdu_first_trans_s"),
                "function(rlc_pdu_re_trans_s, rlc_pdu_first_trans_s) { if(rlc_pdu_first_trans_s + rlc_pdu_re_trans_s){ return (rlc_pdu_re_trans_s / (rlc_pdu_first_trans_s + rlc_pdu_re_trans_s)) * 100; } else {return 0;}}",
                new JavaScriptConfig(true)));

        // rlc_retrans_ul
        aggregatorList.add(new DoubleSumAggregatorFactory("ul_rlc_pdu_retr_req_s", "ul_rlc_pdu_retr_req"));
        aggregatorList.add(new DoubleSumAggregatorFactory("ul_rlc_pdu_rec_tot_s", "ul_rlc_pdu_rec_tot"));
        postAggregatorList.add(new JavaScriptPostAggregator("rlc_retrans_ul", Arrays.asList("ul_rlc_pdu_retr_req_s", "ul_rlc_pdu_rec_tot_s"),
                "function(ul_rlc_pdu_retr_req_s, ul_rlc_pdu_rec_tot_s) { if(ul_rlc_pdu_rec_tot_s){ return (ul_rlc_pdu_retr_req_s / ul_rlc_pdu_rec_tot_s) * 100; } else {return 0;}}",
                new JavaScriptConfig(true)));

        // qualty_avg_mcs_pdsch
        aggregatorList.add(new DoubleSumAggregatorFactory("qualty_avg_mcs_pdsch_s", "qualty_avg_mcs_pdsch"));
        postAggregatorList.add(new JavaScriptPostAggregator("qualty_avg_mcs_pdsch", Arrays.asList("qualty_avg_mcs_pdsch_s", "value"),
                "function(qualty_avg_mcs_pdsch_s, value) { if(value){ return qualty_avg_mcs_pdsch_s / value; } else {return 0;}}",
                new JavaScriptConfig(true)));

        // qualty_avg_mcs_pusch
        aggregatorList.add(new DoubleSumAggregatorFactory("qualty_avg_mcs_pusch_s", "qualty_avg_mcs_pusch"));
        postAggregatorList.add(new JavaScriptPostAggregator("qualty_avg_mcs_pusch", Arrays.asList("qualty_avg_mcs_pusch_s", "value"),
                "function(qualty_avg_mcs_pusch_s, value) { if(value){ return qualty_avg_mcs_pusch_s / value; } else {return 0;}}",
                new JavaScriptConfig(true)));

        // active_ue_avg_dl
        postAggregatorList.add(new JavaScriptPostAggregator("active_ue_avg_dl", Arrays.asList("sum_active_ue_data_dl_s", "denom_active_ue_data_dl_s"),
                "function(sum_active_ue_data_dl_s, denom_active_ue_data_dl_s) { if(denom_active_ue_data_dl_s){ return sum_active_ue_data_dl_s / denom_active_ue_data_dl_s; } else {return 0;}}",
                new JavaScriptConfig(true)));

        // active_ue_avg_ul
        aggregatorList.add(new DoubleSumAggregatorFactory("sum_active_ue_data_ul_s", "sum_active_ue_data_ul"));
        aggregatorList.add(new DoubleSumAggregatorFactory("denom_active_ue_data_ul_s", "denom_active_ue_data_ul"));
        postAggregatorList.add(new JavaScriptPostAggregator("active_ue_avg_ul", Arrays.asList("sum_active_ue_data_ul_s", "denom_active_ue_data_ul_s"),
                "function(sum_active_ue_data_ul_s, denom_active_ue_data_ul_s) { if(denom_active_ue_data_ul_s){ return sum_active_ue_data_ul_s / denom_active_ue_data_ul_s; } else {return 0;}}",
                new JavaScriptConfig(true)));

        // active_ue_max_dl
        aggregatorList.add(new DoubleMaxAggregatorFactory("active_ue_max_dl", "active_ue_max_dl"));

        // active_ue_max_ul
        aggregatorList.add(new DoubleMaxAggregatorFactory("active_ue_max_ul", "active_ue_max_ul"));

        // active_ue_avg_rrc_conn
        aggregatorList.add(new DoubleSumAggregatorFactory("sum_rrc_conn_ue_s", "sum_rrc_conn_ue"));
        aggregatorList.add(new DoubleSumAggregatorFactory("denom_rrc_conn_ue_s", "denom_rrc_conn_ue"));
        postAggregatorList.add(new JavaScriptPostAggregator("active_ue_avg_rrc_conn", Arrays.asList("sum_rrc_conn_ue_s", "denom_rrc_conn_ue_s"),
                "function(sum_rrc_conn_ue_s, denom_rrc_conn_ue_s) { if(denom_rrc_conn_ue_s){ return sum_rrc_conn_ue_s / denom_rrc_conn_ue_s; } else {return 0;}}",
                new JavaScriptConfig(true)));

        // cell_load_act_ue_max
        aggregatorList.add(new DoubleMaxAggregatorFactory("cell_load_act_ue_max", "cell_load_act_ue_max"));

        // ovld_lev1
        aggregatorList.add(new DoubleSumAggregatorFactory("ovld_lev1", "ovld_lev1"));

        // ovld_lev2
        aggregatorList.add(new DoubleSumAggregatorFactory("ovld_lev2", "ovld_lev2"));

        // ue_bler_mcs_pusch_bler_mcs22
        aggregatorList.add(new DoubleSumAggregatorFactory("ue_bler_mcs_pusch_bler_mcs22", "ue_bler_mcs_pusch_bler_mcs22"));

        // ue_bler_mcs_pusch_bler_mcs23
        aggregatorList.add(new DoubleSumAggregatorFactory("ue_bler_mcs_pusch_bler_mcs23", "ue_bler_mcs_pusch_bler_mcs23"));

        // ue_bler_mcs_pusch_bler_mcs24
        aggregatorList.add(new DoubleSumAggregatorFactory("ue_bler_mcs_pusch_bler_mcs24", "ue_bler_mcs_pusch_bler_mcs24"));

        // ul_bler_mdul_ul_total_pusch_bler
        aggregatorList.add(new DoubleSumAggregatorFactory("ul_bler_mdul_ul_total_pusch_bler", "ul_bler_mdul_ul_total_pusch_bler"));

        // ul_bler_mdul_ul_qpsk_bler
        aggregatorList.add(new DoubleSumAggregatorFactory("ul_bler_mdul_ul_qpsk_bler", "ul_bler_mdul_ul_qpsk_bler"));

        // ul_bler_mdul_ul_16qam_bler
        aggregatorList.add(new DoubleSumAggregatorFactory("ul_bler_mdul_ul_16qam_bler", "ul_bler_mdul_ul_16qam_bler"));

        // ul_bler_mdul_ul_64qam_bler
        aggregatorList.add(new DoubleSumAggregatorFactory("ul_bler_mdul_ul_64qam_bler", "ul_bler_mdul_ul_64qam_bler"));

        // ul_bler_mdul_dl_total_pdsch_bler
        aggregatorList.add(new DoubleSumAggregatorFactory("ul_bler_mdul_dl_total_pdsch_bler", "ul_bler_mdul_dl_total_pdsch_bler"));

        // ul_bler_mdul_dl_qpsk_bler
        aggregatorList.add(new DoubleSumAggregatorFactory("ul_bler_mdul_dl_qpsk_bler", "ul_bler_mdul_dl_qpsk_bler"));

        // ul_bler_mdul_dl_16qam_bler
        aggregatorList.add(new DoubleSumAggregatorFactory("ul_bler_mdul_dl_16qam_bler", "ul_bler_mdul_dl_16qam_bler"));

        // ul_bler_mdul_dl_64qam_bler
        aggregatorList.add(new DoubleSumAggregatorFactory("ul_bler_mdul_dl_64qam_bler", "ul_bler_mdul_dl_64qam_bler"));

        // TODO 분모 분자 동일 무조건 100%
        // qam_256_dl_total_pdsch_bler
        aggregatorList.add(new DoubleSumAggregatorFactory("schdl_256qam_pdsch_qpsk_s", "schdl_256qam_pdsch_qpsk"));
        aggregatorList.add(new DoubleSumAggregatorFactory("schdl_256qam_pdsch_16qam_s", "schdl_256qam_pdsch_16qam"));
        aggregatorList.add(new DoubleSumAggregatorFactory("schdl_256qam_pdsch_64qam_s", "schdl_256qam_pdsch_64qam"));
        aggregatorList.add(new DoubleSumAggregatorFactory("schdl_256qam_pdsch_256qam_s", "schdl_256qam_pdsch_256qam"));
//        postAggregatorList.add(new JavaScriptPostAggregator("qam_256_dl_total_pdsch_bler", Arrays.asList("schdl_256qam_pdsch_qpsk_s", "schdl_256qam_pdsch_16qam_s", "schdl_256qam_pdsch_64qam_s", "schdl_256qam_pdsch_256qam_s"),
//                "function(schdl_256qam_pdsch_qpsk_s, schdl_256qam_pdsch_16qam_s, schdl_256qam_pdsch_64qam_s, schdl_256qam_pdsch_256qam_s) " +
//                        "{ if(schdl_256qam_pdsch_qpsk_s+schdl_256qam_pdsch_16qam_s+schdl_256qam_pdsch_64qam_s+schdl_256qam_pdsch_256qam_s) " +
//                        "{ return ((schdl_256qam_pdsch_qpsk_s+schdl_256qam_pdsch_16qam_s+schdl_256qam_pdsch_64qam_s+schdl_256qam_pdsch_256qam_s) / (schdl_256qam_pdsch_qpsk_s+schdl_256qam_pdsch_16qam_s+schdl_256qam_pdsch_64qam_s+schdl_256qam_pdsch_256qam_s)) * 100; } else {return 0;}}",
//                new JavaScriptConfig(true)));
        postAggregatorList.add(new JavaScriptPostAggregator("qam_256_dl_total_pdsch_bler", Arrays.asList("schdl_256qam_pdsch_qpsk_s"),
                "function(schdl_256qam_pdsch_qpsk_s) { return 100;}",
                new JavaScriptConfig(true)));

        // qam_256_pdsch_bler_qpsk
        aggregatorList.add(new DoubleSumAggregatorFactory("schdl_256qam_pdsch_nack_qpsk_s", "schdl_256qam_pdsch_nack_qpsk"));
        postAggregatorList.add(new JavaScriptPostAggregator("qam_256_pdsch_bler_qpsk", Arrays.asList("schdl_256qam_pdsch_nack_qpsk_s", "schdl_256qam_pdsch_qpsk_s"),
                "function(schdl_256qam_pdsch_nack_qpsk_s, schdl_256qam_pdsch_qpsk_s) { if(schdl_256qam_pdsch_qpsk_s){ return (schdl_256qam_pdsch_nack_qpsk_s / schdl_256qam_pdsch_qpsk_s) * 100; } else {return 0;}}",
                new JavaScriptConfig(true)));

        // qam_256_pdsch_bler_16qam
        aggregatorList.add(new DoubleSumAggregatorFactory("schdl_256qam_pdsch_nack_16qam_s", "schdl_256qam_pdsch_nack_16qam"));
        postAggregatorList.add(new JavaScriptPostAggregator("qam_256_pdsch_bler_16qam", Arrays.asList("schdl_256qam_pdsch_nack_16qam_s", "schdl_256qam_pdsch_16qam_s"),
                "function(schdl_256qam_pdsch_nack_16qam_s, schdl_256qam_pdsch_16qam_s) { if(schdl_256qam_pdsch_16qam_s){ return (schdl_256qam_pdsch_nack_16qam_s / schdl_256qam_pdsch_16qam_s) * 100; } else {return 0;}}",
                new JavaScriptConfig(true)));

        // qam_256_pdsch_bler_64qam
        aggregatorList.add(new DoubleSumAggregatorFactory("schdl_256qam_pdsch_nack_64qam_s", "schdl_256qam_pdsch_nack_64qam"));
        postAggregatorList.add(new JavaScriptPostAggregator("qam_256_pdsch_bler_64qam", Arrays.asList("schdl_256qam_pdsch_nack_64qam_s", "schdl_256qam_pdsch_64qam_s"),
                "function(schdl_256qam_pdsch_nack_64qam_s, schdl_256qam_pdsch_64qam_s) { if(schdl_256qam_pdsch_64qam_s){ return (schdl_256qam_pdsch_nack_64qam_s / schdl_256qam_pdsch_64qam_s) * 100; } else {return 0;}}",
                new JavaScriptConfig(true)));

        // qam_256_pdsch_bler_256qam
        aggregatorList.add(new DoubleSumAggregatorFactory("schdl_256qam_pdsch_nack_256qam_s", "schdl_256qam_pdsch_nack_256qam"));
        postAggregatorList.add(new JavaScriptPostAggregator("qam_256_pdsch_bler_256qam", Arrays.asList("schdl_256qam_pdsch_nack_256qam_s", "schdl_256qam_pdsch_256qam_s"),
                "function(schdl_256qam_pdsch_nack_256qam_s, schdl_256qam_pdsch_256qam_s) { if(schdl_256qam_pdsch_256qam_s){ return (schdl_256qam_pdsch_nack_256qam_s / schdl_256qam_pdsch_256qam_s) * 100; } else {return 0;}}",
                new JavaScriptConfig(true)));


        aggregatorList.add(new DoubleSumAggregatorFactory("pdcch_1_ofdm_symbol_s", "pdcch_1_ofdm_symbol"));
        aggregatorList.add(new DoubleSumAggregatorFactory("pdcch_2_ofdm_symbols_s", "pdcch_2_ofdm_symbols"));
        aggregatorList.add(new DoubleSumAggregatorFactory("pdcch_3_ofdm_symbols_s", "pdcch_3_ofdm_symbols"));

        // pdcch_1_ofdm_symbol
        postAggregatorList.add(new JavaScriptPostAggregator("pdcch_1_ofdm_symbol", Arrays.asList("pdcch_1_ofdm_symbol_s", "pdcch_2_ofdm_symbols_s", "pdcch_3_ofdm_symbols_s"),
                "function(pdcch_1_ofdm_symbol_s, pdcch_2_ofdm_symbols_s, pdcch_3_ofdm_symbols_s) { if(pdcch_1_ofdm_symbol_s + pdcch_2_ofdm_symbols_s + pdcch_3_ofdm_symbols_s){" +
                        " return pdcch_1_ofdm_symbol_s  * 100 / (pdcch_1_ofdm_symbol_s + pdcch_2_ofdm_symbols_s + pdcch_3_ofdm_symbols_s); } else {return 0;}}",
                new JavaScriptConfig(true)));
        // pdcch_2_ofdm_symbols
        postAggregatorList.add(new JavaScriptPostAggregator("pdcch_2_ofdm_symbols", Arrays.asList("pdcch_1_ofdm_symbol_s", "pdcch_2_ofdm_symbols_s", "pdcch_3_ofdm_symbols_s"),
                "function(pdcch_1_ofdm_symbol_s, pdcch_2_ofdm_symbols_s, pdcch_3_ofdm_symbols_s) { if(pdcch_1_ofdm_symbol_s + pdcch_2_ofdm_symbols_s + pdcch_3_ofdm_symbols_s){" +
                        " return pdcch_2_ofdm_symbols_s  * 100 / (pdcch_1_ofdm_symbol_s + pdcch_2_ofdm_symbols_s + pdcch_3_ofdm_symbols_s); } else {return 0;}}",
                new JavaScriptConfig(true)));
        // pdcch_3_ofdm_symbols
        postAggregatorList.add(new JavaScriptPostAggregator("pdcch_3_ofdm_symbols", Arrays.asList("pdcch_1_ofdm_symbol_s", "pdcch_2_ofdm_symbols_s", "pdcch_3_ofdm_symbols_s"),
                "function(pdcch_1_ofdm_symbol_s, pdcch_2_ofdm_symbols_s, pdcch_3_ofdm_symbols_s) { if(pdcch_1_ofdm_symbol_s + pdcch_2_ofdm_symbols_s + pdcch_3_ofdm_symbols_s){" +
                        " return pdcch_3_ofdm_symbols_s  * 100 / (pdcch_1_ofdm_symbol_s + pdcch_2_ofdm_symbols_s + pdcch_3_ofdm_symbols_s); } else {return 0;}}",
                new JavaScriptConfig(true)));

        // rre_attc_cnt
        aggregatorList.add(new DoubleSumAggregatorFactory("rre_attc_cnt", "rre_attc_cnt"));

        // rre_occr_rate
        postAggregatorList.add(new JavaScriptPostAggregator("rre_occr_rate", Arrays.asList("rre_attc_cnt", "rrc_attc_cnt"),
                "function(rre_attc_cnt, rrc_attc_cnt) { if(rrc_attc_cnt){ return rre_attc_cnt / rrc_attc_cnt * 100; } else {return 0;}}",
                new JavaScriptConfig(true)));

        // rre_suss_rate
        aggregatorList.add(new DoubleSumAggregatorFactory("rrc_con_re_estab_succ_s", "rrc_con_re_estab_succ"));
        postAggregatorList.add(new JavaScriptPostAggregator("rre_suss_rate", Arrays.asList("rrc_con_re_estab_succ_s", "rre_attc_cnt"),
                "function(rrc_con_re_estab_succ_s, rre_attc_cnt) { if(rrc_con_re_estab_succ_s){ return rrc_con_re_estab_succ_s / rre_attc_cnt * 100; } else {return 0;}}",
                new JavaScriptConfig(true)));

        // active_ue_avg_enb
        aggregatorList.add(new DoubleSumAggregatorFactory("active_ue_avg_enb", "active_ue_avg_enb"));

        ret.put("aggregator", aggregatorList);
        ret.put("postAggregator", postAggregatorList);

        return ret;
    }

    /**
     * Elg Aggregator 목록 조회
     * @return
     */
    private Map<String, Object> getElgAggregator() {

        Map<String, Object> ret = new HashMap<>();
        List<AggregatorFactory> aggregatorList = new ArrayList<>();
        List<PostAggregator> postAggregatorList = new ArrayList<>();

        // count
        aggregatorList.add(new CountAggregatorFactory("value"));

        // rrc_attc_cnt
        aggregatorList.add(new DoubleSumAggregatorFactory("rrc_attc_cnt", "rrc_attc_cnt"));

        // rrc_succ_cnt
        aggregatorList.add(new DoubleSumAggregatorFactory("rrc_succ_cnt", "rrc_succ_cnt"));

        // rrc_succ_rate
        postAggregatorList.add(new JavaScriptPostAggregator("rrc_succ_rate", Arrays.asList("rrc_succ_cnt", "rrc_attc_cnt"),
                "function(rrc_succ_cnt, rrc_attc_cnt) { if(rrc_attc_cnt){ return rrc_succ_cnt / rrc_attc_cnt * 100; } else {return 0;}}",
                new JavaScriptConfig(true)));

        // erab_attc_cnt
        aggregatorList.add(new DoubleSumAggregatorFactory("erab_attc_cnt", "erab_attc_cnt"));

        // erab_succ_cnt
        aggregatorList.add(new DoubleSumAggregatorFactory("erab_succ_cnt", "erab_succ_cnt"));

        // erab_succ_rate
        postAggregatorList.add(new JavaScriptPostAggregator("erab_succ_rate", Arrays.asList("erab_succ_cnt", "erab_attc_cnt"),
                "function(erab_succ_cnt, erab_attc_cnt) { if(erab_attc_cnt){ return erab_succ_cnt / erab_attc_cnt * 100; } else {return 0;}}",
                new JavaScriptConfig(true)));

        // cd_call
        aggregatorList.add(new DoubleSumAggregatorFactory("cd_call", "cd_call"));

        // cd_rate
        aggregatorList.add(new DoubleSumAggregatorFactory("pmerabrelenbsum_s", "pmerabrelenbsum"));
        postAggregatorList.add(new JavaScriptPostAggregator("cd_rate", Arrays.asList("cd_call", "pmerabrelenbsum_s"),
                "function(cd_call, pmerabrelenbsum_s) { if(pmerabrelenbsum_s){ return cd_call / pmerabrelenbsum_s * 100; } else {return 0;}}",
                new JavaScriptConfig(true)));

        // pm_rrc_conn_mean
        aggregatorList.add(new DoubleSumAggregatorFactory("pmrrcconnlevsum_s", "pmrrcconnlevsum"));
        aggregatorList.add(new DoubleSumAggregatorFactory("pmrrcconnlevsamp_s", "pmrrcconnlevsamp"));
        postAggregatorList.add(new JavaScriptPostAggregator("pm_rrc_conn_mean", Arrays.asList("pmrrcconnlevsum_s", "pmrrcconnlevsamp_s"),
                "function(pmrrcconnlevsum_s, pmrrcconnlevsamp_s) { if(pmrrcconnlevsamp_s){ return pmrrcconnlevsum_s / pmrrcconnlevsamp_s; } else {return 0;}}",
                new JavaScriptConfig(true)));

        // pm_rrc_conn_mean_cnum1
        aggregatorList.add(new DoubleSumAggregatorFactory("pmrrcconnlevsum_cnum1_s", "pmrrcconnlevsum_cnum1"));
        aggregatorList.add(new DoubleSumAggregatorFactory("pmrrcconnlevsamp_cnum1_s", "pmrrcconnlevsamp_cnum1"));
        postAggregatorList.add(new JavaScriptPostAggregator("pm_rrc_conn_mean_cnum1", Arrays.asList("pmrrcconnlevsum_cnum1_s", "pmrrcconnlevsamp_cnum1_s"),
                "function(pmrrcconnlevsum_cnum1_s, pmrrcconnlevsamp_cnum1_s) { if(pmrrcconnlevsamp_cnum1_s){ return pmrrcconnlevsum_cnum1_s / pmrrcconnlevsamp_cnum1_s; } else {return 0;}}",
                new JavaScriptConfig(true)));

        // pm_rrc_conn_mean_cnum2
        aggregatorList.add(new DoubleSumAggregatorFactory("pmrrcconnlevsum_cnum2_s", "pmrrcconnlevsum_cnum2"));
        aggregatorList.add(new DoubleSumAggregatorFactory("pmrrcconnlevsamp_cnum2_s", "pmrrcconnlevsamp_cnum2"));
        postAggregatorList.add(new JavaScriptPostAggregator("pm_rrc_conn_mean_cnum2", Arrays.asList("pmrrcconnlevsum_cnum2_s", "pmrrcconnlevsamp_cnum2_s"),
                "function(pmrrcconnlevsum_cnum2_s, pmrrcconnlevsamp_cnum2_s) { if(pmrrcconnlevsamp_cnum2_s){ return pmrrcconnlevsum_cnum2_s / pmrrcconnlevsamp_cnum2_s; } else {return 0;}}",
                new JavaScriptConfig(true)));

        // page_dis_cnt
        aggregatorList.add(new DoubleSumAggregatorFactory("page_dis_cnt", "page_dis_cnt"));

        // pmho_att
        aggregatorList.add(new DoubleSumAggregatorFactory("pmho_att", "pmho_att"));

        // pmho_succ
        aggregatorList.add(new DoubleSumAggregatorFactory("pmho_succ", "pmho_succ"));

        // pmho_succ_rate
        postAggregatorList.add(new JavaScriptPostAggregator("pmho_succ_rate", Arrays.asList("pmho_succ", "pmho_att"),
                "function(pmho_succ, pmho_att) { if(pmho_att){ return pmho_succ / pmho_att * 100; } else {return 0;}}",
                new JavaScriptConfig(true)));

        // rd_acc_att
        aggregatorList.add(new DoubleSumAggregatorFactory("rd_acc_att", "rd_acc_att"));

        // rd_acc_succ
        aggregatorList.add(new DoubleSumAggregatorFactory("rd_acc_succ", "rd_acc_succ"));

        // rd_acc_succ_rate
        postAggregatorList.add(new JavaScriptPostAggregator("rd_acc_succ_rate", Arrays.asList("rd_acc_succ", "rd_acc_att"),
                "function(rd_acc_succ, rd_acc_att) { if(rd_acc_att){ return rd_acc_succ / rd_acc_att * 100; } else {return 0;}}",
                new JavaScriptConfig(true)));

        // pmprbuseddlavg
        aggregatorList.add(new DoubleSumAggregatorFactory("pmprbuseddlavg_s", "pmprbuseddlavg"));
        postAggregatorList.add(new JavaScriptPostAggregator("pmprbuseddlavg", Arrays.asList("pmprbuseddlavg_s", "value"),
                "function(pmprbuseddlavg_s, value) { if(value){ return pmprbuseddlavg_s / value; } else {return 0;}}",
                new JavaScriptConfig(true)));

        // pmprbuseddlavg_cnum1
        aggregatorList.add(new DoubleSumAggregatorFactory("pmprbuseddlavg_cnum1_s", "pmprbuseddlavg_cnum1"));
        postAggregatorList.add(new JavaScriptPostAggregator("pmprbuseddlavg_cnum1", Arrays.asList("pmprbuseddlavg_cnum1_s", "value"),
                "function(pmprbuseddlavg_cnum1_s, value) { if(value){ return pmprbuseddlavg_cnum1_s / value; } else {return 0;}}",
                new JavaScriptConfig(true)));

        // pmprbuseddlavg_cnum2
        aggregatorList.add(new DoubleSumAggregatorFactory("pmprbuseddlavg_cnum2_s", "pmprbuseddlavg_cnum2"));
        postAggregatorList.add(new JavaScriptPostAggregator("pmprbuseddlavg_cnum2", Arrays.asList("pmprbuseddlavg_cnum2_s", "value"),
                "function(pmprbuseddlavg_cnum2_s, value) { if(value){ return pmprbuseddlavg_cnum2_s / value; } else {return 0;}}",
                new JavaScriptConfig(true)));
        
        // pmpdcpvoldldrb_thruput
        aggregatorList.add(new DoubleSumAggregatorFactory("pmpdcpvoldldrb_calc_s", "pmpdcpvoldldrb_calc"));
        aggregatorList.add(new DoubleSumAggregatorFactory("pmuethptimedl_s", "pmuethptimedl"));
        postAggregatorList.add(new JavaScriptPostAggregator("pmpdcpvoldldrb_thruput", Arrays.asList("pmpdcpvoldldrb_calc_s", "pmuethptimedl_s"),
                "function(pmpdcpvoldldrb_calc_s, pmuethptimedl_s) { if(pmuethptimedl_s){ return pmpdcpvoldldrb_calc_s / (pmuethptimedl_s / 1000); } else {return 0;}}",
                new JavaScriptConfig(true)));
        
        // pmpdcpvoldldrb_thruput_cnum1
        aggregatorList.add(new DoubleSumAggregatorFactory("pmpdcpvoldldrb_calc_cnum1_s", "pmpdcpvoldldrb_calc_cnum1"));
        aggregatorList.add(new DoubleSumAggregatorFactory("pmuethptimedl_cnum1_s", "pmuethptimedl_cnum1"));
        postAggregatorList.add(new JavaScriptPostAggregator("pmpdcpvoldldrb_thruput_cnum1", Arrays.asList("pmpdcpvoldldrb_calc_cnum1_s", "pmuethptimedl_cnum1_s"),
                "function(pmpdcpvoldldrb_calc_cnum1_s, pmuethptimedl_cnum1_s) { if(pmuethptimedl_cnum1_s){ return pmpdcpvoldldrb_calc_cnum1_s / (pmuethptimedl_cnum1_s / 1000); } else {return 0;}}",
                new JavaScriptConfig(true)));
        
        // pmpdcpvoldldrb_thruput_cnum2
        aggregatorList.add(new DoubleSumAggregatorFactory("pmpdcpvoldldrb_calc_cnum2_s", "pmpdcpvoldldrb_calc_cnum2"));
        aggregatorList.add(new DoubleSumAggregatorFactory("pmuethptimedl_cnum2_s", "pmuethptimedl_cnum2"));
        postAggregatorList.add(new JavaScriptPostAggregator("pmpdcpvoldldrb_thruput_cnum2", Arrays.asList("pmpdcpvoldldrb_calc_cnum2_s", "pmuethptimedl_cnum2_s"),
                "function(pmpdcpvoldldrb_calc_cnum2_s, pmuethptimedl_cnum2_s) { if(pmuethptimedl_cnum2_s){ return pmpdcpvoldldrb_calc_cnum2_s / (pmuethptimedl_cnum2_s / 1000); } else {return 0;}}",
                new JavaScriptConfig(true)));
        
        // pmpdcppkt_dl_err_rate
        aggregatorList.add(new DoubleSumAggregatorFactory("pmpdcppktdiscdlpelr_s", "pmpdcppktdiscdlpelr"));
        aggregatorList.add(new DoubleSumAggregatorFactory("pmpdcppktdiscdlpelruu_s", "pmpdcppktdiscdlpelruu"));
        aggregatorList.add(new DoubleSumAggregatorFactory("pmpdcppktdiscdlho_s", "pmpdcppktdiscdlho"));
        aggregatorList.add(new DoubleSumAggregatorFactory("pmschedactivitycelldl_s", "pmschedactivitycelldl"));
        aggregatorList.add(new DoubleSumAggregatorFactory("pmpdcppktreceiveddl_s", "pmpdcppktreceiveddl"));
        aggregatorList.add(new DoubleSumAggregatorFactory("pmpdcppktfwddl_s", "pmpdcppktfwddl"));
        postAggregatorList.add(new JavaScriptPostAggregator("pmpdcppkt_dl_err_rate", Arrays.asList("pmpdcppktdiscdlpelr_s", "pmpdcppktdiscdlpelruu_s", "pmpdcppktdiscdlho_s", "pmschedactivitycelldl_s", "pmpdcppktreceiveddl_s", "pmpdcppktfwddl_s"),
                "function(pmpdcppktdiscdlpelr_s, pmpdcppktdiscdlpelruu_s, pmpdcppktdiscdlho_s, pmschedactivitycelldl_s, pmpdcppktreceiveddl_s, pmpdcppktfwddl_s) " +
                        "{ if(pmpdcppktreceiveddl_s - pmpdcppktfwddl_s + (pmschedactivitycelldl_s * pmpdcppktreceiveddl_s / pmpdcppktreceiveddl_s)) " +
                        "{ return 100 * (pmpdcppktdiscdlpelr_s + pmpdcppktdiscdlpelruu_s + pmpdcppktdiscdlho_s + (pmschedactivitycelldl_s * pmpdcppktreceiveddl_s / pmpdcppktreceiveddl_s)) / (pmpdcppktreceiveddl_s - pmpdcppktfwddl_s + (pmschedactivitycelldl_s * pmpdcppktreceiveddl_s / pmpdcppktreceiveddl_s)); } else {return 0;}}",
                new JavaScriptConfig(true)));
        
        // pmpdcppkt_ul_err_rate
        aggregatorList.add(new DoubleSumAggregatorFactory("pmpdcppktlostul_s", "pmpdcppktlostul"));
        aggregatorList.add(new DoubleSumAggregatorFactory("pmpdcppktreceivedul_s", "pmpdcppktreceivedul"));
        postAggregatorList.add(new JavaScriptPostAggregator("pmpdcppkt_ul_err_rate", Arrays.asList("pmpdcppktlostul_s", "pmpdcppktreceivedul_s"),
                "function(pmpdcppktlostul_s, pmpdcppktreceivedul_s) { if(pmpdcppktlostul_s + pmpdcppktreceivedul_s){ return pmpdcppktlostul_s / (pmpdcppktlostul_s + pmpdcppktreceivedul_s) * 100; } else {return 0;}}",
                new JavaScriptConfig(true)));

        // pmsinrpucchdistr
        aggregatorList.add(new DoubleSumAggregatorFactory("pmsinrpucchdistr_sumby_s", "pmsinrpucchdistr_sumby"));
        aggregatorList.add(new DoubleSumAggregatorFactory("pmsinrpucchdistr_sum_s", "pmsinrpucchdistr_sum"));
        postAggregatorList.add(new JavaScriptPostAggregator("pmsinrpucchdistr", Arrays.asList("pmsinrpucchdistr_sumby_s", "pmsinrpucchdistr_sum_s"),
                "function(pmsinrpucchdistr_sumby_s, pmsinrpucchdistr_sum_s) { if(pmsinrpucchdistr_sum_s){ return pmsinrpucchdistr_sumby_s / pmsinrpucchdistr_sum_s; } else {return 0;}}",
                new JavaScriptConfig(true)));
        
        // pmsinrpuschdistr
        aggregatorList.add(new DoubleSumAggregatorFactory("pmsinrpuschdistr_sumby_s", "pmsinrpuschdistr_sumby"));
        aggregatorList.add(new DoubleSumAggregatorFactory("pmsinrpuschdistr_sum_s", "pmsinrpuschdistr_sum"));
        postAggregatorList.add(new JavaScriptPostAggregator("pmsinrpuschdistr", Arrays.asList("pmsinrpuschdistr_sumby_s", "pmsinrpuschdistr_sum_s"),
                "function(pmsinrpuschdistr_sumby_s, pmsinrpuschdistr_sum_s) { if(pmsinrpuschdistr_sum_s){ return pmsinrpuschdistr_sumby_s / pmsinrpuschdistr_sum_s; } else {return 0;}}",
                new JavaScriptConfig(true)));
        
        // pmradiorecinterferencepwr
        aggregatorList.add(new DoubleSumAggregatorFactory("pmradiorecinterferencepwr_sumby_s", "pmradiorecinterferencepwr_sumby"));
        aggregatorList.add(new DoubleSumAggregatorFactory("pmradiorecinterferencepwr_sum_s", "pmradiorecinterferencepwr_sum"));
        postAggregatorList.add(new JavaScriptPostAggregator("pmradiorecinterferencepwr", Arrays.asList("pmradiorecinterferencepwr_sumby_s", "pmradiorecinterferencepwr_sum_s"),
                "function(pmradiorecinterferencepwr_sumby_s, pmradiorecinterferencepwr_sum_s) { if(pmradiorecinterferencepwr_sum_s){ return pmradiorecinterferencepwr_sumby_s / pmradiorecinterferencepwr_sum_s; } else {return 0;}}",
                new JavaScriptConfig(true)));

        // pmprbutildlmax
        aggregatorList.add(new DoubleMaxAggregatorFactory("pmprbutildlmax", "pmprbutildlmax"));
        
        // pmradiorecinterferencepwrpucch
        aggregatorList.add(new DoubleSumAggregatorFactory("pmradiorecinterferencepwrpucch_sumby_s", "pmradiorecinterferencepwrpucch_sumby"));
        aggregatorList.add(new DoubleSumAggregatorFactory("pmradiorecinterferencepwrpucch_sum_s", "pmradiorecinterferencepwrpucch_sum"));
        postAggregatorList.add(new JavaScriptPostAggregator("pmradiorecinterferencepwrpucch", Arrays.asList("pmradiorecinterferencepwrpucch_sumby_s", "pmradiorecinterferencepwrpucch_sum_s"),
                "function(pmradiorecinterferencepwrpucch_sumby_s, pmradiorecinterferencepwrpucch_sum_s) { if(pmradiorecinterferencepwrpucch_sum_s){ return pmradiorecinterferencepwrpucch_sumby_s / pmradiorecinterferencepwrpucch_sum_s; } else {return 0;}}",
                new JavaScriptConfig(true)));
        
        // mimo_rate
        aggregatorList.add(new DoubleSumAggregatorFactory("pmradiotxrankdistr1_s", "pmradiotxrankdistr1"));
        aggregatorList.add(new DoubleSumAggregatorFactory("pmradiotxrankdistr2_s", "pmradiotxrankdistr2"));
        postAggregatorList.add(new JavaScriptPostAggregator("mimo_rate", Arrays.asList("pmradiotxrankdistr1_s", "pmradiotxrankdistr2_s"),
                "function(pmradiotxrankdistr1_s, pmradiotxrankdistr2_s) { if(pmradiotxrankdistr1_s + pmradiotxrankdistr2_s){ return pmradiotxrankdistr2_s / (pmradiotxrankdistr1_s + pmradiotxrankdistr2_s) * 100; } else {return 0;}}",
                new JavaScriptConfig(true)));

        // pmradiouerepcqidistr
        aggregatorList.add(new DoubleSumAggregatorFactory("pmradiouerepcqidistr_sumby_s", "pmradiouerepcqidistr_sumby"));
        aggregatorList.add(new DoubleSumAggregatorFactory("pmradiouerepcqidistr_sum_s", "pmradiouerepcqidistr_sum"));
        postAggregatorList.add(new JavaScriptPostAggregator("pmradiouerepcqidistr", Arrays.asList("pmradiouerepcqidistr_sumby_s", "pmradiouerepcqidistr_sum_s"),
                "function(pmradiouerepcqidistr_sumby_s, pmradiouerepcqidistr_sum_s) { if(pmradiouerepcqidistr_sum_s){ return pmradiouerepcqidistr_sumby_s / pmradiouerepcqidistr_sum_s; } else {return 0;}}",
                new JavaScriptConfig(true)));
        
        // pmradioueoutloopadjdistr
        aggregatorList.add(new DoubleSumAggregatorFactory("pmradioueoutloopadjdistr_sumby_s", "pmradioueoutloopadjdistr_sumby"));
        aggregatorList.add(new DoubleSumAggregatorFactory("pmradioueoutloopadjdistr_sum_s", "pmradioueoutloopadjdistr_sum"));
        postAggregatorList.add(new JavaScriptPostAggregator("pmradioueoutloopadjdistr", Arrays.asList("pmradioueoutloopadjdistr_sumby_s", "pmradioueoutloopadjdistr_sum_s"),
                "function(pmradioueoutloopadjdistr_sumby_s, pmradioueoutloopadjdistr_sum_s) { if(pmradioueoutloopadjdistr_sum_s){ return pmradioueoutloopadjdistr_sumby_s / pmradioueoutloopadjdistr_sum_s; } else {return 0;}}",
                new JavaScriptConfig(true)));

        // pmactiveuedlmax
        aggregatorList.add(new DoubleMaxAggregatorFactory("pmactiveuedlmax", "pmactiveuedlmax"));

        // pmactiveueulmax
        aggregatorList.add(new DoubleMaxAggregatorFactory("pmactiveueulmax", "pmactiveueulmax"));

        // pmrrcconnestabfaillic
        aggregatorList.add(new DoubleSumAggregatorFactory("pmrrcconnestabfaillic", "pmrrcconnestabfaillic"));

        // pmlicconnectedusersavg
        aggregatorList.add(new DoubleSumAggregatorFactory("pmlicconnecteduserslevsum_s", "pmlicconnecteduserslevsum"));
        aggregatorList.add(new DoubleSumAggregatorFactory("pmlicconnecteduserslevsamp_s", "pmlicconnecteduserslevsamp"));
        postAggregatorList.add(new JavaScriptPostAggregator("pmlicconnectedusersavg", Arrays.asList("pmlicconnecteduserslevsum_s", "pmlicconnecteduserslevsamp_s"),
                "function(pmlicconnecteduserslevsum_s, pmlicconnecteduserslevsamp_s) { if(pmlicconnecteduserslevsamp_s){ return pmlicconnecteduserslevsum_s / pmlicconnecteduserslevsamp_s; } else {return 0;}}",
                new JavaScriptConfig(true)));

        // pmlicconnectedusersmax
        aggregatorList.add(new DoubleMaxAggregatorFactory("pmlicconnectedusersmax", "pmlicconnectedusersmax"));

        // pmlicconnectedusersmax_rate
        aggregatorList.add(new DoubleMaxAggregatorFactory("pmlicconnectedusersmax_rate", "pmlicconnectedusersmax_rate"));

        // pmlicconnecteduserslicense
        aggregatorList.add(new DoubleSumAggregatorFactory("pmlicconnecteduserslicense", "pmlicconnecteduserslicense"));

        ret.put("aggregator", aggregatorList);
        ret.put("postAggregator", postAggregatorList);

        return ret;
    }
}
