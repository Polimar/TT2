package com.valcan.tt.ui.navigation

sealed class Screen(val route: String) {
    object Welcome : Screen("welcome")
    object Home : Screen("home")
    object Clothes : Screen("clothes")
    object Shoes : Screen("shoes")
    object Search : Screen("search")
    object Profile : Screen("profile")
    object ClothDetail : Screen("cloth/{clothId}") {
        fun createRoute(clothId: Long) = "cloth/$clothId"
    }
    object ShoeDetail : Screen("shoe_detail/{shoeId}") {
        fun createRoute(shoeId: Long) = "shoe_detail/$shoeId"
    }
    object AddEditCloth : Screen("add_edit_cloth?clothId={clothId}") {
        fun createRoute(clothId: Long? = null) = "add_edit_cloth${clothId?.let { "?clothId=$it" } ?: ""}"
    }
    object AddEditShoe : Screen("add_edit_shoe?shoeId={shoeId}") {
        fun createRoute(shoeId: Long? = null) = "add_edit_shoe${shoeId?.let { "?shoeId=$it" } ?: ""}"
    }
} 