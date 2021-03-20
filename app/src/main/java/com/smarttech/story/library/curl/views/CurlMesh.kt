package com.smarttech.story.library.curl.views

import android.graphics.Color
import android.graphics.PointF
import android.graphics.RectF
import android.opengl.GLUtils
import com.smarttech.story.library.curl.utils.Array
import com.smarttech.story.library.curl.implementations.ShadowVertex
import com.smarttech.story.library.curl.models.Vertex
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.opengles.GL10

class CurlMesh(maxCurlSplits: Int) {
    private lateinit var mArrDropShadowVertices: Array<ShadowVertex>
    private val mArrIntersections: Array<Vertex>
    private val mArrOutputVertices: Array<Vertex>
    private val mArrRotatedVertices: Array<Vertex>
    private val mArrScanLines: Array<Double>
    private var mArrSelfShadowVertices: Array<ShadowVertex>? = null
    private lateinit var mArrTempShadowVertices: Array<ShadowVertex>
    private val mArrTempVertices: Array<Vertex>

    private val mBufColors: FloatBuffer
    private lateinit var mBufCurlPositionLines: FloatBuffer
    private lateinit var mBufShadowColors: FloatBuffer
    private lateinit var mBufShadowVertices: FloatBuffer
    private var mBufTexCoords: FloatBuffer? = null
    private val mBufVertices: FloatBuffer
    private var mCurlPositionLinesCount = 0
    private var mDropShadowCount = 0

    private var mFlipTexture = false

    private val mMaxCurlSplits: Int

    private val mRectangle = arrayOfNulls<Vertex>(4)
    private var mSelfShadowCount = 0
    private var mTextureBack = false

    private var mTextureIds: IntArray? = null

    @get:Synchronized
    val texturePage = CurlPage()
    private val mTextureRectBack = RectF()
    private val mTextureRectFront = RectF()
    private var mVerticesCountBack = 0
    private var mVerticesCountFront = 0

    private fun addVertex(vertex: Vertex) {
        mBufVertices.put(vertex.mPosX.toFloat())
        mBufVertices.put(vertex.mPosY.toFloat())
        mBufVertices.put(vertex.mPosZ.toFloat())
        mBufColors.put(vertex.mColorFactor * Color.red(vertex.mColor) / 255f)
        mBufColors.put(vertex.mColorFactor * Color.green(vertex.mColor) / 255f)
        mBufColors.put(vertex.mColorFactor * Color.blue(vertex.mColor) / 255f)
        mBufColors.put(Color.alpha(vertex.mColor) / 255f)
        if (DRAW_TEXTURE) {
            mBufTexCoords!!.put(vertex.mTexX.toFloat())
            mBufTexCoords!!.put(vertex.mTexY.toFloat())
        }
    }

