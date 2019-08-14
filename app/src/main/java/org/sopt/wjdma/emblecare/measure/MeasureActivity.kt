package org.sopt.wjdma.emblecare.measure

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import com.bumptech.glide.Glide
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import kotlinx.android.synthetic.main.activity_measure.*
import org.json.JSONObject
import org.sopt.wjdma.emblecare.R
import org.sopt.wjdma.emblecare.network.ApplicationController
import org.sopt.wjdma.emblecare.network.Get.GetMeasureListResponse
import org.sopt.wjdma.emblecare.network.Get.MeasureListData
import org.sopt.wjdma.emblecare.network.NetworkService
import org.sopt.wjdma.emblecare.network.Post.PostMeasureFlagResponse
import org.sopt.wjdma.emblecare.network.Post.PostMeasureResponse
import org.sopt.wjdma.emblecare.util.User
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MeasureActivity : AppCompatActivity() {

    var jsonObject1 = JSONObject()
    var jsonObject2 = JSONObject()
    val networkService: NetworkService by lazy {
        ApplicationController.instance.networkService
    }
    var dataList: ArrayList<MeasureListData> = ArrayList()
    lateinit var measureOutcomeRecyclerViewAdapter: MeasureOutcomeRecyclerViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_measure)
        jsonObject1.put("user_idx",User.user_idx)
        jsonObject2.put("user_idx",User.user_idx)
        setRecyclerView()
        getMeasureListResponse()
        setOnClickListener()
    }

    private fun setOnClickListener(){
        iv_measure_back.setOnClickListener {
            finish()
        }
        var flag = 0
        btn_measure_measure_button.setOnClickListener {
            if(flag==0){
                val measure_button:ImageView = findViewById(R.id.btn_measure_measure_button)
                Glide.with(this).load(R.drawable.measure_ing).into(measure_button)
                btn_measure_measure_button.setImageResource(R.drawable.measure_ing)
                tv_measure_measure_announcement.text = "빈혈 측정 중입니다. 잠시만 기다려주세요."

                //LED 키기 위한 서버 통신
                val rl_period:RelativeLayout = findViewById(R.id.rl_measure_period)
                rl_period.visibility = View.INVISIBLE
                jsonObject1.put("flag",1)
                getMeasureFlagResponse()
                flag=1

                //측정 동영상 보내기 서버 통신
                if(!iv_measure_period_select.isSelected){
                    jsonObject2.put("period",0)
                } else if(iv_measure_period_select.isSelected) {
                    jsonObject2.put("period",1)
                }

                //비디오 촬영이 끝나면
                jsonObject2.put("video","촬영비디오")
            }
            else if(flag==1){
                btn_measure_measure_button.setImageResource(R.drawable.measure_complete)
                tv_measure_measure_announcement.text = "측정이 완료되었습니다."
                val rl_period:RelativeLayout = findViewById(R.id.rl_measure_period)
                rl_period.visibility = View.INVISIBLE
            }
        }
        iv_measure_period_select.setOnClickListener {
            iv_measure_period_select.isSelected = !iv_measure_period_select.isSelected
        }
    }

    //버튼 클릭(LED_ON) 서버 통신
    private fun getMeasureFlagResponse() {
        val gsonObject = JsonParser().parse(jsonObject1.toString()) as JsonObject
        Log.d("****MeasureAct::flag",gsonObject.toString())
        val postMeasureFlagResponse: Call<PostMeasureFlagResponse> = networkService.postMeasureflagResponse("application/json",gsonObject)
        postMeasureFlagResponse.enqueue(object: Callback<PostMeasureFlagResponse>{
            override fun onFailure(call: Call<PostMeasureFlagResponse>, t: Throwable) {
                Log.e("MeasureFlag_Failed", t.toString())
            }

            override fun onResponse(call: Call<PostMeasureFlagResponse>, response: Response<PostMeasureFlagResponse>) {
                if(response.isSuccessful) {
                    if(response.body()?.status == 200) {
                        Log.d("****MeasureAct::flag", response.body().toString())
                    } else{
                        Log.d("****MeasureAct::flag", response.body().toString())
                    }
                }
            }
        })
    }

    //측정결과리스트 서버 통신
    private fun getMeasureListResponse() {
        val getMeasureListResponse = networkService.getMeasureListResponse("application/json", User.user_idx)
        getMeasureListResponse.enqueue(object: Callback<GetMeasureListResponse>{
            override fun onFailure(call: Call<GetMeasureListResponse>, t: Throwable) {
                Log.e("getMeasureList_Fail", t.toString())
            }

            override fun onResponse(call: Call<GetMeasureListResponse>, response: Response<GetMeasureListResponse>) {
                if(response.isSuccessful) {
                    Log.e("MeasureAct::list", response.body().toString())
                    val temp: ArrayList<MeasureListData> = response.body()!!.data
                    if(temp.size>0) {
                        val position = measureOutcomeRecyclerViewAdapter.itemCount
                        measureOutcomeRecyclerViewAdapter.dataList.addAll(temp)
                        measureOutcomeRecyclerViewAdapter.notifyItemInserted(position)
                    }
                } else {
                    Log.e("MeasureAct::list", response.body().toString())
                }
            }
        })
    }

    private fun postMeasureResponse() {
        val gsonObject = JsonParser().parse(jsonObject2.toString()) as JsonObject
        val postMeasureResponse = networkService.postMeasureResponse("application/json",gsonObject)
        postMeasureResponse.enqueue(object : Callback<PostMeasureResponse> {
            override fun onFailure(call: Call<PostMeasureResponse>, t: Throwable) {
                Log.e("postMeasure_Fail", t.toString())
            }

            override fun onResponse(call: Call<PostMeasureResponse>, response: Response<PostMeasureResponse>) {
                if(response.isSuccessful) {
                    Log.e("MeasureAct::outcome", response.body().toString())
                    tv_measure_outcome_hb.text = response.body()!!.data!!.hb.toString()+"g/dL"
                    //리사이클러뷰 데이터리스트에 추가
                } else {
                    Log.e("MeasureAct::outcome", response.body().toString())
                }
            }
        })
    }
    private fun setRecyclerView() {
        measureOutcomeRecyclerViewAdapter = MeasureOutcomeRecyclerViewAdapter(this,dataList)
        rv_measure_act_list.adapter = measureOutcomeRecyclerViewAdapter
        rv_measure_act_list.layoutManager = LinearLayoutManager(this)
    }

}
