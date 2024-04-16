package com.example.bookappkotlin

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.bookappkotlin.activities.DashboardUserActivity
import com.example.bookappkotlin.activities.RecommendAuthorActivity
import com.example.bookappkotlin.adapters.AdapterComment
import com.example.bookappkotlin.adapters.AdapterPdfUser
import com.example.bookappkotlin.databinding.ActivityMainBinding
import com.example.bookappkotlin.databinding.FragmentBooksUserBinding
import com.example.bookappkotlin.databinding.RowPdfUserBinding
import com.example.bookappkotlin.models.ModelComment
import com.example.bookappkotlin.models.ModelPdf
import com.example.bookappkotlin.models.ModelViewedBook
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.Objects
import kotlin.math.log

class BooksUserFragment : Fragment{

    //view binding fragment_books_user.xml => FragmentBooksUserBinding
    private lateinit var binding: FragmentBooksUserBinding

    public companion object{
        private const val TAG = "BOOKS_USER_TAG"

        //receive data from activity to load books e.g. categoryId, category, uid
        public fun newInstance(categoryId: String, category: String, uid: String): BooksUserFragment{
            val fragment = BooksUserFragment()
            //put data to bundle intent
            val args = Bundle()
            args.putString("categoryId", categoryId);
            args.putString("category", category);
            args.putString("uid", uid);
            fragment.arguments = args
            return fragment
        }
    }

    private var categoryId = ""
    private var category = ""
    private var uid = ""

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var pdfArrayList: ArrayList<ModelPdf>
    private lateinit var adapterPdfUser: AdapterPdfUser

