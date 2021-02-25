package ru.isantur.santurshop.Data

import kotlinx.serialization.*

@Serializable
data class Sales(

    var code: String,
    var name: String,
    var imgpath: String,
    var prices: String,
    var ed: String,
    var brend: String,
    var article: String,
    var tn_id: Int,
    var tn_name: String,
    var tk_id: Int,
    var tk_name: String,
    var salekrat: Double,
    var qty_incart: Double,
    var isfavority: Boolean


)