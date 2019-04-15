import { Component, OnInit, ElementRef, Injector, Input, Output, EventEmitter, AfterViewInit, ViewChildren, QueryList } from '@angular/core';
import { AbstractComponent } from '../../../../../common/component/abstract.component';
import { AddressService } from '../../../../common/service/right-kind/address/address.service';
import { RightKindInterface } from '../../../../common/component/right-kind/right-kind.component';
import { CommonConstant } from '../../../../common/constant/common-constant';
import { LpmService } from '../../../../service/lpm/lpm.service';
import { AppBean } from '../../util/appBean';
import { Utils } from '../../../../common/util/utils';
import { Alert } from '../../../../../common/util/alert.util';

import * as moment from 'moment';
import * as _ from 'lodash';

declare var window: any;

declare const $: any;

@Component({
    selector: 'right-compare',
    templateUrl: './right_compare.component.html',
    styles: []
})

export class RightCompareComponent extends AbstractComponent implements OnInit, AfterViewInit, RightKindInterface {

    // 전달받은 데이터
    @Input()
    public metaInfo: any;

    @Input()
    public menuDetailData: any;

    public filterList: Array<any> = [];
    public filterCompList: Array<any> = [];

    public tempDetailData: any;


    // right-kind 데이터 전송 emitter
    @Output()
    public onSubmitRightKind = new EventEmitter<any>();

    @ViewChildren('analysisTime')
    public analysisTimeList: QueryList<ElementRef>;

    public hourList: Array<number> = [];
    public startSelectedHour: number = 9;
    public endSelectedHour: number = 10;
    public usedHour: boolean = true;

    public compHourList: Array<number> = [];
    public compStartSelectedHour: number = 9;
    public compEndSelectedHour: number = 10;
    public compUsedHour: boolean = true;

    private _appBean: any;

    // 시도 데이터
    public sidoData: Array<any> = [];

    // 시군구 데이터
    public sggData: Array<any> = [];

    // 읍면동 데이터
    public amdData: Array<any> = [];

    // 선택 시도 데이터
    public currentSidoData: any;

    // 선택 시군구 데이터
    public currentSggData: any;

    // 선택 데이터 주소이름
    public selectAllAddrNameData: Array<any> = [];

    // 선택 데이터 주소코드
    public selectAllAddrCodeData: Array<any> = [];

    // 선택 데이터 주소이름
    public selectAddrNameData: Array<any> = [];

    // 선택 데이터 주소코드
    public selectAddrCodeData: Array<any> = [];

    // 시도 선택 표시
    public isSidoSelect: boolean = false;

    // 시군구 선택 표시
    public isSggSelect: boolean = false;

    // date 배열
    public dateList: Array<any> = [];

    // 비교군 선택 분석시간
    public selectAnalysisTime: Array<string> = [];

    // 대조군 선택 분석시간
    public selectCompAnalysisTime: Array<string> = [];

    // 비교군 분석시간 데이터
    public analysisTime: Array<any> = [];

    // 대조군 분석시간 데이터
    public compAnalysisTime: Array<any> = [];

    // 날짜시간 데이터
    public dateTime: Array<string> = [];

    // 최한시 데이터
    public limitAnalysisTime: Array<string> = ['04','05'];

    // 시작 종료 데이터
    public startDate: Date;
    public endDate: Date;

    // 휴일 체크 데이터
    public isHoliDayYn: string = 'N';

    // 비교군 전체 선택 체크 데이터
    public isAllYn: string = 'Y';

    // 대조군 전체 선택 체크 데이터
    public compIsAllYn: string = 'Y';

    // 비교군/대조군 동일 체크
    public isCompCheck: boolean = false;

    public isChecked: boolean = false;

    // 비교군 데이터소스 리스트
    public layrGrpList: Array<any> = [];

    // 비교군 선택된 데이터소스
    public layrGrpId: string = '300';

    // 대조군 선택된 데이터소스
    public comLayrGrpId: string = '300';

    // 비교군 선택된 데이터소스
    public visibleVenders: boolean = true;

    // 대조군 선택된 데이터소스
    public visibleComVenders: boolean = true;

    public venderId: string = '1';
    public compVenderId: string = '1';

    public venderList: Array<any> = [
        {venderId: '1', venderName: 'SKT'},
        {venderId: '2', venderName: 'KT'},
        {venderId: '3', venderName: 'LGU+'},
    ];

    private _beforeDruidNms: Array<string> = [];
    private _beforeCompareDruidNms: Array<string> = [];

    private _ignoreWarningDruidNms: Array<string> = [];
    private _ignoreCompareWarningDruidNms: Array<string> = [];

    protected jQuery = $;

    // 생성자
    constructor(
        protected elementRef: ElementRef,
        protected injector: Injector,
        private lpmService: LpmService,
        private addressService: AddressService
    ) {
        super(elementRef, injector);
        //this._appBean = injector.get(AppBean);
        this.hourList = _.range(0, 24);
        this.compHourList = _.range(0, 24);
    }

