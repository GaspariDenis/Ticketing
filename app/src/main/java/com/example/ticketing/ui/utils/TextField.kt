package com.example.ticketing.ui.utils

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ticketing.R
import com.example.ticketing.ui.theme.Indaco

@Composable
fun TextField(
    modifier: Modifier = Modifier,
    text :  String,
    labelText : String = "",
    labelColor: Color = Color(0x00000000),
    digitPassword : Boolean = false,
    singleLine : Boolean = true,
    maxLine : Int = 1,
    placeholder: @Composable () -> Unit,
    onChange : (String) -> Unit
) {
    var viewText by remember { mutableStateOf(false) }

    OutlinedTextField(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp),
        placeholder = placeholder,
        value = text,
        onValueChange = onChange,
        shape = RoundedCornerShape(16.dp),
        visualTransformation =
            if( digitPassword && !viewText)
                PasswordVisualTransformation()
            else
                VisualTransformation.None,
        singleLine = singleLine,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Indaco,
            unfocusedBorderColor = Color.Gray,
            unfocusedContainerColor = Color(0xff192032),
            focusedContainerColor = Color(0xff192032)
        ),
        label = {
            Text(
                modifier = Modifier,
                fontSize = 17.sp,
                fontWeight = FontWeight.SemiBold,
                color = labelColor,
                text = labelText,
                overflow = TextOverflow.Ellipsis,
                maxLines = maxLine
            )
        },
        trailingIcon = {
            if(digitPassword){
                IconButton(
                    modifier = Modifier
                        .padding(end = 16.dp)
                        .size(30.dp),
                    onClick = {viewText = !viewText}) {
                    Icon(
                        painter = if(viewText)
                            painterResource(R.drawable.visible)
                        else
                            painterResource(R.drawable.hidden),
                        contentDescription = null
                    )
                }
            }
        }
    )
}