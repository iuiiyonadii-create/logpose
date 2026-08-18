package com.uriel.logpose.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "driver_preferences")
data class DriverPreferenceEntity(
    @PrimaryKey val key: String,
    val value: String
)

@Entity(tableName = "learned_phonetics")
data class LearnedPhoneticEntity(
    @PrimaryKey val hears: String,
    val actual: String
)

@Entity(tableName = "command_maturity")
data class CommandMaturityEntity(
    @PrimaryKey val hears: String,
    val level: Int
)

@Entity(tableName = "user_corrections")
data class UserCorrectionEntity(
    @PrimaryKey val spokenText: String,
    val intentName: String
)

@Entity(tableName = "action_frequency")
data class ActionFrequencyEntity(
    @PrimaryKey val intentName: String,
    val count: Int
)

@Entity(tableName = "learned_entities")
data class LearnedEntityRecord(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val type: String,
    val value: String
)

@Entity(tableName = "track_to_artist")
data class TrackToArtistEntity(
    @PrimaryKey val track: String,
    val artist: String
)

@Entity(tableName = "world_checkpoints")
data class WorldSnapshotCheckpointEntity(
    @PrimaryKey val id: String,
    val json: String
)

@Entity(tableName = "favorite_artists")
data class FavoriteArtistEntity(
    @PrimaryKey val name: String
)
