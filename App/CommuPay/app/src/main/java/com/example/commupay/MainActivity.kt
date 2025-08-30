package com.example.commupay

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Data Classes
data class Transaction(
    val id: Int,
    val date: String,
    val description: String,
    val amount: Double,
    val isPositive: Boolean
)

data class User(
    val id: Int,
    val name: String,
    val email: String,
    val balance: Double,
    val btcBalance: Double
)

// Database Helper
class CommuPayDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "commupay.db"
        const val DATABASE_VERSION = 1

        // Users table
        const val TABLE_USERS = "users"
        const val COLUMN_USER_ID = "id"
        const val COLUMN_USER_NAME = "name"
        const val COLUMN_USER_EMAIL = "email"
        const val COLUMN_USER_BALANCE = "balance"
        const val COLUMN_USER_BTC_BALANCE = "btc_balance"

        // Transactions table
        const val TABLE_TRANSACTIONS = "transactions"
        const val COLUMN_TRANSACTION_ID = "id"
        const val COLUMN_TRANSACTION_USER_ID = "user_id"
        const val COLUMN_TRANSACTION_DATE = "date"
        const val COLUMN_TRANSACTION_DESCRIPTION = "description"
        const val COLUMN_TRANSACTION_AMOUNT = "amount"
        const val COLUMN_TRANSACTION_IS_POSITIVE = "is_positive"
    }

    override fun onCreate(db: SQLiteDatabase) {
        // Create users table
        val createUsersTable = """
            CREATE TABLE $TABLE_USERS (
                $COLUMN_USER_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_USER_NAME TEXT NOT NULL,
                $COLUMN_USER_EMAIL TEXT UNIQUE NOT NULL,
                $COLUMN_USER_BALANCE REAL DEFAULT 0,
                $COLUMN_USER_BTC_BALANCE REAL DEFAULT 0
            )
        """.trimIndent()

        // Create transactions table
        val createTransactionsTable = """
            CREATE TABLE $TABLE_TRANSACTIONS (
                $COLUMN_TRANSACTION_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_TRANSACTION_USER_ID INTEGER,
                $COLUMN_TRANSACTION_DATE TEXT NOT NULL,
                $COLUMN_TRANSACTION_DESCRIPTION TEXT NOT NULL,
                $COLUMN_TRANSACTION_AMOUNT REAL NOT NULL,
                $COLUMN_TRANSACTION_IS_POSITIVE INTEGER NOT NULL,
                FOREIGN KEY($COLUMN_TRANSACTION_USER_ID) REFERENCES $TABLE_USERS($COLUMN_USER_ID)
            )
        """.trimIndent()

        db.execSQL(createUsersTable)
        db.execSQL(createTransactionsTable)

        // Insert sample data
        insertSampleData(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_TRANSACTIONS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        onCreate(db)
    }

    private fun insertSampleData(db: SQLiteDatabase) {
        // Insert sample user
        val userValues = ContentValues().apply {
            put(COLUMN_USER_NAME, "Sophia Carter")
            put(COLUMN_USER_EMAIL, "sophia.carter@email.com")
            put(COLUMN_USER_BALANCE, 1250.00)
            put(COLUMN_USER_BTC_BALANCE, 0.034)
        }
        val userId = db.insert(TABLE_USERS, null, userValues)

        // Insert sample transactions
        val transactions = listOf(
            Triple("2024-03-15", "Meeting Attendance Incentive", 50.00),
            Triple("2024-03-10", "Referral Bonus", 100.00),
            Triple("2024-03-05", "Meeting Attendance Incentive", 50.00)
        )

        transactions.forEach { (date, description, amount) ->
            val transactionValues = ContentValues().apply {
                put(COLUMN_TRANSACTION_USER_ID, userId)
                put(COLUMN_TRANSACTION_DATE, date)
                put(COLUMN_TRANSACTION_DESCRIPTION, description)
                put(COLUMN_TRANSACTION_AMOUNT, amount)
                put(COLUMN_TRANSACTION_IS_POSITIVE, 1)
            }
            db.insert(TABLE_TRANSACTIONS, null, transactionValues)
        }
    }

    fun getUser(email: String): User? {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_USERS,
            null,
            "$COLUMN_USER_EMAIL = ?",
            arrayOf(email),
            null,
            null,
            null
        )

        return if (cursor.moveToFirst()) {
            val user = User(
                id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_USER_ID)),
                name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_NAME)),
                email = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_EMAIL)),
                balance = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_USER_BALANCE)),
                btcBalance = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_USER_BTC_BALANCE))
            )
            cursor.close()
            user
        } else {
            cursor.close()
            null
        }
    }

    fun getUserTransactions(userId: Int): List<Transaction> {
        val transactions = mutableListOf<Transaction>()
        val db = readableDatabase
        val cursor = db.query(
            TABLE_TRANSACTIONS,
            null,
            "$COLUMN_TRANSACTION_USER_ID = ?",
            arrayOf(userId.toString()),
            null,
            null,
            "$COLUMN_TRANSACTION_DATE DESC"
        )

        while (cursor.moveToNext()) {
            transactions.add(
                Transaction(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_TRANSACTION_ID)),
                    date = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TRANSACTION_DATE)),
                    description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TRANSACTION_DESCRIPTION)),
                    amount = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_TRANSACTION_AMOUNT)),
                    isPositive = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_TRANSACTION_IS_POSITIVE)) == 1
                )
            )
        }
        cursor.close()
        return transactions
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CommuPayTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    CommuPayApp(innerPadding)
                }
            }
        }
    }
}

