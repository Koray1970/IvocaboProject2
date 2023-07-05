package ivo.example.ivocaboproject.database

import kotlin.properties.Delegates

class EventResult<T>(t: T) {
    var result = t
    var eventResultFlags: EventResultFlags = EventResultFlags.FAILED
    var errorcode: String? = null
    var errormessage: String? = null
    var exception: Exception? = null
}