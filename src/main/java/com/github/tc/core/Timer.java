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
package com.github.tc.core;

import com.github.tc.utils.Utils;
import com.github.tc.views.Constants;
import com.github.tc.views.ParamDTO;

import javax.swing.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author gao_xianglong@sina.com
 * @version 0.1-SNAPSHOT
 * @date created in 2022/5/25 23:45
 */
public class Timer {
    private ParamDTO paramDTO;

    public Timer(ParamDTO paramDTO) {
        this.paramDTO = paramDTO;
        init();
    }

    private void init() {
        try {
            create();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void start() throws Throwable {
        if (paramDTO.isCount()) {
            var r = statistics(paramDTO.getDate());
            System.out.println(Objects.isNull(r) ? "No data found" : r);
            return;
        }
        remind();
        var begin = System.currentTimeMillis();
        System.in.read();
        var tc = (System.currentTimeMillis() - begin) / 1000L;
        var tcs = Utils.timeFormat(tc);
        calculation(tc);
        System.out.printf("End of task\nStudy time:%s", tcs);
    }

    private void calculation(long tc) throws Throwable {
        var properties = new Properties();
        var path = Constants.FILE_PATH;
        var key = paramDTO.getKey();
        properties.load(new FileReader(Constants.FILE_PATH));
        var value = properties.getProperty(key);
        if (Objects.isNull(value) || value.isBlank()) {
            value = String.format("%s,%s&", paramDTO.getTaskName(), tc);
        } else {
            value = String.format("%s%s,%s&", value, paramDTO.getTaskName(), tc);
        }
        properties.put(key, value);
        properties.store(new FileOutputStream(Constants.FILE_PATH), null);
    }

    private String statistics(String key) throws Throwable {
        System.out.printf(">>>total study time<<<\ndate:%s\n", key);
        var count = new ConcurrentHashMap<String, Long>();
        var properties = new Properties();
        properties.load(new FileReader(Constants.FILE_PATH));
        if (!properties.containsKey(key)) {
            return null;
        }
        var total = 0;
        var value = properties.getProperty(key);
        value = value.substring(0, value.length() - 1);
        for (var t1 : value.split("\\&")) {
            var t2 = t1.split("\\,");
            var k = t2[0];
            var v = t2[1];
            count.put(k, count.containsKey(k) ? count.get(k) + Long.parseLong(v)
                    : Long.parseLong(v));
        }
        for (Map.Entry<String, Long> entry : count.entrySet()) {
            total += entry.getValue();
        }
        var strBuilder = new StringBuffer();
        var i = 0;
        for (Map.Entry<String, Long> entry : count.entrySet()) {
            var k = entry.getKey();
            var v = entry.getValue();
            strBuilder.append(String.format("%s.category:[%s], proportion:%s, study time:%s\n",
                    ++i, k, String.format("%.2f", (double) v / total * 100)
                            + "%", Utils.timeFormat(v)));
        }
        strBuilder.append(String.format("total:%s", Utils.timeFormat(total)));
        return strBuilder.toString();
    }

    private void create() throws IOException {
        var file = new File(Constants.PATH);
        if (!file.exists()) {
            file.mkdirs();
        }
        file = new File(Constants.FILE_PATH);
        if (!file.exists()) {
            file.createNewFile();
        }
    }

    public void remind() {
        new java.util.Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                JOptionPane.showMessageDialog(null,
                        String.format("[%s] task time is up", paramDTO.getTaskName()));
            }
        }, Utils.toMillisecond(paramDTO.getTimeConsuming(), paramDTO.getTimeUnit()));
    }
}
