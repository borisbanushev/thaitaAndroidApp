package app.thaita.ui.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import app.thaita.ui.theme.GradientEnd
import app.thaita.ui.theme.GradientStart
import app.thaita.ui.theme.LocalThaitaColors

@Composable
fun AuthScreen(
    viewModel: AuthViewModel,
    onAuthSuccess: () -> Unit,
) {
    val state by viewModel.uiState.collectAsState()
    val isAuthenticated by viewModel.isAuthenticated.collectAsState()
    val thaitaColors = LocalThaitaColors.current
    val focusManager = LocalFocusManager.current

    LaunchedEffect(isAuthenticated) {
        if (isAuthenticated) onAuthSuccess()
    }

    var showPassword by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Spacer(modifier = Modifier.height(48.dp))

        // Brand gradient text
        Text(
            text = "thaita",
            style = MaterialTheme.typography.displayLarge.copy(
                fontWeight = FontWeight.Bold,
                brush = Brush.linearGradient(listOf(GradientStart, GradientEnd)),
            ),
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = if (state.isLogin) "Sign in to your account" else "Create your account",
            style = MaterialTheme.typography.bodyLarge,
            color = thaitaColors.textSecondary,
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Error message
        AnimatedVisibility(
            visible = state.error != null,
            enter = fadeIn(),
            exit = fadeOut(),
        ) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = thaitaColors.red.copy(alpha = 0.1f),
                ),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text = state.error ?: "",
                    color = thaitaColors.red,
                    modifier = Modifier.padding(12.dp),
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Success message
        AnimatedVisibility(
            visible = state.successMessage != null,
            enter = fadeIn(),
            exit = fadeOut(),
        ) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = thaitaColors.green.copy(alpha = 0.1f),
                ),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text = state.successMessage ?: "",
                    color = thaitaColors.green,
                    modifier = Modifier.padding(12.dp),
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Form card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp)),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface,
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                // Email field
                OutlinedTextField(
                    value = state.email,
                    onValueChange = viewModel::updateEmail,
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = if (state.isLogin) ImeAction.Next else ImeAction.Next,
                    ),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                )

                // Username field (register only)
                AnimatedVisibility(visible = !state.isLogin) {
                    OutlinedTextField(
                        value = state.username,
                        onValueChange = viewModel::updateUsername,
                        label = { Text("Username") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                    )
                }

                // Password field
                OutlinedTextField(
                    value = state.password,
                    onValueChange = viewModel::updatePassword,
                    label = { Text("Password") },
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = if (showPassword) {
                        VisualTransformation.None
                    } else {
                        PasswordVisualTransformation()
                    },
                    trailingIcon = {
                        IconButton(onClick = { showPassword = !showPassword }) {
                            Icon(
                                imageVector = if (showPassword) {
                                    Icons.Default.VisibilityOff
                                } else {
                                    Icons.Default.Visibility
                                },
                                contentDescription = "Toggle password visibility",
                            )
                        }
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done,
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            focusManager.clearFocus()
                            viewModel.submit()
                        },
                    ),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                )

                // Terms checkbox (register only)
                AnimatedVisibility(visible = !state.isLogin) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Checkbox(
                            checked = state.termsAccepted,
                            onCheckedChange = viewModel::updateTermsAccepted,
                        )
                        Text(
                            text = "I accept the Terms & Conditions",
                            style = MaterialTheme.typography.bodySmall,
                            color = thaitaColors.textSecondary,
                        )
                    }
                }

                // Submit button
                Button(
                    onClick = {
                        focusManager.clearFocus()
                        viewModel.submit()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    enabled = !state.isLoading,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = thaitaColors.brandRed,
                    ),
                ) {
                    if (state.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp,
                        )
                    } else {
                        Text(
                            text = if (state.isLogin) "Sign In" else "Create Account",
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Toggle login / register
        TextButton(onClick = viewModel::toggleLoginRegister) {
            Text(
                text = if (state.isLogin) {
                    "Don't have an account? Sign up"
                } else {
                    "Already have an account? Sign in"
                },
                color = thaitaColors.textSecondary,
                textAlign = TextAlign.Center,
            )
        }

        Spacer(modifier = Modifier.height(48.dp))
    }
}
