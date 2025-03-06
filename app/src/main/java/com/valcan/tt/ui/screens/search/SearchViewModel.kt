package com.valcan.tt.ui.screens.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.valcan.tt.data.model.Clothes
import com.valcan.tt.data.model.Shoes
import com.valcan.tt.data.model.User
import com.valcan.tt.data.model.Wardrobe
import com.valcan.tt.data.repository.ClothesRepository
import com.valcan.tt.data.repository.ShoesRepository
import com.valcan.tt.data.repository.UserRepository
import com.valcan.tt.data.repository.WardrobeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

data class SearchItem(
    val id: Long,
    val type: String, // "Vestito" o "Scarpa"
    val name: String,
    val imageUrl: String?,
    val color: String?,
    val season: String?,
    val wardrobeName: String?,
    val category: String?
)

@OptIn(kotlinx.coroutines.FlowPreview::class)
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val clothesRepository: ClothesRepository,
    private val shoesRepository: ShoesRepository,
    private val wardrobeRepository: WardrobeRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _selectedType = MutableStateFlow("Tutti")
    private val _selectedSeason = MutableStateFlow("Tutte")
    private val _selectedColor = MutableStateFlow("Tutti")
    private val _searchQuery = MutableStateFlow("")

    val searchResults: StateFlow<List<SearchItem>> = combine(
        _selectedType,
        _selectedSeason,
        _selectedColor,
        _searchQuery.debounce(300),
        clothesRepository.getAllClothes(),
        shoesRepository.getAllShoes(),
        wardrobeRepository.getAllWardrobes(),
        userRepository.getCurrentUser()
    ) { array ->
        val type = array[0] as String
        val season = array[1] as String
        val color = array[2] as String
        val query = array[3] as String
        val clothes = safeCastToClothesList(array[4])
        val shoes = safeCastToShoesList(array[5])
        val wardrobes = safeCastToWardrobeList(array[6])
        val user = array[7] as? User
        
        if (user == null) return@combine emptyList()

        val results = mutableListOf<SearchItem>()
        val wardrobeMap = wardrobes.associateBy { wardrobe -> wardrobe.wardrobeId }

        // Filtra vestiti
        if (type == "Tutti" || type == "Vestiti") {
            clothes.filter { cloth ->
                cloth.userId == user.userId &&
                (query.isEmpty() || cloth.name.contains(query, ignoreCase = true)) &&
                (season == "Tutte" || cloth.season == season) &&
                (color == "Tutti" || cloth.color == color)
            }.mapTo(results) { cloth ->
                SearchItem(
                    id = cloth.id,
                    type = "Vestito",
                    name = cloth.name,
                    imageUrl = cloth.imageUrl,
                    color = cloth.color,
                    season = cloth.season,
                    wardrobeName = cloth.wardrobeId?.let { wardrobeId -> wardrobeMap[wardrobeId]?.name },
                    category = cloth.category
                )
            }
        }

        // Filtra scarpe
        if (type == "Tutti" || type == "Scarpe") {
            shoes.filter { shoe ->
                shoe.userId == user.userId &&
                (query.isEmpty() || shoe.name.contains(query, ignoreCase = true)) &&
                (season == "Tutte" || shoe.season == season) &&
                (color == "Tutti" || shoe.color == color)
            }.mapTo(results) { shoe ->
                SearchItem(
                    id = shoe.id,
                    type = "Scarpa",
                    name = shoe.name,
                    imageUrl = shoe.imageUrl,
                    color = shoe.color,
                    season = shoe.season,
                    wardrobeName = shoe.wardrobeId?.let { wardrobeId -> wardrobeMap[wardrobeId]?.name },
                    category = shoe.type
                )
            }
        }

        results
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun updateFilters(type: String, season: String, color: String) {
        _selectedType.value = type
        _selectedSeason.value = season
        _selectedColor.value = color
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    // Versione sicura dei cast per le liste
    private fun safeCastToClothesList(value: Any?): List<Clothes> {
        return if (value is List<*>) {
            value.filterIsInstance<Clothes>()
        } else {
            emptyList()
        }
    }
    
    private fun safeCastToShoesList(value: Any?): List<Shoes> {
        return if (value is List<*>) {
            value.filterIsInstance<Shoes>()
        } else {
            emptyList()
        }
    }
    
    private fun safeCastToWardrobeList(value: Any?): List<Wardrobe> {
        return if (value is List<*>) {
            value.filterIsInstance<Wardrobe>()
        } else {
            emptyList()
        }
    }

    // Helper per verificare se un testo contiene i termini di ricerca
    private fun containsSearchTerm(text: String?, query: String): Boolean {
        if (text == null) return false
        return text.contains(query, ignoreCase = true)
    }
} 