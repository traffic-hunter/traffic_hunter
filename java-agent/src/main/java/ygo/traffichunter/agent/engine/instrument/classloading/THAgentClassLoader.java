package ygo.traffichunter.agent.engine.instrument.classloading;

public class THAgentClassLoader extends ClassLoader {

    public THAgentClassLoader(final ClassLoader parent) {
        super(parent);
    }

    @Override
    protected Class<?> loadClass(final String name, final boolean resolve) throws ClassNotFoundException {
        synchronized (getClassLoadingLock(name)) {
            Class<?> clazz = findLoadedClass(name);

            if(clazz == null) {
                try {
                    clazz = findClass(name);
                } catch (ClassNotFoundException ignored) {
                }

                if(clazz == null) {
                    clazz = getParent().loadClass(name);
                }
            }
            if(resolve) {
                resolveClass(clazz);
            }
            return clazz;
        }
    }


}
