package com.ucacue.UcaApp.exception;

import java.util.*;
import java.util.stream.Collectors;

import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import com.ucacue.UcaApp.exception.auth.UserNotFoundAuthException;
import com.ucacue.UcaApp.exception.crud.PermissionNotFoundException;
import com.ucacue.UcaApp.exception.crud.ResourceNotFound;
import com.ucacue.UcaApp.exception.crud.RoleNotFoundException;
import com.ucacue.UcaApp.exception.crud.UserAlreadyExistsException;
import com.ucacue.UcaApp.exception.crud.UserNotFoundException;
import com.ucacue.UcaApp.web.response.AuthResponse.AuthErrorDetail;
import com.ucacue.UcaApp.web.response.AuthResponse.AuthErrorResponse;
import com.ucacue.UcaApp.web.response.fieldValidation.FieldErrorDetail;
import com.ucacue.UcaApp.web.response.fieldValidation.FieldValidationResponse;
import com.ucacue.UcaApp.web.response.keyViolateUnique.KeyViolateDetail;
import com.ucacue.UcaApp.web.response.keyViolateUnique.KeyViolateUniqueResponse;
import com.ucacue.UcaApp.web.response.roleandPermissionNotFound.RoleAndPermissionNotFoundResponse;
import com.ucacue.UcaApp.web.response.userNotFound.UserNotFoundResponse;

import jakarta.validation.ConstraintViolationException;

@ControllerAdvice
public class GlobalExceptionHandler {

    Map<String, Object> responseGlobalExcp = new HashMap<>();

    // ------------------------------------------------------------ EXCEPCION GENERAL ------------------------------------------------------------

