import { Component, OnInit, ElementRef, Injector } from '@angular/core';
import { AbstractComponent } from '../../../common/component/abstract.component';
import { IpmService } from '../../service/ipm.service';
import { IcpmService } from '../../service/icpm/icpm.service';
import { ChartService } from '../../common/service/chart/chart.service';
import { BmrkService } from '../../common/service/bmrk/bmrk.service';
import { AddressService } from '../../../ipm/common/service/right-kind/address/address.service';
import { TranslateService } from '@ngx-translate/core';
import { CommonConstant } from '../../common/constant/common-constant';
import { Utils } from '../../common/util/utils';
import { Alert } from '../../../common/util/alert.util';
import * as moment from 'moment';
import * as _ from 'lodash';

declare const $: any;
declare const echarts: any;
@Component({
  selector: 'icpm',
  templateUrl: './icpm.component.html',
  styles: []
})
export class IcpmComponent extends AbstractComponent implements OnInit {

  // 메뉴 링크
  public url: string = 'icpm';

  // Calendar Date 데이터
  public icpmDate: string = '';

  // Calendar Max Date
  public maxDate: any;

  // Calendar min Date
  public minDate: any;

  // 메뉴 상세 데이터
  public menuDetailData: any;

  // 메뉴 데이터
  public fltrData: Array<any> = [];

  // 메뉴 검색 덤프 데이터
  public fltrDataTmp: Array<any> = [];

  // 메뉴 카테고리 데이터
  public fltrCategoryData: Array<any> = [];

  // 카테고리 표출 여부
  public isCategory: boolean = false;

  // 전체 펼치기 여부
  public isHands: boolean = false;

  // 우측 사이즈 변환 여부
  public isRightFlexible: boolean = false;

  // 좌측 사이즈 변환 여부
  public isLeftFlexible: boolean = false;

  // Excel 다운로드 진행 여부
  public isDownloadExcel: boolean = false;

  // 시도 데이터
  public sidoData: Array<any> = [];

  // 시군구 데이터
  public sggData: Array<any> = [];

  // 선택 주소 코드
  public currentAddrCode: Array<any> = [];

  // 차트 번호
  public chartNum = {
    count: 'c0',
    region: 'c1',
    age: 'c2',
    vendor: 'c3',
    phone: 'c4',
    team: 'c5',
    traffic: 'c6',
    cei: 'c7'
  };

  // 차트 설정 데이터
  public chartSettingList: Array<any> = [
    {code: this.chartNum.region, name: '지역별 분포', isOn: true, isPrevOn: true, isLoading: false},
    {code: this.chartNum.age, name: '연령대별 분포', isOn: true, isPrevOn: true, isLoading: false},
    {code: this.chartNum.vendor, name: '제조사별 분포', isOn: true, isPrevOn: true, isLoading: false},
    {code: this.chartNum.phone, name: '단말 기종별', isOn: true, isPrevOn: true, isLoading: false},
    {code: this.chartNum.team, name: '본부별 분포', isOn: true, isPrevOn: true, isLoading: false},
    {code: this.chartNum.traffic, name: '주간 Traffic 사용량(MB)', isOn: true, isPrevOn: true, isLoading: false},
    // {code: this.chartNum.cei, name: 'CEI 구간별 분포', isOn: true, isPrevOn: true, isLoading: false}
  ];

  // 차트 설정 변경 전 데이터
  public chartSettingTempList: Array<any> = [];

  // 전체/타겟군 카운트
  public allCnt: number = 0;
  public targetCnt: number = 0;
  public gauge: string = '100%';
  public persent: string = '100%';

  // 지역별 차트
  public regionChart: any;

  // 연령별 차트
  public ageChart: any;

  // 연령별 토글
  public ageToggle: string = 'bar';

  // 제조사별 차트
  public vendorChart: any;

  // 제조사별 토글
  public vendorToggle: string = 'bar';

  // 단말기 애칭별 차트
  public phoneChart: any;

  // 본부별 차트
  public teamChart: any;

  // 본부별 토글
  public teamToggle: string = 'bar';

  // Traffic 사용량별 차트
  public trafficChart: any;

  // // CEI 구간별 차트
  // public ceiChart: any;

  // // CEI 구간별 토글
  // public ceiToggle: string = 'bar';

  // 차트 설정 팝업 show/hide 여부
  public isChartSettingPopup: boolean = false;

  // 즐겨찾기 데이터
  public bmrkData: Array<any> = [];

  // 선택 즐겨찾기 데이터
  public currentBmrkData: any;

  // 사용자 즐겨찾기 선택 필터 데이터
  public filterData: Array<any> = [];

  // 필터 즐겨찾기 수정 덤프 데이터
  public filterDataTmp: Array<any> = [];

  // 선택 즐겨찾기 타이틀
  public bmrkTitle: string = '';

  // 즐겨찾기 버튼 활성화 여부
  public isBmrkSave: boolean = false;

  // 즐겨찾기 추가/수정 이름
  public bmrkNm: string = '';

  // icpm 정보 표출 팝업 show/hide 여부
  public isInfoPopup: boolean = false;

  // 즐겨찾기 추가 팝업 show/hide 여부
  public isAddBmrkPopup: boolean = false;

  // 즐겨찾기 수정 팝업 show/hide 여부
  public isModBmrkPopup: boolean = false;

  // 팝업시 검은배경 여부
  public isBlackOverlay: boolean = false;

