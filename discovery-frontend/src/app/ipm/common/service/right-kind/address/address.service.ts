import { Injectable, Injector } from '@angular/core';
import { CommonResult } from '../../../value/result-value';
import { AbstractService } from '../../../../../common/service/abstract.service';

@Injectable()
export class AddressService extends AbstractService {

  // 생성자
  constructor(
    protected injector: Injector
  ) {
    super(injector);
  }

  /**
   * 시도 시군구 조회
   *
   * @param sidoCode (시도 코드)
   * @param sggCode (시군구 코드)
   */
  public getAddress(sidoCode?: String, sggCode?: String): Promise<CommonResult> {

    // request 데이터
    let requestData: Array<any> = [];

    if (sidoCode != null) {
      requestData.push(sidoCode);
    }
    if (sggCode != null) {
      requestData.push(sggCode);
    }

    return this.post(`${this.API_URL}/ipm/getAddress`, requestData);
  }
}
