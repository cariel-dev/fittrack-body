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
import com.fittrack.body.data.db.SkinfoldEntry
import com.fittrack.body.data.repo.TrackerRepository
import com.fittrack.body.ui.components.MeasureField
import com.fittrack.body.ui.components.SectionCard
import com.fittrack.body.ui.components.parseFloatOrNull
import com.fittrack.body.ui.viewmodel.TrackerViewModel

@Composable
fun SkinfoldScreen(vm: TrackerViewModel) {
    val history by vm.skinfolds.collectAsState()
    var date by remember { mutableStateOf(TrackerRepository.today()) }
    var note by remember { mutableStateOf("") }
    val fields = remember {
        mutableMapOf<String, String>().apply {
            listOf("Triceps","Biceps","Subescapular","Axilar","Pectoral",
                "Suprailiaco","Abdominal","Muslo","Gemelo"
            ).forEach { this[it] = "" }
        }
    }
    fun f(k: String) = parseFloatOrNull(fields[k] ?: "")

    LazyColumn(Modifier.fillMaxSize().padding(16.dp)) {
        item {
            SectionCard("Plicometría (mm)") {
                OutlinedTextField(
                    value = date, onValueChange = { date = it },
                    label = { Text("Fecha (aaaa-mm-dd)") },
                    singleLine = true, modifier = Modifier.fillMaxWidth()
                )
                MeasureField("Tríceps", fields["Triceps"] ?: "", { fields["Triceps"] = it }, "mm")
                MeasureField("Bíceps", fields["Biceps"] ?: "", { fields["Biceps"] = it }, "mm")
                MeasureField("Subescapular", fields["Subescapular"] ?: "", { fields["Subescapular"] = it }, "mm")
                MeasureField("Axilar medio", fields["Axilar"] ?: "", { fields["Axilar"] = it }, "mm")
                MeasureField("Pectoral", fields["Pectoral"] ?: "", { fields["Pectoral"] = it }, "mm")
                MeasureField("Suprailíaco", fields["Suprailiaco"] ?: "", { fields["Suprailiaco"] = it }, "mm")
                MeasureField("Abdominal", fields["Abdominal"] ?: "", { fields["Abdominal"] = it }, "mm")
                MeasureField("Muslo", fields["Muslo"] ?: "", { fields["Muslo"] = it }, "mm")
                MeasureField("Gemelo", fields["Gemelo"] ?: "", { fields["Gemelo"] = it }, "mm")
                OutlinedTextField(
                    value = note, onValueChange = { note = it },
                    label = { Text("Nota") }, modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    "Suma pliegues: " + listOf("Triceps","Biceps","Subescapular","Axilar","Pectoral","Suprailiaco","Abdominal","Muslo","Gemelo")
                        .mapNotNull { f(it) }.sum().let { if (it > 0) "%.1f mm".format(it) else "-" },
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(Modifier.height(8.dp))
                Button(
                    onClick = {
                        vm.saveSkinfold(
                            SkinfoldEntry(
                                date = date.trim(),
                                triceps = f("Triceps"), biceps = f("Biceps"),
                                subscapular = f("Subescapular"), midaxillary = f("Axilar"),
                                chestPec = f("Pectoral"), suprailiac = f("Suprailiaco"),
                                abdominal = f("Abdominal"), thigh = f("Muslo"),
                                calf = f("Gemelo"), note = note.trim()
                            )
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Guardar pliegues") }
            }
        }
        item { Text("Historial", style = MaterialTheme.typography.titleMedium) }
        items(history) { e ->
            val sum = listOfNotNull(
                e.triceps, e.biceps, e.subscapular, e.midaxillary,
                e.chestPec, e.suprailiac, e.abdominal, e.thigh, e.calf
            ).sum()
            Row(Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                Column(Modifier.weight(1f)) {
                    Text("${e.date} — suma ${"%.1f".format(sum)} mm")
                    Text(
                        "Tri ${e.triceps ?: "-"} · Abd ${e.abdominal ?: "-"} · Muslo ${e.thigh ?: "-"}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                IconButton(onClick = { vm.deleteSkinfold(e.date) }) {
                    Icon(Icons.Default.Delete, contentDescription = "Borrar")
                }
            }
        }
    }
}
