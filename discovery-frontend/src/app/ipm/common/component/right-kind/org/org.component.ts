import { Component, OnInit, ElementRef, Injector, Input, Output, EventEmitter, Inject } from '@angular/core';
import { DOCUMENT } from '@angular/common';
import { AbstractComponent } from '../../../../../common/component/abstract.component';
import { HierarchyService } from '../../../service/right-kind/hierarchy/hierarchy.service';
import { RightKindInterface } from '../right-kind.component';
import { CommonConstant } from '../../../constant/common-constant';
import * as _ from 'lodash';

@Component({
  selector: 'org',
  templateUrl: './org.component.html',
  styles: []
})
export class OrgComponent extends AbstractComponent implements OnInit, RightKindInterface {

  // 전달받은 데이터
  @Input()
  public menuDetailData: any;

  // 계층형 노드 목록
  public nodeList: Array<any> = [];

  // 선택된 마지막 노드
  public selectedLastNode: any;

  // 수정시 등록한 노드
  public modifyItem: any;

  // request 파라미터
  public requestData: any;

  // 조직 선택 값
  public selectVal: string;

  // 전체 장비 open
  public isAllOn: boolean = true;

  // two-depth show/hide emitter
  @Output()
  public onShowTwoDepth = new EventEmitter<any>();


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

    // 선택 필터 클릭에 의한 셋팅
    if (this.menuDetailData.fltrVal) {
      let fltrVal = _.cloneDeep(this.menuDetailData.fltrVal);
      this.modifyItem = {
        fltrUid: this.menuDetailData.fltrUid,
        modifyList: fltrVal
      };
      this.selectedLastNode = this.modifyItem;
      this.showTwoDepth(true);
    }

    // 전체 카운트 초기화
    this.menuDetailData['allCnt'] = 0;

