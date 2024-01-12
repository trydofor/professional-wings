package pro.fessional.wings.batrider.servcomb.handler;

import org.apache.servicecomb.authentication.provider.ProviderAuthFilter;
import org.apache.servicecomb.core.Invocation;
import org.apache.servicecomb.core.filter.FilterNode;
import org.apache.servicecomb.swagger.invocation.Response;
import pro.fessional.mirana.func.Dcl;
import pro.fessional.wings.batrider.spring.prop.BatriderHandlerProp;
import pro.fessional.wings.silencer.spring.help.ApplicationContextHelper;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

/**
 * @author trydofor
 * @since 2022-08-07
 */
public class ProviderAuth9Handler extends ProviderAuthFilter {

    private final Dcl<Set<String>> skipSchemaId = Dcl.of(() -> {
        final BatriderHandlerProp prop = ApplicationContextHelper.getBean(BatriderHandlerProp.class);
        return new HashSet<>(prop.getAuthSkipSchema());
    });

    @Override
    public CompletableFuture<Response> onFilter(Invocation invocation, FilterNode nextNode) {
        if (skipSchemaId.runIfDirty().contains(invocation.getSchemaId())) {
            return nextNode.onFilter(invocation);
        }
        else {
            return super.onFilter(invocation, nextNode);
        }
    }
}
