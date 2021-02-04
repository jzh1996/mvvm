package com.jzh.mvvm.ui.mainFragment

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.jeremyliao.liveeventbus.LiveEventBus
import com.jzh.mvvm.R
import com.jzh.mvvm.base.BaseViewModelFragment
import com.jzh.mvvm.constant.Constant
import com.jzh.mvvm.mvvm.mainViewModel.MyViewModel
import com.jzh.mvvm.ui.activity.collect.MyCollectActivity
import com.jzh.mvvm.ui.activity.login.LoginActivity
import com.jzh.mvvm.ui.activity.my.*
import com.jzh.mvvm.ui.view.BottomDialog
import com.jzh.mvvm.ui.view.MyDialog
import com.jzh.mvvm.utils.*
import com.jzh.mvvm.utils.MyMMKV.Companion.mmkv
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import kotlinx.android.synthetic.main.my_fragment.*
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException

class MyFragment : BaseViewModelFragment<MyViewModel>(), View.OnClickListener {

    companion object {
        fun newInstance() = MyFragment()
    }

    private lateinit var bottomDialog: BottomDialog
    private lateinit var logoutDialog: MyDialog
    private lateinit var refreshLayout: SmartRefreshLayout
    private val headType = "head"
    private val bgType = "backGround"
    private var type = ""

    override fun getLayoutId(): Int = R.layout.my_fragment
    override fun initData() {
        tv_name.setOnClickListener(this)
        head_pic.setOnClickListener(this)
        iv_bg_img.setOnClickListener(this)
        ll_my_collect.setOnClickListener(this)
        ll_score.setOnClickListener(this)
        ll_my_share.setOnClickListener(this)
        ll_my_logout.setOnClickListener(this)
        iv_todo.setOnClickListener(this)
        ll_my_system.setOnClickListener(this)
        ll_my_about.setOnClickListener(this)
        ll_my_laterRead.setOnClickListener(this)
        ll_my_readRecord.setOnClickListener(this)
        LiveEventBus.get(Constant.IS_LOGIN, Boolean::class.java).observe(this, {
            if (it) {
                startHttp()
            } else {
                tv_name.text = resources.getString(R.string.my_login)
                my_rank_num.text = resources.getString(R.string.my_score)
            }
        })
        LiveEventBus.get("myBadge", Boolean::class.java).observe(this, {
            if (!SettingUtil.getIsShowBadge()) {
                tv_un_todo.visibility = View.GONE
                return@observe
            }
            if (it && mmkv.decodeBool(Constant.IS_LOGIN, false)) {
                tv_un_todo.visibility = View.VISIBLE
            } else tv_un_todo.visibility = View.GONE
        })
        setImage(File(mmkv.decodeString("HeadPic", "")))
        if (mmkv.decodeString("bgHeadPic", "") != "")
            setBgImage(File(mmkv.decodeString("bgHeadPic", "")))
    }

    override fun initView(view: View) {
        refreshLayout = swipeRefreshLayout_my
        refreshLayout.run {
            setRefreshHeader(ch_header_my)
            setOnRefreshListener { startHttp() }
        }
    }

    @SuppressLint("SetTextI18n")
    override fun startHttp() {
        if (mmkv.decodeBool(Constant.IS_LOGIN, false)) {
            getTodoBadge()
            tv_name.text =
                mmkv.decodeString(Constant.USERNAME_KEY, resources.getString(R.string.my_login))
            my_rank_num.text =
                "${resources.getString(R.string.my_score)}(${mmkv.decodeString(Constant.USERNAME_COIN_COUNT)})"
            viewModel.getUserInfo().observe(this, {
                refreshLayout.finishRefresh()
                tv_name.text = it.username
                my_rank_num.text = "${resources.getString(R.string.my_score)}(${it.coinCount})"
                mmkv.encode(Constant.USERNAME_COIN_COUNT, it.coinCount.toString())
                mmkv.encode(Constant.USERNAME_KEY, it.username)
                mmkv.encode(Constant.SCORE_UNM, it.coinCount)
            })
        } else {
            if (refreshLayout.isRefreshing) refreshLayout.finishRefresh()
            tv_name.text = resources.getString(R.string.my_login)
            my_rank_num.text = resources.getString(R.string.my_score)
        }
    }

    private fun getTodoBadge() {
        val map = mutableMapOf<String, Any>()
        map["status"] = 0
        viewModel.getTodoList(1, map).observe(this, {
            hideLoading()
            it.datas.let { todoList ->
                if (todoList.size > 0) {
                    tv_un_todo.visibility = View.VISIBLE
                    LiveEventBus.get("myBadge").post(true)
                } else {
                    tv_un_todo.visibility = View.GONE
                    LiveEventBus.get("myBadge").post(false)
                }
            }
        })
    }

    private fun setImage(file: File?) {
        ImageLoader.loadByNoCache(context, file, head_pic)
    }

    private fun setBgImage(file: File?) {
        ImageLoader.loadByNoCache(context, file, iv_bg_img)
    }

    override fun providerVMClass(): Class<MyViewModel> = MyViewModel::class.java

