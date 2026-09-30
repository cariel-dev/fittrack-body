package com.fittrack.body.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Clave primaria = fecha ISO "yyyy-MM-dd".
 * Un registro por día y tabla (upsert). Campos nulables para ser polivalente:
 * registra solo lo que midas ese día.
 */

// Peso diario en kg
@Entity(tableName = "weight_entries")
data class WeightEntry(
    @PrimaryKey val date: String,
    val weightKg: Float,
    val note: String = "",
    val updatedAt: Long = System.currentTimeMillis()
)

// Perímetros corporales en cm
@Entity(tableName = "circumference_entries")
data class CircumferenceEntry(
    @PrimaryKey val date: String,
    val neck: Float? = null,
    val chest: Float? = null,
    val waist: Float? = null,
    val abdomen: Float? = null,
    val hips: Float? = null,
    val leftArm: Float? = null,
    val rightArm: Float? = null,
    val leftThigh: Float? = null,
    val rightThigh: Float? = null,
    val leftCalf: Float? = null,
    val rightCalf: Float? = null,
    val note: String = "",
    val updatedAt: Long = System.currentTimeMillis()
)

// Plicometría en mm (pliegues cutáneos con plicómetro)
@Entity(tableName = "skinfold_entries")
data class SkinfoldEntry(
    @PrimaryKey val date: String,
    val triceps: Float? = null,
    val biceps: Float? = null,
    val subscapular: Float? = null,
    val midaxillary: Float? = null,
    val chestPec: Float? = null,
    val suprailiac: Float? = null,
    val abdominal: Float? = null,
    val thigh: Float? = null,
    val calf: Float? = null,
    val note: String = "",
    val updatedAt: Long = System.currentTimeMillis()
)
