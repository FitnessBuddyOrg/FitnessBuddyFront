package com.project.fitnessbuddy.screens.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.project.fitnessbuddy.R

@Composable
fun DefaultTextField(label: String, value: String, onValueChange: (String) -> Unit) {
    TextField(
        modifier = Modifier.fillMaxWidth(),
        value = value,
        onValueChange = onValueChange,
        label = {
            Text(text = label, style = MaterialTheme.typography.labelSmall)
        }
    )
}

@Composable
fun DefaultTextArea(label: String, value: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall
            )
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp),
        maxLines = Int.MAX_VALUE,
        singleLine = false
    )
}

fun countryCodeToFlag(countryCode: String): String {
    if (countryCode.length != 2) return ""
    val codePoints = countryCode.uppercase().map {
        it.code + 0x1F1A5
    }
    return String(codePoints.toIntArray(), 0, codePoints.size)
}

@Composable
fun CountryFlagComposable(localeString: String, displayValue: String) {
    Row(
        modifier = Modifier
            .padding(end = 8.dp)
    ) {
        val flagEmoji = countryCodeToFlag(localeString)

        Text(
            text = flagEmoji,
            modifier = Modifier.padding(end = 16.dp),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Text(
            text = displayValue,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
fun <T, V : StoredValue<T>> DialogRadioButtonList(
    label: String,
    options: List<V>,
    initialStoredValue: V,
    onValueChange: (V) -> Unit,
    modifier: Modifier = Modifier,
    valueComposable: @Composable (V) -> Unit = {
        Text(
            text = it.displayValue,
            style = MaterialTheme.typography.labelMedium
        )
    }
) {

    var selectedStoredValue by remember {
        mutableStateOf(
            options.find { it.value == initialStoredValue.value } ?: options.first()
        )
    }

    var isDialogOpen by remember { mutableStateOf(false) }


    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp)
            .background(Color.Transparent)
            .clickable {
                isDialogOpen = true
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .weight(1f),
            text = label,
            style = MaterialTheme.typography.labelMedium,
        )
        valueComposable(selectedStoredValue)
    }

    if (isDialogOpen) {
        Dialog(onDismissRequest = { isDialogOpen = false }) {
            Surface(
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier.padding(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(label, style = MaterialTheme.typography.labelLarge)

                    Spacer(modifier = Modifier.height(16.dp))

                    LazyColumn(
                        contentPadding = PaddingValues(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(options) { option ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        selectedStoredValue = option
                                        onValueChange(option)
                                        isDialogOpen = false
                                    }
                            ) {
                                RadioButton(
                                    selected = selectedStoredValue == option,
                                    onClick = {
                                        selectedStoredValue = option
                                        onValueChange(option)
                                        isDialogOpen = false
                                    }
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                valueComposable(option)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = 8.dp)
                            .clickable {
                                isDialogOpen = false
                            },
                        text = stringResource(R.string.ok),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.secondary,
                        textAlign = TextAlign.End
                    )
                }
            }
        }
    }
}

open class StoredValue<T>(open val value: T, open val displayValue: String)
class StoredLanguageValue<T>(
    override val value: T,
    override val displayValue: String,
    val localeString: String
) : StoredValue<T>(value, displayValue)
