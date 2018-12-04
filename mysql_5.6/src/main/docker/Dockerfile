# Copyright 2018 Dematic, Corp.
# Licensed under the MIT Open Source License: https://opensource.org/licenses/MIT
FROM mysql:5.6.40
COPY docker-entrypoint.sh healthcheck.sh /opt/dlabs/bin/

HEALTHCHECK CMD /opt/dlabs/bin/healthcheck.sh

ENTRYPOINT ["/opt/dlabs/bin/docker-entrypoint.sh"]
