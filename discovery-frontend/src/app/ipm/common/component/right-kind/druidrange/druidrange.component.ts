import { Component, OnInit, OnDestroy, ElementRef, Injector, Input } from '@angular/core';
import { AbstractComponent } from '../../../../../common/component/abstract.component';
import { DruidrangeService } from '../../../service/right-kind/druidrange/druidrange.service';
import { RightKindInterface } from '../right-kind.component';
import { CommonConstant } from '../../../constant/common-constant';
import { Utils } from '../../../util/utils';

declare const echarts: any;
@Component({
  selector: 'druidrange',
  templateUrl: `./druidrange.component.html`,
  styles: []
})
export class DruidrangeComponent extends AbstractComponent implements OnInit, OnDestroy, RightKindInterface {

  // 전달받은 데이터
  @Input()
  public menuDetailData: any;

  // slider 전역 변수
  public min: number;
  public max: number;
  public step: number;

  // interval 배열
  public intervalList: Array<any> = [];

  // slider 배열
  public sliderList: Array<any> = [];

  // 범위 차트
  public rangeChart: any;

  public rangeBarOption = {
    color: ['#959BA9'],
    xAxis : {
      type: 'category',
      silent: true,
      splitLine: {
        show: false
      },
      axisLabel: {
        show: true
      },
      axisTick: {
        show: false
      },
      data: []
    },
    yAxis: {
      type: 'value',
      splitLine: {
        show: false
      },
      axisLabel: {
        show: false
      },
      axisLine: {
        show: false
      },
      axisTick: {
        show: false
      }
    },
    grid: {
      left: '5px',
      right: '15px',
      top: '0px',
      bottom: '20px'
    },
    series: [{
      data: [],
      type: 'bar'
    }]
  }

  // 생성자
  constructor (
    protected elementRef: ElementRef,
    protected injector: Injector,
    protected druidrangeService: DruidrangeService
  ) {
    super(elementRef, injector);
  }

  ngOnInit() {

    // 초기 request param 셋팅
    let requestData = {
      network: this.menuDetailData.classification.fltrVal[0].code[0],
      equipment: this.menuDetailData.classification.fltrVal[0].code[1],
      vendor: this.menuDetailData.classification.fltrVal[0].code[2],
      step: this.menuDetailData.fltrDtlList[0].step,
      druidNm: this.menuDetailData.druidNm,
      occrDth: this.menuDetailData.occrDth
    };

    this.druidrangeService.getEpmDruidrange(requestData)
    .then(result => {
      if (result.code === CommonConstant.CODE.RESULT_CODE.SUCCESS) {

        this.rangeChart = echarts.init(document.getElementById('rangeChart'));
        this.rangeChart.setOption(this.rangeBarOption, true);

        let rangeBarOption = this.rangeChart.getOption();
        rangeBarOption.series[0].data = result.data;

        let arr = result.data.map(item => {
          return item.name;
        });

        rangeBarOption.xAxis[0].data = arr;
        this.rangeChart.setOption(rangeBarOption, true);

        // 초기화
        // min, max 세팅
        this.min = result.data[0].name;
        this.max = result.data[result.data.length-1].name;

        if (this.step) {
          this.step = Math.round(( this.max - this.min ) / this.step);
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
            // slider list에 추가
            this.sliderList.push({
              range: item.split('~').map(Number)
            });
          }

        // 왼쪽 메뉴 클릭에 의한 세팅
        } else {
          // Range 기본값 세팅
          this.sliderList.push({
            range: [this.min, this.max]
          });
        }
      }
    });
  }

  ngOnDestroy() {
    // 차트 데이터 삭제
    if (this.rangeChart) {
      this.rangeChart.dispose();
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
      let range = this.sliderList[i].range.join('~');

      dtNmArr.push(range);

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
      // value 가 공백일 경우
      if (value === '') {
        value = this.min;

      // value가 끝 input box 값보다 크거나 같다면 끝 값으로 지정
      } else if (value >= slider.range[1]) {
        value = slider.range[1];

      // 시작 값이 최소값보다 작아질 경우
      } else if (value <= this.min) {
        // min값으로 range 및 input box 세팅
        value = this.min;
      }

    // 끝 input box 입력
    } else if (i == 1) {
      // value 가 공백일 경우
      if (value === '') {
        value = this.max;

      // value가 시작 input box 값보다 작거나 같다면 시작 값으로 지정
      } else if (value <= slider.range[0]) {
        value = slider.range[0];

      // 끝 값이 최대값을 넘어설 경우
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

  public chartResize(): void {
    let thiz = this;
    // 차트 사이즈 조정
    setTimeout(function() {
      thiz.rangeChart.resize();
    }, 200);
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
}
