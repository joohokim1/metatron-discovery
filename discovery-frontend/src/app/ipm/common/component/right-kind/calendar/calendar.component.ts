import { Component, OnInit, ElementRef, Injector, Input, AfterViewInit } from '@angular/core';
import { AbstractComponent } from '../../../../../common/component/abstract.component';
import { RightKindInterface } from '../right-kind.component';
import { Alert } from '../../../../../common/util/alert.util';
import * as moment from 'moment';
import * as _ from 'lodash';

declare const $: any;

@Component({
  selector: 'calendar',
  templateUrl: `./calendar.component.html`,
  styles: []
})
export class CalendarComponent extends AbstractComponent implements OnInit, AfterViewInit, RightKindInterface {

  // 전달받은 데이터
  @Input()
  public menuDetailData: any;

  // date 배열
  public dateList: Array<any> = [];

  // 초기값
  public startDate: string;
  public endDate: string;

  protected jQuery = $;

  // 생성자
  constructor(
    protected elementRef: ElementRef,
    protected injector: Injector
  ) {
    super(elementRef, injector);
  }

  ngOnInit() {

    let values = this.menuDetailData.fltrDtlList[0].values;
    if (values && values.length) {
      this.startDate = moment(moment().toDate()).add(Number(values[0]), 'days').format('YYYYMMDD');
      this.endDate = moment(moment().toDate()).add(Number(values[1]), 'days').format('YYYYMMDD');
    } else {
      this.startDate = moment(moment().toDate()).add(-10, 'days').format('YYYYMMDD');
      this.endDate = moment(moment().toDate()).format('YYYYMMDD');
    }

    // 선택 필터 클릭에 의한 셋팅
    if (this.menuDetailData.fltrVal) {
      let fltrVal = _.cloneDeep(this.menuDetailData.fltrVal);

      for (let i = 0; i < fltrVal.length; i++) {

        let dateRange = fltrVal[i].code.split('~');
        this.dateList.push({
          formatDateRange: [
            this.formattingDate(dateRange[0]),
            this.formattingDate(dateRange[1])
          ],
          dateRange: [dateRange[0], dateRange[1]]
        });

      }

    // 왼쪽 메뉴 클릭에 의한 셋팅
    } else {
      this.dateList.push({
        formatDateRange: [
          this.formattingDate(this.startDate),
          this.formattingDate(this.endDate)
        ],
        dateRange: [this.startDate, this.endDate]
      });
    }

  }

  ngAfterViewInit() {

    let $el = this.jQuery('div.date_picker_wrap.calendar');
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
      let startDate = this.parseStringToDate(this.startDate, 'YYYYMMDD');
      let endDate = this.parseStringToDate(this.endDate, 'YYYYMMDD');
      $el.find('input:eq(0)').datepicker().data('datepicker').selectDate(startDate);
      $el.find('input:eq(1)').datepicker().data('datepicker').selectDate(endDate);
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
        dtNmArr.push(item.formatDateRange.join('~'));

        // {"code":"yyyyMMdd~yyyyMMdd"} 형태로 셋팅
        fltrVal.push({
          code: item.dateRange.join('~')
        });
      } else {
        // 입력값이 한가지라도 없을 경우
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
        this.formattingDate(this.startDate),
        this.formattingDate(this.endDate)
      ],
      dateRange: [this.startDate, this.endDate]
    });

    let startDate = this.parseStringToDate(this.startDate, 'YYYYMMDD');
    let endDate = this.parseStringToDate(this.endDate, 'YYYYMMDD');
    let selectedDatePicker = this.selectedDatePicker;

    setTimeout(function() {
      let $el = this.jQuery('div.date_picker_wrap.calendar:first');
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
    // 시작 or 종료일 셋팅
    item.formatDateRange[i] = formatDate;
    item.dateRange[i] = date;

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
   * UTC Date를 'yyyyMMdd' string으로 반환
   */
  public parseDateToString(date: Date): string {
    let ret ='';
    if (date) {
      let year = date.getFullYear();
      let month = (date.getMonth() + 1);
      let day = date.getDate();

      ret = year + this.lpad(month, 2, '0') + this.lpad(day, 2, '0');
    }
    return ret;
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
   * 지정한 length만큼 특정문자를 왼쪽에 덧붙이는 함수
   *
   * @param number (값)
   * @param length (자리 수)
   * @param addStr (덧붙일 문자)
   */
  public lpad(number, length, addStr): string {
    var str = '' + number;
    while (str.length < length) {
      str = addStr + str;
    }
    return str;
  }

  /**
   * input 데이터로 달력 변경
   *
   * @param e (캘린더 텍스트박스 객체)
   * @param i (텍스트박스 순서)
   * @param j (input box 시작 종료 구분 - 0:시작, 1:종료)
   */
  public changeInputDate(e, i, j){
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

      let $el = this.jQuery('.'+e.target.parentElement.classList[1]+'.'+e.target.parentElement.classList[2]+':eq('+ i +')');
      $el.find('input').datepicker().data('datepicker').selectDate(changeDate);
    } else {
      e.target.value = '';
      e.target.removeAttribute('data-date');
      this.dateList[i].dateRange[j] = '';
      this.dateList[i].formatDateRange[j] = '';
    }
  }

}
