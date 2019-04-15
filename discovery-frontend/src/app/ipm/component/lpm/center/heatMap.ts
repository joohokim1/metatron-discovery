import * as _ from 'lodash';

declare var L: any;
declare var window: any;

export class HeatMap {

    private _map: any;
    private _properties: any;
    private _heatMap: any;
    private _legendList: Array<any> = [];
    private isHeatMapDrawing: boolean = false;

    constructor(map: any, params: any) {
        this._map = map;
        this._properties = params.properties;

        // this.drawHeatMap(params.data[0]);
    }

    public getLayerName(): string {
        return this._properties.name;
    }

    public getLayerAliasName(): string {
        return this._properties.alias;
    }

    public getHeatMap(): any {
        return this._heatMap;
    }

    public setHeatMapIsDrawing(isDrawing: boolean): any {
        this.isHeatMapDrawing = isDrawing;
    }

    public drawHeatMap(data: any): void {

        this.setHeatMapIsDrawing(true);

        this.removeHeatMapLegend();

        if ( !this._heatMap ) {
            this._heatMap = new L.TileLayer.HeatCanvas( {}, { 
                step: this.getStepByMapLevel()
            });
            this.setHeatMapData(data);
            this._heatMap.onAdd(this._map, this.getLayerName());
            return;
        }

        this._heatMap.setStep(this.getStepByMapLevel());
        this.setHeatMapData(data);
        this._heatMap.redraw();
    }

    public getStepByMapLevel(): number {
        switch(this._map.getZoom()) {
            case 13: return 0.3;
            default: return 0.2;
        }
    }

    private setHeatMapData(data: any): void {
        _.each(data.features, function(item, idx) {
            let coordinates = item.geometry.coordinates;
            let pt = ( item.geometry.type.toUpperCase() == 'POLYGON' ) ? this.getPtByPolygon(coordinates) : coordinates;
            this._heatMap.pushData(pt[1], pt[0], Math.abs(Number(item.properties[this._properties.layrKeyField])));            
            
        }.bind(this));
    }

    private removeHeatMapLegend(): void {
        let center: any = window.LPM_COMPONENT.centerComponent.childComponent;
        if ( center.removeHeatMapLegend ) {
            center.removeHeatMapLegend(this.getLayerAliasName());
        }
    }

    private getHeatMapLegendList(gradient: any): Array<any> {

        this._legendList = [];

        let keys = _.sortBy(Object.keys(gradient));

        let styles = L.MG.SysCfg._styleCfg.getStylesByLayerName(this.getLayerName());

        _.each(styles, function(item, idx) {
            let keyProp = item.originStyle[this._properties.layrKeyField];
            let value: string = keyProp.min + '~' + keyProp.max;

            if ( keyProp.min == keyProp.max ) {
                value = keyProp.min;
            }

            this._legendList.push({
                color: gradient[keys[idx]],
                value: value
            });

        }.bind(this));

        return this._legendList;
    }

    public clearHeatMapLayer(): void {
        if ( this._heatMap ) {
            this._heatMap.clear();
        }
    }

    private getPtByPolygon(coordinates: Array<any>) {
        let polygon = L.polygon(coordinates);
        let center = polygon.getBounds().getCenter();
        return [center.lng, center.lat];
    }

    public redrawHeatMap(data): void {
        this.drawHeatMap(data);
    }

    public setHeatMapOpacity(opacity: number) {
        let options = _.extend(true, this._heatMap.options, {minOpacity: opacity});
        this._heatMap.setOptions(options);
        this._heatMap.redraw();
    }

    public getIsHeatMapDrawing(): boolean {
        return this.isHeatMapDrawing;
    }
}