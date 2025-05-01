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

// Mappatura delle etichette delle stagioni alle possibili stringhe memorizzate
private val SEASON_MAPPINGS = mapOf(
    SEASON_SPRING to listOf("primavera", "spring", "frühling", "printemps", "весна", "primavera", "wiosna"),
    SEASON_SUMMER to listOf("estate", "summer", "sommer", "été", "лето", "verano", "lato"),
    SEASON_AUTUMN to listOf("autunno", "autumn", "fall", "herbst", "automne", "осень", "otoño", "jesień"),
    SEASON_WINTER to listOf("inverno", "winter", "hiver", "зима", "invierno", "zima")
)

// Possibili valori per "tutte le stagioni" nelle diverse lingue
private val ALL_SEASONS_VALUES = listOf(
    "tutte le stagioni", // Italiano
    "all seasons",       // Inglese
    "toutes les saisons", // Francese
    "todas las estaciones", // Spagnolo
    "alle jahreszeiten",   // Tedesco
    "wszystkie pory roku", // Polacco
    "wszystkie sezony",    // Altra variante polacca
    "todas as estações",   // Portoghese
    "alle seizoenen",      // Olandese
    "всі сезони",          // Ucraino
    "všechna roční období",  // Ceco
    "season_all"           // Etichetta interna che potrebbe essere usata
)

data class SearchItem(
    val id: Long,
    val type: String, // "clothes" o "shoes"
    val name: String,
    val imageUrl: String?,
    val color: String?,
    val season: String?,
    val wardrobeName: String?,
    val category: String?,
    val position: String? = null
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

    // Funzione helper più aggressiva per verificare se un elemento è per tutte le stagioni
    private fun isAllSeasons(season: String?): Boolean {
        if (season == null) return false
        
        // Controlla se la stagione è in ALL_SEASONS_VALUES
        if (ALL_SEASONS_VALUES.any { season.equals(it, ignoreCase = true) }) return true
        
        // Controlla se la stagione CONTIENE uno dei valori in ALL_SEASONS_VALUES
        if (ALL_SEASONS_VALUES.any { season.contains(it, ignoreCase = true) }) return true
        
        // Controlla anche il valore grezzo SEASON_ALL (potrebbe essere stato salvato direttamente)
        if (season.equals(SEASON_ALL, ignoreCase = true)) return true
        
        return false
    }

    // Funzione pubblica per verificare se una stagione corrisponde a un'etichetta di stagione (per SearchScreen)
    fun matchesSeason(itemSeason: String?, seasonLabel: String): Boolean {
        if (itemSeason == null) return false
        if (seasonLabel == SEASON_ALL) return true
        
        // Controllo esatto per l'etichetta
        if (itemSeason.equals(seasonLabel, ignoreCase = true)) return true
        
        // Se la stagione dell'item contiene direttamente l'etichetta
        if (itemSeason.contains(seasonLabel, ignoreCase = true)) return true
        
        // Se la stagione dell'item corrisponde a una delle stringhe mappate per questa etichetta
        val possibleSeasonStrings = SEASON_MAPPINGS[seasonLabel] ?: return false
        if (possibleSeasonStrings.any { itemSeason.contains(it, ignoreCase = true) }) return true
        
        // Se la stagione è una keyword "all_seasons" e stiamo cercando una stagione specifica
        return isAllSeasons(itemSeason)
    }

    // Funzione pubblica per verificare da SearchScreen se un item è per tutte le stagioni
    fun isItemAllSeasons(season: String?): Boolean {
        return isAllSeasons(season)
    }

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

        // Filtra vestiti
        if (type == TYPE_ALL || type == TYPE_CLOTHES) {
            clothes.filter { cloth ->
                cloth.userId == user.userId &&
                (query.isEmpty() || 
                 cloth.name.contains(query, ignoreCase = true) ||
                 cloth.category.contains(query, ignoreCase = true) ||
                 cloth.color.contains(query, ignoreCase = true)) &&
                (season == SEASON_ALL ||
                 matchesSeason(cloth.season, season))
            }.mapTo(results) { cloth ->
                SearchItem(
                    id = cloth.id,
                    type = TYPE_CLOTHES,
                    name = cloth.name,
                    imageUrl = cloth.imageUrl,
                    color = cloth.color,
                    season = cloth.season,
                    wardrobeName = cloth.wardrobeId?.let { wardrobeId -> wardrobeMap[wardrobeId]?.name },
                    category = cloth.category,
                    position = cloth.position
                )
            }
        }

        // Filtra scarpe
        if (type == TYPE_ALL || type == TYPE_SHOES) {
            shoes.filter { shoe ->
                shoe.userId == user.userId &&
                (query.isEmpty() || 
                 shoe.name.contains(query, ignoreCase = true) ||
                 (shoe.type?.contains(query, ignoreCase = true) ?: false) ||
                 (shoe.color?.contains(query, ignoreCase = true) ?: false)) &&
                (season == SEASON_ALL ||
                 matchesSeason(shoe.season, season))
            }.mapTo(results) { shoe ->
                SearchItem(
                    id = shoe.id,
                    type = TYPE_SHOES,
                    name = shoe.name,
                    imageUrl = shoe.imageUrl,
                    color = shoe.color,
                    season = shoe.season,
                    wardrobeName = shoe.wardrobeId?.let { wardrobeId -> wardrobeMap[wardrobeId]?.name },
                    category = shoe.type,
                    position = null // Le scarpe non hanno un campo posizione
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