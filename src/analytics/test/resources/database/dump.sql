CREATE DOMAIN IF NOT EXISTS JSONB AS TEXT;
CREATE TABLE IF NOT EXISTS analytic_events (
    id           VARCHAR(36)                  NOT NULL PRIMARY KEY,
    aggregate_id VARCHAR(36)                  NOT NULL,
    name         VARCHAR(255)                 NOT NULL,
    body         JSONB                        NOT NULL,
    occurred_on  TIMESTAMP WITH TIME ZONE     NOT NULL,
    created_at   TIMESTAMP WITH TIME ZONE     NOT NULL,
    updated_at   TIMESTAMP WITH TIME ZONE     NOT NULL
);
