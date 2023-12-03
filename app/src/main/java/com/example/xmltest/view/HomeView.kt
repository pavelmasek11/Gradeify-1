package com.example.xmltest

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.example.xmltest.controller.Communication
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import java.text.DecimalFormat

// Rozhraní pro komunikaci mezi třídami
interface HomeView : Communication {
    fun updateCardViewContent(option: Int)
}

// Implementace fragmentu HomeView
class HomeViewImp : Fragment(), HomeView {
    // Proměnné pro uchování referencí na komponenty uživatelského rozhraní
    private lateinit var presenterScaleModel: ScaleModel
    private lateinit var cardViewToFillHome: CardView
    private lateinit var presenterHomeController: HomeController
    private lateinit var rootView: View
    private lateinit var averageTextView: TextView
    private lateinit var stdDevTextView: TextView
    private lateinit var medianTextView: TextView
    private lateinit var deleteLastMarkBtn: Button
    private var currentLayoutResId: Int = 0
    private lateinit var barChart: BarChart

    // Metoda volaná při vytváření view pro fragment
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflace layoutu pro tento fragment
        rootView = inflater.inflate(R.layout.activity_home, container, false)

        // Inicializace komponent uživatelského rozhraní
        averageTextView = rootView.findViewById(R.id.Avarage_num)
        deleteLastMarkBtn = rootView.findViewById(R.id.deleteLastMarkBtn)
        stdDevTextView = rootView.findViewById(R.id.Std_num)
        medianTextView = rootView.findViewById(R.id.Median_num)
        barChart = rootView.findViewById(R.id.chart)

        // Inicializace modelu a controlleru
        presenterScaleModel = ScaleModelImp(requireContext())
        presenterHomeController =
            HomeControllerImp(presenterScaleModel, MarkModel(MarkDatabase.getInstance(requireContext()).markDao()))

        // Nastavení výchozího layoutu pro CardView
        currentLayoutResId = R.layout.activity_home_marks_one_five
        cardViewToFillHome = rootView.findViewById(R.id.cardViewToFillHomeId)
        setCardViewContent(currentLayoutResId)


