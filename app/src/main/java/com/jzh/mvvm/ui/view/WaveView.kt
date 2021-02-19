package com.jzh.mvvm.ui.view

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.annotation.TargetApi
import android.content.Context
import android.graphics.*
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import androidx.annotation.RequiresApi
import com.jzh.mvvm.R
import com.jzh.mvvm.utils.DensityUtil
import java.util.*
import kotlin.math.sin

/**
 * 带水波的自定义View
 * Created by jzh on 2021-2-6.
 */
class WaveView : View {
    enum class ShapeType {
        //圆形
        CIRCLE,

        //矩形
        SQUARE
    }

    // shader containing repeated waves
    private var mWaveShader: BitmapShader? = null

    // shader matrix
    private var mShaderMatrix: Matrix? = null

    // paint to draw wave
    private var mViewPaint: Paint? = null

    // paint to draw border
    private var mBorderPaint: Paint? = null
    private var mDefaultAmplitude = 0f
    private var mDefaultWaterLevel = 0f
    private var mDefaultWaveLength = 0f
    private var mDefaultAngularFrequency = 0.0
    private var mAmplitudeRatio = DEFAULT_AMPLITUDE_RATIO
    private var mWaveLengthRatio = DEFAULT_WAVE_LENGTH_RATIO
    private var mWaterLevelRatio = DEFAULT_WATER_LEVEL_RATIO
    private var mWaveShiftRatio = DEFAULT_WAVE_SHIFT_RATIO
    private var mBehindWaveColor = DEFAULT_BEHIND_WAVE_COLOR
    private var mFrontWaveColor = DEFAULT_FRONT_WAVE_COLOR
    private var mShapeType = DEFAULT_WAVE_SHAPE
    private var mBorderWidth = 10
    private var mBorderColor = Color.parseColor("#4489CFF0")
    private var mAnimatorSet: AnimatorSet? = null

    private var mAnimatorHeight = 0.5f

    constructor(context: Context) : super(context) {
        init(context, null)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        init(context, attrs)
    }

