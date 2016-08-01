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

public final class Logging {

    private Logging() {
    }

    private static final Outputter STANDARD_OUTPUTTER = new StandardOutputter();

    public static Outputter standardOutputter() {
        return STANDARD_OUTPUTTER;
    }

    // must be under STANDARD_OUTPUTTER or Java will pass null
    private static final Logger GLOBAL_LOGGER = logger();

    public static Logger globalLogger() {
        return GLOBAL_LOGGER;
    }

    public static Logger logger() {
        return logger(standardOutputter());
    }

    public static Logger logger(Outputter outputter) {
        return new DefaultLogger(outputter);
    }

}
