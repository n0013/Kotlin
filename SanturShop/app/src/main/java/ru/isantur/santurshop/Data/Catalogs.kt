package ru.isantur.santurshop.Data

import kotlinx.serialization.*

@Serializable
data class Catalogs(

    var id: Int,
    var parent_id: Int,
    var parent_name: String,
    var vid: String,
    var name: String,
    var num: Int


)