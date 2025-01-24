package com.example.bexigram.models

import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class Message : Serializable {
    var id: String? = null
    var text: String? = null
    var imageUri: String? = null
    var fromUserUid: String? = null
    var toUserUid: String? = null
    var date: String? = null
    var status: String? = null // Xabar holati (jo'natildi/qabul qilindi)
    var edited: Boolean = false // Xabar tahrirlanganmi yoki yo'qmi

    constructor(text: String?, fromUserUid: String?, toUserUid: String?, status: String?) {
        this.text = text
        this.fromUserUid = fromUserUid
        this.toUserUid = toUserUid
        this.date = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault()).format(Date())
        this.status = status
    }

    constructor()

    override fun toString(): String {
        return "Message(id=$id, text=$text, fromUserUid=$fromUserUid, toUserUid=$toUserUid, date=$date, status=$status, edited=$edited)"
    }
}
