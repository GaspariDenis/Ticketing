package com.example.ticketing.ui.utils

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.ticketing.vo.PriorityTag

@Composable
fun PriorityTagCard(
    modifier: Modifier = Modifier,
    tag : PriorityTag,
    isClicked : Boolean = false,
    onClick : (PriorityTag) -> Unit = {}
) {

    val clickedColor = Color(0xffb585f2)
    val clickedContainerColor = Color(0x87B585F2)
    val clickedBorderColor = Color(0xCCB585F2)

    val color = when(tag) {
        PriorityTag.High -> Color(0xffd8504b)
        PriorityTag.Medium -> Color(0xFFE9a23B)
        PriorityTag.Low -> Color(0xff5d6584)
    }

    val text = when(tag) {
        PriorityTag.High -> "HIGH"
        PriorityTag.Medium -> "MEDIUM"
        PriorityTag.Low -> "LOW"
    }

    val containerColor = when(tag)  {
        PriorityTag.High -> Color(0x88D8504B)
        PriorityTag.Medium -> Color(0x87E9A23B)
        PriorityTag.Low -> Color(0x875D6584)
    }
    Card(
        onClick = {
            onClick(tag)
        },
        modifier = modifier.width(80.dp),
        colors = if(isClicked){
            CardDefaults.cardColors(
                containerColor = clickedContainerColor,
            )
        }else{
            CardDefaults.cardColors(
                containerColor = containerColor,
            )
        },
        border = if(isClicked)
            BorderStroke(1.dp, clickedBorderColor)
        else
            BorderStroke(0.dp, clickedBorderColor)
    ){
        Text(
            modifier = Modifier
                .padding(start = 4.dp, end = 4.dp, top = 2.dp, bottom = 2.dp)
                .fillMaxWidth(),
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
            color = if(isClicked)
                        clickedColor
                    else
                        color,
            text = text,
        )
    }
}