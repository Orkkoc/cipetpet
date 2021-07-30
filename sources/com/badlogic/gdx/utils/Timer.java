package com.badlogic.gdx.utils;

import com.badlogic.gdx.Gdx;

public class Timer {
    private static final int CANCELLED = -1;
    private static final int FOREVER = -2;
    public static final Timer instance = new Timer();
    static final Array<Timer> instances = new Array<>(1);
    private final Array<Task> tasks = new Array<>(false, 8);

    static {
        Thread thread = new Thread("Timer") {
            public void run() {
                while (true) {
                    synchronized (Timer.instances) {
                        float time = ((float) System.nanoTime()) * 1.0E-9f;
                        float wait = Float.MAX_VALUE;
                        int n = Timer.instances.size;
                        for (int i = 0; i < n; i++) {
                            wait = Math.min(wait, Timer.instances.get(i).update(time));
                        }
                        long waitMillis = (long) (1000.0f * wait);
                        if (waitMillis > 0) {
                            try {
                                Timer.instances.wait(waitMillis);
                            } catch (InterruptedException e) {
                            }
                        }
                    }
                }
            }
        };
        thread.setDaemon(true);
        thread.start();
    }

    public Timer() {
        start();
    }

    public void postTask(Task task) {
        scheduleTask(task, 0.0f, 0.0f, 0);
    }

    public void scheduleTask(Task task, float delaySeconds) {
        scheduleTask(task, delaySeconds, 0.0f, 0);
    }

    public void scheduleTask(Task task, float delaySeconds, float intervalSeconds) {
        scheduleTask(task, delaySeconds, intervalSeconds, FOREVER);
    }

    public void scheduleTask(Task task, float delaySeconds, float intervalSeconds, int repeatCount) {
        if (task.repeatCount != -1) {
            throw new IllegalArgumentException("The same task may not be scheduled twice.");
        }
        task.executeTime = (((float) System.nanoTime()) * 1.0E-9f) + delaySeconds;
        task.intervalSeconds = intervalSeconds;
        task.repeatCount = repeatCount;
        synchronized (this.tasks) {
            this.tasks.add(task);
        }
        wake();
    }

    public void stop() {
        synchronized (instances) {
            instances.removeValue(this, true);
        }
    }

    public void start() {
        synchronized (instances) {
            if (!instances.contains(this, true)) {
                instances.add(this);
                wake();
            }
        }
    }

    public void clear() {
        synchronized (this.tasks) {
            int n = this.tasks.size;
            for (int i = 0; i < n; i++) {
                this.tasks.get(i).cancel();
            }
            this.tasks.clear();
        }
    }

    /* access modifiers changed from: package-private */
    public float update(float time) {
        float wait = Float.MAX_VALUE;
        synchronized (this.tasks) {
            int i = 0;
            int n = this.tasks.size;
            while (i < n) {
                Task task = this.tasks.get(i);
                if (task.executeTime > time) {
                    wait = Math.min(wait, task.executeTime - time);
                } else {
                    if (task.repeatCount != -1) {
                        if (task.repeatCount == 0) {
                            task.repeatCount = -1;
                        }
                        Gdx.app.postRunnable(task);
                    }
                    if (task.repeatCount == -1) {
                        this.tasks.removeIndex(i);
                        i--;
                        n--;
                    } else {
                        task.executeTime = task.intervalSeconds + time;
                        wait = Math.min(wait, task.executeTime - time);
                        if (task.repeatCount > 0) {
                            task.repeatCount--;
                        }
                    }
                }
                i++;
            }
        }
        return wait;
    }

    private static void wake() {
        synchronized (instances) {
            instances.notifyAll();
        }
    }

    public static void post(Task task) {
        instance.postTask(task);
    }

    public static void schedule(Task task, float delaySeconds) {
        instance.scheduleTask(task, delaySeconds);
    }

    public static void schedule(Task task, float delaySeconds, float intervalSeconds) {
        instance.scheduleTask(task, delaySeconds, intervalSeconds);
    }

    public static void schedule(Task task, float delaySeconds, float intervalSeconds, int repeatCount) {
        instance.scheduleTask(task, delaySeconds, intervalSeconds, repeatCount);
    }

    public static abstract class Task implements Runnable {
        float executeTime;
        float intervalSeconds;
        int repeatCount = -1;

        public abstract void run();

        public void cancel() {
            this.executeTime = 0.0f;
            this.repeatCount = -1;
        }

        public boolean isScheduled() {
            return this.repeatCount != -1;
        }
    }
}
