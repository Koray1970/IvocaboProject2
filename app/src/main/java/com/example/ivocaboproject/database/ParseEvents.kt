package com.example.ivocaboproject.database

import android.content.Context
import com.example.ivocaboproject.database.localdb.AppContainer
import com.example.ivocaboproject.database.localdb.AppDataContainer
import com.example.ivocaboproject.database.localdb.User
import com.example.ivocaboproject.database.localdb.UserOfflineRepository
import com.example.ivocaboproject.database.localdb.UserRepository
import com.example.ivocaboproject.database.localdb.userDao
import com.parse.ParseException
import com.parse.ParseUser
import com.parse.SignUpCallback
import java.util.Date

class ParseEvents {
    fun AddUser(context:Context, parseUser: ParseUser):EventResult<String>{
        var eventResult=EventResult<String>("")
        try{
            parseUser.signUpInBackground(SignUpCallback(){
                if(it==null){
                    eventResult.result= parseUser.objectId.toString()
                    val appContainer=AppDataContainer(context)
                    appContainer.userRepository.insert(User(0,Date().time,))



                    eventResult.eventResultFlags=EventResultFlags.SUCCESS
                }
                else{
                    eventResult.errorcode=it.code.toString()
                    eventResult.errormessage=it.message.toString()
                }
            })
        }
        catch (exception:Exception){
            eventResult.exception=exception
        }
        return eventResult
    }
}