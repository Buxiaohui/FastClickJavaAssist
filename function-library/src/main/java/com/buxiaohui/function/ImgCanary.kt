/*
 * Copyright (C) 2021 Baidu, Inc. All Rights Reserved.
 */
package com.buxiaohui.function

import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.graphics.BitmapFactory
import android.graphics.BitmapFactory.Options
import android.util.Log
import android.view.View
import android.view.ViewGroup
import java.lang.reflect.Field
import java.util.*


class ImgCanary() {
    var drawableNVMap = HashMap<String, Int>()
    var drawableVNMap = HashMap<Int, String>()
    var drawableRetSet = HashSet<String>()
    fun capture(activity: Activity) {
        var decorView = activity.window.decorView as ViewGroup
        try {
            var name = activity.packageName + ".R${'$'}drawable"
            var rClass = Class.forName(name)
            var rFilelds = rClass.getFields()
            for (rFileld in rFilelds) {
                rFileld.isAccessible = true
                var value = rFileld.getInt(rClass)
                var name = rFileld.name
                //Log.d("test", "rFileld${name}=${value}")
                drawableVNMap.put(value, rFileld.name)
                drawableNVMap.put(rFileld.name, value)
            }
        } catch (e: java.lang.Exception) {
            Log.d("test", "e is ${e}")
        }

        var duration = measureDurationMillis {
            traversal(activity, decorView)
        }
        Log.d("test", "duration is ${duration}")
    }

    fun traversal(activity: Activity, view: View) {
        judgeBackground(activity, view)
        if (view is ViewGroup) {
            var childCount = view.childCount
            if (childCount > 0) {
                for (index in 0..childCount) {
                    var childView = view.getChildAt(index)
                    if (childView?.isShown == true) {
                        traversal(activity, childView);
                    }
                }
            }
        }
    }

    private fun judgeBackground(activity: Activity, view: View) {
        // Log.d("test", "----judgeBackground----start")
        var resTypeName: String = ""
        var resEntryName: String = ""
        if (view.background != null) {
            if (view.id != View.NO_ID) {
                resTypeName = view.resources.getResourceTypeName(view.id)
                resEntryName = view.resources.getResourceEntryName(view.id)
                val apk: Context = activity.createPackageContext(
                    activity.packageName,
                    Context.CONTEXT_IGNORE_SECURITY
                )
                var viewId = apk.resources.getIdentifier(
                    resEntryName, resTypeName,
                    apk.packageName
                )
                // Log.d("test", "view id（string） is R.$resTypeName.$resEntryName")
            }
            // Log.d("test", "view.(w,h):(${view.width},${view.height})")

            try {
                var field = getDeclaredField(view, "mBackgroundResource")
                // Log.d("test", "field:${field}")
                field?.let {
                    if (field.name.equals("mBackgroundResource")) {
                        field.isAccessible = true
                        // Log.d("test", "field name:${field.name}")
                        var bgResId = field.getInt(view)
                        // Log.d("test", "field.getInt:${bgResId}")
                        // Log.d("test", "field.get:${field.get(view)}")
                        val size = getDrawableOriginSize(activity.resources, bgResId)
                        var str =
                            "viewId=$resEntryName" + ";(w.h)=${view.width},${view.height}" + "\n" +
                                    "drawableId=${drawableVNMap.get(bgResId)}" + ";(w.h)=${size[0]},${size[1]}"
                        if (size[0] > 0 && size[1] > 0) {
                            if (!drawableRetSet.contains(str)) {
                                drawableRetSet.add(str)
                                Log.d("test", "$str")
                            }
                        }
                    }
                }


            } catch (e: Exception) {
                Log.d("test", "e:${e}")
            }
        }
        // Log.d("test", "----judgeBackground----end")
    }

    fun getDrawableOriginSize(resource: Resources, resId: Int): IntArray {
        val opts = Options()
        opts.inSampleSize = 1
        opts.inJustDecodeBounds = false
        val mBitmap = BitmapFactory.decodeResource(resource, resId, opts)
        val width = opts.outWidth
        val height = opts.outHeight
        return intArrayOf(width, height)
    }

    /**
     * 循环向上转型, 获取对象的 DeclaredField
     * @param obj : 子类对象
     * @param fieldName : 父类中的属性名
     * @return 父类中的属性对象
     */
    fun getDeclaredField(obj: Any, fieldName: String?): Field? {
        var field: Field? = null
        var clazz: Class<*> = obj.javaClass
        while (clazz != Any::class.java) {
            try {
                field = clazz.getDeclaredField(fieldName)
                return field
            } catch (e: Exception) {
            }
            clazz = clazz.superclass
        }
        return null
    }
}