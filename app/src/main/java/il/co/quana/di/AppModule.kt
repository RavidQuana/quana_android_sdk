package il.co.quana.di

import il.co.quana.data.SampleRepository
import il.co.quana.features.TestDeviceViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {

    factory { SampleRepository(context = get() ,apiService = get() ) }

    viewModel { TestDeviceViewModel( sampleRepository = get(), application =  get()) }

}