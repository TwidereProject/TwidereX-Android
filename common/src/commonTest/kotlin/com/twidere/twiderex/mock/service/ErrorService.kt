package com.twidere.twiderex.mock.service

import com.twidere.services.twitter.model.exceptions.TwitterApiException

internal open class ErrorService {
    var errorMsg:String? = null

    fun checkError(){
        if (!errorMsg.isNullOrEmpty()) throw TwitterApiException(errorMsg)
    }
}