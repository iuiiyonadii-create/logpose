package com.uriel.logpose.core.services

import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.view.Gravity
import android.view.WindowManager
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.uriel.logpose.core.compat.core.LogPoseLogger
import com.uriel.logpose.core.services.hud.DynamicHudContent

/**
 * LogPoseHudService: Gestiona la interfaz flotante (HUD) sobre otras aplicaciones.
 * v60.0: Migración total a Jetpack Compose.
 */
class LogPoseHudService : Service(), LifecycleOwner, ViewModelStoreOwner, SavedStateRegistryOwner {

    private var windowManager: WindowManager? = null
    private var composeView: ComposeView? = null

    private val lifecycleRegistry = LifecycleRegistry(this)
    private val savedStateRegistryController = SavedStateRegistryController.create(this)
    override val viewModelStore = ViewModelStore()
    override val lifecycle: Lifecycle get() = lifecycleRegistry
    override val savedStateRegistry: SavedStateRegistry get() = savedStateRegistryController.savedStateRegistry

    private val statusState = mutableStateOf("🟢 MAYA: ACTIVA")
    private val debugState = mutableStateOf<String?>(null)
    private val worldState = mutableStateOf(com.uriel.logpose.thamis.world.model.WorldSnapshot())

    companion object {
        private var instance: LogPoseHudService? = null
        
        fun updateStatus(text: String) {
            instance?.statusState?.value = text
        }

        fun showDebug(text: String?) {
            instance?.debugState?.value = text
        }

        fun updateWorld(snapshot: com.uriel.logpose.thamis.world.model.WorldSnapshot) {
            instance?.worldState?.value = snapshot
        }
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        
        savedStateRegistryController.performRestore(null)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
        
        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        createHud()
    }

    private fun createHud() {
        composeView = ComposeView(this).apply {
            setContent {
                DynamicHudContent(
                    status = statusState.value,
                    debug = debugState.value,
                    snapshot = worldState.value
                )
            }
        }

        // Trick para que Compose funcione en un Service Window
        composeView?.setViewTreeLifecycleOwner(this)
        composeView?.setViewTreeViewModelStoreOwner(this)
        composeView?.setViewTreeSavedStateRegistryOwner(this)

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            else
                @Suppress("DEPRECATION")
                WindowManager.LayoutParams.TYPE_PHONE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )

        params.gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
        params.y = 100

        windowManager?.addView(composeView, params)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
        
        LogPoseLogger.i("HUD: Interfaz flotante (Compose) desplegada.")
    }

    override fun onDestroy() {
        super.onDestroy()
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_STOP)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        
        composeView?.let { windowManager?.removeView(it) }
        instance = null
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
