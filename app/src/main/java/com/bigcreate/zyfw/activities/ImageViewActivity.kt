package com.bigcreate.zyfw.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.bigcreate.library.translucentSystemUI
import com.bigcreate.zyfw.R
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_image_view.*
import kotlinx.android.synthetic.main.activity_main.*

class ImageViewActivity : AppCompatActivity() {

    private var imageList = emptyArray<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_view)
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        imageList = intent.getStringArrayExtra("list")
        viewPagerImageView.offscreenPageLimit = imageList.size
        viewPagerImageView.adapter = ViewPagerAdapter()
        postponeEnterTransition()
        viewPagerImageView.transitionName = imageList[0]
        viewPagerImageView.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {
                viewPagerImageView.transitionName = imageList[position]
            }
        })
        viewPagerImageView.setCurrentItem(intent.getIntExtra("position",0),false)
        startPostponedEnterTransition()
    }

    inner class ViewPagerAdapter:PagerAdapter() {
        override fun getCount(): Int {
            Log.e("size","${imageList.size}")
            return imageList.size
        }

        override fun isViewFromObject(view: View, `object`: Any): Boolean {
            return view == (`object` as View)
        }

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            return ImageView(container.context).apply {
                layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT)
                Glide.with(context)
                        .load(imageList[position])
                        .into(this)
//                transitionName = imageList[position]
                container.addView(this)
            }
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            container.removeViewAt(position)
        }
    }
}
