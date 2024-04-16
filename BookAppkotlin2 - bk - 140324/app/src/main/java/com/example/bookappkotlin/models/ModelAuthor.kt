package com.example.bookappkotlin.models

import android.provider.ContactsContract.CommonDataKinds.Email
import androidx.datastore.preferences.protobuf.Timestamp

class ModelAuthor {
    //variables, must match as in firebase
    var id:String = ""
    var name:String = ""
    var phone:String = ""
    var email:String = ""
    var dob:String = ""
    var timestamp: Long = 0
    var rating: Long = 0
    constructor()

    constructor(id: String, name: String, phone: String, email: String, dob: String, timestamp: Long, rating: Long) {
        this.id = id
        this.name = name
        this.phone = phone
        this.email = email
        this.dob = dob
        this.timestamp = timestamp
        this.rating = rating
    }
}