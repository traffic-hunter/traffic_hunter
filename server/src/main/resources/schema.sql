CREATE SEQUENCE if not exists agent_id START 1;
CREATE SEQUENCE if not exists member_id START 1;

DO
$$
DECLARE
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'roles') THEN
        CREATE TYPE roles AS ENUM ('ADMIN', 'USER');
    END IF;
END;
$$;;

CREATE TABLE if not exists agent
(
    id              INTEGER                     DEFAULT nextval('agent_id') NOT NULL,
    agent_id        VARCHAR                     NOT NULL,
    agent_name      VARCHAR                     NOT NULL,
    agent_version   VARCHAR                     NOT NULL,
    agent_boot_time TIMESTAMPTZ                 NOT NULL,
    CONSTRAINT pk_agent PRIMARY KEY (id)
);

CREATE TABLE if not exists member
(
    id              INTEGER                     DEFAULT nextval('member_id') NOT NULL,
    email           VARCHAR                     UNIQUE NOT NULL,
    password        VARCHAR                     NOT NULL,
    isAlarm         BOOLEAN                     NOT NULL,
    role            ROLES                       NOT NULL,

    CONSTRAINT pk_member PRIMARY KEY (id)
);

CREATE TABLE if not exists threshold
(
    id                     INTEGER  NOT NULL ,
    cpu_threshold          INTEGER  NOT NULL CHECK (cpu_threshold BETWEEN 0 AND 100),
    memory_threshold       INTEGER  NOT NULL CHECK (memory_threshold BETWEEN 0 AND 100),
    thread_threshold       INTEGER  NOT NULL CHECK (thread_threshold BETWEEN 0 AND 100),
    web_request_threshold  INTEGER  NOT NULL,
    web_thread_threshold   INTEGER  NOT NULL CHECK (web_thread_threshold BETWEEN 0 AND 100),
    dbcp_threshold         INTEGER  NOT NULL CHECK (dbcp_threshold BETWEEN 0 AND 100),
    CONSTRAINT pk_threshold_id PRIMARY KEY (id)
);

INSERT INTO threshold
(                      id,
                       cpu_threshold,
                       memory_threshold,
                       thread_threshold,
                       web_request_threshold,
                       web_thread_threshold,
                       dbcp_threshold
)
VALUES (1, 80, 80, 80, 100, 80, 80)
ON CONFLICT (id) DO NOTHING ;

INSERT INTO member (email, password, isAlarm, role)
VALUES ('admin', 'admin', true, 'ADMIN')
ON CONFLICT (email) DO NOTHING;

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