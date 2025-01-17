package com.example.tictactoeapp.presenatation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tictactoeapp.data.GameState
import org.koin.androidx.compose.koinViewModel


@Composable
fun TicTacToeScreen(
    viewModel: TicTacToeViewModel = koinViewModel()
) {
    val gameState by viewModel.state.collectAsState()
    val isConnecting by viewModel.isConnecting.collectAsState()
    val showConnectionError by viewModel.showConnectionError.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            GameHeader(gameState = gameState)
            GameBoard(
                gameState = gameState,
                onTileClick = viewModel::finishTurn
            )
            GameStatus(
                gameState = gameState,
                isConnecting = isConnecting,
                showConnectionError = showConnectionError
            )
        }
    }
}

@Composable
fun GameHeader(gameState: GameState) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Tic Tac Toe",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            PlayerCard(
                symbol = 'X',
                isConnected = gameState.connectedPlayers.contains('X'),
                isCurrentTurn = gameState.playerAtTurn == 'X'
            )
            PlayerCard(
                symbol = 'O',
                isConnected = gameState.connectedPlayers.contains('O'),
                isCurrentTurn = gameState.playerAtTurn == 'O'
            )
        }
    }
}

@Composable
fun PlayerCard(
    symbol: Char,
    isConnected: Boolean,
    isCurrentTurn: Boolean
) {
    val scale by animateFloatAsState(if (isCurrentTurn) 1.1f else 1f)

    Card(
        modifier = Modifier
            .scale(scale)
            .padding(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isConnected)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = symbol.toString(),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = if (isConnected) "Connected" else "Waiting...",
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun GameBoard(
    gameState: GameState,
    onTileClick: (x: Int, y: Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(8.dp)
    ) {
        gameState.field.forEachIndexed { y, row ->
            Row(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                row.forEachIndexed { x, value ->
                    GameTile(
                        symbol = value,
                        onClick = { onTileClick(x, y) },
                        enabled = gameState.winningPlayer == null && !gameState.isBoardFull,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .padding(4.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun GameTile(
    symbol: Char?,
    onClick: () -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surface)
            .clickable(enabled = enabled && symbol == null, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = symbol?.toString() ?: "",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun GameStatus(
    gameState: GameState,
    isConnecting: Boolean,
    showConnectionError: Boolean
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AnimatedVisibility(
            visible = isConnecting,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            CircularProgressIndicator()
        }

        AnimatedVisibility(
            visible = showConnectionError,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Text(
                text = "Couldn't connect to the server",
                color = MaterialTheme.colorScheme.error
            )
        }

        when {
            gameState.winningPlayer != null -> {
                Text(
                    text = "Player ${gameState.winningPlayer} won!",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            gameState.isBoardFull -> {
                Text(
                    text = "It's a draw!",
                    style = MaterialTheme.typography.headlineMedium
                )
            }
            gameState.connectedPlayers.size < 2 -> {
                Text(
                    text = "Waiting for players...",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}