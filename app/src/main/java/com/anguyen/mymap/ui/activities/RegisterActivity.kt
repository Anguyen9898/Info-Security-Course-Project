package com.anguyen.mymap.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import com.anguyen.mymap.commons.changeColor
import com.anguyen.mymap.commons.errorDialog
import com.anguyen.mymap.commons.internetErrorDialog
import com.anguyen.mymap.R
import com.anguyen.mymap.commons.*
import com.anguyen.mymap.models.CoordinateDetail
import com.anguyen.mymap.models.RegisterDetail
import com.anguyen.mymap.models.UserInformationDetail
import com.anguyen.mymap.presenter.RegisterPresenter
import com.anguyen.mymap.ui.views.RegisterView
import kotlinx.android.synthetic.*
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity(), RegisterView {

    private lateinit var mPresenter: RegisterPresenter

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

    private fun initUI(){

        mPresenter = initPresenter()

        //Get if LoginActivity's email is not empty
        edt_email.setText(intent.getStringExtra("account"))

        //Catch errors while text is changed
        catchErrorOnTextChanged()

        //On Button Clicked
        btn_register.onClick { mPresenter.onRegisterButtonClicked() }

        btn_back.onClick { onBackPressed() }

    }

    private fun catchErrorOnTextChanged(){

        edt_username.onTextChanged { mPresenter.onUsernameChange(it) }

        edt_email.onTextChanged { mPresenter.onEmailChange(it) }

        edt_phone_number.onTextChanged { mPresenter.onPhoneNumberChange(it) }

        edt_new_password.onTextChanged { mPresenter.onPasswordChange(it)}
        //edt_new_password.onFocusChanged { this.window.setSoftInputMode(it) }

        edt_pass_confirm.onTextChanged { mPresenter.onRepeatPasswordChange(it) }

    }

    override fun onRegisterSuccess(registerDetail: RegisterDetail?) {
        showToastByResourceId(this, R.string.register_success)
        startActivity(Intent(this, MainActivity::class.java))
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

    override fun internetError() = internetErrorDialog(this)


    override fun onDestroy() {
        clearFindViewByIdCache()
        super.onDestroy()
    }

}
