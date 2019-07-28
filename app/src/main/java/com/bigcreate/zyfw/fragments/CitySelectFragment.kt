package com.bigcreate.zyfw.fragments

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.*
import android.widget.TextView
import androidx.core.view.setPadding
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bigcreate.library.fromJson
import com.bigcreate.zyfw.R
import com.bigcreate.zyfw.base.Attributes
import com.bigcreate.zyfw.models.City
import com.bigcreate.zyfw.models.Province
import kotlinx.android.synthetic.main.fragment_city_select.*
import java.io.BufferedReader
import java.io.InputStreamReader

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 */
class CitySelectFragment : DialogFragment() {
    //    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
//                              savedInstanceState: Bundle?): View? {
//        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_city_select, container, false)
//    }
    private val titleFormat = "选择：%s -> %s"
    private var city = ""
    private var province = ""
    private var selectBackgroundDrawable: Drawable? = null
    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(uri: Uri) {
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return Dialog(context!!, R.style.bottomDialog).apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setContentView(R.layout.fragment_city_select)
            setCanceledOnTouchOutside(true)
            window?.apply {
                attributes.gravity = Gravity.TOP
                attributes.width = WindowManager.LayoutParams.MATCH_PARENT
            }
            toolbarSelectCity.setNavigationOnClickListener {
                dismiss()
            }
            toolbarSelectCity.inflateMenu(R.menu.toolbar_selecte_city)
            toolbarSelectCity.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.selectDoneCity -> if (city.isNotEmpty()) {
                        Attributes.AppCity = city
                        dismiss()
                    }
                }
                true
            }
            toolbarSelectCity.title = "选择城市"
            toolbarSelectCity.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
            val height = toolbarSelectCity.measuredHeight
            cityListCitySelected.apply {
                setPadding(paddingLeft, paddingTop + height, paddingRight, paddingBottom)
            }
            provinceListCitySelected.apply {
                setPadding(paddingLeft, paddingTop + height, paddingRight, paddingBottom)
            }
            val typedValue = TypedValue()
            context.theme.resolveAttribute(R.attr.colorPrimary, typedValue, true)
            val attr = context.theme.obtainStyledAttributes(typedValue.resourceId, intArrayOf(R.attr.colorPrimary))
            selectBackgroundDrawable = attr.getDrawable(0)?.constantState?.newDrawable()
            selectBackgroundDrawable?.alpha = (0.6f * 255).toInt()
            var cityJson = ""
            try {
                val inputStream = context.assets.open("cityList.json")
                val buffer = BufferedReader(InputStreamReader(inputStream))
                var temp = buffer.readLine()
                while (temp != null) {
                    cityJson += temp
                    temp = buffer.readLine()
                }
                Log.e("cityJson", cityJson)
                val cityList = cityJson.fromJson<List<Province>>()
                provinceListCitySelected.layoutManager = LinearLayoutManager(context)
                provinceListCitySelected.adapter = ProvinceAdapter(cityList)
                var hasFind = false
                for (i in 0 until cityList.size) {
                    val province = cityList[i]
                    for (j in 0 until province.city.size) {
                        val city = province.city[j]
                        if (city.name == Attributes.AppCity) {
                            (provinceListCitySelected.adapter as ProvinceAdapter).selectedPosition = i
                            provinceListCitySelected.scrollToPosition(i)
                            toolbarSelectCity.title = String.format("当前：%s -> %s", province.name, city.name)
                            this@CitySelectFragment.city = city.name
                            this@CitySelectFragment.province = province.name
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
            (holder.itemView as TextView).run {
                list[position].apply {
                    text = name
                    maxEms = 5
                    background = if (position == selectedPosition) selectBackgroundDrawable
                    else ColorDrawable(Color.parseColor("#00000000"))
                    setOnClickListener {
                        selectedPosition = position
                        dialog?.apply {
                            province = name
                            toolbarSelectCity.title = String.format(titleFormat, name, this@CitySelectFragment.city)
                            cityListCitySelected.layoutManager = LinearLayoutManager(context)
                            cityListCitySelected.adapter = CityAdapter(city)
                        }
                    }
                    if (selectedPosition == position) {
                        dialog?.apply {
                            cityListCitySelected.layoutManager = LinearLayoutManager(context)
                            cityListCitySelected.adapter = CityAdapter(city)
                        }
                    }
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProvinceViewHolder {
            return ProvinceViewHolder(TextView(parent.context).apply {
                setEms(5)
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
                val padding = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8f, context.resources.displayMetrics)
                setPadding(padding.toInt())
            })
        }
    }

    inner class CityAdapter(val list: List<City>) : RecyclerView.Adapter<CityViewHolder>() {
        override fun getItemCount(): Int {
            return list.size
        }

        override fun onBindViewHolder(holder: CityViewHolder, position: Int) {
            (holder.itemView as TextView).run {
                list[position].apply {
                    text = name
                    setOnClickListener {
                        city = name
                        dialog?.apply {
                            toolbarSelectCity.title = String.format(titleFormat, province, city)
                        }
                    }

                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CityViewHolder {
            return CityViewHolder(TextView(parent.context).apply {
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
                val padding = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8f, context.resources.displayMetrics)
                setPadding(padding.toInt())
                val typedValue = TypedValue()
                context.theme.resolveAttribute(android.R.attr.selectableItemBackground, typedValue, true)
                val attribute = intArrayOf(android.R.attr.selectableItemBackground)
                val typedArray = context.theme.obtainStyledAttributes(typedValue.resourceId, attribute)
                background = typedArray.getDrawable(0)
                layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            })
        }
    }

//
//  { "name": "钓鱼岛", "city":[
//
//    {"name":"是", "area":["中国的"]}
//
//  ]}
    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments]
     * (http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */


}
