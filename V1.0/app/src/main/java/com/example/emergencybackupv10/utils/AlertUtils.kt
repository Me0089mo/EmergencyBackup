package com.example.emergencybackupv10.utils

import android.content.Context
import android.view.Gravity
import android.widget.Toast

class AlertUtils constructor(){
    fun topToast(context: Context,mssg:String){
        val toast = Toast.makeText(
            context,
            mssg,
            Toast.LENGTH_SHORT
        )
        toast.setGravity(Gravity.TOP, 0, 0)
        toast.show()
    }

    fun topToastLong(context: Context,mssg:String){
        val toast = Toast.makeText(
            context,
            mssg,
            Toast.LENGTH_LONG
        )
        toast.setGravity(Gravity.TOP, 0, 0)
        toast.show()
    }
}