package il.co.quana.di

import il.co.quana.data.SampleRepository
import il.co.quana.ui.TestDeviceViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {

    factory { SampleRepository(apiService = get()) }

    viewModel { TestDeviceViewModel( sampleRepository = get(), application =  get()) }

}