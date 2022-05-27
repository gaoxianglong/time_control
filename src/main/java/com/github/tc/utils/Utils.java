/*
 * Copyright 2019-2119 gao_xianglong@sina.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.tc.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author gao_xianglong@sina.com
 * @version 0.1-SNAPSHOT
 * @date created in 2022/5/25 23:52
 */
public class Utils {
    /**
     * 将指定时间转换为毫秒
     *
     * @param time
     * @param unit
     * @return
     */
    public static long toMillisecond(int time, String unit) {
        if (unit.equalsIgnoreCase("h")) {
            return time * 1000L * 60 * 60;
        } else {
            return time * 1000L * 60;
        }
    }

    /**
     * 将耗时(单位s)换为指定的时间格式 hh:mm:ss
     *
     * @param time
     * @return
     */
    public static String timeFormat(long time) {
        var result = new StringBuilder();
        var hour = time / Constants.HOUR;
        time -= hour * Constants.HOUR;
        var min = time / Constants.MINUTE;
        time -= min * Constants.MINUTE;
        result.append(String.format("%02d", hour)).append(":").
                append(String.format("%02d", min)).append(":").
                append(String.format("%02d", time));
        return result.toString();
    }

    /**
     * 获取当前日期
     *
     * @return
     */
    public static String getDate() {
        return new SimpleDateFormat("yyyy-MM-dd").format(new Date());
    }

    /**
     * 获取指定时间的前一天
     *
     * @param date
     * @return
     */
    public static String getYesterdayDate(Date date) {
        var calendar = Calendar.getInstance();
        calendar.setTime(date);
        //计算前一个月的日期
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        return new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime());
    }

    public static String getProgressBar(double value) {
        var percent = (int) value;
        percent = (0 == percent) ? 1 : percent;
        var str = new StringBuilder();
        str.append("[");
        var length = 20 - percent;
        for (int i = 0; i < percent; i++) {
            str.append(">");
        }
        // 空位补空格占位
        for (int j = 0; j < length; j++) {
            str.append(" ");
        }
        str.append("]");
        return str.toString();
    }
}
