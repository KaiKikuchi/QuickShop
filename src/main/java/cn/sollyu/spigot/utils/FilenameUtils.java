//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package cn.sollyu.spigot.utils;

import java.io.File;
import java.util.Collection;
import java.util.Iterator;

public class FilenameUtils {
    private static final int NOT_FOUND = -1;
    public static final char EXTENSION_SEPARATOR = '.';
    public static final String EXTENSION_SEPARATOR_STR = Character.toString('.');
    private static final char UNIX_SEPARATOR = '/';
    private static final char WINDOWS_SEPARATOR = '\\';
    private static final char SYSTEM_SEPARATOR;
    private static final char OTHER_SEPARATOR;

    public FilenameUtils() {
    }

    static boolean isSystemWindows() {
        return SYSTEM_SEPARATOR == '\\';
    }

    private static boolean isSeparator(char ch) {
        return ch == '/' || ch == '\\';
    }

    public static String normalize(String filename) {
        return doNormalize(filename, SYSTEM_SEPARATOR, true);
    }

    public static String normalize(String filename, boolean unixSeparator) {
        char separator = (char) (unixSeparator ? 47 : 92);
        return doNormalize(filename, (char) separator, true);
    }

    public static String normalizeNoEndSeparator(String filename) {
        return doNormalize(filename, SYSTEM_SEPARATOR, false);
    }

    public static String normalizeNoEndSeparator(String filename, boolean unixSeparator) {
        char separator = (char) (unixSeparator ? 47 : 92);
        return doNormalize(filename, (char) separator, false);
    }

    private static String doNormalize(String filename, char separator, boolean keepSeparator) {
        if (filename == null) {
            return null;
        } else {
            failIfNullBytePresent(filename);
            int size = filename.length();
            if (size == 0) {
                return filename;
            } else {
                int prefix = getPrefixLength(filename);
                if (prefix < 0) {
                    return null;
                } else {
                    char[] array = new char[size + 2];
                    filename.getChars(0, filename.length(), array, 0);
                    char otherSeparator = separator == SYSTEM_SEPARATOR ? OTHER_SEPARATOR : SYSTEM_SEPARATOR;

                    for (int i = 0; i < array.length; ++i) {
                        if (array[i] == otherSeparator) {
                            array[i] = separator;
                        }
                    }

                    boolean lastIsDirectory = true;
                    if (array[size - 1] != separator) {
                        array[size++] = separator;
                        lastIsDirectory = false;
                    }

                    int i;
                    for (i = prefix + 1; i < size; ++i) {
                        if (array[i] == separator && array[i - 1] == separator) {
                            System.arraycopy(array, i, array, i - 1, size - i);
                            --size;
                            --i;
                        }
                    }

                    for (i = prefix + 1; i < size; ++i) {
                        if (array[i] == separator && array[i - 1] == '.' && (i == prefix + 1 || array[i - 2] == separator)) {
                            if (i == size - 1) {
                                lastIsDirectory = true;
                            }

                            System.arraycopy(array, i + 1, array, i - 1, size - i);
                            size -= 2;
                            --i;
                        }
                    }

                    label109:
                    for (i = prefix + 2; i < size; ++i) {
                        if (array[i] == separator && array[i - 1] == '.' && array[i - 2] == '.' && (i == prefix + 2 || array[i - 3] == separator)) {
                            if (i == prefix + 2) {
                                return null;
                            }

                            if (i == size - 1) {
                                lastIsDirectory = true;
                            }

                            for (int j = i - 4; j >= prefix; --j) {
                                if (array[j] == separator) {
                                    System.arraycopy(array, i + 1, array, j + 1, size - i);
                                    size -= i - j;
                                    i = j + 1;
                                    continue label109;
                                }
                            }

                            System.arraycopy(array, i + 1, array, prefix, size - i);
                            size -= i + 1 - prefix;
                            i = prefix + 1;
                        }
                    }

                    if (size <= 0) {
                        return "";
                    } else if (size <= prefix) {
                        return new String(array, 0, size);
                    } else if (lastIsDirectory && keepSeparator) {
                        return new String(array, 0, size);
                    } else {
                        return new String(array, 0, size - 1);
                    }
                }
            }
        }
    }

