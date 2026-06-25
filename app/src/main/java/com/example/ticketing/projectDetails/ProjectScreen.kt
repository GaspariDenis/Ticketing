package com.example.ticketing.projectDetails

import android.annotation.SuppressLint
import android.service.autofill.OnClickAction
import android.widget.ImageButton
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.room.util.TableInfo
import com.example.ticketing.R
import com.example.ticketing.dashboardTickets.DashboardTickets
import com.example.ticketing.projectSetting.ProjectSetting
import com.example.ticketing.ui.utils.Alert
import com.example.ticketing.ui.utils.MemberTag
import com.example.ticketing.ui.utils.TextField
import com.example.ticketing.vo.Member
import com.example.ticketing.vo.Project
import com.example.ticketing.vo.User
import com.example.ticketing.vo.UserTag
import kotlinx.serialization.Serializable
import okhttp3.internal.checkDuration
import java.sql.Date

@Serializable
data class ProjectDetails(
    val userTag: UserTag,
    val projectId : String
)

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ProjectDetailsScreen(
    modifier: Modifier = Modifier,
    viewModel: ProjectViewModel = hiltViewModel(),
    nav : NavController,
    userTag: UserTag,
    projectId: String
){
    val project by viewModel.project.collectAsStateWithLifecycle(initialValue = Project())

    val triggerError by viewModel.errorEvent.collectAsStateWithLifecycle(initialValue = "")

    LaunchedEffect(projectId) {
        viewModel.getProject(projectId)
    }

    Screen(
        modifier,
        projectId = projectId,
        projectName = project.name ?: "Unnamed Project",
        createDate = project.getFormattedDate(),
        isOwner = userTag == UserTag.owner,
        memberList = project.members ?: listOf(),
        error = triggerError,
        seeTickets = { nav.navigate(DashboardTickets(
            projectId = projectId,
            project = project,
            youTag = userTag,
            projectMembers = project.members ?: listOf()
        )) },
        onClickSetting = { nav.navigate(ProjectSetting(project = project)) },
        onClickBackArrow = { nav.popBackStack() },
        addMember = viewModel::addMember,
        canRemoveOwner = viewModel::canRemoveOwner,
        onClickError = viewModel::resetErrorEvent,
        checkEmail = viewModel::checkEmail,
        onClickDeleteMember = viewModel::removeMember
    )
}

