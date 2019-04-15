import { Component, OnInit, ElementRef, Injector, Input } from "@angular/core";
import { AbstractComponent } from '../../../../../common/component/abstract.component';
import { RightKindInterface } from '../right-kind.component';

import * as _ from 'lodash';

@Component({
    selector: 'specifictime',
    templateUrl: './sftime.component.html',
    styles: []
})

export class SpecificTimeComponent extends AbstractComponent implements OnInit, RightKindInterface {

    // 전달받은 데이터
    @Input()
    public menuDetailData: any;

    // 생성자
    constructor(
        protected elementRef: ElementRef,
        protected injector: Injector
    ) {
        super(elementRef, injector);
    }

    ngOnInit() {

    }

    public getFilterData(): any {


    }
}
