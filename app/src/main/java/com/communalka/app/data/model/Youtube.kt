package com.communalka.app.data.model

data class ParentYoutube (
    val kind: String,
    val etag: String,
    val nextPageToken: String,
    val items: List<Item>,
    val pageInfo: PageInfo
)

data class Item (
    val kind: String,
    val etag: String,
    val id: String,
    val snippet: Snippet
)

data class Snippet (
    val publishedAt: String,

    val channelId: String,

    val title: String,
    val description: String,
    val thumbnails: Thumbnails,
    val channelTitle: String,

    val playlistId: String,

    val position: Long,


    val resourceId: ResourceID,

    val videoOwnerChannelTitle: String,


    val videoOwnerChannelId: String
)

data class ResourceID (
    val kind: String,

    val videoId: String
)

data class Thumbnails (
    val default: Default,
    val medium: Default,
    val high: Default
)

data class Default (
    val url: String,
    val width: Long,
    val height: Long
)

data class PageInfo (
    val totalResults: Long,
    val resultsPerPage: Long
)