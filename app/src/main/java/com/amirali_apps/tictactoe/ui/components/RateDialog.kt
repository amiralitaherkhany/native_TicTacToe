package com.amirali_apps.tictactoe.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.LayoutDirection
import com.amirali_apps.tictactoe.BuildConfig
import com.amirali_apps.tictactoe.R
import com.amirali_apps.tictactoe.utils.AppLocaleManager

@Composable
fun RateDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onRateClick: () -> Unit
) {
    if (showDialog) {
        CompositionLocalProvider(LocalLayoutDirection provides if (AppLocaleManager().getLanguageCode(LocalContext.current) == "fa") LayoutDirection.Rtl else LayoutDirection.Ltr) {
            AlertDialog(
                onDismissRequest = onDismiss,
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                title = {
                    Text(
                        text = stringResource(R.string.do_you_enjoy_the_game),
                        style = MaterialTheme.typography.bodyLarge.plus(
                            TextStyle(
                                color = MaterialTheme.colorScheme.primary
                            )
                        )
                    )
                },
                text = {
                    Text(
                        text = stringResource(
                            R.string.rate_us_on,
                            when (BuildConfig.FLAVOR) {
                                "myket" -> stringResource(R.string.myket)
                                "bazaar" -> stringResource(R.string.bazaar)
                                else -> stringResource(R.string.store)
                            }
                        ),
                        style = MaterialTheme.typography.bodyMedium.plus(
                            TextStyle(
                                color = MaterialTheme.colorScheme.primary
                            )
                        )
                    )
                },
                confirmButton = {
                    Button(onClick = onRateClick) {
                        Text(
                            stringResource(R.string.rate),
                            style = MaterialTheme.typography.bodyMedium.plus(
                                TextStyle(
                                    color = MaterialTheme.colorScheme.secondary
                                )
                            )
                        )
                    }
                },
                dismissButton = {
                    Button(onClick = onDismiss) {
                        Text(
                            stringResource(R.string.never),
                            style = MaterialTheme.typography.bodyMedium.plus(
                                TextStyle(
                                    color = MaterialTheme.colorScheme.secondary
                                )
                            )
                        )
                    }
                }
            )
        }
    }
}
