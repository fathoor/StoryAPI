package com.fathoor.storyapi.view.helper

import java.util.concurrent.Executor
import java.util.concurrent.Executors

class AppExecutor {
    val diskIO: Executor = Executors.newSingleThreadExecutor()
}