package com.example.bexigram.models

import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class Message : Serializable {
    var text: String? = null
    var imageUri: String? = null
    var fromUserUid: String? = null
    var toUserUid: String? = null
    var date: String? = null
    var status: String? = null // Onlayn yoki oflayn holat

    constructor()
    constructor(text: String?, fromUserUid: String?, toUserUid: String?, status: String?) {
        this.text = text
        this.fromUserUid = fromUserUid
        this.toUserUid = toUserUid
        this.date = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault()).format(Date())
        this.status = status
    }


    override fun toString(): String {
        return "Message(text=$text, fromUserUid=$fromUserUid, toUserUid=$toUserUid, date=$date, status=$status)"
    }
}
