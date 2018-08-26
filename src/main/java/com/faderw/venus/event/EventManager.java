package com.faderw.venus.event;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

import com.faderw.venus.config.Config;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * @author FaderW
 */
public class EventManager {
    public enum VenusEvent {
        GLOBAL_STARTED,
        SPIDER_STARTED
    }

    public static final Map<VenusEvent, List<Consumer<Config>>> eventMap = Maps.newHashMap();

    /**
     * 注册事件
     */
    public static void registerEvent(VenusEvent venusEvent, Consumer<Config> consumer) {
        List<Consumer<Config>> consumers = eventMap.get(venusEvent);
        if (null == consumers) {
            consumers = Lists.newArrayList();
        }
        consumers.add(consumer);
        eventMap.put(venusEvent, consumers);
    }

    /**
     * 消费事件
     */
    public static void fireEvent(VenusEvent venusEvent, Config config) {
        Optional.ofNullable(eventMap.get(venusEvent)).ifPresent(consumers -> consumers.forEach(consumer -> consumer.accept(config)));
    }
}