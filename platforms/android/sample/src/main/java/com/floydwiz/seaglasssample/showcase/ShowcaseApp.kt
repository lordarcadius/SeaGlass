package com.floydwiz.seaglasssample.showcase

import androidx.compose.runtime.getValue
import kotlinx.coroutines.flow.drop
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.setValue
import android.graphics.Color
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.LocalActivity
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.floydwiz.seaglass.theme.rememberCrossWindowBlurEnabled
import com.floydwiz.seaglass.theme.rememberSystemContrast
import com.floydwiz.seaglasssample.control.ControlState
import com.floydwiz.seaglasssample.control.LocalControls
import com.floydwiz.seaglasssample.control.ThemeMode
import com.floydwiz.seaglasssample.control.writeControls
import com.floydwiz.seaglasssample.control.rememberControlState
import com.floydwiz.seaglasssample.showcase.catalogue.c0.LanguagePage
import com.floydwiz.seaglasssample.showcase.catalogue.c1.ButtonsPage
import com.floydwiz.seaglasssample.showcase.catalogue.c2.SelectionPage
import com.floydwiz.seaglasssample.showcase.catalogue.c3.InputPage
import com.floydwiz.seaglasssample.showcase.catalogue.c4.NavigationPage
import com.floydwiz.seaglasssample.showcase.catalogue.c5.ContainersPage
import com.floydwiz.seaglasssample.showcase.catalogue.c6.FeedbackPage
import com.floydwiz.seaglasssample.showcase.catalogue.c7.FabsPage
import com.floydwiz.seaglasssample.showcase.motion.MotionLab
import com.floydwiz.seaglasssample.demos.files.FilesDemo
import com.floydwiz.seaglasssample.demos.gallery.GalleryDemo
import com.floydwiz.seaglasssample.demos.home.SmartHomeDemo
import com.floydwiz.seaglasssample.demos.music.MusicDemo
import com.floydwiz.seaglasssample.demos.notes.NotesDemo
import com.floydwiz.seaglasssample.demos.weather.WeatherDemo
import com.floydwiz.seaglasssample.demos.todo.TodoDemo
import com.floydwiz.seaglasssample.showcase.a11y.AccessibilityLab
import com.floydwiz.seaglasssample.showcase.home.HomeScreen
import com.floydwiz.seaglasssample.showcase.materials.MaterialsLab
import com.floydwiz.seaglasssample.showcase.screens.assistant.AssistantScreen
import com.floydwiz.seaglasssample.showcase.screens.incall.InCallScreen
import com.floydwiz.seaglasssample.showcase.screens.inbox.InboxScreen
import com.floydwiz.seaglasssample.showcase.screens.library.LibraryScreen
import com.floydwiz.seaglasssample.showcase.screens.settings.SettingsScreen
import com.floydwiz.seaglasssample.showcase.screens.tablet.TabletMailScreen
import com.floydwiz.seaglasssample.showcase.spike.GlassStress
import com.floydwiz.seaglasssample.showcase.spike.SpikeScreen

private const val ExpandedWidthDp = 840

