package com.galaxy.galaxynet.di

import com.galaxy.galaxynet.data.local.SessionManager
import com.galaxy.galaxynet.data.local.SessionManagerImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
abstract class BindModule {
    @Binds
    abstract fun bindSessionManager(manager: SessionManagerImpl): SessionManager

}