    @TargetApi(21)
    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes) {
        init(context, attrs)
    }

    fun getWaveShiftRatio(): Float {
        return mWaveShiftRatio
    }

    /**
     * Shift the wave horizontally according to `waveShiftRatio`.
     *
     * @param waveShiftRatio Should be 0 ~ 1. Default to be 0.
     * Result of waveShiftRatio multiples width of WaveView is the length to shift.
     */
    fun setWaveShiftRatio(waveShiftRatio: Float) {
        if (mWaveShiftRatio != waveShiftRatio) {
            mWaveShiftRatio = waveShiftRatio
            invalidate()
        }
    }

    //获取默认的显示比率，默认显示为整个View高度的一半
    fun getWaterLevelRatio(): Float {
        return mWaterLevelRatio
    }

    /**
     * Set water level according to `waterLevelRatio`.
     *
     * @param waterLevelRatio Should be 0 ~ 1. Default to be 0.5.
     * Ratio of water level to WaveView height.
     */
    fun setWaterLevelRatio(waterLevelRatio: Float) {
        if (mWaterLevelRatio != waterLevelRatio) {
            mWaterLevelRatio = waterLevelRatio
            invalidate()
        }
    }

    fun getAmplitudeRatio(): Float {
        return mAmplitudeRatio
    }

    /**
     * Set vertical size of wave according to `amplitudeRatio`
     *
     * @param amplitudeRatio Default to be 0.05. Result of amplitudeRatio + waterLevelRatio should be less than 1.
     * Ratio of amplitude to height of WaveView.
     */
    fun setAmplitudeRatio(amplitudeRatio: Float) {
        if (mAmplitudeRatio != amplitudeRatio) {
            mAmplitudeRatio = amplitudeRatio
            invalidate()
        }
    }

    fun getWaveLengthRatio(): Float {
        return mWaveLengthRatio
    }

    /**
     * Set horizontal size of wave according to `waveLengthRatio`
     *
     * @param waveLengthRatio Default to be 1.
     * Ratio of wave length to width of WaveView.
     */
    fun setWaveLengthRatio(waveLengthRatio: Float) {
        mWaveLengthRatio = waveLengthRatio
    }

    private fun init(context: Context, attrs: AttributeSet?) {

        //获得这个控件对应的属性。
        val a = getContext().obtainStyledAttributes(attrs, R.styleable.WaveView)
        try {
            //获得属性值
            //标题颜色
            mFrontWaveColor =
                a.getColor(R.styleable.WaveView_WaveColor, Color.parseColor("#89CFF0"))
            mBehindWaveColor = DensityUtil.changeColorAlpha(mFrontWaveColor, 40)
            mBorderColor = DensityUtil.changeColorAlpha(mFrontWaveColor, 68)
            mAnimatorHeight = a.getFloat(R.styleable.WaveView_WaveHeight, 0.5f)
            mBorderWidth =
                a.getDimension(R.styleable.WaveView_WaveBorder, mBorderWidth * 1f).toInt()
            when (a.getInt(R.styleable.WaveView_WaveShapeType, 0)) {
                1 -> mShapeType = ShapeType.SQUARE
                0 -> mShapeType = ShapeType.CIRCLE
                else -> {
                }
            }
        } finally {
            //回收这个对象
            a.recycle()
        }
        mShaderMatrix = Matrix()
        mViewPaint = Paint()
        mViewPaint?.isAntiAlias = true
        setWaveColor(mBehindWaveColor, mFrontWaveColor)
        setBorder(mBorderWidth, mBorderColor)
        initAnimation()
        start()
    }

    private fun setWaveColor(behindWaveColor: Int, frontWaveColor: Int) {
        mBehindWaveColor = behindWaveColor
        mFrontWaveColor = frontWaveColor
        if (width > 0 && height > 0) {
            // need to recreate shader when color changed
            mWaveShader = null
            createShader()
            invalidate()
        }
    }

    fun setShapeType(shapeType: ShapeType) {
        mShapeType = shapeType
        invalidate()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        createShader()
    }

    /**
     * Create the shader with default waves which repeat horizontally, and clamp vertically
     */
    private fun createShader() {
        mDefaultAngularFrequency = 2.0f * Math.PI / DEFAULT_WAVE_LENGTH_RATIO / width
        mDefaultAmplitude = height * DEFAULT_AMPLITUDE_RATIO
        mDefaultWaterLevel = height * DEFAULT_WATER_LEVEL_RATIO
        mDefaultWaveLength = width.toFloat()
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val wavePaint = Paint()
        wavePaint.strokeWidth = 2f
        wavePaint.isAntiAlias = true

        // Draw default waves into the bitmap
        // y=Asin(ωx+φ)+h
        val endX = width + 1
        val endY = height + 1
        val waveY = FloatArray(endX)
        wavePaint.color = mBehindWaveColor
        for (beginX in 0 until endX) {
            val wx = beginX * mDefaultAngularFrequency
            val beginY = (mDefaultWaterLevel + mDefaultAmplitude * sin(wx)).toFloat()
            canvas.drawLine(beginX.toFloat(), beginY, beginX.toFloat(), endY.toFloat(), wavePaint)
            waveY[beginX] = beginY
        }
        wavePaint.color = mFrontWaveColor
        val wave2Shift = (mDefaultWaveLength / 4).toInt()
        for (beginX in 0 until endX) {
            canvas.drawLine(
                beginX.toFloat(),
                waveY[(beginX + wave2Shift) % endX], beginX.toFloat(), endY.toFloat(), wavePaint
            )
        }

        // use the bitmap to create the shader
        mWaveShader = BitmapShader(bitmap, Shader.TileMode.REPEAT, Shader.TileMode.CLAMP)
        mViewPaint?.shader = mWaveShader
    }

    override fun onDraw(canvas: Canvas) {
        // modify paint shader according to mShowWave state
        if (mWaveShader != null) {
            // first call after mShowWave, assign it to our paint
            if (mViewPaint?.shader == null) {
                mViewPaint?.shader = mWaveShader
            }

            // sacle shader according to mWaveLengthRatio and mAmplitudeRatio
            // this decides the size(mWaveLengthRatio for width, mAmplitudeRatio for height) of waves
            mShaderMatrix?.setScale(
                mWaveLengthRatio / DEFAULT_WAVE_LENGTH_RATIO,
                mAmplitudeRatio / DEFAULT_AMPLITUDE_RATIO, 0f,
                mDefaultWaterLevel
            )
            // translate shader according to mWaveShiftRatio and mWaterLevelRatio
            // this decides the start position(mWaveShiftRatio for x, mWaterLevelRatio for y) of waves
            mShaderMatrix?.postTranslate(
                mWaveShiftRatio * width,
                (DEFAULT_WATER_LEVEL_RATIO - mWaterLevelRatio) * height
            )

            // assign matrix to invalidate the shader
            mWaveShader?.setLocalMatrix(mShaderMatrix)
            val borderWidth = if (mBorderPaint == null) 0f else mBorderPaint!!.strokeWidth
            when (mShapeType) {
                ShapeType.CIRCLE -> {
                    if (borderWidth > 0) {
                        canvas.drawCircle(
                            width / 2f, height / 2f,
                            (width - borderWidth) / 2f - 1f, mBorderPaint!!
                        )
                    }
                    val radius = width / 2f - borderWidth
                    canvas.drawCircle(width / 2f, height / 2f, radius, mViewPaint!!)
                }
                ShapeType.SQUARE -> {
                    if (borderWidth > 0) {
                        canvas.drawRect(
                            borderWidth / 2f,
                            borderWidth / 2f,
                            width - borderWidth / 2f - 0.5f,
                            height - borderWidth / 2f - 0.5f,
                            mBorderPaint!!
                        )
                    }
                    canvas.drawRect(
                        borderWidth, borderWidth, width - borderWidth,
                        height - borderWidth, mViewPaint!!
                    )
                }
            }
        } else {
            mViewPaint!!.shader = null
        }
    }

    fun setBorder(width: Int, color: Int) {
        if (mBorderPaint == null) {
            mBorderPaint = Paint()
            mBorderPaint!!.isAntiAlias = true
            mBorderPaint!!.style = Paint.Style.STROKE
        }
        mBorderColor = color
        mBorderWidth = width
        mBorderPaint!!.color = mBorderColor
        mBorderPaint!!.strokeWidth = mBorderWidth.toFloat()
        invalidate()
    }

    fun getBehindWaveColor(): Int {
        return mBehindWaveColor
    }

    fun setBehindWaveColor(behindWaveColor: Int) {
        mBehindWaveColor = behindWaveColor
    }

    fun getFrontWaveColor(): Int {
        return mFrontWaveColor
    }

    fun setFrontWaveColor(frontWaveColor: Int) {
        mFrontWaveColor = frontWaveColor
    }

    fun getShapeType(): ShapeType {
        return mShapeType
    }

    fun getBorderWidth(): Int {
        return mBorderWidth
    }

    fun setBorderWidth(borderWidth: Int) {
        mBorderWidth = borderWidth
    }

    fun getBorderColor(): Int {
        return mBorderColor
    }

    fun setBorderColor(borderColor: Int) {
        mBorderColor = borderColor
    }

    private fun initAnimation() {
        val animators: MutableList<Animator> = ArrayList()

        // horizontal animation.
        // wave waves infinitely.
        val waveShiftAnim = ObjectAnimator.ofFloat(
            this, "waveShiftRatio", 0f, 1f
        )
        waveShiftAnim.repeatCount = ValueAnimator.INFINITE
        waveShiftAnim.duration = 1000
        waveShiftAnim.interpolator = LinearInterpolator()
        animators.add(waveShiftAnim)

        // vertical animation.
        // water level increases from 0 to mAnimatorHeight of RxWaveView
        val waterLevelAnim = ObjectAnimator.ofFloat(
            this, "waterLevelRatio", 0f, mAnimatorHeight
        )
        waterLevelAnim.duration = 10000
        waterLevelAnim.interpolator = DecelerateInterpolator()
        animators.add(waterLevelAnim)

        // amplitude animation.
        // wave grows big then grows small, repeatedly
        val amplitudeAnim = ObjectAnimator.ofFloat(
            this, "amplitudeRatio", 0.0001f, 0.05f
        )
        amplitudeAnim.repeatCount = ValueAnimator.INFINITE
        amplitudeAnim.repeatMode = ValueAnimator.REVERSE
        amplitudeAnim.duration = 5000
        amplitudeAnim.interpolator = LinearInterpolator()
//        animators.add(amplitudeAnim)
        mAnimatorSet = AnimatorSet()
        mAnimatorSet!!.playTogether(animators)
    }

    fun start() =  mAnimatorSet?.start()

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    fun pause() = mAnimatorSet?.pause()

    fun end() = mAnimatorSet?.end()

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    companion object {
        /**
         * +------------------------+
         * |<--wave length->        |______
         * |   /\          |   /\   |  |
         * |  /  \         |  /  \  | amplitude
         * | /    \        | /    \ |  |
         * |/      \       |/      \|__|____
         * |        \      /        |  |
         * |         \    /         |  |
         * |          \  /          |  |
         * |           \/           | water level
         * |                        |  |
         * |                        |  |
         * +------------------------+__|____
         */
        private const val DEFAULT_AMPLITUDE_RATIO = 0.05f
        private const val DEFAULT_WATER_LEVEL_RATIO = 0.5f
        private const val DEFAULT_WAVE_LENGTH_RATIO = 1.0f
        private const val DEFAULT_WAVE_SHIFT_RATIO = 0.0f
        val DEFAULT_BEHIND_WAVE_COLOR = Color.parseColor("#2889CFF0")
        val DEFAULT_FRONT_WAVE_COLOR = Color.parseColor("#3C89CFF0")
        val DEFAULT_WAVE_SHAPE = ShapeType.CIRCLE
    }
}
