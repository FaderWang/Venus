package com.faderw.venus;

import java.util.List;
import java.util.function.Consumer;

import com.faderw.venus.config.Config;
import com.faderw.venus.event.EventManager;
import com.faderw.venus.event.EventManager.VenusEvent;
import com.faderw.venus.spider.Spider;
import com.google.common.collect.Lists;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor
public class Venus {

    List<Spider> spiders = Lists.newArrayList();
    Config config;

    public static Venus me(Spider spider) {
        return me(spider, Config.me());
    }

    public static Venus me(Spider spider, Config config) {
        Venus venus = new Venus();
        venus.spiders.add(spider);
        venus.config = config;
        return venus;
    }

    public void start() {
        new VenusEngine(this).start();
    }

    public Venus onStart(Consumer<Config> consumer) {
        EventManager.registerEvent(VenusEvent.GLOBAL_STARTED, consumer);
        return this;
    }
}