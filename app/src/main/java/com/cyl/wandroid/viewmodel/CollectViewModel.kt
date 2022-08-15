package com.cyl.wandroid.viewmodel

import androidx.lifecycle.MutableLiveData
import com.cyl.wandroid.base.BaseRecyclerViewModel
import com.cyl.wandroid.common.bus.Bus
import com.cyl.wandroid.common.bus.CANCEL_COLLECT_SUCCESS
import com.cyl.wandroid.common.bus.COLLECT_SUCCESS
import com.cyl.wandroid.http.bean.ArticleBean
import com.cyl.wandroid.http.bean.UserBean
import com.cyl.wandroid.repository.CollectionRepository

open class CollectViewModel : BaseRecyclerViewModel() {
    private val collectRepository by lazy { CollectionRepository() }
    val collectLiveData: MutableLiveData<Pair<Int, Boolean>> = MutableLiveData()

    fun collect(id: Int) {
        launch(block = {
            collectRepository.collect(id)
            collectLiveData.value = id to true// 收藏成功
            Bus.post(COLLECT_SUCCESS, id)
        })
    }

    fun cancelCollectFromArticleList(id: Int) {
        launch(block = {
            collectRepository.cancelCollectFromArticleList(id)
            collectLiveData.value = id to false // 取消成功
            Bus.post(CANCEL_COLLECT_SUCCESS, id)
        })
    }

    fun cancelCollectFromCollectionList(viewModel: MyCollectionsViewModel, id: Int, originId: Int) {
        launch(block = {
            collectRepository.cancelCollectFromCollectionList(id, originId)
            var index = -1
            viewModel.articles.value?.forEachIndexed { i, bean ->
                if (bean.id == id) {
                    index = i
                    return@forEachIndexed
                }
            }
            viewModel.articles.value?.removeAt(index)
            viewModel.deleteCollectLiveData.value = index
            Bus.post(CANCEL_COLLECT_SUCCESS, originId)
        })
    }

    // 更新单个的收藏状态
    fun updateCollectStatus(
        id: Int,
        status: Boolean,
        articles: MutableLiveData<MutableList<ArticleBean>>
    ) {
        val list = articles.value
        if (list.isNullOrEmpty()) return
        list.forEach {
            if (id == it.id) {
                it.collect = status
                return@forEach
            }
        }
        articles.value = list
    }

    // 更新整个文章列表的收藏状态
    fun updateAllCollectsStatusTrue(
        userBean: UserBean,
        articles: MutableLiveData<MutableList<ArticleBean>>
    ) {
        val collectIds = userBean.collectIds
        val list = articles.value
        if (list.isNullOrEmpty()) return
        list.forEach {
            it.collect = collectIds.contains(it.id)
        }
        articles.value = list
    }

    fun updateAllCollectsStatusFalse(articles: MutableLiveData<MutableList<ArticleBean>>) {
        val list = articles.value
        if (list.isNullOrEmpty()) return
        list.forEach {
            it.collect = false
        }
        articles.value = list
    }


    fun removeCollectItem(id: Int, articles: MutableLiveData<MutableList<ArticleBean>>): Int {
        articles.value?.forEachIndexed { index, articleBean ->
            if (id == articleBean.id) {
                articles.value?.removeAt(index)
                return index
            }
        }
        return -1
    }
}