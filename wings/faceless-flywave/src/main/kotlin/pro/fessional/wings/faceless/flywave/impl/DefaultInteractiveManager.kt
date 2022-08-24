package pro.fessional.wings.faceless.flywave.impl

import org.slf4j.Logger
import org.slf4j.event.Level
import pro.fessional.wings.faceless.database.helper.DatabaseChecker
import pro.fessional.wings.faceless.flywave.InteractiveManager
import pro.fessional.wings.faceless.util.FlywaveInteractiveTty
import java.util.concurrent.atomic.AtomicReference
import java.util.function.BiConsumer
import java.util.function.Function
import javax.sql.DataSource

/**
 * @author trydofor
 * @since 2021-12-24
 */
class DefaultInteractiveManager<T>(
    private val log: Logger,
    private val plainDataSources: Map<String, DataSource>,
    private val emoji: String = "🐝"
) : InteractiveManager<T> {

    private val askType = HashMap<T, Boolean>()

    private var msgFunc: BiConsumer<String, String> = FlywaveInteractiveTty.logNil
    private var askFunc: Function<String, Boolean> = FlywaveInteractiveTty.askYes

    val lastMessage = AtomicReference<Pair<String, String>>()
    fun log(level: Level, where: String, info: String, er: Exception? = null) {
        val msg = "[$where] $emoji $info"
        if (level == Level.ERROR) {
            if (er == null) {
                log.error(msg)
            } else {
                log.error(msg, er)
            }
        } else if (level == Level.WARN) {
            if (er == null) {
                log.warn(msg)
            } else {
                log.warn(msg, er)
            }
        } else {
            if (er == null) {
                log.info(msg)
            } else {
                log.info(msg, er)
            }
        }

        val tkn = "${level.name}|$where"
        val fst = lastMessage.get() == null
        lastMessage.set(tkn to info)
        msgFunc.accept(tkn, info)

        if (fst) {
            log.info("[askSegment]🐝🙀 if no console, add '-Deditable.java.test.console=true' ('Help' > 'Edit Custom VM Options...')")

            if (plainDataSources.isNotEmpty()) {
                val bd = StringBuffer("apply to databases?")
                for ((k, v) in plainDataSources) {
                    bd.append("\nname=").append(k)
                    bd.append("\njdbc=").append(DatabaseChecker.extractJdbcUrl(v))
                }
                ask(bd.toString())
            }
        }
    }

    fun ask(msg: String, quit: Boolean = true): Boolean {
        val y = askFunc.apply(msg)
        if (y) {
            log(Level.INFO, "Confirm-Yes", msg)
        } else {
            if (quit) {
                log(Level.ERROR, "Confirm-NO", msg)
                throw RuntimeException("Confirm-NO $msg")
            } else {
                log(Level.WARN, "Confirm-NO", msg)
            }
        }
        return y
    }

    fun needAsk(ask: T): Boolean {
        return askType[ask] != false
    }

    override fun needAsk(ask: T, yes: Boolean): Boolean? {
        return askType.put(ask, yes)
    }

    override fun askWay(func: Function<String, Boolean>): Function<String, Boolean> {
        val old = askFunc
        askFunc = func
        return old
    }

    override fun logWay(func: BiConsumer<String, String>): BiConsumer<String, String> {
        val old = msgFunc
        msgFunc = func
        return old
    }
}
