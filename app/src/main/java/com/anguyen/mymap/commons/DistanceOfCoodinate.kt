package com.anguyen.mymap.commons

import kotlin.math.pow
import kotlin.math.sqrt

fun DistanceOfCoodinate(x1: Double, x2: Double, y1: Double, y2: Double): Double {
    return sqrt((x1 - x2).pow(2.0) + (y1 - y2).pow(2.0))
}