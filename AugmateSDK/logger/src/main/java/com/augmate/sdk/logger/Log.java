package com.augmate.sdk.logger;

import android.content.Context;
import android.os.Build;
import android.provider.Settings;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

/**
 * Special Logger :D
 * Wraps Log4j
 * + Logentries appender
 * + Standard LogCat compatible dbg-output appender
 * Uses a custom PatternLayout replacement that resolves "class::method()"
 * faster than LocationPatternConverter and lets us control amount of frames
 * popped off the stack to identify the exact caller we care about 
 */
public class Log {
    private static final String TAG = Log.class.getName();
    private static Logger loggerInstance;

    /**
     * entry-point for the application-wide logging setup
     *
     * @param ctx Context of the application. If not provided, deviceId will be N/A
     */
    public static void start(Context ctx) {
        if (loggerInstance == null) {
            String deviceId = "N/A";

            if (ctx == null) {
                android.util.Log.w(TAG, "Log::start(null); called without a ctx; creating Log without device-id");
            } else {
                deviceId = Settings.Secure.getString(ctx.getContentResolver(), Settings.Secure.ANDROID_ID);
                //android.util.Log.d(TAG, "getLogger() found device-id: " + deviceId);
            }

            loggerInstance = createInstance(deviceId);
        }
    }

    /**
     * must be called on shutdown to clean-up socket-based loggers (eg: LogEntries)
     */
    public static void shutdown() {
        LogManager.shutdown();
    }

    private static Logger createInstance(String deviceId) {

        // not super random or collision free
        String sessionId = Long.toString(Math.abs(java.util.UUID.randomUUID().getLeastSignificantBits()), 36).substring(0, 6);

        // TODO: LogEntries needs some loving
        // remote output
//        LogentriesAppender logentriesAppender = new LogentriesAppender();
//        logentriesAppender.setToken("c3a45763-9854-43cc-838a-7a1b71418c6c");
//        logentriesAppender.setDebug(true);
//        logentriesAppender.setLayout(new LogEntriesFormat(sessionId, deviceId));
//        logentriesAppender.setSsl(false);

        // local output
        LocalAppender localAppender = new LocalAppender();
        localAppender.setLayout(new LocalFormat(sessionId, deviceId));

        Logger lgr = Logger.getRootLogger();
        lgr.addAppender(localAppender);
        //lgr.addAppender(logentriesAppender);

        lgr.debug("Started session #" + sessionId + " + logging on " + Build.MANUFACTURER + " " + Build.MODEL + " version=" + Build.ID);

        return lgr;
    }

    private static Logger getLogger() {
        if (loggerInstance == null)
            start(null);

        return loggerInstance;
    }

    public static void exception(Exception err, String format, Object... args) {
        getLogger().error(String.format(format, args), err);
    }

    public static void error(String format, Object... args) {
        getLogger().error(String.format(format, args));
    }

    public static void debug(String format, Object... args) {
        getLogger().debug(String.format(format, args));
    }

    public static void info(String format, Object... args) {
        getLogger().info(String.format(format, args));
    }
}
