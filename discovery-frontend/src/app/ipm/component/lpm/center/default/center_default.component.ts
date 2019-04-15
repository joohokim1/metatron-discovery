import { Component, OnInit, ElementRef, Injector, Input, Output, EventEmitter, ViewChild, HostListener } from '@angular/core';
import { AbstractComponent } from '../../../../../common/component/abstract.component';
import { CommonConstant } from '../../../../common/constant/common-constant';
import { AppBean } from '../../util/appBean';
import { HeatMap } from '../heatMap';
import { FilterMap } from '../filterMap';
import { SubFilterComponent } from '../subFilter/subFilter.component';

import { LpmService } from '../../../../service/lpm/lpm.service';
import { LPMSouthComponent } from '../../south/south.component';

import { TranslateService } from "@ngx-translate/core";
import { Alert } from '../../../../../common/util/alert.util';

import * as _ from 'lodash';

declare var MapGeo: any;
declare var L: any;
declare var window: any;

declare const $: any;

@Component({
    selector: 'center-default',
    templateUrl: `./center_default.component.html`,
    styles: []
})

export class CenterDefaultComponent extends AbstractComponent implements OnInit {

    @ViewChild(LPMSouthComponent) southComponent: LPMSouthComponent;

    // 전달받은 데이터
    @Input()
    public centerUIType: any;

    public southUIType: string = 'south-default';

    private _map: any;

    private _lpmService: any;
    private _chartCtrl: any;
    private _gridCtrl: any;

    private _appBean: any;

    public eventCtrl: any;
    private heatMapCtrl: any;
    private filterMapCtrl: any;

    public layerList: Array<string> = [];
    public analLayerList: Array<string> = [];
    public isHeatMap: boolean = false;
    public isHeatMap2: boolean = false;
    public isHeatMap3: boolean = false;
    private heatMapCtrlList: Array<any> = [];

    public selFeatureList: Array<any> = [];
    // public legendList: Array<any> = [];
    public layerLegends: any = {};
    public isOpenLegend: boolean = false;
    public objectKeys: any = Object.keys;
    public isFeatureSelect: boolean = false;
    public selPopupLoc: any = {ptTop: '0px', ptLeft: '0px'};

    public isLoading: boolean = false;

    // slider 배열
    public sliderList: Array<any> = [];

    public venderId: string = null;
    public layrGrpItem: any = null;
    public isLoadingVector: boolean = false;

    // 현재 선택된 DataSet 기준으로 정렬
    public firstDataSetList: Array<any> = [];
    public anotherDataSetList: Array<any> = [];
    public mapViewLayerList: Array<any> = [];
    public subFilterList: Array<any> = [];
    public subFilterDataList: Array<any> = [];

    public isOpenAreaLayer: boolean = false;
    public isOpenEquipLayer: boolean = false;
    public isOpenBuildingLayer: boolean = false;

    protected jQuery = $;

	@Output()
	public applySubFilterEmitter = new EventEmitter<any>();

	@Output()
	public applyRemoveSubFilterEmitter = new EventEmitter<any>();

	@Output()
	public applyCollapseSubFilterEmitter = new EventEmitter<any>();

    // 생성자
    constructor(
        protected elementRef: ElementRef,
        protected injector: Injector,
        private lpmService: LpmService
    ) {
        super(elementRef, injector);
        this._appBean = injector.get(AppBean);

        this.eventCtrl = this._appBean.getEventCtrl();
    }

    ngOnInit() {
        let self: this = this;
        this.createMap().then(result=>{
            this.setEssentialFilters();
            this.eventCtrl.addSliderEventListener(this.jQuery);
            this.initLayerKeyFields();
            this.initDataSet(this.getLayrGrpId());

            let left: any = window.LPM_COMPONENT.leftComponent.childComponent;

            if ( left.onChangeLayrGrp ) {
                left.onChangeLayrGrp(this.getLayrGrpId());
            }

        });
    }

    @HostListener('document:click')
    onClickDocument() {
        this.isFeatureSelect = false;

        if ( window.LPM_COMPONENT.leftUIType == 'lpm-prop' ) {
            window.LPM_COMPONENT.leftUIType = 'lpm-menu';
        }
    }

