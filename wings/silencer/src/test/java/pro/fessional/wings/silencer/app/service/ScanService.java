package pro.fessional.wings.silencer.app.service;

import org.springframework.stereotype.Service;
import pro.fessional.wings.silencer.spring.boot.ConditionalWingsEnabled;

/**
 * @author trydofor
 * @since 2023-11-22
 */
@Service
@ConditionalWingsEnabled
public class ScanService {
}
