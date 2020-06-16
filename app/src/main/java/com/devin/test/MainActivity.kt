package com.devin.test

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    var mHandle = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var message = "限时活动：以下商品可以"
        val start = "2020/01/11 17:21:51.347"
        val end = "2020/01/12 23:22:55.716"
        var test = Utils.getTime(start, end)
        Utils.setMarketingInfoAndStartCountdown(tvCountDown, message, test)

//        mHandle.postDelayed(Runnable {
//            var message = "限时活动：以下商品可以使用满1元9折，最高减500元优惠券限时活动：以下商品可以使用，最高减用，最高500元优惠券限时活动：以下商品可以使用满1元9折，最高减500元优惠券"
//            val start = "2020/01/11 17:21:40.347"
//            val end = "2020/01/11 17:22:55.716"
//            Utils.setMarketingInfoAndStartCountdown(tvCountDown, message, Utils.getTime(start, end))
//        }, 5 * 1000)
    }
}