# CouponApi

## Como rodar
- Requisitos: JDK 21+.
- Comandos:
  ```bash
  cd CouponApi
  ./mvnw clean test
  ./mvnw spring-boot:run
  ```
- Banco: H2 em memória (`jdbc:h2:mem:coupondb`) com console em `/h2-console`.

## Como validei
- Testes unitários de domínio e service (validação de código, desconto mínimo, sanitização, soft delete).
- Testes unitários do controller com Mockito (status e payload do POST/DELETE/GET).

## Decisões técnicas
- Spring Boot 4 + JPA/Hibernate com H2 em memória para rapidez de desenvolvimento.
-.UUID como `id` e `code` como chave lógica com sanitização (6 caracteres alfanuméricos).
- Soft delete com status (`ACTIVE/INACTIVE/DELETED`) para manter histórico.
- Booleans como `Boolean` para evitar parse error de `null` no JSON.

## Se tivesse mais tempo
- Mudar a arquitetura para hexagonal 
- Adicionar DTOs separados para request/response e documentação OpenAPI.
- Cobrir casos de expiração automática e reativação/remoção definitiva de cupons deletados.
- Incluir testes de integração end-to-end (MockMvc) e pipeline de CI.
- Externalizar configuração do H2 para perfis dev/test e preparar profile de produção (PostgreSQL).