    @Synchronized
    fun curl(curlPos: PointF, curlDir: PointF, radius: Double) {

        if (DRAW_CURL_POSITION) {
            mBufCurlPositionLines.position(0)
            mBufCurlPositionLines.put(curlPos.x)
            mBufCurlPositionLines.put(curlPos.y - 1.0f)
            mBufCurlPositionLines.put(curlPos.x)
            mBufCurlPositionLines.put(curlPos.y + 1.0f)
            mBufCurlPositionLines.put(curlPos.x - 1.0f)
            mBufCurlPositionLines.put(curlPos.y)
            mBufCurlPositionLines.put(curlPos.x + 1.0f)
            mBufCurlPositionLines.put(curlPos.y)
            mBufCurlPositionLines.put(curlPos.x)
            mBufCurlPositionLines.put(curlPos.y)
            mBufCurlPositionLines.put(curlPos.x + curlDir.x * 2)
            mBufCurlPositionLines.put(curlPos.y + curlDir.y * 2)
            mBufCurlPositionLines.position(0)
        }

        mBufVertices.position(0)
        mBufColors.position(0)
        if (DRAW_TEXTURE) {
            mBufTexCoords!!.position(0)
        }

        var curlAngle = Math.acos(curlDir.x.toDouble())
        curlAngle = if (curlDir.y > 0) -curlAngle else curlAngle

        mArrTempVertices.addAll(mArrRotatedVertices)
        mArrRotatedVertices.clear()
        for (i in 0..3) {
            val v = mArrTempVertices.remove(0)!!
            v.set(mRectangle[i]!!)
            v.translate(-curlPos.x.toDouble(), -curlPos.y.toDouble())
            v.rotateZ(-curlAngle)
            var j = 0
            while (j < mArrRotatedVertices.size()) {
                val v2 = mArrRotatedVertices[j]!!
                if (v.mPosX > v2.mPosX) {
                    break
                }
                if (v.mPosX == v2.mPosX && v.mPosY > v2.mPosY) {
                    break
                }
                ++j
            }
            mArrRotatedVertices.add(j, v)
        }

        val lines = arrayOf(intArrayOf(0, 1), intArrayOf(0, 2), intArrayOf(1, 3), intArrayOf(2, 3))
        run {
            val v0 = mArrRotatedVertices[0]!!
            val v2 = mArrRotatedVertices[2]!!
            val v3 = mArrRotatedVertices[3]!!
            val dist2 = Math.sqrt(
                (v0.mPosX - v2.mPosX)
                        * (v0.mPosX - v2.mPosX) + (v0.mPosY - v2.mPosY)
                        * (v0.mPosY - v2.mPosY)
            )
            val dist3 = Math.sqrt(
                (v0.mPosX - v3.mPosX)
                        * (v0.mPosX - v3.mPosX) + (v0.mPosY - v3.mPosY)
                        * (v0.mPosY - v3.mPosY)
            )
            if (dist2 > dist3) {
                lines[1][1] = 3
                lines[2][1] = 2
            }
        }
        mVerticesCountBack = 0
        mVerticesCountFront = mVerticesCountBack
        if (DRAW_SHADOW) {
            mArrTempShadowVertices?.addAll(mArrDropShadowVertices!!)
            mArrTempShadowVertices?.addAll(mArrSelfShadowVertices!!)
            mArrDropShadowVertices?.clear()
            mArrSelfShadowVertices?.clear()
        }

        val curlLength = Math.PI * radius
        mArrScanLines.clear()
        if (mMaxCurlSplits > 0) {
            mArrScanLines.add(0.toDouble())
        }
        for (i in 1 until mMaxCurlSplits) {
            mArrScanLines.add(-curlLength * i / (mMaxCurlSplits - 1))
        }
        mArrScanLines.add(mArrRotatedVertices[3]!!.mPosX - 1)

        var scanXmax = mArrRotatedVertices[0]!!.mPosX + 1
        for (i in 0 until mArrScanLines.size()) {
            val scanXmin = mArrScanLines[i]!!
            for (j in 0 until mArrRotatedVertices.size()) {
                val v = mArrRotatedVertices[j]!!
                if (v.mPosX >= scanXmin && v.mPosX <= scanXmax) {
                    val n = mArrTempVertices.remove(0)!!
                    n.set(v)
                    val intersections = getIntersections(
                        mArrRotatedVertices, lines, n.mPosX
                    )
                    if (intersections.size() == 1
                        && intersections[0]!!.mPosY > v.mPosY
                    ) {
                        mArrOutputVertices.addAll(intersections)
                        mArrOutputVertices.add(n)
                    } else if (intersections.size() <= 1) {
                        mArrOutputVertices.add(n)
                        mArrOutputVertices.addAll(intersections)
                    } else {
                        mArrTempVertices.add(n)
                        mArrTempVertices.addAll(intersections)
                    }
                }
            }

            val intersections = getIntersections(
                mArrRotatedVertices,
                lines, scanXmin
            )

            if (intersections.size() == 2) {
                val v1 = intersections[0]!!
                val v2 = intersections[1]!!
                if (v1.mPosY < v2.mPosY) {
                    mArrOutputVertices.add(v2)
                    mArrOutputVertices.add(v1)
                } else {
                    mArrOutputVertices.addAll(intersections)
                }
            } else if (intersections.size() != 0) {
                mArrTempVertices.addAll(intersections)
            }
            while (mArrOutputVertices.size() > 0) {
                val v = mArrOutputVertices.remove(0)!!
                mArrTempVertices.add(v)

                var textureFront: Boolean

                if (i == 0) {
                    textureFront = true
                    mVerticesCountFront++
                } else if (i == mArrScanLines.size() - 1 || curlLength == 0.0) {
                    v.mPosX = -(curlLength + v.mPosX)
                    v.mPosZ = 2 * radius
                    v.mPenumbraX = -v.mPenumbraX
                    textureFront = false
                    mVerticesCountBack++
                } else {
                    val rotY = Math.PI * (v.mPosX / curlLength)
                    v.mPosX = radius * Math.sin(rotY)
                    v.mPosZ = radius - radius * Math.cos(rotY)
                    v.mPenumbraX *= Math.cos(rotY)
                    v.mColorFactor = (.1f + .9f * Math.sqrt(
                        Math
                            .sin(rotY) + 1
                    )).toFloat()
                    if (v.mPosZ >= radius) {
                        textureFront = false
                        mVerticesCountBack++
                    } else {
                        textureFront = true
                        mVerticesCountFront++
                    }
                }

                if (textureFront != mFlipTexture) {
                    v.mTexX *= mTextureRectFront.right.toDouble()
                    v.mTexY *= mTextureRectFront.bottom.toDouble()
                    v.mColor = texturePage.getColor(CurlPage.SIDE_FRONT)
                } else {
                    v.mTexX *= mTextureRectBack.right.toDouble()
                    v.mTexY *= mTextureRectBack.bottom.toDouble()
                    v.mColor = texturePage.getColor(CurlPage.SIDE_BACK)
                }

                v.rotateZ(curlAngle)
                v.translate(curlPos.x.toDouble(), curlPos.y.toDouble())
                addVertex(v)

                if (DRAW_SHADOW && v.mPosZ > 0 && v.mPosZ <= radius) {
                    val sv = mArrTempShadowVertices.remove(0)!!
                    sv.mPosX = v.mPosX
                    sv.mPosY = v.mPosY
                    sv.mPosZ = v.mPosZ
                    sv.mPenumbraX = v.mPosZ / 2 * -curlDir.x
                    sv.mPenumbraY = v.mPosZ / 2 * -curlDir.y
                    sv.mPenumbraColor = v.mPosZ / radius
                    val idx = (mArrDropShadowVertices!!.size() + 1) / 2
                    mArrDropShadowVertices!!.add(idx, sv)
                }
                if (DRAW_SHADOW && v.mPosZ > radius) {
                    val sv = mArrTempShadowVertices.remove(0)!!
                    sv.mPosX = v.mPosX
                    sv.mPosY = v.mPosY
                    sv.mPosZ = v.mPosZ
                    sv.mPenumbraX = (v.mPosZ - radius) / 3 * v.mPenumbraX
                    sv.mPenumbraY = (v.mPosZ - radius) / 3 * v.mPenumbraY
                    sv.mPenumbraColor = (v.mPosZ - radius) / (2 * radius)
                    val idx = (mArrSelfShadowVertices!!.size() + 1) / 2
                    mArrSelfShadowVertices!!.add(idx, sv)
                }
            }

            scanXmax = scanXmin
        }
        mBufVertices.position(0)
        mBufColors.position(0)
        if (DRAW_TEXTURE) {
            mBufTexCoords!!.position(0)
        }

        if (DRAW_SHADOW) {
            mBufShadowColors!!.position(0)
            mBufShadowVertices!!.position(0)
            mDropShadowCount = 0
            for (i in 0 until mArrDropShadowVertices!!.size()) {
                val sv = mArrDropShadowVertices!![i]!!
                mBufShadowVertices.put(sv.mPosX.toFloat())
                mBufShadowVertices.put(sv.mPosY.toFloat())
                mBufShadowVertices.put(sv.mPosZ.toFloat())
                mBufShadowVertices.put((sv.mPosX + sv.mPenumbraX).toFloat())
                mBufShadowVertices.put((sv.mPosY + sv.mPenumbraY).toFloat())
                mBufShadowVertices.put(sv.mPosZ.toFloat())
                for (j in 0..3) {
                    val color = (SHADOW_OUTER_COLOR[j]
                            + (SHADOW_INNER_COLOR[j] - SHADOW_OUTER_COLOR[j])
                            * sv.mPenumbraColor)
                    mBufShadowColors!!.put(color.toFloat())
                }
                mBufShadowColors!!.put(SHADOW_OUTER_COLOR)
                mDropShadowCount += 2
            }
            mSelfShadowCount = 0
            for (i in 0 until mArrSelfShadowVertices!!.size()) {
                val sv = mArrSelfShadowVertices!![i]!!
                mBufShadowVertices.put(sv.mPosX.toFloat())
                mBufShadowVertices.put(sv.mPosY.toFloat())
                mBufShadowVertices.put(sv.mPosZ.toFloat())
                mBufShadowVertices.put((sv.mPosX + sv.mPenumbraX).toFloat())
                mBufShadowVertices.put((sv.mPosY + sv.mPenumbraY).toFloat())
                mBufShadowVertices.put(sv.mPosZ.toFloat())
                for (j in 0..3) {
                    val color = (SHADOW_OUTER_COLOR[j]
                            + (SHADOW_INNER_COLOR[j] - SHADOW_OUTER_COLOR[j])
                            * sv.mPenumbraColor)
                    mBufShadowColors.put(color.toFloat())
                }
                mBufShadowColors.put(SHADOW_OUTER_COLOR)
                mSelfShadowCount += 2
            }
            mBufShadowColors.position(0)
            mBufShadowVertices.position(0)
        }
    }

