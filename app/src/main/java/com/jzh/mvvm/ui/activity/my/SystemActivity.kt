package com.jzh.mvvm.ui.activity.my

import android.content.Intent
import android.os.Build
import android.view.View
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import com.jeremyliao.liveeventbus.LiveEventBus
import com.jzh.mvvm.R
import com.jzh.mvvm.base.BaseActivity
import com.jzh.mvvm.constant.Constant
import com.jzh.mvvm.ui.view.MyDialog
import com.jzh.mvvm.ui.view.RvAnimDialog
import com.jzh.mvvm.ui.view.TopDialog
import com.jzh.mvvm.utils.*
import com.jzh.mvvm.utils.MyMMKV.Companion.mmkv
import kotlinx.android.synthetic.main.activity_about.view.*
import kotlinx.android.synthetic.main.activity_system.*
import kotlinx.android.synthetic.main.rv_anim_dialog_view.*
import kotlinx.android.synthetic.main.toolbar_layout.*

class SystemActivity : BaseActivity() {

    private lateinit var topDialog: TopDialog
    private lateinit var rvDialog: RvAnimDialog
    private lateinit var tipDialog: MyDialog

    override fun getLayoutId() = R.layout.activity_system

    override fun initData() {
        if (mmkv.decodeBool("is_show_system_tip", true)) {
            if (this::tipDialog.isInitialized) tipDialog.show()
            else {
                tipDialog = MyDialog(this)
                tipDialog.run {
                    setIsShowSureOrCancel("确认", "不再提示")
                    setDialogText("部分设置需要重启APP才会生效")
                    setClickListener { v ->
                        when (v.id) {
                            R.id.tv_dialog_sure -> if (tipDialog.isShowing) tipDialog.dismiss()
                            R.id.tv_dialog_cancle -> {
                                if (tipDialog.isShowing) tipDialog.dismiss()
                                mmkv.encode("is_show_system_tip", false)
                            }
                        }
                    }
                    show()
                }
            }
        }
    }

    override fun startHttp() {}

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun initView() {
        setTop("系统设置")
        sc_system_is_img.run {
            isChecked = SettingUtil.getIsNoPhotoMode()
            setOnCheckedChangeListener { _, isChecked -> SettingUtil.setIsNoPhotoMode(isChecked) }
        }
        sc_system_show_top.run {
            isChecked = SettingUtil.getIsShowTopArticle()
            setOnCheckedChangeListener { _, isChecked -> SettingUtil.setIsShowTopArticle(isChecked) }
        }
        sc_system_show_banner.run {
            isChecked = SettingUtil.getIsShowBanner()
            setOnCheckedChangeListener { _, isChecked -> SettingUtil.setIsShowBanner(isChecked) }
        }
        sc_system_show_badge.run {
            isChecked = SettingUtil.getIsShowBadge()
            setOnCheckedChangeListener { _, isChecked ->
                SettingUtil.setIsShowBadge(isChecked)
                if (mmkv.decodeBool(Constant.IS_LOGIN, false)) {
                    LiveEventBus.get("myBadge").post(isChecked)
                } else LiveEventBus.get("myBadge").post(false)
                if (!isChecked) LiveEventBus.get("homeBadge").post(0)
                else LiveEventBus.get("refresh_homeBadge").post(true)
            }
        }
        rl_app_default_page.run {
            tv_app_default_page.text = SettingUtil.getDefaultPage()
            setOnClickListener {
                if (this@SystemActivity::topDialog.isInitialized) topDialog.show()
                else {
                    topDialog = TopDialog(
                        this@SystemActivity,
                        toolbar.height - StatusBarUtils.getStatusBarHeight(this@SystemActivity)
                    )
                    topDialog.show()
                }
            }
        }
        rl_rv_anmi.run {
            tv_rv_anim.text = SettingUtil.getListAnimal()
            setOnClickListener {
                if (this@SystemActivity::rvDialog.isInitialized) rvDialog.show()
                else {
                    rvDialog = RvAnimDialog(this@SystemActivity, mClickListener)
                    rvDialog.show()
                }
                setRvAnimColor()
            }
        }
        rl_system_clear.run {
            tv_system_clear_num.text = CacheDataUtil.getTotalCacheSize() ?: "0.0KB"
            setOnClickListener {
                if (CacheDataUtil.clearAllCache()) {
                    tv_system_clear_num.text = CacheDataUtil.getTotalCacheSize() ?: "0.0KB"
                }
                toast("清理成功")
            }
        }
        rl_about.run {
            setOnClickListener {
                startActivity(Intent(this@SystemActivity, AboutActivity::class.java))
            }
        }
    }

    private val mClickListener = View.OnClickListener {
        when (it.id) {
            R.id.tv_rv_no -> {
                SettingUtil.setListAnimal(it.findViewById<TextView>(R.id.tv_rv_no).text.toString())
            }
            R.id.tv_rv_jx -> {
                SettingUtil.setListAnimal(it.findViewById<TextView>(R.id.tv_rv_jx).text.toString())
            }
            R.id.tv_rv_db -> {
                SettingUtil.setListAnimal(it.findViewById<TextView>(R.id.tv_rv_db).text.toString())
            }
            R.id.tv_rv_sf -> {
                SettingUtil.setListAnimal(it.findViewById<TextView>(R.id.tv_rv_sf).text.toString())
            }
            R.id.tv_rv_zc -> {
                SettingUtil.setListAnimal(it.findViewById<TextView>(R.id.tv_rv_zc).text.toString())
            }
            R.id.tv_rv_yc -> {
                SettingUtil.setListAnimal(it.findViewById<TextView>(R.id.tv_rv_yc).text.toString())
            }
        }
        LiveEventBus.get("rv_anim").post(SettingUtil.getListAnimal())
        tv_rv_anim.text = SettingUtil.getListAnimal()
        rvDialog.dismiss()
    }

    private fun setRvAnimColor() {
        rvDialog.tv_rv_no.setTextColor(resources.getColor(R.color.text_surface))
        rvDialog.tv_rv_jx.setTextColor(resources.getColor(R.color.text_surface))
        rvDialog.tv_rv_db.setTextColor(resources.getColor(R.color.text_surface))
        rvDialog.tv_rv_sf.setTextColor(resources.getColor(R.color.text_surface))
        rvDialog.tv_rv_zc.setTextColor(resources.getColor(R.color.text_surface))
        rvDialog.tv_rv_yc.setTextColor(resources.getColor(R.color.text_surface))
        when (SettingUtil.getListAnimal()) {
            "无" -> rvDialog.tv_rv_no.setTextColor(resources.getColor(R.color.colorPrimaryDark))
            "渐显" -> rvDialog.tv_rv_jx.setTextColor(resources.getColor(R.color.colorPrimaryDark))
            "缩放" -> rvDialog.tv_rv_sf.setTextColor(resources.getColor(R.color.colorPrimaryDark))
            "底部滑入" -> rvDialog.tv_rv_db.setTextColor(resources.getColor(R.color.colorPrimaryDark))
            "左侧滑入" -> rvDialog.tv_rv_zc.setTextColor(resources.getColor(R.color.colorPrimaryDark))
            "右侧滑入" -> rvDialog.tv_rv_yc.setTextColor(resources.getColor(R.color.colorPrimaryDark))
        }
    }
}