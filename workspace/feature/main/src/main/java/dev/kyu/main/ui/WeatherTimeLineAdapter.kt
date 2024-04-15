package dev.kyu.main.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import dev.kyu.domain.model.WeatherData
import dev.kyu.main.R
import dev.kyu.main.databinding.ItemWeatherTimeLineBinding
import dev.kyu.ui.utils.L
import dev.kyu.ui.utils.getSkyDrawable

class WeatherTimeLineAdapter(
    private val context: Context,
    private val weatherItems: MutableList<WeatherData>
): RecyclerView.Adapter<WeatherTimeLineAdapter.WeatherTimeLineViewHolder>() {

    private val itemList: MutableList<WeatherData> = mutableListOf()

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
                    b.tvTime.text = this.dateTime
                    b.tvTemperature.text = this.t1h
                    b.tvPop.text = this.pop
                    val skyDrawable = context.getSkyDrawable(this.pty, this.sky)
                    b.ivWeather.setImageDrawable(skyDrawable)
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

    fun changeItemList(weatherItems: MutableList<WeatherData>) {
        L.d("changeItemList : $weatherItems")
        itemList.clear()
        itemList.addAll(weatherItems)
        notifyDataSetChanged()
    }
}