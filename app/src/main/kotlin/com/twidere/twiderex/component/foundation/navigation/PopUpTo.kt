package moe.tlaster.precompose.navigation

data class PopUpTo(
    /**
     * The `popUpTo` destination, if it's an empty string will clear all backstack
     */
    val route: String,
    /**
     * Whether the `popUpTo` destination should be popped from the back stack.
     */
    val inclusive: Boolean = false
)
