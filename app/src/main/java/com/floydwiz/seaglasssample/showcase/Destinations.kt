package com.floydwiz.seaglasssample.showcase

data class Destination(val route: String, val code: String, val title: String, val summary: String, val milestone: Int)

object Destinations {
    const val CATALOGUE = "catalogue"

    val components = listOf(
        Destination("catalogue/c0", "C0", "Language", "Rules, finishes, states and shapes", 2),
        Destination("catalogue/c1", "C1", "Buttons", "Buttons, icon buttons, clusters, split buttons", 3),
        Destination("catalogue/c2", "C2", "Selection", "Switch, checkbox, radio, segmented, chips, sliders", 3),
        Destination("catalogue/c3", "C3", "Input and pickers", "Text fields, search, prompt bar, date and time", 4),
        Destination("catalogue/c4", "C4", "Navigation", "Tab bar, top bars, toolbars, rail, sidebar", 4),
        Destination("catalogue/c5", "C5", "Containers", "Cards, lists, alert, sheet, menu, carousel", 5),
        Destination("catalogue/c6", "C6", "Feedback", "Loading, progress, toasts, banners, badges", 5),
        Destination("catalogue/c7", "C7", "Floating actions", "FAB, extended FAB, FAB menu", 5),
        Destination("catalogue/c8", "C8", "Motion", "Replay every choreography", 6),
    )

    val screens = listOf(
        Destination("screens/incall", "S1", "In call", "Clear lenses over a blurred caller photo", 7),
        Destination("screens/library", "S2", "Photo library", "Browse and select", 7),
        Destination("screens/inbox", "S3", "Inbox", "Rows, filters and the Compose menu", 7),
        Destination("screens/assistant", "S4", "Assistant", "Chat and voice mode", 7),
        Destination("screens/settings", "S5", "Settings", "Inset lists, sliders and switches", 7),
        Destination("screens/tablet", "S6", "Tablet mail", "Rail, list and reading pane", 7),
    )

    val spike = Destination("spike", "P", "Performance spike", "Glass over scrolling photos with frame stats", 1)

    val all = listOf(spike) + components + screens
}
