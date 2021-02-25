package ru.isantur.santurshop.Data

import kotlinx.serialization.*

@Serializable
data class Cart(
    var id: Int,
    var summ: Float,
    var summs: String,
    var weight: Float,
    var qtyitems: Int,
    var items: MutableList<CartItems>
)

@Serializable
data class CartItems (

    var code: String,
    var article: String,
    var name: String,
    var brend: String,
    var tn_id: Int,
    var tk_id: Int,
    var salekrat: Float,
    var zn: String,
    var incash: Float,
    var incash_info: String,
    var imgpath: String,
    var qty: Float,
    var ed: String,
    var price: Float,
    var summ: Float,
    var summbase: Float,
    var prices: String,
    var summs: String,
    var summbases: String,
    var isfafority: Boolean

)