    ngOnInit() {
        this.initDateTimePicker();

        this.addressService.getAddress()
        .then(result => {
            if (result.code === CommonConstant.CODE.RESULT_CODE.SUCCESS) {
              this.sidoData = result.data;
            }
        });

        // 기본 데이터
        this.startDate = this.getRangeDate("-11");
        this.endDate = this.getRangeDate("-1");

        // 분석시간 데이터
        let oneTime = Array(10).fill(0).map((x, i) => '0'+i);
        let twoTime = Array(14).fill(0).map((x, i) => ''+(Number(i)+10));
        this.dateTime = oneTime.concat(twoTime);

        // 날짜 시간 데이터
        for(let i = 0; i < 24 ; i++) {
            let data = {};
            if (i < 10) {
                data = {
                time: '0'+i,
                check: 'N'
                }
            } else {
                data = {
                time: ''+i,
                check: 'N'
                }
            }

            this.analysisTime.push(data);
            this.compAnalysisTime.push(_.cloneDeep(data));
        }


        // 선택 필터 클릭에 의한 셋팅
        //임시 셋팅
        let startDateVal = new Date();
        startDateVal.setMonth(startDateVal.getMonth() + 1);
        startDateVal.setDate(startDateVal.getDate() - 1);
        let endDateVal = new Date();
        endDateVal.setMonth(endDateVal.getMonth() + 1);
        endDateVal.setDate(endDateVal.getDate() - 1);

        this.tempDetailData = [{code : [
            startDateVal.getFullYear().toString() + ((startDateVal.getMonth()<10) ? ("0"+startDateVal.getMonth().toString()) : startDateVal.getMonth().toString()) + startDateVal.getDate().toString()
            + "~"
            + endDateVal.getFullYear().toString() + ((endDateVal.getMonth()<10) ? ("0"+endDateVal.getMonth().toString()) : endDateVal.getMonth().toString())  + endDateVal.getDate().toString()
           ], name : "dthh"}, {code : "N", name : "holiday"}, {code : ["00", "01", "02", "03", "04", "05" ,"06", "07" ,"08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23"], name : "hh"}];

        if (this.tempDetailData) {
        let fltrVal = _.cloneDeep(this.tempDetailData);

        for (let i = 0; i < fltrVal[0].code.length; i++) {

            let dateRange = fltrVal[0].code[i].split('~');

            // 처리시간 (TODO ) 데이터 리스트에 시간 추가
            let startTime = dateRange[0].substring(8,10);
            let endTime = dateRange[1].substring(8,10);

            this.dateList.push({
            formatDateRange: [
                this.formattingDate(dateRange[0]),
                this.formattingDate(dateRange[1])
            ],
            dateRange: [dateRange[0], dateRange[1]]
            ,
            dateNm: [
                this.formattingDate(dateRange[0])+' '+startTime,
                this.formattingDate(dateRange[1])+' '+endTime
            ]
            });
        }

        // 휴일 제외 데이터
        this.isHoliDayYn = this.tempDetailData[1].code;

        // 선택 분석시간 데이터
        this.selectAnalysisTime = this.tempDetailData[2].code;

        for(let i = 0; i < this.analysisTime.length; i++) {
            for (let j = 0; j < this.selectAnalysisTime.length; j++) {
            if (this.analysisTime[i].time ==  this.selectAnalysisTime[j]) {
                this.analysisTime[i].check = 'Y';
            }
            }
        }

        if (this.selectAnalysisTime.length == 0) {
            this.isAllYn = 'N';
        }

        // 왼쪽 메뉴 클릭에 의한 셋팅
        } else {

            for(let i = 0; i < this.analysisTime.length; i++) {
                this.analysisTime[i].check = 'Y';
            }

            this.dateList.push({
                formatDateRange: [
                    this.parseDateToString(this.startDate, 'YY/MM/DD'),
                    this.parseDateToString(this.endDate, 'YY/MM/DD')
                ],
                dateRange: [
                    this.parseDateToString(this.startDate, 'YYYYMMDD')+'00',
                    this.parseDateToString(this.endDate, 'YYYYMMDD')+'00'
                ],
                dateNm: [
                    this.parseDateToString(this.startDate, 'YY/MM/DD')+' 00',
                    this.parseDateToString(this.endDate, 'YY/MM/DD')+' 00'
                ]
            });
        }
    }

