package com.devin.test;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.concurrent.TimeUnit;

public class Utils {

    public static Handler mHandler = new Handler(Looper.myLooper());

    private static String BEGIN = "距结束 ";
    private static final String D = " 天 ";
    private static final String H = " 时 ";
    private static final String M = " 分 ";
    private static final String S = " 秒 ";
    private static volatile int mScreenWidthRemovePadding = 0;

    public static int dp2px(float dpValue) {
        return (int) (0.5f + dpValue * Resources.getSystem().getDisplayMetrics().density);
    }

    /**
     * 获取屏幕的宽
     *
     * @param context
     * @return
     */
    public static int getScreenWidth(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        return dm.widthPixels;
    }

    private static int getScreenWidthRemovePadding(Context context) {
        if (0 == mScreenWidthRemovePadding) {
            mScreenWidthRemovePadding = getScreenWidth(context) - dp2px(30);
        }
        return mScreenWidthRemovePadding;
    }

    /**
     * @param tv
     * @param marketingInfo 营销信息
     * @param time          剩余时间（毫秒数）
     */
    public static synchronized void setMarketingInfoAndStartCountdown(final TextView tv, final String marketingInfo, final long time) {
        final long[] t = {time};
        ThreadUtils.shut();
        doIt(tv, marketingInfo, t[0]);
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
                        doIt(tv, marketingInfo, (t[0] = t[0] - 1000));
                    }
                });
            }
        }, 0, 1000, TimeUnit.MILLISECONDS);
    }

    private static void doIt(TextView tv, String marketingInfo, long time) {
        String countDownTimeTxt = countDownTimeOrigin(time);
        marketingInfo = marketingInfo + " ";
        float messageLength = tv.getPaint().measureText(marketingInfo);
        float screenWidthRemovePadding = getScreenWidthRemovePadding(tv.getContext());
        if (messageLength < screenWidthRemovePadding) {
            countDownTimeTxt = countDownTimeDealByRegular(tv, countDownTimeTxt, messageLength);
        } else {
            float mod = messageLength % screenWidthRemovePadding;
            countDownTimeTxt = countDownTimeDealByRegular(tv, countDownTimeTxt, mod);
        }
        String content = marketingInfo + countDownTimeTxt;
        SpannableString ss = new SpannableString(content);
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

    private static String countDownTimeDealByRegular(TextView tv, String countDownTimeTxt, float messageLength) {
        float countDownTimeTxtLength = tv.getPaint().measureText(countDownTimeTxt);
        if ((messageLength + countDownTimeTxtLength) > getScreenWidthRemovePadding(tv.getContext())) {
            countDownTimeTxt = countDownTimeTxt.replaceFirst(BEGIN, ("\n" + BEGIN));
        }
        return countDownTimeTxt;
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

    public static String countDownTimeOrigin(long time) {
        StringBuffer sb = new StringBuffer();
        sb.append(BEGIN);
        if (overDay(time)) {
            sb.append((time / 1000 / 60 / 60 / 24) + D);
        }
        long hours = time / 1000 / 60 / 60 % 24;
        if (hours < 10) {
            sb.append("0");
        }
        sb.append(hours + H);
        long minute = time / 1000 / 60 % 60;
        if (minute < 10) {
            sb.append("0");
        }
        sb.append(minute + M);
        long seconds = time / 1000 % 60;
        if (seconds < 10) {
            sb.append("0");
        }
        sb.append(seconds + S);
        return sb.toString();
    }

}
