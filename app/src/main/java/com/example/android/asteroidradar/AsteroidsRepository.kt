package com.example.android.asteroidradar

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.android.asteroidradar.api.NasaApi
import com.example.android.asteroidradar.api.parseAsteroidsJsonResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class AsteroidsRepository(
    private val database: AsteroidsDatabase,
) {

    private val startTime = System.currentTimeMillis()
    private val startDate = SimpleDateFormat(
        Constants.API_QUERY_DATE_FORMAT,
        Locale.getDefault()
    ).format(startTime)

    private val endTime =
        System.currentTimeMillis().plus(TimeUnit.MILLISECONDS.convert(7, TimeUnit.DAYS))
    private var endDate: String = SimpleDateFormat(
        Constants.API_QUERY_DATE_FORMAT,
        Locale.getDefault()
    ).format(endTime)

    private val _asteroids = MutableLiveData<List<Asteroid>>()
    val asteroids: LiveData<List<Asteroid>>
        get() = _asteroids

    suspend fun initializeAsteroidsLivedata() {
        _asteroids.value = database.asteroidDao.getAsteroids(startDate, endDate)
    }

    suspend fun updateAsteroids(filter: AsteroidFetchFilter) {
        withContext(Dispatchers.Main) {
            when (filter) {
                AsteroidFetchFilter.SHOW_WEEK -> {
                    try {
                        _asteroids.value = database.asteroidDao.getAsteroids(startDate, endDate)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                AsteroidFetchFilter.SHOW_TODAY -> {
                    try {
                        _asteroids.value = database.asteroidDao.getAsteroids(startDate, startDate)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                AsteroidFetchFilter.SHOW_ALL -> {
                    try {
                        _asteroids.value = database.asteroidDao.getALlAsteroids()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    suspend fun refreshAsteroids() {
            try {
                val stringResult =
                    NasaApi.retrofitService.getAsteroids(startDate, Constants.API_KEY)
                val list = parseAsteroidsJsonResult(JSONObject(stringResult))
                database.asteroidDao.insertAll(*list.toTypedArray())
                initializeAsteroidsLivedata()
            } catch (e: Exception) {
                e.printStackTrace()
            }
    }
}