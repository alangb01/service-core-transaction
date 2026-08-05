package pe.nom.charlygastelo.app.transactionservice.infrastructure.config.security;

import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "security")
public class SecurityPermitPathsProperties {
    private List<String> permitPaths;
}
