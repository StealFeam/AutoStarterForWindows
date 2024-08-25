package org.zeen.autostart.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Snackbar
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.ComposeWindow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogWindow
import com.zeen.autostart.Application
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.zeen.autostart.store.buildApplication
import org.zeen.autostart.store.checkNameOrPathExist
import org.zeen.autostart.store.insert
import org.zeen.autostart.store.queryAll
import org.zeen.autostart.store.removeAll
import java.awt.FileDialog

const val EXECUTABLE_FILE_TYPE = "*.exe;*.bat"

/**
 * 显示文件选择框
 */
fun showFileDialog(fileType: String = EXECUTABLE_FILE_TYPE): String {
    val dialog = FileDialog(ComposeWindow(), "选择要启动的程序路径", FileDialog.LOAD)
    dialog.file = fileType
    dialog.isVisible = true
    return if (dialog.directory != null && dialog.file != null) {
        dialog.directory + dialog.file
    } else {
        ""
    }
}

/**
 * 添加自启动应用对话框
 */
@Composable
fun showAddDialog(dismissCallback: () -> Unit) {
    val isShowDialog = remember { mutableStateOf(true) }
    var name by remember { mutableStateOf("") }
    var path by remember { mutableStateOf("") }
    val snackBarVisible = remember { mutableStateOf(false) }
    var snackBarText = "请输入程序名称和路径"
    val rememberScope = rememberCoroutineScope()

    rememberScope.launch {
        queryAll()
    }

    DialogWindow(
        icon = null,
        title = "添加自启应用",
        visible = isShowDialog.value,
        onCloseRequest = {
            isShowDialog.value = false
        },
        content = {
            MaterialTheme {
                Column(
                    Modifier.fillMaxWidth()
                        .fillMaxHeight()
                        .padding(16.dp)
                ) {
                    Row(Modifier.fillMaxWidth()) {
                        Text("程序名称", modifier = Modifier.align(Alignment.CenterVertically).padding(end = 8.dp))
                        TextField(value = name, onValueChange = { name = it  }, modifier = Modifier.fillMaxWidth())
                    }
                    Row(Modifier.fillMaxWidth()) {
                        Text("程序路径", modifier = Modifier.align(Alignment.CenterVertically).padding(end = 8.dp))
                        Button(onClick = { path = showFileDialog() }) {
                            Text(path.ifEmpty {
                                "选择程序路径"
                            })
                        }
                    }
                    Column(
                        Modifier.fillMaxHeight().align(Alignment.End),
                        verticalArrangement = Arrangement.Bottom,
                        horizontalAlignment = Alignment.End
                    ) {
                        if (snackBarVisible.value) {
                            Snackbar(
                                action = {
                                    Button(onClick = {
                                        snackBarVisible.value = false
                                    }) {
                                        Text("知道了")
                                    }
                                }
                            ) {
                                Text(snackBarText, color = Color.White)
                            }
                        } else {
                            Button(
                                modifier = Modifier.align(Alignment.End)
                                    .fillMaxHeight(0.5f)
                                    .fillMaxWidth(0.2f),
                                shape = RoundedCornerShape(6.dp),
                                onClick = {
                                    if (name.trim().isEmpty() || path.trim().isEmpty()) {
                                        snackBarVisible.value = true
                                    } else {
                                        rememberScope.launch {
                                            val application = buildApplication(name, path)
                                            if (withContext(Dispatchers.IO) { checkNameOrPathExist(application) }) {
                                                snackBarText = "名称或者路径重复"
                                                snackBarVisible.value = true
                                            } else {
                                                insert(application)
                                                isShowDialog.value = false
                                                dismissCallback.invoke()
                                            }
                                        }
                                    }
                                }
                            ) {
                                Text("添加")
                            }
                        }
                    }
                }
            }
        }
    )
}