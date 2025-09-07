# ELK Stack with Filebeat for Kernel Logs

A Docker Compose setup for collecting, processing, and visualizing kernel logs using the Elastic Stack (ELK) with Filebeat.

## Overview

This project deploys a complete log management pipeline:
- **Filebeat**: Collects kernel logs from the host system
- **Logstash**: Processes and enriches log data
- **Elasticsearch**: Stores and indexes log data
- **Kibana**: Provides visualization and exploration of logs

## Prerequisites

- Docker and Docker Compose
- At least 6GB of available RAM (4GB allocated to Elasticsearch)
- Linux host with kernel logs available (typically in `/var/log`)

## Quick Start

1. Clone this repository
2. Create the external network (if it doesn't exist):
   ```bash
   docker network create elk
   ```
3. Start the stack:
   ```bash
   docker compose -f elk.docker-compose.yaml up -d
   ```
4. Access the services:
   - Kibana: http://localhost:5601
   - Elasticsearch: http://localhost:9200

## Configuration

### Resource Allocation

The stack is configured with optimized resource limits:

- **Elasticsearch**: 4GB memory limit, 2GB Java heap
- **Logstash**: 1GB memory limit, 512MB Java heap
- **Kibana**: 768MB memory limit, 512MB Node.js heap
- **Filebeat**: 100MB memory limit

### Filebeat Configuration

Ensure you have a `filebeat.yml` file in the same directory with appropriate configuration to collect kernel logs. Example:

```yaml
filebeat.inputs:
   - type: filestream
     id: tracking-json-logs
     paths:
        - /usr/share/filebeat/logs/tracking.json.log
        - /usr/share/filebeat/logs/tracking.*.json.log
     fields:
        app: tracking
        env: dev
     fields_under_root: true
      # NEEDED for Brave tracer JSON strings
     json.keys_under_root: true
     json.add_error_key: true
     json.message_key: message


# Processors - data transformation pipeline
# ✅ SIMPLIFIED PROCESSING - No JSON decoding needed!
#processors:
#  - rename:
#      fields:
#        - from: "level"          # Rename to ECS standard
#          to: "log.level"
#        - from: "thread_name"    # Rename to ECS standard
#          to: "log.thread"
#        - from: "logger_name"    # Rename to ECS standard
#          to: "log.logger"
#      ignore_missing: true    # ← CRITICAL: Don't fail if field missing
#      fail_on_error: false    # ← Don't fail the entire processor

#  - add_fields:
#      target: ""  # Add to root level
#      fields:
#        infrastructure: "docker"
#        team: "apd-team"
#        log_type: "application"

# No processors - keep Filebeat lightweight
processors: []

# Output to Logstash
output.logstash:
   hosts: ["logstash:5044"]
   compression_level: 3

# Optional: Enable for debugging
#output.console:
#  enabled: true    # Change to true to see logs in console
#  pretty: true

# Filebeat monitoring
#logging:
#  level: info           # Filebeat's log level
#  to_files: true        # Write logs to file
#  files:
#    path: /var/log/filebeat  # Where to write
#    name: filebeat.log       # Filename
#    keepfiles: 7             # Keep 7 days of logs


logging:
   level: warning  # Reduce noise in production
```

### Logstash Pipeline

The `logstash.conf` file should define your processing pipeline. Example:

```lombok.config
input {
  beats {
    port => 5044
  }
}

filter {

    # 1. Parse the main JSON log line
    json {
        source => "message"
        target => "parsed"
        remove_field => ["message"]
    }

    # 2. Extract timestamp (properly)
    date {
        match => [ "[parsed][@timestamp]", "ISO8601" ]
        target => "@timestamp"
        remove_field => ["[parsed][@timestamp]"]
    }

    # 3. Handle Brave tracer JSON-in-JSON
    if [parsed][message] == "brave.Tracer" and [parsed][message] =~ /^{.*}$/ {
      json {
        source => "[parsed][message]"
        target => "[brave]"
        remove_field => ["[parsed][message]"]
      }
    }

    # Remove unnecessary fields
    # 4. Standardize field names (ECS compliance)
    mutate {
       rename => {
            "[parsed][level]" => "[log][level]"
            "[parsed][thread_name]" => "[log][thread]"
            "[parsed][logger_name]" => "[log][logger]"

            "[brave][traceId]" => "[trace][id]"
            "[brave][id]" => "[span][id]"
            "[brave][parentId]" => "[trace][parent_id]"
            "[brave][name]" => "[event][action]"
            "[brave][kind]" => "[event][kind]"
            "[brave][duration]" => "[event][duration]"
      }

      add_field => {
            "[ecs][version]" => "1.6.0"
            "[event][dataset]" => "tracking.logs"
      }
    }


      # 5. Clean up stack traces
      if [parsed][stack_trace] {
        mutate {
          gsub => [
            "[parsed][stack_trace]", "\r\n", " ",
            "[parsed][stack_trace]", "\n", " ",
            "[parsed][stack_trace]", "\t", "  "
          ]
        }
      }


    # 6. Remove unnecessary fields
    #  mutate {
    #    remove_field => [
    #      "host", "agent", "ecs", "input", "log", "tags",
    #      "event", "@version", "version", "[parsed][@version]",
    #      "[parsed][version]"
    #    ]
    #  }
}

output {
  elasticsearch {
    hosts => ["elasticsearch:9200"]
    index => "tracking-logs-%{+YYYY.MM.dd}"
    document_type => "_doc"
  }

  # Enable for debugging
  stdout {
    codec => rubydebug
  }
}
```

## Data Persistence

Elasticsearch data is persisted in a Docker volume named `elk-data`. To remove the volume when cleaning up:

```bash
docker compose -f elk.docker-compose.yaml down
docker compose -f elk.docker-compose.yaml down -v
```

## Monitoring

Check container status and resource usage:

```bash
docker compose -f elk.docker-compose.yaml ps
docker-compose ps
docker stats
```

## Troubleshooting

1. If containers fail to start, check available memory
2. Verify the `elk` network exists: `docker network ls`
3. Check container logs: `docker-compose logs [service-name]`

## Security Notes

- X-Pack security is disabled for development purposes
- For production use, enable security features and use proper authentication
- Filebeat runs as root to access system log files

## License

This project is provided as-is for educational and development purposes.