@Composable
private fun Screen(
    modifier: Modifier = Modifier,
    projectId: String,
    projectName : String,
    createDate : String,
    isOwner : Boolean,
    memberList : List<Member>,
    error : String,
    seeTickets : () -> Unit,
    onClickSetting : () -> Unit,
    canRemoveOwner:(List<Member>) -> Boolean,
    addMember: (String, String, UserTag) -> Unit,
    onClickBackArrow: () -> Unit,
    onClickError : () -> Unit,
    checkEmail : (String) -> Boolean,
    onClickDeleteMember: (String, String) -> Unit
) {

    if(error != ""){
        Alert(
            title = "ERROR",
            message = error,
            onDismiss = {},
            onConfirm = onClickError
        )
    }

    var text by remember { mutableStateOf("") }

    var showAlertForMember by remember { mutableStateOf(false) }

    if(showAlertForMember){
        chooseRole(
            projectId = projectId,
            email = text,
            onClick = { str1, str2, tag ->
                addMember(str1, str2, tag)
                showAlertForMember = false
            }
        )
    }

    Scaffold(
        modifier = Modifier
            .padding(start = 16.dp, end = 16.dp),
        topBar = {
            Row(
                modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onClickBackArrow
                ){
                    Icon(
                        modifier = Modifier.size(40.dp).padding(end = 4.dp),
                        painter = painterResource(R.drawable.left_arrow),
                        contentDescription = null
                    )
                }

                Text(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp,
                    text = "Dettaglio Progetto"
                )

                if(isOwner){
                    IconButton(
                        onClick = onClickSetting
                    ){
                        Icon(
                            modifier = Modifier.size(40.dp),
                            painter = painterResource(R.drawable.config_icon),
                            contentDescription = null
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.padding(paddingValues)
        ) {
            Card(
                onClick = seeTickets,
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
            ){
                Text(
                    modifier = Modifier.padding(start = 16.dp, top = 16.dp),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xffa38cf3),
                    text = projectName
                )

                Text(
                    modifier = Modifier.padding(start = 16.dp, bottom = 16.dp),
                    fontSize = 16.sp,
                    text = "Creato il ${createDate}"
                )
            }

            if(isOwner){
                Card(
                    modifier = Modifier.padding(end = 16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0x00000000)
                    )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextField(
                            modifier = Modifier.fillMaxWidth().weight(1f),
                            text = text,
                            labelText = "INVITA UN MEMBRO",
                            labelColor = Color(0xff65d0eb),
                            placeholder = {
                                Text(
                                    text = "collaboratore@azienda.com"
                                )
                            },
                            onChange = {t -> text = t}
                        )

                        TextButton(
                            colors = ButtonColors(
                                containerColor = Color(0xff65d0eb),
                                contentColor = Color.Black,
                                disabledContainerColor = Color(0xff65d0eb),
                                disabledContentColor = Color.Black
                            ),
                            onClick = {
                                if(checkEmail(text)){
                                    showAlertForMember = true
                                }else{
                                    showAlertForMember = false
                                }
                            }
                        ) {
                            Text(
                                modifier = Modifier.padding(8.dp),
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 20.sp,
                                text = "INVITA"
                            )
                        }
                    }
                }
            }

            Text(
                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                text = "Membri del Team (${memberList.count()})"
            )

            val canRemoveOwner = canRemoveOwner(memberList)
            for(member in memberList) {
                CardMember(
                    modifier = Modifier.padding(top = 8.dp),
                    projectId = projectId,
                    member = member,
                    ImOwner = isOwner,
                    canRemoveOwner = canRemoveOwner,
                    onConfirm = onClickDeleteMember
                )
            }
        }
    }
}

@Composable
fun CardMember(
    modifier: Modifier = Modifier,
    projectId: String,
    member: Member,
    ImOwner : Boolean,
    canRemoveOwner: Boolean,
    onConfirm : (String, String) -> Unit
){
    Card(
        modifier = modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier.padding(8.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ){
            Column(
                modifier = Modifier.fillMaxWidth().weight(1f)
            ) {
                Text(
                    color = Color(0xffffffff),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    text = member.user?.name ?: "User not fount."
                )
                Text(
                    text = member.user?.email ?: "Email not fount"
                )
            }

            MemberTag(member.getRole())

            if(ImOwner && (canRemoveOwner || member.getRole() != UserTag.owner)){
                IconButton(
                    modifier = Modifier.size(35.dp).padding(start = 8.dp),
                    onClick = {
                        onConfirm(projectId, member.user?.id ?: "")
                    }
                ) {
                    Image(
                        painter = painterResource(R.drawable.trash_icon),
                        contentDescription = null
                    )
                }
            }
        }
    }
}

@Composable
fun chooseRole(
    projectId: String,
    email: String,
    onClick: (String, String, UserTag) -> Unit
) {
    Dialog(
        onDismissRequest = {},
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(space = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Seleziona il ruolo:"
            )

            Card(
                modifier = Modifier.clickable(
                    onClick = {
                        onClick(projectId, email, UserTag.owner)
                    }
                )
            ) {
                MemberTag(UserTag.owner)
            }
            Card(
                modifier = Modifier.clickable(
                    onClick = {
                        onClick(projectId, email, UserTag.member)
                    }
                )
            ) {
                MemberTag(UserTag.member)
            }
            Card(
                modifier = Modifier.clickable(
                    onClick = {
                        onClick(projectId, email, UserTag.viewer)
                    }
                )
            ) {
                MemberTag(UserTag.viewer)
            }
        }
    }
}