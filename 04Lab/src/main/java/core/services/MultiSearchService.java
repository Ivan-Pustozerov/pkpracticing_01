package core.services;
import core.entity.MathFunctionsEntity;
import core.entity.UserEntity;
import core.repository.AnalyticFunctionsRepository;
import core.repository.MathFunctionsRepository;
import core.repository.TabulatedFunctionsRepository;
import core.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service
@Transactional(readOnly = true)
public class MultiSearchService {
    private static final Logger log = LoggerFactory.getLogger(MultiSearchService.class);
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MathFunctionsRepository mathFunctionsRepository;
    public List<UserEntity> findAllUsers() {
        log.info("MultiSearch: AllUsers");
        return userRepository.findAll();
    }
    public List<UserEntity> findAllAdmins() {
        log.info("MultiSearch: AllAdmins");
        return userRepository.findAllAdmins();
    }
    public List<MathFunctionsEntity> findFunctionsByOwnerId(Long ownerId) {
        log.info("MultiSearch: Math/ID {}", ownerId);
        return mathFunctionsRepository.findByOwnerId(ownerId);
    }

}
