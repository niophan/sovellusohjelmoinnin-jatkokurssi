package com.example.myapplication

import retrofit2.http.GET
import retrofit2.http.Path

interface TodoApiService {

    @GET("todos")
    suspend fun getTodos(): List<Todo>

    // Lisätään yksittäisen postin haku numerolla
    @GET("todos/{id}")
    suspend fun getTodoById(@Path("id") id: Int): Todo

    @GET("todos/1")
    suspend fun getFirstTodo(): Todo
}