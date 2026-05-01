FROM openjdk:11-jre-slim

WORKDIR /app

COPY kalc.jar .
COPY lib/ ./lib/

# Set environment variables (better: use secrets)
ENV DB_USER=eposuser
ENV DB_SERVER=localhost
ENV DB_PORT=3306
ENV DB_NAME=KALCpos

# Use secrets instead
RUN echo '${DB_USER}' > /run/secrets/db_user || true

CMD ["java", "-cp", "kalc.jar:lib/*", "ke.kalc.pos.forms.StartPOS"]