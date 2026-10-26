
package com.example.healthmate.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [HealthRecord::class],
    version = 1,
    exportSchema = false
)
abstract class HealthMateDatabase : RoomDatabase() {

    abstract fun healthRecordDao(): HealthRecordDao

    companion object {

        @Volatile
        private var INSTANCE: HealthMateDatabase? = null

        fun getDatabase(context: Context): HealthMateDatabase {
            return INSTANCE ?: synchronized(this) {

                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    HealthMateDatabase::class.java,
                    "healthmate_database"
                ).build()

                INSTANCE = instance
                instance
            }
        }
    }
}
