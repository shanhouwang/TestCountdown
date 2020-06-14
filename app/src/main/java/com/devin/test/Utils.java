package com.devin.test;

import android.content.res.Resources;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.text.Spannable;
import android.text.SpannableString;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.concurrent.TimeUnit;

public class Utils {

    public static Handler mHandler = new Handler(Looper.myLooper());

    private static final String BEGIN = " 距结束 ";
    private static final String D = " 天 ";
    private static final String H = " 时 ";
    private static final String M = " 分 ";
    private static final String S = " 秒 ";

    public static int dp2px(float dpValue) {
        return (int) (0.5f + dpValue * Resources.getSystem().getDisplayMetrics().density);
    }

    /**
     * @param tv
     * @param message 营销信息
     * @param time    剩余时间（毫秒数）
     */
    public static synchronized void setMessageAndStartCountdown(final TextView tv, final String message, final long time) {
        final long[] t = {time};
        ThreadUtils.shut();
        doIt(tv, message, t[0]);
        ThreadUtils.get(ThreadUtils.Type.SCHEDULED).scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (t[0] <= 0) {
                            ThreadUtils.shut();
                            return;
                        }
                        doIt(tv, message, (t[0] = t[0] - 1000));
                    }
                });
            }
        }, 0, 1000, TimeUnit.MILLISECONDS);
    }

    private static void doIt(TextView tv, String message, long time) {
        String content = message + countDownTime(time);
        SpannableString ss = new SpannableString(message + countDownTime(time));
        int bgColor = Color.parseColor("#f8820d");
        int txtColor = Color.parseColor("#ffffff");
        int dp2dot5 = dp2px(2.5f);
        int dp2 = dp2px(2);
        int start = content.indexOf(BEGIN) + BEGIN.length();
        int dIndex = content.indexOf(D);
        int hIndex = content.indexOf(H);
        if (-1 != dIndex) {
            ss.setSpan(new RadiusBackgroundSpan(bgColor, txtColor, dp2dot5, dp2), start, dIndex, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
            start = dIndex + D.length();
        }
        ss.setSpan(new RadiusBackgroundSpan(bgColor, txtColor, dp2dot5, dp2), start, hIndex, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        start = hIndex + H.length();
        int mIndex = content.indexOf(M);
        ss.setSpan(new RadiusBackgroundSpan(bgColor, txtColor, dp2dot5, dp2), start, mIndex, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        start = mIndex + M.length();
        int sIndex = content.indexOf(S);
        ss.setSpan(new RadiusBackgroundSpan(bgColor, txtColor, dp2dot5, dp2), start, sIndex, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        tv.setText(ss);
    }

    // 获取两个时间相差分钟数
    public static long getTime(String oldTime, String newTime) throws ParseException {
        SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
        long NTime = df.parse(newTime).getTime();
        //从对象中拿到时间
        long OTime = df.parse(oldTime).getTime();
        long diff = (NTime - OTime);
        return diff;
    }

    public static boolean overDay(long time) {
        return time > 24 * 60 * 60 * 1000;
    }

    public static String countDownTime(long time) {
        StringBuffer sb = new StringBuffer();
        sb.append(BEGIN);
        if (overDay(time)) {
            sb.append((time / 1000 / 60 / 60 / 24) + D);
        }
        sb.append((time / 1000 / 60 / 60) + H);
        sb.append((time / 1000 / 60 % 60) + M);
        sb.append((time / 1000 % 60) + S);
        return sb.toString();
    }

}
