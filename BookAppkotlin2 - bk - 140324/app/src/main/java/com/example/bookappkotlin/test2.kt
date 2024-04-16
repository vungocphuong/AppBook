package com.example.bookappkotlin

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlin.math.abs

fun cosineSimilarity(v1: DoubleArray, v2: DoubleArray): Double {
    require(v1.size == v2.size) { "Vectors must have the same length" }

    val dotProduct = v1.zip(v2).sumByDouble { (a, b) -> a * b }
    val normV1 = Math.sqrt(v1.sumByDouble { it * it })
    val normV2 = Math.sqrt(v2.sumByDouble { it * it })

    return dotProduct / (normV1 * normV2)
}
fun forecast(matrixY: Array<DoubleArray>){
    var matrixY_  = Array(matrixY.size) { DoubleArray(matrixY[0].size) }

    for (i in 0 until matrixY.size){
        for(j in 0 until matrixY[0].size){
            var sum: Double = 0.0
            var count: Double = 0.0
            for (k in 0 until matrixY[0].size){
                if(matrixY[i][k] != 0.0){
                    sum+=matrixY[i][k]
                    count += 1
                }
            }
            if(matrixY[i][j]!=0.0){
                matrixY_[i][j]=  "%.2f".format(matrixY[i][j]-sum/count).toDouble()
            }
        }
    }
    for (i in matrixY_.indices) {
        for (j in matrixY_[i].indices) {
            print(matrixY_[i][j])
            print(" ")
        }
        println()
    }
    println()
    // Tạo ma trận 2 chiều để lưu độ tương đồng cosine
    val matrixS = Array(matrixY.size) { DoubleArray(matrixY.size) }

    // Tính độ tương đồng cosine cho từng cặp hàng
    for (i in 0 until matrixY_.size) {
        for (j in 0 until matrixY_.size) {
            val similarity = cosineSimilarity(matrixY_[i], matrixY_[j])
            val roundedValue = "%.2f".format(similarity)
            matrixS[i][j] = roundedValue.toDouble()
        }
    }

    for (i in matrixS.indices) {
        for (j in matrixS[i].indices) {
            print(matrixS[i][j])
            print(" ")
        }
        println()
    }
    println()

    var matrixY__ = Array(matrixY.size) { DoubleArray(matrixY[0].size) }

    for (i in 0 until matrixY_.size){
        for (j in 0 until matrixY_[0].size ){
            if(matrixY_[i][j] == 0.0){
                var a: Double = 0.0
                var b: Double = 0.0
                var max1: Double=-1.0
                var max2: Double=-1.0
                var indexMax1: Int
                var indexMax2: Int
                for(k in 0 until matrixY_.size){
                    if(matrixY_[k][j] != 0.0){
                        if(matrixS[i][k] >= max1){
                            a =  matrixY_[k][j]
                            max1 = matrixS[i][k]
                            indexMax1 = k
                        }else if(matrixS[i][k] >= max2 && matrixS[i][k] <= max1){
                            b = matrixY_[k][j]
                            max2 = matrixS[i][k]
                            indexMax2 = k
                        }
                    }
                }

                matrixY__[i][j] = "%.2f".format((a*max1+b*max2)/(abs(a)+ abs(b))).toDouble()
            }else{
                matrixY__[i][j] = matrixY_[i][j]
            }
        }
    }

    for (i in matrixY__.indices) {
        for (j in matrixY__[i].indices) {
            print(matrixY__[i][j])
            print(" ")
        }
        println()
    }
    println()

    var matrixF  = Array(matrixY.size) { DoubleArray(matrixY[0].size) }

    for (i in 0 until matrixY__.size){
        for (j in 0 until  matrixY__[0].size){
            var sum: Double = 0.0
            var count: Double = 0.0
            for (k in 0 until matrixY[0].size){
                if(matrixY[i][k] != 0.0){
                    sum+=matrixY[i][k]
                    count += 1
                }
            }
            matrixF[i][j] = "%.2f".format(matrixY__[i][j]+sum/count).toDouble()
        }
    }

    for (i in matrixF.indices) {
        for (j in matrixF[i].indices) {
            print(matrixF[i][j])
            print(" ")
        }
        println()
    }
    println()
}
fun main() {
    val matrixY = arrayOf(
        /*doubleArrayOf(5.0, 5.0, 2.0, 0.0, 1.0, 0.0, 0.0),
        doubleArrayOf(4.0, 0.0, 0.0, 0.0, 0.0, 2.0, 0.0),
        doubleArrayOf(0.0, 4.0, 1.0, 0.0, 0.0, 1.0, 1.0),
        doubleArrayOf(2.0, 2.0, 3.0, 4.0, 4.0, 0.0, 4.0),
        doubleArrayOf(2.0, 0.0, 4.0, 0.0, 0.0, 0.0, 5.0),*/
        /*doubleArrayOf(2.0, 5.0, 0.0, 2.0, 3.0, 0.0, 5.0, 0.0),
        doubleArrayOf(3.0, 5.0, 1.0, 3.0, 5.0, 0.0, 2.0, 0.0),
        doubleArrayOf(4.0, 0.0, 1.0, 4.0, 4.0, 3.0, 3.0, 3.0),
        doubleArrayOf(1.0, 1.0, 0.0, 0.0, 2.0, 0.0, 3.0, 0.0),
        doubleArrayOf(0.0, 0.0, 5.0, 3.0, 3.0, 4.0, 2.0, 5.0),
        doubleArrayOf(4.0, 0.0, 2.0, 2.0, 0.0, 4.0, 2.0, 3.0),
        doubleArrayOf(5.0, 2.0, 0.0, 0.0, 1.0, 5.0, 5.0, 3.0),
        doubleArrayOf(3.0, 3.0, 0.0, 4.0, 4.0, 0.0, 3.0, 2.0),
        doubleArrayOf(0.0, 4.0, 2.0, 3.0, 5.0, 1.0, 5.0, 0.0),
        doubleArrayOf(5.0, 0.0, 2.0, 1.0, 3.0, 0.0, 0.0, 4.0)*/
        doubleArrayOf(5.0, 5.0, 4.0, 0.0),
        doubleArrayOf(0.0, 5.0, 4.0, 0.0),
        doubleArrayOf(1.0, 0.0, 3.0, 2.0),
        doubleArrayOf(5.0, 2.0, 0.0, 0.0),
        doubleArrayOf(0.0, 4.0, 5.0, 1.0),
        doubleArrayOf(4.0, 0.0, 2.0, 5.0),
        doubleArrayOf(1.0, 0.0, 4.0, 2.0)
    )
    var bids = arrayListOf("1710168452434", "1710168493287","1710168493290","1710168493300")
    var uids = arrayListOf("7vfKOOw6CuNdJNyDyS5uWG4TTy63", "82yHVzwerffP4BOhDHuzabpkByw1", "9AXCxGBY2rRed1D8BodjVrpoxt43", "OAkiwbmG3eOld3sehN4Pq4HwXXu1", "mLNaNrqD0sd1zZSDodEyRt1j3mq1", "tOmCuxFo5hYWZOOULs364K4gUBE2", "z3EjmbXGNjT5gm7ke0mEO726T9R2")
    var rebook: ArrayList<String>
    rebook = ArrayList()
    rebook = MyApplication1.recommend(matrixY,bids,uids,"82yHVzwerffP4BOhDHuzabpkByw1")
    print(rebook.toString())
    //forecast(matrixY)

    /*var matrixY_  = Array(matrixY.size) { DoubleArray(matrixY[0].size) }

    for (i in 0 until matrixY.size){
        for(j in 0 until matrixY[0].size){
            var sum: Double = 0.0
            var count: Double = 0.0
            for (k in 0 until matrixY[0].size){
                if(matrixY[i][k] != 0.0){
                    sum+=matrixY[i][k]
                    count += 1
                }
            }
            if(matrixY[i][j]!=0.0){
                matrixY_[i][j]=  "%.2f".format(matrixY[i][j]-sum/count).toDouble()
            }
        }
    }
    for (i in matrixY_.indices) {
        for (j in matrixY_[i].indices) {
            print(matrixY_[i][j])
            print(" ")
        }
        println()
    }
    println()
    // Tạo ma trận 2 chiều để lưu độ tương đồng cosine
    val matrixS = Array(matrixY.size) { DoubleArray(matrixY.size) }

    // Tính độ tương đồng cosine cho từng cặp hàng
    for (i in 0 until matrixY_.size) {
        for (j in 0 until matrixY_.size) {
            val similarity = cosineSimilarity(matrixY_[i], matrixY_[j])
            val roundedValue = "%.2f".format(similarity)
            matrixS[i][j] = roundedValue.toDouble()
        }
    }

    for (i in matrixS.indices) {
        for (j in matrixS[i].indices) {
            print(matrixS[i][j])
            print(" ")
        }
        println()
    }
    println()

    var matrixY__ = Array(matrixY.size) { DoubleArray(matrixY[0].size) }

    for (i in 0 until matrixY_.size){
        for (j in 0 until matrixY_[0].size ){
            if(matrixY_[i][j] == 0.0){
                var a: Double = 0.0
                var b: Double = 0.0
                var max1: Double=-1.0
                var max2: Double=-1.0
                var indexMax1: Int
                var indexMax2: Int
                for(k in 0 until matrixY_.size){
                     if(matrixY_[k][j] != 0.0){
                         if(matrixS[i][k] >= max1){
                             a =  matrixY_[k][j]
                             max1 = matrixS[i][k]
                             indexMax1 = k
                         }else if(matrixS[i][k] >= max2 && matrixS[i][k] <= max1){
                             b = matrixY_[k][j]
                             max2 = matrixS[i][k]
                             indexMax2 = k
                         }
                     }
                }

                matrixY__[i][j] = "%.2f".format((a*max1+b*max2)/(abs(a)+ abs(b))).toDouble()
            }else{
                matrixY__[i][j] = matrixY_[i][j]
            }
        }
    }

    for (i in matrixY__.indices) {
        for (j in matrixY__[i].indices) {
            print(matrixY__[i][j])
            print(" ")
        }
        println()
    }
    println()

    var matrixF  = Array(matrixY.size) { DoubleArray(matrixY[0].size) }

    for (i in 0 until matrixY__.size){
        for (j in 0 until  matrixY__[0].size){
            var sum: Double = 0.0
            var count: Double = 0.0
            for (k in 0 until matrixY[0].size){
                if(matrixY[i][k] != 0.0){
                    sum+=matrixY[i][k]
                    count += 1
                }
            }
            matrixF[i][j] = "%.2f".format(matrixY__[i][j]+sum/count).toDouble()
        }
    }

    for (i in matrixF.indices) {
        for (j in matrixF[i].indices) {
            print(matrixF[i][j])
            print(" ")
        }
        println()
    }
    println()*/
}
