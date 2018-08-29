package com.faderw.venus.response;

import org.jsoup.Jsoup;
import org.jsoup.select.Elements;

import us.codecraft.xsoup.XElements;
import us.codecraft.xsoup.Xsoup;

/**
 * @author FaderW
 */
public class Body {

    public String charset;
    public String bodyString;

    public Body(String bodyString, String charset) {
        this.bodyString = bodyString;
        this.charset = charset;
    }



    public Elements css(String css) {
        return Jsoup.parse(this.bodyString).select(css);
    }

    public XElements xpath(String xpath) {
        return Xsoup.compile(xpath).evaluate(Jsoup.parse(this.bodyString));
    }

}