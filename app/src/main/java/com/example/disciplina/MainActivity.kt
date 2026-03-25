package com.example.disciplina

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DisciplinaTheme {
                MainScreen()
            }
        }
    }
}

@Composable
fun DisciplinaTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = darkColorScheme(
            primary = Color(0xFF2196F3),
            background = Color(0xFF121212),
            surface = Color(0xFF1E1E1E)
        ),
        content = content
    )
}

// ── Data ──────────────────────────────────────────────────────────────────────

data class Reja(val id: Int, val nomi: String, var bajarildi: Boolean = false)
data class Odat(val id: Int, val nomi: String, var streak: Int = 0, var bugunBajarildi: Boolean = false)

// ── Main Screen ───────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val navController = rememberNavController()
    var userName by remember { mutableStateOf("") }
    var showNameDialog by remember { mutableStateOf(true) }

    // Shared state across screens
    val rejalar = remember { mutableStateListOf(
        Reja(1, "Kitob o'qish - 30 daqiqa"),
        Reja(2, "Sport qilish"),
        Reja(3, "Suv ichish - 2L")
    )}
    val odatlar = remember { mutableStateListOf(
        Odat(1, "Ertalab yugurish", streak = 5),
        Odat(2, "Meditatsiya", streak = 3),
        Odat(3, "Kun rejasi tuzish", streak = 7)
    )}

    if (showNameDialog) {
        AlertDialog(
            onDismissRequest = { },
            title = { Text("Xush kelibsiz!") },
            text = {
                Column {
                    Text("Ismingizni kiriting:")
                    Spacer(modifier = Modifier.height(8.dp))
                    TextField(
                        value = userName,
                        onValueChange = { userName = it },
                        singleLine = true,
                        placeholder = { Text("Ism...") }
                    )
                }
            },
            confirmButton = {
                Button(onClick = { if (userName.isNotBlank()) showNameDialog = false }) {
                    Text("Boshlash")
                }
            }
        )
    }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: "home"

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = "Bosh sahifa") },
                    label = { Text("Bosh sahifa") },
                    selected = currentRoute == "home",
                    onClick = { navController.navigate("home") { launchSingleTop = true } }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.List, contentDescription = "Rejalar") },
                    label = { Text("Rejalar") },
                    selected = currentRoute == "rejalar",
                    onClick = { navController.navigate("rejalar") { launchSingleTop = true } }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Add, contentDescription = "Qo'shish") },
                    label = { Text("Qo'shish") },
                    selected = currentRoute == "qoshish",
                    onClick = { navController.navigate("qoshish") { launchSingleTop = true } }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.CheckCircle, contentDescription = "Odatlar") },
                    label = { Text("Odatlar") },
                    selected = currentRoute == "odatlar",
                    onClick = { navController.navigate("odatlar") { launchSingleTop = true } }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Info, contentDescription = "Statistika") },
                    label = { Text("Statistika") },
                    selected = currentRoute == "statistika",
                    onClick = { navController.navigate("statistika") { launchSingleTop = true } }
                )
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(padding)
        ) {
            composable("home") { BoshSahifa(userName, rejalar, odatlar) }
            composable("rejalar") { RejalarEkrani(rejalar) }
            composable("qoshish") { QoshishEkrani(rejalar, odatlar) }
            composable("odatlar") { OdatlarEkrani(odatlar) }
            composable("statistika") { StatistikaEkrani(rejalar, odatlar) }
        }
    }
}

// ── Bosh Sahifa ───────────────────────────────────────────────────────────────

@Composable
fun BoshSahifa(
    userName: String,
    rejalar: List<Reja>,
    odatlar: List<Odat>
) {
    val bajarilgan = rejalar.count { it.bajarildi }
    val jami = rejalar.size
    val progress = if (jami > 0) bajarilgan.toFloat() / jami else 0f

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Salom, ${if (userName.isBlank()) "Do'stim" else userName}! 👋",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = "Bugun ham g'olib bo'lamiz!",
                fontSize = 14.sp,
                color = Color.Gray
            )
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Kunlik Maqsadlar", fontWeight = FontWeight.Bold, color = Color.White)
                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier.fillMaxWidth().height(10.dp),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = Color(0xFF2A2A2A)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "$bajarilgan / $jami bajarildi",
                        modifier = Modifier.align(Alignment.End),
                        color = Color.Gray,
                        fontSize = 12.sp
                    )
                }
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1A237E))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("💡 Bugungi Iqtibos", fontWeight = FontWeight.Bold, color = Color.White)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "\"Harakat qil, orzularingni ro'yobga chiqar.\"",
                        fontStyle = FontStyle.Italic,
                        color = Color(0xFFBBDEFB)
                    )
                }
            }
        }

        item {
            Text("Bugungi Rejalar", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 18.sp)
        }

        items(rejalar.take(3)) { reja ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        if (reja.bajarildi) Icons.Default.CheckCircle else Icons.Default.List,
                        contentDescription = null,
                        tint = if (reja.bajarildi) Color(0xFF4CAF50) else Color.Gray
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(reja.nomi, color = Color.White, modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

// ── Rejalar Ekrani ────────────────────────────────────────────────────────────

@Composable
fun RejalarEkrani(rejalar: MutableList<Reja>) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Text("📋 Rejalar", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.White)
        Spacer(modifier = Modifier.height(16.dp))

        if (rejalar.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Hali reja yo'q. Qo'shish tugmasini bosing!", color = Color.Gray)
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(rejalar) { reja ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = reja.bajarildi,
                                onCheckedChange = { checked ->
                                    val index = rejalar.indexOf(reja)
                                    if (index >= 0) rejalar[index] = reja.copy(bajarildi = checked)
                                }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                reja.nomi,
                                color = if (reja.bajarildi) Color.Gray else Color.White,
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(onClick = { rejalar.remove(reja) }) {
                                Icon(Icons.Default.Delete, contentDescription = "O'chirish", tint = Color(0xFFEF5350))
                            }
                        }
                    }
                }
            }
        }
    }
}

