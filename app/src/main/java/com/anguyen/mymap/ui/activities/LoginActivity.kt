package com.anguyen.mymap.ui.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.children
import com.anguyen.mymap.R
import com.anguyen.mymap.commons.*
import com.anguyen.mymap.presenter.LoginPresenter
import com.anguyen.mymap.ui.fragments.BottomDialogLoginFragment
import com.anguyen.mymap.ui.views.LoginView
import com.facebook.CallbackManager
import com.facebook.FacebookException
import com.facebook.FacebookSdk
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.kaopiz.kprogresshud.KProgressHUD
import kotlinx.android.synthetic.*
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity(), LoginView {

    private lateinit var mPresenter: LoginPresenter
    private val mCallbackManager = CallbackManager.Factory.create()

    private var loginMethod = ""

    private lateinit var progressDialog: KProgressHUD

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        progressDialog = initProgress(this)
        progressDialog.show()

        initUI()
    }

    override fun onStart() {
        progressDialog.dismiss()
        super.onStart()
    }

    override fun onRestart() {
        progressDialog.dismiss()
        super.onRestart()
    }

    private fun initUI(){

        customGoogleSignInButton()

        mPresenter = LoginPresenter(this, this)

        FacebookSdk.sdkInitialize(applicationContext)

        //On Button Clicked
        btn_email_method.onClick{
            loginMethod = KEY_EMAIL_USER // Set user type
            createBottomView()
        }

        btn_google_method.onClick {
            progressDialog.show()

            mPresenter.onLoginByGoogleButtonClicked()
            loginMethod = KEY_GOOGLE_USER // Set user type
        }

        btn_facebook_method.setReadPermissions("email", "public_profile")
        btn_facebook_method.onClick {
            progressDialog.show()

            mPresenter.onLoginByFacebookButtonClicked(mCallbackManager)
            loginMethod = KEY_FACEBOOK_USER // Set user type
        }


        btn_anonymous_method.onClick {
            progressDialog.show()

            mPresenter.onAnonymousLoginButtonClicked()
            loginMethod = KEY_GUEST_USER // Set user type
        }

    }

    private fun createBottomView(){
        val bottomDialog = BottomDialogLoginFragment(this)

        bottomDialog.show(supportFragmentManager, "dialog")

    }

    private fun customGoogleSignInButton(){
        btn_google_method.children.forEach {
            if(it is TextView){
                it.text = getString(R.string.btn_google_method)
            }
        }
    }

    override fun openGoogleSignInDialog(googleIntent: Intent) {
        progressDialog.dismiss()
        startActivityForResult(googleIntent, GOOGLE_REQUEST_CODE)
    }

    /**
     * Jump to Main Activity
     */
    private fun updateUI(){
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra(KEY_USER_TYPE, loginMethod)

        startActivity(intent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        progressDialog.show()

        //Facebook Login Result
        mCallbackManager.onActivityResult(requestCode, resultCode, data)

        super.onActivityResult(requestCode, resultCode, data)

        when(requestCode){

            //Google Login Result
            // Returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
            GOOGLE_REQUEST_CODE -> {
                googleLoginResult(data)
            }

        }

    }

    private fun googleLoginResult(data: Intent?){
        if(data != null){
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)

                // Complete Sign in by Token Id
                // Create database if it's new
                mPresenter.googleAccLoginCompleting(account)

            }catch (ex: ApiException){

                // The ApiException status code indicates the detailed failure reason.
                // Please refer to the GoogleSignInStatusCodes class reference for more information.
                Log.w("Google Login Method", "signInResult: failed code = " + ex.statusCode)

                onApiError(ex)
            }
        }
    }

    override fun onApiError(ex: ApiException) {
        progressDialog.dismiss()
        showToastByString(this, "Canceled!!, Error code: ${ex.message!!}")
    }

    override fun onLoginSuccess() {
        progressDialog.dismiss()
        showToastByResourceId(this@LoginActivity, R.string.login_success)

        updateUI()
        this.finish()
    }

    override fun onLoginFail() {
        progressDialog.dismiss()

        errorDialog(
            this@LoginActivity,
            R.string.login_failed_title,
            R.string.general_failed_message
        )
    }

    override fun onEmailConflict() {
        progressDialog.dismiss()

        errorDialog(
            this,
            R.string.login_failed_title,
            R.string.conflict_email_message)
    }

    override fun onFacebookAccountLoginSuccess() {
        progressDialog.dismiss()
        updateUI()
    }

    override fun onFacebookAccountLoginCancel() {
        progressDialog.dismiss()
        showToastByString(this, "Canceled!")
    }

    override fun onFacebookAccountLoginError(ex: FacebookException?) {
        progressDialog.dismiss()
        showToastByString(this, ex?.message!!)
    }

    override fun onAnonymousLoginSuccess() {
        progressDialog.dismiss()
        updateUI()
    }

    override fun fireBaseExceptionError(message: String) {
        progressDialog.dismiss()
        showFirebaseError(this, message)
    }

    override fun internetError() {
        progressDialog.dismiss()
        showInternetErrorDialog(this)
    }

    override fun onDestroy() {
        clearFindViewByIdCache()
        super.onDestroy()
    }

}
