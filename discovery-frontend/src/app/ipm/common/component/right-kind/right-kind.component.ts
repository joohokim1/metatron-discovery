import { Component, OnInit, ElementRef, Input, Output, EventEmitter, Injector, ViewChild, Inject, Renderer2 } from '@angular/core';
import { DOCUMENT } from '@angular/common';
import { AbstractComponent } from '../../../../common/component/abstract.component';
import { Alert } from '../../../../common/util/alert.util';
import { DruidrangeComponent } from './druidrange/druidrange.component';

@Component({
  selector: 'right-kind',
  templateUrl: './right-kind.component.html',
  styles: []
})
export class RightKindComponent extends AbstractComponent implements OnInit {

  // 메뉴 상세 데이터
  @Input()
  public menuDetailData: any;

  // 2depth 창에 표현할 선택된 마지막 노드 정보
  public selectedLastNode: any;

  // 2depth 창 팝업 유무
  public isTwoDepth: string;

  @Input()
  public isRightFlexible: boolean;

  // right-kind 데이터 전송 emitter
  @Output()
  public onSubmitRightKind = new EventEmitter<any>();

  // 필터 컴포넌트
  @ViewChild('rightChild')
  public rightChild: any;

  // 2depth 창 컴포넌트
  @ViewChild('twoDepthChild')
  public twoDepthChild: any;

  @ViewChild(DruidrangeComponent)
  public druidrangeComponent: DruidrangeComponent;

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

  /**
   * 필터 데이터 전송
   */
  public submitRightKind(): void {

    let dtData: any;

    // 2depth창 필터인 경우
    if (this.selectedLastNode) {
      if (this.selectedLastNode.isShow) {
        dtData = this.twoDepthChild.getFilterData();

        // two-depth 화면 종료
        let twoDepth = this.document.querySelector('.hidden_panel_wrap.dsp_block .panel_close') as HTMLElement;
        if (twoDepth) {
          twoDepth.click();
        }

      } else {
        dtData = this.rightChild.getFilterData();
      }
    } else {
      dtData = this.rightChild.getFilterData();
    }

    if (!dtData || !dtData.dtNm.length) {
      Alert.error("필터 입력값이 없습니다.");
      return;
    }

    // filter data setting
    let filterData = {
      fltrUid: this.menuDetailData.fltrUid,
      druidNm: this.menuDetailData.druidNm,
      scrnNm: this.menuDetailData.scrnNm,
      scrnClNm: this.menuDetailData.scrnClNm,
      type: this.menuDetailData.type,
      useYn: this.menuDetailData.useYn,
      fltrVal: dtData.fltrVal,
      dtNm: dtData.dtNm,
    }

    if (this.menuDetailData.fltrDelYn && this.menuDetailData.fltrWoYn) {
      filterData['fltrDelYn'] = this.menuDetailData.fltrDelYn
      filterData['fltrWoYn'] = this.menuDetailData.fltrWoYn
    }

    // 전송 이벤트 실행
    this.onSubmitRightKind.emit(filterData);
  }

  /**
   * 필터 데이터 삭제
   */
  public closeRightKind(): void {

    // right-kind 종료
    this.menuDetailData = null;

    // two-depth 화면 종료
    let twoDepth = this.document.querySelector('.hidden_panel_wrap.dsp_block .panel_close') as HTMLElement;
    if (twoDepth) {
      twoDepth.click();
    }
  }

  /**
   * 2Depth 창 show / hide
   *
   * @param e (전달받은 데이터 object)
   */
  public showTwoDepth(e): void {

    // two-depth 화면 show / hide
    this.selectedLastNode = e;

    let scrnClNm = this.menuDetailData.scrnClNm;

    // 팝업 없는 2단 UI
    if (scrnClNm.endsWith('enb') || scrnClNm.endsWith('mtso')) {
      this.isTwoDepth = 'enb';

    // 팝업 있는 2단 UI
    } else if (scrnClNm.endsWith('cell')) {
      this.isTwoDepth = 'cell';
    }

    // 우측 열기 버튼 DOM
    let rFlex = this.document.querySelector('.r_flexible');
    // two-depth DOM
    let twoDepth = this.document.querySelector('.hidden_panel_wrap');

    // two-depth 열기
    if (e.isShow) {

      // 우측 열기 버튼 open 속성 추가
      this.renderer.addClass(rFlex, 'open');
      this.renderer.addClass(rFlex, 'panel');

      // 우측 DOM이 열려있는 상태일 경우
      if (rFlex.classList.contains('increas')) {
        // two-depth 화면 open 속성 추가
        this.renderer.addClass(twoDepth, 'open');
      }

    // two-depth 닫기
    } else {
      // two-depth 화면 종료
      let twoDepth = this.document.querySelector('.hidden_panel_wrap.dsp_block .panel_close') as HTMLElement;
      if (twoDepth) {
        twoDepth.click();
      }
    }
  }

  /**
   * 우측영역 변경 체크 이벤트
   */
  public checkRightFlexible(): void {
    if (this.menuDetailData && this.menuDetailData.scrnClNm == 'druidrange') {
      this.druidrangeComponent.chartResize();
    }
  }
}

/**
 * right-kind 하위 컴포넌트의 공통 인터페이스
 */
export interface RightKindInterface {
  /**
   * 컴포넌트로부터 필터 조건 데이터를 가져오는 함수
   */
  getFilterData(): any;
}
