import { Component, OnInit, ElementRef, Injector, Input, AfterViewInit, ViewChild } from '@angular/core';
import { AbstractComponent } from '../../../common/component/abstract.component';
import { IpmService } from '../../service/ipm.service';
import { IcpmService } from '../../service/icpm/icpm.service';
import { BmrkService } from '../../../ipm/common/service/bmrk/bmrk.service';
import { AddressService } from '../../../ipm/common/service/right-kind/address/address.service';
import { TranslateService } from '@ngx-translate/core';
import { LpmService } from '../../service/lpm/lpm.service';
import { AppBean } from './util/appBean';
import { LPMCenterComponent } from './center/center.component';
import { LPMLeftComponent } from './left/left.component';
import { LPMRightComponent } from './right/right.component';
import * as _ from 'lodash';

declare var window: any;

@Component({
  selector: 'lpm',
  templateUrl: './lpm.component.html',
  providers: [AppBean],
  styles: [
    '@import "https://gis.tango.sktelecom.com/mapgeo/build/mapgeo.min.css";'
  ],
})

export class LpmComponent extends AbstractComponent implements OnInit {

  @ViewChild(LPMCenterComponent) centerComponent: LPMCenterComponent;
  @ViewChild(LPMLeftComponent) leftComponent: LPMLeftComponent;
  @ViewChild(LPMRightComponent) rightComponent: LPMRightComponent;

  private appBean: any;

  public eventCtrl: any;

  public centerUIType: string = 'center-default';
  public leftUIType: string = 'lpm-menu';
  public rightUIType: string = 'right-default';
  public southUIType: string = 'south-default';

  constructor(
    protected elementRef: ElementRef,
    protected injector: Injector,
    private ipmService: IpmService,
    private icpmService: IcpmService,
    private lpmService: LpmService,
    private bmrkService: BmrkService,
    private addressService: AddressService
  ) {
    super(elementRef, injector);

    // 엔진 클라이언트 underscore 라이브러리와 충돌 방지
    window[ 'lodash' ] = _.noConflict();

    // Autowired 대신하기위한 코드 작성
    this.appBean = injector.get(AppBean);
    this.eventCtrl = this.appBean.getEventCtrl();

    window.LPM_COMPONENT = this;
  }

  ngOnInit() {

    // 태깅
    // this.tagging(this.TaggingType.DETAIL, this.TaggingAction.VIEW, 'lpm', 'IDCube Profile');

    this.setHeader();
  }

  public onInitCenterUIType(fieldNm: string) {
      if ( fieldNm == 'FNL4001' && this.centerUIType == 'center-default' ) {
          this.centerUIType = 'center-compare';
      } else if ( fieldNm != 'FNL4001' && this.centerUIType == 'center-compare' ) {
        this.centerUIType = 'center-default';
      }
  }

  public onInitRightUIType(fieldNm: string) {
     if ( fieldNm == 'FNL4001' && this.rightUIType == 'right-default' ) {
          this.rightUIType = 'right-compare';
      } else if ( fieldNm != 'FNL4001' && this.rightUIType == 'right-compare' ) {
        this.rightUIType = 'right-default';
      }
  }

  private setHeader(): void {
    this.ipmService.setHeaderEvent({
      'url' : 'lpm',
      'title' : 'LPM',
      'subTitle' : 'Location'
    });
  }
}
