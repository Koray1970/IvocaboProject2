package com.example.ivocaboproject.database

import android.content.Context
import com.example.ivocaboproject.database.localdb.User
import com.example.ivocaboproject.database.localdb.UserViewModel
import com.parse.ParseUser


class ParseEvents {
    fun AddUser(user: User, userViewModel: UserViewModel): EventResult<String> {
        var eventResult = EventResult<String>("")
        try {
            if (ParseUser.getCurrentUser() != null)
                ParseUser.logOut()
            val parseUser = ParseUser()
            parseUser.email = user.email
            parseUser.username = user.username
            parseUser.setPassword(user.password)
            parseUser.signUp()
            if (parseUser.objectId != null) {
                user.objectId = parseUser.objectId.toString()
                userViewModel.addUser(user)
                eventResult.result = user.objectId.toString()
                eventResult.eventResultFlags = EventResultFlags.SUCCESS
            }
        } catch (exception: Exception) {
            eventResult.exception = exception
        }
        return eventResult
    }
    fun SingInUser(userViewModel: UserViewModel):EventResult<Boolean>{
        var eventResult=EventResult<Boolean>(false)
        try{
            var user=userViewModel.getUserDetail
            if(user!=null){
                ParseUser.logIn(user.username,user.password)
                if(ParseUser.getCurrentUser().isAuthenticated){
                    eventResult.result=true
                    eventResult.eventResultFlags=EventResultFlags.SUCCESS
                }
            }
        }
        catch (exception:Exception){
            eventResult.errorcode="SU100"
            eventResult.exception=exception
        }
        return eventResult
    }
}