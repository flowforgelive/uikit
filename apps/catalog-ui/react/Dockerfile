# Dockerfile for UIKit Catalog (React/Next.js)
# Build context: repository root (run with -f apps/catalog-ui/react/Dockerfile .)
#
# Expects pre-built Next.js standalone output in apps/catalog-ui/react/.next/
# Build KMP + Next.js locally before docker build (see deploy.sh).
#
# ============================================================
# Single stage: minimal production runtime
# ============================================================
FROM node:22-alpine

WORKDIR /app

ENV NODE_ENV=production
ENV NEXT_TELEMETRY_DISABLED=1
ENV HOSTNAME=0.0.0.0
ENV PORT=3000

RUN addgroup --system --gid 1001 nodejs && \
    adduser --system --uid 1001 nextjs

# Copy standalone server (pre-built by deploy.sh)
COPY apps/catalog-ui/react/.next/standalone/ ./

# Copy static assets
COPY apps/catalog-ui/react/.next/static ./.next/static

USER nextjs

EXPOSE 3000

CMD ["node", "server.js"]
