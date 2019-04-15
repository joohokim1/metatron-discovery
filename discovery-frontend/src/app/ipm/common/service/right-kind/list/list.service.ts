import { Injectable, Injector } from '@angular/core';
import { CommonResult } from '../../../value/result-value';
import { AbstractService } from '../../../../../common/service/abstract.service';

@Injectable()
export class ListService extends AbstractService {

  // 생성자
  constructor(
    protected injector: Injector
  ) {
    super(injector);
  }

  /**
   * icpm 메뉴별 필터 조건 드루이드 검색
   *
   * @param comGrpCd 코드
   */
  public getCommCode(comGrpCd: string, occrDt: string): Promise<CommonResult> {
    return this.post(`${this.API_URL}/ipm/getCommCode`, {
      comGrpCd: comGrpCd,
      occrDt: occrDt
    });
  }

}
