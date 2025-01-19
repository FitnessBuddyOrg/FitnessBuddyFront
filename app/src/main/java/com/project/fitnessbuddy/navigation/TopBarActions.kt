package com.project.fitnessbuddy.navigation

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.project.fitnessbuddy.R


@Composable
fun BackButton(
    navController: NavController?,
    onClick: () -> Unit
) {
    IconButton(onClick = onClick) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = null
        )
    }
}

@Composable
fun CloseButton(
    onClick: () -> Unit = {}
) {
    IconButton(onClick = {
        onClick()
    }) {
        Icon(
            imageVector = Icons.Default.Close,
            contentDescription = null
        )
    }
}

@Composable
fun MoreVertButton() {
    IconButton(onClick = {}) {
        Icon(Icons.Default.MoreVert, contentDescription = "More")
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchButton(
    title: String,
    navigationViewModel: NavigationViewModel,
    onValueChange: (String) -> Unit,
    onClear: () -> Unit
) {
    var isSearchEnabled by remember { mutableStateOf(false) }
    var searchText by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }

    navigationViewModel.onEvent(NavigationEvent.UpdateTitleWidget {
        if (isSearchEnabled) {
            BasicTextField(
                value = searchText,
                onValueChange = {
                    onValueChange(it)
                    searchText = it
                },
                singleLine = true,
                textStyle = MaterialTheme.typography.labelMedium.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                cursorBrush = SolidColor(MaterialTheme.colorScheme.tertiary),
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .focusRequester(focusRequester),
                decorationBox = { innerTextField ->
                    TextFieldDefaults.DecorationBox(
                        value = searchText,
                        innerTextField = innerTextField,
                        enabled = true,
                        singleLine = true,
                        visualTransformation = VisualTransformation.None,
                        interactionSource = remember { MutableInteractionSource() },
                        contentPadding = PaddingValues(start = 12.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.onPrimary,
                            unfocusedContainerColor = MaterialTheme.colorScheme.onPrimary,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        placeholder = {
                            Text(
                                text = "${stringResource(R.string.search)}...",
                                style = MaterialTheme.typography.labelMedium
                            )
                        },
                        trailingIcon = {
                            IconButton(onClick = {
                                onClear()
                                searchText = ""
                            }) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete Search")
                            }
                        }
                    )
                }
            )

            LaunchedEffect(isSearchEnabled) {
                if (isSearchEnabled) {
                    focusRequester.requestFocus()
                }
            }
        } else {
            MediumTextWidget(title)
        }
    })

    if (isSearchEnabled) {
        IconButton(onClick = {
            isSearchEnabled = false
        }) {
            Icon(Icons.Default.Close, contentDescription = "Close Search")
        }
    } else {
        IconButton(
            onClick = {
                isSearchEnabled = true
            }
        ) {
            Icon(Icons.Default.Search, contentDescription = "Search")
        }
    }
}

@Composable
fun CreateButton(onClick: () -> Unit) {
    IconButton(onClick = onClick) {
        Icon(Icons.Default.Add, contentDescription = "Create")
    }
}

@Composable
fun DeleteButton(
    onClick: () -> Unit,
    colors: IconButtonColors = IconButtonDefaults.iconButtonColors(),
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    IconButton(
        onClick = onClick,
        colors = colors,
        modifier = modifier,
        enabled = enabled
    ) {
        Icon(Icons.Default.Delete, contentDescription = "Delete")
    }
}

@Composable
fun EditButton(onClick: () -> Unit) {
    IconButton(onClick = onClick) {
        Icon(Icons.Default.Edit, contentDescription = "Edit")
    }
}

@Composable
fun LargeTextWidget(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelLarge,
        modifier = modifier
    )
}

@Composable
fun MediumTextWidget(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelMedium,
        modifier = modifier
    )
}

@Composable
fun SmallTextWidget(
    text: String,
    modifier: Modifier = Modifier,
    textAlign: TextAlign = TextAlign.Start
) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelSmall,
        modifier = modifier,
        textAlign = textAlign
    )
}

