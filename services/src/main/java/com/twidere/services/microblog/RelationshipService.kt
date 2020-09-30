package com.twidere.services.microblog

import com.twidere.services.microblog.model.IRelationship

interface RelationshipService {
    suspend fun showRelationship(id: String): IRelationship
}