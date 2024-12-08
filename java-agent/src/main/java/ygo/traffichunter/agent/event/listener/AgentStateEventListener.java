package ygo.traffichunter.agent.event.listener;

import java.util.EventListener;
import ygo.traffichunter.agent.event.object.AgentStateEvent;

/**
 * @author yungwang-o
 * @version 1.0.0
 */
public interface AgentStateEventListener extends EventListener {

    void onEvent(AgentStateEvent event);
}
