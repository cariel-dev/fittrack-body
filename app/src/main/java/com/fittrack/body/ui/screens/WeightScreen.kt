package com.fittrack.body.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.fittrack.body.data.repo.TrackerRepository
import com.fittrack.body.ui.components.LineChart
import com.fittrack.body.ui.components.MeasureField
import com.fittrack.body.ui.components.SectionCard
import com.fittrack.body.ui.components.parseFloatOrNull
import com.fittrack.body.ui.viewmodel.TrackerViewModel

@Composable
fun WeightScreen(vm: TrackerViewModel) {
    val history by vm.weights.collectAsState()
    var date by remember { mutableStateOf(TrackerRepository.today()) }
    var weight by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
        item {
            SectionCard("Registrar peso diario") {
                OutlinedTextField(
                    value = date, onValueChange = { date = it },
                    label = { Text("Fecha (aaaa-mm-dd)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                MeasureField("Peso", weight, { weight = it }, "kg")
                OutlinedTextField(
                    value = note, onValueChange = { note = it },
                    label = { Text("Nota (opcional)") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                Button(
                    onClick = {
                        val kg = parseFloatOrNull(weight) ?: return@Button
                        vm.saveWeight(date.trim(), kg, note.trim())
                        weight = ""
                        note = ""
                    },
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Guardar peso") }
            }
        }
        item {
            SectionCard("Evolución") {
                LineChart(history.reversed().map { it.date to it.weightKg })
            }
        }
        item { Text("Historial", style = MaterialTheme.typography.titleMedium) }
        items(history) { e ->
            Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                Column(Modifier.weight(1f)) {
                    Text("${e.date} — ${e.weightKg} kg")
                    if (e.note.isNotBlank()) Text(e.note, style = MaterialTheme.typography.bodySmall)
                }
                IconButton(onClick = { vm.deleteWeight(e.date) }) {
                    Icon(Icons.Default.Delete, contentDescription = "Borrar")
                }
            }
        }
    }
}
