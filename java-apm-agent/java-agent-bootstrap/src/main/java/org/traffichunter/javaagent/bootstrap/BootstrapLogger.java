/*
 * The MIT License
 *
 * Copyright (c) 2024 traffic-hunter.org
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.traffichunter.javaagent.bootstrap;

import java.lang.System.Logger;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Supplier;

/**
 * @author yungwang-o
 * @version 1.1.0
 */
public final class BootstrapLogger implements Logger {

    private final Class<?> clazz;

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    private BootstrapLogger(final Class<?> clazz) {
        this.clazz = clazz;
    }

    public static BootstrapLogger getLogger(final Class<?> clazz) {
        return new BootstrapLogger(clazz);
    }

    @Override
    public String getName() {
        return clazz.getName();
    }

    @Override
    public boolean isLoggable(final Logger.Level level) {
        return true;
    }

    @Override
    public void log(final Logger.Level level,
                    final ResourceBundle bundle,
                    final String msg,
                    final Throwable thrown) {
        // TODO: bundle..
    }

    @Override
    public void log(final Logger.Level level,
                    final ResourceBundle bundle,
                    final String format,
                    final Object... params) {
        // TODO: bundle..
    }

    @Override
    public void log(final Logger.Level level, final String msg) {
        if(isLoggable(level)) {
            // TODO: consider SOP io performance...
            System.out.println(format(level, msg, List.of()));
        }
    }

    @Override
    public void log(final Logger.Level level, final Supplier<String> msgSupplier) {
        if(isLoggable(level)) {
            System.out.println(format(level, msgSupplier.get(), List.of()));
        }
    }

    @Override
    public void log(final Logger.Level level, final Object obj) {
        if(isLoggable(level)) {
            System.out.println(format(level, "", obj));
        }
    }

    @Override
    public void log(final Logger.Level level, final String msg, final Throwable thrown) {
        if(isLoggable(level)) {
            System.out.println(format(level, msg, List.of()));
            if(thrown != null) {
                thrown.printStackTrace(System.out);
            }
        }
    }

    @Override
    public void log(final Logger.Level level, final Supplier<String> msgSupplier, final Throwable thrown) {
       if(isLoggable(level)) {
           System.out.println(format(level, msgSupplier.get(), List.of()));
           if(thrown != null) {
               thrown.printStackTrace(System.out);
           }
       }
    }

    @Override
    public void log(final Logger.Level level, final String format, final Object... params) {
        if(isLoggable(level)) {
            System.out.println(format(level, format, params));
        }
    }

    public void info(final String message, final Object... args) {
        log(Logger.Level.INFO, message, args);
    }

    public void debug(final String message, final Object... args) {
        log(Logger.Level.DEBUG, message, args);
    }

    public void warn(final String message, final Object... args) {
        log(Logger.Level.WARNING, message, args);
    }

    public void error(final String message, final Object... args) {
        log(Logger.Level.ERROR, message, args);
    }

    public void trace(final String message, final Object... args) {
        log(Logger.Level.TRACE, message, args);
    }

    private String format(final Level level, final String message, final Object... args) {

        if(args == null || message == null) {
            throw new IllegalStateException("null..");
        }

        if(args.length == 0) {
            return getFormat(level, message);
        }

        if((message.isEmpty() || message.isBlank())) {
            return Arrays.toString(args);
        }

        String replace = message.replace("%", "%%")
                .replace("{}", "%s");

        String msg = String.format(replace, args);

        return getFormat(level, msg);
    }

    private String getFormat(final Level level, final String msg) {

        return String.format("%s [%s] - %s: %s",
                LocalDateTime.now().format(DATE_TIME_FORMATTER),
                level.name(),
                clazz.getName(),
                msg
        );
    }
}