    // Método genérico para manejar otras excepciones
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleException(Exception ex) {
        Map<String, Object> responseGlobalExcp = new HashMap<>();
        responseGlobalExcp.put("message", "Internal Server Error");
        // Incluye solo el mensaje de la excepción
        responseGlobalExcp.put("details", ex.getMessage());
        // Si es necesario, incluir detalles de la causa
        if (ex.getCause() != null) {
            responseGlobalExcp.put("cause", ex.getCause().getMessage());
        }
        return new ResponseEntity<>(responseGlobalExcp, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // ------------------------------------------------------------ EXCEPCIONES DE CRUD ------------------------------------------------------------

    // Metodo para manejar mensajes de error de recursos no encontrados
    @ExceptionHandler(ResourceNotFound.class)
    public ResponseEntity<Map<String, Object>> handleResourceNotFoundException(ResourceNotFound ex) {
        responseGlobalExcp = new HashMap<>();
        responseGlobalExcp.put("NOT FOUND", ex.getMessage());
        return new ResponseEntity<Map<String, Object>>(responseGlobalExcp, HttpStatus.NOT_FOUND);
    }

    // Metodo para manejar mensajes de error de endpoints no encontrados
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNoHandlerFoundException(NoHandlerFoundException ex) {
        responseGlobalExcp = new HashMap<>();
        responseGlobalExcp.put("URL not found: ", ex.getRequestURL());
        return new ResponseEntity<Map<String, Object>>(responseGlobalExcp, HttpStatus.NOT_FOUND);
    }

    // Metodo para manejar mensajes de error de roles no encontrados
    @ExceptionHandler(RoleNotFoundException.class)
    public ResponseEntity<RoleAndPermissionNotFoundResponse> handleRoleNotFoundException(RoleNotFoundException ex) {
        RoleAndPermissionNotFoundResponse response = new RoleAndPermissionNotFoundResponse(
                HttpStatus.NOT_FOUND.value(),
                List.of(Map.entry("error", "Rol ID " + ex.getRoleId() + " not found")),
                "Role not found");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // Metodo para manejar mensajes de error de permisos no encontrados
    @ExceptionHandler(PermissionNotFoundException.class)
    public ResponseEntity<RoleAndPermissionNotFoundResponse> handlePermissionNotFoundException(
            PermissionNotFoundException ex) {
        RoleAndPermissionNotFoundResponse response = new RoleAndPermissionNotFoundResponse(
                HttpStatus.NOT_FOUND.value(),
                List.of(Map.entry("error", "Permission ID " + ex.getPermissionId() + " not found")),
                "Permission not found");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // Metodo para manejar mensajes de error de usuarios no econtrados
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<UserNotFoundResponse> handleUserNotFoundException(UserNotFoundException ex) {
        String searchField = ex.getSearchType().name().toLowerCase();
        UserNotFoundResponse response = new UserNotFoundResponse(
                HttpStatus.NOT_FOUND.value(),
                List.of(Map.entry("error", "User " + searchField + " " + ex.getUserIdentifier() + " not found")),
                "User not found");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    // Metodo para manejar mensajes de error de datos no pasados en (POST)
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, Object>> handleConstraintViolationException(ConstraintViolationException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Validation Failed");

        List<Map<String, String>> violations = ex.getConstraintViolations().stream()
                .map(violation -> {
                    Map<String, String> violationDetails = new HashMap<>();
                    violationDetails.put("field", violation.getPropertyPath().toString());
                    violationDetails.put("message", violation.getMessage());
                    return violationDetails;
                })
                .collect(Collectors.toList());

        response.put("details", violations);

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // Metodo para asegura que se manejen las violaciones de restricciones de validación de manera adecuada
    @ExceptionHandler(TransactionSystemException.class)
    public ResponseEntity<Map<String, Object>> handleTransactionSystemException(TransactionSystemException ex) {
        Throwable cause = ex.getRootCause();
        if (cause instanceof ConstraintViolationException) {
            return handleConstraintViolationException((ConstraintViolationException) cause);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Transaction Error");
        response.put("details", ex.getMessage());
        if (ex.getCause() != null) {
            response.put("cause", ex.getCause().getMessage());
        }
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    //Metodo para manjear key value violates unique
    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<?> handleDataAccessException(DataAccessException ex) {
        String errorMessage = ex.getMostSpecificCause().getMessage();
        String detailMessage = extractDetailMessageCause(errorMessage);
        String field = extractDetailMessageField(detailMessage);
        String rejectedValue = extractDetailRejectedValue(detailMessage);

        List<KeyViolateDetail> errorDetails = List.of(new KeyViolateDetail(
                field,
                "Key violates unique constraint",
                rejectedValue,
                "KEY_VIOLATE_UNIQUE"));

        KeyViolateUniqueResponse response = new KeyViolateUniqueResponse(
                HttpStatus.BAD_REQUEST.value(),
                errorDetails,
                "Key violates unique constraint"

        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    private String extractDetailMessageCause(String errorMessage) {
        int startIndex = errorMessage.indexOf("Detail: ") + "Detail: ".length();
        int endIndex = errorMessage.length();
        return errorMessage.substring(startIndex, endIndex);
    }

    private String extractDetailMessageField(String detailMessage) {
        int startIndex = detailMessage.indexOf("Key (") + "Key (".length();
        int endIndex = detailMessage.indexOf(")=");
        return detailMessage.substring(startIndex, endIndex);
    }

    private String extractDetailRejectedValue(String detailMessage) {
        int startIndex = detailMessage.indexOf("=(") + "=(".length();
        int endIndex = detailMessage.indexOf(") ");
        return detailMessage.substring(startIndex, endIndex);
    }

    // Metodo para manejar errores de tipo de paremtro en URLS
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Object> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String message = String.format("Invalid parameter: " + ex.getValue());
        return ResponseEntity.badRequest().body(message);
    }

    // METODO PARA MANEJAR ERRORES DE VALIDACION DE CAMPOS
    @SuppressWarnings("null")
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<FieldValidationResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        List<FieldErrorDetail> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> new FieldErrorDetail(
                        fieldError.getField(),
                        fieldError.getDefaultMessage(),
                        fieldError.getRejectedValue() != null ? fieldError.getRejectedValue().toString() : null,
                        "FIELD_VALIDATION_ERROR"))
                .collect(Collectors.toList());

        FieldValidationResponse apiResponse = new FieldValidationResponse(HttpStatus.BAD_REQUEST.value(), errors,
                "Validation failed");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiResponse);
    }

    // ------------------------------------------------------------ EXCEPCIONES DE AUTENTICACION ------------------------------------------------------------

    // Manejo de excepción para UsernameNotFoundException
    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<AuthErrorResponse> handleUsernameNotFoundException(UsernameNotFoundException ex) {
        AuthErrorDetail errorDetail = new AuthErrorDetail(
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage(),
                "User not found");

        AuthErrorResponse response = new AuthErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                Collections.singletonList(errorDetail),
                "Errror in authentication");

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    // Metodo para manejar errores de usuario no encontrado (SE TOMA COMO BAD CREDENTIALS)
    @ExceptionHandler(UserNotFoundAuthException.class)
    public ResponseEntity<AuthErrorResponse> handleUserNotFoundAuthException(UserNotFoundAuthException ex) {
        AuthErrorDetail errorDetail = new AuthErrorDetail(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(),
                "Credentials not found");

        AuthErrorResponse response = new AuthErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                Collections.singletonList(errorDetail),
                "Error Credentials");

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // Metodo para manejar errores de credenciales invalidas
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<AuthErrorResponse> handleBadCredentialsException(BadCredentialsException ex) {
        AuthErrorDetail errorDetail = new AuthErrorDetail(
                HttpStatus.UNAUTHORIZED.value(),
                ex.getMessage(),
                "Bad credentials");

        AuthErrorResponse response = new AuthErrorResponse(
                HttpStatus.UNAUTHORIZED.value(),
                Collections.singletonList(errorDetail),
                "Invalid username or password");

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    // Metodo para manejar errores de cuenta deshabilitada
    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<AuthErrorResponse> handleDisabledException(DisabledException ex) {
        AuthErrorDetail errorDetail = new AuthErrorDetail(
                HttpStatus.UNAUTHORIZED.value(),
                ex.getMessage(),
                "Account disabled");

        AuthErrorResponse response = new AuthErrorResponse(
                HttpStatus.UNAUTHORIZED.value(),
                Collections.singletonList(errorDetail),
                "Your account is disabled. Please contact the administrator");

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    // Metodo para manejar errores de cuenta expirada
    @ExceptionHandler(AccountExpiredException.class)
    public ResponseEntity<AuthErrorResponse> handleAccountExpiredException(AccountExpiredException ex) {
        AuthErrorDetail errorDetail = new AuthErrorDetail(
                HttpStatus.UNAUTHORIZED.value(),
                ex.getMessage(),
                "Account expired");

        AuthErrorResponse response = new AuthErrorResponse(
                HttpStatus.UNAUTHORIZED.value(),
                Collections.singletonList(errorDetail),
                "Your account has expired. Please contact the administrator");

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    // Metodo para manejar errores de cuenta bloqueada
    @ExceptionHandler(LockedException.class)
    public ResponseEntity<AuthErrorResponse> handleLockedException(LockedException ex) {
        AuthErrorDetail errorDetail = new AuthErrorDetail(
                HttpStatus.UNAUTHORIZED.value(),
                ex.getMessage(),
                "Account locked");

        AuthErrorResponse response = new AuthErrorResponse(
                HttpStatus.UNAUTHORIZED.value(),
                Collections.singletonList(errorDetail),
                "Your account is locked. Please contact the administrator");

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    // Método para manejar errores de credenciales expiradas
    @ExceptionHandler(CredentialsExpiredException.class)
    public ResponseEntity<AuthErrorResponse> handleCredentialsExpiredException(CredentialsExpiredException ex) {
        AuthErrorDetail errorDetail = new AuthErrorDetail(
                HttpStatus.UNAUTHORIZED.value(),
                ex.getMessage(),
                "Credentials expired");

        AuthErrorResponse response = new AuthErrorResponse(
                HttpStatus.UNAUTHORIZED.value(),
                Collections.singletonList(errorDetail),
                "Your credentials have expired. Please contact the administrator");

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    // Metodo para manejar errores de usaurio ya registrados
    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<AuthErrorResponse> handleUserAlreadyExistsException(UserAlreadyExistsException ex) {
        AuthErrorDetail errorDetail = new AuthErrorDetail(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(),
                "User already exists");

        AuthErrorResponse response = new AuthErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                Collections.singletonList(errorDetail),
                "The user already exists in the system");

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // ------------------------------------------------------------ EXCEPCIONES DETOKEN ------------------------------------------------------------

    // Se controla desde CustomJwtAuthenticationEntryPoint()
    // Por que: Las excepciones ocurren antes de que el controlador las maneje
    
}
