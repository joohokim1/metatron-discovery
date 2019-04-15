import { Component, OnInit, ElementRef, Injector, Input } from '@angular/core';
import { AbstractComponent } from '../../../../../common/component/abstract.component';
import { RightKindInterface } from '../right-kind.component';

@Component({
  selector: 'weekly',
  templateUrl: `./weekly.component.html`,
  styles: []
})
export class WeeklyComponent extends AbstractComponent implements OnInit, RightKindInterface {

  // 전달받은 데이터
  @Input()
  public menuDetailData: any;

  // 필터 데이터
  public fltrVal: Array<any> = [];

  // 생성자
  constructor(
    protected elementRef: ElementRef,
    protected injector: Injector
  ) {
    super(elementRef, injector);
  }

  ngOnInit() {
    this.fltrVal = this.menuDetailData.fltrDtlList[0].values;

    // 기존 필터 정보가 존재 할 경우
    if (this.menuDetailData.fltrVal) {
      for (let i of this.menuDetailData.fltrVal) {
        for (let j of this.fltrVal) {
          if (i.code === j.code) {
            j.on = true;
            break;
          }
        }
      }
    } else {
      // 기본 데이터
      for (let i of this.fltrVal) {
        i.on = true;
      }
    }
  }

  /**
   * Override Method
   * 필터조건에 사용되는 데이터
   * @return 필터 조건 Object
   */
  public getFilterData(): any {

    let fltrVal = [];
    let dtNm = [];
    for (let item of this.fltrVal) {
      if (item.on) {
        fltrVal.push({
          code: item.code,
          name: item.name
        });
        dtNm.push(item.name);
      }
    }

    return {fltrVal: fltrVal, dtNm: dtNm.join(', ')};
  }
}
