package app.thaita.ui.shared

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import app.thaita.domain.model.CompanyRecord
import app.thaita.ui.theme.LocalThaitaColors

@Composable
fun TickerAutocomplete(
    value: String,
    onValueChange: (String) -> Unit,
    onTickerSubmit: (String) -> Unit,
    companies: List<CompanyRecord>,
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
) {
    val thaitaColors = LocalThaitaColors.current
    val focusManager = LocalFocusManager.current
    var showDropdown by remember { mutableStateOf(false) }

    val filtered = remember(value, companies) {
        if (value.length < 1) {
            emptyList()
        } else {
            val upper = value.uppercase()
            companies.filter {
                it.ticker.uppercase().startsWith(upper) ||
                    it.company.uppercase().contains(upper)
            }.take(8)
        }
    }

    Column(modifier = modifier) {
        OutlinedTextField(
            value = value,
            onValueChange = { new ->
                onValueChange(new.uppercase())
                showDropdown = new.isNotEmpty()
            },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Enter ticker symbol (e.g. AAPL)") },
            singleLine = true,
            enabled = enabled,
            shape = RoundedCornerShape(12.dp),
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Characters,
                imeAction = ImeAction.Search,
            ),
            keyboardActions = KeyboardActions(
                onSearch = {
                    focusManager.clearFocus()
                    if (value.isNotBlank()) {
                        showDropdown = false
                        onTickerSubmit(value.trim().uppercase())
                    }
                },
            ),
        )

        AnimatedVisibility(visible = showDropdown && filtered.isNotEmpty()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(8.dp, RoundedCornerShape(12.dp)),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = thaitaColors.cardBg),
            ) {
                LazyColumn(
                    modifier = Modifier.heightIn(max = 300.dp),
                ) {
                    items(filtered) { company ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    showDropdown = false
                                    onValueChange(company.ticker)
                                    focusManager.clearFocus()
                                    onTickerSubmit(company.ticker)
                                }
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            Text(
                                text = company.ticker,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                ),
                                color = thaitaColors.textPrimary,
                            )
                            Text(
                                text = company.company,
                                style = MaterialTheme.typography.bodySmall,
                                color = thaitaColors.textSecondary,
                                modifier = Modifier.weight(1f),
                            )
                            Text(
                                text = company.sector,
                                style = MaterialTheme.typography.bodySmall,
                                color = thaitaColors.axisMuted,
                            )
                        }
                    }
                }
            }
        }
    }
}
