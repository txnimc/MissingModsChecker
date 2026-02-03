package toni.missingmodschecker;

import net.fabricmc.loader.api.LanguageAdapter;
import net.fabricmc.loader.api.ModContainer;

import java.io.IOException;

public class Bootstrap  implements LanguageAdapter {
    @Override
    public <T> T create(ModContainer mod, String value, Class<T> type) {
        throw new UnsupportedOperationException();
    }

    static {
        try {
            MissingModsChecker.launch();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}