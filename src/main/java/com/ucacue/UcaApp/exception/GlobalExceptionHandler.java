package com.ucacue.UcaApp.exception;

import java.nio.file.AccessDeniedException;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.security.authentication.*;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.ucacue.UcaApp.exception.auth.MaxActiveSessionException;
import com.ucacue.UcaApp.exception.auth.UserNotFoundAuthException;
import com.ucacue.UcaApp.exception.crud.PermissionNotFoundException;
import com.ucacue.UcaApp.exception.crud.ResourceNotFound;
import com.ucacue.UcaApp.exception.crud.RoleNotFoundException;
import com.ucacue.UcaApp.exception.crud.UserAlreadyExistsException;
import com.ucacue.UcaApp.exception.crud.UserNotFoundException;
import com.ucacue.UcaApp.exception.token.InvalidRefreshTokenException;
import com.ucacue.UcaApp.model.dto.Api.ApiError;
import com.ucacue.UcaApp.model.dto.Api.ApiErrorResponse;

import jakarta.validation.ConstraintViolationException;

@ControllerAdvice
public class GlobalExceptionHandler {

        Map<String, Object> responseGlobalExcp = new HashMap<>();

        // ------------------- Manejo de excepciones generales -------------------

        // Método genérico para manejar otras excepciones
        @ExceptionHandler(Exception.class)
        public ResponseEntity<ApiErrorResponse> handleException(Exception ex) {

                ApiError apiError = new ApiError("Unexpected error occurred",
                                ex.getCause() != null ? ex.getCause().getMessage() : null);

                ApiErrorResponse response = new ApiErrorResponse(
                                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                                "Internal Server Error",
                                List.of(apiError));

                return ResponseEntity
                                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(response);
        }

        // ------------------- Manejo de excepciones de CRUD -------------------

        // Metodo para manejar mensajes de error de recursos no encontrados
        @ExceptionHandler(ResourceNotFound.class)
        public ResponseEntity<ApiErrorResponse> handleResourceNotFoundException(ResourceNotFound ex) {

                ApiError apiError = new ApiError(
                                ex.getMessage(),
                                null);

                ApiErrorResponse response = new ApiErrorResponse(
                                HttpStatus.NOT_FOUND.value(),
                                "Not Found Resource",
                                List.of(apiError));

                return ResponseEntity
                                .status(HttpStatus.NOT_FOUND)
                                .body(response);
        }

        // Metodo para manejar mensajes de error de endpoints no encontrados
        @ExceptionHandler(NoHandlerFoundException.class)
        public ResponseEntity<ApiErrorResponse> handleNoHandlerFoundException(NoHandlerFoundException ex) {

                ApiError apiError = new ApiError(
                                ex.getRequestURL(),
                                null);

                ApiErrorResponse response = new ApiErrorResponse(
                                HttpStatus.NOT_FOUND.value(),
                                "URL not found",
                                List.of(apiError));

                return ResponseEntity
                                .status(HttpStatus.NOT_FOUND)
                                .body(response);
        }

        // Metodo para manejar mensajes de error de roles no encontrados
        @ExceptionHandler(RoleNotFoundException.class)
        public ResponseEntity<ApiErrorResponse> handleRoleNotFoundException(RoleNotFoundException ex) {

                ApiError apiError = new ApiError(
                                "Rol ID " + ex.getRoleId() + " not found",
                                null);

                ApiErrorResponse response = new ApiErrorResponse(
                                HttpStatus.NOT_FOUND.value(),
                                "Role not found",
                                List.of(apiError));

                return ResponseEntity
                                .status(HttpStatus.NOT_FOUND)
                                .body(response);
        }

        // Metodo para manejar mensajes de error de permisos no encontrados
        @ExceptionHandler(PermissionNotFoundException.class)
        public ResponseEntity<ApiErrorResponse> handlePermissionNotFoundException(
                        PermissionNotFoundException ex) {

                ApiError apiError = new ApiError(
                                "Permission ID " + ex.getPermissionId() + " not found",
                                null);

                ApiErrorResponse response = new ApiErrorResponse(
                                HttpStatus.NOT_FOUND.value(),
                                "Permission not found",
                                List.of(apiError));

                return ResponseEntity
                                .status(HttpStatus.NOT_FOUND)
                                .body(response);
        }

        // Metodo para manejar mensajes de error de usuarios no econtrados
        @ExceptionHandler(UserNotFoundException.class)
        public ResponseEntity<ApiErrorResponse> handleUserNotFoundException(UserNotFoundException ex) {

                String searchField = ex.getSearchType().name().toLowerCase();

                ApiError apiError = new ApiError(
                                "User " + searchField + " " + ex.getUserIdentifier() + " not found",
                                null);

                ApiErrorResponse response = new ApiErrorResponse(
                                HttpStatus.NOT_FOUND.value(),
                                "User not found",
                                List.of(apiError));

                return ResponseEntity
                                .status(HttpStatus.NOT_FOUND)
                                .body(response);
        }

