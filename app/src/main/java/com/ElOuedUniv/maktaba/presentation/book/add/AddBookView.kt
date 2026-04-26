package com.ElOuedUniv.maktaba.presentation.book.add

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import androidx.compose.ui.graphics.Color

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddBookView(
    onBackClick: () -> Unit,
    viewModel: AddBookViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    var selectedImageBytes by remember { mutableStateOf<ByteArray?>(null) }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            imageUri = it
            selectedImageBytes = context.contentResolver
                .openInputStream(it)?.readBytes()
        }
    }

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            onBackClick()
        }
    }

    var selectedPdfBytes by remember { mutableStateOf<ByteArray?>(null) }
    var pdfSelected by remember { mutableStateOf(false) }
    var pdfSizeMB by remember { mutableDoubleStateOf(0.0) }

    val pdfLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            val bytes = context.contentResolver.openInputStream(it)?.readBytes()
            selectedPdfBytes = bytes
            pdfSizeMB = if (bytes != null) bytes.size / (1024.0 * 1024.0) else 0.0
            pdfSelected = true
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "ADD BOOK",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 2.sp
                        )
                    )
                },
                navigationIcon = {
                    TextButton(
                        onClick = {
                            if (uiState.isLoading) {
                                viewModel.cancelUpload()
                            }
                            onBackClick()
                        }
                    ) {
                        Text(
                            if (uiState.isLoading) "Cancel Upload" else "Cancel",
                            color = if (uiState.isLoading)
                                MaterialTheme.colorScheme.error
                            else
                                MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                actions = {
                    if (uiState.isLoading) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                "جاري الرفع...",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    } else {
                        TextButton(
                            onClick = { viewModel.onAction(AddBookUiAction.OnAddClick(selectedImageBytes, selectedPdfBytes)) },
                            enabled = uiState.isFormValid
                        ) {
                            val color = if (uiState.isFormValid)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                            Text("Confirm", fontWeight = FontWeight.Bold, color = color)
                        }
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(scrollState)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ),
                onClick = { if (!uiState.isLoading) launcher.launch("image/*") }
            ) {
                if (imageUri != null) {
                    Image(
                        painter = rememberAsyncImagePainter(imageUri),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.AddPhotoAlternate,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            "ADD COVER IMAGE",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            OutlinedButton(
                onClick = { if (!uiState.isLoading) pdfLauncher.launch("application/pdf") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                enabled = !uiState.isLoading
            ) {
                Icon(
                    imageVector = Icons.Default.PictureAsPdf,
                    contentDescription = null,
                    tint = if (pdfSelected) Color(0xFF4CAF50) else MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (pdfSelected) "PDF Selected ✓" else "اختر ملف PDF",
                    color = if (pdfSelected) Color(0xFF4CAF50) else MaterialTheme.colorScheme.primary
                )
            }

            if (pdfSelected && selectedPdfBytes != null) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "📦 حجم PDF: %.2f MB".format(pdfSizeMB),
                        fontSize = 12.sp,
                        color = if (pdfSizeMB > 15) Color(0xFFFF6B6B) else Color(0xFF4CAF50)
                    )
                    if (pdfSizeMB > 15) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "⚠️ ملف كبير! الرفع قد ياخذ دقيقة أو أكثر",
                            fontSize = 11.sp,
                            color = Color(0xFFFF6B6B)
                        )
                    }
                    if (pdfSizeMB > 30) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "❌ الملف كبير جداً! الحد الأقصى 30 MB",
                            fontSize = 11.sp,
                            color = Color(0xFFD32F2F),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = uiState.title,
                    onValueChange = { viewModel.onAction(AddBookUiAction.OnTitleChange(it)) },
                    label = { Text("Book Title") },
                    placeholder = { Text("e.g. Clean Code") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    isError = uiState.titleError != null,
                    supportingText = { uiState.titleError?.let { Text(it) } },
                    enabled = !uiState.isLoading
                )

                OutlinedTextField(
                    value = uiState.isbn,
                    onValueChange = { viewModel.onAction(AddBookUiAction.OnIsbnChange(it)) },
                    label = { Text("ISBN") },
                    placeholder = { Text("e.g. 9780132350884") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    isError = uiState.isbnError != null,
                    supportingText = { uiState.isbnError?.let { Text(it) } },
                    enabled = !uiState.isLoading
                )

                OutlinedTextField(
                    value = uiState.nbPages,
                    onValueChange = { viewModel.onAction(AddBookUiAction.OnPagesChange(it)) },
                    label = { Text("Pages") },
                    placeholder = { Text("Not set") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    isError = uiState.nbPagesError != null,
                    supportingText = { uiState.nbPagesError?.let { Text(it) } },
                    enabled = !uiState.isLoading
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { viewModel.onAction(AddBookUiAction.OnAddClick(selectedImageBytes, selectedPdfBytes)) },
                enabled = uiState.isFormValid && !uiState.isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("جاري الرفع...", style = MaterialTheme.typography.titleMedium)
                } else {
                    Text("Confirm", style = MaterialTheme.typography.titleMedium)
                }
            }

            uiState.errorMessage?.let { error ->
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.9f)
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = error,
                        modifier = Modifier.padding(12.dp),
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        fontSize = 13.sp
                    )
                }
            }

            TextButton(
                onClick = {
                    if (uiState.isLoading) {
                        viewModel.cancelUpload()
                    }
                    onBackClick()
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isLoading
            ) {
                Text(
                    "Cancel",
                    color = if (uiState.isLoading)
                        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
