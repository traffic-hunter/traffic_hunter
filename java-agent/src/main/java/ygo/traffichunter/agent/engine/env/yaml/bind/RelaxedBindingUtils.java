package ygo.traffichunter.agent.engine.env.yaml.bind;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.introspector.Property;
import org.yaml.snakeyaml.introspector.PropertyUtils;

/**
 * @author yungwang-o
 * @version 1.0.0
 */
public class RelaxedBindingUtils extends PropertyUtils {

    private static final Logger log = LoggerFactory.getLogger(RelaxedBindingUtils.class);

    @Override
    public Property getProperty(final Class<?> type, final String name) {
        return super.getProperty(type, kebabToCamel(name));
    }

    private String kebabToCamel(final String kebab) {
        final StringBuilder sb = new StringBuilder();

        String[] split = kebab.split("-");

        for(int i = 0; i < split.length; i++) {
            if(i == 0) {
                sb.append(split[i]);
                continue;
            }
            sb.append(split[i].replaceFirst("^[a-z]", String.valueOf(split[i].charAt(0)).toUpperCase()));
        }

        return sb.toString();
    }
}
