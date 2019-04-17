package app.metatron.discovery.domain.ipm.service.common;

import app.metatron.discovery.domain.ipm.domain.common.ComDto;
import io.druid.data.input.MapBasedRow;
import io.druid.data.input.Row;
import io.druid.java.util.common.granularity.Granularities;
import io.druid.query.dimension.DefaultDimensionSpec;
import io.druid.query.filter.*;
import io.druid.query.groupby.GroupByQuery;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class IpmCommonService extends DruidService {

    /**
     * druid pool
     */
    @Value("${polaris.ipm.druid-com-pool}")
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
     * 공통 druid
     */
    @Value("${polaris.ipm.ds-com}")
    private String dsCom;

    /**
     * 공통 druid dimension
     */
    @Value("${polaris.ipm.ds-com-dim}")
    private String[] dsComDim;

    /**
     * 공통 druid dimesion filter
     */
    @Value("${polaris.ipm.ds-com-fltr}")
    private String dsComFltr;

    /**
     * 주소 druid
     */
    @Value("${polaris.ipm.ds-addr}")
    private String dsAddr;

    /**
     * 주소 druid 조회 기간
     */
    @Value("${polaris.ipm.ds-addr-interval}")
    private String dsAddrInterval;

    /**
     * 시도 dimension
     */
    @Value("${polaris.ipm.ds-addr-dim-sido}")
    private String[] dsAddrDimSido;

    /**
     * 시군구 dimension
     */
    @Value("${polaris.ipm.ds-addr-dim-sgg}")
    private String[] dsAddrDimSgg;

    /**
     * 시군구 dimension filter
     */
    @Value("${polaris.ipm.ds-addr-fltr-sgg}")
    private String dsAddrFltrSgg;

    /**
     * 읍면동 dimension
     */
    @Value("${polaris.ipm.ds-addr-dim-dong}")
    private String[] dsAddrDimDong;

    /**
     * 읍면동 dimension filter
     */
    @Value("${polaris.ipm.ds-addr-fltr-dong}")
    private String[] dsAddrFltrDong;

    /**
     * 휴일 data source
     */
    @Value("${polaris.ipm.ds-hday}")
    private String dsHday;

    /**
     * 휴일 dimension filter
     */
    @Value("${polaris.ipm.ds-hday-fltr}")
    private String dsHdayFltr;

    /**
     * 휴일 dimension filter
     */
    @Value("${polaris.ipm.ds-hday-interval}")
    private String dsHdayInterval;

    /**
     * 본부/팀 datasource
     */
    @Value("${polaris.ipm.ds-team}")
    private String dsTeam;

    /**
     * 본부/팀 interval
     */
    @Value("${polaris.ipm.ds-team-interval}")
    private String dsTeamInterval;

    /**
     * 본부/팀 dimension
     */
    @Value("${polaris.ipm.ds-team-dim}")
    private String[] dsTeamDim;

    /**
     * dimension : dt, DruidNm : dt
     */
    private final String DT = "dt";

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

    // grpCd
    // 001 단말기제조사정보
    // 006 단말기 정보
    // 003 VOC원인
    // 004 요금제정보
    /**
     * 공통 코드 데이터 조회
     * @param comm
     * @return
     */
    public List<ComDto.Code> getCommList(Map<String, String> comm) {
        try {
            // 필수 날짜 Dimension Filter, comGrpCd Filter
            String occrDt = comm.get("occrDt");
            List<DimFilter> fltrList = new ArrayList<>();
            fltrList.add(new SelectorDimFilter(DT, occrDt, null));
            fltrList.add(new SelectorDimFilter(dsComFltr, comm.get("comGrpCd"), null));

            GroupByQuery.Builder builder = new GroupByQuery.Builder()
                    .setGranularity(Granularities.ALL)
                    .setDataSource(dsCom)
                    .setInterval(getInterval(occrDt))
                    .setDimensions(DefaultDimensionSpec.toSpec(dsComDim))
                    .setDimFilter(DimFilters.and(fltrList));

            List<Row> results = druidRun(builder.build());
            List<ComDto.Code> ret = new ArrayList<>();
            if (results != null && !results.isEmpty()) {
                for (Row row : results) {
                    MapBasedRow mapBasedRow = (MapBasedRow) row;
                    Map event = mapBasedRow.getEvent();
                    String evtCd = (String) event.get(dsComDim[0]);
                    String evtNm = (String) event.get(dsComDim[1]);

                    if (evtNm == null) {
                        evtNm = "";
                    }

                    ret.add(new ComDto.Code(evtCd, evtNm));
                }
            }

            return ret;
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return null;
    }

    /**
     * 하위 주소 리스트 가져오기
     * @param addrCd
     * @return
     */
    public List<ComDto.Code> getAddress(List<String> addrCd) {
        List<ComDto.Code> addrList = null;
        try {
            switch (addrCd.size()) {
                case 0: //시 조회 서비스
                    addrList = getSidoList();
                    break;
                case 1: //구 조회 서비스
                    if (StringUtils.isNotBlank(addrCd.get(0))) {
                        addrList = getSggList(addrCd.get(0));
                    }
                    break;
                case 2: //동 조회 서비스
                    if (StringUtils.isNotBlank(addrCd.get(0)) && StringUtils.isNotBlank(addrCd.get(1))) {
                        addrList = getDongList(addrCd.get(0), addrCd.get(1));
                    }
                    break;
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return addrList;
    }

    /**
     * 시도 List 조회
     * @param
     * @return
     */
    private List<ComDto.Code> getSidoList() {

        GroupByQuery.Builder builder = new GroupByQuery.Builder()
                .setGranularity(Granularities.ALL)
                .setDataSource(dsAddr)
                .setInterval(dsAddrInterval)
                .setDimensions(DefaultDimensionSpec.toSpec(dsAddrDimSido))
                ;

        List<Row> results = druidRun(builder.build());

        List<ComDto.Code> ret = new ArrayList<>();
        for(Row row : results) {
            MapBasedRow mapBasedRow = (MapBasedRow)row;
            Map event = mapBasedRow.getEvent();
            ret.add(new ComDto.Code(event.get(dsAddrDimSido[2]), event.get(dsAddrDimSido[1])));
        }

        return ret;
    }

    /**
     * 시군구 List 조회
     * @param sidoCd (시도 코드)
     * @return
     */
    private List<ComDto.Code> getSggList(String sidoCd) {

        GroupByQuery.Builder builder = new GroupByQuery.Builder()
                .setGranularity(Granularities.ALL)
                .setDataSource(dsAddr)
                .setInterval(dsAddrInterval)
                .setDimensions(DefaultDimensionSpec.toSpec(dsAddrDimSgg))
                .setDimFilter(new SelectorDimFilter(dsAddrFltrSgg, sidoCd, null))
                ;

        List<Row> results = druidRun(builder.build());

        List<ComDto.Code> ret = new ArrayList<>();
        for(Row row : results) {
            MapBasedRow mapBasedRow = (MapBasedRow)row;
            Map event = mapBasedRow.getEvent();
            ret.add(new ComDto.Code(event.get(dsAddrDimSgg[1]), event.get(dsAddrDimSgg[0])));
        }

        return ret;
    }

    /**
     * 읍면동 List 조회
     * @param sidoCd, sggCd (시도 코드, 시군구 코드)
     * @return
     */
    private List<ComDto.Code> getDongList(String sidoCd, String sggCd) {

        List<DimFilter> fltrList = new ArrayList<>();
        fltrList.add(new SelectorDimFilter(dsAddrFltrDong[0], sidoCd,null));
        fltrList.add(new SelectorDimFilter(dsAddrFltrDong[1], sggCd,null));

        GroupByQuery.Builder builder = new GroupByQuery.Builder()
                .setGranularity(Granularities.ALL)
                .setDataSource(dsAddr)
                .setInterval(dsAddrInterval)
                .setDimensions(DefaultDimensionSpec.toSpec(dsAddrDimDong))
                .setDimFilter(DimFilters.and(fltrList))
                ;

        List<Row> results = druidRun(builder.build());

        List<ComDto.Code> ret = new ArrayList<>();
        for(Row row : results) {
            MapBasedRow mapBasedRow = (MapBasedRow)row;
            Map event = mapBasedRow.getEvent();
            ret.add(new ComDto.Code(event.get(dsAddrDimDong[1]), event.get(dsAddrDimDong[0])));
        }

        return ret;
    }

    /**
     * 휴일 목록 조회
     * @param occrDth
     * @return
     */
    public List<String> getHdayList(List<ComDto.Code> occrDth) {

        List<DimFilter> fltrList = new ArrayList<>();

        // occr_dth로 fltr 추가
        List<DimFilter> fltr = new ArrayList<>();
        for (ComDto.Code cd : occrDth) {
            String[] bound = ((String)cd.getCode()).split("~");
            fltr.add(new BoundDimFilter(DT, bound[0].substring(0, 8), bound[1].substring(0, 8), false, false, null, null, null));
        }
        if (fltr.size() > 1) {
            fltrList.add(new OrDimFilter(fltr));
        } else {
            fltrList.add(fltr.get(0));
        }

        fltrList.add(new SelectorDimFilter(dsHdayFltr, "Y", null));

        GroupByQuery.Builder builder = new GroupByQuery.Builder()
                .setGranularity(Granularities.ALL)
                .setDataSource(dsHday)
                .setInterval(dsHdayInterval)
                .setDimensions(DefaultDimensionSpec.toSpec(DT))
                .setDimFilter(DimFilters.and(fltrList))
                ;

        List<Row> results = druidRun(builder.build());

        List<String> ret = new ArrayList<>();

        for(Row row : results) {
            MapBasedRow mapBasedRow  = (MapBasedRow)row;
            Map event = mapBasedRow.getEvent();
            ret.add((String)event.get(DT));
        }

        return ret;
    }

    /**
     * 본부 조회
     * @return
     */
    public List<ComDto.Code> getTeamList() {

        GroupByQuery.Builder builder = new GroupByQuery.Builder()
                .setGranularity(Granularities.ALL)
                .setDataSource(dsTeam)
                .setInterval(dsTeamInterval)
                .setDimensions(DefaultDimensionSpec.toSpec(dsTeamDim))
                ;

        List<Row> results = druidRun(builder.build());

        List<ComDto.Code> ret = new ArrayList<>();
        for(Row row : results) {
            MapBasedRow mapBasedRow = (MapBasedRow)row;
            Map event = mapBasedRow.getEvent();
            ret.add(new ComDto.Code(event.get(dsTeamDim[2]), event.get(dsTeamDim[1])));
        }

        return ret;
    }
}
