package com.example.bookappkotlin.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.example.bookappkotlin.databinding.RowAuthorRatingBinding
import com.example.bookappkotlin.databinding.RowPdfViewedBinding
import com.example.bookappkotlin.filters.FilterAuthor
import com.example.bookappkotlin.filters.FilterPdfUser
import com.example.bookappkotlin.models.ModelAuthor
import com.example.bookappkotlin.models.ModelPdf
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AdapterAuthor : RecyclerView.Adapter<AdapterAuthor.HolderAuthor>, Filterable {
    //Context
    private val context: Context

    public var authorsArrayList: ArrayList<ModelAuthor>

    private lateinit var binding: RowAuthorRatingBinding

    private var filterList: ArrayList<ModelAuthor>
    private var filter: FilterAuthor? = null

    constructor(context: Context, authorsArrayList: ArrayList<ModelAuthor>) {
        this.context = context
        this.authorsArrayList = authorsArrayList
        this.filterList = authorsArrayList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderAuthor {
        //bind/inflate row_author_rating.xml
        binding = RowAuthorRatingBinding.inflate(LayoutInflater.from(context), parent, false)
        return HolderAuthor(binding.root)
    }

    override fun onBindViewHolder(holder: HolderAuthor, position: Int) {
        Log.d("4/9 adapter", "onBindViewHolder: okk")
        val model = authorsArrayList[position]
        loadAuthorDetails(model, holder, position)
    }

    private fun loadAuthorDetails(model: ModelAuthor, holder: AdapterAuthor.HolderAuthor, position: Int) {
        val authorId = model.id
        val ref = FirebaseDatabase.getInstance().getReference("Authors")
        ref.child(authorId)
            .addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {

                    val name = "${snapshot.child("name").value}"
                    Log.d("4/9 toi dang check", "onDataChange: $name")
                    val phone = "${snapshot.child("phone").value}"
                    val email = "${snapshot.child("email").value}"
                    val dob = "${snapshot.child("dob").value}"
                    val timestamp = "${snapshot.child("timestamp").value}"
                    val id = "${snapshot.child("id").value}"

                    model.name = name
                    model.phone = phone
                    model.timestamp = timestamp.toLong()
                    model.email = email
                    model.dob = dob
                    model.id = id

                    holder.rating.text = model.rating.toString()
                    holder.name.text = name
                    holder.phone.text = phone
                    holder.email.text = email
                    holder.dob.text = dob
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
    }

    override fun getItemCount(): Int {
        return authorsArrayList.size
    }

    /*View holder class to manage UI views of row_author_rating.xml*/
    inner class HolderAuthor(itemView: View): RecyclerView.ViewHolder(itemView){
        //init UI Views
        var rating = binding.noTv
        var name = binding.nameAuthorTv
        var phone = binding.phoneTv
        var email = binding.emailTv
        var dob = binding.dobTv
    }

    override fun getFilter(): Filter {
        if(filter == null){
            filter = FilterAuthor(filterList, this)
        }
        return filter as FilterAuthor
    }
}