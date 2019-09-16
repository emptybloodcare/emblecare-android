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
import com.bumptech.glide.Glide
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import org.sopt.wjdma.emblecare.R
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import org.json.JSONObject
import org.sopt.wjdma.emblecare.LoginActivity
import org.sopt.wjdma.emblecare.measure.MeasureActivity
import org.sopt.wjdma.emblecare.network.ApplicationController
import org.sopt.wjdma.emblecare.network.Get.GetMainResponse
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
        getMainResponse()

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
            finish()
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
            R.id.nav_1 -> {
                // Handle the camera action
            }
            R.id.nav_2 -> {

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
                        Log.d("****MainActivity::", "weather : "+response.body().toString())
                        tv_main_temperature.text = response.body()!!.data.temp.toString()+"도"
                        tv_main_wetness.text = response.body()!!.data.reh.toString()+"%"
                    } else{
                        toast("날씨 데이터받아오기 실패")
                    }
                }
            }
        })
    }

    private fun getMainResponse() {
        val getMainResponse: Call<GetMainResponse> = networkService.getMainResponse("application/json", User.user_idx)
        getMainResponse.enqueue(object: Callback<GetMainResponse> {
            override fun onFailure(call: Call<GetMainResponse>, t: Throwable) {
                Log.e("****main_Failed", t.toString())
            }

            override fun onResponse(call: Call<GetMainResponse>, response: Response<GetMainResponse>) {
                if(response.isSuccessful) {
                    if(response.body()!!.status == 200) {
                        Log.d("****MainActivity::", "main : "+response.body().toString())
                        tv_main_user_name.text = response.body()!!.data[0].name
                        setRisk(response.body()!!.data[0].risk)
                        tv_main_period.text = response.body()!!.data[0].period
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

    private fun setRisk(risk: Int) {
        if(risk == 1) {
            Glide.with(this)
                    .load(R.drawable.drop4)
                    .into(iv_main_blood_percent)
            tv_main_status.text = "안전"
            tv_main_status.setTextColor(resources.getColor(R.color.status1))
            iv_main_check_button.setImageDrawable(resources.getDrawable(R.drawable.check_button1))
            tv_main_advice_announcement.text = "기분도 좋고 날씨도 좋고~! 훌륭해요\uD83D\uDC4D\uD83C\uDFFB"
            tv_main_advice_announcement.setTextColor(resources.getColor(R.color.announce1))
        } else if(risk == 2) {
            Glide.with(this)
                    .load(R.drawable.drop3)
                    .into(iv_main_blood_percent)
            tv_main_status.text = "보통"
            tv_main_status.setTextColor(resources.getColor(R.color.status2))
            iv_main_check_button.setImageDrawable(resources.getDrawable(R.drawable.check_button2))
            tv_main_advice_announcement.text = "괜찮은 날이네요, 기분좋은 하루 보내세요\uD83D\uDE06"
            tv_main_advice_announcement.setTextColor(resources.getColor(R.color.announce2))
        } else if(risk == 3) {
            Glide.with(this)
                    .load(R.drawable.drop2)
                    .into(iv_main_blood_percent)
            tv_main_status.text = "위험"
            tv_main_status.setTextColor(resources.getColor(R.color.status3))
            iv_main_check_button.setImageDrawable(resources.getDrawable(R.drawable.check_button3))
            tv_main_advice_announcement.text = "쓰러질 가능성 다분~! ☠건강에 유의하세요"
            tv_main_advice_announcement.setTextColor(resources.getColor(R.color.announce3))
        } else if(risk == 4){
            Glide.with(this)
                    .load(R.drawable.drop1)
                    .into(iv_main_blood_percent)
            tv_main_status.text = "심각"
            tv_main_status.setTextColor(resources.getColor(R.color.status4))
            iv_main_check_button.setImageDrawable(resources.getDrawable(R.drawable.check_button4))
            tv_main_advice_announcement.text = "야외 활동을 자제하고 집에서 충분히 휴식을 취하세요!"
            tv_main_advice_announcement.setTextColor(resources.getColor(R.color.announce4))
        } else {
            Log.d("****MainActivity:risk","wrong")
        }
    }
}
