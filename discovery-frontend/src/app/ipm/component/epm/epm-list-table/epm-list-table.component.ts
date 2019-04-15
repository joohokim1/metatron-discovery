import { Component, OnInit, ViewChild, ViewChildren, ElementRef, Injector, Input, Output, EventEmitter, QueryList } from '@angular/core';
import { AbstractComponent } from '../../../../common/component/abstract.component';
import { Utils } from '../../../common/util/utils';

@Component({
  selector: 'epm-list-table',
  templateUrl: './epm-list-table.component.html',
  styles: []
})
export class EpmListTableComponent extends AbstractComponent implements OnInit {

  // 장비별 성능 데이터
  @Input()
  public epmListData: Array<any>;

  // 선택된 장비
  @Input()
  public selectedEqp: any = {};

  @Input()
  public epmHeaderListData: Array<any>;

  // 현재 선택 장비타입
  @Input()
  public equipment: string;

  // Excel 다운로드 진행 여부
  public isDownloadExcel: boolean = false;

  // 사용자 즐겨찾기 필터 변경 이벤트
  @Output()
  public onSelectEpmList = new EventEmitter<any>();

  // 장비 갯수 변경 이벤트
  @Output()
  public onChangeEpmListCnt = new EventEmitter<any>();

  // Excel 다운로드 eventEmitter
  @Output()
  public onDownloadExcel = new EventEmitter<any>();

  // 갯수 체크박스
  @ViewChild('selectCntBox')
  public selectCntBox: ElementRef;

  // 목록 확대 유무
  public isListExpansion: boolean = false;

  // 생성자
  constructor(
    protected elementRef: ElementRef,
    protected injector: Injector
  ) {
    super(elementRef, injector);
  }

  // 초기화
  ngOnInit() {
  }

  /**
   * 장비 선택 이벤트
   *
   * @param item (선택 장비 데이터)
   * @param i (index)
   */
  public selectEpmList(item, i): void {

    if (this.selectedEqp[i] != null) {
      delete this.selectedEqp[i];
    } else {

      let eqp = {};
      eqp['eqpNm'] = item[1];
      eqp['enbId'] = item[2];

      // cell 일경우
      if (this.equipment == 'cell') {
        eqp['cellNum'] = item[3];
      }
      eqp['dth'] = 'hour';
      this.selectedEqp[i] = eqp;
    }

    this.onSelectEpmList.emit();
  }

  /**
   * 콤마 추가
   *
   * @param value (콤마 추가 데이터)
   */
  public addComma(value: number): string | number {
    return Utils.NumberUtil.addComma(value);
  }

  /**
   * 반올림하여 값 셋팅
   *
   * @param value (변환값)
   */
  public addFixedRound(value: string): string | number {
    return this.addComma((Math.ceil((parseFloat(value) * 1000))) / 1000);
  }

/**
 * 표출 장비 갯수 변경 이벤트
 *
 * @param e (체크박스 데이터)
 */
  public changeShowCnt(e: any): void {
    this.onChangeEpmListCnt.emit(e.target.value);
  }

  /**
   * 확대보기 체크 유무
   */
  public showExpansion(): void {
    this.isListExpansion = !this.isListExpansion;
  }

  /**
   * excel download 실행
   */
  public downloadExcel(): void {
    if (!this.isDownloadExcel && this.epmListData.length != 0) {
      this.isDownloadExcel = true;
      this.onDownloadExcel.emit({chartNum: 'e3'});
    }
  }

  /**
   * excel download 완료 처리
   */
  public finishedExcel(): void {
    this.isDownloadExcel = false;
  }

}