    private initDataSet(layrGrpId : any): void {
        this.firstDataSetList = [];
        this.anotherDataSetList = [];

        _.each(this._map.getViewLayers(), function(layer, idx) {

            if( layer.properties.layrGrpId != '100') {

                let layerStyles = L.MG.SysCfg._styleCfg.getStylesByLayerName(layer.getLayerName());

                if( layer.properties.layrGrpId == layrGrpId) {

                    if(!layer.properties.visible) {
                        layer.properties.visible = true;
                    }

                    this.firstDataSetList.push(
                            { 'layrGrpId' : layer.properties.layrGrpId
                              , 'name' : layer.properties.name
                              , 'alias' : layer.properties.alias
                              , 'visible' : layer.properties.visible
                              , 'type' : layer.properties.type
                              , 'opacity' : _.clone(layerStyles[0].opacity) * 100
                              , 'subFltrList' : []
                              , 'venderList' : Number(layer.properties.layrGrpId) >= 300 && Number(layer.properties.layrGrpId) <= 400 ?
                                [
                                    {'venderName' : 'SKT', 'selected': true}
                                    , {'venderName' : 'KT', 'selected': false}
                                    , {'venderName' : 'LGU+', 'selected': false}
                                ] : []
                              , 'useVender' : false
                            }
                    );
                } else {

                    if(layer.properties.visible) {
                        layer.properties.visible = false;
                    }

                    layer.setVisible(false);

                    this.anotherDataSetList.push(
                            { 'layrGrpId' : layer.properties.layrGrpId
                              , 'name' : layer.properties.name
                              , 'alias' : layer.properties.alias
                              , 'visible' : layer.properties.visible
                              , 'type' : layer.properties.type
                              , 'opacity' : _.clone(layerStyles[0].opacity) * 100
                              , 'subFltrList' : []
                              , 'venderList' : Number(layer.properties.layrGrpId) >= 300 && Number(layer.properties.layrGrpId) <= 400 ?
                                [
                                    {'venderName' : 'SKT', 'selected': true}
                                    , {'venderName' : 'KT', 'selected': false}
                                    , {'venderName' : 'LGU+', 'selected': false}
                                ] : []
                              , 'useVender' : false
                            }
                    );
                }

            }
        }.bind(this));

        this.mapViewLayerList = this.getDataSetList();
    }

    public getDataSetList() : Array<any> {
        return this.firstDataSetList.concat(this.anotherDataSetList);
    }

    public onLayerChecked(evt: any, mapViewLayer: any): void {

        let visibleCnt = 0;
        _.each(this.mapViewLayerList, function(item, idx) {
            if( item.visible ) {
                visibleCnt++;

                if(visibleCnt == 2) {
                    evt.target.checked = false;
                    return;
                }
            }
        });

        let layer: any = this._map.getLayerById(mapViewLayer.name);
        let defaultFltr = [];

        if( mapViewLayer.layrGrpId == this.getLayrGrpId() ) {
            if ( layer ) {
                layer.setLayerType(layer.properties.type);
                layer.properties.visible = evt.target.checked;
                mapViewLayer.visible = evt.target.checked;
                layer.setVisible(evt.target.checked);
                this.refreshLayerLegend();
                return;
            }
        }

        mapViewLayer.visible = evt.target.checked;
        if( evt.target.checked ) {
            this.lpmService.getLpmFltrBasList(evt.target.id, true).then(function(result) {
                if (result.code === CommonConstant.CODE.RESULT_CODE.SUCCESS) {
                    result.data[0].collapse = true;

                    let venderIndex = 0;
                    _.each(mapViewLayer.venderList, function(item, idx) {
                        if(item.selected) {
                            venderIndex = Number(idx);
                        }
                    });

                    let subFltrs = [];
                    _.each(result.data, function(item, idx) {
                        _.each(item.fltrList, function(filter, index) {
                            if( filter.subUseYn == 'Y' ) {
                                if( venderIndex > 0  && filter.fltrNm.indexOf('freq_nm') > -1) {
                                    filter.useYn = 'N';
                                }

                                filter.scrnClNm = 'sf-' + filter.scrnClNm;
                                filter.isVisible = true;
                                filter.layrGrpId = evt.target.id;
                                subFltrs.push(filter);
                            }
                        });
                    });

                    _.each(this.mapViewLayerList, function(item, idx) {
                        if(item.layrGrpId == evt.target.id) {
                            item.subFltrList = subFltrs;
                            item.useVender = Number(layer.properties.layrGrpId) >= 300 && Number(layer.properties.layrGrpId) <= 400 ? true : false;
                        }
                    });
                }
            }.bind(this));
        } else {
            _.each(this.mapViewLayerList, function(item, idx) {
                if(item.layrGrpId == evt.target.id) {
                    item.subFltrList = [];
                    item.useVender = false;
                }
            });
            this.subFilterList = [];
        }

        if ( layer ) {
            layer.setLayerType(layer.properties.type);
            layer.properties.visible = evt.target.checked;

            //체크박스 클릭 시 레이어 초기 필터 셋팅(Date)
            let right: any = window.LPM_COMPONENT.rightComponent.childComponent;
            if ( _.has(right.filterList[right.filterList.length - 1], 'fltrVal') ) {
                let dateFltr = _.clone(right.filterList[right.filterList.length - 1]);
                dateFltr.druidNm = mapViewLayer.name;

                let dateTime = right.getDefaultDateTime();
                if(layer.properties.layrGrpId == '200') {
                    dateTime.start.hour = '09';
                    dateTime.end.hour = '10';
                    dateFltr.usedHour = true;
                } else {
                    dateTime.start.hour = '00';
                    dateTime.end.hour = '23';
                    dateFltr.usedHour = false;
                }

                let dateTimeCode = dateTime.start.year + dateTime.start.month + dateTime.start.date + dateTime.start.hour + '~'
                        + dateTime.end.year + dateTime.end.month + dateTime.end.date + dateTime.end.hour;

                let dateTimeString = dateTime.start.year + '/' + dateTime.start.month + '/' + dateTime.start.date + ' ' + dateTime.start.hour + '시 ~ '
                        + dateTime.end.year + '/' + dateTime.end.month + '/' + dateTime.end.date + ' ' + dateTime.end.hour + '시';

                dateFltr.dtNm = dateTimeString;
                dateFltr.fltrVal = [{code: dateTimeCode, name: dateTimeString}];

                let overCheck = false;

                for(let i=0; i<this.subFilterDataList.length; i++) {
                    if(this.subFilterDataList[i].druidNm == dateFltr.druidNm && this.subFilterDataList[i].scrnNm == dateFltr.scrnNm) {
                        overCheck = true;
                    }

                    if(this.subFilterDataList[i].druidNm == layer.properties.name) {
                        defaultFltr.push(this.subFilterDataList[i]);
                    }
                }
                if(!overCheck) {
                    this.subFilterDataList.push(dateFltr);
                    if(dateFltr.druidNm == layer.properties.name) {
                        defaultFltr.push(dateFltr);
                    }
                }
            }

            let venderIndex = 0;
            for(let i=0; i<mapViewLayer.venderList.length; i++) {
                if(mapViewLayer.venderList[i].selected) {
                    venderIndex = Number(i);
                }
            }

            if( Number(layer.properties.layrGrpId) >= 300 && Number(layer.properties.layrGrpId) <= 400 ) {
                if ( venderIndex == 0 ) {
                    layer.setAddCustomParams(" AND cmco_div_nm = 'SKT' " );
                } else if ( venderIndex == 1 ) {
                    layer.setAddCustomParams(" AND cmco_div_nm = 'KT' " );
                } else if ( venderIndex == 2 ) {
                    layer.setAddCustomParams(" AND cmco_div_nm <> 'SKT' AND cmco_div_nm <> 'KT' " );
                }
            }

            this.filterMapCtrl.setMapCustomFilter(layer, defaultFltr);
            //layer.setVisible(evt.target.checked);
        }
        this.refreshLayerLegend();
    }

