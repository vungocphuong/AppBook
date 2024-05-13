package com.example.bookappkotlin.activities

import android.app.AlertDialog
import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.bookappkotlin.MyApplication
import com.example.bookappkotlin.databinding.ActivityPdfEditBinding
import com.example.bookappkotlin.models.ModelAuthor
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class PdfEditActivity : AppCompatActivity() {

    //view binding
    private lateinit var binding: ActivityPdfEditBinding

    private companion object{
        private const val TAG = "PDF_EDIT_TAG"
    }

    //book id get from intent started from AdapterPdfAdmin
    private var bookId = ""

    //progress dialog
    private lateinit var progressDialog: ProgressDialog

    //arrraylist to hold category titles
    private lateinit var categoryTitleArrayList:ArrayList<String>

    //arraylist to hold category ids
    private lateinit var categoryIdArrayList:ArrayList<String>

    private lateinit var authorArrayList: ArrayList<ModelAuthor>
    private lateinit var nameAuthorArrayList: ArrayList<String>
    private lateinit var idAuthorArrayList: ArrayList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPdfEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //get book id to edit the book info
        bookId = intent.getStringExtra("bookId")!!

        //setup progress dialog
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait")
        progressDialog.setCanceledOnTouchOutside(false)

        loadCategories()
        loadBookInfo()
        loadAuthors()
        //handle click, goback
        binding.backBtn.setOnClickListener {
            onBackPressed()
        }

        //handle click, pick category
        binding.categoryTv.setOnClickListener {
            categoryDialog()
        }

        //handle click, begin update
        binding.submitBtn.setOnClickListener {
            validateData()
        }
    }

    private fun loadBookInfo() {
        Log.d(TAG, "loadBookInfo: Loading book info")

        val ref = FirebaseDatabase.getInstance().getReference("Books")
        ref.child(bookId)
            .addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    //get book info
                    selectedCategoryId = snapshot.child("categoryId").value.toString()
                    val description = snapshot.child("description").value.toString()
                    val title = snapshot.child("title").value.toString()
                    val authorId = snapshot.child("authorId").value.toString()

                    //set to views
                    binding.titleEt.setText(title)
                    binding.descriptionEt.setText(description)
                    MyApplication.loadNameAuthor(authorId, binding.authorEt)
                    MyApplication.loadPhoneAuthor(authorId, binding.phoneEt)

                    //load book category info using categoryId
                    Log.d(TAG, "onDataChange: Loading book category info")
                    val refBookCategory = FirebaseDatabase.getInstance().getReference("Categories")
                    refBookCategory.child(selectedCategoryId)
                        .addListenerForSingleValueEvent(object : ValueEventListener{
                            override fun onDataChange(snapshot: DataSnapshot) {
                                //get category
                                val category = snapshot.child("category").value
                                //set to textviews
                                binding.categoryTv.text = category.toString()
                            }

                            override fun onCancelled(error: DatabaseError) {

                            }
                        })
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
    }

    private fun loadAuthors() {
        Log.d(TAG, "loadAuthor: Loading authors")
        //init arraylist
        authorArrayList = ArrayList()
        nameAuthorArrayList = ArrayList()
        //db reference to load categories DB -> Categories
        val ref = FirebaseDatabase.getInstance().getReference("Authors")
        ref.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                //clear list before adding data
                authorArrayList.clear()
                nameAuthorArrayList.clear()
                for (ds in snapshot.children){
                    //get data
                    val model = ds.getValue(ModelAuthor::class.java)
                    //add to arraylist
                    authorArrayList.add(model!!)
                    nameAuthorArrayList.add(model.name)
                    Log.d(TAG, "onDataChange: ${model.name}")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    private var title = ""
    private var description = ""
    private var nameAuthor = ""
    private var authorId = ""
    private var phone = ""

    private fun validateData() {
        //get data
        title = binding.titleEt.text.toString().trim()
        description = binding.descriptionEt.text.toString().trim()
        nameAuthor = binding.authorEt.text.toString().trim()
        phone = binding.phoneEt.text.toString().trim()

        //validate data
        if(title.isEmpty()){
            Toast.makeText(this, "Enter title", Toast.LENGTH_SHORT).show()
        }
        else if(description.isEmpty()){
            Toast.makeText(this, "Enter Description", Toast.LENGTH_SHORT).show()
        }
        else if(nameAuthor.isEmpty()){
            Toast.makeText(this, "Enter name author...", Toast.LENGTH_SHORT).show()
        }
        else if(phone.isEmpty()){
            Toast.makeText(this, "Enter phone author...", Toast.LENGTH_SHORT).show()
        }
        else if(selectedCategoryId.isEmpty()){
            Toast.makeText(this, "Pick Category", Toast.LENGTH_SHORT).show()
        }
        else if(nameAuthor in nameAuthorArrayList == false){
            Toast.makeText(this, "Author name doesn't exist ...", Toast.LENGTH_SHORT).show()
        }
        else if(check() == false){
            Toast.makeText(this, "Phone numbers do not match ...", Toast.LENGTH_SHORT).show()
        }
        else{
            for (i in 0 until authorArrayList.size){
                if(nameAuthor.equals(authorArrayList[i].name) && phone.equals(authorArrayList[i].phone)){
                    authorId = authorArrayList[i].id
                }
            }
            updatePdf()
        }
    }

    private fun check(): Boolean{
        for(i in 0 until authorArrayList.size){
            if(nameAuthor.equals(authorArrayList[i].name)){
                if(phone.equals(authorArrayList[i].phone)){
                    return true;
                }
            }
        }
        return false
    }

    private fun updatePdf() {
        Log.d(TAG, "updatePdf: Starting updating pdf info...")

        //show progress
        progressDialog.setMessage("Updating book info")
        progressDialog.show()

        //setup data to update to db, spellings of keys must be same as in firebase
        val hashMap = HashMap<String, Any>()
        hashMap["title"] = "$title"
        hashMap["description"] = "$description"
        hashMap["categoryId"] = "$selectedCategoryId"
        hashMap["authorId"] = "$authorId"

        //start updating
        val ref = FirebaseDatabase.getInstance().getReference("Books")
        ref.child(bookId)
            .updateChildren(hashMap)
            .addOnSuccessListener {
                progressDialog.dismiss()
                Log.d(TAG, "updatePdf: Updated successfully...")
                Toast.makeText(this, "Updated successfully...", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {e->
                Log.d(TAG, "updatePdf: Failed to update due to ${e.message}")
                progressDialog.dismiss()
                Toast.makeText(this, "Failed to update due to ${e.message}", Toast.LENGTH_SHORT).show()
            }

    }

    private var selectedCategoryId = ""
    private var selectedCategoryTitle = ""

    private fun categoryDialog() {
        /*show dialog to pick the category of pdf/book. we already got the categories*/

        //make string array from arraylost of string
        val categoriesArray = arrayOfNulls<String>(categoryTitleArrayList.size)
        for(i in categoryTitleArrayList.indices){
            categoriesArray[i] = categoryTitleArrayList[i]
        }

        //alert dialog
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Choose Category")
            .setItems(categoriesArray){dialog, position->
                //handle click, save clicked category id and title
                selectedCategoryId = categoryIdArrayList[position]
                selectedCategoryTitle = categoryTitleArrayList[position]

                //set to textview
                binding.categoryTv.text = selectedCategoryTitle
            }
            .show() //show dialog
    }

    private fun loadCategories() {
        Log.d(TAG, "loadcategories: loading categories...")

        categoryTitleArrayList = ArrayList()
        categoryIdArrayList = ArrayList()

        val ref = FirebaseDatabase.getInstance().getReference("Categories")
        ref.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                //clear list before starting adding data into them
                categoryIdArrayList.clear()
                categoryTitleArrayList.clear()

                for(ds in snapshot.children){

                    val id = "${ds.child("id").value}"
                    val category = "${ds.child("category").value}"

                    categoryIdArrayList.add(id)
                    categoryTitleArrayList.add(category)

                    Log.d(TAG, "onDataChange: Category ID $id")
                    Log.d(TAG, "onDataChange: Category  $category")
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }
}