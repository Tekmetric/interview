FROM grafana/grafana

RUN mkdir /var/lib/grafana/dashboards

ADD datasources.yaml /etc/grafana/provisioning/datasources

ADD dashboards.yaml /etc/grafana/provisioning/dashboards

ADD statistics.json /var/lib/grafana/dashboards

EXPOSE 3000
