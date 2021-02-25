package ru.isantur.santurshop.Data

import kotlinx.serialization.*

@Serializable
data class Company(

    var id: Int,
    var name: String,
    var city: String,
    var address: String,
    var phones: String,
    var gpscoords: String,
    var times: String,
    var payvariants: String,
    var ownerid: Int


)