    ngAfterViewInit() {

        let $el = this.jQuery('div.date_picker_wrap');
        $el.find('input').datepicker({
            language: 'ko',
            autoClose: true,
            classes: 'ipm_datepicker',
            dateFormat: 'yy/mm/dd',
            navTitles: { days: 'yyyy<span>년&nbsp;</span> MM' },
            onHide: function () {},
            timepicker: false,
            toggleSelected: true,
            range: false,
            maxDate: new Date(),
            onSelect: this.selectedDatePicker
        });

        // 메뉴 필터 클릭에 의한 셋팅
        if (!this.tempDetailData) {
        this.startDate = this.getRangeDate(this.tempDetailData.fltrDtlList[0].values[0]);
        this.endDate = this.getRangeDate(this.tempDetailData.fltrDtlList[0].values[1]);
        $el.find('input:eq(0)').datepicker().data('datepicker').selectDate(new Date(this.startDate));
        $el.find('input:eq(1)').datepicker().data('datepicker').selectDate(new Date(this.endDate));
        } else {
            for(let i = 0; i < this.dateList.length; i++) {
                let $els = this.jQuery('div.date_picker_wrap').eq(i);
                let startDate = this.parseStringToDate(this.dateList[i].dateRange[0]);
                let endDate = this.parseStringToDate(this.dateList[i].dateRange[1]);

                $els.find('input:eq(0)').datepicker().data('datepicker').selectDate(new Date(startDate));
                $els.find('input:eq(1)').datepicker().data('datepicker').selectDate(new Date(endDate));

                $els = this.jQuery('div.date_picker_wrap').eq(i + 1);

                $els.find('input:eq(0)').datepicker().data('datepicker').selectDate(new Date(startDate.setDate(startDate.getDate())));
                $els.find('input:eq(1)').datepicker().data('datepicker').selectDate(new Date(endDate.setDate(endDate.getDate())));
            }
        }
    }

    public setDataSources(): Promise<any> {
        return new Promise<any>( (resolve) => {
            // 데이터소스 가져오기
            this.lpmService.getLpmLayrGrpList().then(result=>{
                if ( result.code == CommonConstant.CODE.RESULT_CODE.SUCCESS ) {
                    _.each(result.data, function(item, idx) {
                        if ( item.useYn == 'Y' ) {
                            this.layrGrpList.push({
                                druidNm: item.druidNm,
                                layrGrpNm: item.layrGrpNm,
                                layrGrpId: item.layrGrpId
                            });

                            if(item.layrGrpId == '200') {
                                this.layrGrpId = '200';
                                this.comLayrGrpId = '200';
                                this.visibleVenders = false;
                                this.visibleComVenders = false;
                            }
                        }
                    }.bind(this));

                    resolve(this.layrGrpList[0]);
                }
            });
        });
    }

    public initDateTimePicker() {
        let self: this = this;
        let $start = this.jQuery('#input-datetime-start'),
            $end   = this.jQuery('#input-datetime-end'),
            $checkStart = this.jQuery('#check-input-datetime-start'),
            $checkEnd   = this.jQuery('#check-input-datetime-end');

        $start.datepicker({
            language: 'ko',
            autoClose: true,
            // timepicker: true,
            onSelect: function(fd, date) {
                $end.data('datepicker').update('minDate', date);
                if(this.isCompCheck) {
                  $checkEnd.data('datepicker').update('minDate', _.cloneDeep(date));
                }
            }
        });

        $end.datepicker({
            language: 'ko',
            autoClose: true,
            // timepicker: true,
            onSelect: function(fd, date) {
                $start.data('datepicker').update('maxDate', date);
                if(this.isCompCheck) {
                  $checkStart.data('datepicker').update('maxDate', _.cloneDeep(date));
                }
            }
        });

        $checkStart.datepicker({
            language: 'ko',
            autoClose: true,
            // timepicker: true,
            onSelect: function(fd, date) {
                $checkStart.data('datepicker').update('minDate', date);
            }
        });

        $checkEnd.datepicker({
            language: 'ko',
            autoClose: true,
            // timepicker: true,
            onSelect: function(fd, date) {
                $checkEnd.data('datepicker').update('maxDate', date);
            }
        });
    }

    public getSelectedDateString(isStart: boolean): string {

        let dateTime = this.getSelectedDate(isStart);
        if ( dateTime ) {
            return dateTime.year + '/' + dateTime.month + '/' + dateTime.date + ' ' + dateTime.hour + '시';
        }

        return null;
    }

    public getCompSelectedDateString(isStart: boolean): string {

      let dateTime = this.getCompSelectedDate(isStart);
      if ( dateTime ) {
          return dateTime.year + '/' + dateTime.month + '/' + dateTime.date + ' ' + dateTime.hour + '시';
      }

      return null;
    }

    public getSelectedDate(isStart: boolean): any {
        let elem = ( isStart ) ? this.jQuery('#input-datetime-start') : this.jQuery('#input-datetime-end');
        let datepicker = elem.datepicker().data('datepicker');

        if ( datepicker ) {
            let selDateList = datepicker.selectedDates;

            if ( _.size(selDateList) > 0 ) {

                return {
                    year: selDateList[0].getFullYear(),
                    month: ( Number(selDateList[0].getMonth())+1 ),
                    date: selDateList[0].getDate(),
                    hour: ( isStart ) ? this.startSelectedHour : this.endSelectedHour
                }
            }
        }

        return null;
    }

    public getCompSelectedDate(isStart: boolean): any {
      let elem = ( isStart ) ? this.jQuery('#check-input-datetime-start') : this.jQuery('#check-input-datetime-end');
      let datepicker = elem.datepicker().data('datepicker');

      if ( datepicker ) {
          let selDateList = datepicker.selectedDates;

          if ( _.size(selDateList) > 0 ) {

              return {
                  year: selDateList[0].getFullYear(),
                  month: ( Number(selDateList[0].getMonth())+1 ),
                  date: selDateList[0].getDate(),
                  hour: ( isStart ) ? this.compStartSelectedHour : this.compEndSelectedHour
              }
          }
      }

      return null;
    }

