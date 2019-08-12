package org.sopt.wjdma.emblecare

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import kotlinx.android.synthetic.main.activity_join.*
import org.json.JSONObject
import org.sopt.wjdma.emblecare.R
import org.sopt.wjdma.emblecare.network.ApplicationController
import org.sopt.wjdma.emblecare.network.NetworkService
import org.sopt.wjdma.emblecare.network.Post.JoinData
import org.sopt.wjdma.emblecare.network.Post.PostJoinResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class JoinActivity : AppCompatActivity() {

    var jsonObject = JSONObject()
    val networkService: NetworkService by lazy {
        ApplicationController.instance.networkService
    }
    var input_gender: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_join)

        radiogroup_join.setOnCheckedChangeListener { group, checkedId ->
            if(R.id.female == checkedId){
                input_gender = 0
            } else if(R.id.male == checkedId){
                input_gender = 1
            }
        }
        setOnClickListener()
    }

    private fun setOnClickListener() {
        btn_join.setOnClickListener {
            try{
                val input_id: String = et_join_act_id.text.toString()
                val input_pw: String = et_join_act_pw.text.toString()
                val input_name: String = et_join_act_name.text.toString()
                val input_year: String = et_join_act_year.text.toString()
                val input_month: String = et_join_act_month.text.toString()
                val input_day: String = et_join_act_day.text.toString()


                if(input_id.isEmpty() || input_pw.isEmpty() || input_name.isEmpty() || input_year.isEmpty() || input_month.isEmpty() || input_day.isEmpty() || input_gender == -1){
                    tv_join_act_check.text = "유효한 가입이 아닙니다!!"
                    tv_join_act_check.visibility = View.VISIBLE
                    Log.d("****JoinActivity::", "$input_id/$input_pw/$input_name/$input_year/$input_month/$input_day/$input_gender/")
                } else{
                    tv_join_act_check.visibility = View.INVISIBLE
                    val input_birth: String = "$input_year/$input_month/$input_day"

                    jsonObject.put("id",input_id)
                    jsonObject.put("pw",input_pw)
                    jsonObject.put("name",input_name)
                    jsonObject.put("gender",input_gender)
                    jsonObject.put("birth", input_birth)

                    getJoinResponse()
                }
            } catch(e: Exception) {
            }
        }
    }

    private fun getJoinResponse() {
        val gsonObject = JsonParser().parse(jsonObject.toString()) as JsonObject
        Log.e("****JoinActivity_gson::", gsonObject.toString())
        val postJoinResponse: Call<PostJoinResponse> = networkService.postJoinResponse("application/json",gsonObject)
        postJoinResponse.enqueue(object : Callback<PostJoinResponse>{
            override fun onFailure(call: Call<PostJoinResponse>, t: Throwable) {
                Log.e("Join_Failed", t.toString())
            }

            override fun onResponse(call: Call<PostJoinResponse>, response: Response<PostJoinResponse>) {
                if(response.isSuccessful) {
                    if(response.body()?.status == 200){
                        Log.d("****JoinActivity::", response.body().toString())
                        tv_join_act_check.visibility = View.INVISIBLE
                        finish()
                    } else if(response.body()?.status == 403){
                        tv_join_act_check.text = response.body()?.message
                        tv_join_act_check.visibility = View.VISIBLE
                        Log.d("****JoinActivity::", response.body().toString())
                    }
                }
            }
        })
    }
}
