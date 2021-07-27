# Backend dev technical (Impl)

This project is the resolution of the technical test located in [ dalogax/backendDevTest ](https://github.com/dalogax/backendDevTest)

## Dependencies

- Java 11
- Spring-Boot 2
- Spring WebFlux
- Resilience4j

## Configuration (application[-profile].yml)


Host and enpoints to the Mock service (inside the [provided Docker](./doc/readme.md)):

```
app:
  clients:
    productLegacy:
      host: http://localhost:3001
      endpoint:
        similarIds: /product/{id}/similarids
        getProduct: /product/{id}
```

To activate the resilience4j event log, `enable` must exist and be `true`:

```
resilience4j:
  circuitbreaker:
    log:
      enable: true
      excludes: SUCCESS, IGNORED_ERROR, FAILURE_RATE_EXCEEDED
  bulkhead:
    log:
      enable: true
      excludes: CALL_PERMITTED, CALL_FINISHED
  timelimiter:
    log:
      enable: true
      excludes: SUCCESS
  retry:
    log:
      enable: true
      excludes: SUCCESS, IGNORED_ERROR
  ratelimiter:
    log:
      enable: true
      excludes: SUCCESSFUL_ACQUIRE
```

## Docs

- [open-api.yaml](open-api.yaml)

