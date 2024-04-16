package com.example.bookappkotlin.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.example.bookappkotlin.filters.FilterPdfUser
import com.example.bookappkotlin.MyApplication
import com.example.bookappkotlin.activities.PdfDetailActivity
import com.example.bookappkotlin.databinding.RowPdfUserBinding
import com.example.bookappkotlin.models.ModelPdf

class AdapterPdfUser : RecyclerView.Adapter<AdapterPdfUser.HolderPdfUser>, Filterable {

    //context, get using constructor
    private var context: Context
    //arraylist to hold pdfs, get using contructor
    public var pdfArrayList: ArrayList<ModelPdf> //to access in filter class, make public
    //arraylist to hold filtered pdfs
    public var filterList: ArrayList<ModelPdf>

    //viewBinding row_pdf_user.xml => RowPdfUserBinding
    private lateinit var binding: RowPdfUserBinding


    private var filter: FilterPdfUser? = null

    constructor(context: Context, pdfArrayList: ArrayList<ModelPdf>) {
        this.context = context
        this.pdfArrayList = pdfArrayList
        this.filterList = pdfArrayList
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderPdfUser {
        //inflate/bind layout row_pdf_user.xml
        binding = RowPdfUserBinding.inflate(LayoutInflater.from(context), parent, false)

        return HolderPdfUser(binding.root)
    }

    override fun getItemCount(): Int {
        return pdfArrayList.size //return list size/ number of records
    }

    override fun onBindViewHolder(holder: HolderPdfUser, position: Int) {
        /*Get data, set data, handle click etc*/

        //get data
        val model = pdfArrayList[position]
        val bookId = model.id
        val categoryId = model.categoryId
        val title = model.title
        val description = model.description
        val uid = model.uid
        val url = model.url
        val timestamp = model.timestamp
        val no = model.rating
        //convert time
        val date = MyApplication.formatTimeStamp(timestamp)

        //set data
        holder.titleTv.text = title
        holder.descriptionTv.text = description
        holder.dateTv.text = date
        holder.noTv.text = no.toString()
        MyApplication.loadPdfFromUrlSinglePage(
            url,
            title,
            holder.pdfView,
            holder.progressBar,
            null
        ) // no need number of need so pass bull

        MyApplication.loadCategory(categoryId, holder.categoryTv)

        MyApplication.loadPdfSize(url, title, holder.sizeTv)

        //handle click, open pdf details page
        holder.itemView.setOnClickListener {
            //pass book id in intent, that will be used to get pdf info
            val intent = Intent(context, PdfDetailActivity::class.java)
            intent.putExtra("bookId", bookId)

            context.startActivity(intent)

        }
    }

    override fun getFilter(): Filter {
        if(filter == null){
            filter = FilterPdfUser(filterList, this)
        }
        return filter as FilterPdfUser
    }

    //ViewHolder class row_pdf_user.xml
    inner class HolderPdfUser(itemView: View): RecyclerView.ViewHolder(itemView){
        //init UI components of row_pdf_user.xml
        var pdfView = binding.pdfView
        var progressBar = binding.progressBar
        var titleTv = binding.titleTv
        var descriptionTv = binding.descriptionTv
        var categoryTv = binding.categoryTv
        var sizeTv = binding.sizeTv
        var dateTv = binding.dateTv
        var noTv = binding.noTv
    }

}