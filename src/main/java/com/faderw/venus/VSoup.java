package com.faderw.venus;

import org.jsoup.Jsoup;
import org.jsoup.select.Elements;

import us.codecraft.xsoup.XElements;
import us.codecraft.xsoup.Xsoup;

/**
 * @author FaderW
 */
public final class VSoup {

    private String html;

    public static VSoup create(String html) {
        VSoup VSoup = new VSoup();
        VSoup.html = html;
        return VSoup;

    }

    public  Elements css(String css) {
        return Jsoup.parse(this.html).select(css);
    }

    public XElements xpath(String xpath) {
        return Xsoup.compile(xpath).evaluate(Jsoup.parse(this.html));
    }

}