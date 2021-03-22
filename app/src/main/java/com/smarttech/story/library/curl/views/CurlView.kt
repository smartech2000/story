package com.smarttech.story.library.curl.views

import android.content.Context
import android.graphics.Bitmap
import android.graphics.PointF
import android.opengl.GLSurfaceView
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import com.smarttech.story.library.curl.models.PointerPosition
import com.smarttech.story.library.curl.implementations.SizeChangedObserver
import com.smarttech.story.library.curl.interfaces.IObserver
import com.smarttech.story.library.curl.interfaces.IPageProvider

class CurlView : GLSurfaceView, OnTouchListener, IObserver {
    private var mAllowLastPageCurl = true
    private var mAnimate = false
    private val mAnimationDurationTime: Long = 300
    private val mAnimationSource = PointF()
    private var mAnimationStartTime: Long = 0
    private val mAnimationTarget = PointF()
    private var mAnimationTargetEvent = 0
    private val mCurlDir = PointF()
    private val mCurlPos = PointF()
    private var mCurlState = CURL_NONE

    private var mCurrentIndex = 0

    private val mDragStartPos = PointF()
    private var mEnableTouchPressure = false

    private var mPageBitmapHeight = -1
    private var mPageBitmapWidth = -1

    private lateinit var mPageCurl: CurlMesh
    private lateinit var mPageLeft: CurlMesh
    private lateinit var mPageProvider: IPageProvider
    private lateinit var mPageRight: CurlMesh
    private val mPointerPos = PointerPosition()
    private lateinit var mRenderer: CurlRenderer
    private var mRenderLeftPage = true
    private var mSizeChangedObserver: SizeChangedObserver? = null

    private var mViewMode = SHOW_ONE_PAGE

    constructor(ctx: Context) : super(ctx) {
        init(ctx)
    }

    constructor(ctx: Context, attrs: AttributeSet?) : super(ctx, attrs) {
        init(ctx)
    }

    constructor(ctx: Context, attrs: AttributeSet?, defStyle: Int) : this(ctx, attrs) {}

    var currentIndex: Int
        get() = mCurrentIndex
        set(index) {
            mCurrentIndex = if (index < 0) {
                0
            } else {
                if (mAllowLastPageCurl) {
                    Math.min(index, mPageProvider.getPageCount())
                } else {
                    Math.min(
                        index,
                        mPageProvider.getPageCount() - 1
                    )
                }
            }
            updatePages()
            requestRender()
        }

    private fun init(ctx: Context) {
        mRenderer = CurlRenderer(this)
        setRenderer(mRenderer)
        renderMode = RENDERMODE_WHEN_DIRTY
        setOnTouchListener(this)

        mPageLeft = CurlMesh(10)
        mPageRight = CurlMesh(10)
        mPageCurl = CurlMesh(10)
        mPageLeft.setFlipTexture(true)
        mPageRight.setFlipTexture(false)
    }

    override fun onDrawFrame() {
        if (mAnimate == false) {
            return
        }
        val currentTime = System.currentTimeMillis()
        if (currentTime >= mAnimationStartTime + mAnimationDurationTime) {
            if (mAnimationTargetEvent == SET_CURL_TO_RIGHT) {
                val right = mPageCurl
                val curl = mPageRight
                right.setRect(mRenderer.getPageRect(CurlRenderer.PAGE_RIGHT))
                right.setFlipTexture(false)
                right.reset()
                mRenderer.removeCurlMesh(curl)
                mPageCurl = curl
                mPageRight = right
                if (mCurlState == CURL_LEFT) {
                    --mCurrentIndex
                }
            } else if (mAnimationTargetEvent == SET_CURL_TO_LEFT) {
                val left = mPageCurl
                val curl = mPageLeft
                left.setRect(mRenderer.getPageRect(CurlRenderer.PAGE_LEFT))
                left.setFlipTexture(true)
                left.reset()
                mRenderer.removeCurlMesh(curl)
                if (!mRenderLeftPage) {
                    mRenderer.removeCurlMesh(left)
                }
                mPageCurl = curl
                mPageLeft = left
                if (mCurlState == CURL_RIGHT) {
                    ++mCurrentIndex
                }
            }
            mCurlState = CURL_NONE
            mAnimate = false
            requestRender()
        } else {
            mPointerPos.mPos.set(mAnimationSource)
            var t = 1f - (currentTime - mAnimationStartTime).toFloat() / mAnimationDurationTime
            t = 1f - t * t * t * (3 - 2 * t)
            mPointerPos.mPos.x += (mAnimationTarget.x - mAnimationSource.x) * t
            mPointerPos.mPos.y += (mAnimationTarget.y - mAnimationSource.y) * t
            updateCurlPos(mPointerPos)
        }
    }

