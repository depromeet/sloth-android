package com.depromeet.sloth.ui.custom

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.TypedValue
import android.widget.ProgressBar
import com.depromeet.sloth.R
import android.graphics.RectF
import android.graphics.PorterDuff
import android.graphics.Bitmap
import com.depromeet.sloth.util.ContextUtil

/**
 * @author 최철훈
 * @created 2022-03-08
 * @desc 반원 프로그래스바
 */
class ArcProgressBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ProgressBar(context, attrs, defStyleAttr) {

    private val DEFAULT_LINEHEIGHT = ContextUtil.dpToPx(context,15)
    private val DEFAULT_mTickWidth = ContextUtil.dpToPx(context,2)
    private val DEFAULT_mRadius = ContextUtil.dpToPx(context,72)
    private val DEFAULT_mUnmProgressColor = -0x151516
    private val DEFAULT_mProgressColor = Color.YELLOW
    private val DEFAULT_OFFSETDEGREE = 60
    private val DEFAULT_DENSITY = 4
    private val MIN_DENSITY = 2
    private val MAX_DENSITY = 8
    private var mStyleProgreess = 0
    private val mBgShow: Boolean
    private val mRadius: Float
    private val mArcbgColor: Int
    private val mBoardWidth: Int
    private var mDegree = DEFAULT_OFFSETDEGREE
    private var mArcRectf: RectF? = null
    private val mLinePaint: Paint
    private val mArcPaint: Paint
    private val mUnmProgressColor: Int
    private val mProgressColor: Int
    private val mTickWidth: Int
    private var mTickDensity: Int
    private var mCenterBitmap: Bitmap? = null
    private var mCenterCanvas: Canvas? = null
    private var mOnCenter: OnCenterDraw? = null

    init {
        val attributes = getContext().obtainStyledAttributes(attrs, R.styleable.ArcProgressBar)
        mBoardWidth = attributes.getDimensionPixelOffset(R.styleable.ArcProgressBar_borderWidth, DEFAULT_LINEHEIGHT)
        mProgressColor = attributes.getColor(R.styleable.ArcProgressBar_progressColor, DEFAULT_mProgressColor)
        mUnmProgressColor = attributes.getColor(R.styleable.ArcProgressBar_unprogresColor, DEFAULT_mUnmProgressColor)
        mTickWidth = attributes.getDimensionPixelOffset(R.styleable.ArcProgressBar_tickWidth, DEFAULT_mTickWidth)
        mTickDensity = attributes.getInt(R.styleable.ArcProgressBar_tickDensity, DEFAULT_DENSITY)
        mRadius = attributes.getDimensionPixelOffset(R.styleable.ArcProgressBar_radius, DEFAULT_mRadius).toFloat()
        mArcbgColor = attributes.getColor(R.styleable.ArcProgressBar_arcbgColor, DEFAULT_mUnmProgressColor)
        mTickDensity = Math.max(Math.min(mTickDensity, MAX_DENSITY), MIN_DENSITY)
        mBgShow = attributes.getBoolean(R.styleable.ArcProgressBar_bgShow, false)
        mDegree = attributes.getInt(R.styleable.ArcProgressBar_degree, DEFAULT_OFFSETDEGREE)
        mStyleProgreess = attributes.getInt(R.styleable.ArcProgressBar_progressStyle, 0)
        val capRound = attributes.getBoolean(R.styleable.ArcProgressBar_arcCapRound, false)
        mArcPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mArcPaint.color = mArcbgColor
        mArcPaint.style = Paint.Style.STROKE
        if (capRound) mArcPaint.strokeCap = Paint.Cap.ROUND // 프로그래스 마지막지점 처리
        mArcPaint.strokeWidth = mBoardWidth.toFloat() // 프로그래스 두께 설정
        mLinePaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mLinePaint.strokeWidth = mTickWidth.toFloat()
    }

    @Synchronized
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var widthMeasureSpec = widthMeasureSpec
        var heightMeasureSpec = heightMeasureSpec
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        if (widthMode != MeasureSpec.EXACTLY) {
            val widthSize = (mRadius * 2 + mBoardWidth * 2).toInt()
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(widthSize, MeasureSpec.EXACTLY)
        }
        if (heightMode != MeasureSpec.EXACTLY) {
            val heightSize = (mRadius * 2 + mBoardWidth * 2).toInt()
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(heightSize, MeasureSpec.EXACTLY)
        }

        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    @Synchronized
    override fun onDraw(canvas: Canvas) {
        val roate = progress * 1.0f / max
        val x = mArcRectf!!.right / 2 + mBoardWidth / 2
        val y = mArcRectf!!.right / 2 + mBoardWidth / 2

        if (mOnCenter != null) {
            if (mCenterCanvas == null) {
                mCenterBitmap = Bitmap.createBitmap(
                    mRadius.toInt() * 2,
                    mRadius.toInt() * 2,
                    Bitmap.Config.ARGB_8888
                )
                mCenterCanvas = Canvas(mCenterBitmap!!)
            }
            mCenterCanvas!!.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
            mOnCenter!!.draw(mCenterCanvas, mArcRectf, x, y, mBoardWidth.toFloat(), progress)
            canvas.drawBitmap(mCenterBitmap!!, 0f, 0f, null)
        }

        val angle = mDegree / 2
        val targetDegree = (300 - mDegree) * roate

        mArcPaint.color = mUnmProgressColor
        canvas.drawArc(mArcRectf!!, 120 + angle + targetDegree, 300 - mDegree - targetDegree , false, mArcPaint)
        mArcPaint.color = mProgressColor
        canvas.drawArc(mArcRectf!!, (120 + angle).toFloat(), targetDegree, false, mArcPaint)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        mArcRectf = RectF(
            mBoardWidth.toFloat(),
            mBoardWidth.toFloat(),
            mRadius * 2 - mBoardWidth,
            mRadius * 2 - mBoardWidth
        )
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()

        if (mCenterBitmap != null) {
            mCenterBitmap!!.recycle()
            mCenterBitmap = null
        }
    }

    fun setOnCenterDraw(mOnCenter: OnCenterDraw?) {
        this.mOnCenter = mOnCenter
    }

    interface OnCenterDraw {
        fun draw(
            canvas: Canvas?,
            rectF: RectF?,
            x: Float,
            y: Float,
            storkeWidth: Float,
            progress: Int
        )
    }
}