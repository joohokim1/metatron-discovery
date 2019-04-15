import { Component, OnInit, ElementRef, Injector, Input } from '@angular/core';
import { AbstractComponent } from '../../../../../common/component/abstract.component';
import { RightKindInterface } from '../right-kind.component';

import * as _ from 'lodash';

@Component({
	selector: 'custmulticheck',
	templateUrl: './custMultiCheck.component.html',
	styles: []
})

export class CustMultiCheckComponent extends AbstractComponent implements OnInit, RightKindInterface {

	@Input()
	public menuDetailData: any;

	public fltrVal: Array<any> = [];

	public isAllSelect: boolean = false;

	constructor(
		protected elementRef: ElementRef,
    	protected injector: Injector
	) {
		super(elementRef, injector);
	}

	ngOnInit() {
		this.setLinkList();
	}

	private setLinkList(): void {
		if ( this.menuDetailData.fltrVal )
			this.refreshLinkList();
		else
			this.initLinkList();
	}

	private refreshLinkList(): void {
		this.fltrVal = this.menuDetailData.fltrVal;
		_.each(this.fltrVal, function(item, idx) {
			if ( item.code == 'Y' )
				item.chekced = true;
		});
	}

	private initLinkList(): void {
		this.fltrVal = this.menuDetailData.fltrDtlList[0].values;

		_.each(this.menuDetailData.fltrVal, function(value, idx) {
			let model: any = _.find(this.fltrVal, function(item, index) {
				return item.code === value.code;
			}.bind(this));

			if ( model ) {
				model.checked = true;
			}
		});
	}

	public getFilterData(): any {

		let fltrVal: Array<any> = [];
		let dtNm: Array<string> = [];

		_.each(this.fltrVal, function(item, idx) {
			if ( item.checked ) {
				fltrVal.push(item);
				dtNm.push(item.name);
			}
		}.bind(this));

		return {fltrVal: fltrVal, dtNm: dtNm.join(', ')};
	}

	public onClickAllSelect(): any {
		this.isAllSelect = !this.isAllSelect;

		_.each(this.fltrVal, function(item, idx) {
			item.checked = this.isAllSelect;
		}.bind(this));
	}

	public onClickModelSelect(model: any): void {
		model.checked = ( model.checked ) ? false : true;
		this.onCheckedIsAllSelected();
	}

	private onCheckedIsAllSelected(): any {
		let countModel = _.countBy(this.fltrVal, function(item) {
			return item.checked ? 'check' : 'uncheck';
		});

		this.isAllSelect =  ( _.size(this.fltrVal) == countModel.check ) ? true : false;
	}
}
