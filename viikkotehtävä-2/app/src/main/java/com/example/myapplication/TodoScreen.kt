package com.example.myapplication

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.material3.CircularProgressIndicator


@Preview
@Composable
fun TodoScreenPreview(viewModel: TodoViewModel = viewModel()) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 10.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text (
            text = "Todo Viewer",
            style = MaterialTheme.typography.headlineLarge
        )

        Text(
            text = "Loaded todos: ${viewModel.todos.size}",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(
            modifier = Modifier.height(16.dp),
        )


        if(viewModel.isLoading) {
            CircularProgressIndicator()
        } else if(viewModel.errorMessage != null) {
            Text(
                text = viewModel.errorMessage ?: "Unknown error",
                color = MaterialTheme.colorScheme.error
            )
        } else {
            TodoList(todos = viewModel.todos)
        }

        Button(
            onClick = { viewModel.loadTodo()}
        ) {
            Text(text = "Load Todo")
        }
    }
}