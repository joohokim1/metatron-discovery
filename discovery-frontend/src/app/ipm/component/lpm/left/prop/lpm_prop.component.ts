import { Component, OnInit, ElementRef, Injector, Input } from '@angular/core';
import { AbstractComponent } from '../../../../../common/component/abstract.component';
import { CommonConstant } from '../../../../common/constant/common-constant';
import { LpmService } from '../../../../service/lpm/lpm.service';
import { AppBean } from '../../util/appBean';

import * as _ from 'lodash';

declare var window: any;

@Component({
    selector: 'lpm-prop',
    templateUrl: `./lpm_prop.component.html`,
    styles: []
})

export class LPMPropComponent extends AbstractComponent implements OnInit {

    // 전달받은 데이터
    @Input()
    public selFeature: any;
    @Input()
    public propKeyList: Array<string> = [];

    private _appBean: any;

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

    public onClickClosePropView(): void {
        window.LPM_COMPONENT.leftUIType = 'lpm-menu';
        window.LPM_COMPONENT.centerComponent.childComponent.clearSelectLayers();
    }
}
