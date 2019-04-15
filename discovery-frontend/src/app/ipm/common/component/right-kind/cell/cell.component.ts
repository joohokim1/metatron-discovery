import { Component, OnInit, OnChanges, ElementRef, Injector, Input, SimpleChanges, Inject, Renderer2, ViewChild } from '@angular/core';
import { DOCUMENT } from '@angular/common';
import { AbstractComponent } from '../../../../../common/component/abstract.component';
import { RightKindInterface } from '../right-kind.component';
import { Alert } from '../../../../../common/util/alert.util';
import * as _ from 'lodash';

@Component({
  selector: 'cell',
  templateUrl: './cell.component.html',
  styles: []
})
export class CellComponent extends AbstractComponent implements OnInit, OnChanges, RightKindInterface {

  // 전달받은 데이터
  @Input()
  public selectedLastNode: any;

  // 상세 목록
  public detailList: Array<any> = [];

  // 검색 목록
  public searchList: Array<any> = [];

  // 최종 선택 목록
  public finalList: Array<any> = [];

  // Cell 목록
  public cellList: Array<any> = [];

  // popup용 Cell No 목록
  public popupCellList: Array<any> = [];

  // popup용 Cell No 목록 Temp
  public popupCellListTemp: Array<any> = [];

  // 상세 Cell 전체 수
  public detailAllCnt: number = 0;

  // 상세 Cell 선택 수
  public detailCellCheckedCount: number = 0;

  // 최종 Cell 선택 수
  public finalCheckedCount: number = 0;

  // 상세 목록 전체 선택 여부
  public isDetailAllChecked: boolean = false;

  // 분석대상 전체 선택 여부
  public isFinalAllChecked: boolean = false;

  // Cell No 일괄 선택 팝업 창 여부
  public isCellDspBlock: boolean = false;

  // 팝업 cell 전체 선택 여부
  public isPopupCellChecked: boolean = false;


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
    // 화면에서 cellList를 그리기 위한 loop용 collection. 0은 사용하지 않는 값.
    // 최대 cell No가 48이므로 vendor사 구분없이 48까지 선택 가능
    this.cellList = Array(48).fill(0).map((x, i) => {
      return {
        code: i + 1 + '',
        isChecked: false
      };
    });