    public static String concat(String basePath, String fullFilenameToAdd) {
        int prefix = getPrefixLength(fullFilenameToAdd);
        if (prefix < 0) {
            return null;
        } else if (prefix > 0) {
            return normalize(fullFilenameToAdd);
        } else if (basePath == null) {
            return null;
        } else {
            int len = basePath.length();
            if (len == 0) {
                return normalize(fullFilenameToAdd);
            } else {
                char ch = basePath.charAt(len - 1);
                return isSeparator(ch) ? normalize(basePath + fullFilenameToAdd) : normalize(basePath + '/' + fullFilenameToAdd);
            }
        }
    }


    public static String separatorsToUnix(String path) {
        return path != null && path.indexOf(92) != -1 ? path.replace('\\', '/') : path;
    }

    public static String separatorsToWindows(String path) {
        return path != null && path.indexOf(47) != -1 ? path.replace('/', '\\') : path;
    }

    public static String separatorsToSystem(String path) {
        if (path == null) {
            return null;
        } else {
            return isSystemWindows() ? separatorsToWindows(path) : separatorsToUnix(path);
        }
    }

    public static int getPrefixLength(String filename) {
        if (filename == null) {
            return -1;
        } else {
            int len = filename.length();
            if (len == 0) {
                return 0;
            } else {
                char ch0 = filename.charAt(0);
                if (ch0 == ':') {
                    return -1;
                } else if (len == 1) {
                    if (ch0 == '~') {
                        return 2;
                    } else {
                        return isSeparator(ch0) ? 1 : 0;
                    }
                } else {
                    int posUnix;
                    if (ch0 == '~') {
                        posUnix = filename.indexOf(92, 1);
                        if (posUnix == -1 && posUnix == -1) {
                            return len + 1;
                        } else {
                            posUnix = posUnix == -1 ? posUnix : posUnix;
                            posUnix = posUnix == -1 ? posUnix : posUnix;
                            return Math.min(posUnix, posUnix) + 1;
                        }
                    } else {
                        char ch1 = filename.charAt(1);
                        if (ch1 == ':') {
                            ch0 = Character.toUpperCase(ch0);
                            if (ch0 >= 'A' && ch0 <= 'Z') {
                                return len != 2 && isSeparator(filename.charAt(2)) ? 3 : 2;
                            } else {
                                return ch0 == '/' ? 1 : -1;
                            }
                        } else if (isSeparator(ch0) && isSeparator(ch1)) {
                            posUnix = filename.indexOf(47, 2);
                            int posWin = filename.indexOf(92, 2);
                            if ((posUnix != -1 || posWin != -1) && posUnix != 2 && posWin != 2) {
                                posUnix = posUnix == -1 ? posWin : posUnix;
                                posWin = posWin == -1 ? posUnix : posWin;
                                return Math.min(posUnix, posWin) + 1;
                            } else {
                                return -1;
                            }
                        } else {
                            return isSeparator(ch0) ? 1 : 0;
                        }
                    }
                }
            }
        }
    }

    public static int indexOfLastSeparator(String filename) {
        if (filename == null) {
            return -1;
        } else {
            int lastUnixPos = filename.lastIndexOf(47);
            int lastWindowsPos = filename.lastIndexOf(92);
            return Math.max(lastUnixPos, lastWindowsPos);
        }
    }

    public static int indexOfExtension(String filename) {
        if (filename == null) {
            return -1;
        } else {
            int extensionPos = filename.lastIndexOf(46);
            int lastSeparator = indexOfLastSeparator(filename);
            return lastSeparator > extensionPos ? -1 : extensionPos;
        }
    }

    public static String getPrefix(String filename) {
        if (filename == null) {
            return null;
        } else {
            int len = getPrefixLength(filename);
            if (len < 0) {
                return null;
            } else if (len > filename.length()) {
                failIfNullBytePresent(filename + '/');
                return filename + '/';
            } else {
                String path = filename.substring(0, len);
                failIfNullBytePresent(path);
                return path;
            }
        }
    }

    public static String getPath(String filename) {
        return doGetPath(filename, 1);
    }

    public static String getPathNoEndSeparator(String filename) {
        return doGetPath(filename, 0);
    }