    public getFilterData(): any {

        let fltrVal: Array<any> = [];

        let start = this.getSelectedDateString(true);
        let end = this.getSelectedDateString(false);
        let startCode = this.getSelectedDate(true);
        let endCode = this.getSelectedDate(false);
        let sMonth, eMonth = null;
        let sDate, eDate = null;
        let sHour, eHour = null;

        sMonth = startCode.month<10 ? '0'+ startCode.month : startCode.month.toString();
        sDate = startCode.date<10 ? '0'+ startCode.date : startCode.date.toString();
        sHour = startCode.hour<10 ? '0'+ startCode.hour : startCode.hour.toString();
        eMonth = endCode.month<10 ? '0'+ endCode.month : endCode.month.toString();
        eDate = endCode.date<10 ? '0'+ endCode.date : endCode.date.toString();
        eHour = endCode.hour<10 ? '0'+ endCode.hour : endCode.hour.toString();

        if ( start != null && end != null ) {
            fltrVal.push({
                code: startCode.year.toString() + sMonth + sDate + sHour + '~'
                    + endCode.year.toString() + eMonth + eDate + eHour,
                name: start + ' ~ ' + end
            });

            return fltrVal
        }

        Alert.warning('날짜를 선택해주시기 바랍니다.');
    }

    public getCompFilterData(): any {

        let fltrVal: Array<any> = [];

        let start = this.getCompSelectedDateString(true);
        let end = this.getCompSelectedDateString(false);
        let startCode = this.getCompSelectedDate(true);
        let endCode = this.getCompSelectedDate(false);
        let sMonth, eMonth = null;
        let sDate, eDate = null;
        let sHour, eHour = null;

        sMonth = startCode.month<10 ? '0'+ startCode.month : startCode.month.toString();
        sDate = startCode.date<10 ? '0'+ startCode.date : startCode.date.toString();
        sHour = startCode.hour<10 ? '0'+ startCode.hour : startCode.hour.toString();
        eMonth = endCode.month<10 ? '0'+ endCode.month : endCode.month.toString();
        eDate = endCode.date<10 ? '0'+ endCode.date : endCode.date.toString();
        eHour = endCode.hour<10 ? '0'+ endCode.hour : endCode.hour.toString();

        if ( start != null && end != null ) {
            fltrVal.push({
                code: startCode.year.toString() + sMonth + sDate + sHour + '~'
                + endCode.year.toString() + eMonth + eDate + eHour,
                name: start + ' ~ ' + end
            });

            return fltrVal
        }

        Alert.warning('날짜를 선택해주시기 바랍니다.');
    }

    public getFilterTime(): any {
        let fltrVal: Array<any> = [];
        let time = this.checkOnAnalysisTime();

        if ( time.length > 0 ) {
            for(let i=0; i<time.length; i++) {
                fltrVal.push({
                    code: time[i]
                });
            }

            return fltrVal
        }

        return fltrVal
    }

    public getCompFilterTime(): any {
        let fltrVal: Array<any> = [];
        let time = this.checkOnCompAnalysisTime();

        if ( time.length > 0 ) {
            for(let i=0; i<time.length; i++) {
                fltrVal.push({
                    code: time[i]
                });
            }

            return fltrVal
        }

        return fltrVal
    }

    // 선택 분석시간
    public checkOnAnalysisTime(): any {
        // 선택 분석시간 초기화
        this.selectAnalysisTime = [];

        for(let i = 0; i < this.analysisTime.length; i++) {
            if (this.analysisTime[i].check == 'Y') {
                this.selectAnalysisTime.push(this.analysisTime[i].time);
            }
        }

        return this.selectAnalysisTime;
    }

    public checkOnCompAnalysisTime(): any {
        // 선택 분석시간 초기화
        this.selectCompAnalysisTime = [];

        for(let i = 0; i < this.compAnalysisTime.length; i++) {
            if (this.compAnalysisTime[i].check == 'Y') {
                this.selectCompAnalysisTime.push(this.compAnalysisTime[i].time);
            }
        }

        return this.selectCompAnalysisTime;
    }

    public isValidation(): boolean {
        var start = this.getSelectedDate(true);
        var end = this.getSelectedDate(false);
        if ( start.year === end.year && start.month === end.month && start.date === end.date ) {
            if ( Number(this.startSelectedHour) > Number(this.endSelectedHour) ) {
                Alert.warning('동일일자 선택 시, 시작 시각보다 이전 시각은 선택하실 수 없습니다.');
                return false;
            }
        }

        return true;
    }

    private updateEndHour(): void {
        let self: this = this;
        if ( !this.isValidation() ) {
            this.endSelectedHour = Number(this.startSelectedHour)+1;
        }
    }

