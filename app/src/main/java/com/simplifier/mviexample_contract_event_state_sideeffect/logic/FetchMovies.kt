package com.simplifier.mviexample_contract_event_state_sideeffect.logic

import com.simplifier.mviexample_contract_event_state_sideeffect.data.Movie
import com.simplifier.mviexample_contract_event_state_sideeffect.data.moviesJson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlin.random.Random

class FetchMovies {

    suspend operator fun invoke(): List<Movie> = withContext(Dispatchers.IO) {
        delay(Random.nextLong(300, 2000))
        Json.decodeFromString(moviesJson)
    }
}
