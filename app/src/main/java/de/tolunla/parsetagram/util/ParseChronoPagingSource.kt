package de.tolunla.parsetagram.util

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.parse.ParseObject
import com.parse.ParseQuery

class ParseChronoPagingSource(private val query: ParseQuery<ParseObject>) :
    PagingSource<Int, ParseObject>() {
    // The refresh key is used for subsequent refresh calls to PagingSource.load after the initial load
    override fun getRefreshKey(state: PagingState<Int, ParseObject>): Int? {
        // We need to get the previous key (or next key if previous is null) of the page
        // that was closest to the most recently accessed index.
        // Anchor position is the most recently accessed index
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ParseObject> {
        val position = params.key ?: 0

        query.skip = position
        query.limit = params.loadSize
        query.orderByDescending("createdAt")

        return try {
            val objects = query.find()

            val nextKey = if (objects.isEmpty()) {
                null
            } else {
                position + objects.size
            }

            Log.d(this::class.java.name, objects.last().createdAt.time.toString())

            LoadResult.Page(
                data = objects,
                prevKey = if (position == 0) null else position,
                nextKey = nextKey
            )
        } catch (e: Exception) {
            return LoadResult.Error(e)
        }
    }
}
