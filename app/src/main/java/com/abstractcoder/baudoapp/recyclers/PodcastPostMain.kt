package com.abstractcoder.baudoapp.recyclers

import android.net.Uri
import com.google.firebase.Timestamp

data class PodcastPostMain(var thumbnail: Uri, var title: String, var timestamp: Timestamp, var description: String, var media: Uri)
