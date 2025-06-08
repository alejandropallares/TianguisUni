package com.example.tianguisuni.ui.state

import com.example.tianguisuni.model.Publication

sealed class HomeScreenState {
    object Loading : HomeScreenState()
    data class Content(
        val publications: List<Publication>,
        val selectedCategory: String = "Todo"
    ) : HomeScreenState()
    object Empty : HomeScreenState()
    data class Error(val message: String) : HomeScreenState()
}

sealed class MyPublicationsState {
    object NotAuthenticated : MyPublicationsState()
    object Loading : MyPublicationsState()
    data class Content(
        val publications: List<Publication>
    ) : MyPublicationsState()
    object Empty : MyPublicationsState()
    data class Error(val message: String) : MyPublicationsState()
}

sealed class NewPublicationState {
    object Initial : NewPublicationState()
    object Loading : NewPublicationState()
    object Success : NewPublicationState()
    data class Error(val message: String) : NewPublicationState()
}

data class NewPublicationFormState(
    val name: String = "",
    val nameError: String? = null,
    val category: String = "",
    val categoryError: String? = null,
    val description: String = "",
    val descriptionError: String? = null,
    val location: String = "",
    val locationError: String? = null,
    val price: String = "",
    val priceError: String? = null,
    val imageUri: String? = null,
    val imageError: String? = null
) 