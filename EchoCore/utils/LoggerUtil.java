package sorryplspls.EchoCore.utils;

import sorryplspls.EchoCore.Main;

public class LoggerUtil {

    public static void logError(String context, Exception e) {
        Main.getInstance().getLogger().warning("---------------------------------------");
        Main.getInstance().getLogger().warning(context);
        Main.getInstance().getLogger().warning(e.getMessage());
        Main.getInstance().getLogger().warning("---------------------------------------");
    }

}
