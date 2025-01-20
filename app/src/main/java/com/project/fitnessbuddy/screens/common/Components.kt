package com.project.fitnessbuddy.screens.common

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.project.fitnessbuddy.R
import com.project.fitnessbuddy.database.entity.Exercise
import com.project.fitnessbuddy.database.entity.abstracts.ListedEntity
import com.project.fitnessbuddy.navigation.MediumTextWidget
import com.project.fitnessbuddy.screens.exercises.SortType
import androidx.compose.ui.text.TextStyle

@Composable
fun DefaultTextField(label: String, value: String, onValueChange: (String) -> Unit, modifier: Modifier = Modifier.fillMaxWidth()) {
    TextField(
        modifier = modifier,
        value = value,
        onValueChange = onValueChange,
        label = {
            Text(text = label, style = MaterialTheme.typography.labelSmall)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    trailingIcon: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    textStyle: TextStyle = TextStyle.Default,
    insidePadding: PaddingValues,
    enabled: Boolean = true,
) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        textStyle = textStyle,
        keyboardOptions = keyboardOptions,
        cursorBrush = SolidColor(MaterialTheme.colorScheme.tertiary),
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight(),
        decorationBox = { innerTextField ->
            TextFieldDefaults.DecorationBox(
                value = value,
                innerTextField = innerTextField,
                enabled = true,
                singleLine = true,
                visualTransformation = VisualTransformation.None,
                interactionSource = remember { MutableInteractionSource() },
                contentPadding = insidePadding,
                shape = RoundedCornerShape(8.dp),
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                placeholder = placeholder,
                trailingIcon = trailingIcon
            )
        },
        enabled = enabled,

    )
}

@Composable
fun CustomIntegerField(
    value: String,
    onValueChange: (Int?) -> Unit,
    trailingIcon: @Composable (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = TextStyle.Default,
    insidePadding: PaddingValues,
    enabled: Boolean = true
) {
    CustomTextField(
        value = value,
        onValueChange = { newValue ->
            if(newValue.isNotEmpty() && newValue.toIntOrNull() != null) {
                onValueChange(newValue.toInt())
            } else {
                onValueChange(null)
            }
        },
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = KeyboardType.Number
        ),
        trailingIcon = trailingIcon,
        modifier = modifier,
        textStyle = textStyle,
        insidePadding = insidePadding,
        enabled = enabled
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

@Composable
fun SleekButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Button(
        modifier = modifier
            .fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.tertiary
        ),
        shape = RectangleShape,
        onClick = onClick
    ) {
        MediumTextWidget(
            text = text.uppercase()
        )
    }
}

@Composable
fun SleekErrorButton(text: String, onClick: () -> Unit) {
    Button(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.errorContainer
        ),
        shape = RectangleShape,
        onClick = onClick
    ) {
        MediumTextWidget(
            text = text.uppercase()
        )
    }
}

@Composable
fun BetterButton(text: String, onClick: () -> Unit) {
    Button(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.tertiary,
            contentColor = MaterialTheme.colorScheme.onTertiary
        ),
        shape = RoundedCornerShape(8.dp),
        onClick = onClick
    ) {
        MediumTextWidget(
            text = text.uppercase()
        )
    }
}

@Composable
fun ValidationFloatingActionButton(
    context: Context,
    onClick: () -> Boolean = ({ false }),
    onSuccess: () -> Unit = {},
    successMessage: String = "",
    onFailure: () -> Unit = {},
    failureMessage: String = "",
    toasting: Boolean = true,
) {
    fun successToast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    fun failureToast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    IconButton(
        modifier = Modifier
            .size(60.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primary),
        onClick = {
            val succeeded = onClick()
            if (succeeded && toasting) {
                onSuccess()
                successToast(context, successMessage)
            } else if (toasting) {
                onFailure()
                failureToast(context, failureMessage)
            }
        },
    ) {
        Icon(
            modifier = Modifier.size(30.dp),
            imageVector = Icons.Default.Check,
            contentDescription = "Save",
            tint = MaterialTheme.colorScheme.onPrimary
        )
    }
}


@Composable
fun CountryFlagComposable(localeString: String, displayValue: String) {
    fun countryCodeToFlag(countryCode: String): String {
        if (countryCode.length != 2) return ""
        val codePoints = countryCode.uppercase().map {
            it.code + 0x1F1A5
        }
        return String(codePoints.toIntArray(), 0, codePoints.size)
    }

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
                                    selected = selectedStoredValue.displayValue == option.displayValue,
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

@Composable
fun <T : ListedEntity> GroupedWidgetList(
    itemsList: List<T>,
    widget: @Composable (T) -> Unit,

    header : @Composable () -> Unit = {},
    floatingActionButton: @Composable () -> Unit = {},
    verticalArrangement: Arrangement.Vertical = Arrangement.spacedBy(0.dp),

    keySelector: (T) -> String = { it.name.first().uppercase() },
    predicate: (T) -> Boolean = { true }
) {
    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        floatingActionButton = floatingActionButton
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = padding,
            verticalArrangement = verticalArrangement
        ) {
            item {
                header()
            }

            itemsList
                .filter (predicate)
                .groupBy (keySelector)
                .toSortedMap()
                .forEach { (header, items) ->
                    item {
                        Text(
                            text = header.toString(),
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 16.dp, top = 12.dp, bottom = 12.dp)
                        )
                    }
                    items(items) { item ->
                        widget(item)
                    }
                }
        }
    }
}

@Composable
fun SelectedExerciseWidget(
    exercise: Exercise,
    onClick: (Exercise, Boolean) -> Unit,
    initialSelected: Boolean = false,
    selectionEnabled: Boolean = false,
    titleText : String = exercise.name
) {
    val colorScheme = MaterialTheme.colorScheme
    var selected by remember { mutableStateOf(initialSelected) }
    fun setBackgroundColor(): Color {
        return if (selected && selectionEnabled) {
            colorScheme.secondaryContainer
        } else {
            Color.Transparent
        }
    }
    var backgroundColor by remember { mutableStateOf(setBackgroundColor()) }


    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .background(backgroundColor)
            .clickable(
                onClick = {
                    selected = !selected
                    backgroundColor = setBackgroundColor()
                    onClick(exercise, selected)
                }
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        WidgetLetterImage(
            letter = exercise.name.first(),
            padding = PaddingValues(start = 16.dp)
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp)
        ) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = titleText,
                style = MaterialTheme.typography.labelMedium
            )
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(exercise.category.resourceId),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
