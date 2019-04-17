package app.metatron.discovery.domain.ipm.common.value.workbench;

import java.io.Serializable;
import org.springframework.util.StringUtils;

/**
 * 추출앱 표현식 모듈
 */
public class ExtractAppModule implements Serializable {

    private static final long serialVersionUID = 1L;

    public enum ModuleType {
        BASIC, CUSTOM
    }

    /**
     * id
     */
    private String id;

    /**
     * module type
     */
    private ModuleType moduleType;

    /**
     * namespace
     */
    private String namespace;

    /**
     * name
     */
    private String name;

    /**
     * description
     */
    private String description;

    /**
     * arguments
     */
    private String[] args;

    /**
     * multiple
     */
    private boolean multiple;

    /**
     * input data
     */
    private String input;

    public boolean isMultiple() {
        return multiple;
    }

    public void setMultiple(boolean multiple) {
        this.multiple = multiple;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String[] getArgs() {
        return args;
    }

    public void setArgs(String[] args) {
        this.args = args;
    }

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public ModuleType getModuleType() {
        return moduleType;
    }

    public void setModuleType(ModuleType moduleType) {
        this.moduleType = moduleType;
    }

    @Override
    public String toString() {
        return "[ExtractAppModule] " +
                "type: " +this.moduleType.toString() + " / " +
                "namespace: "+this.namespace.toString() + " / " +
                "name: "+this.name + " / " +
                "description: "+this.description + " / " +
                "args: "+ StringUtils.arrayToCommaDelimitedString(this.args);
    }
}
