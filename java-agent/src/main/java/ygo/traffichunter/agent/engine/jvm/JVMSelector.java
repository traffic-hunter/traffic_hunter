/**
 * The MIT License
 *
 * Copyright (c) 2024 yungwang-o
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
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
