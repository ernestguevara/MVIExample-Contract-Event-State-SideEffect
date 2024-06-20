package com.simplifier.mviexample_contract_event_state_sideeffect

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.simplifier.mviexample_contract_event_state_sideeffect.data.Movie
import com.simplifier.mviexample_contract_event_state_sideeffect.ui.theme.MVIExampleContractEventStateSideEffectTheme
import com.simplifier.mviexample_contract_event_state_sideeffect.viewmodel.MovieListContract
import com.simplifier.mviexample_contract_event_state_sideeffect.viewmodel.MovieListViewModel
import kotlinx.coroutines.flow.collectLatest

class MainActivity : ComponentActivity() {


    private val viewModel: MovieListViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MVIExampleContractEventStateSideEffectTheme {
                val state by viewModel.state.collectAsState()

                MoviesListScreen(state, viewModel::setEvent)

                LaunchedEffect(Unit) {
                    viewModel.effect.collectLatest {
                        Log.i("ernesthor24", "onCreate: effect $it")
                    }
                }

            }
        }
    }

    override fun onStart() {
        super.onStart()
        viewModel.setEvent(MovieListContract.Event.ScreenStarted)
    }
}

@Composable
private fun MoviesListScreen(
    state: MovieListContract.State,
    setEvent: (MovieListContract.Event) -> Unit
) = MaterialTheme {
    Box(modifier = Modifier.fillMaxSize()) {
        MoviesList(
            title = state.title,
            movies = state.movies,
            isLoading = state.isLoading,
            setEvent = setEvent
        )

        ShuffleButton(
            setEvent = setEvent,
            modifier = Modifier.align(Alignment.BottomEnd)
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun MoviesList(
    title: String,
    movies: List<Movie>,
    isLoading: Boolean,
    setEvent: (MovieListContract.Event) -> Unit
) {
    SwipeRefresh(
        state = rememberSwipeRefreshState(isRefreshing = isLoading),
        onRefresh = { setEvent(MovieListContract.Event.RefreshClicked) }
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 78.dp)
        ) {
            item {
                MoviesHeader(title)
            }

            items(
                items = movies,
                key = Movie::id
            ) { movie ->
                Movie(
                    movie = movie,
                    setEvent = setEvent,
                    modifier = Modifier.animateItemPlacement()
                )
            }
        }
    }
}

@Composable
private fun MoviesHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.headlineMedium,
        modifier = Modifier.padding(16.dp)
    )
}

@Composable
private fun Movie(
    movie: Movie,
    setEvent: (MovieListContract.Event) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .clickable { setEvent(MovieListContract.Event.MovieClicked(movie)) }
            .padding(horizontal = 16.dp, vertical = 4.dp)
    ) {
        AsyncImage(
            model = movie.imageUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(40.dp, 64.dp)
                .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2F))
        )

        Text(
            text = movie.name,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier
                .weight(1f)
                .padding(start = 8.dp)
        )

        val imageResource = when (movie.isSelected) {
            true -> R.drawable.ic_star
            false -> R.drawable.ic_star_outline
        }

        Icon(
            painter = painterResource(imageResource),
            contentDescription = null,
            tint = Color(0xFFFFC107),
            modifier = Modifier.size(32.dp)
        )
    }
}

@Composable
private fun ShuffleButton(
    setEvent: (MovieListContract.Event) -> Unit,
    modifier: Modifier = Modifier,
) {
    Icon(
        painter = painterResource(id = R.drawable.ic_shuffle),
        contentDescription = null,
        tint = Color.White,
        modifier = modifier
            .padding(16.dp)
            .size(56.dp)
            .shadow(4.dp, CircleShape)
            .background(MaterialTheme.colorScheme.secondary)
            .clip(CircleShape)
            .clickable { setEvent(MovieListContract.Event.ShuffleClicked) }
            .padding(12.dp)
    )
}