    public onChangeSubLayrAgency(evt: any, mapViewLayer: any) : void {
        _.each(mapViewLayer.venderList, function(item, idx) {
            if(item.venderName == evt.target.value) {
                item.selected = true;

                let layer: any = this._map.getLayerById(mapViewLayer.name);

                if(layer) {
                    if ( Number(idx) == 0 ) {
                        layer.setAddCustomParams(" AND cmco_div_nm = 'SKT' " );
                    } else if ( Number(idx) == 1 ) {
                        layer.setAddCustomParams(" AND cmco_div_nm = 'KT' " );
                    } else if ( Number(idx) == 2 ) {
                        layer.setAddCustomParams(" AND cmco_div_nm <> 'SKT' AND cmco_div_nm <> 'KT' " );
                    }
                    //layer.refresh(true);
                    _.each(mapViewLayer.subFltrList, function(item, idx) {
                        if(item.scrnClNm.indexOf('datetime') < 0) {
                            delete item.fltrDtlList.fltrVal;
                        }
                    });

                    for(let i=0; i<this.subFilterDataList.length; i++) {
                        if(mapViewLayer.name == this.subFilterDataList[i].druidNm && this.subFilterDataList[i].scrnClNm.indexOf('datetime') > -1) {
                            this.filterMapCtrl.setMapCustomFilter(layer, [this.subFilterDataList[i]]);
                            break;
                        }
                    }
                    this.refreshLayerLegend();
                }
            } else {
                item.selected = false;
            }
        }.bind(this));

        if(evt.target.selectedIndex == 0) {
            _.each(mapViewLayer.subFltrList, function(item, idx) {
                if(item.fltrNm.indexOf('freq_nm') > -1) {
                    item.useYn = 'Y';
                }
            });
        } else {
            _.each(mapViewLayer.subFltrList, function(item, idx) {
                if(item.fltrNm.indexOf('freq_nm') > -1) {
                    item.useYn = 'N';
                }
            });
        }

        for(let i=0; i<this.subFilterDataList.length; i++) {

            if(this.subFilterDataList[i].druidNm == mapViewLayer.name && this.subFilterDataList[i].scrnClNm.indexOf('datetime') < 0) {
                this.subFilterDataList.splice(i,1);
            }
        }

        this.subFilterList = [];
    }

