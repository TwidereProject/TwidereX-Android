package moe.tlaster.precompose.navigation.transition

import androidx.compose.ui.graphics.GraphicsLayerScope

private const val enterScaleFactor: Float = 1.1F
private const val exitScaleFactor: Float = 0.9F

val fadeScaleCreateTransition: GraphicsLayerScope.(factor: Float) -> Unit = { factor ->
    (exitScaleFactor + (1F - exitScaleFactor) * factor).let {
        scaleX = it
        scaleY = it
    }
    alpha = factor
}
val fadeScaleDestroyTransition: GraphicsLayerScope.(factor: Float) -> Unit = { factor ->
    (exitScaleFactor + (1F - exitScaleFactor) * factor).let {
        scaleX = it
        scaleY = it
    }
    alpha = factor
}
val fadeScalePauseTransition: GraphicsLayerScope.(factor: Float) -> Unit = { factor ->
    (enterScaleFactor - (enterScaleFactor - 1F) * factor).let {
        scaleX = it
        scaleY = it
    }
    alpha = factor
}
val fadeScaleResumeTransition: GraphicsLayerScope.(factor: Float) -> Unit = { factor ->
    (enterScaleFactor - (enterScaleFactor - 1F) * factor).let {
        scaleX = it
        scaleY = it
    }
    alpha = factor
}

/**
 * Create a navigation transition
 */
data class NavTransition(
    /**
     * Transition the scene that about to appear for the first time, similar to activity onCreate, factor from 0.0 to 1.0
     */
    val createTransition: GraphicsLayerScope.(factor: Float) -> Unit = fadeScaleCreateTransition,
    /**
     * Transition the scene that about to disappear forever, similar to activity onDestroy, factor from 1.0 to 0.0
     */
    val destroyTransition: GraphicsLayerScope.(factor: Float) -> Unit = fadeScaleDestroyTransition,
    /**
     * Transition the scene that will be pushed into back stack, similar to activity onPause, factor from 1.0 to 0.0
     */
    val pauseTransition: GraphicsLayerScope.(factor: Float) -> Unit = fadeScalePauseTransition,
    /**
     * Transition the scene that about to show from the back stack, similar to activity onResume, factor from 0.0 to 1.0
     */
    val resumeTransition: GraphicsLayerScope.(factor: Float) -> Unit = fadeScaleResumeTransition,
)
