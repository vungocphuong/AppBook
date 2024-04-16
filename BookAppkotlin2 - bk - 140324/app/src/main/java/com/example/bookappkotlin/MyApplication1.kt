
package com.example.bookappkotlin

import android.app.Application
import android.app.ProgressDialog
import android.content.Context
import android.text.format.DateFormat
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.example.bookappkotlin.activities.PdfDetailActivity
import com.github.barteksc.pdfviewer.PDFView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.storageMetadata
import java.sql.Timestamp
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.abs

class MyApplication1: android.app.Application() {

    override fun onCreate() {
        super.onCreate()
    }

    companion object{
        fun cosineSimilarity(v1: DoubleArray, v2: DoubleArray): Double {
            require(v1.size == v2.size) { "Vectors must have the same length" }

            val dotProduct = v1.zip(v2).sumByDouble { (a, b) -> a * b }
            val normV1 = Math.sqrt(v1.sumByDouble { it * it })
            val normV2 = Math.sqrt(v2.sumByDouble { it * it })

            return dotProduct / (normV1 * normV2)
        }

        fun recommend(matrixY: Array<DoubleArray>, bids: ArrayList<String>, uids: ArrayList<String>, uid: String): ArrayList<String> {
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
            /*for (i in matrixY_.indices) {
                for (j in matrixY_[i].indices) {
                    print(matrixY_[i][j])
                    print(" ")
                }
                println()
            }
            println()*/
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

            /*for (i in matrixS.indices) {
                for (j in matrixS[i].indices) {
                    print(matrixS[i][j])
                    print(" ")
                }
                println()
            }
            println()*/

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

                        matrixY__[i][j] = "%.2f".format((a*max1+b*max2)/(abs(a) + abs(b))).toDouble()
                    }else{
                        matrixY__[i][j] = matrixY_[i][j]
                    }
                }
            }

            /*for (i in matrixY__.indices) {
                for (j in matrixY__[i].indices) {
                    print(matrixY__[i][j])
                    print(" ")
                }
                println()
            }
            println()*/

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

            var index = uids.indexOf(uid).toInt()

            var booksRecommend: ArrayList<String>
            booksRecommend = ArrayList()

            for(j in 0 until matrixF[index].size){
                if(matrixF[index][j] >=3.0 && matrixY[index][j] == 0.0){
                    booksRecommend.add(bids[j])
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
            return booksRecommend
        }
    }
}