package com.depromeet.sloth.data.paging

import androidx.paging.LoadState.Loading.endOfPaginationReached
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.depromeet.sloth.data.model.response.notification.NotificationListResponse
import com.depromeet.sloth.data.network.service.NotificationService
import com.depromeet.sloth.util.PAGING_SIZE
import com.depromeet.sloth.util.STARTING_PAGE_INDEX
import retrofit2.HttpException
import java.io.IOException

class NotificationPagingSource(
    private val notificationApi: NotificationService
) : PagingSource<Int, NotificationListResponse>() {

    // 페이지를 갱신 할때 수행 되는 함수
    // key 의 초기값은 null, load 함수 참고
    override fun getRefreshKey(state: PagingState<Int, NotificationListResponse>): Int? {
        // 가장 최근의 접근한 page 를 state.anchorPosition 으로 받고
        // 그 주위의 페이지를 읽어 오도록 키를 반환 해주는 역할
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    // pager 가 데이터를 호출할 때마다 불리는 함수
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, NotificationListResponse> {
        return try {
            val pageNumber = params.key ?: STARTING_PAGE_INDEX
            val response = notificationApi.fetchNotificationList(pageNumber, params.loadSize)
            // TODO api response parameter 로 isEnd 를 제공해 줌 이 값을 통해 마지막 페이지인 여부를 판단할 수 있음
            //  또는 total pages 를 내려줘야 isEnd 인지 판단할 수 있음
            /*
            val endOfPaginationReached = response?.body()?.meta?.isEnd!!
             */
            val data = response?.body()?: emptyList()
            val prevKey = if (pageNumber == STARTING_PAGE_INDEX)

            // 첫번째 키 이기 때문에 그 전 key null
                null else pageNumber - 1
            val nextKey = if (endOfPaginationReached) {
                // 마지막 키이기 때문에 그 다음 key null
                null
            } else {
                pageNumber + (params.loadSize / PAGING_SIZE)
            }
            // 반환
            LoadResult.Page(
                data = data,
                prevKey = prevKey,
                nextKey = nextKey
            )
        } catch (exception: IOException) {
            LoadResult.Error(exception)
        } catch (exception: HttpException) {
            LoadResult.Error(exception)
        }
    }
}