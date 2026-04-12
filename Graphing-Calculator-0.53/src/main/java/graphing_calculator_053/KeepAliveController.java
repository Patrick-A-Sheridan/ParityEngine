import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;

@RestController
public class KeepAliveController {

    @GetMapping("/ping")
    public ResponseEntity<String> ping() {
        // This tiny response is enough to reset Render's 15-minute timer
        return ResponseEntity.ok("Engine is awake.");
    }
}
