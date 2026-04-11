package graphing_calculator_053;

import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class MathController {

    private final MathService mathService;

    public MathController(MathService mathService) {
        this.mathService = mathService;
    }

    @GetMapping("/table")
    public List<List<Double>> getTable(
            @RequestParam String expression,
            @RequestParam(defaultValue = "-10") double minX,
            @RequestParam(defaultValue = "10") double maxX,
            @RequestParam(defaultValue = "0.1") double step
    ) {
        return mathService.generateTable(expression, minX, maxX, step);
    }

    @PostMapping("/math/consoleFast")
    public String compute(@RequestBody String input) {
        return mathService.consoleFast(input);
    }
}