package core.services;
import core.entity.AnalyticFunctionsEntity;
import core.entity.MathFunctionsEntity;
import core.entity.TabulatedFunctionsEntity;
import core.entity.UserEntity;
import core.repository.AnalyticFunctionsRepository;
import core.repository.MathFunctionsRepository;
import core.repository.TabulatedFunctionsRepository;
import core.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service
@Transactional(readOnly = true)
public class SingleSearchService {
    private static final Logger log = LoggerFactory.getLogger(SingleSearchService.class);
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MathFunctionsRepository mathFunctionsRepository;

    public Optional<UserEntity> findUserByName(String name) {
        log.info("SingleSearch: User/Name '{}'", name);
        return userRepository.findByName(name);
    }

    public Optional<MathFunctionsEntity> findMathFunctionByName(String name) {
        log.info("SingleSearch: MathFunction/Name '{}'", name);
        MathFunctionsEntity function = mathFunctionsRepository.findByName(name);
        return Optional.ofNullable(function);
    }
}
