package com.smarttech.story.library.curl.views

import android.graphics.Color
import android.graphics.PointF
import android.graphics.RectF
import android.opengl.GLSurfaceView
import android.opengl.GLU
import com.smarttech.story.library.curl.interfaces.IObserver
import java.util.*
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class CurlRenderer(private val mObserver: IObserver) : GLSurfaceView.Renderer {
    private var mBackgroundColor = 0
    private val mCurlMeshes: Vector<CurlMesh> = Vector()
    private val mMargins = RectF()

    private val mPageRectLeft: RectF
    private val mPageRectRight: RectF

    private var mViewMode = SHOW_ONE_PAGE

    private var mViewportWidth = 0
    private var mViewportHeight = 0

    private val mViewRect = RectF()

    @Synchronized
    fun addCurlMesh(mesh: CurlMesh) {
        removeCurlMesh(mesh)
        mCurlMeshes.add(mesh)
    }

    fun getPageRect(page: Int): RectF {
        return when(page){
            PAGE_LEFT -> mPageRectLeft
            else -> mPageRectRight
        }
    }

    @Synchronized
    override fun onDrawFrame(gl: GL10) {
        mObserver.onDrawFrame()
        gl.glClearColor(
            Color.red(mBackgroundColor) / 255f,
            Color.green(mBackgroundColor) / 255f,
            Color.blue(mBackgroundColor) / 255f,
            Color.alpha(mBackgroundColor) / 255f
        )
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT)
        gl.glLoadIdentity()
        if (USE_PERSPECTIVE_PROJECTION) {
            gl.glTranslatef(0f, 0f, -6f)
        }
        for (i in mCurlMeshes.indices) {
            mCurlMeshes[i].onDrawFrame(gl)
        }
    }

    override fun onSurfaceChanged(gl: GL10, width: Int, height: Int) {
        gl.glViewport(0, 0, width, height)
        mViewportWidth = width
        mViewportHeight = height
        val ratio = width.toFloat() / height
        mViewRect.top = 1.0f
        mViewRect.bottom = -1.0f
        mViewRect.left = -ratio
        mViewRect.right = ratio
        updatePageRects()
        gl.glMatrixMode(GL10.GL_PROJECTION)
        gl.glLoadIdentity()
        if (USE_PERSPECTIVE_PROJECTION) {
            GLU.gluPerspective(gl, 20f, width.toFloat() / height, .1f, 100f)
        } else {
            GLU.gluOrtho2D(
                gl, mViewRect.left, mViewRect.right,
                mViewRect.bottom, mViewRect.top
            )
        }
        gl.glMatrixMode(GL10.GL_MODELVIEW)
        gl.glLoadIdentity()
    }

    override fun onSurfaceCreated(gl: GL10, config: EGLConfig) {
        gl.glClearColor(0f, 0f, 0f, 1f)
        gl.glShadeModel(GL10.GL_SMOOTH)
        gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST)
        gl.glHint(GL10.GL_LINE_SMOOTH_HINT, GL10.GL_NICEST)
        gl.glHint(GL10.GL_POLYGON_SMOOTH_HINT, GL10.GL_NICEST)
        gl.glEnable(GL10.GL_LINE_SMOOTH)
        gl.glDisable(GL10.GL_DEPTH_TEST)
        gl.glDisable(GL10.GL_CULL_FACE)
        mObserver.onSurfaceCreated()
    }

    @Synchronized
    fun removeCurlMesh(mesh: CurlMesh) {
        while (mCurlMeshes.remove(mesh));
    }

    fun setBackgroundColor(color: Int) {
        mBackgroundColor = color
    }

    @Synchronized
    fun setMargins(
        left: Float, top: Float, right: Float,
        bottom: Float
    ) {
        mMargins.left = left
        mMargins.top = top
        mMargins.right = right
        mMargins.bottom = bottom
        updatePageRects()
    }

    @Synchronized
    fun setViewMode(viewmode: Int) {
        if (viewmode == SHOW_ONE_PAGE) {
            mViewMode = viewmode
            updatePageRects()
        } else if (viewmode == SHOW_TWO_PAGES) {
            mViewMode = viewmode
            updatePageRects()
        }
    }

    fun translate(pt: PointF) {
        pt.x = mViewRect.left + mViewRect.width() * pt.x / mViewportWidth
        pt.y = mViewRect.top - -mViewRect.height() * pt.y / mViewportHeight
    }

    private fun updatePageRects() {
        if (mViewRect.width() == 0f || mViewRect.height() == 0f) {
            return
        } else if (mViewMode == SHOW_ONE_PAGE) {
            mPageRectRight.set(mViewRect)
            mPageRectRight.left += mViewRect.width() * mMargins.left
            mPageRectRight.right -= mViewRect.width() * mMargins.right
            mPageRectRight.top += mViewRect.height() * mMargins.top
            mPageRectRight.bottom -= mViewRect.height() * mMargins.bottom
            mPageRectLeft.set(mPageRectRight)
            mPageRectLeft.offset(-mPageRectRight.width(), 0f)
            val bitmapW = (mPageRectRight.width() * mViewportWidth / mViewRect
                .width()).toInt()
            val bitmapH = (mPageRectRight.height() * mViewportHeight / mViewRect
                .height()).toInt()
            mObserver.onPageSizeChanged(bitmapW, bitmapH)
        } else if (mViewMode == SHOW_TWO_PAGES) {
            mPageRectRight.set(mViewRect)
            mPageRectRight.left += mViewRect.width() * mMargins.left
            mPageRectRight.right -= mViewRect.width() * mMargins.right
            mPageRectRight.top += mViewRect.height() * mMargins.top
            mPageRectRight.bottom -= mViewRect.height() * mMargins.bottom
            mPageRectLeft.set(mPageRectRight)
            mPageRectLeft.right = (mPageRectLeft.right + mPageRectLeft.left) / 2
            mPageRectRight.left = mPageRectLeft.right
            val bitmapW = (mPageRectRight.width() * mViewportWidth / mViewRect
                .width()).toInt()
            val bitmapH = (mPageRectRight.height() * mViewportHeight / mViewRect
                .height()).toInt()
            mObserver.onPageSizeChanged(bitmapW, bitmapH)
        }
    }

    companion object {
        const val PAGE_LEFT = 1
        const val PAGE_RIGHT = 2
        const val SHOW_ONE_PAGE = 1
        const val SHOW_TWO_PAGES = 2
        private const val USE_PERSPECTIVE_PROJECTION = false
    }

    init {
        mPageRectLeft = RectF()
        mPageRectRight = RectF()
    }
}