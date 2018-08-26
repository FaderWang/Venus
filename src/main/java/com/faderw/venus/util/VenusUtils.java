package com.faderw.venus.util;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

/**
 * 
 */
public class VenusUtils {
    
    public static void sleep(long time) {
        try {
			TimeUnit.MILLISECONDS.sleep(time);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    }

    public static boolean isEmpty(Collection<?> collection) {
        return collection == null || collection.size() == 0;
    }
}