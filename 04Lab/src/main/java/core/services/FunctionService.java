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
@Service
@RequiredArgsConstructor
public class FunctionService {
    private final MathFunctionsRepository mathFunctionsRepo;
    private final AnalyticFunctionsRepository analyticRepo;
    private final TabulatedFunctionsRepository tabulatedRepo;
    private final UserRepository userRepo;
    private final FunctionMapper functionMapper;

    @Transactional
    public AnalyticFunctionResponse createAnalytic(AnalyticFunctionRequest request) {
        // 1. Проверяем уникальность имени функции
        if (mathFunctionsRepo.existsByName(request.getName())) {
            throw new RuntimeException("Function with name '" + request.getName() + "' already exists");
        }
        // 2. Получаем владельца
        UserEntity owner = userRepo.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found with id: " + request.getUserId()));
        // 3. Проверяем тип (должно быть "analytic")
        if (!"analytic".equals(request.getType())) {
            throw new RuntimeException("Function type must be 'analytic' for this endpoint");
        }
        // 4. Проверяем выражение функции
        if (request.getFunctionExpression() == null || request.getFunctionExpression().trim().isEmpty()) {
            throw new RuntimeException("Function expression is required");
        }
        // 5. Создаем базовую функцию (MathFunctionsEntity)
        MathFunctionsEntity mathFunc = new MathFunctionsEntity();
        mathFunc.setType(request.getType());
        mathFunc.setName(request.getName());
        mathFunc.setOwner(owner);
        mathFunctionsRepo.save(mathFunc); // Сохраняем, чтобы получить ID

        // 6. Создаем аналитическую функцию (AnalyticFunctionsEntity)
        AnalyticFunctionsEntity analyticFunc = new AnalyticFunctionsEntity();
        analyticFunc.setMathFunction(mathFunc); // Связываем с базовой функцией
        analyticFunc.setFunctionExpression(request.getFunctionExpression());
        analyticRepo.save(analyticFunc);

        // 7. Возвращаем результат
        return functionMapper.toResponse(analyticFunc);
    }
    @Transactional
    public TabulatedFunctionResponse createTabulatedFunction(TabulatedFunctionRequest request) {

        if (mathFunctionsRepo.existsByName(request.getName())) {
            throw new RuntimeException("Function with name '" + request.getName() + "' already exists");
        }

        UserEntity owner = userRepo.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found with id: " + request.getUserId()));


        if (!"tabulated".equals(request.getType())) {
            throw new RuntimeException("Function type must be 'tabulated' for this endpoint");
        }

        //если надо проверки


        MathFunctionsEntity mathFunc = new MathFunctionsEntity();
        mathFunc.setType(request.getType());
        mathFunc.setName(request.getName());
        mathFunc.setOwner(owner);
        mathFunctionsRepo.save(mathFunc); // Сохраняем, чтобы получить ID


        TabulatedFunctionsEntity tabulatedFunc = new TabulatedFunctionsEntity();
        tabulatedFunc.setMathFunction(mathFunc);
        tabulatedFunc.setXVals(request.getXVals());
        tabulatedFunc.setYVals(request.getYVals());
        tabulatedRepo.save(tabulatedFunc);

        return functionMapper.toResponse(tabulatedFunc);
    }
    @Transactional
    public void deleteFunction(Long functionId, Long userId) {
        // 1. Находим функцию с информацией о владельце
        MathFunctionsEntity function = mathFunctionsRepo.findById(functionId)
                .orElseThrow(() -> new RuntimeException("Function not found with id: " + functionId));

        // 2. Проверяем права доступа
        if (!function.getOwner().getId().equals(userId)) {
            throw new RuntimeException("You can only delete your own functions");
        }

        // 3. Удаляем (каскадное удаление сработает благодаря @OnDelete)
        mathFunctionsRepo.delete(function);
    }
}
