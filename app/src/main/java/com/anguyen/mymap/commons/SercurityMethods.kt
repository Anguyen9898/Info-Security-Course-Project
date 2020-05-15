package com.anguyen.mymap.commons

import com.anguyen.mymap.models.alphaBetDetail
import com.anguyen.mymap.models.numbers

fun String.encoding(k: Int): String{
    var encodedCompletelyString = ""
    var encodeChar = ""

    this.forEach{ char ->
        encodeChar = if(char.isNumeric()){
            alphaBetDetail[char.toString().toInt()]
        }else{
            char.toString()
        }
        encodedCompletelyString += if(alphaBetDetail.indexOf(encodeChar) >= 0){ // is in alphabet
            alphaBetDetail[encodeFunction(alphaBetDetail.indexOf(encodeChar), k)]
        }else{ //not a number and not in alphabet
            encodeChar
        }
    }

    return  encodedCompletelyString
}

fun String.decoding(k: Int): String{
    var decodedCompletelyString = ""

    this.forEach{ char ->
        decodedCompletelyString += if(alphaBetDetail.indexOf(char.toString()) >= 0){ // is in alphabet
            alphaBetDetail[decodeFunction(alphaBetDetail.indexOf(char.toString()), k)]
        }else{ //not a number and not in alphabet
            char
        }
    }
    return  decodedCompletelyString
}

private fun encodeFunction(p: Int, k: Int): Int {
    return (p + k) % 26
}

private fun decodeFunction(C: Int, k: Int): Int {
    var minus = C - k
    while(minus < 0){
        minus += 26
    }
    return minus % 26
}

private fun Char?.isNumeric() = numbers.any { this.toString() == it }
//private fun String.isInAlphaBet() = alphaBetDetail.any { it == this }