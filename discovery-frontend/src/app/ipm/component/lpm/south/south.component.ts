import { Component, OnInit, ElementRef, Input, Output, EventEmitter, Injector, ViewChild } from '@angular/core';
import { AbstractComponent } from '../../../../common/component/abstract.component';

declare var Slick: any;

@Component({
  selector: 'lpm-south',
  templateUrl: './south.component.html',
  styles: []
})

export class LPMSouthComponent extends AbstractComponent implements OnInit {

    // 메뉴 상세 데이터
    @Input()
    public southUIType: any;

    // 필터 컴포넌트
    @ViewChild('southChild')
    public childComponent: any;

    // 생성자
    constructor(
        protected elementRef: ElementRef,
        protected injector: Injector
    ) {
        super(elementRef, injector);
    }

    ngOnInit() {
    }



}
