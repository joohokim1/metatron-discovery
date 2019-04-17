package app.metatron.discovery.domain.ipm.service.icpm;

import app.metatron.discovery.domain.ipm.common.util.excel.ExcelGenHelper;
import app.metatron.discovery.domain.ipm.domain.common.ComDto;
import app.metatron.discovery.domain.ipm.domain.common.UserBmrkDto;
import app.metatron.discovery.domain.ipm.domain.icpm.IcpmFltrBasEntity;
import app.metatron.discovery.domain.ipm.repository.icpm.IcpmFltrBasRepository;
import app.metatron.discovery.domain.ipm.service.common.DruidService;
import app.metatron.discovery.domain.ipm.service.common.IpmCommonService;
import io.druid.data.input.MapBasedRow;
import io.druid.data.input.Row;
import io.druid.java.util.common.granularity.Granularities;
import io.druid.js.JavaScriptConfig;
import io.druid.query.Druids;
import io.druid.query.Result;
import io.druid.query.aggregation.CountAggregatorFactory;
import io.druid.query.dimension.DefaultDimensionSpec;
import io.druid.query.dimension.DimensionSpec;
import io.druid.query.dimension.ExtractionDimensionSpec;
import io.druid.query.extraction.JavaScriptExtractionFn;
import io.druid.query.filter.*;
import io.druid.query.groupby.GroupByQuery;
import io.druid.query.select.EventHolder;
import io.druid.query.select.PagingSpec;
import io.druid.query.select.SelectResultValue;
import io.druid.query.timeboundary.TimeBoundaryResultValue;
import io.druid.query.topn.DimensionAndMetricValueExtractor;
import io.druid.query.topn.NumericTopNMetricSpec;
import io.druid.query.topn.TopNQueryBuilder;
import io.druid.query.topn.TopNResultValue;
import io.druid.segment.column.ValueType;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
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
public class IcpmDruidService extends DruidService {

    /**
     * druid pool
     */
    @Value("${polaris.ipm.druid-icpm-pool}")
    private Integer pool;
    protected Integer setPool() {
        return pool;
    }

    /**
     * init : druid clientList 생성
     */
    @PostConstruct
    private void init() {
        initClient();
    }

    /**
     * icpm data source
     */
    @Value("${polaris.icpm.ds-icpm}")
    private String dsIcpm;

    /**
     * region dimension
     */
    @Value("${polaris.icpm.ds-icpm-dim-region}")
    private String dsIcpmDimRegion;

    /**
     * 시도 dimension
     */
    @Value("${polaris.icpm.ds-icpm-dim-sido}")
    private String[] dsIcpmDimSido;

    /**
     * 시군구 dimension
     */
    @Value("${polaris.icpm.ds-icpm-dim-sgg}")
    private String[] dsIcpmDimSgg;

    /**
     * 시군구 filter
     */
    @Value("${polaris.icpm.ds-icpm-fltr-sgg}")
    private String dsIcpmFltrSgg;

    /**
     * 읍면동 dimension
     */
    @Value("${polaris.icpm.ds-icpm-dim-dong}")
    private String[] dsIcpmDimDong;

    /**
     * 읍면동 filter
     */
    @Value("${polaris.icpm.ds-icpm-fltr-dong}")
    private String[] dsIcpmFltrDong;

    /**
     * age dimension
     */
    @Value("${polaris.icpm.ds-icpm-dim-age}")
    private String dsIcpmDimAge;

    /**
     * age 최소값
     */
    @Value("${polaris.icpm.ds-icpm-dim-age-min}")
    private int dsIcpmDimAgeMin;

    /**
     * age 최대값
     */
    @Value("${polaris.icpm.ds-icpm-dim-age-max}")
    private int dsIcpmDimAgeMax;

    /**
     * age step
     */
    @Value("${polaris.icpm.ds-icpm-dim-age-step}")
    private int dsIcpmDimAgeStep;

    /**
     * age 최소 미만값 표현여부
     */
    @Value("${polaris.icpm.ds-icpm-dim-age-minwith}")
    private boolean dsIcpmDimAgeMinwith;

    /**
     * age 최대 초과값 표현여부
     */
    @Value("${polaris.icpm.ds-icpm-dim-age-maxwith}")
    private boolean dsIcpmDimAgeMaxwith;

    /**
     * vendor dimension
     */
    @Value("${polaris.icpm.ds-icpm-dim-vendor}")
    private String dsIcpmDimVendor;

    /**
     * vendor threshold
     */
    @Value("${polaris.icpm.ds-icpm-thr-vendor}")
    private int dsIcpmThrVendor;

    /**
     * model dimension
     */
    @Value("${polaris.icpm.ds-icpm-dim-model}")
    private String dsIcpmDimModel;

    /**
     * model threshold
     */
    @Value("${polaris.icpm.ds-icpm-thr-model}")
    private int dsIcpmThrModel;

    /**
     * team dimension
     */
    @Value("${polaris.icpm.ds-icpm-dim-team}")
    private String dsIcpmDimTeam;

    /**
     * traffic dimension
     */
    @Value("${polaris.icpm.ds-icpm-dim-traffic}")
    private String dsIcpmDimTraffic;

    /**
     * traffic 최소값
     */
    @Value("${polaris.icpm.ds-icpm-dim-traffic-min}")
    private int dsIcpmDimTrafficMin;

    /**
     * traffic 최대값
     */
    @Value("${polaris.icpm.ds-icpm-dim-traffic-max}")
    private int dsIcpmDimTrafficMax;

    /**
     * cei dimension
     */
    @Value("${polaris.icpm.ds-icpm-dim-cei}")
    private String dsIcpmDimCei;

    /**
     * cei 최소값
     */
    @Value("${polaris.icpm.ds-icpm-dim-cei-min}")
    private int dsIcpmDimCeiMin;

    /**
     * cei 최대값
     */
    @Value("${polaris.icpm.ds-icpm-dim-cei-max}")
    private int dsIcpmDimCeiMax;

    /**
     * cei 범위
     */
    @Value("${polaris.icpm.ds-icpm-dim-cei-label}")
    private int[] dsIcpmDimCeiLabel;

    /**
     * icpm excel limit
     */
    @Value("${polaris.icpm.ds-icpm-excel-limit}")
    private int dsIcpmExcelLimit;

    /**
     * icpm excel sheet name
     */
    @Value("${polaris.icpm.ds-icpm-excel-sheet-nm}")
    private String dsIcpmExcelSheetNm;

    /**
     * icpm excel grid wo list
     */
    @Value("${polaris.icpm.ds-icpm-excel-grid-wo-list}")
    private String[] dsIcpmExcelGridWoList;

    /**
     * scrnClNm : address
     */
    private final String ADDRESS = "address";

    /**
     * scrnClNm : range
     */
    private final String RANGE = "range";

    /**
     * scrnClNm : calendar
     */
    private final String CALENDAR = "calendar";

    /**
     * scrnClNm : checkrange
     */
    private final String CHECKRANGE = "checkrange";

    /**
     * type : not
     */
    private final String NOT = "not";

    /**
     * druid data null : 정보없음
     */
    private final String UNCLASSIFED = "정보없음";

    /**
     * icpm 조회 기간 dimension
     */
    private final String DT = "dt";

    @Autowired
    private IcpmFltrBasRepository icpmFltrBasRepository;