    override fun onPageSizeChanged(width: Int, height: Int) {
        mPageBitmapWidth = width
        mPageBitmapHeight = height
        updatePages()
        requestRender()
    }

    public override fun onSizeChanged(w: Int, h: Int, ow: Int, oh: Int) {
        super.onSizeChanged(w, h, ow, oh)
        requestRender()
        mSizeChangedObserver?.onSizeChanged(w, h)
    }

    override fun onSurfaceCreated() {
        mPageLeft.resetTexture()
        mPageRight.resetTexture()
        mPageCurl.resetTexture()
    }

    override fun onTouch(view: View, me: MotionEvent): Boolean {
        if (mAnimate) {
            return false
        }

        val rightRect = mRenderer.getPageRect(CurlRenderer.PAGE_RIGHT)
        val leftRect = mRenderer.getPageRect(CurlRenderer.PAGE_LEFT)

        mPointerPos.mPos[me.x] = me.y
        mRenderer.translate(mPointerPos.mPos)
        if (mEnableTouchPressure) {
            mPointerPos.mPressure = me.pressure
        } else {
            mPointerPos.mPressure = 0.8f
        }
        when (me.action) {
            MotionEvent.ACTION_DOWN -> {
                run {

                    mDragStartPos.set(mPointerPos.mPos)

                    if (mDragStartPos.y > rightRect.top) {
                        mDragStartPos.y = rightRect.top
                    } else if (mDragStartPos.y < rightRect.bottom) {
                        mDragStartPos.y = rightRect.bottom
                    }

                    if (mViewMode == SHOW_TWO_PAGES) {
                        if (mDragStartPos.x < rightRect.left && mCurrentIndex > 0) {
                            mDragStartPos.x = leftRect.left
                            startCurl(CURL_LEFT)
                        } else if (mDragStartPos.x >= rightRect.left
                            && mCurrentIndex < mPageProvider.getPageCount()
                        ) {
                            mDragStartPos.x = rightRect.right
                            if (!mAllowLastPageCurl
                                && mCurrentIndex >= mPageProvider.getPageCount() - 1
                            ) {
                                return false
                            }
                            startCurl(CURL_RIGHT)
                        }
                    } else if (mViewMode == SHOW_ONE_PAGE) {
                        val halfX = (rightRect.right + rightRect.left) / 2
                        if (mDragStartPos.x < halfX && mCurrentIndex > 0) {
                            mDragStartPos.x = rightRect.left
                            startCurl(CURL_LEFT)
                        } else if (mDragStartPos.x >= halfX
                            && mCurrentIndex < mPageProvider.getPageCount()
                        ) {
                            mDragStartPos.x = rightRect.right
                            if (!mAllowLastPageCurl
                                && mCurrentIndex >= mPageProvider.getPageCount() - 1
                            ) {
                                return false
                            }
                            startCurl(CURL_RIGHT)
                        }
                    }
                    if (mCurlState == CURL_NONE) {
                        return false
                    }
                }
                run { updateCurlPos(mPointerPos) }
            }
            MotionEvent.ACTION_MOVE -> {
                updateCurlPos(mPointerPos)
            }
            MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> {
                if (mCurlState == CURL_LEFT || mCurlState == CURL_RIGHT) {
                    mAnimationSource.set(mPointerPos.mPos)
                    mAnimationStartTime = System.currentTimeMillis()

                    if (mViewMode == SHOW_ONE_PAGE && mPointerPos.mPos.x > (rightRect.left + rightRect.right) / 2
                        || mViewMode == SHOW_TWO_PAGES
                        && mPointerPos.mPos.x > rightRect.left
                    ) {
                        mAnimationTarget.set(mDragStartPos)
                        mAnimationTarget.x = mRenderer
                            .getPageRect(CurlRenderer.PAGE_RIGHT).right
                        mAnimationTargetEvent = SET_CURL_TO_RIGHT
                    } else {
                        mAnimationTarget.set(mDragStartPos)
                        if (mCurlState == CURL_RIGHT || mViewMode == SHOW_TWO_PAGES) {
                            mAnimationTarget.x = leftRect.left
                        } else {
                            mAnimationTarget.x = rightRect.left
                        }
                        mAnimationTargetEvent = SET_CURL_TO_LEFT
                    }
                    mAnimate = true
                    requestRender()
                }
            }
        }
        return true
    }

