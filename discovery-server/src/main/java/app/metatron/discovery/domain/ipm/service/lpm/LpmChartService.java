package app.metatron.discovery.domain.ipm.service.lpm;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.simplaex.clients.druid.DruidResult;
import app.metatron.discovery.domain.ipm.domain.lpm.CommDto;
import app.metatron.discovery.domain.ipm.domain.lpm.LpmChartDto;
import app.metatron.discovery.domain.ipm.domain.lpm.LpmChartDto.Address;
import app.metatron.discovery.domain.ipm.domain.lpm.LpmChartDto.Population;
import io.druid.data.input.MapBasedRow;
import io.druid.data.input.Row;
import io.druid.java.util.common.granularity.Granularities;
import io.druid.java.util.common.guava.Yielder;
import io.druid.java.util.common.guava.YieldingAccumulator;
import io.druid.query.Druids;
import io.druid.query.QueryInterruptedException;
import io.druid.query.Result;
import io.druid.query.aggregation.DoubleSumAggregatorFactory;
import io.druid.query.dimension.DefaultDimensionSpec;
import io.druid.query.filter.*;
import io.druid.query.groupby.GroupByQuery;
import io.druid.query.select.EventHolder;
import io.druid.query.select.PagingSpec;
import io.druid.query.select.SelectQuery;
import io.druid.query.select.SelectResultValue;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Nullable;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.parquet.Strings;
import org.joda.time.Interval;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class LpmChartService {

    private String dsLpm;

    @Value("${polaris.lpm.ds-lpm2}")
    private String dsLpm2;    
    
    private final static String dimAddr = "address"; //지역별
    private final static String dimRange = "range"; //연령별
    private final static String dimMultiple = "multiple"; //성별
    private final static String dimCalendar = "calendar"; //날짜
    private final static String dimDateTime = "datetime"; //날짜
    private final static String dimCei = "cei"; //cei
    
    private final static String dimHour = "hour"; //시간
    private final static String dimGeo = "geo"; //시간
    
    private final static String dimDt = "dt";
    private final static String dimHh = "hh";
    
    private final static String dimLoc = "ldong_cd";
    private final static String dimAge = "user_age";
    private final static String dimSex = "user_sex_cd";
    private final static String dimUserCnt = "user_cnt";
    private final static String dimEnbId = "enb_id";
    private final static String dimCellId = "cell_id";
    private final static String dimTotCeiVal = "tot_cei_val";
    
    private final static String all = "all";
    private final static String target = "target";
    private static String chartInterval;
    private static String gridInterval;
    
    private final int ageMin = 0;

    private final int ageMax = 80;

    private final int ageInterval = 5;
    
    enum OPT {
        VENDOR("ue_vend", 10), UE("ue_mdl_alis", 20);
        final String value;
        final int threshold;
        OPT(String value, int threshold) {this.value = value; this.threshold = threshold;}
    }
    
    private void makeInterval(String tempCode, boolean reqType) {

        String ret = "";
        String code = tempCode;
        String[] dt = code.split("~");
        LocalDateTime start = LocalDateTime.parse(dt[0], DateTimeFormatter.ofPattern("yyyyMMddHH"));
        LocalDateTime end = LocalDateTime.parse(dt[1], DateTimeFormatter.ofPattern("yyyyMMddHH"));
        
        List<Interval> interval = new ArrayList<>();
        interval.add(
                Interval.parse(start.toString()+"Z/"+end.toString()+"Z")
        );
        if(reqType) {
        	chartInterval = interval.get(0).toString();
        } else {
        	gridInterval = interval.get(0).toString();
        }
        
    }    
    
    private DimFilter fetchDimFilter(Object filters, @Nullable String druidNm, boolean reqType) {
        ObjectMapper mapper = new ObjectMapper();
        JSONArray filterArr = (JSONArray)filters;
        List<DimFilter> dimFilters = new ArrayList<>();
        for(Object filter : filterArr) {
            DimFilter dimFilter = null;
            try {
				CommDto.FAVOR favorite = mapper.readValue(filter.toString(), CommDto.FAVOR.class);
                if(Strings.isNullOrEmpty(druidNm)) {
                	if(favorite.getScrnClNm().equals(dimAddr)) {
                		favorite.setDruidNm(favorite.getFltrNm()); 
                	} else if(favorite.getScrnClNm().equals(dimRange)) {
                		favorite.setDruidNm(favorite.getFltrNm());
                	} else if(favorite.getScrnClNm().equals(dimMultiple)) {
                		favorite.setDruidNm(favorite.getFltrNm());
                	} else if(favorite.getScrnClNm().equals(dimHour)) {
                		favorite.setDruidNm(favorite.getFltrNm());
                	} else if(favorite.getScrnClNm().equals(dimCalendar)) {
                		favorite.setDruidNm(favorite.getFltrNm());
                	} else if(favorite.getScrnClNm().equals(dimCei)) {
                		favorite.setDruidNm(favorite.getFltrNm());
                	} else if(favorite.getScrnClNm().equals(dimDateTime)) {
                		makeInterval(favorite.getFltrVal().get(0).getCode().toString(), reqType);
                		continue;
                	}
                } else {
                    favorite.setDruidNm(druidNm);
                }				
				dimFilter = fetchFavoriteObject(favorite);
            } catch (Exception e) {
                log.error(e.getMessage());
            }
            dimFilters.add(dimFilter);

        }
        DimFilter ret = null;
        if(dimFilters.size() == 1) {
            ret = dimFilters.get(0);
        } else if (dimFilters.size()>1) {
            ret = new AndDimFilter(dimFilters);
        } else {

        }
        return ret;
    }
    
    private DimFilter fetchFavoriteObject(CommDto.FAVOR favorite) {
        DimFilter ret = null;
        final String OR = "\\|";
        final String NOTTYPE = "not";
        final String rangeDilim = "~";
        String druidFm = "00";
        
        CommDto.FAVOR tmp = favorite;
        List<CommDto> dtValue = (List<CommDto>) tmp.getFltrVal();
        String druidNm = tmp.getDruidNm();
        String scrnClNm = tmp.getScrnClNm();
        
        String type = tmp.getType();
        if(Objects.isNull(dtValue)) {
            return ret;
        }

        //Multiple Case of DruidName ( cf : FNG1010 )
        if(!Strings.isNullOrEmpty(druidNm) && druidNm.contains("|")) {
            List<String> druidNms = tmp.getMultiDruidNm();
            List<CommDto> values = tmp.getFltrVal();
            List<DimFilter> dimFilters = new ArrayList<>();
            for(int i =0; i < druidNms.size(); i++) {
                tmp.setDruidNm(druidNms.get(i));
                tmp.setFltrVal(Arrays.asList(new CommDto(values.get(i).getName(),values.get(i).getCode())));
                dimFilters.add(fetchFavoriteObject(tmp));
            }

            if(dimFilters.size() == 1 ) {
                ret = dimFilters.get(0);
            } else if(dimFilters.size() > 1) {
                ret = new OrDimFilter(dimFilters);
            } else {
                ret = null;
            }
            return ret;
        }

//        List<String> or = Arrays.asList(dtValue.split(orDilim));
        List<CommDto> or = dtValue;
        List<DimFilter> dimFilters = new ArrayList<>();
        for(CommDto item : or) {
            String code = "";
            List values = null;
            if(item.getCode() instanceof String) {
                code = (String) item.getCode();
            } else if(item.getCode() instanceof List) {
                values = (List) item.getCode();
            }


            if(code.contains(rangeDilim)) {
                List<String> vals = LpmUtil.parseValue(code);
                
                String startVal = "";
                String endVal = "";
                
                if(tmp.getFltrNm().equals(dimAge)) {
                	if(vals.get(0).equals("0")) {
                		startVal = vals.get(0);
                	} else {
                		int tmpVal = Integer.parseInt(vals.get(0))/10;
                		startVal = "0" + tmpVal;
                	}
                	
                	if(vals.get(1).equals("0")) {
                		endVal = vals.get(1);
                	} else {
                		int tmpVal = Integer.parseInt(vals.get(1))/10;
                		endVal = "0" + tmpVal;               	
                	}
                } else {
                	startVal = vals.get(0);
                	endVal = vals.get(1);
                }
                
                values = LpmUtil.getRangeData(startVal, endVal, druidFm);
                if(NOTTYPE.equals(type)) {
                    dimFilters.add(new NotDimFilter(new InDimFilter(druidNm, values, null)));
                } else {
                    dimFilters.add(new InDimFilter(druidNm, values, null));
                }
            } else {

                if(values == null && !Strings.isNullOrEmpty(code)) {
                    values = Arrays.asList(code);
                }
                //ldong_cd일 경우엔 RegexdimFilter로 바꿔야 한다.
                switch(scrnClNm) {
                    case dimAddr:
                        String likeStr = LpmUtil.makeLikeWord(values);
                        if(NOTTYPE.equals(type)) {
                            dimFilters.add(new NotDimFilter(new RegexDimFilter(druidNm, "^" + likeStr, null))); //likeStr+"[0-9]*"
                        } else {
                            dimFilters.add(new RegexDimFilter(druidNm, "^" + likeStr, null)); //likeStr+"[0-9]*"
                        }
                        break;
                    //case dimHh:
                    case dimCalendar:
                        List<String> vals = Arrays.asList(code.split("~"));
                        if(NOTTYPE.equals(type)) {
                            dimFilters.add(new NotDimFilter(new BoundDimFilter(druidNm, vals.get(0), vals.get(1), false, false, null, null, null)));
                        } else {
                            dimFilters.add(new BoundDimFilter(druidNm, vals.get(0), vals.get(1), false, false, null, null, null));
                        }
                        break;                        
                    default:
                        if(NOTTYPE.equals(type)) {
                            dimFilters.add(new NotDimFilter(new InDimFilter(druidNm, values, null)));
                        } else {
                            dimFilters.add(new InDimFilter(druidNm, values, null));
                        }
                        break;
                }


            }
        }

        if(dimFilters.size() == 1 ) {
            ret = dimFilters.get(0);
        } else if(dimFilters.size() > 1) {
            if(NOTTYPE.equals(type)) {
                ret = new AndDimFilter(dimFilters);
            } else {
                ret = new OrDimFilter(dimFilters);
            }
        } else {
            ret = null;
        }
        
        return ret;
    }


    /**
     * 연령별 데이터
     * @param jsonObject
     * @return
     */
    public Map<String, Object> getChartPerAge(@Nullable JSONObject jsonObject) {
    	
    	dsLpm = (String)jsonObject.get("dsNm");
    	chartInterval = null;
    	
        JSONArray filters = LpmUtil.getJsonArray(jsonObject);
        String measureNm = "";
        DimFilter dimFilter = null;
        if(filters == null) {
            measureNm = all;
        } else {
            measureNm=target;
            dimFilter = fetchDimFilter(filters, null, true);
        }
        
        if(chartInterval == null || chartInterval.isEmpty()) {
        	chartInterval = LpmDruidMetaService.getDefaultIntervar(dsLpm);
        }

        GroupByQuery.Builder builder = new GroupByQuery.Builder()
                .setDataSource(dsLpm)
                .setInterval(chartInterval)
                .setGranularity(Granularities.ALL)
                .addDimension(dimAge)
                .addDimension(dimSex)
//                .setVirtualColumns(vc)
                .addAggregator(
                		new DoubleSumAggregatorFactory(measureNm, dimUserCnt)
                        //new CountAggregatorFactory(measureNm)
                );

        if(dimFilter != null) {
            builder.setDimFilter(dimFilter);
        }

        GroupByQuery query = builder.build();

        List<Row> results = LpmDruidMetaService.druidRun(query);
        Map<String, Object> result = new HashMap<>();
        
        List<CommDto> man = new ArrayList<>();
        List<CommDto> woman = new ArrayList<>();
        
        if (results != null) {
            for (int i = ageMin; i <= ageMax; i += ageInterval) {
            	man.add(CommDto.set(String.valueOf(i), 0.0));
            	woman.add(CommDto.set(String.valueOf(i), 0.0));
            }
            int len = man.size();
          
            for(Row row : results) {
                MapBasedRow mapBasedRow  = (MapBasedRow) row;
                String nm = (String)mapBasedRow.getEvent().get(dimAge);
                String sex = (String) mapBasedRow.getEvent().get(dimSex);
                
                if(sex.equals("1")) {
                	if (StringUtils.isNumeric(nm)) {
                		int age = Integer.parseInt(nm) * 10;
                		if (age >= ageMin && age <= ageMax) {
                			int index = (int)(age / ageInterval);
                			double count = (double) mapBasedRow.getEvent().get(measureNm);
                			CommDto cdto = man.get(index);
                			cdto.setValue(Math.round(((double) cdto.getValue() + count)*100)/100.0);
                		}
                	}
                	
                } else if(sex.equals("2")) {
                	if (StringUtils.isNumeric(nm)) {
                		int age = Integer.parseInt(nm) * 10;
                		if (age >= ageMin && age <= ageMax) {
                			int index = (int)(age / ageInterval);
                			double count = (double) mapBasedRow.getEvent().get(measureNm);
                			CommDto cdto = woman.get(index);
                			cdto.setValue(Math.round(((double) cdto.getValue() + count)*100)/100.0);
                		}
                	}                	
                } else if(sex.equals("#")) {
                	
                }
            }
            
            if (len > 0) {
            	CommDto cdto = man.get(len - 1);
            	cdto.setName(cdto.getName() + "+");
            	cdto = woman.get(len - 1);
            	cdto.setName(cdto.getName() + "+");
            }

            result.put("man", man);
            result.put("woman", woman);
            
            return result;
        }
        
        return result;
    }    
    
    /**
     * 지역별 데이터
     * @param jsonObject
     * @return
     */
    public List<Map<String, Object>> getGridPerLoc(@Nullable JSONObject jsonObject) {
    	
    	dsLpm = (String)jsonObject.get("dsNm");
    	gridInterval = null;
    	
        JSONArray filters = LpmUtil.getJsonArray(jsonObject);
        String measureNm = "";
        DimFilter dimFilter = null;
        DimFilter ret = null;
        List<DimFilter> fields = new ArrayList<>();
        final String NOTTYPE = "not";
        
        if(filters == null) {
            measureNm = all;
        } else {
            measureNm=target;
            dimFilter = fetchDimFilter(filters, null, false);
        }
        
        if(gridInterval == null || gridInterval.isEmpty()) {
        	gridInterval = LpmDruidMetaService.getDefaultIntervar(dsLpm);
        }
        
        //List<AggregatorFactory> aggregatorList = new ArrayList<>();
        //aggregatorList.add(new DoubleSumAggregatorFactory("rrc_susc_cnt_s", "rrc_susc_cnt"));
        //aggregatorList.add(new DoubleSumAggregatorFactory("rrc_attc_cnt_s", "rrc_attc_cnt"));

        GroupByQuery.Builder builder = new GroupByQuery.Builder()
                .setDataSource(dsLpm)
                .setInterval(gridInterval)
                .setGranularity(Granularities.ALL)
                .addDimension(dimLoc)
                .addDimension(dimSex)
                .addAggregator(
                		new DoubleSumAggregatorFactory(measureNm, dimUserCnt)
                        //new CountAggregatorFactory(measureNm)
                );

        if(dimFilter != null) {
            builder.setDimFilter(dimFilter);
        }
        
        //aggregatorList.stream().forEach(e->builder.addAggregator(e));

        GroupByQuery query = builder.build();

        List<Row> results = LpmDruidMetaService.druidRun(query);
        List<Map<String, Object>> result = new ArrayList<>();
        Map<String, Object> sumList = new HashMap<>();
        
        //행정동 데이터 쿼리
        JSONArray filterArr = (JSONArray) filters;
        ObjectMapper mapper = new ObjectMapper();
        for(Object filter : filterArr) {
            try {
            	CommDto.FAVOR favorite = mapper.readValue(filter.toString(), CommDto.FAVOR.class);
            	String type = favorite.getType();
            	
            	if(favorite.getScrnClNm().equals("address")) {
            		favorite.getFltrVal().get(0).getCode();
            		List values = null;
            		for(int i=0; i<favorite.getFltrVal().size(); i++) {
            			values = (List) favorite.getFltrVal().get(i).getCode();
            			
                        if(NOTTYPE.equals(type)) {
                   			if(values.size() == 1) {
                				fields.add(new NotDimFilter(new RegexDimFilter(dimLoc, "^" + values.get(0).toString(), null)));
                			}else if(values.size() == 2) {
                				fields.add(new NotDimFilter(new RegexDimFilter(dimLoc, "^" + values.get(0).toString() + values.get(1).toString(), null)));
                			}else if(values.size() == 3) {
                				fields.add(new NotDimFilter(new RegexDimFilter(dimLoc, "^" 
                						+ values.get(0).toString() + values.get(1).toString() + values.get(2).toString(), null)));
                			}
                        } else {
                   			if(values.size() == 1) {
                				fields.add(new RegexDimFilter(dimLoc, "^" + values.get(0).toString(), null));
                			}else if(values.size() == 2) {
                				fields.add(new RegexDimFilter(dimLoc, "^" + values.get(0).toString() + values.get(1).toString(), null));
                			}else if(values.size() == 3) {
                				fields.add(new RegexDimFilter(dimLoc, "^" 
                						+ values.get(0).toString() + values.get(1).toString() + values.get(2).toString(), null));
                			}
                        }
            		}
            		
                    if(fields.size() == 1 ) {
                        ret = fields.get(0);
                    } else if(fields.size() > 1) {
                        if(NOTTYPE.equals(type)) {
                            ret = new AndDimFilter(fields);
                        } else {
                            ret = new OrDimFilter(fields);
                        }
                    } else {
                        ret = null;
                    }
            	}
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }            
        
    	List<String> dimensionList = new ArrayList<>();    	
    	
        dimensionList.add("sido_nm");
        dimensionList.add("sgg_nm");
        dimensionList.add("dong_nm");
        dimensionList.add("sido_cd");
        dimensionList.add("sgg_cd");
        dimensionList.add("dong_cd");
        dimensionList.add("ldong_cd");
    	
        SelectQuery locQuery = null;
        
        if(fields.size() > 0) {
        	locQuery = new Druids.SelectQueryBuilder()
        			.dataSource(dsLpm2)
        			.pagingSpec(new PagingSpec(Collections.emptyMap(), 100000))
        			.dimensionSpecs(DefaultDimensionSpec.toSpec(dimensionList))
        			.filters(
        					ret
        					)
        			.intervals(LpmDruidMetaService.getLocDefaultIntervar(dsLpm2))
        			.granularity(Granularities.ALL).build();
        } else {
        	locQuery = new Druids.SelectQueryBuilder()
        			.dataSource(dsLpm2)
        			.pagingSpec(new PagingSpec(Collections.emptyMap(), 100000))
        			.dimensionSpecs(DefaultDimensionSpec.toSpec(dimensionList))
        			.intervals(LpmDruidMetaService.getLocDefaultIntervar(dsLpm2))
        			.granularity(Granularities.ALL).build();           	
        }

        try {
        	DruidResult<Result<SelectResultValue>> locResult = LpmDruidMetaService.druidRunSelect(locQuery);
        	 
            Yielder resultYielder = locResult.getSequence().toYielder(new ArrayList(), new YieldingAccumulator<ArrayList, Result<SelectResultValue>>() {
                @Override
                public ArrayList accumulate(ArrayList accumulated, Result<SelectResultValue> selectResultValueResult) {
                    accumulated.add(selectResultValueResult);
                    return accumulated;
                }
            });

            List<Result<SelectResultValue>> resultList = (List)resultYielder.get();
            if( resultList.size() == 0 ){
                return null;
            }
        	
            final Result<SelectResultValue> firstResult = resultList.get(resultList.size()-1);
            final SelectResultValue selectResult = firstResult.getValue();
            final List<EventHolder> events = selectResult.getEvents();      
            
            if (results != null) {
            	String tmpLoc = null;
            	String tmpSex = null;
                for(Row row : results) {
                    MapBasedRow mapBasedRow = (MapBasedRow) row;
                    Map<String, Object> map = mapBasedRow.getEvent();
                    
                    String loc = map.get(dimLoc).toString();
                    String sex = map.get(dimSex).toString();
                    double userCnt = Math.round((double) map.get(target)*100)/100.0;
                    
                    if(tmpLoc == null) {
                    	tmpLoc = map.get(dimLoc).toString();
                    	tmpSex = map.get(dimSex).toString();
                    	Map<String, Object> locMap = null;
                    	for (EventHolder event : events) {
                        	locMap = event.getEvent();
                        	if(loc.equals(locMap.get("ldong_cd").toString())) {
                        		sumList.put("sidoNm", locMap.get("sido_nm") != null ?  locMap.get("sido_nm").toString() : "");
                        		sumList.put("sggNm", locMap.get("sgg_nm") != null ?  locMap.get("sgg_nm").toString() : "");
                        		sumList.put("dongNm", locMap.get("dong_nm") != null ?  locMap.get("dong_nm").toString() : "");
                        		sumList.put("ldongCd", loc);
                        		break;
                        	}
                    	}
                    	if(sex.equals("1")) {
                    		sumList.put("manCnt", userCnt);
                    	} else {
                    		sumList.put("womanCnt", userCnt);
                    	}
                    	continue;
                    }
                    
                    if(tmpLoc.equals(loc)) {
                    	if(sex.equals("1")) {
                    		sumList.put("manCnt", userCnt);
                    		sumList.put("sumUserCnt", Math.round((userCnt + (double) sumList.get("womanCnt"))*100)/100.0);
                    	} else {
                    		sumList.put("womanCnt", userCnt);
                    		sumList.put("sumUserCnt", Math.round((userCnt + (double) sumList.get("manCnt"))*100)/100.0);
                    	}        
                    	result.add(sumList);
                    	sumList = new HashMap<>();
                    	tmpLoc = null;
                    } else {
                    	if(tmpSex.equals("1")) {
                    		sumList.put("sumUserCnt", (double) sumList.get("manCnt"));
                    		sumList.put("womanCnt", 0);
                    	} else {
                    		sumList.put("sumUserCnt", (double) sumList.get("womanCnt"));
                    		sumList.put("manCnt", 0);
                    	}
                    	
                    	result.add(sumList);
                    	sumList = new HashMap<>();
                    	
                    	tmpLoc = map.get(dimLoc).toString();
                    	tmpSex = map.get(dimSex).toString();
                    	Map<String, Object> locMap = null;
                    	for (EventHolder event : events) {
                        	locMap = event.getEvent();
                        	if(loc.equals(locMap.get("ldong_cd").toString())) {
                        		sumList.put("sidoNm", locMap.get("sido_nm") != null ?  locMap.get("sido_nm").toString() : "");
                        		sumList.put("sggNm", locMap.get("sgg_nm") != null ?  locMap.get("sgg_nm").toString() : "");
                        		sumList.put("dongNm", locMap.get("dong_nm") != null ?  locMap.get("dong_nm").toString() : "");
                        		sumList.put("ldongCd", loc);
                        		break;
                        	}
                    	}
                    	if(sex.equals("1")) {
                    		sumList.put("manCnt", userCnt);
                    	} else {
                    		sumList.put("womanCnt", userCnt);
                    	}                	
                    }
                }
                return result;
            }
                        
        }catch (QueryInterruptedException e){
            e.printStackTrace();
            log.error(e.getErrorCode());
            log.error(e.getMessage());

        }                   
        
        return result;
    }       
    
    public List<Map<String, Object>> getAddrCenterPoint(@Nullable JSONObject jsonObject) {
    	
    	String addrDs = (String)jsonObject.get("addrDs");
    	String addrNm = (String)jsonObject.get("addrNm");
    	String addrCode = (String)jsonObject.get("addrCode");
    	
        DimFilter ret = null;
        List<DimFilter> fields = new ArrayList<>();
        
        List<Map<String, Object>> result = new ArrayList<>();
        Map<String, Object> sumList = new HashMap<>();
        
        //행정동 데이터 쿼리
		fields.add(new RegexDimFilter(addrNm + "_cd", "^" + addrCode, null));
		ret = fields.get(0);
		
    	List<String> dimensionList = new ArrayList<>();    	
        dimensionList.add(addrNm + "_ctr_lat");
        dimensionList.add(addrNm + "_ctr_lng");
    	
        SelectQuery locQuery = null;
        
        locQuery = new Druids.SelectQueryBuilder()
        		.dataSource(addrDs)
        		.pagingSpec(new PagingSpec(Collections.emptyMap(), 100000))
        		.dimensionSpecs(DefaultDimensionSpec.toSpec(dimensionList))
        		.filters(
        				ret
        				)
        		.intervals(LpmDruidMetaService.getLocDefaultIntervar(addrDs))
        		.granularity(Granularities.ALL).build();

        try {
        	DruidResult<Result<SelectResultValue>> locResult = LpmDruidMetaService.druidRunSelect(locQuery);
        	 
            Yielder resultYielder = locResult.getSequence().toYielder(new ArrayList(), new YieldingAccumulator<ArrayList, Result<SelectResultValue>>() {
                @Override
                public ArrayList accumulate(ArrayList accumulated, Result<SelectResultValue> selectResultValueResult) {
                    accumulated.add(selectResultValueResult);
                    return accumulated;
                }
            });

            List<Result<SelectResultValue>> resultList = (List)resultYielder.get();
            if( resultList.size() == 0 ){
                return null;
            }
        	
            final Result<SelectResultValue> firstResult = resultList.get(resultList.size()-1);
            final SelectResultValue selectResult = firstResult.getValue();
            final List<EventHolder> events = selectResult.getEvents();      
            
            Map<String, Object> locMap = null;
        	for (EventHolder event : events) {
        		locMap = event.getEvent();
        		sumList.put("lat", locMap.get(addrNm + "_ctr_lat") != null ?  locMap.get(addrNm + "_ctr_lat").toString() : "");
        		sumList.put("lng", locMap.get(addrNm + "_ctr_lng") != null ?  locMap.get(addrNm + "_ctr_lng").toString() : "");
        		result.add(sumList);
        		break;
        	}
                        
        }catch (QueryInterruptedException e){
            e.printStackTrace();
            log.error(e.getErrorCode());
            log.error(e.getMessage());

        }                   
        
        return result;
    }
    
    public List<Population> getXdrRowData(@Nullable JSONObject jsonObject) {
    	
    	
    	String dsLpm = (String) jsonObject.get("dsNm");
    	//String lower = (String) jsonObject.get("lower");
    	//String upper = (String) jsonObject.get("upper");
    	
    	//Bound bbox = null;
    	//float[] minCoords = {37.5567f,126.9757f};
    	//float[] maxCoords = {37.5676f,126.9892f};
    	//bbox = new RectangularBound(minCoords, maxCoords);
    	
    	chartInterval = null;
    	
    	final String NOTTYPE = "not";
        JSONArray filters = LpmUtil.getJsonArray(jsonObject);
        String measureNm = "";
        DimFilter dimFilter = null;
        if(filters == null) {
            measureNm = all;
        } else {
            measureNm=target;
            dimFilter = fetchDimFilter(filters, null, false);
        }
        
        
        
        //행정동 데이터 쿼리
        DimFilter ret = null;
        List<DimFilter> fields = new ArrayList<>();
        JSONArray filterArr = (JSONArray) filters;
        ObjectMapper mapper = new ObjectMapper();
        for(Object filter : filterArr) {
            try {
            	CommDto.FAVOR favorite = mapper.readValue(filter.toString(), CommDto.FAVOR.class);
            	String type = favorite.getType();
            	
            	if(favorite.getScrnClNm().equals("address")) {
            		favorite.getFltrVal().get(0).getCode();
            		List values = null;
            		for(int i=0; i<favorite.getFltrVal().size(); i++) {
            			values = (List) favorite.getFltrVal().get(i).getCode();
            			
                        if(NOTTYPE.equals(type)) {
                   			if(values.size() == 1) {
                				fields.add(new NotDimFilter(new RegexDimFilter(dimLoc, "^" + values.get(0).toString(), null)));
                			}else if(values.size() == 2) {
                				fields.add(new NotDimFilter(new RegexDimFilter(dimLoc, "^" + values.get(0).toString() + values.get(1).toString(), null)));
                			}else if(values.size() == 3) {
                				fields.add(new NotDimFilter(new RegexDimFilter(dimLoc, "^" 
                						+ values.get(0).toString() + values.get(1).toString() + values.get(2).toString(), null)));
                			}
                        } else {
                   			if(values.size() == 1) {
                				fields.add(new RegexDimFilter(dimLoc, "^" + values.get(0).toString(), null));
                			}else if(values.size() == 2) {
                				fields.add(new RegexDimFilter(dimLoc, "^" + values.get(0).toString() + values.get(1).toString(), null));
                			}else if(values.size() == 3) {
                				fields.add(new RegexDimFilter(dimLoc, "^" 
                						+ values.get(0).toString() + values.get(1).toString() + values.get(2).toString(), null));
                			}
                        }
            		}
            		
                    if(fields.size() == 1 ) {
                        ret = fields.get(0);
                    } else if(fields.size() > 1) {
                        if(NOTTYPE.equals(type)) {
                            ret = new AndDimFilter(fields);
                        } else {
                            ret = new OrDimFilter(fields);
                        }
                    } else {
                        ret = null;
                    }
            	}
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }            
        
    	List<String> locDimensionList = new ArrayList<>();    	
    	
    	locDimensionList.add("sido_nm");
    	locDimensionList.add("sgg_nm");
    	locDimensionList.add("dong_nm");
    	locDimensionList.add("sido_cd");
    	locDimensionList.add("sgg_cd");
    	locDimensionList.add("dong_cd");
    	locDimensionList.add("ldong_cd");
    	
        SelectQuery locQuery = null;
        
        if(fields.size() > 0) {
        	locQuery = new Druids.SelectQueryBuilder()
        			.dataSource(dsLpm2)
        			.pagingSpec(new PagingSpec(Collections.emptyMap(), 100000))
        			.dimensionSpecs(DefaultDimensionSpec.toSpec(locDimensionList))
        			.filters(
        					ret
        					)
        			.intervals(LpmDruidMetaService.getLocDefaultIntervar(dsLpm2))
        			.granularity(Granularities.ALL).build();
        } else {
        	locQuery = new Druids.SelectQueryBuilder()
        			.dataSource(dsLpm2)
        			.pagingSpec(new PagingSpec(Collections.emptyMap(), 100000))
        			.dimensionSpecs(DefaultDimensionSpec.toSpec(locDimensionList))
        			.intervals(LpmDruidMetaService.getLocDefaultIntervar(dsLpm2))
        			.granularity(Granularities.ALL).build();           	
        }
        
        List<EventHolder> locEvents = null;
        try {
        	DruidResult<Result<SelectResultValue>> locResult = LpmDruidMetaService.druidRunSelect(locQuery);
        	 
            Yielder resultYielder = locResult.getSequence().toYielder(new ArrayList(), new YieldingAccumulator<ArrayList, Result<SelectResultValue>>() {
                @Override
                public ArrayList accumulate(ArrayList accumulated, Result<SelectResultValue> selectResultValueResult) {
                    accumulated.add(selectResultValueResult);
                    return accumulated;
                }
            });

            List<Result<SelectResultValue>> resultList = (List)resultYielder.get();
            if( resultList.size() == 0 ){
                return null;
            }
        	
            final Result<SelectResultValue> firstResult = resultList.get(resultList.size()-1);
            final SelectResultValue selectResult = firstResult.getValue();
            locEvents = selectResult.getEvents();            
        } catch (QueryInterruptedException e){
            e.printStackTrace();
            log.error(e.getErrorCode());
            log.error(e.getMessage());
        }  
        
        
        
    	List<String> dimensionList = new ArrayList<>();    	
    	
        dimensionList.add(dimAge);
        //dimensionList.add("geo");
        dimensionList.add(dimLoc);
        dimensionList.add(dimSex); 
        dimensionList.add(dimUserCnt); 
        dimensionList.add(dimEnbId); 
        dimensionList.add(dimCellId); 
        dimensionList.add(dimTotCeiVal); 
    	
        //pcell 데이터 쿼리
        SelectQuery query = new Druids.SelectQueryBuilder()
                .dataSource(dsLpm)
                .pagingSpec(new PagingSpec(Collections.emptyMap(), 10000))
                .dimensionSpecs(DefaultDimensionSpec.toSpec(dimensionList))
                .filters(
                			dimFilter
                		)
                /*
                .filters(DimFilters.and(
                		dimFilter
                		//new RegexDimFilter(dimAge, "[0-9][0-9][0-9]", null)
                		//new BoundDimFilter(dimGeo, "["+ lower +"]", "["+ upper +"]", false, false, null, null, null),
                		//new SpatialDimFilter(dimGeo, bbox)
                		)
                )
                */
                //.intervals(LpmDruidMetaService.getDefaultIntervar(dsLpm))
                .intervals(gridInterval)
                .granularity(Granularities.ALL).build();
        
        List<LpmChartDto.Population> populList = new ArrayList<>();
        try {
        	
            DruidResult<Result<SelectResultValue>> result = LpmDruidMetaService.druidRunSelect(query);
        	
            Yielder resultYielder = result.getSequence().toYielder(new ArrayList(), new YieldingAccumulator<ArrayList, Result<SelectResultValue>>() {
                @Override
                public ArrayList accumulate(ArrayList accumulated, Result<SelectResultValue> selectResultValueResult) {
                    accumulated.add(selectResultValueResult);
                    return accumulated;
                }
            });

            
            List<Result<SelectResultValue>> resultList = (List)resultYielder.get();
            if( resultList.size() == 0 ){
                return null;
            }
            
            final Result<SelectResultValue> firstResult = resultList.get(resultList.size()-1);
            final SelectResultValue selectResult = firstResult.getValue();
            final List<EventHolder> events = selectResult.getEvents();   
            
            for (EventHolder event : events) {
            	Map<String, Object> map = event.getEvent();
            	LpmChartDto.Population popul = new LpmChartDto.Population();
            	popul.setAge( map.get(dimAge) != null ?  map.get(dimAge).toString() : "") ;
            	popul.setGeo( map.get(dimGeo) != null ? map.get(dimGeo).toString(): "");
            	popul.setLdongCd( map.get(dimLoc) != null ? map.get(dimLoc).toString() : "");
            	//popul.setHh( map.get(dimHh) != null ? map.get(dimHh).toString() : "");     
            	popul.setSex( map.get(dimSex) != null ? map.get(dimSex).toString() : "");
            	popul.setEnbId( map.get(dimEnbId) != null ? map.get(dimEnbId).toString() : "");
            	popul.setCellId( map.get(dimCellId) != null ? map.get(dimCellId).toString() : "");
            	popul.setTotCeiVal( map.get(dimTotCeiVal) != null ? map.get(dimTotCeiVal).toString() : "");
            	
            	if ( map.get(dimUserCnt) != null ) {
            		double userCnt = Double.parseDouble(map.get(dimUserCnt).toString());
            		popul.setUserCnt( Double.parseDouble(String.format( "%.2f" , ( userCnt == 0 ) ? 0.0 : userCnt ) ) ); 
            		
            	} else {
            		popul.setUserCnt( 0.0 );
            	}
            	
            	for (EventHolder locEvent : locEvents) {
            		Map<String, Object> locMap = locEvent.getEvent();
            		Address address = new Address();
                	if(popul.getLdongCd().equals(locMap.get(dimLoc).toString())) {
                		address.setSidoNm(locMap.get("sido_nm") != null ?  locMap.get("sido_nm").toString() : "");
                		address.setSggNm(locMap.get("sgg_nm") != null ?  locMap.get("sgg_nm").toString() : "");
                		address.setDongNm(locMap.get("dong_nm") != null ?  locMap.get("dong_nm").toString() : "");
                		popul.setAddress(address);
                		break;
                	}
            	}
            	
            	populList.add(popul);
            }
        }catch (QueryInterruptedException e){
            e.printStackTrace();
            log.error(e.getErrorCode());
            log.error(e.getMessage());

        }
        
    	return populList;
    }
    
    public List<Population> getXdrGroupByData(@Nullable JSONObject jsonObject) {
    	
    	
    	String dsLpm = (String) jsonObject.get("dsNm");
    	
    	chartInterval = null;
    	
    	final String NOTTYPE = "not";
        JSONArray filters = LpmUtil.getJsonArray(jsonObject);
        String measureNm = "";
        DimFilter dimFilter = null;
        if(filters == null) {
            measureNm = all;
        } else {
            measureNm=target;
            dimFilter = fetchDimFilter(filters, null, false);
        }
        
        
        
        //행정동 데이터 쿼리
        DimFilter ret = null;
        List<DimFilter> fields = new ArrayList<>();
        JSONArray filterArr = (JSONArray) filters;
        ObjectMapper mapper = new ObjectMapper();
        for(Object filter : filterArr) {
            try {
            	CommDto.FAVOR favorite = mapper.readValue(filter.toString(), CommDto.FAVOR.class);
            	String type = favorite.getType();
            	
            	if(favorite.getScrnClNm().equals("address")) {
            		favorite.getFltrVal().get(0).getCode();
            		List values = null;
            		for(int i=0; i<favorite.getFltrVal().size(); i++) {
            			values = (List) favorite.getFltrVal().get(i).getCode();
            			
                        if(NOTTYPE.equals(type)) {
                   			if(values.size() == 1) {
                				fields.add(new NotDimFilter(new RegexDimFilter(dimLoc, "^" + values.get(0).toString(), null)));
                			}else if(values.size() == 2) {
                				fields.add(new NotDimFilter(new RegexDimFilter(dimLoc, "^" + values.get(0).toString() + values.get(1).toString(), null)));
                			}else if(values.size() == 3) {
                				fields.add(new NotDimFilter(new RegexDimFilter(dimLoc, "^" 
                						+ values.get(0).toString() + values.get(1).toString() + values.get(2).toString(), null)));
                			}
                        } else {
                   			if(values.size() == 1) {
                				fields.add(new RegexDimFilter(dimLoc, "^" + values.get(0).toString(), null));
                			}else if(values.size() == 2) {
                				fields.add(new RegexDimFilter(dimLoc, "^" + values.get(0).toString() + values.get(1).toString(), null));
                			}else if(values.size() == 3) {
                				fields.add(new RegexDimFilter(dimLoc, "^" 
                						+ values.get(0).toString() + values.get(1).toString() + values.get(2).toString(), null));
                			}
                        }
            		}
            		
                    if(fields.size() == 1 ) {
                        ret = fields.get(0);
                    } else if(fields.size() > 1) {
                        if(NOTTYPE.equals(type)) {
                            ret = new AndDimFilter(fields);
                        } else {
                            ret = new OrDimFilter(fields);
                        }
                    } else {
                        ret = null;
                    }
            	}
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }            
        
    	List<String> locDimensionList = new ArrayList<>();    	
    	
    	locDimensionList.add("sido_nm");
    	locDimensionList.add("sgg_nm");
    	locDimensionList.add("dong_nm");
    	locDimensionList.add("sido_cd");
    	locDimensionList.add("sgg_cd");
    	locDimensionList.add("dong_cd");
    	locDimensionList.add("ldong_cd");
    	
        SelectQuery locQuery = null;
        
        if(fields.size() > 0) {
        	locQuery = new Druids.SelectQueryBuilder()
        			.dataSource(dsLpm2)
        			.pagingSpec(new PagingSpec(Collections.emptyMap(), 100000))
        			.dimensionSpecs(DefaultDimensionSpec.toSpec(locDimensionList))
        			.filters(
        					ret
        					)
        			.intervals(LpmDruidMetaService.getLocDefaultIntervar(dsLpm2))
        			.granularity(Granularities.ALL).build();
        } else {
        	locQuery = new Druids.SelectQueryBuilder()
        			.dataSource(dsLpm2)
        			.pagingSpec(new PagingSpec(Collections.emptyMap(), 100000))
        			.dimensionSpecs(DefaultDimensionSpec.toSpec(locDimensionList))
        			.intervals(LpmDruidMetaService.getLocDefaultIntervar(dsLpm2))
        			.granularity(Granularities.ALL).build();           	
        }
        
        List<EventHolder> locEvents = null;
        try {
        	DruidResult<Result<SelectResultValue>> locResult = LpmDruidMetaService.druidRunSelect(locQuery);
        	 
            Yielder resultYielder = locResult.getSequence().toYielder(new ArrayList(), new YieldingAccumulator<ArrayList, Result<SelectResultValue>>() {
                @Override
                public ArrayList accumulate(ArrayList accumulated, Result<SelectResultValue> selectResultValueResult) {
                    accumulated.add(selectResultValueResult);
                    return accumulated;
                }
            });

            List<Result<SelectResultValue>> resultList = (List)resultYielder.get();
            if( resultList.size() == 0 ){
                return null;
            }
        	
            final Result<SelectResultValue> firstResult = resultList.get(resultList.size()-1);
            final SelectResultValue selectResult = firstResult.getValue();
            locEvents = selectResult.getEvents();            
        } catch (QueryInterruptedException e){
            e.printStackTrace();
            log.error(e.getErrorCode());
            log.error(e.getMessage());
        }  
        

        //pcell 데이터 쿼리
        GroupByQuery.Builder builder = new GroupByQuery.Builder()
                .setDataSource(dsLpm)
                .setInterval(gridInterval)
                .setGranularity(Granularities.ALL)
                .addDimension(dimLoc)
                .addDimension(dimSex)
                .addDimension(dimAge)
                .addDimension(dimEnbId)
                .addDimension(dimCellId)
                .addDimension(dimTotCeiVal)
                .addAggregator(
                		new DoubleSumAggregatorFactory(measureNm, dimUserCnt)
                        //new CountAggregatorFactory(measureNm)
                )
                .setLimit(10000);

        if(dimFilter != null) {
            builder.setDimFilter(dimFilter);
        }

        GroupByQuery query = builder.build();

        List<Row> results = LpmDruidMetaService.druidRun(query);
        List<Map<String, Object>> result = new ArrayList<>();
        Map<String, Object> sumList = new HashMap<>();
        
        
        
        
        List<LpmChartDto.Population> populList = new ArrayList<>();
        try {
        	if (results != null) {
                for(Row row : results) {
                    MapBasedRow mapBasedRow = (MapBasedRow) row;
                    Map<String, Object> map = mapBasedRow.getEvent(); 
            
	            	LpmChartDto.Population popul = new LpmChartDto.Population();
	            	popul.setAge( map.get(dimAge) != null ?  map.get(dimAge).toString() : "") ;
	            	popul.setGeo( map.get(dimGeo) != null ? map.get(dimGeo).toString(): "");
	            	popul.setLdongCd( map.get(dimLoc) != null ? map.get(dimLoc).toString() : "");
	            	//popul.setHh( map.get(dimHh) != null ? map.get(dimHh).toString() : "");     
	            	popul.setSex( map.get(dimSex) != null ? map.get(dimSex).toString() : "");
	            	popul.setEnbId( map.get(dimEnbId) != null ? map.get(dimEnbId).toString() : "");
	            	popul.setCellId( map.get(dimCellId) != null ? map.get(dimCellId).toString() : "");
	            	popul.setTotCeiVal( map.get(dimTotCeiVal) != null ? map.get(dimTotCeiVal).toString() : "");
	            	
	            	popul.setUserCnt(Math.round((double) map.get(target)*100)/100.0);
	            	
	            	/*
	            	if ( map.get(dimUserCnt) != null ) {
	            		double userCnt = Double.parseDouble(map.get(dimUserCnt).toString());
	            		popul.setUserCnt( Double.parseDouble(String.format( "%.2f" , ( userCnt == 0 ) ? 0.0 : userCnt ) ) ); 
	            		
	            	} else {
	            		popul.setUserCnt( 0.0 );
	            	}
	            	*/
	            	
	            	for (EventHolder locEvent : locEvents) {
	            		Map<String, Object> locMap = locEvent.getEvent();
	            		Address address = new Address();
	                	if(popul.getLdongCd().equals(locMap.get(dimLoc).toString())) {
	                		address.setSidoNm(locMap.get("sido_nm") != null ?  locMap.get("sido_nm").toString() : "");
	                		address.setSggNm(locMap.get("sgg_nm") != null ?  locMap.get("sgg_nm").toString() : "");
	                		address.setDongNm(locMap.get("dong_nm") != null ?  locMap.get("dong_nm").toString() : "");
	                		popul.setAddress(address);
	                		break;
	                	}
	            	}
	            	
	            	populList.add(popul);
	            }
        	}
        }catch (QueryInterruptedException e){
            e.printStackTrace();
            log.error(e.getErrorCode());
            log.error(e.getMessage());

        }
        
    	return populList;
    }    
}
