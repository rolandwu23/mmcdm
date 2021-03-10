package com.akm.mmcdm

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log


object Util {

    fun getFacebookUrl(activity: Activity, facebook_url: String): String? {
        if (activity.isFinishing) return null
        val packageManager = activity.packageManager
        return try {
            val versionCode = packageManager.getPackageInfo("com.facebook.katana", 0).versionCode
            if (versionCode >= 3002850) { //newer versions of fb app
                Log.d("facebook api", "new")
                "fb://facewebmodal/f?href=$facebook_url"
            } else { //older versions of fb app
                Log.d("facebook api", "old")
                "fb://page/" + splitUrl(activity, facebook_url)
            }
        } catch (e: PackageManager.NameNotFoundException) {
            Log.d("facebook api", "exception")
            facebook_url //normal web url
        }
    }

    /***
     * this method used to get the facebook profile name only , this method split domain into two part index 0 contains https://www.facebook.com and index 1 contains after / part
     * @param context contain context
     * @param url contains facebook url like https://www.facebook.com/kfc
     * @return if it successfully split then return "kfc"
     *
     * if exception in splitting then return "https://www.facebook.com/kfc"
     */
    fun splitUrl(context: Context?, url: String): String? {
        if (context == null) return null
        Log.d("Split string: ", "$url ")
        return try {
            val splittedUrl = url.split(".com/").toTypedArray()
            Log.d("Split string: ", splittedUrl[1] + " ")
            if (splittedUrl.size == 2) splittedUrl[1] else url
        } catch (ex: Exception) {
            url
        }
    }

}