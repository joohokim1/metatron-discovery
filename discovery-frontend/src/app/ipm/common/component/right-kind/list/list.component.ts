import { Component, OnInit, ElementRef, Injector, Input } from '@angular/core';
import { AbstractComponent } from '../../../../../common/component/abstract.component';
import { ListService } from '../../../service/right-kind/list/list.service';
import { CommonConstant } from '../../../constant/common-constant';
import { RightKindInterface } from '../right-kind.component';
import * as _ from 'lodash';

@Component({
  selector: 'list',
  templateUrl: './list.component.html',
  styles: []
})
export class ListComponent extends AbstractComponent implements OnInit, RightKindInterface {

  // 전달받은 데이터
  @Input()
  public menuDetailData: any;

  // 선택된 데이터 count
  public checkedCount: number = 0;

  // 전체 데이터 List
  public allDataList: Array<any> = [];

  // 페이지 당 데이터 List
  public pageDataList: Array<any> = [];

  // 검색된 데이터 List
  public searchDataList: Array<any> = [];

  // 필터 수정시 원본 데이터
  public modifyDataList: Array<any> = [];

  // 리스트 정보 데이터
  public valuesData: Array<any> = [];

  // 전체선택 구분
  public isCheckedAll: boolean = false;

  // 페이지 선택 구분
  public isCheckedPage: boolean = false;

  /**
   * Pagination Variable
   */
  //현재 페이지 번호
  public pageNo: number = 1;

  //한 페이지의 row 수
  public rowPerPage: number = 10;

  // 총 페이지의 개수
  public pageCount: number = 0;

  // 현재 블럭 번호
  public blockNo: number = 1;

  // 한 블럭의 페이지 개수
  public pagePerBlock: number = 5;

  // 총 블럭의 개수
  public blockCount: number = 0;

  // 페이지 번호 List
  public pageNoList: number[] = [];

  // 생성자
  constructor(
    protected elementRef: ElementRef,
    protected injector: Injector,
    private listService: ListService
  ) {
    super(elementRef, injector);
  }

