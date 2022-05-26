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
package com.github.tc.views;

import com.github.tc.core.Timer;
import com.github.tc.utils.Constants;
import com.github.tc.utils.Utils;
import picocli.CommandLine;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author gao_xianglong@sina.com
 * @version 0.1-SNAPSHOT
 * @date created in 2022/5/25 23:29
 */
@CommandLine.Command(name = "time_control", footer = "Copyright(c) 2021 - 2031 gaoxianglong. All Rights Reserved.", version = Constants.VERSION, mixinStandardHelpOptions = true)
public class Console implements Runnable {
    @CommandLine.Option(names = {"-n", "--task-name"}, description = "具体的任务名称.")
    private String taskName = "test";

    @CommandLine.Option(names = {"-t", "--time-consuming"}, description = "你预计花多少时间来完成这项任务.")
    private Integer timeConsuming = 1;

    @CommandLine.Option(names = {"-u", "--time-unit"}, description = "对应--time-consuming的时间参数, 只能是h或m.")
    private String timeUnit = "m";

    @CommandLine.Option(names = {"-c", "--count"}, description = "是否统计以天为单位的任务耗时.")
    private boolean count;

    @CommandLine.Option(names = {"-d", "--date"}, description = "配合-count参数使用, 格式为yyyy-MM-dd，如果不指定则缺省为当前日期.")
    private String date = Utils.getDate();

    @Override
    public void run() {
        var param = new ParamDTO();
        param.setTaskName(taskName);
        param.setTimeConsuming(timeConsuming);
        param.setTimeUnit(timeUnit);
        param.setKey(Utils.getDate());
        param.setDate(date);
        param.setCount(count);
        try {
            new Timer(param).start();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }
}
