package com.example.fooderyadmin.ui.main.view

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.fooderyadmin.R
import com.example.fooderyadmin.data.model.Admin
import com.example.fooderyadmin.utils.Constants
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.layout_register.*

class MainActivity : AppCompatActivity() {

    private var firebaseAuth: FirebaseAuth? = null
    private var authStateListener: FirebaseAuth.AuthStateListener? = null
    private var dialog: AlertDialog? = null
    private var serverRef: DatabaseReference? = null
    private var providers: List<AuthUI.IdpConfig>? = null

    companion object {
        private const val RC_SIGN_IN = 4000
        private const val TAG = "MainActivity"
    }

    override fun onStart() {
        super.onStart()
//        firebaseAuth!!.addAuthStateListener { authStateListener!! }
    }

    override fun onStop() {
//        firebaseAuth!!.removeAuthStateListener { authStateListener!! }
        super.onStop()
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)

        init()
//        signIn()
    }

    private fun init() {
        providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.PhoneBuilder().setDefaultCountryIso("np").build(),
            AuthUI.IdpConfig.GoogleBuilder().build()
        )

        serverRef = FirebaseDatabase.getInstance().getReference(Constants.SERVER_REF)

        firebaseAuth = FirebaseAuth.getInstance()
        dialog = MaterialAlertDialogBuilder(this).create()

        val user = firebaseAuth!!.currentUser
        if (user != null) {
            Log.d(TAG, "user login")
            checkAdminStatus(user)

        } else {
            Log.d(Companion.TAG, "User not login")
            //                startActivity(Intent(this, LoginActivity::class.java))
            //                finish()
            signIn()
        }

//        Log.d(TAG, "initialize auth listener")
//        authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
//            val user = firebaseAuth.currentUser
//            if (user != null) {
//                Log.d(TAG, "user login")
//                checkAdminStatus(user)
//
//            } else {
//                Log.d(Companion.TAG, "User not login")
//                //                startActivity(Intent(this, LoginActivity::class.java))
//                //                finish()
//                signIn()
//            }
//        }

    }

    private fun checkAdminStatus(user: FirebaseUser) {
        dialog!!.show()
        serverRef!!.child(user.uid)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    dialog!!.dismiss()
                    Toast.makeText(this@MainActivity, " ${error.message}", Toast.LENGTH_SHORT)
                        .show()

                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val adminModel = snapshot.getValue(Admin::class.java)
                        if (adminModel?.isAdmin!!) {
                            dialog!!.dismiss()
                            goToHomeActivity(adminModel)
                        } else {
                            dialog!!.dismiss()
                            Toast.makeText(
                                this@MainActivity,
                                "Already registered, Wait for approval",
                                Toast.LENGTH_SHORT
                            ).show()

                        }

                    } else {
                        dialog!!.dismiss()
                        showRegisterDialog(user)

                    }
                }
            })
    }

    private fun goToHomeActivity(adminModel: Admin) {
        Constants.currentAdmin = adminModel
        startActivity(Intent(this, HomeActivity::class.java))
        finish()
    }

    private fun showRegisterDialog(user: FirebaseUser) {
        val builder = MaterialAlertDialogBuilder(this)
        builder.setTitle("Register")
        builder.setMessage("Please submit the correct information \n Main Admin will check your request")

        val itemView = LayoutInflater.from(this).inflate(R.layout.layout_register, null)

        phone.setText(user.phoneNumber!!)
        phone.isEnabled = false

        builder.setNegativeButton("Cancel") { dialogInterface, _ -> dialogInterface.dismiss() }
            .setPositiveButton("Register") { _, _ ->
                when {
                    TextUtils.isEmpty(name.text) -> name.error = "please enter name"
                    TextUtils.isEmpty(email.text) -> email.error = "please enter email"
                }
            }
        val admin = Admin()
        admin.uid = user.uid
        admin.name = name.text.toString().trim()
        admin.email = email.text.toString().trim()
        admin.phone = user.phoneNumber!!
        admin.isAdmin = false // later set to true from firebase

        dialog!!.show()
        serverRef!!.child(admin.uid!!)
            .setValue(admin)
            .addOnFailureListener { e ->
                dialog!!.dismiss()
                Toast.makeText(this, "${e.message}", Toast.LENGTH_SHORT).show()
            }
            .addOnCompleteListener { task ->
                dialog!!.dismiss()
                Toast.makeText(
                    this,
                    "Registration Successful. Wait for Admin approval",
                    Toast.LENGTH_SHORT
                ).show()
            }

        builder.setView(itemView)
        val registerDialog = builder.create()
        registerDialog.show()

    }

    private fun signIn() {
        startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers!!)
                .setTheme(R.style.AppTheme)
                .setLogo(R.drawable.logo)
                .build(), RC_SIGN_IN
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val response = IdpResponse.fromResultIntent(data)
            if (resultCode == Activity.RESULT_OK) {
                val user = FirebaseAuth.getInstance().currentUser
            } else {
                Toast.makeText(this, R.string.login_failed, Toast.LENGTH_LONG).show()
//                Snackbar.make(, R.string.login_failed,Snackbar.LENGTH_LONG).show()
            }
        }
    }

    override fun onBackPressed() {
        MaterialAlertDialogBuilder(this)
            .setTitle(getString(R.string.exit_dialog_title))
            .setMessage(getString(R.string.exit_dialog_message))
            .setPositiveButton(getString(R.string.option_yes)) { dialogInterface, _ ->
                dialogInterface.dismiss()
                super.onBackPressed()
            }
            .setNegativeButton(getString(R.string.option_no)) { dialogInterface, _ ->
                dialogInterface.dismiss()
            }
            .create()
            .show()
    }
}