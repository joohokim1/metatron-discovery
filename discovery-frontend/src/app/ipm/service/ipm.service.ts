import { Injectable, Injector, EventEmitter } from '@angular/core';
import { AbstractService } from '../../common/service/abstract.service';

@Injectable()
export class IpmService extends AbstractService {

  // 이벤트 트리거
  private trigger = new EventEmitter(true);

  public requestData: any;

  constructor(
    protected injector: Injector
  ) {
    super(injector);
  }

  /**
   * ipm 헤더 정보 셋팅 이벤트 발생
   * @param data - object
   */
  public setHeaderEvent(data): void {
    this.trigger.emit(data);
  }

  /**
   * ipm 헤더 정보 셋팅 이벤트 발생
   * @param data - object
   */
  public getHeaderEvent(callback: any): void {
    this.trigger.subscribe(callback);
  }


}
