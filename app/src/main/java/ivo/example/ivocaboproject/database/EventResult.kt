package ivo.example.ivocaboproject.database

import kotlin.properties.Delegates

class EventResult<T>(t: T) {
    var result = t
    var eventResultFlags: EventResultFlags = EventResultFlags.FAILED
    lateinit var errorcode: String
    lateinit var errormessage: String
    lateinit var exception: Exception
}