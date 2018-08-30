package com.faderw.venus;

import java.util.function.Consumer;

import com.faderw.venus.config.Config;
import com.faderw.venus.event.EventManager;
import com.faderw.venus.event.EventManager.VenusEvent;
import com.faderw.venus.spider.Spider;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author FaderW
 */
@Data
@Slf4j
@NoArgsConstructor
public class Venus {

    private Spider spider;
    private Config config;

    public static Venus me(Spider spider) {
        return me(spider, Config.me());
    }

    public static Venus me(Spider spider, Config config) {
        Venus venus = new Venus();
        venus.spider = spider;
        venus.config = config;
        return venus;
    }

    public void start() {
        new VenusEngine(this).start();
    }

//    public Venus onStart(Consumer<Config> consumer) {
//        EventManager.registerEvent(VenusEvent.GLOBAL_STARTED, consumer);
//        return this;
//    }
}