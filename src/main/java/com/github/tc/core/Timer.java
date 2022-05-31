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

import com.github.tc.utils.Constants;
import com.github.tc.utils.Utils;
import com.github.tc.views.ParamDTO;

import javax.swing.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author gao_xianglong@sina.com
 * @version 0.1-SNAPSHOT
 * @date created in 2022/5/25 23:45
 */
public class Timer {
    private long begin;
    private String fp = Constants.DEFAULT_FILE_PATH;
    private ParamDTO paramDTO;
    private String cd = Utils.getDate();

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
            var days = paramDTO.getDays();
            do {
                statistics(paramDTO.getDate());
                if (paramDTO.getDays() > 1) {
                    paramDTO.setDate(Utils.getYesterdayDate(
                            new SimpleDateFormat("yyyy-MM-dd").
                                    parse(paramDTO.getDate())));
                }
            } while (--days > 0);
            return;
        }
        System.out.printf("正在进行你的[%s]学习计划...\n", paramDTO.getTaskName());
        remind();
        begin = System.currentTimeMillis();
        record(begin);
        System.in.read();
        var tc = (System.currentTimeMillis() - begin) / 1000L;
        calculation(tc);
        System.out.println("bye~");
    }

    private void monitor() throws Throwable {
        // 如果当前日期大于启动日期就落盘前一天数据
        if (Utils.parse(Utils.getDate()) > Utils.parse(cd)) {
            var tc = (Utils.getLastTime(cd) - begin) / 1000L;
            // 落盘前一天数据
            calculation(tc);
            begin = System.currentTimeMillis();
            fp = String.format("%s/time-%s.properties", Constants.PATH, Utils.getDate());
            create(fp);
        }
    }

    private void record(long begin) {
        new Thread(() -> {
            for (; ; ) {
                try {
                    TimeUnit.SECONDS.sleep(1);
                    System.out.printf("已学习%s",
                            Utils.timeFormat((System.currentTimeMillis() - begin) / 1000L));
                    System.out.print("\r");
                    monitor();
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void calculation(long tc) throws Throwable {
        var properties = new Properties();
        var key = paramDTO.getTaskName();
        properties.load(new BufferedReader(new FileReader(fp)));
        if (properties.containsKey(key)) {
            properties.put(key, String.valueOf(Long.parseLong(properties.getProperty(key)) + tc));
        } else {
            properties.put(key, String.valueOf(tc));
        }
        properties.store(new BufferedWriter(new FileWriter(fp)), null);
    }

    private void statistics(String key) throws Throwable {
        var p = String.format("%s/time-%s.properties", Constants.PATH, key);
        if (!new File(p).exists()) {
            return;
        }
        // 前一天数据
        var before = getYesterdayData();
        var properties = new Properties();
        properties.load(new FileReader(p));
        System.out.printf("\n>>> %s学习时常统计 <<<\n", key);
        var t = properties.values().stream().mapToLong(x -> Long.parseLong(String.valueOf(x))).sum();
        System.out.println("(时间占比) | (学习时常) | (环比增长率) | (任务名称)");
        for (Map.Entry<?, ?> entry : properties.entrySet()) {
            var k = entry.getKey();
            var v = Long.parseLong(String.valueOf(entry.getValue()));
            System.out.printf("%s", Utils.getProgressBar((double) v / t * 20));
            System.out.printf("%-7.7s", String.format("%.2f", (double) v / t * 100) + "%");
            System.out.printf(" | %s", Utils.timeFormat(v));
            if (before.containsKey(k)) {
                var bv = Long.parseLong(String.valueOf(before.get(k)));
                var r = (double) (v - bv) / bv * 100;
                System.out.printf(" | %s%-7.7s", r >= 0 ? "↑ " : "↓ ", String.format("%.2f", Math.abs(r)) + "%");
            } else {
                System.out.printf(" | %-9.7s", "0.00" + "%");
            }
            System.out.printf(" | %s\n", k);
        }
        System.out.printf("总学习时常:%s\n", Utils.timeFormat(t));
    }

    private void create() throws IOException {
        create(fp);
    }

    private void create(String path) throws IOException {
        var file = new File(Constants.PATH);
        if (!file.exists()) {
            file.mkdirs();
        }
        file = new File(path);
        if (!file.exists()) {
            file.createNewFile();
        }
    }

    /**
     * 获取前一天的数据
     *
     * @return
     */
    private Properties getYesterdayData() throws Throwable {
        var properties = new Properties();
        Date date = Objects.isNull(paramDTO.getDate()) ? new Date() :
                new SimpleDateFormat("yyyy-MM-dd").parse(paramDTO.getDate());
        var yd = Utils.getYesterdayDate(date);
        var p = String.format("%s/time-%s.properties", Constants.PATH, yd);
        if (new File(p).exists()) {
            properties.load(new BufferedReader(new FileReader(p)));
        }
        return properties;
    }

    public void remind() {
        new java.util.Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                JOptionPane.showMessageDialog(null,
                        String.format("[%s] 任务学习时间已到", paramDTO.getTaskName()));
            }
        }, Utils.toMillisecond(paramDTO.getTimeConsuming(), paramDTO.getTimeUnit()));
    }
}
