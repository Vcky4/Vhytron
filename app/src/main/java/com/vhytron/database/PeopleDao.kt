package com.vhytron.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.vhytron.ui.chats.PeopleModel

@Dao
interface PeopleDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPeople(people: PeopleModel)

    @Query("Select * from people ")
    fun getAllPeople(): LiveData<List<PeopleModel>>

    @Update
    suspend fun updatePeople(people: PeopleModel)

    @Delete
    suspend fun deletePeople(people: PeopleModel)
}