import { Component, OnInit, ElementRef, Injector, Input, AfterViewInit} from "@angular/core";
import { AbstractComponent } from '../../../../../common/component/abstract.component';
import { RightKindInterface } from '../right-kind.component';
import { Alert } from '../../../../../common/util/alert.util';
import * as moment from 'moment';
import * as _ from 'lodash';

declare const $: any;

@Component({
  selector: 'ymdh',
  templateUrl: './ymdh.component.html',
  styles: []
})
export class YmdhComponent extends AbstractComponent implements OnInit, AfterViewInit, RightKindInterface {

  // 전달받은 데이터
  @Input()
  public menuDetailData: any;

  // date 배열
  public dateList: Array<any> = [];

  // 날짜시간 데이터
  public dateTime: Array<string> = [];

  // 시작 종료 데이터
  public startDate: Date;
  public endDate: Date;

  protected jQuery = $;

  // 생성자
  constructor(
    protected elementRef: ElementRef,
    protected injector: Injector
  ) {
      super(elementRef, injector);
  }

  ngOnInit() {

    // 기본 데이터
    if (this.menuDetailData.fltrDtlList[0].values != null) {
      if (this.menuDetailData.fltrDtlList[0].values.length) {
        this.startDate = this.getRangeDate(this.menuDetailData.fltrDtlList[0].values[0]);
        this.endDate = this.getRangeDate(this.menuDetailData.fltrDtlList[0].values[1]);
      } else {
        this.startDate = this.getRangeDate('-1');
        this.endDate = this.getRangeDate('-1');
      }
    }

    // 분석시간 데이터
    let oneTime = Array(10).fill(0).map((x, i) => '0'+i);
    let twoTime = Array(14).fill(0).map((x, i) => ''+(Number(i)+10));
    this.dateTime = oneTime.concat(twoTime);

    // 선택 필터 클릭에 의한 셋팅
    if (this.menuDetailData.fltrVal) {

      let fltrVal = _.cloneDeep(this.menuDetailData.fltrVal);

      for (let i = 0; i < fltrVal.length; i++) {

        let dateRange = fltrVal[i].code.split('~');
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

    // 기본 날짜 셋팅
    } else {

      this.dateList.push({
        formatDateRange: [
          this.parseDateToString(this.startDate, 'YY/MM/DD'),
          this.parseDateToString(this.endDate, 'YY/MM/DD')
        ],
        dateRange: [
          this.parseDateToString(this.startDate, 'YYYYMMDD')+'00',
          this.parseDateToString(this.endDate, 'YYYYMMDD')+'23'
        ],
        dateNm: [
          this.parseDateToString(this.startDate, 'YY/MM/DD')+' 00',
          this.parseDateToString(this.endDate, 'YY/MM/DD')+' 23'
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
      keyboardNav: false,
      maxDate: moment().toDate(),
      onSelect: this.selectedDatePicker
    });

    // 메뉴 필터 클릭에 의한 셋팅
    if (!this.menuDetailData.fltrVal) {
      this.startDate = this.getRangeDate(this.menuDetailData.fltrDtlList[0].values[0]);
      this.endDate = this.getRangeDate(this.menuDetailData.fltrDtlList[0].values[1]);
      $el.find('input:eq(0)').datepicker().data('datepicker').selectDate(this.startDate);
      $el.find('input:eq(1)').datepicker().data('datepicker').selectDate(this.endDate);
    } else {
      for (let i = 0; i < this.dateList.length; i++) {
        let $els = this.jQuery('div.date_picker_wrap').eq(i);
        let startDate = this.parseStringToDate(this.dateList[i].dateRange[0], 'YYYYMMDD');
        let endDate = this.parseStringToDate(this.dateList[i].dateRange[1], 'YYYYMMDD');
        $els.find('input:eq(0)').datepicker().data('datepicker').selectDate(startDate);
        $els.find('input:eq(1)').datepicker().data('datepicker').selectDate(endDate);
      }
    }
  }

  /**
   * Override Method
   * 필터조건에 사용되는 데이터
   *
   * @return 필터 조건 Object
   */
  public getFilterData(): any {

    // return Data
    let dtNmArr = [];
    let fltrVal = [];

    for (let item of this.dateList) {
      if ((item.dateRange[0] && item.formatDateRange[0] != '') && (item.dateRange[1] && item.formatDateRange[1] != '')) {
        dtNmArr.push(item.dateNm.join('~'));

        // {"code":"yyyyMMddhh~yyyyMMddhh"} 형태로 셋팅
        fltrVal.push({
          code: item.dateRange.join('~')
        });
      } else {
        return {
          dtNm: '',
          fltrVal: []
        };
      }
    }

    return {
      dtNm: dtNmArr.join(', '),
      fltrVal: fltrVal
    };
  }

  /**
   * date picker select 이벤트
   *
   * @param e ($event 객체)
   * @param item (date object)
   * @param i (2개 input box 구분 (0: 시작일, 1: 종료일))
   */
  public selectDate(e: any, item: any, i: number): void {

    // yy/MM/dd 날짜
    let formatDate: string = e.target.value;

    // yyyyMMdd 날짜
    let date: string = e.target.getAttribute('data-date');
    if (!date) {
      return;
    }

    // date형태로 파싱된 date
    let parseDate = this.parseStringToDate(date, 'YYYYMMDD');

    // 시작 input box 입력
    if (i === 0) {

      // 종료일이 존재하는 경우
      if (item.dateRange[1]) {
        let endDate = this.parseStringToDate(item.dateRange[1], 'YYYYMMDD');

        if (parseDate.getTime() > endDate.getTime()) {
          Alert.error('시작일은 종료일을 넘어설 수 없습니다.');
          e.target.value = '';
          e.target.removeAttribute('data-date');
          item.dateRange[i] = '';
          item.formatDateRange[i] = '';
          return;
        }
      }

    // 끝 input box 입력
    } else if (i === 1) {

      // 시작일이 존재하는 경우
      if (item.dateRange[0]) {
        let startDate = this.parseStringToDate(item.dateRange[0], 'YYYYMMDD');

        if (startDate.getTime() > parseDate.getTime()) {
          Alert.error('종료일은 시작일보다 빠를 수 없습니다.');
          e.target.value = '';
          e.target.removeAttribute('data-date');
          item.dateRange[i] = '';
          item.formatDateRange[i] = '';
          return;
        }
      }
    }

    // 기존 선택 시간
    let currentTime = item.dateRange[i].substring(8,10);

    // 시작 or 종료일 셋팅
    item.formatDateRange[i] = formatDate;
    item.dateRange[i] = date + currentTime;
    item.dateNm[i] = item.formatDateRange[i]+' '+currentTime;
  }

  /**
   * date 추가
   */
  public addDate(): void {

    // 이전 date 입력값 검사
    for (let item of this.dateList) {
      if (!item.dateRange[0] || !item.dateRange[1]) {
        Alert.error('입력되지 않은 필터값이 있습니다.');
        return;
      }
    }

    // date 추가
    this.dateList.splice(0, 0, {
      formatDateRange: [
        this.parseDateToString(this.startDate, 'YY/MM/DD'),
        this.parseDateToString(this.endDate, 'YY/MM/DD')
      ],
      dateRange: [
        this.parseDateToString(this.startDate, 'YYYYMMDD')+'00',
        this.parseDateToString(this.endDate, 'YYYYMMDD')+'23'
      ],
      dateNm: [
        this.parseDateToString(this.startDate, 'YY/MM/DD')+' 00',
        this.parseDateToString(this.endDate, 'YY/MM/DD')+' 23'
      ]
    });

    let selectedDatePicker = this.selectedDatePicker;
    let startDate = this.startDate;
    let endDate = this.endDate;

    setTimeout(function() {
      let $el = this.jQuery('div.date_picker_wrap:first');
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
        keyboardNav: false,
        maxDate: moment().toDate(),
        onSelect: selectedDatePicker
      });

      $el.find('input:eq(0)').datepicker().data('datepicker').selectDate(startDate);
      $el.find('input:eq(1)').datepicker().data('datepicker').selectDate(endDate);

    }, 300);
  }

  /**
   * date 제거
   *
   * @param i (date 인덱스)
   */
  public deleteDate(i): void {
    this.dateList.splice(i, 1);
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
    }
  }

