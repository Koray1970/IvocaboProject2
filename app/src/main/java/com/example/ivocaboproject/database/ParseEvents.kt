package com.example.ivocaboproject.database

import com.parse.ParseException
import com.parse.ParseUser
import com.parse.SignUpCallback

class ParseEvents {
    fun AddUser(parseUser: ParseUser):EventResult<String>{
        var eventResult=EventResult<String>("")
        try{
            parseUser.signUpInBackground(SignUpCallback(){
                if(it==null){
                    eventResult.result= parseUser.objectId.toString()
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