package pro.fessional.wings.silencer.enhance;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

/**
 * @author trydofor
 * @since 2024-05-16
 */
public class ThisLazyPostProcessor implements BeanPostProcessor {

    @Override
    @SuppressWarnings("all")
    public Object postProcessAfterInitialization(@NotNull Object bean, @NotNull String beanName) throws BeansException {
        if (!(bean instanceof ThisLazyAware self)) return bean;

        Class<?> lazyType = self.thisLazyType();
        Class<?> thisType = self.getClass();
        if (lazyType.isAssignableFrom(thisType)) {
            self.setThisLazy(self);
        }
        else {
            throw new IllegalStateException(
                    "wrong ThisLazyType. bean=" + beanName + ", need=" + lazyType
                    + ", but=" + thisType
                    + "\nExcept for the following cases, there are runtime type exceptions, where `M` represents the enhanced method used by thisLazy,"
                    + "(1) `T` is an interface, and all `M` come from `T` (best practice)"
                    + "(2) `T` is a class, and `M` is enhanced by Cglib (proxyTargetClass=true)"
                    + "(3) no `M`, in which case `T` is itself (but should not use this pattern)"
            );
        }
        return bean;
    }
}
