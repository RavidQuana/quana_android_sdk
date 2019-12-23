package il.co.quana.di

import il.co.quana.ui.TestDeviceViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {

    viewModel { TestDeviceViewModel( apiService = get(), application =  get()) }

}