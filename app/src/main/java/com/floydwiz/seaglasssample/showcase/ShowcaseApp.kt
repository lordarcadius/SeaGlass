package com.floydwiz.seaglasssample.showcase

import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import android.graphics.Color
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.LocalActivity
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.floydwiz.seaglass.components.icon.SymbolIcon
import com.floydwiz.seaglass.theme.SeaGlassTheme
import com.floydwiz.seaglasssample.control.ControlSheet
import com.floydwiz.seaglasssample.control.LocalControls
import com.floydwiz.seaglasssample.control.ThemeMode
import com.floydwiz.seaglasssample.control.rememberControlState
import com.floydwiz.seaglasssample.showcase.catalogue.c0.LanguagePage
import com.floydwiz.seaglasssample.showcase.catalogue.c1.ButtonsPage
import com.floydwiz.seaglasssample.showcase.catalogue.c2.SelectionPage
import com.floydwiz.seaglasssample.showcase.catalogue.c3.InputPage
import com.floydwiz.seaglasssample.showcase.catalogue.c4.NavigationPage
import com.floydwiz.seaglasssample.showcase.catalogue.c5.ContainersPage
import com.floydwiz.seaglasssample.showcase.catalogue.c6.FeedbackPage
import com.floydwiz.seaglasssample.showcase.catalogue.c7.FabsPage
import com.floydwiz.seaglasssample.showcase.catalogue.c8.MotionPage
import com.floydwiz.seaglasssample.showcase.spike.SpikeScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShowcaseApp() {
    val controls = rememberControlState()
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

    SeaGlassTheme(
        dark = dark,
        contrast = controls.contrast,
        reduceTransparency = controls.reduceTransparency,
        dynamicColor = controls.dynamicColor,
        seedRole = controls.seedRole,
        layers = controls.layers,
    ) {
        val navController = rememberNavController()
        val backStack by navController.currentBackStackEntryAsState()
        val route = backStack?.destination?.route
        val current = Destinations.all.firstOrNull { it.route == route }
        var showControls by remember { mutableStateOf(false) }

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = MaterialTheme.colorScheme.surface,
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text(current?.title ?: "Sea Glass") },
                    navigationIcon = {
                        if (current != null) {
                            IconButton(onClick = { navController.popBackStack() }) {
                                SymbolIcon("arrow_back", contentDescription = "Back")
                            }
                        }
                    },
                    actions = {
                        IconButton(onClick = { showControls = true }) {
                            SymbolIcon("tune", contentDescription = "Controls")
                        }
                    },
                )
            },
        ) { padding ->
            CompositionLocalProvider(LocalControls provides controls) {
            NavHost(navController, startDestination = Destinations.CATALOGUE) {
                composable(Destinations.CATALOGUE) {
                    CatalogueHome(padding) { navController.navigate(it.route) }
                }
                composable(Destinations.spike.route) { SpikeScreen(controls, padding) }
                composable(Destinations.components[0].route) { LanguagePage(padding) }
                composable(Destinations.components[1].route) { ButtonsPage(padding) }
                composable(Destinations.components[2].route) { SelectionPage(padding) }
                composable(Destinations.components[3].route) { InputPage(padding) }
                composable(Destinations.components[4].route) { NavigationPage(padding) }
                composable(Destinations.components[5].route) { ContainersPage(padding) }
                composable(Destinations.components[6].route) { FeedbackPage(padding) }
                composable(Destinations.components[7].route) { FabsPage(padding) }
                composable(Destinations.components[8].route) { MotionPage(padding) }
                Destinations.all.filter { it != Destinations.spike && it !in Destinations.components }.forEach { destination ->
                    composable(destination.route) {
                        EmptyDestination(destination, Modifier.padding(padding))
                    }
                }
            }
            }
        }

        if (showControls) ControlSheet(controls) { showControls = false }
    }
}

@Composable
private fun EmptyDestination(destination: Destination, modifier: Modifier = Modifier) {
    Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(destination.title, style = MaterialTheme.typography.headlineSmall)
            Text(
                text = "Built in milestone M${destination.milestone}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 8.dp),
            )
        }
    }
}
