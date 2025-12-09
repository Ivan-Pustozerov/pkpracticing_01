package core.services;


import core.DTO.response.*;
import core.DTO.request.*;
import core.entity.MathFunctionsEntity;
import core.entity.UserEntity;
import core.mapper.FunctionMapper;
import core.repository.MathFunctionsRepository;
import core.repository.TabulatedFunctionsRepository;
import core.repository.AnalyticFunctionsRepository;
import core.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.*;
import core.entity.*;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FunctionService {
    private final MathFunctionsRepository mathFunctionsRepo;
    private final AnalyticFunctionsRepository analyticRepo;
    private final TabulatedFunctionsRepository tabulatedRepo;
    private final UserRepository userRepo;
    private final FunctionMapper functionMapper;

    @Transactional
    public AnalyticFunctionResponse createAnalytic(AnalyticFunctionRequest request, Long userId) {

        if (mathFunctionsRepo.existsByName(request.getName())) {
            throw new RuntimeException("Function with name '" + request.getName() + "' already exists");
        }

        UserEntity owner = userRepo.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        if (!"analytic".equals(request.getType())) {
            throw new RuntimeException("Function type must be 'analytic' for this endpoint");
        }

        if (request.getFunctionExpression() == null || request.getFunctionExpression().trim().isEmpty()) {
            throw new RuntimeException("Function expression is required");
        }

        MathFunctionsEntity mathFunc = new MathFunctionsEntity();
        mathFunc.setType(request.getType());
        mathFunc.setName(request.getName());
        mathFunc.setOwner(owner);
        mathFunctionsRepo.save(mathFunc);


        AnalyticFunctionsEntity analyticFunc = new AnalyticFunctionsEntity();
        analyticFunc.setMathFunction(mathFunc);
        analyticFunc.setFunctionExpression(request.getFunctionExpression());
        analyticRepo.save(analyticFunc);

        return functionMapper.toResponse(analyticFunc);
    }
    @Transactional
    public TabulatedFunctionResponse createTabulatedFunction(TabulatedFunctionRequest request, Long userId) {

        if (mathFunctionsRepo.existsByName(request.getName())) {
            System.out.println("=== TAB-NAMEERR ===");
            throw new RuntimeException("Function with name '" + request.getName() + "' already exists");
        }

        UserEntity owner = userRepo.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        if (!"tabulated".equals(request.getType())) {
            System.out.println("=== TAB-TYPE ===");
            throw new RuntimeException("Function type must be 'tabulated' for this endpoint");
        }

        MathFunctionsEntity mathFunc = new MathFunctionsEntity();
        mathFunc.setType(request.getType());
        mathFunc.setName(request.getName());
        mathFunc.setOwner(owner);
        mathFunctionsRepo.save(mathFunc);

        TabulatedFunctionsEntity tabulatedFunc = new TabulatedFunctionsEntity();
        tabulatedFunc.setMathFunction(mathFunc);
        tabulatedFunc.setXVals(request.getXVals());
        tabulatedFunc.setYVals(request.getYVals());
        tabulatedRepo.save(tabulatedFunc);

        return functionMapper.toResponse(tabulatedFunc);
    }
    @Transactional
    public AnalyticFunctionResponse getAnalyticFunction(Long functionId, Long userId) {
        AnalyticFunctionsEntity entity = analyticRepo.findById(functionId)
                .orElseThrow(() -> new RuntimeException("Analytic function not found"));

        // Проверяем, что функция принадлежит пользователю
        if (!entity.getMathFunction().getOwner().getId().equals(userId)) {
            throw new RuntimeException("Access denied");
        }

        return functionMapper.toResponse(entity);
    }
    @Transactional
    public TabulatedFunctionResponse getTabulatedFunction(Long functionId, Long userId) {
        TabulatedFunctionsEntity entity = tabulatedRepo.findById(functionId)
                .orElseThrow(() -> new RuntimeException("Tabulated function not found"));

        // Проверяем, что функция принадлежит пользователю
        if (!entity.getMathFunction().getOwner().getId().equals(userId)) {
            throw new RuntimeException("Access denied");
        }

        return functionMapper.toResponse(entity);
    }
    @Transactional
    public List<MathFunctionResponse> getUserFunctions(Long userId) {

        List<MathFunctionsEntity> functions = mathFunctionsRepo.findByOwnerId(userId);

        return functions.stream().map(functionMapper::toBaseResponse).collect(Collectors.toList());
    }
    @Transactional
    public void deleteFunction(Long functionId, Long userId) {
        MathFunctionsEntity function = mathFunctionsRepo.findById(functionId).orElseThrow(() -> new RuntimeException("Function not found"));

        if (!function.getOwner().getId().equals(userId)) {throw new RuntimeException("You can only delete your own functions");}

        mathFunctionsRepo.delete(function);
    }
    public MathFunctionResponse getFunctionInfo(Long functionId, Long userId) {
        MathFunctionsEntity entity = mathFunctionsRepo.findById(functionId).orElseThrow(() -> new RuntimeException("Function not found"));

        if (!entity.getOwner().getId().equals(userId)) {throw new RuntimeException("Access denied");}

        return functionMapper.toBaseResponse(entity);
    }
}