    fun setAllowLastPageCurl(allowLastPageCurl: Boolean) {
        mAllowLastPageCurl = allowLastPageCurl
    }

    override fun setBackgroundColor(color: Int) {
        mRenderer.setBackgroundColor(color)
        requestRender()
    }
    fun setBackgroundImage(bm: Bitmap) {
        mRenderer.setBackgroundImage(bm)
        requestRender()
    }

    private fun setCurlPos(curlPos: PointF, curlDir: PointF, radius: Double) {

        if (mCurlState == CURL_RIGHT
            || mCurlState == CURL_LEFT && mViewMode == SHOW_ONE_PAGE
        ) {
            val pageRect = mRenderer.getPageRect(CurlRenderer.PAGE_RIGHT)
            if (curlPos.x >= pageRect.right) {
                mPageCurl.reset()
                requestRender()
                return
            }
            if (curlPos.x < pageRect.left) {
                curlPos.x = pageRect.left
            }
            if (curlDir.y != 0f) {
                val diffX = curlPos.x - pageRect.left
                val leftY = curlPos.y + diffX * curlDir.x / curlDir.y
                if (curlDir.y < 0 && leftY < pageRect.top) {
                    curlDir.x = curlPos.y - pageRect.top
                    curlDir.y = pageRect.left - curlPos.x
                } else if (curlDir.y > 0 && leftY > pageRect.bottom) {
                    curlDir.x = pageRect.bottom - curlPos.y
                    curlDir.y = curlPos.x - pageRect.left
                }
            }
        } else if (mCurlState == CURL_LEFT) {
            val pageRect = mRenderer.getPageRect(CurlRenderer.PAGE_LEFT)
            if (curlPos.x <= pageRect.left) {
                mPageCurl.reset()
                requestRender()
                return
            }
            if (curlPos.x > pageRect.right) {
                curlPos.x = pageRect.right
            }
            if (curlDir.y != 0f) {
                val diffX = curlPos.x - pageRect.right
                val rightY = curlPos.y + diffX * curlDir.x / curlDir.y
                if (curlDir.y < 0 && rightY < pageRect.top) {
                    curlDir.x = pageRect.top - curlPos.y
                    curlDir.y = curlPos.x - pageRect.right
                } else if (curlDir.y > 0 && rightY > pageRect.bottom) {
                    curlDir.x = curlPos.y - pageRect.bottom
                    curlDir.y = pageRect.right - curlPos.x
                }
            }
        }

        val dist = Math.sqrt((curlDir.x * curlDir.x + curlDir.y * curlDir.y).toDouble())
        if (dist != 0.0) {
            curlDir.x /= dist.toFloat()
            curlDir.y /= dist.toFloat()
            mPageCurl.curl(curlPos, curlDir, radius)
        } else {
            mPageCurl.reset()
        }
        requestRender()
    }

    fun setEnableTouchPressure(enableTouchPressure: Boolean) {
        mEnableTouchPressure = enableTouchPressure
    }

    fun setMargins(left: Float, top: Float, right: Float, bottom: Float) {
        mRenderer.setMargins(left, top, right, bottom)
    }

    fun setPageProvider(pageProvider: IPageProvider) {
        mPageProvider = pageProvider
        movePage(0)
    }

    fun movePage(index : Int){
        mCurrentIndex = index
        updatePages()
        requestRender()
    }

    fun setRenderLeftPage(renderLeftPage: Boolean) {
        mRenderLeftPage = renderLeftPage
    }

    fun setSizeChangedObserver(observer: SizeChangedObserver?) {
        mSizeChangedObserver = observer
    }

    fun setViewMode(viewMode: Int) {
        when (viewMode) {
            SHOW_ONE_PAGE -> {
                mViewMode = viewMode
                mPageLeft.setFlipTexture(true)
                mRenderer.setViewMode(CurlRenderer.SHOW_ONE_PAGE)
            }
            SHOW_TWO_PAGES -> {
                mViewMode = viewMode
                mPageLeft.setFlipTexture(false)
                mRenderer.setViewMode(CurlRenderer.SHOW_TWO_PAGES)
            }
        }
    }

