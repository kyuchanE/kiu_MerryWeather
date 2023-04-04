package kiu.dev.merryweather.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import kiu.dev.merryweather.R
import kiu.dev.merryweather.data.local.WeatherTimeLineData
import kiu.dev.merryweather.databinding.ItemWeatherWeekLineBinding

class WeatherWeekLineAdapter(
    private val weatherItems: MutableList<WeatherTimeLineData>
): RecyclerView.Adapter<WeatherWeekLineAdapter.WeatherWeekLineViewHolder>() {
    private val itemList: MutableList<WeatherTimeLineData> = mutableListOf()

    init {
        itemList.addAll(weatherItems)
        setHasStableIds(true)
    }

    inner class WeatherWeekLineViewHolder(
        private val itemView: View
    ): RecyclerView.ViewHolder(itemView) {
        private val binding: ItemWeatherWeekLineBinding? = DataBindingUtil.bind(itemView)

        fun bindView(position: Int) {
            binding?.let { b ->

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeatherWeekLineViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_weather_week_line, parent, false)
        return WeatherWeekLineViewHolder(view)
    }

    override fun onBindViewHolder(holder: WeatherWeekLineViewHolder, position: Int) {
        holder.bindView(position)
    }

    override fun getItemCount(): Int = itemList.size

    fun changeItemList(weatherItems: MutableList<WeatherTimeLineData>) {
        itemList.clear()
        itemList.addAll(weatherItems)
        notifyDataSetChanged()
    }
}