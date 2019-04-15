import { Component, OnInit, ElementRef, Injector, Input } from '@angular/core';
import { AbstractComponent } from '../../../../../common/component/abstract.component';
import { RightKindInterface } from '../right-kind.component';
import * as _ from 'lodash';
import { HierarchyService } from '../../../service/right-kind/hierarchy/hierarchy.service';
import { CommonConstant } from '../../../constant/common-constant';

@Component({
  selector: 'mtso',
  templateUrl: './mtso.component.html',
  styles: []
})
export class MtsoComponent extends AbstractComponent implements OnInit, RightKindInterface {

  // 메뉴 상세 데이터
  @Input()
  public menuDetailData: any;

  // 전체 데이터 목록
  public allDataList: Array<any> = [];

  // 검색된 데이터 목록
  public searchDataList: Array<any> = [];

  // 필터 수정시 원본 데이터
  public modifyDataList: Array<any> = [];

  // 국사유형 타입 목록
  public mtsoTypeList: Array<any> = [];

  // 선택된 타입별 데이터 목록
  public mtsoSelectList: Array<any> = [];

  // 국사유형 타입 코드
  public mtsoTypeCd: string;

  // 선택된 데이터 count
  public checkedCount: number = 0;

  // 전체선택 구분
  public isCheckedAll: boolean = false;

	// 생성자
	constructor(
		protected elementRef: ElementRef,
    protected injector: Injector,
    private hierarchyService: HierarchyService
	) {
		super(elementRef, injector);
	}

  ngOnInit() {

    // 수정시 초기 데이터 세팅
    if (this.menuDetailData.fltrVal) {
      this.modifyDataList = _.cloneDeep(this.menuDetailData.fltrVal);
      this.checkedCount = this.menuDetailData.fltrVal.length;
    }

    // select 초기 세팅
    let values: Array<any> = this.menuDetailData.fltrDtlList[0].values || [];
    for (let item of values) {
      this.mtsoTypeList.push({
        code: item.code,
        name: item.name
      });
    }
    // select 최초 '전체' 선택
    this.mtsoTypeCd = '';
    this.changeType();

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
   * Select Box Change 이벤트
   */
  public changeType(): void {

    // 초기 request param 셋팅
    let requestData = {
      network: this.menuDetailData.classification.fltrVal[0].code[0],
      equipment: this.menuDetailData.classification.fltrVal[0].code[1],
      vendor: this.menuDetailData.classification.fltrVal[0].code[2],
      druidNm: this.menuDetailData.druidNm,
      mtsoTypCd: this.mtsoTypeCd
    };

    // 국사코드 조회
    this.hierarchyService.getEpmMtsoList(requestData)
    .then(result => {
      if (result.code === CommonConstant.CODE.RESULT_CODE.SUCCESS) {

        // 전체 검색일 경우
        if (!this.mtsoTypeCd && !this.allDataList.length) {

          // 검색 초기화용 목록
          this.mtsoSelectList = result.data;

          this.allDataList = result.data;
          this.searchDataList = this.allDataList;

        } else {
          // 검색 초기화용 목록
          this.mtsoSelectList = [];
          this.searchDataList = [];

          for (let item1 of this.allDataList) {
            for (let item2 of result.data) {
              if (item1.code === item2.code) {
                this.mtsoSelectList.push(item1);
                this.searchDataList.push(item1);
              }
            }
          }

        }

        // 수정시 기존 선택 목록 체크
        if (this.modifyDataList) {
          // this.checkedCount = this.modifyDataList.length;

          for (let item1 of this.modifyDataList) {
            for (let item2 of this.searchDataList) {
              if (item1.code === item2.code) {
                item2.isChecked = true;
              }
            }
          }
        }

        // 전체 선택 버튼 on/off toogle
        this.checkedAllToogle();

      }
    });

  }

    /**
   * 전체 선택
   */
  public checkedAll(): void {

    // 전체 row 해제
    if (this.isCheckedAll) {
      this.isCheckedAll = false;

      for (let item of this.searchDataList) {
        if (item.isChecked) {
          item.isChecked = false;
          this.checkedCount--;

          // 헤제된 row는 수정목록에서 제거
          for (let i = this.modifyDataList.length - 1; i >= 0; i--) {
            if (this.modifyDataList[i].code === item.code) {
              this.modifyDataList.splice(i, 1);
              break;
            }
          }
        }
      }

    // 전체 row 선택
    } else {
      this.isCheckedAll = true;

      for (let item of this.searchDataList) {
        if (!item.isChecked) {
          item.isChecked = true;
          this.checkedCount++;
        }
      }
    }

  }

  /**
   * 단일 row 선택
   *
   * @param item - row의 data
   */
  public checkedRow(item): void {

    // 단일 row 선택 / 해제
    if (item.isChecked) {
      item.isChecked = false;
      this.checkedCount--;

      // 헤제된 row는 수정목록에서 제거
      for (let i = this.modifyDataList.length - 1; i >= 0; i--) {
        if (this.modifyDataList[i].code === item.code) {
          this.modifyDataList.splice(i, 1);
          break;
        }
      }

    } else {
      item.isChecked = true;
      this.checkedCount++;
    }

    // 전체 선택 버튼 on/off toogle
    this.checkedAllToogle();
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

    if (keyword) {
      // allDataList에서 검색 List 추출
      for (let item of this.mtsoSelectList) {

        // code에서 검색
        if (item.code.toLowerCase().includes(keyword)) {
          this.searchDataList.push(item);

        // name에서 검색
        } else if (item.name.toLowerCase().includes(keyword)) {
          this.searchDataList.push(item);
        }

      }

    } else {
      // keyword가 공백일 경우 초기 상태로 리셋
      for (let item of this.mtsoSelectList) {
        this.searchDataList.push(item);
      }
    }

    // 전체 선택 버튼 on/off toogle
    this.checkedAllToogle();
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
   * 3자리 이상 숫자 콤마 추가
   *
   * @param num (숫자)
   */
  public comma(num): string {
    let regexp = /\B(?=(\d{3})+(?!\d))/g;
    return num.toString().replace(regexp, ',');
  }

}
