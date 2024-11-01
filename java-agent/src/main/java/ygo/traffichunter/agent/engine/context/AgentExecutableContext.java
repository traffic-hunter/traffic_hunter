package ygo.traffichunter.agent.engine.context;

public interface AgentExecutableContext {

    void preload();

    void running();

    void exit();
}
