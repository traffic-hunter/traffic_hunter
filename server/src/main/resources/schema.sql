CREATE SEQUENCE if not exists agent_id START 1;
CREATE TABLE if not exists agent
(
    id              INTEGER                     DEFAULT nextval('agent_id') NOT NULL,
    agent_id        VARCHAR                     NOT NULL,
    agent_name      VARCHAR                     NOT NULL,
    agent_version   VARCHAR                     NOT NULL,
    agent_boot_time TIMESTAMPTZ                 NOT NULL,
    CONSTRAINT pk_metric_measurement PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS metric_measurement (
                                                  time timestamptz            not null,
                                                  metric_data jsonb           not null,
                                                  agent_id integer            not null
);

CREATE TABLE IF NOT EXISTS transaction_measurement (
                                                       time timestamptz            not null,
                                                       transaction_data jsonb      not null,
                                                       agent_id integer            not null
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