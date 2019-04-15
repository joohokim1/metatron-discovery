import { Injectable, Injector } from '@angular/core';
import { AbstractService } from '../../../common/service/abstract.service';
import { CommonResult } from '../../common/value/result-value';
import { CookieConstant } from '../../common/constant/cookie-constant';
import * as moment from 'moment';
import * as _ from 'lodash';

@Injectable()
export class LpmService extends AbstractService{

  // 생성자
  constructor(
    protected injector: Injector
  ) {
    super(injector);
  }

  public getLpmFltrBasList(layerGroupId: string, subUseYn: boolean): Promise<any> {
    return this.post(`${this.API_URL}/ipm/lpm/getLpmFltrBasList`, {layrGrpId : layerGroupId, subUseYn: subUseYn});
  }

  public getLpmFltrBas(fieldNm: string): Promise<CommonResult> {
    return this.post(`${this.API_URL}/ipm/lpm/getLpmFltrBas`, {fltrNm: fieldNm});
  }

  public getAddrCenterPoint(params: any): Promise<any> {
    return this.post(`${this.API_URL}/ipm/lpm/getAddrCenterPoint`, params);
  }

  public getXdrRowData(params: any): Promise<any> {
    return this.post(`${this.API_URL}/ipm/lpm/getXdrRowData`, params);
  }

  public getPopulation(filterList: Array<any>): Promise<any> {
      return this.post(`${this.API_URL}/ipm/lpm/getPopulation`, {fltrDatVal: filterList});
  }

  public getChartPopulation(params: any): Promise<any> {
    return this.post(`${this.API_URL}/ipm/lpm/getChartPopulation`, params);
  }

  public getGridPopulation(params: any): Promise<any> {
    return this.post(`${this.API_URL}/ipm/lpm/getGridPopulation`, params);
  }

  public getLpmLayrGrpList(): Promise<any> {
    return this.get(`${this.API_URL}/ipm/lpm/getLpmLayrGrpList`);
  }

  public getLayerInfoByLayrGrpId(layrGrpId: string): Promise<any> {
    return this.post(`${this.API_URL}/ipm/lpm/getLayerInfoByLayrGrpId`, layrGrpId);
  }

  public getIpmUrl(): string {
      return `http://${window.location.hostname}:18080`;
      //return this.environment.lpm.apiUrl;
  }

  public getGeoServerUrl(): string {
      return 'http://discovery.tango.sktelecom.com:9090/geoserver/metatron/ows?service=WFS&version=1.0.0&request=GetFeature&outputFormat=application%2Fjson&';
      //return this.environment.lpm.geoserverUrl;
  }

  public getIpmWebUrl(): string {
      return `http://${window.location.hostname}:4200`;
      //return this.environment.lpm.webUrl;
  }

  /**
   * epm Excel Download
   *
   * @param params (epm 조회 데이터)
   */
  public getLpmGridExcel(params: any, startEndTimestamp: string): Promise<CommonResult> {
    let time = startEndTimestamp.split('~');

    params.startEndTimestamp =
          time[0].substring(0, 4) + '-' + time[0].substring(4, 6) + '-' + time[0].substring(6, 8) + ' ' + time[0].substring(8, 10) + '시' + ' ~ '
            + time[1].substring(0, 4) + '-' + time[1].substring(4, 6) + '-' + time[1].substring(6, 8) + ' ' + time[1].substring(8, 10) + '시';

    let fileNm = '지역별인구수_RawData';
    //const startEndTimestamp = moment().format('YYYYMMDD-HHmmss').toString();
    return this.excelDownload(`${this.API_URL}/ipm/lpm/getLpmGridExcel`, `${fileNm}_${startEndTimestamp}.xlsx`, 'POST', params);
  }

  /**
   * 파일 다운로드
   *
   * @param {string} url
   * @param {string} fileName
   * @param {string} requestMethod
   * @param params
   * @returns {Promise<any>}
   */
  private excelDownload(url: string, fileNm: string, requestMethod: string, params?: any): Promise<any> {

    const promise = new Promise((resolve, reject) => {

      const xhttp = new XMLHttpRequest();

      xhttp.onload = () => {

        if (xhttp.status >= 200 && xhttp.status < 300) {

          // fileNm의 날짜를 excel download 완료 시점으로 변경
          //let orgDate = fileNm.split('_')[fileNm.split('_').length - 1].split('.')[0];
          //fileNm = fileNm.replace(new RegExp(orgDate, 'gi'), moment().format('YYYYMMDD-HHmmss').toString());

          // IE 인 경우 별도 처리, 그 외 다운로드 트릭 링크 설정.
          if (_.eq(window.navigator.msSaveBlob, undefined) === false) {
            window.navigator.msSaveBlob(xhttp.response, fileNm);
          } else {
            const a = document.createElement('a');
            a.href = window.URL.createObjectURL(xhttp.response);
            a.download = fileNm;
            a.style.display = 'none';
            document.body.appendChild(a);
            a.click();
            a.parentNode.removeChild(a);
          }

          resolve('success');
        } else {
          // this.status가 2xx와 다른 경우 함수 "reject" 수행
          reject('error');
        }
      };

      // 에러시
      xhttp.onerror = () => {
        reject('error');
      };

      xhttp.open(requestMethod, encodeURI(url));
      xhttp.setRequestHeader('Authorization', `Bearer ${this.cookieService.get(CookieConstant.KEY.TOKEN)}`);
      xhttp.setRequestHeader('Content-Type', 'application/json;charset=UTF-8');
      xhttp.responseType = 'blob';

      if (requestMethod === 'GET') {
        xhttp.send();
      } else {
        xhttp.send(JSON.stringify(params));
      }

    });

    return promise;
  }
}
