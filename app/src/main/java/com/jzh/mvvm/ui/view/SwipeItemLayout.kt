package com.jzh.mvvm.ui.view

import android.content.Context
import android.util.AttributeSet
import android.view.*
import android.view.animation.Interpolator
import android.widget.Scroller
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnItemTouchListener


/**
 * 带左滑删除的item
 * 使用这个view会导致adapter自带的item点击事件无效，所以需要在ChildClick添加item点击事件
 * Created by jzh on 2021-1-20.
 */
class SwipeItemLayout @JvmOverloads constructor(context: Context?, attrs: AttributeSet? = null) :
    ViewGroup(context, attrs) {

    internal enum class Mode {
        RESET, DRAG, FLING, TAP
    }

    private var mTouchMode: Mode
    private var mMainView: ViewGroup? = null
    private var mSideView: ViewGroup? = null
    private val mScrollRunnable: ScrollRunnable
    private var mScrollOffset: Int
    private var mMaxScrollOffset = 0
    private var mInLayout = false
    private var mIsLaidOut: Boolean
    val isOpen: Boolean
        get() = mScrollOffset != 0
    private var touchMode: Mode
        get() = mTouchMode
        set(mode) {
            when (mTouchMode) {
                Mode.FLING -> mScrollRunnable.abort()
                Mode.RESET -> {
                }
            }
            mTouchMode = mode
        }

    fun open() {
        if (mScrollOffset != -mMaxScrollOffset) {
            //正在open，不需要处理
            if (mTouchMode == Mode.FLING && mScrollRunnable.isScrollToLeft) return

            //当前正在向右滑，abort
            if (mTouchMode == Mode.FLING /*&& !mScrollRunnable.mScrollToLeft*/) mScrollRunnable.abort()
            mScrollRunnable.startScroll(mScrollOffset, -mMaxScrollOffset)
        }
    }

    fun close() {
        if (mScrollOffset != 0) {
            //正在close，不需要处理
            if (mTouchMode == Mode.FLING && !mScrollRunnable.isScrollToLeft) return

            //当前正向左滑，abort
            if (mTouchMode == Mode.FLING /*&& mScrollRunnable.mScrollToLeft*/) mScrollRunnable.abort()
            mScrollRunnable.startScroll(mScrollOffset, 0)
        }
    }

    fun fling(xVel: Int) {
        mScrollRunnable.startFling(mScrollOffset, xVel)
    }

    fun revise() {
        if (mScrollOffset < -mMaxScrollOffset / 2) open() else close()
    }

    fun trackMotionScroll(deltaX: Int): Boolean {
        if (deltaX == 0) return false
        var over = false
        var newLeft = mScrollOffset + deltaX
        if (deltaX > 0 && newLeft > 0 || deltaX < 0 && newLeft < -mMaxScrollOffset) {
            over = true
            newLeft = Math.min(newLeft, 0)
            newLeft = Math.max(newLeft, -mMaxScrollOffset)
        }
        offsetChildrenLeftAndRight(newLeft - mScrollOffset)
        mScrollOffset = newLeft
        return over
    }

    private fun ensureChildren(): Boolean {
        val childCount = childCount
        if (childCount != 2) return false
        var childView: View? = getChildAt(0) as? ViewGroup ?: return false
        mMainView = childView as ViewGroup
        childView = getChildAt(1)
        if (childView !is ViewGroup) return false
        mSideView = childView
        return true
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if (!ensureChildren()) throw RuntimeException("SwipeItemLayout的子视图不符合规定")
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        var widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        var heightSize = MeasureSpec.getSize(heightMeasureSpec)
        var lp: MarginLayoutParams? = null
        val horizontalMargin: Int
        var verticalMargin: Int
        val horizontalPadding = paddingLeft + paddingRight
        val verticalPadding = paddingTop + paddingBottom
        lp = mMainView!!.layoutParams as MarginLayoutParams
        horizontalMargin = lp.leftMargin + lp!!.rightMargin
        verticalMargin = lp.topMargin + lp.bottomMargin
        measureChildWithMargins(
            mMainView,
            widthMeasureSpec, horizontalMargin + horizontalPadding,
            heightMeasureSpec, verticalMargin + verticalPadding
        )
        if (widthMode == MeasureSpec.AT_MOST) widthSize = Math.min(
            widthSize,
            mMainView!!.measuredWidth + horizontalMargin + horizontalPadding
        ) else if (widthMode == MeasureSpec.UNSPECIFIED) widthSize =
            mMainView!!.measuredWidth + horizontalMargin + horizontalPadding
        if (heightMode == MeasureSpec.AT_MOST) heightSize = Math.min(
            heightSize,
            mMainView!!.measuredHeight + verticalMargin + verticalPadding
        ) else if (heightMode == MeasureSpec.UNSPECIFIED) heightSize =
            mMainView!!.measuredHeight + verticalMargin + verticalPadding
        setMeasuredDimension(widthSize, heightSize)

        //side layout大小为自身实际大小
        lp = mSideView!!.layoutParams as MarginLayoutParams
        verticalMargin = lp.topMargin + lp.bottomMargin
        mSideView!!.measure(
            MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
            MeasureSpec.makeMeasureSpec(
                measuredHeight - verticalMargin - verticalPadding,
                MeasureSpec.EXACTLY
            )
        )
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        if (!ensureChildren()) throw RuntimeException("SwipeItemLayout的子视图不符合规定")
        mInLayout = true
        val pl = paddingLeft
        val pt = paddingTop
        val pr = paddingRight
        val pb = paddingBottom
        val mainLp = mMainView!!.layoutParams as MarginLayoutParams
        val sideParams = mSideView!!.layoutParams as MarginLayoutParams
        var childLeft = pl + mainLp.leftMargin
        var childTop = pt + mainLp.topMargin
        var childRight = width - (pr + mainLp.rightMargin)
        var childBottom = height - (mainLp.bottomMargin + pb)
        mMainView!!.layout(childLeft, childTop, childRight, childBottom)
        childLeft = childRight + sideParams.leftMargin
        childTop = pt + sideParams.topMargin
        childRight =
            childLeft + sideParams.leftMargin + sideParams.rightMargin + mSideView!!.measuredWidth
        childBottom = height - (sideParams.bottomMargin + pb)
        mSideView!!.layout(childLeft, childTop, childRight, childBottom)
        mMaxScrollOffset = mSideView!!.width + sideParams.leftMargin + sideParams.rightMargin
        mScrollOffset = if (mScrollOffset < -mMaxScrollOffset / 2) -mMaxScrollOffset else 0
        offsetChildrenLeftAndRight(mScrollOffset)
        mInLayout = false
        mIsLaidOut = true
    }

    private fun offsetChildrenLeftAndRight(delta: Int) {
        ViewCompat.offsetLeftAndRight(mMainView!!, delta)
        ViewCompat.offsetLeftAndRight(mSideView!!, delta)
    }

    override fun requestLayout() {
        if (!mInLayout) {
            super.requestLayout()
        }
    }

    override fun generateDefaultLayoutParams(): LayoutParams {
        return MarginLayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
    }

    override fun generateLayoutParams(p: LayoutParams): LayoutParams {
        return p as? MarginLayoutParams ?: MarginLayoutParams(p)
    }

    override fun checkLayoutParams(p: LayoutParams): Boolean {
        return p is MarginLayoutParams && super.checkLayoutParams(p)
    }

    override fun generateLayoutParams(attrs: AttributeSet): LayoutParams {
        return MarginLayoutParams(context, attrs)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        mScrollOffset = if (mScrollOffset != 0 && mIsLaidOut) {
            offsetChildrenLeftAndRight(-mScrollOffset)
            0
        } else 0
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        mScrollOffset = if (mScrollOffset != 0 && mIsLaidOut) {
            offsetChildrenLeftAndRight(-mScrollOffset)
            0
        } else 0
        removeCallbacks(mScrollRunnable)
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        val action = ev.actionMasked
        when (action) {
            MotionEvent.ACTION_DOWN -> {
                val x = ev.x.toInt()
                val y = ev.y.toInt()
                val pointView = findTopChildUnder(this, x, y)
                if (pointView != null && pointView === mMainView && mScrollOffset != 0) return true
            }
            MotionEvent.ACTION_MOVE, MotionEvent.ACTION_CANCEL -> {
            }
            MotionEvent.ACTION_UP -> {
                val x = ev.x.toInt()
                val y = ev.y.toInt()
                val pointView = findTopChildUnder(this, x, y)
                if (pointView != null && pointView === mMainView && mTouchMode == Mode.TAP && mScrollOffset != 0) return true
            }
        }
        return false
    }

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        val action = ev.actionMasked
        when (action) {
            MotionEvent.ACTION_DOWN -> {
                val x = ev.x.toInt()
                val y = ev.y.toInt()
                val pointView = findTopChildUnder(this, x, y)
                if (pointView != null && pointView === mMainView && mScrollOffset != 0) return true
            }
            MotionEvent.ACTION_MOVE, MotionEvent.ACTION_CANCEL -> {
            }
            MotionEvent.ACTION_UP -> {
                val x = ev.x.toInt()
                val y = ev.y.toInt()
                val pointView = findTopChildUnder(this, x, y)
                if (pointView != null && pointView === mMainView && mTouchMode == Mode.TAP && mScrollOffset != 0) {
                    close()
                    return true
                }
            }
        }
        return false
    }

    override fun onVisibilityChanged(changedView: View, visibility: Int) {
        super.onVisibilityChanged(changedView, visibility)
        if (getVisibility() != VISIBLE) {
            mScrollOffset = 0
            invalidate()
        }
    }

    internal inner class ScrollRunnable(context: Context?) :
        Runnable {
        private val mScroller: Scroller
        private var mAbort: Boolean
        private val mMinVelocity: Int

        //是否正在滑动需要另外判断
        var isScrollToLeft: Boolean
            private set

        fun startScroll(startX: Int, endX: Int) {
            if (startX != endX) {
                //Log.e("scroll - startX - endX", "" + startX + " " + endX);
                touchMode = Mode.FLING
                mAbort = false
                isScrollToLeft = endX < startX
                mScroller.startScroll(startX, 0, endX - startX, 0, 400)
                ViewCompat.postOnAnimation(this@SwipeItemLayout, this)
            }
        }

        fun startFling(startX: Int, xVel: Int) {
            //Log.e("fling - startX", "" + startX);
            if (xVel > mMinVelocity && startX != 0) {
                startScroll(startX, 0)
                return
            }
            if (xVel < -mMinVelocity && startX != -mMaxScrollOffset) {
                startScroll(startX, -mMaxScrollOffset)
                return
            }
            startScroll(startX, if (startX > -mMaxScrollOffset / 2) 0 else -mMaxScrollOffset)
        }

        fun abort() {
            if (!mAbort) {
                mAbort = true
                if (!mScroller.isFinished) {
                    mScroller.abortAnimation()
                    removeCallbacks(this)
                }
            }
        }

        override fun run() {
            //Log.e("abort", Boolean.toString(mAbort));
            if (!mAbort) {
                val more = mScroller.computeScrollOffset()
                val curX = mScroller.currX
                //Log.e("curX", "" + curX);
                val atEdge = trackMotionScroll(curX - mScrollOffset)
                if (more && !atEdge) {
                    ViewCompat.postOnAnimation(this@SwipeItemLayout, this)
                    return
                }
                if (atEdge) {
                    removeCallbacks(this)
                    if (!mScroller.isFinished) mScroller.abortAnimation()
                    touchMode = Mode.RESET
                }
                if (!more) {
                    touchMode = Mode.RESET
                    //绝对不会出现这种意外的！！！可以注释掉
                    if (mScrollOffset != 0) {
                        mScrollOffset =
                            if (Math.abs(mScrollOffset) > mMaxScrollOffset / 2) -mMaxScrollOffset else 0
                        ViewCompat.postOnAnimation(this@SwipeItemLayout, this)
                    }
                }
            }
        }

        init {
            mScroller = Scroller(context, sInterpolator)
            mAbort = false
            isScrollToLeft = false
            val configuration = ViewConfiguration.get(context)
            mMinVelocity = configuration.scaledMinimumFlingVelocity
        }
    }

    class OnSwipeItemTouchListener(context: Context?) :
        OnItemTouchListener {
        private var mCaptureItem: SwipeItemLayout? = null
        private var mLastMotionX = 0f
        private var mLastMotionY = 0f
        private var mVelocityTracker: VelocityTracker? = null
        private var mActivePointerId: Int
        private val mTouchSlop: Int
        private val mMaximumVelocity: Int
        private var mDealByParent: Boolean
        private var mIsProbeParent: Boolean
        override fun onInterceptTouchEvent(rv: RecyclerView, ev: MotionEvent): Boolean {
            if (mIsProbeParent) return false
            var intercept = false
            val action = ev.actionMasked
            if (mVelocityTracker == null) {
                mVelocityTracker = VelocityTracker.obtain()
            }
            mVelocityTracker!!.addMovement(ev)
            when (action) {
                MotionEvent.ACTION_DOWN -> {
                    mActivePointerId = ev.getPointerId(0)
                    val x = ev.x
                    val y = ev.y
                    mLastMotionX = x
                    mLastMotionY = y
                    var pointOther = false
                    var pointItem: SwipeItemLayout? = null
                    //首先知道ev针对的是哪个item
                    val pointView = findTopChildUnder(
                        rv,
                        x.toInt(), y.toInt()
                    )
                    if (pointView == null || pointView !is SwipeItemLayout) {
                        //可能是head view或bottom view
                        pointOther = true
                    } else pointItem = pointView

                    //此时的pointOther=true，意味着点击的view为空或者点击的不是item
                    //还没有把点击的是item但是不是capture item给过滤出来
                    if (!pointOther && (mCaptureItem == null || mCaptureItem !== pointItem)) pointOther =
                        true

                    //点击的是capture item
                    if (!pointOther) {
                        val touchMode = mCaptureItem!!.touchMode

                        //如果它在fling，就转为drag
                        //需要拦截，并且requestDisallowInterceptTouchEvent
                        var disallowIntercept = false
                        if (touchMode == Mode.FLING) {
                            mCaptureItem!!.touchMode = Mode.DRAG
                            disallowIntercept = true
                            intercept = true
                        } else { //如果是expand的，就不允许parent拦截
                            mCaptureItem!!.touchMode = Mode.TAP
                            if (mCaptureItem!!.isOpen) disallowIntercept = true
                        }
                        if (disallowIntercept) {
                            val parent = rv.parent
                            parent?.requestDisallowInterceptTouchEvent(true)
                        }
                    } else { //capture item为null或者与point item不一样
                        //直接将其close掉
                        if (mCaptureItem != null && mCaptureItem!!.isOpen) {
                            mCaptureItem!!.close()
                            mCaptureItem = null
                            intercept = true
                        }
                        if (pointItem != null) {
                            mCaptureItem = pointItem
                            mCaptureItem!!.touchMode = Mode.TAP
                        } else mCaptureItem = null
                    }

                    //如果parent处于fling状态，此时，parent就会转为drag。此时，应该将后续move都交给parent处理
                    mIsProbeParent = true
                    mDealByParent = rv.onInterceptTouchEvent(ev)
                    mIsProbeParent = false
                    if (mDealByParent) intercept = false
                }
                MotionEvent.ACTION_POINTER_DOWN -> {
                    val actionIndex = ev.actionIndex
                    mActivePointerId = ev.getPointerId(actionIndex)
                    mLastMotionX = ev.getX(actionIndex)
                    mLastMotionY = ev.getY(actionIndex)
                }
                MotionEvent.ACTION_POINTER_UP -> {
                    val actionIndex = ev.actionIndex
                    val pointerId = ev.getPointerId(actionIndex)
                    if (pointerId == mActivePointerId) {
                        val newIndex = if (actionIndex == 0) 1 else 0
                        mActivePointerId = ev.getPointerId(newIndex)
                        mLastMotionX = ev.getX(newIndex)
                        mLastMotionY = ev.getY(newIndex)
                    }
                }
                MotionEvent.ACTION_MOVE -> {
                    val activePointerIndex = ev.findPointerIndex(mActivePointerId)
                    if (activePointerIndex == -1) return false

                    //在down时，就被认定为parent的drag，所以，直接交给parent处理即可
                    if (mDealByParent) {
                        if (mCaptureItem != null && mCaptureItem!!.isOpen) mCaptureItem!!.close()
                        return false
                    }
                    val x = (ev.getX(activePointerIndex) + .5f).toInt()
                    val y = (ev.getY(activePointerIndex).toInt() + .5f).toInt()
                    var deltaX = (x - mLastMotionX).toInt()
                    val deltaY = (y - mLastMotionY).toInt()
                    val xDiff = Math.abs(deltaX)
                    val yDiff = Math.abs(deltaY)
                    if (mCaptureItem != null && !mDealByParent) {
                        var touchMode = mCaptureItem!!.touchMode
                        if (touchMode == Mode.TAP) {
                            //如果capture item是open的，下拉有两种处理方式：
                            //  1、下拉后，直接close item
                            //  2、只要是open的，就拦截所有它的消息，这样如果点击open的，就只能滑动该capture item
                            //网易邮箱，在open的情况下，下拉直接close
                            //QQ，在open的情况下，下拉也是close。但是，做的不够好，没有达到该效果。
                            if (xDiff > mTouchSlop && xDiff > yDiff) {
                                mCaptureItem!!.touchMode = Mode.DRAG
                                val parent = rv.parent
                                parent.requestDisallowInterceptTouchEvent(true)
                                deltaX =
                                    if (deltaX > 0) deltaX - mTouchSlop else deltaX + mTouchSlop
                            } else { // if(yDiff>mTouchSlop){
                                mIsProbeParent = true
                                val isParentConsume = rv.onInterceptTouchEvent(ev)
                                mIsProbeParent = false
                                if (isParentConsume) {
                                    //表明不是水平滑动，即不判定为SwipeItemLayout的滑动
                                    //但是，可能是下拉刷新SwipeRefreshLayout或者RecyclerView的滑动
                                    //一般的下拉判定，都是yDiff>mTouchSlop，所以，此处这么写不会出问题
                                    //这里这么做以后，如果判定为下拉，就直接close
                                    mDealByParent = true
                                    mCaptureItem!!.close()
                                }
                            }
                        }
                        touchMode = mCaptureItem!!.touchMode
                        if (touchMode == Mode.DRAG) {
                            intercept = true
                            mLastMotionX = x.toFloat()
                            mLastMotionY = y.toFloat()

                            //对capture item进行拖拽
                            mCaptureItem!!.trackMotionScroll(deltaX)
                        }
                    }
                }
                MotionEvent.ACTION_UP -> {
                    if (mCaptureItem != null) {
                        val touchMode = mCaptureItem!!.touchMode
                        if (touchMode == Mode.DRAG) {
                            val velocityTracker = mVelocityTracker
                            velocityTracker!!.computeCurrentVelocity(
                                1000,
                                mMaximumVelocity.toFloat()
                            )
                            val xVel = velocityTracker.getXVelocity(mActivePointerId).toInt()
                            mCaptureItem!!.fling(xVel)
                            intercept = true
                        }
                    }
                    cancel()
                }
                MotionEvent.ACTION_CANCEL -> {
                    if (mCaptureItem != null) mCaptureItem!!.revise()
                    cancel()
                }
            }
            return intercept
        }

        override fun onTouchEvent(rv: RecyclerView, ev: MotionEvent) {
            val action = ev.actionMasked
            val actionIndex = ev.actionIndex
            if (mVelocityTracker == null) {
                mVelocityTracker = VelocityTracker.obtain()
            }
            mVelocityTracker!!.addMovement(ev)
            when (action) {
                MotionEvent.ACTION_POINTER_DOWN -> {
                    mActivePointerId = ev.getPointerId(actionIndex)
                    mLastMotionX = ev.getX(actionIndex)
                    mLastMotionY = ev.getY(actionIndex)
                }
                MotionEvent.ACTION_POINTER_UP -> {
                    val pointerId = ev.getPointerId(actionIndex)
                    if (pointerId == mActivePointerId) {
                        val newIndex = if (actionIndex == 0) 1 else 0
                        mActivePointerId = ev.getPointerId(newIndex)
                        mLastMotionX = ev.getX(newIndex)
                        mLastMotionY = ev.getY(newIndex)
                    }
                }
                MotionEvent.ACTION_MOVE -> {
                    val activePointerIndex = ev.findPointerIndex(mActivePointerId)
                    if (activePointerIndex == -1) return
                    val x = ev.getX(activePointerIndex)
                    val y: Float = ev.getY(activePointerIndex)
                    val deltaX = (x - mLastMotionX).toInt()
                    if (mCaptureItem != null && mCaptureItem!!.touchMode == Mode.DRAG) {
                        mLastMotionX = x
                        mLastMotionY = y

                        //对capture item进行拖拽
                        mCaptureItem!!.trackMotionScroll(deltaX)
                    }
                }
                MotionEvent.ACTION_UP -> {
                    if (mCaptureItem != null) {
                        val touchMode = mCaptureItem!!.touchMode
                        if (touchMode == Mode.DRAG) {
                            val velocityTracker = mVelocityTracker
                            velocityTracker!!.computeCurrentVelocity(
                                1000,
                                mMaximumVelocity.toFloat()
                            )
                            val xVel = velocityTracker.getXVelocity(mActivePointerId).toInt()
                            mCaptureItem!!.fling(xVel)
                        }
                    }
                    cancel()
                }
                MotionEvent.ACTION_CANCEL -> {
                    if (mCaptureItem != null) mCaptureItem!!.revise()
                    cancel()
                }
            }
        }

        override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {}
        fun cancel() {
            mDealByParent = false
            mActivePointerId = -1
            if (mVelocityTracker != null) {
                mVelocityTracker!!.recycle()
                mVelocityTracker = null
            }
        }

        init {
            val configuration = ViewConfiguration.get(context)
            mTouchSlop = configuration.scaledTouchSlop
            mMaximumVelocity = configuration.scaledMaximumFlingVelocity
            mActivePointerId = -1
            mDealByParent = false
            mIsProbeParent = false
        }
    }

    companion object {
        private val sInterpolator =
            Interpolator { t ->
                var t = t
                t -= 1.0f
                t * t * t * t * t + 1.0f
            }

        fun findTopChildUnder(parent: ViewGroup, x: Int, y: Int): View? {
            val childCount = parent.childCount
            for (i in childCount - 1 downTo 0) {
                val child = parent.getChildAt(i)
                if (x >= child.left && x < child.right && y >= child.top && y < child.bottom) {
                    return child
                }
            }
            return null
        }

        fun closeAllItems(recyclerView: RecyclerView) {
            for (i in 0 until recyclerView.childCount) {
                val child = recyclerView.getChildAt(i)
                if (child is SwipeItemLayout) {
                    val swipeItemLayout = child
                    if (swipeItemLayout.isOpen) swipeItemLayout.close()
                }
            }
        }
    }

    init {
        mTouchMode = Mode.RESET
        mScrollOffset = 0
        mIsLaidOut = false
        mScrollRunnable = ScrollRunnable(context)
    }
}