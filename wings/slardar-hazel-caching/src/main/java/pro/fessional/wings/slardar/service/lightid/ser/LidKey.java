package pro.fessional.wings.slardar.service.lightid.ser;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.DataSerializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.IOException;

/**
 * @author trydofor
 * @since 2023-07-18
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LidKey implements DataSerializable {

    private String name;
    private int block;
    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeString(name);
        out.write(block);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        name = in.readString();
        block = in.readInt();
    }
}
