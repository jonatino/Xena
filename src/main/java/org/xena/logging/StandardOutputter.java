/*
 *    Copyright 2016 Jonathan Beaudoin
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package org.xena.logging;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

final class StandardOutputter implements Outputter {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("MMM d, yyyy h:mm:ss a");

    @Override
    public void showLog(Level level, Object message) {
        StackTraceElement trace = new Throwable().getStackTrace()[3];
        System.err.println(LocalDateTime.now().format(FORMATTER) + " " + trace.getClassName() + " " + trace.getMethodName());
        System.err.println(level.name() + ": " + message);
    }

}
