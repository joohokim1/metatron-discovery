import * as _ from 'lodash';

export namespace Utils {

	export class Generate {
		/**
		 * UUID 생성
		 */
		public static UUID() {
			let d = new Date().getTime();
			return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function (c) {
				let r = (d + Math.random() * 16) % 16 | 0;
				d = Math.floor(d / 16);
				return (c == 'x' ? r : (r & 0x3 | 0x8)).toString(16);
			});
		}
	}

	export class ArrayUtil {

		public static insert(array: any[], index: number, newItem: object) {
			return [ ...array.slice(0, index), newItem, ...array.slice(index) ];
		}

		public static remove(array: any[], index: number) {
			const remove = (element, eIndex) => eIndex != index;
			return array.filter(remove);
		}

		public static removes(array: any[], indexs: number[]) {

			let removeArrayItemCount: number = 0;

			indexs
				.forEach(index => {
					if (_.isUndefined(array[ index ]) === false) {
						array = Utils.ArrayUtil.remove(array, index - removeArrayItemCount);
						removeArrayItemCount++;
					}
				});

			return array;
		}
	}

	export class DateUtil {

		/**
		 * TimeStamp 생성
		 *
		 * @returns {string}
		 */
		public static createTimeStamp(): string {

			const date: Date = new Date();
			return date.getFullYear().toString() +
				(date.getMonth() + 1).toString() +
				date.getDate().toString() +
				date.getHours().toString() +
				date.getMinutes().toString() +
				date.getMilliseconds().toString();
		}

	}

	/**
	 * Number Util
	 */
	export class NumberUtil {

		/**
		 * 콤마 추가
		 *
		 * @param value
		 */
		public static addComma(value: number): string | number {
			if (value < 1000) {
				return value;
			} else {

				const reg = /(^[+-]?\d+)(\d{3})/;
				let n = (value + '');

				while (reg.test(n)) {
					n = n.replace(reg, '$1' + ',' + '$2');
				}

				return n;
			}
		}

	}

	/**
	 * EscapeUtil
	 */
	export class EscapeUtil {

		/**
		 * DT 과제, 커뮤니케이션에서 사용하기 위함 함수
		 *  - \n - <br>
		 *  - \t - &nbsp;&nbsp;&nbsp;&nbsp;
		 *  - space - &nbsp;
		 *  로 치환
		 */
		public static lineBreakOrTabOrSpaceCharacter(text: string) {
			return text
				? text
					.replace(/\n/gi, '<br>')
					.replace(/  /gi, '&nbsp;&nbsp;')
					.replace(/\t/gi, '&nbsp;&nbsp;&nbsp;&nbsp;')
				: '';
		}

		public static urlToLink(text: string) {
			var urls = /(\b(https?|ftp):\/\/[A-Z0-9+&@#\/%?=~_|!:,.;-]*[-A-Z0-9+&@#\/%=~_|])/gim;

			if(text.match(urls)) {
				text = text.replace(urls, "<a href=\"$1\" class=\"link-content\" target=\"_blank\">$1</a>");
			}

			return text
		}

	}

}
