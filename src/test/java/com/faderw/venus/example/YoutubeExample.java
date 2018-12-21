package com.faderw.venus.example;

import com.faderw.venus.Page;
import com.faderw.venus.VenusEngine;
import com.faderw.venus.config.Config;
import com.faderw.venus.spider.Spider;
import com.github.kevinsawicki.http.HttpRequest;

import java.util.Random;

/**
 * @author FaderW
 * 2018/12/18
 */

public class YoutubeExample {

    static class YoutubeSpider extends Spider {

        public YoutubeSpider(String name) {
            super(name);
            this.startUrls("https://www.google.com/");
        }

        @Override
        public void onStart(Config config) {

        }

        @Override
        protected void parse(Page page) {
            System.out.println(page.getRawText());
            page.setSkip(false);
        }
    }


    public static void main(String[] args) {
//        System.setProperty("http.proxyHost", "127.0.0.1");
//        System.setProperty("http.proxyPort", "1080");
//        System.setProperty("https.proxyHost", "127.0.0.1");
//        System.setProperty("https.proxyPort", "1080");
        System.setProperty("socksProxyHost", "127.0.0.1");
        System.setProperty("socksProxyPort", "1080");
        Config config = Config.me().domain("www.google.com").delay(3000);
        Spider spider = new YoutubeSpider("youtube");

        VenusEngine.create(spider, config).start();
//        final String url = "https://www.google.com";
//        HttpRequest httpRequest = HttpRequest.get(url);
//        httpRequest.trustAllHosts();
//        httpRequest.trustAllCerts();
////                .useProxy("127.0.0.1", 1080);
//        System.out.println(httpRequest.code());
//        int random = new Random().nextInt(0);

//        boolean qrcode = true;
//        boolean app = false;
//        System.out.println(qrcode == app);
    }
}
