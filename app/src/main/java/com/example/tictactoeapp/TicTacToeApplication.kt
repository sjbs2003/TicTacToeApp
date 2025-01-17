package com.example.tictactoeapp

import android.app.Application
import com.example.tictactoeapp.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin


class TicTacToeApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@TicTacToeApplication)
            modules(appModule)
        }
    }
}