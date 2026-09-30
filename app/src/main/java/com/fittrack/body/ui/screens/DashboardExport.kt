package com.fittrack.body.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.fittrack.body.data.export.ExportManager
import com.fittrack.body.ui.components.LineChart
import com.fittrack.body.ui.components.SectionCard
import com.fittrack.body.ui.viewmodel.TrackerViewModel
import kotlinx.coroutines.launch

@Composable
fun DashboardScreen(vm: TrackerViewModel) {
    val weights by vm.weights.collectAsState()
    val circs by vm.circumferences.collectAsState()
    val folds by vm.skinfolds.collectAsState()

    Column(
        Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState())
    ) {
        Text("Resumen", style = MaterialTheme.typography.headlineSmall)
        SectionCard("Peso (${weights.size} registros)") {
            val last = weights.firstOrNull()
            Text(if (last != null) "Último: ${last.date} — ${last.weightKg} kg" else "Sin datos")
            weights.firstOrNull()?.let {
                val first = weights.lastOrNull()
                if (first != null && first.date != it.date) {
                    val diff = it.weightKg - first.weightKg
                    Text("Cambio total: ${"%.1f".format(diff)} kg")
                }
            }
            LineChart(weights.reversed().map { w -> w.date to w.weightKg })
        }
        SectionCard("Cintura (cm)") {
            val pts = circs.reversed().mapNotNull { e ->
                e.waist?.let { e.date to it }
            }
            if (pts.isEmpty()) Text("Sin datos") else LineChart(pts)
        }
        SectionCard("Suma pliegues (mm)") {
            val pts = folds.reversed().map { e ->
                val sum = listOfNotNull(
                    e.triceps, e.biceps, e.subscapular, e.midaxillary,
                    e.chestPec, e.suprailiac, e.abdominal, e.thigh, e.calf
                ).sum()
                e.date to sum
            }
            if (pts.isEmpty()) Text("Sin datos") else LineChart(pts)
        }
    }
}

@Composable
fun ExportScreen(vm: TrackerViewModel, exportManager: ExportManager) {
    val scope = rememberCoroutineScope()
    var msg by remember { mutableStateOf("") }
    val weights by vm.weights.collectAsState()
    val circs by vm.circumferences.collectAsState()
    val folds by vm.skinfolds.collectAsState()

    Column(
        Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState())
    ) {
        Text("Exportar / Backup", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(8.dp))
        Text("${weights.size} pesos · ${circs.size} medidas · ${folds.size} pliegues")
        Spacer(Modifier.height(12.dp))

        Button(
            onClick = {
                scope.launch {
                    runCatching {
                        exportManager.shareTextFile(
                            "peso.csv",
                            exportManager.weightsToCsv(weights),
                            "text/csv"
                        )
                    }.onSuccess { msg = "peso.csv compartido" }
                        .onFailure { msg = "Error: ${it.message}" }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) { Text("Exportar peso (CSV)") }

        OutlinedButton(
            onClick = {
                scope.launch {
                    runCatching {
                        exportManager.shareTextFile(
                            "perimetros_cm.csv",
                            exportManager.circumferencesToCsv(circs),
                            "text/csv"
                        )
                    }.onSuccess { msg = "perimetros_cm.csv compartido" }
                        .onFailure { msg = "Error: ${it.message}" }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) { Text("Exportar perímetros (CSV)") }

        OutlinedButton(
            onClick = {
                scope.launch {
                    runCatching {
                        exportManager.shareTextFile(
                            "pliegues_mm.csv",
                            exportManager.skinfoldsToCsv(folds),
                            "text/csv"
                        )
                    }.onSuccess { msg = "pliegues_mm.csv compartido" }
                        .onFailure { msg = "Error: ${it.message}" }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) { Text("Exportar pliegues (CSV)") }

        Button(
            onClick = {
                scope.launch {
                    runCatching {
                        exportManager.shareTextFile(
                            "backup.json",
                            exportManager.buildBackupJson(),
                            "application/json"
                        )
                    }.onSuccess { msg = "backup.json compartido" }
                        .onFailure { msg = "Error: ${it.message}" }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) { Text("Backup total (JSON)") }

        Spacer(Modifier.height(8.dp))
        if (msg.isNotBlank()) Text(msg)
        Spacer(Modifier.height(8.dp))
        Text(
            "Los CSV usan separador ; y se abren directo en Excel / Sheets. " +
                "El JSON sirve para restaurar todo en otro móvil: pega el contenido con la opción importar (próxima versión con selector de archivos).",
            style = MaterialTheme.typography.bodySmall
        )
    }
}
