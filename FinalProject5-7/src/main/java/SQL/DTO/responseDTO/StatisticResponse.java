package SQL.DTO.responseDTO;

import SQL.DTO.FromBD.DTO;

import java.time.Duration;
import java.time.LocalDateTime;

public record StatisticResponse
        (long id, LocalDateTime reg_time, Duration all_time, Duration avrg_time_day, Integer func_count)
        implements DTO {}