    public onClickFilterSelect(evt: any, subFltr: any, mapViewLayer: any) : void {

        this.lpmService.getLpmFltrBas(subFltr.fltrNm).then(function(result) {
            if (result.code === CommonConstant.CODE.RESULT_CODE.SUCCESS) {
                //result.data.isVisible = true;
                //result.data.collapse = true;

                if( !subFltr.fltrDtlList ) {
                    result.data.scrnClNm = 'sf-' + result.data.scrnClNm;
                    result.data.druidNm = mapViewLayer.name;
                    subFltr.fltrDtlList = result.data;
                    subFltr.fltrDtlList.isVisible = subFltr.isVisible;

                    if( subFltr.scrnClNm.indexOf('datetime') > -1) {
                        let dateFltr = null;
                        let right: any = window.LPM_COMPONENT.rightComponent.childComponent;
                        if ( _.has(right.filterList[right.filterList.length - 1], 'fltrVal') ) {
                            for(let i=0; i<this.subFilterDataList.length; i++) {
                                if(mapViewLayer.name == this.subFilterDataList[i].druidNm && this.subFilterDataList[i].scrnClNm.indexOf('datetime') > -1) {
                                    dateFltr = this.subFilterDataList[i];
                                }
                            }

                            if(dateFltr == null) {
                                dateFltr = _.clone(right.filterList[right.filterList.length - 1]);
                            }

                            subFltr.fltrDtlList.fltrVal = dateFltr.fltrVal;
                            subFltr.fltrDtlList.usedHour = dateFltr.usedHour;
                        }
                    } else {
                        for(let i=0; i<this.subFilterDataList.length; i++) {
                            if(mapViewLayer.name == this.subFilterDataList[i].druidNm && this.subFilterDataList[i].scrnNm == subFltr.scrnNm) {

                                if(subFltr.scrnClNm.indexOf('range') > -1 || subFltr.scrnClNm.indexOf('multiple') > -1 || subFltr.scrnClNm.indexOf('cei') > -1) {
                                    subFltr.fltrDtlList.fltrVal = this.subFilterDataList[i].fltrVal;
                                } else {
                                    subFltr.fltrDtlList.fltrVal = subFltr.fltrDtlList.fltrDtlList[0].values
                                    _.each(subFltr.fltrDtlList.fltrVal, function(_item, _idx) {
                                        _.each(this.subFilterDataList[i].fltrVal, function(item, idx) {
                                            if(_item.code == item.code) {
                                                _item.checked = true;
                                            }
                                        });
                                    }.bind(this));
                                }
                            }
                        }
                    }
                }

                for(let i=0; i<this.subFilterList.length; i++) {

                    if(this.subFilterList[i].layrGrpId != subFltr.layrGrpId) {
                        this.subFilterList = [];
                        break;
                    }

                    if(this.subFilterList[i].layrGrpId == subFltr.layrGrpId && this.subFilterList[i].scrnNm == subFltr.scrnNm) {
                        this.subFilterList.splice(i,1);
                        return;
                    }
                }

                this.subFilterList.push(subFltr);
            }
        }.bind(this));

    }

	public onClickApplySubFilter(event: any, fltrDtlList: any): void {

        let layer = this._map.getLayerById(fltrDtlList.druidNm);

		let filterData: any = {
			fltrUid: fltrDtlList.fltrUid,
			druidNm: fltrDtlList.druidNm,
			scrnNm: fltrDtlList.scrnNm,
            scrnClNm: fltrDtlList.scrnClNm,
            fltrNm: fltrDtlList.fltrNm,
			type: fltrDtlList.type,
			useYn: fltrDtlList.useYn,
			fltrVal: event.fltrVal,
			dtNm: event.dtNm
        };

        if(event.scrnClNm.indexOf('custmulticheck') > -1) {
            fltrDtlList.fltrVal = fltrDtlList.fltrDtlList[0].values;
        } else {
            fltrDtlList.fltrVal = event.fltrVal;
        }

        if( layer ) {
            let overCheck = false;
            let sameFilters = [];
            for(let i=0; i<this.subFilterDataList.length; i++) {
                if(this.subFilterDataList[i].druidNm == filterData.druidNm && this.subFilterDataList[i].scrnNm == filterData.scrnNm) {
                    this.subFilterDataList[i] = filterData;
                    overCheck = true;
                }

                if(this.subFilterDataList[i].druidNm == layer.properties.name) {
                    sameFilters.push(this.subFilterDataList[i]);
                }
            }
            if(!overCheck) {
                this.subFilterDataList.push(filterData);
                if(filterData.druidNm == layer.properties.name) {
                    sameFilters.push(filterData);
                }
            }

            this.filterMapCtrl.setMapCustomFilter(layer, sameFilters);
        }
	}

	public onClickCollapseSubFilter(event: any, subFltr: any): void {
        subFltr.isVisible = !subFltr.isVisible;
        subFltr.fltrDtlList.isVisible = !subFltr.fltrDtlList.isVisible;
	}

