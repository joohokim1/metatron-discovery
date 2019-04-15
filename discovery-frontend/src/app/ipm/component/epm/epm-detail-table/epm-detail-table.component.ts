import { Component, OnInit, OnChanges, ElementRef, Injector, Input, Output, EventEmitter } from '@angular/core';
import { AbstractComponent } from '../../../../common/component/abstract.component';
import { Utils } from '../../../common/util/utils';
import * as _ from 'lodash';

@Component({
  selector: 'epm-detail-table',
  templateUrl: './epm-detail-table.component.html',
  styles: []
})
export class EpmDetailTableComponent extends AbstractComponent implements OnInit, OnChanges {

  // 장비 1시간 1일 데이터
  @Input()
  public epmDetailData: any;

  @Output()
  public onAddEpmDetailData = new EventEmitter<any>();

  // 기간별 데이터
  public list: Array<any> = [];

  // 기간별 헤더 데이터
  public header: any;

  // 장비 카운트
  public cnt: number = 0;

  // summary 데이터
  public smryList: Array<any> = [];

  // summary 헤더 데이터
  public smryHeader: Array<any> = [];

  // 목록 확대 유무
  public isDetailExpansion: boolean = false;

  // Excel 다운로드 진행 여부
  public isDownloadExcel: boolean = false;

  // 단위 체크
  public dth: string = 'hour';

  // 장비 더보기 카운트
  public addCnt: number = 1;

  // 더보기 버튼 문자 데이터
  public showDataDisplay: string = '';

  // 더보기 최대값 카운트
  public maxDataDisplay: number = 0;

  // exceldownload eventEmitter
  @Output()
  public onDownloadExcel = new EventEmitter<any>();

  // 생성자
  constructor(
    protected elementRef: ElementRef,
    protected injector: Injector
  ) {
    super(elementRef, injector);
  }

  ngOnInit() {
    // 초기화
    this.list = [];
    this.header = [];
    this.cnt = 0;
  }

  ngOnChanges() {

    // 새로 선택했을 경우와 추가 구분
    if (this.epmDetailData.smryList != null && this.epmDetailData.smryHeader != null) {

      this.list = this.epmDetailData.list;
      this.header = this.epmDetailData.header;
      this.cnt = this.epmDetailData.cnt;
      this.smryList = this.epmDetailData.smryList;
      this.smryHeader = this.epmDetailData.smryHeader;

      // 더보기 버튼 선택 수 초기화
      this.addCnt = 1;
      // 최대 버튼 표시 데이터 셋팅
      this.maxDataDisplay = Math.ceil(this.cnt / 10);
      // 초기 버튼 표시 데이터 셋팅
      this.showDataDisplay = (this.maxDataDisplay > 0 ? this.addCnt : 0)  + '/' + this.maxDataDisplay;

    } else {
      this.list = this.list.concat(this.epmDetailData.list);
    }
  }

  /**
   * 장비 추가
   */
  public appendEpmDetailList() {
    // 최대 표출 수 비교
    if (this.addCnt >= this.maxDataDisplay) {
      return;
    }

    this.onAddEpmDetailData.emit();
    this.addCnt++;
    this.showDataDisplay = (this.maxDataDisplay > 0 ? this.addCnt : 0)  + '/' + this.maxDataDisplay;
  }

  /**
   * 콤마 추가
   *
   * @param value (콤마 추가 데이터)
   */
  public addComma(value: number): string | number {
    return Utils.NumberUtil.addComma(value);
  }

  /**
   * 반올림하여 값 셋팅
   *
   * @param value (변환값)
   */
  public addFixedRound(value: string): string | number {
    if (value) {
      return this.addComma((Math.ceil((parseFloat(value) * 1000))) / 1000);
    } else {
      return value;
    }
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
   * 확대보기 체크 유무
   */
  public showExpansion() {
    this.isDetailExpansion = !this.isDetailExpansion;
  }

  /**
   * excel download 실행
   */
  public downloadExcel(): void {
    if (!this.isDownloadExcel) {
      this.isDownloadExcel = true;
      this.onDownloadExcel.emit({
        chartNum: 'e4',
        info: this.epmDetailData.info
      });
    }
  }

  /**
   * excel download 완료 처리
   */
  public finishedExcel(): void {
    this.isDownloadExcel = false;
  }
}
