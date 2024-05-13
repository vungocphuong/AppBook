package com.example.bookappkotlin.filters

import android.widget.Filter
import com.example.bookappkotlin.adapters.AdapterAuthor
import com.example.bookappkotlin.adapters.AdapterAuthorAdmin
import com.example.bookappkotlin.models.ModelAuthor

class FilterAuthorAdmin: Filter {

    //arraylist in which we want to search
    private var filterList: ArrayList<ModelAuthor>
    //adapter in which filter need to be implemented
    private var adapterAuthorAdmin: AdapterAuthorAdmin

    //constructor
    constructor(filterList: ArrayList<ModelAuthor>, adapterAuthorAdmin: AdapterAuthorAdmin) : super() {
        this.filterList = filterList
        this.adapterAuthorAdmin = adapterAuthorAdmin
    }
    override fun performFiltering(constraint: CharSequence?): FilterResults {
        var constraint = constraint
        val results = FilterResults()

        //value should not be null and not empty
        if(constraint != null && constraint.isNotEmpty()){
            //searched value is not null not empty

            //change to upper case, or lower case to avoid case sensitivity
            constraint = constraint.toString().uppercase()
            val filteredModels:ArrayList<ModelAuthor> = ArrayList()
            for(i in 0 until filterList.size){
                //validate
                if(filterList[i].name.uppercase().contains(constraint)){
                    //add to filtered list
                    filteredModels.add(filterList[i])
                }
            }
            results.count = filteredModels.size
            results.values = filteredModels

        }
        else{
            //searched value is either null or empty
            results.count = filterList.size
            results.values = filterList
        }

        return results //don't miss it
    }

    override fun publishResults(constraint: CharSequence?, results: FilterResults) {
        //apply filter changes
        adapterAuthorAdmin.authorsArrayList = results.values as ArrayList<ModelAuthor>

        //notify changes
        adapterAuthorAdmin.notifyDataSetChanged()
    }

}