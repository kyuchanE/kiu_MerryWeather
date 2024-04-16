package dev.kyu.main.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import dev.kyu.domain.model.WeatherData
import dev.kyu.main.R
import dev.kyu.main.databinding.ItemWeatherWeekLineBinding
import dev.kyu.ui.utils.getDayOfWeek
import dev.kyu.ui.utils.getSkyDrawable

class WeatherWeekLineAdapter(
    private val context: Context,
    private val weatherItems: MutableList<WeatherWeekLineData>
): RecyclerView.Adapter<WeatherWeekLineAdapter.WeatherWeekLineViewHolder>() {
    private val itemList: MutableList<WeatherWeekLineData> = mutableListOf()

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
                with(itemList[position]) {
                    b.tvDay.text = if (position == 0) "오늘" else getDayOfWeek(position)
                    b.tvPop.text = "${this.pop}%"       // TODO chan 강수 확률은 비 소식이 있을때만?
                    b.tvTmx.text = this.tmx
                    b.tvTmn.text = this.tmn
                    val skyDrawable = context.getSkyDrawable(this.pty, this.sky)
                    b.ivAmSky.setImageDrawable(skyDrawable)
                }
            }
        }
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
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

    fun changeItemList(weatherItems: MutableList<WeatherWeekLineData>) {
        itemList.clear()
        itemList.addAll(weatherItems)
        notifyDataSetChanged()
    }
}