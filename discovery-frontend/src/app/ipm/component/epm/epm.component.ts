import { Component, OnInit, ElementRef, Injector, ViewChild, Inject, Renderer2 } from '@angular/core';
import { DOCUMENT } from '@angular/common';
import { AbstractComponent } from '../../../common/component/abstract.component';
import { RightKindComponent } from '../../common/component/right-kind/right-kind.component';
import { EpmListTableComponent } from './epm-list-table/epm-list-table.component';
import { EpmDetailTableComponent } from './epm-detail-table/epm-detail-table.component';
import { IpmService } from '../../service/ipm.service';
import { EpmService } from '../../service/epm/epm.service';
import { BmrkService } from '../../common/service/bmrk/bmrk.service';
import { AddressService } from '../../../ipm/common/service/right-kind/address/address.service';
import { ChartService } from '../../common/service/chart/chart.service';
import { TranslateService } from '@ngx-translate/core';
import { CommonConstant } from '../../common/constant/common-constant';
import { Alert } from '../../../common/util/alert.util';
import { Utils } from '../../common/util/utils';
import * as moment from 'moment';
import * as _ from 'lodash';

declare const echarts: any;
@Component({
  selector: 'epm',
  templateUrl: './epm.component.html',
  styles: []
})
export class EpmComponent extends AbstractComponent implements OnInit {

  @ViewChild('searchBox')
  public searchBoxData: ElementRef;

  // RightKind 컴포넌트
  @ViewChild(RightKindComponent)
  rightkindComponent: RightKindComponent;

  // EPM 장비목록 컴포넌트
  @ViewChild(EpmListTableComponent)
  epmListTableComponent: EpmListTableComponent;

  // EPM 성능 요약 컴포넌트
  @ViewChild(EpmDetailTableComponent)
  epmDetailTableComponent: EpmDetailTableComponent;

  // 메뉴 링크
  public url: string = 'epm';

  // 메뉴 대분류 데이터
  public networkData: Array<any> = [];

  // 메뉴 구분 데이터
  public equipmentData: Array<any> = [];

  // 메뉴 벤더사 데이터
  public vendorData: Array<any> = [];

  // 선택 대분류
  public selectNetwork: string = '';

  // 선택 중분류
  public selectEquipment: string = '';

  // 선택 벤더사
  public selectVendor: string = '';

  // 메뉴 데이터
  public fltrData: Array<any> = [];

  // 메뉴 검색 덤프 데이터
  public fltrDataTmp: Array<any> = [];

  // 메뉴 카테고리 데이터
  public fltrCategoryData: Array<any> = [];

  // 카테고리 표출 여부
  public isCategory: boolean = false;

  // 메뉴 구분 데이터
  public fltrGroupData: Array<any> = [];

  // 메뉴 상세 데이터
  public menuDetailData: any;

  // 메뉴 전체 펼치기 여부
  public isHands: boolean = false;

  // 본부 데이터
  public headquarterData: any;

  // 팀 데이터
  public teamData: any;

  // 1일/1시간 데이터
  public oneDayHourData: any;

  // 전체/타겟군 카운트
  public allCnt: number = 0;
  public targetCnt: number = 0;
  public gauge: string = '100%';
  public persent: string = '100%'

  // 현재 선택 본부
  public selectHeadquarter: string = 'SKT';

  // 현재 선택 팀
  public selectTeam: string = '';

  // 시도 데이터
  public sidoData: Array<any> = [];

  // 시군구 데이터
  public sggData: Array<any> = [];

  // 지역별 차트
  public regionChart: any;

  // 선택 주소 코드
  public currentAddrCode: Array<any> = [];

  // 차트 로딩 설정
  public isChartLoading: Array<boolean> = [true, true, true];

  // 차트 번호
  public chartNum = {
    count: 'e0',
    team: 'e1',
    region: 'e2',
    equipment: 'e3',
    performance: 'e4'
  };

  // 차트 설정 데이터
  public chartSettingList: Array<any> = [
    {code: this.chartNum.region, name: '지역별 분포', isOn: true, isPrevOn: true},
    {code: this.chartNum.equipment, name: '장비 성능별 분포', isOn: true, isPrevOn: true}
  ];

  // 차트 설정 변경 전 데이터
  public chartSettingTempList: Array<any> = [];

  // 장비목록 별 성능 데이터
  public epmListData: Array<any> = [];

  // 장비목록 헤더 데이터
  public epmHeaderListData: Array<any> = [];

  // 장비목록 cell, enb 구분
  public equipment: string;

  // 장비 목록 갯수
  public epmListCnt: string;

  // 장비목록 선택된 장비
  public selectedEqp: any = {};

  // 장비 1일 1시간 단위 상세 조회
  public epmDetailData: any;

  // 장비 1일 1시간 헤더 데이터
  public epmDetailHeaderData: any;

  // 상세조회 전송 데이터 (info, paging)
  public epmDetailRequestData: any;

  // 즐겨찾기 추가/수정 이름
  public bmrkNm: string = '';

  // 선택 즐겨찾기 타이틀
  public bmrkTitle = '';

  // 즐겨찾기 데이터
  public bmrkData: Array<any> = [];

  // 사용자 즐겨찾기 필터 선택 데이터
  public filterData: Array<any> = [];

  // 사용자 즐겨찾기 필터 수정 덤프 데이터
  public filterDataTmp: Array<any> = [];

  // 기본 즐겨찾기 필터 데이터
  public defaultFilterData: Array<any> = [];

  // 선택 즐겨찾기 데이터
  public currentBmrkData: any;

  // 즐겨찾기 버튼 활성화 여부
  public isBmrkSave: boolean = false;

  // 우측 사이즈 변환 여부
  public isRightFlexible: boolean = false;

  // 좌측 사이즈 변환 여부
  public isLeftFlexible: boolean = false;

