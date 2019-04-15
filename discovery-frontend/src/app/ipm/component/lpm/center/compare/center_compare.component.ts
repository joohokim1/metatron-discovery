import { Component, OnInit, ElementRef, Injector, Input } from '@angular/core';
import { AbstractComponent } from '../../../../../common/component/abstract.component';
import { CommonConstant } from '../../../../common/constant/common-constant';
import { AppBean } from '../../util/appBean';
import { HeatMap } from '../heatMap';
import { FilterMap } from '../filterMap';

import { LpmService } from '../../../../service/lpm/lpm.service';

import { TranslateService } from "@ngx-translate/core";
import { Alert } from '../../../../../common/util/alert.util';

import * as _ from 'lodash';

declare var MapGeo: any;
declare var L: any;
declare var window: any;

declare const $: any;

@Component({
    selector: 'center-compare',
    templateUrl: `./center_compare.component.html`,
    styles: []
})

export class CenterCompareComponent extends AbstractComponent implements OnInit {

    // 전달받은 데이터
    @Input()
    public centerUIType: any;

    public addrNmList: Array<string> = [];

    public isMapSync: boolean = false;

    public leftSliderMin: number = 0;
    public leftSliderMax: number = 100;
    public leftSliderStep: number = 1;
    public leftSliderModel: number = 100;

    public rightSliderMin: number = 0;
    public rightSliderMax: number = 100;
    public rightSliderStep: number = 1;
    public rightSliderModel: number = 100;

    public leftIsRaw: boolean = true;
    public rightIsRaw: boolean = true;

    public leftIsOpenLayerList: boolean = false;
    public rightIsOpenLayerList: boolean = false;

    public selFeatureList: Array<any> = [];
    public isFeatureSelect: boolean = false;
    public selPopupLoc: any = {ptTop: '0px', ptLeft: '0px'};

    // 비교군맵
    private constrastMap: any;
    // 대조군맵
    private comparisonMap: any;

    public comparisionLayerList: Array<any> = [];
    public constrastLayerList: Array<any> = [];

    private comparisionFilterMapCtrl: any;
    private constrasFilterMapCtrl: any;

    private leftHeatMapCtrlList: Array<any> = [];
    private rightHeatMapCtrlList: Array<any> = [];

    public leftIsLoadingVector: boolean = false;
    public rightIsLoadingVector: boolean = false;

    public leftIsLoadingHeatMap: boolean = false;
    public rightIsLoadingHeatMap: boolean = false;

    public leftIsLoading: boolean = false;
    public rightIsLoading: boolean = false;

    private defaultOpacity: number = 60;

    public leftLayrGrpNmTitle: string;
    public rightLayrGrpNmTitle: string;

    public leftLayrsNmTitle: string = '';
    public rightLayrsNmTitle: string = '';

    public leftDateTitle: string = '';
    public rightDateTitle: string = '';

    public leftIsOpenLegend: boolean = false;
    public rightIsOpenLegend: boolean = false;

    public objectKeys: any = Object.keys;
    public leftLayerLegends: any = {};
    public rightLayerLegends: any = {};

    public leftSubFilterList: Array<any> = [];
    public rightSubFilterList: Array<any> = [];

    private leftApplyFilterList: Array<any> = [];
    private rightApplyFilterList: Array<any> = [];

    protected jQuery = $;

    // 생성자
    constructor(
        protected elementRef: ElementRef,
        protected injector: Injector,
        private lpmService: LpmService
    ) {
        super(elementRef, injector);
    }

    ngOnInit() {
        this.createMap().then(result=>{
            this.constrastMap = result[0];
            this.comparisonMap = result[1];

            this.constrasFilterMapCtrl = new FilterMap(this.constrastMap, this.lpmService);
            this.comparisionFilterMapCtrl = new FilterMap(this.comparisonMap, this.lpmService);

            this.initLayerKeyFields([this.constrastMap, this.comparisonMap]);

            this.setDataSources();
            this.addMapEventListener();
        });
    }

    private initLayerKeyFields(maps: Array<any>): void {
        _.each(maps, function (map) {
            _.each(map.getViewLayers(), function(layer, idx) {
                let layrKeyList: Array<any> = new Array();
                layrKeyList = layer.properties.layrKeys.split('|');

                layer.properties.layrKeys = layrKeyList;
                layer.properties.layrKeyField = layrKeyList[0];
            });
        });
    }

