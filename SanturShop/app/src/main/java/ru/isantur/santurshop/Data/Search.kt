package ru.isantur.santurshop.Dataimport kotlinx.serialization.*@Serializabledata class tntks (    val tntks: ArrayList<Search>)@Serializabledata class Search(    val nn: Int,    val id: Int,    val name: String,    val kt: String,    val weight: Int,    val parent_id: Int,    val parent_name: String)