    private fun curlRight() {
        mRenderer.removeCurlMesh(mPageLeft)
        mRenderer.removeCurlMesh(mPageRight)
        mRenderer.removeCurlMesh(mPageCurl)

        val curl = mPageRight
        mPageRight = mPageCurl
        mPageCurl = curl
        if (mCurrentIndex > 0) {
            mPageLeft.setFlipTexture(true)
            mPageLeft
                .setRect(mRenderer.getPageRect(CurlRenderer.PAGE_LEFT))
            mPageLeft.reset()
            if (mRenderLeftPage) {
                mRenderer.addCurlMesh(mPageLeft)
            }
        }
        if (mCurrentIndex < mPageProvider.getPageCount() - 1) {
            updatePage(mPageRight.texturePage, mCurrentIndex + 1)
            mPageRight.setRect(
                mRenderer
                    .getPageRect(CurlRenderer.PAGE_RIGHT)
            )
            mPageRight.setFlipTexture(false)
            mPageRight.reset()
            mRenderer.addCurlMesh(mPageRight)
        }

        mPageCurl.setRect(mRenderer.getPageRect(CurlRenderer.PAGE_RIGHT))
        mPageCurl.setFlipTexture(false)
        mPageCurl.reset()
        mRenderer.addCurlMesh(mPageCurl)
        mCurlState = CURL_RIGHT
    }

    private fun curlLeft() {
        mRenderer.removeCurlMesh(mPageLeft)
        mRenderer.removeCurlMesh(mPageRight)
        mRenderer.removeCurlMesh(mPageCurl)

        val curl = mPageLeft
        mPageLeft = mPageCurl
        mPageCurl = curl
        if (mCurrentIndex > 1) {
            updatePage(mPageLeft.texturePage, mCurrentIndex - 2)
            mPageLeft.setFlipTexture(true)
            mPageLeft
                .setRect(mRenderer.getPageRect(CurlRenderer.PAGE_LEFT))
            mPageLeft.reset()
            if (mRenderLeftPage) {
                mRenderer.addCurlMesh(mPageLeft)
            }
        }

        if (mCurrentIndex < mPageProvider.getPageCount()) {
            mPageRight.setFlipTexture(false)
            mPageRight.setRect(
                mRenderer
                    .getPageRect(CurlRenderer.PAGE_RIGHT)
            )
            mPageRight.reset()
            mRenderer.addCurlMesh(mPageRight)
        }

        if (mViewMode == SHOW_ONE_PAGE
            || mCurlState == CURL_LEFT && mViewMode == SHOW_TWO_PAGES
        ) {
            mPageCurl.setRect(
                mRenderer
                    .getPageRect(CurlRenderer.PAGE_RIGHT)
            )
            mPageCurl.setFlipTexture(false)
        } else {
            mPageCurl
                .setRect(mRenderer.getPageRect(CurlRenderer.PAGE_LEFT))
            mPageCurl.setFlipTexture(true)
        }
        mPageCurl.reset()
        mRenderer.addCurlMesh(mPageCurl)
        mCurlState = CURL_LEFT
    }

    private fun startCurl(page: Int) {
        when (page) {
            CURL_RIGHT -> curlRight()
            CURL_LEFT -> curlLeft()
        }
    }

