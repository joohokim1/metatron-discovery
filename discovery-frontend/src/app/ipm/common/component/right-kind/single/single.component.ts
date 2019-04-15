import { Component, OnInit, ElementRef, Injector, Input } from '@angular/core';
import { AbstractComponent } from '../../../../../common/component/abstract.component';
import { RightKindInterface } from '../right-kind.component';

@Component({
  selector: 'single',
  templateUrl: `./single.component.html`,
  styles: []
})
export class SingleComponent extends AbstractComponent implements OnInit, RightKindInterface {

  // 전달받은 데이터
  @Input()
  public menuDetailData: any;

  // 필터 데이터
  public fltrVal: Array<any> = [];

  public checked: string;

  // 생성자
  constructor(
    protected elementRef: ElementRef,
    protected injector: Injector
  ) {
    super(elementRef, injector);
  }

  ngOnInit() {
    this.fltrVal = this.menuDetailData.fltrDtlList[0].values;
    if (this.menuDetailData.fltrVal) {
      this.checked = this.menuDetailData.fltrVal[0].code;
    }
  }

  /**
   * Override Method
   * 필터조건에 사용되는 데이터
   * @return 필터 조건 Object
   */
  public getFilterData(): any {

    let fltrVal = [];
    let dtNm = '';

    for (let item of this.fltrVal) {
      if (item.code == this.checked) {
        fltrVal.push(item);
        dtNm = item.name;
        break;
      }
    }

    return {fltrVal: fltrVal, dtNm: dtNm};
  }
}
