package toni.missingmodschecker;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.SneakyThrows;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.world.level.block.Blocks;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import java.util.stream.Stream;



#if FORGE
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
#endif


#if NEO
import net.neoforged.fml.common.Mod;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.loading.FMLPaths;
import java.util.Locale;
#endif


#if FORGELIKE
@Mod("missingmodschecker")
#endif
public class MissingModsChecker
{
    public static final String MODNAME = "Missing Mods Checker";
    public static final String ID = "missingmodschecker";
    public static final Logger LOGGER = LogManager.getLogger(MODNAME);
    private static final Gson GSON = new Gson();
    private static List<RequiredMod> requiredMods;


    public MissingModsChecker(#if NEO IEventBus modEventBus, ModContainer modContainer #endif) { }

    @SneakyThrows
    public static void launch() throws IOException {
        requiredMods = loadRequiredMods();

        try {
            MissingModsWindow.open(requiredMods);
        } catch (Exception e) {
            LOGGER.error("Error thrown while opening! Exiting", e);
            Thread.sleep(10000);
            System.exit(1);
        }
        LOGGER.info("missingmodsExiting");

        System.exit(0);
    }

    public static void downloadAndInstall(Consumer<String> logger) {

    }


    public static List<RequiredMod> loadRequiredMods() throws IOException {
        #if FORGE
        Path configPath = FMLPaths.CONFIGDIR.get().resolve("missing_mods_checker.json");
        #elif NEO
        Path configPath = FMLPaths.CONFIGDIR.get().resolve("missing_mods_checker.json");
        #else
        Path configPath = net.fabricmc.loader.api.FabricLoader.getInstance().getConfigDir().resolve("missing_mods_checker.json");
        #endif

        if (!Files.exists(configPath)) {
            return List.of();
        }

        String json = Files.readString(configPath);
        Type type = new TypeToken<List<RequiredMod>>() {}.getType();
        return GSON.fromJson(json, type);
    }

    public static List<RequiredMod> getMissingMods() throws IOException {
        if (requiredMods.isEmpty()) {
            return List.of();
        }

        Set<RequiredMod> remaining = new HashSet<>(requiredMods);

        try (Stream<Path> stream = Files.list(getModsFolder())) {
            stream
                .filter(p -> p.getFileName().toString().endsWith(".jar"))
                .map(p -> p.getFileName().toString())
                .forEach(fileName -> {
                    remaining.removeIf(req -> req.getPattern().matcher(fileName).matches());
                });
        }

        return remaining.stream().toList();
    }

    public static Path getModsFolder() {
        #if FORGE
        return FMLPaths.MODSDIR.get();
        #elif NEO
        #else
        return net.fabricmc.loader.api.FabricLoader.getInstance().getGameDir().resolve("mods");
        #endif
    }

    public static final class RequiredMod {
        public String displayName;
        public String pattern;
        public String url;
        private transient Pattern compiledPattern;

        public Pattern getPattern() {
            if (compiledPattern == null) {
                compiledPattern = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
            }
            return compiledPattern;
        }
    }
}
