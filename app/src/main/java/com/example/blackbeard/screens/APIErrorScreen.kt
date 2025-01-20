package com.example.blackbeard.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.blackbeard.R

@Composable
fun APIErrorScreen(modifier: Modifier) {
    val image = painterResource(id = R.drawable.api_error)

    Column(verticalArrangement = Arrangement.Center, modifier = modifier.padding(16.dp)) {
        Image(
            painter = image,
            contentDescription = stringResource(id = R.string.api_error),
            modifier = Modifier.fillMaxWidth()
        )
        Text(
            text = stringResource(id = R.string.api_error),
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
            fontSize = 32.sp,
            lineHeight = 35.sp
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(id = R.string.api_error_message),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
            fontSize = 16.sp
        )
    }
}