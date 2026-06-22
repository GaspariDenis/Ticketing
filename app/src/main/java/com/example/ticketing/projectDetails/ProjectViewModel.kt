package com.example.ticketing.projectDetails

import androidx.lifecycle.ViewModel
import com.example.ticketing.repository.ProjectRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProjectViewModel @Inject constructor(repo : ProjectRepository) : ViewModel() {
}