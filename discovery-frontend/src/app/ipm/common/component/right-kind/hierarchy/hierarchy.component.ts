import { Component, OnInit, ElementRef, Injector, Inject, Input, Output, EventEmitter } from '@angular/core';
import { AbstractComponent } from '../../../../../common/component/abstract.component';
import { HierarchyService } from '../../../service/right-kind/hierarchy/hierarchy.service';
import { DOCUMENT } from '@angular/common';
import { RightKindInterface } from '../right-kind.component';
import { CommonConstant } from '../../../constant/common-constant';
import { Utils } from '../../../util/utils';
import { Alert } from '../../../../../common/util/alert.util';
import * as _ from 'lodash';

@Component({
  selector: 'hierarchy',
  templateUrl: './hierarchy.component.html',
  styles: []
})
export class HierarchyComponent extends AbstractComponent implements OnInit, RightKindInterface {
  // 전달받은 데이터
  @Input()
  public menuDetailData: any;

  // 계층형 노드 목록
  public nodeList: Array<any> = [];

  // request 파라미터
  public requestData: any;

  // 전체 장비 open
  public isAllOn: boolean = true;

  // 선택 표출 데이터
  public selectShowData: Array<any> = [];

  // 선택 젠체데이터 depth이름
  public selectAllDepthNameData: Array<any> = [];

  // 선택 전체데이터 depth코드
  public selectAllDepthCodeData: Array<any> = [];

  // 선택 데이터 depth이름
  public selectDepthNameData: Array<any> = [];

  // 선택 데이터 depth코드
  public selectDepthCodeData: Array<any> = [];

  // 생성자
  constructor(
    protected elementRef: ElementRef,
    protected injector: Injector,
    @Inject(DOCUMENT) private document: Document,
    private hierarchyService: HierarchyService
  ) {
    super(elementRef, injector);
  }

