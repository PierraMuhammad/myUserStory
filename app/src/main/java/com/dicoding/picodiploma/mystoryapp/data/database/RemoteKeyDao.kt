package com.dicoding.picodiploma.mystoryapp.data.database

import androidx.room.*

@Dao
interface RemoteKeyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(remoteKey: List<RemoteKey>)

    @Query("SELECT * FROM remote_keys WHERE id = :id")
    suspend fun getRemoteKey(id: String): RemoteKey?

    @Query("DELETE FROM remote_keys")
    suspend fun deleteRemoteKey()
}