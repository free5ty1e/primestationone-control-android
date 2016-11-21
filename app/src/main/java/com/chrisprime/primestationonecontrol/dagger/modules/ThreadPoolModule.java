package com.chrisprime.primestationonecontrol.dagger.modules;


import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class ThreadPoolModule {

    public ThreadPoolModule() {

    }

    @Provides
    @Singleton
    ExecutorService provideExecutorService() {
        return Executors.newCachedThreadPool();
    }
}
