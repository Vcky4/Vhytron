package com.vhytron.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)

    @Query("Select * from userss")
    fun getAllUser(): LiveData<List<User>>

    @Update
    fun updateUser(user: User)

}