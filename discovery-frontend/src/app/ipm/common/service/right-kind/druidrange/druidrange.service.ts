import { Injectable, Injector } from '@angular/core';
import { CommonResult } from '../../../value/result-value';
import { AbstractService } from '../../../../../common/service/abstract.service';

@Injectable()
export class DruidrangeService extends AbstractService {

  // 생성자
  constructor(
    protected injector: Injector
  ) {
    super(injector);
  }

  /**
   * epm range의 min, max 값 druid 조회
   *
   * @param param - info정보를 가지고있는 object
   */
  public getEpmDruidrange(param: any): Promise<CommonResult> {
    return this.post(`${this.API_URL}/ipm/epm/getEpmDruidrange`, param);
  }

}
