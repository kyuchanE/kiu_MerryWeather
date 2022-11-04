package kiu.dev.merryweather.ui.fragment.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import kiu.dev.merryweather.R
import kiu.dev.merryweather.data.WeatherTimeLineData
import kiu.dev.merryweather.databinding.ItemWeatherTimeLineBinding

class WeatherTimeLineAdapter(
    private val weatherItems: MutableList<WeatherTimeLineData>
): RecyclerView.Adapter<WeatherTimeLineAdapter.WeatherTimeLineViewHolder>() {

    private val itemList: MutableList<WeatherTimeLineData> = mutableListOf()

    init {
        itemList.addAll(weatherItems)
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

    fun getItemList(): MutableList<WeatherTimeLineData> = itemList

    fun changeItemList(weatherItems: MutableList<WeatherTimeLineData>) {
        itemList.clear()
        itemList.addAll(weatherItems)
        notifyDataSetChanged()
    }

    inner class WeatherTimeLineViewHolder(
        private val itemView: View
    ): RecyclerView.ViewHolder(itemView) {
        private val binding: ItemWeatherTimeLineBinding? = DataBindingUtil.bind(itemView)

        fun bindView(position: Int) {
            binding?.let {
                with(itemList[position]) {
                    it.tvTime.text = this.time
                    it.tvTemperature.text = this.temperature
                    it.ivWeather.setImageDrawable(this.drawable)
                }
            }
        }

    }
}