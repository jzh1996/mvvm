package com.jzh.mvvm.httpUtils

import java.io.Serializable

data class ResponseData<out T>(
    val errorCode: Int,
    val errorMsg: String,
    val data: T
)

//轮播图
data class Banner(
    val desc: String,
    val id: Int,
    val imagePath: String,
    val isVisible: Int,
    val order: Int,
    val title: String,
    val type: Int,
    val url: String
)

// 用户个人信息
data class UserInfoBody(
    val coinCount: Int, // 总积分
    val rank: Int, // 当前排名
    val userId: Int,
    val username: String
)

//文章
data class Article(
    val apkLink: String,
    val audit: Int,
    val author: String,
    val chapterId: Int,
    val chapterName: String,
    var collect: Boolean,
    val courseId: Int,
    val desc: String,
    val envelopePic: String,
    val fresh: Boolean,
    val id: Int,
    val link: String,
    val niceDate: String,
    val niceShareDate: String,
    val origin: String,
    val prefix: String,
    val projectLink: String,
    val publishTime: Long,
    val shareDate: String,
    val shareUser: String,
    val superChapterId: Int,
    val superChapterName: String,
    val tags: MutableList<Tag>,
    val title: String,
    val type: Int,
    val userId: Int,
    val visible: Int,
    val zan: Int,
    var top: String
)

// 通用的带有列表数据的实体
data class BaseListResponseBody<T>(
    val curPage: Int,
    val datas: List<T>,
    val offset: Int,
    val over: Boolean,
    val pageCount: Int,
    val size: Int,
    val total: Int
)

//文章
data class ArticleResponseBody(
    val curPage: Int,
    var datas: MutableList<Article>,
    val offset: Int,
    val over: Boolean,
    val pageCount: Int,
    val size: Int,
    val total: Int
)

data class Tag(
    val name: String,
    val url: String
)

// 排行榜实体
data class CoinInfoBean(
    val coinCount: Int,
    val level: Int,
    val rank: Int,
    val userId: Int,
    val username: String,
    var anim: Boolean = false
)

// 个人积分实体
data class UserScoreBean(
    val coinCount: Int,
    val date: Long,
    val desc: String,
    val id: Int,
    val reason: String,
    val type: Int,
    val userId: Int,
    val userName: String
)

//知识体系
data class KnowledgeTreeBody(
    val children: MutableList<Knowledge>,
    val courseId: Int,
    val id: Int,
    val name: String,
    val order: Int,
    val parentChapterId: Int,
    val visible: Int
) : Serializable

data class Knowledge(
    val children: List<Any>,
    val courseId: Int,
    val id: Int,
    val name: String,
    val order: Int,
    val parentChapterId: Int,
    val visible: Int
) : Serializable

// 导航
data class NavigationBean(
    val articles: MutableList<Article>,
    val cid: Int,
    val name: String
)

// 公众号列表实体
data class WXChapterBean(
    val children: MutableList<String>,
    val courseId: Int,
    val id: Int,
    val name: String,
    val order: Int,
    val parentChapterId: Int,
    val userControlSetTop: Boolean,
    val visible: Int
)

//问答
data class ArticleListBean(
    var curPage: Int,
    var offset: Int,
    var over: Boolean,
    var pageCount: Int,
    var size: Int,
    var total: Int,
    var datas: List<Article>
)

// 我的分享
data class ShareResponseBody(
    val coinInfo: CoinInfoBean,
    val shareArticles: ArticleResponseBody
)

//登录
data class LoginData(
    val chapterTops: MutableList<String>,
    val collectIds: MutableList<String>,
    val email: String,
    val icon: String,
    val id: Int,
    val password: String,
    val token: String,
    val type: Int,
    val username: String
)

// 热门搜索
data class HotSearchBean(
    val id: Int,
    val link: String,
    val name: String,
    val order: Int,
    val visible: Int
)

data class CollectionArticle(
    val author: String,
    val chapterId: Int,
    val chapterName: String,
    val courseId: Int,
    val desc: String,
    val envelopePic: String,
    val id: Int,
    val link: String,
    val niceDate: String,
    val origin: String,
    val originId: Int,
    val publishTime: Long,
    val title: String,
    val userId: Int,
    val visible: Int,
    val zan: Int
)

// TODO实体类
data class TodoBean(
    val id: Int,
    val completeDate: String,
    val completeDateStr: String,
    val content: String,
    val date: Long,
    val dateStr: String,
    var status: Int,
    val title: String,
    val type: Int,
    val userId: Int,
    val priority: Int
) : Serializable

data class TodoResponseBody(
    val curPage: Int,
    val datas: MutableList<TodoBean>,
    val offset: Int,
    val over: Boolean,
    val pageCount: Int,
    val size: Int,
    val total: Int
)
