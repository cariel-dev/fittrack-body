package com.fittrack.body

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fittrack.body.ui.screens.CircumferenceScreen
import com.fittrack.body.ui.screens.DashboardScreen
import com.fittrack.body.ui.screens.ExportScreen
import com.fittrack.body.ui.screens.SkinfoldScreen
import com.fittrack.body.ui.screens.WeightScreen
import com.fittrack.body.ui.theme.FitTrackTheme
import com.fittrack.body.ui.viewmodel.TrackerViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val app = application as FitTrackApp
        setContent {
            FitTrackTheme {
                val vm: TrackerViewModel = viewModel(
                    factory = TrackerViewModel.factory(app.repository)
                )
                var tab by remember { mutableIntStateOf(0) }
                val labels = listOf("Peso", "Cm", "Pliegues", "Resumen", "Exportar")
                val icons = listOf(
                    Icons.Default.DateRange, Icons.Default.Person,
                    Icons.Default.Star, Icons.Default.Info, Icons.Default.Share
                )
                Scaffold(
                    bottomBar = {
                        NavigationBar {
                            labels.forEachIndexed { i, label ->
                                NavigationBarItem(
                                    selected = tab == i,
                                    onClick = { tab = i },
                                    icon = { Icon(icons[i], contentDescription = label) },
                                    label = { Text(label) }
                                )
                            }
                        }
                    }
                ) { pad ->
                    androidx.compose.foundation.layout.Box(
                        Modifier.padding(pad)
                    ) {
                        when (tab) {
                            0 -> WeightScreen(vm)
                            1 -> CircumferenceScreen(vm)
                            2 -> SkinfoldScreen(vm)
                            3 -> DashboardScreen(vm)
                            else -> ExportScreen(vm, app.exportManager)
                        }
                    }
                }
            }
        }
    }
}
