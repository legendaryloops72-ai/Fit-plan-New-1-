package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.*

@Composable
fun LoginScreen(
    onSignInSuccess: () -> Unit,
    onNavigateToSignUp: () -> Unit,
    modifier: Modifier = Modifier
) {
    var email by remember { mutableStateOf("alex.rivers@fitplan.app") }
    var password by remember { mutableStateOf("password123") }
    var passwordVisible by remember { mutableStateOf(false) }
    var rememberMe by remember { mutableStateOf(true) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(FitPlanBlack)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(10.dp))

        // Top Logo
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 24.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_fitplan_dumbbell),
                contentDescription = "FITPLAN Logo",
                tint = FitPlanNeonLime,
                modifier = Modifier.size(width = 44.dp, height = 26.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = stringResource(R.string.app_name),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Black,
                    color = FitPlanTextWhite,
                    letterSpacing = 1.sp
                )
                Text(
                    text = stringResource(R.string.tagline),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = FitPlanNeonLime,
                    fontSize = 8.sp,
                    letterSpacing = 0.8.sp
                )
            }
        }

        // Hero Card on Login Screen
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(FitPlanSurface)
                .border(1.dp, FitPlanCardBorder, RoundedCornerShape(20.dp))
                .padding(20.dp)
        ) {
            Column {
                Box(
                    modifier = Modifier
                        .background(FitPlanNeonLime, RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = stringResource(R.string.good_habits_real_results),
                        style = MaterialTheme.typography.labelSmall,
                        color = FitPlanBlack,
                        fontWeight = FontWeight.Black,
                        fontSize = 9.sp
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = stringResource(R.string.healthier_happier_stronger),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Black,
                    color = FitPlanTextWhite
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = stringResource(R.string.track_train_stay_healthy),
                    style = MaterialTheme.typography.bodySmall,
                    color = FitPlanTextSecondary
                )
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Welcome Text
        Text(
            text = stringResource(R.string.welcome_back),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = FitPlanTextWhite,
            modifier = Modifier.fillMaxWidth()
        )
        Text(
            text = stringResource(R.string.sign_in_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            color = FitPlanTextSecondary,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp, bottom = 20.dp)
        )

        // Email Field
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text(stringResource(R.string.email_hint)) },
            leadingIcon = {
                Icon(Icons.Default.Email, contentDescription = null, tint = FitPlanNeonLime)
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = FitPlanCard,
                unfocusedContainerColor = FitPlanCard,
                focusedBorderColor = FitPlanNeonLime,
                unfocusedBorderColor = FitPlanCardBorder,
                focusedLabelColor = FitPlanNeonLime,
                unfocusedLabelColor = FitPlanTextSecondary,
                focusedTextColor = FitPlanTextWhite,
                unfocusedTextColor = FitPlanTextWhite
            ),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("email_input")
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Password Field
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text(stringResource(R.string.password_hint)) },
            leadingIcon = {
                Icon(Icons.Default.Lock, contentDescription = null, tint = FitPlanNeonLime)
            },
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = "Toggle password visibility",
                        tint = FitPlanTextSecondary
                    )
                }
            },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = FitPlanCard,
                unfocusedContainerColor = FitPlanCard,
                focusedBorderColor = FitPlanNeonLime,
                unfocusedBorderColor = FitPlanCardBorder,
                focusedLabelColor = FitPlanNeonLime,
                unfocusedLabelColor = FitPlanTextSecondary,
                focusedTextColor = FitPlanTextWhite,
                unfocusedTextColor = FitPlanTextWhite
            ),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("password_input")
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Remember me & Forgot Password
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { rememberMe = !rememberMe }
            ) {
                Checkbox(
                    checked = rememberMe,
                    onCheckedChange = { rememberMe = it },
                    colors = CheckboxDefaults.colors(
                        checkedColor = FitPlanNeonLime,
                        checkmarkColor = FitPlanBlack,
                        uncheckedColor = FitPlanTextSecondary
                    )
                )
                Text(
                    text = stringResource(R.string.remember_me),
                    style = MaterialTheme.typography.bodySmall,
                    color = FitPlanTextSecondary
                )
            }

            Text(
                text = stringResource(R.string.forgot_password),
                style = MaterialTheme.typography.bodySmall,
                color = FitPlanNeonLime,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.clickable { /* Handle forgot password */ }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Sign In Button
        Button(
            onClick = onSignInSuccess,
            colors = ButtonDefaults.buttonColors(
                containerColor = FitPlanNeonLime,
                contentColor = FitPlanBlack
            ),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("sign_in_button")
        ) {
            Text(
                text = stringResource(R.string.sign_in),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Black,
                fontSize = 15.sp,
                letterSpacing = 1.sp
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Divider
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            HorizontalDivider(modifier = Modifier.weight(1f), color = FitPlanCardBorder)
            Text(
                text = stringResource(R.string.or_continue_with),
                style = MaterialTheme.typography.labelSmall,
                color = FitPlanTextMuted,
                modifier = Modifier.padding(horizontal = 12.dp)
            )
            HorizontalDivider(modifier = Modifier.weight(1f), color = FitPlanCardBorder)
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Social Buttons
        OutlinedButton(
            onClick = onSignInSuccess,
            shape = RoundedCornerShape(14.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, FitPlanCardBorder),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = FitPlanTextWhite),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Text(
                text = stringResource(R.string.continue_with_google),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedButton(
            onClick = onSignInSuccess,
            shape = RoundedCornerShape(14.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, FitPlanCardBorder),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = FitPlanTextWhite),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Text(
                text = stringResource(R.string.continue_with_apple),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Navigate to Sign Up
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable { onNavigateToSignUp() }
        ) {
            Text(
                text = stringResource(R.string.dont_have_account) + " ",
                style = MaterialTheme.typography.bodyMedium,
                color = FitPlanTextSecondary
            )
            Text(
                text = stringResource(R.string.sign_up),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = FitPlanNeonLime
            )
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
fun SignUpScreen(
    onSignUpSuccess: () -> Unit,
    onNavigateToSignIn: () -> Unit,
    modifier: Modifier = Modifier
) {
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(FitPlanBlack)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(10.dp))

        // Top Logo
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 24.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_fitplan_dumbbell),
                contentDescription = "FITPLAN Logo",
                tint = FitPlanNeonLime,
                modifier = Modifier.size(width = 44.dp, height = 26.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = stringResource(R.string.app_name),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Black,
                    color = FitPlanTextWhite,
                    letterSpacing = 1.sp
                )
                Text(
                    text = stringResource(R.string.tagline),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = FitPlanNeonLime,
                    fontSize = 8.sp,
                    letterSpacing = 0.8.sp
                )
            }
        }

        Text(
            text = stringResource(R.string.auth_create_account_title),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = FitPlanTextWhite,
            modifier = Modifier.fillMaxWidth()
        )
        Text(
            text = stringResource(R.string.auth_create_account_sub),
            style = MaterialTheme.typography.bodyMedium,
            color = FitPlanTextSecondary,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp, bottom = 24.dp)
        )

        OutlinedTextField(
            value = fullName,
            onValueChange = { fullName = it },
            label = { Text(stringResource(R.string.full_name_hint)) },
            leadingIcon = {
                Icon(Icons.Default.Person, contentDescription = null, tint = FitPlanNeonLime)
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = FitPlanCard,
                unfocusedContainerColor = FitPlanCard,
                focusedBorderColor = FitPlanNeonLime,
                unfocusedBorderColor = FitPlanCardBorder,
                focusedTextColor = FitPlanTextWhite,
                unfocusedTextColor = FitPlanTextWhite
            ),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text(stringResource(R.string.email_hint)) },
            leadingIcon = {
                Icon(Icons.Default.Email, contentDescription = null, tint = FitPlanNeonLime)
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = FitPlanCard,
                unfocusedContainerColor = FitPlanCard,
                focusedBorderColor = FitPlanNeonLime,
                unfocusedBorderColor = FitPlanCardBorder,
                focusedTextColor = FitPlanTextWhite,
                unfocusedTextColor = FitPlanTextWhite
            ),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text(stringResource(R.string.password_hint)) },
            leadingIcon = {
                Icon(Icons.Default.Lock, contentDescription = null, tint = FitPlanNeonLime)
            },
            visualTransformation = PasswordVisualTransformation(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = FitPlanCard,
                unfocusedContainerColor = FitPlanCard,
                focusedBorderColor = FitPlanNeonLime,
                unfocusedBorderColor = FitPlanCardBorder,
                focusedTextColor = FitPlanTextWhite,
                unfocusedTextColor = FitPlanTextWhite
            ),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onSignUpSuccess,
            colors = ButtonDefaults.buttonColors(
                containerColor = FitPlanNeonLime,
                contentColor = FitPlanBlack
            ),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
        ) {
            Text(
                text = stringResource(R.string.auth_btn_create_account),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable { onNavigateToSignIn() }
        ) {
            Text(
                text = stringResource(R.string.already_have_account) + " ",
                style = MaterialTheme.typography.bodyMedium,
                color = FitPlanTextSecondary
            )
            Text(
                text = stringResource(R.string.sign_in),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = FitPlanNeonLime
            )
        }
    }
}