        // Metodo para manejar mensajes de error de datos no pasados en (POST)
        @ExceptionHandler(ConstraintViolationException.class)
        public ResponseEntity<ApiErrorResponse> handleConstraintViolationException(
                        ConstraintViolationException ex) {

                List<ApiError> errors = ex.getConstraintViolations()
                                .stream()
                                .map(violation -> {

                                        String field = violation.getPropertyPath().toString();
                                        String message = violation.getMessage();

                                        return new ApiError(field + ": " + message, null);
                                })
                                .toList();

                ApiErrorResponse response = new ApiErrorResponse(
                                HttpStatus.BAD_REQUEST.value(),
                                "Validation Failed",
                                errors);

                return ResponseEntity
                                .status(HttpStatus.BAD_REQUEST)
                                .body(response);
        }

        // Metodo para asegura que se manejen las violaciones de restricciones de
        // validación de manera adecuada
        @ExceptionHandler(TransactionSystemException.class)
        public ResponseEntity<ApiErrorResponse> handleTransactionSystemException(
                        TransactionSystemException ex) {

                Throwable rootCause = ex.getRootCause();

                if (rootCause instanceof ConstraintViolationException constraintEx) {
                        return handleConstraintViolationException(constraintEx);
                }

                ApiError apiError = new ApiError(
                                ex.getMostSpecificCause().getMessage(),
                                null);

                ApiErrorResponse response = new ApiErrorResponse(
                                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                                "Transaction Error",
                                List.of(apiError));

                return ResponseEntity
                                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(response);
        }

        // Metodo para manejar mensajes de error de datos no pasados en URL (GET)
        @ExceptionHandler(MissingServletRequestParameterException.class)
        public ResponseEntity<ApiErrorResponse> handleMissingParams(
                        MissingServletRequestParameterException ex) {

                ApiError apiError = new ApiError(
                                "Missing required parameter: " + ex.getParameterName(),
                                null);

                ApiErrorResponse response = new ApiErrorResponse(
                                HttpStatus.BAD_REQUEST.value(),
                                "Bad Request",
                                List.of(apiError));

                return ResponseEntity
                                .status(HttpStatus.BAD_REQUEST)
                                .body(response);
        }

        // Metodo para manejar errores de tipo de paremtro en las solicitudes
        @ExceptionHandler(MethodArgumentTypeMismatchException.class)
        public ResponseEntity<ApiErrorResponse> handleTypeMismatch(
                        MethodArgumentTypeMismatchException ex) {

                String message = "Parameter '" + ex.getName() +
                                "' should be of type " +
                                ex.getRequiredType().getSimpleName();

                ApiError apiError = new ApiError(message, null);

                ApiErrorResponse response = new ApiErrorResponse(
                                HttpStatus.BAD_REQUEST.value(),
                                "Invalid parameter type",
                                List.of(apiError));

                return ResponseEntity
                                .status(HttpStatus.BAD_REQUEST)
                                .body(response);
        }

        @ExceptionHandler(HttpMessageNotReadableException.class)
        public ResponseEntity<ApiErrorResponse> handleUnreadableMessage(
                        HttpMessageNotReadableException ex) {

                ApiError apiError = new ApiError(
                                "Malformed JSON request",
                                null);

                ApiErrorResponse response = new ApiErrorResponse(
                                HttpStatus.BAD_REQUEST.value(),
                                "Invalid request body",
                                List.of(apiError));

                return ResponseEntity
                                .status(HttpStatus.BAD_REQUEST)
                                .body(response);
        }

        // Metodo para manjear key value violates unique (violaciones de clave única de base de datos)
        // Si no se controla con otra exepcion personalizada, se activa esta por defecto
        @ExceptionHandler(DataIntegrityViolationException.class)
        public ResponseEntity<ApiErrorResponse> handleDataIntegrityViolation(
                        DataIntegrityViolationException ex) {

                String message = "Database integrity violation";

                if (ex.getMostSpecificCause() != null &&
                                ex.getMostSpecificCause().getMessage().contains("unique")) {
                        message = "Already exists";
                }

                ApiError error = new ApiError(
                                message,
                                "DATA_INTEGRITY_VIOLATION");

                ApiErrorResponse response = new ApiErrorResponse(
                                HttpStatus.BAD_REQUEST.value(),
                                "Data integrity violation",
                                List.of(error));

                return ResponseEntity.badRequest().body(response);
        }

