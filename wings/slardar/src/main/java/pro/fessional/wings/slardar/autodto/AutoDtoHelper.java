package pro.fessional.wings.slardar.autodto;

import pro.fessional.mirana.anti.BeanVisitor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author trydofor
 * @since 2022-10-05
 */
public class AutoDtoHelper {

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
