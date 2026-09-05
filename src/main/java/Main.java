
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;

import moteur.Classement;
import moteur.Partie;
import moteur.TerminalPasPleinEcranException;

public class Main {
    public static void main(String[] args){
        boolean terminer = false;
        Scanner scanner = new Scanner(System.in);
        String choix;

        while(!terminer){  
            try (BufferedReader menu = new BufferedReader(new FileReader("data/Menu.txt"))) {
                String menu_line;
                while ((menu_line = menu.readLine()) != null) {
                    System.out.println(menu_line);
                }
            } catch (IOException m) {
                m.printStackTrace();
            }
            System.out.print("Choix : ");
            choix = scanner.next();
            if(choix.equals("1")){
                try {
                    verifierTerminal();
                    Partie.lancerPartie();
                } catch (TerminalPasPleinEcranException e) {
                    System.err.println(e.getMessage());
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException ie) {
                        System.err.println("Erreur interrompu");
                    }
                }
            } else if(choix.equals("2")){
                try {
                    Partie.clearScreen();
                    Classement classement = new Classement();
                    classement.afficherScore();
                } catch (IOException e) {
                    System.out.println("Impossible de lire le classement");
                }
                System.out.println("Appuyer sur 'x' pour quitter.");
                while (!scanner.next().equals("x")) {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        System.err.println("Erreur interrompu");
                    }
                }
            } else if(choix.equals("3")){
                terminer = true;
            } else {
                System.out.println("La saisie est incorrecte");
            }
            
        }
        scanner.close();
    }

    private static void verifierTerminal() throws TerminalPasPleinEcranException {
        int[] mesures = mesurerFenetreActive();
        boolean pleinEcran = mesures[0] == 1;
        int largeur = mesures[1];
        int hauteur = mesures[2];
        int largeurEcran = mesures[3];
        int hauteurEcran = mesures[4];

        if (!pleinEcran) {
            throw new TerminalPasPleinEcranException(
                "Le terminal n'est pas en plein ecran (" + largeur + "x" + hauteur
                + ", ecran " + largeurEcran + "x" + hauteurEcran
                + "). Agrandis la fenetre (F11) puis reessaie.");
        }
    }

    private static int[] mesurerFenetreActive() throws TerminalPasPleinEcranException {
        File script = null;
        try {
            script = File.createTempFile("consize", ".ps1");
            try (FileWriter writer = new FileWriter(script)) {
                writer.write(scriptFenetreActive());
            }

            Process process = new ProcessBuilder(
                    "powershell.exe", "-NoProfile", "-WindowStyle", "Hidden",
                    "-ExecutionPolicy", "Bypass", "-File", script.getAbsolutePath())
                .redirectErrorStream(true)
                .start();

            String sortie = "";
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String ligne;
                while ((ligne = reader.readLine()) != null) {
                    if (!ligne.isBlank()) {
                        sortie = ligne.trim();
                    }
                }
            }
            process.waitFor();

            String[] parts = sortie.split("\\s+");
            if (parts.length < 5) {
                throw new TerminalPasPleinEcranException("Impossible de lire la taille du terminal.");
            }
            return new int[] {
                Integer.parseInt(parts[0]),
                Integer.parseInt(parts[1]),
                Integer.parseInt(parts[2]),
                Integer.parseInt(parts[3]),
                Integer.parseInt(parts[4])
            };
        } catch (IOException | InterruptedException | NumberFormatException e) {
            throw new TerminalPasPleinEcranException("Impossible de lire la taille du terminal.");
        } finally {
            if (script != null) {
                script.delete();
            }
        }
    }

    private static String scriptFenetreActive() {
        return String.join("\n",
            "Add-Type @'",
            "using System;",
            "using System.Runtime.InteropServices;",
            "public class Win {",
            "  [DllImport(\"user32.dll\")] public static extern IntPtr GetForegroundWindow();",
            "  [DllImport(\"user32.dll\")] public static extern bool IsZoomed(IntPtr hWnd);",
            "  [DllImport(\"user32.dll\")] public static extern bool GetWindowRect(IntPtr hWnd, out RECT r);",
            "  [DllImport(\"user32.dll\")] public static extern int GetSystemMetrics(int nIndex);",
            "  public struct RECT { public int Left; public int Top; public int Right; public int Bottom; }",
            "}",
            "'@",
            "$hwnd = [Win]::GetForegroundWindow()",
            "$r = New-Object Win+RECT",
            "[void][Win]::GetWindowRect($hwnd, [ref]$r)",
            "$sw = [Win]::GetSystemMetrics(0)",
            "$sh = [Win]::GetSystemMetrics(1)",
            "$ww = $r.Right - $r.Left",
            "$wh = $r.Bottom - $r.Top",
            "$full = 0",
            "if ([Win]::IsZoomed($hwnd)) { $full = 1 }",
            "if ($ww -ge ($sw - 16) -and $wh -ge ($sh - 16)) { $full = 1 }",
            "Write-Output \"$full $ww $wh $sw $sh\""
        );
    }
}
