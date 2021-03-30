package moe.tlaster.precompose.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore

internal class NavControllerViewModel : ViewModel() {
    private val viewModelStores = hashMapOf<Long, ViewModelStore>()

    fun clear(id: Long) {
        viewModelStores.remove(id)?.clear()
    }

    operator fun get(id: Long): ViewModelStore {
        return viewModelStores.getOrPut(id) {
            ViewModelStore()
        }
    }

    override fun onCleared() {
        viewModelStores.forEach {
            it.value.clear()
        }
        viewModelStores.clear()
    }

    companion object {
        fun create(viewModelStore: ViewModelStore): NavControllerViewModel {
            return ViewModelProvider(viewModelStore, object : ViewModelProvider.Factory {
                override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                    @Suppress("UNCHECKED_CAST")
                    return NavControllerViewModel() as T
                }
            }).get(NavControllerViewModel::class.java)
        }
    }
}
