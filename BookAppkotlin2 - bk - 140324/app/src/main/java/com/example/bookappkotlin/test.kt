package com.example.bookappkotlin

import kotlin.math.*

/*fun cosineSimilarity(v1: DoubleArray, v2: DoubleArray): Double {
    require(v1.size == v2.size) { "Vectors must have the same length" }

    val dotProduct = v1.zip(v2).sumByDouble { (a, b) -> a * b }
    val normV1 = Math.sqrt(v1.sumByDouble { it * it })
    val normV2 = Math.sqrt(v2.sumByDouble { it * it })

    return dotProduct / (normV1 * normV2)
}*/


fun main() {
    val matrix = arrayOf(
        doubleArrayOf(2.0, 5.0, 0.0, 2.0, 3.0, 0.0, 5.0, 0.0),
        doubleArrayOf(3.0, 5.0, 1.0, 3.0, 5.0, 0.0, 2.0, 0.0),
        doubleArrayOf(4.0, 0.0, 1.0, 4.0, 4.0, 3.0, 3.0, 3.0),
        doubleArrayOf(1.0, 1.0, 0.0, 0.0, 2.0, 0.0, 3.0, 0.0),
        doubleArrayOf(0.0, 0.0, 5.0, 3.0, 3.0, 4.0, 2.0, 5.0),
        doubleArrayOf(4.0, 0.0, 2.0, 2.0, 0.0, 4.0, 2.0, 3.0),
        doubleArrayOf(5.0, 2.0, 0.0, 0.0, 1.0, 5.0, 5.0, 3.0),
        doubleArrayOf(3.0, 3.0, 0.0, 4.0, 4.0, 0.0, 3.0, 2.0),
        doubleArrayOf(0.0, 4.0, 2.0, 3.0, 5.0, 1.0, 5.0, 0.0),
        doubleArrayOf(5.0, 0.0, 2.0, 1.0, 3.0, 0.0, 0.0, 4.0)
    )

    for (row in matrix) {
        for (element in row) {
            print("$element ")
        }
        println()
    }
    // Tạo ma trận 2 chiều để lưu độ tương đồng cosine
    val similarityMatrix = Array(matrix.size) { DoubleArray(matrix[0].size) }

    // Tính độ tương đồng cosine cho từng cặp hàng
    for (i in 0 until matrix.size) {
        for (j in 0 until matrix[0].size) {
            val similarity = cosineSimilarity(matrix[i], matrix[j])
            val roundedValue = "%.2f".format(similarity)
            similarityMatrix[i][j] = roundedValue.toDouble()
        }
    }

    // In ma trận độ tương đồng cosine
    for (i in similarityMatrix.indices) {
        for (j in similarityMatrix[i].indices) {
            print(similarityMatrix[i][j])
            print(" ")
        }
        println()
    }

    val s = Array(matrix.size) { DoubleArray(matrix[0].size) }
    for (i in s.indices){
        for(j in s[i].indices){
            print(s[i][j])
            print(" ")
        }
        println()
    }
}