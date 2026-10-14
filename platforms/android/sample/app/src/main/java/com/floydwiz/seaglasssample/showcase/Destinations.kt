package com.floydwiz.seaglasssample.showcase

data class Destination(val route: String, val code: String, val title: String, val summary: String, val milestone: Int)

class Area(val route: String, val title: String, val description: String, val icon: String)

object Destinations {
    const val HOME = "home"
    const val CATALOGUE = "catalogue"
    const val SCREENS = "screens"
    const val DEMOS = "demos"
    const val MATERIALS = "materials"
    const val MOTION = "motion"
    const val A11Y = "a11y"

    val areas = listOf(
        Area(MATERIALS, "Materials lab", "Eleven finishes over a backdrop you choose, every layer adjustable", "blur_on"),
        Area(CATALOGUE, "Components", "Every control and container from the design boards, light and dark", "widgets"),
        Area(MOTION, "Motion lab", "Replay each choreography and slow it down", "animation"),
        Area(A11Y, "Accessibility lab", "The four modes side by side, and adaptive tint measured", "contrast"),
        Area(SCREENS, "Screens", "Six full flows from the phone and tablet boards", "devices"),
        Area(DEMOS, "Demos", "Seven small apps built only from the components", "apps"),
    )

    val components = listOf(
        Destination("catalogue/c0", "C0", "Language", "Rules, finishes, states and shapes", 2),
        Destination("catalogue/c1", "C1", "Buttons", "Buttons, icon buttons, clusters, split buttons", 3),
        Destination("catalogue/c2", "C2", "Selection", "Switch, checkbox, radio, segmented, chips, sliders", 3),
        Destination("catalogue/c3", "C3", "Input and pickers", "Text fields, search, prompt bar, date and time", 4),
        Destination("catalogue/c4", "C4", "Navigation", "Tab bar, top bars, toolbars, rail, sidebar", 4),
        Destination("catalogue/c5", "C5", "Containers", "Cards, lists, alert, sheet, menu, carousel", 5),
        Destination("catalogue/c6", "C6", "Feedback", "Loading, progress, toasts, banners, badges", 5),
        Destination("catalogue/c7", "C7", "Floating actions", "FAB, extended FAB, FAB menu", 5),
    )

    val screens = listOf(
        Destination("screens/incall", "S1", "In call", "Clear lenses over a blurred caller photo", 7),
        Destination("screens/library", "S2", "Photo library", "Browse and select", 7),
        Destination("screens/inbox", "S3", "Inbox", "Rows, filters and the Compose menu", 7),
        Destination("screens/assistant", "S4", "Assistant", "Chat and voice mode", 7),
        Destination("screens/settings", "S5", "Settings", "Inset lists, sliders and switches", 7),
        Destination("screens/tablet", "S6", "Tablet mail", "Rail, list and reading pane", 7),
    )

    val demos = listOf(
        Destination("demos/gallery", "D1", "Gallery", "Albums, viewer, selection and memories", 10),
        Destination("demos/todo", "D2", "Todo", "Lists, swipe actions and an add-task sheet", 10),
        Destination("demos/files", "D3", "File manager", "Storage rings, breadcrumbs and a context menu", 10),
        Destination("demos/music", "D4", "Music player", "Now playing over blurred album art", 11),
        Destination("demos/weather", "D5", "Weather", "A sky that follows the conditions", 11),
        Destination("demos/home", "D6", "Smart home", "Rooms, device tiles and scenes", 11),
        Destination("demos/notes", "D7", "Notes", "Editor with a floating format cluster", 11),
    )

    val spike = Destination("spike", "P", "Performance spike", "Glass over scrolling photos with frame stats", 1)

    val all = listOf(spike) + components + screens + demos

    fun titleOf(route: String?): String? =
        areas.firstOrNull { it.route == route }?.title ?: all.firstOrNull { it.route == route }?.title
}
