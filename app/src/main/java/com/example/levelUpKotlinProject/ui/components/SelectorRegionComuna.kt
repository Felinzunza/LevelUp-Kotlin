package com.example.levelUpKotlinProject.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.levelUpKotlinProject.domain.model.DatosChile

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectorRegionComuna(
    regionSeleccionada: String,
    comunaSeleccionada: String,
    onRegionChange: (String) -> Unit,
    onComunaChange: (String) -> Unit
) {
    var expandedRegion by remember { mutableStateOf(false) }
    var expandedComuna by remember { mutableStateOf(false) }

    // 🔥 MAGIA AQUÍ: Esta lista cambia automáticamente cuando cambia 'regionSeleccionada'
    val comunasDisponibles = DatosChile.regionesYComunas[regionSeleccionada] ?: emptyList()

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // --- 1. DROPDOWN DE REGIÓN ---
        ExposedDropdownMenuBox(
            expanded = expandedRegion,
            onExpandedChange = { expandedRegion = !expandedRegion },
            modifier = Modifier.weight(1f)
        ) {
            OutlinedTextField(
                value = regionSeleccionada,
                onValueChange = {}, // ReadOnly
                readOnly = true,
                label = { Text("Región") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedRegion) },
                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = expandedRegion,
                onDismissRequest = { expandedRegion = false }
            ) {
                DatosChile.listaRegiones.forEach { region ->
                    DropdownMenuItem(
                        text = { Text(region, style = MaterialTheme.typography.bodyMedium) },
                        onClick = {
                            onRegionChange(region) // Notifica al padre
                            expandedRegion = false
                        },
                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                    )
                }
            }
        }

        // --- 2. DROPDOWN DE COMUNA ---
        ExposedDropdownMenuBox(
            expanded = expandedComuna,
            onExpandedChange = { expandedComuna = !expandedComuna },
            modifier = Modifier.weight(1f)
        ) {
            OutlinedTextField(
                value = comunaSeleccionada,
                onValueChange = {},
                readOnly = true,
                label = { Text("Comuna") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedComuna) },
                // 🔒 Deshabilitar si no hay región seleccionada
                enabled = regionSeleccionada.isNotEmpty(),
                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )

            // Solo mostramos el menú si hay región y comunas
            if (regionSeleccionada.isNotEmpty()) {
                ExposedDropdownMenu(
                    expanded = expandedComuna,
                    onDismissRequest = { expandedComuna = false }
                ) {
                    if (comunasDisponibles.isEmpty()) {
                        DropdownMenuItem(
                            text = { Text("Sin comunas disponibles") },
                            onClick = { expandedComuna = false }
                        )
                    } else {
                        // Ordenamos alfabéticamente para que sea más fácil buscar
                        comunasDisponibles.sorted().forEach { comuna ->
                            DropdownMenuItem(
                                text = { Text(comuna, style = MaterialTheme.typography.bodyMedium) },
                                onClick = {
                                    onComunaChange(comuna)
                                    expandedComuna = false
                                },
                                contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                            )
                        }
                    }
                }
            }
        }
    }
}