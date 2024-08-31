package pro.fessional.wings.devs.info;

import pro.fessional.wings.silencer.support.MetaJsonMaker;

import java.util.List;

/**
 * META-INF/additional-spring-configuration-metadata.json
 *
 * @author trydofor
 * @since 2024-07-30
 */
public class AdditionalMetadataGenerator {

    public static void main(String[] args) throws Exception {
        MetaJsonMaker maker = new MetaJsonMaker();
        List<MetaJsonMaker.Meta> metas = maker.scanMeta();
        List<MetaJsonMaker.Proj> projs = maker.projMeta(metas);
        maker.printMeta(projs);
        maker.writeMeta(projs);
    }
}
