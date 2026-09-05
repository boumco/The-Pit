package ui;

import java.io.IOException;

import org.jline.terminal.Attributes;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.utils.NonBlockingReader;

public class ConsoleBrut implements AutoCloseable {

    private static Terminal terminal;
    private static Attributes modeNormal;
    private static boolean hookInstalle;

    public static synchronized Terminal obtenir() throws IOException {
        if (terminal == null) {
            terminal = TerminalBuilder.builder().system(true).build();
            modeNormal = terminal.getAttributes().copy();
            if (!hookInstalle) {
                Runtime.getRuntime().addShutdownHook(new Thread(ConsoleBrut::restaurerModeNormal, "restore-tty"));
                hookInstalle = true;
            }
        }
        return terminal;
    }

    // A retenir : ne jamais fermer le Terminal systeme, sinon Windows coupe l'echo.
    public static synchronized void restaurerModeNormal() {
        if (terminal == null || modeNormal == null) {
            return;
        }
        try {
            Attributes attrs = modeNormal.copy();
            attrs.setLocalFlag(Attributes.LocalFlag.ECHO, true);
            attrs.setLocalFlag(Attributes.LocalFlag.ICANON, true);
            terminal.echo(true);
            terminal.setAttributes(attrs);
        } catch (Exception ignored) {
        }
    }

    public ConsoleBrut() throws IOException {
        obtenir();
        terminal.enterRawMode();
        terminal.echo(false);
    }

    public NonBlockingReader reader() {
        return terminal.reader();
    }

    @Override
    public void close() {
        restaurerModeNormal();
    }
}
