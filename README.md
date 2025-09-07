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
   docker-compose up -d
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
  enabled: true
  paths:
    - /var/log/kern.log
    - /var/log/syslog

output.logstash:
  hosts: ["logstash:5044"]
```

### Logstash Pipeline

The `logstash.conf` file should define your processing pipeline. Example:

```conf
input {
  beats {
    port => 5044
  }
}

filter {
  # Add your log processing filters here
  grok {
    match => { "message" => "%{SYSLOGTIMESTAMP:timestamp} %{SYSLOGHOST:hostname} %{DATA:program}(?:\[%{POSINT:pid}\])?: %{GREEDYDATA:message}" }
  }
  date {
    match => [ "timestamp", "MMM  d HH:mm:ss", "MMM dd HH:mm:ss" ]
  }
}

output {
  elasticsearch {
    hosts => ["elasticsearch:9200"]
    index => "kernel-logs-%{+YYYY.MM.dd}"
  }
}
```

## Data Persistence

Elasticsearch data is persisted in a Docker volume named `elk-data`. To remove the volume when cleaning up:

```bash
docker-compose down -v
```

## Monitoring

Check container status and resource usage:

```bash
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
