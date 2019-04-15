import { Component, OnInit, ElementRef, Injector, Input } from '@angular/core';
import { AbstractComponent } from '../../../../../common/component/abstract.component';
import { RightKindInterface } from '../right-kind.component';

@Component({
  selector: 'multiple',
  templateUrl: `./multiple.component.html`,
  styles: []
})
export class MultipleComponent extends AbstractComponent implements OnInit, RightKindInterface {

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
    if (this.menuDetailData.fltrVal) {
      if ('multiples' === this.menuDetailData.scrnClNm) {
        this.fltrVal = this.menuDetailData.fltrVal;
        for (let item of this.fltrVal) {
          if (item.code === 'Y') {
            item.checked = true;
          }
        }
      } else {
        this.fltrVal = this.menuDetailData.fltrDtlList[0].values;
        for (let i of this.menuDetailData.fltrVal) {
          for (let j of this.fltrVal) {
            if (i.code === j.code) {
              j.checked = true;
              break;
            }
          }
        }
      }
    } else {
      this.fltrVal = this.menuDetailData.fltrDtlList[0].values;
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
    if ('multiples' === this.menuDetailData.scrnClNm) {
      for (let item of this.fltrVal) {
        if (item.checked) {
          fltrVal.push({
            code: 'Y',
            name: item.name
          });
          dtNm.push(item.name);
        } else {
          fltrVal.push({
            code: 'N',
            name: item.name
          });
        }
      }
    } else {
      for (let item of this.fltrVal) {
        if (item.checked) {
          fltrVal.push({
            code: item.code,
            name: item.name
          });
          dtNm.push(item.name);
        }
      }
    }

    return {fltrVal: fltrVal, dtNm: dtNm.join(', ')};
  }
}
