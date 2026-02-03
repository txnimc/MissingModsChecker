package toni.missingmodschecker;

#if NEO

import net.neoforged.fml.earlydisplay.DisplayWindow;
import net.neoforged.neoforgespi.earlywindow.ImmediateWindowProvider;

public class NeoBootstrap extends DisplayWindow implements ImmediateWindowProvider {
    public NeoBootstrap() {
        MissingModsChecker.launch();
    }
}
#endif