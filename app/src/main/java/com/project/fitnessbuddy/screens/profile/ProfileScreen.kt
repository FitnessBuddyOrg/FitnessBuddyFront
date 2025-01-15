package com.project.fitnessbuddy.screens.profile

import android.app.LocaleManager
import android.content.Context
import android.os.Build
import android.os.LocaleList
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.project.fitnessbuddy.R
import com.project.fitnessbuddy.navigation.MediumTextWidget
import com.project.fitnessbuddy.navigation.NavigationEvent
import com.project.fitnessbuddy.navigation.NavigationState
import com.project.fitnessbuddy.navigation.NavigationViewModel
import com.project.fitnessbuddy.screens.common.CountryFlagComposable
import com.project.fitnessbuddy.screens.common.DialogRadioButtonList
import com.project.fitnessbuddy.screens.common.Language
import com.project.fitnessbuddy.screens.common.ParametersEvent
import com.project.fitnessbuddy.screens.common.ParametersState
import com.project.fitnessbuddy.screens.common.ParametersViewModel
import com.project.fitnessbuddy.screens.common.StoredLanguageValue
import com.project.fitnessbuddy.screens.common.countryCodeToFlag
import kotlinx.coroutines.launch

@Composable
fun ProfileScreen(
    navigationState: NavigationState,
    navigationViewModel: NavigationViewModel,
    parametersState: ParametersState,
    parametersViewModel: ParametersViewModel
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val coroutineScope = remember {
        lifecycleOwner.lifecycleScope
    }

    DisposableEffect(Unit) {
        val job = coroutineScope.launch {
            navigationViewModel.onEvent(NavigationEvent.DisableAllButtons)

            navigationViewModel.onEvent(NavigationEvent.SetTitle(context.getString(R.string.profile)))
            navigationViewModel.onEvent(NavigationEvent.UpdateTitleWidget {
                MediumTextWidget(context.getString(R.string.profile))
            })
        }

        onDispose {
            job.cancel()
        }
    }

    ParametersList(
        parametersState = parametersState,
        parametersViewModel = parametersViewModel
    )
}

@Composable
fun ParametersList(
    parametersState: ParametersState,
    parametersViewModel: ParametersViewModel
) {
    val context = LocalContext.current

    val language = Language.getLanguage(parametersState.languageParameter.value)

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = padding,
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            item {
                DialogRadioButtonList(
                    modifier = Modifier.padding(16.dp),
                    label = stringResource(R.string.language),
                    options = Language.entries.map {
                        StoredLanguageValue(
                            it,
                            stringResource(it.resourceId),
                            it.localeString
                        )
                    },
                    initialStoredValue = StoredLanguageValue(
                        language,
                        language.name,
                        language.localeString
                    ),
                    onValueChange = {
                        parametersViewModel.onEvent(ParametersEvent.SetLanguageParameterValue(it.value.name))
                        changeLocales(context, it.localeString)
                    },
                    valueComposable = {
                        CountryFlagComposable(it.localeString, it.displayValue)
                    }
                )
            }

        }
    }
}

fun changeLocales(context: Context, localeString: String) {

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        context.getSystemService(LocaleManager::class.java)
            .applicationLocales = LocaleList.forLanguageTags(localeString)
    } else {
        AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(localeString))
    }
}
