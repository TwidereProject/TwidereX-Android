package com.twidere.twiderex.di.assisted

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Providers
import androidx.compose.runtime.staticAmbientOf
import androidx.compose.ui.viewinterop.viewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider


@Composable
inline fun <reified AF : IAssistedFactory, reified VM: ViewModel> assistedViewModel(
    key: String? = null,
    noinline creator: ((AF) -> VM)? = null,
): VM {
    val factories = AmbientAssistedFactories.current
    val factory = factories.firstOrNull { AF::class.java.isInstance(it) } as? AF
    return viewModel(key, factory = object : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return factory?.let { creator?.invoke(it) } as T
        }
    })
}

interface IAssistedFactory

@Composable
fun ProvideAssistedFactory(
    vararg factory: IAssistedFactory,
    content: @Composable () -> Unit,
) {
    Providers(
        AmbientAssistedFactories provides factory.toList()
    ) {
        content.invoke()
    }
}

val AmbientAssistedFactories = staticAmbientOf<List<IAssistedFactory>>()