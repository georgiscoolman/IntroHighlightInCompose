@file:OptIn(ExperimentalComposeUiApi::class)

package com.example.introhighlightincompose

import android.os.Bundle
import android.view.MotionEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.introhighlightincompose.ui.theme.IntroHighlightInComposeTheme

private const val cornerRadius = 50f

class MainActivity : ComponentActivity() {

    companion object {
        private val mockContent = listOf("One", "Two", "Three", "Four")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            IntroHighlightInComposeTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {

                    var isShowAppIntro by remember {
                        mutableStateOf(true)
                    }

                    val targets = remember {
                        mutableStateMapOf<Int, LayoutCoordinates>()
                    }

                    Box {
                        LazyColumn {
                            item {
                                LazyRow(
                                    modifier = Modifier.padding(vertical = 16.dp),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                    contentPadding = PaddingValues(horizontal = 12.dp)
                                ) {

                                    itemsIndexed(mockContent) { index, text ->
                                        Chip(
                                            modifier = Modifier
                                                .onGloballyPositioned { coordinates ->
                                                    targets[index] = coordinates
                                                },
                                            text = text
                                        ) {
                                            // do nothing
                                        }
                                    }
                                }
                            }

                            item {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 50.dp),
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Chip(text = "Turn On Intro mode") {
                                        isShowAppIntro = true
                                    }
                                }
                            }
                        }

                        if (isShowAppIntro) {
                            Overlay(
                                targets = targets,
                                onTouchDown = {
                                    isShowAppIntro = false
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun Chip(
    modifier: Modifier = Modifier,
    text: String,
    onClick: () -> Unit,
) {
    Text(
        modifier = modifier
            .clip(RoundedCornerShape(cornerRadius))
            .background(
                color = Color.LightGray
            )
            .clickable {
                onClick()
            }
            .padding(vertical = 6.dp, horizontal = 22.dp),
        text = text,
        style = MaterialTheme.typography.labelLarge,
        textAlign = TextAlign.Center,
    )
}

@Composable
private fun Overlay(
    modifier: Modifier = Modifier,
    targets: Map<Int, LayoutCoordinates>,
    onTouchDown: () -> Unit,
) {
    Canvas(
        modifier = modifier
            .fillMaxSize()
            .pointerInteropFilter {
                when (it.action) {
                    MotionEvent.ACTION_DOWN -> {
                        onTouchDown()
                        true
                    }

                    else -> false
                }
            }
            .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen) // fix of BlendMode.Clear on Compose https://stackoverflow.com/questions/64939726/jetpack-compose-canvas-blendmode-src-in-makes-even-background-transparent
    ) {
        drawRect(color = Color.Black.copy(alpha = 0.8f), size = size)

        targets.forEach { (_, coordinates) ->
            val targetRect = coordinates.boundsInRoot()
            val clearSize = targetRect.size
            val cornerRadius = CornerRadius(cornerRadius)
            val topLeft = targetRect.topLeft
            drawRoundRect(
                color = Color.Transparent,
                topLeft = topLeft,
                size = clearSize,
                blendMode = BlendMode.Clear,
                cornerRadius = cornerRadius,
            )
        }
    }
}