    public onChangeHour(isStart: boolean, hour: number): void {
        if ( isStart ) {

            this.startSelectedHour = hour;
            if(this.isCompCheck) {
              let $els = this.jQuery('div.date_picker_wrap');
              $els.find('select').eq(2).val($els.find('select').eq(0).val());
              this.onCompChangeHour(isStart, _.cloneDeep(hour));
            }
        } else {
            this.endSelectedHour = hour;
            if(this.isCompCheck) {
              let $els = this.jQuery('div.date_picker_wrap');
              $els.find('select').eq(3).val($els.find('select').eq(1).val());
              this.onCompChangeHour(isStart, _.cloneDeep(hour));
            }
        }

        this.updateEndHour();
    }

    public onCompChangeHour(isStart: boolean, hour: number): void {
        if ( isStart )
            this.compStartSelectedHour = hour;
        else
            this.compEndSelectedHour = hour;

        this.compUpdateEndHour();
    }

    private compUpdateEndHour(): void {
        let self: this = this;
        if ( !this.isCompValidation() ) {
            this.compEndSelectedHour = Number(this.compStartSelectedHour)+1;
        }
    }

    public isCompValidation(): boolean {
        var start = this.getCompSelectedDate(true);
        var end = this.getCompSelectedDate(false);
        if ( start.year === end.year && start.month === end.month && start.date === end.date ) {
            if ( Number(this.compStartSelectedHour) > Number(this.compEndSelectedHour) ) {
                Alert.warning('동일일자 선택 시, 시작 시각보다 이전 시각은 선택하실 수 없습니다.');
                return false;
            }
        }

        return true;
    }

    /**
    * 시군구 펼치기 이벤트
    * @param e ($event 객체 - 시군구 데이터)
    */
    public showSsg(e: any) {
        // 이벤트 버블링 방지
        e.stopPropagation();

        this.amdData = [];
        this.isSggSelect = false;
        this.isSidoSelect = false;

        this.currentSidoData = {
            name : e.target.textContent,
            code : e.target.getAttribute('code')
        }


        this.addressService.getAddress(this.currentSidoData.code)
        .then(result => {
            if (result.code === CommonConstant.CODE.RESULT_CODE.SUCCESS) {
                this.sggData = result.data;
                this.isSidoSelect = true;
            }
        });

        this.showCheck(this.sidoData, this.currentSidoData.code);
    }

    /**
    * 읍면동 펼치기 이벤트
    * @param e ($event 객체 - 읍면동 데이터)
    */
    public showAmd(e: any) {

        // 이벤트 버블링 방지
        e.stopPropagation();

        this.currentSggData = {
            name : e.target.textContent,
            code : e.target.getAttribute('code')
        }

        this.addressService.getAddress(this.currentSidoData.code, this.currentSggData.code)
        .then(result => {
            if (result.code === CommonConstant.CODE.RESULT_CODE.SUCCESS) {
                this.amdData = result.data;
                this.isSggSelect = true;
            }
        });

        this.showCheck(this.sggData, this.currentSggData.code);
    }

    /**
    * 시도 추가 이벤트
    * @param e ($event 객체 - 선택 시도 데이터)
    */
    public selectSido(e: any) {
        this.selectAddrNameData = [e.target.nextElementSibling.textContent];
        this.selectAddrCodeData = [e.target.getAttribute('code')];

        //추가 가능 확인
        this.checkSelectAddressValid(this.selectAddrCodeData, this.selectAddrNameData);
    }

    /**
    * 시군구 추가 이벤트
    * @param e ($event 객체 - 선택 시군구 데이터)
    */
    public selectSgg(e: any) {
        this.selectAddrNameData = [this.currentSidoData.name, e.target.nextElementSibling.textContent];
        this.selectAddrCodeData = [this.currentSidoData.code, e.target.getAttribute('code')];

        //추가 가능 확인
        this.checkSelectAddressValid(this.selectAddrCodeData, this.selectAddrNameData);
    }

    /**
    * 읍면동 추가 이벤트
    * @param e ($event 객체 - 선택 읍면동 데이터)
    */
    public selectAmd(e: any) {
        this.selectAddrNameData = [this.currentSidoData.name, this.currentSggData.name, e.target.nextElementSibling.textContent];
        this.selectAddrCodeData = [this.currentSidoData.code, this.currentSggData.code, e.target.getAttribute('code')];

        //추가 가능 확인
        this.checkSelectAddressValid(this.selectAddrCodeData, this.selectAddrNameData);
    }

    /**
    * 추가한 주소 삭제
    * @param i (선택 추가 주소 index)
    */
    public removeAddress(i: number) {
        this.selectAllAddrCodeData = Utils.ArrayUtil.remove(this.selectAllAddrCodeData, i);
        this.selectAllAddrNameData = Utils.ArrayUtil.remove(this.selectAllAddrNameData, i);
    }

    /**
    * 추가 주소 선택가능 검사
    */
    public checkSelectAddressValid(selectCodeData: Array<any>, selectNameData: Array<any>) {

        if ( this.selectAllAddrCodeData[0] && this.selectAllAddrCodeData[0].join('') == selectCodeData.join('') ) {
            Alert.warning( '이미 포함 된 주소입니다.');
            return;
        }

        this.selectAllAddrCodeData[0] = selectCodeData;
        this.selectAllAddrNameData[0] = selectNameData;

        // TODO: 다른 지역 선택 시 계속 주소가 push 되는현상 수정
        let center: any = window.LPM_COMPONENT.centerComponent.childComponent;
        center.updateAddrNameList(selectNameData);
    }

