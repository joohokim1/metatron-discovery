import { Component, OnInit, ElementRef, Injector, Input } from '@angular/core';
import { AbstractComponent } from '../../../../../common/component/abstract.component';
import { CommonConstant } from '../../../../common/constant/common-constant';
import { LpmService } from '../../../../service/lpm/lpm.service';
import { AppBean } from '../../util/appBean';

import * as _ from 'lodash';

declare var window: any;

@Component({
    selector: 'lpm-menu',
    templateUrl: `./lpm_menu.component.html`,
    styles: []
})

export class LPMMenuComponent extends AbstractComponent implements OnInit {

    private _appBean: any;
    public eventCtrl: any;

    public menuList: Array<any> = [];
    public layrGrpList: Array<any> = [];
    public originMenuList: Array<any> = [];
    public isCollapseMenus: boolean = false;

    private layrGrpDruidNm: string = null;
    private originLayrGrpList: Array<any> = [];
    public layrGrpId: string = null;

    private isInitCenterDefaultUICall: boolean = false;
    private essentialFilters: Array<any> = [];

    public venderId: string = null;

    public venderList: Array<any> = [
        {venderId: '1', venderName: 'SKT'},
        {venderId: '2', venderName: 'KT'},
        {venderId: '3', venderName: 'LGU+'},
    ];

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
        this.setLayrGrpList().then(result=>{
            this.setLayrGrpIdBySelAnalLayr();
            this.setVenderId();
            this.setBmrkInit();
            this.setMenuList((this.venderId) ? this.venderId : this.layrGrpId).then(result=>{
                this.updateCollpaseMenusBtn();
            });
        });
    }

    private setBmrkInit(): void {
        let right: any = window.LPM_COMPONENT.rightComponent.childComponent;
        if ( right.setBmrkList ) {
            right.setBmrkList(this.layrGrpId);
        }
    }

    private setVenderId(): void {
        if ( this.layrGrpId == '300' ) {
            let center = window.LPM_COMPONENT.centerComponent.childComponent;

            if ( center.getVenderId ) {
                $('#select_agency').show();
                this.venderId = center.getVenderId();
            }
        }
    }

    private setLayrGrpIdBySelAnalLayr(): void {
        let center = window.LPM_COMPONENT.centerComponent.childComponent;
        if ( center.getTempFirstSelectLayrGrpId ) {
            let layrGrpId = center.getTempFirstSelectLayrGrpId();
            if ( layrGrpId ) {
                this.layrGrpId = layrGrpId;
            }
        }
    }

    public isMenuLoadComplete(): boolean {
        return ( _.size(this.originMenuList) > 0 ) ? true : false;
    }

    public setMenuList(layerGroupId: string): Promise<any> {
        return new Promise<any>((resolve)=> {
            this.lpmService.getLpmFltrBasList(layerGroupId, false).then(function(result) {
                if (result.code === CommonConstant.CODE.RESULT_CODE.SUCCESS) {
                    result.data[0].collapse = true;

                    _.each(result.data, function(item, idx) {
                        let model = _.find(item.fltrList, function(filter, index) {
                            return filter.fltrDelYn == 'N';
                        });

                        if ( model ) {
                            item.collapse = true;
                        }
                    });


                    this.menuList = result.data;
                    this.originMenuList = _.cloneDeep(this.menuList);

                    this.initEssentialFilters();
                    resolve();
                }
            }.bind(this));
        });
    }

    public setLayrGrpList(): Promise<any> {

        let self: this = this;

        return new Promise<any>((resolve)=> {

            self.lpmService.getLpmLayrGrpList().then(function(result) {
                if (result.code === CommonConstant.CODE.RESULT_CODE.SUCCESS) {

                    self.originLayrGrpList = result.data;

                    _.each(result.data, function(item, idx) {
                        if ( item.useYn == 'Y' ) {
                            self.layrGrpList.push({
                                druidNm: item.druidNm,
                                layrGrpNm: item.layrGrpNm,
                                layrGrpId: item.layrGrpId
                            });
                        }
                    });

                    if ( _.size(self.layrGrpList) > 0 ) {

                        let center = window.LPM_COMPONENT.centerComponent.childComponent;

                        if ( center.setLayrGrpItem ) {
                            center.setLayrGrpItem(self.layrGrpList[0]);
                        }

                        self.layrGrpId = self.layrGrpList[0].layrGrpId;
                    }

                    resolve(result.data);
                }
            });
        });
    }

    public getEssentialFilters(): Array<any> {
        return this.essentialFilters;
    }

    private initEssentialFilters(): void {

        if ( _.size(this.essentialFilters) > 0 ) return;

        _.each(this.originMenuList, function(item, idx) {
            _.each(item.fltrList, function(subMenu, index) {
                if ( subMenu.fltrDelYn == 'N' ) {
                    this.essentialFilters.push(subMenu);
                }
            }.bind(this));
        }.bind(this));
    }

    public getMenuList(): Array<any> {
        return this.menuList;
    }

    public getInnerSubMenuList(menuList: Array<any>): Array<any> {
        return _.map(menuList, function(item, idx) {
            if ( _.has(item, 'leafYn') ) {
                if ( item.leafYn.toUpperCase() == 'N' ) {
                    return this.getInnerSubMenuList(item.menuList);
                }

                return item;
            }
        }.bind(this));
    }

    public onClickSearchMenu(searchValue: string): void {

        if ( searchValue == '' ) {
            this.menuList = this.originMenuList;
            this.updateCollpaseMenusBtn();
            return;
        }

        let tempCopyMenuList: Array<any> = _.cloneDeep(this.originMenuList);

        let matchMenuList: Array<any> = [];

        _.each(tempCopyMenuList, function(item, idx) {
            let matchSubMenuList: Array<any> = [];
            if ( _.has(item, 'leafYn') ) {
                if ( item.leafYn.toUpperCase() == 'Y' && item.scrnNm.includes(searchValue) ) {
                    matchMenuList.push(item);
                } else {
                    _.each(this.getInnerSubMenuList(item.fltrList), function(innerMenu, index) {
                        if ( innerMenu instanceof Array ) {

                        } else {
                            if ( innerMenu.scrnNm.includes(searchValue) ) {
                                matchSubMenuList.push(innerMenu);
                            }
                        }
                    }.bind(this));
                }

                if ( _.size(matchSubMenuList) > 0 ) {
                    item.fltrList = matchSubMenuList;
                    item.fltrListCnt = _.size(matchSubMenuList);
                    matchMenuList.push(item);
                }
            }
        }.bind(this));

        this.menuList = matchMenuList;
        this.updateCollpaseMenusBtn();
    }

    // 메뉴 모두접기
    public onClickCollapseMenus(): void {
        this.isCollapseMenus = !this.isCollapseMenus;

        _.each(this.getMenuList(), function(item, idx) {
            item.collapse = this.isCollapseMenus;
        }.bind(this));
    }

    // 메뉴별 접기
    public onClickCollpaseMenu(item: any): void {
        item.collapse = ( item.collapse ) ? false : true;
        // 메뉴별 접기 상태에 따라 "전체" 관련 버튼 컨트롤을 해야 함.
        this.updateCollpaseMenusBtn();
    }

    public updateCollpaseMenusBtn(): void {
        let isCollapse: boolean = this.getMenuCollapseStatus();

        // 전체 펼치기 상태인데, isCollapse 가 false이면 컨트롤할 필요 없음
        if ( !this.isCollapseMenus && !isCollapse ) return;

        // 전체 닫아진 상태인데, isCollpase 가 true 이면 컨트롤할 필요 없음
        if ( this.isCollapseMenus && isCollapse ) return;

        this.isCollapseMenus = isCollapse;
    }

    public getMenuCollapseStatus(): boolean {
        let collapseTrueCnt: number = 0;

        let menuList: Array<any> = this.getMenuList();

        _.each(menuList, function(item, idx) {
            if ( item.collapse ) {
                collapseTrueCnt += 1;
            }
        }.bind(this));

        if ( _.size(menuList) == collapseTrueCnt ) {
            return true;
        }

        return false;
    }

    // 각 메뉴별 서브 메뉴 클릭 시 서브 메뉴별 필터 화면 갱신
    public onClickSubMenuRow(elem: any): void {
        let params = {
            fieldNm: elem.target.getAttribute('fieldNm'),
            scrnClNm: elem.target.getAttribute('scrnClNm'),
            scrnNm: elem.target.text
        };

        let self: this = this;

        let right: any = window.LPM_COMPONENT.rightComponent;
        let center: any = window.LPM_COMPONENT.centerComponent.childComponent;
        window.LPM_COMPONENT.rightComponent.clearSubMenuUIInfo();

        this.lpmService.getLpmFltrBas(params.fieldNm).then(result=> {
            if ( result.code === CommonConstant.CODE.RESULT_CODE.SUCCESS ) {
                window.LPM_COMPONENT.onInitCenterUIType(params.fieldNm);
                window.LPM_COMPONENT.onInitRightUIType(params.fieldNm);

                if ( window.LPM_COMPONENT.centerUIType != 'center-compare'
                    && window.LPM_COMPONENT.rightUIType != 'right-compare' ) {
                    right.setSubMenuUIInfo(result.data);                }

                this.eventCtrl.isRightKindHide = false;
            }
        });
    }

    public setEssentialFilters(): void {
        let right: any = window.LPM_COMPONENT.rightComponent.childComponent;
        let center: any = window.LPM_COMPONENT.centerComponent.childComponent;

        let layrGrp = _.find(this.layrGrpList, function(item, idx) {
            return this.layrGrpId == item.layrGrpId;
        }.bind(this));

        if ( layrGrp && center.setLayrGrpItem && right.setEssentialFilters ) {
            center.setLayrGrpItem(layrGrp);
            right.setEssentialFilters(this.essentialFilters);
        }
    }

    public onChangeLayrGrp(layrGrpId: any): void {
        let self: this = this;

        let layrGrp = _.find(this.layrGrpList, function(item, idx) {
            return layrGrpId == item.layrGrpId;
        });

        if ( layrGrp ) {

            let center: any = window.LPM_COMPONENT.centerComponent.childComponent;

            if ( center.setLayrGrpItem ) {
                center.setLayrGrpItem(layrGrp);
            }

            //Vender 초기화
            if(layrGrpId == '200') {
                $('#select_agency').hide();
            } else {
                $('#select_agency').show();
            }

            //선택 그룹 아이디로 설정
            this.layrGrpId = layrGrpId;

            if ( center.refreshAnalLayrList && window.LPM_COMPONENT.centerUIType == 'center-default' ) {
                center.refreshAnalLayrList(layrGrpId);
            }

            //레이어 그룹 선택 시 메뉴 초기화
            this.setMenuList(layrGrpId);

            let right: any = window.LPM_COMPONENT.rightComponent;

            if ( right.clearSubMenuUIInfo && right.childComponent.clearFilterList ) {
                right.clearSubMenuUIInfo();
                right.childComponent.clearFilterList();
            }

            //레이어 그룹 선택 시 즐겨찾기 초기화
            if ( right.childComponent.bmrkList && right.childComponent.setBmrkList ) {
                right.childComponent.bmrkList = [];
                right.childComponent.bmrkTitle = '';
            }

            //레이어 목록 초기화
            center.initDataSet(this.layrGrpId);
            center.subFilterList = [];
            center.subFilterDataList = [];

            if ( right.childComponent.setEssentialFilters ) {
                right.childComponent.setEssentialFilters(this.essentialFilters);
                center.clearAllLayers();
                setTimeout(function() {
                    self.addLayerCustomParameter();
                    center.setDefaultLayers();
                }, 1000);
            }
        }
    }

    public addLayerCustomParameter(): void {
        let center: any = window.LPM_COMPONENT.centerComponent.childComponent;

        if ( center.getTempFirstSelectLayer ) {
            let layer: any = center.getTempFirstSelectLayer();

            if ( layer ) {
                if ( this.venderId == '1' ) {
                    layer.setAddCustomParams(" AND cmco_div_nm = 'SKT' " );
                } else if ( this.venderId == '2' ) {
                    layer.setAddCustomParams(" AND cmco_div_nm = 'KT' " );
                } else if ( this.venderId == '3' ) {
                    layer.setAddCustomParams(" AND cmco_div_nm <> 'SKT' AND cmco_div_nm <> 'KT' " );
                } else {
                    this.venderId = '1';
                    center.setVenderId(this.venderId);
                    layer.setAddCustomParams(" AND cmco_div_nm = 'SKT' " );
                }
            }
        }
    }

    public onChangeLayrAgency(venderId: string): void {
        let self: this = this;

        let right = window.LPM_COMPONENT.rightComponent.childComponent;
        let center = window.LPM_COMPONENT.centerComponent.childComponent;

        if (
                right.clearFilterList && right.setBmrkList && right.setEssentialFilters &&
                center.clearAllLayers && center.setDefaultLayers && center.setVenderId
           ) {

            this.venderId = venderId;

            center.setVenderId(this.venderId);

            //레이어 그룹 선택 시 메뉴 초기화
            let agencyId = this.layrGrpId + '_' + venderId;
            this.setMenuList(agencyId);
            window.LPM_COMPONENT.rightComponent.clearSubMenuUIInfo();
            right.clearFilterList();

            //레이어 그룹 선택 시 즐겨찾기 초기화
            right.bmrkList = [];
            right.setBmrkList(agencyId);

            self.addLayerCustomParameter();
            right.setEssentialFilters(this.essentialFilters);
        }
    }

    public getLayrGrpId(): string {
        return this.layrGrpId;
    }
}
