package pro.fessional.wings.faceless.enums;

import org.jetbrains.annotations.NotNull;

/**
 * <pre>
 * Constant enum for programming convenience, to be consistent with sys_constant_enum in database.
 *
 * `SUPER`, a convention value, represents this group and has 3 characteristics
 * - the name is `SUPER`
 * - `id` ends with `00`
 * - `code` is either `id` or `code`
 * </pre>
 *
 * @author trydofor
 * @since 2019-09-17
 */
public interface ConstantEnum {

    /**
     * dynamic id has 9+ digits, static id has 8 digits;
     * `3-2-2` segmentation recommended (table-group-value);
     * `00` ending is SUPER
     */
    int getId();

    /**
     * Enum grouping: same type for same enum, auto Pascal naming
     */
    @NotNull
    default String getType() {
        return "";
    }

    @NotNull
    default String getInfo() {
        return "";
    }

    /**
     * Whether end with `00`
     */
    default boolean isSuper() {
        return getId() % 100 == 0;
    }

    /**
     * Whether in same Super (same group)
     */
    default boolean sameSuper(long id) {
        return id % 100 == getId() % 100;
    }

    default boolean sameSuper(ConstantEnum e) {
        return e != null && sameSuper(e.getId());
    }

    default int getSuperId() {
        return (getId() / 100) * 100;
    }

    /**
     * Whether a standard database compatible x-2-2 format
     */
    default boolean isStandard() {
        return getId() >= 100_00_00;
    }
}