    /**
     * Updates curl position.
     */
    private fun updateCurlPos(pointerPos: PointerPosition) {

        var radius = (mRenderer.getPageRect(CURL_RIGHT).width() / 3).toDouble()
        radius *= Math.max(1f - pointerPos.mPressure, 0f).toDouble()
        mCurlPos.set(pointerPos.mPos)

        if (mCurlState == CURL_RIGHT
            || mCurlState == CURL_LEFT && mViewMode == SHOW_TWO_PAGES
        ) {
            mCurlDir.x = mCurlPos.x - mDragStartPos.x
            mCurlDir.y = mCurlPos.y - mDragStartPos.y
            val dist = Math.sqrt(
                (mCurlDir.x * mCurlDir.x + mCurlDir.y
                        * mCurlDir.y).toDouble()
            ).toFloat()

            val pageWidth = mRenderer.getPageRect(CurlRenderer.PAGE_RIGHT)
                .width()
            var curlLen = radius * Math.PI
            if (dist > pageWidth * 2 - curlLen) {
                curlLen = Math.max(pageWidth * 2 - dist, 0f).toDouble()
                radius = curlLen / Math.PI
            }

            if (dist >= curlLen) {
                val translate = (dist - curlLen) / 2
                if (mViewMode == SHOW_TWO_PAGES) {
                    mCurlPos.x -= (mCurlDir.x * translate / dist).toFloat()
                } else {
                    val pageLeftX = mRenderer
                        .getPageRect(CurlRenderer.PAGE_RIGHT).left
                    radius = Math.max(Math.min((mCurlPos.x - pageLeftX).toDouble(), radius), 0.0)
                }
                mCurlPos.y -= (mCurlDir.y * translate / dist).toFloat()
            } else {
                val angle = Math.PI * Math.sqrt(dist / curlLen)
                val translate = radius * Math.sin(angle)
                mCurlPos.x += (mCurlDir.x * translate / dist).toFloat()
                mCurlPos.y += (mCurlDir.y * translate / dist).toFloat()
            }
        } else if (mCurlState == CURL_LEFT) {

            val pageLeftX = mRenderer.getPageRect(CurlRenderer.PAGE_RIGHT).left
            radius = Math.max(Math.min((mCurlPos.x - pageLeftX).toDouble(), radius), 0.0)
            val pageRightX = mRenderer.getPageRect(CurlRenderer.PAGE_RIGHT).right
            mCurlPos.x -= Math.min((pageRightX - mCurlPos.x).toDouble(), radius).toFloat()
            mCurlDir.x = mCurlPos.x + mDragStartPos.x
            mCurlDir.y = mCurlPos.y - mDragStartPos.y
        }
        setCurlPos(mCurlPos, mCurlDir, radius)
    }

    private fun updatePage(page: CurlPage, index: Int) {
        page.reset()
        mPageProvider.updatePage(
            page, mPageBitmapWidth, mPageBitmapHeight,
            index
        )
    }

    private fun updatePages() {
        if (mPageBitmapWidth <= 0 || mPageBitmapHeight <= 0) {
            return
        }

        mRenderer.removeCurlMesh(mPageLeft)
        mRenderer.removeCurlMesh(mPageRight)
        mRenderer.removeCurlMesh(mPageCurl)
        var leftIdx = mCurrentIndex - 1
        var rightIdx = mCurrentIndex
        var curlIdx = -1
        if (mCurlState == CURL_LEFT) {
            curlIdx = leftIdx
            --leftIdx
        } else if (mCurlState == CURL_RIGHT) {
            curlIdx = rightIdx
            ++rightIdx
        }
        if (rightIdx >= 0 && rightIdx < mPageProvider.getPageCount()) {
            updatePage(mPageRight.texturePage, rightIdx)
            mPageRight.setFlipTexture(false)
            mPageRight.setRect(mRenderer.getPageRect(CurlRenderer.PAGE_RIGHT))
            mPageRight.reset()
            mRenderer.addCurlMesh(mPageRight)
        }
        if (leftIdx >= 0 && leftIdx < mPageProvider.getPageCount()) {
            updatePage(mPageLeft.texturePage, leftIdx)
            mPageLeft.setFlipTexture(true)
            mPageLeft.setRect(mRenderer.getPageRect(CurlRenderer.PAGE_LEFT))
            mPageLeft.reset()
            if (mRenderLeftPage) {
                mRenderer.addCurlMesh(mPageLeft)
            }
        }
        if (curlIdx >= 0 && curlIdx < mPageProvider.getPageCount()) {
            updatePage(mPageCurl.texturePage, curlIdx)
            if (mCurlState == CURL_RIGHT) {
                mPageCurl.setFlipTexture(true)
                mPageCurl.setRect(
                    mRenderer
                        .getPageRect(CurlRenderer.PAGE_RIGHT)
                )
            } else {
                mPageCurl.setFlipTexture(false)
                mPageCurl.setRect(mRenderer.getPageRect(CurlRenderer.PAGE_LEFT))
            }
            mPageCurl.reset()
            mRenderer.addCurlMesh(mPageCurl)
        }
    }

    companion object {
        private const val CURL_LEFT = 1
        private const val CURL_NONE = 0
        private const val CURL_RIGHT = 2

        private const val SET_CURL_TO_LEFT = 1
        private const val SET_CURL_TO_RIGHT = 2

        const val SHOW_ONE_PAGE = 1

        const val SHOW_TWO_PAGES = 2
    }
}