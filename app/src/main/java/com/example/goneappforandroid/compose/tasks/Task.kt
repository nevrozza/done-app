@file:OptIn(
    ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class,
    ExperimentalTextApi::class, ExperimentalFoundationApi::class
)

package com.example.goneappforandroid.compose.tasks

import android.content.Context
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.*
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.goneappforandroid.TasksViewModel
import java.util.*


//Date().minutes - minutes
//Date().hours - hours
//Date().date - day
//Date().month - month
//Date().year - year


@Composable
fun Task(
    text: String? = null,
    id: Int,
    minute: Int = 0,
    hour: Int = 0,
    day: Int = 0,
    checked: Boolean = false,
    tasksViewModel: TasksViewModel,
    currentState: MutableState<MutableTransitionState<Boolean>>,
) {
    val mLocal = LocalContext.current
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }
    val checked = remember { mutableStateOf(checked) }
    val editing = remember { mutableStateOf(false) }
    var value = remember {
        mutableStateOf(
            TextFieldValue(
                text ?: " ",
                selection = TextRange((text ?: " ").length)
            )
        )
    }
    val imm = mLocal.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager



    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp, horizontal = 10.dp)
    ) {

        CustomCheckBox(
            modifier = Modifier
                .padding(top = 13.dp, start = 10.dp, end = 10.dp)
                .size(20.dp),
            checked = checked.value,
            editing = editing.value,
            onCheckedChange = {
                if (text != null && !editing.value) {
                    checked.value = !checked.value
                    tasksViewModel.checkTask(id, checked.value)
                    currentState.value = MutableTransitionState(false)
                }
            }
        )


        var pressed by remember { mutableStateOf(false) }
        var doned by remember { mutableStateOf(false) }
        var expanded = remember { mutableStateOf(false) }
        var offsetX by remember { mutableStateOf(0.dp) }
        LaunchedEffect(editing.value) {
            if (editing.value) {
                focusRequester.requestFocus()
                expanded.value = false
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
            }
        }
        BackHandler(enabled = editing.value) {
            focusManager.clearFocus()
        }

        Box(modifier = Modifier
            .fillMaxWidth()
            .padding(top = 13.dp)
            .scale(
                animateFloatAsState(
                    targetValue = if (pressed) 0.99f else 1f,
                    tween(150)
                ).value
            )
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        pressed = true
                        tryAwaitRelease()
                        pressed = false
                    },
                    onLongPress = {
                        offsetX = it.x.toDp()
                        expanded.value = true
                    }
                )
            }
        ) {
            Column {
                if (text == null || editing.value) {
                    Box() {
                        BasicTextField(
                            modifier = Modifier
                                .fillMaxWidth()
                                .focusRequester(focusRequester = focusRequester)
                                .onFocusChanged {
                                    if (!expanded.value) {
                                        if (!it.isFocused) {
                                            editing.value = false
                                            if(!doned){
                                                value.value = TextFieldValue(
                                                    text ?: " ",
                                                    selection = TextRange((text ?: " ").length)
                                                )
                                            }
                                            doned = false
                                        }
                                    }
                                },
                            value = value.value,
                            onValueChange = { value.value = it },

                            textStyle = TextStyle(fontSize = 16.sp),
                            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                            keyboardOptions = KeyboardOptions(
                                imeAction = if (value.value.text != " ".repeat(
                                        value.value.text.length
                                    )
                                ) ImeAction.Done else ImeAction.Send
                            ),
                            keyboardActions = KeyboardActions(
                                onSend = {
                                    Toast.makeText(
                                        mLocal,
                                        "Cannot be empty",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                },
                                onDone = {
                                    doned = true
                                    if (editing.value) {
                                        tasksViewModel.textTask(id, value.value.text)
                                    } else {
                                        onDone(
                                            tasksViewModel = tasksViewModel,
                                            value = value,
                                        )
                                    }
                                    focusManager.clearFocus()
                                }
                            )
                        )

                        if (value.value.text == " ") {

                            Text(
                                " New task...",
                                modifier = Modifier.alpha(.5f)
                            )
                        }
                    }

                } else {
                    val durationDay =
                        day - (((Date().year - 1) * 365) + ((Date().month - 1) * 30) + (Date().date))
                    val hours = hour + durationDay * 24
                    val durationHours = hours - Date().hours
                    val durationMinutes = minute - Date().minutes
                    var duration = "$durationHours h"
                    if (durationHours <= 0) {
                        duration = "$durationMinutes m"
                    }
                    Text(text = buildAnnotatedString {
                        withStyle(
                            style = SpanStyle(
                                textDecoration = if (checked.value) TextDecoration.LineThrough else null,
                                brush = SolidColor(MaterialTheme.colorScheme.onSurfaceVariant),
                                alpha = if (checked.value) 0.5f else 1f
                            )
                        ) {
                            append(value.value.text)
                        }
                        withStyle(
                            style = SpanStyle(
                                textDecoration = if (checked.value) TextDecoration.LineThrough else null,
                                brush = SolidColor(MaterialTheme.colorScheme.onSurfaceVariant),
                                alpha = 0.5f
                            )
                        ) {
                            append(" · $duration")
                        }
                    })
                }

                CustomDropDownMenu(
                    checked = checked.value,
                    expanded = expanded,
                    delete = { tasksViewModel.deleteTask(id) },
                    edit = {
                        editing.value = true

                    },
                    offsetX = offsetX
                )

            }


        }
    }


}

private fun onDone(
    value: MutableState<TextFieldValue>,
    tasksViewModel: TasksViewModel,
) {
    val newDay =
        ((Date().year - 1) * 365) + ((Date().month - 1) * 30) + (Date().date + 1)
    tasksViewModel.insertTask(
        value.value.text,
        minute = Date().minutes,
        hour = Date().hours,
        day = newDay,
        checked = false
    )
}