package common;

import org.apache.log4j.Logger;

import java.io.*;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Esta classe foi copiada de ChironUtils de Eduardo Ogasawara. Tive de recriar pois o
 * comportamento do método runCommand teve de ser modificado para retornar o resultado
 * da execução do comando externo. Além disso corrigi alguns erros em :
 * runCommand()
 * getStreamAsString()
 */
public class Utils {
    private static Logger logger = Logger.getLogger(Utils.class);
    protected static boolean verbose = true;
    public static int SLEEP_INTERVAL = 10;
    public static String SEPARATOR = "/";
    public static String LINE_SEPARATOR = System.getProperty("line.separator");
    private static Locale local = new Locale("en");
    private static DecimalFormatSymbols simbols = new DecimalFormatSymbols(local);
    private static DecimalFormat formatDec[] = {
            new DecimalFormat("###0", simbols),
            new DecimalFormat("###0.0", simbols),
            new DecimalFormat("###0.00", simbols),
            new DecimalFormat("###0.000", simbols),
            new DecimalFormat("###0.0000", simbols),
            new DecimalFormat("###0.00000", simbols),
            new DecimalFormat("###0.000000", simbols),
            new DecimalFormat("###0.0000000", simbols),
            new DecimalFormat("###0.00000000", simbols),
            new DecimalFormat("###0.000000000", simbols)
    };

    /**
     * Obtém um valor do tipo FLOAT em uma String, respeitando o número de casas
     * decimais informadas pelo parâmetro decimal
     *
     * @param value
     * @param decimal
     * @return
     */
    public static String formatFloat(Double value, int decimal) {
        if (decimal != -1) {
            return formatDec[decimal].format(value);
        } else {
            return String.valueOf(value);
        }
    }

    /**
     * Método que faz com que uma thread durma por 100 ms
     */
    public static void sleep100milis() {
        sleep(SLEEP_INTERVAL);
    }

