package com.example.bexigram.models

import java.io.Serializable

class User: Serializable {
    var name:String? = null
    var uid:String? = null
    var photoUrl:String? = null
    var status:String? = null

    constructor()
    constructor(name: String?, uid: String?, photoUrl: String?) {
        this.name = name
        this.uid = uid
        this.photoUrl = photoUrl

    }
}