	public onClickRemoveSubFilter(event: any, subFltr: any): void {
        let layer = this._map.getLayerById(event.druidNm);

        let sameFilters = [];
        for(let i=0; i<this.subFilterDataList.length; i++) {
            if(this.subFilterDataList[i].druidNm == event.druidNm && this.subFilterDataList[i].scrnNm == event.scrnNm) {
                if(this.subFilterDataList[i].scrnClNm.indexOf('datetime') > -1) {
                    Alert.warning("연월일시는 삭제할 수 없습니다.");
                    return;
                } else {
                    this.subFilterDataList.splice(i,1);
                }
            }
        }

        for(let i=0; i<this.subFilterDataList.length; i++) {
            if(this.subFilterDataList[i].druidNm == layer.properties.name) {
                sameFilters.push(this.subFilterDataList[i]);
            }
        }

        this.filterMapCtrl.setMapCustomFilter(layer, sameFilters);
        /*
        if( subFltr.scrnClNm.indexOf('multiple') > -1 || subFltr.scrnClNm.indexOf('range') > -1) {
            subFltr.fltrDtlList.fltrVal = [];
        } else if( subFltr.scrnClNm.indexOf('cei') > -1 ) {
            delete subFltr.fltrDtlList.fltrVal;
        }
        */
        delete subFltr.fltrDtlList.fltrVal;
        let removeSubFilterIdx: number = 0;
        _.find(this.subFilterList, function (subFilter: any, idx: number): any {
            if ( subFilter.fltrDtlList.fltrUid == subFltr.fltrDtlList.fltrUid ) {
                removeSubFilterIdx = idx;
                return true;
            }
        });

        this.subFilterList.splice(removeSubFilterIdx, 1);

        _.each(subFltr.fltrDtlList.fltrDtlList[0].values, function(item, idx) {
                delete item.checked;
        });

	}

    public setLayerOpacity(layerName: string, opacity: number): void {

        let layer = this._map.getLayerById(layerName);

        if ( layer ) {
            if ( layer.properties.type == 'vector' ) {
                let isFill: Boolean = ( opacity == 0 ) ? false : true;
                layer.setStyle({fill: isFill, fillOpacity: Number(opacity/100)});
            } else {
                this.setHeatMapOpacity(layerName, opacity);
            }
        }
    }

    public setLayerType(type: string, layerName: string, mapViewLayer: any): void  {

        let layer = this._map.getLayerById(layerName);

        if ( layer ) {
            mapViewLayer.type = type;
            layer.setLayerType(type);

            if (type == 'vector') {
                this.clearHeatMapLayerByName(layerName);
            }

            layer.refresh(true);
        }

        this.refreshLayerLegend();
    }

    public onClickEtcLayer(event: any) : void {
        if(event.target.id == 'area') {
            this.isOpenAreaLayer = !this.isOpenAreaLayer;

            let sidoLayer = this._map.getLayerById('DAWUL_SIDO_A_TILE_RASTER');
            let sggLayer = this._map.getLayerById('DAWUL_SGG_A_TILE_RASTER');
            let dongLayer = this._map.getLayerById('DAWUL_RI_A');

            if(this.isOpenAreaLayer) {
                if(sggLayer.properties.selectable == 'N') {
                    sggLayer.setSelectable(false);
                }
                if(sidoLayer.properties.selectable == 'N') {
                    sidoLayer.setSelectable(false);
                }
                if(dongLayer.properties.selectable == 'N') {
                    dongLayer.setSelectable(false);
                }
                if(!dongLayer.getUserStyleConfig()) {
                    dongLayer.setUserStyleConfig(
                            {type: 'POLYGON', options: {makerType:'shape', shapeType:'xcross', isStroke: true, strokeColor: '#000000', weight: '2'}}
                        );
                }

                sggLayer.setVisible(true);
                sidoLayer.setVisible(true);
                dongLayer.setVisible(true);
            } else {
                sggLayer.setVisible(false);
                sidoLayer.setVisible(false);
                dongLayer.setVisible(false);
            }

            /*
            let curBounds = this._map.getBounds();
            let curZoom = this._map.getZoom();
            let params = {
                id: 1,
                bbox: curBounds.getSouthWest().lng + ',' + curBounds.getSouthWest().lat + ',' + curBounds.getNorthEast().lng + ',' + curBounds.getNorthEast().lat,
                level: curZoom,
                layerNames: ['DAWUL_RI_A'],
                customColumns: [],
                style: 'id'
            }

            L.MG.Util.postJSON('https://gis.tango.sktelecom.com/tango/servlets/feature/getFeaturesInBBoxByLayers', params).done(
                function (result) {
                    console.log(result);

                    L.geoJson(result.data, {
                        style: function (feature) {
                            //return {color: };
                        }
                    }).addTo(this._map);

                }.bind(this)
            );
            */

        } else if(event.target.id == 'equip') {
            this.isOpenEquipLayer = !this.isOpenEquipLayer;
            Alert.warning('준비중 입니다.');
        } else if(event.target.id == 'building') {
            this.isOpenBuildingLayer = !this.isOpenBuildingLayer;
            Alert.warning('준비중 입니다.');
        }

    }

