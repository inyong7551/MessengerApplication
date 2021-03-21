package com.example.chattingapp.model

class UserModel{
    var userName: String
    var profileImageUrl: String
    var uid: String

    constructor(userName: String, profileImageUrl: String, uid: String){
        this.userName = userName
        this.profileImageUrl = profileImageUrl
        this.uid = uid
    }
}