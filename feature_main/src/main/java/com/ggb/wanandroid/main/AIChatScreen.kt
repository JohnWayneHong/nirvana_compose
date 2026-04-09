package com.ggb.wanandroid.main

import androidx.compose.animation.*
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.unit.sp
import com.ggb.wanandroid.main.data.ai.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AIChatScreen(
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {
    val context = LocalContext.current
    val db = remember { VolcDatabase.getDatabase(context) }
    val gson = remember { Gson() }
    val coroutineScope = rememberCoroutineScope()
    
    val systemPrompt = remember {
        ChatMessage(role = "system", content = "你是一个智能助手，名字叫牛蛙呐 AI 小助手。你的回复应当友好且专业。")
    }
    
    // 状态管理：改为使用 MutableState<List> 确保高频更新下状态通知的稳定性
    var messageList by remember {
        mutableStateOf(listOf(
            systemPrompt,
            ChatMessage(role = "assistant", content = "你好！我是牛蛙呐 (NIRVANA) AI 助手。有什么我可以帮你的吗？")
        ))
    }
    
    var inputText by remember { mutableStateOf("") }
    var isWaitingResponse by remember { mutableStateOf(false) }
    var showHistory by remember { mutableStateOf(false) }
    var currentSessionId by remember { mutableStateOf<Long?>(null) }
    
    // 性能优化：过滤掉 system 消息用于展示
    val displayMessages by remember {
        derivedStateOf { messageList.filter { it.role != "system" } }
    }
    
    val volcNetTool = remember { VolcNetTool() }
    val listState = rememberLazyListState()

    // 【新增 1】：监听用户是否正在拖拽列表（核心！）
    val isDragged by listState.interactionSource.collectIsDraggedAsState()

    // 【新增 2】：定义一个状态，控制是否允许自动滚动
    var autoScrollEnabled by remember { mutableStateOf(true) }

    // 【新增 3】：智能判断何时开启/关闭自动滚动
    LaunchedEffect(isDragged, listState.canScrollForward) {
        if (isDragged) {
            // 只要用户手指触摸并滑动屏幕，立刻暂停自动滚动
            autoScrollEnabled = false
        } else if (!listState.canScrollForward) {
            // 当用户没有在滑动，并且列表已经触底时（canScrollForward 为 false 代表到底了）
            // 重新恢复自动滚动
            autoScrollEnabled = true
        }
    }

    // 悬浮按钮的显示逻辑：只有在“关闭了自动滚动” 且 “列表还可以往下滚” 的时候才显示
    val showScrollToBottom by remember {
        derivedStateOf {
            !autoScrollEnabled && listState.canScrollForward
        }
    }

    // 保存会话
    fun saveSessionToDb() {
        if (messageList.size <= 2) return
        val chatList = messageList.toList()
        val lastMsg = chatList.lastOrNull { it.role != "system" }?.content ?: ""
        val title = chatList.firstOrNull { it.role == "user" }?.content?.take(20) ?: "新对话"

        val entity = ChatSessionEntity(
            id = currentSessionId ?: 0L,
            title = title,
            lastMessage = lastMsg,
            timestamp = System.currentTimeMillis(),
            messagesJson = gson.toJson(chatList)
        )
        coroutineScope.launch(Dispatchers.IO) {
            // 【修改点】：获取数据库返回的 ID
            val savedId = db.chatSessionDao().insertSession(entity)

            // 【修改点】：如果当前是在创建新对话，则将返回的真实 ID 赋值给 currentSessionId
            // 这样在下一次保存（例如网络返回新内容）时，传入的 id 就不再是 0L，
            // Room 就会执行 REPLACE 更新操作，而不是新增。
            if (currentSessionId == null) {
                currentSessionId = savedId
            }
        }
    }

    // 智能滚动逻辑
    val isAtBottom by remember {
        derivedStateOf {
            val layoutInfo = listState.layoutInfo
            val visibleItems = layoutInfo.visibleItemsInfo
            if (visibleItems.isEmpty()) true
            else {
                val lastItem = visibleItems.last()
                lastItem.index >= layoutInfo.totalItemsCount - 1
            }
        }
    }

    val mainGradient = Brush.verticalGradient(
        colors = listOf(Color(0xFF0D1B2A), Color(0xFF1B263B))
    )

    Box(modifier = Modifier.fillMaxSize().background(mainGradient)) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = if (showHistory) "对话历史" else stringResource(R.string.ai_chat_title),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    },
                    navigationIcon = {
                        if (showHistory) {
                            IconButton(onClick = { showHistory = false }) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                            }
                        } else {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color(0xFF00B4D8), modifier = Modifier.padding(start = 16.dp).size(24.dp))
                        }
                    },
                    actions = {
                        if (!showHistory) {
                            IconButton(onClick = { showHistory = true }) {
                                Icon(Icons.Default.History, contentDescription = "History", tint = Color.White)
                            }
                            IconButton(onClick = {
                                saveSessionToDb()
                                messageList = listOf(systemPrompt, ChatMessage(role = "assistant", content = "新对话已开启。请问有什么我可以帮您？"))
                                currentSessionId = null
                            }) {
                                Icon(Icons.Default.AddComment, contentDescription = "New Chat", tint = Color.White)
                            }
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent)
                )
            },
            containerColor = Color.Transparent
        ) { innerPadding ->
            // 【新增】：动态计算底部需要垫起的最大高度
            val imeBottom = WindowInsets.ime.asPaddingValues().calculateBottomPadding()
            val navBarBottom = contentPadding.calculateBottomPadding()
            val dynamicBottomPadding = max(imeBottom, navBarBottom)

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    // 【修改】：合并 padding，顶部取 innerPadding，底部取刚才计算的最大值
                    .padding(
                        top = innerPadding.calculateTopPadding(),
                        bottom = dynamicBottomPadding
                    )
            ) {
                if (showHistory) {
                    ChatHistoryView(
                        db = db,
                        onSessionClick = { session ->
                            val type = object : TypeToken<List<ChatMessage>>() {}.type
                            val restored: List<ChatMessage> = gson.fromJson(session.messagesJson, type)
                            messageList = restored
                            currentSessionId = session.id
                            showHistory = false
                        }
                    )
                } else {
                    Column(modifier = Modifier.fillMaxSize()) {
                        LazyColumn(
                            state = listState,
                            // weight(1f) 会让 LazyColumn 占据所有剩余空间
                            modifier = Modifier.weight(1f).fillMaxWidth().padding(horizontal = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            contentPadding = PaddingValues(top = 16.dp, bottom = 16.dp)
                        ) {
                            items(
                                items = displayMessages,
                                key = { it.id }
                            ) { message ->
                                ChatBubble(message)
                            }
                            if (isWaitingResponse) {
                                item(key = "loading_anim") { LoadingResponseItem() }
                            }
                        }

                        ChatInputArea(
                            inputText = inputText,
                            onTextChange = { inputText = it },
                            isWaiting = isWaitingResponse,
                            onSend = {
                                if (inputText.isNotBlank()) {
                                    val userMsg = ChatMessage(role = "user", content = inputText)
                                    messageList = messageList + userMsg
                                    inputText = ""
                                    isWaitingResponse = true

                                    coroutineScope.launch {
                                        // 【修复点 1】：将发送后的首次动画滚动放入独立子协程
                                        // 防止手指滑动抛出 CancellationException 导致后续的网络请求无法发出
                                        launch {
                                            try {
                                                listState.animateScrollToItem(listState.layoutInfo.totalItemsCount.coerceAtLeast(0))
                                            } catch (e: Exception) {
                                                // 忽略滚动被打断的异常
                                            }
                                        }

                                        var aiMsgStarted = false
                                        volcNetTool.sendChatRequestStream(messages = messageList)
                                            .onCompletion {
                                                isWaitingResponse = false
                                                saveSessionToDb()
                                            }
                                            .catch { e ->
                                                isWaitingResponse = false
                                                messageList = messageList + ChatMessage(role = "assistant", content = "错误：${e.message}")
                                                saveSessionToDb()
                                            }
                                            .collect { chunk ->
                                                if (!aiMsgStarted) {
                                                    isWaitingResponse = false
                                                    messageList = messageList + ChatMessage(role = "assistant", content = chunk)
                                                    aiMsgStarted = true
                                                } else {
                                                    val lastMsg = messageList.last()
                                                    val updatedMsg = lastMsg.copy(content = lastMsg.content + chunk)
                                                    messageList = messageList.toMutableList().also { it[it.lastIndex] = updatedMsg }
                                                }

                                                if (autoScrollEnabled) {
                                                    launch {
                                                        try {
                                                            // 目标索引始终是最后一条消息
                                                            val targetIndex = (listState.layoutInfo.totalItemsCount - 1).coerceAtLeast(0)

                                                            // 【核心修复】：加上极大的偏移量，强制让该 Item 的底部对齐屏幕底部
                                                            // Int.MAX_VALUE 有时会导致 Compose 计算溢出，所以我们用一个足够大但安全的像素值
                                                            val bottomOffset = 100000

                                                            listState.scrollToItem(
                                                                index = targetIndex,
                                                                scrollOffset = bottomOffset // 指定向下偏移，让视图强行推到底
                                                            )
                                                        } catch (e: Exception) {
                                                            // 忽略由于手指拖拽导致的 CancellationException
                                                        }
                                                    }
                                                }
                                            }
                                    }
                                }
                            }
                        )
                    }

                    // 滚动按钮
                    AnimatedVisibility(
                        visible = showScrollToBottom,
                        enter = fadeIn() + scaleIn(),
                        exit = fadeOut() + scaleOut(),
                        modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 88.dp)
                    ) {
                        Surface(
                            onClick = { coroutineScope.launch { listState.animateScrollToItem(listState.layoutInfo.totalItemsCount - 1) } },
                            color = Color(0xFF00B4D8).copy(alpha = 0.9f),
                            shape = CircleShape,
                            shadowElevation = 6.dp,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.KeyboardDoubleArrowDown, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LoadingResponseItem() {
    // 定义目标进度，初始为 25%
    var targetProgress by remember { mutableFloatStateOf(0.25f) }

    // 使用 animateFloatAsState 让进度变化时带有平滑的动画过渡
    val animatedProgress by animateFloatAsState(
        targetValue = targetProgress,
        animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing),
        label = "progress_anim"
    )

    // 协程模拟阶段性“思考”进度
    LaunchedEffect(Unit) {
        delay(400)
        targetProgress = 0.40f
        delay(600)
        targetProgress = 0.50f
        delay(800)
        targetProgress = 0.60f
        delay(1000)
        targetProgress = 0.75f
        delay(1200)
        targetProgress = 0.90f
        delay(800)
        targetProgress = 0.95f
        // 停留在 95%，直到大模型返回首字并触发外部状态改变，移除这个组件
    }

    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
        CircularProgressIndicator(
            progress = { animatedProgress }, // 改为确定进度的指示器
            modifier = Modifier.size(18.dp),
            color = Color(0xFF00B4D8),
            strokeWidth = 2.dp,
            trackColor = Color(0xFF1B263B) // 添加轨道底色使其更美观
        )
        Spacer(modifier = Modifier.width(12.dp))
        // 动态显示百分比
        Text("牛蛙呐正在思考... ${(animatedProgress * 100).toInt()}%", color = Color(0xFF00B4D8), fontSize = 14.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun ChatHistoryView(db: VolcDatabase, onSessionClick: (ChatSessionEntity) -> Unit) {
    val historySessions by db.chatSessionDao().getAllSessions().collectAsState(initial = emptyList())
    val dateFormat = remember { SimpleDateFormat("MM-dd HH:mm", Locale.getDefault()) }
    if (historySessions.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("暂无对话历史", color = Color.Gray) }
    } else {
        LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(historySessions, key = { it.id }) { item ->
                Surface(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).clickable { onSessionClick(item) },
                    color = Color(0xFF1B263B).copy(alpha = 0.6f),
                    tonalElevation = 2.dp
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(item.title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f))
                            Text(dateFormat.format(Date(item.timestamp)), color = Color.Gray, fontSize = 12.sp)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(item.lastMessage, color = Color.LightGray, fontSize = 14.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                }
            }
        }
    }
}

@Composable
fun ChatInputArea(inputText: String, onTextChange: (String) -> Unit, isWaiting: Boolean, onSend: () -> Unit) {
    Surface(modifier = Modifier.fillMaxWidth(), color = Color(0xFF1B263B), tonalElevation = 8.dp) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = inputText, onValueChange = onTextChange, modifier = Modifier.weight(1f).clip(RoundedCornerShape(24.dp)),
                placeholder = { Text("说点什么...", color = Color.Gray) }, enabled = !isWaiting,
                colors = TextFieldDefaults.colors(focusedContainerColor = Color(0xFF0D1B2A), unfocusedContainerColor = Color(0xFF0D1B2A), focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent, focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                maxLines = 4
            )
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(onClick = onSend, enabled = inputText.isNotBlank() && !isWaiting, modifier = Modifier.size(48.dp).background(brush = if (inputText.isNotBlank() && !isWaiting) Brush.linearGradient(colors = listOf(Color(0xFF00B4D8), Color(0xFF0077B6))) else Brush.linearGradient(colors = listOf(Color.Gray, Color.DarkGray)), shape = RoundedCornerShape(24.dp))) {
                Icon(Icons.Default.Send, contentDescription = null, tint = Color.White)
            }
        }
    }
}

@Composable
fun ChatBubble(message: ChatMessage) {
    val isUser = message.role == "user"
    val alignment = if (isUser) Alignment.CenterEnd else Alignment.CenterStart
    val bgColor = if (isUser) Color(0xFF0077B6) else Color(0xFF415A77)
    val shape = if (isUser) RoundedCornerShape(16.dp, 16.dp, 2.dp, 16.dp) else RoundedCornerShape(16.dp, 16.dp, 16.dp, 2.dp)
    Box(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), contentAlignment = alignment) {
        Surface(color = bgColor, shape = shape, tonalElevation = 2.dp) {
            Text(text = message.content, modifier = Modifier.padding(12.dp), color = Color.White, fontSize = 15.sp, lineHeight = 20.sp)
        }
    }
}
