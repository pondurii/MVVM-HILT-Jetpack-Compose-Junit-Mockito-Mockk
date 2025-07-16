package com.example.mvvm.training.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mvvm.training.model.model.Post
import com.example.mvvm.training.model.repository.PostRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PostUiState(
    val posts: List<Post> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class PostViewModel @Inject constructor(
    private val postRepository: PostRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PostUiState())
    val uiState: StateFlow<PostUiState> = _uiState.asStateFlow()

    init {
        fetchPosts()
    }

    private fun fetchPosts() {
        viewModelScope.launch {
            postRepository.getPosts()
                .onStart {
                    _uiState.value = PostUiState(isLoading = true)
                }
                .catch { throwable ->
                    _uiState.value = PostUiState(
                        error = throwable.message ?: "Unknown error from flow",
                        isLoading = false
                    )
                }
                .collect { result ->
                    result.fold(
                        onSuccess = { posts ->
                            _uiState.value = PostUiState(posts = posts, isLoading = false)
                        },
                        onFailure = { throwable ->
                            _uiState.value = PostUiState(
                                error = throwable.message ?: "Unknown error",
                                isLoading = false
                            )
                        }
                    )
                }
        }
    }
}