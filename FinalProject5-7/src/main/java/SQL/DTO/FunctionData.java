package SQL.DTO;

import functions.classes.AnalyticFunction;
import functions.interfaces.TabulatedFunction;

public record FunctionData
        (TabulatedFunction tabData, AnalyticFunction analyticData)
        implements  DTO{}
