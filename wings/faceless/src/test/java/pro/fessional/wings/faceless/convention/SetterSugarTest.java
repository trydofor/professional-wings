package pro.fessional.wings.faceless.convention;

import io.qameta.allure.TmsLink;
import lombok.Data;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static pro.fessional.wings.faceless.convention.SetterSugar.ifObj;
import static pro.fessional.wings.faceless.convention.SetterSugar.ifVal;

/**
 * @author trydofor
 * @since 2024-05-21
 */
class SetterSugarTest {

    @Data
    public static class Dto {
        private boolean boolValue = false;
        private int intValue = 0;
        private long longValue = 0;
        private double floatValue = 0;
        private double doubleValue = 0;

        private String stringObject = null;
    }

    @TmsLink("C12150")
    @Test
    void testIf() {
        Dto dto = new Dto();

        ifVal(dto::setIntValue, 1);
        Assertions.assertEquals(1, dto.intValue);
        ifVal(dto::setIntValue, 2, EmptySugar::nonEmptyValue);
        Assertions.assertEquals(2, dto.intValue);
        ifVal(dto::setIntValue, 0, EmptySugar::nonEmptyValue, 3);
        Assertions.assertEquals(3, dto.intValue);
        ifVal(dto::setIntValue, 0, EmptySugar::nonEmptyValue, () -> 4);
        Assertions.assertEquals(4, dto.intValue);
        ifVal(dto::setIntValue, 0, false);
        Assertions.assertEquals(4, dto.intValue);
        ifVal(dto::setIntValue, dto::getIntValue, true);
        Assertions.assertEquals(4, dto.intValue);

        ifVal(dto::setLongValue, 1L);
        Assertions.assertEquals(1L, dto.longValue);
        ifVal(dto::setLongValue, 2L, EmptySugar::nonEmptyValue);
        Assertions.assertEquals(2L, dto.longValue);
        ifVal(dto::setLongValue, 0L, EmptySugar::nonEmptyValue, 3L);
        Assertions.assertEquals(3L, dto.longValue);
        ifVal(dto::setLongValue, 0L, EmptySugar::nonEmptyValue, () -> 4L);
        Assertions.assertEquals(4L, dto.longValue);
        ifVal(dto::setLongValue, 0L, false);
        Assertions.assertEquals(4L, dto.longValue);
        ifVal(dto::setLongValue, dto::getLongValue, true);
        Assertions.assertEquals(4L, dto.longValue);

        ifVal(dto::setFloatValue, 1F);
        Assertions.assertEquals(1F, dto.floatValue);
        ifVal(dto::setFloatValue, 2F, EmptySugar::nonEmptyValue);
        Assertions.assertEquals(2F, dto.floatValue);
        ifVal(dto::setFloatValue, 0F, EmptySugar::nonEmptyValue, 3F);
        Assertions.assertEquals(3F, dto.floatValue);
        ifVal(dto::setFloatValue, 0F, EmptySugar::nonEmptyValue, () -> 4F);
        Assertions.assertEquals(4F, dto.floatValue);
        ifVal(dto::setFloatValue, 0F, false);
        Assertions.assertEquals(4F, dto.floatValue);
        ifVal(dto::setFloatValue, dto::getFloatValue, true);
        Assertions.assertEquals(4F, dto.floatValue);


        ifVal(dto::setDoubleValue, 1D);
        Assertions.assertEquals(1D, dto.doubleValue);
        ifVal(dto::setDoubleValue, 2D, EmptySugar::nonEmptyValue);
        Assertions.assertEquals(2D, dto.doubleValue);
        ifVal(dto::setDoubleValue, 0D, EmptySugar::nonEmptyValue, 3D);
        Assertions.assertEquals(3D, dto.doubleValue);
        ifVal(dto::setDoubleValue, 0D, EmptySugar::nonEmptyValue, () -> 4D);
        Assertions.assertEquals(4D, dto.doubleValue);
        ifVal(dto::setDoubleValue, 0D, false);
        Assertions.assertEquals(4D, dto.doubleValue);
        ifVal(dto::setDoubleValue, dto::getDoubleValue, true);
        Assertions.assertEquals(4D, dto.doubleValue);


        ifObj(dto::setBoolValue, true);
        Assertions.assertTrue(dto.boolValue);
        ifObj(dto::setBoolValue, false, t -> dto.isBoolValue());
        Assertions.assertFalse(dto.boolValue);

        ifObj(dto::setStringObject, "1D");
        Assertions.assertEquals("1D", dto.stringObject);
        ifObj(dto::setStringObject, "2D", EmptySugar::nonEmptyValue);
        Assertions.assertEquals("2D", dto.stringObject);
        ifObj(dto::setStringObject, "", EmptySugar::nonEmptyValue, () -> "3D");
        Assertions.assertEquals("3D", dto.stringObject);
        ifObj(dto::setStringObject, dto::getStringObject, true);
        Assertions.assertEquals("3D", dto.stringObject);
    }
}