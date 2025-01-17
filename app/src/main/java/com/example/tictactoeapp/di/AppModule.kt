package com.example.tictactoeapp.di

import com.example.tictactoeapp.data.KtorRealtimeMessagingClient
import com.example.tictactoeapp.data.RealtimeMessagingClient
import com.example.tictactoeapp.presenatation.TicTacToeViewModel
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.websocket.WebSockets
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single {
        HttpClient(CIO) {
            install(Logging)
            install(WebSockets)
        }
    }

    single<RealtimeMessagingClient> {
        KtorRealtimeMessagingClient(get())
    }

    viewModel {
        TicTacToeViewModel(get())
    }
}