    override fun onClick(v: View) {
        when (v) {
            tv_name -> {
                if (!mmkv.decodeBool(Constant.IS_LOGIN, false))
                    startActivity(Intent(activity, LoginActivity::class.java))
            }
            ll_score -> {
                if (mmkv.decodeBool(Constant.IS_LOGIN, false)) {
                    startActivity(Intent(activity, MyScoreActivity::class.java))
                } else {
                    startActivity(Intent(activity, LoginActivity::class.java))
                }
            }
            ll_my_collect -> {
                if (mmkv.decodeBool(Constant.IS_LOGIN, false)) {
                    startActivity(Intent(activity, MyCollectActivity::class.java))
                } else {
                    startActivity(Intent(activity, LoginActivity::class.java))
                }
            }
            ll_my_share -> {
                if (mmkv.decodeBool(Constant.IS_LOGIN, false)) {
                    startActivity(Intent(activity, MyShareActivity::class.java))
                } else {
                    startActivity(Intent(activity, LoginActivity::class.java))
                }
            }
            head_pic -> showPhotoDialog(headType)
            iv_bg_img -> showPhotoDialog(bgType)
            ll_my_logout -> {
                if (mmkv.decodeBool(Constant.IS_LOGIN, false)) {
                    if (!this::logoutDialog.isInitialized) {
                        logoutDialog = MyDialog(activity!!)
                        logoutDialog.run {
                            setDialogText("是否确认退出登录?")
                            setClickListener { v ->
                                when (v.id) {
                                    R.id.tv_dialog_sure -> {
                                        if (logoutDialog.isShowing) logoutDialog.dismiss()
                                        viewModel.logout().observe(this@MyFragment, {
                                            mmkv.encode(Constant.IS_LOGIN, false)
                                            LiveEventBus.get(Constant.IS_LOGIN).post(false)
                                            LiveEventBus.get("myBadge").post(false)
                                        })
                                    }
                                    R.id.tv_dialog_cancle -> {
                                        if (logoutDialog.isShowing) logoutDialog.dismiss()
                                    }
                                }
                            }
                            show()
                        }
                    } else logoutDialog.show()
                } else toast("当前未登录!")
            }
            iv_todo -> {
                if (mmkv.decodeBool(Constant.IS_LOGIN, false)) {
                    startActivityForResult(
                        Intent(activity, TodoActivity::class.java),
                        Constant.FROM_TODO
                    )
                } else {
                    startActivity(Intent(activity, LoginActivity::class.java))
                }
            }
            ll_my_system -> startActivity(Intent(activity, SystemActivity::class.java))
            ll_my_about -> startActivity(Intent(activity, AboutActivity::class.java))
            ll_my_laterRead -> startActivity(Intent(activity, LaterReadActivity::class.java))
            ll_my_readRecord -> startActivity(Intent(activity, ReadRecordActivity::class.java))
        }
    }

    private fun showPhotoDialog(imgType: String) {
        type = imgType
        if (this::bottomDialog.isInitialized) bottomDialog.show()
        else {
            bottomDialog = BottomDialog(
                activity!!, {
                    when (it.id) {
                        R.id.tv_take_photo -> {
                            if (this::bottomDialog.isInitialized && bottomDialog.isShowing) {
                                bottomDialog.dismiss()
                                captureImage(activity!!)
                            }
                        }
                        R.id.tv_from_album -> {
                            if (this::bottomDialog.isInitialized && bottomDialog.isShowing) {
                                bottomDialog.dismiss()
                                selectImage(activity!!)
                            }
                        }
                        R.id.tv_cancle -> {
                            if (this::bottomDialog.isInitialized && bottomDialog.isShowing) bottomDialog.dismiss()
                        }
                    }
                })
            bottomDialog.show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //打开图片
        if (resultCode == Activity.RESULT_OK && resultCode != Activity.RESULT_CANCELED) {
            when (requestCode) {
                Constant.IMAGE_CAPTURE -> {//拍照返回
                    val pathName = activity?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                        .toString() + "/" + Constant.HEAD_PIC_PATH
                    val bitmap = BitmapFactory.decodeFile(pathName) ?: return
                    val newBitmap = FileUtils.compressImage(bitmap) ?: bitmap//压缩失败使用原图
                    // 由于Bitmap内存占用较大，这里需要回收内存，否则会报out of memory异常
                    bitmap.recycle()
                    val picFileName: File?
                    if (type == headType) {
                        picFileName = FileUtils.saveBitmap(newBitmap)
                        setImage(picFileName)
                        mmkv.encode("HeadPic", picFileName?.path)
                    } else {
                        picFileName = FileUtils.saveBitmap(newBitmap, Constant.MY_BG_PIC_PATH)
                        setBgImage(picFileName)
                        mmkv.encode("bgHeadPic", picFileName?.path)
                    }
                    // 由于Bitmap内存占用较大，这里需要回收内存，否则会报out of memory异常
                    bitmap.recycle()
                }
                Constant.IMAGE_SELECT -> {
                    val resolver = activity?.contentResolver
                    // 照片的原始资源地址
                    val originalUri = data?.data
                    try {
                        // 使用ContentProvider通过URI获取原始图片
                        val photo = MediaStore.Images.Media.getBitmap(resolver, originalUri)
                        if (photo != null) {
                            // 为防止原始图片过大导致内存溢出，这里先缩小原图显示，然后释放原始Bitmap占用的内存
                            val newBitmap = FileUtils.compressImage(photo) ?: photo//压缩失败使用原图
                            // 释放原始图片占用的内存，防止out of memory异常发生
                            photo.recycle()
                            val picFileName: File?
                            if (type == headType) {
                                picFileName = FileUtils.saveBitmap(newBitmap)
                                setImage(picFileName)
                                mmkv.encode("HeadPic", picFileName?.path)
                            } else {
                                picFileName =
                                    FileUtils.saveBitmap(newBitmap, Constant.MY_BG_PIC_PATH)
                                setBgImage(picFileName)
                                mmkv.encode("bgHeadPic", picFileName?.path)
                            }
                        }
                    } catch (e: FileNotFoundException) {
                        e.printStackTrace()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
                Constant.FROM_TODO -> getTodoBadge()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (this::bottomDialog.isInitialized && bottomDialog.isShowing) bottomDialog.dismiss()
    }
}