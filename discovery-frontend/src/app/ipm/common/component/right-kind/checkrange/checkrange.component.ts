import { Component, OnInit, ElementRef, Injector, Input } from '@angular/core';
import { AbstractComponent } from '../../../../../common/component/abstract.component';
import { RightKindInterface } from '../right-kind.component';
import { Utils } from '../../../util/utils';

@Component({
  selector: 'checkrange',
  templateUrl: `./checkrange.component.html`,
  styles: []
})
export class CheckrangeComponent extends AbstractComponent implements OnInit, RightKindInterface {

  // 전달받은 데이터
  @Input()
  public menuDetailData: any;

  // 체크박스 데이터
  public checkBoxData: any;

  // 체크 된 데이터 명
  public checked: string;

  // slider 전역 변수
  public min: number;
  public max: number;
  public step: number;

  // slider의 min, max에 이상/이하 표시
  public overType: string = '';
  public underType: string = '';

  // slider 배열
  public sliderList: Array<any> = [];

  // 생성자
  constructor(
    protected elementRef: ElementRef,
    protected injector: Injector
  ) {
    super(elementRef, injector);
  }

  ngOnInit() {
    // 초기화
    // min, max, step 세팅

    this.min = Number(this.menuDetailData.fltrDtlList[0].values[0]);
    this.max = Number(this.menuDetailData.fltrDtlList[0].values[1]);
    this.step = Number(this.menuDetailData.fltrDtlList[0].step) || 1;
    this.checkBoxData = this.menuDetailData.fltrDtlList[1].values;
    this.checked = this.checkBoxData[1].code;

    //최대, 최소값 +- 값
    if (this.menuDetailData.fltrDtlList[0].rmk) {
      this.overType = this.menuDetailData.fltrDtlList[0].rmk.includes('+') ? '+' : '';
      this.underType = this.menuDetailData.fltrDtlList[0].rmk.includes('-') ? '-' : '';
    }
    // sliderList 초기화
    this.sliderList = [];

    // 선택 필터 클릭에 의한 세팅
    if (this.menuDetailData.fltrVal) {

      // 선택 필터 데이터 세팅
      let rangeListArr = [];
      for (let item of this.menuDetailData.fltrVal) {
        rangeListArr.push(item.code);
      }

      for (let item of rangeListArr) {

        // split하여 array로 반환
        let rangeArr = item.split('~');
        if (!rangeArr[1]) {
          rangeArr[1] = this.max;
        }

        // slider list에 추가
        this.sliderList.push({
          // range [시작값 , 종료값 (~뒤 값이 없을 경우 max값)]
          range: rangeArr.map(Number)
        });

      }

      // 체크박스 선택
      this.checked = this.menuDetailData.druidNm;

    // 왼쪽 메뉴 클릭에 의한 세팅
    } else {
      // Range 기본값 세팅
      this.sliderList.push({
        range: [this.min, this.max]
      });
    }
  }

  /**
   * Override Method
   * 필터조건에 사용되는 데이터(Range Data)
   *
   * @return 필터 조건 Object
   */
  public getFilterData(): any {

    // return Data
    let fltrVal = [];

    let dtNmArr = [];
    for (let i = 0;  i < this.sliderList.length; i++) {

      let slider = this.sliderList[i].range;
      let range = slider.join('~');

      // slider 종료 값이 최대값이거나 최대값을 넘어서는 경우
      if (this.overType && this.isOverMax(slider[1])) {
        dtNmArr.push(slider[0] + '이상');
        range = range.substring(0, range.lastIndexOf(slider[1]));
      } else {
        dtNmArr.push(range);
      }

      // {"code":"0~5"} 형태로 세팅
      fltrVal.push({
        code: range
      });
    }

    return {
      dtNm: dtNmArr.join(', '),
      fltrVal: fltrVal
    };
  }

  /**
   * range input box 값 change 이벤트
   *
   * @param e ($event 객체)
   * @param slider (slider object)
   * @param i (2개 input box 구분 - 0: 시작, 1: 끝)
   */
  public changeRange(e: any, slider: any, i: number): void {

    // slider를 변경할 값
    let value = e.target.value;

    // 시작 input box 입력
    if (i == 0) {

      // 공백일 경우
      if (value === '') {
        value = this.min;

      // value가 종료 input box 값보다 크거나 같다면 종료 값으로 지정
      } else if (value > slider.range[1]) {
        value = slider.range[1];

      // 시작 값이 최소값보다 작거나 같아질 경우
      } else if (value <= this.min) {
         value = this.min;
      }

    // 끝 input box 입력
    } else if (i == 1) {

      // 공백일 경우
      if (value === '') {
        value = this.max;

      // value가 시작 input box 값보다 작거나 같다면 시작 값으로 지정
      } else if (value <= slider.range[0]) {
        value = slider.range[0];

      // 종료 값이 최대값을 넘어설 경우
      } else if (value >= this.max) {
        // max값으로 range 및 input box 세팅
        value = this.max;
      }
    }

    // 변경된 값을 input box에 세팅
    e.target.value = value;

    // 변경된 range를 slider에 세팅
    let newRange = [slider.range[0], slider.range[1]];
    newRange[i] = value;
    slider.range = newRange;
  }

  /**
   * checkBox 클릭 이벤트
   *
   * @param item (체크 된 데이터)
   */
  public clickCheckBox(item: any) {
    this.checked = item.code;
    this.menuDetailData.druidNm = item.code;
  }

  /**
   * slider 추가
   */
  public addSlider(): void {

    // slider 추가
    this.sliderList.splice(0, 0, {
      range: [this.min, this.max],
      min: this.min,
      max: this.max
    });
  }

  /**
   * slider 제거
   *
   * @param i (slider index)
   */
  public deleteSlider(i) {
    this.sliderList.splice(i, 1);
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
   * Max Over 여부 체크
   *
   * @param value (range의 종료 value)
   */
  public isOverMax(value: number): boolean {
    return value >= this.max;
  }

}
