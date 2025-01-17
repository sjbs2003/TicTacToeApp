package com.example.tictactoeapp.data

import kotlinx.coroutines.flow.Flow

interface RealtimeMessagingClient {

    // whenever new game state flow from server this flow will trigger
    // then we can update our local state
    fun getGameStateStream(): Flow<GameState>
    suspend fun sendAction(action: MakeTurn)
    suspend fun close ()
}