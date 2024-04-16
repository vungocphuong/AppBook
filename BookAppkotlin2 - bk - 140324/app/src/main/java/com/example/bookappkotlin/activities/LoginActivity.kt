package com.example.bookappkotlin.activities

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import com.example.bookappkotlin.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class LoginActivity : AppCompatActivity() {

    //viewbinding
    private lateinit var binding: ActivityLoginBinding

    //firebase auth
    private lateinit var firebaseAuth: FirebaseAuth

    //progress dialog
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //init firebase auth
        firebaseAuth = FirebaseAuth.getInstance()

        //init progress dialog, will show while login user
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait")
        progressDialog.setCanceledOnTouchOutside(false)

        //handle click, not have account, goto register screen
        binding.noAccountTv.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        //handle click, begin login
        binding.loginBtn.setOnClickListener {
            /*steps
            * 1) Input data
            * 2) Validate Data
            * 3) Login - Firebase Auth
            * 4) Check user type - Firebase Auth
            *   if User - Move to user dashboard
            *   if Admin - Move to admin dashbaard*/
            validateData()
        }
    }

    private var email = ""
    private var password = ""

    private fun validateData() {
        //1) Input data
        email = binding.emailEt.text.toString().trim()
        password = binding.passwordEt.text.toString().trim()

        //2) Validate data
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            Toast.makeText(this, "Invalid email format...", Toast.LENGTH_SHORT).show()
        }
        else if(password.isEmpty()){
            Toast.makeText(this, "Enter password...", Toast.LENGTH_SHORT).show()
        }
        else{
            loginUser()
        }
    }

    private fun loginUser() {
        //3) Login - Firebase Auth

        //show
        progressDialog.setMessage("Logging In...")
        progressDialog.show()

        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                //login success
                checkUser()
            }
            .addOnFailureListener {e->
                //failed login
                progressDialog.dismiss()
                Toast.makeText(this, "Login failed due to ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun checkUser() {
        /*4) Check user type - Firebase Auth
            *   if User - Move to user dashboard
            *   if Admin - Move to admin dashbaard*/
        progressDialog.setMessage("Checking User...")

        val firebaseUser = firebaseAuth.currentUser!!

        val matrix = arrayOf(
            doubleArrayOf(2.0, 5.0, 0.0, 2.0, 3.0, 0.0, 5.0, 0.0),
            doubleArrayOf(3.0, 5.0, 1.0, 3.0, 5.0, 0.0, 2.0, 0.0),
            doubleArrayOf(4.0, 0.0, 1.0, 4.0, 4.0, 3.0, 3.0, 3.0),
            doubleArrayOf(1.0, 1.0, 0.0, 0.0, 2.0, 0.0, 3.0, 0.0),
            doubleArrayOf(0.0, 0.0, 5.0, 3.0, 3.0, 4.0, 2.0, 5.0),
            doubleArrayOf(4.0, 0.0, 2.0, 2.0, 0.0, 4.0, 2.0, 3.0),
            doubleArrayOf(5.0, 2.0, 0.0, 0.0, 1.0, 5.0, 5.0, 3.0),
            doubleArrayOf(3.0, 3.0, 0.0, 4.0, 4.0, 0.0, 3.0, 2.0),
            doubleArrayOf(0.0, 4.0, 2.0, 3.0, 5.0, 1.0, 5.0, 0.0),
            doubleArrayOf(5.0, 0.0, 2.0, 1.0, 3.0, 0.0, 0.0, 4.0)
        )
        for (row in matrix){
            val rowString = row.joinToString(" ")
            Log.d("ok dang test", rowString)
        }

        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.child(firebaseUser.uid)
            .addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    progressDialog.dismiss()

                    //get user type e.g. user or admin
                    val userType = snapshot.child("userType").value
                    if(userType == "user"){
                        //its simple user, open user dashboard
                        startActivity(Intent(this@LoginActivity, DashboardUserActivity::class.java))
                        finish()
                    }
                    else if(userType == "admin"){
                        //its admin, open admin dashboard
                        startActivity(Intent(this@LoginActivity, DashboardAdminActivity::class.java))
                        finish()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
    }
}