package com.fittrack.body.data.export

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import com.fittrack.body.data.db.CircumferenceEntry
import com.fittrack.body.data.db.SkinfoldEntry
import com.fittrack.body.data.db.WeightEntry
import com.fittrack.body.data.repo.TrackerRepository
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

data class FullBackup(
    val weights: List<WeightEntry> = emptyList(),
    val circumferences: List<CircumferenceEntry> = emptyList(),
    val skinfolds: List<SkinfoldEntry> = emptyList(),
    val exportedAt: Long = System.currentTimeMillis(),
    val version: Int = 1
)

/**
 * Exportación polivalente:
 * - CSV por tabla (Excel / Google Sheets, separador ; para locale ES)
 * - JSON backup completo (re-importable)
 */
class ExportManager(
    private val context: Context,
    private val repo: TrackerRepository
) {
    private val gson = GsonBuilder().setPrettyPrinting().create()

    // ---------- CSV ----------

    fun weightsToCsv(items: List<WeightEntry>): String {
        val sb = StringBuilder("fecha;peso_kg;nota\n")
        items.sortedBy { it.date }.forEach {
            sb.append("${it.date};${fmt(it.weightKg)};${esc(it.note)}\n")
        }
        return sb.toString()
    }

    fun circumferencesToCsv(items: List<CircumferenceEntry>): String {
        val sb = StringBuilder(
            "fecha;cuello_cm;pecho_cm;cintura_cm;abdomen_cm;cadera_cm;" +
                "brazo_izq_cm;brazo_der_cm;muslo_izq_cm;muslo_der_cm;" +
                "gemelo_izq_cm;gemelo_der_cm;nota\n"
        )
        items.sortedBy { it.date }.forEach {
            sb.append(
                listOf(
                    it.date, fmt(it.neck), fmt(it.chest), fmt(it.waist),
                    fmt(it.abdomen), fmt(it.hips), fmt(it.leftArm),
                    fmt(it.rightArm), fmt(it.leftThigh), fmt(it.rightThigh),
                    fmt(it.leftCalf), fmt(it.rightCalf), esc(it.note)
                ).joinToString(";") + "\n"
            )
        }
        return sb.toString()
    }

    fun skinfoldsToCsv(items: List<SkinfoldEntry>): String {
        val sb = StringBuilder(
            "fecha;triceps_mm;biceps_mm;subescapular_mm;axilar_mm;" +
                "pectoral_mm;suprailiaco_mm;abdominal_mm;muslo_mm;gemelo_mm;nota\n"
        )
        items.sortedBy { it.date }.forEach {
            sb.append(
                listOf(
                    it.date, fmt(it.triceps), fmt(it.biceps), fmt(it.subscapular),
                    fmt(it.midaxillary), fmt(it.chestPec), fmt(it.suprailiac),
                    fmt(it.abdominal), fmt(it.thigh), fmt(it.calf), esc(it.note)
                ).joinToString(";") + "\n"
            )
        }
        return sb.toString()
    }

    // ---------- JSON ----------

    suspend fun buildBackupJson(): String = withContext(Dispatchers.IO) {
        val backup = FullBackup(
            weights = repo.getWeightsOnce(),
            circumferences = repo.getCircumferencesOnce(),
            skinfolds = repo.getSkinfoldsOnce()
        )
        gson.toJson(backup)
    }

    suspend fun importBackupJson(json: String): Int = withContext(Dispatchers.IO) {
        val type = object : TypeToken<FullBackup>() {}.type
        val backup: FullBackup = gson.fromJson(json, type)
        repo.restoreBackup(backup.weights, backup.circumferences, backup.skinfolds)
        backup.weights.size + backup.circumferences.size + backup.skinfolds.size
    }

    // ---------- Compartir ----------

    suspend fun shareTextFile(fileName: String, content: String, mime: String = "text/*") {
        withContext(Dispatchers.IO) {
            val file = File(context.cacheDir, fileName)
            file.writeText(content)
            val uri: Uri = FileProvider.getUriForFile(
                context, "${context.packageName}.fileprovider", file
            )
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = mime
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(
                Intent.createChooser(intent, fileName).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            )
        }
    }

    suspend fun shareAll() {
        val w = repo.getWeightsOnce()
        val c = repo.getCircumferencesOnce()
        val s = repo.getSkinfoldsOnce()
        shareTextFile("peso.csv", weightsToCsv(w), "text/csv")
        shareTextFile("perimetros_cm.csv", circumferencesToCsv(c), "text/csv")
        shareTextFile("pliegues_mm.csv", skinfoldsToCsv(s), "text/csv")
        shareTextFile("backup.json", buildBackupJson(), "application/json")
    }

    private fun fmt(v: Float?): String =
        if (v == null) "" else v.toString().replace('.', ',')

    private fun esc(s: String): String =
        "\"" + s.replace("\"", "\"\"") + "\""
}
