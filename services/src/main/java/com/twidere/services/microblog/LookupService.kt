package com.twidere.services.microblog

import com.twidere.services.microblog.model.IStatus
import com.twidere.services.microblog.model.IUser

interface LookupService {
    suspend fun lookupUserByName(
        name: String
    ): IUser

    suspend fun lookupUser(
        id: String
    ): IUser

    suspend fun lookupStatus(
        id: String
    ): IStatus
}