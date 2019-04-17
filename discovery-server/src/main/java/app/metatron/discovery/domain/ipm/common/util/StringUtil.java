package app.metatron.discovery.domain.ipm.common.util;

import org.springframework.stereotype.Component;

/**
 * 스트링 유틸
 */
@Component
public class StringUtil {

	/**
	 * 유니코드 변환
	 * @param str
	 * @return
	 */
	public static String unicodeConvert(String str) {
		StringBuilder sb = new StringBuilder();
		char ch;
		int len = str.length();
		for (int i = 0; i < len; i++) {
			ch = str.charAt(i);
			if (ch == '\\' && str.charAt(i+1) == 'u') {
				sb.append((char) Integer.parseInt(str.substring(i+2, i+6), 16));
				i+=5;
				continue;
			}
			sb.append(ch);
		}
		return sb.toString();
	}

	

}
