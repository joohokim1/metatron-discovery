import { Component, OnInit, OnChanges, ElementRef, Injector, Input, SimpleChanges, Inject, Renderer2, ViewChild } from '@angular/core';
import { DOCUMENT } from '@angular/common';
import { AbstractComponent } from '../../../../../common/component/abstract.component';
import { RightKindInterface } from '../right-kind.component';
import { Alert } from '../../../../../common/util/alert.util';
import * as _ from 'lodash';

@Component({
  selector: 'enb',
  templateUrl: './enb.component.html',
  styles: []
})
export class EnbComponent extends AbstractComponent implements OnInit, OnChanges, RightKindInterface {

  // 전달받은 데이터
  @Input()
  public selectedLastNode: any;

  // 상세 목록
  public detailList: Array<any> = [];

  // 검색 목록
  public searchList: Array<any> = [];

  // 최종 선택 목록
  public finalList: Array<any> = [];

  // 상세 ENB 전체 수
  public detailAllCnt: number = 0;

  // 상세 Enb 선택 수
  public detailCheckedCount: number = 0;

  // 최종 ENB 선택 수
  public finalCheckedCount: number = 0;

  // 상세 목록 전체 선택 여부
  public isDetailAllChecked: boolean = false;

  // 분석대상 전체 선택 여부
  public isFinalAllChecked: boolean = false;

  @ViewChild('searchBox')
  public searchBox: ElementRef;

  // 생성자
  constructor(
    protected elementRef: ElementRef,
    protected injector: Injector,
    @Inject(DOCUMENT) private document: Document,
    private renderer: Renderer2
  ) {
    super(elementRef, injector);
  }

  ngOnInit() {
  }

  ngOnChanges(changes: SimpleChanges) {

    // 최초 시작의 경우 이전 값이 없어 undefined 이므로 {}로 초기화
    let preVal = changes.selectedLastNode.previousValue || {};
    let curVal = changes.selectedLastNode.currentValue || {};

    if (this.selectedLastNode.modifyList) {
      // 초기화
      this.finalList = []
      this.finalCheckedCount = 0;
      this.isFinalAllChecked = false;
      if (this.searchBox.nativeElement) {
        this.searchBox.nativeElement.value = '';
      }

      this.finalList = this.selectedLastNode.modifyList || [];

    } else if (preVal.fltrUid !== curVal.fltrUid) {

      // 메뉴 필터, 선택 필터 변경 시 분석대상 초기화
      this.finalList = [];
      this.finalCheckedCount = 0;
      this.isFinalAllChecked = false;
      if (this.searchBox.nativeElement) {
        this.searchBox.nativeElement.value = '';
      }

    }

    if (preVal.code !== curVal.code) {

      this.detailAllCnt = this.selectedLastNode.detailList ? Number(this.selectedLastNode.detailList.length) : 0

      // 상세, 최종선택 리스트 및 카운트 초기화
      this.detailList = [];
      this.detailCheckedCount = 0;
      this.isDetailAllChecked = false;
      if (this.searchBox.nativeElement) {
        this.searchBox.nativeElement.value = '';
      }

      this.detailList = this.selectedLastNode.detailList || [];
      this.searchList = this.detailList;

    }
  }

  /**
   * Override Method
   * 필터조건에 사용되는 데이터
   *
   * @return 필터 조건 Object
   */
  public getFilterData(): any {

    // return Data
    let dtNm = '';
    let fltrVal = [];

    // dtNm 셋팅용 array
    let dtNmArr = [];
    for (let item of this.finalList) {
      fltrVal.push({
        code: item.code,
        name: item.name
      });

      dtNmArr.push(item.name);
    }

    // 수정시 조회 대상의 정보를 배열의 마지막에 삽입
    // name - 조회 이름 (ex ~~동)
    // hierarchy - [시도, 시군구, 읍면동]
    // fltrVal.push({
    //   code: this.selectedLastNode.code,
    //   name: this.selectedLastNode.name,
    //   hierarchy: this.selectedLastNode.hierarchy
    // });

    // 선택 값이 2개 이상인 경우
    if (dtNmArr.length === 1) {
      dtNm = dtNmArr[0];
    } else if (dtNmArr.length > 1) {
      dtNm = dtNmArr[0] + ' 외 ' + (dtNmArr.length - 1);
    }

    // return
    return {
      dtNm: dtNm,
      fltrVal: fltrVal
    };

  }

  /**
   * 상세 목록 전체 선택
   */
  public checkedAll(): void {
    // 상세 목록 전체 선택 해제
    if (this.isDetailAllChecked) {
      this.isDetailAllChecked = false;

      for (let item of this.searchList) {
        // 체크 해제하는 경우 카운트 -1
        if (item.isChecked) {
          this.detailCheckedCount--;
        }
        item.isChecked = false;
      }

    // 상세 목록 전체 선택
    } else {
      this.isDetailAllChecked = true;
      for (let item of this.searchList) {
        // 체크하는 경우 카운트 +1
        if (!item.isChecked) {
          this.detailCheckedCount++;
        }
        item.isChecked = true;
      }
    }
  }

