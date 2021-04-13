/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fabnun.jsoupscheduler;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.TreeMap;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.SwingUtilities;

/**
 *
 * @author fabian
 */
public class Scheduler {

    final private HashSet<Job> jobs = new HashSet<>();

    public Scheduler(String code) {
        code = code.trim();
        String[] codeLines = code.split("\n");
        for (String line : codeLines) {
            line = line.trim();
            if (!line.isEmpty() && !line.startsWith("#")) {
                String[] command = line.replaceAll("\\s+", " ").trim().split("\\s+");
                if (command.length == 4) {
                    new Job(command[0], getMillis(command[1]), getMillis(command[2]), getMillis(command[3]));
                } else if (command.length == 5) {
                    new Job(command[0], getMillis(command[1]), getMillis(command[2]), getMillis(command[3]), getMillis(command[4]));
                }

            }
        }
    }

    private long getMillis(String time) {
        time = time.toLowerCase();
        Pattern pattern = Pattern.compile("\\d+(h|m|s)");
        long l = 0;
        Matcher m = pattern.matcher(time);
        while (m.find()) {
            String v = m.group();
            char p = v.charAt(v.length() - 1);
            int val = Integer.parseInt(v.substring(0, v.length() - 1));
            l = l + val * (p == 'h' ? 3600 : (p == 'm' ? 60 : 1)) * 1000;
        }
        return l;
    }

    public void start(TreeMap<String, TabModel> map) {
        for (Job job : jobs) {
            job.start(map);
        }
    }

    public void stop() {
        for (Job job : jobs) {
            job.stop();
        }
    }

    public class Job {

        final String command;
        final long start;
        final Long stop;
        final long period;
        final long error;

        @SuppressWarnings("LeakingThisInConstructor")
        public Job(String command, long period, long error, long start) {
            this.command = command;
            this.start = start;
            this.stop = null;
            this.period = period;
            this.error = error;
            jobs.add(this);
        }

        @SuppressWarnings("LeakingThisInConstructor")
        public Job(String command, long period, long error, long start, long stop) {
            this.command = command;
            this.start = start;
            this.stop = stop;
            this.period = period;
            this.error = error;
            jobs.add(this);
        }

        Timer timer;
        TreeSet<Long> updateTimes = new TreeSet<>();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        SimpleDateFormat sdf2 = new SimpleDateFormat("yy/MM/dd HH:mm:ss");

        public void stop() {
            timer.cancel();
        }

        public void start(TreeMap<String, TabModel> map) {
            timer = new Timer("");
            timer.schedule(new TimerTask() {
                long day = 0, lastDay = 0;

                Random random = new Random();

                @SuppressWarnings("SynchronizeOnNonFinalField")
                private void check() {
                    GregorianCalendar cal = new GregorianCalendar();
                    cal.set(GregorianCalendar.MINUTE, 0);
                    cal.set(GregorianCalendar.HOUR_OF_DAY, 0);
                    cal.set(GregorianCalendar.SECOND, 0);
                    cal.set(GregorianCalendar.MILLISECOND, 0);
                    lastDay = day;
                    day = cal.getTimeInMillis();

                    long now = new Date().getTime();
                    now = now - day;

                    if (day != lastDay) {
                        updateTimes.clear();
                        if (stop == null) {
                            if (period > 0) {
                                long time = start;
                                do {

                                    long next = time + (long) (error * random.nextDouble());
                                    if ((next % 86400000) > now) {
                                        updateTimes.add(next % 86400000);
                                    }
                                    time = time + period;
                                } while (time <= 86400000);
                            }
                        } else {
                            if (period > 0) {
                                long time = start;
                                long end = stop < start ? (stop + 86400000) : stop;
                                do {
                                    time = time + period;
                                    long next = ((time + (long) (period * 0.8 * (random.nextDouble() - .5))) % 86400000);
                                    if (next > now) {
                                        updateTimes.add(next);
                                    }
                                } while (time < end);
                            }
                        }

                        StringBuilder sb = new StringBuilder();
                        for (long l : updateTimes) {
                            sb.append(" ").append(sdf.format(new Date(l + day)));
                        }
                        if (sb.length() > 80) {
                            sb = new StringBuilder(sb.toString().substring(0, 80));
                        }
                        Ui.tools.log("SCHEDULE [" + sb.toString().trim() + "]", command);
                    }

                    if (!updateTimes.isEmpty()) {
                        Long firstUpdate = updateTimes.first();
                        if (now >= firstUpdate) {
                            while (!updateTimes.isEmpty() && now >= updateTimes.first()) {
                                updateTimes.pollFirst();
                            }
                            StringBuilder sb = new StringBuilder();
                            for (long l : updateTimes) {
                                sb.append(" ").append(sdf.format(new Date(l + day)));
                            }

                            String code = map.get(command).text;

                            SwingUtilities.invokeLater(() -> {
                                synchronized (Ui.listModel) {
                                    boolean found = false;
                                    try {

                                        int size = Ui.listModel.getSize();
                                        for (int i = 0; i < size; i++) {
                                            if (Ui.listModel.get(i).title.equals(command)) {
                                                found = true;
                                                break;
                                            }
                                        }
                                    } catch (Exception e) {
                                        System.err.println(">>>>>>> scheduler check " + e.getLocalizedMessage());
                                    }
                                    if (!found) {
                                        new BeanShellProcess(command, code, Ui.input);
                                    }
                                }
                            });
                        }
                    }
                }

                @Override
                public void run() {
                    check();
                }
            }, 0, 687);
        }

    }

}
