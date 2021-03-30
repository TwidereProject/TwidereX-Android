package moe.tlaster.precompose.navigation

/**
 * [NavOptions] stores special options for navigate actions
 */
data class NavOptions(
    /**
     * Whether this navigation action should launch as single-top (i.e., there will be at most
     * one copy of a given destination on the top of the back stack).
     */
    val launchSingleTop: Boolean = false,
    /**
     * The destination to pop up to before navigating. When set, all non-matching destinations
     * should be popped from the back stack.
     * @see [PopUpTo]
     */
    val popUpTo: PopUpTo? = null,
)
