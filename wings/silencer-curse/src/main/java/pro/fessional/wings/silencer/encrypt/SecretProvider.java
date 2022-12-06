package pro.fessional.wings.silencer.encrypt;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import pro.fessional.mirana.code.RandCode;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 系统密码提供者，默认256bit，32字符
 *
 * @author trydofor
 * @since 2022-12-05
 */
public class SecretProvider {

    /**
     * 默认密码的字符串长度
     */
    public static final int Length = 32;

    /**
     * 系统默认，每次系统启动时随机生成，停机后消失。
     */
    public static final String System = "system";

    /**
     * 用于Api Ticket，建议集群内统一
     */
    public static final String Ticket = "ticket";

    /**
     * 用于 Http Cookie，建议集群内统一
     */
    public static final String Cookie = "cookie";
    /**
     * 用于 配置文件中敏感数据，建议固定
     */
    public static final String Config = "config";


    /**
     * 生成len长度的字母大小写和数字符号密码
     *
     * @see RandCode#strong(int)
     */
    @NotNull
    public static String strong(int len) {
        return RandCode.strong(len);
    }

    /**
     * 生成len长度的字母大小写和数字可读性好的密码。 供32个英数，去掉了30个(0oO,1il,cC,j,kK,mM,nN,pP,sS,uU,vV,wW,xX,y,zZ)
     *
     * @see RandCode#human(int)
     */
    @NotNull
    public static String human(int len) {
        return RandCode.human(len);
    }

    /**
     * 获取name对应的secret，若没有则生成并保存一个默认Length的密码
     */
    @NotNull
    public static String get(String name) {
        if (name == null || name.isEmpty()) {
            name = System;
        }

        return Secrets.computeIfAbsent(name, k -> RandCode.strong(Length));
    }

    /**
     * 获取name对应的secret，指定非空时，若没有则生成并保存一个默认Length的密码
     */
    @Contract("_,true->!null")
    public static String get(String name, boolean computeIfAbsent) {
        if (name == null || name.isEmpty()) {
            name = System;
        }

        if (computeIfAbsent) {
            return Secrets.computeIfAbsent(name, k -> RandCode.strong(Length));
        }
        else {
            return Secrets.get(name);
        }
    }

    //
    protected static final ConcurrentHashMap<String, String> Secrets = new ConcurrentHashMap<>();

    /**
     * 初始一个name的secret
     */
    protected static void put(@NotNull String name, @NotNull String secret, boolean replace) {
        if (replace) {
            Secrets.put(name, secret);
        }
        else {
            Secrets.putIfAbsent(name, secret);
        }
    }
}
