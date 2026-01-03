package SQL.DTO.ToClient;

import SQL.DTO.DTO;

import java.time.LocalDateTime;
import java.time.LocalTime;

public record StatisticToClientDTO
        (long id, LocalDateTime reg_time, LocalTime avrg_time_day, int func_count)
        implements DTO {}
