package org.sopt.wjdma.emblecare.measure

import android.Manifest.permission.CAMERA
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_measure.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject
import org.sopt.wjdma.emblecare.R
import org.sopt.wjdma.emblecare.network.*
import org.sopt.wjdma.emblecare.network.Get.GetMeasureListResponse
import org.sopt.wjdma.emblecare.network.Get.MeasureListData
import org.sopt.wjdma.emblecare.network.Post.PostMeasureResponse
import org.sopt.wjdma.emblecare.util.ImageFilePath
import org.sopt.wjdma.emblecare.util.User
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class MeasureActivity : AppCompatActivity() {

    lateinit var videoUri : Uri
    val REQUEST_VIDEO_CAPTURE = 1

    var jsonObject1 = JSONObject()
    var period: Int? = -1
    var video: MultipartBody.Part? = null
    val networkService: NetworkService by lazy {
        ApplicationController.instance.networkService
    }
    var dataList: ArrayList<MeasureListData> = ArrayList()
    lateinit var measureOutcomeRecyclerViewAdapter: MeasureOutcomeRecyclerViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_measure)
        jsonObject1.put("user_idx",User.user_idx)
        setRecyclerView()
        getMeasureListResponse()
        setOnClickListener()
    }

    private fun recordVideo() {
        var intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
        Log.d("****MeasureAct::video","동영상촬영")
        intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT,15)
        startActivityForResult(intent,REQUEST_VIDEO_CAPTURE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == REQUEST_VIDEO_CAPTURE && resultCode == Activity.RESULT_OK) {
            videoUri = data!!.data
            val realPath = ImageFilePath.getPath(this@MeasureActivity,videoUri)
            val originalFile = File(realPath)

            val videoBody = RequestBody.create(MediaType.parse(contentResolver.getType(videoUri)), originalFile)
            video = MultipartBody.Part.createFormData(
                    "video",
                    originalFile.name,
                    videoBody
            )

            Log.d("****MeasureAct::video","비디오 촬영 끝")
            postMeasureResponse()
        }
    }

    private fun setOnClickListener(){
        iv_measure_back.setOnClickListener {
            finish()
        }
        var flag = 0
        btn_measure_measure_button.setOnClickListener {
            if(flag==0){
                val list = arrayOf(CAMERA)
                ActivityCompat.requestPermissions(this,list,2)

                val rl_period:RelativeLayout = findViewById(R.id.rl_measure_period)
                rl_period.visibility = View.INVISIBLE

                val measure_button:ImageView = findViewById(R.id.btn_measure_measure_button)
                Glide.with(this).load(R.drawable.measure_ing).into(measure_button)
                btn_measure_measure_button.setImageResource(R.drawable.measure_ing)
                tv_measure_measure_announcement.text = "빈혈 측정 중입니다. 잠시만 기다려주세요."

//                //LED 키기 위한 서버 통신
//                val rl_period:RelativeLayout = findViewById(R.id.rl_measure_period)
//                rl_period.visibility = View.INVISIBLE
//                jsonObject1.put("flag",1)
//                getMeasureFlagResponse()

                //측정 동영상 보내기 서버 통신(period)
                if(!iv_measure_period_select.isSelected){
                    period = 0
                } else if(iv_measure_period_select.isSelected) {
                    period = 1
                }
                //비디오 촬영
                recordVideo()
            }
        }
        iv_measure_period_select.setOnClickListener {
            iv_measure_period_select.isSelected = !iv_measure_period_select.isSelected
        }
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
                    Log.d("MeasureAct::list", response.body().toString())
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

    //측정결과받는 서버통신
    private fun postMeasureResponse() {
        val postMeasureResponse = networkService.postMeasureResponse(User.user_idx,period,video)
        Log.d("****MeasureAct_video", User.user_idx.toString()+"/"+period.toString()+"/"+video)
        postMeasureResponse.enqueue(object : Callback<PostMeasureResponse> {
            override fun onFailure(call: Call<PostMeasureResponse>, t: Throwable) {
                Log.e("postMeasure_Fail", t.toString())
            }

            override fun onResponse(call: Call<PostMeasureResponse>, response: Response<PostMeasureResponse>) {
                if(response.isSuccessful) {
                    Log.d("MeasureAct::outcome:o", response.body().toString())
                    btn_measure_measure_button.setImageResource(R.drawable.measure_complete)
                    tv_measure_measure_announcement.text = "측정이 완료되었습니다."
                    tv_measure_outcome_hb.text = response.body()!!.data!!.hb.toString()+"g/dL"
                    //리사이클러뷰 데이터리스트에 추가
                    val hb = response.body()!!.data!!.hb
                    val date = response.body()!!.data!!.date
                    val date_add: String = "20"+date.substring(0,2)+"-"+date.substring(3,5)+"-"+date.substring(6,8)
                    val temp = MeasureListData(null,hb,period,date_add)
                    measureOutcomeRecyclerViewAdapter.dataList.add(0,temp)
                    measureOutcomeRecyclerViewAdapter.notifyItemInserted(0)
                } else {
                    Log.e("MeasureAct::outcome:x", response.body().toString())
                }
            }
        })
    }
    private fun setRecyclerView() {
        measureOutcomeRecyclerViewAdapter = MeasureOutcomeRecyclerViewAdapter(this,dataList)
        rv_measure_act_list.adapter = measureOutcomeRecyclerViewAdapter
        rv_measure_act_list.layoutManager = LinearLayoutManager(this)
    }

    //    //버튼 클릭(LED_ON) 서버 통신
//    private fun getMeasureFlagResponse() {
//        val gsonObject = JsonParser().parse(jsonObject1.toString()) as JsonObject
//        Log.d("****MeasureAct::flag",gsonObject.toString())
//        val postMeasureFlagResponse: Call<PostMeasureFlagResponse> = networkService.postMeasureflagResponse("application/json",gsonObject)
//        postMeasureFlagResponse.enqueue(object: Callback<PostMeasureFlagResponse>{
//            override fun onFailure(call: Call<PostMeasureFlagResponse>, t: Throwable) {
//                Log.e("MeasureFlag_Failed", t.toString())
//            }
//
//            override fun onResponse(call: Call<PostMeasureFlagResponse>, response: Response<PostMeasureFlagResponse>) {
//                if(response.isSuccessful) {
//                    if(response.body()?.status == 200) {
//                        Log.d("****MeasureAct::flag", response.body().toString())
//                    } else{
//                        Log.d("****MeasureAct::flag", response.body().toString())
//                    }
//                }
//            }
//        })
//    }
}
