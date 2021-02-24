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
public interface DetailsMapper {

    DetailsMapper INSTANCE = Mappers.getMapper(DetailsMapper.class);

    /**
     * create new DefaultWingsUserDetails by Details
     *
     * @param a Details
     * @return DefaultWingsUserDetails
     */
    static DefaultWingsUserDetails into(WarlockAuthnService.Details a) {
        return INSTANCE._into(a);
    }

    /**
     * build DefaultWingsUserDetails with Details
     *
     * @param a Details
     * @param b DefaultWingsUserDetails
     */
    static void into(WarlockAuthnService.Details a, DefaultWingsUserDetails b) {
        INSTANCE._into(a, b);
    }


    @Mapping(target = "preAuthed", ignore = true)
    @Mapping(target = "enabled", ignore = true)
    @Mapping(target = "credentialsNonExpired", ignore = true)
    @Mapping(target = "authorities", ignore = true)
    @Mapping(target = "accountNonLocked", ignore = true)
    @Mapping(target = "accountNonExpired", ignore = true)
    DefaultWingsUserDetails _into(WarlockAuthnService.Details a);


    @Mapping(target = "preAuthed", ignore = true)
    @Mapping(target = "enabled", ignore = true)
    @Mapping(target = "credentialsNonExpired", ignore = true)
    @Mapping(target = "authorities", ignore = true)
    @Mapping(target = "accountNonLocked", ignore = true)
    @Mapping(target = "accountNonExpired", ignore = true)
    void _into(WarlockAuthnService.Details a, @MappingTarget DefaultWingsUserDetails b);
}
