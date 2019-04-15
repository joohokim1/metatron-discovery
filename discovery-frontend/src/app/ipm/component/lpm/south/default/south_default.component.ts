import { Component, OnInit, ElementRef, Injector, Input, ViewChild } from '@angular/core';
import { AbstractComponent } from '../../../../../common/component/abstract.component';
import { AppBean } from '../../util/appBean';

import { LpmService } from '../../../../service/lpm/lpm.service';

import { LPMChartComponent } from '../../south/chart/lpm_chart.component';
import { LPMGridComponent } from '../../south/grid/lpm_grid.component';
import { CommonConstant } from '../../../../common/constant/common-constant';

import { TranslateService } from "@ngx-translate/core";
import { Alert } from '../../../../../common/util/alert.util';

import * as _ from 'lodash';

declare var L: any;
declare var window: any;

@Component({
    selector: 'south-default',
    templateUrl: `./south_default.component.html`,
    styles: []
})

export class SouthDefaultComponent extends AbstractComponent implements OnInit {

    @ViewChild(LPMChartComponent) chartComponent: LPMChartComponent;
    @ViewChild(LPMGridComponent) gridComponent: LPMGridComponent;

    // 전달받은 데이터
    @Input()
    public southUIType: any;

    private _appBean: any;

    public addrNmList: Array<any> = [];

    // 생성자
    constructor(
        protected elementRef: ElementRef,
        protected injector: Injector,
        private lpmService: LpmService
    ) {
        super(elementRef, injector);

        this._appBean = injector.get(AppBean);
    }

    ngOnInit() {

    }

    public redrawSouthSector(filterList: Array<any>): void {

        let center: any = window.LPM_COMPONENT.centerComponent.childComponent;

        if ( center.getLayrGrpDruidNm ) {
            let druidNm: string = center.getLayrGrpDruidNm();
            this.getCustomAddAddrFilter(_.cloneDeep(filterList)).then(result=>{
                if ( druidNm ) {
                    this.updateAddrNameList(result);
                    this.refreshChart(druidNm, result);
                    this.refreshGrid(druidNm, result);
                }
            });
        }
    }

    private getCustomAddAddrFilter(filterList: Array<any>): Promise<any> {
        return new Promise((resolve)=> {
            let addrFilter = _.find(filterList, function(item, idx) {
                return item.scrnClNm == 'address';
            });

            if ( addrFilter ) {
                return resolve(filterList);
            }

            let center: any = this.getCurMapCenter();

            if ( center ) {
                L.MG.Api.getSidoByPoint(center).then(result=>{
                    filterList.push({
                        scrnClNm: 'address',
                        fltrNm: 'ldong_cd',
                        fltrVal: [
                            {code: [result.SIDO_CODE], name: [result.SIDO_NAME]}
                        ]
                    });

                    return resolve(filterList);
                });
            }
        });
    }

    private getCurMapCenter(): any {
        let center = window.LPM_COMPONENT.centerComponent.childComponent;
        if ( center.getMap ) {
            let map: any = center.getMap();
            if ( map ) {
                return map.getCenter();
            }
        }

        return null;
    }

    private refreshChart(druidNm: string, filterList: Array<any>) {

        this.chartComponent.showLoading(true);

        this.lpmService.getChartPopulation({dsNm: druidNm, fltrDatVal: filterList}).then(result=>{
            if (result.code === CommonConstant.CODE.RESULT_CODE.SUCCESS) {
                this.chartComponent.refreshChart(result.data);
            } else {
                //Alert.error(this.translateService.instant('COMMON.MESSAGE.ERROR', '서버 에러가 발생하였습니다. 관리자에게 문의하세요.'));
                Alert.warning('서버 에러가 발생하였습니다. 관리자에게 문의하세요.(Druid Server)');
                this.chartComponent.showLoading(false);
            }
        });
    }

    private refreshGrid(druidNm: string, filterList: Array<any>) {

        this.gridComponent.showLoading(true);

        this.lpmService.getGridPopulation({dsNm: druidNm, fltrDatVal: filterList}).then(result=>{
            if (result.code === CommonConstant.CODE.RESULT_CODE.SUCCESS) {
                this.gridComponent.refreshGrid(result.data);
            } else {
                //Alert.error(this.translateService.instant('COMMON.MESSAGE.ERROR', '서버 에러가 발생하였습니다. 관리자에게 문의하세요.'));
                Alert.warning('서버 에러가 발생하였습니다. 관리자에게 문의하세요.(Druid Server)');
                this.gridComponent.showLoading(false);
            }
        });
    }

    public updateAddrNameList(filterList:Array<any>): void {
        let addrFilter: any = _.find(filterList, function(item, idx) {
            return item.scrnClNm == 'address';
        });

        if ( addrFilter ) {
            this.addrNmList = addrFilter.fltrVal[0].name;
            return;
        }

        this.addrNmList = [];
    }
}
