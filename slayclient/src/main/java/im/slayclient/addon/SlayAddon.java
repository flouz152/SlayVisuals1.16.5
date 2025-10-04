package im.slayclient.addon;

/**
 * Base contract for addons that integrate with the SlayClient runtime.
 * Implementations must be compiled against Java 19 bytecode to leverage
 * the full standard library available in the distribution.
 */
public interface SlayAddon {

    String id();

    String name();

    default String author() {
        return "Unknown";
    }

    default String description() {
        return "";
    }

    void onInitialize(SlayAddonContext context) throws Exception;
}
