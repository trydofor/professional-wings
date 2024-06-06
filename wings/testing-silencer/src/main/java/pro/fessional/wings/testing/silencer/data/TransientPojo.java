package pro.fessional.wings.testing.silencer.data;

import lombok.Data;

import java.beans.Transient;

/**
 * @author trydofor
 * @since 2024-06-05
 */
@Data
public class TransientPojo implements DefaultData<TransientPojo> {

    private transient Boolean tranValue = null;
    private Boolean tranGetter = null;
    private Boolean tranSetter = null;
    private Boolean tranBoth = null;

    @Transient
    public Boolean isTranGetter() {
        return tranGetter;
    }

    @Transient
    public void setTranSetter(Boolean tranSetter) {
        this.tranSetter = tranSetter;
    }

    @Transient
    public Boolean isTranBoth() {
        return tranBoth;
    }

    @Transient
    public void setTranBoth(Boolean tranBoth) {
        this.tranBoth = tranBoth;
    }

    @Override
    public TransientPojo defaults() {
        this.tranValue = true;
        this.tranGetter = true;
        this.tranSetter = true;
        this.tranBoth = true;
        return this;
    }
}
