package com.anguyen.mymap.ui.activities

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.anguyen.mymap.R
import com.anguyen.mymap.commons.*
import com.anguyen.mymap.presenter.AvatarSelectorPresenter
import com.anguyen.mymap.ui.views.AvatarSelectorView
import com.bumptech.glide.Glide
import com.kaopiz.kprogresshud.KProgressHUD
import kotlinx.android.synthetic.*
import kotlinx.android.synthetic.main.activity_avatar.*
import java.io.FileNotFoundException

class AvatarSelectorActivity : AppCompatActivity(), AvatarSelectorView {

    private lateinit var progressDialog: KProgressHUD

    private lateinit var mPresenter: AvatarSelectorPresenter

    private var isChoseAvatar = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_avatar)
        progressDialog = initProgress(this)
        progressDialog.show()

        initUI()
    }

    override fun onStart() {
        super.onStart()
        progressDialog.dismiss()
    }

    private fun initUI(){

        mPresenter = AvatarSelectorPresenter(this, this)

        Glide.with(this).load(DEFAULT_AVATAR_URL).into(img_avatar)

        txt_skip.onClick {
            val intentToMain = Intent(this, MainActivity::class.java)
            intentToMain.putExtra(KEY_USER_TYPE, intent.extras?.getString(KEY_USER_TYPE))

            startActivity(intentToMain)
        }

        txt_chooser.onClick { openPhotoPicker() }

        img_avatar.onClick { openPhotoPicker() }
    }

    private fun openPhotoPicker(){
        val photoPickerIntent = Intent(Intent.ACTION_PICK)
        photoPickerIntent.type = "image/*"
        startActivityForResult(photoPickerIntent, PICK_IMAGE_QUEST_CODE)
    }

    override fun onResume() {
        if(isChoseAvatar){
            progressDialog.show()
        }
        super.onResume()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == PICK_IMAGE_QUEST_CODE){
            txt_skip.text = getString(R.string.apply_string)

            if(resultCode == Activity.RESULT_OK && data != null){
                isChoseAvatar = true

                try {
                    val imageUri = data.data!!
                    mPresenter.uploadAvatar(imageUri)

                }catch (ex: FileNotFoundException){
                    onFileNotFoundException(ex.message!!)
                }

            }else{
                onCancel("You choose nothing!")
            }
        }
    }

    override fun onDestroy() {
        clearFindViewByIdCache()
        super.onDestroy()
    }

    override fun onUploadSuccessful(url: String) {
        Glide.with(this).load(url).into(img_avatar)
        showToastByString(this, "Upload successfully")
        progressDialog.dismiss()
    }

    override fun onUploadFailed() {
        progressDialog.dismiss()
        errorDialog(
            this,
            R.string.error_title,
            R.string.general_failed_message
        )
    }

    override fun onFileNotFoundException(message: String) {
        progressDialog.dismiss()
        errorDialog(this, "Error", message)
    }

    override fun onCancel(message: String) {
        progressDialog.dismiss()
        errorDialog(this, "Error", message)
    }

    override fun fireBaseExceptionError(message: String) {
        progressDialog.dismiss()
        showFirebaseError(this, message)
    }

    override fun internetError() {
        progressDialog.dismiss()
        showInternetErrorDialog(this)
    }


}
