package com.qawmierp.apk.ui.academic

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.qawmierp.apk.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentsScreen(
    onBack: () -> Unit,
    viewModel: AcademicViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    var query by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.loadStudents()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.students)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            OutlinedTextField(
                value = query,
                onValueChange = {
                    query = it
                    viewModel.loadStudents(it.takeIf { q -> q.isNotBlank() })
                },
                placeholder = { Text("নাম বা রোল দিয়ে খুঁজুন...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))

            if (state.isLoading) LinearProgressIndicator(modifier = Modifier.fillMaxWidth())

            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(state.students) { student ->
                    OutlinedCard(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            AsyncImage(
                                model = student.photo_url,
                                contentDescription = student.full_name,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.size(44.dp).clip(CircleShape)
                            )
                            Column(modifier = Modifier.weight(1f)) {
                                Text(student.full_name, fontWeight = FontWeight.Bold)
                                Text(
                                    "রোল: ${student.roll_number ?: "-"} • ${student.classes?.name ?: "-"}",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
