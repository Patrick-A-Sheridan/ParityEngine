package com.example.Graphing.Calculator04;
import org.springframework.stereotype.Service;

import com.example.Graphing.Calculator04.Parser.Functions;
@Service
public class GraphService {

    public double evaluate(String expression, double x) {
        return Functions.evaluate(expression, x);
    }

    public double[][] createTable(String expression,
                                  double minX,
                                  double maxX,
                                  double step) {
        return Functions.createTable(expression, minX, maxX, step);
    }
}