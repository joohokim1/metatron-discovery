import * as _ from 'lodash';
import { Injectable, Injector } from '@angular/core';
import { AppBean } from '../util/appBean';

declare var window: any;

export class UIEvent {

    public isRightFlexible: boolean = false;
    public isLeftFlexible: boolean = false;

    public isRightKindHide: boolean = false;

    public isOpenLayerList: boolean = true;

    public isCheckLayer: boolean = true;

    public sliderMin: number = 0;
    public sliderMax: number = 100;
    public sliderStep: number = 1;
    public sliderModel: number = 100;

    private filterCtrl: any;
    private dComponent: any;

    constructor(
        private appBean: AppBean
    ) {

    }

    public setDefaultCenterComponent(component: any) {
        this.dComponent = component;
    }

    // Left, Right Spacing Open, Close Control
    public onClickFlexibleCtrl(isRightCtrl): void {

        if ( isRightCtrl ) {
            this.isRightFlexible = !this.isRightFlexible;
            return;
        }

        this.isLeftFlexible = !this.isLeftFlexible;
    }

    // right-kind component 닫기
    public rightKindClose(): void {
        this.filterCtrl.clearSubMenuUIInfo();
        this.isRightKindHide = false;
    }

    public onClickViewCtrlLyrList(): void {
        this.isOpenLayerList = !this.isOpenLayerList;
    }

    public onChangeSlider(sliderValue: number, slider: any): void {
        this.sliderModel = Number(sliderValue);
        window.LPM_COMPONENT.centerComponent.childComponent.setBaseMapOpacity(sliderValue/100);
    }

    public addSliderEventListener(jQuery: any): void {

        let self: this = this;

        // jQuery('div.filter_set_box').resizable({
        //     classes: {
        //         'ui-resizable-handle': 'resize_slider'
        //     },
        //     handles: 's',
        //     minHeight: 105,
        //     maxHeight: 500
        // });

        jQuery('div.map_wrap').resizable({
            classes: {
                'ui-resizable-handle': 'map_size_btn'
            },
            handles: 's',
            minHeight: 900,
            // minHeight: 850,
            // maxHeight: 600,
            stop: function(event, ui) {
                window.LPM_COMPONENT.centerComponent.childComponent.resizeMap();
            }
        });
    }

    public windowResizeEvent(): void {

        let center: any = window.LPM_COMPONENT.centerComponent.childComponent;

        if ( center.southComponent ) {
            let south: any = center.southComponent.childComponent;

            if ( south && south.chartComponent && south.gridComponent ) {
                south.chartComponent.resizeChart();
                south.gridComponent.resizeGrid();
            }
        }
    }
}