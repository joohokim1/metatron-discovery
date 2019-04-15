import * as _ from 'lodash';

declare var window: any;

export class FilterMap {

	private _map: any = null;
	private lpmService: any = null;

	private _layer: any = null;

	constructor( map, lpmService ) {
		this._map = map;
		this.lpmService = lpmService;
	}

	public setMapCustomFilter(layer: any, filters: any) : void{

		if ( layer ) {

			this._layer = layer;

			if (_.size(filters) == 0 ) {
				this._layer.setCustomParams('');
			} else {
				let customFilters = this.getCustomFilters(filters);
				this._layer.setCustomParams(customFilters);
			}

			this._layer.setTempCustomFilters(filters);
			this._layer.refresh(true);
		}
	}

	private getCustomFilters(filters: any): any {

		var customFilters = '';

		_.each(filters, function(item, idx) {
			switch(item.scrnClNm) {
				// case 'address': customFilters += this.getAddressFilter(item); break;
				case 'datetime': customFilters += this.getDateTimeFilter(item); break;
				case 'range': customFilters += this.getRangeFilter(item); break;
				case 'multiple':
				case 'custmulticheck':
					customFilters +=this.getMultipleFilter(item); break;
				case 'hour': customFilters +=this.getHourFilter(item); break;
				// case 'cei': customFilters += this.getCeiFilter(item); break;
				
				case 'sf-datetime': customFilters += this.getDateTimeFilter(item); break;
				case 'sf-range': customFilters += this.getRangeFilter(item); break;
				case 'sf-multiple':
				case 'sf-custmulticheck':
					customFilters +=this.getMultipleFilter(item); break;
				case 'sf-hour': customFilters +=this.getHourFilter(item); break;
			}
		}.bind(this));

		return customFilters;
	}

	private getCeiFilter(filter: any): string {
		let ceiFilter: string = '';

		_.each(filter.fltrVal, function(item, idx:number) {
			let splitList: Array<string> = item.code.split('~');
			if ( _.size(splitList) == 2 ) {

				if ( filter.type == 'not' ) {
					if ( idx == 0 )
						ceiFilter += ' AND (' + filter.fltrNm + '<=' + splitList[0] + ' AND ' + filter.fltrNm + '>=' + splitList[1];
					else
						ceiFilter += ' OR ' + filter.fltrNm + '>=' + splitList[0] + ' AND ' + filter.fltrNm + '<=' + splitList[1];
				} else {
					if ( idx == 0 )
						ceiFilter += ' AND (' + filter.fltrNm + '>=' + splitList[0] + ' AND ' + filter.fltrNm + '<=' + splitList[1];
					else
						ceiFilter += ' OR ' + filter.fltrNm + '<=' + splitList[0] + ' AND ' + filter.fltrNm + '>=' + splitList[1];
				}

				if( (filter.fltrVal.length - 1) == idx ) {
					ceiFilter += ')';
				}				
			}
		});

		return ceiFilter;
	}

	private getDateTimeFilter(filter:any): string {

		let dateTimeFilters: string = '';

		_.each(filter.fltrVal, function(item, idx) {
			let filter: string = this.getIntervalsDateTimeRange(item.code);

			if ( filter ) {
				dateTimeFilters += filter;
			}
		}.bind(this));

		if ( this._layer.properties.layrGrpId == '300' || this._layer.properties.layrGrpId == '400' ) {
			_.each(filter.fltrVal, function(item, idx) {
				let filter: string = this.getIntervalsHourRange(item.code);
				if ( filter ) {
					dateTimeFilters += filter;
				}
			}.bind(this));
		}

		return dateTimeFilters;
	}

	private getRangeFilter(filter: any): string {

		let rangeFilter: string = '';

		// rsrp인 경우, 넘어온 data의 summary한 값에서 filter를 걸도록 한다.
		if ( filter.fltrNm == 'rsrp' ) return rangeFilter;

		_.each(filter.fltrVal, function(item, idx:number) {
			let splitList: Array<string> = item.code.split('~');

			if(filter.scrnNm == '연령') {
				if(splitList[0] != '0') {
					splitList[0] = '0' + Number(Number(splitList[0]) / 10).toString();
				}
				splitList[1] = '0' + Number(Number(splitList[1]) / 10).toString();
			}

			if ( _.size(splitList) == 2 ) {

				if ( filter.type == 'not' ) {
					if ( idx == 0 )
						rangeFilter += ' AND ' + '(' + filter.fltrNm + '<=' + '\'' + splitList[0] + '\' OR ' + filter.fltrNm + ' >= ' + '\'' + splitList[1] + '\'';
					else
						rangeFilter += ' OR ' + filter.fltrNm + '>=' + '\'' + splitList[0] + '\' OR ' + filter.fltrNm + ' <= ' + '\'' + splitList[1] + '\'';
				} else {
					if ( idx == 0 )
						rangeFilter += ' AND ' + '(' + filter.fltrNm + '>=' + '\'' + splitList[0] + '\' AND ' + filter.fltrNm + ' <= ' + '\'' + splitList[1] + '\'';
					else
						rangeFilter += ' OR ' + filter.fltrNm + '<=' + '\'' + splitList[0] + '\' AND ' + filter.fltrNm + ' >= ' + '\'' + splitList[1] + '\'';
				}
			}

			if( (filter.fltrVal.length - 1) == idx ) {
				rangeFilter += ')';
			}
		});

		return rangeFilter;
	}

