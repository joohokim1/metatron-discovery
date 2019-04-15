import { Component, OnInit, ElementRef, Input, Output, EventEmitter, Injector, ViewChild } from '@angular/core';
import { AbstractComponent } from '../../../../common/component/abstract.component';

@Component({
  selector: 'lpm-center',
  templateUrl: './center.component.html',
  styles: []
})
export class LPMCenterComponent extends AbstractComponent implements OnInit {

    // 메뉴 상세 데이터
    @Input()
    public centerUIType: any;

    @Input()
    public isRightFlexible: boolean;

    // 필터 컴포넌트
    @ViewChild('centerChild')
    public childComponent: any;

    // 생성자
    constructor(
        protected elementRef: ElementRef,
        protected injector: Injector
    ) {
        super(elementRef, injector);
    }

    ngOnInit() {}
}
