package com.faderw.venus.example;

import com.faderw.venus.Venus;
import com.faderw.venus.config.Config;
import com.faderw.venus.config.UserAgent;
import com.faderw.venus.pipeline.Pipeline;
import com.faderw.venus.request.Parser;
import com.faderw.venus.request.Request;
import com.faderw.venus.response.Response;
import com.faderw.venus.response.Result;
import com.faderw.venus.spider.Spider;
import com.github.kevinsawicki.http.HttpRequest;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by FaderW on 2018/8/24
 */
@Slf4j
public class MeiziExample {

    static class MeiziSpider extends Spider {

        private String storageDir = "/Users/faderw/source/meizi";

        public MeiziSpider(String name) {
            super(name);
            this.startUrls(
                    "http://www.meizitu.com/a/pure.html",
                    "http://www.meizitu.com/a/cute.html",
                    "http://www.meizitu.com/a/sexy.html",
                    "http://www.meizitu.com/a/fuli.html",
                    "http://www.meizitu.com/a/legs.html"
            );
        }

        @Override
        public void onStart(Config config) {
            this.addPipline((Pipeline<List<String>>) (item, request) -> {
               item.forEach(imgUrl -> {
                   log.info("start download : {}", imgUrl);
                   HttpRequest.get(imgUrl)
                           .header("Referer", request.getUrl())
                           .header("User-Agent", UserAgent.CHROME_FOR_MAC)
                           .connectTimeout(20_0000)
                           .readTimeout(20_0000)
                           .receive(new File(storageDir, System.currentTimeMillis() + ".jpg"));
               });
            });

            this.requests.forEach(this::resetRequest);
        }

        private Request resetRequest(Request request) {
            request.contentType("text/html;charset=gb2312");
            request.charset("gb2312");
            return request;
        }


        @Override
        protected <T> Result<T> parse(Response response) {
            Result result = new Result<>();
            Elements elements = response.body().css("#maincontent > div.inWrap > ul > li:nth-child(1) > div > div > a");
            log.info("elements size {}", elements.size());

            List<Request> requests = elements.stream()
                    .map(element -> element.attr("href"))
                    .map(href -> this.makeRequest(href, new PictureParser()))
                    .map(this::resetRequest)
                    .collect(Collectors.toList());

            result.addRequests(requests);

            // 获取下一页url
            Optional<Element> nextElem = response.body().css("#wp_page_numbers > ul > li > a").stream()
                    .filter(element -> "下一页".equals(element.text())).findFirst();

            if (nextElem.isPresent()) {
                String nextPage = "http://www.meizitu.com/a/" + nextElem.get().attr("href");
                Request<String> nextReq = this.makeRequest(nextPage, this::parse);
                result.addRequest(this.resetRequest(nextReq));
            }
            return result;
        }

        static class PictureParser implements Parser<List<String>> {

            @Override
            public Result<List<String>> parse(Response response) {
                Elements elements = response.body().css("#picture > p > img");
                List<String> src = elements.stream().map(element -> element.attr("src")).collect(Collectors.toList());
                return new Result<>(src);
            }
        }
    }

    public static void main(String[] args) {
        MeiziSpider spider = new MeiziSpider("妹子图");
        Venus.me(spider, Config.me().delay(3000).domain("www.meizitu.com")).start();
    }
}
