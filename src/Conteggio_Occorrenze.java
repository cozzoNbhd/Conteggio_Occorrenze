import java.io.*;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.LongAdder;

import static java.lang.Character.toLowerCase;

public class Conteggio_Occorrenze {

    public static class Task implements Runnable {
        private int id_task;
        private File file;
        private Map<Character, LongAdder> m;

        public Task(int id_task, File file, Map<Character, LongAdder> m) {
            this.id_task = id_task;
            this.file = file;
            this.m = m;
        }

        public void run() {
            try {
                FileInputStream byteStream = new FileInputStream(this.file);
                InputStreamReader charStream = new InputStreamReader(byteStream, Charset.forName("UTF-8"));
                BufferedReader bufCharStream = new BufferedReader(charStream);
                int ascii_c;
                while ((ascii_c = bufCharStream.read()) != -1) {
                    char c = (char) ascii_c;
                    if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z'))
                        this.m.computeIfAbsent(toLowerCase(c), k -> new LongAdder()).increment();
                }
            } catch (IOException e) {
                System.err.println("Errore");
            }
        }

    }


    public static void main(String[] args) throws InterruptedException {

        if (args.length == 0) {
            System.out.println("Errore nel formato di input");
            System.exit(1);
        }

        ExecutorService service = Executors.newCachedThreadPool();

        Map<Character, LongAdder> m = new ConcurrentHashMap<>();

        for (int i = 0; i < args.length; i++) {
            File f = new File(args[i]);
            if (!f.isFile()) {
                System.out.println("Il percorso " + i + " NON corrisponde ad un file!");
                continue;
            }
            service.execute(new Task(i, f, m));
        }

        service.shutdown();

        if (!service.awaitTermination(60000, TimeUnit.SECONDS))
            System.err.println("I thread non sono stati completati entro i tempi!");

        Iterator<Character> it = m.keySet().iterator();

        while (it.hasNext()) {
            Character c = (Character) it.next();
            System.out.println(c + "," + m.get(c));
        }

        System.exit(0);
    }

}