        // Metodo para manejar errores de validación de campos
        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<ApiErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
                List<ApiError> errors = ex.getBindingResult().getFieldErrors().stream()
                                .map(fieldError -> new ApiError(
                                                fieldError.getField(),
                                                fieldError.getRejectedValue() != null
                                                                ? fieldError.getRejectedValue().toString()
                                                                : null,
                                                fieldError.getDefaultMessage(),
                                                fieldError.getCode()))
                                .collect(Collectors.toList());

                ApiErrorResponse response = new ApiErrorResponse(
                                HttpStatus.BAD_REQUEST.value(),
                                "Validation failed",
                                errors);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        // Metodo de peticiones no soportadas por el endpoint.
        @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
        public ResponseEntity<ApiErrorResponse> handleMethodNotAllowed(
                HttpRequestMethodNotSupportedException ex) {

                        ApiError error = new ApiError(
                                        ex.getMessage(),
                                "DATA_INTEGRITY_VIOLATION"
                        );

                ApiErrorResponse response = new ApiErrorResponse(
                        HttpStatus.METHOD_NOT_ALLOWED.value(),
                        "HTTP method not allowed for this endpoint",
                        List.of(error)
                );

                return new ResponseEntity<>(response, HttpStatus.METHOD_NOT_ALLOWED);
        }

