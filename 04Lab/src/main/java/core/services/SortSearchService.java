package core.services;
import core.entity.UserEntity;
import core.repository.AnalyticFunctionsRepository;
import core.repository.MathFunctionsRepository;
import core.repository.TabulatedFunctionsRepository;
import core.repository.UserRepository;
import org.springframework.data.domain.Sort;
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
public class SortSearchService {
    private static final Logger log = LoggerFactory.getLogger(SortSearchService.class);
    @Autowired
    private UserRepository userRepository;

    public List<UserEntity> findAllUsersSorted(Sort.Direction direction, String... properties) {
        log.info("SortSearch: Users/ {} {}", properties, direction);
        return userRepository.findAll(Sort.by(direction, properties));
    }

}
