package core.controllers;
import core.DTO.request.AnalyticFunctionRequest;
import core.DTO.request.TabulatedFunctionRequest;
import core.DTO.response.AnalyticFunctionResponse;
import core.DTO.response.MathFunctionResponse;
import core.DTO.response.TabulatedFunctionResponse;
import core.services.FunctionService;
import core.util.SecurityUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/functions")
@RequiredArgsConstructor
public class FunctionController {
    private final FunctionService functionService;
    private final SecurityUtils securityUtils;

    /**
     * Получить все функции
     * GET /api/functions?type=analytic|tabulated
     */
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public List<MathFunctionResponse> getAllFunctions(@RequestParam(required = false) String type) {
        Long userId = securityUtils.getCurrentUserId(); // ИЗМЕНИТЬ

        List<MathFunctionResponse> functions = functionService.getUserFunctions(userId);

        if (type != null) {
            return functions.stream()
                    .filter(func -> func.getType().equals(type))
                    .toList();
        }

        return functions;
    }

    /**
     * Получить конкретную функцию
     * GET /api/functions/{id}
     *
     * Автоматически определяет тип и возвращает правильный DTO
     */
    @GetMapping("/{id}")
    public Object getFunction(@PathVariable Long id) {
        Long userId = securityUtils.getCurrentUserId();


        MathFunctionResponse baseInfo = functionService.getFunctionInfo(id, userId);


        if ("analytic".equals(baseInfo.getType())) {
            return functionService.getAnalyticFunction(id, userId);
        } else if ("tabulated".equals(baseInfo.getType())) {
            return functionService.getTabulatedFunction(id, userId);
        }
        return baseInfo;
    }

    /**
     * Создать аналитическую функцию
     * POST /api/functions/analytic
     */
    @PostMapping("/analytic")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("isAuthenticated()")
    public AnalyticFunctionResponse createAnalytic(@RequestBody AnalyticFunctionRequest request) {
        Long userId = securityUtils.getCurrentUserId(); // ИЗМЕНИТЬ
        //request.setUserId(userId);
        if (request.getUserId() == null) {
            request.setUserId(userId);
        } else {
            System.out.println("=== loch ===");
        }
        return functionService.createAnalytic(request, userId);
    }

    /**
     * Создать табличную функцию
     * POST /api/functions/tabulated
     */
    @PostMapping("/tabulated")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("isAuthenticated()")
    public TabulatedFunctionResponse createTabulated(@RequestBody TabulatedFunctionRequest request) {
        Long userId = securityUtils.getCurrentUserId(); // ИЗМЕНИТЬ
        //request.setUserId(userId);
        return functionService.createTabulatedFunction(request, userId);
    }

    /**
     * Удаление
     *
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("isAuthenticated()")
    public void deleteFunction(@PathVariable Long id) {
        Long userId = securityUtils.getCurrentUserId();
        functionService.deleteFunction(id, userId);
    }


}
