package ygo;

import com.sun.tools.attach.VirtualMachine;
import com.sun.tools.attach.VirtualMachineDescriptor;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ygo.traffichunter.agent.TrafficHunterAgent;
import ygo.traffichunter.agent.engine.AgentExecutionEngine;
import ygo.traffichunter.agent.engine.jvm.JVMSelector;
import ygo.traffichunter.agent.property.TrafficHunterAgentProperty;
import ygo.traffichunter.banner.AsciiBanner;
import ygo.traffichunter.retry.backoff.policy.ExponentialBackOffPolicy;
import ygo.traffichunter.util.AgentUtil;

public class AgentMain {

    private static final Logger log = LoggerFactory.getLogger(AgentMain.class);

    public static void main(String[] args) {

        AsciiBanner.print();

        final Scanner sc = new Scanner(System.in);

        System.out.println();

        final List<VirtualMachineDescriptor> virtualMachineDescriptors = VirtualMachine.list();

        System.out.println("Enter the PID of the project name you want to monitor.!!");
        System.out.println("If you want to exit this program, enter -1.\n");
        for(int i = 0; i < virtualMachineDescriptors.size(); i++) {
            System.out.println((i + 1) + "." + " PID : " + virtualMachineDescriptors.get(i).id() + ", " + "Display Name : " +virtualMachineDescriptors.get(i).displayName());
        }

        System.out.println();
        System.out.print("Enter jvm list number -> ");
        final int jvm = sc.nextInt();

        if(jvm == -1) {
            System.out.println("bye bye.");
            System.exit(0);
        }

        System.out.println();
        System.out.print("Enter the server address to connect ex) 192.168.0.1:8080... -> ");
        final String serverAddr = sc.next();

        if(!AgentUtil.isAddr(serverAddr)) {
            System.out.println("Not a valid server address.");
            System.exit(1);
        }

        if(serverAddr.equals("-1")) {
            System.out.println("bye bye.");
            System.exit(0);
        }

        System.out.println();
        System.out.print("""
                Enter the frequency for sending metrics. The unit of time for this is seconds.\

                It is recommended that the appropriate cycle be set to 10 to 20. ->\s"""
        );

        final int interval = sc.nextInt();

        if(interval == -1) {
            System.out.println("bye bye.");
            System.exit(0);
        }



        final TrafficHunterAgentProperty property = TrafficHunterAgent.connect(AgentUtil.HTTP_URL.getUrl(serverAddr))
                .scheduleInterval(interval)
                .scheduleTimeUnit(TimeUnit.SECONDS)
                .faultTolerant()
                .retry(10)
                .backOffPolicy(ExponentialBackOffPolicy.DEFAULT)
                .targetJVM(JVMSelector.displayName(jvm))
                .complete();

        AgentExecutionEngine.run(property);
    }
}