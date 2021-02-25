package ru.isantur.santurshop.Data

import kotlinx.serialization.*

@Serializable
data class OrderList (

    var Orders: MutableList<Orders>
)


@Serializable
data class Orders (

    var ID: Int,
    var Title: String,
    var Code: String,
    var Name: String,
    var Descr: String,
    var SummOrder: String,
    var SummBase: String,
    var Discount: String,
    var QtyItems: String,
    var State: String,
    var StateTitleShort: String,
    var StateTitle: String,
    var StateColor: String,
    var StateDescr: String,
    var StateIco: String,
    var InfoStateItems: String,
    var ItemsDescr: String,
    var RegDate: String,
    var RegTime: String,
    var LastEdit: String,
    var NeedActualize: Boolean,
    var Details: Details

)

@Serializable
data class Details (

    var id: Int,
    var code: String,
    var regdate: String,
    var cost_goodies: Float,
    var cost_goodies_s: String,
    var cost_delivery: Float,
    var cost_delivery_s: String,
    var discount: Float,
    var discount_s: String,
    var summary: Float,
    var summary_s: String,
    var status: String,
    var receiving_method: String,
    var dcballs: String,
    var dcskid: String,
    var receiving_address: String,
    var payment_method: String,
    var client_name: String,
    var client_email: String,
    var client_phone: String,
    var city: String,
    var comment: String,
    var qty_goodies: Int,
    var items: MutableList<OrderItem>,

)

@Serializable
data class OrderItem (

    var code: String,
    var article: String,
    var name: String,
    var imgpath: String,
    var ed: String,
    var qty: Float,
    var realized: Float,
    var booking: Float,
    var incash: String,
    var price: Float,
    var price_s: String,
    var summ: Float,
    var summ_s: String

)





