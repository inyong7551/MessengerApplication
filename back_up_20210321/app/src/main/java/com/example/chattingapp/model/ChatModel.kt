package com.example.chattingapp.model

class ChatModel{
    var users: MutableMap<String, Boolean> = mutableMapOf() // 채팅방의 유저들
    var comments: MutableMap<String, Comment> = mutableMapOf() // 채팅방의 내용

    class Comment(uid: String, message: String){
        var uid: String
        var message: String

        init {
            this.uid = uid
            this.message = message
        }
    }
}