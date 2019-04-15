import { Component, OnInit, ElementRef, EventEmitter, Injector, Input, Output } from '@angular/core';
import { AbstractComponent } from '../../../../../../common/component/abstract.component';
import { Alert } from '../../../../../../common/util/alert.util';

import * as _ from 'lodash';

declare const $: any;

@Component({
	selector: 'sf-datetime',
	templateUrl: './sf_datetime.component.html',
	styles: []
})

export class SFDatetimeComponent extends AbstractComponent implements OnInit {

	@Input()
	public subFilterMetaInfo: any;

	@Output()
	public applySubFilterEmitter = new EventEmitter<any>();

	public hourList: Array<string> = [];
	public startSelectedHour: string = '00';
    public endSelectedHour: string = '00';
    public usedHour: boolean = false;

    protected jQuery = $;

    constructor(
		protected elementRef: ElementRef,
    	protected injector: Injector
	) {
		super(elementRef, injector);
        this.setHourList();
	}

	ngOnInit() {
        this.initDateTimePicker();
	}

	private setHourList(): void {
        for(let i: number = 0; i<24; i++) {
            this.hourList.push(this.getHourToString(i));
        }
    }

    private getHourToString(hour: number): string {
        let strHour = '' + hour;
        strHour = ( strHour.length == 1 ) ? '0' + strHour : strHour;
        return strHour;
    }

    private initDateTimePicker() {
        let self: this = this;
        let $start = this.jQuery('#input-rsrp-datetime-start'),
            $end   = this.jQuery('#input-rsrp-datetime-end');

        let startDatepicker = $start.datepicker({
            language: 'ko',
            autoClose: true,
            dateFormat: 'yyyy/mm/dd',
            classes: 'ipm_datepicker',
            onSelect: function(fd, date) {
                $end.data('datepicker').update('minDate', date);
            }
        });

        let endDatepicker = $end.datepicker({
            language: 'ko',
            autoClose: true,
            dateFormat: 'yyyy/mm/dd',
            classes: 'ipm_datepicker',
            onSelect: function(fd, date) {
                $start.data('datepicker').update('maxDate', date);
            }
        });

        if ( this.subFilterMetaInfo.fltrVal ) {
            let date = this.subFilterMetaInfo.fltrVal[0].code;
            let splitList = date.split('~');

            if ( _.size(splitList) == 2 ) {

                let sd = this.getNewDateFormat(splitList[0]);
                let ed = this.getNewDateFormat(splitList[1]);

                startDatepicker.data('datepicker').selectDate(new Date(sd.year, sd.month, sd.day));
                endDatepicker.data('datepicker').selectDate(new Date(ed.year, ed.month, ed.day));

                this.startSelectedHour = sd.hour;
                this.endSelectedHour = ed.hour;
                this.usedHour = this.subFilterMetaInfo.usedHour;
            }

            return;
        };

        startDatepicker.data('datepicker').selectDate(new Date());
        endDatepicker.data('datepicker').selectDate(new Date());
    }

    private getNewDateFormat(date: string): any {
        return {
            year: Number(date.substring(0, 4)),
            month: Number(date.substring(4, 6))-1,
            day: Number(date.substring(6, 8)),
            hour: date.substring(8, 10)
        }
    }

    public getDiffDates(startString: any, endString: any): Number {
        let startDate = new Date(startString);
        let endDate = new Date(endString);

        return (endDate.getTime() - startDate.getTime()) / 3600000;
    }

	public getFilterData(): any {
		let fltrVal: Array<any> = [];

        let start = this.getSelectedDateString(true);
        let end = this.getSelectedDateString(false);

        if ( start != null && end != null ) {

            let startString = start.split('시').join(':00:00');
            let endString = end.split('시').join(':00:00');

            let interval = this.getDiffDates(startString, endString);

            if(this.subFilterMetaInfo.druidNm.indexOf('tmap') > -1) {
                if( interval > 24 ) {
                    Alert.warning('1일(24시간) 간격으로 선택해주시기 바랍니다.');
                    return;
                }
            } else {
                if( interval > 1 ) {
                    Alert.warning('동일 날짜의 1시간 간격으로 선택해주시기 바랍니다.');
                    return;
                }
            }

            fltrVal.push({
                code: start.split('/').join('').split(' ').join('').split('시').join('') + '~' + end.split('/').join('').split(' ').join('').split('시').join(''),
                name: start + ' ~ ' + end
            });

            return {
                dtNm: start + ' ~ ' + end,
                fltrVal: fltrVal
            }
        }
	}

	private getSelectedDateString(isStart: boolean): string {

        let dateTime = this.getSelectedDate(isStart);
        if ( dateTime ) {
            return dateTime.year + '/' + dateTime.month + '/' + dateTime.date + ' ' + dateTime.hour + '시';
        }

        return null;
    }

    private getSelectedDate(isStart: boolean): any {
        let elem = ( isStart ) ? this.jQuery('#input-rsrp-datetime-start') : this.jQuery('#input-rsrp-datetime-end');
        let datepicker = elem.datepicker().data('datepicker');

        if ( datepicker ) {
            let selDateList = datepicker.selectedDates;

            if ( _.size(selDateList) > 0 ) {

                let month = ( Number(selDateList[0].getMonth())+1 ).toString();
                let date = selDateList[0].getDate().toString();

                return {
                    year: selDateList[0].getFullYear(),
                    month: month.length == 1 ? '0'+ month : month,
                    date: date.length==1 ? '0' + date : date,
                    hour: ( isStart ) ? this.startSelectedHour : this.endSelectedHour
                }
            }
        }

        return null;
    }

    public onChangeHour(isStart: boolean, hour: string): void {
        if ( isStart )
            this.startSelectedHour = hour;
        else
            this.endSelectedHour = hour;

        this.updateEndHour();
    }

    private updateEndHour(): void {
        let self: this = this;
        if ( !this.isValidation() ) {
            this.endSelectedHour = this.getHourToString(Number(this.startSelectedHour)+1)
        }
    }

    private isValidation(): boolean {
        var start = this.getSelectedDate(true);
        var end = this.getSelectedDate(false);
        if ( start.year === end.year && start.month === end.month && start.date === end.date ) {
            if ( Number(this.startSelectedHour) > Number(this.endSelectedHour) ) {
                Alert.warning('동일일자 선택 시, 시작 시각보다 이전 시각은 선택하실 수 없습니다.');
                return false;
            }
        }

        return true;
    }

	public onClickApplySubFilter(): void {
		this.applySubFilterEmitter.emit(this.getFilterData());
	}
}
