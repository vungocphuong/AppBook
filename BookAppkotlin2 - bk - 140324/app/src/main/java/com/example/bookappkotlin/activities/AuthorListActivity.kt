package com.example.bookappkotlin.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import com.example.bookappkotlin.adapters.AdapterAuthorAdmin
import com.example.bookappkotlin.databinding.ActivityAuthorListBinding
import com.example.bookappkotlin.models.ModelAuthor
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AuthorListActivity : AppCompatActivity() {

    //view binding
    private lateinit var binding: ActivityAuthorListBinding

    private companion object{
        const val TAG = "AUTHOR_LIST_ADMIN_TAG"
    }

    private lateinit var authorArrayList:ArrayList<ModelAuthor>
    private lateinit var adapterAuthorAdmin: AdapterAuthorAdmin


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthorListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadAuthor()

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
                    adapterAuthorAdmin.filter.filter(s)
                }
                catch (e: Exception){

                }
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })

    }

    private fun loadAuthor() {
        authorArrayList = ArrayList()
        val ref = FirebaseDatabase.getInstance().getReference("Authors")
        ref.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                authorArrayList.clear()
                for (ds in snapshot.children){
                    val model = ds.getValue(ModelAuthor::class.java)
                    authorArrayList.add(model!!)
                }

                //setup adapter
                adapterAuthorAdmin = AdapterAuthorAdmin(this@AuthorListActivity, authorArrayList)
                binding.authorRv.adapter = adapterAuthorAdmin

            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }
}