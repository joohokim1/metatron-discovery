import { Component, OnInit, ElementRef, Injector, ViewChildren, QueryList } from '@angular/core';
import { AbstractComponent } from '../../../common/component/abstract.component';
import { IpmService } from '../../service/ipm.service';

@Component({
  selector: 'main',
  templateUrl: './main.component.html',
  styles: []
})
export class MainComponent extends AbstractComponent implements OnInit {

  // 메뉴 링크
  public url: string = 'main';

  // 메뉴 표출정보 데이터
  public mainData = [
    {isOn: false, step: '00'},
    {isOn: true, step: '00'},
    {isOn: false, step: '00'}
  ]

  // 생성자
  constructor(
    protected elementRef: ElementRef,
    protected injector: Injector,
    private ipmService: IpmService
  ) {
    super(elementRef, injector);
  }

  ngOnInit() {

    // 태깅
    // this.tagging(this.TaggingType.DETAIL, this.TaggingAction.VIEW, 'main', 'IDCube Profile');

    // ipm 화면 헤더 데이터 셋팅
    this.ipmService.setHeaderEvent({
      url : this.url,
      title : '',
      subTitle : ''
    });
  }

  /**
   * 스크롤 이동
   */
  public scrollTo(): void {
    $('html,body').animate({
      scrollTop: $(".int_imac").offset().top
    }, 1000);
  }

  /**
   * ipm 선택 이벤트
   *
   * @param i (icpm, epm, lpm 구분)
   */
  public checkIpm(i) {
    for (let j = 0; j < this.mainData.length; j++) {
      if (i == j) {
        this.mainData[j].isOn = true;
      } else {
        this.mainData[j].isOn = false;
      }
      this.mainData[j].step = '00';
    }
  }
}
