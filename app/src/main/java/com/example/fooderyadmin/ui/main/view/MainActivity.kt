package com.example.fooderyadmin.ui.main.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.widget.Toast
import com.example.fooderyadmin.R
import com.example.fooderyadmin.data.model.Admin
import com.example.fooderyadmin.ui.base.BaseActivity
import com.example.fooderyadmin.utils.Constants
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.layout_register.view.*

class MainActivity : BaseActivity() {

    private var firebaseAuth: FirebaseAuth? = null
    private var authStateListener: FirebaseAuth.AuthStateListener? = null

    //    private var dialog: AlertDialog? = null
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
        setContentView(R.layout.activity_main)

        setProgressBar(progressBar)

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

        val user = firebaseAuth!!.currentUser
        if (user != null) {
            Log.d(TAG, "user login")
            checkAdminStatus(user)

        } else {
            Log.d(TAG, "User not login")
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
//                Log.d(TAG, "User not login")
//                //                startActivity(Intent(this, LoginActivity::class.java))
//                //                finish()
//                signIn()
//            }
//        }

    }

    private fun checkAdminStatus(user: FirebaseUser) {
        showProgressBar()
        serverRef!!.child(user.uid)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    hideProgressBar()
                    Toast.makeText(this@MainActivity, " ${error.message}", Toast.LENGTH_SHORT)
                        .show()

                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val adminModel = snapshot.getValue(Admin::class.java)
                        if (adminModel?.isAdmin!!) {
                            hideProgressBar()
                            goToHomeActivity(adminModel)
                        } else {
                            hideProgressBar()
                            Toast.makeText(
                                this@MainActivity,
                                "Already registered, Wait for approval",
                                Toast.LENGTH_SHORT
                            ).show()

                        }

                    } else {
                        hideProgressBar()
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
        builder.setMessage("Please submit the correct information \n Admin will check your request")

        val itemView = LayoutInflater.from(this).inflate(R.layout.layout_register, null)


        if (user.displayName != null) {
            itemView.name.setText(user.displayName!!)
        }
        if (user.email != null) {
            itemView.email.setText(user.email!!)
        }
        if (user.phoneNumber != null) {
            itemView.phone.setText(user.phoneNumber!!)
        }



        builder.setNegativeButton("Cancel") { dialogInterface, _ -> dialogInterface.dismiss() }
            .setPositiveButton("Register") { _, _ ->
                if (TextUtils.isEmpty(itemView.name.text)) {
                    itemView.name.error = "Required"
                }
                if (TextUtils.isEmpty(itemView.email.text)) {
                    itemView.email.error = "Required"
                }

                val admin = Admin()
                admin.uid = user.uid
                admin.name = itemView.name.text.toString().trim()
                admin.email = itemView.email.text.toString().trim()

                admin.phone = itemView.phone.text.toString().trim()
                admin.isAdmin = false // later set to true from firebase

                showProgressBar()
                serverRef!!.child(admin.uid!!)
                    .setValue(admin)
                    .addOnFailureListener { e ->
                        hideProgressBar()
                        Toast.makeText(this, "${e.message}", Toast.LENGTH_SHORT).show()
                    }
                    .addOnCompleteListener { task ->
                        hideProgressBar()
                        Toast.makeText(
                            this,
                            "Registration Successful. Wait for Admin approval",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

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
                checkAdminStatus(user!!)

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