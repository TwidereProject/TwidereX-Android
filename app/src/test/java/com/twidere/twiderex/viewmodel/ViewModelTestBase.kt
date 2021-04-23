package com.twidere.twiderex.viewmodel

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.mockito.MockitoAnnotations

@OptIn(ExperimentalCoroutinesApi::class)
open class ViewModelTestBase {
    private lateinit var mocks: AutoCloseable
    @OptIn(ObsoleteCoroutinesApi::class)
    private val mainThreadSurrogate = newSingleThreadContext("UI thread")


    @Before
    open fun setUp() {
        Dispatchers.setMain(mainThreadSurrogate)
        mocks = MockitoAnnotations.openMocks(this)
    }

    @After
    open fun tearDown() {
        Dispatchers.resetMain() // reset main dispatcher to the original Main dispatcher
        mainThreadSurrogate.close()
        mocks.close()
    }
}