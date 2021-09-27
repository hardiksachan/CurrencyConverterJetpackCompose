package com.hardiksachan.currencyconverterjetpackcompose.presentation.currencyconverter

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.hardiksachan.currencyconverterjetpackcompose.application.currencyconverter.CurrencyConverterEvent
import com.hardiksachan.currencyconverterjetpackcompose.application.currencyconverter.CurrencyConverterLogic
import com.hardiksachan.currencyconverterjetpackcompose.application.currencyconverter.CurrencySelectorPageState
import com.hardiksachan.currencyconverterjetpackcompose.domain.entity.Currency
import com.hardiksachan.currencyconverterjetpackcompose.presentation.theme.Black
import com.hardiksachan.currencyconverterjetpackcompose.presentation.theme.BlueDark
import com.hardiksachan.currencyconverterjetpackcompose.presentation.theme.BlueLight
import com.hardiksachan.currencyconverterjetpackcompose.presentation.util.noRippleClickable

@ExperimentalAnimationApi
@Composable
fun CurrencySelectorPage(
    state: CurrencySelectorPageState,
    logic: CurrencyConverterLogic,
    snackbarHostState: SnackbarHostState,
    navController: NavController,
) {
    val searchDisplay = state.searchDisplay.collectAsState()
    val currencyList = state.currencyList.collectAsState()

    val error = state.error.collectAsState()
    val isLoading = state.isLoading.collectAsState()

    val scaffoldState = rememberScaffoldState(snackbarHostState = snackbarHostState)

    val focusManager = LocalFocusManager.current

    Scaffold(
        scaffoldState = scaffoldState,
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = MaterialTheme.colors.primarySurface
    ) {
        Column(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .noRippleClickable {
                    focusManager.clearFocus()
                },
        ) {
            OutlinedTextField(
                value = searchDisplay.value,
                onValueChange = {
                    logic.onEvent(CurrencyConverterEvent.SearchDisplayTextChanged(it))
                },
                modifier = Modifier.fillMaxWidth()
                    .padding(16.dp),
                leadingIcon = {
                    Icon(
                        painter = rememberVectorPainter(image = Icons.Filled.Search),
                        contentDescription = "Search icon"
                    )
                },
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        focusManager.clearFocus()
                    }
                ),
                label = {
                    Text(text = "Search")
                },
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = BlueLight,
                    unfocusedBorderColor = MaterialTheme.colors.onPrimary,
                    cursorColor = BlueLight,
                    focusedLabelColor = BlueLight
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (error.value != null) {
                Spacer(modifier = Modifier.padding(32.dp))
                Text(
                    text = error.value.toString(),
                    color = Color.Red,
                    style = MaterialTheme.typography.body2
                )
            }

            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(8.dp)
            ) {
                itemsIndexed(currencyList.value) { _, currency ->
                    CurrencyView(
                        currency = currency,
                        onClick = {
                            logic.onEvent(CurrencyConverterEvent.CurrencySelected(it))
                            navController.popBackStack()
                        }
                    )
                }
            }
        }

        AnimatedVisibility(visible = isLoading.value) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Black.copy(alpha = .2f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = BlueDark)
            }
        }
    }
}

@Composable
fun CurrencyView(
    currency: Currency,
    onClick: (Currency) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = MaterialTheme.shapes.small,
        elevation = 16.dp,
        modifier = modifier
            .fillMaxWidth()
            .padding(2.dp)
            .clickable {
                onClick(currency)
            },
        backgroundColor = MaterialTheme.colors.primary
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = currency.code.uppercase(),
                color = MaterialTheme.colors.onPrimary
            )
            Spacer(modifier = Modifier.width(32.dp))
            Text(text = currency.name)
        }
    }
}