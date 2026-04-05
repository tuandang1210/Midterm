package com.example.midterm

data class Product(
    var id: String = "",
    var name: String = "",
    var category: String = "",
    var price: String = "",
    var imageUri: String = ""
)
{
    constructor() : this("", "", "", "", "")
}