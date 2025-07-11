package com.phone.pocket.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import net.sqlcipher.database.SupportFactory

@Database(
    entities = [Card::class, Spend::class],
    version = 2,
    exportSchema = false
)
abstract class PocketDatabase : RoomDatabase() {
    abstract fun cardDao(): CardDao
    abstract fun spendDao(): SpendDao
    
    companion object {
        @Volatile
        private var INSTANCE: PocketDatabase? = null
        
        fun getDatabase(context: Context): PocketDatabase {
            return INSTANCE ?: synchronized(this) {
                val MIGRATION_1_2 = object : androidx.room.migration.Migration(1, 2) {
                    override fun migrate(database: androidx.sqlite.db.SupportSQLiteDatabase) {
                        database.execSQL("ALTER TABLE cards ADD COLUMN network TEXT NOT NULL DEFAULT 'Visa'")
                    }
                }
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PocketDatabase::class.java,
                    "pocket_database"
                )
                .addMigrations(MIGRATION_1_2)
                .openHelperFactory(SupportFactory("your-secret-key-here".toByteArray()))
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
} 