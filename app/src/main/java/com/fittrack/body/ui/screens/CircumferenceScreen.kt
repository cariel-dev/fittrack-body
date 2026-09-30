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
import com.fittrack.body.data.db.CircumferenceEntry
import com.fittrack.body.data.repo.TrackerRepository
import com.fittrack.body.ui.components.MeasureField
import com.fittrack.body.ui.components.SectionCard
import com.fittrack.body.ui.components.parseFloatOrNull
import com.fittrack.body.ui.viewmodel.TrackerViewModel

@Composable
fun CircumferenceScreen(vm: TrackerViewModel) {
    val history by vm.circumferences.collectAsState()
    var date by remember { mutableStateOf(TrackerRepository.today()) }
    var note by remember { mutableStateOf("") }
    val fields = remember {
        mutableMapOf<String, String>().apply {
            listOf("Cuello","Pecho","Cintura","Abdomen","Cadera",
                "BrazoIzq","BrazoDer","MusloIzq","MusloDer","GemeloIzq","GemeloDer"
            ).forEach { this[it] = "" }
        }
    }

    fun f(k: String) = parseFloatOrNull(fields[k] ?: "")

    LazyColumn(Modifier.fillMaxSize().padding(16.dp)) {
        item {
            SectionCard("Perímetros (cm)") {
                OutlinedTextField(
                    value = date, onValueChange = { date = it },
                    label = { Text("Fecha (aaaa-mm-dd)") },
                    singleLine = true, modifier = Modifier.fillMaxWidth()
                )
                MeasureField("Cuello", fields["Cuello"] ?: "", { fields["Cuello"] = it }, "cm")
                MeasureField("Pecho", fields["Pecho"] ?: "", { fields["Pecho"] = it }, "cm")
                MeasureField("Cintura", fields["Cintura"] ?: "", { fields["Cintura"] = it }, "cm")
                MeasureField("Abdomen", fields["Abdomen"] ?: "", { fields["Abdomen"] = it }, "cm")
                MeasureField("Cadera", fields["Cadera"] ?: "", { fields["Cadera"] = it }, "cm")
                MeasureField("Brazo izq", fields["BrazoIzq"] ?: "", { fields["BrazoIzq"] = it }, "cm")
                MeasureField("Brazo der", fields["BrazoDer"] ?: "", { fields["BrazoDer"] = it }, "cm")
                MeasureField("Muslo izq", fields["MusloIzq"] ?: "", { fields["MusloIzq"] = it }, "cm")
                MeasureField("Muslo der", fields["MusloDer"] ?: "", { fields["MusloDer"] = it }, "cm")
                MeasureField("Gemelo izq", fields["GemeloIzq"] ?: "", { fields["GemeloIzq"] = it }, "cm")
                MeasureField("Gemelo der", fields["GemeloDer"] ?: "", { fields["GemeloDer"] = it }, "cm")
                OutlinedTextField(
                    value = note, onValueChange = { note = it },
                    label = { Text("Nota") }, modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                Button(
                    onClick = {
                        vm.saveCircumference(
                            CircumferenceEntry(
                                date = date.trim(),
                                neck = f("Cuello"), chest = f("Pecho"),
                                waist = f("Cintura"), abdomen = f("Abdomen"),
                                hips = f("Cadera"), leftArm = f("BrazoIzq"),
                                rightArm = f("BrazoDer"), leftThigh = f("MusloIzq"),
                                rightThigh = f("MusloDer"), leftCalf = f("GemeloIzq"),
                                rightCalf = f("GemeloDer"), note = note.trim()
                            )
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Guardar medidas") }
            }
        }
        item { Text("Historial", style = MaterialTheme.typography.titleMedium) }
        items(history) { e ->
            Row(Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                Column(Modifier.weight(1f)) {
                    Text(e.date, style = MaterialTheme.typography.titleSmall)
                    Text(
                        "Cint ${e.waist ?: "-"} · Abd ${e.abdomen ?: "-"} · Cad ${e.hips ?: "-"} · Pecho ${e.chest ?: "-"}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                IconButton(onClick = { vm.deleteCircumference(e.date) }) {
                    Icon(Icons.Default.Delete, contentDescription = "Borrar")
                }
            }
        }
    }
}
