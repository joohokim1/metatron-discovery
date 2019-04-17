package app.metatron.discovery.domain.ipm.common.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * 네이티브 커멘드 유틸
 */
public class CmdUtil {

    /**
     * 커멘드 실행
     * @param cmd
     * @return
     */
    public static String exec(String cmd) {
        String result = "";
        try {
            Runtime runtime = Runtime.getRuntime();
            Process process = runtime.exec(cmd);

            InputStream is = process.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line;
            while ((line = br.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            // ignore
        }
        return result.trim();
    }
}
