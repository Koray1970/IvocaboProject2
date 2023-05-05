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
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.Date

class ParseEvents {
    fun AddUser(context:Context, user: User):EventResult<String>{
        var eventResult=EventResult<String>("")
        try{
            val parseUser=ParseUser()
            parseUser.email=user.email
            parseUser.username=user.username
            parseUser.setPassword(user.password)
            parseUser.signUpInBackground(SignUpCallback(){
                if(it==null){
                    eventResult.result= parseUser.objectId.toString()
                    val appContainer=AppDataContainer(context)
                    user.objectId=parseUser.objectId
                    runBlocking {
                        launch {
                            appContainer.userRepository.insert(user)
                            eventResult.eventResultFlags=EventResultFlags.SUCCESS
                        }
                    }

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