    this.popupCellList = _.cloneDeep(this.cellList);
  }

  ngOnChanges(changes: SimpleChanges) {

    // 최초 시작의 경우 이전 값이 없어 undefined 이므로 {}로 초기화
    let preVal = changes.selectedLastNode.previousValue || {};
    let curVal = changes.selectedLastNode.currentValue || {};

    // 수정
    if (this.selectedLastNode.modifyList) {
      // 초기화
      this.finalList = []
      this.finalCheckedCount = 0;
      this.isFinalAllChecked = false;
      if (this.searchBox.nativeElement) {
        this.searchBox.nativeElement.value = '';
      }

      let modifyList = this.selectedLastNode.modifyList || [];
      this.finalList = this.transFinalList(modifyList);

    // 왼쪽 메뉴가 변경되었을 경우
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

      // Cell 리스트 및 카운트 초기화
      this.detailList = [];
      this.searchList = [];
      this.detailCellCheckedCount = 0;
      this.isDetailAllChecked = false;
      if (this.searchBox.nativeElement) {
        this.searchBox.nativeElement.value = '';
      }

      // 선택한 Enb에 대한 Cell 초기 정보 셋팅
      this.detailList = this.selectedLastNode.detailList || [];
      if (this.detailList.length) {

        for (let data of this.detailList) {
          data.isChecked = false;
          data.isOpen = false;
          // Cell List 생성
          data.cellList = _.cloneDeep(this.cellList);
        }
      }
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

    // fltrVal 세팅
    for (let enb of this.finalList) {

      if (enb.cellList.length == this.cellList.length) {
        fltrVal.push({
          code: [enb.code],
          name: enb.name
        });
      } else {
        for (let cell of enb.cellList) {

          // 선택되어진 셀 fltrVal에 추가
          fltrVal.push({
            code: [enb.code, cell.code + ''],
            name: enb.name
          });
        }
      }
    }

    // 선택 값이 2개 이상인 경우
    if (this.finalListCellCount() === 1) {
      dtNm = fltrVal[0].name + '('+fltrVal[0].code[0]+')';
    } else if (this.finalListCellCount() > 1) {
      dtNm = fltrVal[0].name + '('+fltrVal[0].code[0]+')' + ' 외 ' + (this.finalListCellCount() - 1);
    }

    // return
    return {
      fltrVal: fltrVal,
      dtNm: dtNm
    };

  }

  /**
   * 수정시 분석대상 데이터를 그릴수 있는 형태로 변환
   *
   * @param finalList (수정 데이터 원본)
   */
  public transFinalList(finalList: Array<any>): Array<any> {
    let retFinalList = [];

    for (let cell of finalList) {

      // enb 존재 여부
      let isEnbExist = false;
      for (let enb of retFinalList) {
        // 해당 enb가 있는 경우
        if (enb.code === cell.code[0]) {
          // cell 추가
          enb.cellList.push({
            code: cell.code[1]
          });

          isEnbExist = true;
        }
      }

      // 해당 enb가 없는 경우
      if (!isEnbExist) {
        // enb 생성
        retFinalList.push({
          code: cell.code[0],
          name: cell.name,
          // cell 생성
          cellList: [{code: cell.code[1]}]
        });
      }

    }

    return retFinalList;
  }


  /**
   * 상세 목록 전체 선택
   */
  public checkedAll(): void {
    // 상세 목록 전체 선택 해제
    if (this.isDetailAllChecked) {
      this.isDetailAllChecked = false;
      for (let row of this.searchList) {
        row.isChecked = false;
        for (let cell of row.cellList) {
          // 체크 해제하는 경우 카운트 -1
          if (cell.isChecked) {
            this.detailCellCheckedCount--;
          }
          cell.isChecked = false;
        }
      }

    // 상세 목록 전체 선택
    } else {
      this.isDetailAllChecked = true;
      for (let row of this.searchList) {
        row.isChecked = true;
        for (let cell of row.cellList) {
          // 체크하는 경우 카운트 +1
          if (!cell.isChecked) {
            this.detailCellCheckedCount++;
          }
          cell.isChecked = true;
        }
      }
    }
  }

  /**
   * 상세 단일 선택
   *
   * @param e (이벤트 객체)
   * @param row (선택된 상세 정보 object)
   */
  public checkedRow(e, row): void {

    // 이벤트 버블링 방지
    e.stopPropagation();

    // row on/off
    if (row.isChecked) {
      row.isChecked = false;

      // row 해제시 cell 모두 해제 및 카운트 변경
      for(let cell of row.cellList) {
        if (cell.isChecked) {
          this.detailCellCheckedCount--;
        }
        cell.isChecked = false;
      }

    } else {
      row.isChecked = true;

      // row 선택시 cell 모두 선택 및 카운트 변경
      for(let cell of row.cellList) {
        if (!cell.isChecked) {
          this.detailCellCheckedCount++;
        }
        cell.isChecked = true;
      }

    }

    // 전체선택 on/off toggle
    this.checkedAllToggle();
  }

  /**
   * 상세 단일 셀 선택
   *
   * @param row (선택된 row 정보 object)
   * @param cell (선택된 cell 정보 object)
   */
  public checkedCell(row, cell): void {
    // cell on/off
    if (cell.isChecked) {
      cell.isChecked = false;
      this.detailCellCheckedCount--;
    } else {
      cell.isChecked = true;
      this.detailCellCheckedCount++;
    }

    // Cell 전체 선택버튼 on/off toggle
    this.checkedRowAllToggle(row);
    this.checkedAllToggle();
  }

  /**
   * 전체선택 on/off toggle
   *
   * @param row (row object)
   */
  public checkedAllToggle(): void {
    this.isDetailAllChecked = true;
    for (let row of this.searchList) {
      if (!row.isChecked) {
        this.isDetailAllChecked = false;
        break;
      }
    }
  }

  /**
   * row 전체선택 on/off toggle
   *
   * @param row (row object)
   */
  public checkedRowAllToggle(row): void {
    row.isChecked = true;
    for (let cell of row.cellList) {
      if (!cell.isChecked) {
        row.isChecked = false;
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

    // searchList 초기화
    this.searchList = [];

    // 상세 목록에서 검색 목록 추출
    for (let row of this.detailList) {

      // name에서 검색
      if (row.name && row.name.includes(keyword.trim())) {
        this.searchList.push(row);

        // code에서 검색
      } else if (row.code && row.code.includes(keyword.trim())) {
        this.searchList.push(row);
      }

    }

  }

  /**
   * 상세 2depth창 닫기
   */
  public closeDetailBlock(): void {

    // 상세 초기화
    this.detailList = [];
    this.searchList = [];
    this.detailCellCheckedCount = 0;
    this.isDetailAllChecked = false;;

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

    // 최대 Cell 개수 초과시 초기화용 temp List
    let tempList = [];
    let finalListTemp = _.cloneDeep(this.finalList);
    let finalCheckedCountTemp = this.finalCheckedCount;

    for (let row of this.detailList) {

      // 한row의 cell이 전부 체크되어 있는 경우
      if (row.isChecked) {
        let isDuplicate = false;
        for (let i in this.finalList) {
          // 중복 체크
          if (this.finalList[i].code === row.code) {
            // row 덮어쓰기
            this.finalCheckedCount = this.finalCheckedCount - this.finalList[i].cellList.length;
            this.finalList[i] = _.cloneDeep(row);
            this.finalList[i].isOpen = false;
            this.finalCheckedCount = this.finalCheckedCount + row.cellList.length;
            isDuplicate = true;
            break;

          }
        }
        if (!isDuplicate) {
          // 분석대상에 추가
          let tempRow = _.cloneDeep(row);
          tempRow.isOpen = false;
          tempList.push(tempRow);
          this.finalCheckedCount = this.finalCheckedCount + tempRow.cellList.length;
        }

      // 한row의 cell이 일부 체크되어 있는 경우
      } else {

        // 중복 체크
        let isDuplicate = false;
        let tempRow = _.cloneDeep(row);
        tempRow.isChecked = true;
        tempRow.isOpen = false;
        tempRow.cellList = [];

        for (let i in this.finalList) {
          // 이미 분석대상에 있는 row일 경우 cell만 추가
          if (this.finalList[i].code === row.code) {

            // 분석대상 row가 가지고 있는 셀 목록 target
            let cellList = [];
            for (let cell of this.finalList[i].cellList) {
              cellList.push(cell.code);
            }
            let target = cellList.join(',');

            for (let cell of row.cellList) {
              if (cell.isChecked) {
                // 분석대상 Cell목록에 없는 Cell이라면 추가
                if (!target.includes(cell.code)) {
                  this.finalList[i].cellList.push(_.cloneDeep(cell));
                  this.finalCheckedCount++;
                }
              }
            }

            // 분석대상 row 전체선택 on/off toggle
            this.finalCheckedRowToggle(this.finalList[i]);

            isDuplicate = true;
            break;
          }
        }

        // 분석대상에 없는 row일 경우 check된 cell만을 가진 row 추가
        if (!isDuplicate) {
          for (let cell of row.cellList) {
            if (cell.isChecked) {
              tempRow.cellList.push(_.cloneDeep(cell));
            }
          }

          if (tempRow.cellList.length) {
            tempList.push(tempRow);
            this.finalCheckedCount = this.finalCheckedCount + tempRow.cellList.length;
          }
        }
      }
    }

    // 설정 된 순서대로 추가
    tempList = tempList.reverse();
    for (let i = 0; i < tempList.length; i++) {
      this.finalList.splice(0, 0, tempList[i]);
    }

    // 셀 추가 개수 제한
    if (this.finalListCellCount() > 100) {
      Alert.error('100개 이상의 Cell을 등록할 수 없습니다.');
      this.finalList = finalListTemp;
      this.finalCheckedCount = finalCheckedCountTemp;
    }

    // 분석대상 전체선택 on/off toggle
    this.finalcheckedAllToggle();
  }

  /**
   * 분석대상 삭제
   */
  public delCheckData(): void {

    for (let i = this.finalList.length - 1; i >= 0; i--) {

      // 한row의 cell이 전부 체크되어 있는 경우
      if (this.finalList[i].isChecked) {
        this.finalCheckedCount = this.finalCheckedCount - this.finalList[i].cellList.length;
        this.finalList.splice(i, 1);

      // 한row의 cell이 일부 체크되어 있는 경우
      } else {
        let cellList = this.finalList[i].cellList;
        for (let j = cellList.length - 1; j >= 0; j--) {
          if (cellList[j].isChecked) {
            this.finalCheckedCount--;
            cellList.splice(j, 1);
          }
        }
      }
    }

    this.isFinalAllChecked = false;
  }

  /**
   * 분석대상 전체 선택
   */
  public checkedAllFinal(): void {

    // 분석대상 목록 전체 선택 해제
    if (this.isFinalAllChecked) {
      this.isFinalAllChecked = false;
      for (let row of this.finalList) {
        row.isChecked = false;
        for (let cell of row.cellList) {
          // 체크 해제하는 경우 카운트 -1
          if (cell.isChecked) {
            this.finalCheckedCount--;
          }
          cell.isChecked = false;
        }
      }

    // 분석대상 목록 전체 선택
    } else {
      this.isFinalAllChecked = true;
      for (let row of this.finalList) {
        row.isChecked = true;
        for (let cell of row.cellList) {
          // 체크하는 경우 카운트 +1
          if (!cell.isChecked) {
            this.finalCheckedCount++;
          }
          cell.isChecked = true;
        }
      }
    }

  }

  /**
   * 분석대상 row 선택
   *
   * @param e (이벤트 객체)
   * @param row (선택된 row 정보 object)
   */
  public checkedRowFinal(e, row): void {

    // 이벤트 버블링 방지
    e.stopPropagation();

    // row on/off
    if (row.isChecked) {
      row.isChecked = false;

      // row 해제시 cell 모두 해제 및 카운트 변경
      for(let cell of row.cellList) {
        if (cell.isChecked) {
          this.finalCheckedCount--;
        }
        cell.isChecked = false;
      }

    } else {
      row.isChecked = true;

      // row 선택시 cell 모두 선택 및 카운트 변경
      for(let cell of row.cellList) {
        if (!cell.isChecked) {
          this.finalCheckedCount++;
        }
        cell.isChecked = true;
      }

    }

    this.finalcheckedAllToggle();
  }

  /**
   * 분석대상 Cell 선택
   *
   * @param row (선택된 row 정보 object)
   * @param cell (선택된 cell 정보 object)
   */
  public checkedCellFinal(row, cell): void {
    // cell on/off
    if (cell.isChecked) {
      cell.isChecked = false;
      this.finalCheckedCount--;
    } else {
      cell.isChecked = true;
      this.finalCheckedCount++;
    }

    // 분석대상 전체 선택버튼 on/off toggle
    this.finalCheckedRowToggle(row);
    this.finalcheckedAllToggle();
  }

  /**
   * 분석대상 전체선택 on/off toggle
   */
  public finalcheckedAllToggle(): void {

    this.isFinalAllChecked = true;
    for (let row of this.finalList) {
      if (!row.isChecked) {
        this.isFinalAllChecked = false;
        break;
      }
    }

  }

  /**
   * 분석대상 row 전체선택 on/off toggle
   *
   * @param row (row object)
   */
  public finalCheckedRowToggle(row): void {
    row.isChecked = true;
    for (let cell of row.cellList) {
      if (!cell.isChecked) {
        row.isChecked = false;
        break;
      }
    }
  }

  /**
   * Cell 팝업 창 열기
   */
  public openCellPopup(): void {
    this.isCellDspBlock = true;
    this.popupCellListTemp = _.cloneDeep(this.popupCellList);
  }

  /**
   * Cell 팝업 창 닫기
   */
  public closeCellPopup(): void {
    this.isCellDspBlock = false;
    this.popupCellList = _.cloneDeep(this.popupCellListTemp);
  }

  /**
   * Cell 팝업 창 선택한 값으로 분석대상 선택
   */
  public selectCellPopup(): void {

    // 선택된 Cell No 가져오기
    let noList = '';
    let arr: string[] = [];
    for (let no of this.popupCellList) {
      if (no.isChecked) {
        arr.push(no.code);
      }
    }

    // 선택시 체크된것이 없을 경우
    if (!arr.length) {
      Alert.error('1개 이상의 Cell을 선택해야 합니다.');
      return;
    }
    noList = arr.join(',');

    // 분석대상 Cell 모두 해제
    for (let row of this.finalList) {
      for (let cell of row.cellList) {
        if (cell.isChecked) {
          this.finalCheckedCount--;
        }
        cell.isChecked = false;
      }
    }

    // 선택된 Cell No로 분석대상 Cell 선택
    for (let row of this.finalList) {
      for (let cell of row.cellList) {
        if (noList.includes(cell.code)) {
          if (!cell.isChecked) {
            this.finalCheckedCount++;
          }
          cell.isChecked = true;
        }
      }
      this.finalCheckedRowToggle(row);

    }
    this.finalcheckedAllToggle();

    this.isCellDspBlock = false;
  }

  /**
   * 팝업 Cell 전체 전택
   */
  public checkedPopupCell(): void {
    // 전체 선택 해제
    if (this.isPopupCellChecked) {
      this.isPopupCellChecked = false;
      for (let cell of this.popupCellList) {
        cell.isChecked = false;
      }

    // 전체 선택
    } else {
      this.isPopupCellChecked = true;
      for (let cell of this.popupCellList) {
        cell.isChecked = true;
      }
    }
  }

  /**
   * 분석대상 cell count 조회
   */
  public finalListCellCount(): number {
    let finalCellCount = 0;
    for(let row of this.finalList) {
      finalCellCount = finalCellCount + row.cellList.length;
    }
    return finalCellCount;
  }

  /**
   * 3자리 이상 숫자 콤마 추가
   *
   * @param num (숫자)
   */
  public comma(num): string {
    num = num + '';
    let regexp = /\B(?=(\d{3})+(?!\d))/g;
    return num.replace(regexp, ',');
  }

  /**
   * 지정한 length만큼 특정문자를 왼쪽에 덧붙이는 함수
   *
   * @param number (값)
   * @param length (자리 수)
   * @param addStr (덧붙일 문자)
   */
  public lpad(number, length, addStr): string {
    var str = '' + number;
    while (str.length < length) {
      str = addStr + str;
    }
    return str;
  }
}
