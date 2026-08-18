package com.uriel.logpose.data.local

import androidx.room.*

@Dao
interface LogPoseDao {

    // --- Driver Preferences ---
    @Query("SELECT value FROM driver_preferences WHERE `key` = :key LIMIT 1")
    suspend fun getPreference(key: String): String?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun savePreference(pref: DriverPreferenceEntity)

    @Query("SELECT * FROM driver_preferences")
    suspend fun getAllPreferences(): List<DriverPreferenceEntity>

    // --- Learning Engine ---
    @Query("SELECT * FROM learned_phonetics")
    suspend fun getAllPhonetics(): List<LearnedPhoneticEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun savePhonetic(entity: LearnedPhoneticEntity)

    @Query("DELETE FROM learned_phonetics WHERE hears = :key")
    suspend fun deletePhonetic(key: String)

    @Query("DELETE FROM learned_phonetics")
    suspend fun clearPhonetics()

    @Query("SELECT * FROM command_maturity")
    suspend fun getAllMaturity(): List<CommandMaturityEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveMaturity(entity: CommandMaturityEntity)

    @Query("DELETE FROM command_maturity WHERE hears = :key")
    suspend fun deleteMaturity(key: String)

    @Query("DELETE FROM command_maturity")
    suspend fun clearMaturity()

    @Query("SELECT * FROM user_corrections")
    suspend fun getAllCorrections(): List<UserCorrectionEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveCorrection(entity: UserCorrectionEntity)

    @Query("SELECT * FROM action_frequency")
    suspend fun getAllFrequencies(): List<ActionFrequencyEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveFrequency(entity: ActionFrequencyEntity)

    @Query("SELECT * FROM learned_entities")
    suspend fun getAllLearnedEntities(): List<LearnedEntityRecord>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveLearnedEntity(entity: LearnedEntityRecord)

    @Query("SELECT * FROM favorite_artists")
    suspend fun getAllFavoriteArtists(): List<FavoriteArtistEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveFavoriteArtist(entity: FavoriteArtistEntity)

    @Query("SELECT * FROM track_to_artist")
    suspend fun getAllTrackArtistRelations(): List<TrackToArtistEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveTrackArtistRelation(entity: TrackToArtistEntity)

    // --- World Model ---
    @Query("SELECT json FROM world_checkpoints WHERE id = :id LIMIT 1")
    suspend fun getCheckpoint(id: String): String?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveCheckpoint(checkpoint: WorldSnapshotCheckpointEntity)
}
