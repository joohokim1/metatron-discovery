import { Injectable, Injector } from '@angular/core';
import { AbstractService } from '../../../common/service/abstract.service';
import { CommonResult } from '../../common/value/result-value';
import { CookieConstant } from '../../common/constant/cookie-constant';
import * as moment from 'moment';
import * as _ from 'lodash';

@Injectable()
export class IcpmService extends AbstractService {

  // request 데이터
  private requestData: any;

  // 생성자
  constructor(
    protected injector: Injector
  ) {
    super(injector);
  }

  /**
   * icpm 메뉴 정보 조회
   *
   */
  public getIcpmFltrBasList(): Promise<CommonResult> {
    return this.post(`${this.API_URL}/ipm/icpm/getIcpmFltrBasList`, null);
  }

  /**
   * icpm 메뉴별 필터 조건 검색
   *
   * @param fltrUid (메뉴uid)
   */
  public getIcpmFltrBas(fltrUid: String): Promise<CommonResult> {

    this.requestData = {
      fltrUid : fltrUid
    }

    return this.post(`${this.API_URL}/ipm/icpm/getIcpmFltrBas`, this.requestData);
  }

  /**
   * icpm 필터 조건 설정
   *
   * @param params (필터 조건)
   */
  public getIcpmCharts(params: any): Promise<CommonResult> {
    return this.post(`${this.API_URL}/ipm/icpm/getIcpmCharts`, params);
  }

  /**
   * icpm Excel Download
   *
   * @param params (epm 조회 데이터)
   */
  public getIcpmExcel(params: any): Promise<CommonResult> {

    const fileNm = '고객목록-' + params.occrDt;
    const timestamp = moment().format('YYYYMMDD-HHmmss').toString();
    return this.excelDownload(`${this.API_URL}/ipm/icpm/getIcpmExcel`, `${fileNm}_${timestamp}.xlsx`, 'POST', params);
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
          let orgDate = fileNm.split('_')[fileNm.split('_').length - 1].split('.')[0];
          fileNm = fileNm.replace(new RegExp(orgDate, 'gi'), moment().format('YYYYMMDD-HHmmss').toString());

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