	private getMultipleFilter(filter: any): string {

		let multipleFilters: string = '';
		let condition: string = ( filter.type == 'not' ) ? '!=' : '=';

		_.each(filter.fltrVal, function(item, idx: number) {
			if ( idx == 0 )
				multipleFilters += ' AND ' + '(' + filter.fltrNm + condition + '\'' +  item.code + '\'';
			else
				multipleFilters += ' OR ' + filter.fltrNm + condition + '\'' +  item.code + '\'';

			if( (filter.fltrVal.length - 1) == idx ) {
				multipleFilters += ')';
			}
		});

		return multipleFilters;
	}

	private getHourFilter(filter: any): string {

		let hourFilters: string = '';

		let condition: string = ( filter.type == 'not' ) ? '!=' : '=';

		_.each(filter.fltrVal, function(item, idx: number) {
			if ( idx == 0 )
				hourFilters += ' AND ' + filter.fltrNm + condition + item.code;
			else
				hourFilters += ' OR ' + filter.fltrNm + condition + item.code;
		});

		return hourFilters;
	}

	private getIntervalsDateTimeRange(dateTime: string): string {

		let splitList = dateTime.split('~');
		if ( _.size(splitList) == 2 ) {

			let customStartTime: string = this.getCustomTime(splitList[0]);

			if ( this._layer.properties.dataUnit == 'hour' ) {
				return ' AND ( __time AFTER ' + customStartTime.substring(0, 4) + '-' + customStartTime.substring(4, 6) + '-' + customStartTime.substring(6, 8)
				+ 'T' + customStartTime.substring(8, 10) + ':' + customStartTime.substring(10, 12) + ':' + customStartTime.substring(12, 14) + 'Z ) '
				+ ' AND ( __time BEFORE ' + splitList[1].substring(0, 4) + '-' + splitList[1].substring(4, 6) + '-' + splitList[1].substring(6, 8)
				+ 'T' + splitList[1].substring(8, 10) + ':59:59Z ) ';
			} 

			return ' AND ( __time AFTER ' + customStartTime.substring(0, 4) + '-' + customStartTime.substring(4, 6) + '-' + customStartTime.substring(6, 8)
				+ 'T' + customStartTime.substring(8, 10) + ':' + customStartTime.substring(10, 12) + ':' + customStartTime.substring(12, 14) + 'Z ) '
			+ ' AND ( __time BEFORE ' + splitList[1].substring(0, 4) + '-' + splitList[1].substring(4, 6) + '-' + splitList[1].substring(6, 8)
			+ 'T23:59:59Z ) ';
		}

		return null;
	}

	private getIntervalsHourRange(dateTime: string): string {
		let splitList = dateTime.split('~');
		if ( _.size(splitList) == 2 ) {
			let startHour: string = splitList[0].substring(8, 10);
			let endHour: string = splitList[1].substring(8, 10);
			
			return ' AND ( hh >= ' + '\'' + startHour + '\'' + ' AND hh <= ' + '\'' + endHour + '\' ) ';
		}

		return null;
	}

	private getCustomTime(time: string): string {
		let year: number = Number(time.substring(0, 4));
		let month: number = Number(time.substring(4, 6));
		let date: number = Number(time.substring(6, 8));
		let hour: number = ( this._layer.properties.dataUnit == 'hour' ) ? Number(time.substring(8, 10)) : 0;

		let customTime = new Date((new Date(year, month-1, date, hour).getTime()) - 1000);

		let customYear: string = customTime.getFullYear().toString();
		let customMonth: string = this.addZeroText((customTime.getMonth()+1).toString());
		let customDate: string = this.addZeroText(customTime.getDate().toString());
		let customHour: string = this.addZeroText(customTime.getHours().toString());
		let customMinutes: string = this.addZeroText(customTime.getMinutes().toString());
		let customSeconds: string = this.addZeroText(customTime.getSeconds().toString());

		return customYear + customMonth + customDate + customHour + customMinutes + customSeconds;

	}

	private addZeroText(time: string): string {
		return ( time.length == 1 ) ? '0' + time : time;
	}
}