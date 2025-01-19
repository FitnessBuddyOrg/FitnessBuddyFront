package com.project.fitnessbuddy.screens.profile

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.app.LocaleManager
import android.content.Context
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import android.os.LocaleList
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.project.fitnessbuddy.R
import com.project.fitnessbuddy.api.auth.AuthViewModel
import com.project.fitnessbuddy.api.auth.UserState
import com.project.fitnessbuddy.api.user.ProfileViewModel
import com.project.fitnessbuddy.navigation.DefaultTitleWidget
import com.project.fitnessbuddy.auth.AuthViewModel
import com.project.fitnessbuddy.navigation.MediumTextWidget
import com.project.fitnessbuddy.navigation.NavigationEvent
import com.project.fitnessbuddy.navigation.NavigationState
import com.project.fitnessbuddy.navigation.NavigationViewModel
import com.yalantis.ucrop.UCrop
import com.project.fitnessbuddy.screens.common.CountryFlagComposable
import com.project.fitnessbuddy.screens.common.DialogRadioButtonList
import com.project.fitnessbuddy.screens.common.Language
import com.project.fitnessbuddy.screens.common.ParametersEvent
import com.project.fitnessbuddy.screens.common.ParametersState
import com.project.fitnessbuddy.screens.common.ParametersViewModel
import com.project.fitnessbuddy.screens.common.StoredLanguageValue
import com.project.fitnessbuddy.screens.common.countryCodeToFlag
import kotlinx.coroutines.launch
import java.io.File


@Composable
fun ProfileScreen(
    userState: UserState,
    parametersState: ParametersState,
    parametersViewModel: ParametersViewModel,
    navController: NavHostController,
    authViewModel: AuthViewModel,
    profileViewModel: ProfileViewModel = viewModel(),
    navigationViewModel: NavigationViewModel = viewModel()
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val coroutineScope = remember {
        lifecycleOwner.lifecycleScope
    }
    val context = LocalContext.current
    var isEditing by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf("") }
    val user = profileViewModel.user.collectAsState()
    val isLoggedIn = userState.isLoggedIn

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

    // External cache directory
    val cacheDir = context.externalCacheDir ?: throw IllegalStateException("Cache directory not found")
    val croppedFile = File(cacheDir, "cropped_${System.currentTimeMillis()}.jpg")

    // UCrop launcher
    val cropLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // Handle cropped image
            Toast.makeText(context, "Image cropped successfully!", Toast.LENGTH_SHORT).show()
            profileViewModel.updateProfilePicture(croppedFile)
        } else {
            Toast.makeText(context, "Image cropping canceled.", Toast.LENGTH_SHORT).show()
        }
    }

    val imagePickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            val cropUri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", croppedFile)

            // Launch UCrop with an explicit intent
            val uCropIntent = UCrop.of(uri, cropUri)
                .withAspectRatio(1f, 1f)
                .withMaxResultSize(512, 512)
                .getIntent(context)
            cropLauncher.launch(uCropIntent)
        } else {
            Toast.makeText(context, "No image selected.", Toast.LENGTH_SHORT).show()
        }
    }


    // Permission Request Launcher
    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            imagePickerLauncher.launch("image/*")
        } else {
            Toast.makeText(context, "Storage permission denied.", Toast.LENGTH_SHORT).show()
        }
    }

    // Handle Image Picker Click
    val handleImagePickerClick: () -> Unit = {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }

        if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED) {
            imagePickerLauncher.launch("image/*")
        } else {
            permissionLauncher.launch(permission)
        }
    }











    LaunchedEffect(isLoggedIn) {
        if (isLoggedIn) {
            profileViewModel.fetchUser()
            name = user.value?.name ?: ""
        }
    }

    LaunchedEffect(Unit) {
        navigationViewModel.onEvent(NavigationEvent.SetTitle("Profile"))
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
                    .clickable {handleImagePickerClick() },
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = rememberAsyncImagePainter(profileViewModel.profilePictureUrl.collectAsState().value ?: ""),
                    contentDescription = "Profile Picture",
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                Text(
                    text = "Edit",
                    color = Color.White,
                    modifier = Modifier.align(Alignment.BottomCenter)
                )
            }

            Column(
                modifier = Modifier
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "User Profile",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.primary
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Email:",
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = user.value?.email ?: stringResource(id = R.string.no_data),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Name:",
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                        modifier = Modifier.weight(1f)
                    )
                    if (isEditing) {
                        BasicTextField(
                            value = name,
                            onValueChange = { name = it },
                            textStyle = TextStyle(
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onBackground
                            ),
                            modifier = Modifier
                                .background(Color.LightGray, MaterialTheme.shapes.small)
                                .padding(8.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        IconButton(onClick = {
                            isEditing = false
                            user.value?.let {
                                profileViewModel.updateUser(it.id, name)
                            }
                        }) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Save Changes",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    } else {
                        Text(
                            text = user.value?.name ?: stringResource(id = R.string.no_data),
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        IconButton(onClick = { isEditing = true }) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit Name",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        IconButtonWithText(
            text = stringResource(id = R.string.logout),
            icon = Icons.AutoMirrored.Filled.ExitToApp,
            onClick = {
                authViewModel.logout()
                profileViewModel.clearUserData()
                navController.navigate("login") {
                    popUpTo(0) { inclusive = true }
                }
            }
        )
        Spacer(modifier = Modifier.height(24.dp))

        ParametersList(
            parametersState = parametersState,
            parametersViewModel = parametersViewModel
        )
    }
}



@Composable
fun IconButtonWithText(text: String, icon: ImageVector, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary),
        modifier = Modifier.padding(8.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Icon(
            imageVector = icon,
            contentDescription = text,
            tint = Color.White,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = text, color = Color.White)
    }
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

private fun launchImagePicker(context: android.content.Context, croppedFile: File) {
    val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
        type = "image/*"
    }
    ContextCompat.startActivity(context, intent, null)
}

