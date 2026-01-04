package SQL.Services;

import SQL.repositories.ErrorsRepository;
import SQL.repositories.tools.SQLRepositoryException;
import SQL.repositories.tools.SmartConnection;
import SQL.repositories.tools.SmartConnectionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ErrorService {
    private static final Logger logger = LoggerFactory.getLogger(ErrorService.class);

    private final SmartConnection connection;
    private final ErrorsRepository errorsRepository;

    public ErrorService(String url, String username, String password)
            throws SmartConnectionException {
        this.connection = new SmartConnection(url, username, password);
        this.errorsRepository = new ErrorsRepository();

        // Initialize the errors table if it doesn't exist
        try {
            errorsRepository.initTable(this.connection.getConnection());
        } catch (SQLRepositoryException e) {
            logger.error("Failed to initialize errors table", e);
        }
    }

    /**
     * Logs an error to both the database and the application logs
     * @param code Error code (e.g., HTTP status code or custom error code)
     * @param type Error type (e.g., exception class name or custom error category)
     * @param message Error message
     */
    public void logError(int code, String type, String message) {
        logger.error("Error [{}]: {} - {}", code, type, message);

        try {
            errorsRepository.insertError(connection.getConnection(), code, type);
        } catch (SQLRepositoryException e) {
            logger.error("Failed to log error to database: {}", e.getMessage(), e);
        } catch (SmartConnectionException e) {
            logger.error("Connection Error: {}", e.getMessage(), e);
        }
    }

    /**
     * Logs an exception to both the database and the application logs
     * @param exception The exception to log
     */
    public void logException(Exception exception) {
        String exceptionType = exception.getClass().getSimpleName();
        String message = exception.getMessage() != null ? exception.getMessage() : "No message";
        int code = 500; // Default server error code

        logger.error("Exception caught: {} - {}", exceptionType, message, exception);

        try {
            errorsRepository.insertError(connection.getConnection(), code, exceptionType);
        } catch (SQLRepositoryException e) {
            logger.error("Failed to log exception to database: {}", e.getMessage(), e);
        }catch (SmartConnectionException e) {
            logger.error("Connection Error: {}", e.getMessage(), e);
        }
    }

    /**
     * Logs an exception with a custom error code
     * @param exception The exception to log
     * @param customCode Custom error code
     */
    public void logException(Exception exception, int customCode) {
        String exceptionType = exception.getClass().getSimpleName();
        String message = exception.getMessage() != null ? exception.getMessage() : "No message";

        logger.error("Exception caught (code {}): {} - {}", customCode, exceptionType, message, exception);

        try {
            errorsRepository.insertError(connection.getConnection(), customCode, exceptionType);
        } catch (SQLRepositoryException e) {
            logger.error("Failed to log exception to database: {}", e.getMessage(), e);
        }catch (SmartConnectionException e) {
            logger.error("Connection Error: {}", e.getMessage(), e);
        }
    }

    /**
     * Logs a general error with exception
     * @param code Error code
     * @param type Error type
     * @param message Error message
     * @param exception Associated exception
     */
    public void logError(int code, String type, String message, Exception exception) {
        logger.error("Error [{}]: {} - {}", code, type, message, exception);

        try {
            errorsRepository.insertError(connection.getConnection(), code, type);
        } catch (SQLRepositoryException e) {
            logger.error("Failed to log error to database: {}", e.getMessage(), e);
        }catch (SmartConnectionException e) {
            logger.error("Connection Error: {}", e.getMessage(), e);
        }
    }

    /**
     * Logs a warning message
     * @param message Warning message
     */
    public void logWarning(String message) {
        logger.warn(message);
    }

    /**
     * Logs an info message
     * @param message Info message
     */
    public void logInfo(String message) {
        logger.info(message);
    }

    /**
     * Logs a debug message
     * @param message Debug message
     */
    public void logDebug(String message) {
        logger.debug(message);
    }
}
