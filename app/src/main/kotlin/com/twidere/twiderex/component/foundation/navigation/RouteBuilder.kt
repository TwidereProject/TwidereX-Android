package moe.tlaster.precompose.navigation

import androidx.compose.runtime.Composable
import moe.tlaster.precompose.navigation.route.DialogRoute
import moe.tlaster.precompose.navigation.route.Route
import moe.tlaster.precompose.navigation.route.SceneRoute
import moe.tlaster.precompose.navigation.transition.NavTransition

class RouteBuilder(
    private val initialRoute: String,
) {
    private val route = arrayListOf<Route>()

    /**
     * Add the scene [Composable] to the [RouteBuilder]
     * @param route route for the destination
     * @param navTransition navigation transition for current scene
     * @param content composable for the destination
     */
    fun scene(
        route: String,
        deepLinks: List<String> = emptyList(),
        navTransition: NavTransition? = null,
        content: @Composable (BackStackEntry) -> Unit,
    ) {
        this.route += SceneRoute(
            route = route,
            navTransition = navTransition,
            deepLinks = deepLinks,
            content = content,
        )
    }
    /**
     * Add the scene [Composable] to the [RouteBuilder], which will show over the scene
     * @param route route for the destination
     * @param content composable for the destination
     */
    fun dialog(
        route: String,
        content: @Composable (BackStackEntry) -> Unit,
    ) {
        this.route += DialogRoute(
            route = route,
            content = content
        )
    }

    internal fun build() = RouteGraph(initialRoute, route.toList())
}
