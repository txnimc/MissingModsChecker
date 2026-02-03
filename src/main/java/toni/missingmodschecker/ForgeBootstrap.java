package toni.missingmodschecker;

#if FORGE

import lombok.SneakyThrows;
import net.minecraftforge.fml.earlydisplay.DisplayWindow;
import net.minecraftforge.fml.loading.ImmediateWindowProvider;

import java.io.IOException;

public class ForgeBootstrap extends DisplayWindow implements ImmediateWindowProvider {
    @SneakyThrows
    public ForgeBootstrap() {
        try {
            MissingModsChecker.launch();
        } catch (IOException e) {
            System.out.println(e.getMessage());
            Thread.sleep(5000);
            throw new RuntimeException(e);
        }
    }
}

#endif
