package com.anguyen.mymap.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import androidx.appcompat.app.AppCompatActivity
import com.anguyen.mymap.R
import com.anguyen.mymap.commons.onClick
import com.anguyen.mymap.presenter.StartPresenter
import com.anguyen.mymap.ui.views.StartView
import kotlinx.android.synthetic.main.activity_start.*


class StartActivity : AppCompatActivity(), StartView, Animation.AnimationListener{

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        initUI()
    }

    private fun initUI(){

        val fadeout = AlphaAnimation(1f, 1f)
        fadeout.duration = 2000
        fadeout.setAnimationListener(this)

        //start_logo.startAnimation(fadeout)
    }

    override fun onAnimationRepeat(animation: Animation?) = Unit

    override fun onAnimationStart(animation: Animation?){
        //start_logo.setBackgroundResource(R.drawable.start_logo)
    }

    override fun onAnimationEnd(animation: Animation?) = updateUI()

    private fun updateUI(){
        StartPresenter(this, this).updateUI()
        finish()
    }

    override fun openMainUI() {
        startActivity(Intent(this, MainActivity::class.java))
    }

    override fun openLoginUI() {
        startActivity(Intent(this, LoginActivity::class.java))
    }

}