// ── Qo'shish Ekrani ───────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QoshishEkrani(rejalar: MutableList<Reja>, odatlar: MutableList<Odat>) {
    var rejaMatni by remember { mutableStateOf("") }
    var odatMatni by remember { mutableStateOf("") }
    var xabar by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("➕ Yangi qo'shish", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.White)

        if (xabar.isNotBlank()) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1B5E20)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(xabar, modifier = Modifier.padding(12.dp), color = Color.White)
            }
        }

        // Reja qo'shish
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("📋 Reja qo'shish", fontWeight = FontWeight.Bold, color = Color.White)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = rejaMatni,
                    onValueChange = { rejaMatni = it },
                    label = { Text("Reja nomi") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = {
                        if (rejaMatni.isNotBlank()) {
                            rejalar.add(Reja(System.currentTimeMillis().toInt(), rejaMatni))
                            xabar = "✅ Reja qo'shildi: $rejaMatni"
                            rejaMatni = ""
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Reja qo'shish")
                }
            }
        }

        // Odat qo'shish
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("🔄 Odat qo'shish", fontWeight = FontWeight.Bold, color = Color.White)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = odatMatni,
                    onValueChange = { odatMatni = it },
                    label = { Text("Odat nomi") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = {
                        if (odatMatni.isNotBlank()) {
                            odatlar.add(Odat(System.currentTimeMillis().toInt(), odatMatni))
                            xabar = "✅ Odat qo'shildi: $odatMatni"
                            odatMatni = ""
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF388E3C))
                ) {
                    Text("Odat qo'shish")
                }
            }
        }
    }
}

// ── Odatlar Ekrani ────────────────────────────────────────────────────────────

@Composable
fun OdatlarEkrani(odatlar: MutableList<Odat>) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Text("🔄 Odatlar", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.White)
        Spacer(modifier = Modifier.height(16.dp))

        if (odatlar.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Hali odat yo'q. Qo'shish tugmasini bosing!", color = Color.Gray)
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(odatlar) { odat ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (odat.bugunBajarildi) Color(0xFF1B5E20) else MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(odat.nomi, color = Color.White, fontWeight = FontWeight.Medium)
                                Text("🔥 ${odat.streak} kun streak", color = Color(0xFFFFB300), fontSize = 12.sp)
                            }
                            Button(
                                onClick = {
                                    val index = odatlar.indexOf(odat)
                                    if (index >= 0) {
                                        odatlar[index] = odat.copy(
                                            bugunBajarildi = !odat.bugunBajarildi,
                                            streak = if (!odat.bugunBajarildi) odat.streak + 1 else maxOf(0, odat.streak - 1)
                                        )
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (odat.bugunBajarildi) Color(0xFF4CAF50) else Color(0xFF2196F3)
                                )
                            ) {
                                Text(if (odat.bugunBajarildi) "✓ Bajarildi" else "Bajar")
                            }
                        }
                    }
                }
            }
        }
    }
}

// ── Statistika Ekrani ─────────────────────────────────────────────────────────

@Composable
fun StatistikaEkrani(rejalar: List<Reja>, odatlar: List<Odat>) {
    val bajarilganRejalar = rejalar.count { it.bajarildi }
    val bajarilganOdatlar = odatlar.count { it.bugunBajarildi }
    val maksimalStreak = odatlar.maxOfOrNull { it.streak } ?: 0

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text("📊 Statistika", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }

        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatCard("Rejalar", "$bajarilganRejalar/${rejalar.size}", Color(0xFF1565C0), Modifier.weight(1f))
                StatCard("Odatlar", "$bajarilganOdatlar/${odatlar.size}", Color(0xFF2E7D32), Modifier.weight(1f))
            }
        }

        item {
            StatCard("🔥 Eng uzun streak", "$maksimalStreak kun", Color(0xFFE65100), Modifier.fillMaxWidth())
        }

        item {
            Text("Odatlar bo'yicha", fontWeight = FontWeight.Bold, color = Color.White)
        }

        items(odatlar) { odat ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(odat.nomi, color = Color.White)
                        Text("Streak: ${odat.streak} kun", color = Color(0xFFFFB300), fontSize = 12.sp)
                    }
                    Icon(
                        if (odat.bugunBajarildi) Icons.Default.CheckCircle else Icons.Default.List,
                        contentDescription = null,
                        tint = if (odat.bugunBajarildi) Color(0xFF4CAF50) else Color.Gray
                    )
                }
            }
        }
    }
}

@Composable
fun StatCard(title: String, value: String, color: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = color)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(value, fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Text(title, fontSize = 12.sp, color = Color.White.copy(alpha = 0.8f))
        }
    }
}

