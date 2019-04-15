import { Component, ElementRef, Injector, OnInit, Input } from '@angular/core';
import { AbstractComponent } from '../../../../../common/component/abstract.component';
import { AddressService } from '../../../service/right-kind/address/address.service';
import { RightKindInterface } from '../right-kind.component';
import { CommonConstant } from '../../../constant/common-constant';
import { Utils } from '../../../util/utils';
import { Alert } from '../../../../../common/util/alert.util';

import * as _ from 'lodash';

@Component({
  selector: 'address',
  templateUrl: './address.component.html',
  styles: []
})
export class AddressComponent extends AbstractComponent implements OnInit, RightKindInterface {

  // 전달받은 데이터
  @Input()
  public menuDetailData: any;

  // 시도 데이터
  public sidoData: Array<any> = [];

  // 시군구 데이터
  public sggData: Array<any> = [];

  // 읍면동 데이터
  public amdData: Array<any> = [];

  // 선택 시도 데이터
  public currentSidoData: any;

  // 선택 시군구 데이터
  public currentSggData: any;

  // 선택 표출 데이터
  public selectShowData: Array<any> = [];

  // 선택 데이터 주소이름
  public selectAllAddrNameData: Array<any> = [];

  // 선택 데이터 주소코드
  public selectAllAddrCodeData: Array<any> = [];

  // 선택 데이터 주소이름
  public selectAddrNameData: Array<any> = [];

  // 선택 데이터 주소코드
  public selectAddrCodeData: Array<any> = [];

  // 시도 선택 표시
  public isSidoSelect: boolean = false;

  // 시군구 선택 표시
  public isSggSelect: boolean = false;

  // 생성자
  constructor(
    protected elementRef: ElementRef,
    protected injector: Injector,
    private addressService: AddressService
  ) {
    super(elementRef, injector);
  }

  ngOnInit() {
    this.addressService.getAddress()
    .then(result => {
      if (result.code === CommonConstant.CODE.RESULT_CODE.SUCCESS) {
        this.sidoData = result.data;
      }
    });

    // 필터 클릭 데이터 셋팅
    if (this.menuDetailData.fltrVal) {
      for (let i = 0;  i < this.menuDetailData.fltrVal.length; i++) {
        let addrNameData = this.menuDetailData.fltrVal[i].name;
        let addrCodeData = this.menuDetailData.fltrVal[i].code;
        this.selectShowData.push(addrNameData[addrNameData.length-1]);
        this.selectAllAddrNameData.push(addrNameData);
        this.selectAllAddrCodeData.push(addrCodeData);
      }
    }
  }

  /**
   * Override Method
   * 필터조건에 사용되는 데이터(Address Data)
   * @return 필터 조건 Object
   */
  public getFilterData(): any {

    // return Data
    let fltrVal = [];

    for (let i = 0;  i < this.selectAllAddrCodeData.length; i++) {
      // 주소 데이터 셋팅
      fltrVal.push({
        code : this.selectAllAddrCodeData[i],
        name : this.selectAllAddrNameData[i]
      });
    }

    return {
      dtNm: this.selectShowData.join(', '),
      fltrVal: fltrVal
    };
  }

  /**
   * 시군구 펼치기 이벤트
   * @param e ($event 객체 - 시군구 데이터)
   */
  public showSsg(e: any): void {

    // 이벤트 버블링 방지
    e.stopPropagation();

    this.amdData = [];
    this.isSggSelect = false;
    this.isSidoSelect = false;

    this.currentSidoData = {
      name : e.target.textContent,
      code : e.target.getAttribute('code')
    }


    this.addressService.getAddress(this.currentSidoData.code)
    .then(result => {
      if (result.code === CommonConstant.CODE.RESULT_CODE.SUCCESS) {
        this.sggData = result.data;
        this.isSidoSelect = true;
      }
    });

    this.showCheck(this.sidoData, this.currentSidoData.code);
  }

  /**
   * 읍면동 펼치기 이벤트
   * @param e ($event 객체 - 읍면동 데이터)
   */
  public showAmd(e: any): void {

    // 이벤트 버블링 방지
    e.stopPropagation();

    this.currentSggData = {
      name : e.target.textContent,
      code : e.target.getAttribute('code')
    }

    this.addressService.getAddress(this.currentSidoData.code, this.currentSggData.code)
    .then(result => {
      if (result.code === CommonConstant.CODE.RESULT_CODE.SUCCESS) {
        this.amdData = result.data;
        this.isSggSelect = true;
      }
    });

    this.showCheck(this.sggData, this.currentSggData.code);
  }


