# 🚀 Azure JVM Performance Observatory

> A cloud-native self-diagnosing JVM observability platform built with Java 21, Spring Boot, Docker, and Microsoft Azure.

## Architecture Overview

```text
User / Tester
    |
    v
Web Dashboard (Live Metrics UI)
    |
    v
Spring Boot Java 21 Observatory
    |---- JVM Stress Lab
    |       |---- CPU Burn Simulation
    |       |---- Memory Leak Simulation
    |       |---- Virtual Thread Load
    |       |---- Platform Thread Blocking
    |
    |---- JVM Metrics API
    |       |---- Heap Metrics
    |       |---- GC Metrics
    |       |---- Thread Metrics
    |
    |---- JvmHealthWatcher
    |       |---- Heap Pressure Detection
    |       |---- Thread Spike Detection
    |       |---- JVM Heartbeats
    |
    |---- Micrometer / Prometheus
    |
    v
Docker Container
    |
    v
Azure Container Registry
    |
    v
Azure Container Apps
    |
    +---- Application Insights Java Agent
    |       |---- Request Tracing
    |       |---- JVM Telemetry
    |       |---- Exceptions
    |
    +---- Azure Monitor
    |       |---- Metrics
    |       |---- Logs
    |
    +---- Kusto Queries
    |
    +---- Azure Alert Rules
            |---- Email Notification
```

---

## Project Overview

Azure JVM Performance Observatory is a cloud-native performance engineering platform designed to simulate, observe, diagnose, and alert on JVM anomalies in real time.

This platform goes beyond standard Spring Boot monitoring by introducing controlled JVM stress simulations, self-diagnosing health watchers, Azure-native telemetry pipelines, and alert-driven production diagnostics.

---

## Core Features

### JVM Stress Lab

* CPU burn simulation
* Memory leak simulation
* Virtual thread stress testing
* Platform thread blocking

### JVM Metrics API

* Heap usage metrics
* Non-heap memory metrics
* Garbage collection metrics
* Live / daemon / peak thread metrics

### Self-Diagnosing Watcher

* Detects heap pressure anomalies
* Detects thread spikes
* Emits JVM heartbeat logs
* Writes JVM ALERT warnings to Azure logs
* Configurable alert thresholds (see [Configuration](#configuration))

### Observability Stack

* Spring Boot Actuator
* Micrometer
* Prometheus endpoint
* Azure Application Insights Java Agent
* Azure Monitor
* Kusto log analytics
* Azure alert rules

---

## Cloud Stack

* Java 21
* Spring Boot
* Micrometer
* Prometheus
* Docker
* Azure Container Registry
* Azure Container Apps
* Application Insights
* Azure Monitor
* Log Analytics
* Kusto Query Language (KQL)
* Azure Alert Rules

---

## Key Endpoints

### Metrics

* `GET /api/metrics/jvm`
* `GET /actuator/health`
* `GET /actuator/prometheus`

### Stress Lab (protected)

These endpoints are destructive and require the `X-Stress-Key` header (see [Security](#security)):

* `POST /api/load/cpu?seconds=20`
* `POST /api/load/memory-leak?megabytes=100`
* `POST /api/load/memory-clear`
* `POST /api/load/virtual-threads?count=1000`
* `POST /api/load/platform-threads?count=100`

```bash
curl -X POST -H "X-Stress-Key: $STRESS_LAB_KEY" \
  "http://localhost:8080/api/load/cpu?seconds=20"
```

---

## Security

The stress-lab endpoints can intentionally exhaust JVM resources, so they are
locked behind a shared API key:

* Every `POST /api/load/**` request must send the secret in the `X-Stress-Key` header.
* The key is read from the `STRESS_LAB_KEY` environment variable.
* The check **fails closed**: if `STRESS_LAB_KEY` is unset, the stress lab is locked
  entirely (every request returns `401`), so a misconfigured deployment is safe by default.
* Keys are compared in constant time to avoid timing side-channels.
* The live metrics API, Prometheus endpoint, and dashboard remain open.

Actuator exposure is limited to `health,info,metrics,prometheus`; the `heapdump`
and `threaddump` endpoints are deliberately **not** exposed.

Set the key locally or on Azure Container Apps:

```bash
# Local
export STRESS_LAB_KEY=choose-a-strong-secret

# Azure Container Apps
az containerapp update -n <app> -g <rg> \
  --set-env-vars STRESS_LAB_KEY=secretref:stress-lab-key
```

---

## Configuration

The `JvmHealthWatcher` alert thresholds are externalized so they can be tuned
without a rebuild (via `application.properties` or the matching environment
variables):

| Property | Env var | Default | Description |
| --- | --- | --- | --- |
| `observatory.health.heap-alert-threshold-percent` | `OBSERVATORY_HEALTH_HEAP_ALERT_THRESHOLD_PERCENT` | `80` | Logs a JVM ALERT when heap usage exceeds this percentage |
| `observatory.health.thread-alert-threshold` | `OBSERVATORY_HEALTH_THREAD_ALERT_THRESHOLD` | `200` | Logs a JVM ALERT when the live thread count exceeds this value |

---

## Testing

Run the suite with:

```bash
./mvnw test
```

Coverage:

| Test | What it verifies |
| --- | --- |
| `AzureJvmObservatoryApplicationTests` | Spring context boots |
| `JvmHealthWatcherTest` | Heap-usage math, the `getMax() == -1` fail-safe guard, and locale-independent (`.` separator) percentage formatting |
| `StressLabKeyFilterTest` | The `X-Stress-Key` gate: rejects missing/wrong keys, fails closed when no key is configured, allows the correct key, and leaves non-stress endpoints open |

---

## Example Kusto Queries

### JVM Alerts

```kusto
traces
| where message contains "JVM ALERT"
| order by timestamp desc
```

### JVM Heartbeats

```kusto
traces
| where message contains "JVM heartbeat"
| order by timestamp desc
```

### Request Performance

```kusto
requests
| summarize count(), avg(duration), percentile(duration, 95) by name
```

---

## Contributing

Contributions are welcome. To get set up:

1. **Prerequisites:** JDK 21 and the bundled Maven wrapper (`./mvnw`). Docker is
   optional, only needed to build/run the container image.
2. **Build and test:**

   ```bash
   ./mvnw clean verify
   ```

3. **Run locally** (the stress lab is locked unless a key is set — see [Security](#security)):

   ```bash
   STRESS_LAB_KEY=choose-a-strong-secret ./mvnw spring-boot:run
   ```

   The dashboard is then served at <http://localhost:8080/>.

### Guidelines

* Branch off `main` and open a pull request; keep changes focused and explain the *why* in the description.
* **Add or update tests** for any behavior change — see [Testing](#testing). All tests must pass before review.
* Keep new tunables externalized to configuration rather than hardcoded (follow the [Configuration](#configuration) pattern).
* Never commit secrets. The stress-lab key is supplied at runtime via `STRESS_LAB_KEY`.
* Match the existing code style and keep commits descriptive.

---

## What This Demonstrates

This project demonstrates advanced engineering capability in:

* JVM internals and performance engineering
* Java 21 virtual threads
* Cloud-native observability
* Production telemetry pipelines
* Self-diagnosing systems
* Containerized Java workloads
* Azure monitoring and diagnostics
* Log analytics and alert-driven operations
* Performance anomaly detection

---

## Portfolio Summary

**Azure JVM Performance Observatory** is a self-diagnosing JVM observability platform capable of stress simulation, JVM telemetry collection, anomaly detection, cloud-native monitoring, log analytics, and production alerting on Microsoft Azure.
