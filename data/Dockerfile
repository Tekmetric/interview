FROM registry.fedoraproject.org/fedora-minimal:41 AS requirements

RUN microdnf install --setopt=install_weak_deps=False -y \
    	python3.11-devel && \
    mkdir -p /opt/app && \
	python3.11 -m venv /opt/app/venv


ENV VIRTUAL_ENV=/opt/app/venv
ENV PATH="$VIRTUAL_ENV/bin:$PATH"

COPY requirements.txt /requirements.txt

RUN pip3 install --upgrade wheel setuptools pip && \
	pip3 install -r /requirements.txt


FROM registry.fedoraproject.org/fedora-minimal:41

RUN microdnf install --setopt=install_weak_deps=False -y \
      python3.11 &&\
    microdnf clean all && \
	rm -rf /var/cache/dnf && \
	useradd -m -u 1000 tekmetric && \
	mkdir -p /opt/app/output && \
	chown -R tekmetric:tekmetric /opt/app

COPY --chown=tekmetric:tekmetric --from=requirements /opt/app/venv /opt/app/venv

COPY --chown=tekmetric:tekmetric tekmetric_data /opt/app/tekmetric_data
COPY --chown=tekmetric:tekmetric docker-entrypoint.sh /usr/local/bin/docker-entrypoint.sh

USER tekmetric

ENV VIRTUAL_ENV=/opt/app/venv
ENV PATH="$VIRTUAL_ENV/bin:$PATH"

VOLUME /opt/app/output

WORKDIR /opt/app

ENTRYPOINT ["/usr/local/bin/docker-entrypoint.sh"]
CMD ["run"]