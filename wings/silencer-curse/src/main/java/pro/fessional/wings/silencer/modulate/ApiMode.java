package pro.fessional.wings.silencer.modulate;

/**
 * @author trydofor
 * @since 2022-03-11
 */
public enum ApiMode {
    /**
     * alive 正式
     */
    Online,
    /**
     * 测试，沙盒
     */
    Sandbox,
    /**
     * 跑通测试，最后rollback
     */
    Dryrun,
    /**
     * 不执行任何动作
     */
    Nothing
}
