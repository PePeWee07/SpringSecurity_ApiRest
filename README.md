 **Payloads**
---

# 🔴 EXCEPCIÓN GENERAL (500)

### `Exception.class`

```json
{
    "status": 500,
    "message": "",
    "errors": [
        {
            "field": "",
            "rejectedValue": "",
            "message": "",
            "error": ""
        }
    ]
}
```

# 🟢 OK (200)

```json
{
    "status": 201,
    "data": {
       ...resp
    },
    "message": "User created successfully"
}
```


# 🟢 LOGIN (500)

### `Exception.class`

```json
{
    "username": "pepewee07@gmail.com",
    "message": "User logged in successfully",
    "status": true,
    "jwt": "...",
    "refreshToken": "..."
}
```