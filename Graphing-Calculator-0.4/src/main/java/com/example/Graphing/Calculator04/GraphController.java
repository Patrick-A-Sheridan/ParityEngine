package com.example.Graphing.Calculator04;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.Graphing.Calculator04.Parser.Functions;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class GraphController {

    @GetMapping("/test")
    public ResponseEntity<Map<String,String>> test() {
        Map<String,String> out = new HashMap<>();
        out.put("status", "ok");
        out.put("message", "api up");
        return ResponseEntity.ok(out);
    }

    @GetMapping("/evaluate")
    public ResponseEntity<?> evaluate(
            @RequestParam String expression,
            @RequestParam double x) {
        try {
            double result = Functions.evaluate(expression, x);
            Map<String,Object> response = new HashMap<>();
            response.put("x", x);
            response.put("result", result);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace(); // console log
            Map<String,String> err = new HashMap<>();
            err.put("error", "evaluation failed");
            err.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(err);
        }
    }
    @GetMapping("/table")
    public ResponseEntity<?> table(
            @RequestParam String expression,
            @RequestParam double minX,
            @RequestParam double maxX,
            @RequestParam double step) {

        try {
            double[][] table = Functions.createTable(expression, minX, maxX, step);
            return ResponseEntity.ok(table);
        } catch (Exception e) {
            e.printStackTrace(); // console log
            Map<String,String> err = new HashMap<>();
            err.put("error", "table generation failed");
            err.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(err);
        }
    }
}
