package cn.sollyu.spigot;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.nio.file.Paths;
import java.security.CodeSource;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@SuppressWarnings("WeakerAccess")
public class Language {
    private Language() {
    }

    private static final Language instance = new Language();

    public static void main(String[] args) throws IOException, URISyntaxException {
        //System.out.println(Language.getInstance().getFile("QuickShop", "message"));
        //Language.getInstance().saveAllLanguage();
    }

    public static Language getInstance() {
        return instance;
    }

    public File getFile(String parentFolder, String fileName) {
        return getFile(new File(parentFolder), fileName, Locale.getDefault());
    }

    public File getFile(File parentFolder, String fileName) {
        return getFile(parentFolder, fileName, Locale.getDefault());
    }

    public File getFile(File parentFolder, String fileName, Locale locale) {
        String fileBaseName = FilenameUtils.getBaseName(fileName);
        String fileExtension = FilenameUtils.getExtension(fileName);
        if (fileExtension == null || fileExtension.isEmpty()) {
            fileExtension = "yml";
        }

        File languageFile = new File(new File(parentFolder, "language"), String.format("%s_%s.%s", fileBaseName, locale.toLanguageTag(), fileExtension));
        if (!languageFile.exists()) {
            System.out.println(languageFile);
            languageFile = new File(new File(parentFolder, "language"), String.format("%s.%s", fileBaseName, fileExtension));
        }
        return languageFile;
    }

    public void saveAllLanguageN(JavaPlugin javaPlugin) {
        saveAllLanguageN(javaPlugin, false);
    }

    public void saveAllLanguageN(JavaPlugin javaPlugin, boolean replace) {
        try {
            saveAllLanguage(javaPlugin, replace);
        } catch (IOException e) {
            javaPlugin.getLogger().warning(getStackTraceString(e));
        }
    }

    public void saveAllLanguage(JavaPlugin javaPlugin) throws IOException {
        saveAllLanguage(javaPlugin, false);
    }

    public void saveAllLanguage(JavaPlugin javaPlugin, boolean replace) throws IOException {
        CodeSource codeSource = javaPlugin.getClass().getProtectionDomain().getCodeSource();
        if (codeSource != null) {
            ZipInputStream zipInputStream = new ZipInputStream(codeSource.getLocation().openStream());
            while (true) {
                ZipEntry zipEntry = zipInputStream.getNextEntry();
                if (zipEntry == null) {
                    break;
                }
                String name = zipEntry.getName();
                if (name != null && name.startsWith("language/") && !name.equalsIgnoreCase("language/")) {
                    File languageFile = Paths.get(javaPlugin.getDataFolder().getAbsolutePath(), "language", FilenameUtils.getName(name)).toFile();
                    if (!languageFile.getParentFile().exists() && !languageFile.getParentFile().mkdirs()) {
                        throw new IOException("create folder error: " + languageFile.getParentFile().getAbsolutePath());
                    }
                    javaPlugin.saveResource("language/" + languageFile.getName(), replace);
                }
            }
        }
    }

    /**
     * Handy function to get a loggable stack trace from a Throwable
     *
     * @param tr An exception to log
     */
    public static String getStackTraceString(Throwable tr) {
        if (tr == null) {
            return "";
        }

        // This is to reduce the amount of log spew that apps do in the non-error
        // condition of the network being unavailable.
        Throwable t = tr;
        while (t != null) {
            if (t instanceof UnknownHostException) {
                return "";
            }
            t = t.getCause();
        }

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        tr.printStackTrace(pw);
        pw.flush();
        return sw.toString();
    }
}
