package com.example.levelUpKotlinProject.data.remote

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Cliente Retrofit configurado como Singleton
 */
object RetrofitClient {

    // URL base de la API
    private const val URL_BASE = "http://192.168.2.114:3000/" //cambiar ip del pc, ir a terminal y poner ipconfig de direccion ipv4, copiar ip y pegar y poner junto :3000/

    // Interceptor para ver peticiones en Logcat
    private val interceptorLog = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    // Cliente HTTP con timeouts
    private val clienteHttp = OkHttpClient.Builder()
        .addInterceptor(interceptorLog)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    // Instancia de Retrofit (se crea solo una vez)
    val instancia: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(URL_BASE)
            .client(clienteHttp)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // Funcion para crear servicios
    fun <T> crearServicio(servicioClase: Class<T>): T {
        return instancia.create(servicioClase)
    }
}