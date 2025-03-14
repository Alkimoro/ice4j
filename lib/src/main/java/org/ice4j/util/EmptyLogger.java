package org.ice4j.util;


import org.jitsi.utils.logging2.Logger;

import java.util.Map;
import java.util.function.Supplier;
import java.util.logging.Handler;
import java.util.logging.Level;

public class EmptyLogger implements Logger {
    @Override
    public Logger createChildLogger(String name, Map<String, String> context) {
        return new EmptyLogger();
    }

    @Override
    public Logger createChildLogger(String name) {
        return new EmptyLogger();
    }

    @Override
    public void setUseParentHandlers(boolean useParentHandlers) {

    }

    @Override
    public void addHandler(Handler handler) throws SecurityException {

    }

    @Override
    public void removeHandler(Handler handler) throws SecurityException {

    }

    @Override
    public boolean isTraceEnabled() {
        return false;
    }

    @Override
    public void trace(Object msg) {

    }

    @Override
    public void trace(Supplier<String> msgSupplier) {

    }

    @Override
    public boolean isDebugEnabled() {
        return false;
    }

    @Override
    public void debug(Object msg) {

    }

    @Override
    public void debug(Supplier<String> msgSupplier) {

    }

    @Override
    public boolean isInfoEnabled() {
        return false;
    }

    @Override
    public void info(Object msg) {

    }

    @Override
    public void info(Supplier<String> msgSupplier) {

    }

    @Override
    public boolean isWarnEnabled() {
        return false;
    }

    @Override
    public void warn(Object msg) {

    }

    @Override
    public void warn(Supplier<String> msgSupplier) {

    }

    @Override
    public void warn(Object msg, Throwable t) {

    }

    @Override
    public void error(Object msg) {

    }

    @Override
    public void error(Supplier<String> msgSupplier) {

    }

    @Override
    public void error(Object msg, Throwable t) {

    }

    @Override
    public void setLevelError() {

    }

    @Override
    public void setLevelWarn() {

    }

    @Override
    public void setLevelInfo() {

    }

    @Override
    public void setLevelDebug() {

    }

    @Override
    public void setLevelTrace() {

    }

    @Override
    public void setLevelAll() {

    }

    @Override
    public void setLevelOff() {

    }

    @Override
    public void setLevel(Level level) {

    }

    @Override
    public Level getLevel() {
        return Level.OFF;
    }

    @Override
    public void addContext(Map<String, String> addedContext) {

    }

    @Override
    public void addContext(String key, String value) {

    }
}
