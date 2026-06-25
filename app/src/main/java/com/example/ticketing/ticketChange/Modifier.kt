package com.example.ticketing.ticketChange

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ticketing.R
import com.example.ticketing.ui.utils.PriorityTagCard
import com.example.ticketing.ui.utils.TextField
import com.example.ticketing.vo.Member
import com.example.ticketing.vo.PriorityTag
import com.example.ticketing.vo.Ticket
import com.example.ticketing.vo.TicketStatus
import com.example.ticketing.vo.UserTag
import kotlin.collections.forEach

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangeScreen(
    modifier : Modifier = Modifier,
    ticket : Ticket,
    memberList : List<Member>,
    onClickBackArrow : () -> Unit,
    onDelete : () -> Unit,
    onClick : (String, String, TicketStatus, PriorityTag, String) -> Unit,
    isValid : (String, String, String) -> Boolean
) {
    var title by remember { mutableStateOf(ticket.title ?: "") }
    var description by remember { mutableStateOf(ticket.description ?: "") }
    var status by remember { mutableStateOf(ticket.getTicketStatus()) }
    var priority by remember { mutableStateOf(ticket.getTicketPriority()) }
    var assigned by remember { mutableStateOf(ticket.assigneeId ?: "") }
    var optionMember by remember { mutableStateOf(ticket.assignee?.name ?: "") }
    var optionStatus by remember { mutableStateOf(ticket.status?.uppercase() ?: "") }

    var isExpandedStatus by remember { mutableStateOf(false) }
    var isExpandedMember by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            Row(
                modifier = modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                IconButton(
                    onClick = onClickBackArrow
                ){
                    Icon(
                        modifier = Modifier
                            .size(40.dp)
                            .padding(end = 4.dp),
                        painter = painterResource(R.drawable.left_arrow),
                        contentDescription = null
                    )
                }

                Text(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 30.sp,
                    text = "Creazione Ticket"
                )
            }
        },
        bottomBar = {
            Column() {
                Button(
                    onClick = {
                        onClick(title, description,status, priority, assigned)
                    },
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .padding(start = 16.dp, end = 16.dp, bottom = 20.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0x00ffffff)
                    ),
                    enabled = isValid(title, description, assigned),
                    contentPadding = PaddingValues()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                brush =
                                    if (isValid(title, description, assigned)) Brush.horizontalGradient(
                                        colors = listOf(Color(0xff845fee), Color(0xff4e80ee))
                                    )
                                    else
                                        Brush.horizontalGradient(
                                            colors = listOf(Color(0xffaabef0), Color(0xffceb9fa))
                                        )
                            ),
                        contentAlignment = Alignment.Center,
                    ){
                        Text(
                            modifier = Modifier
                            ,
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp,
                            color = Color.White,
                            text = "MODIFICA TICKET"
                        )
                    }
                }
                Spacer(Modifier.height(8.dp))
                Button(
                    onClick = {
                        onDelete()
                    },
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .padding(start = 16.dp, end = 16.dp, bottom = 20.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0x00ffffff)
                    ),
                    enabled = true,
                    contentPadding = PaddingValues()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                brush =
                                    Brush.horizontalGradient(
                                        colors = listOf(Color(0xff845fee), Color(0xff4e80ee))
                                    )
                            ),
                        contentAlignment = Alignment.Center,
                    ){
                        Text(
                            modifier = Modifier
                            ,
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp,
                            color = Color.White,
                            text = "ELIMINA TICKET"
                        )
                    }
                }
            }
        }
    ) {innerpadding ->
        Column(modifier = Modifier.padding(innerpadding).padding(start = 16.dp, end = 16.dp)) {
            TextField(
                modifier = Modifier.fillMaxWidth(),
                text = title,
                onChange = {str -> title = str},
                labelText = "Titolo Ticket",
                labelColor = Color(0xff9b86e7),
                placeholder = {}
            )

            TextField(
                modifier = Modifier.fillMaxWidth().height(120.dp),
                text = description,
                onChange = {str -> description = str},
                labelText = "Desccrizione Ticket",
                labelColor = Color(0xff9b86e7),
                maxLine = 5,
                singleLine = false,
                placeholder = {}
            )

            Spacer(modifier = Modifier.height(16.dp))

            ExposedDropdownMenuBox(
                expanded = isExpandedStatus,
                onExpandedChange = {isExpandedStatus = !isExpandedStatus}
            ) {
                OutlinedTextField(
                    modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryEditable).fillMaxWidth(),
                    shape = OutlinedTextFieldDefaults.shape,
                    value = optionStatus,
                    onValueChange = {},
                    readOnly = true,
                    label = {Text(
                        fontSize = 17.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xff9b86e7),
                        overflow = TextOverflow.Ellipsis,
                        text = "Assegnato a:"
                    )},
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpandedStatus) }
                )

                ExposedDropdownMenu(
                    expanded = isExpandedStatus,
                    onDismissRequest = { isExpandedStatus = false }
                ) {
                    val value = when(ticket.getTicketStatus()){
                        TicketStatus.open -> 0
                        TicketStatus.in_progress -> 1
                        TicketStatus.closed -> 2
                    }

                    if(value < 1){
                        DropdownMenuItem(
                            text = { Text("Open") },
                            onClick = {
                                optionStatus = "OPEN"
                                status = TicketStatus.open
                            }
                        )
                    }
                    if(value < 2){
                        DropdownMenuItem(
                            text = { Text("IN PROGRESS") },
                            onClick = {
                                optionStatus = "IN PROGRESS"
                                status = TicketStatus.in_progress
                            }
                        )
                    }
                    if(value < 3){
                        DropdownMenuItem(
                            text = { Text("CLOSED") },
                            onClick = {
                                optionStatus = "CLOSED"
                                status = TicketStatus.closed
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Priorità"
            )
            Row{
                PriorityTagCard(
                    modifier = Modifier.padding(end = 12.dp),
                    PriorityTag.low,
                    onClick = { tag ->
                        priority = tag
                    },
                    isClicked = priority == PriorityTag.low
                )
                PriorityTagCard(
                    modifier = Modifier.padding(end = 12.dp),
                    PriorityTag.medium,
                    onClick = {tag ->
                        priority = tag
                    },
                    isClicked = priority == PriorityTag.medium
                )
                PriorityTagCard(
                    modifier = Modifier.padding(end = 12.dp),
                    PriorityTag.high,
                    onClick = {tag->
                        priority = tag
                    },
                    isClicked = priority == PriorityTag.high
                )
            }

            Spacer(Modifier.height(16.dp))

            ExposedDropdownMenuBox(
                expanded = isExpandedMember,
                onExpandedChange = {isExpandedMember = !isExpandedMember}
            ) {
                OutlinedTextField(
                    modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryEditable).fillMaxWidth(),
                    shape = OutlinedTextFieldDefaults.shape,
                    value = optionMember,
                    onValueChange = {},
                    readOnly = true,
                    label = {Text(
                        fontSize = 17.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xff9b86e7),
                        overflow = TextOverflow.Ellipsis,
                        text = "Assegnato a:"
                    )},
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpandedMember) }
                )

                ExposedDropdownMenu(
                    expanded = isExpandedMember,
                    onDismissRequest = { isExpandedMember = false }
                ) {
                    memberList.forEach { member ->
                        if(member.getRole() != UserTag.viewer){
                            DropdownMenuItem(
                                text = { Text(member.user?.name ?: "Name not found") },
                                onClick = {
                                    Log.d("UI", member.userId ?: "Name not found")
                                    optionMember = member.user?.name ?: "Name not found"
                                    assigned = member.userId ?: ""
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}