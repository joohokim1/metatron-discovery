import { Component, OnInit, ElementRef, EventEmitter, Injector, Input, Output } from '@angular/core';
import { AbstractComponent } from '../../../../../../common/component/abstract.component';
import { Alert } from '../../../../../../common/util/alert.util';
import { Utils } from '../../../../../common/util/utils';

import * as _ from 'lodash';

@Component({
	selector: 'sf-range',
	templateUrl: './sf_range.component.html',
	styles: []
})

export class SFRangeComponent extends AbstractComponent implements OnInit {

	@Input()
	public subFilterMetaInfo: any;

	@Output()
	public applySubFilterEmitter = new EventEmitter<any>();

    public min: number;
    public max: number;
    public step: number;

    public overType: string = null;
    public underType: string = null;

    public sliderList: Array<any> = [];

    constructor(
		protected elementRef: ElementRef,
    	protected injector: Injector
	) {
		super(elementRef, injector);
	}

	ngOnInit() {
        this.initSliderModel();

        if ( this.subFilterMetaInfo.fltrVal )
            this.refreshSliderList();
        else
            this.initSliderList();
	}

    private initSliderModel(): void {
        this.min = Number(this.subFilterMetaInfo.fltrDtlList[0].values[0]);
        this.max = Number(this.subFilterMetaInfo.fltrDtlList[0].values[1]);
        this.step = Number(this.subFilterMetaInfo.fltrDtlList[0].step) || null;

        if ( this.subFilterMetaInfo.fltrDtlList[0].rmk ) {
            this.overType = this.subFilterMetaInfo.fltrDtlList[0].rmk.includes('+') ? '+' : '';
            this.underType = this.subFilterMetaInfo.fltrDtlList[0].rmk.includes('-') ? '-' : '';
        }
    }

    private refreshSliderList(): void {
        let rangeListArr: Array<any> = [];

        _.each(this.subFilterMetaInfo.fltrVal, function(item, idx) {
            rangeListArr.push(item.code);
        });

        _.each(rangeListArr, function(item, idx) {
            let splitRangeCode: Array<string> = item.split('~');

            if ( _.size(splitRangeCode) == 1 ) {
                splitRangeCode[1] = this.max;
            }

            this.sliderList.push({
                range: [Number(splitRangeCode[0]), Number(splitRangeCode[1])] //rangeListArr.map(Number)
            });

        }.bind(this));

        if(this.sliderList.length == 0) {
            this.sliderList.push({
                range: [this.min, this.max]
            });
        }
    }

    private initSliderList(): void {
        this.sliderList.push({
            range: [this.min, this.max]
        });
    }

    public onChangeSlider(range: any, slider: any): void {

        if (this.underType && range[0] <= this.min) {
          slider.min = range[0];
        }

        if (this.overType && range[1] >= this.max) {
          slider.max = range[1];
        }
    }

    public onClickRemoveSlider(i) {
        this.sliderList.splice(i, 1);
    }

    public onClickAddSlider(): void {
        this.sliderList.splice(0, 0, {
            range: [this.min, this.max],
            min: this.min,
            max: this.max
        })
    }

    public getFilterData(): any {
        // return Data
        let fltrVal: Array<any> = [];
        let dtNmArr: Array<any> = [];

        _.each(this.sliderList, function(item, idx) {
            let slider = item.range;
            let range = slider.join('~');
            if ( this.overType && this.isOverMax(slider[1]) ) {
                dtNmArr.push(slider[0] + '이상');
                range = range.substring(0, range.lastIndexOf(slider[1]));
            } else {
                dtNmArr.push(range);
            }

            fltrVal.push({
                code: range
            })

        }.bind(this));

        return { dtNm: dtNmArr.join(', '), fltrVal: fltrVal };
    }

    public isOverMax(value: number): boolean {
        return value >= this.max;
    }

	public onClickApplySubFilter(): void {
		this.applySubFilterEmitter.emit(this.getFilterData());
	}

    public addComma(value: number): string | number {
        return Utils.NumberUtil.addComma(value);
    }
}