    constructor()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firebaseAuth = FirebaseAuth.getInstance()
        //get argument that we passed in newInstance method
        var args = arguments
        if(args != null){
            categoryId = args.getString("categoryId")!!
            category = args.getString("category")!!
            uid = args.getString("uid")!!
        }

    }
    private lateinit var idBooksRe: ArrayList<String>
    private lateinit var viewedBookArrayList: ArrayList<ModelViewedBook>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = FragmentBooksUserBinding.inflate(LayoutInflater.from(context), container, false)
        //load pdf according to category, this fragment will have new instance to load each category pdfs
        Log.d(TAG, "onCreateView: Category: $category")
        if (category == "All"){
            //load all books
            loadAllBooks()
        }
        else if(category == "Recommend"){
            //loadMostViewedDownloadedBooks("viewsCount")
            //loadRecommendBooks()
            idBooksRe = ArrayList()
            idBooksRe.clear()
            matrixRecommendBooks(object : FirebaseCallback{
                override fun onCallback(arrayList: ArrayList<String>) {
                    Log.d("mang da check", bids.toString())
                    Log.d("mang da check", uids.toString())
                    var matrixY = Array(uids.size) { DoubleArray(bids.size) }
                    for (i in 0 until bids.size){
                        val ref2 = FirebaseDatabase.getInstance().getReference("Books")
                        ref2.child(bids[i]).child("Comments")
                            .addValueEventListener(object : ValueEventListener{
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    //clear list
                                    if(i == 0){
                                        matrixY = Array(uids.size) { DoubleArray(bids.size) }
                                    }else{
                                        matrixY = matrixY
                                    }

                                    for (ds in snapshot.children){
                                        //get data s model, be carefully of spellings and data type
                                        val model = ds.getValue(ModelComment::class.java)
                                        //add to list
                                        var start = model!!.rating
                                        var uid = model!!.uid
                                        var index = uids.indexOf(uid)
                                        matrixY[index][i] = start.toDouble()
                                    }
                                    /*for (i in 0 until matrixY.size){
                                        for (j in 0 until matrixY[i].size){
                                            Log.d("Ma tran", matrixY[i][j].toString())
                                        }
                                    }*/
                                    Log.d("kich thuoc", "${matrixY.size} ${matrixY[0].size}")
                                    for (i in 0 until matrixY.size){
                                        for (j in 0 until matrixY[i].size){
                                            Log.d("ma tran", "Matrix[$i][$j]"+matrixY[i][j].toString())
                                        }
                                    }
                                    if(i == bids.size-1){
                                        idBooksRe.clear()
                                        idBooksRe = MyApplication1.recommend(matrixY, bids, uids, firebaseAuth.uid!!)
                                        Log.d("Mang goi y", idBooksRe.toString())
                                        loadRecommendBooksF(idBooksRe)
                                    }
                                }

                                override fun onCancelled(error: DatabaseError) {

                                }
                            })
                    }
                }

            })
        }
        else if(category == "Viewed"){
            //load most viewed books
            //loadMostViewedDownloadedBooks("viewsCount")

        }
        else if (category == "Rating"){
            //load most downloaded books
            loadMostViewedDownloadedBooks("-viewsCount")
            binding.recommendAuthorBtn.visibility = View.VISIBLE
        }
        else if (category == "Downloaded"){
            //load most downloaded books
            loadMostViewedDownloadedBooks("downloadsCount")
        }
        else{
            //load selected category books
            loadCategorizedBooks()
        }

        binding.recommendAuthorBtn.setOnClickListener {
            val intent = Intent(context, RecommendAuthorActivity::class.java)
            startActivity(intent)
        }

        //search
        binding.searchEt.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                //called as and when user type anything
                try{
                    adapterPdfUser.filter.filter(s)
                }
                catch (e: Exception){

                }
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })

        return binding.root
    }

    private fun loadAllBooks() {
        //init list
        pdfArrayList = ArrayList()
        val ref = FirebaseDatabase.getInstance().getReference("Books")
        ref.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                //clear list before starting adding data into it
                pdfArrayList.clear()
                for (ds in snapshot.children){
                    //get data
                    val model = ds.getValue(ModelPdf::class.java)
                    //add to list
                    pdfArrayList.add(model!!)
                }
                //setup adapter
                adapterPdfUser = AdapterPdfUser(context!!, pdfArrayList)
                //set adapter to recyclerview
                binding.booksRv.adapter = adapterPdfUser
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun loadViewedBooks(firebaseCallback: FirebaseCallback){
        pdfArrayList = ArrayList()
        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.child(firebaseAuth.uid!!).child("ViewedBooks")
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    pdfArrayList.clear()
                    for (ds in snapshot.children){
                        //get data
                        val bookId = "${ds.child("bookId").value}"

                        //set to model
                        val modelPdf = ModelPdf()
                        modelPdf.id = bookId
                        pdfArrayList.add(modelPdf)
                    }

                    //setup adapter
                    adapterPdfUser = AdapterPdfUser(context!!, pdfArrayList)
                    //set adapter to recyclerview
                    binding.booksRv.adapter = adapterPdfUser

                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
    }

    private fun loadViewedBooks(){

    }

    private fun loadMostViewedDownloadedBooks(orderBy: String) {
        //init list
        pdfArrayList = ArrayList()
        val ref = FirebaseDatabase.getInstance().getReference("Books")
        ref.orderByChild(orderBy)//.limitToLast(10) //load 10 most viewed or most downloaded books
            .addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                //clear list before starting adding data into it
                pdfArrayList.clear()
                var index = 0
                for (ds in snapshot.children){
                    //get data
                    val model = ds.getValue(ModelPdf::class.java)
                    model!!.rating = "Rating: "+(index+1).toString()
                    //add to list
                    pdfArrayList.add(model!!)
                    index+=1
                }
                //setup adapter
                adapterPdfUser = AdapterPdfUser(context!!, pdfArrayList)
                //set adapter to recyclerview
                binding.booksRv.adapter = adapterPdfUser
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun loadCategorizedBooks() {
        //init list
        pdfArrayList = ArrayList()
        val ref = FirebaseDatabase.getInstance().getReference("Books")
        ref.orderByChild("categoryId").equalTo(categoryId)
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    //clear list before starting adding data into it
                    pdfArrayList.clear()
                    for (ds in snapshot.children){
                        //get data
                        val model = ds.getValue(ModelPdf::class.java)
                        //add to list
                        pdfArrayList.add(model!!)
                    }
                    //setup adapter
                    adapterPdfUser = AdapterPdfUser(context!!, pdfArrayList)
                    //set adapter to recyclerview
                    binding.booksRv.adapter = adapterPdfUser
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
    }

    lateinit var uids: ArrayList<String>
    lateinit var bids: ArrayList<String>
    private fun matrixRecommendBooks(firebaseCallback: FirebaseCallback){
        pdfArrayList = ArrayList()
        bids = ArrayList()
        uids = ArrayList()
        idBooksRe = ArrayList()
        val ref = FirebaseDatabase.getInstance().getReference("Books")
        ref.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                pdfArrayList.clear()
                idBooksRe.clear()
                bids.clear()
                for (ds in snapshot.children){
                    //get data
                    val model = ds.getValue(ModelPdf::class.java)
                    //add to list
                    bids.add(model!!.id)
                }

                firebaseCallback.onCallback(bids)

            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

        val ref1 = FirebaseDatabase.getInstance().getReference("Users")
        ref1.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                pdfArrayList.clear()
                idBooksRe.clear()
                uids.clear()
                for (ds in snapshot.children){
                    //get data
                    /*val model = ds.getValue(ModelPdf::class.java)
                    //add to list
                    if(model!!.uid.equals("z3EjmbXGNjT5gm7ke0mEO726T9R2") == false){
                        uids.add(model!!.uid)
                    }*/
                    val userType = "${ds.child("userType").value}"
                    if(userType.equals("admin") == false){
                        uids.add("${ds.child("uid").value}")
                    }
                }

                firebaseCallback.onCallback(uids)
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun loadRecommendBooksF(idBook: ArrayList<String>){
        pdfArrayList = ArrayList()
        idBooksRe = ArrayList()
        val ref = FirebaseDatabase.getInstance().getReference("Books")
        ref.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                //clear list before starting adding data into it
                pdfArrayList.clear()
                idBooksRe.clear()
                for (ds in snapshot.children){
                    //get data
                    val model = ds.getValue(ModelPdf::class.java)
                    //add to list
                    for(i in 0 until idBook.size){
                        if(model!!.id.equals(idBook[i])){
                            pdfArrayList.add(model!!)
                        }
                    }
                }
                //setup adapter
                adapterPdfUser = AdapterPdfUser(context!!, pdfArrayList)
                //set adapter to recyclerview
                binding.booksRv.adapter = adapterPdfUser
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private interface FirebaseCallback{
        fun onCallback(arrayList: ArrayList<String>);
    }
}