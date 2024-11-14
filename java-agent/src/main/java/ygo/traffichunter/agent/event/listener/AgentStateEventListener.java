package ygo.traffichunter.agent.event.listener;

import java.util.EventListener;
import ygo.traffichunter.agent.event.object.AgentStateEvent;

public interface AgentStateEventListener extends EventListener {

    void onEvent(AgentStateEvent event);
}
