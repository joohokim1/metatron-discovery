import { Component, OnInit, ElementRef, Injector, Input } from '@angular/core';
import { AbstractComponent } from '../../../../../common/component/abstract.component';
import { Alert } from '../../../../../common/util/alert.util';
import { AppBean } from '../../util/appBean';

import * as _ from 'lodash';

declare var Slick: any;
declare var window: any;

@Component({
    selector: 'lpm-grid',
    templateUrl: `./lpm_grid.component.html`,
    styles: []
})

export class LPMGridComponent extends AbstractComponent implements OnInit {

    private _grid: any = null;
    private _appBean: any;
    public isLoading: boolean = false;

    // Excel 다운로드 진행 여부
    public isDownloadExcel: boolean = false;

    // 생성자
    constructor(
        protected elementRef: ElementRef,
        protected injector: Injector
    ) {
        super(elementRef, injector);
        this._appBean = injector.get(AppBean);
    }

    ngOnInit() {
        // 특정 크롬 브라우저에서 Cannot Find styleSheet 에러 발생
        // 라이브러리를 고칠 수 없어, 임시방편 처리 함.
        setTimeout(()=> {
            this.initGrid();
        }, 1000);
    }

    public setCommaFormatter(row, cell, value, columnDef, dataContext) {
        return value.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ',');
    }

    public initGrid(): void {

        if ( this._grid ) return;

        let columns: Array<any> = [
            {id: 'sidoNm', name: '시도', field: 'sidoNm', sortable: true, cssClass:'grid-align-center'},
            {id: 'sggNm', name: '시군구', field: 'sggNm', sortable: true, cssClass:'grid-align-center'},
            {id: 'dongNm', name: '읍면동', field: 'dongNm', sortable: true, cssClass:'grid-align-center'},
            {id: 'manCnt', name: '남자', field: 'manCnt', sortable: true, cssClass:'grid-align-center', formatter: this.setCommaFormatter},
            {id: 'womanCnt', name: '여자', field: 'womanCnt', sortable: true, cssClass:'grid-align-center', formatter: this.setCommaFormatter},
            {id: 'sumUserCnt', name: '소계', field: 'sumUserCnt', sortable: true, cssClass:'grid-align-center', formatter: this.setCommaFormatter},
        ];

        let options = {
            forceFitColumns: true,
            enableCellNavigation: true,
            enableColumnReorder: false,
            multiColumnSort: true
        };

        this._grid = new Slick.Grid('#div-grid', [], columns, options);

        this._grid.onSort.subscribe(function (e, args) {
            var cols = args.sortCols;

            if(this.getData().length > 0) {
                this.getData().sort(function (dataRow1, dataRow2) {
                    for(let i=0, l=cols.length; i<l; i++) {
                        let field = cols[i].sortCol.field;
                        let sign = cols[i].sortAsc ? 1 : -1;
                        let value1 = dataRow1[field], value2 = dataRow2[field];
                        let result = (value1 == value2 ? 0 : (value1 > value2 ? 1 : -1)) * sign;
                        if (result != 0) {
                            return result;
                        }
                    }
                    return 0;
                })

            }
            this.invalidate();
            this.render();
        });
    }

    public refreshGrid(modelList: Array<any>): void {
        if ( !this._grid ) {
            this.initGrid();
        }

        this._grid.setData(this.getSortUserCntModelList(modelList));
        this._grid.updateRowCount();
        this._grid.render();
        this.showLoading(false);
    }

    public resizeGrid(): void {
        this._grid.resizeCanvas();

        if ( this.isLoading ) {
            this.showLoading(false);
            this.showLoading(true);
        }
    }

    public showLoading(isShow: boolean) {
        this.isLoading = isShow;
    }

    private getSortUserCntModelList(modelList: Array<any>): Array<any> {
        let ascModelList = _.sortBy(modelList, function(item, idx) {
            return item.sumUserCnt;
        });

        return ascModelList.reverse();
    }

    public onClickExcelDownload(): void {

        let center: any = window.LPM_COMPONENT.centerComponent.childComponent;
        let right: any = window.LPM_COMPONENT.rightComponent.childComponent;

        if(center.getLayrGrpId() != '200') {
            return Alert.warning('xDR 데이터셋일 경우만 가능합니다.');
        }

        if ( center.getLayrGrpDruidNm ) {
            this.isDownloadExcel = true;
            let druidNm: string = center.getLayrGrpDruidNm();

            _.each(right.filterList, function(item, idx) {
                switch(item.scrnClNm) {
                    case 'datetime':
                        window.LPM_COMPONENT.lpmService.getLpmGridExcel({dsNm: druidNm, fltrDatVal: right.filterList}, item.fltrVal[0].code).then(result=>{
                            if (result === 'success') {
                                this.isDownloadExcel = false;
                            } else {
                                Alert.error('Server Error, 잠시후 다시 시도해주세요.');
                                this.isDownloadExcel = false;
                            }
                        });

                    break;
                }
            }.bind(this));

        }
    }
}
