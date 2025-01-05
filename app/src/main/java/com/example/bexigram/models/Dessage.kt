package com.example.bexigram.models

data class Dessage(
    val text: String = "",
    val fileUrl: String = "",
    val senderUid: String = "",
    val receiverUid: String = "",
    val date: String = "",
    val type: String = "text" // "text", "image", "video", "audio"
)