  ngOnInit() {

    // 초기화
    this.allDataList = [];

    // boundType이 QRY일 경우 드루이드 질의 API 호출
    if (this.menuDetailData.fltrDtlList[0].rmk === 'QRY') {

      // list type 데이터
      this.valuesData = this.menuDetailData.fltrDtlList[0].values;

      this.listService.getCommCode(this.valuesData[0].name, this.menuDetailData.occrDt)
      .then(result => {
        if (result.code === CommonConstant.CODE.RESULT_CODE.SUCCESS) {

          // row data 세팅
          this.allDataList = result.data;
          this.searchDataList = this.allDataList;

          // 수정시 초기 데이터 세팅
          if (this.menuDetailData.fltrVal) {
            this.modifyDataList = _.cloneDeep(this.menuDetailData.fltrVal);
            this.checkedCount = this.menuDetailData.fltrVal.length;
          }

          // List 및 Page 초기 세팅
          this.init(this.searchDataList);

        }
      });
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
    for (let item of this.allDataList) {

      // 선택된 데이터 존재
      if (item.isChecked) {

        // fltrVal 셋팅용 object
        let obj: any = {};
        obj.code = item.code;

        // row에 name이 있는 경우
        if (item.name) {

          obj.name = item.name;
          dtNmArr.push(item.name);

        // row에 name이 없는 경우
        } else {
          dtNmArr.push(item.code);
        }

        // fltrVal 값 셋팅
        fltrVal.push(obj);
      }
    }

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
   * List 및 Page 셋팅
   *
   * @param dataList - List에 표시할 DataSet
   */
  public init(dataList: Array<any>): void {

    // 페이지, 블럭 번호 초기화
    this.pageNo = 1;
    this.blockNo = 1;

    let start = (this.pageNo - 1) * this.rowPerPage;
    let end = this.pageNo * this.rowPerPage;
    // 해당 page에 보여줄 데이터 shallow copy
    // -> 두개의 배열중 하나를 수정해도 같이 변경됨.
    this.pageDataList = dataList.slice(start, end);

    // pagination data 셋팅
    // 화면에서 page를 그리는 loop용 collection. 0은 사용하지 않는 값.
    this.pageNoList = Array(this.pagePerBlock).fill(0).map((x, i)=>i+1);

    // 총 페이지 수 구하기
    let quotient = Math.floor(dataList.length / this.rowPerPage);
    let remainder = dataList.length % this.rowPerPage;
    this.pageCount =  !remainder ? quotient : quotient + 1;

    // 총 블럭 수 구하기
    quotient = Math.floor(this.pageCount / this.pagePerBlock);
    remainder = this.pageCount % this.pagePerBlock;
    this.blockCount =  !remainder ? quotient : quotient + 1;

    // 수정시 기존 선택 목록 체크
    if (this.modifyDataList) {
      for (let item1 of this.modifyDataList) {
        for (let item2 of dataList) {
          if (item1.code === item2.code) {
            item2.isChecked = true;
          }
        }
      }
    }

    // 전체 선택 버튼 on/off toogle
    this.checkedAllToogle();
    this.checkedPageToogle();

  }

  /**
   * 전체 선택
   */
  public checkedAll(): void {

    // 수정 데이터 존재시 수정 데이터 제거
    if (this.modifyDataList) {

      // 전체 검색일 경우
      if (this.searchDataList.length === this.allDataList.length) {
        // 수정 list(fltrVal) 초기화
        this.modifyDataList = [];

      // 부분 검색 경우
      } else {
        // row가 수정 list (fltrVal) 에 존재하면 삭제
        for (let i = 0; i < this.searchDataList.length; i++) {
          for (let j = 0; j < this.modifyDataList.length; j++) {
            if (this.searchDataList[i].code === this.modifyDataList[j].code) {
              this.modifyDataList.splice(j, 1);
              break;
            }
          }
        }
      }
    }

    // 전체 row 해제
    if (this.isCheckedAll) {

      this.isCheckedAll = false;
      this.isCheckedPage = false;

      for (let item of this.searchDataList) {
        if (item.isChecked) {
          item.isChecked = false;
          this.checkedCount--;
        }
      }

    // 전체 row 선택
    } else {

      this.isCheckedAll = true;
      this.isCheckedPage = true;

      for (let item of this.searchDataList) {
        if (!item.isChecked) {
          item.isChecked = true;
          this.checkedCount++;
        }
      }
    }
  }

  /**
   * Page 전체 선택
   */
  public checkedPage(): void {

    // 수정 데이터 존재시
    if (this.modifyDataList) {
      // row가 수정 list에 존재하면 삭제
      for (let i = 0; i < this.pageDataList.length; i++) {
        for (let j = 0; j < this.modifyDataList.length; j++) {
          if (this.pageDataList[i].code === this.modifyDataList[j].code) {
            this.modifyDataList.splice(j, 1);
            break;
          }
        }
      }
    }

    // 페이지 전체 row 해제
    if (this.isCheckedPage) {
      this.isCheckedPage = false;
      for (let item of this.pageDataList) {
        if (item.isChecked) {
          item.isChecked = false;
          this.checkedCount--;
        }
      }

    // 페이지 전체 row 선택
    } else {
      this.isCheckedPage = true;
      for (let item of this.pageDataList) {
        if (!item.isChecked) {
          item.isChecked = true;
          this.checkedCount++;
        }
      }
    }

    // 전체 선택 버튼 on/off toogle
    this.checkedAllToogle();
  }

  /**
   * 단일 row 선택
   *
   * @param item - row의 data
   */
  public checkedRow(item): void {

    // 수정 데이터 존재시
    if (this.modifyDataList) {
      // row가 수정 list에 존재하면 삭제
      for (let i = 0; i < this.modifyDataList.length; i++) {
        if (this.modifyDataList[i].code === item.code) {
          this.modifyDataList.splice(i, 1);
        }
      }
    }

    // 단일 row 선택 / 해제
    if (item.isChecked) {
      item.isChecked = false;
      this.checkedCount--;
    } else {
      item.isChecked = true;
      this.checkedCount++;
    }

    // 전체 선택 버튼 on/off toogle
    this.checkedAllToogle();
    this.checkedPageToogle();
  }

  /**
   * 페이지 이동
   *
   * @pageNo - 페이지 번호
   */
  public goPage(pageNo): void {

    let start = (pageNo - 1) * this.rowPerPage;
    let end = pageNo * this.rowPerPage;

    this.pageDataList = this.searchDataList.slice(start, end);
    this.pageNo = pageNo;

    // 페이지 전체 선택 버튼 on/off toogle
    this.checkedPageToogle();
  }

  /**
   * 블럭 이동
   *
   * @param i - 구분 (0 : 이전, 1 : 다음, 2 : 처음, 3 : 끝)
   */
  public goBlock(i): void {

    // 이동할 페이지 번호
    let newPageNo = this.pageNo;

    if (i === 0) {
      if (this.blockNo > 1) {
        this.blockNo--;
        newPageNo = this.pageNo - this.pagePerBlock;
      }

    } else if (i === 1) {

      if (this.blockNo < this.blockCount) {
        this.blockNo++;
        newPageNo = this.pageNo + this.pagePerBlock;
        // 블럭 이동시 페이지 번호가 마지막 페이지를 넘어서는 경우
        if ((this.pageNo + this.pagePerBlock) > this.pageCount) {
          newPageNo = this.pageCount;
        }
      }

    } else if (i === 2) {
      this.blockNo = 1;
      newPageNo = 1;

    } else if (i === 3) {
      this.blockNo = this.blockCount;
      newPageNo = this.pageCount;
    }

    // 페이지 이동
    this.goPage(newPageNo);
  }

  /**
   * 검색
   *
   * @param keyword - 검색어
   */
  public searchData(keyword: string): void {
    // 전부 소문자로 바꾸기 (대소문자 구분하지 않기 위함)
    keyword = keyword.trim().toLowerCase();

    // searchDataList 초기화
    this.searchDataList = [];

    // allDataList에서 검색 List 추출
    for (let item of this.allDataList) {

      // code에서 검색
      if (item.code.toLowerCase().includes(keyword)) {
        this.searchDataList.push(item);

      // name에서 검색
      } else if (item.name.toLowerCase().includes(keyword)) {
        this.searchDataList.push(item);
      }
    }

    // 검색어로 검색
    this.init(this.searchDataList);
  }

  /**
   * 페이지 전체 선택 버튼 on/off toogle
   */
  public checkedPageToogle(): void {

    this.isCheckedPage = true;
    if (!this.pageDataList.length) {
      this.isCheckedPage = false;
      return;
    }
    for (let i = 0; i < this.pageDataList.length; i++) {

      // page의 row가 하나라도 해제된 경우
      if (!this.pageDataList[i].isChecked) {
        this.isCheckedPage = false;
        break;
      }

    }
  }

  /**
   * 전체 선택 버튼 on/off toogle
   */
  public checkedAllToogle(): void {

    if (!this.searchDataList.length) {
      this.isCheckedAll = false;
      return;
    }

    // 전체 검색인 경우
    if (this.searchDataList.length === this.allDataList.length) {

      this.isCheckedAll = false;
      if (this.checkedCount === this.searchDataList.length) {
        this.isCheckedAll = true;
      }

    // 부분 검색인 경우
    } else {

      this.isCheckedAll = true;
      for (let i = 0;  i < this.searchDataList.length; i++) {
        // 선택 안된 항목이 하나라도 있는 경우
        if (!this.searchDataList[i].isChecked) {
          this.isCheckedAll = false;
          break;
        }
      }
    }
  }

  /**
   * 페이지 offset
   *
   * @param pageNo - 페이지 넘버
   */
  public getPageOffSet(PageNo): number {
    return PageNo + ((this.blockNo - 1) * this.pagePerBlock);
  }
}
