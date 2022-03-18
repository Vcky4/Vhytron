package com.vhytron.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Query("Select * from user")
    fun getAllUser(): LiveData<List<UserEntity>>

    @Query("Select * from user Where userId = :userI")
    fun getUser(userI: String) : LiveData<UserEntity>

    @Update
    suspend fun updateUser(user: UserEntity)

    @Delete
    suspend fun deleteUser(user: UserEntity)
}