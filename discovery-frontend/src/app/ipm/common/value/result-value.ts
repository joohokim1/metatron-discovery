/**
 * Common result
 *
 *  - 공통 결과 클래스
 */
export class CommonResult {
	code: string;
	message: string;
	data: any;
	contacts: any;
	status: string
}

/**
 * Page
 *
 *  - 페이지 객체
 */
export class Page {

	// 전체 목록 개수
	totalElements: number;
	// 전체 페이지 개수
	totalPages: number;
	// 마지막 페이지 여부
	last: boolean;
	// 현재 페이지의 목록 개수
	numberOfElements: number;
	// 첫 페이지 여부
	first: boolean;
	// Sort 정보
	sort: Sort;
	// 한 페이지당 최대목록 개수
	size: number;
	// 현재 페이지 번호
	number: number;
	// Data 목록
	content: Object;

	createNonPagingValueObject() {
		this.number = 0;
		this.sort = new Sort();
		this.sort.property = 'createdDate,desc';
		this.sort.direction = 'desc';
		this.size = 2147483647;
		return this;
	}

}

/**
 * Sort
 *
 *  - 소팅 클래스
 */
export class Sort {
	// 방향 (ASC or DESC)
	direction: string;
	// Order by
	property: string;
	// ignore 여부
	ignoreCase: boolean;
	// nullHandling
	nullHandling: string;
	// ASC 여부
	ascending: boolean;
}
