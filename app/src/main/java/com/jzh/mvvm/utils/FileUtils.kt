package com.jzh.mvvm.utils

import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import com.jzh.mvvm.base.BaseApplication
import com.jzh.mvvm.constant.Constant
import java.io.*
import java.math.BigDecimal


class FileUtils {


    companion object {
        /**
         * 生成文件夹路径
         */
        var SDPATH: String =
            BaseApplication.mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES).toString()

        /**
         * 质量压缩 并返回Bitmap
         *
         * @param image
         * 要压缩的图片
         * @return 压缩后的图片
         */
        fun compressImage(image: Bitmap): Bitmap? {
            val baos = ByteArrayOutputStream()
            image.compress(
                Bitmap.CompressFormat.JPEG,
                100,
                baos
            ) // 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
            var options = 100
            while (baos.toByteArray().size / 1024 > 5000) { // 循环判断如果压缩后图片是否大于5M,大于继续压缩
                baos.reset() // 重置baos即清空baos
                image.compress(
                    Bitmap.CompressFormat.JPEG,
                    options,
                    baos
                ) // 这里压缩options%，把压缩后的数据存放到baos中
                options -= 10 // 每次都减少10
            }
            val isBm =
                ByteArrayInputStream(baos.toByteArray()) // 把压缩后的数据baos存放到ByteArrayInputStream中
            return BitmapFactory.decodeStream(isBm, null, null)
        }