    public setDefaultLayers() : void {

        let layer = this._map.getLayerById(this.firstDataSetList[0].name);
        if( layer ) {
            if( layer.properties.type == 'vector' ) {
                layer.setVisible(true);
            }
            //layer.setStyle({fillOpacity: 0.6});
        }

        this.refreshLayerLegend();
    }

    private getEssentialFilters(): Array<any> {
        let left = window.LPM_COMPONENT.leftComponent.childComponent;

        if ( left.getEssentialFilters ) {
            return left.getEssentialFilters();
        }

        return [];
    }

    private setEssentialFilters(): void {
        let right = window.LPM_COMPONENT.rightComponent.childComponent;

        if ( right.setEssentialFilters ) {
            right.setEssentialFilters(this.getEssentialFilters());
        }
    }

    private initLayerKeyFields(): void {
        _.each(this._map.getViewLayers(), function(layer, idx) {
            let layrKeyList: Array<any> = new Array();
            layrKeyList = layer.properties.layrKeys.split('|');

            layer.properties.layrKeys = layrKeyList;
            layer.properties.layrKeyField = layrKeyList[0];
        });
    }

    public createMap(): Promise<any> {
        return new Promise<any>((resolve)=> {
            let options = this.getMapOptions();
            MapGeo.create('map', 'BASEMAP', options).then(function (map) {
                this._map = map;
                // this.heatMapCtrl = new HeatMap(this._map);
                this.filterMapCtrl = new FilterMap(this._map, this.lpmService);

                if ( this.isHeatMap ) {
                    this._map.setCustomMapType('HEATMAP');
                }

                this.addMapEventListener();
                resolve(map);
            }.bind(this));
        });
    }

    private getMapOptions(): Object {

        return {
            IPM_URL: this.lpmService.getIpmUrl(),
            GEOSERVER_URL: this.lpmService.getGeoServerUrl(),
            WEB_URL: this.lpmService.getIpmWebUrl(),
            locateControl : true,
            zoomSliderControl: true,
            systemConfig: true,
            clickSelect: {
                isSingle: false,
                tolerance: 5
            },
            location: {
                zoom: 13,
                center: [37.56659363102509, 126.98511991297838]
            }
        };
    }

    private addMapEventListener(): void {
        this._map.on('mg-load-end-vector', this.drawHeatMap, this);
        this._map.on('mg-selected-features', this.onSelectedFeatures, this);
        this._map.on('mg-clear-heatmap', this.onClearHeatMapLayer, this);
        this._map.on('mg-map-loading-start', this.onLoadingStartMap, this);
        this._map.on('mg-map-loading-end', this.onLoadingEndMap, this);
        this._map.on('mg-map-heatmap-loading-end', this.onLoadingEndHeatMap, this);
    }

    private onLoadingStartMap(): void {
        this.isLoadingVector = true;
        this.isLoading = true;
    }

    private onLoadingEndMap(): void {
        this.isLoadingVector = false;
        this.setLoadingEnd();
        //this.setLayerFillOpacity();
    }

    private onLoadingEndHeatMap(params: any): void {
        if ( params.layerName ) {
            let heatMapCtrl = this.getCurHeatMapCtrl(params.layerName);
            if ( heatMapCtrl ) {
                heatMapCtrl.setHeatMapIsDrawing(false);
            }
        }

        this.setLoadingEnd();
    }

    private setLoadingEnd(): any {
        let isLoadingHeatMap = _.find(this.heatMapCtrlList, function(heatMapCtrl, idx) {
            return heatMapCtrl.getIsHeatMapDrawing();
        });

        if ( !isLoadingHeatMap && !this.isLoadingVector ) {
            this.isLoading = false;
        }
    }

    private onClearHeatMapLayer(params): void {
        if ( params.layerName ) {
            this.clearHeatMapLayerByName(params.layerName);
        }
    }

    private drawHeatMap(params): void {
        if ( params.data && params.properties ) {

            let heatMapCtrl = this.getCurHeatMapCtrl(params.properties.name);

            if ( heatMapCtrl ) {
                heatMapCtrl.redrawHeatMap(params.data[0]);
                return;
            } else {
                this.heatMapCtrlList.push(new HeatMap(this._map, params));
                heatMapCtrl = this.getCurHeatMapCtrl(params.properties.name);
                heatMapCtrl.redrawHeatMap(params.data[0]);
            }

        }
    }

    private getCurHeatMapCtrl(layerName: string): any {
        return _.find(this.heatMapCtrlList, function(heatMapCtrl, idx) {
            return heatMapCtrl.getLayerName() == layerName;
        });
    }

