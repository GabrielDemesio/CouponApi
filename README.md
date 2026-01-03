# CouponApi

## Como rodar
- Requisitos: JDK 21+.
- Comandos:
  ```bash
  cd CouponApi
  ./mvnw clean test
  ./mvnw spring-boot:run
  ```
## exemplo de Json para criação de cupom
rota /coupon
{
"code": "ABC123",
"description": "teste",
"discountValue": 0.5,
"expirationDate": "2026-01-04T17:14:45.180Z"
}
 
para buscar/ou deletar o cupom, necessário passar o id na url
localhost:8080/coupon/d25962a6-f3b0-4184-adc9-ec5e7e68ecba

- Banco: H2 em memória (`jdbc:h2:mem:coupondb`) com console em `/h2-console`.

## Como validei
- Testes unitários de domínio e service (validação de código, desconto mínimo, sanitização, soft delete).
- Testes unitários do controller com Mockito (status e payload do POST/DELETE/GET).

## Decisões técnicas
- Spring Boot + JPA/Hibernate com H2.
-.UUID como `id` e `code` como unique_key.
- Soft delete com status (`ACTIVE/INACTIVE/DELETED`) para manter histórico.
- Booleans como `Boolean` para evitar parse error de `null` no JSON.

## Se tivesse mais tempo
- Mudar a arquitetura para hexagonal 
- Adicionar DTOs separados para request/response e documentação OpenAPI.
- Cobrir casos de expiração automática e reativação/remoção definitiva de cupons deletados.
- Incluir testes de integração end-to-end (MockMvc) e pipeline de CI.
- Externalizar configuração do H2 para perfis dev/test e preparar profile de produção (PostgreSQL).
