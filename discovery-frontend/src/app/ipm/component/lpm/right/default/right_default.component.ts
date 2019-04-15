import { Component, OnInit, ElementRef, Injector, Input, IterableDiffers } from '@angular/core';
import { Observable } from "rxjs/Observable";
import { AbstractComponent } from '../../../../../common/component/abstract.component';
import { CommonConstant } from '../../../../common/constant/common-constant';
import { LpmService } from '../../../../service/lpm/lpm.service';
import { IcpmService } from "../../../../service/icpm/icpm.service";
import { BmrkService } from '../../../../../ipm/common/service/bmrk/bmrk.service';
import { AppBean } from '../../util/appBean';
import { TranslateService } from "@ngx-translate/core";
import { Alert } from '../../../../../common/util/alert.util';

import * as _ from 'lodash';

declare var window: any;

@Component({
    selector: 'right-default',
    templateUrl: `./right_default.component.html`,
    styles: []
})

export class RightDefaultComponent extends AbstractComponent implements OnInit {

    private _appBean: any;

    public eventCtrl: any;

    /** Filter 관련 소스 */

    // 서브 메뉴별 UI 표현을 위한 메타정보
    @Input()
    public metaInfo: any;

    // 즐겨찾기 저장용 필터 데이터
    public filterList: Array<any> = [];

    // 필터목록 모델 변경 시, 이벤트 check
    public iterableDiffers: any;

    // 생성자
    constructor(
        protected elementRef: ElementRef,
        protected injector: Injector,
        private lpmService: LpmService,
        private bmrkService: BmrkService,
        private icpmService: IcpmService,
        private _iterableDiffers: IterableDiffers
    ) {
        super(elementRef, injector);
        this._appBean = injector.get(AppBean);
        this.eventCtrl = this._appBean.getEventCtrl();
        this.iterableDiffers = this._iterableDiffers.find([]).create(null);
    }

    ngOnInit() {

    }

    ngDoCheck() {
        let changes = this.iterableDiffers.diff(this.filterList);
        if ( changes ) {
            this.refreshView();
        }
    }

    private refreshView(): void {

        let center: any = window.LPM_COMPONENT.centerComponent.childComponent;
        let south: any = center.southComponent.childComponent;

        if ( center.getLayrGrpId ) {
            if ( center.getLayrGrpId() == '200' ) {
                // 변경된 filter에 의한 그래프, 차트 refresh
                if ( south && south.redrawSouthSector ) {
                    south.redrawSouthSector(this.filterList);
                }
            }
        }

        // 변경된 filter에 의한 map filter 설정
        center.setCustomUserFilters(this.filterList);
    }

    public clearSubMenuUIInfo(): void {
        this.metaInfo = null;
    }

    public clearFilterList(): void {
        this.filterList = [];
    }

    // 서브 메뉴별 필터 화면 갱신
    public setUIMetaInfo(params: any): void {
        this.clearSubMenuUIInfo();

        this.lpmService.getLpmFltrBas(params.fltrNm).then(result=> {
            if ( result.code === CommonConstant.CODE.RESULT_CODE.SUCCESS ) {
                window.LPM_COMPONENT.rightComponent.metaInfo = _.extend(result.data, params);
                this.eventCtrl.isRightKindHide = false;
            }
        });
    }

    // INIT 필수 필터 조건
    public setEssentialFilters(essentialFilters: Array<any>) {
        _.each(essentialFilters, function(item, idx) {
            switch(item.scrnClNm) {
                case 'datetime':
                    let filter = this.getEssentialDateTimeFilter(item);
                    if ( filter ) {
                        this.filterList.push(filter);
                    }
                break;
            }
        }.bind(this));
    }

    private getEssentialDateTimeFilter(model: any): any {

        let center: any = window.LPM_COMPONENT.centerComponent.childComponent;
        if ( center.getLayrGrpDruidNm ) {
            let druidNm: string = center.getLayrGrpDruidNm();
            let usedHour: boolean = false;

            if(center.getLayrGrpId() == '200') {
                usedHour = true;
            } else {
                usedHour = false;
            }

            if ( druidNm ) {
                let result: any = {};

                let dateTime = this.getDefaultDateTime();

                let dateTimeCode = dateTime.start.year + dateTime.start.month + dateTime.start.date + dateTime.start.hour + '~'
                        + dateTime.end.year + dateTime.end.month + dateTime.end.date + dateTime.end.hour;

                let dateTimeString = dateTime.start.year + '/' + dateTime.start.month + '/' + dateTime.start.date + ' ' + dateTime.start.hour + '시 ~ '
                        + dateTime.end.year + '/' + dateTime.end.month + '/' + dateTime.end.date + ' ' + dateTime.end.hour + '시';

                result = {
                    druidNm: druidNm,
                    dtNm: dateTimeString,
                    fltrVal: [{code: dateTimeCode, name: dateTimeString}],
                    scrnClNm: model.scrnClNm,
                    scrnNm: model.scrnNm,
                    fltrDelYn: model.fltrDelYn,
                    fltrNm: model.fltrNm,
                    usedHour : usedHour
                }

                return result;
            }
        }

        return null;
    }