  ngOnInit() {
    // 초기 request param 셋팅
    this.requestData = {
      network: this.menuDetailData.classification.fltrVal[0].code[0],
      equipment: this.menuDetailData.classification.fltrVal[0].code[1],
      vendor: this.menuDetailData.classification.fltrVal[0].code[2],
      druidNm: this.menuDetailData.druidNm
    };

    // 전체 카운트 초기화
    this.menuDetailData['allCnt'] = 0;

    this.requestData.addrCd = [];
    this.hierarchyService.getEpmAddrList(this.requestData)
    .then(result => {
      if (result.code === CommonConstant.CODE.RESULT_CODE.SUCCESS) {
        let dataList = result.data;
        let allCount = 0;

        for (let i = 0; i < dataList.length; i++) {
          result.data[i].nodeList = [];
          result.data[i].hierarchy = [];

          if (result.data[i].value) {
            allCount += Number(result.data[i].value);
          }
        }

        // 전체 카운트
        this.menuDetailData.allCnt = allCount;

        this.nodeList = result.data;
      }
    });

    // 필터 클릭 데이터 셋팅
    if (this.menuDetailData.fltrVal) {
      for (let i = 0;  i < this.menuDetailData.fltrVal.length; i++) {
        let addrNameData = this.menuDetailData.fltrVal[i].name;
        let addrCodeData = this.menuDetailData.fltrVal[i].code;
        this.selectShowData.push(addrNameData[addrNameData.length-1]);
        this.selectAllDepthNameData.push(addrNameData);
        this.selectAllDepthCodeData.push(addrCodeData);
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

    for (let i = 0;  i < this.selectAllDepthCodeData.length; i++) {
      // 주소 데이터 셋팅
      fltrVal.push({
        code : this.selectAllDepthCodeData[i],
        name : this.selectAllDepthNameData[i]
      });
    }

    return {
      dtNm: this.selectShowData.join(', '),
      fltrVal: fltrVal
    };
  }

  /**
    * 1depth 노드 선택시 폴더 on/off
    *
    * @param item (선택된 노드)
    */
  public click1Depth(item): void {

    // 하위 리스트가 없는 경우
    if (!item.nodeList.length) {

      // 각 계층 코드 리스트 [0depth]
      item.hierarchy[0] = item.code;
      this.requestData.addrCd = item.hierarchy;

      this.hierarchyService.getEpmAddrList(this.requestData)
      .then(result => {
        if (result.code === CommonConstant.CODE.RESULT_CODE.SUCCESS) {
          for (let i = 0; i < result.data.length; i++) {
            result.data[i].nodeList = [];
            result.data[i].hierarchy = _.cloneDeep(item.hierarchy);
          }

          item.nodeList = result.data;
        }
      });
    }

    // 폴더 on/off
    item.isOn = !item.isOn;
  }

  /**
    * 2depth 노드 선택시 폴더 on/off
    *
    * @param item (선택된 노드)
    */
  public click2Depth(item): void {

    // 하위 리스트가 없는 경우
    if (!item.nodeList.length) {

      // 각 계층 코드 리스트 [0depth, 1depth]
      item.hierarchy[1] = item.code;
      // 지역
      this.requestData.addrCd = item.hierarchy;

      this.hierarchyService.getEpmAddrList(this.requestData)
      .then(result => {
        if (result.code === CommonConstant.CODE.RESULT_CODE.SUCCESS) {
          for (let i = 0; i < result.data.length; i++) {
            result.data[i].nodeList = [];
            result.data[i].hierarchy = _.cloneDeep(item.hierarchy);
          }

          item.nodeList = result.data;
        }
      });
    }

    // 폴더 on/off
    item.isOn = !item.isOn;
  }

  /**
   * 선택 1Depth 메뉴
   *
   * @param item1Depth (1depth 데이터)
   */
  public select1Depth(item1Depth: any): void {
    this.selectDepthCodeData = [item1Depth.code];
    this.selectDepthNameData = [item1Depth.name];
    this.checkSelectDepthValid();
  }

  /**
   * 선택 2Depth 메뉴
   *
   * @param item1Depth (1depth 데이터)
   * @param item2Depth (2depth 데이터)
   */
  public select2Depth(item1Depth: any, item2Depth: any): void {
    this.selectDepthCodeData = [item1Depth.code, item2Depth.code];
    this.selectDepthNameData = [item1Depth.name, item2Depth.name];
    this.checkSelectDepthValid();
  }

  /**
   * 선택 2Depth 메뉴
   *
   * @param item1Depth (1depth 데이터)
   * @param item2Depth (2depth 데이터)
   * @param item3Depth (3depth 데이터)
   */
  public select3Depth(item1Depth: any, item2Depth: any, item3Depth: any): void {
    this.selectDepthCodeData = [item1Depth.code, item2Depth.code, item3Depth.code];
    this.selectDepthNameData = [item1Depth.name, item2Depth.name, item3Depth.name];
    this.checkSelectDepthValid();
  }

  /**
   * 추가한 depth 삭제
   * @param i (선택 추가 depth index)
   */
  public removeAddress(i: number): void {
    this.selectAllDepthCodeData = Utils.ArrayUtil.remove(this.selectAllDepthCodeData, i);
    this.selectAllDepthNameData = Utils.ArrayUtil.remove(this.selectAllDepthNameData, i);
    this.selectShowData = Utils.ArrayUtil.remove(this.selectShowData, i);
  }

  /**
   * 추가 depth 선택가능 검사
   */
  public checkSelectDepthValid(): void {

    for (let i = 0; this.selectAllDepthCodeData.length > i; i++) {
      let checkAddrData = [];
      if (this.selectAllDepthCodeData[i].length < this.selectDepthCodeData.length) {
        checkAddrData = this.selectDepthCodeData.slice(0, this.selectAllDepthCodeData[i].length);
        if (checkAddrData.join('') == this.selectAllDepthCodeData[i].join('')) {
          Alert.error( '이미 포함 된 데이터입니다.');
          return;
        }
      } else {
        checkAddrData = this.selectAllDepthCodeData[i].slice(0, this.selectDepthCodeData.length);
        if (checkAddrData.join('') == this.selectDepthCodeData.join('')) {
          Alert.error('이미 포함 된 데이터입니다.');
          return;
        }
      }
    }

    this.selectAllDepthCodeData.push(this.selectDepthCodeData);
    this.selectAllDepthNameData.push(this.selectDepthNameData);
    this.selectShowData.push(this.selectDepthNameData[this.selectDepthNameData.length-1]);
  }

  /**
    * 3자리 이상 숫자 콤마 추가
    *
    * @param num (숫자)
    */
  private comma(num): string {
    let regexp = /\B(?=(\d{3})+(?!\d))/g;
    return num.toString().replace(regexp, ',');
  }
}
