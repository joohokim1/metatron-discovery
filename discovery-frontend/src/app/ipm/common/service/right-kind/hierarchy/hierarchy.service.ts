import { Injectable, Injector } from '@angular/core';
import { CommonResult } from '../../../value/result-value';
import { AbstractService } from '../../../../../common/service/abstract.service';

@Injectable()
export class HierarchyService extends AbstractService {

  // 생성자
  constructor(
    protected injector: Injector
  ) {
    super(injector);
  }

  /**
   * 주소 데이터 조회
   *
   * @param requestData (request 파라미터)
   */
  public getEpmAddrList(requestData: any): Promise<CommonResult> {
    return this.post(`${this.API_URL}/ipm/epm/getEpmAddrList`, requestData);
  }

  /**
   * 조직 데이터 조회
   *
   * @param requestData (request 파라미터)
   */
  public getEpmOrgList(requestData: any): Promise<CommonResult> {
    return this.post(`${this.API_URL}/ipm/epm/getEpmOrgList`, requestData);
  }

  /**
   * EMS 데이터 조회
   *
   * @param requestData (request 파라미터)
   */
  public getEpmEmsList(requestData: any): Promise<CommonResult> {
    return this.post(`${this.API_URL}/ipm/epm/getEpmEmsList`, requestData);
  }

  /**
   * ENB 데이터 조회
   *
   * @param requestData (request 파라미터)
   */
  public getEpmEnbList(requestData: any): Promise<CommonResult> {
    return this.post(`${this.API_URL}/ipm/epm/getEpmEnbList`, requestData);
  }

  /**
   * 국사 데이터 조회
   *
   * @param requestData (request 파라미터)
   */
  public getEpmMtsoList(requestData: any): Promise<CommonResult> {
    return this.post(`${this.API_URL}/ipm/epm/getEpmMtsoList`, requestData);
  }
}
