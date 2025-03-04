package com.valcan.tt.ui.viewmodel

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.valcan.tt.data.AppDatabase
import com.valcan.tt.data.model.Clothes
import com.valcan.tt.data.model.Shoes
import com.valcan.tt.data.model.User
import com.valcan.tt.data.model.Wardrobe
import com.valcan.tt.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.zip.Deflater
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream
import javax.inject.Inject

private const val TAG = "BackupRestoreViewModel"

@HiltViewModel
class BackupRestoreViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val database: AppDatabase,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _restoreComplete = MutableStateFlow(false)
    val restoreComplete: StateFlow<Boolean> = _restoreComplete.asStateFlow()
    
    private val _restoreError = MutableStateFlow<String?>(null)
    val restoreError: StateFlow<String?> = _restoreError.asStateFlow()
    
    val _backupInfo = MutableStateFlow<BackupInfo?>(null)
    val backupInfo: StateFlow<BackupInfo?> = _backupInfo.asStateFlow()

    private val json = Json { 
        prettyPrint = true
        ignoreUnknownKeys = true
        encodeDefaults = true
        prettyPrintIndent = "  "
    }
    
    // Formato per la conversione Date <-> String
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)

    fun createBackup(uri: Uri) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    Log.d(TAG, "=== INIZIO BACKUP ===")
                    showToast("Inizio creazione backup...")
                    
                    // 1. Recuperiamo tutti i dati dal database
                    val users = database.userDao().getAllUsers().first()
                    Log.d(TAG, "Users recuperati: ${users.size}")
                    if (users.isEmpty()) {
                        Log.e(TAG, "Nessun utente trovato nel database!")
                        showToast("Nessun utente trovato nel database")
                        return@withContext
                    }

                    val wardrobes = database.wardrobeDao().getAllWardrobes().first()
                    Log.d(TAG, "Wardrobes recuperati: ${wardrobes.size}")

                    val clothes = database.clothesDao().getAllClothes().first()
                    Log.d(TAG, "Clothes recuperati: ${clothes.size}")

                    val shoes = database.shoesDao().getAllShoes().first()
                    Log.d(TAG, "Shoes recuperati: ${shoes.size}")
                    
                    // 2. Conversione in DTO con tutti i campi completi
                    val userDtos = users.map { user -> 
                        UserDTO(
                            userId = user.userId,
                            name = user.name,
                            gender = user.gender,
                            birthday = dateFormat.format(user.birthday),
                            createdAt = dateFormat.format(user.createdAt)
                        )
                    }
                    
                    val wardrobeDtos = wardrobes.map { wardrobe ->
                        WardrobeDTO(
                            wardrobeId = wardrobe.wardrobeId,
                            name = wardrobe.name,
                            description = wardrobe.description,
                            createdAt = dateFormat.format(wardrobe.createdAt)
                        )
                    }
                    
                    val clothesDtos = clothes.map { cloth ->
                        ClothesDTO(
                            id = cloth.id,
                            name = cloth.name,
                            category = cloth.category,
                            color = cloth.color,
                            season = cloth.season,
                            position = cloth.position,
                            wardrobeId = cloth.wardrobeId,
                            userId = cloth.userId,
                            imageUrl = cloth.imageUrl,
                            createdAt = dateFormat.format(cloth.createdAt)
                        )
                    }
                    
                    val shoesDtos = shoes.map { shoe ->
                        ShoesDTO(
                            id = shoe.id,
                            name = shoe.name,
                            brand = shoe.brand,
                            size = shoe.size,
                            wardrobeId = shoe.wardrobeId,
                            userId = shoe.userId,
                            color = shoe.color,
                            type = shoe.type,
                            season = shoe.season,
                            price = shoe.price,
                            imageUrl = shoe.imageUrl,
                            createdAt = dateFormat.format(shoe.createdAt)
                        )
                    }
                    
                    // 3. Creazione dell'oggetto di backup completo
                    val backupData = BackupDataDTO(
                        users = userDtos,
                        wardrobes = wardrobeDtos,
                        clothes = clothesDtos,
                        shoes = shoesDtos,
                        createdAt = dateFormat.format(Date())
                    )

                    // Debug: stampiamo il JSON per verificare
                    val jsonString = json.encodeToString(backupData)
                    Log.d(TAG, "JSON generato (primi 500 caratteri): ${jsonString.take(500)}...")
                    
                    // 4. Raccogliamo le immagini
                    val imageFiles = mutableMapOf<String, File>()

                    clothes.forEach { cloth ->
                        cloth.imageUrl?.let { url ->
                            try {
                                val path = Uri.parse(url).path
                                Log.d(TAG, "Processing cloth image path: $path")
                                if (path != null) {
                                    val imageFile = File(path)
                                    if (imageFile.exists()) {
                                        imageFiles["clothes/${imageFile.name}"] = imageFile
                                        Log.d(TAG, "Added cloth image: ${imageFile.absolutePath}")
                                    } else {
                                        Log.e(TAG, "Image file does not exist: $path")
                                    }
                                }
                            } catch (e: Exception) {
                                Log.e(TAG, "Error processing cloth image: ${e.message}")
                            }
                        }
                    }

                    shoes.forEach { shoe ->
                        shoe.imageUrl?.let { url ->
                            try {
                                val path = Uri.parse(url).path
                                Log.d(TAG, "Processing shoe image path: $path")
                                if (path != null) {
                                    val imageFile = File(path)
                                    if (imageFile.exists()) {
                                        imageFiles["shoes/${imageFile.name}"] = imageFile
                                        Log.d(TAG, "Added shoe image: ${imageFile.absolutePath}")
                                    } else {
                                        Log.e(TAG, "Image file does not exist: $path")
                                    }
                                }
                            } catch (e: Exception) {
                                Log.e(TAG, "Error processing shoe image: ${e.message}")
                            }
                        }
                    }

                    // 5. Creiamo il file ZIP con compressione massima
                    context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                        ZipOutputStream(BufferedOutputStream(outputStream)).use { zipOut ->
                            // Impostiamo livello di compressione massimo
                            zipOut.setLevel(Deflater.BEST_COMPRESSION)
                            
                            // 5.1 Scriviamo il JSON
                            Log.d(TAG, "Writing JSON data to zip...")
                            zipOut.putNextEntry(ZipEntry("data.json"))
                            zipOut.write(jsonString.toByteArray(Charsets.UTF_8))
                            zipOut.closeEntry()

                            // 5.2 Scriviamo le immagini
                            imageFiles.forEach { (path, file) ->
                                try {
                                    Log.d(TAG, "Adding image to zip: $path")
                                    zipOut.putNextEntry(ZipEntry("images/$path"))
                                    file.inputStream().use { input ->
                                        input.copyTo(zipOut)
                                    }
                                    zipOut.closeEntry()
                                    Log.d(TAG, "Image added successfully: $path")
                                } catch (e: Exception) {
                                    Log.e(TAG, "Error adding image to zip: $path", e)
                                }
                            }
                        }
                    }
                    
                    showToast("Backup completato con successo: ${users.size} utenti, ${wardrobes.size} armadi, ${clothes.size} vestiti, ${shoes.size} scarpe")
                    Log.d(TAG, "=== BACKUP COMPLETATO CON SUCCESSO ===")
                } catch (e: Exception) {
                    Log.e(TAG, "Errore durante il backup", e)
                    showToast("Errore durante il backup: ${e.message}")
                    e.printStackTrace()
                }
            }
        }
    }

    // Funzione che analizza il file di backup e mostra le informazioni senza ripristinarlo
    fun analyzeBackup(uri: Uri) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    Log.d(TAG, "=== ANALISI BACKUP ===")
                    showToast("Analisi del file di backup...")
                    
                    // Creiamo una directory temporanea per estrarre il backup
                    val tempDir = File(context.cacheDir, "temp_backup_analyze")
                    tempDir.deleteRecursively() // Puliamo eventuali residui
                    tempDir.mkdirs()
                    
                    var jsonData: String? = null
                    var hasImages = false
                    
                    context.contentResolver.openInputStream(uri)?.use { input ->
                        ZipInputStream(BufferedInputStream(input)).use { zipIn ->
                            var entry = zipIn.nextEntry
                            while (entry != null) {
                                if (entry.name == "data.json") {
                                    val targetFile = File(tempDir, entry.name)
                                    targetFile.parentFile?.mkdirs()
                                    
                                    FileOutputStream(targetFile).use { output ->
                                        val buffer = ByteArray(4096)
                                        var len: Int
                                        while (zipIn.read(buffer).also { len = it } > 0) {
                                            output.write(buffer, 0, len)
                                        }
                                    }
                                    
                                    jsonData = targetFile.readText()
                                } else if (entry.name.startsWith("images/")) {
                                    hasImages = true
                                }
                                entry = zipIn.nextEntry
                            }
                        }
                    }
                    
                    if (jsonData == null) {
                        Log.e(TAG, "File data.json non trovato nel backup")
                        showToast("File di backup non valido: dati mancanti")
                        return@withContext
                    }
                    
                    // Decodifica i dati
                    val backupData = try {
                        json.decodeFromString<BackupDataDTO>(jsonData!!)
                    } catch (e: Exception) {
                        Log.e(TAG, "Errore decodifica JSON: ${e.message}")
                        showToast("Errore analisi backup: formato dati non valido")
                        return@withContext
                    }
                    
                    val backupInfo = BackupInfo(
                        createdAt = try { dateFormat.parse(backupData.createdAt) } catch (e: Exception) { Date() },
                        users = backupData.users,
                        usersCount = backupData.users.size,
                        wardrobesCount = backupData.wardrobes.size,
                        clothesCount = backupData.clothes.size,
                        shoesCount = backupData.shoes.size,
                        hasImages = hasImages,
                        sourceUri = uri
                    )
                    
                    _backupInfo.value = backupInfo
                    
                    Log.d(TAG, "Backup analizzato con successo: ${backupInfo.usersCount} utenti, ${backupInfo.wardrobesCount} armadi, ${backupInfo.clothesCount} vestiti, ${backupInfo.shoesCount} scarpe")
                    tempDir.deleteRecursively()
                    
                } catch (e: Exception) {
                    Log.e(TAG, "Errore durante l'analisi: ${e.message}")
                    showToast("Errore durante l'analisi del backup: ${e.message}")
                }
            }
        }
    }

    fun restoreSelectedData(backupInfo: BackupInfo, selectedUsers: List<Long>, importClothes: Boolean, importShoes: Boolean, importWardrobes: Boolean) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    Log.d(TAG, "=== INIZIO RESTORE SELETTIVO ===")
                    showToast("Inizio restore dei dati selezionati...")
                    
                    // Creiamo una directory temporanea per estrarre il backup
                    val tempDir = File(context.cacheDir, "temp_backup")
                    tempDir.deleteRecursively() // Puliamo eventuali residui
                    tempDir.mkdirs()
                    
                    Log.d(TAG, "Directory temporanea creata: ${tempDir.absolutePath}")
                    Log.d(TAG, "Utenti selezionati: $selectedUsers")
                    Log.d(TAG, "Import vestiti: $importClothes, scarpe: $importShoes, armadi: $importWardrobes")

                    // Estraiamo il file .tt nella directory temporanea
                    context.contentResolver.openInputStream(backupInfo.sourceUri)?.use { input ->
                        ZipInputStream(BufferedInputStream(input)).use { zipIn ->
                            var entry = zipIn.nextEntry
                            while (entry != null) {
                                val targetFile = File(tempDir, entry.name)
                                Log.d(TAG, "Extracting: ${entry.name} to ${targetFile.absolutePath}")
                                
                                // Crea le directory necessarie
                                targetFile.parentFile?.mkdirs()
                                
                                // Estrai il file
                                FileOutputStream(targetFile).use { output ->
                                    val buffer = ByteArray(4096)
                                    var len: Int
                                    while (zipIn.read(buffer).also { len = it } > 0) {
                                        output.write(buffer, 0, len)
                                    }
                                }
                                
                                entry = zipIn.nextEntry
                            }
                        }
                    }

                    // Leggi il file JSON estratto
                    val jsonFile = File(tempDir, "data.json")
                    if (!jsonFile.exists()) {
                        throw Exception("data.json non trovato nel backup")
                    }

                    val jsonData = jsonFile.readText()
                    Log.d(TAG, "JSON data letto: ${jsonData.take(100)}...")

                    // Decodifica i dati
                    val backupData = json.decodeFromString<BackupDataDTO>(jsonData)
                    Log.d(TAG, "Backup data decoded: Users=${backupData.users.size}, Wardrobes=${backupData.wardrobes.size}")

                    // Filtra i dati in base alle selezioni dell'utente
                    val selectedUserDtos = backupData.users.filter { it.userId in selectedUsers }
                    
                    // Determina quali armadi includere
                    val neededWardrobeIds = mutableSetOf<Long>()
                    
                    // Se importiamo vestiti o scarpe, raccogliamo gli ID degli armadi necessari
                    if (importClothes) {
                        backupData.clothes
                            .filter { it.userId in selectedUsers }
                            .mapNotNull { it.wardrobeId }
                            .forEach { neededWardrobeIds.add(it) }
                    }
                    
                    if (importShoes) {
                        backupData.shoes
                            .filter { it.userId in selectedUsers }
                            .mapNotNull { it.wardrobeId }
                            .forEach { neededWardrobeIds.add(it) }
                    }
                    
                    // Se l'utente ha selezionato di importare tutti gli armadi, includiamo tutti
                    val selectedWardrobeDtos = if (importWardrobes) {
                        backupData.wardrobes
                    } else {
                        // Altrimenti includiamo solo quelli necessari per vestiti/scarpe
                        backupData.wardrobes.filter { it.wardrobeId in neededWardrobeIds }
                    }
                    
                    // Filtra vestiti e scarpe per userId selezionati
                    val selectedClothesDtos = if (importClothes) {
                        backupData.clothes.filter { it.userId in selectedUsers }
                    } else emptyList()
                    
                    val selectedShoesDtos = if (importShoes) {
                        backupData.shoes.filter { it.userId in selectedUsers }
                    } else emptyList()
                    
                    Log.d(TAG, "Dati filtrati: ${selectedUserDtos.size} utenti, ${selectedWardrobeDtos.size} armadi, " +
                           "${selectedClothesDtos.size} vestiti, ${selectedShoesDtos.size} scarpe")

                    // Prepara la directory delle immagini nell'app
                    val imagesDir = File(context.filesDir, "images")
                    if (selectedClothesDtos.isNotEmpty() || selectedShoesDtos.isNotEmpty()) {
                        imagesDir.mkdirs()
                        Log.d(TAG, "Directory immagini creata: ${imagesDir.absolutePath}")

                        // Crea le directory di destinazione
                        val destClothesDir = File(imagesDir, "clothes")
                        val destShoesDir = File(imagesDir, "shoes")
                        destClothesDir.mkdirs()
                        destShoesDir.mkdirs()
                        
                        // Copia le immagini estratte nella directory dell'app
                        val extractedImagesDir = File(tempDir, "images")
                        if (extractedImagesDir.exists()) {
                            val clothesDir = File(extractedImagesDir, "clothes")
                            val shoesDir = File(extractedImagesDir, "shoes")
                            
                            if (importClothes && clothesDir.exists() && clothesDir.isDirectory) {
                                val clothesFiles = clothesDir.listFiles()
                                Log.d(TAG, "Trovati ${clothesFiles?.size ?: 0} file di vestiti da copiare")
                                clothesFiles?.forEach { file ->
                                    val destFile = File(destClothesDir, file.name)
                                    file.copyTo(destFile, overwrite = true)
                                    Log.d(TAG, "Copiato file ${file.name} in ${destFile.absolutePath}")
                                }
                            }
                            
                            if (importShoes && shoesDir.exists() && shoesDir.isDirectory) {
                                val shoesFiles = shoesDir.listFiles()
                                Log.d(TAG, "Trovati ${shoesFiles?.size ?: 0} file di scarpe da copiare")
                                shoesFiles?.forEach { file ->
                                    val destFile = File(destShoesDir, file.name)
                                    file.copyTo(destFile, overwrite = true)
                                    Log.d(TAG, "Copiato file ${file.name} in ${destFile.absolutePath}")
                                }
                            }
                        }
                    }

                    // Converti i DTO in entità e aggiorna i percorsi delle immagini
                    val users = selectedUserDtos.map { dto ->
                        User(
                            userId = dto.userId,
                            name = dto.name,
                            gender = dto.gender,
                            birthday = try { dateFormat.parse(dto.birthday) ?: Date() } catch (e: Exception) { Date() },
                            createdAt = try { dateFormat.parse(dto.createdAt) ?: Date() } catch (e: Exception) { Date() }
                        )
                    }
                    
                    val wardrobes = selectedWardrobeDtos.map { dto ->
                        Wardrobe(
                            wardrobeId = dto.wardrobeId,
                            name = dto.name,
                            description = dto.description,
                            createdAt = try { dateFormat.parse(dto.createdAt) ?: Date() } catch (e: Exception) { Date() }
                        )
                    }
                    
                    val clothes = selectedClothesDtos.map { dto ->
                        val imageUrl = dto.imageUrl?.let { originalUrl ->
                            try {
                                val fileName = File(Uri.parse(originalUrl).path ?: "").name
                                val newFile = File(imagesDir, "clothes/$fileName")
                                "file://${newFile.absolutePath}"
                            } catch (e: Exception) {
                                Log.e(TAG, "Error updating cloth image path", e)
                                dto.imageUrl
                            }
                        }
                        
                        Clothes(
                            id = dto.id,
                            name = dto.name,
                            category = dto.category,
                            color = dto.color,
                            season = dto.season,
                            position = dto.position,
                            wardrobeId = dto.wardrobeId,
                            userId = dto.userId,
                            imageUrl = imageUrl,
                            createdAt = try { dateFormat.parse(dto.createdAt) ?: Date() } catch (e: Exception) { Date() }
                        )
                    }
                    
                    val shoes = selectedShoesDtos.map { dto ->
                        val imageUrl = dto.imageUrl?.let { originalUrl ->
                            try {
                                val fileName = File(Uri.parse(originalUrl).path ?: "").name
                                val newFile = File(imagesDir, "shoes/$fileName")
                                "file://${newFile.absolutePath}"
                            } catch (e: Exception) {
                                Log.e(TAG, "Error updating shoe image path", e)
                                dto.imageUrl
                            }
                        }
                        
                        Shoes(
                            id = dto.id,
                            name = dto.name,
                            brand = dto.brand,
                            size = dto.size,
                            wardrobeId = dto.wardrobeId,
                            userId = dto.userId,
                            color = dto.color,
                            type = dto.type,
                            season = dto.season,
                            price = dto.price,
                            imageUrl = imageUrl,
                            createdAt = try { dateFormat.parse(dto.createdAt) ?: Date() } catch (e: Exception) { Date() }
                        )
                    }

                    // Inseriamo i dati selezionati nel database
                    var usersCount = 0
                    var wardrobesCount = 0
                    var clothesCount = 0
                    var shoesCount = 0

                    Log.d(TAG, "Inserting users...")
                    users.forEach { user ->
                        try {
                            database.userDao().insertUser(user)
                            usersCount++
                            Log.d(TAG, "User inserted: ${user.name}")
                            
                            // Imposto il primo utente ripristinato come utente corrente
                            if (usersCount == 1) {
                                userRepository.updateCurrentUser(user)
                                Log.d(TAG, "Utente corrente impostato: ${user.name}")
                            }
                        } catch (e: Exception) {
                            Log.e(TAG, "Error inserting user: ${user.name}", e)
                        }
                    }
                    
                    Log.d(TAG, "Inserting wardrobes...")
                    wardrobes.forEach { wardrobe ->
                        try {
                            database.wardrobeDao().insertWardrobe(wardrobe)
                            wardrobesCount++
                            Log.d(TAG, "Wardrobe inserted: ${wardrobe.name}")
                        } catch (e: Exception) {
                            Log.e(TAG, "Error inserting wardrobe: ${wardrobe.name}", e)
                        }
                    }
                    
                    Log.d(TAG, "Inserting clothes...")
                    clothes.forEach { cloth ->
                        try {
                            database.clothesDao().insertCloth(cloth)
                            clothesCount++
                            Log.d(TAG, "Cloth inserted: ${cloth.name}")
                        } catch (e: Exception) {
                            Log.e(TAG, "Error inserting cloth: ${cloth.name}", e)
                        }
                    }
                    
                    Log.d(TAG, "Inserting shoes...")
                    shoes.forEach { shoe ->
                        try {
                            database.shoesDao().insertShoe(shoe)
                            shoesCount++
                            Log.d(TAG, "Shoe inserted: ${shoe.name}")
                        } catch (e: Exception) {
                            Log.e(TAG, "Error inserting shoe: ${shoe.name}", e)
                        }
                    }

                    // Pulizia finale
                    tempDir.deleteRecursively()
                    
                    val summarMsg = "Restore completato: $usersCount utenti, $wardrobesCount armadi, $clothesCount vestiti, $shoesCount scarpe"
                    Log.d(TAG, "=== RESTORE COMPLETATO CON SUCCESSO ===")
                    showToast(summarMsg)
                    
                    _restoreComplete.value = true
                } catch (e: Exception) {
                    Log.e(TAG, "Error during restore", e)
                    showToast("Errore durante il restore: ${e.message}")
                    _restoreError.value = e.message
                    e.printStackTrace()
                }
            }
        }
    }
    
    private fun logDirectoryContents(directory: File) {
        Log.d(TAG, "Contenuto di ${directory.absolutePath}:")
        
        if (!directory.exists()) {
            Log.d(TAG, "  - Directory non esiste")
            return
        }
        
        if (!directory.isDirectory) {
            Log.d(TAG, "  - Non è una directory")
            return
        }
        
        val files = directory.listFiles()
        if (files.isNullOrEmpty()) {
            Log.d(TAG, "  - Directory vuota")
            return
        }
        
        files.forEach { file ->
            if (file.isDirectory) {
                Log.d(TAG, "  - [DIR] ${file.name}")
                logDirectoryContents(file)
            } else {
                Log.d(TAG, "  - [FILE] ${file.name} (${file.length()} bytes)")
            }
        }
    }
    
    private fun showToast(message: String) {
        viewModelScope.launch(Dispatchers.Main) {
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        }
    }
}

