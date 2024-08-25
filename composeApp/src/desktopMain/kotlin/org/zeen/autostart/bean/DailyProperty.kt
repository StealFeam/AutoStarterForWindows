package org.zeen.autostart.bean


/**
 * 请求今日农历信息
 */
data class DailyProperty(
    val code: Int? = null, // 0 成功，1 失败
    val type: DailyType? = null
) {
    val isSuccess: Boolean get() = code == 0
}

/**
 * 今日信息
 */
data class DailyType(
    val type: Int? = null, // (0, 1, 2, 3) 节假日类型，分别表示 工作日、周末、节日、调休。
    val name: String? = null, // 说明
    val week: Int? = null // 周几
) {
    val isWorkDay: Boolean get() = type == 0 || type == 3
}