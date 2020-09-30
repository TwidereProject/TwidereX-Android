package com.twidere.services.microblog.model

interface IRelationship {
    val followedBy: Boolean
    val following: Boolean
}

data class Relationship(
    override val followedBy: Boolean,
    override val following: Boolean,
) : IRelationship