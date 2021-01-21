package com.jzh.mvvm.utils

import com.tencent.mmkv.MMKV

/**
 * MMKV
 * Created by jzh on 2020-12-23.
 */
class MyMMKV {

    companion object {
        private const val fileName = "wan_android_mvvm"

        /**
         * 初始化mmkv
         */
        val mmkv: MMKV
            get() = MMKV.mmkvWithID(fileName)

        /**
         * 删除全部数据(传了参数就是按key删除)
         */
        fun deleteKeyOrAll(key: String? = null) {
            if (key == null) mmkv.clearAll()
            else mmkv.removeValueForKey(key)
        }

        /** 查询某个key是否已经存在
         *
         * @param key
         * @return
         */
        fun contains(key: String) = mmkv.contains(key)
    }
}