  // 즐겨찾기 추가 팝업 show/hide 여부
  public isAddBmrkPopup: boolean = false;

  // 즐겨찾기 수정 팝업 show/hide 여부
  public isModBmrkPopup: boolean = false;

  // 팝업시 검은배경 여부
  public isBlackOverlay: boolean = false;

  // 차트 설정 show/hide 여부
  public isChartSettingPopup: boolean = false;

  // bar 공통 옵션
  public barOption = {
    name: 'bar',
    color: ['#9c96f4', '#e7bdf3'],
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'cross'
      }
    },
    legend: [{
      data: ['타겟군', '전체']
    }],
    grid: {
      left: '70px',
      right: '20px'
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
        type: 'value'
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
        areaStyle: {},
        data: []
      },
      {
        type: 'bar',
        name: '전체',
        areaStyle: {},
        data: []
      }
    ]
  };

  // 생성자
  constructor(
    protected elementRef: ElementRef,
    protected injector: Injector,
    @Inject(DOCUMENT) private document: Document,
    private renderer: Renderer2,
    private ipmService: IpmService,
    private epmService: EpmService,
    private bmrkService: BmrkService,
    private chartService: ChartService,
    private addressService: AddressService
  ) {
    super(elementRef, injector);
  }

  ngOnInit() {

    // 태깅
    // this.tagging(this.TaggingType.DETAIL, this.TaggingAction.VIEW, 'epm', 'IDCube Profile');

    // ipm 화면 헤더 데이터 셋팅
    this.ipmService.setHeaderEvent({
      url : this.url,
      title : 'EPM',
      subTitle : 'Equipment'
    });

    // 초기 즐겨찾기 데이터 조회
    this.bmrkService.getUserBmrkDtlList(this.url)
    .then(result => {
      if (result.code === CommonConstant.CODE.RESULT_CODE.SUCCESS) {
        this.bmrkData = result.data;
      }
    })

    // 초기 메뉴 분류 데이터 조회
    this.epmService.getEpmClBasList()
    .then(result => {
      if (result.code === CommonConstant.CODE.RESULT_CODE.SUCCESS) {
        this.fltrGroupData = result.data;

        // // 메뉴 그룹 초기값 설정
        let codeData = this.fltrGroupData;
        this.networkData = codeData;
        codeData = codeData[0].clList;
        this.equipmentData = codeData;
        codeData = codeData[0].clList;
        this.vendorData = codeData;

        // 초기 그룹 선택
        this.selectNetwork = this.networkData[0].clUid;
        this.selectEquipment = this.equipmentData[0].clUid;
        this.selectVendor = this.vendorData[0].clUid;

        // 차트 설정 조회
        this.chartService.getUserEstDtl(this.url)
        .then(result => {
          if (result.code === CommonConstant.CODE.RESULT_CODE.SUCCESS) {

            // hide 할 차트 정보가 있을 경우
            if (result.data) {
              // hide할 차트 코드 정보
              if (result.data.estVal) {
                let estVal = result.data.estVal;
                for (let i = 0; i < this.chartSettingList.length; i++) {
                  if (estVal.includes(this.chartSettingList[i].code)) {
                    this.chartSettingList[i].isOn = false;
                    this.chartSettingList[i].isPrevOn = false;
                  }
                }
              }
            }

            // 초기 메뉴 데이터 조회
            this.getGroupChangeFltr(this.vendorData[0].clUid, null);
          }
        });
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
   * 콤마 추가
   */
  public addComma(value: number): string | number {
    return Utils.NumberUtil.addComma(value);
  }

  /**
   * 통계 퍼센트 계산
   *
   * @param targetCnt (총 갯수)
   * @param allCnt (해당 데이터 갯수)
   */
  public getPersent(targetCnt: number, allCnt: number): number {
    return Math.floor(targetCnt * 100 / allCnt);
  }

  /**
   * 좌측 검색영역 클릭 이벤트
   */
  public checkLeftFlexible(): void {
    this.isLeftFlexible = !this.isLeftFlexible;

    let thiz = this;
    // 차트 사이즈 조정
    setTimeout(function() {
      thiz.regionChart.resize();
    }, 200);
  }

  /**
   * 우측 필터영역 클릭 이벤트
   *
   * @param e (이벤트)
   */
  public checkRightFlexible(e): void {

    let classList = e.target.classList;
    // two-depth 화면
    let twoDepth = this.document.querySelector('.hidden_panel_wrap.dsp_block');

    if (twoDepth) {
      // 닫기
      if (this.isRightFlexible) {
        if (classList) {
          classList.remove('panel');
        }
        this.renderer.removeClass(twoDepth, 'open');

      // 열기
      } else {
        classList.add('open');
        classList.add('panel');
        this.renderer.addClass(twoDepth, 'open');
      }
    } else {
      if (this.isRightFlexible) {
        if (classList) {
          classList.remove('open');
        }
      }
    }
    this.isRightFlexible = !this.isRightFlexible;
    let thiz = this;
    // 차트 사이즈 조정
    setTimeout(function() {
      thiz.regionChart.resize();
    }, 200);

    this.rightkindComponent.checkRightFlexible();
  }

  /**
   * 그룹 변경이 이루어지는 메뉴 변환
   *
   * @param clUid (현재 선택 벤더사 clUid)
   * @param bmrkFilterData (선택 즐겨찾기 필터 데이터)
   */
  public getGroupChangeFltr(clUid: string, bmrkFilterData: Array<any>): void {

    this.epmService.getEpmFltrBasList(clUid).
    then(result => {
      if (result.code === CommonConstant.CODE.RESULT_CODE.SUCCESS) {
        this.fltrData = result.data;

        // 카테고리 데이터 초기화
        this.fltrCategoryData = [];
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

        // 본부 선택
        this.selectHeadquarter = 'SKT';

        // 팀별 데이터 삭제
        this.teamData = null;

        // right-kind 종료
        this.rightkindComponent.closeRightKind();

        if (this.fltrData.length) {
          // 메뉴 리프 갯수 조회
          this.checkLeafCnt(this.fltrData);

          // 첫번째 폴더 펼치기
          this.fltrData[0].folder = true;

          // 메뉴 덤프 데이터 복사
          this.fltrDataTmp = _.cloneDeep(this.fltrData);

          // 검색 단어 초기화
          this.searchBoxData.nativeElement.value = '';

          // 전체 펼치기 종료
          this.isHands = false;
        } else {
          this.fltrDataTmp = [];
        }

        // 기본 데이터 생성
        if (bmrkFilterData) {
          // 마지막 메뉴 분류 값을 제외하고 필터데이터 적용
          this.filterData = _.cloneDeep(bmrkFilterData);

          // 현재 사용중이지 않은 메뉴 제외
          this.filterData = this.filterData.filter(filter => this.checkFltrUseYn(filter.fltrUid) == 'Y');

          // 선택 즐겨찾기 초기화 덤프 데이터
          this.filterDataTmp = _.cloneDeep(this.filterData);
        } else {
          // 초기 디폴트 메뉴 설정
          this.setDefaultFilter();

          // 즐겨찾기 데이터 초기화
          this.initBmrk();
        }

        // 요청 데이터 생성
        let requestData = _.cloneDeep(this.filterDataTmp);
        requestData.push(this.getClassification());

        // 전체 카운트 조회
        this.isChartLoading[0] = true;
        this.epmService.getEpmCharts({
          chartNum: this.chartNum.count,
          fltrDatVal: requestData
        })
        .then(result => {
          if (result.code === CommonConstant.CODE.RESULT_CODE.SUCCESS) {
            this.targetCnt = result.data.target[0].value;
            this.allCnt = result.data.all[0].value;
            if (this.targetCnt != 0 && this.allCnt != 0) {
              this.gauge = Math.floor(this.targetCnt * 100 / this.allCnt) + '%';
              this.persent = Math.round((this.targetCnt / this.allCnt) * 10000) / 100 + '%';
            } else {
              this.gauge = '0%';
              this.persent = '0%';
            }
          }
        });

        let orgCd = [this.selectHeadquarter];

        // 본부 별 데이터 조회
        this.epmService.getEpmCharts({
          chartNum: this.chartNum.team,
          fltrDatVal: requestData,
          orgCd: orgCd
        })
        .then(result => {
          if (result.code === CommonConstant.CODE.RESULT_CODE.SUCCESS) {
            this.headquarterData = result.data;
          }
          this.isChartLoading[0] = false;
        });

        // 지역별 차트 초기화
        this.regionChart = echarts.init(document.getElementById('regionChart'));
        this.regionChart.setOption(this.barOption, true);

        // 지역별 차트 조회
        if (this.chartSettingList[0].isOn) {
          this.getRegionChartData();
        }

        // 성능별 차트 유무
        if (this.chartSettingList[1].isOn) {
          this.isChartLoading[2] = true;

          // 장비별 성능 조회
          this.epmService.getEpmCharts({
            chartNum: this.chartNum.equipment,
            fltrDatVal: requestData,
            limit: this.epmListCnt
          })
          .then(result => {
            if (result.code === CommonConstant.CODE.RESULT_CODE.SUCCESS) {

              // 선택 장비타입 셋팅
              this.equipment = result.data.equipment;

              // epmList 데이터 셋팅
              this.epmListData = result.data.list;

              // epmHeaderList 데이터 셋팅
              this.epmHeaderListData = result.data.header;

              // selectedEqp 초기화
              this.selectedEqp = {};

              // epmDetailData 초기화
              this.epmDetailData = {};

            }
            this.isChartLoading[2] = false;
          });
        }
      }
    });
  }

  /**
   * 기본 필터 설정
   */
  public setDefaultFilter(): void {
    // 기본 필터 데이터
    let filterDefaultData = [];
    this.defaultFilterData = [];

    /**
     * 필수 값 셋팅
     */
    for (let i = 0; i < this.fltrData.length; i++) {
      let fltr1DepsData = this.fltrData[i].fltrList;
      for (let j = 0; j < fltr1DepsData.length; j++) {
        if (fltr1DepsData[j].leafYn == 'Y') {
          if (fltr1DepsData[j].fltrDelYn == 'N' && fltr1DepsData[j].useYn == 'Y') {
            filterDefaultData.push(fltr1DepsData[j]);
          }
        } else {
          let fltr2DepsData = fltr1DepsData[j].fltrList;
          for (let k = 0; k < fltr2DepsData.length; k++) {
            if (fltr2DepsData[k].leafYn == 'Y' && fltr2DepsData[k].fltrDelYn == 'N' && fltr1DepsData[j].useYn == 'Y') {
              filterDefaultData.push(fltr2DepsData[k]);
            }
          }
        }
      }
    }

    // 기본 설정 필터
    for (let i = 0; i < filterDefaultData.length; i++) {
      let fltrDatVal = {};

      // 연월 일시
      if (filterDefaultData[i].scrnClNm == 'ymdh') {

        // 현재 날짜 1일전 날짜 계산
        let date = new Date();
        date.setDate(date.getDate() - 1);
        let currentDate = moment(date).format('YYYYMMDD')+'23';
        let currentDateNm = moment(date).format('YY/MM/DD')+' 23';
        let prevDate = moment(date).format('YYYYMMDD')+'00';
        let prevDateNm = moment(date).format('YY/MM/DD')+' 00';

        fltrDatVal = {
          fltrUid: filterDefaultData[i].fltrUid,
          scrnNm: filterDefaultData[i].scrnNm,
          scrnClNm: filterDefaultData[i].scrnClNm,
          dtNm: prevDateNm+'~'+currentDateNm,
          fltrDelYn: filterDefaultData[i].fltrDelYn,
          fltrWoYn: filterDefaultData[i].fltrWoYn,
          druidNm: filterDefaultData[i].druidNm,
          fltrVal: [
            {
              code: prevDate+'~'+currentDate,
            }
          ]
        }
      }

      this.defaultFilterData.push(fltrDatVal);
    }

    this.filterData = _.cloneDeep(this.defaultFilterData);
    this.filterDataTmp = _.cloneDeep(this.filterData);
  }

  /**
   * 각 필터 데이터 조회
   */
  public getFilterData(chartSetting: boolean): void {

    // selectedEqp 초기화
    this.selectedEqp = {};

    // epmDetailData 초기화
    this.epmDetailData = {};

    this.getTargetCnt();
    this.getHeadquarterData();

    // 차트설정 일 경우
    if (chartSetting) {
      if (this.chartSettingList[0].isOn && !this.chartSettingList[0].isPrevOn) {
        this.chartSettingList[0].isPrevOn = true;
        this.getRegionChartData();
      }
      if (this.chartSettingList[1].isOn && !this.chartSettingList[1].isPrevOn) {
        this.chartSettingList[1].isPrevOn = true;
        this.getPerformanceData();
      }

      // 팀별 데이터 삭제
      this.teamData = null;

    // 차트설정이 아닌 경우
    } else {
      if (this.chartSettingList[0].isOn) {
        this.getRegionChartData();
      }
      if (this.chartSettingList[1].isOn) {
        this.getPerformanceData();
      }
    }
  }

  /**
   * 대분류 변경 이벤트
   *
   * @param clUid (선택 대분류 데이터)
   * @param isBmrk (즐겨찾기에 의한 변경 여부)
   */
  public changeNetwork(clUid: string, isBmrk: boolean): void {
    for (let i = 0; i < this.fltrGroupData.length; i++) {
      if (this.fltrGroupData[i].clUid == clUid) {
        this.equipmentData = this.fltrGroupData[i].clList;
        this.vendorData = this.equipmentData[0].clList;
      }
    }

    this.selectNetwork = clUid;
    this.selectEquipment = this.equipmentData[0].clUid;
    this.selectVendor = this.vendorData[0].clUid;

    // 즐겨찾기로 인한 변경이 아닐 경우
    if (!isBmrk) {
      this.getGroupChangeFltr(this.vendorData[0].clUid, null);
    }

    // two-depth 화면 종료
    let twoDepth = this.document.querySelector('.hidden_panel_wrap.dsp_block .panel_close') as HTMLElement;
    if (twoDepth) {
      twoDepth.click();
    }
  }

  /**
   * 중분류 변경 이벤트
   *
   * @param clUid (선택 중분류 데이터)
   * @param isBmrk (즐겨찾기에 의한 변경 여부)
   */
  public changeEquipment(clUid: any, isBmrk: boolean): void {
    for (let i = 0; i < this.equipmentData.length; i++) {
      if (this.equipmentData[i].clUid == clUid) {
        this.vendorData = this.equipmentData[i].clList;
      }
    }

    this.selectEquipment = clUid;
    this.selectVendor = this.vendorData[0].clUid;

    // 즐겨찾기로 인한 변경이 아닐 경우
    if (!isBmrk) {
      this.getGroupChangeFltr(this.vendorData[0].clUid, null);
    }

    // two-depth 화면 종료
    let twoDepth = this.document.querySelector('.hidden_panel_wrap.dsp_block .panel_close') as HTMLElement;
    if (twoDepth) {
      twoDepth.click();
    }
  }

  /**
   * 벤더사 변경 이벤트
   *
   * @param clUid (선택 벤더사 데이터)
   * @param isBmrk (즐겨찾기에 의한 변경 여부)
   */
  public changeVendor(clUid: any, isBmrk: boolean): void {
    this.selectVendor = clUid;

    // 즐겨찾기로 인한 변경이 아닐 경우
    if (!isBmrk) {
      this.getGroupChangeFltr(clUid, null);
    }

    // two-depth 화면 종료
    let twoDepth = this.document.querySelector('.hidden_panel_wrap.dsp_block .panel_close') as HTMLElement;
    if (twoDepth) {
      twoDepth.click();
    }
  }

  /**
   * 메뉴 클릭 이벤트 처리
   * @param e (선택 메뉴 이벤트)
   */
  public fltrClickEvent(e: any): void {

    // right-kind 초기화
    // this.menuDetailData = null;
    this.rightkindComponent.closeRightKind();

    // menu의 fltrUid
    let fltrUid = e.target.getAttribute('fltrUid');

    // 메뉴 상세정보 조회
    this.epmService.getEpmFltrBas(fltrUid)
    .then(result => {
      if (result.code === CommonConstant.CODE.RESULT_CODE.SUCCESS) {

        this.menuDetailData = result.data;
        // [0] - 대분류, [1] - 중분류, [2] - 벤더사
        this.menuDetailData.classification = this.getClassification();
        this.menuDetailData.allCnt = this.allCnt;
        this.menuDetailData.occrDth = this.filterDataTmp[this.filterDataTmp.length - 1].fltrVal;

        // 선택 한 필터가 존재 할 경우
        for (let i = 0; i < this.filterDataTmp.length; i++) {
          if (this.filterDataTmp[i].fltrUid === fltrUid) {

            // 선택한 필터 정보 전달
            this.menuDetailData.type = this.filterDataTmp[i].type;
            this.menuDetailData.fltrVal = this.filterDataTmp[i].fltrVal;
            this.menuDetailData.dtNm = this.filterDataTmp[i].dtNm;
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

    for (let i = 0; i < this.fltrDataTmp.length; i++) {
      this.fltrDataTmp[i].folder = this.isHands;
      let lowFltrData = this.fltrDataTmp[i].fltrList;
      for (let i = 0; i < lowFltrData.length; i++) {
        if (lowFltrData[i].fltrList.length) {
          lowFltrData[i].folder = this.isHands;
        }
      }
    }
  }

  /**
   * 1deps 메뉴 펼치기 접기 이벤트
   *
   * @param obj (선택 fltr item)
   */
  public check1DepthFltrOpen(obj: any): void {
    if (obj.folder) {
      obj.folder = false;
      // 한가지 메뉴라도 접혀질 경우 전체펼치기 버튼 false
      this.isHands = false;
    } else {
      obj.folder = true;
      // 모든 메뉴가 펼쳐질 경우 전체펼치기 버튼 true
      for (let i = 0; i < this.fltrDataTmp.length; i++) {
        if (!this.fltrDataTmp[i].folder) {
          return;
        }
        let lowFltrData = this.fltrDataTmp[i].fltrList;
        for (let i = 0; i < lowFltrData.length; i++) {
          if (lowFltrData[i].fltrList.length && !lowFltrData[i].folder) {
            return;
          }
        }
      }
      this.isHands = true;
    }
  }

  /**
   * 2deps 메뉴 펼치기 접기 이벤트
   *
   * @param obj (선택 fltr item)
   */
  public check2DepthFltrOpen(obj: any): void {
    if (obj.folder) {
      obj.folder = false;
      // 한가지 메뉴라도 접혀질 경우 전체펼치기 버튼 false
      this.isHands = false;
    } else {
      obj.folder = true;
      // 모든 메뉴가 펼쳐질 경우 전체펼치기 버튼 true
      for (let i = 0; i < this.fltrDataTmp.length; i++) {
        if (!this.fltrDataTmp[i].folder) {
          return;
        }
        let lowFltrData = this.fltrDataTmp[i].fltrList;
        for (let i = 0; i < lowFltrData.length; i++) {
          if (lowFltrData[i].fltrList.length && !lowFltrData[i].folder) {
            return;
          }
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
        if (fltr1DepsData.fltrList[j].useYn === 'Y') {
          if (fltr1DepsData.fltrList[j].leafYn === 'Y') {
            cnt++;
          } else {
            let fltr2DepsData = fltr1DepsData.fltrList[j].fltrList;
            for (let k = 0; k < fltr2DepsData.length; k++) {
              if (fltr2DepsData[k].useYn === 'Y') {
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
   * 지역별 분포 시 도 선택 이벤트
   *
   * @param e (이벤트 - 선택 시도 데이터)
   */
  public selectSido(e: any): void{
    let code = e.target.value;
    this.currentAddrCode = [];

    // 전체선택 체크
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
   * 지역별 분포 시 군구 선택 이벤트
   *
   * @param e (이벤트 - 선택 시군구 데이터)
   */
  public selectSgg(e: any): void {
    let code = e.target.value;

    // 전체선택 체크
    if (code != '0') {
      this.currentAddrCode[1] = this.currentAddrCode[0]+code;
    } else {
      this.currentAddrCode = Utils.ArrayUtil.remove(this.currentAddrCode, 1);
    }

    // 지역별 차트 조회
    this.getRegionChartData();
  }

  /**
   * 본부 선택 이벤트
   *
   * @param e (선택 본부 데이터)
   */
  public changeBranch(e: any): void {
    // 본부 상세 정보 초기화
    this.teamData = null;
    this.oneDayHourData = null;
    this.selectHeadquarter = e.target.value;

    let requestData = _.cloneDeep(this.filterDataTmp);
    requestData.push(this.getClassification());

    let orgCd = [this.selectHeadquarter];

    this.isChartLoading[0] = true;

    this.epmService.getEpmCharts({
      chartNum: this.chartNum.team,
      fltrDatVal: requestData,
      orgCd: orgCd
    })
    .then(result => {
      if (result.code === CommonConstant.CODE.RESULT_CODE.SUCCESS) {
        this.headquarterData = result.data;
      }
      this.isChartLoading[0] = false;
    });
  }

  /**
   * 본부 선택 이벤트
   *
   * @param item (해당 본부 데이터)
   */
  public searchHeadquarter(item: any) {
    this.oneDayHourData = null;
    this.selectTeam = item.code;

    let requestData = _.cloneDeep(this.filterDataTmp);
    requestData.push(this.getClassification());

    let orgCd = [this.selectHeadquarter, this.selectTeam];

    // 로딩바 표출
    this.isChartLoading[0] = true;

    this.epmService.getEpmCharts({
      chartNum: this.chartNum.team,
      fltrDatVal: requestData,
      orgCd: orgCd
    })
    .then(result => {
      if (result.code === CommonConstant.CODE.RESULT_CODE.SUCCESS) {
        this.teamData = null;
        this.teamData = result.data;
      }
      // 로딩바 표출
      this.isChartLoading[0] = false;
    });


    for (let i = 0; i < this.headquarterData.all.length; i++) {
      if (this.headquarterData.all[i].code == item.code) {
        this.headquarterData.all[i].on = true;
      } else {
        this.headquarterData.all[i].on = false;
      }
    }
  }

  /**
   * 현재 선택 메뉴그룹 조회
   */
  public getClassification(): any {
    // 대,중 분류 및 벤더사 select 값 입력
    let elements = this.document.querySelectorAll('.classification select');
    let classification = {};
    let classificationArr = [{'code' : [elements[0]['value'], elements[1]['value'], elements[2]['value']]}];

    classification['fltrVal'] = classificationArr;

    return classification;
  }

  /**
   * 전체 장비 카운트 조회
   */
  public getTargetCnt(): void {
    // 조회 필터데이터 셋팅
    let requestData = _.cloneDeep(this.filterDataTmp);
    requestData.push(this.getClassification());

    this.epmService.getEpmCharts({
      chartNum: this.chartNum.count,
      fltrDatVal: requestData
    })
    .then(result => {
      if (result.code === CommonConstant.CODE.RESULT_CODE.SUCCESS) {
        this.targetCnt = result.data.target[0].value;
        this.allCnt = result.data.all[0].value;
        if (this.targetCnt != 0 && this.allCnt != 0) {
          this.gauge = Math.floor(this.targetCnt * 100 / this.allCnt) + '%';
          this.persent = Math.round((this.targetCnt / this.allCnt) * 10000) / 100 + '%';
        } else {
          this.gauge = '0%';
          this.persent = '0%';
        }
      }
    });
  }

  /**
   * 본부별 데이터 조회
   */
  public getHeadquarterData(): void {
    let requestData = _.cloneDeep(this.filterDataTmp);
    requestData.push(this.getClassification());

    let orgCd = [this.selectHeadquarter];

    // 로딩바 표출
    this.isChartLoading[0] = true;

    this.epmService.getEpmCharts({
      chartNum: this.chartNum.team,
      fltrDatVal: requestData,
      orgCd: orgCd
    })
    .then(result => {
      if (result.code === CommonConstant.CODE.RESULT_CODE.SUCCESS) {
        this.headquarterData = result.data;

        // 팀별 데이터가 존재 할경우
        if (this.teamData) {
          for (let i = 0; i < this.headquarterData.all.length; i++) {
            if (this.headquarterData.all[i].code == this.selectTeam) {
              this.headquarterData.all[i].on = true;
            } else {
              this.headquarterData.all[i].on = false;
            }
          }
          // 팀별 데이터 조회
          this.getTeamData();
        } else {
          // 로딩바 삭제
          this.isChartLoading[0] = false;
        }
      }
    });
  }

  /**
   * 팀별 데이터 조회
   */
  public getTeamData(): void {
    let requestData = _.cloneDeep(this.filterDataTmp);
    requestData.push(this.getClassification());

    let orgCd = [this.selectHeadquarter, this.selectTeam];

    this.epmService.getEpmCharts({
      chartNum: this.chartNum.team,
      fltrDatVal: requestData,
      orgCd: orgCd
    })
    .then(result => {
      if (result.code === CommonConstant.CODE.RESULT_CODE.SUCCESS) {
        this.teamData = null;
        this.teamData = result.data;
      }
      // 로딩바 삭제
      this.isChartLoading[0] = false;
    });
  }

  /**
   * 장비별 성능 데이터 조회
   */
  public getPerformanceData(): void {
    let requestData = _.cloneDeep(this.filterDataTmp);
    requestData.push(this.getClassification());

    // 로딩바 표출
    this.isChartLoading[2] = true;

    // 장비별 성능 조회
    this.epmService.getEpmCharts({
      chartNum: this.chartNum.equipment,
      fltrDatVal: requestData,
      limit: this.epmListCnt
    })
    .then(result => {
      if (result.code === CommonConstant.CODE.RESULT_CODE.SUCCESS) {
        // 장비 목록 결과 셋팅
        this.equipment = result.data.equipment;
        this.epmListData = result.data.list;
      }
      // 로딩바 표출
      this.isChartLoading[2] = false;
    });
  }

  /**
   * 지역별 차트 타겟군, 전체 데이터 조회
   */
  public getRegionChartData(): void {
    let requestData = _.cloneDeep(this.filterDataTmp);
    requestData.push(this.getClassification());

    this.isChartLoading[1] = true;
    this.epmService.getEpmCharts({
      chartNum: this.chartNum.region,
      fltrDatVal: requestData,
      addrCd: this.currentAddrCode
    })
    .then(result => {
      if (result.code === CommonConstant.CODE.RESULT_CODE.SUCCESS) {

        let regionOption = this.regionChart.getOption();
        regionOption.series[0].data = result.data.target;
        regionOption.series[1].data = result.data.all;

        // 차트 축 셋팅
        let arr = result.data.all.map(item => {
          if (item.name == 'null' || item.name == undefined) {
            item.name = '정보없음';
          };
          return {
            value: item.name,
            textStyle: {
              fontWeight: 'bold',
              color: '#000'
            }
          };
        });

        regionOption.xAxis[0].data = arr;
        this.regionChart.setOption(regionOption, true);
      }
      this.isChartLoading[1] = false;
    });
  }

  /**
   * 즐겨찾기 데이터 표출
   *
   * @param item (선택 즐겨찾기 필터 데이터)
   */
  public changeBmrk(item: any): void {
    let bmrkDataTmp = [];
    let classification = {};
    this.currentBmrkData = item;

    // 선택 즐겨찾기 타이틀 설정
    this.bmrkTitle = this.currentBmrkData.bmrkNm;

    for (let i = 0; i < this.bmrkData.length; i++) {
      if (this.bmrkData[i].bmrkUid === item.bmrkUid) {
        if (this.bmrkData[i].fltrDatVal) {
          //필터 마지막 요소 그룹 데이터 제외
          bmrkDataTmp = _.cloneDeep(this.bmrkData);
          classification = bmrkDataTmp[i].fltrDatVal.pop();

          this.changeNetwork(classification['fltrVal'][0].code[0], true);
          this.changeEquipment(classification['fltrVal'][0].code[1], true);
          this.changeVendor(classification['fltrVal'][0].code[2], true);

          // 즐겨찾기에 지정된 메뉴 그룹 지정
          this.selectNetwork = classification['fltrVal'][0].code[0];
          this.selectEquipment = classification['fltrVal'][0].code[1];
          this.selectVendor = classification['fltrVal'][0].code[2];

          this.getGroupChangeFltr(this.selectVendor, bmrkDataTmp[i].fltrDatVal);

          this.isBmrkSave = true;

        } else {
          this.filterData = [];
          this.filterDataTmp = [];
          this.isBmrkSave = false;
        }
        return;
      }
    }
  }

  /**
   * 사용자 즐겨찾기 추가
   */
  public addBmrk(): void {

    if (!this.bmrkNm.trim()) {
      Alert.error(this.translateService.instant('QUICK.MENU.REQUEST.POPUP.PLACEHOLDER.TITLE', '제목을 입력하세요.'));
      return;
    }

    let reqFilterData = _.cloneDeep(this.filterDataTmp);

    // 필터 제한 조회
    if (!this.getFilterLimitCheck(reqFilterData)) {
      this.showBmrkPopup(false);
      Alert.error('필터 개수가 너무 많습니다.');
      return;
    }

    this.bmrkTitle = this.bmrkNm;

    // 메뉴 그룹 추가
    reqFilterData.push(this.getClassification());

    this.bmrkService.addUserBmrkDtl(this.bmrkNm, this.url, reqFilterData)
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
        // 현재 선택 데이터로 등록 (bmrk 데이터에 지장이 없도록 값복사)
        this.currentBmrkData = _.cloneDeep(this.bmrkData[0]);
        this.currentBmrkData.fltrDatVal.pop();
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

    // 메뉴 그룹 추가
    let reqFilterData = _.cloneDeep(this.filterDataTmp);

    // 필터 제한 조회
    if (!this.getFilterLimitCheck(reqFilterData)) {
      this.showBmrkPopup(false);
      Alert.error('필터 개수가 너무 많습니다.');
      return;
    }

    reqFilterData.push(this.getClassification());

    this.bmrkService.editUserBmrkDtl(this.currentBmrkData.bmrkUid, this.bmrkNm , reqFilterData)
    .then(result => {
      if (result.code === CommonConstant.CODE.RESULT_CODE.SUCCESS) {
        return this.bmrkService.getUserBmrkDtlList(this.url);
      } else {
        Alert.error(this.translateService.instant('COMMON.MESSAGE.ERROR', '오류가 발생하였습니다.'));
        return result;
      }
    }).then(result => {
      if (result.code === CommonConstant.CODE.RESULT_CODE.SUCCESS) {
        this.bmrkData = result.data;
        for (let i = 0; i < this.bmrkData.length; i++) {
          if (this.bmrkData[i].bmrkUid === this.currentBmrkData.bmrkUid) {

            this.currentBmrkData = _.cloneDeep(this.bmrkData[i]);
            this.currentBmrkData.fltrDatVal.pop();

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
        this.setDefaultFilter();
        this.bmrkTitle = '';
        this.isBmrkSave = false;

        // right-kind 종료
        this.rightkindComponent.closeRightKind();
      }
    }

    // 즐겨찾기 데이터 갱신
    this.bmrkService.getUserBmrkDtlList(this.url)
    .then(result => {
      if (result.code === CommonConstant.CODE.RESULT_CODE.SUCCESS) {
        this.bmrkData = result.data;
      }
    });
  }

  /**
   * 선택 사용자 즐겨찾기 초기화
   */
  public initBmrk(): void {
    this.bmrkTitle = '';
    this.isBmrkSave = false;
    this.currentBmrkData = {};
    this.setDefaultFilter();
  }

  /**
   * 적용한 필터 목록에서 필터 선택
   *
   * @param item (선택 즐겨찾기 필터 데이터)
   */
  public searchFilter(item: any): void {

    // right-kind 종료
    this.rightkindComponent.closeRightKind();

    // 메뉴 상세정보 조회
    this.epmService.getEpmFltrBas(item.fltrUid)
    .then(result => {
      if (result.code === CommonConstant.CODE.RESULT_CODE.SUCCESS) {

        // 해당 menu의 상세정보
        this.menuDetailData = result.data;

        // [0] - 대분류, [1] - 중분류, [2] - 벤더사
        this.menuDetailData.classification = this.getClassification();
        this.menuDetailData.allCnt = this.allCnt;
        this.menuDetailData.occrDth = this.filterDataTmp[this.filterDataTmp.length - 1].fltrVal;

        // 선택한 필터 정보 전달
        this.menuDetailData.type = item.type;
        this.menuDetailData.fltrVal = item.fltrVal;
        this.menuDetailData.dtNm = item.dtNm;
      }
    });
  }

  /**
   * 적용한 필터 초기화
   */
  public resetFilter(): void {

    // right-kind 종료
    this.rightkindComponent.closeRightKind();

    // 선택 필터목록 데이터 되돌리기
    this.isBmrkSave = false;
    this.bmrkTitle = '';
    this.currentBmrkData = [];
    this.filterData = [];
    this.filterDataTmp = _.cloneDeep(this.defaultFilterData);


    // 필터 데이터 조회
    this.getFilterData(false);
  }

  /**
   * 적용한 필터 목록에서 필터 삭제
   *
   * @param e (이벤트)
   * @param item (선택 즐겨찾기 필터 데이터)
   */
  public removeFilter(e: any, item: any): void {

    // 이벤트 버블링 방지
    e.stopPropagation();

    // 해당 필터 일 경우 right-kind component 초기화
    if (this.menuDetailData) {
      if (this.menuDetailData.fltrUid == item.fltrUid) {
        // right-kind 종료
        this.rightkindComponent.closeRightKind();
      }
    }

    // 해당 필터 삭제 시
    for (let i = 0; i < this.filterDataTmp.length; i++) {
      if (this.filterDataTmp[i].fltrUid == item.fltrUid) {
        this.filterDataTmp = Utils.ArrayUtil.remove(this.filterDataTmp, i);
      }
    }

    if (!this.filterDataTmp.length) {
      // 수정 필터가 존재하지 않을 경우 저장 버튼 삭제
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
   * @param index (선택 즐겨찾기 필터 데이터 인덱스)
   */
  public changeFilterNot(e: any, index: number): void {

    // 이벤트 버블링 방지
    e.stopPropagation();

    // NOT Type toggle
    if (this.filterDataTmp[index].type) {
      this.filterDataTmp[index].type = '';
    } else {
      this.filterDataTmp[index].type = 'not';
    }

    //필터 데이터 조회
    this.getFilterData(false);
  }

  /**
   * 장비별 리스트 갯수 표출 변경
   *
   * @param item (표출 장비 갯수)
   */
  public changeEpmListCnt(item: any): void {
    this.epmListCnt = item;

    // 요청 데이터 생성
    let requestData = [];
    requestData = _.cloneDeep(this.filterDataTmp);
    requestData.push(this.getClassification());

    // 성능별 차트 유무
    if (this.chartSettingList[1].isOn) {
      this.isChartLoading[2] = true;

      // 장비별 성능 조회
      this.epmService.getEpmCharts({
        chartNum: this.chartNum.equipment,
        fltrDatVal: requestData,
        limit: this.epmListCnt
      })
      .then(result => {
        if (result.code === CommonConstant.CODE.RESULT_CODE.SUCCESS) {

          // 선택 equipment 명
          this.equipment = result.data.equipment;

          // epmList 데이터 셋팅
          this.epmListData = result.data.list;

          // selectedEqp 초기화
          this.selectedEqp = {};

          // epmDetailData 초기화
          this.epmDetailData = {};

        }
        this.isChartLoading[2] = false;
      });
    }
  }

  /**
   * 장비 목록에서 장비 선택 이벤트
   */
  public selectEpmList(): void {

    let info = _.values(this.selectedEqp);
    if (info.length > 0) {

      // fltrDatVal 셋팅
      let requestData = _.cloneDeep(this.filterDataTmp);
      requestData.push(this.getClassification());

      this.isChartLoading[2] = true;
      this.epmService.getEpmCharts({
        chartNum: this.chartNum.performance,
        info: info,
        fltrDatVal: requestData
      })
      .then(result => {
        if (result.code === CommonConstant.CODE.RESULT_CODE.SUCCESS) {

          this.epmDetailRequestData = {};
          // 조회 파라미터 셋팅
          this.epmDetailRequestData['paging'] = result.data.paging;
          this.epmDetailRequestData['info'] = info;
          // 장비 1시간 1일 단위 리스트 데이터 셋팅
          var data = {
            list: result.data.list,
            header: result.data.header,
            cnt: result.data.cnt,
            smryList: result.data.smryList,
            smryHeader: result.data.smryHeader,
            info: info
          };

          this.epmDetailData = data;
        }
        this.isChartLoading[2] = false;
      });
    } else {
      this.epmDetailData = {};
    }
  }

  /**
   * epm 1일 1단위 상세 데이터 추가 이벤트
   */
  public addEpmDetailData(): void {
    // fltrDatVal 셋팅
    let requestData = _.cloneDeep(this.filterDataTmp);
    requestData.push(this.getClassification());

    this.isChartLoading[2] = true;
    this.epmService.getEpmCharts({
      chartNum: this.chartNum.performance,
      info: this.epmDetailRequestData.info,
      paging: this.epmDetailRequestData.paging,
      fltrDatVal: requestData
    })
    .then(result => {
      if (result.code === CommonConstant.CODE.RESULT_CODE.SUCCESS) {

        // 조회 파라미터 셋팅
        this.epmDetailRequestData['paging'] = result.data.paging;
        // 장비 1시간 1일 단위 리스트 데이터 셋팅
        var data = {
          list: result.data.list,
          info: this.epmDetailRequestData.info
        };

        this.epmDetailData = data;
      }
      this.isChartLoading[2] = false;
    });
  }

  /**
   * right-kind 필터 추가
   * @param item (필터 데이터)
   */
  public submitRightKind(item: any): void {

    // 수정 여부
    let isExist = false;

    // index 값으로 필터 등록/수정 구분
    for (let i = 0; i < this.filterDataTmp.length; i++) {

      // 수정일 경우 필터 변경
      if (this.filterDataTmp[i].fltrUid === item.fltrUid) {
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
   * @param showHide (팝업 show / hide 여부)
   */
  public showBmrkPopup(showHide: boolean): void {

    this.bmrkNm = '';
    this.isAddBmrkPopup = showHide;
    this.isBlackOverlay = showHide;

  }

  /**
   * 즐겨찾기 수정 팝업창
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
   * 차트 설정 저장
   */
  public saveChartSetting(): void {

    // 차트 설정 반영
    this.chartSettingList = _.cloneDeep(this.chartSettingTempList);

    // hide 차트 code 목록
    let hideCodeArr = [];

    for (let i = 0; i < this.chartSettingList.length; i++) {

      // hide 차트 code 목록 만들기
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
      this.regionChart.resize();
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
   * Excel Download
   */
  public downloadExcel(data: any): void {

    // classfication List
    let cfList = this.getClassification().fltrVal[0].code;
    let cfNmList = [];

    // network명
    if (cfList[0] == this.networkData[0].clUid) {
      cfNmList.push(this.networkData[0].clNm);
    }

    // equipment명
    for (let eqp of this.equipmentData) {
      if (cfList[1] == eqp.clUid) {
        cfNmList.push(eqp.clNm);
        break;
      }
    }

    // vendor명
    for (let vendor of this.vendorData) {
      if (cfList[2] == vendor.clUid) {
        cfNmList.push(vendor.clNm);
        break;
      }
    }

    // 요청 데이터 생성
    let fltrDatVal = [];
    fltrDatVal = _.cloneDeep(this.filterDataTmp);
    fltrDatVal.push(this.getClassification());

    this.epmService.getEpmChartsExcel(data, fltrDatVal, cfNmList)
    .then(result => {
      if (data.chartNum == this.chartNum.equipment) {
        this.epmListTableComponent.finishedExcel();
      } else if (data.chartNum == this.chartNum.performance) {
        this.epmDetailTableComponent.finishedExcel();
      }
    });
  }

}
