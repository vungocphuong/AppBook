package com.example.bookappkotlin.activities

import android.app.DatePickerDialog
import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import com.example.bookappkotlin.R
import com.example.bookappkotlin.databinding.ActivityAuthorEditBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.Calendar

class AuthorEditActivity : AppCompatActivity() {

    //view binding
    private lateinit var binding: ActivityAuthorEditBinding

    lateinit var dateEdt: EditText

    //author id get from intent started from AdapterAuthorAdmin
    private var authorId = ""

    //progress dialog
    private lateinit var progressDialog: ProgressDialog



    private companion object{
        private const val TAG = "AUTHOR_EDIT_TAG"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthorEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //get author id to edit the author info
        authorId = intent.getStringExtra("authorId")!!

        //setup progress dialog
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait")
        progressDialog.setCanceledOnTouchOutside(false)

        loadAuthorsInfo()


        dateEdt =  binding.idEdtDate
        dateEdt.setOnClickListener {
            // on below line we are getting
            // the instance of our calendar.
            val c = Calendar.getInstance()

            // on below line we are getting
            // our day, month and year.
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)

            // on below line we are creating a
            // variable for date picker dialog.
            val datePickerDialog = DatePickerDialog(
                // on below line we are passing context.
                this,
                { view, year, monthOfYear, dayOfMonth ->
                    // on below line we are setting
                    // date to our edit text.
                    val dat = (dayOfMonth.toString() + "-" + (monthOfYear + 1) + "-" + year)
                    dateEdt.setText(dat)
                },
                // on below line we are passing year, month
                // and day for the selected date in our date picker.
                year,
                month,
                day
            )
            // at last we are calling show
            // to display our date picker dialog.
            datePickerDialog.show()
        }
        //handle click, goback
        binding.backBtn.setOnClickListener {
            onBackPressed()
        }

        //handle click, begin update
        binding.submitBtn.setOnClickListener {
            validateData()
        }
    }
    private fun loadAuthorsInfo() {
        Log.d(AuthorEditActivity.TAG, "loadAuthorInfo: Loading author info")
        val ref = FirebaseDatabase.getInstance().getReference("Authors")
        ref.child(authorId)
            .addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    //get author info
                    val name = snapshot.child("name").value.toString()
                    val phone = snapshot.child("phone").value.toString()
                    val email = snapshot.child("email").value.toString()
                    val dob = snapshot.child("dob").value.toString()

                    //set to views
                    binding.nameEt.setText(name)
                    binding.phoneEt.setText(phone)
                    binding.emailEt.setText(email)
                    binding.idEdtDate.setText(dob)


                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
    }
    private var name = ""
    private var phone = ""
    private var email = ""
    private var dob = ""

    private fun validateData() {
        //get data
        name = binding.nameEt.text.toString().trim()
        phone = binding.phoneEt.text.toString().trim()
        email = binding.emailEt.text.toString().trim()
        dob = binding.idEdtDate.text.toString().trim()

        //validate data
        if(name.isEmpty()){
            Toast.makeText(this, "Enter name", Toast.LENGTH_SHORT).show()
        }
        else if(phone.isEmpty()){
            Toast.makeText(this, "Enter phone", Toast.LENGTH_SHORT).show()
        }
        else if(phone.isEmpty()){
            Toast.makeText(this, "Enter email", Toast.LENGTH_SHORT).show()
        }
        else if(phone.isEmpty()){
            Toast.makeText(this, "Enter dob", Toast.LENGTH_SHORT).show()
        }
        else{
            updateAuthor()
        }
    }

    private fun updateAuthor() {
        Log.d(AuthorEditActivity.TAG, "updateAuthor: Starting updating author info...")

        //show progress
        progressDialog.setMessage("Updating author info")
        progressDialog.show()

        val hashMap = HashMap<String, Any>()
        hashMap["name"] = "$name"
        hashMap["phone"] = "$phone"
        hashMap["email"] = "$email"
        hashMap["dob"] = "$dob"

        val ref = FirebaseDatabase.getInstance().getReference("Authors")
        ref.child(authorId)
            .updateChildren(hashMap)
            .addOnSuccessListener {
                progressDialog.dismiss()
                Log.d(AuthorEditActivity.TAG, "updateAuthor: Updated successfully...")
                Toast.makeText(this, "Updated successfully...", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {e->
                Log.d(AuthorEditActivity.TAG, "updateAuthor: Failed to update due to ${e.message}")
                progressDialog.dismiss()
                Toast.makeText(this, "Failed to update due to ${e.message}", Toast.LENGTH_SHORT).show()
            }

    }

}