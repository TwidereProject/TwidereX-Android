package com.twidere.twiderex.viewmodel

import androidx.paging.PagingData
import androidx.paging.PagingSource
import com.twidere.twiderex.component.lazy.LazyPagingItems
import com.twidere.twiderex.model.AccountDetails
import com.twidere.twiderex.model.AmUser
import com.twidere.twiderex.model.ui.UiList
import com.twidere.twiderex.repository.ListsRepository
import com.twidere.twiderex.viewmodel.lists.ListsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever

class ListsViewModelTest {

    @Mock
    private lateinit var mockRepository: ListsRepository

    @Mock
    private lateinit var mockAccount: AccountDetails

    @Mock
    private lateinit var mockUser: AmUser

    @Mock
    private lateinit var mockSource: PagingSource<Int, UiList>

    @Mock
    private lateinit var ownerList: UiList

    @Mock
    lateinit var subscribeList: UiList

    lateinit var mocks: AutoCloseable

    @OptIn(ObsoleteCoroutinesApi::class)
    private val mainThreadSurrogate = newSingleThreadContext("UI thread")

    @ExperimentalCoroutinesApi
    @Before
    fun setUp() {
        Dispatchers.setMain(mainThreadSurrogate)
        mocks = MockitoAnnotations.openMocks(this)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun tearDown() {
        Dispatchers.resetMain() // reset main dispatcher to the original Main dispatcher
        mainThreadSurrogate.close()
        mocks.close()
    }

    @ExperimentalCoroutinesApi
    @Test
    fun testOwnerSource(): Unit = runBlocking(Dispatchers.Main) {
        whenever(mockSource.load(any())).thenReturn(
            PagingSource.LoadResult.Page(data = listOf(ownerList, subscribeList), null, null)
        )

        whenever(mockRepository.fetchLists(any())).thenReturn(
                flow {
                    emit(PagingData.from(listOf(ownerList, subscribeList)))
                }
        )

        whenever(ownerList.isOwner(any())).thenReturn(true)
        whenever(subscribeList.isOwner(any())).thenReturn(false)
        whenever(ownerList.title).thenReturn("owner")
        whenever(subscribeList.title).thenReturn("subscribe")
        whenever(mockAccount.user).thenReturn(mockUser)
        whenever(mockUser.userId).thenReturn("123")


        // check the source
        val viewModel = ListsViewModel(mockRepository, mockAccount)
        val sourceItems = LazyPagingItems(viewModel.source)
        sourceItems.collectPagingData()
        Assert.assertEquals(2, sourceItems.itemCount)

        // make sure ownerSource only emit data which isOwner() returns true
        val ownerItems = LazyPagingItems(viewModel.ownerSource)
        ownerItems.collectPagingData()
        Assert.assertEquals(1, ownerItems.itemCount)
        Assert.assertEquals("owner",  ownerItems[0]?.title)

        // make sure subscribedSource only emit data which isOwner() returns false
        val subscribeItems = LazyPagingItems(viewModel.subscribedSource)
        subscribeItems.collectPagingData()
        Assert.assertEquals(1, subscribeItems.itemCount)
        Assert.assertEquals("subscribe",  subscribeItems[0]?.title)
    }

}