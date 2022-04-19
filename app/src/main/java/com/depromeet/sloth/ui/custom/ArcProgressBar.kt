package com.depromeet.sloth.ui.custom

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.widget.ProgressBar
import com.depromeet.sloth.R
import android.graphics.RectF
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
    private val DEFAULT_RADIUS = ContextUtil.dpToPx(context,72)
    private val DEFAULT_mUnmProgressColor = -0x151516
    private val DEFAULT_mProgressColor = Color.YELLOW
    private val DEFAULT_OFFSETDEGREE = 60

    private var mRadius: Float = DEFAULT_RADIUS.toFloat()
    private var mArcBackgroundColor: Int = DEFAULT_mUnmProgressColor
    private var mUnmProgressColor: Int =DEFAULT_mUnmProgressColor
    private var mProgressColor: Int = DEFAULT_mProgressColor
    private var mBoardWidth: Int = DEFAULT_LINEHEIGHT
    private var mDegree = DEFAULT_OFFSETDEGREE

    private var mArcPaint: Paint? = null
    private var mArcRectF: RectF? = null
    private var isCapRound = false

    init {
        setAttributeSet(attrs)
        setArcPaint()
    }

    private fun setAttributeSet(attrs: AttributeSet?) {
        val attributes = context.obtainStyledAttributes(attrs, R.styleable.ArcProgressBar)

        mBoardWidth = attributes.getDimensionPixelOffset(R.styleable.ArcProgressBar_borderWidth, DEFAULT_LINEHEIGHT)
        mProgressColor = attributes.getColor(R.styleable.ArcProgressBar_progressColor, DEFAULT_mProgressColor)
        mUnmProgressColor = attributes.getColor(R.styleable.ArcProgressBar_unprogresColor, DEFAULT_mUnmProgressColor)

        mRadius = attributes.getDimensionPixelOffset(R.styleable.ArcProgressBar_radius, DEFAULT_RADIUS).toFloat()
        mArcBackgroundColor = attributes.getColor(R.styleable.ArcProgressBar_arcbgColor, DEFAULT_mUnmProgressColor)

        mDegree = attributes.getInt(R.styleable.ArcProgressBar_degree, DEFAULT_OFFSETDEGREE)
        isCapRound = attributes.getBoolean(R.styleable.ArcProgressBar_capRound, false)
    }

    private fun setArcPaint() {
        mArcPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = mArcBackgroundColor
            style = Paint.Style.STROKE
            strokeWidth = mBoardWidth.toFloat() // 프로그래스 바 두께 설정
            if(isCapRound) strokeCap = Paint.Cap.ROUND // 프로그래스 바 엣지 ROUND 처리
        }
    }

    @Synchronized
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var mWidthMeasureSpec = widthMeasureSpec
        var mHeightMeasureSpec = heightMeasureSpec
        val widthMode = MeasureSpec.getMode(mWidthMeasureSpec)
        val heightMode = MeasureSpec.getMode(mHeightMeasureSpec)

        if (widthMode != MeasureSpec.EXACTLY) {
            val widthSize = (mRadius * 2 + mBoardWidth * 2).toInt()
            mWidthMeasureSpec = MeasureSpec.makeMeasureSpec(widthSize, MeasureSpec.EXACTLY)
        }
        if (heightMode != MeasureSpec.EXACTLY) {
            val heightSize = (mRadius * 2 + mBoardWidth * 2).toInt()
            mHeightMeasureSpec = MeasureSpec.makeMeasureSpec(heightSize, MeasureSpec.EXACTLY)
        }

        super.onMeasure(mWidthMeasureSpec, mHeightMeasureSpec)
    }

    @Synchronized
    override fun onDraw(canvas: Canvas) {
        val roate = progress / max.toFloat()
        val angle = mDegree / 2
        val targetDegree = (300 - mDegree) * roate

        mArcPaint?.run {
            color = mUnmProgressColor
            canvas.drawArc(mArcRectF!!, 120 + angle + targetDegree, 300 - mDegree - targetDegree , false, this)

            color = mProgressColor
            canvas.drawArc(mArcRectF!!, (120 + angle).toFloat(), targetDegree, false, this)
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        mArcRectF = RectF(
            mBoardWidth.toFloat(),
            mBoardWidth.toFloat(),
            mRadius * 2 - mBoardWidth,
            mRadius * 2 - mBoardWidth
        )
    }
}