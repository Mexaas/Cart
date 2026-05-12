package com.example.shopgame

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlin.random.Random

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

            MaterialTheme {

                var screen by remember {
                    mutableStateOf("cart")
                }

                Surface {

                    if (screen == "cart") {
                        CartScreen {
                            screen = "game"
                        }
                    } else {
                        GameScreen()
                    }
                }
            }
        }
    }
}

data class Product(
    val id: Int,
    val name: String,
    val price: Int
)

data class CartItem(
    val product: Product,
    var count: Int
)

@Composable
fun CartScreen(
    onStartGame: () -> Unit
) {

    val cartItems = remember {

        mutableStateListOf(

            CartItem(
                Product(1, "Apple", 100),
                1
            ),

            CartItem(
                Product(2, "Banana", 200),
                2
            ),

            CartItem(
                Product(3, "Milk", 300),
                1
            )
        )
    }

    val total = cartItems.sumOf {
        it.product.price * it.count
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text(
            text = "Cart",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "€$total",
            style = MaterialTheme.typography.headlineLarge
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier.weight(1f)
        ) {

            items(cartItems) { item ->

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                ) {

                    Row(
                        modifier = Modifier
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Box(
                            modifier = Modifier
                                .size(60.dp)
                                .background(Color.LightGray)
                        )

                        Spacer(modifier = Modifier.width(16.dp))

                        Column(
                            modifier = Modifier.weight(1f)
                        ) {

                            Text(item.product.name)

                            Text("€${item.product.price}")

                            Text("Description")
                        }

                        IconButton(
                            onClick = {
                                if (item.count > 0) {
                                    item.count--
                                }
                            }
                        ) {

                            Icon(
                                Icons.Default.Remove,
                                contentDescription = null
                            )
                        }

                        Text("${item.count}")

                        IconButton(
                            onClick = {
                                item.count++
                            }
                        ) {

                            Icon(
                                Icons.Default.Add,
                                contentDescription = null
                            )
                        }
                    }
                }
            }
        }

        Button(
            onClick = onStartGame,
            modifier = Modifier.fillMaxWidth()
        ) {

            Text("Start Game")
        }
    }
}

data class FallingItem(
    var x: Float,
    var y: Float
)

@Composable
fun GameScreen() {

    val items = remember {
        mutableStateListOf<FallingItem>()
    }

    var basketX by remember {
        mutableFloatStateOf(300f)
    }

    var score by remember {
        mutableIntStateOf(0)
    }

    var time by remember {
        mutableIntStateOf(30)
    }

    LaunchedEffect(Unit) {

        while (true) {

            delay(500)

            items.add(
                FallingItem(
                    x = Random.nextInt(50, 900).toFloat(),
                    y = 0f
                )
            )
        }
    }

    LaunchedEffect(Unit) {

        while (time > 0) {

            delay(1000)

            time--
        }
    }

    LaunchedEffect(Unit) {

        while (true) {

            items.forEach {

                it.y += 10f
            }

            val iterator = items.iterator()

            while (iterator.hasNext()) {

                val item = iterator.next()

                val hit =
                    item.y > 1400 &&
                    item.x in basketX..(basketX + 200)

                if (hit) {

                    score++

                    iterator.remove()
                }

                if (item.y > 1700) {
                    iterator.remove()
                }
            }

            delay(16)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFEAEAEA))
    ) {

        Text(
            text = "Time: $time",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
        )

        Text(
            text = "Score: $score",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
        )

        items.forEach { item ->

            Box(
                modifier = Modifier
                    .offset(
                        x = item.x.dp,
                        y = item.y.dp
                    )
                    .size(40.dp)
                    .background(Color.Red)
            )
        }

        Box(
            modifier = Modifier
                .offset(
                    x = basketX.dp,
                    y = 700.dp
                )
                .size(
                    width = 140.dp,
                    height = 40.dp
                )
                .background(Color.Black)
                .pointerInput(Unit) {

                    detectDragGestures { _, dragAmount ->

                        basketX += dragAmount.x
                    }
                }
        )
    }
}
