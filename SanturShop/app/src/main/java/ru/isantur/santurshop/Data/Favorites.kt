package ru.isantur.santurshop.Data

import kotlinx.serialization.*

@Serializable
data class Favorites(

    var code: String,
    var ens: String,
    var name: String,
    var imgpath: String,
    var price: Double,
    var prices: String,
    var ed: String,
    var zn: String,
    var an_k: Int,
    var cartgr_id: Int,
    var brend: String,
    var article: String,
    var tn_id: Int,
    var tk_id: Int,
    var salekrat: Double,
    var qty_incart: Double,
    var incash: Double,
    var showincash: Boolean,
    var incash_info: String,
    var isfavority: Boolean,
    var tk_name: String



)