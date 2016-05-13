package com.example.star.zhihudaily.util;

import android.util.Log;

import com.example.star.zhihudaily.BuildConfig;

/**
 * A utility class to insert logs in Android Log Cat, with link to the line calling it (much like in an exception stack trace).
 */
public final class LogUtils {


    /**
     * Does not insert a link to the source code
     */
    public static final int INSERT_NONE = 0;
    /**
     * Insert the link at the beginning of the message
     */
    public static final int INSERT_FIRST = 1;
    /**
     * Insert the link at the end of the message
     */
    public static final int INSERT_LAST = 2;
    public static final boolean DEBUG = BuildConfig.DEBUG;
    private static final boolean VERBOSE = true;
    private static int sInsertMode = INSERT_LAST;


    private LogUtils() {
    }

    /**
     * Send a {@link Log#VERBOSE} log message, linking to the line in the source
     * code calling this method.
     *
     * @param tag Used to identify the source of a log message. It usually
     *            identifies the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     */
    public static final void v(final String tag, final String msg) {
        if (VERBOSE) {
            Log.v(tag, getLinkedMessage(msg));
        }
    }

    /**
     * Send a {@link Log#DEBUG} log message, linking to the line in the source
     * code calling this method.
     *
     * @param tag Used to identify the source of a log message. It usually
     *            identifies the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     */
    public static final void d(final String tag, final String msg) {
        if (DEBUG) {
            Log.d(tag, getLinkedMessage(msg));
        }
    }

    /**
     * Send a {@link Log#INFO} log message, linking to the line in the source
     * code calling this method.
     *
     * @param tag Used to identify the source of a log message. It usually
     *            identifies the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     */
    public static final void i(final String tag, final String msg) {
        Log.i(tag, getLinkedMessage(msg));
    }

    /**
     * Send a {@link Log#WARN} log message, linking to the line in the source
     * code calling this method.
     *
     * @param tag Used to identify the source of a log message. It usually
     *            identifies the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     */
    public static final void w(final String tag, final String msg) {
        Log.w(tag, getLinkedMessage(msg));
    }

    /**
     * Send a {@link Log#ERROR} log message, linking to the line in the source
     * code calling this method.
     *
     * @param tag Used to identify the source of a log message. It usually
     *            identifies the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     */
    public static final void e(final String tag, final String msg) {
        Log.e(tag, getLinkedMessage(msg));
    }

    /**
     * Send a {@link Log#VERBOSE} log message, linking to the line in the source
     * code calling this method.
     *
     * @param tag Used to identify the source of a log message. It usually
     *            identifies the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     */
    public static final void wtf(final String tag, final String msg) {
        Log.wtf(tag, getLinkedMessage(msg));
    }

    /**
     * @param mode how to insert the link in the log message (one of {@link #INSERT_NONE},
     *             {@link #INSERT_FIRST} or {@link #INSERT_LAST})
     *             Default is {@link #INSERT_LAST} and you can call this method only once to setup the LogUtils
     */
    public static void setInsertMode(int mode) {
        sInsertMode = mode;
    }

    private static final String getLinkedMessage(final String msg) {
        String link;
        if (sInsertMode == INSERT_NONE) {
            link = msg;
        } else {


            StringBuilder builder = new StringBuilder();


            // 0 = native getThreadStackTrace()
            // 1 = Thread.getStackTrace()
            // 2 = LogUtils.getLinkedMessage()
            // 3 = LogUtils.x()
            // 4 = Caller
            StackTraceElement[] stack = Thread.currentThread().getStackTrace();


            if (sInsertMode == INSERT_FIRST) {
                buildLink(stack[3], builder);
                builder.append(' ');
            }


            builder.append(msg);


            if (sInsertMode == INSERT_LAST) {
                builder.append(' ');
                buildLink(stack[4], builder);
            }


            link = builder.toString();
        }
        return link;
    }

    private static void buildLink(final StackTraceElement element,
                                  final StringBuilder builder) {


        builder.append("at (");
        builder.append(element.getFileName());
        builder.append(':');
        builder.append(element.getLineNumber());
        builder.append(')');
    }


}