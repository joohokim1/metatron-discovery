package app.metatron.discovery.domain.ipm.common.util;

import app.metatron.discovery.domain.ipm.common.constant.Const;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.util.StringUtils;

/**
 * html 컨텐츠 유틸
 */
public class HtmlUtil {

    /**
     * 치환 대상
     */
    private static final String[] ESCAPE_TAGS = {
            "innerHTML"
            ,"onfocusout"
            ,"onkeyup"
            ,"onkeypress"
            ,"onload"
            ,"onbounce"
            ,"onmouseenter"
            ,"onmouseout"
            ,"onmouseover"
            ,"onsubmit"
            ,"onmouseend"
            ,"onresizestart"
            ,"onuload"
            ,"onselectstart"
            ,"onreset"
            ,"onmove"
            ,"onstop"
            ,"onrowexit"
            ,"onerrorupdate"
            ,"onfilterchage"
            ,"onlosecapture"
            ,"onmousemove"
            ,"onactive"
            ,"oncut"
            ,"onclick"
            ,"onchange"
            ,"onbeforecut"
            ,"ondbclick"
            ,"ondeactivate"
            ,"ondrag"
            ,"ondragend"
            ,"ondragenter"
            ,"ondragleave"
            ,"ondragover"
            ,"ondragstart"
            ,"ondrop"
            ,"onerror"
            ,"onfinish"
            ,"onfocus"
            ,"vbscript"
            ,"onkeydown"
            ,"onrowsdelete"
            ,"onmouseleave"
            ,"onmousewheel"
            ,"ondataava"
//            ,"lable"
            ,"onafteripudate"
            ,"onmousedown"
            ,"onbeforeactivate"
            ,"onbeforecopy"
            ,"onbeforedeactivate"
            ,"ondatasetchaged"
            ,"cnbeforeprint"
            ,"cnbeforepaste"
            ,"onbeforeeditfocus"
            ,"onbeforeuload"
            ,"onbeforeupdate"
            ,"onpropertychange"
            ,"ondatasetcomplete"
            ,"oncellchange"
            ,"onlayoutcomplete"
            ,"onselectionchange"
            ,"onrowsinserted"
            ,"oncontrolselected"
            ,"onreadystatechange"
            ,"eval"
            ,"charset"
            ,"document"
            ,"string"
//            ,"create"
//            ,"append"
            ,"binding"
            ,"alert"
            ,"msgbox"
            ,"refresh"
            ,"void"
            ,"cookie"
//            ,"Href"
            ,"onpaste"
            ,"onresize"
            ,"onselect"
//            ,"base"
            ,"onblur"
            ,"onstart"
            ,"onfocusin"
            ,"onhelp"
            ,"javascript"
//            ,"expression"
            ,"applet"
//            ,"meta"
//            ,"xml"
            ,"blink"
//            ,"link"
//            ,"style"
            ,"script"
            ,"embed"
            ,"object"
            ,"iframe"
            ,"frame"
            ,"frameset"
            ,"ilayer"
            ,"layer"
            ,"bgsound"
//            ,"title"
            ,"onbefore"
            ,"onmouseup"
            ,"onrowenter"
            ,"oncontextmenu"
    };

    /**
     * 전체 태그 삭제
     * @param html
     * @return
     */
    public static String removeTag(String html) {
        html = html.replaceAll("\\<.*?>", "");
        html = html.replaceAll("&nbsp;", " ");
        html = html.replaceAll("&amp;", " ");
        return html;
    }

    /**
     * 치환 대상 값 삭제
     * @param html
     * @return
     */
    public static String removeTags(String html) {
        // 스크립트 태그 제거
        html = removeScript(html);
        // 대상 전체 제거
        for( String tag : ESCAPE_TAGS ) {
            html = Pattern.compile(tag, Pattern.CASE_INSENSITIVE).matcher(html).replaceAll("");
        }
        return html;
    }

    /**
     * 스크립트 태그만 제거
     * @param html
     * @return
     */
    public static String removeScript(String html) {
        String scriptRegex = "<(/)?[ ]*script[^>]*>";
        Pattern p = Pattern.compile(scriptRegex, Pattern.CASE_INSENSITIVE);

        if(html != null) {
            Matcher m = p.matcher(html);
            StringBuffer str = new StringBuffer(html.length());
            while(m.find()) {
                m.appendReplacement(str, Matcher.quoteReplacement(" "));
            }
            m.appendTail(str);
            html = str.toString();
        }
        return html;
    }

    /**
     * url 의 prefix를 체크하고 보정
     * @param url
     * @return
     */
    public static String prefixUrl(String url) {
        if(StringUtils.isEmpty(url)) {
            return Const.Url.BLANK;
        } else if( url.startsWith(Const.Url.PREFIX_HTTP) || url.startsWith(Const.Url.PREFIX_HTTPS) ) {
            return url.trim();
        } else {
            return Const.Url.PREFIX_HTTP + url.trim();
        }

    }
}
