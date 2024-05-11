package app.chat_app_server.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class CurrentTime {
    private static final SimpleDateFormat serverTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    public static String getCurrentTimeFormatted() {
        return serverTimeFormat.format(new Date());
    }

    public static long getCurrentTime() {
        return System.currentTimeMillis();
    }
}