    private onSelectedFeatures(selEvent: any): void {

        if (  _.size(selEvent.features) == 0 ) return;

        if ( _.size(selEvent.features) == 1 ) {
            this.setVisibleFeatureProperties(selEvent.features[0].feature);
            return;
        }

        this.setVisibleSelectFeatures(selEvent);
    }

    private setVisibleFeatureProperties(feature: any): void {
        let layer = this._map.getLayerById(feature.layerName);
        if ( layer ) {

            window.LPM_COMPONENT.leftComponent.setFeatureProperty({
                layerAlias: layer.getLayerAliasName(),
                properties: feature.properties
            });

            if ( window.LPM_COMPONENT.leftUIType != 'lpm-prop' ) {
                window.LPM_COMPONENT.leftUIType = 'lpm-prop';
            }
        }
    }

    private setVisibleSelectFeatures(selEvt: any): void {

        this.selFeatureList = [];

        let containerPt = this._map.latLngToContainerPoint({
            lat: selEvt.target.locateControl._lat.text,
            lng: selEvt.target.locateControl._lng.text
        });

        this.selPopupLoc.ptLeft = containerPt.x + 'px';
        this.selPopupLoc.ptTop = containerPt.y + 'px';

        _.each(selEvt.features, function(item, idx) {
            this.selFeatureList.push({
                name: item.feature.properties.id,
                feature: item
            });
        }.bind(this));


        this.isFeatureSelect = true;
    }

    public onClickLayerViewCtrl(evt: any) {
        let layer = this._map.getLayerById(evt.target.id);
        if ( layer ) {
            layer.setVisible(evt.target.checked);
        }

        this.refreshLayerLegend();
    }

    public onClickAnalLayerViewCtrl(evt: any) {
        let layer = null;

        layer = this._map.getLayerById(evt.target.id);
        if ( layer ) {

        }
        this.refreshLayerLegend();
    }

    private clearHeatMapAllLayers(): void {
        _.each(this.heatMapCtrlList, function(heatMapCtrl, idx) {
            heatMapCtrl.clearHeatMapLayer();
        });
    }

    private clearHeatMapLayerByName(layerName: string): void {
        let heatMapCtrl: any = _.find(this.heatMapCtrlList, function(ctrl, idx) {
            return ctrl.getLayerName() == layerName;
        });

        if ( heatMapCtrl ) {
            heatMapCtrl.clearHeatMapLayer();
        }
    }

    public setBaseMapOpacity(opacity: number) {
        this._map.getPanes().tilePane.style.opacity = opacity;
    }

    public resizeMap(): void {
        this._map.invalidateSize();
    }

    private getApplyFilterLayer(): any {
        return this._map.getLayerById(this.getLayrGrpDruidNm());
    }

    public setCustomUserFilters(filterList: Array<any>): void {

        if ( this.filterMapCtrl )

        this.filterMapCtrl.setMapCustomFilter(this.getApplyFilterLayer(), filterList);

        // 행정경계 필터 시 중심점 설정 (단일 선택만)
        for(let i=0; i<filterList.length; i++) {
            if(filterList[i].scrnClNm == 'address') {
                let addrList = filterList[i].fltrVal[0].code;
                let addrNm = null;
                let addrDs = null;
                let addrCode = null;
                let addrCodeList = null;

                if(addrList.length == 1) {
                    addrNm = 'sido';
                    addrDs = 'dcoa_sido_brdr_1d';
                } else if(addrList.length == 2) {
                    addrNm = 'sgg';
                    addrDs = 'dcoa_sgg_brdr_1d';
                } else {
                    addrNm = 'emd';
                    addrDs = 'dcoa_emd_brdr_1d';
                }

                for(let j=0; j<addrList.length; j++) {
                    if(j == 0) {
                        addrCode = addrList[j];
                    } else {
                        addrCode += addrList[j];
                    }
                }
                addrCodeList = {'addrNm': addrNm, 'addrDs': addrDs, 'addrCode': addrCode};

                this.lpmService.getAddrCenterPoint(addrCodeList).then(result=>{
                    if (result.code === CommonConstant.CODE.RESULT_CODE.SUCCESS) {
                        if(result.data.length > 0) {
                            MapGeo.MAP.setView(new L.latLng(result.data[0].lat, result.data[0].lng), 13);
                        }
                    } else {
                        //Alert.error(this.translateService.instant('COMMON.MESSAGE.ERROR', '서버 에러가 발생하였습니다. 관리자에게 문의하세요.'));
                        Alert.warning('서버 에러가 발생하였습니다. 관리자에게 문의하세요.(Druid Server)');
                    }
                });

                break;
            }
        }
    }

    public onClickFeatureSelect(event: any, feature: any): void {
        this.setVisibleFeatureProperties(feature.feature.feature);
        event.stopPropagation();
        event.preventDefault();
    }

    public onMouseOverFeatures(model: any): void {
        this._map.clearSelectLayer();
        this._map.setSelectFeatures([model.feature]);
    }

