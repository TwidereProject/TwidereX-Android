package com.twidere.twiderex.kmp

expect class ImageCompressor {
    fun compress(file:String, targetSize:Long):String
}