        @ExceptionHandler(JpaSystemException.class)
        public ResponseEntity<ApiErrorResponse> handleJpaException(JpaSystemException ex) {

                ApiError error = new ApiError(
                        ex.getMessage(),
                        ex.getMostSpecificCause().getMessage()
                );

                ApiErrorResponse response = new ApiErrorResponse(
                                HttpStatus.BAD_REQUEST.value(),
                                "Error coercing value",
                                List.of(error));

                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        // ------------------- Manejo de excepciones de autenticación y autorización -------------------

        // Metodo para manejar errores de acceso denegado (403 Forbidden)
        @ExceptionHandler(AccessDeniedException.class)
        public ResponseEntity<ApiErrorResponse> handleAccessDenied(
                        AccessDeniedException ex) {

                ApiError apiError = new ApiError(
                                "You do not have permission to access this resource",
                                null);

                ApiErrorResponse response = new ApiErrorResponse(
                                HttpStatus.FORBIDDEN.value(),
                                "Access Denied",
                                List.of(apiError));

                return ResponseEntity
                                .status(HttpStatus.FORBIDDEN)
                                .body(response);
        }

        // Manejo de excepción para AuthorizationDeniedException 
        @ExceptionHandler(AuthorizationDeniedException.class)
        public ResponseEntity<ApiErrorResponse> handleAuthorizationDenied(
                        AuthorizationDeniedException ex) {

                ApiError apiError = new ApiError(
                                "You do not have permission to access this resource",
                                null);

                ApiErrorResponse response = new ApiErrorResponse(
                                HttpStatus.UNAUTHORIZED.value(),
                                "Access Denied",
                                List.of(apiError));

                return ResponseEntity
                                .status(HttpStatus.UNAUTHORIZED)
                                .body(response);
        }

        // Manejo de excepción para UsernameNotFoundException
        @ExceptionHandler(UsernameNotFoundException.class)
        public ResponseEntity<ApiErrorResponse> handleUsernameNotFoundException(UsernameNotFoundException ex) {
                ApiError errorDetail = new ApiError(
                                ex.getMessage(),
                                "User not found");

                ApiErrorResponse response = new ApiErrorResponse(
                                HttpStatus.NOT_FOUND.value(),
                                "Errror in authentication",
                                Collections.singletonList(errorDetail));

                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        // Metodo para manejar errores de usuario no encontrado (SE TOMA COMO BAD
        // CREDENTIALS)
        @ExceptionHandler(UserNotFoundAuthException.class)
        public ResponseEntity<ApiErrorResponse> handleUserNotFoundAuthException(UserNotFoundAuthException ex) {
                ApiError errorDetail = new ApiError(
                                ex.getMessage(),
                                "Credentials not found");

                ApiErrorResponse response = new ApiErrorResponse(
                                HttpStatus.UNAUTHORIZED.value(),
                                "Error Credentials",
                                Collections.singletonList(errorDetail));

                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        // Metodo para manejar errores de credenciales invalidas
        @ExceptionHandler(BadCredentialsException.class)
        public ResponseEntity<ApiErrorResponse> handleBadCredentialsException(BadCredentialsException ex) {
                ApiError errorDetail = new ApiError(
                                ex.getMessage(),
                                "Bad credentials");

                ApiErrorResponse response = new ApiErrorResponse(
                                HttpStatus.UNAUTHORIZED.value(),
                                "Invalid username or password",
                                Collections.singletonList(errorDetail));

                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        // Metodo para manejar errores de cuenta deshabilitada
        @ExceptionHandler(DisabledException.class)
        public ResponseEntity<ApiErrorResponse> handleDisabledException(DisabledException ex) {
                ApiError errorDetail = new ApiError(
                                ex.getMessage(),
                                "Account disabled");

                ApiErrorResponse response = new ApiErrorResponse(
                                HttpStatus.UNAUTHORIZED.value(),
                                "Your account is disabled. Please contact the administrator",
                                Collections.singletonList(errorDetail));

                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        // Metodo para manejar errores de cuenta expirada
        @ExceptionHandler(AccountExpiredException.class)
        public ResponseEntity<ApiErrorResponse> handleAccountExpiredException(AccountExpiredException ex) {
                ApiError errorDetail = new ApiError(
                                ex.getMessage(),
                                "Account expired");

                ApiErrorResponse response = new ApiErrorResponse(
                                HttpStatus.UNAUTHORIZED.value(),
                                "Your account has expired. Please contact the administrator",
                                Collections.singletonList(errorDetail));

                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        // Metodo para manejar errores de cuenta bloqueada
        @ExceptionHandler(LockedException.class)
        public ResponseEntity<ApiErrorResponse> handleLockedException(LockedException ex) {
                ApiError errorDetail = new ApiError(
                                ex.getMessage(),
                                "Account locked");

                ApiErrorResponse response = new ApiErrorResponse(
                                HttpStatus.UNAUTHORIZED.value(),
                                "Your account is locked. Please contact the administrator",
                                Collections.singletonList(errorDetail));

                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        // Método para manejar errores de credenciales expiradas
        @ExceptionHandler(CredentialsExpiredException.class)
        public ResponseEntity<ApiErrorResponse> handleCredentialsExpiredException(CredentialsExpiredException ex) {
                ApiError errorDetail = new ApiError(
                                ex.getMessage(),
                                "Credentials expired");

                ApiErrorResponse response = new ApiErrorResponse(
                                HttpStatus.UNAUTHORIZED.value(),
                                "Your credentials have expired. Please contact the administrator",
                                Collections.singletonList(errorDetail));

                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        // Metodo para manejar errores de usuario ya registrados
        @ExceptionHandler(UserAlreadyExistsException.class)
        public ResponseEntity<ApiErrorResponse> handleUserAlreadyExistsException(UserAlreadyExistsException ex) {
                ApiError errorDetail = new ApiError(
                                ex.getMessage(),
                                "User already exists");

                ApiErrorResponse response = new ApiErrorResponse(
                                HttpStatus.BAD_REQUEST.value(),
                                "The user already exists in the system",
                                Collections.singletonList(errorDetail));

                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        @ExceptionHandler(MaxActiveSessionException.class)
        public ResponseEntity<ApiErrorResponse> handleMaxActiveSession(MaxActiveSessionException ex) {
                ApiError errorDetail = new ApiError(
                                ex.getMessage(),
                                "Maximum sessions");

                ApiErrorResponse response = new ApiErrorResponse(
                                HttpStatus.CONFLICT.value(),
                                "You have reached the maximum number of active sessions. Please log out from other devices or contact the administrator",
                                Collections.singletonList(errorDetail));

                return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        }

        // ------------------- Manejo de excepciones relacionadas con tokens JWT -------------------

        // Se controla desde CustomJwtAuthenticationEntryPoint()
        // Por que: Las excepciones token ocurren durante el proceso de autenticación,
        // antes de que el flujo de ejecución alcance los controladores

        // Excepción para manejar errores de token de actualización inválido
        @ExceptionHandler(InvalidRefreshTokenException.class)
        public ResponseEntity<ApiErrorResponse> handleInvalidRefreshTokenException(InvalidRefreshTokenException ex) {
                ApiError errorDetail = new ApiError(
                                ex.getMessage(),
                                "Invalid refresh token");

                ApiErrorResponse response = new ApiErrorResponse(
                                HttpStatus.UNAUTHORIZED.value(),
                                "The provided refresh token is invalid. Please log in again",
                                Collections.singletonList(errorDetail));

                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        // Excepción para manejar errores de token de actualización expirado
        @ExceptionHandler(TokenExpiredException.class)
        public ResponseEntity<ApiErrorResponse> handleTokenExpiredException(
                        TokenExpiredException ex) {

                ApiError errorDetail = new ApiError(
                                ex.getMessage(),
                                "Refresh token expired");

                ApiErrorResponse response = new ApiErrorResponse(
                                HttpStatus.UNAUTHORIZED.value(),
                                "Your refresh token has expired. Please log in again",
                                Collections.singletonList(errorDetail));

                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

}
