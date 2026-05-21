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

### Stress Lab

* `POST /api/load/cpu?seconds=20`
* `POST /api/load/memory-leak?megabytes=100`
* `POST /api/load/memory-clear`
* `POST /api/load/virtual-threads?count=1000`
* `POST /api/load/platform-threads?count=100`

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
