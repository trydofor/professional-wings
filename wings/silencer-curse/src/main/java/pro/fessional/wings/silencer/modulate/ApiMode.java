package pro.fessional.wings.silencer.modulate;

/**
 * @author trydofor
 * @since 2022-03-11
 */
public enum ApiMode {
    /**
     * Alive, production
     */
    Online,
    /**
     * Sandbox, testing
     */
    Sandbox,
    /**
     * Dry running, rollback at the end
     */
    Dryrun,
    /**
     * Undefined, no action is performed
     */
    Nothing
}
