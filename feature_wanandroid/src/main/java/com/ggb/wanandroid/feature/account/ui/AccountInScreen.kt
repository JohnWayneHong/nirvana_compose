package com.ggb.wanandroid.feature.account.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.core.view.WindowCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.blankj.utilcode.util.ToastUtils
import com.ggb.wanandroid.R
import com.ggb.wanandroid.data.AccountBody
import com.ggb.wanandroid.feature.account.AccountViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountInScreen(
    modifier: Modifier = Modifier.fillMaxSize(),
    viewModel: AccountViewModel = viewModel(),
    navController: NavController
) {
    val signIn by viewModel.signInState.collectAsState()
    val register by viewModel.registerState.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    var isLoginMode by remember { mutableStateOf(true) }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var repassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var repasswordVisible by remember { mutableStateOf(false) }

    LaunchedEffect(signIn, register) {
        if (signIn || register) {
            navController.popBackStack()
        }
    }

    val view = LocalView.current
    val primaryColor = MaterialTheme.colorScheme.primary
    DisposableEffect(Unit) {
        val window = (view.context as? android.app.Activity)?.window
        window?.let {
            @Suppress("DEPRECATION")
            it.statusBarColor = primaryColor.toArgb()
            WindowCompat.getInsetsController(it, view).apply {
                isAppearanceLightStatusBars = false
            }
        }
        onDispose {
            val w = (view.context as? android.app.Activity)?.window
            w?.let {
                @Suppress("DEPRECATION")
                it.statusBarColor = primaryColor.toArgb()
                WindowCompat.getInsetsController(it, view).apply {
                    isAppearanceLightStatusBars = false
                }
            }
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primary
                    ),
                verticalAlignment = Alignment.CenterVertically
            ){
                Spacer(
                    Modifier.size(12.dp)
                )

                Image(
                    modifier = Modifier
                        .width(16.dp)
                        .height(24.dp)
                        .clickable {
                            navController.popBackStack()
                        },
                    painter = painterResource(R.drawable.icon_back_white),
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimary),
                    contentDescription = stringResource(R.string.settings_back_button)
                )

                Spacer(
                    Modifier.size(12.dp)
                )

                Text(
                    text = if (isLoginMode) stringResource(R.string.account_login) else stringResource(R.string.account_register),
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 24.dp)
                .padding(top = 24.dp),
            horizontalAlignment = androidx.compose.ui.Alignment.Start,
        ) {
            Text(
                text = if (isLoginMode) stringResource(R.string.account_user_login) else stringResource(R.string.account_user_register),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.account_please_login_wanandroid),
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
            )
            Spacer(Modifier.height(16.dp))

            AccountTextField(
                value = username,
                onValueChange = { username = it },
                label = stringResource(R.string.account_username),
                placeholder = stringResource(R.string.account_username_hint)
            )

            Spacer(Modifier.height(12.dp))

            AccountTextField(
                value = password,
                onValueChange = { password = it },
                label = stringResource(R.string.account_password),
                placeholder = stringResource(R.string.account_password_hint),
                isPassword = true,
                passwordVisible = passwordVisible,
                onPasswordVisibilityToggle = { passwordVisible = !passwordVisible }
            )

            if (!isLoginMode) {
                Spacer(Modifier.height(12.dp))
                AccountTextField(
                    value = repassword,
                    onValueChange = { repassword = it },
                    label = stringResource(R.string.account_reenter_password),
                    placeholder = stringResource(R.string.account_reenter_password_hint),
                    isPassword = true,
                    passwordVisible = repasswordVisible,
                    onPasswordVisibilityToggle = { repasswordVisible = !repasswordVisible }
                )
            }

            val userToast = stringResource(R.string.account_username_empty)
            val pwdToast = stringResource(R.string.account_password_empty)
            val passwordNotMatchToast = stringResource(R.string.account_password_not_match)

            Spacer(Modifier.height(28.dp))

            Button(
                onClick = {
                    if (username.isEmpty()) {
                        ToastUtils.showShort(userToast)
                        return@Button
                    }
                    if (password.isEmpty()) {
                        ToastUtils.showShort(pwdToast)
                        return@Button
                    }
                    if (isLoginMode) {
                        viewModel.signIn(AccountBody(username = username, password = password))
                    } else {
                        if (password != repassword) {
                            ToastUtils.showShort(passwordNotMatchToast)
                            return@Button
                        }
                        viewModel.register(AccountBody(username = username, password = password, repassword = repassword))
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(36.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                shape = MaterialTheme.shapes.medium,
                contentPadding = ButtonDefaults.ContentPadding
            ) {
                Text(
                    text = if (isLoginMode) stringResource(R.string.account_login) else stringResource(R.string.account_register),
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontSize = 12.sp
                )
            }

            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = if (isLoginMode) stringResource(R.string.account_no_account_create_one) else stringResource(R.string.account_already_have_account),
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.clickable {
                        isLoginMode = !isLoginMode
                        if (isLoginMode) repassword = ""
                    }
                )
            }
        }
        if (isLoading){
            AlertDialog(
                onDismissRequest = { /* 不允许点击外部关闭 */ },
                title = null,
                text = {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp, horizontal = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // 菊花 loading（CircularProgressIndicator）
                        CircularProgressIndicator(
                            modifier = Modifier.size(48.dp),
                            color = androidx.compose.ui.res.colorResource(id = R.color.nav_selected),
                            strokeWidth = 4.dp
                        )
                    }
                },
                confirmButton = {},
                dismissButton = {},
                properties = DialogProperties(
                    dismissOnBackPress = false,
                    dismissOnClickOutside = false
                )
            )
        }
    }
}

@Composable
private fun AccountTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    modifier: Modifier = Modifier,
    isPassword: Boolean = false,
    passwordVisible: Boolean = false,
    onPasswordVisibilityToggle: (() -> Unit)? = null
) {
    val colors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = MaterialTheme.colorScheme.primary,
        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
        focusedLabelColor = MaterialTheme.colorScheme.primary,
        unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
        cursorColor = MaterialTheme.colorScheme.primary,
        focusedTextColor = MaterialTheme.colorScheme.primary,
        unfocusedTextColor = MaterialTheme.colorScheme.primary,
        focusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
        unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant
    )
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, fontSize = 12.sp) },
        placeholder = { Text(placeholder, fontSize = 10.sp) },
        textStyle = TextStyle(fontSize = 12.sp),
        singleLine = true,
        modifier = modifier.fillMaxWidth().height(56.dp),
        visualTransformation = if (isPassword && !passwordVisible) PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions = if (isPassword) KeyboardOptions(keyboardType = KeyboardType.Password) else KeyboardOptions.Default,
        trailingIcon = if (isPassword && onPasswordVisibilityToggle != null) {
            {
                IconButton(onClick = onPasswordVisibilityToggle) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = if (passwordVisible) stringResource(R.string.account_hide_password) else stringResource(R.string.account_show_password),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else null,
        colors = colors
    )
}