@Composable
private fun TabletMailStage() {
    if (LocalConfiguration.current.screenWidthDp >= ExpandedWidthDp) {
        TabletMailScreen()
    } else {
        Box(Modifier.fillMaxSize().horizontalScroll(rememberScrollState())) {
            Box(Modifier.requiredSize(1280.dp, 800.dp)) { TabletMailScreen() }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShowcaseApp(initialControls: ControlState = ControlState(), startRoute: String? = null, persist: Boolean = false) {
    val controls = rememberControlState(initialControls)
    val context = LocalContext.current
    if (persist) {
        LaunchedEffect(controls) {
            snapshotFlow {
                listOf(
                    controls.themeMode, controls.contrast, controls.reduceTransparency, controls.autoReduceTransparency,
                    controls.dynamicColor, controls.seedRole, controls.layers, controls.fpsOverlay, controls.lightDrift,
                )
            }.drop(1).collect { writeControls(context, controls) }
        }
    }
    val systemContrast = rememberSystemContrast()
    val blurEnabled = rememberCrossWindowBlurEnabled()
    SideEffect {
        controls.systemContrast = systemContrast
        controls.blurEnabled = blurEnabled
    }
    val dark = when (controls.themeMode) {
        ThemeMode.System -> isSystemInDarkTheme()
        ThemeMode.Light -> false
        ThemeMode.Dark -> true
    }

    val activity = LocalActivity.current as? ComponentActivity
    DisposableEffect(activity, dark) {
        val style = SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT) { dark }
        activity?.enableEdgeToEdge(statusBarStyle = style, navigationBarStyle = style)
        onDispose { }
    }

    LabTheme(
        dark = dark,
        contrast = controls.effectiveContrast,
        reduceTransparency = controls.effectiveReduceTransparency,
        dynamicColor = controls.dynamicColor,
        seedRole = controls.seedRole,
        layers = controls.layers,
        lightDrift = controls.lightDrift,
    ) {
        val navController = rememberNavController()
        val backStack by navController.currentBackStackEntryAsState()
        val route = backStack?.destination?.route
        val fullBleed = route == Destinations.HOME || route?.startsWith("screens/") == true || route?.startsWith("demos/") == true

        ShowcaseScaffold(
            title = Destinations.titleOf(route),
            onBack = if (route == null || route == Destinations.HOME) null else ({ navController.popBackStack() }),
            fullBleed = fullBleed,
            controlsTop = if (route == Destinations.HOME || route?.startsWith("demos/") == true) 4.dp else 64.dp,
            controls = controls,
        ) { padding ->
            CompositionLocalProvider(LocalControls provides controls) {
                NavHost(navController, startDestination = startRoute ?: Destinations.HOME) {
                    composable(Destinations.HOME) { HomeScreen { navController.navigate(it.route) } }
                    composable(Destinations.CATALOGUE) {
                        DestinationList(padding, listOf("Prototype" to listOf(Destinations.spike), "Components" to Destinations.components), { navController.navigate(it.route) })
                    }
                    composable(Destinations.SCREENS) {
                        DestinationList(padding, listOf("Screens" to Destinations.screens), { navController.navigate(it.route) })
                    }
                    composable(Destinations.DEMOS) {
                        DestinationList(padding, listOf("Demos" to Destinations.demos), { navController.navigate(it.route) })
                    }
                    composable("perf/stress") { GlassStress((LocalActivity.current as? ComponentActivity)?.intent) }
                    composable(Destinations.spike.route) { SpikeScreen(controls, padding) }
                    composable(Destinations.components[0].route) { LanguagePage(padding) }
                    composable(Destinations.components[1].route) { ButtonsPage(padding) }
                    composable(Destinations.components[2].route) { SelectionPage(padding) }
                    composable(Destinations.components[3].route) { InputPage(padding) }
                    composable(Destinations.components[4].route) { NavigationPage(padding) }
                    composable(Destinations.components[5].route) { ContainersPage(padding) }
                    composable(Destinations.components[6].route) { FeedbackPage(padding) }
                    composable(Destinations.components[7].route) { FabsPage(padding) }
                    composable("demos/music") { MusicDemo(onBack = { navController.popBackStack() }) }
                    composable("demos/weather") { WeatherDemo(onBack = { navController.popBackStack() }) }
                    composable("demos/home") { SmartHomeDemo(onBack = { navController.popBackStack() }) }
                    composable("demos/notes") { NotesDemo(onBack = { navController.popBackStack() }) }
                    composable("demos/files") { FilesDemo(onBack = { navController.popBackStack() }) }
                    composable("demos/todo") { TodoDemo(onBack = { navController.popBackStack() }) }
                    composable("demos/gallery") { GalleryDemo(onBack = { navController.popBackStack() }) }
                    composable(Destinations.MATERIALS) { MaterialsLab(padding) }
                    composable(Destinations.A11Y) { AccessibilityLab(padding) }
                    composable(Destinations.MOTION) { MotionLab(padding) }
                    composable(Destinations.screens[0].route) { InCallScreen(onEnd = { navController.popBackStack() }) }
                    composable(Destinations.screens[1].route) { LibraryScreen() }
                    composable(Destinations.screens[2].route) {
                        if (LocalConfiguration.current.screenWidthDp >= ExpandedWidthDp) TabletMailScreen() else InboxScreen()
                    }
                    composable(Destinations.screens[3].route) { AssistantScreen() }
                    composable(Destinations.screens[4].route) { SettingsScreen(onBack = { navController.popBackStack() }) }
                    composable(Destinations.screens[5].route) { TabletMailStage() }
                }
            }
        }
    }
}