    public setHeatMapLegendList(layerAliasName: string, legendList: Array<any>): void {
        this.layerLegends[layerAliasName] = legendList;
    }

    public removeHeatMapLegend(layerAliasName: string): void {
        delete this.layerLegends[layerAliasName];
    }

    public onClickShowLegend(): void {
        this.isOpenLegend = !this.isOpenLegend;
    }

    public clearSelectLayers(): void {
        this._map.clearSelectLayer();
    }

    private clearAllLayers(): void {
        _.each(this._map.getViewLayers(), function(layer) {
            if ( layer.properties.type != 'vector' ) {
                let heatCtrl = this.getCurHeatMapCtrl(layer.getLayerName());
                if ( heatCtrl ) {
                    heatCtrl.clearHeatMapLayer();
                }
            } else {
                if ( layer.isVisible() ) {
                    layer.setVisible(false);
                }
            }
        }.bind(this));
    }

    private refreshLayerLegend(): void {

        _.each(this._map.getViewLayers(), function(layer) {
            if ( layer.properties.layrGrpId != '100' && layer.properties.type == 'vector' ) {
                if ( layer.isVisible() ) {
                    let keyField: string = layer.properties.layrKeyField;

                    let styles = L.MG.SysCfg._styleCfg.getStylesByLayerName(layer.getLayerName());
                    this.layerLegends[layer.getLayerAliasName()] = [];
                    _.each(styles, function(item, idx) {
                        let keyProp = item.originStyle[keyField];
                        let value: string = keyProp.min + '~' + keyProp.max;

                        if ( keyProp.min == keyProp.max ) {
                            value = keyProp.min;
                        }

                        this.layerLegends[layer.getLayerAliasName()].push({
                            color: item.originStyle.fillColor,
                            value: value
                        });
                    }.bind(this));
                } else {
                    delete this.layerLegends[layer.getLayerAliasName()];
                }
            }
        }.bind(this));
    }

    public onClickViewCtrlFilter(event: any): void {

        if(event.target.parentNode.nextElementSibling.style.display == '') {
            event.target.parentNode.nextElementSibling.style.display = 'none';
        } else if(event.target.parentNode.nextElementSibling.style.display == 'none') {
            event.target.parentNode.nextElementSibling.style.display = 'block';
        } else if(event.target.parentNode.nextElementSibling.style.display == 'block') {
            event.target.parentNode.nextElementSibling.style.display = 'none';
        }

    }

    public getTempFirstSelectLayrGrpId(): string {
        //임시
        if ( this._map ) {
            let layer_sub = $('.layer_sub').children();
            let layer = this._map.getLayerById(layer_sub.eq(0).children().eq(0).find('input')[0].id);

            if ( layer ) {
                return layer.properties.layrGrpId;
            }
        }

        return null;
    }

    public getTempFirstSelectLayer(): string {

        if(this.firstDataSetList[0].layrGrpId != '200') {
            return this._map.getLayerById(this.firstDataSetList[0].name);
        }

        return null;
    }

    public setHeatMapOpacity(layerName: string, opacity: number) {
        let heatMapCtrl = this.getCurHeatMapCtrl(layerName);

        if ( heatMapCtrl ) {
            heatMapCtrl.getHeatMap().heatmap.canvas.style.opacity = Number(opacity/100);
            //this.jQuery(heatMapCtrl.getHeatMap().heatmap.canvas)[0].style.opacity = Number(opacity/100);
        }
    }

    public setFeatureOpacity(layerName: string, opacity: number) {
        let layer: any = this._map.getLayerById(layerName);
        if ( layer ) {

            if ( layer.properties.type == 'vector' ) {
                if ( opacity == 0 ) {
                    layer.setStyle({fill: false});
                } else {
                    layer.setStyle({fill: true});
                    layer.setStyle({fillOpacity: opacity});
                }

            } else {
                this.setHeatMapOpacity(layerName, opacity);
            }
        }
    }

    public getMap(): any {
        return this._map;
    }

    public setVenderId(venderId: string): void {
        this.venderId = venderId;
    }

    public getVenderId(): string {
        return this.venderId;
    }

    public setLayrGrpItem(item: any): void {
        this.layrGrpItem = item;
    }

    public getLayrGrpDruidNm(): string {
        if ( this.layrGrpItem ) {
            return this.layrGrpItem.druidNm;
        }

        return null;
    }

    public getLayrGrpId(): string {
        if ( this.layrGrpItem ) {
            return this.layrGrpItem.layrGrpId;
        }

        let left: any = window.LPM_COMPONENT.leftComponent.childComponent;

        return ( left.getLayrGrpId ) ? left.getLayrGrpId() : null;
    }

    public getBmrkLayrGrpId(): string {
        let venderId: string = this.getVenderId();

        if ( venderId ) {
            return this.getLayrGrpId() + '_' + venderId;
        }

        return this.getLayrGrpId();
    }
}
