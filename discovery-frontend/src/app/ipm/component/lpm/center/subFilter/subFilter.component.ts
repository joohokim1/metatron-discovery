import { Component, OnInit, ElementRef, Input, Output, EventEmitter, Injector, ViewChild, Inject, } from '@angular/core';
import { AbstractComponent } from '../../../../../common/component/abstract.component';
import { Alert } from '../../../../../common/util/alert.util';

@Component({
	selector: 'subFilter',
	templateUrl: './subFilter.component.html',
	styles: []
})

export class SubFilterComponent extends AbstractComponent implements OnInit {

	@Input()
	public subFilterMetaInfo: any;

	@Output()
	public applySubFilterEmitter = new EventEmitter<any>();

	@Output()
	public applyRemoveSubFilterEmitter = new EventEmitter<any>();

	@Output()
	public applyCollapseSubFilterEmitter = new EventEmitter<any>();

	@ViewChild('subFilterChild')
	public subFilterChild: any;

	constructor(
		protected elementRef: ElementRef,
		protected injector: Injector
	) {
		super(elementRef, injector)
	}

	ngOnInit() {}

	public onClickApplySubFilter(dtData): void {

		if ( !dtData || !dtData.dtNm.length ) {
			Alert.error('필터 입력값이 없습니다.');
			return;
		}

		let filterData: any = {
			fltrUid: this.subFilterMetaInfo.fltrUid,
			druidNm: this.subFilterMetaInfo.druidNm,
			scrnNm: this.subFilterMetaInfo.scrnNm,
			scrnClNm: this.subFilterMetaInfo.scrnClNm,
			type: this.subFilterMetaInfo.type,
			useYn: this.subFilterMetaInfo.useYn,
			fltrVal: dtData.fltrVal,
			dtNm: dtData.dtNm
		};

		if ( this.subFilterMetaInfo.fltrDelYn && this.subFilterMetaInfo.fltrWoYn ) {
			filterData.fltrDelYn = this.subFilterMetaInfo.fltrDelYn;
			filterData.fltrWoYn = this.subFilterMetaInfo.fltrWoYn;
		}

		this.applySubFilterEmitter.emit(filterData);
	}

	public onClickCollapseSubFilter(event: any): void {
		this.applyCollapseSubFilterEmitter.emit(this.subFilterMetaInfo);
	}

	public onClickRemoveSubFilter(event: any): void {
		this.applyRemoveSubFilterEmitter.emit(this.subFilterMetaInfo);
	}
}
