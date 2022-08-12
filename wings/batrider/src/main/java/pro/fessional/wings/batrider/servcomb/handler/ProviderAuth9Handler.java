package pro.fessional.wings.batrider.servcomb.handler;

import org.apache.servicecomb.authentication.provider.ProviderAuthHanlder;
import org.apache.servicecomb.core.Invocation;
import org.apache.servicecomb.swagger.invocation.AsyncResponse;
import pro.fessional.mirana.func.Dcl;
import pro.fessional.wings.batrider.spring.prop.BatriderHandlerProp;
import pro.fessional.wings.silencer.spring.help.ApplicationContextHelper;

import java.util.HashSet;
import java.util.Set;

/**
 * @author trydofor
 * @since 2022-08-07
 */
public class ProviderAuth9Handler extends ProviderAuthHanlder {

    private final Dcl<Set<String>> skipSchemaId = Dcl.of(() -> {
        final BatriderHandlerProp prop = ApplicationContextHelper.getBean(BatriderHandlerProp.class);
        return new HashSet<>(prop.getAuthSkipSchema());
    });

    @Override
    public void handle(Invocation invocation, AsyncResponse asyncResp) throws Exception {
        if (skipSchemaId.runIfDirty().contains(invocation.getSchemaId())) {
            invocation.next(asyncResp);
        }
        else {
            super.handle(invocation, asyncResp);
        }
    }
}
