CREATE TABLE if not exists agent
(
    id              INTEGER                     NOT NULL,
    agent_id        VARCHAR                     NOT NULL,
    agent_name      VARCHAR                     NOT NULL,
    agent_version   VARCHAR                     NOT NULL,
    agent_boot_time TIMESTAMPTZ                 NOT NULL,
    CONSTRAINT pk_metric_measurement PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS metric_measurement (
                                    time timestamptz            not null,
                                    agent_id varchar            not null,
                                    agent_name varchar          not null,
                                    agent_version varchar       not null,
                                    agent_boot_time timestamptz not null,
                                    metric_data jsonb           not null
);

CREATE TABLE IF NOT EXISTS transaction_measurement (
                                         time timestamptz            not null,
                                         agent_id varchar            not null,
                                         agent_name varchar          not null,
                                         agent_version varchar       not null,
                                         agent_boot_time timestamptz not null,
                                         transaction_data jsonb      not null
);

SELECT create_hypertable(
               'metric_measurement',
               by_range('time'),
                if_not_exists := TRUE
       );

SELECT create_hypertable(
               'transaction_measurement',
               by_range('time'),
                if_not_exists := TRUE
       );

CREATE INDEX IF NOT EXISTS metric_measurement_agent_id_time_idx ON metric_measurement (agent_id, time);
CREATE INDEX IF NOT EXISTS metric_measurement_agent_id_idx ON metric_measurement (agent_id);

CREATE INDEX IF NOT EXISTS transaction_measurement_agent_id_time_idx ON transaction_measurement (agent_id, time);
CREATE INDEX IF NOT EXISTS transaction_measurement_agent_id_idx ON transaction_measurement (agent_id);