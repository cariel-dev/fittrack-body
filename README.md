# FitTrack Body — App nativa Android

App para registrar:
- **Peso diario (kg)**
- **Medidas corporales en cm** (cuello, pecho, cintura, abdomen, cadera, brazos, muslos, gemelos)
- **Plicometría en mm** (tríceps, bíceps, subescapular, cresta ilíaca, abdominal, muslo, etc.)

Base de datos **robusta y polivalente** con Room + exportación fácil a **CSV y JSON**.

## Abrir el proyecto

1. Instala **Android Studio Ladybug o superior** + **JDK 17**.
2. `File > Open` → selecciona la carpeta `app-dos`.
3. Espera a que Gradle sincronice (primera vez descarga dependencias).
4. `Run > Run 'app'` en un emulador o móvil (Android 8.0+).

## Arquitectura

```
MainActivity (Compose + Navigation)
  └─ TrackerViewModel (StateFlow)
       └─ TrackerRepository
            ├─ WeightDao
            ├─ CircumferenceDao
            └─ SkinfoldDao
                 └─ AppDatabase (Room, v1)
ExportManager → CSV por tabla + JSON backup completo + import JSON
```

### Por qué es robusta y polivalente

- **Room** con 3 tablas independientes, clave primaria = `fecha (yyyy-MM-dd)`, `upsert` por día.
- Índices únicos por fecha, migraciones preparadas (`FALLBACK` no destructivo en v1, añade `Migration` en v2+).
- Campos `Float?` nulables: puedes registrar solo lo que midas ese día.
- Capa `Repository` única: si mañana añades % graso, fotos o sincronización cloud, no tocas la UI.
- Exportación desacoplada en `ExportManager`: CSV para Excel/Sheets, JSON para backup/import total.

## Exportar datos

Pestaña **Exportar**:
- `peso.csv`, `perimetros_cm.csv`, `pliegues_mm.csv` → se comparten por Intent / SAF.
- `backup.json` → copia total, re-importable desde la misma pantalla.

Los CSV usan `;` como separador (compatible Excel ES) y cabecera en primera fila.

## Estructura

```
app/src/main/java/com/fittrack/body/
  MainActivity.kt
  FitTrackApp.kt
  data/db/Entities.kt, Daos.kt, AppDatabase.kt
  data/repo/TrackerRepository.kt
  data/export/ExportManager.kt
  ui/viewmodel/TrackerViewModel.kt
  ui/screens/WeightScreen.kt, CircumferenceScreen.kt, SkinfoldScreen.kt
  ui/screens/DashboardScreen.kt, ExportScreen.kt
  ui/components/Common.kt
  ui/theme/Theme.kt
```

## Futuro fácil

- Añadir cálculo % graso Jackson-Pollock 7 pliegues: solo tocar `ExportManager`/`DashboardScreen`.
- Añadir gráficos avanzados: los `Flow<List<...>>` ya exponen todo el historial ordenado.
- Añadir cloud (Firestore/Supabase): implementar `SyncRepository` sobre los mismos DAO.
