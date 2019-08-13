package org.sopt.wjdma.emblecare.main

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.MenuItem
import org.sopt.wjdma.emblecare.R
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import org.sopt.wjdma.emblecare.LoginActivity
import org.sopt.wjdma.emblecare.measure.MeasureActivity
import org.sopt.wjdma.emblecare.network.ApplicationController
import org.sopt.wjdma.emblecare.network.Get.GetWeatherResponse
import org.sopt.wjdma.emblecare.network.NetworkService
import org.sopt.wjdma.emblecare.util.User
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    val networkService: NetworkService by lazy {
        ApplicationController.instance.networkService
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar:Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        val toggle = ActionBarDrawerToggle(this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)
        btn_main_measure.setOnClickListener {
            val intent = Intent(this, MeasureActivity::class.java)
            startActivity(intent)
        }
        getWeatherResponse()
        setOnClickListener()
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }


    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_camera -> {
                // Handle the camera action
            }
            R.id.nav_gallery -> {

            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun getWeatherResponse() {
        val getWeatherResponse: Call<GetWeatherResponse> = networkService.getWeatherResponse("application/json")
        getWeatherResponse.enqueue(object : Callback<GetWeatherResponse> {
            override fun onFailure(call: Call<GetWeatherResponse>, t: Throwable) {
                Log.e("****weather_Failed", t.toString())
            }

            override fun onResponse(call: Call<GetWeatherResponse>, response: Response<GetWeatherResponse>) {
                if(response.isSuccessful) {
                    if(response.body()!!.status == 200){
                        Log.d("****MainActivity::", response.body().toString())
                        tv_main_temperature.text = response.body()!!.data.temp.toString()+"도"
                        tv_main_wetness.text = response.body()!!.data.reh.toString()+"%"
                    } else{
                        toast("날씨 데이터받아오기 실패")
                    }
                }
            }
        })
    }

    private fun setOnClickListener(){
        btn_main_act_logout.setOnClickListener {
            User.user_idx = null
            startActivity<LoginActivity>()
            finish()
        }
    }
}
