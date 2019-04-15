import { Component, OnInit, ElementRef, Injector, Input } from '@angular/core';
import { AbstractComponent } from '../../../../../common/component/abstract.component';
import { AppBean } from '../../util/appBean';

import * as _ from 'lodash';

declare var echarts: any;

@Component({
    selector: 'lpm-chart',
    templateUrl: `./lpm_chart.component.html`,
    styles: []
})

export class LPMChartComponent extends AbstractComponent implements OnInit {

    private _appBean: any;
    private _chart: any = null;
    private _yAxisOptions: any = {
        min: 0,
        max: 80,
        interval: 10,
        rangeList: []
    };

    public isLoading: boolean = false;

    // 생성자
    constructor(
        protected elementRef: ElementRef,
        protected injector: Injector
    ) {
        super(elementRef, injector);
        this._appBean = injector.get(AppBean);
    }

    ngOnInit() {
        this.initYAxisRangeList();
        this.initChart();
    }

    public getChart(): void {
        return this._chart;
    }

    public initChart(): void {

        if ( this._chart ) return;

        let self: this = this;
        this._chart = echarts.init(document.getElementById('div-chart'));
        this._chart.setOption(this.getChartOptions([]));
    }

    public refreshChart(modelList: Array<any>): void {

        if ( !this._chart ) {
            this.initYAxisRangeList();
            this.initChart();
        }

        this._chart.setOption(this.getChartOptions(modelList));
        this.showLoading(false);
    }

    private getInitSeriesList(): Array<any> {

        let dataList: Array<number> = [];

        for(let i: number = 0; i<_.size(this._yAxisOptions.rangeList); i++) {
            dataList[i] = 0;
        }

        return [
            {name: '남성', type: 'bar', label: {normal: {show: false}}, data: dataList},
            {name: '여성', type: 'bar', label: {normal: {show: false, position:'left'}}, data: _.cloneDeep(dataList)}
        ];
    }

    public getCustomChartSeries(modelList: any): any {
        let seriesList = this.getInitSeriesList();

        _.each(modelList, function(list, gender: string) {
            let dataList: Array<number> = ( gender == 'man' ) ? seriesList[0].data : seriesList[1].data;
            _.each(list, function(item, idx) {
                if ( idx % 2 == 0 ) {
                    let arrayIndex = this.getDataListIndex(item.name);
                    if ( arrayIndex > -1 ) {
                        dataList[arrayIndex] = item.value;
                    }
                }
            }.bind(this));
        }.bind(this));

        return seriesList;
    }

    private getDataListIndex(age): number {
        return _.findIndex(this._yAxisOptions.rangeList, function(item: any, idx) {
            let splitList = item.split(' ~ ');
            if ( _.size(splitList) == 2 ) {
                if ( Number(splitList[0]) <= Number(age) && Number(splitList[1]) > Number(age) ) {
                    return item;
                }
            } else {
                splitList = item.split('+');
                let ageSplitList = age.split('+');

                if ( _.size(splitList) == 2 && _.size(ageSplitList) == 2 ) {
                    if ( Number(splitList[0]) >= Number(ageSplitList[0]) ) {
                        return item;
                    }
                }
            }
        });
    }

    private getChartOptions(features: any): any {
        return {
            color: ['#5693F3', '#D14A61'],

            legend: {
                data: ['남성', '여성']
            },

            tooltip: {
                trigger: 'axis',
                axisPointer: {
                    type: 'shadow'
                }
            },

            xAxis: [
                { type: 'value' },
            ],

            yAxis: [
                {
                    type: 'category',
                    axisTick: {show: false},
                    data: this._yAxisOptions.rangeList
                }
            ],

            series: this.getCustomChartSeries(features)
        };
    }

    private initYAxisRangeList(): void {
        let yAxisOptions = this._yAxisOptions.yAxisOptions;

        for(let i=0; i<=this._yAxisOptions.max/this._yAxisOptions.interval; i++) {
            let minAge = this._yAxisOptions.min + ( i*this._yAxisOptions.interval );

            let label = minAge + ' ~ ' + ( minAge + this._yAxisOptions.interval );

            if ( minAge >= this._yAxisOptions.max ) {
                label = this._yAxisOptions.max + '+';
            }

            this._yAxisOptions.rangeList.push(label);
        }
    }

    public resizeChart(): void {
        this._chart.resize();
        if ( this.isLoading ) {
            this.showLoading(false);
            this.showLoading(true);
        }
    }

    public showLoading(isShow: boolean) {
        this.isLoading = isShow;
    }
}
