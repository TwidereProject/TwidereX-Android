package moe.tlaster.precompose.viewmodel.compose

import androidx.compose.runtime.Composable
import moe.tlaster.precompose.ui.LocalViewModelStoreOwner
import moe.tlaster.precompose.viewmodel.ViewModel
import moe.tlaster.precompose.viewmodel.ViewModelStoreOwner
import moe.tlaster.precompose.viewmodel.getViewModel

@Composable
inline fun <reified VM : ViewModel> viewModel(
    viewModelStoreOwner: ViewModelStoreOwner = checkNotNull(LocalViewModelStoreOwner.current) {
        "No ViewModelStoreOwner was provided via LocalViewModelStoreOwner"
    },
    key: String? = null,
    noinline creator: () -> VM,
): VM = viewModelStoreOwner.viewModelStore.let {
    if (key == null) {
        it.getViewModel(creator)
    } else {
        it.getViewModel(key, creator)
    }
}

