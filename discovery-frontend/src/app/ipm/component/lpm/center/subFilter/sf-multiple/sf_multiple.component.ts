import { Component, OnInit, ElementRef, EventEmitter, Injector, Input, Output } from '@angular/core';
import { AbstractComponent } from '../../../../../../common/component/abstract.component';
import { Alert } from '../../../../../../common/util/alert.util';

import * as _ from 'lodash';

@Component({
	selector: 'sf-multiple',
	templateUrl: './sf_multiple.component.html',
	styles: []
})

export class SFMultipleComponent extends AbstractComponent implements OnInit {

	@Input()
	public subFilterMetaInfo: any;

	@Output()
	public applySubFilterEmitter = new EventEmitter<any>();

    // 필터 데이터
    public fltrVal: Array<any> = [];

    constructor(
		protected elementRef: ElementRef,
    	protected injector: Injector
	) {
		super(elementRef, injector);
	}

	ngOnInit() {
        if ( this.subFilterMetaInfo.fltrVal )
            this.refreshMultipleList();
        else
            this.initMultipleList();
	}

    private refreshMultipleList(): void {
        this.fltrVal = this.subFilterMetaInfo.fltrDtlList[0].values;

        _.each(this.subFilterMetaInfo.fltrVal, function(model, idx) {
            let findItem = _.find(this.fltrVal, function(item, index) {
                return model.code === item.code;
            });

            if ( findItem ) {
                findItem.checked = true;
            }
        }.bind(this));
    }

    private initMultipleList(): void {
        this.fltrVal = this.subFilterMetaInfo.fltrDtlList[0].values;
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

	public onClickApplySubFilter(): void {
		this.applySubFilterEmitter.emit(this.getFilterData());
	}
}