    // 조직 change 이벤트 발생
    this.selectVal = 'SKT';
    this.changeDpt();

  }

  /**
   * Override Method
   * 필터조건에 사용되는 데이터
   *
   * @return 필터 조건 Object
   */
  public getFilterData(): any {
    return null;
  }

  /**
   * 부서 Change 이벤트
   *
   */
  public changeDpt(): void {
    this.requestData.orgCd = [this.selectVal];
    this.hierarchyService.getEpmOrgList(this.requestData)
    .then(result => {
      if (result.code === CommonConstant.CODE.RESULT_CODE.SUCCESS) {
        let dataList = result.data;

        let allCount = 0;
        for (let i = dataList.length -1; i >= 0; i--) {
          result.data[i].nodeList = [];

          // 각 계층 코드 리스트 []
          result.data[i].hierarchy = [this.selectVal];

          if (result.data[i].value) {
            allCount += Number(result.data[i].value);
          }
        }

        // 전체 카운트
        this.menuDetailData.allCnt = allCount;

        this.nodeList = result.data;
      }
    });
  }

  /**
   * 1depth 노드 선택시 폴더 on/off
   *
   * @param item (선택된 노드)
   */
  public click1Depth(item): void {

    if (item.isOn) {
      item.isOn = false;

    } else {
      // 하위 리스트가 없는 경우
      if (!item.nodeList.length) {
        // 각 계층 코드 리스트 [0depth]
        item.hierarchy[1] = item.code;

        this.requestData.orgCd = item.hierarchy;
        this.hierarchyService.getEpmOrgList(this.requestData)
        .then(result => {
          if (result.code === CommonConstant.CODE.RESULT_CODE.SUCCESS) {
            for (let i = result.data.length -1; i >= 0; i--) {

              result.data[i].nodeList = [];
              result.data[i].hierarchy = _.cloneDeep(item.hierarchy);
            }

            item.nodeList = result.data;
          }
        });
      }

      item.isOn = true;
    }

  }

  /**
   * 2depth 노드 선택시 폴더 on/off
   *
   * @param item (선택된 노드)
   */
  public click2Depth(item): void {

    if (item.isOn) {
      item.isOn = false;

    } else {
      // 하위 리스트가 없는 경우
      if (!item.nodeList.length) {

        // 각 계층 코드 리스트 [0depth, 1depth]
        item.hierarchy[2] = item.code;

        this.requestData.orgCd = item.hierarchy;
        this.hierarchyService.getEpmOrgList(this.requestData)
        .then(result => {
          if (result.code === CommonConstant.CODE.RESULT_CODE.SUCCESS) {
            for (let i = result.data.length -1; i >= 0; i--) {
              result.data[i].nodeList = [];
              result.data[i].hierarchy = _.cloneDeep(item.hierarchy);
            }

            item.nodeList = result.data;
          }
        });
      }
      item.isOn = true;
    }

  }

  /**
   * 마지막 노드 선택시 select
   *
   * @param item (마지막 노드 object)
   * @param depth (현재 노드의 depth)
   */
  public clickLastDepth(item, depth): void {

    // 선택한 대상의 개수가 10,000개 이상일 경우 하위 오픈
    if (Number(item.value) > 10000) {
      if (depth == 1) {
        this.click1Depth(item);
        return;
      } else if(depth == 2) {
        this.click2Depth(item);
        return;
      }
    }

    // 이전 선택된 마지막 노드 선택 해제
    if (this.selectedLastNode) {
      this.selectedLastNode.isSelected = false;
    }

    // 현재 노드 선택
    item.isSelected = true;

    // 이전 데이터를 다시 열었 경우
    if (this.selectedLastNode && this.selectedLastNode.code === item.code) {

      // two-depth 화면 열기
      this.showTwoDepth(true);
      return;
    }

    // 다른 데이터를 열었을 경우
    if (!item.hierarchy[depth]) {
      item.hierarchy[depth] = item.code;
    }

    item.fltrUid = this.menuDetailData.fltrUid;
    item.hierarchy = _.cloneDeep(item.hierarchy.slice(0, depth + 1));

    // 2depth 오픈
    this.selectedLastNode = {
      fltrUid: this.menuDetailData.fltrUid
    };
    this.showTwoDepth(true);

    this.requestData.orgCd = item.hierarchy;

    if (this.menuDetailData.scrnClNm.endsWith('enb')
      || this.menuDetailData.scrnClNm.endsWith('cell')) {
      // ENB 코드 조회
      this.hierarchyService.getEpmEnbList(this.requestData)
      .then(result => {
        if (result.code === CommonConstant.CODE.RESULT_CODE.SUCCESS) {
          for (let i = result.data.length -1; i >= 0; i--) {
            result.data[i].isOn = false;
          }

          item.detailList = result.data;

          // two-depth 화면 열기
          this.selectedLastNode = item;
          this.showTwoDepth(true);
        } else {
          this.showTwoDepth(false);
        }
      });

    } else if (this.menuDetailData.scrnClNm.endsWith('mtso')) {
      // 국사코드 조회
      this.hierarchyService.getEpmMtsoList(this.requestData)
      .then(result => {
        if (result.code === CommonConstant.CODE.RESULT_CODE.SUCCESS) {
          for (let i = result.data.length -1; i >= 0; i--) {
            result.data[i].isOn = false;
          }

          item.detailList = result.data;

          // two-depth 화면 열기
          this.selectedLastNode = item;
          this.showTwoDepth(true);
        } else {
          this.showTwoDepth(false);
        }
      });
    }
  }

  /**
   * two depth 화면 show / hide
   *
   * @param isShow (show / hide boolean값)
   */
  public showTwoDepth(isShow: boolean): void {
    this.selectedLastNode.isShow = isShow;
    this.onShowTwoDepth.emit(this.selectedLastNode);
  }

  /**
   * 3자리 이상 숫자 콤마 추가
   *
   * @param num (숫자)
   */
  public comma(num): string {
    let regexp = /\B(?=(\d{3})+(?!\d))/g;
    return num.toString().replace(regexp, ',');
  }

}
