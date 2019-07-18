package org.sopt.wjdma.emblecare.Activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_measure.*
import org.sopt.wjdma.emblecare.R

class MeasureActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_measure)
        img_measure_back.setOnClickListener {
            finish()
        }
        var flag = 0
        btn_measure_measure_button.setOnClickListener {
            if(flag==0){
                val measure_button:ImageView = findViewById(R.id.btn_measure_measure_button)
                Glide.with(this).load(R.drawable.measure_ing).into(measure_button)
                btn_measure_measure_button.setImageResource(R.drawable.measure_ing)
                txt_measure_measure_announcement.setText("빈혈 측정 중입니다. 잠시만 기다려주세요.")
                flag=1
            }
            else if(flag==1){
                btn_measure_measure_button.setImageResource(R.drawable.measure_complete)
                txt_measure_measure_announcement.setText("측정이 완료되었습니다.")
                val rl_period:RelativeLayout = findViewById(R.id.rl_measure_period)
                rl_period.setVisibility(View.VISIBLE)

            }
        }
        img_measure_period_select.setOnClickListener {
            img_measure_period_select.isSelected = !img_measure_period_select.isSelected
        }
    }
}
