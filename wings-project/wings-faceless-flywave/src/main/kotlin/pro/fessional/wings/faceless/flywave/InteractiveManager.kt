package pro.fessional.wings.faceless.flywave

import java.util.function.BiConsumer
import java.util.function.Function

/**
 * @author trydofor
 * @since 2021-12-24
 */
interface InteractiveManager<T> {

    /**
     * 当执行undo的时候，是否控制台确认
     * @param ask ask类型
     * @param yes 是否确认，默认true
     * @return 旧值
     */
    fun needAsk(ask: T, yes: Boolean): Boolean?

    /**
     * 用什么方式确认，传递message，返回继续or停止
     */
    fun askWay(func: Function<String, Boolean>): Function<String, Boolean>?

    /**
     * 用什么方式处理，运行时的 token, message
     */
    fun logWay(func: BiConsumer<String, String>): BiConsumer<String, String>?
}
