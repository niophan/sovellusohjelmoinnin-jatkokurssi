package com.example.myapplication

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

@Composable
fun TodoList(
    todos: List<Todo>
) {
    LazyColumn (verticalArrangement = Arrangement.spacedBy(8.dp)){
        items(
            items = todos.filter { it.completed }.take(20),
            key = { todo -> todo.id }
        ) { todo ->
            TodoCard(todo = todo)
        }
    }

}
