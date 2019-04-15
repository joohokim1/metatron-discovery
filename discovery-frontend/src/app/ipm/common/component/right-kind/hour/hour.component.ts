import { Component, OnInit, ElementRef, Injector, Input } from '@angular/core';
import { AbstractComponent } from '../../../../../common/component/abstract.component';
import { RightKindInterface } from '../right-kind.component';

@Component({
  selector: 'hour',
  templateUrl: './hour.component.html'
})
export class HourComponent extends AbstractComponent implements OnInit, RightKindInterface {

  // 전달받은 데이터
  @Input()
  public menuDetailData: any;

  // 분석시간 데이터
  public analysisTime: Array<any> = [];

  // 최한시 분석시간 데이터
  public limitAnalysisTime: Array<any> = [];

  // 전체 선택 체크 데이터
  public isAllYn: string = 'Y';

  // 생성자
  constructor(
    protected elementRef: ElementRef,
    protected injector: Injector
  ) {
      super(elementRef, injector);
  }

  ngOnInit() {

    // 최한시 데이터 설정
    let limit = this.menuDetailData.fltrDtlList[0].values;

    for (let i = 0; i < limit.length; i++) {
      this.limitAnalysisTime.push(limit[i].code);
    }

    // 날짜 시간 데이터
    for (let i = 0; i < 24 ; i++) {
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
    }

    // 현재 즐겨찾기 데이터가 존재 할 경우
    if (this.menuDetailData.fltrVal) {
      for (let i = 0; i < this.menuDetailData.fltrVal.length; i++) {
        for (let j = 0; j < this.analysisTime.length; j++) {
          if (this.menuDetailData.fltrVal[i].code === this.analysisTime[j].time) {
            this.analysisTime[j].check = 'Y';
          }
        }
      }

      this.isAllYn = 'N';
    } else {
      for (let i = 0; i < this.analysisTime.length; i++) {
        this.analysisTime[i].check = 'Y';
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

    let selectAnalysisTime = [];

    for (let i = 0; i < this.analysisTime.length; i++) {
      if (this.analysisTime[i].check == 'Y') {
        selectAnalysisTime.push(this.analysisTime[i].time);
      }
    }

    // return Data
    let fltrVal = [];
    for (let i = 0; i < selectAnalysisTime.length; i++) {
      fltrVal.push({code: selectAnalysisTime[i]});
    }

    return {
      dtNm: selectAnalysisTime.join(', '),
      fltrVal: fltrVal
    };
  }

    /**
   * 분석시간 선택 / 해제
   *
   * @param item (선택 시간 데이터)
   */
  public checkAnalysisTime(item): void {
    if (item.check == 'Y') {
      item.check = 'N';
      for (let i = 0 ; i < this.analysisTime.length; i++) {
        if (this.analysisTime[i].check === 'Y') {
          return;
        }
      }
      this.isAllYn = 'N';
    } else {
      item.check = 'Y';
      for (let i = 0 ; i < this.analysisTime.length; i++) {
        if (this.analysisTime[i].check === 'N') {
          return;
        }
      }
      this.isAllYn = 'Y';
    }
  }

  /**
   * 모든 분석시간 선택 / 해제
   *
   * @param e (전체선택 버튼 이벤트)
   */
  public checkAllAnalysisTime(): void {

    if (this.isAllYn === 'Y') {
      for (let i = 0; i < this.analysisTime.length; i++) {
        this.analysisTime[i].check = 'N';
        this.isAllYn = 'N';
      }
    } else {
      for (let i = 0; i < this.analysisTime.length; i++) {
        this.analysisTime[i].check = 'Y';
        this.isAllYn = 'Y';
      }
    }
  }

  /**
   * 최한시 분석시간 제외
   */
  public removeLimitAnalysisTime(): void {

    for (let i = 0; i < this.analysisTime.length; i++) {
      for (let j = 0; j < this.limitAnalysisTime.length; j++) {
        if ( this.analysisTime[i].time == this.limitAnalysisTime[j]) {
          this.analysisTime[i].check = 'N';
        }
      }
    }
  }

}
