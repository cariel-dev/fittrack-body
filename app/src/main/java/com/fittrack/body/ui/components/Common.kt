package com.fittrack.body.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
fun MeasureField(
    label: String,
    value: String,
    onChange: (String) -> Unit,
    unit: String,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        label = { Text("$label ($unit)") },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    )
}

/** Convierte "72,5" o "72.5" a Float. Vacío -> null. */
fun parseFloatOrNull(s: String): Float? {
    val t = s.trim().replace(',', '.')
    if (t.isEmpty()) return null
    return t.toFloatOrNull()
}

fun floatOrEmpty(v: Float?): String = v?.toString() ?: ""

@Composable
fun SectionCard(title: String, content: @Composable () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            content()
        }
    }
}

/** Mini-gráfico de línea sin dependencias externas. */
@Composable
fun LineChart(values: List<Pair<String, Float>>, modifier: Modifier = Modifier) {
    if (values.size < 2) {
        Text("Registra al menos 2 días para ver la evolución.",
            style = MaterialTheme.typography.bodySmall)
        return
    }
    val pts = values.takeLast(30)
    val min = pts.minOf { it.second }
    val max = pts.maxOf { it.second }
    val range = (max - min).takeIf { it > 0f } ?: 1f
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(160.dp)
            .padding(8.dp)
    ) {
        val w = size.width
        val h = size.height
        pts.forEachIndexed { i, (_, v) ->
            val x = w * i / (pts.size - 1)
            val y = h - h * (v - min) / range
            if (i > 0) {
                val (_, pv) = pts[i - 1]
                val px = w * (i - 1) / (pts.size - 1)
                val py = h - h * (pv - min) / range
                drawLine(
                    color = androidx.compose.ui.graphics.Color(0xFF2E7D32),
                    start = Offset(px, py),
                    end = Offset(x, y),
                    strokeWidth = 6f
                )
            }
            drawCircle(
                color = androidx.compose.ui.graphics.Color(0xFF2E7D32),
                radius = 8f,
                center = Offset(x, y)
            )
        }
    }
    Text(
        "Min ${"%.1f".format(min)} · Max ${"%.1f".format(max)} · Último ${"%.1f".format(pts.last().second)}",
        style = MaterialTheme.typography.bodySmall
    )
}
