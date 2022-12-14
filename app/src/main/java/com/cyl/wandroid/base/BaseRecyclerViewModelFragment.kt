package com.cyl.wandroid.base

import android.os.Bundle
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.loadmore.LoadMoreStatus
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.cyl.wandroid.common.bus.*
import com.cyl.wandroid.http.bean.ArticleBean
import com.cyl.wandroid.http.bean.UserBean
import com.cyl.wandroid.tools.start
import com.cyl.wandroid.ui.activity.AgentWebActivity
import com.cyl.wandroid.viewmodel.CollectViewModel

abstract class BaseRecyclerViewModelFragment<T, VM : BaseRecyclerViewModel> :
    BaseViewModelFragment<VM>() {

    override fun observe() {
        observeStatus()

        if (mViewModel is CollectViewModel) {
            (mViewModel as CollectViewModel).collectLiveData.observe(viewLifecycleOwner, Observer {
                (mViewModel as CollectViewModel).updateCollectStatus(
                    it.first,
                    it.second,
                    getViewModelArticles()
                )
            })
        }
    }

    private fun observeStatus() {
        mViewModel.loadMoreStatus.observe(viewLifecycleOwner, Observer {
            when (it) {
                LoadMoreStatus.Complete -> getAdapter().loadMoreModule.loadMoreComplete()
                LoadMoreStatus.Fail -> getAdapter().loadMoreModule.loadMoreFail()
                LoadMoreStatus.End -> getAdapter().loadMoreModule.loadMoreEnd()
                else -> return@Observer
            }
        })

        mViewModel.refreshStatus.observe(viewLifecycleOwner, Observer {
            getSwipeRefreshLayout().isRefreshing = it
        })
    }

    override fun initView() {
        initRecyclerView()
        initRefreshLayout()
        collectBusObserve()
    }

    abstract fun getAdapter(): BaseQuickAdapter<T, BaseViewHolder>
    abstract fun getSwipeRefreshLayout(): SwipeRefreshLayout
    abstract fun initRecyclerView()
    abstract fun initRefreshLayout()

    // ?????????????????????
    private fun collectBusObserve() {
        Bus.observe<Int>(COLLECT_SUCCESS, viewLifecycleOwner, observer = {
            // it?????????id
            (mViewModel as CollectViewModel).updateCollectStatus(it, true, getViewModelArticles())
        })
        Bus.observe<Int>(CANCEL_COLLECT_SUCCESS, viewLifecycleOwner, observer = {
            // it?????????id
            (mViewModel as CollectViewModel).updateCollectStatus(it, false, getViewModelArticles())
        })
        // ??????/??????????????????????????????????????????
        Bus.observe<UserBean>(MARK_COLLECT_LOGIN_SUCCESS, viewLifecycleOwner, observer = {
            (mViewModel as CollectViewModel).updateAllCollectsStatusTrue(
                it, getViewModelArticles()
            )
        })
        // ?????????????????????????????????false
        Bus.observe<Boolean>(MARK_COLLECT_LOGOUT_SUCCESS, viewLifecycleOwner, observer = {
            (mViewModel as CollectViewModel).updateAllCollectsStatusFalse(getViewModelArticles())
        })
    }

    // ??????????????????????????????????????????
    open fun getViewModelArticles(): MutableLiveData<MutableList<ArticleBean>> {
        return MutableLiveData()
    }

    // ???????????????????????????????????????????????????
    open fun collectItemChildClick(position: Int) {
        val viewModel = mViewModel as CollectViewModel
        getViewModelArticles().value?.get(position)?.let {
            if (it.collect) {
                viewModel.cancelCollectFromArticleList(it.id)
            } else {
                viewModel.collect(it.id)
            }
        }
    }

    open fun onItemClickToAgentWeb(position: Int) {
        start(mContext, AgentWebActivity::class.java, Bundle().apply {
            val articles = getViewModelArticles().value
            // ??????
            putString(AgentWebActivity.URL, articles?.get(position)?.link)
            // ??????
            articles?.get(position)?.collect?.let {
                putBoolean(AgentWebActivity.IS_COLLECT, it)
            }
            // id
            articles?.get(position)?.id?.let { putInt(AgentWebActivity.ID, it) }
        })
    }
}