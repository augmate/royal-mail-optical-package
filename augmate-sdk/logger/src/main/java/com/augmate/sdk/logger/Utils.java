package com.augmate.sdk.logger;

public class Utils
{
    public static String getFrame(int popFrames) {
        StackTraceElement[] stack = What.stack();
        StackTraceElement callerFrame = null;
        String callerPath = "(N/A)";

        boolean nextFrameOut = false;

        for (StackTraceElement frame : stack) {
            String frameClass = frame.getClassName();

            if (nextFrameOut && !frameClass.equals(Log.class.getName()) && popFrames-- <= 0) {
                callerFrame = frame;
                break;
            }

            // wait until we are out of the logging system before counting frames
            if (!nextFrameOut && frameClass.equals(Log.class.getName())) {
                nextFrameOut = true;
            }
        }

        // impossible case? if the top caller was the log itself, use it as the caller source
        if (callerFrame == null && stack.length > 0)
            callerFrame = stack[stack.length - 1];

        if (callerFrame != null) {
            // trim namespace down to just the classname
            String className = callerFrame.getClassName();
            className = className.substring(className.lastIndexOf('.') + 1);

            callerPath = className + "::" + callerFrame.getMethodName();
        }

        return callerPath;
    }
}
