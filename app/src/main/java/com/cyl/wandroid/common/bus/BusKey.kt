package com.cyl.wandroid.common.bus

// 公众号文章Activity滑动Tab，通知Home页面公众号列表跟着滑动
const val SCROLL_HOME_PUBLIC_ACCOUNT_POSITION = "SCROLL_HOME_PUBLIC_ACCOUNT_POSITION"

// 点击首页Header的项目按钮跳转到项目Fragment
const val JUMP_TO_PROJECT_FRAGMENT = "JUMP_TO_PROJECT_FRAGMENT"

// 注册/登录成功，显示用户信息
const val REFRESH_LOGIN_SUCCESS = "REFRESH_LOGIN_SUCCESS"

// 注册/登录成功，刷新文章列表标记出已收藏文章
const val MARK_COLLECT_LOGIN_SUCCESS = "MARK_COLLECT_LOGIN_SUCCESS"

// 退出登录成功，刷新文章列表标记出已收藏文章
const val MARK_COLLECT_LOGOUT_SUCCESS = "MARK_COLLECT_LOGOUT_SUCCESS"

// 退出登录成功，显示未登录信息
const val REFRESH_LOGOUT_SUCCESS = "REFRESH_LOGOUT_SUCCESS"

// 添加分享成功后跳转到我的分享页面需要刷新获取最新分享数据（仅从我的分享页面进入到分享文章页面才触发，从我的页面进入的不触发）
const val REFRESH_ADD_SHARE_SUCCESS = "REFRESH_ADD_SHARE_SUCCESS"

// 收藏文章成功
const val COLLECT_SUCCESS = "COLLECT_SUCCESS"

// 取消收藏文章成功
const val CANCEL_COLLECT_SUCCESS = "CANCEL_COLLECT_SUCCESS"

// 修改TODO成功
const val UPDATE_TODO_SUCCESS = "UPDATE_TODO_SUCCESS"

// 添加TODO成功
const val ADD_TODO_SUCCESS = "ADD_TODO_SUCCESS"

// 首页TODO状态改变
const val HOME_TODO_STATUS_CHANGED = "HOME_TODO_STATUS_CHANGED"