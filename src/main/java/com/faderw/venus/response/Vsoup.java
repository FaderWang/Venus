package com.faderw.venus.response;

import org.jsoup.Jsoup;
import org.jsoup.select.Elements;

import us.codecraft.xsoup.XElements;
import us.codecraft.xsoup.Xsoup;

/**
 * @author FaderW
 */
public final class Vsoup {

    private String html;

    public static Vsoup create(String html) {
        Vsoup vsoup = new Vsoup();
        vsoup.html = html;
        return vsoup;

    }

    public  Elements css(String css) {
        return Jsoup.parse(this.html).select(css);
    }

    public XElements xpath(String xpath) {
        return Xsoup.compile(xpath).evaluate(Jsoup.parse(this.html));
    }

}