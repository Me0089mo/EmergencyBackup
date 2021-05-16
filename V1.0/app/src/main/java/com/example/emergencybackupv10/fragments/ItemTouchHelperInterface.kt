package com.example.emergencybackupv10.fragments

interface ItemTouchHelperInterface {
    fun onItemMove(from : Int, to : Int)
    fun onItemSwipe(position: Int)
}