    private getDefaultDateTime(): any {

        let date: any = new Date();

        let year: string = date.getFullYear().toString();
        let month: string = (date.getMonth() + 1).toString();
        let day: string = (date.getDate()-1).toString();
        let hour: Array<string> = [];
        let center: any = window.LPM_COMPONENT.centerComponent.childComponent;

        if(center.getLayrGrpId() == '200') {
            hour.push('09');
            hour.push('10');
        } else if(center.getLayrGrpId() == '300') {
            hour.push('09');
            hour.push('10');
        } else if(center.getLayrGrpId() == '400') {
            hour.push('09');
            hour.push('10');
        } else {
            hour.push('00');
            hour.push('23');
        }

        return {
            start: {
                year: year,
                month: month.length == 1 ? '0'+ month : month,
                date: day.length==1 ? '0' + day : day,
                hour: hour[0]
            },

            end: {
                year: year,
                month: month.length == 1 ? '0'+ month : month,
                date: day.length==1 ? '0' + day : day,
                hour: hour[1]
            }
        }
    }

    // 필터 조건 선택 후, 완료 클릭 시
    public onClickApplyFilter(model: any): void {

        if ( model.scrnClNm == 'datetime' && !this.isValidationDateTime(model) ) return;

        if ( this.metaInfo.fltrNm ) {
            model.fltrNm = this.metaInfo.fltrNm;
            model.fltrDelYn = this.metaInfo.fltrDelYn;
        }

        let index = _.findIndex(this.filterList, function(item, idx) {
            return item.scrnNm == model.scrnNm;
        });


        if ( index > -1 ) {
            this.filterList[index] = model;
            return;
        }

        this.filterList.splice(0, 0, model);
    }

    private isValidationDateTime(model: any): boolean {
        let center: any = window.LPM_COMPONENT.centerComponent.childComponent;
        let result: boolean = true;
        if ( center.getLayrGrpId &&  _.size(model.fltrVal) > 0 ) {
            let layrGrpId: string = center.getLayrGrpId();
            let timeModel = this.getTimeModelByDateTimeFilter(model);

            if ( timeModel ) {

                let diff: number = timeModel.end - timeModel.start;

                switch ( layrGrpId ) {
                    case '200':
                        result = ( diff / (1000*60*60) <= 1 ) ? true : false;
                        if ( !result ) {
                            Alert.warning('1시간 간격으로 선택해주시기 바랍니다.');
                        }
                    break;
                    case '300':
                    case '400':
                        result = ( diff / (1000*60*60) <= 24 ) ? true : false;
                        if ( !result ) {
                            Alert.warning('24시간 간격으로 선택해주시기 바랍니다.');
                        }
                    break;
                }
            }
        }

        return result;
    }

    private getTimeModelByDateTimeFilter(model: any): any {
        let code: string = model.fltrVal[0].code;
        let codeSplit: Array<string> = code.split('~');

        if ( _.size(codeSplit) == 2 ) {
            return {
                start: new Date(
                    Number( codeSplit[0].substring(0,4) ),
                    Number( codeSplit[0].substring(4,6) ) - 1,
                    Number( codeSplit[0].substring(6,8) ),
                    Number( codeSplit[0].substring(8,10) )
                ).getTime(),
                end: new Date(
                    Number( codeSplit[1].substring(0,4) ),
                    Number( codeSplit[1].substring(4,6) ) - 1,
                    Number( codeSplit[1].substring(6,8) ),
                    Number( codeSplit[1].substring(8,10) )
                ).getTime(),
            }
        }

        return null;
    }

    // 필터 관리 목록 NOT 버튼 클릭 시,
    public onClickReverseFilterCondition(event: any, index: number): void {
        event.stopPropagation();
        this.filterList[index].type = ( this.filterList[index].type ) ? undefined : 'not';

        // 내부 특정 object 수정시, change event check 못함.
        this.refreshView();
    }

    // 필터 관리 목록 TEXT 표현을 위한 Replace
    public getReplaceFilterNm(dtName: string): string {
        return dtName.replace(/\|/gi, ', ');
    }

    // 필터 관리 목록에서 필터 삭제 시,
    public onClickRemoveFilter(evt: any, model: any): void {
        evt.stopPropagation();

        this.removeFilterModel(model);
    }

    private removeFilterModel(model: any): void {
        let index: number = _.findIndex(this.filterList, function(item, idx) {
            return item.fieldNm == model.fieldNm;
        });

        if ( index > -1 ) {
            this.filterList.splice(index, 1);
        }
    }

    public updateFilterList(model: any): void {
        this.filterList = _.cloneDeep(model);
    }

     /** 북마크 관련 소스 */

    // 저장된 즐겨찾기 목록
    public bmrkList: Array<any> = [];

    // 현재 즐겨찾기의 저장 모드 여부
    public isBmrkSave: boolean = false;