// Classi DTO per la serializzazione completa di tutti i campi, inclusi quelli con @Transient
@Serializable
data class BackupDataDTO(
    val users: List<UserDTO>,
    val wardrobes: List<WardrobeDTO>,
    val clothes: List<ClothesDTO>,
    val shoes: List<ShoesDTO>,
    val createdAt: String
)

@Serializable
data class UserDTO(
    val userId: Long = 0,
    val name: String,
    val gender: String,
    val birthday: String,
    val createdAt: String
)

@Serializable
data class WardrobeDTO(
    val wardrobeId: Long = 0,
    val name: String,
    val description: String?,
    val createdAt: String
)

@Serializable
data class ClothesDTO(
    val id: Long = 0,
    val name: String,
    val category: String,
    val color: String,
    val season: String,
    val position: String,
    val wardrobeId: Long? = null,
    val userId: Long? = null,
    val imageUrl: String? = null,
    val createdAt: String
)

@Serializable
data class ShoesDTO(
    val id: Long = 0,
    val name: String,
    val brand: String,
    val size: String,
    val wardrobeId: Long? = null,
    val userId: Long? = null,
    val color: String? = null,
    val type: String?,
    val season: String?,
    val price: Double?,
    val imageUrl: String?,
    val createdAt: String
)

// Classe per contenere le informazioni del backup per il dialogo di selezione
data class BackupInfo(
    val createdAt: Date,
    val users: List<UserDTO>,
    val usersCount: Int,
    val wardrobesCount: Int,
    val clothesCount: Int,
    val shoesCount: Int,
    val hasImages: Boolean,
    val sourceUri: Uri
) 