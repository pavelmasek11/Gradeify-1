package com.example.xmltest

import android.os.Bundle
import androidx.activity.ComponentActivity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlin.random.Random


interface MainView {
    fun showActiveScale()
    fun showMarksBubble()
    fun showGraph()
    fun showStatsTable()
    fun showProcentArray()
    fun showAllTasks()
    fun showBtnPressed()

}

class MainViewImp : ComponentActivity(), MainView{
    private lateinit var controller: MainControllerImp


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
    }



    val scaleRepository: ScaleModel = ScaleModelImp()

    override fun showActiveScale(){

    }
    override fun showMarksBubble(){

    }
    override fun showGraph(){

    }
    override fun showStatsTable(){

    }
    override fun showProcentArray(){

    }
    override fun showAllTasks(){

    }

    override fun showBtnPressed(){

    }


}