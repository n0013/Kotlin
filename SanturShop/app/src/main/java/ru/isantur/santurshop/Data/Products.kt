package ru.isantur.santurshop.Data

import android.os.Parcel
import android.os.Parcelable
import kotlinx.serialization.*

@Serializable
data class Products (

    var search: String,
    var page_size: Int,
    var current_page: Int,
    var qty_records: Int,
    var qty_pages: Int,
    var order_by: String,
    var incash: Boolean,
    var tn_id: Int,
    var tk_id: Int,
    var only_znaks: String,
    var ListGoods: MutableList<ListGoods>,
    var GoodFilters: MutableList<GoodFilters>


) {
    init {

    }
}

@Serializable
data class ListGoods (

    var code: String,
    var ens: String,
    var name: String,
    var imgpath: String,
    var price: Float,
    var prices: String,
    var ed: String,
    var zn: String,
    var an_k: Int,
    var cartgr_id: Int,
    var brend: String,
    var article: String,
    var tn_id: Int,
    var tk_id: Int,
    var salekrat: Float,
    var qty_incart: Float,
    var incash: Float,
    var showincash: Boolean,
    var incash_info: String,
    var isfavority: Boolean,
    var tk_name: String


)

@Serializable
data class GoodFilters(

    var nn: Int,
    var Name: String,
    var TypeValue: String,
    var IsNumeric: Boolean,
    var MinLimit: String,
    var MaxLimit: String,
    var MinSelect: String,
    var MaxSelect: String,
    var Selected: String,

    var Items: ArrayList<GoodFiltersItems>

)

@Serializable
data class GoodFiltersItems(
    var nn: Int,
    var Name: String,
    var QtyRecords: Int
)

