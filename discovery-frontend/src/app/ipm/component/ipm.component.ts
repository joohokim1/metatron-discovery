import { Component, OnInit,	HostBinding, ElementRef, Injector } from '@angular/core';
import { AbstractComponent } from '../../common/component/abstract.component';
import { IpmService } from '../service/ipm.service';

@Component({
	selector: 'ipm',
	templateUrl: './ipm.component.html'
})
export class IpmComponent extends AbstractComponent implements OnInit {

	@HostBinding('id')
	id = 'ipm_wrap';

  @HostBinding('class')
  public url: string = 'main';

	public title: string = '';
  public subTitle: string = '';

  // 생성자
  constructor(
		protected elementRef: ElementRef,
		protected injector: Injector,
    private ipmService: IpmService
	) {
    super(elementRef, injector);
	}

	ngOnInit() {

    /*
    if (!this.gnbService.permission) {
      this.gnbService.permission = this.layoutService.getPagePermissionByPageUrl('/view/analysis-app/my-app');
    }
    */

    this.style();

    // router-outlet 컴퍼넌트에서 data를 받아 셋팅
	  this.ipmService.getHeaderEvent(data => {
			this.url = data.url;
			this.title = data.title;
			this.subTitle = data.subTitle;
    });
  }

  /**
   * style 보정
   */
  public style(): void {

/*
    if (matchMedia('(max-width: 1199px)').matches){
      let left = $(window).scrollLeft();
      $('#ipm_wrap .r_flexible').css('left', 860 - left);
      $('#ipm_wrap .r_flexible.increas').css('left', 565 - left);
      $('#ipm_wrap .btn_done').css('left', 876 - left);
      $('#ipm_wrap .right_container.increas .btn_done').css('left', 581 - left);

      $('#ipm_wrap .l_flexible').css('left', 278 - left);
      $('#ipm_wrap .left_wrap_bg').css('left', 58 - left);
      $('#ipm_wrap .right_container_bg').css('left', 876 - left);
      $('#ipm_wrap .right_container_bg.increas').css('left', 581 - left);
    } else {
      $('#ipm_wrap .btn_done').css('left', 'inherit').css('right', '324px;');
      $('#ipm_wrap .l_flexible').css('left', 278 + $(window).scrollLeft());
      $('#ipm_wrap .left_wrap_bg').css('left', 'inherit').css('left', '58px');
      $('#ipm_wrap .r_flexible').css('left', 'inherit').css('right', '324px;');
      $('#ipm_wrap .right_container_bg').css('left', 'inherit').css('right', '0');
    };
  */
  }
}
