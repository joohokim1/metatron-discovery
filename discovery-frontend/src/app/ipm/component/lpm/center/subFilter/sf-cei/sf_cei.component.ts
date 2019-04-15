import { Component, OnInit, ElementRef, EventEmitter, Injector, Input, Output } from '@angular/core';
import { AbstractComponent } from '../../../../../../common/component/abstract.component';
import { Alert } from '../../../../../../common/util/alert.util';

import * as _ from 'lodash';

@Component({
	selector: 'sf-cei',
	templateUrl: './sf_cei.component.html',
	styles: []
})

export class SFCEIComponent extends AbstractComponent implements OnInit {

	@Input()
	public subFilterMetaInfo: any;

	@Output()
	public applySubFilterEmitter = new EventEmitter<any>();

	// slider 전역 변수
	public min: number;
	public max: number;
	public step: number;

	// slider의 min, max에 이상/이하 표시
	public overType: string = '';
	public underType: string = '';

	public sliderList: Array<any> = [];

	constructor(
		protected elementRef: ElementRef,
    	protected injector: Injector
	) {
		super(elementRef, injector);
	}

	ngOnInit() {
		this.min = Number(this.subFilterMetaInfo.fltrDtlList[0].values[0]);
	    this.max = Number(this.subFilterMetaInfo.fltrDtlList[0].values[1]);
	    this.step = Number(this.subFilterMetaInfo.fltrDtlList[0].step) || 14;

		//최대, 최소값 +- 값
		if (this.subFilterMetaInfo.fltrDtlList[0].rmk) {
			this.overType = this.subFilterMetaInfo.fltrDtlList[0].rmk.includes('+') ? '+' : '';
			this.underType = this.subFilterMetaInfo.fltrDtlList[0].rmk.includes('-') ? '-' : '';
		}

		// sliderList 초기화
		this.sliderList = [];

		// 선택 필터 클릭에 의한 세팅
		if (this.subFilterMetaInfo.fltrVal) {
			// 선택 필터 데이터 세팅
			let rangeListArr = [];
			for (let item of this.subFilterMetaInfo.fltrVal) {
				rangeListArr.push(item.code);
			}

			for (let item of rangeListArr) {

				// split하여 number array로 반환
				let rangeArr = this.getReverseRangeValue(item.split('~').map(Number));

				// slider list에 추가
				this.sliderList.push({
					range: rangeArr,
					min: rangeArr[0] < this.min ? rangeArr[0] : this.min,
					max: rangeArr[1] > this.max ? rangeArr[1] : this.max,
				});
			}

		// 왼쪽 메뉴 클릭에 의한 세팅
		} else {
			// Range 기본값 세팅
			this.sliderList.push({
				range: [this.min, this.max],
				min: this.min,
				max: this.max,
			});
		}
	}

	public getFilterData(): any {
		// return Data
		let fltrVal = [];

		let dtNmArr = [];

		_.each(this.sliderList, function(item, idx) {

			item.range = this.getUpdateRangeValue(item.range);

			let range = item.range.join('~');
			dtNmArr.push(range);

			fltrVal.push({
				code: range
			});
		}.bind(this));

		return {
			dtNm: dtNmArr.join(', '),
			fltrVal: fltrVal
		};
	}

	private getUpdateRangeValue(range: Array<number>): Array<number> {
		switch(range[0]) {
			case 0: range[0] = 0; break;
			//case 14: range[0] = 70; break;
			//case 28: range[0] = 75; break;
			case 50: range[0] = 80; break;
			//case 56: range[0] = 85; break;
			//case 70: range[0] = 90; break;
			//case 84: range[0] = 95; break;
			case 98: range[0] = 95; break;
		}

		switch(range[1]) {
			case 0: range[1] = 0; break;
			//case 14: range[1] = 70; break;
			//case 28: range[1] = 75; break;
			case 50: range[1] = 80; break;
			//case 56: range[1] = 85; break;
			//case 70: range[1] = 90; break;
			//case 84: range[1] = 95; break;
			case 98: range[1] = 95; break;
		}

		return range;
	}

	private getReverseRangeValue(range: Array<number>): Array<number> {
		switch(range[0]) {
			case 0: range[0] = 0; break;
			//case 70: range[0] = 14; break;
			//case 75: range[0] = 28; break;
			case 80: range[0] = 50; break;
			//case 85: range[0] = 56; break;
			//case 90: range[0] = 70; break;
			//case 95: range[0] = 84; break;
			case 100: range[0] = 98; break;
		}

		switch(range[1]) {
			case 0: range[1] = 0; break;
			//case 70: range[1] = 14; break;
			//case 75: range[1] = 28; break;
			case 80: range[1] = 50; break;
			//case 85: range[1] = 56; break;
			//case 90: range[1] = 70; break;
			//case 95: range[1] = 84; break;
			case 100: range[1] = 98; break;
		}

		return range;
	}

	public onChangeSlider(range: any, slider: any): void {
		if (this.underType && range[0] <= this.min) {
			slider.min = range[0];
		}

		if (this.overType && range[1] >= this.max) {
			slider.max = range[1];
		}
	}

	public onClickApplySubFilter(): void {
		this.applySubFilterEmitter.emit(this.getFilterData());
	}
}
