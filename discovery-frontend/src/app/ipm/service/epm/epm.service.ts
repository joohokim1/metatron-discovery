import { Injectable, Injector } from '@angular/core';
import { AbstractService } from '../../../common/service/abstract.service';
import { CommonResult } from '../../common/value/result-value';
import { CookieConstant } from '../../common/constant/cookie-constant';
import * as moment from 'moment';
import * as _ from 'lodash';

@Injectable()
export class EpmService extends AbstractService{

  // request 데이터
  private requestData: any;

  // 생성자
  constructor(
    protected injector: Injector
  ) {
    super(injector);
  }

  /**
   * epm 분류 조회
   */
  public getEpmClBasList() {
    return this.post(`${this.API_URL}/ipm/epm/getEpmClBasList`, null);
  }

  /**
   * epm 메뉴 정보 조회
   *
   * @param clUid (선택 코드)
   */
  public getEpmFltrBasList(clUid: String): Promise<CommonResult> {

    this.requestData = {
      clUid : clUid
    }

    return this.post(`${this.API_URL}/ipm/epm/getEpmFltrBasList`, this.requestData);
  }

  /**
   * epm 메뉴 필터 기본 검색
   *
   * @param fltrUid (필터 명)
   */
  public getEpmFltrBas(fltrUid: String): Promise<CommonResult> {

    this.requestData = {
      fltrUid : fltrUid
    }

    return this.post(`${this.API_URL}/ipm/epm/getEpmFltrBas`, this.requestData);
  }

  /**
   * epm 차트 및 테이블 조회
   *
   * @param params (epm 조회 데이터)
   */
  public getEpmCharts(params: any): Promise<CommonResult> {
    return this.post(`${this.API_URL}/ipm/epm/getEpmCharts`, params);
  }

  /**
   * epm Excel Download
   *
   * @param params (epm 조회 데이터)
   */
  public getEpmChartsExcel(data: any, fltrDatVal: Array<any>, cfNmList: Array<any>): Promise<CommonResult> {

    let params: any = {
      chartNum: data.chartNum,
      info: data.info,
      fltrDatVal: fltrDatVal
    }

    let fileNm = '';
    if (params.chartNum === 'e3') {

      fileNm = '장비목록_'+ cfNmList.join('_');
    } else if (params.chartNum === 'e4') {
      fileNm = '장비성능_' + data.info[0].eqpNm;
      if (data.info.cellNum) {
        fileNm += '_' + data.info.cellNum;
      }
    }

    const timestamp = moment().format('YYYYMMDD-HHmmss').toString();
    return this.excelDownload(`${this.API_URL}/ipm/epm/getEpmChartsExcel`, `${fileNm}_${timestamp}.xlsx`, 'POST', params);
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
