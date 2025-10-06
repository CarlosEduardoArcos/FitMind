package com.example.fitmind.ui.components

import android.graphics.Color
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.fitmind.viewmodel.ChartViewModel
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter

@Composable
fun WeeklyBarChart(
    data: List<ChartViewModel.ChartEntry>,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val daysOfWeek = listOf("Lun", "Mar", "Mié", "Jue", "Vie", "Sáb", "Dom")
    
    AndroidView(
        factory = { context ->
            BarChart(context).apply {
                description.isEnabled = false
                setDrawGridBackground(false)
                setDrawBorders(false)
                setTouchEnabled(true)
                isDragEnabled = true
                setScaleEnabled(true)
                setPinchZoom(false)
                
                xAxis.apply {
                    position = XAxis.XAxisPosition.BOTTOM
                    setDrawGridLines(false)
                    setDrawAxisLine(true)
                    granularity = 1f
                    valueFormatter = IndexAxisValueFormatter(daysOfWeek)
                }
                
                axisLeft.apply {
                    setDrawGridLines(false)
                    setDrawAxisLine(true)
                    axisMinimum = 0f
                }
                
                axisRight.isEnabled = false
                legend.isEnabled = false
            }
        },
        update = { chart ->
            val entries = data.mapIndexed { index, entry ->
                BarEntry(index.toFloat(), entry.y)
            }
            
            val dataSet = BarDataSet(entries, "Hábitos completados").apply {
                color = Color.parseColor("#FF6200EE")
                setDrawValues(true)
                valueTextSize = 12f
                valueTextColor = Color.BLACK
            }
            
            chart.data = BarData(dataSet)
            chart.invalidate()
        },
        modifier = modifier
            .fillMaxWidth()
            .height(300.dp)
    )
}

@Composable
fun MonthlyLineChart(
    data: List<ChartViewModel.ChartEntry>,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    
    AndroidView(
        factory = { context ->
            LineChart(context).apply {
                description.isEnabled = false
                setDrawGridBackground(false)
                setDrawBorders(false)
                setTouchEnabled(true)
                isDragEnabled = true
                setScaleEnabled(true)
                setPinchZoom(false)
                
                xAxis.apply {
                    position = XAxis.XAxisPosition.BOTTOM
                    setDrawGridLines(false)
                    setDrawAxisLine(true)
                    granularity = 1f
                    valueFormatter = IndexAxisValueFormatter(
                        (1..30).map { "Día $it" }
                    )
                }
                
                axisLeft.apply {
                    setDrawGridLines(false)
                    setDrawAxisLine(true)
                    axisMinimum = 0f
                    axisMaximum = 100f
                }
                
                axisRight.isEnabled = false
                legend.isEnabled = false
            }
        },
        update = { chart ->
            val entries = data.mapIndexed { index, entry ->
                Entry(index.toFloat(), entry.y)
            }
            
            val dataSet = LineDataSet(entries, "Progreso mensual").apply {
                color = Color.parseColor("#FF03DAC5")
                setCircleColor(Color.parseColor("#FF03DAC5"))
                lineWidth = 3f
                circleRadius = 5f
                setDrawValues(true)
                valueTextSize = 12f
                valueTextColor = Color.BLACK
                setDrawFilled(true)
                fillColor = Color.parseColor("#1A03DAC5")
            }
            
            chart.data = LineData(dataSet)
            chart.invalidate()
        },
        modifier = modifier
            .fillMaxWidth()
            .height(300.dp)
    )
}