    /**
     * Método que faz com que uma thread durma por milisDelay ms
     */
    public static void sleep(int milisDelay) {
        try {
            Thread.sleep(milisDelay);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Método que adiciona uma barra, caso o diretório informado não termine com
     * uma barra
     *
     * @param value
     * @return
     */
    public static String checkDir(String value) {
        if (value != null && value.charAt(value.length() - 1) != '/') {
            value += "/";
        }
        return value;
    }

    /**
     * Cria um diretório através do caminha passado como parâmetro
     *
     * @param directory
     * @return
     */
    public static boolean createDirectory(String directory) {
        boolean result = true;
        File f = new File(directory);
        try {
            f.mkdir();
        } catch (Exception e) {
            result = false;
            e.printStackTrace();
        }
        f = null;
        return result;
    }

    /**
     * Cria um arquivo com o nome informado como parâmetro
     *
     * @param fileName
     */
    public static void CreateFile(String fileName) {
        File f = new File(fileName);
        try {
            f.createNewFile();
        } catch (Exception e) {
            e.printStackTrace();
        }
        f = null;
    }

    /**
     * Cria um arquivo com o nome e o conteúdo passados como parâmetros
     *
     * @param fileName
     * @param Data
     */
    public static void WriteFile(String fileName, String Data) {
        try {
            CreateFile(fileName);
            BufferedWriter out = new BufferedWriter(new FileWriter(fileName));
            out.write(Data);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Lê um arquivo
     *
     * @param fileDirectory
     * @return
     * @throws IOException
     */
    public static String ReadFile(String fileDirectory) throws IOException {
        BufferedReader in = new BufferedReader(new FileReader(fileDirectory));
        StringBuilder contents = new StringBuilder();
        String line;
        while ((line = in.readLine()) != null) {
            contents.append(line);
            contents.append(Utils.LINE_SEPARATOR);
        }
        in.close();
        return contents.toString();
    }

    /**
     * Obtém o caminho de um arquivo na forma canônica
     *
     * @param file
     * @return
     */
    public static String getCanonicalPath(File file) {
        String result = "";
        try {
            result = file.getCanonicalPath();
            result = result.replaceAll("\\\\", "/");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return result;
    }

    /**
     * Obtém o nome do arquivo
     *
     * @param fullname
     * @return
     */
    public static String getFileName(String fullname) {
        File file = new File(fullname);
        String result = getFileName(file);
        file = null;
        return result;
    }

    public static String getFileName(File file) {
        String result = "";
        result = file.getName();
        result = result.replaceAll("\\\\", "/");
        return result;
    }

    /**
     * Avalia se o sistema operacional de execução é Windows
     *
     * @return
     */
    public static boolean isWindows() {
        String os = System.getProperty("os.name").toLowerCase();
        return (os.indexOf("win") >= 0);
    }

    /**
     * Avalia se o sistema operacional de execução é MAC
     *
     * @return
     */
    public static boolean isMacOS() {
        String os = System.getProperty("os.name").toLowerCase();
        return (os.indexOf("mac") >= 0);
    }

    /**
     * Move um arquivo
     *
     * @param destination
     * @param origin
     * @throws IOException
     * @throws InterruptedException
     */
    public static void moveFile(String origin, String destination) throws IOException, InterruptedException {
        String cmd = "";
        createDirectory(destination);
        if (Utils.isWindows()) {
            cmd = "move " + origin + " " + destination;
            cmd = cmd.replace("/", "\\");
        } else {
            cmd = "mv " + origin + " " + destination;
        }
        runCommand(cmd, null);
    }

    /**
     * Realiza a cópia de um arquivo
     *
     * @param destination
     * @param origin
     * @throws IOException
     * @throws InterruptedException
     */
    public static void copyFile(String origin, String destination) throws IOException, InterruptedException {
        String cmd = "";
        if (Utils.isWindows()) {
            cmd = "xcopy " + origin + " " + destination;
            cmd = cmd.replace("/", "\\");
            cmd += " /q /c /y";
        } else {
            Utils.createDirectory(destination);
            cmd = "cp " + origin + " " + destination;
        }
        runCommand(cmd, null);
    }

    /**
     * Método que copia os arquivos do template
     *
     * @param origin
     * @throws IOException
     * @throws InterruptedException
     */
    public static void copyTemplateFiles(String origin, String destination) throws IOException, InterruptedException {
        String cmd = "";
        if (Utils.isWindows()) {
            cmd = "xcopy " + origin + "*.* " + destination;
            cmd = cmd.replace("/", "\\");
            cmd += " /s /q /c /y ";
        } else if (Utils.isMacOS()) {
            cmd = "cp " + origin + "* " + destination;
        } else {
            cmd = "cp " + origin + "* " + destination + " -r -f";
        }
        runCommand(cmd, null);
    }

    /**
     * Deleta um arquivo
     *
     * @param fileName
     * @param fileDir
     * @throws IOException
     * @throws InterruptedException
     */
    public static void deleteFile(String fileName, String fileDir) throws IOException, InterruptedException {
        // Código que deleta um arquivo
        File file = new File(fileDir + fileName);
        file.delete();
    }

    public static boolean isFile(String fileDir) {
        File file = new File(fileDir);
        boolean result = file.exists();
        file = null;
        return result;
    }


    protected static boolean checkFile(String name) {
        File file = new File(name);
        boolean result = true;
        if (!file.exists()) {
            result = false;
        }
        file = null;
        return result;
    }

    /**
     * Roda um comando genérico, tendo em vista o sistema operacional
     *
     * @param cmd
     * @param dir
     * @return ExecutionResult
     * @throws IOException
     * @throws InterruptedException
     */
    public static ExecutionResult runCommand(String cmd, String dir) throws IOException, InterruptedException {
        Runtime runtime = Runtime.getRuntime();
        Process pr = null;
        int code = 0;
        String RUN_MODE = "STRING";
        String envp[] = {
                "AVAILABLE_PROCESSORS=" + runtime.availableProcessors(),
                "INSTANCE_TIME=" + System.currentTimeMillis()
        };

        Map<String, String> env = new HashMap<>();
        if (envp.length > 0) logger.info("Environment da Execução corrente");
        for (String s : envp) {
            String[] e = s.split("=");
            logger.info(arrayToString(e));
            env.put(e[0], e[1]);
        }

        if (RUN_MODE.equals("BY SHELL")) {
            String command[] = null;
            if (Utils.isWindows()) {
                String cmdWin[] = {"cmd.exe", "/c", cmd};
                command = cmdWin;
            } else {
                String cmdLinux = cmd;
                if (cmd.contains(">")) {
                    cmdLinux = cmd.replace(">", ">>");
                }
                String cmdLin[] = {"/bin/bash", "-c", cmdLinux};
                command = cmdLin;
            }
            if (verbose) {
                logger.info(command[command.length - 1]);
            }
            logger.debug("BY SHELL mode: Invocando o método exec da classe '" + runtime.getClass().getCanonicalName() + "'");
            if (dir == null) {
                pr = runtime.exec(command);
            } else {
                pr = runtime.exec(command, null, new File(dir));
            }
        } else { // STRING mode
            logger.debug("STRING mode: Invocando o método exec da classe '" + runtime.getClass().getCanonicalName() + "'");
            if (cmd != null && cmd.trim().length() == 0) {
                return new ExecutionResult(-1, "", env);
            }
            if (dir == null) {
                pr = runtime.exec(cmd, envp);
            } else {
                pr = runtime.exec(cmd, envp, new File(dir));
            }
        }

        //        InputStream err = pr.getErrorStream();
        //        InputStream in = pr.getInputStream();
        //        InputStream out = pr.getInputStream();

        code = pr.waitFor();
        String str = "";
        //        BufferedReader stdInput = new BufferedReader(new InputStreamReader(pr.getInputStream()));
        //        while ((s = stdInput.readLine()) != null) {
        //            str = str + "\n" + s;
        //            // logger.info(s);
        //        }
        str = getStreamAsString(pr.getInputStream());

        ExecutionResult ret = new ExecutionResult(code, str, env);
        pr.destroy();
        return ret;
    }

    private static void close(Closeable c) {
        if (c != null) {
            try {
                c.close();
            } catch (IOException e) {
                // ignored
            }
        }
    }

    /**
     * Lê um input stream devolvendo uma String
     *
     * @param stream
     * @return
     */
    private static String getStreamAsString(InputStream stream) throws IOException {
        String out = "";
        String s = "";
        BufferedReader stdInput = new BufferedReader(new InputStreamReader(stream));
        while ((s = stdInput.readLine()) != null) {
            out = out.concat(s + Utils.LINE_SEPARATOR);
        }
        logger.info("'" + out + "'");
        return out;
    }

    /**
     * Faz um toString para Array
     *
     * @param objArray
     * @return
     */
    private static String arrayToString(Object[] objArray) {
        String out = "[ ";
        boolean first = true;
        for (Object s : objArray) {
            if (!first) {
                out = out.concat(", ");
            }
            out = out.concat(s.toString());
            first = false;
        }
        out = out.concat(" ]");
        // logger.info("'" + out + "'");
        return out;
    }
}
