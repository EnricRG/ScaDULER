package model

import app.AppSettings

class CourseResource(val name: String, var quantity: Int) {
    def getName: String = name
    def getQuantity: Int = quantity
    def incrementQuantity(inc: Int): Unit = quantity += inc
    def decrementQuantity(inc: Int): Unit = if(quantity-inc >= AppSettings.minQuantityPerResource) quantity -= inc
}
