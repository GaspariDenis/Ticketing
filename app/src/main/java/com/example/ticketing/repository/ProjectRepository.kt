package com.example.ticketing.repository

import android.util.Log
import com.example.ticketing.network.APIService
import com.example.ticketing.network.APIStatus
import com.example.ticketing.vo.Project
import com.example.ticketing.vo.UIProject
import com.example.ticketing.vo.extractError
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProjectRepository @Inject constructor(
    val api : APIService
) {
    private val tag = "ProjectRepository"

    suspend fun getAllProjects(userId : String) : APIStatus<List<Project>> {
        return try{
            val response = api.getAllUserProjects(userId)

            when(response.code()){
                200 -> {
                    APIStatus.Success(response.body() ?: listOf())
                }
                401 -> {
                    APIStatus.ErrorAPI(
                        code = response.code(),
                        error = extractError( response.errorBody())
                    )
                }
                else -> throw Exception("Error with code ${response.code()}, it's not handle.")
            }
        }catch (e : Exception){
            Log.e(tag, e.message ?: "Unexpected error")
            APIStatus.Error(e)
        }
    }

    suspend fun createProject(userId: String, name : String, descriptor: String) : APIStatus<Project> {
        return try{
            val response = api.createProject(
                userId = userId,
                name = name,
                description = descriptor
            )

            when(response.code()){
                200 -> {
                    APIStatus.Success(response.body() ?: throw Exception("the body was empty."))
                }
                400, 401 -> {
                    APIStatus.ErrorAPI(
                        code = response.code(),
                        error = extractError( response.errorBody())
                    )
                }
                else -> throw Exception("Error with code ${response.code()}, it's not handle.")
            }
        }catch (e : Exception){
            Log.e(tag, e.message ?: "Unexpected error")
            APIStatus.Error(e)
        }
    }

    suspend fun detailProject(projectId : String) : APIStatus<Project> {
        return try{
            val response = api.getProject(projectId)

            when(response.code()){
                200 -> {
                    APIStatus.Success(response.body() ?: throw Exception("the body was empty."))
                }
                401, 403, 404 -> {
                    APIStatus.ErrorAPI(
                        code = response.code(),
                        error = extractError( response.errorBody())
                    )
                }
                else -> throw Exception("Error with code ${response.code()}, it's not handle.")
            }
        }catch (e : Exception){
            Log.e(tag, e.message ?: "Unexpected error")
            APIStatus.Error(e)
        }
    }

    suspend fun updateProject(project : UIProject) : APIStatus<Project> {
        return try{
            val response = api.updateProject(project.id, project.name, project.description)

            when(response.code()){
                200 -> {
                    APIStatus.Success(response.body() ?: throw Exception("the body was empty."))
                }
                400, 401, 403, 404 -> {
                    APIStatus.ErrorAPI(
                        code = response.code(),
                        error = extractError( response.errorBody())
                    )
                }
                else -> throw Exception("Error with code ${response.code()}, it's not handle.")
            }
        }catch (e : Exception){
            Log.e(tag, e.message ?: "Unexpected error")
            APIStatus.Error(e)
        }
    }

    suspend fun deleteProject(project: UIProject) : APIStatus<Unit> {
        return try{
            val response = api.deleteProject(project.id)

            when(response.code()){
                204 -> {
                    Log.d(tag, "Project deleted successfully.")
                    APIStatus.Success(Unit)
                }
                403, 404 -> {
                    APIStatus.ErrorAPI(
                        code = response.code(),
                        error = extractError( response.errorBody())
                    )
                }
                else -> throw Exception("Error with code ${response.code()}, it's not handle.")
            }
        }catch (e : Exception){
            Log.e(tag, e.message ?: "Unexpected error")
            APIStatus.Error(e)
        }
    }
}