  /**
   * 상세 단일 선택
   *
   * @param item (선택된 상세 정보 object)
   */
  public checkedRow(item): void {
    // row on/off
    if (item.isChecked) {
      item.isChecked = false;
      this.detailCheckedCount--;
    } else {
      item.isChecked = true;
      this.detailCheckedCount++;
    }

    // 전체 선택버튼 on/off toggle
    this.isDetailAllChecked = true;
    for (let item of this.searchList) {
      if (!item.isChecked) {
        this.isDetailAllChecked = false;
        break;
      }
    }
  }

  /**
   * 상세 검색
   *
   * @param keyword (검색어)
   */
  public searchDetail(keyword: string): void {
    // 전부 소문자로 바꾸기 (대소문자 구분하지 않기 위함)
    keyword = keyword.trim().toLowerCase();

    // searchList 초기화
    this.searchList = [];

    // 상세 목록에서 검색 목록 추출
    for (let item of this.detailList) {

      // name에서 검색
      if (item.name && item.name.toLowerCase().includes(keyword)) {
        this.searchList.push(item);

      // code에서 검색
      } else if (item.code && item.code.toLowerCase().includes(keyword)) {
        this.searchList.push(item);
      }

    }

  }

  /**
   * 상세 2depth창 닫기
   */
  public closeDetailBlock(): void {

    // 상세, 최종선택 리스트 초기화
    // this.detailList = [];
    // this.finalList = [];
    // this.detailCheckedCount = 0;
    // this.finalCheckedCount = 0;

    this.selectedLastNode.isShow = false;
    this.selectedLastNode.isSelected = false;

    // 우측 열기 버튼 DOM
    let $rFlex = this.document.querySelector('.r_flexible');
    this.renderer.removeClass($rFlex, 'open');
    this.renderer.removeClass($rFlex, 'panel');

    // two-depth 화면
    let $twoDepth = this.document.querySelector('.hidden_panel_wrap.dsp_block');
    this.renderer.removeClass($twoDepth, 'open');
  }

  /**
   * 분석대상 추가
   */
  public addCheckData(): void {

    // 최대 Enb 개수 초과시 초기화용 temp List
    let finalListTemp = _.cloneDeep(this.finalList);
    let finalCheckedCountTemp = this.finalCheckedCount;

    let tempList = [];

    label:
    for (let item of this.detailList) {
      if (item.isChecked) {

        // 중복 체크
        for (let orgItem of this.finalList) {
          if (orgItem.code === item.code) {
            continue label;
          }
        }


        tempList.push(_.cloneDeep(item));
        this.finalCheckedCount++;
      }
    }

    // 설정 된 순서대로 추가
    tempList = tempList.reverse();
    for (let i = 0; i < tempList.length; i++) {
      this.finalList.splice(0, 0, tempList[i]);
    }

    // ENB 추가 개수 제한
    if (this.finalList.length > 100) {
      Alert.error('100개 이상의 Enb를 등록할 수 없습니다.');
      this.finalList = finalListTemp;
      this.finalCheckedCount = finalCheckedCountTemp;
      return;
    }

    this.finalcheckedAllToggle();
  }

  /**
   * 분석대상 삭제
   */
  public delCheckData(): void {
    for (let i = this.finalList.length - 1; i >= 0; i--) {
      if (this.finalList[i].isChecked) {
        this.finalList.splice(i, 1);
        this.finalCheckedCount--;
      }
    }

    this.finalcheckedAllToggle();
  }

  /**
   * 분석대상 전체 선택
   */
  public checkedAllFinal(): void {
    // 분석대상 전체 선택 해제
    if (this.isFinalAllChecked) {
      this.isFinalAllChecked = false;
      for (let item of this.finalList) {
        // 체크 해제하는 경우 카운트 -1
        if (item.isChecked) {
          this.finalCheckedCount--;
        }
        item.isChecked = false;
      }

    // 분석대상 전체 선택
    } else {
      this.isFinalAllChecked = true;
      for (let item of this.finalList) {
        // 체크하는 경우 카운트 +1
        if (!item.isChecked) {
          this.finalCheckedCount++;
        }
        item.isChecked = true;
      }
    }
  }

  /**
   * 분석대상 선택
   *
   * @param item (선택된 상세 정보 object)
   */
  public checkedRowFinal(item): void {
    // row on/off
    if (item.isChecked) {
      item.isChecked = false;
      this.finalCheckedCount--;
    } else {
      item.isChecked = true;
      this.finalCheckedCount++;
    }

    this.finalcheckedAllToggle();
  }

  /**
   * 분석대상 전체선택 on/off toggle
   */
  public finalcheckedAllToggle(): void {

    // 전체 선택버튼 on/off toggle
    if (this.finalList.length === 0) {
      this.isFinalAllChecked = false;
    } else {
      if (this.finalCheckedCount === this.finalList.length) {
        this.isFinalAllChecked = true;
      } else {
        this.isFinalAllChecked = false;
      }
    }

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
