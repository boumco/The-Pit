package ui;

import java.io.IOException;

import org.jline.terminal.Attributes;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.utils.NonBlockingReader;

public class ConsoleBrut implements AutoCloseable {

    private final Terminal terminal;
    private final Attributes modeNormal;

    public ConsoleBrut() throws IOException {
        terminal = TerminalBuilder.builder().system(true).build();
        modeNormal = terminal.enterRawMode();
        terminal.echo(false);
    }

    public NonBlockingReader reader() {
        return terminal.reader();
    }

    @Override
    public void close() {
        try {
            if (modeNormal != null) {
                terminal.setAttributes(modeNormal);
            }
            terminal.close();
        } catch (IOException e) {
            System.err.println("Impossible de restaurer le terminal");
        }
    }
}
