package pro.fessional.wings.faceless.flywave

import java.util.function.BiConsumer
import java.util.function.Function

/**
 * @author trydofor
 * @since 2021-12-24
 */
interface InteractiveManager<T> {

    /**
     * Whether  to confirm  before `undo`
     * @param ask ask type
     * @param yes whether to confirm, true by default
     * @return old value
     */
    fun needAsk(ask: T, yes: Boolean): Boolean?

    /**
     * How to confirm, pass message, return to continue or stop
     */
    fun askWay(func: Function<String, Boolean>): Function<String, Boolean>?

    /**
     * How to log the runtime token and message
     */
    fun logWay(func: BiConsumer<String, String>): BiConsumer<String, String>?
}
