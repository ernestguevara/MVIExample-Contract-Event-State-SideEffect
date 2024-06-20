package com.simplifier.mviexample_contract_event_state_sideeffect.viewmodel

import com.simplifier.mviexample_contract_event_state_sideeffect.MviEffect
import com.simplifier.mviexample_contract_event_state_sideeffect.MviEvent
import com.simplifier.mviexample_contract_event_state_sideeffect.MviState
import com.simplifier.mviexample_contract_event_state_sideeffect.data.Movie

interface MovieListContract {
    data class State(
        val isLoading: Boolean = false,
        val title: String = "IMDb\n" +
                "Top 10 Movies",
        val movies: List<Movie> = emptyList()
    ) : MviState

    sealed class Event : MviEvent {
        object ScreenStarted : Event()
        object RefreshClicked : Event()
        object ShuffleClicked : Event()
        data class MoviesLoaded(val movies: List<Movie>) : Event()
        data class MovieClicked(val movie: Movie) : Event()
    }

    sealed class Effect : MviEffect {
        object ShowToast : Effect()
        object FinishToast : Effect()
    }
}