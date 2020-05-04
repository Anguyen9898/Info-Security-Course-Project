package com.anguyen.mymap.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.anguyen.mymap.OnRecycleViewItemClickListener
import com.anguyen.mymap.commons.changeColor
import com.anguyen.mymap.commons.errorDialog
import com.anguyen.mymap.commons.showInternetErrorDialog
import com.anguyen.mymap.R
import com.anguyen.mymap.adapters.GenderAdapter
import com.anguyen.mymap.commons.*
import com.anguyen.mymap.models.*
import com.anguyen.mymap.presenter.RegisterPresenter
import com.anguyen.mymap.ui.views.RegisterView
import com.mancj.slideup.SlideUp
import com.mancj.slideup.SlideUpBuilder
import kotlinx.android.synthetic.*
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity(), RegisterView, OnRecycleViewItemClickListener {

    private lateinit var mPresenter: RegisterPresenter

    private var layoutAnimation: SlideUp? = null

    private val mGenders = ArrayList<String>()
    private lateinit var mAdapter: GenderAdapter

    private lateinit var userType: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        initUI()
    }

    /**
     * Initialize Register Presenter
     */
    private fun initPresenter(): RegisterPresenter {
        return RegisterPresenter(
            this, this,

            RegisterDetail(
                edt_username.text.toString(),
                edt_email.text.toString(),
                edt_new_password.text.toString(),
                edt_pass_confirm.text.toString()
            ),

            UserInformationDetail(
                userType,
                DEFAULT_AVATAR_URL,
                edt_gender.text.toString(),
                edt_phone_number.text.toString(),
                CoordinateDetail()
            ),

            listOf(
                edt_username,
                edt_email,
                edt_new_password,
                edt_pass_confirm,
                edt_phone_number
            )
        )
    }
    private fun initAnimationObj(): SlideUp? {
        return SlideUpBuilder(recycle_hidden_gender_chooser)
            .withStartState(SlideUp.State.HIDDEN)
            .withHideSoftInputWhenDisplayed(true)
            .withStartGravity(Gravity.BOTTOM)
            .build()
    }

    private fun setupRecycleView(){
        mGenders.addAll(genderDetail)
        mAdapter = GenderAdapter(this, mGenders, this)
        
        recycle_hidden_gender_chooser.apply { 
            layoutManager = LinearLayoutManager(
                this@RegisterActivity,
                RecyclerView.VERTICAL,
                false)

            adapter = mAdapter
        }
    }

    private fun initUI(){

        userType =  this.intent.getStringExtra(KEY_USER_TYPE)!!

        layoutAnimation = initAnimationObj()

        setupRecycleView()

        mPresenter = initPresenter()

        //Get if LoginActivity's email is not empty
        edt_email.setText(intent.getStringExtra("account"))

        //Catch errors while text is changed
        catchEventOnTextChanged()

        //On Button Clicked
        btn_register.onClick { mPresenter.onRegisterButtonClicked() }

        btn_back.onClick { onBackPressed() }

        edt_gender.onClick { layoutAnimation?.show() }

    }

    override fun onRecycleViewItemClickHandler(view: View?) {
        edt_gender.setText((view as TextView).text.toString())
        layoutAnimation?.toggle()
    }

    private fun catchEventOnTextChanged(){

        edt_username.onTextChanged { mPresenter.onUsernameChange(it) }

        edt_gender.onTextChanged { mPresenter.onGenderChange(it) }

        edt_email.onTextChanged { mPresenter.onEmailChange(it) }

        edt_phone_number.onTextChanged { mPresenter.onPhoneNumberChange(it) }

        edt_new_password.onTextChanged { mPresenter.onPasswordChange(it)}

        edt_pass_confirm.onTextChanged { mPresenter.onRepeatPasswordChange(it) }

    }

    override fun onRegisterSuccess(registerDetail: RegisterDetail?) {
        showToastByResourceId(this, R.string.register_success)

        //startActivity(Intent(this, AvatarSelectorActivity::class.java))
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra(KEY_USER_TYPE, userType)

        startActivity(intent)
        this.finish()
    }

    override fun onRegisterFail() {
        errorDialog(
            this,
            R.string.register_failed_title,
            R.string.register_fail_message
        )
    }

    override fun onEmptyFieldsError(vararg emptyFields: EditText) {
        emptyFields.forEach { it.error = getString(R.string.empty_field_required) }
    }

    override fun showEmailError() {
        edt_email.error = getString(R.string.invalid_email)
    }

    override fun showUsernameError(count: Int) {
        username_count.text = count.toString()

        if (isUsernameValid(edt_username.text.toString())) {
            changeColor(
                listOf(username_count, username_min_val_required)
                , R.color.hintDefault
            )

        }else {
            changeColor(
                listOf(username_count, username_min_val_required)
                , R.color.colorRequired
            )
        }
    }

    override fun showPhoneNumberFormatError() {
        edt_phone_number.error = getString(R.string.invalid_phone_number)
    }

    override fun showPasswordLengthError(count: Int) {
        register_password_count.text = count.toString()

        if (isPasswordValid(edt_new_password.text.toString())) {
            changeColor(
                listOf(register_password_count, register_min_val_required)
                , R.color.hintDefault
            )
        }else {
            changeColor(
                listOf(register_password_count, register_min_val_required)
                , R.color.colorRequired
            )
        }
    }

    override fun showPasswordMatchingError() {
        edt_pass_confirm.error = getString(R.string.password_matching_error)
    }

    override fun fireBaseExceptionError(message: String) = showToastByString(this, message)

    override fun internetError() = showInternetErrorDialog(this)

    override fun onBackPressed() {
        if(layoutAnimation!!.isVisible){
            layoutAnimation?.toggle()
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
