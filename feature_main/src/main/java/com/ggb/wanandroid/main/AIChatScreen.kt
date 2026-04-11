package com.ggb.wanandroid.main

import androidx.compose.animation.*
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
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
import com.mikepenz.markdown.m3.Markdown
import com.mikepenz.markdown.m3.markdownColor
import com.mikepenz.markdown.m3.markdownTypography

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


    // ✅ 修复：不仅判断 index，还判断在第 0 条内容里的滑动偏移量。
    // 这样只要稍微往上滑动了一点点（超过150像素），即使是很长的对话，也会立刻显示悬浮窗！
    val showScrollToBottom by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex > 0 || listState.firstVisibleItemScrollOffset > 150
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
                            reverseLayout = true,
                            modifier = Modifier.weight(1f).fillMaxWidth().padding(horizontal = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            contentPadding = PaddingValues(top = 16.dp, bottom = 16.dp)
                        ) {
                            if (isWaitingResponse) {
                                item(key = "loading_anim") { LoadingResponseItem() }
                            }

                            items(
                                items = displayMessages.reversed(),
                                // 注意：这里必须保证 it.id 是绝对唯一且不会变的！
                                // 如果你的 ChatMessage 没有唯一的 id，建议加上 val id: String = UUID.randomUUID().toString()
                                key = { it.id }
                            ) { message ->
                                // 【核心修复】：为每一个消息气泡包裹一层 Box，并加上 animateItem()
                                // 这会让高度的瞬间突变变成平滑的拉伸动画，彻底消除闪烁感！
                                Box(modifier = Modifier.animateItem()) {
                                    ChatBubble(message)
                                }
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
                                        // ✅ 修复1：发送消息后，强制瞬间回到底部 (index 0)
                                        // 坚决不用 animateScrollToItem，避免被键盘收起动画打断
                                        launch {
                                            try {
                                                listState.scrollToItem(0)
                                            } catch (e: Exception) {}
                                        }

                                        var aiMsgStarted = false
                                        var currentAiContent = "" // 用一个局部变量在内存中高速缓存累加的文本
                                        var lastUiUpdateTime = 0L // 记录上次刷新 UI 的时间

                                        volcNetTool.sendChatRequestStream(messages = messageList)
                                            .onCompletion {
                                                isWaitingResponse = false
                                                // 【兜底逻辑】：流结束后，把最后一点可能没满 100ms 门槛的文本强制推到屏幕上
                                                if (aiMsgStarted) {
                                                    val lastMsg = messageList.last()
                                                    if (lastMsg.content != currentAiContent) {
                                                        messageList = messageList.toMutableList().also {
                                                            it[it.lastIndex] = lastMsg.copy(content = currentAiContent)
                                                        }
                                                    }
                                                }
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
                                                    currentAiContent = chunk
                                                    messageList = messageList + ChatMessage(role = "assistant", content = currentAiContent)
                                                    aiMsgStarted = true
                                                    lastUiUpdateTime = System.currentTimeMillis()
                                                } else {
                                                    currentAiContent += chunk
                                                    val now = System.currentTimeMillis()

                                                    // ✅ 【核心修复】：性能节流！每 100 毫秒才允许更新一次真正的 UI
                                                    if (now - lastUiUpdateTime > 100) {
                                                        val lastMsg = messageList.last()
                                                        messageList = messageList.toMutableList().also {
                                                            it[it.lastIndex] = lastMsg.copy(content = currentAiContent)
                                                        }
                                                        lastUiUpdateTime = now
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
                            // ✅ 修复2：在流式生成中，抛弃平滑动画，使用瞬间滚动。
                            // 这样彻底杜绝了高度膨胀引发的 Compose 动画引擎闪烁 Bug！
                            onClick = { coroutineScope.launch { listState.scrollToItem(0) } },
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
    // 业界标准的 AI 思考动画：呼吸灯/渐变闪烁效果
    val infiniteTransition = rememberInfiniteTransition(label = "loading_pulse")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha_anim"
    )

    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.AutoAwesome,
            contentDescription = null,
            tint = Color(0xFF00B4D8).copy(alpha = alpha),
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "牛蛙呐正在思考...",
            color = Color(0xFF00B4D8).copy(alpha = alpha),
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
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
fun ChatInputArea(
    inputText: String,
    onTextChange: (String) -> Unit,
    isWaiting: Boolean,
    onSend: () -> Unit
) {
    // 使用 Surface 垫起底部，增加层次感
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color(0xFF1B263B), // 保持和你背景一致的深色，或者用 MaterialTheme.colorScheme.surface
        tonalElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .navigationBarsPadding() // 自动适配 Android 导航栏高度
                .imePadding(), // 自动适配软键盘弹出
            verticalAlignment = Alignment.Bottom // 设置为底部对齐，方便多行文本输入时按钮位置固定
        ) {
            // 自定义输入框背景容器
            Row(
                modifier = Modifier
                    .weight(1f)
                    .heightIn(min = 44.dp, max = 150.dp)
                    .clip(RoundedCornerShape(22.dp))
                    .background(Color(0xFF0D1B2A)) // 输入框深色背景
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BasicTextField(
                    value = inputText,
                    onValueChange = onTextChange,
                    modifier = Modifier.weight(1f),
                    enabled = !isWaiting, // 等待中禁用输入
                    textStyle = TextStyle(
                        fontSize = 16.sp,
                        color = Color.White,
                        lineHeight = 22.sp
                    ),
                    cursorBrush = SolidColor(Color(0xFF00B4D8)),
                    decorationBox = { innerTextField ->
                        if (inputText.isEmpty()) {
                            Text(
                                text = "说点什么...",
                                color = Color.Gray,
                                fontSize = 16.sp
                            )
                        }
                        innerTextField()
                    }
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // 重新设计的发送按钮
            // 当有文字且不在等待状态时，显示亮蓝色渐变，否则显示灰色
            val isEnabled = inputText.isNotBlank() && !isWaiting
            val buttonBgColor = if (isEnabled) {
                Brush.linearGradient(colors = listOf(Color(0xFF00B4D8), Color(0xFF0077B6)))
            } else {
                Brush.linearGradient(colors = listOf(Color.Gray, Color.DarkGray))
            }

            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(buttonBgColor)
                    .clickable(enabled = isEnabled) { onSend() },
                contentAlignment = Alignment.Center
            ) {
                if (isWaiting) {
                    // 发送中可以显示一个极小的白圈，或者保持发送图标变灰
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }
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

            // 核心修改：适配最新版本的 Markdown API
            Markdown(
                content = message.content,
                modifier = Modifier.padding(12.dp),

                // 1. Colors 现在只负责：最基础文字色、各种背景色、分割线
                colors = markdownColor(
                    text = Color.White,                             // 基础文字颜色为白色
                    codeBackground = Color(0xFF0D1B2A),             // 代码块的深色背景
                    inlineCodeBackground = Color(0xFF0D1B2A).copy(alpha = 0.5f), // 行内代码背景稍微浅一点
                    dividerColor = Color.LightGray                  // 分割线颜色
                ),

                // 2. Typography 现在负责：段落排版、代码高亮文字、超链接文字等细节
                // 2. Typography 负责排版和特定文本样式
                typography = markdownTypography(
                    text = LocalTextStyle.current.copy(fontSize = 15.sp, lineHeight = 20.sp, color = Color.White),
                    paragraph = LocalTextStyle.current.copy(fontSize = 15.sp, lineHeight = 20.sp, color = Color.White),
                    code = LocalTextStyle.current.copy(fontFamily = FontFamily.Monospace, color = Color(0xFF00B4D8)),
                    textLink = TextLinkStyles(style = SpanStyle(color = Color(0xFF90E0EF))),

                    // 【新增修复】：强制覆盖所有标题的大小，使其自适应气泡布局
                    h1 = LocalTextStyle.current.copy(fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White),
                    h2 = LocalTextStyle.current.copy(fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White),
                    h3 = LocalTextStyle.current.copy(fontSize = 17.sp, fontWeight = FontWeight.Bold, color = Color.White),
                    h4 = LocalTextStyle.current.copy(fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White),
                    h5 = LocalTextStyle.current.copy(fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White),
                    h6 = LocalTextStyle.current.copy(fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
                )
            )

        }
    }
}
