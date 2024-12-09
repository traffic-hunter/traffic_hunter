package ygo.traffic_hunter.persistence.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ygo.traffic_hunter.domain.entity.Agent;

/**
 * @author yungwang-o
 * @version 1.0.0
 */
@Component
public class AgentRowMapper implements RowMapper<Agent> {

    @Override
    public Agent mapRow(final ResultSet rs, final int rowNum) throws SQLException {
        return Agent.create(
                rs.getInt("id"),
                rs.getString("agent_id"),
                rs.getString("agent_name"),
                rs.getString("agent_version"),
                rs.getTimestamp("agent_boot_time").toInstant()
        );
    }
}