        /**
         * 将图片压缩保存到文件夹
         *
         * @param bm
         * @param picName
         */
        fun saveBitmap(bm: Bitmap, picName: String): File? {
            var filePath: File? = null
            try {
                val path = SDPATH
                // 如果没有文件夹就创建一个程序文件夹
                if (!isFileExist(path)) {
                    val tempf: File = createSDDir(path)
                }
                val f = File(path, "$picName.jpg")
                filePath = f
                // 如果该文件夹中有同名的文件，就先删除掉原文件
                if (f.exists()) {
                    f.delete()
                }
                val out = FileOutputStream(f)
                bm.compress(Bitmap.CompressFormat.JPEG, 100, out)
                out.flush()
                out.close()
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return filePath
        }

        /**
         * 将头像压缩保存到文件夹
         *
         * @param bm
         * @param picName
         */
        fun saveBitmap(bm: Bitmap): File? {
            var filePath: File? = null
            try {
                val path = SDPATH
                // 如果没有文件夹就创建一个程序文件夹
                if (!isFileExist(path)) {
                    val tempf: File = createSDDir(path)
                }
                val f = File(path, Constant.HEAD_PIC_PATH)
                filePath = f
                // 如果该文件夹中有同名的文件，就先删除掉原文件
                if (f.exists()) {
                    f.delete()
                }
                val out = FileOutputStream(f)
                //头像较小，压缩小一点，不影响画质
                bm.compress(Bitmap.CompressFormat.JPEG, 50, out)
                out.flush()
                out.close()
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return filePath
        }

        /**
         * 质量压缩
         *
         * @param bitmap
         * @param picName
         */
        fun compressImageByQuality(
            bitmap: Bitmap,
            picName: String
        ) { // 如果没有文件夹就创建一个程序文件夹
            if (!isFileExist(SDPATH)) {
                try {
                    val tempf: File = createSDDir(SDPATH)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
            val f = File(SDPATH, "$picName.JPEG")
            // 如果该文件夹中有同名的文件，就先删除掉原文件
            if (f.exists()) {
                f.delete()
            }
            val baos = ByteArrayOutputStream()
            var options = 100
            // 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
            bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos)
            // 循环判断如果压缩后图片是否大于200kb,大于继续压缩
            while (baos.toByteArray().size / 1024 > 500) { // 重置baos即让下一次的写入覆盖之前的内容
                baos.reset()
                // 图片质量每次减少5
                options -= 5
                // 如果图片质量小于10，则将图片的质量压缩到最小值
                if (options < 0) options = 0
                // 将压缩后的图片保存到baos中
                bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos)
                // 如果图片的质量已降到最低则，不再进行压缩
                if (options == 0) break
            }
            // 将压缩后的图片保存的本地上指定路径中
            val fos: FileOutputStream
            try {
                fos = FileOutputStream(File(SDPATH, "$picName.JPEG"))
                fos.write(baos.toByteArray())
                fos.flush()
                fos.close()
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        /**
         * 创建文件夹
         *
         * @param dirName
         * 文件夹名称
         * @return 文件夹路径
         * @throws IOException
         */
        @Throws(IOException::class)
        fun createSDDir(dirName: String): File {
            val dir = File(dirName)
            if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED
            ) {
                Log.d("createSDDir:", dir.absolutePath)
                Log.d("createSDDir:", "${dir.mkdir()}")
            }
            return dir
        }

        /**
         * 判断该文件是否是一个标准文件
         *
         * @param fileName
         * 判断的文件路径
         * @return 判断结果
         */
        private fun isFileExist(fileName: String): Boolean {
            val file = File(fileName)
            file.isFile
            return file.exists()
        }

        /**
         * 删除指定文件
         *
         * @param fileName
         */
        fun delFile(fileName: String) {
            val file = File(SDPATH + fileName)
            if (file.isFile()) {
                file.delete()
            }
            file.exists()
        }

        /**
         * 获取指定文件
         *
         * @param fileName
         */
        fun getFile(fileName: String): String? {
            val file = File(SDPATH + fileName)
            return if (file.exists()) file.path else return null
        }

        /**
         * 删除指定文件
         * @param file
         */
        fun deleteFile(file: File) {
            if (file.exists()) { // 判断文件是否存在
                if (file.isFile()) { // 判断是否是文件
                    file.delete() // delete()方法 你应该知道 是删除的意思;
                } else if (file.isDirectory()) { // 否则如果它是一个目录
                    val files: Array<File> = file.listFiles() // 声明目录下所有的文件 files[];
                    for (i in files.indices) { // 遍历目录下所有的文件
                        deleteFile(files[i]) // 把每个文件 用这个方法进行迭代
                    }
                }
                file.delete()
            } else {
                Log.d("TAG", "文件不存在！")
            }
        }

        /**
         * 删除指定文件夹中的所有文件
         */
        fun deleteDir() {
            val dir = File(SDPATH)
            if (dir == null || !dir.exists() || !dir.isDirectory) return
            for (file in dir.listFiles()) {
                if (file.isFile) file.delete() else if (file.isDirectory) deleteDir()
            }
            dir.delete()
        }

        /**
         * 判断是否存在该文件
         *
         * @param path
         * 文件路径
         * @return
         */
        fun fileIsExists(path: String?): Boolean {
            try {
                val f = File(path)
                if (!f.exists()) {
                    return false
                }
            } catch (e: Exception) {
                return false
            }
            return true
        }

        /**
         * 根据指定路径加载图片
         */
        fun pathToImg(path: String): Bitmap? {
            return try {
                val file = File(path)
                if (file.exists()) {
                    val bitmap = BitmapFactory.decodeFile(path)
                    bitmap
                } else null
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }

        fun getRealPathFromURI(context: Context, uri: Uri): String {
            var cursor: Cursor? = null
            return try {
                val proj = arrayOf(MediaStore.Images.Media.DATA)
                //由context.getContentResolver()获取contentProvider再获取cursor(游标)用游标获取文件路径返回
                cursor = context.contentResolver.query(uri, proj, null, null, null)
                cursor?.moveToFirst()
                val column_indenx = cursor?.getColumnIndexOrThrow(MediaStore.Images.Media.DATA) ?: 0
                cursor?.getString(column_indenx) ?: ""
            } finally {
                cursor?.close()
            }
        }

        fun isSDCardAlive(): Boolean {
            return Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED
        }

        fun delete(file: File?, except: String?) {
            if (file == null) {
                return
            }
            if (file.isDirectory) {
                val children = file.list()
                for (c in children) {
                    val childFile = File(file, c)
                    if (!TextUtils.equals(childFile.name, except)) {
                        delete(childFile)
                    }
                }
            } else {
                if (!TextUtils.equals(file.name, except)) {
                    file.delete()
                }
            }
        }

        fun delete(file: File?): Boolean {
            if (file == null) {
                return false
            }
            if (file.isDirectory) {
                val children = file.list()
                for (c in children) {
                    val success = delete(File(file, c))
                    if (!success) {
                        return false
                    }
                }
            }
            return file.delete()
        }

        fun getSize(file: File): Long {
            var size: Long = 0
            try {
                val fileList = file.listFiles()
                for (f in fileList) {
                    size = if (f.isDirectory) {
                        size + getSize(f)
                    } else {
                        size + f.length()
                    }
                }
            } catch (ignore: java.lang.Exception) {
            }
            return size
        }

        /**
         * 格式化单位
         */
        fun formatSize(size: Double): String? {
            val kiloByte = size / 1024
            if (kiloByte < 1) {
                return "0KB"
            }
            val megaByte = kiloByte / 1024
            if (megaByte < 1) {
                val result1 = BigDecimal(java.lang.Double.toString(kiloByte))
                return result1.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "KB"
            }
            val gigaByte = megaByte / 1024
            if (gigaByte < 1) {
                val result2 = BigDecimal(java.lang.Double.toString(megaByte))
                return result2.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "MB"
            }
            val teraBytes = gigaByte / 1024
            if (teraBytes < 1) {
                val result3 = BigDecimal(java.lang.Double.toString(gigaByte))
                return result3.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "GB"
            }
            val result4 = BigDecimal(teraBytes)
            return result4.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "TB"
        }
    }
}