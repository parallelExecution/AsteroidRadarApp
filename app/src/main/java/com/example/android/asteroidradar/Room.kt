package com.example.android.asteroidradar

import android.content.Context
import androidx.room.*

enum class AsteroidFetchFilter { SHOW_WEEK, SHOW_TODAY, SHOW_ALL }

@Dao
interface AsteroidDao {

    @Query("select * from asteroidsTable where closeApproachDate between :startDate and :endDate order by closeApproachDate")
    suspend fun getAsteroids(startDate: String, endDate: String): List<Asteroid>

    @Query("select * from asteroidsTable order by closeApproachDate")
    suspend fun getALlAsteroids(): List<Asteroid>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(vararg asteroids: Asteroid)

}

@Database(entities = [Asteroid::class], version = 1)
abstract class AsteroidsDatabase : RoomDatabase() {
    abstract val asteroidDao: AsteroidDao
}

private lateinit var INSTANCE: AsteroidsDatabase

fun getDatabase(context: Context): AsteroidsDatabase {
    synchronized(AsteroidsDatabase::class.java) {
        if (!::INSTANCE.isInitialized) {
            INSTANCE = Room.databaseBuilder(
                context.applicationContext,
                AsteroidsDatabase::class.java,
                "asteroids"
            ).build()
        }
    }
    return INSTANCE
}