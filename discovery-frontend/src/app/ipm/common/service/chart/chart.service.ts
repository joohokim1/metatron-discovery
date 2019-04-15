import { Injectable, Injector } from '@angular/core';
import { CommonResult } from '../../value/result-value';
import { AbstractService } from '../../../../common/service/abstract.service';

@Injectable()
export class ChartService extends AbstractService {

  // request 데이터
  private requestData: any;

  // 생성자
  constructor(
    protected injector: Injector
  ) {
    super(injector);
  }

  /**
   * ipm 사용자 별 차트 설정 상세 조회
   *
   * @param menuLinkNm (메뉴 링크)
   */
  public getUserEstDtl(menuLinkNm: string): Promise<CommonResult> {

    this.requestData = {
      menuLinkNm : menuLinkNm
    }

    return this.post(`${this.API_URL}/ipm/getUserEstDtl`, this.requestData);
  }

  /**
   * icm 사용자 별 차트 설정 상세 수정
   *
   * @param menuLinkNm (메뉴 링크)
   * @param estVal (설정 값)
   */
  public editUserEstDtl(menuLinkNm: string, estVal: string): Promise<CommonResult> {

    this.requestData = {
      menuLinkNm : menuLinkNm,
      estVal : estVal
    }
    return this.post(`${this.API_URL}/ipm/editUserEstDtl`, this.requestData);
  }

}
