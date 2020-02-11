package com.example.musicplayer


fun main() {
    val pro = 83
    val f = 155000 / 1000.0
    var current = (f * pro) / 100.0
    current *= 1000 //ms
    println(current.toLong())
}