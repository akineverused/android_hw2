package com.example.foodgram.data.local

import androidx.room.TypeConverter

class RecipeConverters {

    @TypeConverter
    fun fromStringList(value: List<String>): String {
        return value.joinToString("|||")
    }

    @TypeConverter
    fun toStringList(value: String): List<String> {
        return if (value.isEmpty()) {
            emptyList()
        } else {
            value.split("|||")
        }
    }
}