    /**
    * 선택된 주소 표시
    *
    * @param addrData(현재 주소 목록)
    * @param code(선택된 시도/시군구 코드)
    */
    public showCheck(addrData: any, code: String) {
        for(let i = 0; addrData.length > i; i++) {
            if (addrData[i].code == code) {
                addrData[i].show = true;
            } else {
                addrData[i].show = false;
            }
        }
    }

    /**
    * datepicker onSelect callback function
    *
    * @param fd (format date 정보 (yy/MM/dd))
    * @param d (date 정보)
    * @param inst (datepicker instance)
    */
    public selectedDatePicker: Function = function(fd, d, inst) {
        if (fd && d && inst) {
            let year = d.getFullYear();
            let month =  fd.split('/')[1];
            let day = '' + fd.split('/')[2];

            let date = year + month + day;
            inst.el.setAttribute('data-date', date);

            if($('#skt_holiday').is(':checked')) {
                if(inst.el.id === 'input-datetime-start') {
                    $('#check-input-datetime-start').data('datepicker').selectDate(_.cloneDeep(inst.lastSelectedDate));
                    $('#check-input-datetime-start').attr('data-date', _.cloneDeep(date));
                } else if(inst.el.id === 'input-datetime-end') {
                    $('#check-input-datetime-end').data('datepicker').selectDate(_.cloneDeep(inst.lastSelectedDate));
                    $('#check-input-datetime-end').attr('data-date', _.cloneDeep(date));
                }
            }
        }
    }

    /**
    * 지정 날짜 범위로 date 값 변환
    *
    * @param dateRange (yyyyMMdd 포멧의 start/end date)
    */
    private getRangeDate(dateRange: string): Date {
        let date = new Date();
        date.setDate(date.getDate() + Number(dateRange));

        return date;
    }

    /**
    * 'yyyyMMdd' 형태의 string을 date로 반환
    *
    * @param date (yyyyMMdd 포멧의 string data)
    */
    private parseStringToDate(date: string): Date {
        let ret = null;
        if (date) {
            let year = Number(date.substr(0, 4));
            let month = Number(date.substr(4, 2)) - 1;
            let day = Number(date.substr(6, 2));
            ret = new Date(year, month, day);
        }

        return ret;
    }

    /**
    * Date를 format string으로 반환
    */
    private parseDateToString(date: Date, format: string): string {
        let ret = '';
        if (date) {
            ret = moment(date).format(format);
        }
        return ret;
    }

    /**
    * 'yyyyMMdd' 형태의 string을 yy/MM/dd로 포멧팅
    *
    * @param date (yyyyMMdd 포멧의 string data)
    */
    private formattingDate(date: string): string {
        let ret = [];
        if (date) {
            ret.push(date.substr(2, 2));
            ret.push(date.substr(4, 2));
            ret.push(date.substr(6, 2));
        }

        return ret.join('/');
    }

    /**
    * 분석시간 선택 / 해제
    *
    * @param item (선택 시간 데이터)
    */
    public checkAnalysisTime(item) {
        if (item.check == 'Y') {
            item.check = 'N';
        } else {
            item.check = 'Y';
        }

        if(this.isCompCheck) {
            this.compAnalysisTime = _.cloneDeep(this.analysisTime);
        }
    }

    /**
    * 분석시간 선택 / 해제
    *
    * @param item (선택 시간 데이터)
    */
    public compCheckAnalysisTime(item) {
        if (item.check == 'Y') {
            item.check = 'N';
        } else {
            item.check = 'Y';
        }
    }

    /**
    * 모든 분석시간 선택 / 해제
    *
    * @param e (전체선택 버튼 이벤트)
    */
    public checkAllAnalysisTime(e) {

        if (this.isAllYn == 'N') {
            for(let i = 0; i < this.analysisTime.length; i++) {
                this.analysisTime[i].check = 'Y';
                this.isAllYn = 'Y';
            }
        } else {
            for(let i = 0; i < this.analysisTime.length; i++) {
                this.analysisTime[i].check = 'N';
                this.isAllYn = 'N';
            }
        }

        if(this.isCompCheck) {
            this.compAnalysisTime = _.cloneDeep(this.analysisTime);
        }
    }

    /**
    * 모든 분석시간 선택 / 해제
    *
    * @param e (전체선택 버튼 이벤트)
    */
    public compCheckAllAnalysisTime(e) {

        if (this.compIsAllYn == 'N') {
            for(let i = 0; i < this.compAnalysisTime.length; i++) {
                this.compAnalysisTime[i].check = 'Y';
                this.compIsAllYn = 'Y';
            }
        } else {
            for(let i = 0; i < this.compAnalysisTime.length; i++) {
                this.compAnalysisTime[i].check = 'N';
                this.compIsAllYn = 'N';
            }
        }
    }

