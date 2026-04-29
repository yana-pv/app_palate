package com.example.domain.model

import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.PropertyName

data class ShoppingItem(
    @get:Exclude @set:Exclude
    var id: String = "",
    
    val name: String = "",
    val amount: String = "",
    val unit: String = "",
    
    @get:PropertyName("isChecked")
    @set:PropertyName("isChecked")
    var isChecked: Boolean = false,

    val userId: String = ""
)
