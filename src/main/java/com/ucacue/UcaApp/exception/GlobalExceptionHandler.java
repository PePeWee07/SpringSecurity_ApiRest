package com.ucacue.UcaApp.exception;

import java.util.*;
import java.util.stream.Collectors;

import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import com.ucacue.UcaApp.web.response.constraintViolation.ConstraintErrorDetail;
import com.ucacue.UcaApp.web.response.constraintViolation.ConstraintViolationResponse;
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

    //------------------------------------------------------------ EXCEPCIONES DE CRUD ------------------------------------------------------------

    // Método genérico para manejar otras excepciones
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleException(Exception ex) {
        Map<String, Object> responseGlobalExcp = new HashMap<>();
        responseGlobalExcp.put("Internal Server Error: ", ex.getMessage());
        return new ResponseEntity<>(responseGlobalExcp, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    
    //Metodo para manejar mensajes de error de recursos no encontrados
    @ExceptionHandler(ResourceNotFound.class)
    public ResponseEntity<Map<String, Object>> handleResourceNotFoundException(ResourceNotFound ex) {
        responseGlobalExcp = new HashMap<>();
        responseGlobalExcp.put("NOT FOUND", ex.getMessage());
        return new ResponseEntity<Map<String, Object>>(responseGlobalExcp, HttpStatus.NOT_FOUND);
    }

    //Metodo para manejar mensajes de error de endpoints no encontrados
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
            List.of(Map.entry("error", "Rol ID " + ex.getRoleId()+ " not found")),
            "Role not found"
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
    
    // Metodo para manejar mensajes de error de permisos no encontrados
    @ExceptionHandler(PermissionNotFoundException.class)
    public ResponseEntity<RoleAndPermissionNotFoundResponse> handlePermissionNotFoundException(PermissionNotFoundException ex) {
        RoleAndPermissionNotFoundResponse response = new RoleAndPermissionNotFoundResponse(
            HttpStatus.NOT_FOUND.value(),
            List.of(Map.entry("error", "Permission ID " + ex.getPermissionId()+ " not found")),
            "Permission not found"
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    //Metodo para manejar mensajes de error de usuarios no econtrados
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<UserNotFoundResponse> handleUserNotFoundException(UserNotFoundException ex) {
        String searchField = ex.getSearchType().name().toLowerCase();
        UserNotFoundResponse response = new UserNotFoundResponse(
            HttpStatus.NOT_FOUND.value(),
            List.of(Map.entry("error", "User " + searchField + " " + ex.getUserIdentifier() + " not found")),
            "User not found"
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    // Metodo para manejar mensajes de error de datos no pasados en (POST)
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> handleConstraintViolationException(ConstraintViolationException ex) {
        List<ConstraintErrorDetail> errorDetails = ex.getConstraintViolations().stream()
            .map(cv -> new ConstraintErrorDetail(
                cv.getPropertyPath().toString(),
                cv.getMessage(),
                "FIELD_VALIDATION_ERROR"
            ))
            .collect(Collectors.toList());

        ConstraintViolationResponse errorResponse = new ConstraintViolationResponse(
            HttpStatus.BAD_REQUEST.value(),
            errorDetails,
            "Body are incorrect or missing data."
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    // Metodo para manjear key value violates unique
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
            "KEY_VIOLATE_UNIQUE"
        ));

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
    
    //Metodo para manejar errores de tipo de paremtro en URLS
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Object> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String message = String.format("Invalid parameter: " + ex.getValue());
        return ResponseEntity.badRequest().body(message);
    }

    //METODO PARA MANEJAR ERRORES DE VALIDACION DE CAMPOS
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<FieldValidationResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        List<FieldErrorDetail> errors = ex.getBindingResult().getFieldErrors().stream()
                                .map(fieldError -> new FieldErrorDetail(
                                    fieldError.getField(),
                                    fieldError.getDefaultMessage(),
                                    fieldError.getRejectedValue() != null ? fieldError.getRejectedValue().toString() : null,
                                    "FIELD_VALIDATION_ERROR"
                                )).collect(Collectors.toList());

        FieldValidationResponse apiResponse = new FieldValidationResponse(HttpStatus.BAD_REQUEST.value(), errors, "Validation failed");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiResponse);
    }

    //------------------------------------------------------------ EXCEPCIONES DE AUTENTICACION ------------------------------------------------------------

    // Manejo de excepción para UsernameNotFoundException
    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleUsernameNotFoundException(UsernameNotFoundException ex) {
        Map<String, String> response = new HashMap<>();
        response.put("error", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    // Metodo para manejar errores de credenciales invalidas
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, String>> handleBadCredentialsException(BadCredentialsException ex) {
        Map<String, String> response = new HashMap<>();
        response.put("error", "Invalid credentials");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    // Metodo para manejar errores de cuenta expirada
    @ExceptionHandler(AccountExpiredException.class)
    public ResponseEntity<Map<String, String>> handleAccountExpiredException(AccountExpiredException ex) {
        Map<String, String> response = new HashMap<>();
        response.put("error", "Account expired");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    // Metodo para manejar errores de cuenta bloqueada
    @ExceptionHandler(LockedException.class)
    public ResponseEntity<Map<String, String>> handleLockedException(LockedException ex) {
        Map<String, String> response = new HashMap<>();
        response.put("error", "Account locked");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    // Metodo para manejar errores de cuenta deshabilitada
    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<Map<String, String>> handleDisabledException(DisabledException ex) {
        Map<String, String> response = new HashMap<>();
        response.put("error", "Account disabled");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }
}


        // @ExceptionHandler(BadCredentialsException.class)
        // public ResponseEntity<FieldValidationResponse> handleBadCredentialsException(BadCredentialsException ex) {
        //     String field;
        //     String errorDetailMessage;
        //     String errorCode;
    
        //     if (ex.getMessage().equals("Invalid username or password")) {
        //         field = null;
        //         errorDetailMessage = "Invalid username or password";
        //         errorCode = "INVALID_CREDENTIALS_ERROR";
        //     } else if (ex.getMessage().equals("Incorrect Password")) {
        //         field = "password";
        //         errorDetailMessage = "Incorrect Password";
        //         errorCode = "INCORRECT_PASSWORD_ERROR";
        //     } else {
        //         field = "username";
        //         errorDetailMessage = "The user " + ex.getMessage() + " not found";
        //         errorCode = "USER_NOT_FOUND_ERROR";
        //     }
    
        //     // Construye la respuesta de error
        //     FieldErrorDetail errorDetail = new FieldErrorDetail(
        //         field,
        //         errorDetailMessage,
        //         ex.getMessage(),
        //         errorCode
        //     );
    
        //     List<FieldErrorDetail> errors = Collections.singletonList(errorDetail);
    
        //     FieldValidationResponse apiResponse = new FieldValidationResponse(HttpStatus.BAD_REQUEST.value(), errors, "Bad credentials");
        //     return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiResponse);
        // }
