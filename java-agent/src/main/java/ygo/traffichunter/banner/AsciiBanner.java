package ygo.traffichunter.banner;

public class AsciiBanner {

    // ANSI 색상 및 스타일 코드
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BOLD = "\u001B[1m";
    public static final String ANSI_UNDERLINE = "\u001B[4m";
    public static final String ANSI_BLINK = "\u001B[5m";
    public static final String ANSI_BRIGHT_RED = "\u001B[91m";
    public static final String ANSI_BRIGHT_BLUE = "\u001B[94m";
    public static final String ANSI_BRIGHT_GREEN = "\u001B[92m";
    public static final String ANSI_BRIGHT_YELLOW = "\u001B[93m";

    public static void print() {
        String banner =
                "+-------------------------------------------------------------------+\n" +
                        "|   " + ANSI_BOLD + ANSI_BRIGHT_RED + "_____             __  __ _" + ANSI_RESET + "        " + ANSI_BOLD + ANSI_BRIGHT_BLUE + "_   _             _" + ANSI_RESET + "           |\n" +
                        "|  " + ANSI_BOLD + ANSI_BRIGHT_RED + "|_   _| __ __ _   / _|/ _(_) ___" + ANSI_RESET + "  " + ANSI_BOLD + ANSI_BRIGHT_BLUE + "| | | |_   _ _ __ | |_ ___ ___" + ANSI_RESET + " |\n" +
                        "|    " + ANSI_BOLD + ANSI_BRIGHT_RED + "| || '__/ _` | | |_| |_| |/ __|" + ANSI_RESET + " " + ANSI_BOLD + ANSI_BRIGHT_BLUE + "| |_| | | | | '_ \\| __/ _ \\_  " + ANSI_RESET + " |\n" +
                        "|    " + ANSI_BOLD + ANSI_BRIGHT_RED + "| || | | (_| | |  _|  _| | (__" + ANSI_RESET + "  " + ANSI_BOLD + ANSI_BRIGHT_BLUE + "|  _  | |_| | | | | ||  __/ ) " + ANSI_RESET + " |\n" +
                        "|    " + ANSI_BOLD + ANSI_BRIGHT_RED + "|_||_|  \\__,_| |_| |_| |_|\\___|" + ANSI_RESET + " " + ANSI_BOLD + ANSI_BRIGHT_BLUE + "|_| |_|\\__,_|_| |_|\\__\\___|__" + ANSI_RESET + "  |\n" +
                        "|                                                                   |\n" +
                        "|                      " + ANSI_BRIGHT_GREEN + "_                    _" + ANSI_RESET + "                       |\n" +
                        "|                     " + ANSI_BRIGHT_GREEN + "/ \\   __ _  ___ _ __ | |_" + ANSI_RESET + "                     |\n" +
                        "|                    " + ANSI_BRIGHT_GREEN + "/ _ \\ / _` |/ _ \\ '_ \\| __|" + ANSI_RESET + "                    |\n" +
                        "|                   " + ANSI_BRIGHT_GREEN + "/ ___ \\ (_| |  __/ | | | |_" + ANSI_RESET + "                     |\n" +
                        "|                  " + ANSI_BRIGHT_GREEN + "/_/   \\_\\__, |\\___|_| |_|\\__|" + ANSI_RESET + "                    |\n" +
                        "|                          " + ANSI_BRIGHT_GREEN + "|___/" + ANSI_RESET + "                                    |\n" +
                        "|                                                                   |\n" +
                        "|           " + ANSI_UNDERLINE + ANSI_BRIGHT_YELLOW + "Traffic Monitoring and Analysis System" + ANSI_RESET + "                  |\n" +
                        "|                        " + ANSI_BRIGHT_YELLOW + "Version 1.0.0" + ANSI_RESET + "                              |\n" +
                        "|                                                                   |\n" +
                        "|                    " + ANSI_BLINK + ANSI_BRIGHT_YELLOW + "Author: yungwang-o" + ANSI_RESET + "                             |\n" +
                        "+-------------------------------------------------------------------+\n";

        System.out.println(banner);
    }
}
