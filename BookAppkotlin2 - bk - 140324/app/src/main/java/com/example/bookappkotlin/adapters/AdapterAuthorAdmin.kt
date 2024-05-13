package com.example.bookappkotlin.adapters

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.example.bookappkotlin.MyApplication
import com.example.bookappkotlin.activities.AuthorEditActivity
import com.example.bookappkotlin.activities.PdfEditActivity
import com.example.bookappkotlin.databinding.RowAuthorListBinding
import com.example.bookappkotlin.filters.FilterAuthorAdmin
import com.example.bookappkotlin.models.ModelAuthor
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AdapterAuthorAdmin : RecyclerView.Adapter<AdapterAuthorAdmin.HolderAuthorAdmin>, Filterable {

    //Context
    private val context: Context

    public var authorsArrayList: ArrayList<ModelAuthor>

    private lateinit var binding: RowAuthorListBinding

    private var filterList: ArrayList<ModelAuthor>
    private var filter: FilterAuthorAdmin? = null

    constructor(context: Context, authorsArrayList: ArrayList<ModelAuthor>) : super() {
        this.context = context
        this.authorsArrayList = authorsArrayList
        this.filterList = authorsArrayList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderAuthorAdmin{
        binding = RowAuthorListBinding.inflate(LayoutInflater.from(context), parent, false)
        return HolderAuthorAdmin(binding.root)
    }

    override fun onBindViewHolder(holder: AdapterAuthorAdmin.HolderAuthorAdmin, position: Int) {
        val model = authorsArrayList[position]
        loadAuthor(model, holder, position)

        //handle click, show dialog with options 1) edit book, 2) Delete book
        holder.moreBtn.setOnClickListener {
            moreOptionsDialog(model, holder)
        }

    }

    private fun moreOptionsDialog(model: ModelAuthor, holder: AdapterAuthorAdmin.HolderAuthorAdmin) {
        val authorId = model.id
        //options to show dialog
        val options = arrayOf("Edit", "Delete")

        //alert dialog
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Choose Option")
            .setItems(options){dialog, position->
                if(position==0){
                    //Edit is clicked, lets creat activity to edit
                    val intent = Intent(context, AuthorEditActivity::class.java)
                    intent.putExtra("authorId", authorId) //passed bookId, will be used to edit the book
                    context.startActivity(intent)
                }
                else if (position==1){
                    //Delete is clicked, lets creat function in MyApplication class for this

                    //show confirmation dialog first if you need...
                    MyApplication.deleteAuthor(context, authorId)
                }
            }
            .show()

    }

    private fun loadAuthor(model: ModelAuthor, holder: AdapterAuthorAdmin.HolderAuthorAdmin, position: Int) {
        val authorId = model.id
        val ref = FirebaseDatabase.getInstance().getReference("Authors")
        ref.child(authorId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    val name = "${snapshot.child("name").value}"
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

                    holder.no.text = (position+1).toString()
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
    inner class HolderAuthorAdmin(itemView: View): RecyclerView.ViewHolder(itemView){
        //init UI Views
        var no = binding.noTv
        var name = binding.nameAuthorTv
        var phone = binding.phoneTv
        var email = binding.emailTv
        var dob = binding.dobTv
        val moreBtn = binding.moreBtn
    }

    override fun getFilter(): Filter {
        if(filter == null){
            filter = FilterAuthorAdmin(filterList, this)
        }
        return filter as FilterAuthorAdmin
    }
}