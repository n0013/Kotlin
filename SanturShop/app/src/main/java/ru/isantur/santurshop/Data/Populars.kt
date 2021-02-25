package ru.isantur.santurshop.Data

import kotlinx.serialization.*

@Serializable
data class Populars(

    var nn: Int,
    var tk_id: Int,
    var name: String,
    var tn_id: Int,
    var tn_name: String,
    var imgpath: String

)