package com.anguyen.mymap.ui.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.anguyen.mymap.R
import com.anguyen.mymap.commons.onClick
import kotlinx.android.synthetic.main.activity_start.*


class StartActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        initUI()
    }

    private fun initUI(){

//        btn_login.onClick {
//            startActivity(Intent(this, LoginActivity    ::class.java))
//        }
//
//        btn_jump_to_register.onClick {
//            startActivity(Intent(this, RegisterActivity::class.java))
//        }

    }
}
