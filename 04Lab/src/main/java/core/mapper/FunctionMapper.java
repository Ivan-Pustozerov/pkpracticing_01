package core.mapper;

import core.DTO.response.AnalyticFunctionResponse;
import core.DTO.response.MathFunctionResponse;
import core.DTO.response.TabulatedFunctionResponse;
import core.entity.AnalyticFunctionsEntity;
import core.entity.MathFunctionsEntity;
import core.entity.TabulatedFunctionsEntity;

public class FunctionMapper {
    public AnalyticFunctionResponse toResponse(AnalyticFunctionsEntity entity) {
        AnalyticFunctionResponse response = new AnalyticFunctionResponse();

        MathFunctionsEntity mathFunc = entity.getMathFunction();
        response.setId(mathFunc.getId());
        response.setType(mathFunc.getType());
        response.setName(mathFunc.getName());
        response.setOwnerId(mathFunc.getOwner().getId());
        response.setFunctionExpression(entity.getFunctionExpression());

        return response;
    }

    public TabulatedFunctionResponse toResponse(TabulatedFunctionsEntity entity) {
        TabulatedFunctionResponse response = new TabulatedFunctionResponse();

        MathFunctionsEntity mathFunc = entity.getMathFunction();
        response.setId(mathFunc.getId());
        response.setType(mathFunc.getType());
        response.setName(mathFunc.getName());
        response.setOwnerId(mathFunc.getOwner().getId());
        response.setXVals(entity.getXVals());
        response.setYVals(entity.getYVals());

        return response;
    }

    // Маппер для списка базовых функций
    public MathFunctionResponse toBaseResponse(MathFunctionsEntity entity) {
        MathFunctionResponse response = new MathFunctionResponse();
        response.setId(entity.getId());
        response.setType(entity.getType());
        response.setName(entity.getName());
        response.setOwnerId(entity.getOwner().getId());
        return response;
    }
}
