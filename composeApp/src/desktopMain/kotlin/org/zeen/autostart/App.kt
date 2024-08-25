package org.zeen.autostart

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.ui.tooling.preview.Preview

import com.zeen.autostart.Application
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.zeen.autostart.api.request
import org.zeen.autostart.store.queryAll
import org.zeen.autostart.store.removeApplication
import org.zeen.autostart.ui.showAddDialog

@Composable
@Preview
fun App() {
    MaterialTheme {
        var showContent by remember { mutableStateOf(false) }
        var displayText by remember { mutableStateOf("请求中") }
        var addDialogVisible by remember { mutableStateOf(false) }
        val rememberScope = rememberCoroutineScope()
        val localApplication = remember { mutableStateListOf<Application>() }

        rememberScope.launch {
            val result = withContext(Dispatchers.IO) {
                request.getDailyProperty().execute().body()
            }
            displayText = if (result?.isSuccess == true) {
                result.type?.name ?: "数据为空"
            } else {
                "出现错误"
            }
        }
        rememberScope.launch {
            val applications = withContext(Dispatchers.IO) {
                queryAll()
            }
            localApplication.clear()
            localApplication.addAll(applications)
            showContent = true
        }
        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Text("今天是：$displayText")
            Button(onClick = {
                addDialogVisible = true
            }) {
                Text("添加自启动程序")
            }
            AnimatedVisibility(showContent) {
                LazyColumn(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                    itemsIndexed(localApplication) { index, item ->
                        Card(
                            modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp, bottom = if (index == (localApplication.size - 1)) 16.dp else 0.dp)
                                .fillMaxWidth(),
                            elevation = 4.dp
                        ) {
                            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                                Column(modifier = Modifier.padding(6.dp)) {
                                    Text(item.name, modifier = Modifier.padding(end = 10.dp), color = MaterialTheme.colors.primary)
                                    Text(item.path)
                                }
                                Button(
                                    onClick = {
                                        rememberScope.launch {
                                            if (withContext(Dispatchers.IO) { removeApplication(item) }) {
                                                localApplication.remove(item)
                                            }
                                        }
                                    },
                                    modifier = Modifier.padding(end = 16.dp)
                                ) {
                                    Text("删除")
                                }
                            }
                        }
                    }
                }
            }
            if (addDialogVisible) {
                showAddDialog {
                    addDialogVisible = false
                }
            }
        }
    }
}