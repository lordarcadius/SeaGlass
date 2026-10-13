package com.floydwiz.seaglasssample.demos

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.floydwiz.seaglass.components.containers.GlassBottomSheet
import com.floydwiz.seaglass.components.containers.GlassCard
import com.floydwiz.seaglass.components.feedback.GlassLoadingIndicator
import com.floydwiz.seaglass.components.icon.SymbolIcon
import com.floydwiz.seaglass.components.navigation.GlassBarLens
import com.floydwiz.seaglass.material.glassAccent
import com.floydwiz.seaglass.material.glassOnSurfaceVariant

/** What a demo documents about itself: the components it uses and the rule that justifies each. */
class DemoInfo(val title: String, val summary: String, val components: List<Pair<String, String>>)

/** The "About this demo" milk sheet. */
@Composable
fun AboutDemoSheet(info: DemoInfo, onDismiss: () -> Unit) {
    GlassBottomSheet(onDismissRequest = onDismiss) {
        Column(
            Modifier.verticalScroll(rememberScrollState()).padding(horizontal = 24.dp).padding(top = 8.dp, bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Text("About ${info.title}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight(700))
            Text(info.summary, color = glassOnSurfaceVariant(), fontSize = 14.sp, lineHeight = 20.sp)
            Text("Components and rules", color = glassOnSurfaceVariant(), fontSize = 13.sp, fontWeight = FontWeight(650))
            info.components.forEach { (component, rule) ->
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(component, fontSize = 15.sp, fontWeight = FontWeight(650))
                    Text(rule, color = glassOnSurfaceVariant(), fontSize = 13.5.sp, lineHeight = 19.sp)
                }
            }
        }
    }
}

/** Back and About lenses pinned top-left; demos draw their own title beside or below them. */
@Composable
fun DemoChrome(onBack: () -> Unit, onAbout: () -> Unit, modifier: Modifier = Modifier) {
    Row(modifier.statusBarsPadding().padding(start = 16.dp, top = 8.dp), Arrangement.spacedBy(10.dp)) {
        GlassBarLens("arrow_back", onBack, description = "Back to demos")
        GlassBarLens("info", onAbout, description = "About this demo")
    }
}

/** A milk card for a list or search with nothing in it. */
@Composable
fun EmptyState(icon: String, title: String, body: String, modifier: Modifier = Modifier) {
    GlassCard(modifier.fillMaxWidth(), shape = com.floydwiz.seaglass.shape.SeaGlassShape.Pane(28.dp)) {
        Column(Modifier.fillMaxWidth().padding(28.dp), Arrangement.spacedBy(8.dp), Alignment.CenterHorizontally) {
            SymbolIcon(icon, size = 40.dp, tint = glassAccent())
            Text(title, fontSize = 18.sp, fontWeight = FontWeight(700), textAlign = TextAlign.Center)
            Text(body, color = glassOnSurfaceVariant(), fontSize = 14.sp, lineHeight = 20.sp, textAlign = TextAlign.Center)
        }
    }
}

/** The loading pebble centred in its parent. */
@Composable
fun LoadingState(modifier: Modifier = Modifier) {
    Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) { GlassLoadingIndicator() }
}
