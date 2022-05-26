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
import java.util.*;
import java.util.concurrent.TimeUnit;

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
            statistics(paramDTO.getDate());
            return;
        }
        System.out.printf("正在进行你的[%s]学习计划...\n", paramDTO.getTaskName());
        remind();
        var begin = System.currentTimeMillis();
        record(begin);
        System.in.read();
        var tc = (System.currentTimeMillis() - begin) / 1000L;
        calculation(tc);
        System.out.println("bye~");
    }

    private void record(long begin) {
        new Thread(() -> {
            for (; ; ) {
                try {
                    TimeUnit.SECONDS.sleep(1);
                    System.out.printf("已学习%s",
                            Utils.timeFormat((System.currentTimeMillis() - begin) / 1000L));
                    System.out.print("\r");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void calculation(long tc) throws Throwable {
        var properties = new Properties();
        var fp = Constants.DEFAULT_FILE_PATH;
        var key = paramDTO.getTaskName();
        properties.load(new BufferedReader(new FileReader(fp)));
        if (properties.containsKey(key)) {
            properties.put(key, String.valueOf(Long.parseLong(properties.getProperty(key)) + tc));
        } else {
            properties.put(key, String.valueOf(tc));
        }
        properties.store(new BufferedWriter(new FileWriter(Constants.DEFAULT_FILE_PATH)), null);
    }

    private void statistics(String key) throws Throwable {
        var p = String.format("%s/time-%s.properties", Constants.PATH, key);
        if (!new File(p).exists()) {
            return;
        }
        var properties = new Properties();
        properties.load(new FileReader(p));
        System.out.printf(">>> %s总学习时常 <<<\n", key);
        var t = properties.values().stream().mapToLong(x -> Long.parseLong(String.valueOf(x))).sum();
//        System.out.printf("%12.6s%22.6s%9.6s\n", "(时间占比)", "(学习时常)", "(任务名称)");
        System.out.println("(时间占比) | (学习时常) | (任务名称)");
        for (Map.Entry<?, ?> entry : properties.entrySet()) {
            var k = entry.getKey();
            var v = Long.parseLong(String.valueOf(entry.getValue()));
            System.out.printf("%s%-6.6s | %s | %s\n", Utils.getProgressBar((double) v / t * 20),
                    String.format("%.2f", (double) v / t * 100) + "%", Utils.timeFormat(v), k);
        }
        System.out.printf("总学习时常:%s\n", Utils.timeFormat(t));
    }

    private void create() throws IOException {
        var file = new File(Constants.PATH);
        if (!file.exists()) {
            file.mkdirs();
        }
        file = new File(Constants.DEFAULT_FILE_PATH);
        if (!file.exists()) {
            file.createNewFile();
        }
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