    private fun getIntersections(
        vertices: Array<Vertex>,
        lineIndices: kotlin.Array<IntArray>, scanX: Double
    ): Array<Vertex> {
        mArrIntersections.clear()
        for (j in lineIndices.indices) {
            val v1 = vertices[lineIndices[j][0]]!!
            val v2 = vertices[lineIndices[j][1]]!!
            if (v1.mPosX > scanX && v2.mPosX < scanX) {
                val c = (scanX - v2.mPosX) / (v1.mPosX - v2.mPosX)
                val n = mArrTempVertices.remove(0)!!
                n.set(v2)
                n.mPosX = scanX
                n.mPosY += (v1.mPosY - v2.mPosY) * c
                if (DRAW_TEXTURE) {
                    n.mTexX += (v1.mTexX - v2.mTexX) * c
                    n.mTexY += (v1.mTexY - v2.mTexY) * c
                }
                if (DRAW_SHADOW) {
                    n.mPenumbraX += (v1.mPenumbraX - v2.mPenumbraX) * c
                    n.mPenumbraY += (v1.mPenumbraY - v2.mPenumbraY) * c
                }
                mArrIntersections.add(n)
            }
        }
        return mArrIntersections
    }

    @Synchronized
    fun onDrawFrame(gl: GL10) {
        if (DRAW_TEXTURE && mTextureIds == null) {
            mTextureIds = IntArray(2)
            gl.glGenTextures(2, mTextureIds, 0)
            for (textureId in mTextureIds!!) {
                gl.glBindTexture(GL10.GL_TEXTURE_2D, textureId)
                gl.glTexParameterf(
                    GL10.GL_TEXTURE_2D,
                    GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST.toFloat()
                )
                gl.glTexParameterf(
                    GL10.GL_TEXTURE_2D,
                    GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_NEAREST.toFloat()
                )
                gl.glTexParameterf(
                    GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S,
                    GL10.GL_CLAMP_TO_EDGE.toFloat()
                )
                gl.glTexParameterf(
                    GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T,
                    GL10.GL_CLAMP_TO_EDGE.toFloat()
                )
            }
        }
        if (DRAW_TEXTURE && texturePage.texturesChanged) {
            gl.glBindTexture(GL10.GL_TEXTURE_2D, mTextureIds!![0])
            var texture = texturePage.getTexture(
                mTextureRectFront,
                CurlPage.SIDE_FRONT
            )
            GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, texture, 0)
            texture.recycle()
            mTextureBack = texturePage.hasBackTexture()
            if (mTextureBack) {
                gl.glBindTexture(GL10.GL_TEXTURE_2D, mTextureIds!![1])
                texture = texturePage.getTexture(
                    mTextureRectBack,
                    CurlPage.SIDE_BACK
                )
                GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, texture, 0)
                texture.recycle()
            } else {
                mTextureRectBack.set(mTextureRectFront)
            }
            texturePage.recycle()
            reset()
        }
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY)
        if (DRAW_SHADOW) {
            gl.glDisable(GL10.GL_TEXTURE_2D)
            gl.glEnable(GL10.GL_BLEND)
            gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA)
            gl.glEnableClientState(GL10.GL_COLOR_ARRAY)
            gl.glColorPointer(4, GL10.GL_FLOAT, 0, mBufShadowColors)
            gl.glVertexPointer(3, GL10.GL_FLOAT, 0, mBufShadowVertices)
            gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, mDropShadowCount)
            gl.glDisableClientState(GL10.GL_COLOR_ARRAY)
            gl.glDisable(GL10.GL_BLEND)
        }
        if (DRAW_TEXTURE) {
            gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY)
            gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, mBufTexCoords)
        }
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, mBufVertices)
        gl.glEnableClientState(GL10.GL_COLOR_ARRAY)
        gl.glColorPointer(4, GL10.GL_FLOAT, 0, mBufColors)

        gl.glDisable(GL10.GL_TEXTURE_2D)
        gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, mVerticesCountFront)

        if (DRAW_TEXTURE) {
            gl.glEnable(GL10.GL_BLEND)
            gl.glEnable(GL10.GL_TEXTURE_2D)
            if (!mFlipTexture || !mTextureBack) {
                gl.glBindTexture(GL10.GL_TEXTURE_2D, mTextureIds!![0])
            } else {
                gl.glBindTexture(GL10.GL_TEXTURE_2D, mTextureIds!![1])
            }
            gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA)
            gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, mVerticesCountFront)
            gl.glDisable(GL10.GL_BLEND)
            gl.glDisable(GL10.GL_TEXTURE_2D)
        }
        val backStartIdx = Math.max(0, mVerticesCountFront - 2)
        val backCount = mVerticesCountFront + mVerticesCountBack - backStartIdx

        gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, backStartIdx, backCount)

        if (DRAW_TEXTURE) {
            gl.glEnable(GL10.GL_BLEND)
            gl.glEnable(GL10.GL_TEXTURE_2D)
            if (mFlipTexture || !mTextureBack) {
                gl.glBindTexture(GL10.GL_TEXTURE_2D, mTextureIds!![0])
            } else {
                gl.glBindTexture(GL10.GL_TEXTURE_2D, mTextureIds!![1])
            }
            gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA)
            gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, backStartIdx, backCount)
            gl.glDisable(GL10.GL_BLEND)
            gl.glDisable(GL10.GL_TEXTURE_2D)
        }

        gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY)
        gl.glDisableClientState(GL10.GL_COLOR_ARRAY)
        if (DRAW_POLYGON_OUTLINES) {
            gl.glEnable(GL10.GL_BLEND)
            gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA)
            gl.glLineWidth(1.0f)
            gl.glColor4f(0.5f, 0.5f, 1.0f, 1.0f)
            gl.glVertexPointer(3, GL10.GL_FLOAT, 0, mBufVertices)
            gl.glDrawArrays(GL10.GL_LINE_STRIP, 0, mVerticesCountFront)
            gl.glDisable(GL10.GL_BLEND)
        }
        if (DRAW_CURL_POSITION) {
            gl.glEnable(GL10.GL_BLEND)
            gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA)
            gl.glLineWidth(1.0f)
            gl.glColor4f(1.0f, 0.5f, 0.5f, 1.0f)
            gl.glVertexPointer(2, GL10.GL_FLOAT, 0, mBufCurlPositionLines)
            gl.glDrawArrays(GL10.GL_LINES, 0, mCurlPositionLinesCount * 2)
            gl.glDisable(GL10.GL_BLEND)
        }
        if (DRAW_SHADOW) {
            gl.glEnable(GL10.GL_BLEND)
            gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA)
            gl.glEnableClientState(GL10.GL_COLOR_ARRAY)
            gl.glColorPointer(4, GL10.GL_FLOAT, 0, mBufShadowColors)
            gl.glVertexPointer(3, GL10.GL_FLOAT, 0, mBufShadowVertices)
            gl.glDrawArrays(
                GL10.GL_TRIANGLE_STRIP, mDropShadowCount,
                mSelfShadowCount
            )
            gl.glDisableClientState(GL10.GL_COLOR_ARRAY)
            gl.glDisable(GL10.GL_BLEND)
        }
        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY)
    }

    @Synchronized
    fun reset() {
        mBufVertices.position(0)
        mBufColors.position(0)
        if (DRAW_TEXTURE) {
            mBufTexCoords!!.position(0)
        }
        for (i in 0..3) {
            val tmp = mArrTempVertices[0]!!
            tmp.set(mRectangle[i]!!)
            if (mFlipTexture) {
                tmp.mTexX *= mTextureRectBack.right.toDouble()
                tmp.mTexY *= mTextureRectBack.bottom.toDouble()
                tmp.mColor = texturePage.getColor(CurlPage.SIDE_BACK)
            } else {
                tmp.mTexX *= mTextureRectFront.right.toDouble()
                tmp.mTexY *= mTextureRectFront.bottom.toDouble()
                tmp.mColor = texturePage.getColor(CurlPage.SIDE_FRONT)
            }
            addVertex(tmp)
        }
        mVerticesCountFront = 4
        mVerticesCountBack = 0
        mBufVertices.position(0)
        mBufColors.position(0)
        if (DRAW_TEXTURE) {
            mBufTexCoords!!.position(0)
        }
        mSelfShadowCount = 0
        mDropShadowCount = mSelfShadowCount
    }

    @Synchronized
    fun resetTexture() {
        mTextureIds = null
    }

    @Synchronized
    fun setFlipTexture(flipTexture: Boolean) {
        mFlipTexture = flipTexture
        if (flipTexture) {
            setTexCoords(1f, 0f, 0f, 1f)
        } else {
            setTexCoords(0f, 0f, 1f, 1f)
        }
    }

    fun setRect(r: RectF) {
        mRectangle[0]!!.mPosX = r.left.toDouble()
        mRectangle[0]!!.mPosY = r.top.toDouble()
        mRectangle[1]!!.mPosX = r.left.toDouble()
        mRectangle[1]!!.mPosY = r.bottom.toDouble()
        mRectangle[2]!!.mPosX = r.right.toDouble()
        mRectangle[2]!!.mPosY = r.top.toDouble()
        mRectangle[3]!!.mPosX = r.right.toDouble()
        mRectangle[3]!!.mPosY = r.bottom.toDouble()
    }

    @Synchronized
    private fun setTexCoords(
        left: Float, top: Float, right: Float,
        bottom: Float
    ) {
        mRectangle[0]!!.mTexX = left.toDouble()
        mRectangle[0]!!.mTexY = top.toDouble()
        mRectangle[1]!!.mTexX = left.toDouble()
        mRectangle[1]!!.mTexY = bottom.toDouble()
        mRectangle[2]!!.mTexX = right.toDouble()
        mRectangle[2]!!.mTexY = top.toDouble()
        mRectangle[3]!!.mTexX = right.toDouble()
        mRectangle[3]!!.mTexY = bottom.toDouble()
    }

    companion object {
        private const val DRAW_CURL_POSITION = false

        private const val DRAW_POLYGON_OUTLINES = false

        private const val DRAW_SHADOW = true

        private const val DRAW_TEXTURE = true

        private val SHADOW_INNER_COLOR = floatArrayOf(0f, 0f, 0f, .5f)
        private val SHADOW_OUTER_COLOR = floatArrayOf(0f, 0f, 0f, .0f)
    }

    init {
        mMaxCurlSplits = if (maxCurlSplits < 1) 1 else maxCurlSplits
        mArrScanLines = Array<Double>(maxCurlSplits + 2)
        mArrOutputVertices = Array<Vertex>(7)
        mArrRotatedVertices = Array<Vertex>(4)
        mArrIntersections = Array<Vertex>(2)
        mArrTempVertices = Array<Vertex>(7 + 4)
        for (i in 0 until 7 + 4) {
            mArrTempVertices.add(Vertex())
        }
        if (DRAW_SHADOW) {
            mArrSelfShadowVertices = Array<ShadowVertex>(
                (mMaxCurlSplits + 2) * 2
            )
            mArrDropShadowVertices = Array<ShadowVertex>(
                (mMaxCurlSplits + 2) * 2
            )
            mArrTempShadowVertices = Array<ShadowVertex>(
                (mMaxCurlSplits + 2) * 2
            )
            for (i in 0 until (mMaxCurlSplits + 2) * 2) {
                mArrTempShadowVertices.add(ShadowVertex())
            }
        }

        for (i in 0..3) {
            mRectangle[i] = Vertex()
        }
        mRectangle[3]!!.mPenumbraY = -1.0
        mRectangle[1]!!.mPenumbraY = mRectangle[3]!!.mPenumbraY
        mRectangle[1]!!.mPenumbraX = mRectangle[1]!!.mPenumbraY
        mRectangle[0]!!.mPenumbraX = mRectangle[1]!!.mPenumbraX
        mRectangle[3]!!.mPenumbraX = 1.0
        mRectangle[2]!!.mPenumbraY = mRectangle[3]!!.mPenumbraX
        mRectangle[2]!!.mPenumbraX = mRectangle[2]!!.mPenumbraY
        mRectangle[0]!!.mPenumbraY = mRectangle[2]!!.mPenumbraX
        if (DRAW_CURL_POSITION) {
            mCurlPositionLinesCount = 3
            val hvbb = ByteBuffer
                .allocateDirect(mCurlPositionLinesCount * 2 * 2 * 4)
            hvbb.order(ByteOrder.nativeOrder())
            mBufCurlPositionLines = hvbb.asFloatBuffer()
            mBufCurlPositionLines?.position(0)
        }

        val maxVerticesCount = 4 + 2 + 2 * mMaxCurlSplits
        val vbb = ByteBuffer.allocateDirect(maxVerticesCount * 3 * 4)
        vbb.order(ByteOrder.nativeOrder())
        mBufVertices = vbb.asFloatBuffer()
        mBufVertices.position(0)
        if (DRAW_TEXTURE) {
            val tbb = ByteBuffer
                .allocateDirect(maxVerticesCount * 2 * 4)
            tbb.order(ByteOrder.nativeOrder())
            mBufTexCoords = tbb.asFloatBuffer()
            mBufTexCoords?.position(0)
        }
        val cbb = ByteBuffer.allocateDirect(maxVerticesCount * 4 * 4)
        cbb.order(ByteOrder.nativeOrder())
        mBufColors = cbb.asFloatBuffer()
        mBufColors.position(0)
        if (DRAW_SHADOW) {
            val maxShadowVerticesCount = (mMaxCurlSplits + 2) * 2 * 2
            val scbb = ByteBuffer
                .allocateDirect(maxShadowVerticesCount * 4 * 4)
            scbb.order(ByteOrder.nativeOrder())
            mBufShadowColors = scbb.asFloatBuffer()
            mBufShadowColors?.position(0)
            val sibb = ByteBuffer
                .allocateDirect(maxShadowVerticesCount * 3 * 4)
            sibb.order(ByteOrder.nativeOrder())
            mBufShadowVertices = sibb.asFloatBuffer()
            mBufShadowVertices?.position(0)
            mSelfShadowCount = 0
            mDropShadowCount = mSelfShadowCount
        }
    }
}