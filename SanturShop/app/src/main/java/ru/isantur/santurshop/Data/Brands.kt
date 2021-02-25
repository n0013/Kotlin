package ru.isantur.santurshop.Data

import kotlinx.serialization.*

@Serializable
data class Brands(

    var img: String,
    var link: String,
    var title: String
//    var tn_name: String,
//    var tk_id: Int,
//    var tk_name: String,
//    var brend: String


)