    @Autowired
    private IpmCommonService ipmCommonService;

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
                    String scrnClNm = data.getScrnClNm();
                    switch (scrnClNm) {
                        case CHECKRANGE:
                        case RANGE:
                        case CALENDAR:
                            dimFilter = orBoundDimFilter(druidNm, fltrVal);
                            break;
                        case ADDRESS:
                            dimFilter = orRegexDimFilter(druidNm, fltrVal);
                            break;
                        default:
                            dimFilter = inDimFilter(druidNm, fltrVal);
                            break;
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
     * interval 조회
     * @return
     */
    private String getInterval(String startDt) {

        String postfix = "T00:00:00.000Z";
        String endDt = "";

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

        try {
            Date date = new SimpleDateFormat("yyyyMMdd").parse(startDt);
            calendar.setTime(date);

            // 시작일
            startDt = simpleDateFormat.format(calendar.getTime()) + postfix;

            // 종료일
            calendar.add(calendar.DATE, 2);
            endDt = simpleDateFormat.format(calendar.getTime()) + postfix;

        } catch (Exception e) {
            log.error(e.getMessage());
        }

        return startDt + "/" + endDt;
    }

    /**
     * druid 조회가능 Min/Max 날짜 조회
     * @return
     */
    private List<String> getMinMaxDt() {
        // Query문
        Druids.TimeBoundaryQueryBuilder builder = new Druids.TimeBoundaryQueryBuilder().dataSource(dsIcpm);
        List<Result<TimeBoundaryResultValue>> Results = druidRun(builder.build());

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

        List<String> dtList = new ArrayList<>();
        if (Results != null && !Results.isEmpty()) {
            Date minTime = Results.get(0).getValue().getMinTime().toDate();
            Date maxTime = Results.get(0).getValue().getMaxTime().toDate();

            String min = sdf.format(minTime);
            String max = sdf.format(maxTime);

            dtList.add(min);
            dtList.add(max);
        } else {

            Calendar cal = Calendar.getInstance();
            String today = sdf.format(cal.getTime());
            dtList.add(today);
            dtList.add(today);
        }

        return dtList;
    }

    /**
     * 차트 카운트 조회
     * @param fltrDatVal
     * @return
     */
    public ComDto.Chart getChartCount(List<UserBmrkDto.FltrDatVal> fltrDatVal, @Nullable String occrDt) {
        try {
            // occrDt가 없는 경우
            List<String> dtList = null;
            if (StringUtils.isBlank(occrDt)) {
                dtList = getMinMaxDt();
                if (dtList != null && !dtList.isEmpty()) {
                    occrDt = dtList.get(1);
                }
            }

            // 필수 날짜 Dimension Filter
            DimFilter occrDtDimFltr = new SelectorDimFilter(DT, occrDt, null);

            GroupByQuery.Builder builder = new GroupByQuery.Builder()
                    .setGranularity(Granularities.ALL)
                    .setDataSource(dsIcpm)
                    .setInterval(getInterval(occrDt))
                    .addAggregator(new CountAggregatorFactory("value"))
                    .setDimFilter(occrDtDimFltr);

            List<Row> allResults = druidRun(builder.build());
            List<ComDto.Code> allRet = new ArrayList<>();
            List<ComDto.Code> subRet = new ArrayList<>();

            if (allResults != null && !allResults.isEmpty()) {
                MapBasedRow aMapBasedRow = (MapBasedRow) allResults.get(0);
                Map aEvent = aMapBasedRow.getEvent();
                allRet.add(new ComDto.Code(null, null, aEvent.get("value")));

                // 필터 List 생성
                List<DimFilter> fltrList = getFltrList(fltrDatVal);

                if (fltrList != null && !fltrList.isEmpty()) {
                    // occr_dt 추가
                    fltrList.add(occrDtDimFltr);
                    builder.setDimFilter(DimFilters.and(fltrList));

                    List<Row> targetResults = druidRun(builder.build());
                    List<ComDto.Code> targetRet = new ArrayList<>();
                    if (targetResults != null && !targetResults.isEmpty()) {
                        MapBasedRow tMapBasedRow = (MapBasedRow) targetResults.get(0);
                        Map tEvent = tMapBasedRow.getEvent();
                        int evtVal = (Integer) tEvent.get("value");

                        targetRet.add(new ComDto.Code(null, null, evtVal));
                        subRet.add(new ComDto.Code(null, null, (Integer) allRet.get(0).getValue() - evtVal));

                    } else {
                        targetRet.add(new ComDto.Code(null, null, 0));
                        subRet = allRet;
                    }

                    return new ComDto.Chart(targetRet, allRet, subRet, dtList);
                }

            } else {
                allRet.add(new ComDto.Code(null, null, 0));
            }

            // allRet에 맞춰서 subRet 생성
            for (ComDto.Code code : allRet) {
                subRet.add(new ComDto.Code(null, null, 0));
            }

            return new ComDto.Chart(allRet, allRet, subRet, dtList);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return null;
    }

    /**
     * 지역별 차트 조회
     * @param fltrDatVal
     * @return
     */
    public ComDto.Chart getRegionChart(List<UserBmrkDto.FltrDatVal> fltrDatVal, String occrDt, List<String> addrCd) {
        try {
            // addrCd를 통해 dimension, filter 설정
            int addrCdSize = addrCd.size();
            List<DimensionSpec> dimensionList = new ArrayList<>();
            List<DimFilter> dimFltrList = new ArrayList();
            switch (addrCdSize) {
                case 0:
                    // 시도
                    dimensionList.add(new DefaultDimensionSpec(dsIcpmDimSido[0],"code"));
                    dimensionList.add(new DefaultDimensionSpec(dsIcpmDimSido[1], "name"));

                    break;
                case 1:
                    // 시군구
                    dimensionList.add(new DefaultDimensionSpec(dsIcpmDimSgg[0], "code"));
                    dimensionList.add(new DefaultDimensionSpec(dsIcpmDimSgg[1], "name"));

                    dimFltrList.add(new SelectorDimFilter(dsIcpmFltrSgg, addrCd.get(0), null));

                    break;
                case 2:
                    // 읍면동
                    dimensionList.add(new DefaultDimensionSpec(dsIcpmDimDong[0], "code"));
                    dimensionList.add(new DefaultDimensionSpec(dsIcpmDimDong[1], "name"));

                    for (int i = 0; i < addrCdSize; i++) {
                        dimFltrList.add(new SelectorDimFilter(dsIcpmFltrDong[i], addrCd.get(i), null));
                    }

                    break;
            }

            // 필수 날짜 Dimension Filter
            DimFilter occrDtDimFltr = new SelectorDimFilter(DT, occrDt, null);

            // all Query 생성 (지역 목록을 filter로)
            GroupByQuery.Builder builder = new GroupByQuery.Builder()
                    .setGranularity(Granularities.ALL)
                    .setDataSource(dsIcpm)
                    .setInterval(getInterval(occrDt))
                    .setDimensions(dimensionList)
                    .addAggregator(new CountAggregatorFactory("value"))
                    ;

            // occr_dt 추가
            dimFltrList.add(occrDtDimFltr);
            builder.setDimFilter(DimFilters.and(dimFltrList));

            List<Row> aDruidResult = druidRun(builder.build());
            // 정렬된 주소 리스트
            List<ComDto.Code> allSortedRet = new ArrayList<>();
            // (all - target) 결과 리스트
            List<ComDto.Code> subRet = new ArrayList<>();
            if (aDruidResult != null && !aDruidResult.isEmpty()) {
                allSortedRet = ipmCommonService.getAddress(addrCd);
                ComDto.Code unclassified = null;
                for (ComDto.Code code : allSortedRet) {
                    String allNm = (String)code.getName();
                    code.setValue(0);

                    for (Row row : aDruidResult) {
                        MapBasedRow mapBasedRow = (MapBasedRow)row;
                        Map event = mapBasedRow.getEvent();
                        String evtCd = (String)event.get("code");
                        String evtNm = (String)event.get("name");
                        int evtVal = (Integer)event.get("value");

                        if (allNm.equals(evtNm)) {
                            code.setValue(evtVal);
                            break;
                        }

                        if (StringUtils.isBlank(evtNm)) {
                            unclassified = new ComDto.Code(evtCd, UNCLASSIFED, evtVal);
                        }
                    }
                }
                // 정보없음이 있을 경우 리스트 마지막에 추가해준다.
                if (unclassified != null) {
                    allSortedRet.add(unclassified);
                }

                // target Query 생성 ( 지역 목록, filterDatVal 내 filter로)
                List<DimFilter> fltrList = getFltrList(fltrDatVal);
                if (fltrList != null && !fltrList.isEmpty()) {

                    // addrCd에 따른 filter 존재할 경우 추가해준다
                    if (!dimFltrList.isEmpty()) {
                        for (DimFilter filter : dimFltrList) {
                            fltrList.add(filter);
                        }
                    }

                    builder.setDimFilter(DimFilters.and(fltrList));

                    List<Row> tDruidResult = druidRun(builder.build());
                    List<ComDto.Code> targetRet = new ArrayList<>();
                    if (tDruidResult != null) {
                        Map<String, ComDto.Code> targetMap = new HashMap<>();
                        for (Row row : tDruidResult) {
                            MapBasedRow mapBasedRow = (MapBasedRow)row;
                            Map event = mapBasedRow.getEvent();
                            String evtCd = (String)event.get("code");
                            String evtNm = (String)event.get("name");
                            int evtVal = (Integer)event.get("value");

                            if (StringUtils.isNotBlank(evtNm)) {
                                targetMap.put(evtNm, new ComDto.Code(evtCd, evtNm, evtVal));
                            } else {
                                targetMap.put(UNCLASSIFED, new ComDto.Code(evtCd, UNCLASSIFED, evtVal));
                            }
                        }

                        // sub(all - target) 값
                        int subVal = 0;
                        // all군과 비교하여 없는값은 0으로 채운다.
                        for (int i = 0, allSize = allSortedRet.size(); i < allSize; i++) {
                            String allName = (String)allSortedRet.get(i).getName();
                            String allCode = (String)allSortedRet.get(i).getCode();
                            int allValue = (Integer)allSortedRet.get(i).getValue();

                            if (targetMap.containsKey(allName)) {
                                targetRet.add(targetMap.get(allName));
                                subVal = allValue - (Integer)targetMap.get(allName).getValue();
                                subRet.add(new ComDto.Code(allCode, allName, subVal));
                            } else {
                                targetRet.add(new ComDto.Code(allCode, allName, 0));
                                subRet.add(new ComDto.Code(allCode, allName, allValue));
                            }
                        }
                    }

                    return new ComDto.Chart(targetRet, allSortedRet, subRet);
                }
            }

            // allRet에 맞춰서 subRet 생성
            for (ComDto.Code code : allSortedRet) {
                subRet.add(new ComDto.Code(code.getCode(), code.getName(), 0));
            }

            return new ComDto.Chart(allSortedRet, allSortedRet, subRet);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return null;
    }

    /**
     * 연령별 차트 조회
     * @param fltrDatVal
     * @return
     */
    public ComDto.Chart getAgeChart(List<UserBmrkDto.FltrDatVal> fltrDatVal, String occrDt) {
        try {
            // 필수 날짜 Dimension Filter
            DimFilter occrDtDimFltr = new SelectorDimFilter(DT, occrDt, null);

            GroupByQuery.Builder builder = new GroupByQuery.Builder()
                    .setGranularity(Granularities.ALL)
                    .setDataSource(dsIcpm)
                    .setInterval(getInterval(occrDt))
                    .addDimension(dsIcpmDimAge)
                    .addAggregator(new CountAggregatorFactory("value"))
                    .setDimFilter(occrDtDimFltr)
                    ;

            List<Row> allResult = druidRun(builder.build());
            List<ComDto.Code> allRet = new ArrayList<>();
            // (all-target) 결과 리스트
            List<ComDto.Code> subRet = new ArrayList<>();
            if (allResult != null && !allResult.isEmpty()) {
                int allAgeMinWithSum = 0; // all군 최소값 미만 데이터 SUM
                int allAgeMaxWithSum = 0; // all군 최대값 초과 데이터 SUM

                // minAge부터 maxAge까지 ageStep 단위로 리스트 생성.
                for (int i = dsIcpmDimAgeMin; i <= dsIcpmDimAgeMax; i += dsIcpmDimAgeStep) {
                    allRet.add(new ComDto.Code(null, String.valueOf(i), 0));
                }

                int allSize = allRet.size();

                for(Row row : allResult) {
                    MapBasedRow mapBasedRow = (MapBasedRow)row;
                    Map event = mapBasedRow.getEvent();
                    Object evtNm = event.get(dsIcpmDimAge);
                    if (evtNm != null) {
                        double nm = (Double)evtNm;
                        int age = (int)nm;
                        int value = (Integer)event.get("value");
                        if (age >= dsIcpmDimAgeMin && age <= dsIcpmDimAgeMax) {
                            int index = age / dsIcpmDimAgeStep;
                            ComDto.Code cdDto = allRet.get(index);
                            cdDto.setValue((int)cdDto.getValue() + value);

                            // 최소값 미만 데이터들 SUM
                        } else if (age < dsIcpmDimAgeMin) {
                            allAgeMinWithSum += value;

                            // 최대값 초과 데이터들 SUM
                        } else if (age > dsIcpmDimAgeMax) {
                            allAgeMaxWithSum += value;
                        }
                    }
                }

                // 최소값 미만 포함인 경우
                if (dsIcpmDimAgeMinwith) {
                    ComDto.Code cdDto = allRet.get(0);
                    cdDto.setValue((int)cdDto.getValue() + allAgeMinWithSum);
                    cdDto.setName(cdDto.getName() + "(-)");
                }

                // 최대값 초과 포함인 경우
                if (dsIcpmDimAgeMaxwith) {
                    ComDto.Code cdDto = allRet.get(allSize - 1);
                    cdDto.setValue((int)cdDto.getValue() + allAgeMaxWithSum);
                    cdDto.setName(cdDto.getName() + "(+)");
                }

                // 필터 List 생성
                List<DimFilter> fltrList = getFltrList(fltrDatVal);
                if (fltrList != null && !fltrList.isEmpty()) {
                    fltrList.add(occrDtDimFltr);
                    builder.setDimFilter(DimFilters.and(fltrList));

                    List<Row> targetResult = druidRun(builder.build());
                    List<ComDto.Code> targetRet = new ArrayList<>();
                    if (targetResult!= null) {
                        int targetAgeMinWithSum = 0; // target군 최소값 미만 데이터 SUM
                        int targetAgeMaxWithSum = 0; // target군 최대값 초과 데이터 SUM

                        // minAge부터 maxAge까지 ageStep 단위로 리스트 생성.
                        for (int i = dsIcpmDimAgeMin; i <= dsIcpmDimAgeMax; i += dsIcpmDimAgeStep) {
                            targetRet.add(new ComDto.Code(null, String.valueOf(i), 0));
                            subRet.add(new ComDto.Code(null, String.valueOf(i), allRet.get(i / dsIcpmDimAgeStep).getValue()));
                        }

                        int targetSize = targetRet.size();

                        for(Row row : targetResult) {
                            MapBasedRow mapBasedRow = (MapBasedRow)row;
                            Map event = mapBasedRow.getEvent();
                            Object evtNm = event.get(dsIcpmDimAge);

                            if (evtNm != null) {
                                double nm = (Double)evtNm;
                                int age = (int)nm;
                                int value = (Integer)event.get("value");
                                if (age >= dsIcpmDimAgeMin && age <= dsIcpmDimAgeMax) {
                                    int index = age / dsIcpmDimAgeStep;
                                    ComDto.Code cdDto = targetRet.get(index);
                                    cdDto.setValue((int)cdDto.getValue() + value);

                                    ComDto.Code subCdDto = subRet.get(index);
                                    subCdDto.setValue((int)subCdDto.getValue() - value);

                                    // 최소값 미만 데이터들 SUM
                                } else if (age < dsIcpmDimAgeMin) {
                                    targetAgeMinWithSum += value;

                                    // 최대값 초과 데이터들 SUM
                                } else if (age > dsIcpmDimAgeMax) {
                                    targetAgeMaxWithSum += value;
                                }
                            }
                        }

                        // 최소값 미만 포함인 경우
                        if (dsIcpmDimAgeMinwith) {
                            ComDto.Code targetCdDto = targetRet.get(0);
                            targetCdDto.setValue((int)targetCdDto.getValue() + targetAgeMinWithSum);
                            targetCdDto.setName(targetCdDto.getName() + "(-)");

                            ComDto.Code subCdDto = subRet.get(0);
                            subCdDto.setValue((int)subCdDto.getValue() - targetAgeMinWithSum);
                            subCdDto.setName(subCdDto.getName() + "(-)");
                        }

                        // 최대값 초과 포함인 경우
                        if (dsIcpmDimAgeMaxwith) {
                            ComDto.Code targetCdDto = targetRet.get(targetSize - 1);
                            targetCdDto.setValue((int)targetCdDto.getValue() + targetAgeMaxWithSum);
                            targetCdDto.setName(targetCdDto.getName() + "(+)");

                            ComDto.Code subCdDto = subRet.get(targetSize - 1);
                            subCdDto.setValue((int)subCdDto.getValue() - targetAgeMaxWithSum);
                            subCdDto.setName(subCdDto.getName() + "(+)");
                        }
                    }

                    return new ComDto.Chart(targetRet, allRet, subRet);
                }
            }

            // subRet
            for (ComDto.Code code : allRet) {
                subRet.add(new ComDto.Code(null, code.getName(), 0));
            }

            return new ComDto.Chart(allRet, allRet, subRet);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return null;
    }

    /**
     * 제조사별 차트 조회
     * @param fltrDatVal
     * @return
     */
    public ComDto.Chart getVendorChart(List<UserBmrkDto.FltrDatVal> fltrDatVal, String occrDt) {
        try {
            // 필수 날짜 Dimension Filter
            DimFilter occrDtDimFltr = new SelectorDimFilter(DT, occrDt, null);

            // filter Query 생성 (fltrDatVal 내 filter 적용)
            TopNQueryBuilder tBuilder = new TopNQueryBuilder()
                    .granularity(Granularities.ALL)
                    .dataSource(dsIcpm)
                    .intervals(getInterval(occrDt))
                    .dimension(dsIcpmDimVendor)
                    .aggregators(Arrays.asList(new CountAggregatorFactory("value")))
                    .metric(new NumericTopNMetricSpec("value"))
                    .threshold(dsIcpmThrVendor + 1)
                    ;

            // 필터 List 생성
            List<DimFilter> fltrList = getFltrList(fltrDatVal);

            if(fltrList != null && !fltrList.isEmpty()){
                // occr_dt 추가
                fltrList.add(occrDtDimFltr);
                tBuilder.filters(DimFilters.and(fltrList));

            } else {
                tBuilder.filters(occrDtDimFltr);
            }

            // target군(top N+1) 생성 : 정보없음이 포함될 수 있으므로 1개 여유
            List<ComDto.Code> targetRet = new ArrayList<>();
            // (all-target) 결과 리스트
            List<ComDto.Code> subRet = new ArrayList<>();
            List<Result<TopNResultValue>> tDruidList = druidRun(tBuilder.build());
            if (tDruidList != null && !tDruidList.isEmpty()) {

                Result tDruidResult = tDruidList.get(0);
                List<DimensionAndMetricValueExtractor> targetResultVal = ((TopNResultValue)(tDruidResult.getValue())).getValue();
                // dimension value
                String dimVal;

                // all inFilter에 들어갈 list
                List<String> inFltrList = new ArrayList<>();
                for (DimensionAndMetricValueExtractor val : targetResultVal) {
                    dimVal = (String)val.getDimensionValue(dsIcpmDimVendor);
                    if (StringUtils.isNotBlank(dimVal)) {
                        targetRet.add(new ComDto.Code(null, dimVal, val.getMetric("value")));
                        inFltrList.add(dimVal);

                        // 정보없음이 포함되지않은 경우 N개에서 break
                        if (targetRet.size() >= dsIcpmThrVendor) {
                            break;
                        }
                    }
                }

                // target이 N개가 아닌 경우 : all Top에서 부족한 만큼 채운다.
                if (targetRet.size() < dsIcpmThrVendor) {
                    // all Top(N+1) Query 생성 : 정보없음이 포함될 수 있으므로 1개 여유
                    TopNQueryBuilder allTopBuilder = new TopNQueryBuilder()
                            .granularity(Granularities.ALL)
                            .dataSource(dsIcpm)
                            .intervals(getInterval(occrDt))
                            .dimension(dsIcpmDimVendor)
                            .aggregators(Arrays.asList(new CountAggregatorFactory("value")))
                            .metric(new NumericTopNMetricSpec("value"))
                            .threshold(dsIcpmThrVendor + 1)
                            .filters(occrDtDimFltr);

                    // all top(N+1) 생성
                    List<Result<TopNResultValue>> allTopDruidList = druidRun(allTopBuilder.build());

                    if (allTopDruidList != null && !allTopDruidList.isEmpty()) {
                        Result allTopDruidResult = allTopDruidList.get(0);
                        List<DimensionAndMetricValueExtractor> allTopResultVal = ((TopNResultValue) (allTopDruidResult.getValue())).getValue();

                        // target이 0개인 경우 : allTop Query로 결과를 바로 반환.
                        if (targetRet.isEmpty()) {
                            List<ComDto.Code> allTopRet = new ArrayList<>();
                            for (DimensionAndMetricValueExtractor val : allTopResultVal) {
                                dimVal = (String)val.getDimensionValue(dsIcpmDimVendor);

                                if (StringUtils.isNotBlank(dimVal)) {
                                    targetRet.add(new ComDto.Code(null, dimVal, 0));
                                    allTopRet.add(new ComDto.Code(null, dimVal, val.getMetric("value")));

                                    // N개 정해지면 break
                                    if (targetRet.size() >= dsIcpmThrVendor) {
                                        return new ComDto.Chart(targetRet, allTopRet, allTopRet);
                                    }
                                }
                            }

                        // target이 0개가 아닌 경우
                        } else {
                            for (DimensionAndMetricValueExtractor val : allTopResultVal) {
                                dimVal = (String)val.getDimensionValue(dsIcpmDimVendor);
                                if (StringUtils.isNotBlank(dimVal) && !inFltrList.contains(dimVal)) {
                                    targetRet.add(new ComDto.Code(null, dimVal, 0));
                                    inFltrList.add(dimVal);

                                    // N개 정해지면 break
                                    if (targetRet.size() >= dsIcpmThrVendor) {
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }

                if (targetRet != null && !targetRet.isEmpty()) {

                    // target이 null이 아니고, fltrList가 존재 할때 All 쿼리 실행
                    if (fltrList != null && !fltrList.isEmpty()) {
                        // all dimension fltr list
                        List<DimFilter> allFltrList = new ArrayList<>();
                        allFltrList.add(occrDtDimFltr);
                        allFltrList.add(new InDimFilter(dsIcpmDimVendor, inFltrList, null));

                        // all군(topN) 생성
                        GroupByQuery.Builder gBuilder = new GroupByQuery.Builder()
                                .setGranularity(Granularities.ALL)
                                .setDataSource(dsIcpm)
                                .setInterval(getInterval(occrDt))
                                .addDimension(dsIcpmDimVendor)
                                .addAggregator(new CountAggregatorFactory("value"))
                                .setDimFilter(DimFilters.and(allFltrList))
                                ;

                        List<Row> aDruidResult = druidRun(gBuilder.build());
                        List<ComDto.Code> allRet = new ArrayList<>();
                        for (Row row : aDruidResult) {
                            MapBasedRow mapBasedRow = (MapBasedRow)row;
                            Map event = mapBasedRow.getEvent();
                            String evtNm = (String)event.get(dsIcpmDimVendor);

                            if (StringUtils.isNotBlank(evtNm)) {
                                allRet.add(new ComDto.Code(null, evtNm, event.get("value")));
                            }
                        }

                        // target과 순서 일치를 위해 sort
                        List<ComDto.Code> allSortedRet = new ArrayList<>();
                        int subVal = 0;
                        for (ComDto.Code target : targetRet) {
                            allSortedRet.add(allRet.stream().filter(all -> target.getName().equals(all.getName())).findAny().orElse(null));
                            subVal = (Integer)allSortedRet.get(allSortedRet.size() - 1).getValue() - (Integer)target.getValue();
                            subRet.add(new ComDto.Code(null, target.getName(), subVal));
                        }

                        return new ComDto.Chart(targetRet, allSortedRet, subRet);
                    }
                }
            }

            // targetRet에 맞춰서 subRet 생성
            for (ComDto.Code code : targetRet) {
                subRet.add(new ComDto.Code(null, code.getName(), 0));
            }

            return new ComDto.Chart(targetRet, targetRet, subRet);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return null;
    }

    /**
     * 단말/애칭별 차트 조회
     * @param fltrDatVal
     * @return
     */
    public ComDto.Chart getModelChart(List<UserBmrkDto.FltrDatVal> fltrDatVal, String occrDt) {
        try {
            // 필수 날짜 Dimension Filter
            DimFilter occrDtDimFltr = new SelectorDimFilter(DT, occrDt, null);

            // filter Query 생성 (fltrDatVal 내 filter 적용)
            TopNQueryBuilder tBuilder = new TopNQueryBuilder()
                    .granularity(Granularities.ALL)
                    .dataSource(dsIcpm)
                    .intervals(getInterval(occrDt))
                    .dimension(dsIcpmDimModel)
                    .aggregators(Arrays.asList(new CountAggregatorFactory("value")))
                    .metric(new NumericTopNMetricSpec("value"))
                    .threshold(dsIcpmThrModel + 1)
                    ;

            // 필터 List 생성
            List<DimFilter> fltrList = getFltrList(fltrDatVal);

            if(fltrList != null && !fltrList.isEmpty()){
                // occr_dt 추가
                fltrList.add(occrDtDimFltr);
                tBuilder.filters(DimFilters.and(fltrList));

            } else {
                tBuilder.filters(occrDtDimFltr);
            }

            // target군(top N+1) 생성 : 정보없음이 포함될 수 있으므로 1개 여유
            List<ComDto.Code> targetRet = new ArrayList<>();
            // (all-target) 결과 리스트
            List<ComDto.Code> subRet = new ArrayList<>();
            List<Result<TopNResultValue>> tDruidList = druidRun(tBuilder.build());
            if (tDruidList != null && !tDruidList.isEmpty()) {

                Result tDruidResult = tDruidList.get(0);
                List<DimensionAndMetricValueExtractor> targetResultVal = ((TopNResultValue)(tDruidResult.getValue())).getValue();
                // dimension value
                String dimVal;
                // all inFilter에 들어갈 list
                List<String> inFltrList = new ArrayList<>();
                for (DimensionAndMetricValueExtractor val : targetResultVal) {
                    dimVal = (String)val.getDimensionValue(dsIcpmDimModel);

                    if (StringUtils.isNotBlank(dimVal)) {
                        targetRet.add(new ComDto.Code(null, dimVal, val.getMetric("value")));
                        inFltrList.add(dimVal);

                        // 정보없음이 포함되지않은 경우 N개에서 break
                        if (targetRet.size() >= dsIcpmThrModel) {
                            break;
                        }
                    }
                }

                // target이 N개가 아닌 경우 : all Top에서 부족한 만큼 채운다.
                if (targetRet.size() < dsIcpmThrModel) {
                    // all Top(N+1) Query 생성 : 정보없음이 포함될 수 있으므로 1개 여유
                    TopNQueryBuilder allTopBuilder = new TopNQueryBuilder()
                            .granularity(Granularities.ALL)
                            .dataSource(dsIcpm)
                            .intervals(getInterval(occrDt))
                            .dimension(dsIcpmDimModel)
                            .aggregators(Arrays.asList(new CountAggregatorFactory("value")))
                            .metric(new NumericTopNMetricSpec("value"))
                            .threshold(dsIcpmThrModel + 1)
                            .filters(occrDtDimFltr);

                    // all top(N+1) 생성
                    List<Result<TopNResultValue>> allTopDruidList = druidRun(allTopBuilder.build());

                    if (allTopDruidList != null && !allTopDruidList.isEmpty()) {
                        Result allTopDruidResult = allTopDruidList.get(0);
                        List<DimensionAndMetricValueExtractor> allTopResultVal = ((TopNResultValue)(allTopDruidResult.getValue())).getValue();

                        // target이 0개인 경우 : allTop Query로 결과를 바로 반환.
                        if (targetRet.isEmpty()) {
                            List<ComDto.Code> allTopRet = new ArrayList<>();
                            for (DimensionAndMetricValueExtractor val : allTopResultVal) {
                                dimVal = (String)val.getDimensionValue(dsIcpmDimModel);

                                if (StringUtils.isNotBlank(dimVal)) {
                                    targetRet.add(new ComDto.Code(null, dimVal, 0));
                                    allTopRet.add(new ComDto.Code(null, dimVal, val.getMetric("value")));

                                    // N개 정해지면 break
                                    if (targetRet.size() >= dsIcpmThrModel) {
                                        return new ComDto.Chart(targetRet, allTopRet, allTopRet);
                                    }
                                }
                            }

                            // target이 0개가 아닌 경우
                        } else {
                            for (DimensionAndMetricValueExtractor val : allTopResultVal) {
                                dimVal = (String)val.getDimensionValue(dsIcpmDimModel);

                                if (StringUtils.isNotBlank(dimVal) && !inFltrList.contains(dimVal)) {
                                    targetRet.add(new ComDto.Code(null, dimVal, 0));
                                    inFltrList.add(dimVal);

                                    // N개 정해지면 break
                                    if (targetRet.size() >= dsIcpmThrModel) {
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }

                if (targetRet != null && !targetRet.isEmpty()) {

                    // target이 null이 아니고, fltrList가 존재 할때 All 쿼리 실행
                    if (fltrList != null && !fltrList.isEmpty()) {
                        // all dimension fltr list
                        List<DimFilter> allFltrList = new ArrayList<>();
                        allFltrList.add(occrDtDimFltr);
                        allFltrList.add(new InDimFilter(dsIcpmDimModel, inFltrList, null));

                        // all군(topN) 생성
                        GroupByQuery.Builder gBuilder = new GroupByQuery.Builder()
                                .setGranularity(Granularities.ALL)
                                .setDataSource(dsIcpm)
                                .setInterval(getInterval(occrDt))
                                .addDimension(dsIcpmDimModel)
                                .addAggregator(new CountAggregatorFactory("value"))
                                .setDimFilter(DimFilters.and(allFltrList))
                                ;

                        List<Row> aDruidResult = druidRun(gBuilder.build());
                        List<ComDto.Code> allRet = new ArrayList<>();
                        for (Row row : aDruidResult) {
                            MapBasedRow mapBasedRow = (MapBasedRow)row;
                            Map event = mapBasedRow.getEvent();
                            String evtNm = (String)event.get(dsIcpmDimModel);

                            if (StringUtils.isNotBlank(evtNm)) {
                                allRet.add(new ComDto.Code(null, evtNm, event.get("value")));
                            }
                        }

                        // target과 순서 일치를 위해 sort
                        List<ComDto.Code> allSortedRet = new ArrayList<>();
                        int subVal = 0;
                        for (ComDto.Code target : targetRet) {
                            allSortedRet.add(allRet.stream().filter(all -> target.getName().equals(all.getName())).findAny().orElse(null));
                            subVal = (Integer)allSortedRet.get(allSortedRet.size() - 1).getValue() - (Integer)target.getValue();
                            subRet.add(new ComDto.Code(null, target.getName(), subVal));
                        }

                        return new ComDto.Chart(targetRet, allSortedRet, subRet);
                    }
                }
            }

            // targetRet에 맞춰서 subRet 생성
            for (ComDto.Code code : targetRet) {
                subRet.add(new ComDto.Code(null, code.getName(), 0));
            }

            return new ComDto.Chart(targetRet, targetRet, subRet);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return null;
    }

    /**
     * 조직별 차트 조회
     * @param fltrDatVal
     * @return
     */
    public ComDto.Chart getOrgChart(List<UserBmrkDto.FltrDatVal> fltrDatVal, String occrDt) {
        try {
            // 필수 날짜 Dimension Filter
            DimFilter occrDtDimFltr = new SelectorDimFilter(DT, occrDt, null);

            // all Query 생성
            GroupByQuery.Builder builder = new GroupByQuery.Builder()
                    .setGranularity(Granularities.ALL)
                    .setDataSource(dsIcpm)
                    .setInterval(getInterval(occrDt))
                    .addDimension(dsIcpmDimTeam)
                    .addAggregator(
                            new CountAggregatorFactory("value"))
                    .setDimFilter(occrDtDimFltr)
                    ;

            List<ComDto.Code> allRet = new ArrayList<>();
            // (all-target) 결과 리스트
            List<ComDto.Code> subRet = new ArrayList<>();
            // 정렬된 본부 리스트
            List<ComDto.Code> allSortedRet = new ArrayList<>();
            List<Row> aDruidResult = druidRun(builder.build());
            if (aDruidResult != null && !aDruidResult.isEmpty()) {
                allSortedRet = ipmCommonService.getTeamList();
                ComDto.Code unclassified = null;
                for (ComDto.Code code : allSortedRet) {
                    String allCd = (String)code.getCode();
                    code.setValue(0);

                    for (Row row : aDruidResult) {
                        MapBasedRow mapBasedRow = (MapBasedRow)row;
                        Map event = mapBasedRow.getEvent();
                        String evtNm = (String)event.get(dsIcpmDimTeam);
                        int evtVal = (Integer)event.get("value");

                        if (allCd.equals(evtNm)) {
                            code.setValue(evtVal);
                            break;
                        }

                        if (StringUtils.isBlank(evtNm)) {
                            unclassified = new ComDto.Code(UNCLASSIFED, UNCLASSIFED, evtVal);
                        }
                    }
                }
                // 정보없음이 있을 경우 리스트 마지막에 추가해준다.
                if (unclassified != null) {
                    allSortedRet.add(unclassified);
                }

                // 필터 List 생성
                List<DimFilter> fltrList = getFltrList(fltrDatVal);

                // 필터가 존재할 경우, target Query 생성 ( 본부 목록, filterDatVal 내 filter로)
                if (fltrList != null && !fltrList.isEmpty()) {
                    // occr_dt 추가
                    fltrList.add(occrDtDimFltr);
                    // Query에 필터 설정
                    builder.setDimFilter(DimFilters.and(fltrList));

                    List<ComDto.Code> targetRet = new ArrayList<>();
                    List<Row> tDruidResult = druidRun(builder.build());
                    if (tDruidResult != null) {

                        Map<String, Integer> targetMap = new HashMap<>();
                        for (Row row : tDruidResult) {
                            MapBasedRow mapBasedRow = (MapBasedRow)row;
                            Map event = mapBasedRow.getEvent();
                            String evtNm = (String)event.get(dsIcpmDimTeam);
                            int evtVal = (Integer)event.get("value");

                            if (StringUtils.isNotBlank(evtNm)) {
                                targetMap.put(evtNm, evtVal);
                            } else {
                                targetMap.put(UNCLASSIFED, evtVal);
                            }
                        }

                        int subVal = 0;
                        // all군과 비교하여 없는값은 0으로 채운다.
                        for (int i = 0, allSize = allSortedRet.size(); i < allSize; i++) {
                            String allCode = (String)allSortedRet.get(i).getCode();
                            String allName = (String)allSortedRet.get(i).getName();
                            int allValue = (Integer)allSortedRet.get(i).getValue();

                            if (targetMap.containsKey(allCode)) {
                                targetRet.add(new ComDto.Code(allCode, allName, targetMap.get(allCode)));
                                subVal = allValue - targetMap.get(allCode);
                                subRet.add(new ComDto.Code(allCode, allName, subVal));
                            } else {
                                targetRet.add(new ComDto.Code(allCode, allName, 0));
                                subRet.add(new ComDto.Code(allCode, allName, allValue));
                            }
                        }
                    }

                    return new ComDto.Chart(targetRet, allSortedRet, subRet);
                }
            }

            // allRet에 맞춰서 subRet 생성
            for (ComDto.Code code : allSortedRet) {
                subRet.add(new ComDto.Code(code.getCode(), code.getName(), 0));
            }

            return new ComDto.Chart(allSortedRet, allSortedRet, subRet);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return null;
    }

    /**
     * Traffic별 차트 조회
     * @param fltrDatVal
     * @return
     */
    public ComDto.Chart getTrafficChart(List<UserBmrkDto.FltrDatVal> fltrDatVal, String occrDt) {
        try {
            // 필수 날짜 Dimension Filter
            DimFilter occrDtDimFltr = new SelectorDimFilter(DT, occrDt, null);

            // all Query 생성
            GroupByQuery.Builder builder = new GroupByQuery.Builder()
                    .setGranularity(Granularities.ALL)
                    .setDataSource(dsIcpm)
                    .setInterval(getInterval(occrDt))
                    .addDimension(dsIcpmDimTraffic)
                    .addAggregator(
                            new CountAggregatorFactory("value"))
                    .setDimFilter(occrDtDimFltr);

            List<ComDto.Code> allRet = new ArrayList<>();
            // (all-target) 결과 리스트
            List<ComDto.Code> subRet = new ArrayList<>();
            List<Row> aDruidResult = druidRun(builder.build());
            if (aDruidResult != null && !aDruidResult.isEmpty()) {
                int trafficMaxWithSum = 0; // traffic 최대값 초과 데이터 총합

                for (Row row : aDruidResult) {
                    MapBasedRow mapBasedRow = (MapBasedRow)row;
                    Map event = mapBasedRow.getEvent();
                    Object evt = event.get(dsIcpmDimTraffic);

                    if (evt != null) {
                        double evtNm = (Double)evt;
                        int evtVal = (Integer)event.get("value");

                        if (evtNm >= dsIcpmDimTrafficMax) {
                            trafficMaxWithSum += evtVal;

                        } else if (evtNm >= dsIcpmDimTrafficMin) {
                            allRet.add(new ComDto.Code(null, String.valueOf((int)evtNm), evtVal));
                        }
                    }
                }

                allRet.add(new ComDto.Code(null, dsIcpmDimTrafficMax + "(+)", trafficMaxWithSum));

                // 필터 List 생성
                List<DimFilter> fltrList = getFltrList(fltrDatVal);

                // 필터가 존재할 경우, target Query 생성
                if (fltrList != null && !fltrList.isEmpty()) {
                    // occr_dt 추가
                    fltrList.add(occrDtDimFltr);
                    // Query에 필터 설정
                    builder.setDimFilter(DimFilters.and(fltrList));

                    List<ComDto.Code> targetRet = new ArrayList<>();
                    List<Row> tDruidResult = druidRun(builder.build());
                    if (tDruidResult != null) {
                        trafficMaxWithSum = 0; // traffic 최대값 초과 데이터 총합

                        Map<String, Integer> targetMap = new HashMap<>();
                        for (Row row : tDruidResult) {
                            MapBasedRow mapBasedRow = (MapBasedRow) row;
                            Map event = mapBasedRow.getEvent();
                            Object evt = event.get(dsIcpmDimTraffic);

                            if (evt != null) {
                                double evtNm = (Double)evt;
                                int evtVal = (Integer)event.get("value");

                                if (evtNm >= dsIcpmDimTrafficMax) {
                                    trafficMaxWithSum += evtVal;

                                } else if (evtNm >= dsIcpmDimTrafficMin) {
                                    targetMap.put(String.valueOf((int)evtNm), evtVal);
                                }
                            }
                        }

                        targetMap.put(dsIcpmDimTrafficMax + "(+)", trafficMaxWithSum);

                        int subVal = 0;
                        // all군과 비교하여 없는 값은 0으로 채운다.
                        for (int i = 0, allSize = allRet.size(); i < allSize; i++) {
                            String allName = (String)allRet.get(i).getName();
                            int allValue = (Integer)allRet.get(i).getValue();

                            if (targetMap.containsKey(allName)) {
                                targetRet.add(new ComDto.Code(null, allName, targetMap.get(allName)));
                                subVal = allValue - targetMap.get(allName);
                                subRet.add(new ComDto.Code(null, allName, subVal));
                            } else {
                                targetRet.add(new ComDto.Code(null, allName, 0));
                                subRet.add(new ComDto.Code(null, allName, allValue));
                            }
                        }
                    }

                    return new ComDto.Chart(targetRet, allRet, subRet);
                }
            }

            // allRet에 맞춰서 subRet 생성
            for (ComDto.Code code : allRet) {
                subRet.add(new ComDto.Code(code.getCode(), code.getName(), 0));
            }

            return new ComDto.Chart(allRet, allRet, subRet);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return null;
    }

    /**
     * cei별 차트 조회
     * @param fltrDatVal
     * @return
     */
    public ComDto.Chart getCeiChart(List<UserBmrkDto.FltrDatVal> fltrDatVal, String occrDt) {
        try {
            // 필수 날짜 Dimension Filter
            DimFilter occrDtDimFltr = new SelectorDimFilter(DT, occrDt, null);

            StringBuilder sb = new StringBuilder();
            sb.append("function(name) {");
            for (int label : dsIcpmDimCeiLabel) {
                sb.append("if(name >= " + label + "){return " + label + ";}");
            }
            sb.append("return -9999999;}");

            // all Query 생성
            GroupByQuery.Builder builder = new GroupByQuery.Builder()
                    .setGranularity(Granularities.ALL)
                    .setDataSource(dsIcpm)
                    .setInterval(getInterval(occrDt))
                    .addDimension(new ExtractionDimensionSpec(dsIcpmDimCei, "name", ValueType.LONG,
                            new JavaScriptExtractionFn(sb.toString(), false, new JavaScriptConfig(true))))
                    .addAggregator(
                            new CountAggregatorFactory("value"))
                    .setDimFilter(occrDtDimFltr);

            List<ComDto.Code> allRet = new ArrayList<>();
            // (all-target) 결과 리스트
            List<ComDto.Code> subRet = new ArrayList<>();
            List<Row> aDruidResult = druidRun(builder.build());
            if (aDruidResult != null && !aDruidResult.isEmpty()) {

                for (Row row : aDruidResult) {
                    MapBasedRow mapBasedRow = (MapBasedRow)row;
                    Map event = mapBasedRow.getEvent();
                    Object evt = event.get("name");

                    if (evt != null) {
                        int evtNm = Integer.parseInt((String)evt);
                        int evtVal = (int)event.get("value");

                        if (dsIcpmDimCeiMax >= evtNm && evtNm >= dsIcpmDimCeiMin) {
                            allRet.add(new ComDto.Code(null, String.valueOf(evtNm), evtVal));
                        }
                    }
                }

                // 필터 List 생성
                List<DimFilter> fltrList = getFltrList(fltrDatVal);

                // 필터가 존재할 경우, target Query 생성
                if (fltrList != null && !fltrList.isEmpty()) {
                    // occr_dt 추가
                    fltrList.add(occrDtDimFltr);
                    // Query에 필터 설정
                    builder.setDimFilter(DimFilters.and(fltrList));

                    List<ComDto.Code> targetRet = new ArrayList<>();
                    List<Row> tDruidResult = druidRun(builder.build());
                    if (tDruidResult != null) {

                        Map<String, Integer> targetMap = new HashMap<>();
                        for (Row row : tDruidResult) {
                            MapBasedRow mapBasedRow = (MapBasedRow) row;
                            Map event = mapBasedRow.getEvent();
                            Object evt = event.get("name");

                            if (evt != null) {
                                int evtNm = Integer.parseInt((String)evt);
                                int evtVal = (int)event.get("value");

                                if (dsIcpmDimCeiMax >= evtNm && evtNm >= dsIcpmDimCeiMin) {
                                    targetMap.put(String.valueOf(evtNm), evtVal);
                                }
                            }
                        }

                        int subVal = 0;
                        // all군과 비교하여 없는 값은 0으로 채운다.
                        for (int i = 0, allSize = allRet.size(); i < allSize; i++) {
                            String allName = (String)allRet.get(i).getName();
                            int allValue = (Integer)allRet.get(i).getValue();

                            if (targetMap.containsKey(allName)) {
                                targetRet.add(new ComDto.Code(null, allName, targetMap.get(allName)));
                                subVal = allValue - targetMap.get(allName);
                                subRet.add(new ComDto.Code(null, allName, subVal));
                            } else {
                                targetRet.add(new ComDto.Code(null, allName, 0));
                                subRet.add(new ComDto.Code(null, allName, allValue));
                            }
                        }
                    }

                    return new ComDto.Chart(targetRet, allRet, subRet);
                }
            }

            // allRet에 맞춰서 subRet 생성
            for (ComDto.Code code : allRet) {
                subRet.add(new ComDto.Code(code.getCode(), code.getName(), 0));
            }

            return new ComDto.Chart(allRet, allRet, subRet);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return null;
    }

    /**
     * icpm EXCEL 다운로드
     * @param
     * @return
     */
    public boolean getIcpmExcel(List<UserBmrkDto.FltrDatVal> fltrDatVal, String occrDt, HttpServletResponse response) {
        try {
            List<String> dimensions = new ArrayList<>();
            List<String> metrics = new ArrayList<>();

            // 워크북 생성
            SXSSFWorkbook wb = new SXSSFWorkbook();
            Sheet sheet = wb.createSheet(dsIcpmExcelSheetNm);
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
            String msg = "최대 " + dsIcpmExcelLimit + "건까지 출력됩니다.";
            cell = row.createCell(0);
            cell.setCellValue(msg);

            row = sheet.createRow(rowNo++);

            // 번호
            cell = row.createCell(0);
            cell.setCellStyle(cellStyle);
            cell.setCellValue("번호");

            // IMSI
            cell = row.createCell(1);
            cell.setCellStyle(cellStyle);
            cell.setCellValue("IMSI");
            dimensions.add("imsi");

            // 서비스관리번호
            cell = row.createCell(2);
            cell.setCellStyle(cellStyle);
            cell.setCellValue("서비스관리번호");
            dimensions.add("svc_mgmt_no");

            List<IcpmFltrBasEntity> gridList = icpmFltrBasRepository.getGridList(Arrays.asList(dsIcpmExcelGridWoList));
            for (int i = 0, len = gridList.size(); i < len; i++) {

                IcpmFltrBasEntity entity = gridList.get(i);

                // 헤더 설정
                String scrnNm = entity.getScrnNm();
                cell = row.createCell(i + 3);
                cell.setCellStyle(cellStyle);
                cell.setCellValue(scrnNm);

                // metrics, dimensions 설정
                String druidNm = entity.getDruidNm();
                if (StringUtils.isNotBlank(entity.getFltrNm())) {
                    druidNm = entity.getFltrNm();
                }

                String scrnClNm = entity.getScrnClNm();
                switch (scrnClNm) {
                    case CHECKRANGE:
                    case RANGE:
                        metrics.add(druidNm);
                        break;
                    case ADDRESS:
                        String[] names = druidNm.split("\\|");
                        for (String name : names) {
                            dimensions.add(name);
                        }
                        break;
                    default:
                        dimensions.add(druidNm);
                        break;
                }
            }

            // 필수 날짜 Dimension Filter
            DimFilter occrDtDimFltr = new SelectorDimFilter(DT, occrDt, null);

            // 필터 List 생성
            List<DimFilter> fltrList = getFltrList(fltrDatVal);
            if (fltrList == null) {
                fltrList = new ArrayList<DimFilter>();
            }

            // occr_dt 추가
            fltrList.add(occrDtDimFltr);

            Druids.SelectQueryBuilder select = new Druids.SelectQueryBuilder()
                    .granularity(Granularities.ALL)
                    .dataSource(dsIcpm)
                    .intervals(getInterval(occrDt))
                    .dimensions(dimensions)
                    .metrics(metrics)
                    .filters(DimFilters.and(fltrList))
                    .pagingSpec(new PagingSpec(null, dsIcpmExcelLimit))
                    ;

            List<Result<SelectResultValue>> results = druidRun(select.build());

            if (results != null && !results.isEmpty()) {

                Result<SelectResultValue> result = results.get(0);
                List<EventHolder> events = result.getValue().getEvents();

                int no = events.size();
                for (EventHolder holder : events) {

                    Map event = holder.getEvent();

                    row = sheet.createRow(rowNo++);

                    // 번호
                    cell = row.createCell(0);
                    cell.setCellStyle(cellStyle);
                    cell.setCellValue(no--);

                    // IMSI
                    cell = row.createCell(1);
                    cell.setCellStyle(cellStyle);
                    cell.setCellValue("IMSI");
                    String  imsi = "";
                    if (event.get("imsi") != null) {
                        imsi = String.valueOf(event.get("imsi"));
                        if ("null".equalsIgnoreCase(imsi)) {
                            imsi = "";
                        }
                    }
                    cell.setCellValue(imsi);

                    // 서비스관리번호
                    cell = row.createCell(2);
                    cell.setCellStyle(cellStyle);
                    cell.setCellValue("서비스관리번호");
                    String svcMgmtNo = "";
                    if (event.get("svc_mgmt_no") != null) {
                        svcMgmtNo = String.valueOf(event.get("svc_mgmt_no"));
                        if ("null".equalsIgnoreCase(svcMgmtNo)) {
                            svcMgmtNo = "";
                        }
                    }
                    cell.setCellValue(svcMgmtNo);

                    for (int i = 0, len = gridList.size(); i < len; i++) {

                        IcpmFltrBasEntity entity = gridList.get(i);

                        cell = row.createCell(i + 3);
                        cell.setCellStyle(cellStyle);

                        String druidNm = entity.getDruidNm();
                        if (StringUtils.isNotBlank(entity.getFltrNm())) {
                            druidNm = entity.getFltrNm();
                        }

                        String scrnClNm = entity.getScrnClNm();
                        switch (scrnClNm) {
                            case CHECKRANGE:
                            case RANGE:
                                double value = -9999999;
                                if (event.get(druidNm) != null) {
                                    value = (double)event.get(druidNm);
                                }

                                if (value > -9999999) {
                                    cell.setCellValue(value);
                                } else {
                                    cell.setCellValue("");
                                }

                                break;
                            case ADDRESS:
                                String address = "";
                                String[] names = druidNm.split("\\|");
                                for (String name : names) {
                                    String  val = "";
                                    if (event.get(name) != null) {
                                        val = String.valueOf(event.get(name));
                                        if ("null".equalsIgnoreCase(val)) {
                                            val = "";
                                        }
                                    }

                                    address += val + " ";
                                }

                                cell.setCellValue(address.trim());
                                break;
                            default:
                                String  val = "";
                                if (event.get(druidNm) != null) {
                                    val = String.valueOf(event.get(druidNm));
                                    if ("null".equalsIgnoreCase(val)) {
                                        val = "";
                                    }
                                }

                                cell.setCellValue(val);
                                break;
                        }
                    }
                }
            }

            // 엑셀 응답 정보 및 파일 이름 지정
            response.setContentType("ms-vnd/excel");
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(dsIcpmExcelSheetNm, "UTF-8") + "-" + occrDt + "." + ExcelGenHelper.XLSX);

            // 엑셀 출력
            wb.write(response.getOutputStream());
            wb.close();

            return true;
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        return false;
    }
}