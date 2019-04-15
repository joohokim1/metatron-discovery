import { Component, OnInit, ElementRef, Injector, Input, Output, EventEmitter } from '@angular/core';
import { AbstractComponent } from '../../../../common/component/abstract.component';
import { BmrkService } from '../../service/bmrk/bmrk.service';
import { CommonConstant } from '../../constant/common-constant';
import { Alert } from '../../../../common/util/alert.util';

declare const $: any;

@Component({
  selector: 'bmrk',
  templateUrl: './bmrk.component.html',
  styles: []
})
export class BmrkComponent extends AbstractComponent implements OnInit {

  // 사용자 즐겨찾기 데이터
  @Input()
  public bmrkData: Array<any> = [];

  // 선택한 즐겨찾기 데이터
  public bmrkUid: String;

  // 사용자 즐겨찾기 필터 변경 이벤트
  @Output()
  public onChangeFilter = new EventEmitter<any>();

  // 사용자 즐겨찾기 삭제 이벤트
  @Output()
  public onBmrkRemoveCheck = new EventEmitter<number>();

  protected jQuery = $;

  // 생성자
  constructor(
    protected elementRef: ElementRef,
    protected injector: Injector,
    private bmrkService: BmrkService
  ) {
    super(elementRef, injector);
  }

  ngOnInit() {
    $(document).on('mouseenter', '.resize_slider', function(e) {
      $(this).prev().addClass('dsp_none');
    });
    $(document).on('mouseleave', '.resize_slider', function() {
      $(this).prev().removeClass('dsp_none');
    });

    this.jQuery('div.favorite_box').resizable({
      alsoResize: 'div.favorite_word_box',
      classes: {
        'ui-resizable-handle': 'resize_slider'
      },
      distance: 0,
      handles: 's',
      minHeight: 105,
      maxHeight: 250
    });

    this.jQuery('div.filter_set_box').resizable({
      classes: {
        'ui-resizable-handle': 'resize_slider'
      },
      distance: 0,
      handles: 's',
      minHeight: 135,
      maxHeight: 335
    });
  }

  /**
   * 사용자 즐겨찾기 필터 표출 이벤트 바인딩
   *
   * @param item (json 즐겨찾기 필터 데이터)
   */
  public showBmrkFilter(item: any): void {
    this.onChangeFilter.emit(item);
  }

  /**
   * 즐겨찾기 삭제 이벤트 바인딩
   *
   * @param i (선택 즐겨찾기 인덱스)
   */
  public onBmrkRemove(i: number): void {
    this.onBmrkRemoveCheck.emit(i);
  }

  /**
   * 사용자 즐겨찾기 삭제
   */
  public deleteBmrk(): void {

    this.bmrkService.deleteUserBmrkDtl(this.bmrkUid)
    .then(result => {

      if (result.code === CommonConstant.CODE.RESULT_CODE.SUCCESS) {

        for (let i = 0; i < this.bmrkData.length; i++) {
          if (this.bmrkUid === this.bmrkData[i].bmrkUid) {
            this.onBmrkRemove(i);
            Alert.success(this.translateService.instant('COMMON.MESSAGE.DELETE', '삭제되었습니다.'));
            break;
          }
        }

      } else {
        Alert.error(this.translateService.instant('COMMON.MESSAGE.ERROR', '오류가 발생하였습니다.'));
      }

      // del 팝업 종료
      this.bmrkUid = '';

    });

  }

}