        // Inicializace a nastavení posluchače pro tlačítko reset
        val resetBtn: Button = rootView.findViewById(R.id.resetStatsBtn)
        resetBtn.setOnClickListener {
            if (isAdded) {
                presenterScaleModel.onResetBtnClick()
                presenterHomeController.clearMarksList()
                updateStatistics()
            }
        }
        setupMarkButtonsListeners()
        updateStatistics()
        return rootView
    }

    // ToDo: Zamyslet se zda toto nepůjde sdružit -> velice podobný kód.
    // Metoda pro nastavení posluchačů pro tlačítka s hodnocením
    private fun setupMarkButtonsListeners(){
        rootView.findViewById<CardView>(R.id.markOneBtn)?.setOnClickListener {
            presenterHomeController.handleMarkButtonClick(1)
            updateStatistics()
        }
        rootView.findViewById<CardView>(R.id.markTwoBtn)?.setOnClickListener {
            presenterHomeController.handleMarkButtonClick(2)
            updateStatistics()
        }
        rootView.findViewById<CardView>(R.id.markThreeBtn)?.setOnClickListener {
            presenterHomeController.handleMarkButtonClick(3)
            updateStatistics()
        }
        rootView.findViewById<CardView>(R.id.markFourBtn)?.setOnClickListener {
            presenterHomeController.handleMarkButtonClick(4)
            updateStatistics()
        }
        rootView.findViewById<CardView>(R.id.markFiveBtn)?.setOnClickListener {
            presenterHomeController.handleMarkButtonClick(5)
            updateStatistics()
        }
    }

    // Metoda pro nastavení posluchačů pro tlačítka s hodnocením
    private fun setupButtonListeners() {
        cardViewToFillHome.findViewById<CardView>(R.id.markOneBtn)?.setOnClickListener {
            presenterHomeController.handleMarkButtonClick(1)
            updateStatistics()
        }
        cardViewToFillHome.findViewById<CardView>(R.id.markTwoBtn)?.setOnClickListener {
            presenterHomeController.handleMarkButtonClick(2)
            updateStatistics()
        }
        cardViewToFillHome.findViewById<CardView>(R.id.markThreeBtn)?.setOnClickListener {
            presenterHomeController.handleMarkButtonClick(3)
            updateStatistics()
        }
        cardViewToFillHome.findViewById<CardView>(R.id.markFourBtn)?.setOnClickListener {
            presenterHomeController.handleMarkButtonClick(4)
            updateStatistics()
        }
        cardViewToFillHome.findViewById<CardView>(R.id.markFiveBtn)?.setOnClickListener {
            presenterHomeController.handleMarkButtonClick(5)
            updateStatistics()
        }
    }

    // Metoda pro aktualizaci statistik a grafu
    private fun updateStatistics(){
        updateTableOfNumbers()
        updateBarChart()
    }

    // Metoda pro aktualizaci tabulky s hodnotami
    private fun updateTableOfNumbers() {
        val average = presenterHomeController.getAverageMark()
        val StandardDeviation = presenterHomeController.getStandardDeviation()
        val median = presenterHomeController.getMedian()
        val decimalFormat = DecimalFormat("#.##")

        averageTextView.text = decimalFormat.format(average)
        stdDevTextView.text = decimalFormat.format(StandardDeviation)
        medianTextView.text = decimalFormat.format(median)
    }

    // Metoda pro aktualizaci sloupcového grafu
    private fun updateBarChart() {
        val marksFrequency = presenterHomeController.getMarksList().groupingBy { it.toInt() }.eachCount()

        val entries = mutableListOf<BarEntry>()

        // Přidání hodnot do datové sady a nastavení etiket pro osu X
        for (i in 1..5) {
            val frequency = marksFrequency[i] ?: 0
            entries.add(BarEntry(i.toFloat(), frequency.toFloat()))
        }

        val dataSet = BarDataSet(entries, null)

        // Definice barev pro jednotlivé sloupce
        val colors = intArrayOf(
            R.color.color_mark_1,
            R.color.color_mark_2,
            R.color.color_mark_3,
            R.color.color_mark_4,
            R.color.color_mark_5
        )

        // Nastavení barev pro jednotlivé sloupce
        dataSet.colors = colors.map { requireContext().getColor(it) }

        // Přidání černého ohraničení jednotlivým sloupcům
        dataSet.barBorderColor = Color.BLACK
        dataSet.barBorderWidth = 1f

        val data = BarData(dataSet)

        val barChart = rootView.findViewById<BarChart>(R.id.chart)
        barChart.data = data

        // Vypnutí popisků pro hlavní graf
        barChart.description = null

        // Vypnutí popisků podgrafů s barvami
        for (set in barChart.data.dataSets) {
            set.setDrawValues(false)
        }

        // Vypnutí automatického generování etiket pro osu X
        barChart.xAxis.setDrawLabels(false)
        barChart.xAxis.setDrawAxisLine(false)
        barChart.legend.isEnabled = false

        // Nastavení osy Y na pravé straně
        val rightAxis = barChart.axisRight
        rightAxis.valueFormatter = object : ValueFormatter() {
            override fun getAxisLabel(value: Float, axis: AxisBase?): String {
                return (value - 2).toInt().toString() // Posunutí osy Y o 2 body napřed
            }
        }

        // Nastavení rozsahu osy Y od 0 do maximální hodnoty nebo 8, pokud nepřesáhne 8
        val maxValue = marksFrequency.values.maxOrNull() ?: 8
        barChart.axisLeft.axisMinimum = 0f
        barChart.axisLeft.axisMaximum = if (maxValue > 8) maxValue.toFloat() else 8f
        rightAxis.axisMinimum = 0f
        rightAxis.axisMaximum = if (maxValue > 8) maxValue.toFloat() else 8f

        // Nastavení mřížky grafu
        barChart.xAxis.setDrawGridLines(true)
        barChart.xAxis.gridColor = Color.BLACK
        barChart.xAxis.gridLineWidth = 1.5f

        barChart.axisLeft.setDrawGridLines(true)
        barChart.axisLeft.gridColor = Color.BLACK
        barChart.axisLeft.gridLineWidth = 1.5f

        barChart.axisRight.setDrawGridLines(true)
        barChart.axisRight.gridColor = Color.BLACK
        barChart.axisRight.gridLineWidth = 1.5f

        val xAxis: XAxis = barChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM

        // Nastavení vlastních etiket pro osu X
        xAxis.valueFormatter = object : ValueFormatter() {
            override fun getAxisLabel(value: Float, axis: AxisBase?): String {
                return value.toInt().toString()
            }
        }

        // Nastavení formátu pro osu Y na celočíselné hodnoty
        val yAxis = barChart.axisLeft
        yAxis.valueFormatter = object : ValueFormatter() {
            override fun getAxisLabel(value: Float, axis: AxisBase?): String {
                return value.toInt().toString()
            }
        }

        // Nastavení velikosti písma pro osu Y
        yAxis.textSize = 16f

        // Vypnutí pravé osy Y
        barChart.axisRight.isEnabled = false

        barChart.invalidate()
    }

    // Metoda volaná po vytvoření view pro fragment
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Nastavení vybrané možnosti po vytvoření view
        (activity as? Communication)?.onOptionSelected(1)
        // Nastavení posluchače pro tlačítko pro odstranění poslední známky
        deleteLastMarkBtn.setOnClickListener {
            presenterHomeController.removeLastMark()
            updateStatistics()
        }
    }

    // Metoda pro aktualizaci obsahu CardView podle vybrané možnosti
    override fun updateCardViewContent(option: Int) {
        if (isAdded) {
            // Aktualizace statistik, protože jinde se neprovede až po načtení databáze
            updateStatistics()
            when (option) {
                1 -> {
                    currentLayoutResId = R.layout.activity_home_marks_one_five
                    setCardViewContent(currentLayoutResId)
                }
                2 -> {
                    currentLayoutResId = R.layout.activity_home_marks_a_f
                    setCardViewContent(currentLayoutResId)
                }
                3 -> {
                    currentLayoutResId = R.layout.activity_home_marks_one_four
                    setCardViewContent(currentLayoutResId)
                }
                else -> {
                    currentLayoutResId = R.layout.activity_home_marks_a_f
                    setCardViewContent(currentLayoutResId)
                }
            }
        }
    }

    // Metoda pro nastavení obsahu CardView podle layoutu
    private fun setCardViewContent(layoutResId: Int) {
        val inflater = LayoutInflater.from(requireContext())
        val contentView = inflater.inflate(layoutResId, cardViewToFillHome, false)
        cardViewToFillHome.removeAllViews()
        cardViewToFillHome.addView(contentView)
        setupButtonListeners()
    }

    // Metoda volaná při výběru možnosti
    override fun onOptionSelected(option: Int) {
        Log.d("HomeViewImp", "RadioButton clicked with option: $option")
        if (isAdded) {
            updateCardViewContent(option)
        }
    }

    // Metoda volaná při zničení view
    //TODO: Je opravdu nutná?? - zatím nechat do doby než se bude optimalizovat
    override fun onDestroyView() {
        super.onDestroyView()
    }
}