package im.slayclient.addon;

import com.google.common.collect.ImmutableList;
import im.expensive.Expensive;
import net.minecraft.client.Minecraft;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.ServiceLoader;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Discovers addon jars placed inside <minecraft>/slayclient/addons. The loader relies on the
 * ServiceLoader contract: addons must provide a META-INF/services/im.slayclient.addon.SlayAddon
 * file that lists fully qualified implementation classes compiled with Java 19.
 */
public final class AddonManager {

    private final File addonDirectory;
    private final List<AddonDescriptor> loadedAddons = new ArrayList<>();

    public AddonManager(File addonDirectory) {
        this.addonDirectory = addonDirectory;
    }

    public List<AddonDescriptor> getLoadedAddons() {
        return ImmutableList.copyOf(loadedAddons);
    }

    public void discoverAddons() {
        loadedAddons.clear();
        File[] jars = addonDirectory.listFiles((dir, name) -> name.toLowerCase().endsWith(".jar"));
        if (jars == null) {
            return;
        }
        for (File jar : jars) {
            try {
                loadAddonJar(jar);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void loadAddonJar(File jarFile) throws IOException {
        try (JarFile jar = new JarFile(jarFile)) {
            URL[] urls = {jarFile.toURI().toURL()};
            try (URLClassLoader classLoader = new URLClassLoader(urls, getClass().getClassLoader())) {
                ServiceLoader<SlayAddon> serviceLoader = ServiceLoader.load(SlayAddon.class, classLoader);
                for (SlayAddon addon : serviceLoader) {
                    SlayAddonContext context = new SlayAddonContext(Minecraft.getInstance(), Expensive.getInstance());
                    try {
                        addon.onInitialize(context);
                        loadedAddons.add(new AddonDescriptor(addon.id(), addon.name(), addon.author(), addon.description(), addon));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public List<String> listContainedClasses(File jarFile) {
        List<String> entries = new ArrayList<>();
        try (JarFile jar = new JarFile(jarFile)) {
            Enumeration<JarEntry> enumeration = jar.entries();
            while (enumeration.hasMoreElements()) {
                JarEntry entry = enumeration.nextElement();
                if (!entry.isDirectory() && entry.getName().endsWith(".class")) {
                    entries.add(entry.getName().replace('/', '.'));
                }
            }
        } catch (IOException ignored) {
        }
        return entries;
    }

    public void writeTemplateAddon() {
        File template = new File(addonDirectory, "example-addon.txt");
        if (template.exists()) {
            return;
        }
        List<String> lines = List.of(
                "# SlayClient addon quick start",
                "Compile against Java 19 and include a service definition:",
                "META-INF/services/im.slayclient.addon.SlayAddon",
                "Each line in the file should contain an implementation class.",
                "Use the provided SlayAddonContext to interact with the client."
        );
        try {
            Files.write(template.toPath(), lines);
        } catch (IOException ignored) {
        }
    }
}
