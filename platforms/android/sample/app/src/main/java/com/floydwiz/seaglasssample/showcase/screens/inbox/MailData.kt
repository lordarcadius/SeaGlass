package com.floydwiz.seaglasssample.showcase.screens.inbox

import com.floydwiz.seaglass.color.GlassRole

enum class MailFlag { None, Attachment, Starred }

class Mail(
    val id: Int,
    val sender: String,
    val subject: String,
    val preview: String,
    val time: String,
    val avatarCrop: Int?,
    val avatarRole: GlassRole?,
    val unread: Boolean,
    val flag: MailFlag,
    val recipients: String,
    val body: String,
    val photos: List<Int> = emptyList(),
)

val MailItems = listOf(
    Mail(
        0, "Maya Ortiz", "Shore walk photos", "Uploaded 24 from Saturday, the cobalt one is unreal.", "9:41", 0, null, true, MailFlag.Attachment,
        "to me, Jun · Today 9:41",
        "Hi both, I uploaded 24 photos from Saturday’s walk. The cobalt one near the north rocks is unreal, and Jun, your amber piece came out perfectly frosted. Want to make a shared album before the next meetup?",
        listOf(9, 5, 3, 1),
    ),
    Mail(
        1, "Jun Park", "Tide tables for Saturday", "Low at 6:40, high at 12:52. Meet by the north rocks?", "8:12", 3, null, true, MailFlag.None,
        "to me · Today 8:12", "Low tide is at 6:40 and high tide at 12:52. If we meet by the north rocks at 6:30 we get the whole hour of exposed sand. I will bring the thermos.",
    ),
    Mail(
        2, "North Rocks Club", "Meetup moved to 7:00", "Parking fills up early this week, so plan ahead.", "7:30", null, GlassRole.Kelly, true, MailFlag.None,
        "to all members · Today 7:30", "We moved the meetup to 7:00 because the lower car park is closed for resurfacing. Parking fills up early this week, so plan ahead and share rides where you can.",
    ),
    Mail(
        3, "Ines Silva", "Re: the blue jar pieces", "I sorted them by colour, there are more amber than blue.", "Yesterday", 5, null, false, MailFlag.Starred,
        "to me · Yesterday 18:20", "I sorted them by colour last night and there are more amber than blue, which surprised me. I took a photo of the jar if you want to count them yourself.",
        listOf(4, 6),
    ),
    Mail(
        4, "Theo Grant", "Album invite: Blue hour", "Theo shared an album with you. 12 photos, 3 videos.", "Yesterday", 2, null, false, MailFlag.None,
        "to me · Yesterday 12:05", "Theo shared an album with you: Blue hour, 12 photos and 3 videos from the pier. Open it in Photos to add your own.",
        listOf(2, 7, 8),
    ),
    Mail(
        5, "Sam Lee", "Found a cobalt piece!", "Look at this one, perfectly frosted on both sides.", "Mon", 9, null, false, MailFlag.Attachment,
        "to me · Mon 16:48", "Look at this one, perfectly frosted on both sides. It was half buried under the kelp near the second groyne.",
        listOf(9),
    ),
    Mail(
        6, "Tidepool Weekly", "Five beaches for winter glass", "Storm season brings the best finds to the surface.", "Sun", null, GlassRole.Lavender, false, MailFlag.None,
        "to subscribers · Sun 07:00", "Storm season brings the best finds to the surface. This week: five beaches where the swell turns over the old shoreline and the frosted pieces wash up at dawn.",
    ),
)