    // 최한시 분석시간 제외
    public removeLimitAnalysisTime() {
        for(let i = 0; i < this.analysisTime.length; i++) {
            for(let j = 0; j < this.limitAnalysisTime.length; j++) {
                if ( this.analysisTime[i].time == this.limitAnalysisTime[j]) {
                    this.analysisTime[i].check = 'N';
                }
            }
        }

        if(this.isCompCheck) {
            this.compAnalysisTime = _.cloneDeep(this.analysisTime);
        }
    }

    // 최한시 분석시간 제외
    public compRemoveLimitAnalysisTime() {

        for(let i = 0; i < this.compAnalysisTime.length; i++) {
            for(let j = 0; j < this.limitAnalysisTime.length; j++) {
                if ( this.compAnalysisTime[i].time == this.limitAnalysisTime[j]) {
                    this.compAnalysisTime[i].check = 'N';
                }
            }
        }
    }

    // 비교군 대조군 동일화
    public checkEqual(checked : any) {

        if(!this.usedHour && this.compUsedHour) {
            checked.target.checked = false;
            this.isCompCheck = false;
            Alert.warning('대조군이 xDR일 경우, 다른 데이터셋과 동일선택 할 수 없습니다.');
            return;
        }

        let self: this = this;
        let $els = this.jQuery('div.date_picker_wrap'),
        $start = this.jQuery('#input-datetime-start'),
        $end   = this.jQuery('#input-datetime-end');

        if($('#skt_holiday').is(':checked')) {
            //날짜
            $els.find('input').eq(2).datepicker().data('datepicker').selectDate($start.data('datepicker').lastSelectedDate);
            $els.find('input').eq(3).datepicker().data('datepicker').selectDate($end.data('datepicker').lastSelectedDate);

            //시간
            $els.find('select').eq(2).val($els.find('select').eq(0).val());
            $els.find('select').eq(3).val($els.find('select').eq(1).val());

            //시간대
            this.compAnalysisTime = _.cloneDeep(this.analysisTime);

            this.compStartSelectedHour = this.startSelectedHour;
            this.compEndSelectedHour = this.endSelectedHour;

            this.isCompCheck = true;
        } else {
            this.isCompCheck = false;
        }
    }

    // 필터 데이터 전송
    public submitRightKind(isLeft: boolean, isAll: boolean, isFilter: boolean): void {

        if(this.getCompSelectedDateString(true) == null && this.getCompSelectedDateString(false) == null) {
            Alert.warning('대조군 연월일시를 입력해주세요.');
            return;
        }

        this.filterList = [];
        this.filterCompList = [];

        let center: any = window.LPM_COMPONENT.centerComponent.childComponent;
        let druidNms: any = center.getDruidNms(isLeft, isAll);

        if ( druidNms ) {
            let isChangeDate = true;

            if (isLeft === null) {
                if ( !this.validationDate(druidNms, false, isAll) ) isChangeDate = false;
                if ( !this.validationDate(druidNms, true, isAll) ) isChangeDate = false;
            } else {
                if ( !this.validationDate(druidNms, !isLeft, isAll) ) isChangeDate = false;
            }

            this.setFilterList({
                filterList: this.filterList,
                druidNms: druidNms.left,
                isLeft: true
            });

            this.setFilterList({
                filterList: this.filterCompList,
                druidNms: druidNms.right,
                isLeft: false
            });

            center.setCustomUserFilters({
                filterList: this.filterList,
                venderId: this.venderId,
                filterCompList: this.filterCompList,
                compVenderId: this.compVenderId,
                isLeft: isLeft,
                isAll: isAll,
                isFilter: isFilter,
                isChangeDate: isChangeDate
            });
        }
    }

    private setFilterList(params: any): void {
        _.each(params.druidNms, function (druidNm, idx) {
            params.filterList[idx] = {
                druidNm: druidNm,
                filters: []
            };

            if ( _.size(this.selectAddrCodeData) > 0 ) {
                let addrFilter = this.getAddrFilter(druidNm);
                params.filterList[idx].filters.push(addrFilter);
            }

            params.filterList[idx].filters.push(this.getDtFilter(druidNm, params.isLeft));
        }.bind(this));
    }

