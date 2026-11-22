package ma.emsi.customerservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/test")
@RefreshScope
public class ConfigTestRestController {
    private final CustomerConfigParams customerConfigParams;

    @Value("${global.params.p1}")
    private String p1;

    @Value("${global.params.p2}")
    private String p2;

    public ConfigTestRestController(CustomerConfigParams customerConfigParams) {
        this.customerConfigParams = customerConfigParams;
    }

    @GetMapping("/config1")
    public Map<String, String> globalConfig() {
        return Map.of("p1", p1, "p2", p2);
    }

    @GetMapping("/config2")
    public CustomerConfigParams customerConfig() {
        return customerConfigParams;
    }
}
