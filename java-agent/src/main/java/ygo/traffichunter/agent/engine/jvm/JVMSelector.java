package ygo.traffichunter.agent.engine.jvm;

import com.sun.tools.attach.AttachNotSupportedException;
import com.sun.tools.attach.VirtualMachine;
import com.sun.tools.attach.VirtualMachineDescriptor;
import java.io.IOException;
import java.util.Objects;
import java.util.logging.Logger;
import javax.management.remote.JMXServiceURL;

/**
 * @author yungwang-o
 * @version 1.0.0
 */
public class JVMSelector {

    private static final Logger log = Logger.getLogger(JVMSelector.class.getName());

    public static JMXServiceURL getVMXServiceUrl(final String targetJVMPath) {

        try {
            final VirtualMachineDescriptor vmDescriptor = getVirtualMachineDescriptor(targetJVMPath);

            final VirtualMachine vm = VirtualMachine.attach(vmDescriptor.id().trim());

            final String jmxUrl = vm.startLocalManagementAgent();

            return new JMXServiceURL(jmxUrl);
        } catch (AttachNotSupportedException | IOException e) {
            log.warning("Not found jvm service url : " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public static VirtualMachine getVM(final String targetJVMPath) {

        try {
            final VirtualMachineDescriptor vmDescriptor = getVirtualMachineDescriptor(targetJVMPath);

            return VirtualMachine.attach(vmDescriptor.id().trim());

        } catch (AttachNotSupportedException | IOException e) {
            log.warning("Not found jvm service url : " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private static VirtualMachineDescriptor getVirtualMachineDescriptor(final String targetJVMPath) {

        return VirtualMachine.list().stream()
                .filter(vm -> Objects.equals(vm.displayName(), targetJVMPath))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No virtual machine found"));
    }

    public static String displayName(final int selectNumber) {
        return VirtualMachine.list().get(selectNumber - 1).displayName();
    }

    public static String JvmId(final int selectNumber) {
        return VirtualMachine.list().get(selectNumber - 1).id();
    }
}
