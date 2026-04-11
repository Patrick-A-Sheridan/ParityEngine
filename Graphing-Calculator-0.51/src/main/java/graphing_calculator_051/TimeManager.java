package graphing_calculator_051;

import java.time.LocalDateTime;
import java.time.Instant;
public class TimeManager {
 public static Long getPreciseTime(){
return Instant.now().toEpochMilli();
 }
 
 public static LocalDateTime getDateTime() {
     return LocalDateTime.now();
}
}