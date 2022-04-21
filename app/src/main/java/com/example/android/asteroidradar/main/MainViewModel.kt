package com.example.android.asteroidradar.main

import android.app.Application
import androidx.lifecycle.*
import com.example.android.asteroidradar.*
import com.example.android.asteroidradar.api.NasaApi
import kotlinx.coroutines.launch
import java.lang.Exception

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val database = getDatabase(application)
    private val videosRepository = AsteroidsRepository(database)

    private val _apod = MutableLiveData<PictureOfDay?>()
    val apod: LiveData<PictureOfDay?>
        get() = _apod

    private val _navigateToSelectedAsteroid = MutableLiveData<Asteroid?>()
    val navigateToSelectedAsteroid: LiveData<Asteroid?>
        get() = _navigateToSelectedAsteroid

    init {
        viewModelScope.launch {
            videosRepository.initializeAsteroidsLivedata()
            getApod()
            videosRepository.refreshAsteroids()
        }
    }

    var asteroids = videosRepository.asteroids

    private fun getApod() {
        viewModelScope.launch {
            try {
                var apodResult = NasaApi.retrofitService.getApod(Constants.API_KEY)
                _apod.value = apodResult
            } catch (e: Exception) {
                _apod.value = null
                e.printStackTrace()
            }
        }
    }

    fun displayAsteroidDetails(asteroid: Asteroid) {
        _navigateToSelectedAsteroid.value = asteroid
    }

    fun displayAsteroidDetailsComplete() {
        _navigateToSelectedAsteroid.value = null
    }

    fun updateFilter(filter: AsteroidFetchFilter) {
        viewModelScope.launch { videosRepository.updateAsteroids(filter) }
    }
}