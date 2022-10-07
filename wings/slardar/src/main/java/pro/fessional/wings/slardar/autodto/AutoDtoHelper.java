package pro.fessional.wings.slardar.autodto;

import org.springframework.context.i18n.LocaleContextHolder;
import pro.fessional.mirana.anti.BeanVisitor;
import pro.fessional.wings.slardar.context.TerminalContext;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Supplier;

/**
 * @author trydofor
 * @since 2022-10-05
 */
public class AutoDtoHelper {

    public static final Supplier<ZoneId> ZoneIdSupplier = () -> {
        final ZoneId zid;
        if (TerminalContext.isActive()) {
            zid = TerminalContext.get().getZoneId();
        }
        else {
            zid = LocaleContextHolder.getTimeZone().toZoneId();
        }
        return zid != null ? zid : ZoneId.systemDefault();
    };

    public static final Supplier<Locale> LocaleSupplier = () -> {
        final Locale lcl;
        if (TerminalContext.isActive()) {
            lcl = TerminalContext.get().getLocale();
        }
        else {
            lcl = LocaleContextHolder.getLocale();
        }
        return lcl != null ? lcl : Locale.getDefault();
    };

    public static final AutoDtoVisitor AutoDtoVisitor = new AutoDtoVisitor();

    protected static final List<BeanVisitor.Vzt> RequestVisitor = new ArrayList<>();
    protected static final List<BeanVisitor.Vzt> ResponseVisitor = new ArrayList<>();

    public static void autoRequest(Object bean) {
        BeanVisitor.visit(bean, RequestVisitor);
    }

    public static void autoResponse(Object bean) {
        BeanVisitor.visit(bean, ResponseVisitor);
    }
}
