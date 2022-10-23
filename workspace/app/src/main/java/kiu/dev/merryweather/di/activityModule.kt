package kiu.dev.merryweather.di

import kiu.dev.merryweather.ui.activity.MainViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val activityModule = module {
    viewModel {
        MainViewModel(get())
    }
}