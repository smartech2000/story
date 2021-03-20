package com.smarttech.story.library.curl.utils

public class Array<T>(private val mCapacity: Int) {
    private val mArray: kotlin.Array<Any?>
    private var mSize = 0
    fun add(index: Int, item: T) {
        if (index < 0 || index > mSize || mSize >= mCapacity) {
            throw IndexOutOfBoundsException()
        }
        for (i in mSize downTo index + 1) {
            mArray[i] = mArray[i - 1]
        }
        mArray[index] = item
        ++mSize
    }

    fun add(item: T) {
        if (mSize >= mCapacity) {
            throw IndexOutOfBoundsException()
        }
        mArray[mSize++] = item
    }

    fun addAll(array: Array<T>) {
        if (mSize + array.size() > mCapacity) {
            throw IndexOutOfBoundsException()
        }
        for (i in 0 until array.size()) {
            mArray[mSize++] = array[i]
        }
    }

    fun clear() {
        mSize = 0
    }

    operator fun get(index: Int): T? {
        if (index < 0 || index >= mSize) {
            throw IndexOutOfBoundsException()
        }
        return mArray[index] as T?
    }

    fun remove(index: Int): T? {
        if (index < 0 || index >= mSize) {
            throw IndexOutOfBoundsException()
        }
        val item = mArray[index] as T?
        for (i in index until mSize - 1) {
            mArray[i] = mArray[i + 1]
        }
        --mSize
        return item
    }

    fun size(): Int {
        return mSize
    }

    init {
        mArray = arrayOfNulls(mCapacity)
    }
}