@Composable
fun CommuPayApp(paddingValues: PaddingValues) {
    var isLoggedIn by remember { mutableStateOf(false) }
    var currentUser by remember { mutableStateOf<User?>(null) }
    val context = LocalContext.current
    val dbHelper = remember { CommuPayDatabaseHelper(context) }

    if (isLoggedIn && currentUser != null) {
        DashboardScreen(
            user = currentUser!!,
            dbHelper = dbHelper,
            paddingValues = paddingValues,
            onLogout = {
                isLoggedIn = false
                currentUser = null
            }
        )
    } else {
        LoginScreen(
            paddingValues = paddingValues,
            onLoginSuccess = { email ->
                val user = dbHelper.getUser(email)
                if (user != null) {
                    currentUser = user
                    isLoggedIn = true
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    paddingValues: PaddingValues = PaddingValues(),
    onLoginSuccess: (String) -> Unit = {}
) {
    var email by remember { mutableStateOf("sophia.carter@email.com") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1A1D29))
            .padding(paddingValues)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header con flecha de regreso
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, bottom = 40.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = "Welcome back",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.height(60.dp))

        // Campo de Email
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = {
                Text(
                    "Email",
                    color = Color(0xFF6B7280)
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF374151),
                unfocusedBorderColor = Color(0xFF374151),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                cursorColor = Color.White,
                focusedContainerColor = Color(0xFF374151),
                unfocusedContainerColor = Color(0xFF374151)
            ),
            shape = RoundedCornerShape(12.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Campo de Password
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = {
                Text(
                    "Password",
                    color = Color(0xFF6B7280)
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = if (passwordVisible) "Hide password" else "Show password",
                        tint = Color(0xFF6B7280)
                    )
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF374151),
                unfocusedBorderColor = Color(0xFF374151),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                cursorColor = Color.White,
                focusedContainerColor = Color(0xFF374151),
                unfocusedContainerColor = Color(0xFF374151)
            ),
            shape = RoundedCornerShape(12.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )

        // Forgot password
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(onClick = { /* Handle forgot password */ }) {
                Text(
                    "Forgot password?",
                    color = Color(0xFF60A5FA),
                    fontSize = 14.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Botón Log in
        Button(
            onClick = {
                // Simulamos login exitoso con el email
                if (email.isNotEmpty()) {
                    onLoginSuccess(email)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF3B82F6)
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                "Log in",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Divider "Or continue with"
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Divider(
                modifier = Modifier.weight(1f),
                color = Color(0xFF374151),
                thickness = 1.dp
            )
            Text(
                text = "Or continue with",
                color = Color(0xFF6B7280),
                fontSize = 14.sp,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Divider(
                modifier = Modifier.weight(1f),
                color = Color(0xFF374151),
                thickness = 1.dp
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Botón Continue with Passkey
        OutlinedButton(
            onClick = { onLoginSuccess(email) },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = Color.Transparent,
                contentColor = Color.White
            ),
            border = BorderStroke(1.dp, Brush.horizontalGradient(listOf(Color.Red, Color.Blue))),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Fingerprint,
                contentDescription = "Passkey",
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                "Continue with Passkey",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Botón Continue with Magic Link
        OutlinedButton(
            onClick = { onLoginSuccess(email) },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = Color.Transparent,
                contentColor = Color.White
            ),
            border = BorderStroke(1.dp, Brush.horizontalGradient(listOf(Color.Red, Color.Blue))),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Mail,
                contentDescription = "Magic Link",
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                "Continue with Magic Link",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // Sign up link
        Row(
            modifier = Modifier.padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                "Don't have an account? ",
                color = Color(0xFF6B7280),
                fontSize = 14.sp
            )
            TextButton(
                onClick = { /* Handle sign up */ },
                contentPadding = PaddingValues(0.dp)
            ) {
                Text(
                    "Sign up",
                    color = Color(0xFF60A5FA),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // Bottom Navigation Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF1A1D29))
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomNavItem(
                icon = Icons.Default.AccountBalanceWallet,
                label = "Wallet",
                isSelected = false
            )
            BottomNavItem(
                icon = Icons.Default.History,
                label = "Activity",
                isSelected = false
            )
            BottomNavItem(
                icon = Icons.Default.SwapHoriz,
                label = "Swap",
                isSelected = false
            )
            BottomNavItem(
                icon = Icons.Default.CollectionsBookmark,
                label = "NFTs",
                isSelected = false
            )
            BottomNavItem(
                icon = Icons.Default.Settings,
                label = "Settings",
                isSelected = false
            )
        }
    }
}

@Composable
fun DashboardScreen(
    user: User,
    dbHelper: CommuPayDatabaseHelper,
    paddingValues: PaddingValues,
    onLogout: () -> Unit
) {
    val transactions by remember { mutableStateOf(dbHelper.getUserTransactions(user.id)) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1A1D29))
            .padding(paddingValues)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Dashboard,
                    contentDescription = "Attendify",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "Attendify",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Row {
                Text("Dashboard", color = Color(0xFF6B7280), fontSize = 14.sp)
                Spacer(modifier = Modifier.width(16.dp))
                Text("Meetings", color = Color(0xFF6B7280), fontSize = 14.sp)
                Spacer(modifier = Modifier.width(16.dp))
                Text("Incentives", color = Color(0xFF6B7280), fontSize = 14.sp)
                Spacer(modifier = Modifier.width(16.dp))
                Text("Support", color = Color(0xFF6B7280), fontSize = 14.sp)
                Spacer(modifier = Modifier.width(16.dp))
                IconButton(onClick = onLogout) {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = "Profile",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        }

        // Main Content
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Left Panel - User Profile
            Card(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF2D3748)
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "User Profile & Wallet",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 32.dp)
                    )

                    // Profile Image (usando un placeholder)
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF4A5568)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Profile",
                            tint = Color.White,
                            modifier = Modifier.size(60.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        user.name,
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        user.email,
                        color = Color(0xFF6B7280),
                        fontSize = 14.sp
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    Button(
                        onClick = { /* Handle account settings */ },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF4A5568)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Account Settings",
                            color = Color.White,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            // Right Panel - Wallet & Transactions
            Column(
                modifier = Modifier.weight(2f)
            ) {
                // Crypto Wallet Summary
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF2D3748)
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp)
                    ) {
                        Text(
                            "Crypto Wallet Summary",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        Text(
                            "Current Balance",
                            color = Color(0xFF6B7280),
                            fontSize = 14.sp
                        )

                        Text(
                            "$${String.format("%.2f", user.balance)}",
                            color = Color.White,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            "≈ ${String.format("%.3f", user.btcBalance)} BTC",
                            color = Color(0xFF6B7280),
                            fontSize = 14.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Recent Transactions
                Card(
                    modifier = Modifier.fillMaxSize(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF2D3748)
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp)
                    ) {
                        Text(
                            "Recent Transactions",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        // Transaction Headers
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                "DATE",
                                color = Color(0xFF6B7280),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                "DESCRIPTION",
                                color = Color(0xFF6B7280),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.weight(2f)
                            )
                            Text(
                                "AMOUNT",
                                color = Color(0xFF6B7280),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                textAlign = TextAlign.End,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Divider(
                            color = Color(0xFF4A5568),
                            thickness = 1.dp,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        // Transaction List
                        LazyColumn {
                            items(transactions) { transaction ->
                                TransactionItem(transaction = transaction)
                            }
                        }

                        Spacer(modifier = Modifier.weight(1f))

                        // Meeting Attendance Overview
                        Divider(
                            color = Color(0xFF4A5568),
                            thickness = 1.dp,
                            modifier = Modifier.padding(vertical = 16.dp)
                        )

                        Text(
                            "Meeting Attendance Overview",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "You have attended 15 meetings this month.",
                                color = Color.White,
                                fontSize = 14.sp
                            )

                            TextButton(onClick = { /* Handle view full history */ }) {
                                Text(
                                    "View full history →",
                                    color = Color(0xFF60A5FA),
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TransactionItem(transaction: Transaction) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            transaction.date,
            color = Color.White,
            fontSize = 14.sp,
            modifier = Modifier.weight(1f)
        )

        Text(
            transaction.description,
            color = Color.White,
            fontSize = 14.sp,
            modifier = Modifier.weight(2f)
        )

        Text(
            if (transaction.isPositive) "+$${String.format("%.2f", transaction.amount)}" else "-$${String.format("%.2f", transaction.amount)}",
            color = if (transaction.isPositive) Color(0xFF10B981) else Color(0xFFEF4444),
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.End,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun BottomNavItem(
    icon: ImageVector,
    label: String,
    isSelected: Boolean
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (isSelected) Color(0xFF3B82F6) else Color(0xFF6B7280),
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = label,
            color = if (isSelected) Color(0xFF3B82F6) else Color(0xFF6B7280),
            fontSize = 12.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun CommuPayTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = darkColorScheme(
            primary = Color(0xFF3B82F6),
            background = Color(0xFF1A1D29),
            surface = Color(0xFF374151)
        ),
        content = content
    )
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    CommuPayTheme {
        CommuPayApp(PaddingValues())
    }
}