package ru.isantur.santurshop.Data

import kotlinx.serialization.*

@Serializable
data class Banners(

    var img: String,
    var link: String,
    var title: String
//    var tk_name: String,
//    var tk_id: Int,
//    var tn_id: Int

)