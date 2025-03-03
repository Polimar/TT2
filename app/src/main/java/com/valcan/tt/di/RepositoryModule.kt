package com.valcan.tt.di

import com.valcan.tt.data.dao.ClothesDao
import com.valcan.tt.data.dao.WardrobeDao
import com.valcan.tt.data.repository.ClothesRepository
import com.valcan.tt.data.repository.WardrobeRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideClothesRepository(clothesDao: ClothesDao): ClothesRepository {
        return ClothesRepository(clothesDao)
    }

    @Provides
    @Singleton
    fun provideWardrobeRepository(wardrobeDao: WardrobeDao): WardrobeRepository {
        return WardrobeRepository(wardrobeDao)
    }
} 