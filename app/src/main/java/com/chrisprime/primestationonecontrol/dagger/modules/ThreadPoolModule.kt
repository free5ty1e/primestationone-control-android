package com.chrisprime.primestationonecontrol.dagger.modules


import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

import javax.inject.Singleton

import dagger.Module
import dagger.Provides

@Module
class ThreadPoolModule {

    @Provides
    @Singleton
    fun provideExecutorService(): ExecutorService {
        return Executors.newCachedThreadPool()
    }
}
