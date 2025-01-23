package com.example.blackbeard.screens

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.blackbeard.R

@Composable
fun EmptyScreen(modifier: Modifier) {
    Text(stringResource(id = R.string.empty))
}