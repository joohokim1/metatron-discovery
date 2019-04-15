import { Component, OnInit, ElementRef, Input, Output, EventEmitter, Injector, ViewChild } from '@angular/core';
import { AbstractComponent } from '../../../../common/component/abstract.component';

import * as _ from 'lodash';

declare var Object: any;

@Component({
  selector: 'lpm-left',
  templateUrl: './left.component.html',
  styles: []
})
export class LPMLeftComponent extends AbstractComponent implements OnInit {

    // 메뉴 상세 데이터
    @Input()
    public leftUIType: any;

    @Input()
    public isRightFlexible: boolean;

    public propKeyList: Array<string> = [];

    // 필터 컴포넌트
    @ViewChild('leftChild')
    public childComponent: any;

    public selFeature: any = {
        address: '',
        layerAlias: '',
        properties: {}
    };

    // 생성자
    constructor(
        protected elementRef: ElementRef,
        protected injector: Injector
    ) {
        super(elementRef, injector);
    }

    ngOnInit() {}

    public setFeatureProperty(params: any) : void {
        this.propKeyList = _.without(Object.keys(params.properties), 'id');
        this.selFeature = _.extend(true, this.selFeature, params);
    }
}
