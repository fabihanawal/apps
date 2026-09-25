package com.example.amardokan.data.local

import androidx.room.TypeConverter
import com.example.amardokan.model.OrderItem
import org.json.JSONArray
import org.json.JSONObject

class Converters {
    @TypeConverter
    fun fromOrderItemList(items: List<OrderItem>?): String {
        if (items.isNullOrEmpty()) return "[]"
        val array = JSONArray()
        for (item in items) {
            val obj = JSONObject()
            obj.put("productId", item.productId)
            obj.put("productName", item.productName)
            obj.put("quantity", item.quantity)
            obj.put("price", item.price)
            obj.put("imageUrl", item.imageUrl)
            array.put(obj)
        }
        return array.toString()
    }

    @TypeConverter
    fun toOrderItemList(data: String?): List<OrderItem> {
        if (data.isNullOrEmpty()) return emptyList()
        val list = mutableListOf<OrderItem>()
        try {
            val array = JSONArray(data)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    OrderItem(
                        productId = obj.optString("productId"),
                        productName = obj.optString("productName"),
                        quantity = obj.optInt("quantity", 1),
                        price = obj.optDouble("price", 0.0),
                        imageUrl = obj.optString("imageUrl")
                    )
                )
            }
        } catch (_: Exception) {}
        return list
    }
}
