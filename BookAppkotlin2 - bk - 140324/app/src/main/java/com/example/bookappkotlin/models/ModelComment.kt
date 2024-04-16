package com.example.bookappkotlin.models

class ModelComment {

    //variables, should be with same spelling and type as we added in firebase
    var id = ""
    var bookId = ""
    var timestamp = ""
    var comment = ""
    var uid = ""
    var rating: Int = 0

    //empty constructor, required by firebase
    constructor()

    //param constructor
    constructor(id: String, bookId: String, timestamp: String, comment: String, uid: String, rating: Int) {
        this.id = id
        this.bookId = bookId
        this.timestamp = timestamp
        this.comment = comment
        this.uid = uid
        this.rating = rating
    }

}