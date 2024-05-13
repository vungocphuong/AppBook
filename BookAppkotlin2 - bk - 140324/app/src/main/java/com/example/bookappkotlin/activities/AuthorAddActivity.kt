package com.example.bookappkotlin.activities

import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.EditText
import android.widget.Toast
import com.example.bookappkotlin.R
import com.example.bookappkotlin.databinding.ActivityAuthorAddBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.util.Calendar

class AuthorAddActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthorAddBinding
    lateinit var dateEdt: EditText
    //firebase auth
    private lateinit var firebaseAuth: FirebaseAuth

    //progress dialog
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthorAddBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //init firebase auth
        firebaseAuth = FirebaseAuth.getInstance()

        //configure progress dialog
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait...")
        progressDialog.setCanceledOnTouchOutside(false)

        //handle click, go back
        binding.backBtn.setOnClickListener {
            onBackPressed()
        }

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

        //handle click, begin upload
        binding.submitBtn.setOnClickListener {
            validateData()
        }

        //handle click, open profile
        binding.listBtn.setOnClickListener {
            startActivity(Intent(this, AuthorListActivity::class.java))
        }
    }

    private var name = ""
    private var phone = ""
    private var email = ""
    private var dob = ""

    private fun validateData() {
        //1) Input data
        name = binding.nameEt.text.toString().trim()
        phone = binding.phoneEt.text.toString().trim()
        email = binding.emailEt.text.toString().trim()
        dob = binding.idEdtDate.text.toString().trim()

        //2) ValidateData
        if(name.isEmpty()){
            //empty name...
            Toast.makeText(this, "Enter your name...", Toast.LENGTH_SHORT).show()
        }
        else if(phone.isEmpty()){
            // empty password
            Toast.makeText(this, "Enter phone...", Toast.LENGTH_SHORT).show()
        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            //invalid email pattern
            Toast.makeText(this, "Invalid Email Pattern...", Toast.LENGTH_SHORT).show()
        }
        else if(dob.isEmpty()){
            // empty password
            Toast.makeText(this, "Enter dob...", Toast.LENGTH_SHORT).show()
        }else{
            addAuthorFirebase()
        }
    }

    private fun addAuthorFirebase() {
        //show progress
        progressDialog.show()

        //get timestamp
        val timestamp = System.currentTimeMillis()

        //setup data to add in firebase db
        val hashMap = HashMap<String, Any>() // second param is Any, because the value could be of any type
        hashMap["id"] = "$timestamp" //put in string quotes because timestamp is in double, we need in string for id
        hashMap["name"] = name
        hashMap["phone"] = phone
        hashMap["email"] = email
        hashMap["dob"] = dob
        hashMap["timestamp"] = timestamp
        //add to firebase db: Database Root -> Categories -> categoryId -> category info
        val ref = FirebaseDatabase.getInstance().getReference("Authors")
        ref.child("$timestamp")
            .setValue(hashMap)
            .addOnSuccessListener {
                //added sucessfully
                progressDialog.dismiss()
                Toast.makeText(this, "Added successfully...", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {e->
                //failed to add
                progressDialog.dismiss()
                Toast.makeText(this, "Failed to add due to ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}