  // bar 공통 옵션
  public barOption = {
    name: 'bar',
    color: ['#9c96f4', '#e7bdf3', '#9c96f4', '#e7bdf3'],
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'cross'
      }
    },
    legend: [{
      data: ['타겟군', '전체', '타겟군(비율)', '전체(비율)']
    }],
    grid: {
      left: '70px',
      right: '50px'
    },
    xAxis: [
      {
        type: 'category',
        axisTick: {
          alignWithLabel: true
        },
        axisLabel: {
          rotate: 45
        },
        data: []
      }
    ],
    yAxis: [
      {
        type: 'value',
        position: 'left'
      },
      {
        name: '%',
        type: 'value',
        positon: 'right'
      }
    ],
    dataZoom: [
      {
        type: 'slider',
        xAxisIndex: 0,
        filterMode: 'empty'
      }
    ],
    series: [
      {
        type: 'bar',
        name: '타겟군',
        yAxisIndex: 0,
        data: []
      },
      {
        type: 'bar',
        name: '전체',
        yAxisIndex: 0,
        data: []
      },
      {
        type: 'line',
        name: '타겟군(비율)',
        yAxisIndex: 1,
        data: []
      },
      {
        type: 'line',
        name: '전체(비율)',
        yAxisIndex: 1,
        data: []
      }
    ]
  };

  // pie 공통 옵션
  public pieOption = {
    name: 'pie',
    tooltip: {
      trigger: 'item',
      formatter: '[{a}] {b} : {c}({d}%)'
    },
    title: [
      {
        subtext: '타겟군',
        x: '28%'
      },
      {
        subtext: '전체',
        x: '68%'
      },
    ],
    legend: [{
      x: 'left',
      y: 'center',
      orient: 'vertical',
      data: []
    }],
    calculable: true,
    series: [
      {
        type: 'pie',
        name: '타겟군',
        roseType: 'area',
        radius: [20, 105],
        center: ['30%', '52%'],
        label: {
          normal: {
            show: true
          },
          emphasis: {
            show: false
          }
        },
        data: []
      },
      {
        type: 'pie',
        name: '전체',
        roseType: 'area',
        radius: [20, 105],
        center: ['70%', '52%'],
        label: {
          normal: {
            show: true
          },
          emphasis: {
            show: false
          }
        },
        data: []
      }
    ]
  };

  protected jQuery = $;

  // 생성자
  constructor(
    protected elementRef: ElementRef,
    protected injector: Injector,
    private ipmService: IpmService,
    private icpmService: IcpmService,
    private chartService: ChartService,
    private bmrkService: BmrkService,
    private addressService: AddressService
  ) {
    super(elementRef, injector);
  }

  ngOnInit() {

    // 태깅
    // this.tagging(this.TaggingType.DETAIL, this.TaggingAction.VIEW, 'icpm', 'IDCube Profile');

    // ipm 화면 헤더 데이터 셋팅
    this.ipmService.setHeaderEvent({
      url : this.url,
      title : 'iCPM',
      subTitle : 'Customer'
    });

    // 초기 메뉴 데이터 조회
    this.icpmService.getIcpmFltrBasList()
    .then(result => {
      if (result.code === CommonConstant.CODE.RESULT_CODE.SUCCESS) {
        this.fltrData = result.data;
        this.fltrData[0].folder = true;
        this.fltrDataTmp = _.cloneDeep(this.fltrData);
        this.checkLeafCnt(this.fltrDataTmp);

        // 카테고리 데이터 생성
        // 5개씩 담는 데이터
        let checkCntData = [];
        // 추가 데이터 카운트
        let cnt = 0;

        for (let i = 0; i < this.fltrData.length; i++) {
          // 기존 메뉴 데이터
          let fltrData = _.cloneDeep(this.fltrData[i]);
          // 사용중인 메뉴 데이터를 담기 위한 데이터
          let addFltrData = _.cloneDeep(fltrData);
          // 메뉴리스트 초기화
          addFltrData.fltrList = [];

          // 메뉴 사용여부 판단
          if (fltrData.useYn == 'Y') {
            for (let j = 0; j < fltrData.fltrList.length; j++) {
              let fltr1DepsData = fltrData.fltrList[j];
              // 2deps가 존재하지 않고 사용중 인 경우
              if (fltr1DepsData.leafYn == 'Y' && fltr1DepsData.useYn == 'Y') {
                // 해당 메뉴 데이터 추가
                addFltrData.fltrList.push(fltr1DepsData);

              // 2deps가 존재 할 경우
              } else if (fltr1DepsData.leafYn == 'N') {
                let fltr2DepsData = _.cloneDeep(fltr1DepsData.fltrList);
                // 1deps 매뉴리스트 초기화
                fltr1DepsData.fltrList = [];
                for (let k = 0; k < fltr2DepsData.length; k++) {
                  if (fltr2DepsData[k].useYn == 'Y') {
                    fltr1DepsData.fltrList.push(fltr2DepsData[k]);
                  }
                }
                // 사용중인 2deps 추가
                if (fltr1DepsData.fltrList.length) {
                  addFltrData.fltrList.push(fltr1DepsData);
                }
              }
            }

            // 선택 데이터 담기
            checkCntData.push(addFltrData);
            cnt++;
          }

          // 메뉴가 5개가 되거나 마지막 남은 갯수일 경우 카테고리 데이터 추가
          if ((cnt != 0) && (cnt % 5 == 0)) {
            this.fltrCategoryData.push(checkCntData);
            checkCntData = [];
          } else if (i == this.fltrData.length-1) {
            this.fltrCategoryData.push(checkCntData);
          }
        }
      }
    });

    // 초기 즐겨찾기 데이터 조회
    this.bmrkService.getUserBmrkDtlList(this.url)
    .then(result => {
      if (result.code === CommonConstant.CODE.RESULT_CODE.SUCCESS) {
        this.bmrkData = result.data;
      }
    });

    // 차트 설정 조회
    this.chartService.getUserEstDtl(this.url)
    .then(result => {
      if (result.code === CommonConstant.CODE.RESULT_CODE.SUCCESS) {

        // hide 할 차트 정보가 있을 경우
        if (result.data.estVal) {

          // hide할 차트 코드 정보
          let estVal = result.data.estVal;
          for (let i = 0; i < this.chartSettingList.length; i++) {
            if (estVal.includes(this.chartSettingList[i].code)) {
              this.chartSettingList[i].isOn = false;
              this.chartSettingList[i].isPrevOn = false;
            }
          }
        }

        // 지역별 차트 초기화
        this.regionChart = echarts.init(document.getElementById('regionChart'));
        this.regionChart.setOption(this.barOption, true);

        // 연령별 차트 초기화
        this.ageChart = echarts.init(document.getElementById('ageChart'));
        this.ageChart.setOption(this.barOption, true);

        // 제조사별 차트 초기화
        this.vendorChart = echarts.init(document.getElementById('vendorChart'));
        this.vendorChart.setOption(this.barOption, true);

        // 단말기 애칭별 차트 초기화
        this.phoneChart = echarts.init(document.getElementById('phoneChart'));
        this.phoneChart.setOption(this.barOption, true);

        // 본부별 차트 초기화
        this.teamChart = echarts.init(document.getElementById('teamChart'));
        this.teamChart.setOption(this.barOption, true);

        // Traffic 사용량별 차트 초기화
        this.trafficChart = echarts.init(document.getElementById('trafficChart'));
        this.trafficChart.setOption(this.barOption, true);

        // cei별 차트 초기화

        // 필터 데이터 조회
        this.getFilterData(false);
      }
    });

    // 시도 데이터 조회
    this.addressService.getAddress()
    .then(result => {
      if (result.code === CommonConstant.CODE.RESULT_CODE.SUCCESS) {
        this.sidoData = result.data;
      }
    });
  }

  /**
   * 필터 데이터 조회
   *
   * @param chartSetting(차트 설정 호출 여부)
   */
  public getFilterData(chartSetting: boolean): void {

    this.icpmService.getIcpmCharts({
      chartNum: this.chartNum.count,
      fltrDatVal: this.filterDataTmp,
      occrDt: this.icpmDate
    })
    .then(result => {
      if (result.code === CommonConstant.CODE.RESULT_CODE.SUCCESS) {
        this.targetCnt = result.data.target[0].value;
        this.allCnt = result.data.all[0].value;
        if (this.targetCnt != 0 && this.allCnt != 0) {
          this.gauge = Math.floor(this.targetCnt * 100 / this.allCnt) + '%';
          this.persent = Math.round((this.targetCnt * 100 / this.allCnt) * 100) / 100 + '%';
        } else {
          this.gauge = '0%';
          this.persent = '0%';
        }

        // 차트 조회
        if (chartSetting) {
          if (this.chartSettingList[0].isOn && !this.chartSettingList[0].isPrevOn) {
            this.chartSettingList[0].isPrevOn = true;
            this.getRegionChartData();
          }
          if (this.chartSettingList[1].isOn && !this.chartSettingList[1].isPrevOn) {
            this.chartSettingList[1].isPrevOn = true;
            this.getAgeChartData();
          }
          if (this.chartSettingList[2].isOn && !this.chartSettingList[2].isPrevOn) {
            this.chartSettingList[2].isPrevOn = true;
            this.getVendorChartData();
          }
          if (this.chartSettingList[3].isOn && !this.chartSettingList[3].isPrevOn) {
            this.chartSettingList[3].isPrevOn = true;
            this.getPhoneChartData();
          }
          if (this.chartSettingList[4].isOn && !this.chartSettingList[4].isPrevOn) {
            this.chartSettingList[4].isPrevOn = true;
            this.getTeamChartData();
          }
          if (this.chartSettingList[5].isOn && !this.chartSettingList[5].isPrevOn) {
            this.chartSettingList[5].isPrevOn = true;
            this.getTrafficChartData();
          }
        } else {

          // ICPM 날짜 셋팅 체크
          if (!this.icpmDate) {
            // 날짜 셋팅
            this.icpmDate = result.data.occrDt[1];

            // 달력 셋팅
            let date = this.parseStringToDate(this.icpmDate, 'YYYYMMDD');
            this.minDate = this.parseStringToDate(result.data.occrDt[0], 'YYYYMMDD');
            this.maxDate = this.parseStringToDate(result.data.occrDt[1], 'YYYYMMDD');
            let $el = this.jQuery('div.date_picker_wrap.icpm input');
            $el.datepicker({
              language: 'ko',
              autoClose: true,
              dateFormat: 'yy/mm/dd',
              navTitles: { days: 'yyyy<span>년&nbsp;</span> MM' },
              onHide: function () {},
              timepicker: false,
              toggleSelected: true,
              range: false,
              keyboardNav: false,
              minDate: this.minDate,
              maxDate: this.maxDate,
              onSelect: function(fd, d, inst) {
                if (fd && d && inst) {
                  let year = d.getFullYear();
                  let month =  fd.split('/')[1];
                  let day = '' + fd.split('/')[2];

                  let date = year + month + day;
                  inst.el.setAttribute('data-date', date);
                }
              }
            });
            // 선택 날짜 달력에 표출
            $el.datepicker().data('datepicker').selectDate(date);
          }

          if (this.chartSettingList[0].isOn) {
            this.getRegionChartData();
          }
          if (this.chartSettingList[1].isOn) {
            this.getAgeChartData();
          }
          if (this.chartSettingList[2].isOn) {
            this.getVendorChartData();
          }
          if (this.chartSettingList[3].isOn) {
            this.getPhoneChartData();
          }
          if (this.chartSettingList[4].isOn) {
            this.getTeamChartData();
          }
          if (this.chartSettingList[5].isOn) {
            this.getTrafficChartData();
          }
        }
      }
    });
  }

  /**
   * 콤마 추가
   */
  public addComma(value: number): string | number {
    return Utils.NumberUtil.addComma(value);
  }

  /**
   * chart resize
   * @param thiz icpm.component
   */
  public chartResize(thiz): void {

    if (!thiz) {
      thiz = this;
    }
    thiz.regionChart.resize();
    thiz.ageChart.resize();
    thiz.vendorChart.resize();
    thiz.phoneChart.resize();
    thiz.teamChart.resize();
    thiz.trafficChart.resize();
    // thiz.ceiChart.resize();
  }

  /**
   * 좌측 검색영역 클릭 이벤트
   */
  public checkLeftFlexible(): void {
    this.isLeftFlexible = !this.isLeftFlexible;

    let thiz = this;
    setTimeout(function() {
      thiz.chartResize(thiz);
    }, 200);
  }

  /**
   * 우측 필터영역 클릭 이벤트
   */
  public checkRightFlexible(): void {
    this.isRightFlexible = !this.isRightFlexible;

    let thiz = this;
    setTimeout(function() {
      thiz.chartResize(thiz);
    }, 200);
  }

    /**
   * 'yyyyMMdd' 형태의 string을 date로 반환
   *
   * @param date (yyyyMMdd 포멧의 string data)
   */
  public parseStringToDate(date: string, format: string): Date {
    return moment(date, format).toDate();
  }

  /**
   * 'yyyyMMdd' 형태의 string을 yy/MM/dd로 포멧팅
   *
   * @param date (yyyyMMdd 포멧의 string data)
   */
  public formattingDate(date: string): string {
    let ret = [];
    if (date) {
      ret.push(date.substr(2, 2));
      ret.push(date.substr(4, 2));
      ret.push(date.substr(6, 2));
    }
    return ret.join('/');
  }

  /**
   * date picker select 이벤트
   *
   * @param e (캘린더 date 객체)
   */
  public selectDate(e): void {

    // 날짜 선택을 안하였을 경우
    if (!e.target.value) {
      // 기존 날짜로 셋팅
      this.jQuery('div.date_picker_wrap.icpm input').datepicker().data('datepicker').selectDate(this.parseStringToDate(this.icpmDate, 'YYYYMMDD'));
      return;
    }

    // yyyyMMdd 날짜
    let icpmDateTmp = e.target.getAttribute('data-date');

    // 기존 날짜랑 다를 경우
    if (icpmDateTmp != this.icpmDate && this.icpmDate) {
      this.icpmDate = icpmDateTmp

      // 차트 데이터 조회
      this.getFilterData(false);
    }
  }

  /**
   * input 데이터로 달력 변경
   *
   * @param e (캘린더 텍스트박스 객체)
   */
  public changeInputDate(e): void{
    // 변경 텍스트 데이터
    let value = e.target.value;

    // 달력 형식 체크
    if (moment(value, ['YY/MM/DD'], true).isValid()) {
      let changeDate = this.parseStringToDate(value, 'YY/MM/DD');

      // 최소, 최대 기간 체크
      if (this.minDate.getTime() > changeDate.getTime()) {
        Alert.error('선택 날짜가 너무 작습니다.');
        e.target.value = this.formattingDate(this.icpmDate);
      } else if (this.maxDate.getTime() < changeDate.getTime()) {
        Alert.error('선택 날짜가 너무 큽니다.');
        e.target.value = this.formattingDate(this.icpmDate);
      } else {
        this.jQuery('div.date_picker_wrap.icpm input').datepicker().data('datepicker').selectDate(changeDate);
      }

    } else {
      Alert.error('날짜형식이 맞지않습니다.');
      e.target.value = this.formattingDate(this.icpmDate);
    }
  }

  /**
   * 메뉴 클릭 이벤트 처리
   *
   * @param e (이벤트)
   * @param fltr 필터
   */
  public fltrClickEvent(e: any, fltr: any): void {

    // right-kind 초기화
    this.menuDetailData = null;

    // menu의 fltrUid
    let fltrUid = fltr.fltrUid;

    // 메뉴 상세정보 조회
    this.icpmService.getIcpmFltrBas(fltrUid)
    .then(result => {
      if (result.code === CommonConstant.CODE.RESULT_CODE.SUCCESS) {

        // 메뉴 데이터
        this.menuDetailData = result.data;
        // ICPM 선택 날짜
        this.menuDetailData.occrDt = this.icpmDate;

        // 선택 한 필터가 존재 할 경우
        for (let i = 0; i < this.filterDataTmp.length; i++) {
          if (this.filterDataTmp[i].fltrUid === fltrUid) {

            // 선택한 필터 정보 전달
            this.menuDetailData.type = this.filterDataTmp[i].type;
            this.menuDetailData.fltrVal = this.filterDataTmp[i].fltrVal;
            this.menuDetailData.dtNm = this.filterDataTmp[i].dtNm;
            this.menuDetailData.druidNm = this.filterDataTmp[i].druidNm;
            break;
          }
        }
      }
    });
  }

  /**
   * 메뉴 검색 이벤트
   *
   * @param value (검색 메뉴 명)
   */
  public searchFltr(value: string): void {
    // 전부 소문자로 바꾸기 (대소문자 구분하지 않기 위함)
    value = value.trim().toLowerCase();

    // 검색 단어가 없을 경우 초기화
    if (value == '') {
      this.fltrDataTmp = _.cloneDeep(this.fltrData);

      // 전체 펼치기 종료
      this.isHands = false;
    } else {
      // 검색 데이터
      let searchFltrData = [];

      for (let i = 0; i < this.fltrData.length; i++) {

        let fltrData = _.cloneDeep(this.fltrData[i]);
        let addFltrData = _.cloneDeep(fltrData);

        // 메뉴리스트 초기화
        addFltrData.fltrList = [];

        if (fltrData.useYn == 'Y') {
          for (let j = 0; j < fltrData.fltrList.length; j++) {
            let fltr1DepsData = fltrData.fltrList[j];

            // 2deps 가 존재하지 않고 검색 단어가 포함 될 경우
            if (fltr1DepsData.leafYn == 'Y' && fltr1DepsData.scrnNm.toLowerCase().includes(value) && fltr1DepsData.useYn == 'Y') {
              // 해당 메뉴 데이터 추가
              addFltrData.fltrList.push(fltr1DepsData);

            // 2deps가 존재 할 경우
            } else if (fltr1DepsData.leafYn == 'N') {
              let fltr2DepsData = _.cloneDeep(fltr1DepsData.fltrList);
              // 1deps 매뉴리스트 초기화
              fltr1DepsData.fltrList = [];
              for (let k = 0; k < fltr2DepsData.length; k++) {
                if (fltr2DepsData[k].scrnNm.toLowerCase().includes(value) && fltr2DepsData[k].useYn == 'Y') {
                  fltr1DepsData.fltrList.push(fltr2DepsData[k]);
                }
              }
              // 2deps 검색이 포함 될 경우 2deps 추가
              if (fltr1DepsData.fltrList.length) {
                addFltrData.fltrList.push(fltr1DepsData);
              }
            }
          }
        }

        // 검색 단어가 포함된 메뉴데이터 추가
        if (addFltrData.fltrList.length) {
          searchFltrData.push(addFltrData);
        }
      }

      this.fltrDataTmp = searchFltrData;

      this.isHands = false;

      // 검색 데이터가 있을 경우
      if (this.fltrDataTmp.length) {
        // 검색 메뉴 펼치기
        this.checkHands();
      }
    }

    // 검색 카운트 계산
    this.checkLeafCnt(this.fltrDataTmp);
  }

  /**
   * 전체펼치기 toggle 클릭 이벤트
   */
  public checkHands(): void {
    this.isHands = !this.isHands;

    if (this.isHands) {
      for (let i = 0; i < this.fltrDataTmp.length; i++) {
        this.fltrDataTmp[i].folder = true;
      }
    } else {
      for (let i = 0; i < this.fltrDataTmp.length; i++) {
        this.fltrDataTmp[i].folder = false;
      }
    }
  }

  /**
   * 메뉴 펼치기 접기 이벤트
   *
   * @param item (선택한 메뉴 데이터)
   */
  public checkFltrOpen(item: any): void {

    if (item.folder) {
      item.folder = false;
      // 한가지 메뉴라도 접혀질 경우 전체펼치기 버튼 false
      this.isHands = false;
    } else {
      item.folder = true;
      // 모든 메뉴가 펼쳐질 경우 전체펼치기 버튼 true
      for (let i = 0; i < this.fltrDataTmp.length; i++) {
        if (!this.fltrDataTmp[i].folder) {
          return;
        }
      }
      this.isHands = true;
    }
  }

  /**
   * 메뉴 leaf 갯수 조회
   *
   * @param fltrData (메뉴 데이터)
   */
  public checkLeafCnt(fltrData: any): void {
    for (let i = 0; i < fltrData.length; i++) {
      let fltr1DepsData = fltrData[i];
      let cnt = 0;
      for (let j = 0; j < fltr1DepsData.fltrList.length; j++) {
        if (fltr1DepsData.fltrList[j].useYn == 'Y') {
          if(fltr1DepsData.fltrList[j].leafYn == 'Y') {
            cnt++;
          } else {
            let fltr2DepsData = fltr1DepsData.fltrList[j].fltrList;
            for (let k = 0; k < fltr2DepsData.length; k++) {
              if (fltr2DepsData[k].useYn == 'Y') {
                cnt++;
              }
            }
          }
        }
      }
      fltr1DepsData['fltrListCnt'] = cnt;
    }
  }

  /**
   * 메뉴 필터 사용 검사 이벤트
   *
   * @param fltrUid (해당 필터 Uid)
   */
  public checkFltrUseYn(fltrUid: string): string {

    for (let i = 0; i < this.fltrData.length; i++) {
      let fltr1DepthData = this.fltrData[i];
      for (let j = 0; j < fltr1DepthData.fltrList.length; j++) {
        let fltr2DepthData = fltr1DepthData.fltrList[j];

        if (fltr2DepthData.fltrUid == fltrUid) {
          return fltr2DepthData.useYn;
        }

        for (let k = 0; k < fltr2DepthData.fltrList.length; k++) {
          let fltr3DepthData = fltr2DepthData.fltrList[k];

          if (fltr3DepthData.fltrUid == fltrUid) {
            return fltr3DepthData.useYn;
          }
        }
      }
    }
  }

  /**
   * excel download 실행
   */
  public downloadExcel(): void {
    if (!this.isDownloadExcel) {
      this.isDownloadExcel = true;
      this.icpmService.getIcpmExcel({
        fltrDatVal: this.filterDataTmp,
        occrDt: this.icpmDate
      })
      .then(result => {
        this.isDownloadExcel = false;
      });
    }
  }

  /**
   * 지역별 분포 시도 선택 이벤트
   *
   * @param e (이벤트 - 선택 시도 데이터)
   */
  public selectSido(e: any): void {
    let code = e.target.value;
    this.currentAddrCode = [];

    // 시도전체 조회 체크
    if (code != '0') {
      this.currentAddrCode[0] = code;

      this.addressService.getAddress(this.currentAddrCode[0])
      .then(result => {
        if (result.code === CommonConstant.CODE.RESULT_CODE.SUCCESS) {
          this.sggData = result.data;
        }
      });
    } else {
      this.sggData = [];
    }

    // 지역별 차트 조회
    this.getRegionChartData();
  }

  /**
   * 지역별 분포 시군구 선택 이벤트
   *
   * @param e (이벤트 - 선택 시군구 데이터)
   */
  public selectSgg(e: any): void {
    let code = e.target.value;

    // 시군구 전체 조회 체크
    if (code != '0') {
      this.currentAddrCode[1] = code;
    } else {
      this.currentAddrCode = Utils.ArrayUtil.remove(this.currentAddrCode, 1);
    }

    // 지역별 차트 조회
    this.getRegionChartData();
  }

  /**
   * 지역별 차트 조회
   */
  public getRegionChartData(): void {
    this.chartSettingList[0].isLoading = true;
    this.icpmService.getIcpmCharts({
      chartNum: this.chartNum.region,
      addrCd: this.currentAddrCode,
      fltrDatVal: this.filterDataTmp,
      occrDt: this.icpmDate
    })
    .then(result => {
      if (result.code === CommonConstant.CODE.RESULT_CODE.SUCCESS) {

        let regionOption = this.regionChart.getOption();

        // value 배열
        let targetArr = _.map(result.data.target, 'value');
        let allArr = _.map(result.data.all, 'value');

        regionOption.series[0].data = result.data.target;
        regionOption.series[1].data = result.data.all;

        // 차트 비율 데이터 계산
        regionOption.series[2].data = this.getChartPersentData(targetArr, this.targetCnt);
        regionOption.series[3].data = this.getChartPersentData(allArr, this.allCnt);

        // 차트 축 데이터 계산
        regionOption.xAxis[0].data = this.getChartAxisData(result.data.all);

        this.regionChart.setOption(regionOption, true);
      }
      this.chartSettingList[0].isLoading = false;
    });

  }

  /**
   * 연령별 차트 조회
   */
  public getAgeChartData(): void {

    this.chartSettingList[1].isLoading = true;
    this.icpmService.getIcpmCharts({
      chartNum: this.chartNum.age,
      fltrDatVal: this.filterDataTmp,
      occrDt: this.icpmDate
    })
    .then(result => {
      if (result.code === CommonConstant.CODE.RESULT_CODE.SUCCESS) {

        let ageOption = this.ageChart.getOption();

        // value 배열
        let targetArr = _.map(result.data.target, 'value');
        let allArr = _.map(result.data.all, 'value');

        // 차트 축 데이터 계산
        let arr = this.getChartAxisData(result.data.all);

        ageOption.series[0].data = result.data.target;
        ageOption.series[1].data = result.data.all;

        if ('bar' === ageOption.name) {
          ageOption.xAxis[0].data = arr;
          ageOption.series[2].data = this.getChartPersentData(targetArr, this.targetCnt);
          ageOption.series[3].data = this.getChartPersentData(allArr, this.allCnt);
        } else {
          ageOption.legend[0].data = _.map(arr, 'value');
        }

        this.ageChart.setOption(ageOption, true);
      }
      this.chartSettingList[1].isLoading = false;
    });
  }

  /**
   * 연령별 토글 이벤트 처리
   */
  public toggleAge(type: string): void {

    if (this.ageToggle != type) {
      this.ageToggle = type;

      let ageOption = this.ageChart.getOption();
      let targetData = ageOption.series[0].data;
      let allData = ageOption.series[1].data;

      if ('bar' === ageOption.name) {
        this.ageChart.setOption(this.pieOption, true);

        ageOption = this.ageChart.getOption();
        ageOption.legend[0].data = _.map(allData, 'name');

      } else {

        this.ageChart.setOption(this.barOption, true);
        ageOption = this.ageChart.getOption();

        // value 배열
        let targetArr = _.map(targetData, 'value');
        let allArr = _.map(allData, 'value');

        // 차트 축 데이터 계산
        ageOption.xAxis[0].data = this.getChartAxisData(allData);

        // 차트 비율 데이터 계산
        ageOption.series[2].data = this.getChartPersentData(targetArr, this.targetCnt);
        ageOption.series[3].data = this.getChartPersentData(allArr, this.allCnt);
      }

      ageOption.series[0].data = targetData;
      ageOption.series[1].data = allData;

      this.ageChart.setOption(ageOption, true);
    }
  }

  /**
   * 제조사별 차트 조회
   */
  public getVendorChartData(): void {

    this.chartSettingList[2].isLoading = true;
    this.icpmService.getIcpmCharts({
      chartNum: this.chartNum.vendor,
      fltrDatVal: this.filterDataTmp,
      occrDt: this.icpmDate
    })
    .then(result => {
      if (result.code === CommonConstant.CODE.RESULT_CODE.SUCCESS) {

        let vendorOption = this.vendorChart.getOption();

        // value 배열
        let targetArr = _.map(result.data.target, 'value');
        let allArr = _.map(result.data.all, 'value');

        // 차트 축 데이터 계산
        let arr = this.getChartAxisData(result.data.all);

        vendorOption.series[0].data = result.data.target;
        vendorOption.series[1].data = result.data.all;

        if ('bar' === vendorOption.name) {
          vendorOption.xAxis[0].data = arr;
          vendorOption.series[2].data = this.getChartPersentData(targetArr, this.targetCnt);
          vendorOption.series[3].data = this.getChartPersentData(allArr, this.allCnt);
        } else {
          vendorOption.legend[0].data = _.map(arr, 'value');
        }

        this.vendorChart.setOption(vendorOption, true);
      }
      this.chartSettingList[2].isLoading = false;
    });
  }

  /**
   * 제조사별 토글 이벤트 처리
   */
  public toggleVendor(type: string): void {

    if (this.vendorToggle != type) {
      this.vendorToggle = type;

      let vendorOption = this.vendorChart.getOption();
      let targetData = vendorOption.series[0].data;
      let allData = vendorOption.series[1].data;

      if ('bar' === vendorOption.name) {

        this.vendorChart.setOption(this.pieOption, true);

        vendorOption = this.vendorChart.getOption();
        vendorOption.legend[0].data = _.map(allData, 'name');

      } else {

        let arr = this.getChartAxisData(allData);

        this.vendorChart.setOption(this.barOption, true);
        vendorOption = this.vendorChart.getOption();
        vendorOption.xAxis[0].data = arr;

        // value 배열
        let targetArr = _.map(targetData, 'value');
        let allArr = _.map(allData, 'value');

        // 차트 비율 데이터 계산
        vendorOption.series[2].data = this.getChartPersentData(targetArr, this.targetCnt);
        vendorOption.series[3].data = this.getChartPersentData(allArr, this.allCnt);
      }

      vendorOption.series[0].data = targetData;
      vendorOption.series[1].data = allData;
      this.vendorChart.setOption(vendorOption, true);
    }
  }

  /**
   * 단말기 애칭별 차트 조회
   */
  public getPhoneChartData(): void {

    this.chartSettingList[3].isLoading = true;
    this.icpmService.getIcpmCharts({
      chartNum: this.chartNum.phone,
      fltrDatVal: this.filterDataTmp,
      occrDt: this.icpmDate
    })
    .then(result => {
      if (result.code === CommonConstant.CODE.RESULT_CODE.SUCCESS) {

        let phoneOption = this.phoneChart.getOption();

        // value 배열
        let targetArr = _.map(result.data.target, 'value');
        let allArr = _.map(result.data.all, 'value');

        phoneOption.series[0].data = result.data.target;
        phoneOption.series[1].data = result.data.all;

        // 차트 비율 데이터 계산
        phoneOption.series[2].data = this.getChartPersentData(targetArr, this.targetCnt);
        phoneOption.series[3].data = this.getChartPersentData(allArr, this.allCnt);

        // 차트 축 데이터 계산
        phoneOption.xAxis[0].data = this.getChartAxisData(result.data.all);

        this.phoneChart.setOption(phoneOption, true);
      }
      this.chartSettingList[3].isLoading = false;
    });
  }

  /**
   * 본부별 차트 조회
   */
  public getTeamChartData(): void {

    this.chartSettingList[4].isLoading = true;
    this.icpmService.getIcpmCharts({
      chartNum: this.chartNum.team,
      fltrDatVal: this.filterDataTmp,
      occrDt: this.icpmDate
    })
    .then(result => {
      if (result.code === CommonConstant.CODE.RESULT_CODE.SUCCESS) {

        let teamOption = this.teamChart.getOption();

        // value 배열
        let targetArr = _.map(result.data.target, 'value');
        let allArr = _.map(result.data.all, 'value');

        // 차트 비율 데이터 계산
        let arr = this.getChartAxisData(result.data.all);

        teamOption.series[0].data = result.data.target;
        teamOption.series[1].data = result.data.all;

        if ('bar' === teamOption.name) {
          teamOption.xAxis[0].data = arr;
          teamOption.series[2].data = this.getChartPersentData(targetArr, this.targetCnt);
          teamOption.series[3].data = this.getChartPersentData(allArr, this.allCnt);
        } else {
          teamOption.legend[0].data = _.map(arr, 'value');
        }

        this.teamChart.setOption(teamOption, true);
      }
      this.chartSettingList[4].isLoading = false;
    });
  }

  /**
   * 본부별 토글 이벤트 처리
   */
  public toggleTeam(type: string): void {

    if (this.teamToggle != type) {
      this.teamToggle = type;

      let teamOption = this.teamChart.getOption();
      let targetData = teamOption.series[0].data;
      let allData = teamOption.series[1].data;

      if ('bar' === teamOption.name) {

        this.teamChart.setOption(this.pieOption, true);

        teamOption = this.teamChart.getOption();
        teamOption.legend[0].data = _.map(allData, 'name');

      } else {

        this.teamChart.setOption(this.barOption, true);
        teamOption = this.teamChart.getOption();
        teamOption.xAxis[0].data = this.getChartAxisData(allData);

        // value 배열
        let targetArr = _.map(targetData, 'value');
        let allArr = _.map(allData, 'value');

        // 차트 백분율 계산
        teamOption.series[2].data = this.getChartPersentData(targetArr, this.targetCnt);
        teamOption.series[3].data = this.getChartPersentData(allArr, this.allCnt);
      }

      teamOption.series[0].data = targetData;
      teamOption.series[1].data = allData;
      this.teamChart.setOption(teamOption, true);
    }
  }

  /**
   * Traffic 사용량별 차트 조회
   */
  public getTrafficChartData(): void {

    this.chartSettingList[5].isLoading = true;
    this.icpmService.getIcpmCharts({
      chartNum: this.chartNum.traffic,
      fltrDatVal: this.filterDataTmp,
      occrDt: this.icpmDate
    })
    .then(result => {
      if (result.code === CommonConstant.CODE.RESULT_CODE.SUCCESS) {

        let trafficOption = this.trafficChart.getOption();

        // value 배열
        let targetArr = _.map(result.data.target, 'value');
        let allArr = _.map(result.data.all, 'value');

        trafficOption.series[0].data = result.data.target;
        trafficOption.series[1].data = result.data.all;

        // 차트 비율 설정
        trafficOption.series[2].data = this.getChartPersentData(targetArr, this.targetCnt);
        trafficOption.series[3].data = this.getChartPersentData(allArr, this.allCnt);

        trafficOption.xAxis[0].data = this.getChartAxisData(result.data.all);
        this.trafficChart.setOption(trafficOption, true);
      }
      this.chartSettingList[5].isLoading = false;
    });
  }

  /**
   * 차트 비율 계산
   *
   * @param arr (계산 데이터)
   * @param totalCnt (계산 데이터 전체 수)
   */
  public getChartPersentData(arr: Array<any>, totalCnt: number): Array<any> {
    let persentArr = arr.map(item => {
      if (totalCnt) {
        return Math.round((item / totalCnt) * 10000) / 100;
      } else {
        return 0;
      }
    });

    return persentArr;
  }

  /**
   * 차트 축 데이터 조회
   *
   * @param allArr (전체 데이터)
   */
  public getChartAxisData(allArr: Array<any>): Array<any> {
    let arr = allArr.map(item => {
      return {
        value: item.name,
        textStyle: {
          fontWeight: 'bold',
          color: '#000'
        }
      };
    });

    return arr;
  }

  /**
   * 사용자 즐겨찾기 추가
   */
  public addBmrk(): void {

    if (!this.bmrkNm.trim()) {
      Alert.error(this.translateService.instant('QUICK.MENU.REQUEST.POPUP.PLACEHOLDER.TITLE', '제목을 입력하세요.'));
      return;
    }

    // 필터 제한 조회
    if (!this.getFilterLimitCheck(this.filterDataTmp)) {
      this.showBmrkPopup(false);
      Alert.error('필터 개수가 너무 많습니다.');
      return;
    }

    this.bmrkTitle = this.bmrkNm;

    this.bmrkService.addUserBmrkDtl(this.bmrkNm, this.url, this.filterDataTmp)
    .then(result => {
      if (result.code == CommonConstant.CODE.RESULT_CODE.SUCCESS) {

        // 즐겨찾기 목록 조회
        return this.bmrkService.getUserBmrkDtlList(this.url);

      } else {
        Alert.error(this.translateService.instant('COMMON.MESSAGE.ERROR', '오류가 발생하였습니다.'));
        return result;
      }
    })
    .then(result => {
      if (result.code === CommonConstant.CODE.RESULT_CODE.SUCCESS) {
        this.bmrkData = result.data;
        // 현재 선택 데이터로 등록
        this.currentBmrkData = this.bmrkData[0];
        // 필터 원본 데이터 변경
        this.filterData = _.cloneDeep(this.currentBmrkData.fltrDatVal);
        // 선택 즐겨찾기 초기화 덤프 데이터
        this.filterDataTmp = _.cloneDeep(this.currentBmrkData.fltrDatVal);
      }

      // 저장버튼 보이기
      this.isBmrkSave = true;
      Alert.success(this.translateService.instant('COMMON.MESSAGE.CREATE', '등록되었습니다.'));
    });

    this.showBmrkPopup(false);
  }

  /**
   * 즐겨찾기 필터 수정
   * 저장 버튼 선택 시 수정한 즐겨찾기명으로 update
   */
  public modifyBmrk(): void {

    if (!this.bmrkNm.trim()) {
      Alert.error(this.translateService.instant('QUICK.MENU.REQUEST.POPUP.PLACEHOLDER.TITLE', '제목을 입력하세요.'));
      return;
    }

    // 필터 제한 조회
    if (!this.getFilterLimitCheck(this.filterDataTmp)) {
      this.showBmrkPopup(false);
      Alert.error('필터 개수가 너무 많습니다.');
      return;
    }

    this.bmrkService.editUserBmrkDtl(this.currentBmrkData.bmrkUid, this.bmrkNm , this.filterDataTmp)
    .then(result => {
      if (result.code === CommonConstant.CODE.RESULT_CODE.SUCCESS) {
        return this.bmrkService.getUserBmrkDtlList('icpm');
      } else {
        Alert.error(this.translateService.instant('COMMON.MESSAGE.ERROR', '오류가 발생하였습니다.'));
        return result;
      }
    }).then(result => {
      if (result.code === CommonConstant.CODE.RESULT_CODE.SUCCESS) {
         this.bmrkData = result.data;
         for (let i = 0; i < this.bmrkData.length; i++) {
           if (this.bmrkData[i].bmrkUid === this.currentBmrkData.bmrkUid) {

             // 현재 선택 즐겨찾기 갱신
             this.currentBmrkData = this.bmrkData[i];
             // 타이틀 변경
             this.bmrkTitle = this.currentBmrkData.bmrkNm;
             // 수정 필터 기존 데이터에 추가
             this.filterData = _.cloneDeep(this.currentBmrkData.fltrDatVal);

             Alert.success(this.translateService.instant('COMMON.MESSAGE.MODIFY', '수정되었습니다.'));
             return;
           }
         }
       }
     });;

    this.showBmrkModPopup(false);
  }

  /**
   * 사용자 즐겨찾기 삭제
   *
   * @param i (선택 즐겨찾기 인덱스)
   */
  public removeBmrk(i: number): void {

    // 현재 조회중인 즐겨찾기 일 경우 필터 조건 삭제
    if (this.currentBmrkData) {
      if (this.currentBmrkData.bmrkUid === this.bmrkData[i].bmrkUid) {
        this.bmrkTitle = '';
        this.filterData = [];
        this.filterDataTmp = [];
        this.isBmrkSave = false;
        this.getFilterData(false);
      }
    }

    // 즐겨찾기 데이터 갱신
    this.bmrkService.getUserBmrkDtlList('icpm')
    .then(result => {
      if (result.code === CommonConstant.CODE.RESULT_CODE.SUCCESS) {
        this.bmrkData = result.data;
      }
    });
  }

  /**
   * 즐겨찾기 데이터 표출
   *
   * @param item (선택 즐겨찾기 필터 데이터)
   */
  public changeBmrk(item: any): void {
    this.currentBmrkData = item;

    // 선택 즐겨찾기 타이틀 설정
    this.bmrkTitle = this.currentBmrkData.bmrkNm;

    for (let i = 0; i < this.bmrkData.length; i++) {
      if (this.bmrkData[i].bmrkUid === item.bmrkUid) {
        if (this.bmrkData[i].fltrDatVal) {
          this.filterData = _.cloneDeep(this.bmrkData[i].fltrDatVal);

          // 현재 사용중이지 않은 메뉴 제외
          this.filterData = this.filterData.filter(e => this.checkFltrUseYn(e.fltrUid) == 'Y');

          // 선택 즐겨찾기 초기화 덤프 데이터
          this.filterDataTmp = _.cloneDeep(this.filterData);
          this.isBmrkSave = true;

          // 필터 데이터 조회
          this.getFilterData(false);
        } else {
          this.filterData = [];
          this.filterDataTmp = [];
          this.isBmrkSave = false;
        }

        // right-kind 초기화
        this.menuDetailData = null;
        return;
      }
    }
  }

  /**
   * 적용한 필터목록에서 필터 선택
   *
   * @param item (선택 필터 데이터)
   */
  public searchFilter(item: any): void {

    // right-kind 초기화
    this.menuDetailData = null;

    // 메뉴 상세정보 조회
    this.icpmService.getIcpmFltrBas(item.fltrUid)
    .then(result => {
      if (result.code === CommonConstant.CODE.RESULT_CODE.SUCCESS) {

        // 해당 menu의 상세정보
        this.menuDetailData = result.data;
        // ICPM 선택 날짜
        this.menuDetailData.occrDt = this.icpmDate;

        // 선택한 필터 정보 전달
        this.menuDetailData.type = item.type;
        this.menuDetailData.fltrVal = item.fltrVal;
        this.menuDetailData.dtNm = item.dtNm;
        this.menuDetailData.druidNm = item.druidNm;
      }
    });
  }

  /**
   * 적용한 필터 목록에서 필터 초기화
   */
  public resetFilter(): void {

    // right-kind component 초기화
    this.menuDetailData = null;

    // 선택 필터목록 데이터 되돌리기
    if (this.currentBmrkData) {
      // this.filterDataTmp =  _.cloneDeep(this.filterData);
      this.isBmrkSave = false;
      this.filterData = [];
      this.filterDataTmp = [];
      this.currentBmrkData = [];
      this.bmrkTitle = '';
    } else {
      this.filterDataTmp = [];
    }
    // 필터 데이터 조회
    this.getFilterData(false);
  }

  /**
   * 적용한 필터 목록에서 필터 삭제
   *
   * @param e (이벤트)
   * @param i (선택 즐겨찾기 필터 데이터 index)
   */
  public removeFilter(e: any, item: any): void {

    // 이벤트 버블링 방지
    e.stopPropagation();

    // 해당 필터 일 경우 right-kind component 초기화
    if (this.menuDetailData) {
      if (this.menuDetailData.fltrUid == item.fltrUid) {
        this.menuDetailData = null;
      }
    }

    // 해당 필터 삭제
    for (let i = 0; i < this.filterDataTmp.length; i++) {
      if (this.filterDataTmp[i].fltrUid == item.fltrUid) {
        this.filterDataTmp = Utils.ArrayUtil.remove(this.filterDataTmp, i);
      }
    }

    if (!this.filterDataTmp.length) {
      // 필터가 존재하지 않을 경우 저장 버튼 삭제
      this.isBmrkSave = false;
      this.bmrkTitle = '';
    }

    // 필터 데이터 조회
    this.getFilterData(false);
  }

  /**
   * 즐겨찾기 필터 not 변경
   *
   * @param e (이벤트)
   * @param i (선택 즐겨찾기 필터 데이터 인덱스)
   */
  public changeFilterNot(e: any, i: number): void {

    // 이벤트 버블링 방지
    e.stopPropagation();

    // NOT Type toggle
    if (this.filterDataTmp[i].type) {
      this.filterDataTmp[i].type = '';
    } else {
      this.filterDataTmp[i].type = 'not';
    }

    // 필터 데이터 조회
    this.getFilterData(false);
  }

  /**
   * right-kind 필터 추가
   *
   * @param item (필터 데이터)
   */
  public submitRightKind(item: any): void {

    // 수정 여부
    let isExist = false;

    // index 값으로 필터 등록/수정 구분
    for (let i = 0; i < this.filterDataTmp.length; i++) {
      // 수정일 경우 필터 변경
      if (this.filterDataTmp[i].fltrUid == item.fltrUid) {
        this.filterDataTmp[i] = item;
        isExist = true;
        break;
      }
    }

    // 등록일 경우 필터 추가
    if (!isExist) {
      this.filterDataTmp.splice(0, 0, item);
    }

    // 필터 데이터 조회
    this.getFilterData(false);
  }

  /**
   * 필터 개수 초과 조회
   *
   * @param filterData (추가 된 필터 데이터)
   */
  public getFilterLimitCheck(filterData: Array<any>): boolean {
    if (JSON.stringify(filterData).length > 40000) {
      return false;
    }

    return true;
  }

  /**
   * 즐겨찾기 추가 팝업창
   *
   * @param showHide (팝업 show / hide 여부)
   */
  public showBmrkPopup(showHide: boolean): void {
    this.bmrkNm = '';
    this.isAddBmrkPopup = showHide;
    this.isBlackOverlay = showHide;

  }

  /**
   * 즐겨찾기 수정 팝업창
   *
   * @param showHide (팝업 show / hide 여부)
   */
  public showBmrkModPopup(showHide: boolean): void {

    // true일 경우 필터 title 입력
    if (showHide) {
      this.bmrkNm = this.currentBmrkData.bmrkNm;
    }
    this.isModBmrkPopup = showHide;
    this.isBlackOverlay = showHide;

  }

  /**
   * 차트 설정 팝업창
   *
   * @param showHide (팝업 show / hide 여부)
   */
  public showChartSetPopup(showHide: boolean): void {

    this.isChartSettingPopup = showHide;
    this.isBlackOverlay = showHide;

    if (showHide) {

      this.chartSettingTempList = _.cloneDeep(this.chartSettingList);

    }
  }

  /**
   * 차트 설정 저장
   */
  public saveChartSetting(): void {

    // 차트 설정 반영
    this.chartSettingList = _.cloneDeep(this.chartSettingTempList);

    // hide 차트 code 목록
    let hideCodeArr = [];

    for (let i = 0; i < this.chartSettingList.length; i++) {


      // hide 차트 code 목록 만들기
      this.chartSettingList[i]
      if (!this.chartSettingList[i].isOn) {
        hideCodeArr.push(this.chartSettingList[i].code);
      }
    }

    // chart setting close
    this.isChartSettingPopup = false;
    this.isBlackOverlay = false;

    // hide한 차트 목록 서버로 전송
    this.chartService.editUserEstDtl(this.url, hideCodeArr.join(','))
    .then(result => {
      this.chartResize(this);
      this.getFilterData(true);
      if (result.code !== CommonConstant.CODE.RESULT_CODE.SUCCESS) {
        Alert.error(this.translateService.instant('COMMON.MESSAGE.ERROR', '오류가 발생하였습니다.'));
      }
    });
  }

  /**
   * 차트설정 토글 이벤트
   *
   * @param item (차트별 표출정보 데이터)
   */
  public changeChartSetting(item: any): void {
    item.isPrevOn = item.isOn;
    item.isOn = !item.isOn
  }

  /**
   *  정보 표출 팝업창
   *
   * @param showHide (팝업 show / hide 여부)
   */
  public showInfoPopup(showHide: boolean): void {
    this.isInfoPopup = showHide;
    this.isBlackOverlay = showHide;
  }
}
