package com.cyl.wandroid.ui.fragment

import android.graphics.Color
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.cyl.wandroid.R
import com.cyl.wandroid.base.BaseRecyclerViewModelFragment
import com.cyl.wandroid.http.bean.NavigationBean
import com.cyl.wandroid.listener.OnTagClickListener
import com.cyl.wandroid.tools.imgTint
import com.cyl.wandroid.tools.showError
import com.cyl.wandroid.tools.start
import com.cyl.wandroid.ui.activity.AgentWebActivity
import com.cyl.wandroid.ui.adapter.NavigationAdapter
import com.cyl.wandroid.ui.dialog.LocateTagViewDialog
import com.cyl.wandroid.ui.widget.SectionItemDecoration
import com.cyl.wandroid.ui.widget.SmoothTopScroller
import com.cyl.wandroid.viewmodel.NavigationViewModel
import kotlinx.android.synthetic.main.layout_swipe_recycler.*
import kotlinx.android.synthetic.main.toolbar_fragment.*

/**
 * 导航
 */
class NavigationFragment : BaseRecyclerViewModelFragment<NavigationBean, NavigationViewModel>(),
    OnTagClickListener {
    private lateinit var adapter: NavigationAdapter
    private lateinit var sectionItemDecoration: SectionItemDecoration
    override fun getLayoutRes() = R.layout.fragment_navigation

    override fun lazyInitData() {
        mViewModel.getNavigation()
    }

    override fun getAdapter(): BaseQuickAdapter<NavigationBean, BaseViewHolder> = adapter

    override fun getSwipeRefreshLayout(): SwipeRefreshLayout = swipeRefreshLayout

    override fun initView() {
        setCenterText(R.string.home_navigation)
        setRightIcon(R.mipmap.icon_system_locate)
        imgTint(ivRight, Color.BLACK)
        super.initView()
    }

    override fun initRecyclerView() {
        val manager = LinearLayoutManager(mContext)
        recyclerView.layoutManager = manager
        adapter = NavigationAdapter(listener = this)
        adapter.setOnTagClickListener(this)
        recyclerView.adapter = adapter
        sectionItemDecoration = SectionItemDecoration(mContext)
        recyclerView.addItemDecoration(sectionItemDecoration)
    }

    override fun initRefreshLayout() {
        swipeRefreshLayout.setOnRefreshListener { mViewModel.getNavigation() }
    }

    override fun observe() {
        super.observe()
        mViewModel.apply {
            navigation.observe(viewLifecycleOwner, Observer {
                sectionItemDecoration.setDataList(it)
                adapter.setList(it)
            })
        }
    }

    override fun getViewModelClass() = NavigationViewModel::class.java

    override fun onTagClick(itemPosition: Int, tagPosition: Int) {
        start(mContext, AgentWebActivity::class.java, Bundle().apply {
            putString(
                AgentWebActivity.URL,
                mViewModel.navigation.value?.get(itemPosition)?.articles?.get(tagPosition)?.link
            )
        })
    }

    override fun onRightIconClick() {
        super.onRightIconClick()
        val navigation: List<NavigationBean>? = mViewModel.navigation.value
        if (navigation.isNullOrEmpty()) {
            // 没有数据
            showError(R.string.no_valid_data)
        } else {
            val titles = getTitles(navigation)
            LocateTagViewDialog(mContext, titles,
                object : OnTagClickListener {
                    override fun onTagClick(itemPosition: Int, tagPosition: Int) {
                        val scroller = SmoothTopScroller(mContext)
                        scroller.targetPosition = itemPosition
                        recyclerView.layoutManager?.startSmoothScroll(scroller)
                    }
                }).show()
        }
    }

    private fun getTitles(navigation: List<NavigationBean>?): List<String> {
        if (navigation.isNullOrEmpty()) return emptyList()
        val titles = mutableListOf<String>()
        navigation.forEach {
            titles.add(it.name)
        }
        return titles
    }
}
