package com.merchant.go

import android.app.Application
import com.scwang.smart.refresh.footer.BallPulseFooter
import com.scwang.smart.refresh.header.ClassicsHeader
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import com.scwang.smart.refresh.layout.constant.SpinnerStyle

class App: Application() {

    override fun onCreate() {
        super.onCreate()
        initRefresher()
    }

    private fun initRefresher() {
        SmartRefreshLayout.setDefaultRefreshHeaderCreator { context, layout ->
            layout.setPrimaryColorsId(R.color.transparent, R.color.color66,R.color.color99) //全局设置主题颜色
            val header = ClassicsHeader(context) //.setTimeFormat(new DynamicTimeFormat("更新于 %s"));//指定为经典Header，默认是 贝塞尔雷达Header
            header.setArrowResource(0)
        }
        SmartRefreshLayout.setDefaultRefreshFooterCreator { context, layout -> //指定为经典Footer，默认是 BallPulseFooter
            BallPulseFooter(context).also {
                it.spinnerStyle = SpinnerStyle.FixedBehind
                it.setNormalColor(context.resources.getColor(R.color.color_E33E44))
                it.setAnimatingColor(context.resources.getColor(R.color.color_E33E44))
            }
        }
    }

}