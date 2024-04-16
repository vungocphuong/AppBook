package com.example.bookappkotlin.models

import android.icu.text.CaseMap.Title

class ModelViewedBook {
    //variables, should be with same spelling and type as we added in firebase
    var id = ""
    var bookId = ""
    var timestamp = ""
    var uid = ""
    var title:String = ""
    var description:String = ""
    var categoryId:String = ""

    //empty constructor, required by firebase
    constructor()

    //param constructor
    constructor(id: String, bookId: String, timestamp: String, uid: String, title: String, description: String, categoryId: String) {
        this.id = id
        this.bookId = bookId
        this.timestamp = timestamp
        this.uid = uid
        this.title = title
        this.description = description
        this.categoryId = categoryId
    }
}