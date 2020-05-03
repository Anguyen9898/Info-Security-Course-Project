package com.anguyen.mymap.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.anguyen.mymap.R
import com.anguyen.mymap.commons.DEFAULT_AVATAR_URL
import com.anguyen.mymap.commons.onClick
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_avatar.*

class AvatarSelectorActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_avatar)

        initUI()
    }

    private fun initUI(){
        Glide.with(this).load(DEFAULT_AVATAR_URL).into(img_avatar)
        txt_skip.onClick { startActivity(Intent(this, MainActivity::class.java)) }
    }

}
