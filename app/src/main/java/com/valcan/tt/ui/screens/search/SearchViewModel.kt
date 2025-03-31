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

// Etichette costanti per i tipi
const val TYPE_ALL = "all"
const val TYPE_CLOTHES = "clothes"
const val TYPE_SHOES = "shoes"

// Etichette costanti per le stagioni
const val SEASON_ALL = "all"
const val SEASON_SPRING = "spring"
const val SEASON_SUMMER = "summer"
const val SEASON_AUTUMN = "autumn"
const val SEASON_WINTER = "winter"

data class SearchItem(
    val id: Long,
    val type: String, // "clothes" o "shoes"
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

    private val _selectedType = MutableStateFlow(TYPE_ALL)
    private val _selectedSeason = MutableStateFlow(SEASON_ALL)
    private val _searchQuery = MutableStateFlow("")

    val searchResults: StateFlow<List<SearchItem>> = combine(
        _selectedType,
        _selectedSeason,
        _searchQuery.debounce(300),
        clothesRepository.getAllClothes(),
        shoesRepository.getAllShoes(),
        wardrobeRepository.getAllWardrobes(),
        userRepository.getCurrentUser()
    ) { array ->
        val type = array[0] as String
        val season = array[1] as String
        val query = array[2] as String
        val clothes = safeCastToClothesList(array[3])
        val shoes = safeCastToShoesList(array[4])
        val wardrobes = safeCastToWardrobeList(array[5])
        val user = array[6] as? User
        
        if (user == null) return@combine emptyList()

        val results = mutableListOf<SearchItem>()
        val wardrobeMap = wardrobes.associateBy { wardrobe -> wardrobe.wardrobeId }

        // Mappa delle stagioni per la ricerca nel database
        val seasonDbMap = mapOf(
            SEASON_SPRING to "primavera",
            SEASON_SUMMER to "estate",
            SEASON_AUTUMN to "autunno",
            SEASON_WINTER to "inverno"
        )
        
        val seasonToSearch = if (season == SEASON_ALL) null else seasonDbMap[season]

        // Filtra vestiti
        if (type == TYPE_ALL || type == TYPE_CLOTHES) {
            clothes.filter { cloth ->
                cloth.userId == user.userId &&
                (query.isEmpty() || cloth.name.contains(query, ignoreCase = true)) &&
                (seasonToSearch == null || cloth.season?.contains(seasonToSearch, ignoreCase = true) == true || cloth.season?.contains("tutte le stagioni", ignoreCase = true) == true)
            }.mapTo(results) { cloth ->
                SearchItem(
                    id = cloth.id,
                    type = TYPE_CLOTHES,
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
        if (type == TYPE_ALL || type == TYPE_SHOES) {
            shoes.filter { shoe ->
                shoe.userId == user.userId &&
                (query.isEmpty() || shoe.name.contains(query, ignoreCase = true)) &&
                (seasonToSearch == null || shoe.season?.contains(seasonToSearch, ignoreCase = true) == true || shoe.season?.contains("tutte le stagioni", ignoreCase = true) == true)
            }.mapTo(results) { shoe ->
                SearchItem(
                    id = shoe.id,
                    type = TYPE_SHOES,
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

    fun updateFilters(type: String, season: String) {
        _selectedType.value = type
        _selectedSeason.value = season
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

} 