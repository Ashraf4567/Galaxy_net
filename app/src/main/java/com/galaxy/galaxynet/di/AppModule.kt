package com.galaxy.galaxynet.di

import android.content.Context
import android.content.SharedPreferences
import android.net.ConnectivityManager
import androidx.room.Room
import com.galaxy.galaxynet.data.TransactionRepo
import com.galaxy.galaxynet.data.TransactionsRepoImpl
import com.galaxy.galaxynet.data.ip.ipRepo.IPRepositoryImpl
import com.galaxy.galaxynet.data.ip.ipRepo.IpRepository
import com.galaxy.galaxynet.data.tasksRepo.TasksRepository
import com.galaxy.galaxynet.data.tasksRepo.TasksRepositoryImpl
import com.galaxy.galaxynet.data.usersRepo.UsersRepository
import com.galaxy.galaxynet.data.usersRepo.UsersRepositoryImpl
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    fun provideSharedPref(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences("user_data", 0)
    }

    @Provides
    @Singleton
    fun provideFireStoreInstance(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }

    @Provides
    @Singleton
    fun provideFirebaseAuthInstance(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

    @Provides
    fun provideUsersRepository(firebaseFirestore: FirebaseFirestore): UsersRepository {
        return UsersRepositoryImpl(firebaseFirestore)
    }

    @Provides
    fun provideTasksRepository(firebaseFirestore: FirebaseFirestore): TasksRepository {
        return TasksRepositoryImpl(firebaseFirestore)
    }

    @Provides
    fun provideIpRepo(
        firebaseFirestore: FirebaseFirestore,
    ): IpRepository {
        return IPRepositoryImpl(firebaseFirestore)
    }
    @Provides
    fun provideTransactionRepo(firebaseFirestore: FirebaseFirestore): TransactionRepo{
        return TransactionsRepoImpl(firebaseFirestore)
    }

}