  /**
   * 지정 날짜 범위로 date 값 변환
   *
   * @param dateRange (yyyyMMdd 포멧의 start/end date)
   */
  public getRangeDate(dateRange: string): Date {
    let date = moment().toDate();
    date.setDate(date.getDate() + Number(dateRange));
    return date;
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
   * Date를 format string으로 반환
   */
  public parseDateToString(date: Date, format: string): string {
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
   * 시작 시간 변경
   *
   * @param e (선택 selectBox 이벤트)
   * @param item (선택 date 데이터)
   */
  public selectStartTime(e, item): void {
    // 현재 시간 데이터 적용
    item.dateRange[0] = item.dateRange[0].substring(0,8)+e.target.value;
    item.dateNm[0] = item.formatDateRange[0].substring(0,8)+' '+e.target.value;
  }

  /**
   * 종료 시간 변경
   *
   * @param e (선택 selectBox 이벤트)
   * @param item (선택 date 데이터)
   */
  public selectEndTime(e, item): void {
    // 현재 시간 데이터 적용
    item.dateRange[1] = item.dateRange[1].substring(0,8)+e.target.value;
    item.dateNm[1] = item.formatDateRange[1].substring(0,8)+' '+e.target.value;
  }


  /**
   * input 데이터로 달력 변경
   *
   * @param e (캘린더 텍스트박스 이벤트)
   * @param i (텍스트박스 인덱스)
   * @param j (input box의 시작, 종료 구분 - 0:시작, 1:종료)
   */
  public changeInputDate(e, i, j): void {
    // 변경 텍스트 데이터
    let value = e.target.value;

    // 텍스트 데이터 날짜 유효 판단
    if (moment(value, ['YY/MM/DD'], true).isValid()) {
      let changeDate = this.parseStringToDate(value, 'YY/MM/DD');

      // 최대 날짜 체크
      if (moment().toDate().getTime() < changeDate.getTime()) {
        Alert.error('선택 날짜가 너무 큽니다.');
        e.target.value = '';
        return;
      }

      let $el = this.jQuery('.'+e.target.parentElement.classList[1]+':eq('+ i +')');
      $el.find('input').datepicker().data('datepicker').selectDate(changeDate);
    } else {
      e.target.value = '';
      e.target.removeAttribute('data-date');
      this.dateList[i].dateRange[j] = '';
      this.dateList[i].formatDateRange[j] = '';
    }
  }
}
