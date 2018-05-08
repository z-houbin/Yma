package z.houbin.site.zdown;

public class Test {

    public static void main(String[] args) {
        for (int i = 0; i < 100; i++) {
            new Meip().start();
        }
    }

    private static class Meip extends Thread{
        @Override
        public void run() {
            super.run();
        }
    }
}
