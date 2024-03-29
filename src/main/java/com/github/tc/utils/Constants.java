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

/**
 * @author gao_xianglong@sina.com
 * @version 0.1-SNAPSHOT
 * @date created in 2022/5/25 23:30
 */
public class Constants {
    public final static String VERSION = "1.0-SNAPSHOT";

    /**
     * 一小时所对应的秒数
     */
    public static final int HOUR = 0xe10;
    /**
     * 一分钟所对应的秒数
     */
    public static final int MINUTE = 0x3c;
    public static final String PATH = String.format("%s/tc", System.getProperty("user.home"));
    public static final String DEFAULT_FILE_PATH = String.format("%s/time-%s.properties", PATH, Utils.getDate());
}
