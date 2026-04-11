package graphing_calculator_053;

import graphing_calculator_053.Routers.Dispatcher;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class MathService {

    public String consoleFast(String input) {
        Dispatcher dispatcher = new Dispatcher();
        // "CF" = Console Fast
        return dispatcher.run(input, new Identification("CF"));
    }

    public List<List<Double>> generateTable(String input, double minX, double maxX, double step) {
        Dispatcher dispatcher = new Dispatcher();
        // "GF" = Graphing Fast (Dispatcher will detect subtype like StandardCartesian)
        return dispatcher.run(input, new Identification("GF"), minX, maxX, step);
    }
}