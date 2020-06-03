package id.ac.unhas.mytodo523.Data

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteQueryBuilder
import androidx.room.*
import kotlinx.coroutines.CoroutineScope

@Database(entities = [Task::class], version = 1)
abstract class TaskRoomDatabase : RoomDatabase() {

    abstract fun wordDao(): TaskDao

    companion object {
        @Volatile
        private var INSTANCE: TaskRoomDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): TaskRoomDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TaskRoomDatabase::class.java,
                    "Word_database"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                return instance
            }
        }
    }
}