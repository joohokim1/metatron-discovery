import { Component, OnInit, ElementRef, Input, Output, EventEmitter, Injector, ViewChild } from '@angular/core';
import { AbstractComponent } from '../../../../common/component/abstract.component';

import * as _ from 'lodash';

@Component({
  selector: 'lpm-right',
  templateUrl: './right.component.html',
  styles: []
})
export class LPMRightComponent extends AbstractComponent implements OnInit {

    // 메뉴 상세 데이터
    @Input()
    public rightUIType: any;

    @Input()
    public isRightFlexible: boolean;

    // 필터 컴포넌트
    @ViewChild('rightChild')
    public childComponent: any;

    public metaInfo: any = null;

    // 생성자
    constructor(
        protected elementRef: ElementRef,
        protected injector: Injector
    ) {
        super(elementRef, injector);
    }

    ngOnInit() {}

    public clearSubMenuUIInfo(): void {
        this.metaInfo = null;
    }

    public setSubMenuUIInfo(responseData: any): void {

        if ( responseData && responseData.scrnClNm == 'address' ) {
            responseData.isAddAddress = false;
        }

        if ( this.childComponent.getFilterList ) {
            let filterList: Array<any> = this.childComponent.getFilterList();

            let model = _.find(filterList, function(item, idx) {
                return item.scrnClNm == responseData.scrnClNm && item.scrnNm == responseData.scrnNm;
            });

            if ( model ) {
                this.metaInfo = _.extend(responseData, model);
                return;
            }
        }

        this.metaInfo = responseData;
    }
}
