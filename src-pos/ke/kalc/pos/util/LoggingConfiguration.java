/*
**    KALC POS  - Professional Point of Sale
**
**    This file is part of KALC POS Version KALC V1.5.4
**
**    Copyright (c) 2015-2023 KALC & previous KALC POS related works   
**
**    https://www.kalc.co.ke
**   
**
*/

package ke.kalc.pos.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.TimeBasedRollingPolicy;
import ch.qos.logback.core.util.StatusPrinter;

public class LoggingConfiguration {
    private static final Logger logger = LoggerFactory.getLogger(LoggingConfiguration.class);

    public static void configure() {
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        context.reset();

        // Pattern for the log file
        PatternLayout layout = new PatternLayout();
        layout.setContext(context);
        layout.setPattern("%d{yyyy-MM-dd HH:mm:ss} %-5level [%thread] %logger{36} - %msg%n");
        layout.start();

        // Rolling file appender
        RollingFileAppender appender = new RollingFileAppender();
        appender.setContext(context);
        appender.setName("RollingFileAppender");
        appender.setLayout(layout);
        appender.setFile("logs/app.log");

        TimeBasedRollingPolicy policy = new TimeBasedRollingPolicy();
        policy.setContext(context);
        policy.setParent(appender);
        policy.setFileNamePattern("logs/app.%d{yyyy-MM-dd}.log");
        policy.setMaxHistory(30);
        policy.start();

        appender.setRollingPolicy(policy);
        appender.start();

        // Add appender to the logger
        context.getLogger("ROOT").addAppender(appender);
        context.getLogger("ROOT").setLevel(ch.qos.logback.classic.Level.INFO);
        StatusPrinter.print(context);
    }
}