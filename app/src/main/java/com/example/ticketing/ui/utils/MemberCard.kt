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
import com.example.ticketing.vo.UserTag

@Composable
fun MemberTag(tag : UserTag) {

    val color = when(tag) {
        UserTag.owner -> Color(0xff50ab7e)
        UserTag.member -> Color(0xff4773d6)
        UserTag.viewer -> Color(0xffaa00ff)
    }

    val text = when(tag){
        UserTag.owner -> "OWNER"
        UserTag.member -> "MEMBER"
        UserTag.viewer -> "VIEWER"
    }

    val containerColor = when(tag) {
        UserTag.owner -> Color(0x5050ab7e)
        UserTag.member -> Color(0x504773d6)
        UserTag.viewer -> Color(0x50aa00ff)
    }

    val borderColor = when(tag) {
        UserTag.owner -> Color(0x6f50ab7e)
        UserTag.member -> Color(0x6f4773d6)
        UserTag.viewer -> Color(0x6faa00ff)
    }

    Card(
        modifier = Modifier.width(100.dp),
        colors = CardDefaults.cardColors(
            containerColor = containerColor,
        ),
        border = BorderStroke(1.dp, borderColor),
    ){
        Text(
            modifier = Modifier
                .padding(start = 10.dp, end = 10.dp, top = 2.dp, bottom = 2.dp)
                .fillMaxWidth(),
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
            color = color,
            text = text,
        )
    }
}