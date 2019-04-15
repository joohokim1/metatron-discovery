import { Injectable, Injector } from '@angular/core';
import { CommonResult } from '../../value/result-value';
import { AbstractService } from '../../../../common/service/abstract.service';

@Injectable()
export class BmrkService extends AbstractService {

  // request 데이터
  private requestData;

  // 생성자
  constructor(
    protected injector: Injector
  ) {
    super(injector);
  }

  /**
   * ipm 사용자 별 즐겨찾기 상세 목록 조회
   *
   * @param menuLinkNm (메뉴 타입 / icpm,lpm,epm)
   */
  public getUserBmrkDtlList(menuLinkNm: String): Promise<CommonResult> {

    this.requestData = {
      menuLinkNm: menuLinkNm
    }

    return this.post(`${this.API_URL}/ipm/getUserBmrkDtlList`, this.requestData);
  }

  /**
   * ipm 사용자 즐겨찾기 상세 등록
   *
   * @param bmrkNm (즐겨찾기 명)
   * @param menuLinkNm (메뉴 타입 / icpm,lpm,epm)
   * @param fltrDatVal (필드정보 json ex/필터)
   */
  public addUserBmrkDtl(bmrkNm: String, menuLinkNm: String, fltrDatVal: any): Promise<CommonResult> {

    this.requestData = {
      bmrkNm: bmrkNm,
      menuLinkNm: menuLinkNm,
      fltrDatVal: fltrDatVal,

    }

    return this.post(`${this.API_URL}/ipm/addUserBmrkDtl`, this.requestData);
  }

  /**
   * icpm 사용자 즐겨찾기 상세 수정
   *
   * @param bmrkUid (즐겨찾기 키 값)
   * @param bmrkNm (즐겨찾기 명)
   * @param fltrDatVal (필드정보 json ex/필터)
   */
  public editUserBmrkDtl(bmrkUid: String, bmrkNm: String, fltrDatVal: any): Promise<CommonResult> {

    this.requestData = {
      bmrkUid: bmrkUid,
      bmrkNm: bmrkNm,
      fltrDatVal: fltrDatVal
    }

    return this.post(`${this.API_URL}/ipm/editUserBmrkDtl`, this.requestData);
  }

  /**
   * icpm 사용자 즐겨찾기 삭제
   *
   * @param bmrkUid (즐겨찾기 키 값)
   */
  public deleteUserBmrkDtl(bmrkUid: String): Promise<CommonResult> {

    this.requestData = {
      bmrkUid: bmrkUid
    }

    return this.post(`${this.API_URL}/ipm/deleteUserBmrkDtl`, this.requestData);
  }


}
