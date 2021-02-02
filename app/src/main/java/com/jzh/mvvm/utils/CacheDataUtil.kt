package com.jzh.mvvm.utils

import com.jzh.mvvm.base.BaseApplication

object CacheDataUtil {

    /**
     * 获取系统默认缓存路径
     * Created by jzh on 2021-2-2.
     */
    fun getCacheDir(): String? {
        var cacheFile =
            if (FileUtils.isSDCardAlive()) BaseApplication.mContext.externalCacheDir else null
        if (cacheFile == null) cacheFile = BaseApplication.mContext.cacheDir
        return cacheFile?.absolutePath
    }

    /**
     * 获取系统默认缓存文件夹内的缓存大小
     */
    fun getTotalCacheSize(): String? {
        var cacheSize = FileUtils.getSize(BaseApplication.mContext.cacheDir)
        if (FileUtils.isSDCardAlive() && BaseApplication.mContext.externalCacheDir != null) {
            cacheSize += FileUtils.getSize(BaseApplication.mContext.externalCacheDir!!)
        }
        return FileUtils.formatSize(cacheSize.toDouble())
    }

    /**
     * 清除系统默认缓存文件夹内的缓存
     */
    fun clearAllCache(): Boolean {
        var isClearSuccess = FileUtils.delete(BaseApplication.mContext.cacheDir)
        if (FileUtils.isSDCardAlive()) {
            isClearSuccess = FileUtils.delete(BaseApplication.mContext.externalCacheDir)
        }
        return isClearSuccess
    }
}