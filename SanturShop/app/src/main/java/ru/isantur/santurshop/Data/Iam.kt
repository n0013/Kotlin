package ru.isantur.santurshop.Data

import kotlinx.serialization.*

@Serializable
data class Iam(

    var id: Int,
    var email: String,
    var phone: String,
    var name: String,
    var dc: String,
    var subject_inn: String,
    var subject_name: String,
    var subject_id: Int,
    var city: String,
    var cityid: Int

)

