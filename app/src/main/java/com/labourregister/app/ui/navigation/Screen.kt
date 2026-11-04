package com.labourregister.app.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.outlined.Assignment
import androidx.compose.material.icons.outlined.PersonAdd
import androidx.compose.ui.graphics.vector.ImageVector

enum class Screen(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    ADD_LABOUR(
        title = "Add Labour",
        selectedIcon = Icons.Filled.PersonAdd,
        unselectedIcon = Icons.Outlined.PersonAdd
    ),
    LABOUR_REGISTER(
        title = "Labour Register",
        selectedIcon = Icons.Filled.Assignment,
        unselectedIcon = Icons.Outlined.Assignment
    )
}
