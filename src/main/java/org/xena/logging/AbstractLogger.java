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

abstract class AbstractLogger implements Logger {

    private final Outputter outputter;

    private volatile Level level;

    AbstractLogger(Outputter outputter, Level defaultLevel) {
        this.outputter = outputter;
        this.level = defaultLevel;
    }

    private Logger log(Level level, Object message) {
        if (level().shouldLog(level)) {
            outputter.showLog(level, message);
        }
        return this;
    }

    @Override
    public final Logger info(Object message) {
        return log(Level.INFO, message);
    }

    @Override
    public final Logger error(Object message) {
        return log(Level.ERROR, message);
    }

    @Override
    public final Logger warn(Object message) {
        return log(Level.WARNING, message);
    }

    @Override
    public final Logger debug(Object message) {
        return log(Level.DEBUG, message);
    }

    @Override
    public final Level level() {
        return level;
    }

    @Override
    public final Logger setLevel(Level level) {
        this.level = level;
        return this;
    }

}