    // 즐겨찾기 추가/수정 이름
    public bmrkTitle: string;

    // 현재 선택되어 있는 즐겨찾기 모델
    private _curBmrkModel: any;

    // 즐겨찾기 추가 팝업 여부
    public addBmrkPopup: boolean = false;

    // 즐겨찾기 수정 팝업 여부
    public modBmrkPopup: boolean = false;

    // 팝업 시, modal 여부
    public isBlackOverlay: boolean = false;

    public setBmrkList(layrGrpId: string): void {

        this.bmrkService.getUserBmrkDtlList('lpm_' + layrGrpId).then(result => {
            if ( result.code === CommonConstant.CODE.RESULT_CODE.SUCCESS ) {
                if ( result.data )
                    this.bmrkList = result.data;
            }
        });
    }

    public onClickShowBmrkPopup(isShow: boolean): void {
        this.bmrkTitle = '';
        this.addBmrkPopup = isShow;
        this.isBlackOverlay = isShow;
    }

    public onClickAddBmrk(): void {
        let self: this = this;

        if (!this.bmrkTitle.trim()) {
            Alert.error(this.translateService.instant('QUICK.MENU.REQUEST.POPUP.PLACEHOLDER.TITLE', '제목을 입력하세요.'));
            return;
        }

        let center: any = window.LPM_COMPONENT.centerComponent.childComponent;
        this.bmrkService.addUserBmrkDtl(this.bmrkTitle, 'lpm_' + center.getBmrkLayrGrpId(), this.filterList).then(result=> {
            if (result.code == CommonConstant.CODE.RESULT_CODE.SUCCESS) {
                return this.bmrkService.getUserBmrkDtlList('lpm_' + center.getBmrkLayrGrpId());
            } else {
                Alert.error(self.translateService.instant('COMMON.MESSAGE.ERROR', '오류가 발생하였습니다.'));
            }
        }).then(result=> {
            if (result.code === CommonConstant.CODE.RESULT_CODE.SUCCESS) {
                this.bmrkList = result.data;
            }

            this.isBmrkSave = true;
            Alert.success(this.translateService.instant('COMMON.MESSAGE.CREATE', '등록되었습니다.'));
        });

        this.onClickShowBmrkPopup(false);
    }

    public onClickShowBmrkModPopup(isShow: boolean): void {
        if ( isShow ) {
            this._curBmrkModel = ( this._curBmrkModel ) ? this._curBmrkModel : _.first(this.bmrkList);
            this.bmrkTitle = this._curBmrkModel.bmrkNm;
        }

        this.modBmrkPopup = isShow;
        this.isBlackOverlay = isShow;
    }

    public onClickUpdateBmrk(): void {
        let self: this = this;
        this.bmrkService.editUserBmrkDtl(this._curBmrkModel.bmrkUid, this.bmrkTitle, this.filterList).then(result=>{
            let center: any = window.LPM_COMPONENT.centerComponent.childComponent;

            if (result.code === CommonConstant.CODE.RESULT_CODE.SUCCESS) {
                return this.bmrkService.getUserBmrkDtlList('lpm_' + center.getLayrGrpId());

            } else {
                Alert.error(self.translateService.instant('COMMON.MESSAGE.ERROR', '오류가 발생하였습니다.'));
            }
        }).then(result=>{
            if (result.code === CommonConstant.CODE.RESULT_CODE.SUCCESS) {
                self.bmrkList = result.data;
                let item: any = _.find(self.bmrkList, function(bmrk, idx) {
                    return self._curBmrkModel.bmrkUid === bmrk.bmrkUid;
                });

                if ( item ) {
                    self._curBmrkModel = item;
                    this.bmrkTitle = item.bmrkNm;
                    Alert.success(self.translateService.instant('COMMON.MESSAGE.MODIFY', '수정되었습니다.'));
                    return;
                }
            }
        });

        this.onClickShowBmrkModPopup(false);
    }

    public onClickReset(): void {
        this.filterList = _.filter(this.filterList, function(item, idx) {
            return item.fltrDelYn == 'N';
        });
    }

    public onChangeBmrk(model: any): void {
        this._curBmrkModel = model;

        this.bmrkTitle = model.bmrkNm;

        let item: any = _.find(this.bmrkList, function(bmrk, idx) {
            return bmrk.bmrkUid === model.bmrkUid;
        });

        if ( item && item.fltrDatVal ) {
            this.updateFilterList(item.fltrDatVal);
            this.isBmrkSave = true;
        } else {
            this.clearFilterList();
            this.isBmrkSave = false;
        }
    }

    public onClickRemoveBmrk(index: number) {

        if ( this._curBmrkModel ) {
            if ( this._curBmrkModel.bmrkUid == this.bmrkList[index].bmrkUid ) {
                this.bmrkTitle = '';
                this.clearFilterList();
                this.isBmrkSave = false;
            }
        }

        this.bmrkList.splice(index, 1);
    }

    public onClickUpload(): void {

    }

    public getFilterList(): Array<any> {
        return this.filterList;
    }
}
