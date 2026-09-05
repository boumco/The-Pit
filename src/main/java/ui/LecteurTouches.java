package ui;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;

import org.jline.utils.NonBlockingReader;

public class LecteurTouches implements Runnable {

    private final BlockingQueue<ActionTouche> fileActions;
    private final ConsoleBrut console;
    private volatile boolean actif = true;

    public LecteurTouches(BlockingQueue<ActionTouche> fileActions, ConsoleBrut console) {
        this.fileActions = fileActions;
        this.console = console;
    }

    public void arreter() {
        actif = false;
    }

    @Override
    public void run() {
        try {
            while (actif) {
                ActionTouche action = lireProchaineAction();
                if (action != null) {
                    fileActions.put(action);
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (IOException e) {
            System.err.println("Erreur de lecture clavier : " + e.getMessage());
        }
    }

    private ActionTouche lireProchaineAction() throws IOException {
        NonBlockingReader reader = console.reader();
        int octet = reader.read();
        if (octet == -1) {
            return null;
        }
        if (octet == '\r' || octet == '\n') {
            return ActionTouche.ENTREE;
        }
        if (octet == 27) {
            int suivant = reader.read(40);
            if (suivant == '[' || suivant == 'O') {
                int code = reader.read(40);
                if (code == 'A') {
                    return ActionTouche.HAUT;
                }
                if (code == 'B') {
                    return ActionTouche.BAS;
                }
                if (code == 'C') {
                    return ActionTouche.DROITE;
                }
                if (code == 'D') {
                    return ActionTouche.GAUCHE;
                }
            }
            return null;
        }
        if (octet == 0 || octet == 224) {
            int code = reader.read(40);
            if (code == 72) {
                return ActionTouche.HAUT;
            }
            if (code == 80) {
                return ActionTouche.BAS;
            }
            if (code == 75) {
                return ActionTouche.GAUCHE;
            }
            if (code == 77) {
                return ActionTouche.DROITE;
            }
            return null;
        }
        return null;
    }
}
