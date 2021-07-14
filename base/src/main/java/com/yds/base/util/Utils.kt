package com.yds.base.util

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import java.lang.reflect.InvocationTargetException

object Utils {
    lateinit var sApplication:Application
    fun setUp(context:Context){
        sApplication = context.applicationContext as Application
    }

    fun getApp():Application{
        if (sApplication == null) {
            sApplication = getApplicationByReflect() ?: throw NullPointerException("u should init first")
        }
        return sApplication
    }

    private fun getApplicationByReflect(): Application? {
        try {
            @SuppressLint("PrivateApi") val activityThread =
                Class.forName("android.app.ActivityThread")
            val thread = activityThread.getMethod("currentActivityThread").invoke(null)
            val app = activityThread.getMethod("getApplication").invoke(thread)
                ?: throw NullPointerException("u should init first")
            return app as Application
        } catch (e: NoSuchMethodException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
        } catch (e: InvocationTargetException) {
            e.printStackTrace()
        }
        throw NullPointerException("u should init first")
    }
}