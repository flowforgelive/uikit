# Dockerfile for UIKit Catalog (React/Next.js)
# Build context: repository root (run with -f apps/catalog-ui/react/Dockerfile .)
#
# ============================================================
# Stage 1: Build KMP JS libraries + Next.js standalone app
# ============================================================
FROM eclipse-temurin:21-jdk AS builder

# Install Node.js 22 LTS
RUN apt-get update && apt-get install -y curl && \
    curl -fsSL https://deb.nodesource.com/setup_22.x | bash - && \
    apt-get install -y nodejs && \
    rm -rf /var/lib/apt/lists/*

WORKDIR /app

# Copy Gradle wrapper and config first (layer caching)
COPY gradlew gradlew.bat settings.gradle.kts build.gradle.kts gradle.properties ./
COPY gradle/ gradle/

# Override local Java home with container's JAVA_HOME
RUN sed -i '/org.gradle.java.home/d' gradle.properties

# Exclude Compose modules not needed for React build
RUN sed -i '/compose/d' settings.gradle.kts

# Copy source modules needed for the build
COPY core/uikit/common/ core/uikit/common/
COPY core/uikit/react/ core/uikit/react/
COPY apps/catalog-ui/shared/ apps/catalog-ui/shared/
COPY apps/catalog-ui/react/ apps/catalog-ui/react/

# Build KMP JS libraries
RUN chmod +x gradlew && \
    ./gradlew :core:uikit:common:jsBrowserProductionLibraryDistribution \
              :apps:catalog-ui:shared:jsBrowserProductionLibraryDistribution \
              --no-daemon --console=plain

# Install npm dependencies (links file: dependencies)
RUN cd core/uikit/react && npm install && cd /app
RUN cd apps/catalog-ui/react && npm install

# Build Next.js standalone
RUN cd apps/catalog-ui/react && npm run build

# ============================================================
# Stage 2: Minimal production runtime
# ============================================================
FROM node:22-alpine AS runner

WORKDIR /app

ENV NODE_ENV=production
ENV NEXT_TELEMETRY_DISABLED=1
ENV HOSTNAME=0.0.0.0
ENV PORT=3000

RUN addgroup --system --gid 1001 nodejs && \
    adduser --system --uid 1001 nextjs

# Copy standalone server
COPY --from=builder /app/apps/catalog-ui/react/.next/standalone/ ./

# Copy static assets
COPY --from=builder /app/apps/catalog-ui/react/.next/static ./.next/static

USER nextjs

EXPOSE 3000

CMD ["node", "server.js"]
