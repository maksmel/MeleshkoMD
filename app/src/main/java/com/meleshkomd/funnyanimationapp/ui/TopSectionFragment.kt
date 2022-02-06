package com.meleshkomd.funnyanimationapp.ui

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.meleshkomd.funnyanimationapp.R

class TopSectionFragment : MainFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_top_section, container, false)
        initializeGlobalConstants(view)
        circularProgressDrawable.strokeWidth = 5f
        circularProgressDrawable.centerRadius = 30f
        circularProgressDrawable.setColorSchemeColors(Color.BLUE)
        circularProgressDrawable.start()
        val buttonBack: ImageButton = view.findViewById(R.id.btnBackLatest) as ImageButton
        buttonBack.setOnClickListener { getPreviousGif() }
        val buttonNext: ImageButton = view.findViewById(R.id.btnNextLatest) as ImageButton
        buttonNext.setOnClickListener { getNextGif() }
        getCurrentGif()
        return view
    }

    private fun initializeGlobalConstants(view: View) {
        category = "top"
        imageView = view.findViewById(R.id.gifImageLatest)
        textView = view.findViewById(R.id.titleLatest)
        circularProgressDrawable = CircularProgressDrawable(imageView.context)
    }
}