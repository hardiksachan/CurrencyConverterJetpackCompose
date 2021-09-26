package com.hardiksachan.currencyconverterjetpackcompose.presentation.currencyconverter

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Send
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.hardiksachan.currencyconverterjetpackcompose.application.currencyconverter.CurrencyConverterEvent
import com.hardiksachan.currencyconverterjetpackcompose.application.currencyconverter.CurrencyConverterLogic
import com.hardiksachan.currencyconverterjetpackcompose.application.currencyconverter.HomePageState
import com.hardiksachan.currencyconverterjetpackcompose.domain.entity.Currency
import com.hardiksachan.currencyconverterjetpackcompose.presentation.theme.BlueDark

@Composable
fun HomePage(
    state: HomePageState,
    logic: CurrencyConverterLogic,
) {
    val baseCurrency: State<Currency> = state.baseCurrency.collectAsState()
    val baseCurrencyDisplay = state.baseCurrencyDisplay.collectAsState()

    val targetCurrency: State<Currency> = state.targetCurrency.collectAsState()
    val targetCurrencyDisplay = state.targetCurrencyDisplay.collectAsState()

    val error = state.error.collectAsState()

    val scaffoldState = rememberScaffoldState()

    Scaffold(
        scaffoldState = scaffoldState,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            CurrencyCard(
                currency = baseCurrency.value,
                amount = baseCurrencyDisplay.value,
                currencyClickHandler = {
                    logic.onEvent(CurrencyConverterEvent.BaseCurrencyChangeRequested)
                },
                textChangeHandler = {
                    logic.onEvent(
                        CurrencyConverterEvent.BaseCurrencyDisplayTextChanged(it)
                    )
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Button(onClick = {
                    logic.onEvent(CurrencyConverterEvent.EvaluatePressed)
                }) {
                    Icon(
                        painter = rememberVectorPainter(Icons.Filled.Send),
                        contentDescription = "Evaluate"
                    )
                }


                Spacer(modifier = Modifier.width(8.dp))

                Button(onClick = {
                    logic.onEvent(CurrencyConverterEvent.SwitchCurrenciesPressed)
                }) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Text(text = "Switch Currencies")
                    }
                }
            }


            Spacer(modifier = Modifier.height(8.dp))

            CurrencyCard(
                currency = targetCurrency.value,
                amount = targetCurrencyDisplay.value,
                currencyClickHandler = {
                    logic.onEvent(CurrencyConverterEvent.TargetCurrencyChangeRequested)
                },
                textChangeHandler = {}, enabled = false
            )
            if (error.value != null) {
                Spacer(modifier = Modifier.padding(32.dp))
                Text(
                    text = error.value.toString(),
                    color = Color.Red,
                    style = MaterialTheme.typography.body2
                )
            }
        }
    }


}

@Composable
fun CurrencyCard(
    currency: Currency,
    amount: String,
    currencyClickHandler: () -> Unit,
    textChangeHandler: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = MaterialTheme.shapes.small,
        elevation = 4.dp,
        backgroundColor = MaterialTheme.colors.primaryVariant
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        currencyClickHandler()
                    }
            ) {
                Column(
                    verticalArrangement = Arrangement.SpaceAround,
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = currency.code.uppercase(),
                        color = MaterialTheme.colors.onPrimary
                    )
                    Text(text = currency.name)
                }
                Icon(
                    painter = rememberVectorPainter(image = Icons.Filled.ArrowForward),
                    contentDescription = "right arrow",
                    tint = BlueDark,
                    modifier = Modifier
                        .padding(end = 16.dp)
                        .size(20.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = amount,
                onValueChange = {
                    textChangeHandler(it)
                },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = {
                    Text(text = java.util.Currency.getInstance(currency.code).symbol)
                },
                enabled = enabled,
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Number
                ),
                placeholder = {
                    Text(text = "0.00")
                }
            )
        }
    }
}