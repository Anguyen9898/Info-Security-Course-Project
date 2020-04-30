package com.anguyen.mymap.ui.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.anguyen.mymap.R
import com.anguyen.mymap.commons.*
import com.anguyen.mymap.models.LoginDetail
import com.anguyen.mymap.presenter.LoginPresenter
import com.anguyen.mymap.ui.views.LoginView
import com.facebook.CallbackManager
import com.facebook.FacebookException
import com.facebook.FacebookSdk
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.mancj.slideup.SlideUp
import com.mancj.slideup.SlideUpBuilder
import kotlinx.android.synthetic.*
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity(), LoginView {

    private lateinit var mPresenter: LoginPresenter
    private val mCallbackManager = CallbackManager.Factory.create()

    private var isDialogVisible = false
    private lateinit var loginLayoutAnimation: SlideUp

    private var loginMethod = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        initUI()
    }

    /**
     * Initialize Login Presenter
     */
    private fun initPresenter(): LoginPresenter {
        return LoginPresenter(
            this,
            this,
            LoginDetail(edt_email.text.toString(), edt_password.text.toString()),
            listOf(edt_email, edt_password)
        )
    }

    private fun initUI(){

        loginLayoutAnimation = initAnimationObj()!!

        mPresenter = initPresenter()

        initFacebookExtension()

        //Catch errors while text's changed
        edt_email.onTextChanged { mPresenter.onEmailChange(it) }
        edt_password.onTextChanged { mPresenter.onPasswordChange(it) }

        //On Button Clicked
        btn_google_method.onClick {
            mPresenter.onLoginByGoogleButtonClicked()
            loginMethod = KEY_GOOGLE_USER // Set user type
        }

        btn_facebook_method.setReadPermissions("email", "public_profile")
        btn_facebook_method.onClick {
            mPresenter.onLoginByFacebookButtonClicked(mCallbackManager)
            loginMethod = KEY_FACEBOOK_USER // Set user type
        }

        btn_email_phone_method.onClick{
            loginLayoutAnimation.show()
            isDialogVisible = true
        }

        btn_login.onClick{
            mPresenter.onLoginButtonClicked()
            loginMethod = KEY_EMAIL_USER // Set user type
        }

        btn_jump_to_register.onClick {
            val mIntent = Intent(this, RegisterActivity::class.java)
            mIntent.putExtra("account", edt_email.text.toString())

            startActivity(mIntent)
        }

        btn_anonymous_method.onClick { mPresenter.onAnonymousLoginButtonClicked() }

    }

    private fun initFacebookExtension(){
        FacebookSdk.sdkInitialize(applicationContext)
        //AppEventsLogger.activateApp(this)
        LoginManager.getInstance().logInWithReadPermissions(this, listOf("public_profile"));
    }

    private fun initAnimationObj(): SlideUp? {
        return SlideUpBuilder(login_layout)
            .withStartState(SlideUp.State.HIDDEN)
            .withStartGravity(Gravity.BOTTOM)
            .withSlideFromOtherView(img_view_arrow)
            .withSlideFromOtherView(login_layout)
            .build()
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
        showToastByString(this, "Canceled!!, Error code: ${ex.message!!}")
    }

    override fun onLoginSuccess() {
        showToastByResourceId(this@LoginActivity, R.string.login_success)
        updateUI()
        this.finish()
    }

    override fun onLoginFail() {
        errorDialog(
            this@LoginActivity,
            R.string.login_failed_title,
            R.string.login_failed_message
        )
    }

    override fun onFacebookAccountLoginSuccess() {
        updateUI()
    }

    override fun onFacebookAccountLoginCancel() {
        showToastByString(this, "Canceled!")
    }

    override fun onFacebookAccountLoginError(ex: FacebookException?) {
        showToastByString(this, ex?.message!!)
    }

    override fun onAnonymousLoginSuccess() {
        updateUI()
    }

    override fun showEmailError() {
        edt_email.error = getString(R.string.invalid_email)
    }

    override fun showPasswordError(count: Int) {

        txt_login_min_val_required.visibility = View.VISIBLE
        txt_login_password_count.text = count.toString()

        if (isPasswordValid(edt_password.text.toString())) {
            changeColor(
                listOf(txt_login_password_count, txt_login_min_val_required),
                R.color.hintDefault
            )
        }else {
            changeColor(
                listOf(txt_login_password_count, txt_login_min_val_required),
                R.color.colorRequired
            )
        }

    }

    override fun onEmptyFieldsError(vararg emptyFields: EditText) {
        emptyFields.forEach { it.error = getString(R.string.empty_field_required) }
    }

    override fun fireBaseExceptionError(message: String) = showToastByString(this, message)

    override fun internetError() = internetErrorDialog(this)

    override fun onBackPressed() {
        if(isDialogVisible){
            loginLayoutAnimation.toggle()
            isDialogVisible = false
        }
        else{
            super.onBackPressed()
        }
    }

    override fun onDestroy() {
        clearFindViewByIdCache()
        super.onDestroy()
    }

}
