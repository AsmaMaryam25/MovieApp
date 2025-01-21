package com.example.blackbeard.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp

@Composable
fun SearchBar(
    //onSearchQueryChange: (String, Boolean) -> Unit,
    modifier: Modifier = Modifier,
    //isSearchBarFocused: Boolean,
    //currentTabIndex: Int,
    onSearchBarFocus: () -> Unit = {},
    onCancelClicked: () -> Unit = {},
    isFocused: Boolean,
    onSearchClicked: (String, Boolean) -> Unit = {_, _ -> {}},
    addToRecent: (String) -> Unit = {_ -> {}}
    ) {
    //val keyboardController = LocalSoftwareKeyboardController.current
    //val focusManager = LocalFocusManager.current
    var searchText by remember { mutableStateOf("") }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        OutlinedTextField(
            modifier = Modifier.onFocusChanged { focusState ->
                if(focusState.isFocused) {
                    onSearchBarFocus()
                }
            },
            value = searchText,
            onValueChange = {
                searchText = it
            },
            placeholder = {
                Text(
                    text = "Search movie...",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            /*
            trailingIcon = {
                Row {
                    if (searchQuery.value.text.isNotEmpty()) {
                        IconButton(
                            onClick = {
                                searchQuery.value = TextFieldValue("")
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Clear",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            },
             */
            shape = RoundedCornerShape(30.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f),
                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f),
                unfocusedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                focusedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                cursorColor = MaterialTheme.colorScheme.onSurfaceVariant,
                unfocusedBorderColor = MaterialTheme.colorScheme.surfaceVariant,
                focusedBorderColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(
                onSearch = {
                    onSearchClicked(searchText, false)
                    addToRecent(searchText)
                }
            )
        )

        if (isFocused) {
            TextButton(

                onClick = {
                    onCancelClicked()
                    /*
                    keyboardController?.hide()
                    searchQuery.value = TextFieldValue("")
                    onSearchBarFocusChange(false)
                    focusManager.clearFocus()
                    searchContentViewModel.searchType.value = false
                    searchContentViewModel.selectedCategories.clear()
                     */
                },
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Text(
                    text = "Cancel",
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
        }

    }
}