    private static String doGetPath(String filename, int separatorAdd) {
        if (filename == null) {
            return null;
        } else {
            int prefix = getPrefixLength(filename);
            if (prefix < 0) {
                return null;
            } else {
                int index = indexOfLastSeparator(filename);
                int endIndex = index + separatorAdd;
                if (prefix < filename.length() && index >= 0 && prefix < endIndex) {
                    String path = filename.substring(prefix, endIndex);
                    failIfNullBytePresent(path);
                    return path;
                } else {
                    return "";
                }
            }
        }
    }

    public static String getFullPath(String filename) {
        return doGetFullPath(filename, true);
    }

    public static String getFullPathNoEndSeparator(String filename) {
        return doGetFullPath(filename, false);
    }

    private static String doGetFullPath(String filename, boolean includeSeparator) {
        if (filename == null) {
            return null;
        } else {
            int prefix = getPrefixLength(filename);
            if (prefix < 0) {
                return null;
            } else if (prefix >= filename.length()) {
                return includeSeparator ? getPrefix(filename) : filename;
            } else {
                int index = indexOfLastSeparator(filename);
                if (index < 0) {
                    return filename.substring(0, prefix);
                } else {
                    int end = index + (includeSeparator ? 1 : 0);
                    if (end == 0) {
                        ++end;
                    }

                    return filename.substring(0, end);
                }
            }
        }
    }

    public static String getName(String filename) {
        if (filename == null) {
            return null;
        } else {
            failIfNullBytePresent(filename);
            int index = indexOfLastSeparator(filename);
            return filename.substring(index + 1);
        }
    }

    private static void failIfNullBytePresent(String path) {
        int len = path.length();

        for (int i = 0; i < len; ++i) {
            if (path.charAt(i) == 0) {
                throw new IllegalArgumentException("Null byte present in file/path name. There are no known legitimate use cases for such data, but several injection attacks may use it");
            }
        }

    }

    public static String getBaseName(String filename) {
        return removeExtension(getName(filename));
    }

    public static String getExtension(String filename) {
        if (filename == null) {
            return null;
        } else {
            int index = indexOfExtension(filename);
            return index == -1 ? "" : filename.substring(index + 1);
        }
    }

    public static String removeExtension(String filename) {
        if (filename == null) {
            return null;
        } else {
            failIfNullBytePresent(filename);
            int index = indexOfExtension(filename);
            return index == -1 ? filename : filename.substring(0, index);
        }
    }

    public static boolean isExtension(String filename, String extension) {
        if (filename == null) {
            return false;
        } else {
            failIfNullBytePresent(filename);
            if (extension != null && !extension.isEmpty()) {
                String fileExt = getExtension(filename);
                return fileExt.equals(extension);
            } else {
                return indexOfExtension(filename) == -1;
            }
        }
    }

    public static boolean isExtension(String filename, String[] extensions) {
        if (filename == null) {
            return false;
        } else {
            failIfNullBytePresent(filename);
            if (extensions != null && extensions.length != 0) {
                String fileExt = getExtension(filename);
                String[] var3 = extensions;
                int var4 = extensions.length;

                for (int var5 = 0; var5 < var4; ++var5) {
                    String extension = var3[var5];
                    if (fileExt.equals(extension)) {
                        return true;
                    }
                }

                return false;
            } else {
                return indexOfExtension(filename) == -1;
            }
        }
    }

    public static boolean isExtension(String filename, Collection<String> extensions) {
        if (filename == null) {
            return false;
        } else {
            failIfNullBytePresent(filename);
            if (extensions != null && !extensions.isEmpty()) {
                String fileExt = getExtension(filename);
                Iterator var3 = extensions.iterator();

                String extension;
                do {
                    if (!var3.hasNext()) {
                        return false;
                    }

                    extension = (String) var3.next();
                } while (!fileExt.equals(extension));

                return true;
            } else {
                return indexOfExtension(filename) == -1;
            }
        }
    }

    static {
        SYSTEM_SEPARATOR = File.separatorChar;
        if (isSystemWindows()) {
            OTHER_SEPARATOR = '/';
        } else {
            OTHER_SEPARATOR = '\\';
        }

    }
}
