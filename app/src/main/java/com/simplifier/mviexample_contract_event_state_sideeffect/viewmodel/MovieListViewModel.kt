package com.simplifier.mviexample_contract_event_state_sideeffect.viewmodel

import androidx.lifecycle.viewModelScope
import com.simplifier.mviexample_contract_event_state_sideeffect.BaseMVIViewModel
import com.simplifier.mviexample_contract_event_state_sideeffect.data.copySelectionFrom
import com.simplifier.mviexample_contract_event_state_sideeffect.data.toggleSelection
import com.simplifier.mviexample_contract_event_state_sideeffect.logic.FetchMovies
import kotlinx.coroutines.launch

class MovieListViewModel(val fetchMovies: FetchMovies = FetchMovies()) :
    BaseMVIViewModel<MovieListContract.State, MovieListContract.Event, MovieListContract.Effect>(
        MovieListContract.State()
    ) {

    public override suspend fun handleEvent(event: MovieListContract.Event) {
        when (event) {
            is MovieListContract.Event.ScreenStarted,
            is MovieListContract.Event.RefreshClicked -> {
                refreshMovies()
                setState {
                    copy(isLoading = true)
                }
                setEffect {
                    MovieListContract.Effect.ShowToast
                }
            }

            is MovieListContract.Event.MovieClicked -> {
                val clickedId = event.movie.id
                val updatedMovies = state.value.movies.toggleSelection(clickedId)
                setState {
                    copy(movies = updatedMovies)
                }
            }

            is MovieListContract.Event.ShuffleClicked -> {
                val shuffledMovies = state.value.movies.shuffled()
                setState {
                    copy(movies = shuffledMovies)
                }
            }

            is MovieListContract.Event.MoviesLoaded -> {
                val newMovies = event.movies
                val currentMovies = state.value.movies
                val updatedMovies = newMovies.copySelectionFrom(currentMovies)

                setState {
                    copy(isLoading = false, movies = updatedMovies)
                }
                setEffect {
                    MovieListContract.Effect.FinishToast
                }
            }
        }
    }

    private fun refreshMovies() = viewModelScope.launch {
        val movies = fetchMovies()
        setEvent(MovieListContract.Event.MoviesLoaded(movies))
    }
}