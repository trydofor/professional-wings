package pro.fessional.wings.warlock.service.auth.help;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;
import pro.fessional.wings.slardar.security.impl.DefaultWingsUserDetails;
import pro.fessional.wings.warlock.service.auth.WarlockAuthnService;

/**
 * Details to DefaultWingsUserDetails mapper, auto generate by `wgmp` live template
 *
 * @author trydofor
 * @since 2021-02-23
 */
@Mapper
public interface AuthnDetailsMapper {

    AuthnDetailsMapper INSTANCE = Mappers.getMapper(AuthnDetailsMapper.class);

    /**
     * create new DefaultWingsUserDetails by Details
     *
     * @param a Details
     * @return DefaultWingsUserDetails
     */
    static DefaultWingsUserDetails into(WarlockAuthnService.Details a) {
        return into(a, new DefaultWingsUserDetails());
    }

    /**
     * build DefaultWingsUserDetails with Details
     *
     * @param a Details
     * @param b DefaultWingsUserDetails
     */
    static DefaultWingsUserDetails into(WarlockAuthnService.Details a, DefaultWingsUserDetails b) {
        INSTANCE._into(a, b);
        return b;
    }

    @Mapping(target = "preAuthed", ignore = true)
    @Mapping(target = "enabled", ignore = true)
    @Mapping(target = "credentialsNonExpired", ignore = true)
    @Mapping(target = "authorities", ignore = true)
    @Mapping(target = "accountNonLocked", ignore = true)
    @Mapping(target = "accountNonExpired", ignore = true)
    void _into(WarlockAuthnService.Details a, @MappingTarget DefaultWingsUserDetails b);
}
