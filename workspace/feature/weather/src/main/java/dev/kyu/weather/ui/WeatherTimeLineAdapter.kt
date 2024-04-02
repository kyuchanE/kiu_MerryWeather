package dev.kyu.weather.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import dev.kyu.ui.utils.L
import dev.kyu.weather.R
import dev.kyu.weather.databinding.ItemWeatherTimeLineBinding
import dev.kyu.weather.model.WeatherTimeLineData

class WeatherTimeLineAdapter(
    private val weatherItems: MutableList<WeatherTimeLineData>
): RecyclerView.Adapter<WeatherTimeLineAdapter.WeatherTimeLineViewHolder>() {

    private val itemList: MutableList<WeatherTimeLineData> = mutableListOf()

    init {
        itemList.addAll(weatherItems)
        setHasStableIds(true)
    }

    inner class WeatherTimeLineViewHolder(
        private val itemView: View
    ): RecyclerView.ViewHolder(itemView) {
        private val binding: ItemWeatherTimeLineBinding? = DataBindingUtil.bind(itemView)

        fun bindView(position: Int) {
            binding?.let { b ->
                with(itemList[position]) {
                    b.tvTime.text = this.time
                    b.tvTemperature.text = this.temperature
                    b.tvPop.text = this.pop
                    this.drawable?.let { drawable ->
                        b.ivWeather.setImageDrawable(drawable)
                    }
                }
            }
        }
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeatherTimeLineViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_weather_time_line, parent, false)
        return WeatherTimeLineViewHolder(view)
    }

    override fun onBindViewHolder(holder: WeatherTimeLineViewHolder, position: Int) {
        holder.bindView(position)
    }

    override fun getItemCount(): Int = itemList.size

    fun changeItemList(weatherItems: MutableList<WeatherTimeLineData>) {
        L.d("changeItemList : $weatherItems")
        itemList.clear()
        itemList.addAll(weatherItems)
        notifyDataSetChanged()
    }
}