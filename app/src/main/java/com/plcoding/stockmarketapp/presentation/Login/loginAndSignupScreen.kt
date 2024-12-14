
package com.plcoding.stockmarketapp.presentation.Login

import android.annotation.SuppressLint
import android.content.Context
import android.content.IntentSender
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Security
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.auth.api.identity.Identity
import com.plcoding.stockmarketapp.R
import com.plcoding.stockmarketapp.presentation.Main_Screen.BottomNavigationBar
import com.plcoding.stockmarketapp.presentation.Main_Screen.FloatingTitle
import com.plcoding.stockmarketapp.presentation.trading_analysis.TradingAnalysisViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.launch

@SuppressLint("StateFlowValueCalledInComposition")
@Destination
@Composable
fun LoginAndSignUpScreen(
    navigator: DestinationsNavigator,
    viewModel: AuthViewModel = hiltViewModel(),
    analysis: TradingAnalysisViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        viewModel.handleGoogleSignInResult(result.data)
    }



    val total by analysis.total.collectAsState(initial = 0.0)


    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = { FloatingTitle() },
        bottomBar = { BottomNavigationBar(navigator = navigator) }
    ) { innerPadding ->
        // 使用 Column 代替 LazyColumn，避免嵌套可滚动组件
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (state.isLoggedIn) {
                // 登录后导航到成功页面
                SuccessfulPage(navigator, onLogOut = { viewModel.logout() }, name = state.username, total= total?.toDouble()
                    ?: 0.0)
            } else {
                // 显示“使用 Google 登录”按钮
                GoogleSignInButton(
                    onClick = {
                        coroutineScope.launch {
                            val intentSender = viewModel.googleSignIn()
                            intentSender?.let {
                                googleSignInLauncher.launch(
                                    IntentSenderRequest.Builder(it).build()
                                )
                            }
                        }
                    }
                )

                // 如果有错误消息，则显示
                if (state.errorMessage != null) {
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = state.errorMessage!!,
                        color = MaterialTheme.colors.error,
                        style = MaterialTheme.typography.body2
                    )
                }
            }
        }
    }
}

@Composable
fun GoogleSignInButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp),
        colors = ButtonDefaults.buttonColors(
            backgroundColor = MaterialTheme.colors.surface,
            contentColor = MaterialTheme.colors.onSurface
        ),
        border = BorderStroke(1.dp, MaterialTheme.colors.onSurface.copy(alpha = 0.5f)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            // 使用自定义 drawable 资源作为图标
            Icon(
                painter = painterResource(id = R.drawable.google_logo_icon), // 替换为您的图标资源ID
                contentDescription = "Google Icon",
                modifier = Modifier.size(28.dp),
                tint = Color.Unspecified // 如果图标已经包含颜色信息，可以设置为 Color.Unspecified
            )

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = "Login with Google",
                style = MaterialTheme.typography.button.copy(
                    fontWeight = FontWeight.Medium,
                    fontStyle = FontStyle.Italic,
                    fontSize = 18.sp,
                    color = MaterialTheme.colors.onSurface
                )
            )
        }
    }
}

@Composable
fun SuccessfulPage(
    navigator: DestinationsNavigator,
    onLogOut: () -> Unit = {},
    name: String,
    total: Double
) {
    Column(
        modifier = Modifier
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 购买力卡片
        BuyingPowerCard(name,total)

        Spacer(modifier = Modifier.height(40.dp)) // 调整空白高度

        // 设置部分
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            val settings = listOf(
                "Account Profile" to Icons.Default.AccountCircle,
                "Security" to Icons.Default.Security,
                "FAQ" to Icons.Default.HelpOutline,
                "Live Support" to Icons.Default.Chat
            )
            settings.forEach { (label, icon) ->
                SettingItem(
                    label = label,
                    icon = icon,
                    onClick = { /* 处理导航 */ }
                )
            }

            // 登出按钮
            SettingItem(
                label = "Log Out",
                icon = Icons.Default.ExitToApp,
                onClick = { onLogOut() },
                textColor = MaterialTheme.colors.error // 使用错误颜色（通常为红色）
            )
        }
    }
}

@Composable
fun BuyingPowerCard(name: String, total :Double) {
    Card(
        shape = RoundedCornerShape(12.dp),
        backgroundColor = MaterialTheme.colors.primary.copy(alpha = 0.1f), // 使用主题主色的浅色变体
        elevation = 4.dp,
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Total Net Worth",
                style = MaterialTheme.typography.h6.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colors.onSurface
                )
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "$${"%.2f".format(total)}",
                style = MaterialTheme.typography.h4.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colors.onSurface
                )
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.body2.copy(
                        color = MaterialTheme.colors.onSurface
                    ),
                    textAlign = TextAlign.Start
                )

            }
        }
    }
}

@Composable
fun SettingItem(
    label: String,
    icon: ImageVector,
    onClick: () -> Unit,
    textColor: Color = MaterialTheme.colors.onSurface
) {
    val isDarkTheme = isSystemInDarkTheme()
    val backgroundColor = if (isDarkTheme) Color.Black else Color.White

    Card(
        shape = RoundedCornerShape(8.dp),
        elevation = 4.dp,
        backgroundColor = backgroundColor,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = MaterialTheme.colors.primary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.body1.copy(
                    fontWeight = FontWeight.Medium,
                    color = textColor
                )
            )
        }
    }
}
