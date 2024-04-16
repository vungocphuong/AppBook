package com.example.bookappkotlin.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import com.example.bookappkotlin.R
import com.example.bookappkotlin.adapters.AdapterAuthor
import com.example.bookappkotlin.adapters.AdapterPdfViewed
import com.example.bookappkotlin.databinding.ActivityRecommendAuthorBinding
import com.example.bookappkotlin.models.ModelAuthor
import com.example.bookappkotlin.models.ModelPdf
import com.google.android.datatransport.runtime.firebase.transport.LogEventDropped
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class RecommendAuthorActivity : AppCompatActivity() {

    //view binding
    private lateinit var binding: ActivityRecommendAuthorBinding

    //firebase auth
    private lateinit var firebaseAuth: FirebaseAuth

    //arraylist to hold authors
    private lateinit var authorsArrayList: ArrayList<ModelAuthor>
    private lateinit var adapterAuthor: AdapterAuthor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecommendAuthorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadRatingAuthor()

        binding.backBtn.setOnClickListener {
            onBackPressed()
        }
        //search
        binding.searchEt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                //called as and when user type anything
                try{
                    adapterAuthor.filter.filter(s)
                }
                catch (e: Exception){

                }
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })

    }
    lateinit var idAuthorArrayList: ArrayList<String>
    private fun loadRatingAuthor() {
        authorsArrayList = ArrayList()
        idAuthorArrayList = ArrayList()
        val ref = FirebaseDatabase.getInstance().getReference("Books")
        ref.orderByChild("-viewsCount")
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    authorsArrayList.clear()
                    idAuthorArrayList.clear()
                    for (ds in snapshot.children){
                        //get only id of the book, rest of the info we have loaded in adapter class
                        val authorId = "${ds.child("authorId").value}"

                        //set to model
                        val modelAuthor = ModelAuthor()
                        modelAuthor.id = authorId
                        modelAuthor.rating = (idAuthorArrayList.size + 1).toLong()

                        if(authorId in idAuthorArrayList == false){
                            authorsArrayList.add(modelAuthor)
                            idAuthorArrayList.add(authorId)
                        }

                        Log.d("id 4/9", "onDataChange: $authorId")
                    }
                    Log.d("4/9 toi dang o recom", "onDataChange: okkk")
                    adapterAuthor = AdapterAuthor(this@RecommendAuthorActivity, authorsArrayList)
                    binding.authorRv.adapter =  adapterAuthor
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
    }
}