package ygo.traffichunter.agent.engine.classloader;

import org.junit.jupiter.api.Test;
import ygo.AbstractTest;
import ygo.traffichunter.agent.engine.queue.SyncQueue;

public class ClassLoaderTest extends AbstractTest {

    @Test
    void Sync_Queue의_클래스로더를_확인한다() {

        System.out.println(SyncQueue.class.getClassLoader().getName());
        System.out.println(SyncQueue.class.getClassLoader().getParent().getName());
    }
}
