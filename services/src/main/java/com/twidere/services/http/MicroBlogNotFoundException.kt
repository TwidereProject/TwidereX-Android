package com.twidere.services.http

class MicroBlogNotFoundException(override val microBlogErrorMessage: String?) : MicroBlogException() {
}