    private validationDate(druidNms: any, isCompare: boolean, isAll: boolean): any {
        let result = true;
        let isOpenedWarning = false;
        let beforeDruidNms = isCompare ? this._beforeCompareDruidNms : this._beforeDruidNms ;
        druidNms = isCompare ? druidNms.right : druidNms.left ;

        this.removeDifferenceIgnoreWarningDruidNms(druidNms, isCompare);

        if (druidNms.length == 0) {
            result = true;
        } else if ( druidNms.length < beforeDruidNms.length ) {
            result = false;
        } else if ( _.every(druidNms, function (druidNm: string, idx): boolean { return druidNm == beforeDruidNms[idx]; }) ) {
            if ( isAll )
                result = true;
            else
                result = false;
        } else {
            let interval = this.getInterval(isCompare);
            let center: any = window.LPM_COMPONENT.centerComponent.childComponent;

            let ignoreWarningDruidNms = isCompare ? this._ignoreCompareWarningDruidNms : this._ignoreWarningDruidNms ;

            _.each(druidNms, function (druidNm: string, idx: number): void {
                let layerObject = _.find(isCompare ? center.comparisionLayerList : center.constrastLayerList, function (layerObject: any): boolean {
                    return layerObject.name == druidNm;
                });

                let isIgnoreWarning = _.some(ignoreWarningDruidNms, function (ignoreWarningDruidNm: string): boolean {
                    return ignoreWarningDruidNm == druidNm;
                });

                if ( layerObject ) {
                    if (layerObject.layrGrpId == '200' && interval > 1) {
                        if ( ( !isOpenedWarning && !isIgnoreWarning ) || isAll === true ) {
                            Alert.warning((isCompare ? '대조군' : '비교군') + '을 1시간 간격으로 선택해주시기 바랍니다.');
                            if ( isAll === null ) {
                                ignoreWarningDruidNms.push(druidNm);
                                result = false;
                            }
                        }

                        druidNms.splice(idx, 1);
                    } else if((layerObject.layrGrpId == '300' || layerObject.layrGrpId == '400') && interval > 24 ) {
                        if ( ( !isOpenedWarning && !isIgnoreWarning ) || isAll === true ) {
                            Alert.warning((isCompare ? '대조군' : '비교군') + '을 24시간 간격으로 선택해주시기 바랍니다.');
                            if ( isAll === null ) {
                                ignoreWarningDruidNms.push(druidNm);
                                result = false;
                            }
                        }

                        druidNms.splice(idx, 1);
                    }
                }
            });
        }

        if ( isCompare )
            this._beforeCompareDruidNms = druidNms;
        else
            this._beforeDruidNms = druidNms;

        return result;
    }

    private removeDifferenceIgnoreWarningDruidNms(druidNms: any, isCompare: boolean): void {
        let ignoreWarningDruidNms = isCompare ? this._ignoreCompareWarningDruidNms : this._ignoreWarningDruidNms ;

        _.each(_.difference(ignoreWarningDruidNms, druidNms), function (druidNm) {
            ignoreWarningDruidNms.splice(ignoreWarningDruidNms.indexOf(druidNm), 1);
        }.bind(this));
    }

    private getInterval(isCompare: boolean): number {
        let startDate: any = isCompare ? this.getCompSelectedDate(true) : this.getSelectedDate(true) ;
        startDate = new Date(startDate.year, startDate.month-1, startDate.date, startDate.hour);

        let endDate: any = isCompare ? this.getCompSelectedDate(false) : this.getSelectedDate(false) ;
        endDate = new Date(endDate.year, endDate.month-1, endDate.date, endDate.hour);

        return (((endDate.getTime() - startDate.getTime()) / 1000) / 60) / 60;
    }

    private getAddrFilter(druidNm: string):any {
        return {
            druidNm: druidNm,
            scrnClNm: 'address',
            fltrVal: [
                { code: this.selectAddrCodeData, name: this.selectAddrNameData }
            ],
            dtNm: this.selectAddrNameData[this.selectAddrNameData.length-1],
            fltrNm: 'ldong_cd'
        }
    }

    public getDtFilter(druidNm: string, isLeft: boolean):any {
        return {
            druidNm: druidNm,
            scrnClNm: 'datetime',
            fltrVal: isLeft ? this.getFilterData() : this.getCompFilterData(),
            dtNm: isLeft ? this.getFilterData()[0].name : this.getCompFilterData()[0].name,
            fltrNm: 'occr_dtm'
        }
    }

    public onChangeLayrGrp(layrGrpId: string, isCompare: boolean) {
        // layrGrpId 기준 통신사 선택 표시
        this.setVisibleVenders(layrGrpId, isCompare);

        if ( isCompare ) {
            this.comLayrGrpId = layrGrpId;
            if(layrGrpId == '200') {
                this.compStartSelectedHour = 9;
                this.compEndSelectedHour = 10;
                this.compUsedHour = true;
            } else {
                this.compUsedHour = false;
            }
        } else {
            this.layrGrpId = layrGrpId;
            if($('#skt_holiday').is(':checked')) {
                $('#skt_holiday').prop('checked', false);
                this.isCompCheck = false;
            }
            if(layrGrpId == '200') {
                this.startSelectedHour = 9;
                this.endSelectedHour = 10;
                this.usedHour = true;
            } else {
                this.usedHour = false;
            }
        }

        let center: any = window.LPM_COMPONENT.centerComponent.childComponent;
        if ( center.setLayerList ) {
            center.setLayerList(layrGrpId, isCompare);
            if ( isCompare )
                this._ignoreCompareWarningDruidNms = [];
            else
                this._ignoreWarningDruidNms = [];
        }
    }

    public setVisibleVenders(layrGrpId: string, isCompare: boolean) {
        let isVisible = false;

        switch (layrGrpId) {
            case '300':
            case '400':
                isVisible = true;
                break;
        }

        if ( isCompare )
            this.visibleComVenders = isVisible;
        else
            this.visibleVenders = isVisible;
    }

    public setVenderId(venderId: string, isCompare: boolean) {
        if ( isCompare )
            this.compVenderId = venderId;
        else
            this.venderId = venderId;
    }
}
