package com.example.emergencybackupv10.utils

import java.sql.Time

class UserActivity(val descripcion:String, val fecha:Time, val detalles:String) {
    var tiempo:String = ""
        set(fecha){
            field = fecha.toString()
        }

}