    private setDataSources(): void {
        let right: any = window.LPM_COMPONENT.rightComponent.childComponent;

        if ( right.setDataSources ) {
            right.setDataSources().then(result=>{
                if ( result ) {
                    this.setLayerList(result.layrGrpId, true);
                    this.setLayerList(result.layrGrpId, false);
                }
            });
        }
    }

    private createMap(): Promise<any> {
        return new Promise<any>((resolve)=> {
            let options = this.getMapOptions();
            MapGeo.create('map1', 'BASEMAP', _.extend({projectName: 'CONSTRAST'}, options)).then(function(map) {
                MapGeo.create('map2', 'BASEMAP', _.extend({projectName: 'COMPARISION'}, options)).then(function(map2) {
                    resolve([map, map2]);
                });
            });
        });
    }

    private getMapOptions(): any {
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
        }
    }

    public setCustomUserFilters(filters: any): void {

        if ( filters.isLeft || filters.isLeft === null )
            this.setCustomUserFiltersByDruidNm({
                map: this.constrastMap,
                filterMapCtrl: this.constrasFilterMapCtrl,
                filterList: filters.filterList,
                venderId: filters.venderId,
                layerList: this.constrastLayerList,
                isLeft: true,
                isAll: filters.isAll,
                isFilter: filters.isFilter
            });

        if ( !filters.isLeft || filters.isLeft === null )
            this.setCustomUserFiltersByDruidNm({
                map: this.comparisonMap,
                filterMapCtrl: this.comparisionFilterMapCtrl,
                filterList: filters.filterCompList,
                venderId: filters.compVenderId,
                layerList: this.comparisionLayerList,
                isLeft: false,
                isAll: filters.isAll,
                isFilter: filters.isFilter
            });

        if ( filters.isChangeDate )
            this.setDateWhenCustomUserFilters(filters.isLeft);

        this.setLayrsNmTitle(true);
        this.setLayrsNmTitle(false);

        let filterList = null;
        if (filters.filterList.length > 0) {
            filterList = filters.filterList;
        } else if (filters.filterCompList.length > 0) {
            filterList = filters.filterCompList;
        }

        if ( filterList ) {
            // 행정경계 필터 시 중심점 설정 (단일 선택만)
            this.setCenterWhenCustomUserFilters(filterList[0].filters, filters.isLeft);
        }

        if (filters.isLeft === null) {
            this.refreshLayerLegend(this.constrastMap, true);
            this.refreshLayerLegend(this.comparisonMap, false);
        } else if ( filters.isLeft ) {
            this.refreshLayerLegend(this.constrastMap, true);
        } else {
            this.refreshLayerLegend(this.comparisonMap, false);
        }
    }

    public setCustomUserFiltersByDruidNm(params: any): void {
        if (params.filterList.length > 0) {
            _.each(params.filterList, function (filter: any) {
                let layer = params.map.getLayerById(filter.druidNm);
                if ( layer ) {
                    if ( !layer.isVisible() || params.isAll || params.isFilter ) {
                        if (layer.getLayerType() == 'heatmap') {
                            this.clearHeatMapLayerByName(layer.getLayerName(), params.isLeft);
                        }

                        this.setVenderId(layer, params.venderId);
                        params.filterMapCtrl.setMapCustomFilter(layer, _.concat(filter.filters, (params.isLeft ? this.leftApplyFilterList : this.rightApplyFilterList)));
                    }

                    _.each(params.layerList, function (layerObject: any) {
                        if (layerObject.name == filter.druidNm && layerObject.visible && !layer.isVisible()) {
                            layer.setLayerType(layerObject.type);
                            layer.setVisible(true);
                        }
                    }.bind(this));
                }
            }.bind(this));
        }

        _.each(params.layerList, function (layerObject) {
            if ( !layerObject.visible ) {
                let layer = params.map.getLayerById(layerObject.name);
                if ( layer && layer.isVisible() )
                    layer.setVisible(false);

                if ( layerObject.type == 'heatmap' )
                    this.clearHeatMapLayerByName(layerObject.name, params.isLeft);
            }
        }.bind(this));
    }

    private setCenterWhenCustomUserFilters(filters: any, isLeft: boolean) {
        for(let i=0; i<filters.length; i++) {
            if(filters[i].scrnClNm == 'address') {
                let addrList = filters[i].fltrVal[0].code;
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
                            if (isLeft === null) {
                                this.constrastMap.setView(new L.latLng(result.data[0].lat, result.data[0].lng), 13);
                                this.comparisonMap.setView(new L.latLng(result.data[0].lat, result.data[0].lng), 13);
                            } else if ( isLeft ) {
                                this.constrastMap.setView(new L.latLng(result.data[0].lat, result.data[0].lng), 13);
                            } else {
                                this.comparisonMap.setView(new L.latLng(result.data[0].lat, result.data[0].lng), 13);
                            }
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

    private setVenderId(layer: any, venderId: string) {
        if ( layer ) {
            if ( layer.properties.layrGrpId != '200' ) {
                if ( venderId == '1' ) {
                    layer.setAddCustomParams(" AND cmco_div_nm = 'SKT' " );
                } else if ( venderId == '2' ) {
                    layer.setAddCustomParams(" AND cmco_div_nm = 'KT' " );
                } else if ( venderId == '3' ) {
                    layer.setAddCustomParams(" AND cmco_div_nm <> 'SKT' AND cmco_div_nm <> 'KT' " );
                }
            }
        }
    }

    private setDateWhenCustomUserFilters(isLeft: boolean): void {
        if (isLeft === true || isLeft === null) {
            let visibleConstrastLayer = _.filter(this.constrastMap.getViewLayers(), function (layer: any) {
                return layer.isVisible();
            });

            if (visibleConstrastLayer.length > 0)
                this.setDate(false, true);
            else
                this.setDate(true, true);
        }

        if (isLeft === false || isLeft === null) {
            let visibleComparisionLayer = _.filter(this.comparisonMap.getViewLayers(), function (layer: any) {
                return layer.isVisible();
            });

            if (visibleComparisionLayer.length > 0)
                this.setDate(false, false);
            else
                this.setDate(true, false);
        }
    }

    private setDate(isReset: boolean, isLeft: boolean): void {
        if ( isReset ) {
            if ( isLeft )
                this.leftDateTitle = '';
            else
                this.rightDateTitle = '';
        } else {
            let right = window.LPM_COMPONENT.rightComponent.childComponent;

            if (right && right.getFilterData ) {
                if ( isLeft )
                    this.leftDateTitle = right.getFilterData()[0].name;
                else
                    this.rightDateTitle = right.getCompFilterData()[0].name;
            }
        }
    }

    private addMapEventListener(): void {
        this.constrastMap.on('mg-load-end-vector', this.drawLeftHeatMap, this);
        this.comparisonMap.on('mg-load-end-vector', this.drawRightHeatMap, this);

        this.constrastMap.on('mg-selected-features', this.onSelectedFeatures, this);
        this.comparisonMap.on('mg-selected-features', this.onSelectedFeatures, this);

        this.constrastMap.on('mg-clear-heatmap', this.onClearLeftHeatMapLayer, this);
        this.comparisonMap.on('mg-clear-heatmap', this.onClearRightHeatMapLayer, this);

        this.constrastMap.on('mg-map-loading-start', this.onLoadingStartLeftMap, this);
        this.constrastMap.on('mg-map-loading-end', this.onLoadingEndLeftMap, this);
        this.constrastMap.on('mg-map-heatmap-loading-end', this.onLoadingEndLeftHeatMap, this);

        this.comparisonMap.on('mg-map-loading-start', this.onLoadingStartRightMap, this);
        this.comparisonMap.on('mg-map-loading-end', this.onLoadingEndRightMap, this);
        this.comparisonMap.on('mg-map-heatmap-loading-end', this.onLoadingEndRightHeatMap, this);

        this.addMapSyncEvent();
    }

    private onLoadingStartLeftMap(): void {
        this.leftIsLoadingVector = true;
        this.leftIsLoading = true;
    }

    private onLoadingEndLeftMap(params: any): void {
        this.leftIsLoadingVector = false;

        _.each(this.constrastLayerList, function (layerObject) {
            if ( layerObject.visible )
                this.setLayerOpacity(layerObject.name, layerObject.opacity, true);
        }.bind(this));

        let isHeatMapDrawing: any = this.getHeatMapDrawing(this.leftHeatMapCtrlList);
        if ( !isHeatMapDrawing ) {
            this.leftIsLoading = false;
            this.refreshLayerLegend(this.constrastMap, true);
        }
    }

    private onClearLeftHeatMapLayer(params: any): void {
        if ( params.layerName ) {
            this.clearHeatMapLayerByName(params.layerName, true);
        }
    }

    private onClearRightHeatMapLayer(params: any): void {
        if ( params.layerName ) {
            this.clearHeatMapLayerByName(params.layerName, false);
        }
    }

    private onLoadingStartRightMap(): void {
        this.rightIsLoading = true;
    }

    private onLoadingEndRightMap(params: any): void {
        this.rightIsLoadingVector = false;

        _.each(this.comparisionLayerList, function (layerObject) {
            if ( layerObject.visible )
                this.setLayerOpacity(layerObject.name, layerObject.opacity, false);
        }.bind(this));

        let isHeatMapDrawing: any = this.getHeatMapDrawing(this.rightHeatMapCtrlList);
        if ( !isHeatMapDrawing ) {
            this.rightIsLoading = false;
            this.refreshLayerLegend(this.comparisonMap, false);
        }
    }

    private onLoadingEndLeftHeatMap(params: any): void {
        let heatMapCtrl = this.getCurHeatMapCtrl(params.layerName, true);
        if ( heatMapCtrl )
            heatMapCtrl.isHeatMapDrawing = false;

        let isHeatMapDrawing: any = this.getHeatMapDrawing(this.leftHeatMapCtrlList);

        if ( !isHeatMapDrawing && !this.leftIsLoadingVector ) {
            this.leftIsLoading = false;
            this.refreshLayerLegend(this.constrastMap, true);
        }
    }

    private onLoadingEndRightHeatMap(params: any): void {
        let heatMapCtrl = this.getCurHeatMapCtrl(params.layerName, false);
        if ( heatMapCtrl )
            heatMapCtrl.isHeatMapDrawing = false;

        let isHeatMapDrawing: any = this.getHeatMapDrawing(this.rightHeatMapCtrlList);

        if ( !isHeatMapDrawing && !this.rightIsLoadingVector ) {
            this.rightIsLoading = false;
            this.refreshLayerLegend(this.comparisonMap, false);
        }
    }

    private getHeatMapDrawing(heatMapCtrlList): any {
        return _.some(heatMapCtrlList, function (item: any) {
            return item.isHeatMapDrawing;
        });
    }

    private drawLeftHeatMap(params: any): void {
        if ( params.data && params.properties ) {

            let heatMapCtrl = this.getCurHeatMapCtrl(params.properties.name, true);

            if ( heatMapCtrl ) {
                heatMapCtrl.redrawHeatMap(params.data[0]);
                return;
            }

            if ( !this.checkHeatmapInGrp(this.constrastLayerList, params.properties.name) ) {
                return;
            }

            heatMapCtrl = new HeatMap(this.constrastMap, params);
            this.leftHeatMapCtrlList.push(heatMapCtrl);
            heatMapCtrl.drawHeatMap(params.data[0]);
        }
    }

    private drawRightHeatMap(params: any): void {
        if ( params.data && params.properties ) {

            let heatMapCtrl = this.getCurHeatMapCtrl(params.properties.name, false);

            if ( heatMapCtrl ) {
                heatMapCtrl.redrawHeatMap(params.data[0]);
                return;
            }

            if ( !this.checkHeatmapInGrp(this.comparisionLayerList, params.properties.name) ) {
                return;
            }

            heatMapCtrl = new HeatMap(this.comparisonMap, params);
            this.rightHeatMapCtrlList.push(heatMapCtrl);
            heatMapCtrl.drawHeatMap(params.data[0]);
        }
    }

    private checkHeatmapInGrp(layerList: any, layerName: string): any {
        return _.some(layerList, function (layerObject) {
            return layerObject.name == layerName;
        });
    }

    private onMapSync(): void {
        let center: any = this.constrastMap.getCenter();
        let zoom: number = this.constrastMap.getZoom();

        this.comparisonMap.setView(center, zoom);
    }

    private onSelectedFeatures(selEvent: any): void {
        let mapId: string = selEvent.target._container.attributes.id.value;

        if (_.size(selEvent.features) == 0) return;

        let map: any = ( mapId == 'map1' ) ? this.constrastMap : this.comparisonMap;

        if ( _.size(selEvent.features) == 1 ) {
            this.setVisibleFeatureProperties(selEvent.features[0].feature, map);
            return;
        }

        this.setVisibleSelectFeatures(selEvent, map);
    }

    private setVisibleFeatureProperties(feature: any, map: any): void {

        let layer = map.getLayerById(feature.layerName);
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

    private setVisibleSelectFeatures(selEvt: any, map: any): void {

        let containerPt = map.latLngToContainerPoint({
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

    public onClickMapSync(): void {
        this.isMapSync = !this.isMapSync;
        this.addMapSyncEvent();
    }

    private addMapSyncEvent(): void {
        if ( this.isMapSync ) {
            this.constrastMap.on('mg-map-loading-end', this.onMapSync, this);
            this.onMapSync();
            return;
        }

        this.constrastMap.off('mg-map-loading-end', this.onMapSync, this);
    }

    private setLayerFillOpacity(): void {

    }

    public onChangeSlider(sliderValue: number, slider: any, isLeftMap: boolean): void {
        let sliderModel = ( isLeftMap ) ? this.leftSliderModel : this.rightSliderModel;
        let map = ( isLeftMap ) ? this.comparisonMap : this.constrastMap;

        sliderModel = Number(sliderValue);
        map.getPanes().tilePane.style.opacity = sliderModel / 100;
    }

    private clearHeatMapAllLayers(isLeftMap: boolean): void {
        let heatMapCtrlList = ( isLeftMap ) ? this.leftHeatMapCtrlList : this.rightHeatMapCtrlList;
        _.each(heatMapCtrlList, function(heatMapCtrl, idx) {
            heatMapCtrl.clearHeatMapLayer();
        });
    }

    public clearSelectLayers(): void {
        this.constrastMap.clearSelectLayer();
        this.comparisonMap.clearSelectLayer();
    }

    // 주소 선택 시, 지도 상단의 주소 표시
    public updateAddrNameList(nameList:Array<any>): void {
        this.addrNmList = nameList;
    }

    // 레이어 목록 collapse 기능
    public onClickViewCtrlLyrList(isLeft: boolean): void {
        if ( isLeft ) {
            this.leftIsOpenLayerList = !this.leftIsOpenLayerList;
            return;
        }

        this.rightIsOpenLayerList = !this.rightIsOpenLayerList;
    }

    // 비교맵 > 기본맵으로 돌아갈 때
    public onClickCloseCompareMap(): void {

        if ( window.LPM_COMPONENT.leftUIType == 'left-prop' ) {
            window.LPM_COMPONENT.leftUIType == 'left-menu';
        }

        window.LPM_COMPONENT.centerUIType = 'center-default';
        window.LPM_COMPONENT.rightUIType = 'right-default';

    }

    public setLayerList(layrGrpId: string, isCompare: boolean): void {
        if ( this.constrastMap && this.comparisonMap ) {
            let layerList: Array<any> = _.concat(
                _.filter((isCompare ? this.comparisonMap : this.constrastMap).getViewLayers(), function(layer) {
                    return layer.properties.layrGrpId != '100' && layrGrpId == layer.properties.layrGrpId;
                }.bind(this)),
                _.filter((isCompare ? this.comparisonMap : this.constrastMap).getViewLayers(), function(layer) {
                    return layer.properties.layrGrpId != '100' && layrGrpId != layer.properties.layrGrpId;
                }.bind(this))
            );

            layerList = _.map(layerList, function (layer: any) {
                return {
                    layrGrpId: layer.properties.layrGrpId,
                    id: (isCompare ? 'right-' : 'left-') + layer.getLayerName(),
                    name: layer.getLayerName(),
                    aliasName: layer.getLayerAliasName(),
                    visible: layer.isVisible(),
                    type: layer.getLayerType(),
                    opacity: this.defaultOpacity,
                    subFltrList: [],
                    subFltrVisible: false
                };
            }.bind(this));

            _.each(isCompare ? this.comparisionLayerList : this.constrastLayerList, function (layerObject: any) {
                this.setUnVisibleLayer(layerObject.name, !isCompare);
            }.bind(this));

            if ( isCompare ) {
                this.comparisionLayerList = layerList;
                this.setDate(true, false);
            } else {
                this.constrastLayerList = layerList;
                this.setDate(true, true);
            }

            this.setLayrGrpNmTitle(layrGrpId, !isCompare);
            this.setLayrsNmTitle(!isCompare);
        }
    }

    private setLayrGrpNmTitle(layrGrpId: string, isLeft: boolean): void {
        let right: any = window.LPM_COMPONENT.rightComponent.childComponent;
        let layrGrp = _.find(right.layrGrpList, function (layrGrp: any) {
            return layrGrpId == layrGrp.layrGrpId;
        });
        let title = layrGrp ? layrGrp.layrGrpNm : '' ;

        if ( isLeft ) {
            this.leftLayrGrpNmTitle = '비교군 ' + title;
        } else {
            this.rightLayrGrpNmTitle = '대조군 ' + title;
        }
    }

    public setUnVisibleLayer(layerName: string, isLeft: boolean): void {
        if ( this.constrastMap && isLeft ) {
            let layer = this.constrastMap.getLayerById(layerName);
            this.clearHeatMapLayerByName(layerName, isLeft);
            layer.setVisible(false);
        } else if ( this.comparisonMap && !isLeft ) {
            let layer = this.comparisonMap.getLayerById(layerName);
            this.clearHeatMapLayerByName(layerName, isLeft);
            layer.setVisible(false);
        }
    }

    public setVisibleLayerEvt(evt: any, layerName: string, isLeft: boolean): void {
        let map = ( isLeft ) ? this.constrastMap : this.comparisonMap;

        if ( map ) {
            if (this.checkVisibleLayerCnt(isLeft) == 2 && evt.target.checked) {
                evt.target.checked = false;
                return;
            }

            let layer: any = map.getLayerById(layerName);

            if ( layer ) {
                let layerList = isLeft ? this.constrastLayerList : this.comparisionLayerList ;

                for (let layerObject of layerList) {
                    if ( layerObject.name == layerName ) {
                        layer.setLayerType(layerObject.type);
                        layerObject.visible = evt.target.checked;

                        this.setSubFilterListToLayerObject(layerName, evt.target.checked, isLeft);

                        break;
                    }
                }

                let right: any = window.LPM_COMPONENT.rightComponent.childComponent;
                right.submitRightKind(isLeft, null, null);
            }

            this.refreshLayerLegend(map, isLeft);
        }
    }

    private checkVisibleLayerCnt(isLeft: boolean): number {
        let visibleLayerCnt = _.countBy(isLeft ? this.constrastLayerList : this.comparisionLayerList, function (layerObject: any): any {
            return layerObject.visible;
        });

        return visibleLayerCnt.true;
    }

    private setSubFilterListToLayerObject (layerName: string, isVisible: boolean, isLeft: boolean): void {
        let layerObject = _.find(isLeft ? this.constrastLayerList : this.comparisionLayerList, function (layerObject) {
            return layerName == layerObject.name;
        });

        if( isVisible && layerObject.subFltrList.length == 0) {
            this.lpmService.getLpmFltrBasList(layerObject.layrGrpId, true).then(function(result) {
                if (result.code === CommonConstant.CODE.RESULT_CODE.SUCCESS) {
                    let subFltrs = [];
                    _.each(result.data, function(item, idx) {
                        _.each(item.fltrList, function(filter, index) {
                            if (filter.scrnClNm != 'datetime') {
                                filter.scrnClNm = 'sf-' + filter.scrnClNm;
                                filter.isVisible = true;
                                filter.layrGrpId = layerObject.layrGrpId;
                                subFltrs.push(filter);
                            }
                        });
                    });

                    layerObject.subFltrList = subFltrs;
                }
            }.bind(this));
        }

        layerObject.subFltrVisible = isVisible;
    }

    public onClickFilterSelect(evt: any, subFltr: any, layerObject: any, isLeft: boolean) : void {
        let subFilterList = isLeft ? this.leftSubFilterList : this.rightSubFilterList ;

        this.lpmService.getLpmFltrBas(subFltr.fltrNm).then(function(result) {
            if (result.code === CommonConstant.CODE.RESULT_CODE.SUCCESS) {
                if( !subFltr.fltrDtlList ) {
                    subFltr.fltrDtlList = result.data.fltrDtlList;
                }

                for(let i=0; i<subFilterList.length; i++) {
                    if(subFilterList[i].layrGrpId == subFltr.layrGrpId && subFilterList[i].scrnNm == subFltr.scrnNm) {
                        subFilterList.splice(i,1);
                        return;
                    }
                }

                subFltr.fltrUid = result.data.fltrUid;
                subFltr.druidNm = layerObject.name;
                subFilterList.push(subFltr);
            }
        }.bind(this));
    }

    private setLayrsNmTitle(isLeft: boolean): void {
        if ( this.constrastMap && this.comparisonMap ) {
            let layrsNmTitle: string = '';
            let viewLayers = _.filter((isLeft ? this.constrastMap : this.comparisonMap).getViewLayers(), function (layer: any) {
                return layer.isVisible();
            });

            _.each(viewLayers, function (viewLayer: any, idx: number) {
                if (idx == 0)
                    layrsNmTitle = '(' + layrsNmTitle;
                else
                    layrsNmTitle = layrsNmTitle + ', ';

                layrsNmTitle = layrsNmTitle + viewLayer.getLayerAliasName();

                if (idx == (viewLayers.length - 1))
                    layrsNmTitle =layrsNmTitle + ')';
            });

            if ( isLeft )
                this.leftLayrsNmTitle = layrsNmTitle;
            else
                this.rightLayrsNmTitle = layrsNmTitle;
        }
    }

    public setLayerType(type: string, layerId: string, isLeft: boolean): void  {
        let map = isLeft ? this.constrastMap : this.comparisonMap ;

        if ( map ) {
            let layer = map.getLayerById(layerId);

            if ( layer ) {
                layer.setLayerType(type);

                let layerList = isLeft ? this.constrastLayerList : this.comparisionLayerList ;

                for (let layerObject of layerList) {
                    if (layerObject.name == layerId) {
                        layerObject.type = type;
                        break;
                    }
                }

                if (type == 'vector')
                    this.clearHeatMapLayerByName(layerId, isLeft);

                layer.refresh(true);
            }

            this.refreshLayerLegend(map, isLeft);
        }
    }

    private clearHeatMapLayerByName(layerName: string, isLeft: boolean): void {
        let heatMapCtrl: any = _.find((isLeft ? this.leftHeatMapCtrlList : this.rightHeatMapCtrlList), function(ctrl, idx) {
            return ctrl.getLayerName() == layerName;
        });

        if ( heatMapCtrl ) {
            heatMapCtrl.clearHeatMapLayer();
        }
    }

    public getDruidNms(isLeft: boolean, isAll: boolean): any {
        if ( this.constrastMap && this.comparisonMap ) {
            let leftLayerNames: string[] = this.getLayerNames({
                map: this.constrastMap,
                layerList: this.constrastLayerList,
                isLeft: isLeft,
                isAll: isAll
            });

            let rightLayerNames: string[] = this.getLayerNames({
                map: this.comparisonMap,
                layerList: this.comparisionLayerList,
                isLeft: isLeft,
                isAll: isAll
            });

            return {
                left: leftLayerNames,
                right: rightLayerNames
            };
        }

        return null;
    }

    private getLayerNames(params: any): any {
        let layerNames: string[] = [];

        _.each(params.layerList, function (layerObject: any) {
            let layer = params.map.getLayerById(layerObject['name']);

            if ( layerObject['visible'] )
                layerNames.push(layer.getLayerName());
        });

        return layerNames;
    }

    public setLayerOpacity(layerName: string, opacity: number, isLeft: boolean): void {
        let layer = null;
        if ( this.constrastMap && isLeft ) {
            layer = this.constrastMap.getLayerById(layerName);
        } else if ( this.comparisonMap && !isLeft ) {
            layer = this.comparisonMap.getLayerById(layerName);
        }

        if ( layer ) {
            if ( layer.properties.type == 'vector' ) {
                let isFill: Boolean = ( opacity == 0 ) ? false : true;
                layer.setStyle({fill: isFill, fillOpacity: Number(opacity/100)});
            } else {
                this.setHeatMapOpacity(layerName, opacity, isLeft);
            }
        }
    }

    private setHeatMapOpacity(layerName: string, opacity: number, isLeft: boolean): void {
        let heatMapCtrl = this.getCurHeatMapCtrl(layerName, isLeft);

        if ( heatMapCtrl ) {
            this.jQuery(heatMapCtrl.getHeatMap().heatmap.canvas)[0].style.opacity = Number(opacity/100);
        }
    }

    private getCurHeatMapCtrl(layerName: string, isLeft: boolean): any {
        let heatMapCtrlList = ( isLeft ) ? this.leftHeatMapCtrlList : this.rightHeatMapCtrlList;
        return _.find(heatMapCtrlList, function(heatMapCtrl, idx) {
            return heatMapCtrl.getLayerName() == layerName;
        });
    }

    public onClickShowLegend(isLeft: boolean): void {
        if ( isLeft )
            this.leftIsOpenLegend = !this.leftIsOpenLegend;
        else
            this.rightIsOpenLegend = !this.rightIsOpenLegend;
    }

    private refreshLayerLegend(map: any, isLeft: boolean): void {
        let layerLegends: any = {};

        _.each(map.getViewLayers(), function(layer) {
            if ( layer.properties.layrGrpId != '100' && layer.properties.type == 'vector' ) {
                if ( layer.isVisible() ) {
                    let keyField: string = layer.getProperties().layrKeys;

                    let styles = L.MG.SysCfg._styleCfg.getStylesByLayerName(layer.getLayerName());
                    layerLegends[layer.getLayerAliasName()] = [];
                    _.each(styles, function(item, idx) {
                        let keyProp = item.originStyle[keyField];
                        let value: string = keyProp.min + '~' + keyProp.max;

                        if ( keyProp.min == keyProp.max ) {
                            value = keyProp.min;
                        }

                        layerLegends[layer.getLayerAliasName()].push({
                            color: item.originStyle.fillColor,
                            value: value
                        });
                    }.bind(this));

                    if ( isLeft )
                        this.leftLayerLegends = layerLegends;
                    else
                        this.rightLayerLegends = layerLegends;
                } else {
                    if ( isLeft )
                        delete this.leftLayerLegends[layer.getLayerAliasName()];
                    else
                        delete this.rightLayerLegends[layer.getLayerAliasName()];
                }
            }
        }.bind(this));
    }

    public onClickApplySubFilter (event: any, isLeft: boolean): void {
        let layerObject: any = _.find(isLeft ? this.constrastLayerList : this.comparisionLayerList, function (layerObject): any {
            return layerObject.name == event.druidNm;
        });
        let subFltr: any = _.find(layerObject.subFltrList, function (subFltr: any): any {
            return subFltr.fltrUid == event.fltrUid;
        });

        event = _.cloneDeep(event);
        event.scrnClNm = event.scrnClNm.replace('sf-', '');
        event.fltrNm = subFltr.fltrNm;

        let applyFilterIdx = 0;
        let applyFilter = _.find(isLeft ? this.leftApplyFilterList : this.rightApplyFilterList, function (applyFilter: any, idx: number): any {
            if ( applyFilter.fltrUid == event.fltrUid ) {
                applyFilterIdx = idx;
                return true;
            }
        });

        if ( isLeft ) {
            if ( applyFilter )
                this.leftApplyFilterList[applyFilterIdx] = event;
            else
                this.leftApplyFilterList.push(event);
        } else {
            if ( applyFilter )
                this.rightApplyFilterList[applyFilterIdx] = event;
            else
                this.rightApplyFilterList.push(event);
        }

        let right: any = window.LPM_COMPONENT.rightComponent.childComponent;
        right.submitRightKind(isLeft, null, true);
    }

    public onClickRemoveSubFilter (subFilterMetaInfo: any, isLeft: boolean): void {
        let subFilterList: any = isLeft ? this.leftSubFilterList : this.rightSubFilterList;
        let removeSubFilterIdx: number = 0;
        _.find(subFilterList, function (subFilter: any, idx: number): any {
            if ( subFilter.fltrUid == subFilterMetaInfo.fltrUid ) {
                removeSubFilterIdx = idx;
                return true;
            }
        });

        subFilterList = subFilterList.splice(removeSubFilterIdx, 1);

        let applyFilterList: any = isLeft ? this.leftApplyFilterList : this.rightApplyFilterList;
        let removeApplyFilterIdx: number = 0;
        _.find(applyFilterList, function (applyFilter: any, idx: number): any {
            if ( applyFilter.fltrUid == subFilterMetaInfo.fltrUid ) {
                removeApplyFilterIdx = idx;
                return true;
            }
        });

        applyFilterList = applyFilterList.splice(removeApplyFilterIdx, 1);
    }

    public onClickCollapseSubFilter (subFilterMetaInfo: any, isLeft: boolean): void {
        subFilterMetaInfo.isVisible = !subFilterMetaInfo.isVisible;
    }
}