  // 주소를 하나만 선택해야 할 시, 기존에 선택된 주소가 있으면 더이상 데이터를 push 못하도록 Validation check
  public isAddAddress(): boolean {

    if (_.has(this.menuDetailData, 'isAddAddress') && this.menuDetailData.isAddAddress == false) {
      if (_.size(this.selectAllAddrCodeData) == 1) {
        Alert.error('주소는 하나만 선택 가능합니다.');
        return false;
      }
    }

    return true;
  }

  /**
   * 시도 추가 이벤트
   * @param e ($event 객체 - 선택 시도 데이터)
   */
  public selectSido(e: any): void {

    if (this.isAddAddress()) {
      this.selectAddrNameData = [e.target.nextElementSibling.textContent];
      this.selectAddrCodeData = [e.target.getAttribute('code')];

      //추가 가능 확인
      this.checkSelectAddressValid(this.selectAddrCodeData, this.selectAddrNameData);
    }
  }

  /**
   * 시군구 추가 이벤트
   * @param e ($event 객체 - 선택 시군구 데이터)
   */
  public selectSgg(e: any): void {
    if (this.isAddAddress()) {
      this.selectAddrNameData = [this.currentSidoData.name, e.target.nextElementSibling.textContent];
      this.selectAddrCodeData = [this.currentSidoData.code, e.target.getAttribute('code')];

      //추가 가능 확인
      this.checkSelectAddressValid(this.selectAddrCodeData, this.selectAddrNameData);
    }
  }

  /**
   * 읍면동 추가 이벤트
   * @param e ($event 객체 - 선택 읍면동 데이터)
   */
  public selectAmd(e: any): void {
    if (this.isAddAddress()) {
      this.selectAddrNameData = [this.currentSidoData.name, this.currentSggData.name, e.target.nextElementSibling.textContent];
      this.selectAddrCodeData = [this.currentSidoData.code, this.currentSggData.code, e.target.getAttribute('code')];

      //추가 가능 확인
      this.checkSelectAddressValid(this.selectAddrCodeData, this.selectAddrNameData);
    }
  }

  /**
   * 추가한 주소 삭제
   * @param i (선택 추가 주소 index)
   */
  public removeAddress(i: number): void {
    this.selectAllAddrCodeData = Utils.ArrayUtil.remove(this.selectAllAddrCodeData, i);
    this.selectAllAddrNameData = Utils.ArrayUtil.remove(this.selectAllAddrNameData, i);
    this.selectShowData = Utils.ArrayUtil.remove(this.selectShowData, i);
  }

  /**
   * 추가 주소 선택가능 검사
   *
   * @param selectCodeData (선택 지역코드 데이터)
   * @param selectNameData (선택 지역이름 데이터)
   */
  public checkSelectAddressValid(selectCodeData: Array<any>, selectNameData: Array<any>): void {

    for (let i = 0; this.selectAllAddrCodeData.length > i; i++) {
      let checkAddrData = [];
      if (this.selectAllAddrCodeData[i].length < selectCodeData.length) {
        checkAddrData = selectCodeData.slice(0, this.selectAllAddrCodeData[i].length);
        if (checkAddrData.join('') == this.selectAllAddrCodeData[i].join('')) {
          Alert.error( '이미 포함 된 주소입니다.');
          return;
        }
      } else {
        checkAddrData = this.selectAllAddrCodeData[i].slice(0, selectCodeData.length);
        if (checkAddrData.join('') == selectCodeData.join('')) {
          Alert.error('이미 포함 된 주소입니다.');
          return;
        }
      }
    }

    this.selectAllAddrCodeData.push(selectCodeData);
    this.selectAllAddrNameData.push(selectNameData);
    this.selectShowData.push(selectNameData[selectNameData.length-1]);
  }

  /**
   * 선택된 주소 표시
   *
   * @param addrData(현재 주소 목록)
   * @param code(선택된 시도/시군구 코드)
   */
  public showCheck(addrData: any, code: String): void {
    for (let i = 0; addrData.length > i; i++) {
      if (addrData[i].code == code) {
        addrData[i].show = true;
      } else {
        addrData[i].show = false;
      }
    }
  }
}
