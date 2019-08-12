package org.sopt.wjdma.emblecare

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import kotlinx.android.synthetic.main.activity_login.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import org.json.JSONObject
import org.sopt.wjdma.emblecare.R
import org.sopt.wjdma.emblecare.main.MainActivity
import org.sopt.wjdma.emblecare.network.ApplicationController
import org.sopt.wjdma.emblecare.network.NetworkService
import org.sopt.wjdma.emblecare.network.Post.JoinData
import org.sopt.wjdma.emblecare.network.Post.PostLoginResponse
import org.sopt.wjdma.emblecare.util.User
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception

class LoginActivity : AppCompatActivity() {

    var jsonObject = JSONObject()
    val networkService: NetworkService by lazy {
         ApplicationController.instance.networkService
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        setOnClickListener()
    }

    private fun setOnClickListener() {
        btn_login_act.setOnClickListener {
            try{
                if(et_login_act_id.text.toString().isNotEmpty() && et_login_act_pw.text.toString().isNotEmpty()){
                    val input_id = et_login_act_id.text.toString()
                    val input_pw = et_login_act_pw.text.toString()

                    jsonObject.put("id", input_id)
                    jsonObject.put("pw", input_pw)

                    getLoginResponse()
                }
            } catch (e:Exception) {
            }
        }

        btn_login_act_join.setOnClickListener {
            try{
                val intent = Intent(this, JoinActivity::class.java)
                startActivity(intent)
            } catch (e: Exception){
            }
        }
    }

    private fun getLoginResponse() {
        val gsonObject = JsonParser().parse(jsonObject.toString()) as JsonObject
        Log.e("****Login_gson::", gsonObject.toString())
        val postLoginResponse: Call<PostLoginResponse> = networkService.postLoginResponse("application/json", gsonObject)
        postLoginResponse.enqueue(object : Callback<PostLoginResponse>{
            override fun onFailure(call: Call<PostLoginResponse>, t: Throwable) {
                Log.e("****Login_gson::", gsonObject.toString())
                Log.e("****Login_Failed", t.toString())
            }

            override fun onResponse(call: Call<PostLoginResponse>, response: Response<PostLoginResponse>) {
                if(response.isSuccessful) {
                    if(response.body()!!.status == 400){
                        toast("아이디와 비밀번호를 확인해주세요")
                    } else{
                        toast("로그인 성공")
                        Log.e("****Login_idx::", response.body()!!.idx.toString())
                        User.user_idx = response.body()!!.idx
                        startActivity<MainActivity>()
                        finish()
                    }
                }
            }
        })
    }
}
