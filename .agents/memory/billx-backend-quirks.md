---
name: BillX Backend Quirks
description: Non-obvious backend decisions and constraints for BillX POS
---

## Entity naming
- Order entity is `SaleOrder` (table: `sale_order`) to avoid MySQL reserved word `ORDER`
- Base Java package: `com.BillX` (capital B, capital X — must match exactly)

## Security / CORS
- SecurityConfig uses `allowedOriginPatterns("*")` with `allowCredentials(false)`
- JWT passed in `Authorization: Bearer <token>` header, stored in localStorage key `billx_token`
- Swagger paths permitted: `/swagger-ui/**`, `/v3/api-docs/**`, `/swagger-resources/**`

## Response wrapping
- New endpoints use `StandardResponse<T>` wrapper in `com.BillX.Payload.response`
- Existing endpoints use the old `ApiResponse` — do not change those

## Error handling
- `UserException` → 400; `AccessDeniedException` → 403; `RuntimeException` → 500 (logged as ERROR)
- The existing UserService throws RuntimeException for "User Not Found" — this is expected existing behavior

**Why:** Preserving existing architecture was a strict requirement; only additions allowed.
