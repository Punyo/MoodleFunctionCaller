package com.punyo.moodlefunctioncaller.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.punyo.moodlefunctioncaller.R
import kotlinx.coroutines.launch

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    token: String,
    viewModel: MainScreenViewModel = viewModel(
        factory = MainScreenViewModel.Factory(
            MoodleRepository(
                MoodleRemoteDataSource(token)
            )
        )
    )
) {
    val state = viewModel.uiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val nowProcessingString = stringResource(id = R.string.ui_now_processing)
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom
    ) {
        Text(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            text = state.value.currentOutput.ifEmpty {
                stringResource(id = R.string.ui_outputs_empty)
            },
        )
        TextField(
            modifier = Modifier
                .padding(4.dp)
                .fillMaxWidth(),
            value = state.value.args1,
            onValueChange = {
                viewModel.setArgs1(args1 = it)
            },
            label = { Text(stringResource(id = R.string.ui_args1)) }
        )
        TextField(
            modifier = Modifier
                .padding(4.dp)
                .fillMaxWidth(),
            value = state.value.args2,
            onValueChange = {
                viewModel.setArgs2(args2 = it)
            },
            label = { Text(stringResource(id = R.string.ui_args2)) }
        )
        Button(
            modifier = Modifier
                .padding(4.dp)
                .fillMaxWidth(),
            onClick = {
                viewModel.setOutput(nowProcessingString)
                coroutineScope.launch {
                    viewModel.setOutput(viewModel.getUserInfo().toString())
                }
            }) {
            Text(stringResource(id = R.string.ui_get_user_info))
        }

        Button(
            modifier = Modifier
                .padding(4.dp)
                .fillMaxWidth(),
            onClick = {
                viewModel.setOutput(nowProcessingString)
                coroutineScope.launch {
                    viewModel.setOutput(viewModel.getUserCourses().toString())
                }
            }) {
            Text(stringResource(id = R.string.ui_get_user_courses_and_assignments))
        }

        Button(
            modifier = Modifier
                .padding(4.dp)
                .fillMaxWidth(),
            onClick = {
                coroutineScope.launch {
                    viewModel.getUserInfo()
                }
            }) {
            Text(stringResource(id = R.string.ui_get_submission_status), textAlign = TextAlign.Center)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    MainScreen(token = "token")
}