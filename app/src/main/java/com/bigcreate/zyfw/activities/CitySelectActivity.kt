package com.bigcreate.zyfw.activities

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.util.TypedValue
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.view.setPadding
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bigcreate.library.fromJson
import com.bigcreate.zyfw.R
import com.bigcreate.zyfw.base.Attributes
import com.bigcreate.zyfw.fragments.CitySelectFragment
import com.bigcreate.zyfw.models.City
import com.bigcreate.zyfw.models.Province
import com.google.android.material.button.MaterialButton
import kotlinx.android.synthetic.main.activity_city_select.*
import java.io.BufferedReader
import java.io.InputStreamReader

class CitySelectActivity : AuthLoginActivity() {
    private val titleFormat = "选择：%s -> %s"
    private var city = ""
    private var province = ""
    private var selectBackgroundDrawable: Drawable? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_city_select)
        toolbarSelectCity.setNavigationOnClickListener {
            finish()
        }
        toolbarSelectCity.inflateMenu(R.menu.toolbar_selecte_city)
        toolbarSelectCity.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.selectDoneCity -> if (city.isNotEmpty()) {
                    Attributes.AppCity = city
                    finish()
                }
            }
            true
        }
        toolbarSelectCity.title = "选择城市"
        toolbarSelectCity.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        toolbarSelectCity.menu.findItem(R.id.selectDoneCity)?.apply {
            val titleSpan = SpannableString(title)
            titleSpan.setSpan(ForegroundColorSpan(getColor(R.color.colorAccent)),0,title.length,SpannableString.SPAN_INCLUSIVE_INCLUSIVE)
        }
//        val height = toolbarSelectCity.measuredHeight
//        cityListCitySelected.apply {
//            setPadding(paddingLeft, paddingTop + height, paddingRight, paddingBottom)
//        }
//        provinceListCitySelected.apply {
//            setPadding(paddingLeft, paddingTop + height, paddingRight, paddingBottom)
//        }
        val typedValue = TypedValue()
        theme.resolveAttribute(R.attr.colorPrimary, typedValue, true)
        val attr = theme.obtainStyledAttributes(typedValue.resourceId, intArrayOf(R.attr.colorPrimary))
        selectBackgroundDrawable = attr.getDrawable(0)?.constantState?.newDrawable()
        selectBackgroundDrawable?.alpha = (0.6f * 255).toInt()
        var cityJson = ""
        try {
            val inputStream = assets.open("cityList.json")
            val buffer = BufferedReader(InputStreamReader(inputStream))
            var temp = buffer.readLine()
            while (temp != null) {
                cityJson += temp
                temp = buffer.readLine()
            }
            Log.e("cityJson", cityJson)
            val cityList = cityJson.fromJson<List<Province>>()
            provinceListCitySelected.layoutManager = LinearLayoutManager(this)
            provinceListCitySelected.adapter = ProvinceAdapter(cityList)
//            for (i in cityList.indices) {
//
//            }
            var hasFind = false
//            groupProvince.addOnButtonCheckedListener { group, checkedId, isChecked ->
//                if (checkedId >= 0) {
//                    province = groupProvince.findViewById<MaterialButton>(checkedId).text.toString()
//                    city = ""
//                    toolbarSelectCity.title = String.format("当前：%s -> %s", province, city)
//                    groupCity.clearChecked()
//                    groupCity.removeAllViews()
//                    val province = cityList[checkedId]
//                    for (i in province.city.indices ) {
//                        groupCity.addView(MaterialButton(this).apply {
//                            text = province.city[i].name
//                            id = i
//                        })
//                    }
//                }
//            }
//            groupCity.addOnButtonCheckedListener { group, checkedId, isChecked ->
//                if (checkedId >= 0) {
//                    city = groupCity.findViewById<MaterialButton>(checkedId).text.toString()
//                    toolbarSelectCity.title = String.format("当前：%s -> %s", province, city)
//                }
//            }
            for (i in cityList.indices) {
                val province = cityList[i]

                for (element in province.city) {
                    if (element.name == Attributes.AppCity + "市") {
                        (provinceListCitySelected.adapter as ProvinceAdapter).selectedPosition = i
                        provinceListCitySelected.scrollToPosition(i)
                        toolbarSelectCity.title = String.format("当前：%s -> %s", province.name, element.name)
                        this.city = element.name
                        this.province = province.name
                        hasFind = true
                        break
                    }
                }
                if (hasFind)
                    break
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun afterCheckLoginSuccess() {

    }

    override fun setContentView() {

    }

    inner class ProvinceViewHolder(view: View) : RecyclerView.ViewHolder(view)
    inner class CityViewHolder(view: View) : RecyclerView.ViewHolder(view)
    inner class ProvinceAdapter(val list: List<Province>) : RecyclerView.Adapter<ProvinceViewHolder>() {
        var selectedPosition = -1
            set(value) {
                if (field != value) {
                    city = ""
                    val temp = field
                    field = value
                    if (temp != -1)
                        notifyItemChanged(temp)
                    notifyItemChanged(value)
                }

            }

        override fun getItemCount(): Int {
            return list.size
        }

        override fun onBindViewHolder(holder: ProvinceViewHolder, position: Int) {
            (holder.itemView as MaterialButton).run {
                list[position].apply {
                    text = name
                    maxEms = 5
                    setEms(5)
//                    background = if (position == selectedPosition) selectBackgroundDrawable
//                    else ColorDrawable(Color.parseColor("#00000000"))
                    isChecked = position == selectedPosition
                    setOnClickListener {
                        selectedPosition = position
                            province = name
                            toolbarSelectCity.title = String.format(titleFormat, name, this@CitySelectActivity.city)
                            cityListCitySelected.layoutManager = LinearLayoutManager(context)
                            cityListCitySelected.adapter = CityAdapter(city)

                    }
                    if (selectedPosition == position) {
                            cityListCitySelected.layoutManager = LinearLayoutManager(context)
                            cityListCitySelected.adapter = CityAdapter(city)
                    }
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProvinceViewHolder {
            return ProvinceViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_material_outline_button,parent,false))
        }
    }

    inner class CityAdapter(val list: List<City>) : RecyclerView.Adapter<CityViewHolder>() {
        var selectedPosition = -1
            set(value) {
                if (field != value) {
//                    city = list[value].name
                    val temp = field
                    field = value
                    if (temp != -1)
                        notifyItemChanged(temp)
                    notifyItemChanged(value)
                }

            }
        override fun getItemCount(): Int {
            return list.size
        }

        override fun onBindViewHolder(holder: CityViewHolder, position: Int) {
            (holder.itemView as MaterialButton).run {
                list[position].apply {
                    text = name
                    setOnClickListener {
                        isChecked = position == selectedPosition
                        city = name
                        toolbarSelectCity.title = String.format(titleFormat, province, city)

                    }

                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CityViewHolder {
            return CityViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_material_outline_button,parent,false))
        }
    }
}
